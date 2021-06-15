package androidx.constraintlayout.core.motion.utils;

public class Rect {
    public int bottom;
    public int left;
    public int right;
    public int top;

    public int width() {
        return right - left;
    }

    public int height() {
        return bottom - top;
    }
}
