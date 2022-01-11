/*
 * Copyright 2021 The Android Open Source Project
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

package androidx.constraintLayout.desktop.link.java;

import androidx.constraintLayout.desktop.link.DesignSurfaceModification;
import androidx.constraintLayout.desktop.link.MainUI;
import androidx.constraintLayout.desktop.link.MotionLink;
import androidx.constraintLayout.desktop.scan.CLScan;
import androidx.constraintLayout.desktop.scan.CLTreeNode;
import androidx.constraintLayout.desktop.scan.SyntaxHighlight;
import androidx.constraintLayout.desktop.utils.Desk;
import androidx.constraintlayout.core.parser.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Main2 extends JPanel implements MainUI {
    Object mSelectionHighlight = null;
    JFrame layoutInspectorWindow = null;
    MotionLink motionLink = new MotionLink();
    JTextPane mMainText = new JTextPane();
    JLabel mMessages = new JLabel();
    JTree layoutListTree = new JTree();
    JScrollPane scrollPaneList = new JScrollPane(layoutListTree);
    SyntaxHighlight highlight = new SyntaxHighlight(mMainText);
    JScrollPane mMainTextScrollPane = new JScrollPane(mMainText);
    boolean drawDebug = false;
    LayoutInspector2 layoutInspector = null;
    boolean showWest = true;
    int widgetCount = 1;
    CLObject jsonModel;

    public Main2() {
        super(new BorderLayout());
        JButton hide = new JButton("<");
        JButton getButton = new JButton("Get");
        JButton connectButton = new JButton("Connect");
        JButton sendButton = new JButton("Send");
        JButton toggleDrawDebug = new JButton("Toggle Debug");
        JButton showLayout = new JButton("Inspect");
        JButton formatText = new JButton("Format Text");
        mMessages.setHorizontalAlignment(SwingConstants.RIGHT);
        Font font = new Font("Courier", Font.PLAIN, 20);
        mMainText.setFont(font);
        scrollPaneList.setPreferredSize(new Dimension(200, 100));
        JPanel northPanel = new JPanel();
        JPanel bigNorth = new JPanel(new BorderLayout());
        bigNorth.add(northPanel);
        bigNorth.add(hide, BorderLayout.WEST);

        northPanel.add(connectButton);
        northPanel.add(showLayout);
        // TODO: migrate to file menu?
        //        northPanel.add(toggleDrawDebug);
        //        northPanel.add(getButton);
        //        northPanel.add(sendButton);
        northPanel.add(formatText);
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(mMessages, BorderLayout.SOUTH);
        add(bigNorth, BorderLayout.NORTH);
        add(scrollPaneList, BorderLayout.WEST);
        add(mMainTextScrollPane, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
        hide.setPreferredSize(hide.getPreferredSize());
        hide.setBackground(new Color(0, 0, 0, 0));
        hide.setOpaque(false);
        hide.setBorder(null);
        hide.addActionListener((e -> {
            if (showWest) {
                remove(scrollPaneList);
                hide.setText(">");
            } else {
                add(scrollPaneList, BorderLayout.WEST);
                hide.setText("<");
            }
            showWest = !showWest;
        }));
        motionLink.addListener(this::fromLink);
        layoutListTree.getSelectionModel().addTreeSelectionListener(e -> {
            TreePath path = e.getPath();
            if (path.getPathCount() > 2) {
                CLTreeNode selected = (CLTreeNode) path.getLastPathComponent();
                System.out.println("selected " + selected.mKeyStart + "," + selected.mKeyEnd);
                mMainText.select(selected.mKeyStart, selected.mKeyEnd + 1);
                mMainText.requestFocus();
                return;
            }
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) path.getPathComponent(0);
            DefaultMutableTreeNode selected = (DefaultMutableTreeNode) path.getPathComponent(1);
            int index = root.getIndex(selected);
            motionLink.selectMotionScene(index);
            motionLink.getContent();
        });

        motionLink.getLayoutList();
        mMessages.setText("ok");
        connectButton.addActionListener(e -> {
            motionLink.getLayoutList();
        });
        toggleDrawDebug.addActionListener(e -> {
            motionLink.setDrawDebug(drawDebug = !drawDebug);
        });

        showLayout.addActionListener(e -> {
            if (layoutInspectorWindow != null && layoutInspectorWindow.isVisible()) {
                layoutInspector.resetEdit();
                layoutInspectorWindow.setVisible(false);
            } else {
                motionLink.getLayoutList();
                layoutInspector.resetEdit();
                layoutInspectorWindow.setVisible(true);
            }
        });
        getButton.addActionListener(e -> {
            motionLink.getContent();
        });
        formatText.addActionListener(e -> {
            try {
                formatText.setText(formatJson(mMainText.getText()));
                updateTree();
            } catch (Exception ex) {
            }
        });

        mMainText.addCaretListener(e -> {
            String str = mMainText.getText();
            highlight.opposingBracketColor(str, mMainText.getCaretPosition(), str.length());
        });

        mMainText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if ('\n' == e.getKeyChar()) {
                    String str = mMainText.getText();
                    int offset = mMainText.getCaretPosition();
                    int count = 0;

                    for (int i = offset - 2; i >= 1; i--) {
                        char c = str.charAt(i);
                        if (Character.isAlphabetic(c)) {
                            count = 0;
                            continue;
                        } else if (Character.isSpaceChar(c)) {
                            count++;
                        } else if (c == '\n') {
                            break;
                        }
                    }
                    String s = new String(new char[count]).replace('\0', ' ');
                    try {
                        mMainText.getDocument().insertString(offset, s, null);
                    } catch (BadLocationException badLocationException) {
                        badLocationException.printStackTrace();
                    }
                }
            }
        });

        mMainText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (highlight.update) {
                    return;
                }
                motionLink.sendContent(mMainText.getText());
                updateModel(mMainText.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (highlight.update) {
                    return;
                }
                motionLink.sendContent(mMainText.getText());
                updateModel(mMainText.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (highlight.update) {
                    return;
                }
                motionLink.sendContent(mMainText.getText());
                updateModel(mMainText.getText());
            }
        });
    }

    private void fromLink(MotionLink.Event event, MotionLink link) {
        switch (event) {
            case ERROR:
                mMessages.setText(link.errorMessage);
                mMessages.setForeground(Color.RED.darker());
                link.errorMessage = "";
                break;
            case STATUS:
                mMessages.setText(link.errorMessage);
                mMessages.setForeground(Color.BLACK);
                link.errorMessage = "";
                break;
            case LAYOUT_UPDATE:
                if (layoutInspector == null) {
                    layoutInspector = showLayoutInspector(link, new DesignSurfaceModification() {
                        @Override
                        public CLElement getElement(String name) {
                            if (jsonModel != null && jsonModel instanceof CLObject) {
                                try {
                                    return jsonModel.get(name);
                                } catch (CLParsingException e) {
                                    e.printStackTrace();
                                }
                            }
                            return null;
                        }

                        @Override
                        public void updateElement(String name, CLElement content) {
                            if (jsonModel != null && jsonModel instanceof CLObject) {
                                jsonModel.put(name, content);
                                setText(jsonModel.toFormattedJSON(0, 2));
                            }
                        }
                    });
                    link.setUpdateLayoutPolling(true);
                } else {
                    layoutInspector.setLayoutInformation(link.layoutInfos);
                }
                break;
            case LAYOUT_LIST_UPDATE:
                DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
                DefaultTreeModel model = new DefaultTreeModel(root);
                int i = 0;
                while (i < link.layoutNames.length) {
                    String name = link.layoutNames[i];
                    if (i == link.lastUpdateLayout) {
                        name = "<html><b>*" + name + "*</b></html>";
                    }
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(name);

                    root.add(node);
                    i++;
                }
                layoutListTree.setRootVisible(false);
                layoutListTree.setModel(model);
                break;
            case MOTION_SCENE_UPDATE:

                highlight.inOpposingBracketColor = true;
                setText(formatJson(link.motionSceneText));

                updateTree();
                highlight.inOpposingBracketColor = false;
        }

    }

    private void setText(String text) {
        mMainText.setText(text);
        updateModel(text);
    }

    private void updateModel(String text) {
        try {
            jsonModel = CLParser.parse(text);
            if (layoutInspector != null && jsonModel != null) {
                layoutInspector.setModel(jsonModel);
                layoutInspector.setSceneString(mMainText.getText());
            }
        } catch (CLParsingException e) {
            // nothing here... (text might be malformed as edit happens)
        }
    }

    private String formatJson(String text) {
        if (text.length() == 0) {
            return "";
        }
        try {
            CLObject json = CLParser.parse(text);
            int indentation = 2;
            if (json.has("ConstraintSets")) {
                indentation = 3;
            }
            return json.toFormattedJSON(0, indentation);
        } catch (CLParsingException e) {
            System.err.println("error in parsing text \"" + text + "\"");
            throw new RuntimeException("Parse error", e);
        }
    }

    private void updateTree() {
        DefaultTreeModel model = (DefaultTreeModel) layoutListTree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        int n = root.getChildCount();
        for (int i = 0; i < n; i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(i);
            child.removeAllChildren();
            if (motionLink.mSelectedIndex == i) {
                try {
                    CLTreeNode.parse(motionLink.motionSceneText, child);
                } catch (CLParsingException e) {
                    mMessages.setText(e.getMessage());
                    mMessages.setForeground(Color.RED.darker());
                }
            }
        }
        model.reload();
        layoutListTree.expandRow(motionLink.mSelectedIndex);
    }

    File myTmpFile = null;
    long myTempLastModified = 0;
    Timer myTmpTimer  = null;

    void  remoteEditStop() {
        myTmpTimer.stop();
        myTmpFile.deleteOnExit();
        myTmpFile = null;
    }

     public void addDesign(String type) throws CLParsingException, BadLocationException {
        CLKey key = CLScan.findCLKey(CLParser.parse(mMainText.getText()), "Design");
        String uType = upperCaseFirst(type);
        if (key != null) {
            int end = (int) key.getValue().getEnd() - 2;
            StyledDocument document = (StyledDocument) mMainText.getDocument();
            document.insertString(
                    end,
                    ",\n    $type$widgetCount:{ type: '$type' , text: '$uType$widgetCount' }",
                    null
            );
        } else {
            key = CLScan.findCLKey(CLParser.parse(mMainText.getText()), "Header");
            if (key != null) {
                widgetCount = 1;
                int end = (int) key.getValue().getEnd() + 2;
                StyledDocument document = (StyledDocument) mMainText.getDocument();
                String str = "\n  Design : { \n" +
                        "    $type$widgetCount:{ type: '$type' , text: '$uType$widgetCount'} \n  }";
                document.insertString(end, str, null);
            }
        }
        widgetCount++;
    }

    @Override
    public void addConstraint(String widget, String constraint) {
        CLKey key = null;
        try {
            key = CLScan.findCLKeyInRoot(CLParser.parse(mMainText.getText()), widget);
        } catch (CLParsingException e) {
            e.printStackTrace();
        }
        if (key == null) {
            int pos = mMainText.getText().length() - 2;
            String str = ",\n  " + widget + ": {\n    " +
                    constraint + ", \n" +
                    "   }\n";
            StyledDocument document  = (StyledDocument) mMainText.getDocument();
            try {
                document.insertString(pos, str, null);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        } else {
            int pos = (int) key.getValue().getEnd() - 1;
            String str = ",\n    " +constraint + ", \n";

            StyledDocument document=  (StyledDocument) mMainText.getDocument();
            try {
                document.insertString(pos, str, null);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

    String upperCaseFirst(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    @Override
    public void selectKey(String widget) throws CLParsingException {
        CLKey key = CLScan.findCLKey(CLParser.parse(mMainText.getText()), widget);
        clearSelectedKey();
        if (key != null) {

            Highlighter h = mMainText.getHighlighter();
            try {
                mSelectionHighlight = h.addHighlight(
                        (int) key.getStart(),
                        (int) key.getEnd() + 1,
                        new DefaultHighlighter.DefaultHighlightPainter(Color.PINK)
                );
            } catch (BadLocationException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void clearSelectedKey() {
        if (mSelectionHighlight == null) {
            return;
        }
        Highlighter h = mMainText.getHighlighter();
        h.removeHighlight(mSelectionHighlight);
    }

    void  remoteEdit() {
        try {
            File tmp = File.createTempFile(motionLink.selectedLayoutName, ".json5");
            FileWriter fw = new FileWriter(tmp);
            fw.write(motionLink.motionSceneText);
            fw.close();
            myTempLastModified = tmp.lastModified();
            Desktop.getDesktop().open(tmp);
            myTmpFile = tmp;
            myTmpTimer = new Timer(500,  e -> checkForUpdate()  );
            myTmpTimer.setRepeats(true);
            myTmpTimer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void checkForUpdate() {
        long lastM = myTmpFile.lastModified();
        if (lastM - myTempLastModified > 0) {
            try {
                myTempLastModified = lastM;
                FileReader fr = new FileReader(myTmpFile);
                char []buff =  new char[(int) myTmpFile.length()];
                int off = 0;
                while (true) {
                    int len = fr.read(buff, off, buff.length - off);
                    System.out.println(len);
                    if (len <= 0) break;
                    off += len;
                }
                fr.close();
                setText(new String(buff, 0, off));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public LayoutInspector2 showLayoutInspector(MotionLink link, DesignSurfaceModification callback) {
        JFrame frame = new JFrame("Layout Inspector");
        LayoutInspector2 inspector = new LayoutInspector2(link, this);
        frame.setContentPane(inspector);
        Desk.rememberPosition(frame, null);
        inspector.getEditorView().setDesignSurfaceModificationCallback(callback);
        layoutInspectorWindow = frame;
        return inspector;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("ConstraintLayout Live Editor");
        Main2 panel = new Main2();
        frame.setContentPane(panel);

        AbstractAction unlink = new AbstractAction("unlink") {
            public void actionPerformed(ActionEvent e) {
                panel.remoteEditStop();
            }
        };
        AbstractAction link = new AbstractAction("link") {
            public void actionPerformed(ActionEvent e) {
                panel.remoteEdit();
            }
        };

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        JMenu viewMenu = new JMenu("View");
        JMenu advMenu = new JMenu("Advanced");

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);

        fileMenu.add(advMenu);

        advMenu.add(unlink);
        advMenu.add(link);

        Desk.setupMenu(viewMenu);

        frame.setJMenuBar(menuBar);
        Desk.rememberPosition(frame, null);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
