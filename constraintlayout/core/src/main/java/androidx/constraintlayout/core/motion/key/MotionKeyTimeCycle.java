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
import androidx.constraintlayout.core.motion.utils.Oscillator;
import androidx.constraintlayout.core.motion.utils.SplineSet;
import androidx.constraintlayout.core.motion.utils.TimeCycleSplineSet;
import androidx.constraintlayout.core.motion.utils.TypedValues;
import androidx.constraintlayout.core.motion.utils.Utils;

import java.util.HashMap;
import java.util.HashSet;

public class MotionKeyTimeCycle extends MotionKey {
    static final String NAME = "KeyTimeCycle";
    private static final String TAG = NAME;

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
        mCustom = new HashMap<>();
    }

    public void addTimeValues(HashMap<String, TimeCycleSplineSet> splines) {
        for (String s : splines.keySet()) {
            TimeCycleSplineSet splineSet = splines.get(s);
            if (splineSet == null) {
                continue;
            }
            if (s.startsWith(CUSTOM)) {
                String cKey = s.substring(CUSTOM.length() + 1);
                CustomVariable cValue = mCustom.get(cKey);
                if (cValue != null) {
                    ((TimeCycleSplineSet.CustomVarSet) splineSet).setPoint(mFramePosition, cValue, mWavePeriod, mWaveShape, mWaveOffset);
                }
                continue;
            }
            switch (s) {
                case TypedValues.Attributes.S_ALPHA:
                    if (!Float.isNaN(mAlpha)) {
                        splineSet.setPoint(mFramePosition, mAlpha, mWavePeriod, mWaveShape, mWaveOffset);
                    }
                    break;

                case TypedValues.Attributes.S_ROTATION_X:
                    if (!Float.isNaN(mRotationX)) {
                        splineSet.setPoint(mFramePosition, mRotationX, mWavePeriod, mWaveShape, mWaveOffset);
                    }
                    break;
                case TypedValues.Attributes.S_ROTATION_Y:
                    if (!Float.isNaN(mRotationY)) {
                        splineSet.setPoint(mFramePosition, mRotationY, mWavePeriod, mWaveShape, mWaveOffset);
                    }
                    break;
                case TypedValues.Attributes.S_ROTATION_Z:
                    if (!Float.isNaN(mRotation)) {
                        splineSet.setPoint(mFramePosition, mRotation, mWavePeriod, mWaveShape, mWaveOffset);
                    }
                    break;
                case TypedValues.Attributes.S_PATH_ROTATE:
                    if (!Float.isNaN(mTransitionPathRotate)) {
                        splineSet.setPoint(mFramePosition, mTransitionPathRotate, mWavePeriod, mWaveShape, mWaveOffset);
                    }
                    break;

                case TypedValues.Attributes.S_SCALE_X:
                    if (!Float.isNaN(mScaleX)) {
                        splineSet.setPoint(mFramePosition, mScaleX, mWavePeriod, mWaveShape, mWaveOffset);
                    }
                    break;
                case TypedValues.Attributes.S_SCALE_Y:
                    if (!Float.isNaN(mScaleY)) {
                        splineSet.setPoint(mFramePosition, mScaleY, mWavePeriod, mWaveShape, mWaveOffset);
                    }
                    break;
                case TypedValues.Attributes.S_TRANSLATION_X:
                    if (!Float.isNaN(mTranslationX)) {
                        splineSet.setPoint(mFramePosition, mTranslationX, mWavePeriod, mWaveShape, mWaveOffset);
                    }
                    break;
                case TypedValues.Attributes.S_TRANSLATION_Y:
                    if (!Float.isNaN(mTranslationY)) {
                        splineSet.setPoint(mFramePosition, mTranslationY, mWavePeriod, mWaveShape, mWaveOffset);
                    }
                    break;
                case TypedValues.Attributes.S_TRANSLATION_Z:
                    if (!Float.isNaN(mTranslationZ)) {
                        splineSet.setPoint(mFramePosition, mTranslationZ, mWavePeriod, mWaveShape, mWaveOffset);
                    }
                    break;
                case TypedValues.Attributes.S_ELEVATION:
                    if (!Float.isNaN(mTranslationZ)) {
                        splineSet.setPoint(mFramePosition, mTranslationZ, mWavePeriod, mWaveShape, mWaveOffset);
                    }
                    break;
                case TypedValues.Attributes.S_PROGRESS:
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
    public void addValues(HashMap<String, SplineSet> splines) {
    }

    public boolean setValue(int type, int value) {

        switch (type) {
            case TypedValues.TYPE_FRAME_POSITION:
                mFramePosition = value;
                break;
            case Cycle.TYPE_WAVE_SHAPE:
                mWaveShape = value;
                break;
            default:
                return super.setValue(type, value);
        }
        return true;
    }

    public boolean setValue(int type, float value) {
        switch (type) {
            case Cycle.TYPE_ALPHA:
                mAlpha = value;
                break;
            case Cycle.TYPE_CURVE_FIT:
                mCurveFit = toInt(value);
                break;
            case Cycle.TYPE_ELEVATION:
                mElevation = toFloat(value);
                break;
            case Cycle.TYPE_PROGRESS:
                mProgress = toFloat(value);
                break;
            case Cycle.TYPE_ROTATION_Z:
                mRotation = toFloat(value);
                break;
            case Cycle.TYPE_ROTATION_X:
                mRotationX = toFloat(value);
                break;
            case Cycle.TYPE_ROTATION_Y:
                mRotationY = toFloat(value);
                break;
            case Cycle.TYPE_SCALE_X:
                mScaleX = toFloat(value);
                break;
            case Cycle.TYPE_SCALE_Y:
                mScaleY = toFloat(value);
                break;
            case Cycle.TYPE_PATH_ROTATE:
                mTransitionPathRotate = toFloat(value);
                break;
            case Cycle.TYPE_TRANSLATION_X:
                mTranslationX = toFloat(value);
                break;
            case Cycle.TYPE_TRANSLATION_Y:
                mTranslationY = toFloat(value);
                break;
            case Cycle.TYPE_TRANSLATION_Z:
                mTranslationZ = toFloat(value);
                break;
            case Cycle.TYPE_WAVE_PERIOD:
                mWavePeriod = toFloat(value);
                break;
            case Cycle.TYPE_WAVE_OFFSET:
                mWaveOffset = toFloat(value);
                break;
            default:
                return super.setValue(type, value);
        }
        return true;
    }

    public boolean setValue(int type, String value) {
        switch (type) {
            case Cycle.TYPE_WAVE_SHAPE:
                mWaveShape = Oscillator.CUSTOM;
                mCustomWaveShape = value;
                break;
            case Cycle.TYPE_EASING:
                mTransitionEasing = value;
                break;
            default:
                return super.setValue(type, value);
        }
        return true;
    }

    public boolean setValue(int type, boolean value) {
        return super.setValue(type, value);
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

    public MotionKey clone() {
        return new MotionKeyTimeCycle().copy(this);
    }

    @Override
    public int getId(String name) {
        return Cycle.getId(name);
    }
}
