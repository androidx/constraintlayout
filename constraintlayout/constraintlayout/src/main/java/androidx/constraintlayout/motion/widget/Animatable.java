package androidx.constraintlayout.motion.widget;

/**
 * A helper interface allowing MotionLayout to directly drive custom views
 */
public interface Animatable {
    void setProgress(float progress);
    float getProgress();
}
