package androidx.constraintlayout.core.motion;


import androidx.constraintlayout.core.motion.key.MotionKeyAttributes;
import androidx.constraintlayout.core.motion.utils.FloatRect;
import androidx.constraintlayout.core.motion.utils.Rect;
import androidx.constraintlayout.core.motion.utils.TypedValues;
import androidx.constraintlayout.core.state.WidgetFrame;
import androidx.constraintlayout.core.widgets.ConstraintWidget;

public class MotionWidget implements TypedValues {
    WidgetFrame widgetFrame = new WidgetFrame();
    Motion motion = new Motion();
    PropertySet propertySet = new PropertySet();
    private float mProgress;
    float mTransitionPathRotate;

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

    /**
     * @hide
     */
    public static class Motion {
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
        public int visibility = VISIBLE;
        public int mVisibilityMode = VISIBILITY_MODE_NORMAL;
        public float alpha = 1;
        public float mProgress = Float.NaN;
    }

    public MotionWidget() {

    }

    public MotionWidget getParent() {
        return null;
    }

    public MotionWidget findViewById(int mTransformPivotTarget) {
        return null;
    }

    public void setVisibility(int visibility) {
        propertySet.visibility = visibility;
    }

    public String getName() {
        return getClass().getSimpleName();
    }

    public void layout(int l, int t, int r, int b) {
        setBounds(l, t, r, b);
    }

    public String toString() {
        return widgetFrame.left + ", " + widgetFrame.top + ", " + widgetFrame.right + ", " + widgetFrame.bottom;
    }

    public void setBounds(int left, int top, int right, int bottom) {
        if (widgetFrame == null) {
            widgetFrame = new WidgetFrame((ConstraintWidget) null);
        }
        widgetFrame.top = top;
        widgetFrame.left = left;
        widgetFrame.right = right;
        widgetFrame.bottom = bottom;
    }

    public MotionWidget(WidgetFrame f) {
        widgetFrame = f;
    }

    @Override
    public boolean setValue(int id, int value) {
        return setValueAttributes(id, value);
    }

    @Override
    public boolean setValue(int id, float value) {
        boolean set = setValueAttributes(id, value);
        if (set) {
            return true;
        }
        return setValueMotion(id, value);
    }

    @Override
    public boolean setValue(int id, String value) {
       return setValueMotion(id, value);
    }

    @Override
    public boolean setValue(int id, boolean value) {
        return false;
    }

    public boolean setValueMotion(int id, int value) {
        switch (id) {
            case TypedValues.Motion.TYPE_ANIMATE_RELATIVE_TO:
                motion.mAnimateRelativeTo = value;
                break;
            case TypedValues.Motion.TYPE_ANIMATE_CIRCLEANGLE_TO:
                motion.mAnimateCircleAngleTo = value;
                break;
            case TypedValues.Motion.TYPE_PATHMOTION_ARC:
                motion.mPathMotionArc = value;
                break;
            case TypedValues.Motion.TYPE_DRAW_PATH:
                motion.mDrawPath = value;
                break;
            case TypedValues.Motion.TYPE_POLAR_RELATIVETO:
                motion.mPolarRelativeTo = value;
                break;
            case TypedValues.Motion.TYPE_QUANTIZE_MOTIONSTEPS:
                motion.mQuantizeMotionSteps = value;
                break;
            case TypedValues.Motion.TYPE_QUANTIZE_INTERPOLATOR_TYPE:
                motion.mQuantizeInterpolatorType = value;
                break; // undefined
            case TypedValues.Motion.TYPE_QUANTIZE_INTERPOLATOR_ID:
                motion.mQuantizeInterpolatorID = value;
                break;
            default:
                return false;
        }
        return true;
    }

    public boolean setValueMotion(int id, String value) {
        switch (id) {

            case TypedValues.Motion.TYPE_EASING:
                motion.mTransitionEasing = value;
                break;
            case TypedValues.Motion.TYPE_QUANTIZE_INTERPOLATOR:
                motion.mQuantizeInterpolatorString = value;
                break;
            default:
                return false;
        }
        return true;
    }

    public boolean setValueMotion(int id, float value) {
        switch (id) {
            case TypedValues.Motion.TYPE_STAGGER:
                motion.mMotionStagger = value;
                break;
            case TypedValues.Motion.TYPE_PATH_ROTATE:
                motion.mPathRotate = value;
                break;
            case TypedValues.Motion.TYPE_QUANTIZE_MOTION_PHASE:
                motion.mQuantizeMotionPhase = value;
                break;
            default:
                return false;
        }
        return true;
    }

    /**
     * Sets the attributes
     *
     * @param id
     * @param value
     */
    public boolean setValueAttributes(int id, float value) {
        switch (id) {
            case TypedValues.Attributes.TYPE_ALPHA:
                widgetFrame.alpha = value;
                break;
            case TypedValues.Attributes.TYPE_TRANSLATION_X:
                widgetFrame.translationX = value;
                break;
            case TypedValues.Attributes.TYPE_TRANSLATION_Y:
                widgetFrame.translationY = value;
                break;
            case TypedValues.Attributes.TYPE_TRANSLATION_Z:
                 widgetFrame.translationZ = value;
                break;
            case TypedValues.Attributes.TYPE_ROTATION_X:
                widgetFrame.rotationX = value;
                break;
            case TypedValues.Attributes.TYPE_ROTATION_Y:
                widgetFrame.rotationY = value;
                break;
            case TypedValues.Attributes.TYPE_ROTATION_Z:
                widgetFrame.rotationZ = value;
                break;
            case TypedValues.Attributes.TYPE_SCALE_X:
                widgetFrame.scaleX = value;
                break;
            case TypedValues.Attributes.TYPE_SCALE_Y:
                widgetFrame.scaleY = value;
                break;
            case TypedValues.Attributes.TYPE_PIVOT_X:
                widgetFrame.pivotX = value;
                break;
            case TypedValues.Attributes.TYPE_PIVOT_Y:
                widgetFrame.pivotY = value;
                break;
            case TypedValues.Attributes.TYPE_PROGRESS:
                mProgress = value;
                break;
            case TypedValues.Attributes.TYPE_PATH_ROTATE:
                mTransitionPathRotate = value;
                break;
            default:
                return false;
        }
        return true;
    }


    @Override
    public int getId(String name) {
        int ret = TypedValues.Attributes.getId(name);
        if (ret != -1) {
            return ret;
        }
        return TypedValues.Motion.getId(name);
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
        widgetFrame.pivotX = px;
    }

    public void setPivotY(float py) {
        widgetFrame.pivotY = py;
    }

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

    public void setTranslationZ(float tz) {
            widgetFrame.translationZ = tz;
    }

    public float getTranslationZ() {
        return widgetFrame.translationZ;
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
        return propertySet.visibility;
    }

    public float getPivotX() {
        return widgetFrame.pivotX;
    }

    public float getPivotY() {
        return widgetFrame.pivotY;
    }

    public float getAlpha() {
        return propertySet.alpha;
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

    public WidgetFrame getWidgetFrame() {
        return widgetFrame;
    }


}
