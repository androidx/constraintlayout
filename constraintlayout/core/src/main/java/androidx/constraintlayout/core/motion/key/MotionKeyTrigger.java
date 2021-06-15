package androidx.constraintlayout.core.motion.key;

import androidx.constraintlayout.core.motion.MotionWidget;
import androidx.constraintlayout.core.motion.utils.FloatRect;
import androidx.constraintlayout.core.motion.utils.SplineSet;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;

public class MotionKeyTrigger extends MotionKey{
    private static final String TAG = "KeyTrigger";
    public static final String VIEW_TRANSITION_ON_CROSS = "viewTransitionOnCross";
    public static final String VIEW_TRANSITION_ON_POSITIVE_CROSS = "viewTransitionOnPositiveCross";
    public static final String VIEW_TRANSITION_ON_NEGATIVE_CROSS = "viewTransitionOnNegativeCross";
    public static final String POST_LAYOUT = "postLayout";
    public static final String TRIGGER_SLACK = "triggerSlack";
    public static final String TRIGGER_COLLISION_VIEW = "triggerCollisionView";
    public static final String TRIGGER_COLLISION_ID = "triggerCollisionId";
    public static final String TRIGGER_ID = "triggerID";
    public static final String POSITIVE_CROSS = "positiveCross";
    public static final String NEGATIVE_CROSS = "negativeCross";
    public static final String TRIGGER_RECEIVER = "triggerReceiver";
    public static final String CROSS = "CROSS";

    private int mCurveFit = -1;
    private String mCross = null;
    private int mTriggerReceiver = UNSET;
    private String mNegativeCross = null;
    private String mPositiveCross = null;
    private int mTriggerID = UNSET;
    private int mTriggerCollisionId = UNSET;
    private MotionWidget mTriggerCollisionView = null;
    float mTriggerSlack = .1f;
    private boolean mFireCrossReset = true;
    private boolean mFireNegativeReset = true;
    private boolean mFirePositiveReset = true;
    private float mFireThreshold = Float.NaN;
    private float mFireLastPos;
    private boolean mPostLayout = false;
    int mViewTransitionOnNegativeCross = UNSET;
    int mViewTransitionOnPositiveCross = UNSET;
    int mViewTransitionOnCross = UNSET;

    FloatRect mCollisionRect = new FloatRect();
    FloatRect mTargetRect = new FloatRect();
    HashMap<String, Method> mMethodHashMap = new HashMap<>();
    public static final int KEY_TYPE = 5;

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
        switch (tag) {
            case CROSS:
                mCross = value.toString();
                break;
            case TRIGGER_RECEIVER:
                mTriggerReceiver = toInt(value);
                break;
            case NEGATIVE_CROSS:
                mNegativeCross = value.toString();
                break;
            case POSITIVE_CROSS:
                mPositiveCross = value.toString();
                break;
            case TRIGGER_ID:
                mTriggerID = toInt(value);
                break;
            case TRIGGER_COLLISION_ID:
                mTriggerCollisionId = toInt(value);
                break;
            case TRIGGER_COLLISION_VIEW:
                mTriggerCollisionView = (MotionWidget) value;
                break;
            case TRIGGER_SLACK:
                mTriggerSlack = toFloat(value);
                break;
            case POST_LAYOUT:
                mPostLayout = toBoolean(value);
                break;
            case VIEW_TRANSITION_ON_NEGATIVE_CROSS:
                mViewTransitionOnNegativeCross = toInt(value);
                break;
            case VIEW_TRANSITION_ON_POSITIVE_CROSS:
                mViewTransitionOnPositiveCross = toInt(value);
                break;
            case VIEW_TRANSITION_ON_CROSS:
                mViewTransitionOnCross = toInt(value);
                break;

        }
    }


    public MotionKeyTrigger copy(MotionKey src) {
        super.copy(src);
        MotionKeyTrigger k = (MotionKeyTrigger) src;
        mCurveFit = k.mCurveFit;
        mCross = k.mCross;
        mTriggerReceiver = k.mTriggerReceiver;
        mNegativeCross = k.mNegativeCross;
        mPositiveCross = k.mPositiveCross;
        mTriggerID = k.mTriggerID;
        mTriggerCollisionId = k.mTriggerCollisionId;
        mTriggerCollisionView = k.mTriggerCollisionView;
        mTriggerSlack = k.mTriggerSlack;
        mFireCrossReset = k.mFireCrossReset;
        mFireNegativeReset = k.mFireNegativeReset;
        mFirePositiveReset = k.mFirePositiveReset;
        mFireThreshold = k.mFireThreshold;
        mFireLastPos = k.mFireLastPos;
        mPostLayout = k.mPostLayout;
        mCollisionRect = k.mCollisionRect;
        mTargetRect = k.mTargetRect;
        mMethodHashMap = k.mMethodHashMap;
        return this;
    }

    public MotionKey clone() {
        return new MotionKeyTrigger().copy(this);
    }

    public void conditionallyFire(float position, MotionWidget child) {
    }
}
