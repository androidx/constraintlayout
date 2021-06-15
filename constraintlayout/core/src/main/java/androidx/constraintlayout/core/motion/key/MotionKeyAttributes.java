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
    private boolean mVisibility = false;
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
}
