/*
 * Copyright (C) 2020 The Android Open Source Project
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
package androidx.constraintlayout.motion.utils;

import android.os.Build;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import androidx.constraintlayout.core.motion.utils.CurveFit;
import androidx.constraintlayout.core.motion.utils.KeyCache;
import androidx.constraintlayout.core.motion.utils.TimeCycleSplineSet;
import androidx.constraintlayout.motion.widget.Key;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintAttribute;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This engine allows manipulation of attributes by wave shapes oscillating in time
 *
 * @suppress
 */
public abstract class ViewTimeCycle extends TimeCycleSplineSet {
    private static final String TAG = "ViewTimeCycle";

    public abstract boolean setProperty(View view, float t, long time, KeyCache cache);

    public float get(float pos, long time, View view, KeyCache cache) {
        mCurveFit.getPos(pos, mCache);
        float period = mCache[CURVE_PERIOD];
        if (period == 0) {
            mContinue = false;
            return mCache[CURVE_OFFSET];
        }
        if (Float.isNaN(last_cycle)) { // it has not been set
            last_cycle = cache.getFloatValue(view, mType, 0); // check the cache
            if (Float.isNaN(last_cycle)) {  // not in cache so set to 0 (start)
                last_cycle = 0;
            }
        }
        long delta_time = time - last_time;
        last_cycle = (float) ((last_cycle + delta_time * 1E-9 * period) % 1.0);
        cache.setFloatValue(view, mType, 0, last_cycle);
        last_time = time;
        float v = mCache[CURVE_VALUE];
        float wave = calcWave(last_cycle);
        float offset = mCache[CURVE_OFFSET];
        float value = v * wave + offset;
        mContinue = v != 0.0f || period != 0.0f;
        return value;
    }

    public static ViewTimeCycle makeCustomSpline(String str, SparseArray<ConstraintAttribute> attrList) {
        return new CustomSet(str, attrList);
    }

    public static ViewTimeCycle makeSpline(String str, long currentTime) {
        ViewTimeCycle timeCycle;
        switch (str) {
            case Key.ALPHA:
                timeCycle = new AlphaSet();
                break;
            case Key.ELEVATION:
                timeCycle = new ElevationSet();
                break;
            case Key.ROTATION:
                timeCycle = new RotationSet();
                break;
            case Key.ROTATION_X:
                timeCycle = new RotationXset();
                break;
            case Key.ROTATION_Y:
                timeCycle = new RotationYset();
                break;
            case Key.TRANSITION_PATH_ROTATE:
                timeCycle = new PathRotate();
                break;
            case Key.SCALE_X:
                timeCycle = new ScaleXset();
                break;
            case Key.SCALE_Y:
                timeCycle = new ScaleYset();
                break;
            case Key.TRANSLATION_X:
                timeCycle = new TranslationXset();
                break;
            case Key.TRANSLATION_Y:
                timeCycle = new TranslationYset();
                break;
            case Key.TRANSLATION_Z:
                timeCycle = new TranslationZset();
                break;
            case Key.PROGRESS:
                timeCycle = new ProgressSet();
                break;
            default:
                return null;
        }
        timeCycle.setStartTime(currentTime);
        return timeCycle;
    }

