/*
 * Copyright (C) 2018 The Android Open Source Project
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
package androidx.constraintlayout.motion.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import androidx.constraintlayout.widget.ConstraintAttribute;
import androidx.constraintlayout.widget.R;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.util.TypedValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Provide the passive data structure to get KeyPosition information form XML
 *
 * @hide
 */
public class KeyCycle extends Key {
    private static final String TAG = "KeyCycle";
    static final String NAME = "KeyCycle";
    private String mTransitionEasing = null;
    private int mCurveFit = 0;
    private int mWaveShape = -1;
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

    public void load(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.KeyCycle);
        Loader.read(this, a);
    }

    @Override
    public void getAttributeNames(HashSet<String> attributes) {
        if (!Float.isNaN(mAlpha)) {
            attributes.add(Key.ALPHA);
        }
        if (!Float.isNaN(mElevation)) {
            attributes.add(Key.ELEVATION);
        }
        if (!Float.isNaN(mRotation)) {
            attributes.add(Key.ROTATION);
        }
        if (!Float.isNaN(mRotationX)) {
            attributes.add(Key.ROTATION_X);
        }
        if (!Float.isNaN(mRotationY)) {
            attributes.add(Key.ROTATION_Y);
        }
        if (!Float.isNaN(mScaleX)) {
            attributes.add(Key.SCALE_X);
        }
        if (!Float.isNaN(mScaleY)) {
            attributes.add(Key.SCALE_Y);
        }
        if (!Float.isNaN(mTransitionPathRotate)) {
            attributes.add(Key.TRANSITION_PATH_ROTATE);
        }
        if (!Float.isNaN(mTranslationX)) {
            attributes.add(Key.TRANSLATION_X);
        }
        if (!Float.isNaN(mTranslationY)) {
            attributes.add(Key.TRANSLATION_Y);
        }
        if (!Float.isNaN(mTranslationZ)) {
            attributes.add(Key.TRANSLATION_Z);
        }
        if (mCustomConstraints.size() > 0) {
            for (String s : mCustomConstraints.keySet()) {
                attributes.add(Key.CUSTOM + "," + s);
            }
        }
    }

    public void addCycleValues(HashMap<String, KeyCycleOscillator> oscSet) {
        for (String key : oscSet.keySet()) {
            if (key.startsWith(Key.CUSTOM)) {
                String ckey = key.substring(Key.CUSTOM.length() + 1);
                ConstraintAttribute cvalue = mCustomConstraints.get(ckey);
                if (cvalue != null && cvalue.getType() == ConstraintAttribute.AttributeType.FLOAT_TYPE) {
                    oscSet.get(key).setPoint(mFramePosition, mWaveShape, mWaveVariesBy, mWavePeriod, mWaveOffset, mWavePhase, cvalue.getValueToInterpolate(), cvalue);
                }
            }
            float value = getValue(key);
            if (!Float.isNaN(value)) {
                oscSet.get(key).setPoint(mFramePosition, mWaveShape, mWaveVariesBy, mWavePeriod, mWaveOffset, mWavePhase, value);
            }
        }
    }

    public float getValue(String key) {
        switch (key) {
            case Key.ALPHA:
                return mAlpha;
            case Key.ELEVATION:
                return mElevation;
            case Key.ROTATION:
                return mRotation;
            case Key.ROTATION_X:
                return mRotationX;
            case Key.ROTATION_Y:
                return mRotationY;
            case Key.TRANSITION_PATH_ROTATE:
                return mTransitionPathRotate;
            case Key.SCALE_X:
                return mScaleX;
            case Key.SCALE_Y:
                return mScaleY;
            case Key.TRANSLATION_X:
                return mTranslationX;
            case Key.TRANSLATION_Y:
                return mTranslationY;
            case Key.TRANSLATION_Z:
                return mTranslationZ;
            case Key.WAVE_OFFSET:
                return mWaveOffset;
            case Key.WAVE_PHASE:
                return mWavePhase;
            case Key.PROGRESS:
                return mProgress;
            default:
                if (!key.startsWith("CUSTOM")) {
                    Log.v("WARNING! KeyCycle", "  UNKNOWN  " + key);
                }
                return Float.NaN;
        }
    }

    @Override
    public void addValues(HashMap<String, SplineSet> splines) {
        Debug.logStack(TAG, "add " + splines.size() + " values", 2);
        for (String s : splines.keySet()) {
            SplineSet splineSet = splines.get(s);
            switch (s) {
                case Key.ALPHA:
                    splineSet.setPoint(mFramePosition, mAlpha);
                    break;
                case Key.ELEVATION:
                    splineSet.setPoint(mFramePosition, mElevation);
                    break;
                case Key.ROTATION:
                    splineSet.setPoint(mFramePosition, mRotation);
                    break;
                case Key.ROTATION_X:
                    splineSet.setPoint(mFramePosition, mRotationX);
                    break;
                case Key.ROTATION_Y:
                    splineSet.setPoint(mFramePosition, mRotationY);
                    break;
                case Key.TRANSITION_PATH_ROTATE:
                    splineSet.setPoint(mFramePosition, mTransitionPathRotate);
                    break;
                case Key.SCALE_X:
                    splineSet.setPoint(mFramePosition, mScaleX);
                    break;
                case Key.SCALE_Y:
                    splineSet.setPoint(mFramePosition, mScaleY);
                    break;
                case Key.TRANSLATION_X:
                    splineSet.setPoint(mFramePosition, mTranslationX);
                    break;
                case Key.TRANSLATION_Y:
                    splineSet.setPoint(mFramePosition, mTranslationY);
                    break;
                case Key.TRANSLATION_Z:
                    splineSet.setPoint(mFramePosition, mTranslationZ);
                    break;
                case Key.WAVE_OFFSET:
                    splineSet.setPoint(mFramePosition, mWaveOffset);
                    break;
                case Key.WAVE_PHASE:
                    splineSet.setPoint(mFramePosition, mWavePhase);
                    break;
                case Key.PROGRESS:
                    splineSet.setPoint(mFramePosition, mProgress);
                    break;
                default:
                    if (!s.startsWith("CUSTOM")) {
                        Log.v("WARNING KeyCycle", "  UNKNOWN  " + s);
                    }
            }
        }
    }

    private static class Loader {
        private static final int TARGET_ID = 1;
        private static final int FRAME_POSITION = 2;
        private static final int TRANSITION_EASING = 3;
        private static final int CURVE_FIT = 4;
        private static final int WAVE_SHAPE = 5;
        private static final int WAVE_PERIOD = 6;
        private static final int WAVE_OFFSET = 7;
        private static final int WAVE_VARIES_BY = 8;
        private static final int ANDROID_ALPHA = 9;
        private static final int ANDROID_ELEVATION = 10;
        private static final int ANDROID_ROTATION = 11;
        private static final int ANDROID_ROTATION_X = 12;
        private static final int ANDROID_ROTATION_Y = 13;
        private static final int TRANSITION_PATH_ROTATE = 14;
        private static final int ANDROID_SCALE_X = 15;
        private static final int ANDROID_SCALE_Y = 16;
        private static final int ANDROID_TRANSLATION_X = 17;
        private static final int ANDROID_TRANSLATION_Y = 18;
        private static final int ANDROID_TRANSLATION_Z = 19;
        private static final int PROGRESS = 20;
        private static final int WAVE_PHASE = 21;
        private static SparseIntArray mAttrMap = new SparseIntArray();

        static {
            mAttrMap.append(R.styleable.KeyCycle_motionTarget, TARGET_ID);
            mAttrMap.append(R.styleable.KeyCycle_framePosition, FRAME_POSITION);
            mAttrMap.append(R.styleable.KeyCycle_transitionEasing, TRANSITION_EASING);
            mAttrMap.append(R.styleable.KeyCycle_curveFit, CURVE_FIT);
            mAttrMap.append(R.styleable.KeyCycle_waveShape, WAVE_SHAPE);
            mAttrMap.append(R.styleable.KeyCycle_wavePeriod, WAVE_PERIOD);
            mAttrMap.append(R.styleable.KeyCycle_waveOffset, WAVE_OFFSET);
            mAttrMap.append(R.styleable.KeyCycle_waveVariesBy, WAVE_VARIES_BY);
            mAttrMap.append(R.styleable.KeyCycle_android_alpha, ANDROID_ALPHA);
            mAttrMap.append(R.styleable.KeyCycle_android_elevation, ANDROID_ELEVATION);
            mAttrMap.append(R.styleable.KeyCycle_android_rotation, ANDROID_ROTATION);
            mAttrMap.append(R.styleable.KeyCycle_android_rotationX, ANDROID_ROTATION_X);
            mAttrMap.append(R.styleable.KeyCycle_android_rotationY, ANDROID_ROTATION_Y);
            mAttrMap.append(R.styleable.KeyCycle_transitionPathRotate, TRANSITION_PATH_ROTATE);
            mAttrMap.append(R.styleable.KeyCycle_android_scaleX, ANDROID_SCALE_X);
            mAttrMap.append(R.styleable.KeyCycle_android_scaleY, ANDROID_SCALE_Y);
            mAttrMap.append(R.styleable.KeyCycle_android_translationX, ANDROID_TRANSLATION_X);
            mAttrMap.append(R.styleable.KeyCycle_android_translationY, ANDROID_TRANSLATION_Y);
            mAttrMap.append(R.styleable.KeyCycle_android_translationZ, ANDROID_TRANSLATION_Z);
            mAttrMap.append(R.styleable.KeyCycle_motionProgress, PROGRESS);
            mAttrMap.append(R.styleable.KeyCycle_wavePhase, WAVE_PHASE);
        }

        private static void read(KeyCycle c, TypedArray a) {
            final int N = a.getIndexCount();
            for (int i = 0; i < N; i++) {
                int attr = a.getIndex(i);
                switch (mAttrMap.get(attr)) {
                    case TARGET_ID:
                        if (MotionLayout.IS_IN_EDIT_MODE) {
                            c.mTargetId = a.getResourceId(attr, c.mTargetId);
                            if (c.mTargetId == -1) {
                                c.mTargetString = a.getString(attr);
                            }
                        } else {
                            if (a.peekValue(attr).type == TypedValue.TYPE_STRING) {
                                c.mTargetString = a.getString(attr);
                            } else {
                                c.mTargetId = a.getResourceId(attr, c.mTargetId);
                            }
                        }
                        break;
                    case FRAME_POSITION:
                        c.mFramePosition = a.getInt(attr, c.mFramePosition);
                        break;
                    case TRANSITION_EASING:
                        c.mTransitionEasing = a.getString(attr);
                        break;
                    case CURVE_FIT:
                        c.mCurveFit = a.getInteger(attr, c.mCurveFit);
                        break;
                    case WAVE_SHAPE:
                        c.mWaveShape = a.getInt(attr, c.mWaveShape);
                        break;
                    case WAVE_PERIOD:
                        c.mWavePeriod = a.getFloat(attr, c.mWavePeriod);
                        break;
                    case WAVE_OFFSET:
                        TypedValue type = a.peekValue(attr);
                        if (type.type == TypedValue.TYPE_DIMENSION) {
                            c.mWaveOffset = a.getDimension(attr, c.mWaveOffset);
                        } else {
                            c.mWaveOffset = a.getFloat(attr, c.mWaveOffset);
                        }
                        break;
                    case WAVE_VARIES_BY:
                        c.mWaveVariesBy = a.getInt(attr, c.mWaveVariesBy);
                        break;
                    case ANDROID_ALPHA:
                        c.mAlpha = a.getFloat(attr, c.mAlpha);
                        break;
                    case ANDROID_ELEVATION:
                        c.mElevation = a.getDimension(attr, c.mElevation);
                        break;
                    case ANDROID_ROTATION:
                        c.mRotation = a.getFloat(attr, c.mRotation);
                        break;
                    case ANDROID_ROTATION_X:
                        c.mRotationX = a.getFloat(attr, c.mRotationX);
                        break;
                    case ANDROID_ROTATION_Y:
                        c.mRotationY = a.getFloat(attr, c.mRotationY);
                        break;
                    case TRANSITION_PATH_ROTATE:
                        c.mTransitionPathRotate = a.getFloat(attr, c.mTransitionPathRotate);
                        break;
                    case ANDROID_SCALE_X:
                        c.mScaleX = a.getFloat(attr, c.mScaleX);
                        break;
                    case ANDROID_SCALE_Y:
                        c.mScaleY = a.getFloat(attr, c.mScaleY);
                        break;
                    case ANDROID_TRANSLATION_X:
                        c.mTranslationX = a.getDimension(attr, c.mTranslationX);
                        break;
                    case ANDROID_TRANSLATION_Y:
                        c.mTranslationY = a.getDimension(attr, c.mTranslationY);
                        break;
                    case ANDROID_TRANSLATION_Z:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            c.mTranslationZ = a.getDimension(attr, c.mTranslationZ);
                        }
                        break;
                    case PROGRESS:
                        c.mProgress = a.getFloat(attr, c.mProgress);
                        break;
                    case WAVE_PHASE:
                        c.mWavePhase = a.getFloat(attr, c.mWavePhase)/360;
                        break;
                    default:
                        Log.e(TAG, "unused attribute 0x" + Integer.toHexString(attr) + "   " + mAttrMap.get(attr));
                        break;
                }
            }
        }
    }

    @Override
    public void setValue(String tag, Object value) {
        switch (tag) {
            case "alpha":
                mAlpha = toFloat(value);
                break;
            case "curveFit":
                mCurveFit = toInt(value);
                break;
            case "elevation":
                mElevation = toFloat(value);
                break;
            case "progress":
                mProgress = toFloat(value);
                break;
            case "rotation":
                mRotation = toFloat(value);
                break;
            case "rotationX":
                mRotationX = toFloat(value);
                break;
            case "rotationY":
                mRotationY = toFloat(value);
                break;
            case "scaleX":
                mScaleX = toFloat(value);
                break;
            case "scaleY":
                mScaleY = toFloat(value);
                break;
            case "transitionEasing":
                mTransitionEasing = value.toString();
                break;
            case "transitionPathRotate":
                mTransitionPathRotate = toFloat(value);
                break;
            case "translationX":
                mTranslationX = toFloat(value);
                break;
            case "translationY":
                mTranslationY = toFloat(value);
                break;
            case "mTranslationZ":
                mTranslationZ = toFloat(value);
                break;
            case "wavePeriod":
                mWavePeriod = toFloat(value);
                break;
            case "waveOffset":
                mWaveOffset = toFloat(value);
                break;
        }
    }
}
