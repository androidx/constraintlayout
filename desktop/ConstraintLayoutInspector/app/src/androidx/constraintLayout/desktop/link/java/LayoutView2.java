package androidx.constraintLayout.desktop.link.java;

import androidx.constraintLayout.desktop.constraintRendering.layout3d.CheckLayout3d;
import androidx.constraintLayout.desktop.link.Widget;
import androidx.constraintLayout.desktop.scan.LayoutConstraints;
import androidx.constraintLayout.desktop.scan.WidgetFrameUtils;
import androidx.constraintLayout.desktop.ui.utils.Debug;
import androidx.constraintLayout.desktop.utils.ScenePicker;
import androidx.constraintLayout.desktop.utils.ScenePicker.HitElementListener;
import androidx.constraintLayout.desktop.utils.WidgetAttributes;
import androidx.constraintlayout.core.parser.*;
import androidx.constraintlayout.core.state.WidgetFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;


public class LayoutView2 extends JPanel {
    private WidgetAttributes attDisplay = null;
    private CheckLayout3d display3d = null;
    protected  ArrayList<Widget> widgets = new ArrayList<>();
    HashMap<String, LayoutConstraints> startLayoutMap = new HashMap<String, LayoutConstraints>();
    HashMap<String, LayoutConstraints> endLayoutMap = new HashMap<String, LayoutConstraints>();
    float zoom = 0.85f;
    ScenePicker picker = new ScenePicker();
    private float rootWidth = 0f;
    private float rootHeight = 0f;
    private LayoutInspector2 inspector;
    protected float lastRootWidth = 0f;
    protected float lastRootHeight = 0f;
    boolean mReflectOrientation = false;

    protected float scaleX = 0f;
    protected float scaleY = 0f;
    protected float offX = 0.0f;
    protected float offY = 0.0f;
    protected HashSet<String> overWidgets = new HashSet<String>();
    protected HashSet<String> selectWidgets = new HashSet<String>();
    protected String primarySelected = null;

    public LayoutView2(LayoutInspector2 inspector) {
        super(new BorderLayout());
        setBackground(Color.RED);
        this.inspector = inspector;

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selectWidgets.clear();
                picker.setSelectListener((over, dist) -> {
                    if (over instanceof WidgetFrame) {
                        selectWidgets.add(((WidgetFrame) over).name);
                    }
                });
                picker.find(e.getX(), e.getY());

                for (String s : selectWidgets) {
                    if (s.equals("root")) {
                        continue;
                    }
                    if (primarySelected == null) {
                        primarySelected = s;
                        break;
                    }
                    if (!primarySelected.equals(s)) {
                        primarySelected = s;
                        break;
                    }
                }

                if (e.isPopupTrigger())
                    rightMouse(e);
                else {
                    String s = primarySelected;
                    if (s != null) {
                        try {
                            inspector.main.selectKey(s);
                        } catch (CLParsingException clParsingException) {
                            clParsingException.printStackTrace();
                        }
                    }
                }
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {

                inspector.main.clearSelectedKey();
                repaint();
                if (e.isPopupTrigger()) {
                    rightMouse(e);
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                overWidgets.clear();
                picker.setSelectListener((over, dist) -> {
                    if (over instanceof WidgetFrame) {
                        overWidgets.add(((WidgetFrame) over).name);
                    }
                });

                picker.find(e.getX(), e.getY());

                repaint();
            }
        };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    void computeScale(float rootWidth, float rootHeight) {
        lastRootWidth = rootWidth;
        lastRootHeight = rootHeight;
        scaleX = getWidth() / rootWidth;
        scaleY = getHeight()  / rootHeight;

        scaleX *= zoom;
        scaleY *= zoom;

        if (scaleX < scaleY) {
            scaleY = scaleX;
        } else {
            scaleX = scaleY;
        }

        offX = (getWidth()  - rootWidth * scaleX) / 2;
        offY = (getHeight() - rootHeight * scaleY) / 2;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (widgets.size() == 0) {
            return;
        }
        Widget root = widgets.get(0);
        rootWidth = root.width();
        rootHeight = root.height();

        computeScale(rootWidth, rootHeight);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setColor(WidgetFrameUtils.theme.backgroundColor());
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.translate(offX, offY);
        g2.scale(scaleX, scaleY);

        if (mReflectOrientation && !Float.isNaN(WidgetFrame.phone_orientation)) {
            g2.rotate(-WidgetFrame.phone_orientation, rootWidth / 2.0, rootHeight / 2.0);
        }
        picker.reset();
        endLayoutMap.clear();
        startLayoutMap.clear();
        HashMap<String, Boolean> map = inspector.getSetting();
        for (Widget widget : widgets) {

            if (widget.isGuideline()) {
                continue;
            }

            widget.draw(g2, widget == root, picker, overWidgets, primarySelected, map);
            endLayoutMap.put(widget.endLayout.getName(), widget.endLayout);
            startLayoutMap.put(widget.startLayout.getName(), widget.startLayout);
        }
        for (Widget widget : widgets) {
            if (widget.isGuideline() || widget == root) {
                continue;
            }
            if (inspector.getSetting().get("Constraints") == true) {
                widget.drawConnections(g2, picker, startLayoutMap, endLayoutMap, root);
            }
        }
    }

    public void rightMouse(MouseEvent e) {
        System.out.println("popup");
        JPopupMenu menu = new JPopupMenu();
        menu.add("Selected");
        for (String wId : overWidgets) {

            if (wId == "root") {
                continue;
            }
            JMenu sub = new JMenu(wId);
            menu.add(sub);
            sub.add(new AbstractAction("centerVertically") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    inspector.main.addConstraint(wId, "centerVertically: 'parent'");
                }
            });
            sub.add(new AbstractAction("centerHorizontally") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    inspector.main.addConstraint(wId, "centerHorizontally: 'parent'");
                }
            });


        }
        menu.show(e.getComponent(), e.getX(), e.getY());
    }

    public void setLayoutInformation(String information) {
        if (information.trim().isEmpty()) {
            return;
        }
        try {
            CLObject list = CLParser.parse(information);
            widgets.clear();
            float pos = Float.NaN;
            for (int i = 0; i < list.size(); i++) {
                CLElement widget = list.get(i);
                if (widget instanceof CLKey) {
                    String widgetId = widget.content();
                    Widget w  = new Widget(widgetId, (CLKey) widget);
                    widgets.add(w);
                    if (!Float.isNaN(w.getInterpolated().interpolatedPos)) {
                        pos = w.getInterpolated().interpolatedPos;
                    }
                }
                if (!(Float.isNaN(pos)) && inspector.mTimeLinePanel != null) {
                    inspector.mTimeLinePanel.setMotionProgress(pos);
                }
            }

            if (primarySelected != null && attDisplay != null) {
                for (Widget widget : widgets) {
                    if (widget.getName().equals(primarySelected)) {
                        attDisplay.setWidgetFrame(widget.getInterpolated());
                    }
                }
            }
            if (display3d != null) {
                display3d.update( widgets);
            }
            repaint();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayWidgetAttributes() {
        for (Widget widget : widgets) {
            if (widget.getName().equals( primarySelected)) {
                attDisplay = WidgetAttributes.display(widget.getInterpolated());
            }
        }

    }

    public void display3d() {
        display3d = CheckLayout3d.create3d(widgets);
    }
}
