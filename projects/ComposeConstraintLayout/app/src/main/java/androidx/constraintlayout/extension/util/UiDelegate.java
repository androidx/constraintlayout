package androidx.constraintlayout.extension.util;

public interface UiDelegate {
    public boolean post(Runnable runnable);

    public boolean postDelayed(Runnable runnable, long delayMillis);

    public void invalidate();

    public int getWidth();

    public int getHeight();
}
