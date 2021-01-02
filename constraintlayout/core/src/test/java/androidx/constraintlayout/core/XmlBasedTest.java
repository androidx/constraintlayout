/*
 * Copyright (C) 2016 The Android Open Source Project
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
package androidx.constraintlayout.core;

import androidx.constraintlayout.core.widgets.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileFilter;
import java.util.*;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertEqualsNoOrder;
import static org.testng.Assert.assertTrue;

/**
 * This test the the ConstraintWidget system buy loading XML that contain tags with there positions.
 * the xml files can be designed in android studio.
 */
public class XmlBasedTest {
    private static final int ALLOWED_POSITION_ERROR = 1;
    HashMap<String, ConstraintWidget> widgetMap;
    HashMap<ConstraintWidget, String> boundsMap;
    ConstraintWidgetContainer container;
    ArrayList<Connection> connectionList;

    static class Connection {
        ConstraintWidget fromWidget;
        ConstraintAnchor.Type fromType, toType;
        String toName;
        int margin;
        int gonMargin = -1;
    }

    private static HashMap<String, Integer> visibilityMap = new HashMap<>();
    private static Map<String, Integer> stringWidthMap = new HashMap<String, Integer>();
    private static Map<String, Integer> stringHeightMap = new HashMap<String, Integer>();
    private static Map<String, Integer> buttonWidthMap = new HashMap<String, Integer>();
    private static Map<String, Integer> buttonHeightMap = new HashMap<String, Integer>();

    static {
        visibilityMap.put("gone", ConstraintWidget.GONE);
        visibilityMap.put("visible", ConstraintWidget.VISIBLE);
        visibilityMap.put("invisible", ConstraintWidget.INVISIBLE);
        stringWidthMap.put("TextView", 171);
        stringWidthMap.put("Button", 107);
        stringWidthMap.put("Hello World!", 200);
        stringHeightMap.put("TextView", 57);
        stringHeightMap.put("Button", 51);
        stringHeightMap.put("Hello World!", 51);
        String s = "12345678 12345678 12345678 12345678 12345678 12345678 12345678 12345678 12345678 12345678 12345678 12345678 12345678 12345678";
        stringWidthMap.put(s, 984);
        stringHeightMap.put(s, 204);
        buttonWidthMap.put("Button", 264);
        buttonHeightMap.put("Button", 144);
    }

    private static String rtl(String v) {
        if (v.equals("START")) return "LEFT";
        if (v.equals("END")) return "RIGHT";
        return v;
    }
    //@Test
    public void testAccessToResources() {
        String dirName  = System.getProperty("user.dir") + "/src/test/resources/";
        assertTrue(new File(dirName).exists(), " could not find dir "+dirName);
        Object[][]  names =  genListOfName();
        assertTrue(names.length>1," Could not get Path "+dirName);
    }

