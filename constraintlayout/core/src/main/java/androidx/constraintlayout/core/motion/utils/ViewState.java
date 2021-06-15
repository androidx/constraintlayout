package androidx.constraintlayout.core.motion.utils;

import androidx.constraintlayout.core.motion.MotionWidget;

public class ViewState {
    public float rotation;
    public int left, top, right, bottom;

    public void getState(MotionWidget v) {
        left = (int) v.getLeft();
        top = (int) v.getTop();
        right = (int) v.getRight();
        bottom = (int) v.getBottom();
        rotation = (int) v.getRotationZ();
    }

    public int width() {
        return right - left;
    }

    public int height() {
        return bottom - top;
    }
}
