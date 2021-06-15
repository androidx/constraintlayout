package androidx.constraintlayout.core.motion.key;

import androidx.constraintlayout.core.motion.CustomAttribute;
import androidx.constraintlayout.core.motion.MotionWidget;
import androidx.constraintlayout.core.motion.utils.Oscillator;
import androidx.constraintlayout.core.motion.utils.SplineSet;
import androidx.constraintlayout.core.motion.utils.TimeCycleSplineSet;
import androidx.constraintlayout.core.motion.utils.Utils;

import java.util.HashMap;
import java.util.HashSet;

public class MotionKeyTimeCycle extends MotionKey {
    static final String NAME = "KeyTimeCycle";
    private static final String TAG = NAME;
    public static final String WAVE_PERIOD = "wavePeriod";
    public static final String WAVE_OFFSET = "waveOffset";
    public static final String WAVE_SHAPE = "waveShape";
    public static final int SHAPE_SIN_WAVE = Oscillator.SIN_WAVE;
    public static final int SHAPE_SQUARE_WAVE = Oscillator.SQUARE_WAVE;
    public static final int SHAPE_TRIANGLE_WAVE = Oscillator.TRIANGLE_WAVE;
    public static final int SHAPE_SAW_WAVE = Oscillator.SAW_WAVE;
    public static final int SHAPE_REVERSE_SAW_WAVE = Oscillator.REVERSE_SAW_WAVE;
    public static final int SHAPE_COS_WAVE = Oscillator.COS_WAVE;
    public static final int SHAPE_BOUNCE = Oscillator.BOUNCE;

    private String mTransitionEasing;
    private int mCurveFit = -1;
    private float mAlpha = Float.NaN;
    private float mElevation = Float.NaN;
    private float mRotation = Float.NaN;
    private float mRotationX = Float.NaN;
    private float mRotationY = Float.NaN;
    private float mTransitionPathRotate = Float.NaN;
    private float mScaleX = Float.NaN;
    private float mScaleY = Float.NaN;
    private float mTranslationX = Float.NaN;
    private float mTranslationY = Float.NaN;
    private float mTranslationZ = Float.NaN;
    private float mProgress = Float.NaN;
    private int mWaveShape = 0;
    private String mCustomWaveShape = null; // TODO add support of custom wave shapes in KeyTimeCycle
    private float mWavePeriod = Float.NaN;
    private float mWaveOffset = 0;
    public static final int KEY_TYPE = 3;

    {
        mType = KEY_TYPE;
        mCustomConstraints = new HashMap<>();
    }

