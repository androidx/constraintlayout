package androidx.constraintlayout.core.state;

/**
 * This defines the interface to motionScene functionality
 */
public interface CoreMotionScene {

    /**
     * set the Transitions string onto the MotionScene
     * @param elementName the name of the element
     * @param toJSON
     */
    void setTransitionContent(String elementName, String toJSON);

    /**
     * Get the ConstraintSet as a string
     *
     * @param ext
     * @return
     */
    String getConstraintSet(String ext);

    /**
     * set the constraintSet json string
     * @param csName the name of the constraint set
     * @param toJSON the json string of the constraintset
     */
    void setConstraintSetContent(String csName, String toJSON);

    /**
     * set the debug name for remote access
     * @param name name to call this motion scene
     */
    void setDebugName(String name);

    /**
     * reset the force progress flag
     */
    void resetForcedProgress();

    /**
     * Get the progress of the force progress
     * @return
     */
    float getForcedProgress();

    /**
     * get a transition give the name
     * @param str the name of the transition
     * @return the json of the transition
     */
    String getTransition(String str);

    /**
     * get a constraintset
     * @param index of the constraintset
     * @return
     */
    String getConstraintSet(int index);
}
