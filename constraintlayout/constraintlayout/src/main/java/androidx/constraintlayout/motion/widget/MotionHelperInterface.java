package androidx.constraintlayout.motion.widget;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.accessibility.AccessibilityEventSource;

import java.util.HashMap;

public interface MotionHelperInterface extends  Animatable, MotionLayout.TransitionListener {
    boolean isUsedOnShow();

    boolean isUseOnHide();

    boolean isDecorator();

    void onPreDraw(Canvas canvas);

    void onPostDraw(Canvas canvas);

    /**
     * Called after motionController is populated with start and end and keyframes.
     *
     * @param mFrameArrayList
     */
    void preSetup(HashMap<View, MotionController> mFrameArrayList);
}