    public void addTimeValues(HashMap<String, TimeCycleSplineSet> splines) {
        for (String s : splines.keySet()) {
            TimeCycleSplineSet splineSet = splines.get(s);
            if (splineSet == null) {
                continue;
            }
            if (s.startsWith(CUSTOM)) {
                String cKey = s.substring(CUSTOM.length() + 1);
                CustomAttribute cValue = mCustomConstraints.get(cKey);
                if (cValue != null) {
                    ((TimeCycleSplineSet.CustomSet) splineSet).setPoint(mFramePosition, cValue, mWavePeriod, mWaveShape, mWaveOffset);
                }
                continue;
            }
            switch (s) {
                case MotionKey.ALPHA:
                    if (!Float.isNaN(mAlpha)) {
                        splineSet.setPoint(mFramePosition, mAlpha, mWavePeriod, mWaveShape, mWaveOffset);
                    }
                    break;
                case MotionKey.ELEVATION:
                    if (!Float.isNaN(mElevation)) {
                        splineSet.setPoint(mFramePosition, mElevation, mWavePeriod, mWaveShape, mWaveOffset);
                    }
                    break;
                case MotionKey.ROTATION:
                    if (!Float.isNaN(mRotation)) {
                        splineSet.setPoint(mFramePosition, mRotation, mWavePeriod, mWaveShape, mWaveOffset);
                    }
                    break;
                case MotionKey.ROTATION_X:
                    if (!Float.isNaN(mRotationX)) {
                        splineSet.setPoint(mFramePosition, mRotationX, mWavePeriod, mWaveShape, mWaveOffset);
                    }
                    break;
                case MotionKey.ROTATION_Y:
                    if (!Float.isNaN(mRotationY)) {
                        splineSet.setPoint(mFramePosition, mRotationY, mWavePeriod, mWaveShape, mWaveOffset);
                    }
                    break;
                case MotionKey.TRANSITION_PATH_ROTATE:
                    if (!Float.isNaN(mTransitionPathRotate)) {
                        splineSet.setPoint(mFramePosition, mTransitionPathRotate, mWavePeriod, mWaveShape, mWaveOffset);
                    }
                    break;
                case MotionKey.SCALE_X:
                    if (!Float.isNaN(mScaleX)) {
                        splineSet.setPoint(mFramePosition, mScaleX, mWavePeriod, mWaveShape, mWaveOffset);
                    }
                    break;
                case MotionKey.SCALE_Y:
                    if (!Float.isNaN(mScaleY)) {
                        splineSet.setPoint(mFramePosition, mScaleY, mWavePeriod, mWaveShape, mWaveOffset);
                    }
                    break;
                case MotionKey.TRANSLATION_X:
                    if (!Float.isNaN(mTranslationX)) {
                        splineSet.setPoint(mFramePosition, mTranslationX, mWavePeriod, mWaveShape, mWaveOffset);
                    }
                    break;
                case MotionKey.TRANSLATION_Y:
                    if (!Float.isNaN(mTranslationY)) {
                        splineSet.setPoint(mFramePosition, mTranslationY, mWavePeriod, mWaveShape, mWaveOffset);
                    }
                    break;
                case MotionKey.TRANSLATION_Z:
                    if (!Float.isNaN(mTranslationZ)) {
                        splineSet.setPoint(mFramePosition, mTranslationZ, mWavePeriod, mWaveShape, mWaveOffset);
                    }
                    break;
                case MotionKey.PROGRESS:
                    if (!Float.isNaN(mProgress)) {
                        splineSet.setPoint(mFramePosition, mProgress, mWavePeriod, mWaveShape, mWaveOffset);
                    }
                    break;
                default:
                     Utils.loge("KeyTimeCycles", "UNKNOWN addValues \"" + s + "\"");
            }
        }
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
            case ALPHA:
                mAlpha = toFloat(value);
                break;
            case CURVEFIT:
                mCurveFit = toInt(value);
                break;
            case ELEVATION:
                mElevation = toFloat(value);
                break;
            case MOTIONPROGRESS:
                mProgress = toFloat(value);
                break;
            case ROTATION:
                mRotation = toFloat(value);
                break;
            case ROTATION_X:
                mRotationX = toFloat(value);
                break;
            case ROTATION_Y:
                mRotationY = toFloat(value);
                break;
            case SCALE_X:
                mScaleX = toFloat(value);
                break;
            case SCALE_Y:
                mScaleY = toFloat(value);
                break;
            case TRANSITIONEASING:
                mTransitionEasing = value.toString();
                break;
            case TRANSITION_PATH_ROTATE:
                mTransitionPathRotate = toFloat(value);
                break;
            case TRANSLATION_X:
                mTranslationX = toFloat(value);
                break;
            case TRANSLATION_Y:
                mTranslationY = toFloat(value);
                break;
            case TRANSLATION_Z:
                mTranslationZ = toFloat(value);
                break;
            case WAVE_PERIOD:
                mWavePeriod = toFloat(value);
                break;
            case WAVE_OFFSET:
                mWaveOffset = toFloat(value);
                break;
            case WAVE_SHAPE:
                if (value instanceof Integer) {
                    mWaveShape = toInt(value);
                } else {
                    mWaveShape = Oscillator.CUSTOM;
                    mCustomWaveShape = value.toString();
                }
                break;
        }

    }
    public MotionKeyTimeCycle copy(MotionKey src) {
        super.copy(src);
        MotionKeyTimeCycle k = (MotionKeyTimeCycle) src;
        mTransitionEasing = k.mTransitionEasing;
        mCurveFit = k.mCurveFit;
        mWaveShape = k.mWaveShape;
        mWavePeriod = k.mWavePeriod;
        mWaveOffset = k.mWaveOffset;
        mProgress = k.mProgress;
        mAlpha = k.mAlpha;
        mElevation = k.mElevation;
        mRotation = k.mRotation;
        mTransitionPathRotate = k.mTransitionPathRotate;
        mRotationX = k.mRotationX;
        mRotationY = k.mRotationY;
        mScaleX = k.mScaleX;
        mScaleY = k.mScaleY;
        mTranslationX = k.mTranslationX;
        mTranslationY = k.mTranslationY;
        mTranslationZ = k.mTranslationZ;
        return this;
    }

    public MotionKey clone() {
        return new MotionKeyTimeCycle().copy(this);
    }
}
