/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package androidx.constraintlayout.core.motion.key;

import androidx.constraintlayout.core.motion.CustomVariable;
import androidx.constraintlayout.core.motion.utils.KeyCycleOscillator;
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
        if (mCustom.size() > 0) {
            for (String s : mCustom.keySet()) {
                attributes.add(TypedValues.S_CUSTOM + "," + s);
            }
        }
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
                boolean ret =  setValue( type, (float) value);
                if (ret) {
                    return true;
                }
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
            case Cycle.TYPE_PATH_ROTATE:
                mTransitionPathRotate = value;
                break;
            case Cycle.TYPE_WAVE_PERIOD:
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


    public float getValue(String key) {
        switch (key) {
            case Cycle.S_ALPHA:
                return mAlpha;
            case Cycle.S_ELEVATION:
                return mElevation;
            case Cycle.S_ROTATION_Z:
                return mRotation;
            case Cycle.S_ROTATION_X:
                return mRotationX;
            case Cycle.S_ROTATION_Y:
                return mRotationY;
            case Cycle.S_PATH_ROTATE:
                return mTransitionPathRotate;
            case Cycle.S_SCALE_X:
                return mScaleX;
            case Cycle.S_SCALE_Y:
                return mScaleY;
            case Cycle.S_TRANSLATION_X:
                return mTranslationX;
            case Cycle.S_TRANSLATION_Y:
                return mTranslationY;
            case Cycle.S_TRANSLATION_Z:
                return mTranslationZ;
            case Cycle.S_WAVE_OFFSET:
                return mWaveOffset;
            case Cycle.S_WAVE_PHASE:
                return mWavePhase;
            case Cycle.S_PROGRESS:
                return mProgress;
            default:
                return Float.NaN;
        }
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

    public void addCycleValues(HashMap<String, KeyCycleOscillator> oscSet) {

        for (String key : oscSet.keySet()) {
            if (key.startsWith(TypedValues.S_CUSTOM)) {
                String customKey = key.substring(TypedValues.S_CUSTOM.length() + 1);
                CustomVariable cValue = mCustom.get(customKey);
                if (cValue == null || cValue.getType() != Custom.TYPE_FLOAT) {
                    continue;
                }

                KeyCycleOscillator osc = oscSet.get(key);
                if (osc == null) {
                    continue;
                }

                osc.setPoint(mFramePosition, mWaveShape, mCustomWaveShape, -1, mWavePeriod, mWaveOffset, mWavePhase, cValue.getValueToInterpolate(), cValue);
                continue;
            }
            float value = getValue(key);
            if (Float.isNaN(value)) {
                continue;
            }

            KeyCycleOscillator osc = oscSet.get(key);
            if (osc == null) {
                continue;
            }

            osc.setPoint(mFramePosition, mWaveShape, mCustomWaveShape, -1, mWavePeriod, mWaveOffset, mWavePhase, value);
        }
    }



    public void dump() {
        System.out.println( "MotionKeyCycle{" +
                "mWaveShape=" + mWaveShape +
                ", mWavePeriod=" + mWavePeriod +
                ", mWaveOffset=" + mWaveOffset +
                ", mWavePhase=" + mWavePhase +
                ", mRotation=" + mRotation +
                '}');
    }
}
