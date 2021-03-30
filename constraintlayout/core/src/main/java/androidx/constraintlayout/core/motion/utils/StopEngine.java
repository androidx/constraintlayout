package androidx.constraintlayout.core.motion.utils;

public interface StopEngine {
    String debug(String desc, float time);

    float getVelocity(float x);

    float getInterpolation(float v);

    float getVelocity();

    boolean isStopped();
}