    static class ElevationSet extends ViewTimeCycle {
        @Override
        public boolean setProperty(View view, float t, long time, KeyCache cache) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.setElevation(get(t, time, view, cache));
            }
            return mContinue;
        }
    }

    static class AlphaSet extends ViewTimeCycle {
        @Override
        public boolean setProperty(View view, float t, long time, KeyCache cache) {
            view.setAlpha(get(t, time, view, cache));
            return mContinue;
        }
    }

    static class RotationSet extends ViewTimeCycle {
        @Override
        public boolean setProperty(View view, float t, long time, KeyCache cache) {
            view.setRotation(get(t, time, view, cache));
            return mContinue;
        }
    }

    static class RotationXset extends ViewTimeCycle {
        @Override
        public boolean setProperty(View view, float t, long time, KeyCache cache) {
            view.setRotationX(get(t, time, view, cache));
            return mContinue;
        }
    }

    static class RotationYset extends ViewTimeCycle {
        @Override
        public boolean setProperty(View view, float t, long time, KeyCache cache) {
            view.setRotationY(get(t, time, view, cache));
            return mContinue;
        }
    }

    public static class PathRotate extends ViewTimeCycle {
        @Override
        public boolean setProperty(View view, float t, long time, KeyCache cache) {
            return mContinue;
        }

        public boolean setPathRotate(View view, KeyCache cache, float t, long time, double dx, double dy) {
            view.setRotation(get(t, time, view, cache) + (float) Math.toDegrees(Math.atan2(dy, dx)));
            return mContinue;
        }
    }

    static class ScaleXset extends ViewTimeCycle {
        @Override
        public boolean setProperty(View view, float t, long time, KeyCache cache) {
            view.setScaleX(get(t, time, view, cache));
            return mContinue;
        }
    }

    static class ScaleYset extends ViewTimeCycle {
        @Override
        public boolean setProperty(View view, float t, long time, KeyCache cache) {
            view.setScaleY(get(t, time, view, cache));
            return mContinue;
        }
    }

    static class TranslationXset extends ViewTimeCycle {
        @Override
        public boolean setProperty(View view, float t, long time, KeyCache cache) {
            view.setTranslationX(get(t, time, view, cache));
            return mContinue;
        }
    }

    static class TranslationYset extends ViewTimeCycle {
        @Override
        public boolean setProperty(View view, float t, long time, KeyCache cache) {
            view.setTranslationY(get(t, time, view, cache));
            return mContinue;
        }
    }

    static class TranslationZset extends ViewTimeCycle {
        @Override
        public boolean setProperty(View view, float t, long time, KeyCache cache) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.setTranslationZ(get(t, time, view, cache));
            }
            return mContinue;
        }
    }

    public static class CustomSet extends ViewTimeCycle {
        String mAttributeName;
        SparseArray<ConstraintAttribute> mConstraintAttributeList;
        SparseArray<float[]> mWaveProperties = new SparseArray<>();
        float[] mTempValues;
        float[] mCache;

        public CustomSet(String attribute, SparseArray<ConstraintAttribute> attrList) {
            mAttributeName = attribute.split(",")[1];
            mConstraintAttributeList = attrList;
        }

        public void setup(int curveType) {
            int size = mConstraintAttributeList.size();
            int dimensionality = mConstraintAttributeList.valueAt(0).numberOfInterpolatedValues();
            double[] time = new double[size];
            mTempValues = new float[dimensionality + 2];
            mCache = new float[dimensionality];
            double[][] values = new double[size][dimensionality + 2];
            for (int i = 0; i < size; i++) {
                int key = mConstraintAttributeList.keyAt(i);
                ConstraintAttribute ca = mConstraintAttributeList.valueAt(i);
                float[] waveProp = mWaveProperties.valueAt(i);
                time[i] = key * 1E-2;
                ca.getValuesToInterpolate(mTempValues);
                for (int k = 0; k < mTempValues.length; k++) {
                    values[i][k] = mTempValues[k];
                }
                values[i][dimensionality] = waveProp[0];
                values[i][dimensionality + 1] = waveProp[1];
            }
            mCurveFit = CurveFit.get(curveType, time, values);
        }

        public void setPoint(int position, float value, float period, int shape, float offset) {
            throw new RuntimeException("don't call for custom attribute call setPoint(pos, ConstraintAttribute,...)");
        }

        public void setPoint(int position, ConstraintAttribute value, float period, int shape, float offset) {
            mConstraintAttributeList.append(position, value);
            mWaveProperties.append(position, new float[]{period, offset});
            mWaveShape = Math.max(mWaveShape, shape); // the highest value shape is chosen
        }

        @Override
        public boolean setProperty(View view, float t, long time, KeyCache cache) {
            mCurveFit.getPos(t, mTempValues);
            float period = mTempValues[mTempValues.length - 2];
            float offset = mTempValues[mTempValues.length - 1];
            long delta_time = time - last_time;

            if (Float.isNaN(last_cycle)) { // it has not been set
                last_cycle = cache.getFloatValue(view, mAttributeName, 0); // check the cache
                if (Float.isNaN(last_cycle)) {  // not in cache so set to 0 (start)
                    last_cycle = 0;
                }
            }

            last_cycle = (float) ((last_cycle + delta_time * 1E-9 * period) % 1.0);
            last_time = time;
            float wave = calcWave(last_cycle);
            mContinue = false;
            for (int i = 0; i < mCache.length; i++) {
                mContinue |= mTempValues[i] != 0.0;
                mCache[i] = mTempValues[i] * wave + offset;
            }
            mConstraintAttributeList.valueAt(0).setInterpolatedValue(view, mCache);
            if (period != 0.0f) {
                mContinue = true;
            }
            return mContinue;
        }
    }

    static class ProgressSet extends ViewTimeCycle {
        boolean mNoMethod = false;

        @Override
        public boolean setProperty(View view, float t, long time, KeyCache cache) {
            if (view instanceof MotionLayout) {
                ((MotionLayout) view).setProgress(get(t, time, view, cache));
            } else {
                if (mNoMethod) {
                    return false;
                }
                Method method = null;
                try {
                    method = view.getClass().getMethod("setProgress", Float.TYPE);
                } catch (NoSuchMethodException e) {
                    mNoMethod = true;
                }
                if (method != null) {
                    try {
                        method.invoke(view, get(t, time, view, cache));
                    } catch (IllegalAccessException e) {
                        Log.e(TAG, "unable to setProgress", e);
                    } catch (InvocationTargetException e) {
                        Log.e(TAG, "unable to setProgress", e);
                    }
                }
            }
            return mContinue;
        }
    }
}
