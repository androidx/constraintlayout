package androidx.constraintlayout.core.motion.key;

import androidx.constraintlayout.core.motion.utils.Oscillator;
import androidx.constraintlayout.core.motion.utils.SplineSet;
import androidx.constraintlayout.core.motion.utils.TypedValues;

import java.util.HashMap;
import java.util.HashSet;

public class MotionKeyCycle extends MotionKey {
    private static final String TAG = "KeyCycle";
    static final String NAME = "KeyCycle";
    public static final String WAVE_PERIOD = "wavePeriod";
    public static final String WAVE_OFFSET = "waveOffset";
    public static final String WAVE_PHASE = "wavePhase";
    public static final String WAVE_SHAPE = "waveShape";
    public static final int SHAPE_SIN_WAVE = Oscillator.SIN_WAVE;
    public static final int SHAPE_SQUARE_WAVE = Oscillator.SQUARE_WAVE;
    public static final int SHAPE_TRIANGLE_WAVE = Oscillator.TRIANGLE_WAVE;
    public static final int SHAPE_SAW_WAVE = Oscillator.SAW_WAVE;
    public static final int SHAPE_REVERSE_SAW_WAVE = Oscillator.REVERSE_SAW_WAVE;
    public static final int SHAPE_COS_WAVE = Oscillator.COS_WAVE;
    public static final int SHAPE_BOUNCE = Oscillator.BOUNCE;

    private String mTransitionEasing = null;
    private int mCurveFit = 0;
    private int mWaveShape = -1;
    private String mCustomWaveShape = null;
    private float mWavePeriod = Float.NaN;
    private float mWaveOffset = 0;
    private float mWavePhase = 0;
    private float mProgress = Float.NaN;
    private float mAlpha = Float.NaN;
    private float mElevation = Float.NaN;
    private float mRotation = Float.NaN;
    private float mTransitionPathRotate = Float.NaN;
    private float mRotationX = Float.NaN;
    private float mRotationY = Float.NaN;
    private float mScaleX = Float.NaN;
    private float mScaleY = Float.NaN;
    private float mTranslationX = Float.NaN;
    private float mTranslationY = Float.NaN;
    private float mTranslationZ = Float.NaN;
    public static final int KEY_TYPE = 4;

    {
        mType = KEY_TYPE;
        mCustom = new HashMap<>();
    }

    @Override
    public void getAttributeNames(HashSet<String> attributes) {
        if (!Float.isNaN(mAlpha)) {
            attributes.add(Cycle.S_ALPHA);
        }
        if (!Float.isNaN(mElevation)) {
            attributes.add(Cycle.S_ELEVATION);
        }
        if (!Float.isNaN(mRotation)) {
            attributes.add(Cycle.S_ROTATION_Z);
        }
        if (!Float.isNaN(mRotationX)) {
            attributes.add(Cycle.S_ROTATION_X);
        }
        if (!Float.isNaN(mRotationY)) {
            attributes.add(Cycle.S_ROTATION_Y);
        }
        if (!Float.isNaN(mScaleX)) {
            attributes.add(Cycle.S_SCALE_X);
        }
        if (!Float.isNaN(mScaleY)) {
            attributes.add(Cycle.S_SCALE_Y);
        }
        if (!Float.isNaN(mTransitionPathRotate)) {
            attributes.add(Cycle.S_PATH_ROTATE);
        }
        if (!Float.isNaN(mTranslationX)) {
            attributes.add(Cycle.S_TRANSLATION_X);
        }
        if (!Float.isNaN(mTranslationY)) {
            attributes.add(Cycle.S_TRANSLATION_Y);
        }
        if (!Float.isNaN(mTranslationZ)) {
            attributes.add(Cycle.S_TRANSLATION_Z);
        }
//        if (mCustomConstraints.size() > 0) {
//            for (String s : mCustomConstraints.keySet()) {
//                attributes.add(Cycle.S_CUSTOM + "," + s);
//            }
//        }
    }

    @Override
    public void addValues(HashMap<String, SplineSet> splines) {

    }

    public boolean setValue(int type, int value) {
        switch (type) {
            case Cycle.TYPE_CURVE_FIT:
                mCurveFit = value;
                return true;
            case Cycle.TYPE_WAVE_SHAPE:
                mWaveShape = value;
                return true;
            default:
                return super.setValue(type, value);
        }
    }

