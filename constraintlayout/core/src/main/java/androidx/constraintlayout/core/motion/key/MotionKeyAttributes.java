package androidx.constraintlayout.core.motion.key;

import androidx.constraintlayout.core.motion.utils.SplineSet;

import java.util.HashMap;
import java.util.HashSet;

public class MotionKeyAttributes extends MotionKey {
    static final String NAME = "KeyAttribute";
    private static final String TAG = "KeyAttributes";
    private static final boolean DEBUG = false;
    private String mTransitionEasing;
    private int mCurveFit = -1;
    private int mVisibility = 0;
    private float mAlpha = Float.NaN;
    private float mElevation = Float.NaN;
    private float mRotation = Float.NaN;
    private float mRotationX = Float.NaN;
    private float mRotationY = Float.NaN;
    private float mPivotX = Float.NaN;
    private float mPivotY = Float.NaN;
    private float mTransitionPathRotate = Float.NaN;
    private float mScaleX = Float.NaN;
    private float mScaleY = Float.NaN;
    private float mTranslationX = Float.NaN;
    private float mTranslationY = Float.NaN;
    private float mTranslationZ = Float.NaN;
    private float mProgress = Float.NaN;

    public static final int TYPE_CURVE_FIT = 301;
    public static final int TYPE_VISIBILITY = 302;
    public static final int TYPE_ALPHA = 303;
    public static final int TYPE_TRANSLATION_X = 304;
    public static final int TYPE_TRANSLATION_Y = 305;
    public static final int TYPE_TRANSLATION_Z = 306;
    public static final int TYPE_ROTATION_X = 307;
    public static final int TYPE_ROTATION_Y = 308;
    public static final int TYPE_ROTATION_Z = 309;
    public static final int TYPE_SCALE_X = 310;
    public static final int TYPE_SCALE_Y = 311;
    public static final int TYPE_PIVIOT_X = 312;
    public static final int TYPE_PIVIOT_Y = 313;
    public static final int TYPE_PROGRESS = 314;
    public static final int TYPE_PATH_ROTATE = 315;
    public static final int TYPE_EASING= 316;



    public static final int KEY_TYPE = 1;

    {
        mType = KEY_TYPE;
        mCustomConstraints = new HashMap<>();
    }


    @Override
    public void getAttributeNames(HashSet<String> attributes) {

    }

    @Override
    public void addValues(HashMap<String, SplineSet> splines) {

    }

    @Override
    public void setValue(String tag, Object value) {

    }

    @Override
    public MotionKey clone() {
        return null;
    }

    public void setValue(int type, int value){

        switch (type) {
            case TYPE_VISIBILITY:
                mVisibility = value;
                break;
            case TYPE_CURVE_FIT:
                mCurveFit = value;
                break;
            case TYPE_FRAME_POSITION:
                mFramePosition = value;
                break;
            default:
                super.setValue(type,value);
        }
    }

    public void setValue(int type, float value) {
        switch (type) {

            case TYPE_ALPHA:
                mAlpha = value;
                break;
            case TYPE_TRANSLATION_X:
                mTranslationX = value;
                break;
            case TYPE_TRANSLATION_Y:
                mTranslationY = value;
                break;
            case TYPE_TRANSLATION_Z:
                mTranslationZ = value;
                break;
            case TYPE_ROTATION_X:
                mRotationX = value;
                break;
            case TYPE_ROTATION_Y:
                mRotationY = value;
                break;
            case TYPE_ROTATION_Z:
                mRotation = value;
                break;
            case TYPE_SCALE_X:
                mScaleX = value;
                break;
            case TYPE_SCALE_Y:
                mScaleY = value;
                break;
            case TYPE_PIVIOT_X:
                mPivotX = value;
                break;
            case TYPE_PIVIOT_Y:
                mPivotY = value;
                break;
            case TYPE_PROGRESS:
                mProgress = value;
                break;
            case TYPE_PATH_ROTATE:
                mTransitionPathRotate = value;
                break;
            default:
                super.setValue(type, value);
        }
    }

    public void setValue(int type, String value) {
        switch (type) {
            case TYPE_EASING:
                mTransitionEasing  = value;
                break;

            case TYPE_TARGET:
                mTargetString = value;
                break;
            default:
                super.setValue(type, value);
        }
    }

}
