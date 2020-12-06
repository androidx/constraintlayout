package androidx.constraintlayout.motion.widget;

import android.graphics.Canvas;
import android.view.View;

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
     * @param motionLayout
     * @param controllerMap
     */
    void onPreSetup(MotionLayout motionLayout, HashMap<View, MotionController> controllerMap);

    /**
     * This is called after motionLayout read motionScene and assembles all constraintSets
     * @param motionLayout
     */
    void onFinishedMotionScene(MotionLayout motionLayout);
}
