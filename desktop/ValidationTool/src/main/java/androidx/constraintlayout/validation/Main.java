/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.constraintlayout.validation;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class Main extends JPanel {

    private static final boolean DEBUG = false;
    private final static String REFERENCE = "references";
    private static final boolean FILTER_V2_LAYOUTS = false;
    private static final boolean CONSTANT_UPDATE = true;

    static final int OPTIMIZATION_NONE  = 0;
    static final int OPTIMIZATION_DIRECT = 1;
    static final int OPTIMIZATION_BARRIER = 1 << 1;
    static final int OPTIMIZATION_CHAIN = 1 << 2;
    static final int OPTIMIZATION_DIMENSIONS = 1 << 3;
    static final int OPTIMIZATION_RATIO = 1 << 4;
    static final int OPTIMIZATION_GROUPS = 1 << 5;
    static final int OPTIMIZATION_GRAPH = 1 << 6;
    static final int OPTIMIZATION_GRAPH_WRAP = 1 << 7;
    static final int OPTIMIZATION_CACHE_MEASURES = 1 << 8;
    static final int OPTIMIZATION_DEPENDENCY_ORDERING = 1 << 9;
    static final int OPTIMIZATION_CURRENT  = OPTIMIZATION_CACHE_MEASURES;// | OPTIMIZATION_DEPENDENCY_ORDERING;
    static final int OPTIMIZATION_STANDARD = OPTIMIZATION_NONE
             /* OPTIMIZATION_DIRECT
                    | OPTIMIZATION_BARRIER
                    | OPTIMIZATION_CHAIN
            /*                    | OPTIMIZATION_GRAPH */
            /* | OPTIMIZATION_GRAPH_WRAP */
            /* | OPTIMIZATION_DIMENSIONS */
            ;
    DeviceConnection client;
    Display layoutDisplay = new Display();
    JTable table;
    JProgressBar progressBar;
    JLabel progressLabel;
    GraphView performancesDisplay = new GraphView();
    BoxPlotView boxPlotDisplay = new BoxPlotView();

    private HashMap<String, Result> results = new HashMap<>();

    private ArrayList<Layout> layouts = new ArrayList<>();
    private boolean showPerformances = false;

    enum LayoutType {MATCH_MATCH, MATCH_WRAP, WRAP_MATCH, WRAP_WRAP}

    enum ResultType {UNKNOWN, SUCCESS, PASSABLE, FAILURE}

    class Layout {
        String name;
        Result m_m = new Result();
        Result m_w = new Result();
        Result w_m = new Result();
        Result w_w = new Result();

        public Layout(String name) {
            this.name = name;
        }
    }

    class Result {
        int numWidgets;
        ResultType success = ResultType.UNKNOWN;
        long duration;
        long optimizedDuration;
        long referenceDuration;
    }

    ///////////////////////////////////////////////////////////////////////////
    // UI Setup
    ///////////////////////////////////////////////////////////////////////////

    public Main() {
        super(new BorderLayout());
        progressBar = new JProgressBar();
        progressLabel = new JLabel("status: 0/0");
        JButton connect = new JButton("Connect");
        JButton validateSelection = new JButton("Validate selection");
        JButton validate = new JButton("Validate");
        JButton updateAllBaselines = new JButton("Update Full Baseline");
        JButton updateSelectionBaseline = new JButton("Update Baseline of selection");
        JButton showPerformances = new JButton("Show Performances");

        JCheckBox showImage = new JCheckBox("Show Render", true);
        JCheckBox showReferenceBounds = new JCheckBox("Show Reference Bounds", true);
        JCheckBox showCurrentBounds = new JCheckBox("Show Current Bounds", true);

        JCheckBox showNumberOfWidgets = new JCheckBox("Show Number of Widgets", false);
        JCheckBox useLog10 = new JCheckBox("Log10", false);

        showImage.addActionListener(e -> {
            layoutDisplay.setShowImage(showImage.isSelected());
        });

        showReferenceBounds.addActionListener(e -> {
            layoutDisplay.setShowReferenceBounds(showReferenceBounds.isSelected());
        });

        showCurrentBounds.addActionListener(e -> {
            layoutDisplay.setShowCurrentBounds(showCurrentBounds.isSelected());
        });

        showNumberOfWidgets.addActionListener(e -> {
            performancesDisplay.setShowNumberOfWidgets(showNumberOfWidgets.isSelected());
        });

        useLog10.addActionListener(e -> {
            performancesDisplay.useLog10(useLog10.isSelected());
        });

        validate.setEnabled(false);
        connect.addActionListener(e -> {
            if (client != null) {
                client.close();
            }
            client = new DeviceConnection();
            validate.setEnabled(true);
            updateFiles();
        });
        validate.addActionListener(e -> validateLayouts());
        validateSelection.addActionListener(e -> validateSelection());

        updateAllBaselines.addActionListener(e -> updateAllBaselines());
        updateSelectionBaseline.addActionListener(e -> updateSelectionBaseline());
        showPerformances.addActionListener(e -> showPerformances());

        table = new JTable(new TableModel(layouts));
        table.setColumnSelectionAllowed(true);
        table.setRowSelectionAllowed(true);
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        table.getSelectionModel().addListSelectionListener(createListSelectionListener());
        table.getColumnModel().getSelectionModel().addListSelectionListener(createListSelectionListener());
        table.setAutoCreateRowSorter(true);
        table.getRowSorter().addRowSorterListener(e -> {
            ArrayList<Layout> layouts = ((TableModel) table.getModel()).getLayouts(table);
            updateLayoutMeasures(layouts);
        });

        table.setDefaultRenderer(Object.class, new MyTableCellRenderer());

        scrollPane.setPreferredSize(new Dimension(600, 300));
        add(scrollPane, BorderLayout.WEST);
        performancesDisplay.setPreferredSize(new Dimension(700, 300));
        boxPlotDisplay.setPreferredSize(new Dimension(700, 100));

        JPanel displayControlPanel = new JPanel();
        displayControlPanel.setLayout(new BoxLayout(displayControlPanel, BoxLayout.LINE_AXIS));
        displayControlPanel.add(showImage);
        displayControlPanel.add(showReferenceBounds);
        displayControlPanel.add(showCurrentBounds);

        JPanel graphPanel = new JPanel();
        graphPanel.setLayout(new BoxLayout(graphPanel, BoxLayout.PAGE_AXIS));
        graphPanel.add(boxPlotDisplay);
        graphPanel.add(performancesDisplay);
        graphPanel.add(showNumberOfWidgets);
        graphPanel.add(useLog10);

        JPanel displayPanel = new JPanel();
        displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.PAGE_AXIS));
        displayPanel.add(layoutDisplay);
        displayPanel.add(displayControlPanel);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, displayPanel, graphPanel);
        add(splitPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.PAGE_AXIS));
        controlPanel.add(updateAllBaselines);
        controlPanel.add(updateSelectionBaseline);
        controlPanel.add(validateSelection);
        controlPanel.add(validate);
        controlPanel.add(showPerformances);
        controlPanel.add(connect);

        JPanel progressPanel = new JPanel();
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.PAGE_AXIS));
        progressPanel.add(progressBar);
        progressPanel.add(progressLabel);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS));
        bottomPanel.add(controlPanel);
        bottomPanel.add(progressPanel);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Utilities
    ///////////////////////////////////////////////////////////////////////////

    private void showPerformances() {
        showPerformances = true;
        if (performancesDisplay.isShowing()) {
            ArrayList<Layout> referenceLayouts = new ArrayList<>();
            for (Layout layout : layouts) {
                String reference = getReferenceLayout(layout.name, LayoutType.MATCH_MATCH);
                if (reference.length() > 0) {
                    JSONObject referenceObject = new JSONObject(reference);
                    long duration = referenceObject.getLong("duration");
                    JSONObject layoutObject = referenceObject.getJSONObject("layout");
                    int numChildren = layoutObject.getJSONArray("children").length();
                    layout.m_m.referenceDuration = duration;
                    layout.m_m.numWidgets = numChildren;
                }
                reference = getReferenceLayout(layout.name, LayoutType.MATCH_WRAP);
                if (reference.length() > 0) {
                    JSONObject referenceObject = new JSONObject(reference);
                    long duration = referenceObject.getLong("duration");
                    JSONObject layoutObject = referenceObject.getJSONObject("layout");
                    int numChildren = layoutObject.getJSONArray("children").length();
                    layout.m_w.referenceDuration = duration;
                    layout.m_w.numWidgets = numChildren;
                }
                reference = getReferenceLayout(layout.name, LayoutType.WRAP_MATCH);
                if (reference.length() > 0) {
                    JSONObject referenceObject = new JSONObject(reference);
                    long duration = referenceObject.getLong("duration");
                    JSONObject layoutObject = referenceObject.getJSONObject("layout");
                    int numChildren = layoutObject.getJSONArray("children").length();
                    layout.w_m.referenceDuration = duration;
                    layout.w_m.numWidgets = numChildren;
                }
                reference = getReferenceLayout(layout.name, LayoutType.WRAP_WRAP);
                if (reference.length() > 0) {
                    JSONObject referenceObject = new JSONObject(reference);
                    long duration = referenceObject.getLong("duration");
                    JSONObject layoutObject = referenceObject.getJSONObject("layout");
                    int numChildren = layoutObject.getJSONArray("children").length();
                    layout.w_w.referenceDuration = duration;
                    layout.w_w.numWidgets = numChildren;
                }
            }
            updateLayoutMeasures(layouts);
            boxPlotDisplay.setLayoutMeasures(layouts);
        }
    }

    private void validateSelection() {
        int selection = table.getSelectedRow();
        if (selection < 0) {
            return;
        }
        selection = table.convertRowIndexToModel(selection);
        Layout layout = layouts.get(selection);
        validateLayout(layout, OPTIMIZATION_STANDARD);
        if (showPerformances) {
            validateLayout(layout, OPTIMIZATION_CURRENT);
        }
        updateLayoutMeasures(layouts);
    }

    private void validateLayouts() {
        new Thread(() -> {
            int n = 0;
            int total = layouts.size() * 4;
            int[] results = new int[3];
            long start = System.currentTimeMillis();
            for (Layout layout : layouts) {
                validateLayout(layout, OPTIMIZATION_STANDARD);
                if (showPerformances) {
                    validateLayout(layout, OPTIMIZATION_CURRENT);
                }
                check(layout.m_m, results);
                check(layout.m_w, results);
                check(layout.w_m, results);
                check(layout.w_w, results);
                n++;
                progressBar.getModel().setValue(n);
                long duration = System.currentTimeMillis() - start;
                progressLabel.setText("success: " + results[0] + "/" + total
                        + " passable: " + results[1]
                        + " failures: " + results[2] + " in " + duration + " ms");
                updateLayoutMeasures(layouts);
            }
        }).start();
        ((TableModel) table.getModel()).updateResults();
    }

    private void check(Result result, int[] type) {
        if (result.success == ResultType.SUCCESS) {
            type[0]++;
        } else if (result.success == ResultType.PASSABLE) {
            type[1]++;
        } else if (result.success == ResultType.FAILURE) {
            type[2]++;
        }
    }

    private void validateLayout(Layout layout, int optimization) {
        /*
        loadLayout(layout.name, LayoutType.MATCH_MATCH);
        evaluate(layout, client.getLayout(), LayoutType.MATCH_MATCH);
        client.wrapContentVertical();
        evaluate(layout, client.getLayout(), LayoutType.MATCH_WRAP);
        client.wrapContentHorizontal();
        evaluate(layout, client.getLayout(), LayoutType.WRAP_MATCH);
        client.wrapContent();
        evaluate(layout, client.getLayout(), LayoutType.WRAP_WRAP);
        */
        layout.m_m.success = ResultType.UNKNOWN;
        layout.m_w.success = ResultType.UNKNOWN;
        layout.w_m.success = ResultType.UNKNOWN;
        layout.w_w.success = ResultType.UNKNOWN;
        validateLayout(layout.name, layout.m_m, LayoutType.MATCH_MATCH, optimization);
        validateLayout(layout.name, layout.m_w, LayoutType.MATCH_WRAP, optimization);
        validateLayout(layout.name, layout.w_m, LayoutType.WRAP_MATCH, optimization);
        validateLayout(layout.name, layout.w_w, LayoutType.WRAP_WRAP, optimization);
    }

    private void validateLayout(String file, Result result, LayoutType mode, int optimization) {
        String reference = getReferenceLayout(file, mode);
        if (reference == null || reference.length() == 0) {
            return;
        }
        String layout = getLayout(file, mode, optimization);

        if (layout.length() == 0) {
            return;
        }
        JSONObject layoutMeasure = new JSONObject(layout);
        JSONObject referenceMeasure = new JSONObject(reference);

        if (layoutMeasure != null) {
            long duration = layoutMeasure.getLong("duration");
            if (optimization == OPTIMIZATION_STANDARD) {
                result.duration = duration;
//                result.optimizedDuration = duration;
            } else {
                result.optimizedDuration = duration;
            }
            layoutMeasure = layoutMeasure.getJSONObject("layout");
        }
        if (referenceMeasure != null) {
            referenceMeasure = referenceMeasure.getJSONObject("layout");
        }
        if (layoutMeasure == null || referenceMeasure == null) {
            result.success = ResultType.FAILURE;
        } else {
            result.success = compareLayouts(referenceMeasure, layoutMeasure);
        }
        if (DEBUG) {
            if (result.success == ResultType.FAILURE) {
                System.out.println("layout: > " + layout);
                System.out.println("reference: > " + reference);
            }
        }
        updateTable();
    }

    private ResultType compareLayouts(JSONObject referenceMeasure, JSONObject layoutMeasure) {
        JSONObject referenceBounds = referenceMeasure.getJSONObject("bounds");
        JSONObject measureBounds = layoutMeasure.getJSONObject("bounds");
        int delta = compareBounds(referenceBounds, measureBounds);

        ResultType resultType = ResultType.SUCCESS;

        if (delta > 0 && delta <= 4) {
            resultType = ResultType.PASSABLE;
        } else if (delta > 4) {
            if (DEBUG) {
                System.out.println("Failure on " + referenceMeasure.getString("id"));
            }
            return ResultType.FAILURE;
        }

        if (referenceMeasure.has("children")) {
            JSONArray referenceChildren = referenceMeasure.getJSONArray("children");
            JSONArray measureChildren = layoutMeasure.getJSONArray("children");

            if (referenceChildren.length() != measureChildren.length()) {
                return ResultType.FAILURE;
            }
            for (int i = 0; i < referenceChildren.length(); i++) {
                JSONObject childReference = referenceChildren.getJSONObject(i);
                JSONObject childMeasure = measureChildren.getJSONObject(i);
                ResultType childResultType = compareLayouts(childReference, childMeasure);
                if (childResultType == ResultType.FAILURE) {
                    return ResultType.FAILURE;
                }
                if (childResultType == ResultType.PASSABLE) {
                    resultType = ResultType.PASSABLE;
                }
            }
        }
        return resultType;
    }

    private int compareBounds(JSONObject referenceBounds, JSONObject measureBounds) {
        int referenceLeft = referenceBounds.getInt("left");
        int referenceTop = referenceBounds.getInt("top");
        int referenceRight = referenceBounds.getInt("right");
        int referenceBottom = referenceBounds.getInt("bottom");
        int measureLeft = measureBounds.getInt("left");
        int measureTop = measureBounds.getInt("top");
        int measureRight = measureBounds.getInt("right");
        int measureBottom = measureBounds.getInt("bottom");
        int deltaLeft = Math.abs(referenceLeft - measureLeft);
        int deltaTop = Math.abs(referenceTop - measureTop);
        int deltaRight = Math.abs(referenceRight - measureRight);
        int deltaBottom = Math.abs(referenceBottom - measureBottom);
        int total = deltaLeft + deltaTop + deltaRight + deltaBottom;
        return total;
    }

    private String getLayout(String file, LayoutType mode, int optimization) {
        return client.getLayout(file, mode, optimization);
    }

    private String getReferenceLayout(String name, LayoutType mode) {
        StringBuilder result = new StringBuilder();
        try {
            File inputFile = new File(REFERENCE + "/" + name + "_" + mode + ".json");
            if (inputFile.exists()) {
                Path path = Paths.get(inputFile.getPath());
                java.util.List<String> list = Files.readAllLines(path);
                list.forEach(line -> result.append(line));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private void updateAllBaselines() {
        for (Layout layout : layouts) {
            updateBaseline(layout);
        }
    }

    private void updateSelectionBaseline() {
        int selection = table.getSelectedRow();
        if (selection < 0) {
            return;
        }
        selection = table.convertRowIndexToModel(selection);
        Layout layout = layouts.get(selection);
        updateBaseline(layout);
        updateLayoutMeasures(layouts);
    }

    private void updateBaseline(Layout layout) {
        updateBaseline(layout.name, layout.m_m, LayoutType.MATCH_MATCH);
        updateBaseline(layout.name, layout.m_w, LayoutType.MATCH_WRAP);
        updateBaseline(layout.name, layout.w_m, LayoutType.WRAP_MATCH);
        updateBaseline(layout.name, layout.w_w, LayoutType.WRAP_WRAP);
    }

    private void updateBaseline(String name, Result result, LayoutType mode) {
        String layout = client.getLayout(name, mode, 0);
        File reference = new File(REFERENCE + "/");
        try {
            reference.mkdir();
            File outputFile = new File(REFERENCE + "/" + name + "_" + mode + ".json");
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
            JSONObject json = new JSONObject(layout);
            json.write(writer, 2, 0);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    boolean valid(String fileName) {
        if (fileName.equals("check_291")) {
            return false;
        }
        if (!FILTER_V2_LAYOUTS) {
            return true;
        }
        String[] filteredFiles = new String[] {
                "check_187",
                "check_224",
                "check_233",
                "check_234",
                "check_235",
                "check_236",
                "check_241",
                "check_245",
                "check_282",
                "check_283",
                "check_296",
                "check_297",
                "check_298",
                "check_300",
                "check_301",
                "check_302",
                "check_303",
                "check_305",
                "check_306",
                "check_307",
                "check_265" // remove for now as it takes too long
        };
        for (int i = 0; i < filteredFiles.length; i++) {
            if (filteredFiles[i].equals(fileName)) {
                return false;
            }
        }
        return true;
    }

    void updateFiles() {
        ArrayList<String> files = client.listLayoutFiles();
        layouts.clear();
        for (int i = 0; i < files.size(); i++) {
            String fileName = files.get(i);
            if (valid(fileName)) {
                layouts.add(new Layout(fileName));
            }
        }
        ((TableModel) table.getModel()).updateWith(layouts);
        progressBar.getModel().setMinimum(0);
        progressBar.getModel().setMaximum(layouts.size());
        progressBar.getModel().setValue(0);
    }

    void takePicture() {
        Image image = client.takePicture();
        layoutDisplay.setImage(image);
    }

    void updateLayout(String layout) {
        if (layout == null || layout.length() == 0) {
            return;
        }
        JSONObject jsonObject = new JSONObject(layout);
        if (jsonObject != null) {
            layoutDisplay.setValue(jsonObject);
        }
    }

    void updateReference(String reference) {
        if (reference == null || reference.length() == 0) {
            return;
        }
        JSONObject jsonObject = new JSONObject(reference);
        if (jsonObject != null) {
            layoutDisplay.setReferenceValue(jsonObject);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Table management
    ///////////////////////////////////////////////////////////////////////////

    class TableModel extends AbstractTableModel {
        private String[] columnNames = {"File", "N", "Duration (ref)", "Duration", "MxM", "MxW", "WxM", "WxW"};
        private ArrayList<Layout> layouts;

        public TableModel(ArrayList<Layout> files) {
            this.layouts = files;
        }

        public ArrayList<Layout> getLayouts(JTable table) {
            ArrayList<Layout> result = new ArrayList<>();
            int n = getRowCount();
            for (int i = 0; i < n; i++) {
                int index = table.convertRowIndexToModel(i);
                result.add(layouts.get(index));
            }
            return result;
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return layouts.size();
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 1 || columnIndex == 2 || columnIndex == 3) {
                return Long.class;
            }
            return Object.class;
        }

        public Object getValueAt(int row, int col) {
            if (row < 0) {
                return null;
            }
            Layout file = layouts.get(row);
            if (col == 0) {
                return file.name;
            }
            if (col == 1) {
                return file.m_m.numWidgets;
            }
            if (col == 2) {
                return file.m_m.referenceDuration;
            }
            if (col == 3) {
                return file.m_m.duration;
            }
            Result result = null;
            if (col == 4) {
                result = file.m_m;
            } else if (col == 5) {
                result = file.m_w;
            } else if (col == 6) {
                result = file.w_m;
            } else if (col == 7) {
                result = file.w_w;
            }

            if (result == null) {
                return null;
            }
            return result.success;
        }

        public boolean isCellEditable(int row, int col) {
            return false;
        }

        public void updateResults() {
            fireTableDataChanged();
        }

        public void updateWith(ArrayList<Layout> files) {
            layouts = files;
            fireTableDataChanged();
            invalidate();
        }
    }

    private void updateLayoutMeasures(ArrayList<Layout> layouts) {
        performancesDisplay.setLayoutMeasures(layouts);
        if (CONSTANT_UPDATE) {
            boxPlotDisplay.setLayoutMeasures(layouts);
        }
    }

    private ListSelectionListener createListSelectionListener() {
        return e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow < 0) {
                return;
            }
            if (table.getSelectedRowCount() == 1) {
                ArrayList<Layout> layouts = ((TableModel) table.getModel()).getLayouts(table);
                updateLayoutMeasures(layouts);
                selectedRow = table.convertRowIndexToModel(selectedRow);
                Object selection = table.getModel().getValueAt(selectedRow, 0);
                int selectedColumn = table.getSelectedColumn();
                performancesDisplay.setSelection(table.getSelectedRow());
                if (selection != null) {
                    String name = (String) selection;
                    LayoutType mode = LayoutType.MATCH_MATCH;
                    if (selectedColumn <= 4) {
                        client.matchContent();
                        mode = LayoutType.MATCH_MATCH;
                    } else if (selectedColumn == 5) {
                        client.wrapContentVertical();
                        mode = LayoutType.MATCH_WRAP;
                    } else if (selectedColumn == 6) {
                        client.wrapContentHorizontal();
                        mode = LayoutType.WRAP_MATCH;
                    } else if (selectedColumn == 7) {
                        client.wrapContent();
                        mode = LayoutType.WRAP_WRAP;
                    }
                    String current = getLayout(name, mode, 0);
                    String reference = getReferenceLayout(name, mode);
                    layoutDisplay.setLayoutInfo(name, mode);
                    takePicture();
                    updateLayout(current);
                    updateReference(reference);
                }
            } else {
                int[] rows = table.getSelectedRows();
                ArrayList<Layout> selectedLayouts = new ArrayList<>();
                for (int i = 0; i < rows.length; i++) {
                    selectedRow = table.convertRowIndexToModel(rows[i]);
                    Layout layout = layouts.get(selectedRow);
                    selectedLayouts.add(layout);
                }
                updateLayoutMeasures(selectedLayouts);
            }
        };
    }

    private class MyTableCellRenderer implements TableCellRenderer {
        JLabel comp = new JLabel();
        String val;

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {
            comp.setOpaque(true);
            comp.setForeground(Color.BLACK); // text color

            if (value != null) {
                val = value.toString();
                comp.setText(val);
                boolean faster = false;

                if (column == 0) { // current run
                    long baseline = (Long) table.getValueAt(row, 2);
                    long currentValue = (Long) table.getValueAt(row, 3);;
                    if (currentValue > 0 && currentValue < baseline) {
                        faster = true;
                    }
                }

                if (val.equalsIgnoreCase("FAILURE")) {
                    if (isSelected) {
                        comp.setBackground(Color.RED.darker());
                    } else {
                        comp.setBackground(Color.RED);
                    }
                } else if (val.equalsIgnoreCase("PASSABLE")) {
                    if (isSelected) {
                        comp.setBackground(Color.YELLOW.darker());
                    } else {
                        comp.setBackground(Color.YELLOW);
                    }
                } else if (val.equalsIgnoreCase("SUCCESS")) {
                    if (isSelected) {
                        comp.setBackground(Color.GREEN.darker());
                    } else {
                        comp.setBackground(Color.GREEN);
                    }
                } else if (isSelected || table.getSelectedRow() == row) {
                    if (isSelected) {
                        comp.setBackground(Color.BLUE.darker());
                    } else {
                        if (faster) {
                            comp.setBackground(Color.GREEN.brighter());
                        } else {
                            comp.setBackground(Color.BLUE);
                        }
                    }
                    comp.setForeground(Color.WHITE);
                } else {
                    if (faster) {
                        comp.setBackground(Color.GREEN.brighter());
                    } else {
                        comp.setBackground(Color.WHITE);
                    }
                }
            }
            return comp;
        }
    }

    private void updateTable() {
        if (SwingUtilities.isEventDispatchThread()) {
            ((TableModel) table.getModel()).updateResults();
        } else {
            try {
                SwingUtilities.invokeAndWait(() -> {
                    ((TableModel) table.getModel()).updateResults();
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Main
    ///////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) {
        File file = new File(REFERENCE);
        String title = "Validation UI : ";
        if (file.exists()) {
            title += " Reference dir: " + file.getPath();
        } else {
            title += " Reference dir: "+ file.getAbsolutePath()+ " NOT FOUND";
        }

        JFrame f = new JFrame(title);
        Main p = new Main();
        f.setContentPane(p);
        f.setBounds(100, 100, 1500, 800);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setVisible(true);
    }

}
