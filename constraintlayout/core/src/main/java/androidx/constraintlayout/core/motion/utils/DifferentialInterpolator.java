package androidx.constraintlayout.core.motion.utils;

public interface DifferentialInterpolator {
    abstract public float getInterpolation(float v);
    public abstract float getVelocity();
}
