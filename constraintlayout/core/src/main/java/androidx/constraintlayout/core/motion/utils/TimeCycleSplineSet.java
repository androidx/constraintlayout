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
package androidx.constraintlayout.core.motion.utils;

import androidx.constraintlayout.core.motion.CustomAttribute;
import androidx.constraintlayout.core.motion.CustomVariable;
import androidx.constraintlayout.core.motion.MotionWidget;

import java.text.DecimalFormat;

/**
 * This engine allows manipulation of attributes by wave shapes oscillating in time
 *
 * @suppress
 */
public abstract class TimeCycleSplineSet {
    private static final String TAG = "SplineSet";
    protected CurveFit mCurveFit;
    protected int mWaveShape = 0;
    protected int[] mTimePoints = new int[10];
    protected float[][] mValues = new float[10][3];
    protected int count;
    protected String mType;
    protected float[] mCache = new float[3];
    protected static final int CURVE_VALUE = 0;
    protected static final int CURVE_PERIOD = 1;
    protected static final int CURVE_OFFSET = 2;
    protected static float VAL_2PI = (float) (2 * Math.PI);
    protected boolean mContinue = false;
    protected long last_time;
    protected float last_cycle = Float.NaN;

    @Override
    public String toString() {
        String str = mType;
        DecimalFormat df = new DecimalFormat("##.##");
        for (int i = 0; i < count; i++) {
            str += "[" + mTimePoints[i] + " , " + df.format(mValues[i]) + "] ";
        }
        return str;
    }

    public void setType(String type) {
        mType = type;
    }

    /**
     * @param period cycles per second
     * @return
     */
    protected float calcWave(float period) {
        float p = period;
        switch (mWaveShape) {
            default:
            case Oscillator.SIN_WAVE:
                return (float) Math.sin(p * VAL_2PI);
            case Oscillator.SQUARE_WAVE:
                return (float) Math.signum(p * VAL_2PI);
            case Oscillator.TRIANGLE_WAVE:
                return 1 - Math.abs(p);
            case Oscillator.SAW_WAVE:
                return ((p * 2 + 1) % 2) - 1;
            case Oscillator.REVERSE_SAW_WAVE:
                return (1 - ((p * 2 + 1) % 2));
            case Oscillator.COS_WAVE:
                return (float) Math.cos(p * VAL_2PI);
            case Oscillator.BOUNCE:
                float x = 1 - Math.abs((p * 4) % 4 - 2);
                return 1 - x * x;
        }
    }

    public CurveFit getCurveFit() {
        return mCurveFit;
    }

    protected void setStartTime(long currentTime) {
        last_time = currentTime;
    }

    public void setPoint(int position, float value, float period, int shape, float offset) {
        mTimePoints[count] = position;
        mValues[count][CURVE_VALUE] = value;
        mValues[count][CURVE_PERIOD] = period;
        mValues[count][CURVE_OFFSET] = offset;
        mWaveShape = Math.max(mWaveShape, shape); // the highest value shape is chosen
        count++;
    }

    public static class CustomSet extends TimeCycleSplineSet {
        String mAttributeName;
        KeyFrameArray.CustomArray  mConstraintAttributeList;
        KeyFrameArray.FloatArray mWaveProperties = new KeyFrameArray.FloatArray();
        float[] mTempValues;
        float[] mCache;

        public CustomSet(String attribute, KeyFrameArray.CustomArray attrList) {
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
                CustomAttribute ca = mConstraintAttributeList.valueAt(i);
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

        public void setPoint(int position, CustomAttribute value, float period, int shape, float offset) {
            mConstraintAttributeList.append(position, value);
            mWaveProperties.append(position, new float[]{period, offset});
            mWaveShape = Math.max(mWaveShape, shape); // the highest value shape is chosen
        }


        public boolean setProperty(MotionWidget view, float t, long time, KeyCache cache) {
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

    public void setup(int curveType) {
        if (count == 0) {
            System.err.println("Error no points added to " + mType);
            return;
        }
        Sort.doubleQuickSort(mTimePoints, mValues, 0, count - 1);
        int unique = 0;
        for (int i = 1; i < mTimePoints.length; i++) {
            if (mTimePoints[i] != mTimePoints[i - 1]) {
                unique++;
            }
        }
        if (unique == 0) {
            unique = 1;
        }
        double[] time = new double[unique];
        double[][] values = new double[unique][3];
        int k = 0;

        for (int i = 0; i < count; i++) {
            if (i > 0 && mTimePoints[i] == mTimePoints[i - 1]) {
                continue;
            }
            time[k] = mTimePoints[i] * 1E-2;
            values[k][0] = mValues[i][0];
            values[k][1] = mValues[i][1];
            values[k][2] = mValues[i][2];
            k++;
        }
        mCurveFit = CurveFit.get(curveType, time, values);
    }

    protected static class Sort {
        static void doubleQuickSort(int[] key, float[][] value, int low, int hi) {
            int[] stack = new int[key.length + 10];
            int count = 0;
            stack[count++] = hi;
            stack[count++] = low;
            while (count > 0) {
                low = stack[--count];
                hi = stack[--count];
                if (low < hi) {
                    int p = partition(key, value, low, hi);
                    stack[count++] = p - 1;
                    stack[count++] = low;
                    stack[count++] = hi;
                    stack[count++] = p + 1;
                }
            }
        }

        private static int partition(int[] array, float[][] value, int low, int hi) {
            int pivot = array[hi];
            int i = low;
            for (int j = low; j < hi; j++) {
                if (array[j] <= pivot) {
                    swap(array, value, i, j);
                    i++;
                }
            }
            swap(array, value, i, hi);
            return i;
        }

        private static void swap(int[] array, float[][] value, int a, int b) {
            int tmp = array[a];
            array[a] = array[b];
            array[b] = tmp;
            float[] tmpv = value[a];
            value[a] = value[b];
            value[b] = tmpv;
        }
    }




    public static class CustomVarSet extends TimeCycleSplineSet {
        String mAttributeName;
        KeyFrameArray.CustomVar mConstraintAttributeList;
        KeyFrameArray.FloatArray mWaveProperties = new KeyFrameArray.FloatArray();
        float[] mTempValues;
        float[] mCache;

        public CustomVarSet(String attribute, KeyFrameArray.CustomVar attrList) {
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
                CustomVariable ca = mConstraintAttributeList.valueAt(i);
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

        public void setPoint(int position, CustomVariable value, float period, int shape, float offset) {
            mConstraintAttributeList.append(position, value);
            mWaveProperties.append(position, new float[]{period, offset});
            mWaveShape = Math.max(mWaveShape, shape); // the highest value shape is chosen
        }


        public boolean setProperty(MotionWidget view, float t, long time, KeyCache cache) {
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
}