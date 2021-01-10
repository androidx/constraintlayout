package androidx.constraintlayout.motion.widget;

import androidx.annotation.NonNull;

public abstract class TransitionAdapter implements MotionLayout.TransitionListener {
    @Override
    public void onTransitionStarted(@NonNull MotionLayout motionLayout, int startId, int endId) {

    }

    @Override
    public void onTransitionChange(@NonNull MotionLayout motionLayout, int startId, int endId, float progress) {

    }

    @Override
    public void onTransitionCompleted(@NonNull MotionLayout motionLayout, int currentId) {

    }

    @Override
    public void onTransitionTrigger(@NonNull MotionLayout motionLayout, int triggerId, boolean positive, float progress) {

    }
}