    public boolean setValue(int type, String value) {
        switch (type) {
            case Cycle.TYPE_EASING:
                mTransitionEasing = value;
                return true;
            case Cycle.TYPE_CUSTOM_WAVE_SHAPE:
                mCustomWaveShape = value;
                return true;
            default:
                return super.setValue(type, value);
        }

    }

    public boolean setValue(int type, float value) {
        switch (type) {
            case Cycle.TYPE_ALPHA:
                mAlpha = value;
                break;
            case Cycle.TYPE_TRANSLATION_X:
                mTranslationX = value;
                break;
            case Cycle.TYPE_TRANSLATION_Y:
                mTranslationY = value;
                break;
            case Cycle.TYPE_TRANSLATION_Z:
                mTranslationZ = value;
                break;
            case Cycle.TYPE_ELEVATION:
                mElevation = value;
                break;
            case Cycle.TYPE_ROTATION_X:
                mRotationX = value;
                break;
            case Cycle.TYPE_ROTATION_Y:
                mRotationY = value;
                break;
            case Cycle.TYPE_ROTATION_Z:
                mRotation = value;
                break;
            case Cycle.TYPE_SCALE_X:
                mScaleX = value;
                break;
            case Cycle.TYPE_SCALE_Y:
                mScaleY = value;
                break;
            case Cycle.TYPE_PROGRESS:
                mProgress = value;
                break;
            case Attributes.TYPE_PATH_ROTATE:
                mTransitionPathRotate = value;
                break;
            case Cycle.TYPE_PATH_ROTATE:
                mWavePeriod = value;
                break;
            case Cycle.TYPE_WAVE_OFFSET:
                mWaveOffset = value;
                break;
            case Cycle.TYPE_WAVE_PHASE:
                mWavePhase = value;
                break;
            default:
               return super.setValue(type, value);
        }
        return true;
    }

    @Override
    public MotionKey clone() {
        return null;
    }

    @Override
    public int getId(String name) {
        switch (name) {
            case Cycle.S_CURVE_FIT:
                return Cycle.TYPE_CURVE_FIT;
            case Cycle.S_VISIBILITY:
                return Cycle.TYPE_VISIBILITY;
            case Cycle.S_ALPHA:
                return Cycle.TYPE_ALPHA;
            case Cycle.S_TRANSLATION_X:
                return Cycle.TYPE_TRANSLATION_X;
            case Cycle.S_TRANSLATION_Y:
                return Cycle.TYPE_TRANSLATION_Y;
            case Cycle.S_TRANSLATION_Z:
                return Cycle.TYPE_TRANSLATION_Z;
            case Cycle.S_ROTATION_X:
                return Cycle.TYPE_ROTATION_X;
            case Cycle.S_ROTATION_Y:
                return Cycle.TYPE_ROTATION_Y;
            case Cycle.S_ROTATION_Z:
                return Cycle.TYPE_ROTATION_Z;
            case Cycle.S_SCALE_X:
                return Cycle.TYPE_SCALE_X;
            case Cycle.S_SCALE_Y:
                return Cycle.TYPE_SCALE_Y;
            case Cycle.S_PIVOT_X:
                return Cycle.TYPE_PIVOT_X;
            case Cycle.S_PIVOT_Y:
                return Cycle.TYPE_PIVOT_Y;
            case Cycle.S_PROGRESS:
                return Cycle.TYPE_PROGRESS;
            case Cycle.S_PATH_ROTATE:
                return Cycle.TYPE_PATH_ROTATE;
            case Cycle.S_EASING:
                return Cycle.TYPE_EASING;
            case Cycle.S_WAVE_PERIOD:
                return Cycle.TYPE_WAVE_PERIOD;
            case Cycle.S_WAVE_SHAPE:
                return Cycle.TYPE_WAVE_SHAPE;
            case Cycle.S_WAVE_PHASE:
                return Cycle.TYPE_WAVE_PHASE;
            case Cycle.S_WAVE_OFFSET:
                return Cycle.TYPE_WAVE_OFFSET;
            case Cycle.S_CUSTOM_WAVE_SHAPE:
                return Cycle.TYPE_CUSTOM_WAVE_SHAPE;

        }
        return -1;
    }
}
