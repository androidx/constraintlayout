package androidx.constraintlayout.core.motion.utils;

public class FloatRect {
    public float bottom;
    public float left;
    public float right;
    public float top;
    public final float centerX() {
        return (left + right) * 0.5f;
    }
    public final float centerY() {
        return (top + bottom) * 0.5f;
    }

}
