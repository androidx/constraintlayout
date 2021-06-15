package androidx.constraintlayout.core.motion;


import androidx.constraintlayout.core.motion.utils.FloatRect;
import androidx.constraintlayout.core.motion.utils.Rect;
import androidx.constraintlayout.core.state.WidgetFrame;
import androidx.constraintlayout.core.widgets.ConstraintWidget;

public class MotionWidget {
    WidgetFrame widgetFrame;
    Motion motion = new Motion();
    PropertySet propertySet = new PropertySet();

    public MotionWidget getParent() {
        return null;
    }

    public MotionWidget findViewById(int mTransformPivotTarget) {
        return null;
    }

    public int getTop() {
        return widgetFrame.top;
    }

    public int getLeft() {
        return widgetFrame.left;
    }

    public int getBottom() {
        return widgetFrame.bottom;
    }

    public int getRight() {
        return widgetFrame.right;
    }

    public void setPivotX(float px) {
    }

    public void setPivotY(float py) {
    }

    public void setVisibility(int visibility) {
    }

    public String getName() {
        return getClass().getSimpleName();
    }

    public void layout(int l, int t, int r, int b) {
        setBounds(l, t, r, b);
    }

    public String toString() {
        return widgetFrame.left + ","+ widgetFrame.top + ", " + widgetFrame.right + ", " + widgetFrame.bottom;
    }

    public void setBounds(int left, int top, int right, int bottom) {
        if (widgetFrame == null) {
            widgetFrame =   new WidgetFrame((ConstraintWidget) null);
        }
        widgetFrame.top = top;
        widgetFrame.left = left;
        widgetFrame.right = right;
        widgetFrame.bottom = bottom;
    }

    /**
     * @hide
     */
    public static class Motion {
        public boolean mApply = false;
        public int mAnimateRelativeTo = UNSET;
        public int mAnimateCircleAngleTo = 0;
        public String mTransitionEasing = null;
        public int mPathMotionArc = UNSET;
        public int mDrawPath = 0;
        public float mMotionStagger = Float.NaN;
        public int mPolarRelativeTo = UNSET;
        public float mPathRotate = Float.NaN;
        public float mQuantizeMotionPhase = Float.NaN;
        public int mQuantizeMotionSteps = UNSET;
        public String mQuantizeInterpolatorString = null;
        public int mQuantizeInterpolatorType = INTERPOLATOR_UNDEFINED; // undefined
        public int mQuantizeInterpolatorID = -1;
        private static final int INTERPOLATOR_REFERENCE_ID = -2;
        private static final int SPLINE_STRING = -1;
        private static final int INTERPOLATOR_UNDEFINED = -3;
    }
    public static class PropertySet {
        public boolean mApply = false;
        public int visibility = VISIBLE;
        public int mVisibilityMode = VISIBILITY_MODE_NORMAL;
        public float alpha = 1;
        public float mProgress = Float.NaN;
    }

        public static final int VISIBILITY_MODE_NORMAL = 0;
    public static final int VISIBILITY_MODE_IGNORE = 1;
    private static final int INTERNAL_MATCH_PARENT = -1;
    private static final int INTERNAL_WRAP_CONTENT = -2;
    public static final int INVISIBLE = 0;
    public static final int VISIBLE = 4;
    private static final int INTERNAL_MATCH_CONSTRAINT = -3;
    private static final int INTERNAL_WRAP_CONTENT_CONSTRAINED = -4;

    public static final int ROTATE_NONE = 0;
    public static final int ROTATE_PORTRATE_OF_RIGHT = 1;
    public static final int ROTATE_PORTRATE_OF_LEFT = 2;
    public static final int ROTATE_RIGHT_OF_PORTRATE = 3;
    public static final int ROTATE_LEFT_OF_PORTRATE = 4;
    public static final int UNSET = -1;
    public static final int MATCH_CONSTRAINT = 0;
    public static final int PARENT_ID = 0;
    public static final int FILL_PARENT = -1;
    public static final int MATCH_PARENT = -1;
    public static final int WRAP_CONTENT = -2;
    public static final int GONE_UNSET = Integer.MIN_VALUE;
    public static final int MATCH_CONSTRAINT_WRAP = ConstraintWidget.MATCH_CONSTRAINT_WRAP;

    public float getRotationX() {
        return widgetFrame.rotationX;
    }

    public void setRotationX(float rotationX) {
        widgetFrame.rotationX = rotationX;
    }

    public float getRotationY() {
        return widgetFrame.rotationY;
    }

    public void setRotationY(float rotationY) {
        widgetFrame.rotationY = rotationY;
    }

    public float getRotationZ() {
        return widgetFrame.rotationZ;
    }

    public void setRotationZ(float rotationZ) {
        widgetFrame.rotationZ = rotationZ;
    }

    public float getTranslationX() {
        return widgetFrame.translationX;
    }

    public void setTranslationX(float translationX) {
        widgetFrame.translationX = translationX;
    }

    public float getTranslationY() {
        return widgetFrame.translationY;
    }

    public void setTranslationY(float translationY) {
        widgetFrame.translationY = translationY;
    }

    public void setTranslationZ(float translationY) {

    }

    public float getTranslationZ() {
        return 0;
    }

    public float getScaleX() {
        return widgetFrame.scaleX;
    }

    public void setScaleX(float scaleX) {
        widgetFrame.scaleX = scaleX;
    }

    public float getScaleY() {
        return widgetFrame.scaleY;
    }

    public void setScaleY(float scaleY) {
        widgetFrame.scaleY = scaleY;
    }

    public int getVisibility() {
        return 0;
    }

    public float getPivotX() {
        return 0;
    }

    public float getPivotY() {
        return 0;
    }

    public float getAlpha() {
        return 1;
    }

    public int getX() {
        return widgetFrame.left;
    }

    public int getY() {
        return widgetFrame.top;
    }

    public int getWidth() {
        return widgetFrame.right - widgetFrame.left;
    }

    public int getHeight() {
        return widgetFrame.bottom - widgetFrame.top;
    }
}
