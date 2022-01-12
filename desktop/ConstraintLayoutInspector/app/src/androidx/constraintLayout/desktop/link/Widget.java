package androidx.constraintLayout.desktop.link;

import androidx.constraintLayout.desktop.scan.KeyFrameNodes;
import androidx.constraintLayout.desktop.scan.LayoutConstraints;
import androidx.constraintLayout.desktop.scan.WidgetFrameUtils;
import androidx.constraintLayout.desktop.ui.utils.Debug;
import androidx.constraintLayout.desktop.utils.ScenePicker;
import androidx.constraintlayout.core.parser.CLKey;
import androidx.constraintlayout.core.parser.CLObject;
import androidx.constraintlayout.core.parser.CLParsingException;
import androidx.constraintlayout.core.state.WidgetFrame;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.HashMap;
import java.util.HashSet;

import static androidx.constraintLayout.desktop.scan.WidgetFrameUtils.DASH_OUTLINE;

public class Widget {
    private WidgetFrameUtils.LayoutColors layoutColors = new WidgetFrameUtils.LayoutColors();
    WidgetFrame interpolated = new WidgetFrame();
    WidgetFrame start = new WidgetFrame();
    WidgetFrame end = new WidgetFrame();
    public LayoutConstraints endLayout = new LayoutConstraints();
    public LayoutConstraints startLayout = new LayoutConstraints();
    String name = "unknown";
    Path2D.Float path = new Path2D.Float();
    Font drawFont = new Font("Helvetica", Font.ITALIC, 32);
    KeyFrameNodes keyFrames = new KeyFrameNodes();
    boolean isGuideline = false;
    String id;

    public Widget(String id, CLKey key) {
        this.id = id;
        name = key.content();
        endLayout.setName(name);
        startLayout.setName(name);
        CLObject sections = (CLObject) key.getValue();
        int count = sections.size();
        for (int i = 0; i < count; i++) {

            CLKey sec = null;
            try {
                sec = (CLKey) sections.get(i);
            } catch (CLParsingException e) {
                e.printStackTrace();
                return;
            }
            try {
                switch (sec.content()) {
                    case "start":
                        WidgetFrameUtils.deserialize(sec, end, endLayout);
                        break;
                    case "end":
                        WidgetFrameUtils.deserialize(sec, start, startLayout);
                        break;
                    case "interpolated":
                        WidgetFrameUtils.deserialize(sec, interpolated, null);
                        break;
                    case "path":
                        WidgetFrameUtils.getPath(sec, path);
                        break;
                    case "keyPos":
                        keyFrames.setKeyFramesPos(sec);
                        break;
                    case "keyTypes":
                        keyFrames.setKeyFramesTypes(sec);
                        break;
                    case "keyFrames":
                        keyFrames.setKeyFramesProgress(sec);
                        break;
                }
            } catch (Exception e) {

            }
        }
    }

    public int width() {
        return interpolated.width();
    }

    public int height() {
        return interpolated.height();
    }

    public void draw(
            Graphics2D g,
            Boolean drawRoot,
            ScenePicker scenePicker,
            HashSet<String> over,
            String selected,
            java.util.HashMap<String, Boolean> map
    ) {
        if (map == null) {
            return;
        }
        AffineTransform renderScale = WidgetFrameUtils.getTouchScale(g);
        int END_LOOK = WidgetFrameUtils.OUTLINE | DASH_OUTLINE;
        boolean pre = (map.get("PreTransform") == true);
        if (map.get("Start") == true) {
            WidgetFrameUtils.render(start, g, null, WidgetFrameUtils.FrameType.START, END_LOOK, pre, null, startLayout);
        }
        if (map.get("Path") == true) {
            keyFrames.render(g);
        }
        if (map.get("End") == true) {
            WidgetFrameUtils.render(end, g, null, WidgetFrameUtils.FrameType.END, END_LOOK, pre, null, endLayout);
        }
        if (map.get("Path") == true) {
            WidgetFrameUtils.renderPath(path, g);
        }


        var style = WidgetFrameUtils.FILL;
        interpolated.name = name;
        var type = WidgetFrameUtils.FrameType.INTERPOLATED;
        if (drawRoot) {
            type = WidgetFrameUtils.FrameType.ROOT;
        } else {
            if (over.contains(interpolated.name)) {
                type = WidgetFrameUtils.FrameType.INTERPOLATED_HOVER;
            }
            if (selected != null && selected.equals(interpolated.name)) {
                type = WidgetFrameUtils.FrameType.INTERPOLATED_SELECTED;
            }
        }
        g.setFont(drawFont);
        style += WidgetFrameUtils.TEXT;
        WidgetFrameUtils.render(interpolated, g, scenePicker,
                type, style, pre, renderScale, (drawRoot) ? endLayout : null);
        if (drawRoot) {
            startLayout.setBounds(endLayout.getBounds());
        }
    }

    public void drawConnections(
            Graphics2D g,
            ScenePicker picker,
            HashMap<String, LayoutConstraints> startLayoutMap,
            HashMap<String, LayoutConstraints> endLayoutMap,
            Widget root
    ) {
        startLayout.render(g, startLayoutMap, picker, root.endLayout);
        endLayout.render(g, endLayoutMap, picker, root.endLayout);
    }

    public WidgetFrame getInterpolated() {
        return interpolated;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public boolean isGuideline() {
        return isGuideline;
    }
    public void setGuideline(boolean v) {
        isGuideline = v;
    }
}
