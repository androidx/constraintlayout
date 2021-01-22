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

import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * This engine allows manipulation of attributes by Curves
 *
 * @hide
 */

public abstract class SplineSet {
    private static final String TAG = "SplineSet";
    protected CurveFit mCurveFit;
    protected int[] mTimePoints = new int[10];
    protected float[] mValues = new float[10];
    private int count;
    private String mType;

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

    public float get(float t) {
        return (float) mCurveFit.getPos(t, 0);
    }

    public float getSlope(float t) {
        return (float) mCurveFit.getSlope(t, 0);
    }

    public CurveFit getCurveFit() {
        return mCurveFit;
    }

    public void setPoint(int position, float value) {
        if (mTimePoints.length < count + 1) {
            mTimePoints = Arrays.copyOf(mTimePoints, mTimePoints.length * 2);
            mValues = Arrays.copyOf(mValues, mValues.length * 2);
        }
        mTimePoints[count] = position;
        mValues[count] = value;
        count++;
    }

    public void setup(int curveType) {
        if (count == 0) {
            return;
        }

        Sort.doubleQuickSort(mTimePoints, mValues, 0, count - 1);

        int unique = 1;

        for (int i = 1; i < count; i++) {
            if (mTimePoints[i - 1] != mTimePoints[i]) {
                unique++;
            }
        }

        double[] time = new double[unique];
        double[][] values = new double[unique][1];
        int k = 0;
        for (int i = 0; i < count; i++) {
            if (i > 0 && mTimePoints[i] == mTimePoints[i - 1]) {
                continue;
            }

            time[k] = mTimePoints[i] * 1E-2;
            values[k][0] = mValues[i];
            k++;
        }
        mCurveFit = CurveFit.get(curveType, time, values);
    }

    private static class Sort {

        static void doubleQuickSort(int[] key, float[] value, int low, int hi) {
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

        private static int partition(int[] array, float[] value, int low, int hi) {
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

        private static void swap(int[] array, float[] value, int a, int b) {
            int tmp = array[a];
            array[a] = array[b];
            array[b] = tmp;
            float tmpv = value[a];
            value[a] = value[b];
            value[b] = tmpv;
        }
    }
}
