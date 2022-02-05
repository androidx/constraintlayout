package androidx.constraintlayout.core;

import androidx.constraintlayout.core.motion.utils.TypedBundle;
import androidx.constraintlayout.core.widgets.ConstraintAnchor;

import java.util.HashMap;

public class ConstraintDelta {
    public static final int LAYOUT_WIDTH = 1;
    public static final int LAYOUT_HEIGHT = 2;

    public static final int BOTTOM_MARGIN = 7;
    public static final int TOP_MARGIN = 8;
    public static final int LEFT_MARGIN = 9;
    public static final int RIGHT_MARGIN = 10;
    public static final int START_MARGIN = 11;
    public static final int END_MARGIN = 12;
    public static final int BASELINE_MARGIN = 13;

    public static final int GONE_BOTTOM_MARGIN = 14;
    public static final int GONE_LEFT_MARGIN = 15;
    public static final int GONE_RIGHT_MARGIN = 16;
    public static final int GONE_TOP_MARGIN = 17;

    public static final int GUIDE_BEGIN = 18;
    public static final int GUIDE_END = 19;
    public static final int GUIDE_PERCENT = 20;
    public static final int HORIZONTAL_BIAS = 21;
    public static final int VERTICAL_BIAS = 22;
    public static final int HORIZONTAL_WEIGHT = 23;
    public static final int VERTICAL_WEIGHT = 24;
    public static final int CIRCLE_RADIUS = 25;
    public static final int CIRCLE_ANGLE = 26;
    public static final int WIDTH_PERCENT = 27;
    public static final int HEIGHT_PERCENT = 28;

    HashMap<Integer, ConstraintDeltas> deltas = new HashMap<>();

    public static String getLoc() {
        StackTraceElement s = new Throwable().getStackTrace()[1];
        return ".(" + s.getFileName() + ":" + s.getLineNumber() + ") " + s.getMethodName() + "()";
    }

    ConstraintDeltas get(int id) {
        if (!deltas.containsKey(id)) {
            ConstraintDeltas d = new ConstraintDeltas();
            d.mViewId = id;
            deltas.put(id, d);
            return d;
        }
        return deltas.get(id);
    }

    public void setValue(int id, int type, int value) {
        get(id).bundle.add(type, value);
    }
    public void setValue(int id, int type, float value) {
        get(id).bundle.add(type, value);
    }

    private void updateGone(ConstraintAnchor anchor, int value) {
        if (anchor != null) {
            anchor.setGoneMargin(value);
        }
    }

    public TypedBundle getBundle(int id) {
        if (deltas.containsKey(id)) {
            return deltas.get(id).bundle;
        }
        return null;
    }

    public interface ConstraintHolder {
        void setEdits(int id, ConstraintDeltas deltas);
    }

    public static class ConstraintDeltas {
        int mViewId;
        String mTargetString;
        TypedBundle bundle = new TypedBundle();

    }
}
