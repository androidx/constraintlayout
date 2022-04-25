package androidx.constraintlayout.core.state;

/**
 * This defines the interface to motionScene functionality
 * Todo does this belong in ConstraintSet Parser or in a top level
 */
public interface CoreMotionScene {

    void setTransitionContent(String elementName, String toJSON);

    String getConstraintSet(String ext);

    void setConstraintSetContent(String csName, String toJSON);

    void setDebugName(String name);

    void resetForcedProgress();

    float getForcedProgress();

    String getTransition(String str);

    String getConstraintSet(int index);
}
