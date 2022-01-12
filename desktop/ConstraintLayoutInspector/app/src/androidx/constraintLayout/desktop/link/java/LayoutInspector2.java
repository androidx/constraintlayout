package androidx.constraintLayout.desktop.link.java;

import androidx.constraintLayout.desktop.link.MainUI;
import androidx.constraintLayout.desktop.link.MotionLink;
import androidx.constraintLayout.desktop.ui.adapters.vd.ListIcons;
import androidx.constraintLayout.desktop.ui.adapters.vg.VDIcon;
import androidx.constraintLayout.desktop.ui.timeline.TimeLinePanel;
import androidx.constraintlayout.core.parser.CLObject;
import androidx.constraintlayout.core.parser.CLParsingException;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;


class LayoutInspector2 extends JPanel {
    boolean SHOW3D = false;
    final MotionLink motionLink;
    LayoutView2 layoutView;
    LayoutEditor2 editorView;

    MainUI main;
    JButton settings = settings("Start", "End", "Constraints", "Path", "PreTransform");
    JButton timeLineStart = new JButton("TimeLine...");
    JButton showWidgetAttributes = new JButton("Attributes...");
    JButton show3d = new JButton("3D...");

    JButton addButtonButton = new JButton("Button+");
    JButton addTextButton = new JButton("Text+");
    boolean editing = false;
    TimeLinePanel mTimeLinePanel = null;
    String mSceneString = null;

    @Override
    public void add(Component comp, Object constraints) {
        super.add(comp, constraints);
    }

    public LayoutInspector2(MotionLink link, MainUI main) {
        super(new BorderLayout());
        motionLink = link;
        this.main = main;
        layoutView = new LayoutView2(this);
        editorView = new LayoutEditor2(this);


        JPanel northPanel = new JPanel();
        JPanel westPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        JButton edit = new JButton("Edit");
        JCheckBox liveConnection = new JCheckBox("Live connection");
        JToggleButton rotate = new JToggleButton(new VDIcon(ListIcons.getStream("screen_rotation.xml")));
        rotate.setPreferredSize(new Dimension(24, 24));
        rotate.setFocusPainted(false);
        rotate.setSelected(false);
        liveConnection.setSelected(true);

        westPanel.add(addButtonButton, gbc);
        westPanel.add(addTextButton, gbc);
        gbc.weighty = 1.0;
        westPanel.add(Box.createGlue(), gbc);
        northPanel.add(settings);
        northPanel.add(timeLineStart);
        northPanel.add(showWidgetAttributes);
        if (SHOW3D) {
            northPanel.add(show3d);
        }

        northPanel.add(edit);
        northPanel.add(liveConnection);
        northPanel.add(rotate);

        setBackground(Color.YELLOW);
        add(northPanel, BorderLayout.NORTH);
        add(layoutView, BorderLayout.CENTER);
        add(westPanel, BorderLayout.WEST);

        liveConnection.addChangeListener(e -> motionLink.setUpdateLayoutPolling(liveConnection.isSelected()));

        addButtonButton.addActionListener(e -> {
            try {
                main.addDesign("button");
            } catch (CLParsingException clParsingException) {
                clParsingException.printStackTrace();
            } catch (BadLocationException badLocationException) {
                badLocationException.printStackTrace();
            }
        });

        addTextButton.addActionListener(e -> {
            try {
                main.addDesign("text");
            } catch (CLParsingException clParsingException) {
                clParsingException.printStackTrace();
            } catch (BadLocationException badLocationException) {
                badLocationException.printStackTrace();
            }
        });

        edit.addActionListener(e -> {
            editing = !editing;
            updateEditorMode();
        });
        rotate.addActionListener(e -> {
            layoutView.mReflectOrientation = rotate.isSelected();
        });
        timeLineStart.addActionListener(e -> {
            showTimeLine();
        });

        showWidgetAttributes.addActionListener(e -> {
            layoutView.displayWidgetAttributes();
        });
        show3d.addActionListener(e -> {
            layoutView.display3d();
        });
    }

    HashMap<String, Boolean> getSetting() {
        return (HashMap<String, Boolean>) settings.getClientProperty("map");
    }

    JButton settings(String... settings) {
        if (settings == null) {
            settings = new String[]{"Start", "End", "Constraints", "Path", "PreTransform"};
        }
        JButton button = new JButton("Show");
        JPopupMenu popup = new JPopupMenu();
        popup.add(new JMenuItem("Show"));
        popup.addSeparator();
        HashMap<String, Boolean> show = new HashMap<String, Boolean>();
        button.putClientProperty("map", show);
        for (int i = 0; i < settings.length; i++) {
            String set = settings[i];
            JCheckBoxMenuItem cbox = new JCheckBoxMenuItem(new AbstractAction(set) {
                public void actionPerformed(ActionEvent e) {
                    JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
                    show.put(set, item.isSelected());
                }
            });
            cbox.setSelected(true);
            show.put(set, true);
            popup.add(cbox);
        }
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        });
        return button;
    }

    private void updateEditorMode() {
        if (editing) {
            remove(layoutView);
            add(editorView, BorderLayout.CENTER);
            editorView.setBackground(Color.GREEN);
        } else {
            remove(editorView);
            add(layoutView, BorderLayout.CENTER);
            layoutView.setBackground(Color.BLUE);
        }

        revalidate();
        repaint();
    }

    void setLayoutInformation(String layoutInfos) {
        layoutView.setLayoutInformation(layoutInfos);
        editorView.setLayoutInformation(layoutInfos);
    }

    void setModel(CLObject jsonModel) {
        editorView.setModel(jsonModel);
    }

    void showTimeLine() {
        if (mTimeLinePanel == null) {
            if (mSceneString != null) {
                mTimeLinePanel = TimeLinePanel.showTimeline(mSceneString);
                mTimeLinePanel.addTimeLineListener((cmd, pos) -> motionLink.sendProgress(pos));
            }
        } else {
            mTimeLinePanel.popUp();
        }
    }

    void hideTimeLine() {
        if (mTimeLinePanel != null) {
            mTimeLinePanel.popDown();
        }
    }

    void setSceneString(String str) {
        mSceneString = str;
        if (mTimeLinePanel != null) {
            mTimeLinePanel.updateMotionScene(mSceneString);
        }
    }

    void resetEdit() {
        editing = false;
        updateEditorMode();
    }

    public LayoutEditor2 getEditorView() {
        return editorView;
    }
}