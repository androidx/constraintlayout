package androidx.constraintlayout.core.motion.key;

import androidx.constraintlayout.core.motion.utils.Oscillator;
import androidx.constraintlayout.core.motion.utils.SplineSet;

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
    private int mWaveVariesBy = -1;
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