    @DataProvider(name = "test1")
    public static Object[][] genListOfName() {
        String dirName = System.getProperty("user.dir") + "/src/test/resources/";

        File[] f = new File(dirName).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().startsWith("check");
            }
        });
        Arrays.sort(f, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        if (false) {
            Object[][] ret = new Object[1][1];
            ret[0][0] = f[4].getAbsolutePath();
            return ret;
        }
        Object[][] ret = new Object[f.length][1];
        for (int i = 0; i < ret.length; i++) {
            ret[i][0] = f[i].getAbsolutePath();
        }
        return ret;
    }

    String dim(ConstraintWidget w) {
        if (w instanceof Guideline) {
            return w.getLeft() + "," + w.getTop() + "," + 0 + "," + 0;
        }
        if (w.getVisibility() == ConstraintWidget.GONE) {
            return 0 + "," + 0 + "," + 0 + "," + 0;
        }
        return w.getLeft() + "," + w.getTop() + "," + w.getWidth() + "," + w.getHeight();
    }

    @Test(dataProvider = "test1")
    public void testSolverXML(String file) {
        parseXML(file);
        container.setOptimizationLevel(Optimizer.OPTIMIZATION_NONE);
        int[] perm = new int[boundsMap.size()];
        for (int i = 0; i < perm.length; i++) {
            perm[i] = i;
        }
        int total = fact(perm.length);
        int skip = 1 + total / 1000;
        populateContainer(perm);
        makeConnections();
        layout();
        validate();
        int k = 0;
        while (nextPermutation(perm)) {
            k++;
            if (k % skip != 0) continue;

            populateContainer(perm);
            makeConnections();
            layout();
            validate();

        }
    }

    @Test(dataProvider = "test1")
    public void testDirectResolutionXML(String file) {

        parseXML(file);
        container.setOptimizationLevel(Optimizer.OPTIMIZATION_STANDARD);
        int[] perm = new int[boundsMap.size()];
        for (int i = 0; i < perm.length; i++) {
            perm[i] = i;
        }
        int total = fact(perm.length);
        int skip = 1 + total / 1000;
        populateContainer(perm);
        makeConnections();
        layout();
        validate();
        int k = 0;
        while (nextPermutation(perm)) {
            k++;
            if (k % skip != 0) continue;

            populateContainer(perm);
            makeConnections();
            layout();
            validate();
        }
    }

    /**
     * Calculate the Factorial of n
     *
     * @param N input number
     * @return Factorial of n
     */
    public static int fact(int N) {
        int ret = 1;
        while (N > 0) {
            ret *= (N--);
        }
        return ret;
    }

    /**
     * Compare two string containing comer separated integers
     *
     * @param a
     * @param b
     * @return
     */
    private boolean isSame(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        String[] a_split = a.split(",");
        String[] b_split = b.split(",");
        if (a_split.length != b_split.length) {
            return false;
        }
        for (int i = 0; i < a_split.length; i++) {
            if (a_split[i].length() == 0) {
                return false;
            }
            int error = ALLOWED_POSITION_ERROR;
            if (b_split[i].startsWith("+")) {
                error += 10;
            }
            int a_value = Integer.parseInt(a_split[i]);
            int b_value = Integer.parseInt(b_split[i]);
            if (Math.abs(a_value - b_value) > error) {
                return false;
            }
        }
        return true;
    }

    /**
     * Simple dimension parser
     * Multiply dp units by 3 because we simulate a screen with 3 pixels per dp
     *
     * @param dim
     * @return
     */
    static int parseDim(String dim) {
        if (dim.endsWith("dp")) {
            return 3 * Integer.parseInt(dim.substring(0, dim.length() - 2));
        }
        if (dim.equals("wrap_content")) {
            return -1;
        }
        return -2;
    }

    /**
     * parse the XML file
     *
     * @param fileName
     */
    private void parseXML(String fileName) {
        container = new ConstraintWidgetContainer(0, 0, 1080, 1920);
        container.setDebugName("parent");
        widgetMap = new HashMap<String, ConstraintWidget>();
        boundsMap = new HashMap<ConstraintWidget, String>();

        connectionList = new ArrayList<Connection>();

        DefaultHandler handler = new DefaultHandler() {
            String parentId;

            public void startDocument() throws SAXException {
            }

            public void endDocument() throws SAXException {
            }

            public void startElement(String namespaceURI,
                                     String localName,
                                     String qName,
                                     Attributes attributes)
                    throws SAXException {


                if (qName != null) {
                    if (qName.endsWith("ConstraintLayout")) {
                        int n = attributes.getLength();
                        for (int i = 0; i < n; i++) {

                            String attrName = attributes.getLocalName(i);
                            String attrValue = attributes.getValue(i);
                            if (attrName == "android:id") {
                                container.setDebugName(attrValue);
                            }
                        }
                        widgetMap.put(container.getDebugName(), container);
                        widgetMap.put("parent", container);
                    } else if (qName.endsWith("Guideline")) {
                        Guideline guideline = new Guideline();

                        String id = attributes.getValue("android:id");
                        guideline.setDebugName(id);
                        widgetMap.put(id, guideline);
                        boundsMap.put(guideline, attributes.getValue("android:tag"));
                        boolean horizontal = "horizontal".equals(attributes.getValue("android:orientation"));
                        System.out.println("Guideline " + id + " " + (horizontal ? "HORIZONTAL" : "VERTICAL"));
                        guideline.setOrientation(horizontal ? Guideline.HORIZONTAL : Guideline.VERTICAL);
                        if (attributes.getValue("app:layout_constraintGuide_begin") != null) {
                            guideline.setGuideBegin(parseDim(attributes.getValue("app:layout_constraintGuide_begin")));
                            System.out.println("Guideline " + id + " setGuideBegin " + parseDim(attributes.getValue("app:layout_constraintGuide_begin")));

                        } else if (attributes.getValue("app:layout_constraintGuide_percent") != null) {
                            guideline.setGuidePercent(Float.parseFloat(attributes.getValue("app:layout_constraintGuide_percent")));
                            System.out.println("Guideline " + id + " setGuidePercent " + Float.parseFloat(attributes.getValue("app:layout_constraintGuide_percent")));

                        } else if (attributes.getValue("app:layout_constraintGuide_end") != null) {
                            guideline.setGuideEnd(parseDim(attributes.getValue("app:layout_constraintGuide_end")));
                            System.out.println("Guideline " + id + " setGuideBegin " + parseDim(attributes.getValue("app:layout_constraintGuide_end")));
                        }
                        System.out.println(">>>>>>>>>>>>  " + guideline);

                    } else {
                        ConstraintWidget widget = new ConstraintWidget(200, 51);

                        widget.setBaselineDistance(28);

                        Connection[] connect = new Connection[5];
                        widget.setDebugName("widget" + (widgetMap.size() + 1));
                        int n = attributes.getLength();
                        for (int i = 0; i < n; i++) {
                            String attrName = attributes.getLocalName(i);
                            String attrValue = attributes.getValue(i);
                            if (attrName.equals("")) {

                            } else if (attrName.equals("android:tag")) {
                                boundsMap.put(widget, attrValue);
                            } else if (attrName.equals("android:layout_width")) {
                                int v = parseDim(attrValue);
                                ConstraintWidget.DimensionBehaviour behaviour = ConstraintWidget.DimensionBehaviour.FIXED;
                                if (v == 0) {
                                    behaviour = ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT;
                                    widget.setDimension(parseDim(attrValue), widget.getHeight());
                                } else if (v == -1) {
                                    behaviour = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
                                } else {
                                    widget.setDimension(parseDim(attrValue), widget.getHeight());
                                }
                                widget.setHorizontalDimensionBehaviour(behaviour);
                            } else if (attrName.equals("android:layout_height")) {
                                int v = parseDim(attrValue);
                                ConstraintWidget.DimensionBehaviour behaviour = ConstraintWidget.DimensionBehaviour.FIXED;
                                if (v == 0) {
                                    behaviour = ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT;
                                    widget.setDimension(widget.getWidth(), v);
                                } else if (v == -1) {
                                    behaviour = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
                                } else {
                                    widget.setDimension(widget.getWidth(), v);
                                }
                                widget.setVerticalDimensionBehaviour(behaviour);
                            } else if (attrName.equals("android:text")) {
                                System.out.print("text = \"" + attrValue + "\"");
                                Map<String, Integer> wmap = (qName.equals("Button")) ? buttonWidthMap : stringWidthMap;
                                Map<String, Integer> hmap = (qName.equals("Button")) ? buttonHeightMap : stringHeightMap;
                                if (wmap.containsKey(attrValue)) {
//                                    widget.setWrapWidth(wmap.get(attrValue));
                                    widget.setWidth(wmap.get(attrValue));

                                    System.out.print(" W ");

                                }
                                if (hmap.containsKey(attrValue)) {
//                                    widget.setWrapHeight(hmap.get(attrValue));
                                    widget.setHeight(hmap.get(attrValue));
                                    System.out.print(" H ");
                                }
                                System.out.println();

                            } else if (attrName.startsWith("android:visibility")) {

                                widget.setVisibility(visibilityMap.get(attrValue));

                            } else if (attrName.startsWith("app:layout_constraintDimensionRatio")) {
                                widget.setDimensionRatio(attrValue);
                            } else if (attrName.startsWith("app:layout_constraintHorizontal_bias")) {
                                widget.setHorizontalBiasPercent(Float.parseFloat(attrValue));
                            } else if (attrName.startsWith("app:layout_constraintVertical_bias")) {
                                widget.setVerticalBiasPercent(Float.parseFloat(attrValue));
                            } else if (attrName.startsWith("app:layout_constraint")) {

                                String[] sp = attrName.substring("app:layout_constraint".length()).split("_to");
                                String fromString = rtl(sp[0].toUpperCase());
                                ConstraintAnchor.Type from = ConstraintAnchor.Type.valueOf(fromString);
                                String toString = rtl(sp[1].substring(0, sp[1].length() - 2).toUpperCase());
                                ConstraintAnchor.Type to = ConstraintAnchor.Type.valueOf(toString);
                                int side = from.ordinal() - 1;
                                if (connect[side] == null) {
                                    connect[side] = new Connection();
                                }
                                connect[side].fromWidget = widget;
                                connect[side].fromType = from;
                                connect[side].toType = to;
                                connect[side].toName = attrValue;
                            } else if (attrName.startsWith("app:layout_goneMargin")) {
                                String marginSide = rtl(attrName.substring("app:layout_goneMargin".length()).toUpperCase());
                                ConstraintAnchor.Type marginType = ConstraintAnchor.Type.valueOf(marginSide);
                                int side = marginType.ordinal() - 1;
                                if (connect[side] == null) {
                                    connect[side] = new Connection();
                                }
                                connect[side].gonMargin = 3 * Integer.parseInt(attrValue.substring(0, attrValue.length() - 2));

                            } else if (attrName.startsWith("android:layout_margin")) {
                                String marginSide = rtl(attrName.substring("android:layout_margin".length()).toUpperCase());
                                ConstraintAnchor.Type marginType = ConstraintAnchor.Type.valueOf(marginSide);
                                int side = marginType.ordinal() - 1;
                                if (connect[side] == null) {
                                    connect[side] = new Connection();
                                }
                                connect[side].margin = 3 * Integer.parseInt(attrValue.substring(0, attrValue.length() - 2));
                            } else if (attrName.equals("android:id")) {
                                widget.setDebugName(attrValue);
                            }
                        }
                        widgetMap.put(widget.getDebugName(), widget);

                        for (int i = 0; i < connect.length; i++) {
                            if (connect[i] != null) {
                                connectionList.add(connect[i]);
                            }

                        }

                    }
                }


            }
        };

        File file = new File(fileName);
        SAXParserFactory spf = SAXParserFactory.newInstance();

        try {
            SAXParser saxParser = spf.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(handler);
            xmlReader.parse(file.toURI().toString());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateContainer(int[] order) {
        System.out.println(Arrays.toString(order));
        ConstraintWidget[] widgetSet = boundsMap.keySet().toArray(new ConstraintWidget[0]);
        for (int i = 0; i < widgetSet.length; i++) {
            ConstraintWidget constraintWidget = widgetSet[order[i]];
            container.remove(constraintWidget);
            container.add(constraintWidget);
        }
    }

    private void makeConnections() {
        for (Connection connection : connectionList) {
            ConstraintWidget toConnect;
            if (connection.toName.equalsIgnoreCase("parent") || connection.toName.equals(container.getDebugName())) {
                toConnect = container;
            } else {
                toConnect = widgetMap.get(connection.toName);
            }
            if (toConnect == null) {
                System.err.println("   " + connection.toName);
            } else {
                connection.fromWidget.connect(connection.fromType, toConnect, connection.toType, connection.margin);
                connection.fromWidget.setGoneMargin(connection.fromType, connection.gonMargin);
            }
        }
    }

    private void layout() {
        container.layout();
    }

    private void validate() {
        ConstraintWidgetContainer root = (ConstraintWidgetContainer) widgetMap.remove("parent");
        String[] keys = widgetMap.keySet().toArray(new String[0]);
        boolean ok = true;
        String layout = "\n";
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            if (key.contains("activity_main")) {
                continue;
            }
            ConstraintWidget widget = widgetMap.get(key);
            String bounds = boundsMap.get(widget);
            ok &= isSame(dim(widget), bounds);
            layout += rightPad(key.toString(), 17) + rightPad(dim(widget), 15) + "   " + bounds + "\n";
        }
        assertTrue(ok, layout);
    }

    private static String rightPad(String s, int n) {
        s = s + new String(new byte[n]).replace('\0', ' ');
        return s.substring(0, n);
    }

    private static String R(String s) {
        s = "             " + s;
        return s.substring(s.length() - 13);
    }

    /**
     * Ordered array (1,2,3...) will be cycled till the order is reversed (9,8,7...)
     *
     * @param array to be carried
     * @return false when the order is reversed
     */
    private static boolean nextPermutation(int[] array) {
        int i = array.length - 1;
        while (i > 0 && array[i - 1] >= array[i]) {
            i--;
        }
        if (i <= 0)
            return false;
        int j = array.length - 1;
        while (array[j] <= array[i - 1]) {
            j--;
        }

        int temp = array[i - 1];
        array[i - 1] = array[j];
        array[j] = temp;

        j = array.length - 1;
        while (i < j) {
            temp = array[i];
            array[i] = array[j];
            array[j] = temp;
            i++;
            j--;
        }

        return true;
    }

    @Test
    void SimpleTest() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1080, 1920);

        final ConstraintWidget A = new ConstraintWidget(0, 0, 200, 51);
//        A.setWrapWidth(200);
//        A.setWrapHeight(51);
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        A.setDebugName("A");
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 0);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 0);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, 0);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM, 0);
        root.add(A);
        root.layout();
        System.out.println("f) A: " + A + " " + A.getWidth() + "," + A.getHeight());
    }

    @Test
    void GuideLineTest() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1080, 1920);
        final ConstraintWidget A = new ConstraintWidget(0, 0, 200, 51);
        final Guideline guideline = new Guideline();
        root.add(guideline);

        guideline.setGuidePercent(0.50f);
        guideline.setOrientation(Guideline.VERTICAL);
        guideline.setDebugName("guideline");

//        A.setWrapWidth(200);
//        A.setWrapHeight(51);
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        A.setDebugName("A");
        A.connect(ConstraintAnchor.Type.LEFT, guideline, ConstraintAnchor.Type.LEFT, 0);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 0);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, 0);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM, 0);
        root.add(A);

        root.layout();
        System.out.println("f) A: " + A + " " + A.getWidth() + "," + A.getHeight());
        System.out.println("f) A: " + guideline + " " + guideline.getWidth() + "," + guideline.getHeight());

    }


}
