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

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintAttribute;
import androidx.constraintlayout.motion.utils.CurveFit;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * This engine allows manipulation of attributes by Curves
 *
 * @hide
 */

public abstract class SplineSet {
    private static final String TAG = "SplineSet";
    // TODO: Need to write setup into constructor for this to make sense
    @NonNull
    protected CurveFit mCurveFit;
    @NonNull
    protected int[] mTimePoints = new int[10];
    @NonNull
    protected float[] mValues = new float[10];
    private int count;
    // TODO: Need to write setup into constructor for this to make sense
    @NonNull
    private String mType;

    @NonNull
    @Override
    public String toString() {
        String str = mType;
        DecimalFormat df = new DecimalFormat("##.##");
        for (int i = 0; i < count; i++) {
            str += "[" + mTimePoints[i] + " , " + df.format(mValues[i]) + "] ";

        }
        return str;
    }

    public void setType(@NonNull String type) {
        mType = type;
    }

    public abstract void setProperty(@NonNull View view, float t);

    public float get(float t) {
        return (float) mCurveFit.getPos(t, 0);
    }

    public float getSlope(float t) {
        return (float) mCurveFit.getSlope(t, 0);
    }

    @NonNull
    public CurveFit getCurveFit() {
        return mCurveFit;
    }

    static SplineSet makeCustomSpline(String str, SparseArray<ConstraintAttribute> attrList) {
        return new CustomSet(str, attrList);
    }

    static SplineSet makeSpline(String str) {
        switch (str) {
            case Key.ALPHA:
                return new AlphaSet();
            case Key.ELEVATION:
                return new ElevationSet();
            case Key.ROTATION:
                return new RotationSet();
            case Key.ROTATION_X:
                return new RotationXset();
            case Key.ROTATION_Y:
                return new RotationYset();
            case Key.PIVOT_X:
                return new PivotXset();
            case Key.PIVOT_Y:
                return new PivotYset();
            case Key.TRANSITION_PATH_ROTATE:
                return new PathRotate();
            case Key.SCALE_X:
                return new ScaleXset();
            case Key.SCALE_Y:
                return new ScaleYset();
            case Key.WAVE_OFFSET:
                return new AlphaSet();
            case Key.WAVE_VARIES_BY:
                return new AlphaSet();
            case Key.TRANSLATION_X:
                return new TranslationXset();
            case Key.TRANSLATION_Y:
                return new TranslationYset();
            case Key.TRANSLATION_Z:
                return new TranslationZset();
            case Key.PROGRESS:
                return new ProgressSet();

            default:
                return null;

        }
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

    static class ElevationSet extends SplineSet {
        @Override
        public void setProperty(@NonNull View view, float t) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.setElevation(get(t));
            }
        }
    }

    static class AlphaSet extends SplineSet {
        @Override
        public void setProperty(@NonNull View view, float t) {
            view.setAlpha(get(t));
        }
    }

    static class RotationSet extends SplineSet {
        @Override
        public void setProperty(@NonNull View view, float t) {
            view.setRotation(get(t));
        }
    }

    static class RotationXset extends SplineSet {
        @Override
        public void setProperty(@NonNull View view, float t) {
            view.setRotationX(get(t));
        }
    }

    static class RotationYset extends SplineSet {
        @Override
        public void setProperty(@NonNull View view, float t) {
            view.setRotationY(get(t));
        }
    }
    static class PivotXset extends SplineSet {
        @Override
        public void setProperty(@NonNull View view, float t) {
            view.setPivotX(get(t));
        }
    }
    static class PivotYset extends SplineSet {
        @Override
        public void setProperty(@NonNull View view, float t) {
            view.setPivotY(get(t));
        }
    }
    static class PathRotate extends SplineSet {
        @Override
        public void setProperty(@NonNull View view, float t) {
        }

        public void setPathRotate(View view, float t, double dx, double dy) {
            view.setRotation(get(t) + (float) Math.toDegrees(Math.atan2(dy, dx)));
        }
    }

    static class ScaleXset extends SplineSet {
        @Override
        public void setProperty(@NonNull View view, float t) {
            view.setScaleX(get(t));
        }
    }

    static class ScaleYset extends SplineSet {
        @Override
        public void setProperty(@NonNull View view, float t) {
            view.setScaleY(get(t));
        }
    }

    static class TranslationXset extends SplineSet {
        @Override
        public void setProperty(@NonNull View view, float t) {
            view.setTranslationX(get(t));
        }
    }

    static class TranslationYset extends SplineSet {
        @Override
        public void setProperty(@NonNull View view, float t) {
            view.setTranslationY(get(t));
        }
    }

    static class TranslationZset extends SplineSet {
        @Override
        public void setProperty(@NonNull View view, float t) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.setTranslationZ(get(t));
            }
        }
    }

    static class CustomSet extends SplineSet {
        String mAttributeName;
        SparseArray<ConstraintAttribute> mConstraintAttributeList;
        float[] mTempValues;

        public CustomSet(String attribute, SparseArray<ConstraintAttribute> attrList) {
            mAttributeName = attribute.split(",")[1];
            mConstraintAttributeList = attrList;
        }

        public void setup(int curveType) {
            int size = mConstraintAttributeList.size();
            int dimensionality = mConstraintAttributeList.valueAt(0).noOfInterpValues();
            double[] time = new double[size];
            mTempValues = new float[dimensionality];
            double[][] values = new double[size][dimensionality];
            for (int i = 0; i < size; i++) {

                int key = mConstraintAttributeList.keyAt(i);
                ConstraintAttribute ca = mConstraintAttributeList.valueAt(i);

                time[i] = key * 1E-2;
                ca.getValuesToInterpolate(mTempValues);
                for (int k = 0; k < mTempValues.length; k++) {
                    values[i][k] = mTempValues[k];
                }

            }
            mCurveFit = CurveFit.get(curveType, time, values);
        }

        public void setPoint(int position, float value) {
            throw new RuntimeException("don't call for custom attribute call setPoint(pos, ConstraintAttribute)");
        }

        public void setPoint(int position, ConstraintAttribute value) {
            mConstraintAttributeList.append(position, value);
        }

        @Override
        public void setProperty(@NonNull View view, float t) {
            mCurveFit.getPos(t, mTempValues);
            mConstraintAttributeList.valueAt(0).setInterpolatedValue(view, mTempValues);
        }
    }

    static class ProgressSet extends SplineSet {
        boolean mNoMethod = false;

        @Override
        public void setProperty(@NonNull View view, float t) {
            if (view instanceof MotionLayout) {
                ((MotionLayout) view).setProgress(get(t));
            } else {
                if (mNoMethod) {
                    return;
                }
                Method method = null;
                try {
                    method = view.getClass().getMethod("setProgress", Float.TYPE);
                } catch (NoSuchMethodException e) {
                    mNoMethod = true;
                }
                if (method != null) {
                    try {
                        method.invoke(view, get(t));
                    } catch (IllegalAccessException e) {
                        Log.e(TAG, "unable to setProgress", e);
                    } catch (InvocationTargetException e) {
                        Log.e(TAG, "unable to setProgress", e);
                    }
                }
            }

        }
    }

    private static class Sort {

        static void doubleQuickSort(@NonNull int[] key, @NonNull float[] value, int low, int hi) {
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

        private static int partition(@NonNull int[] array, @NonNull float[] value, int low, int hi) {
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

        private static void swap(@NonNull int[] array, @NonNull float[] value, int a, int b) {
            int tmp = array[a];
            array[a] = array[b];
            array[b] = tmp;
            float tmpv = value[a];
            value[a] = value[b];
            value[b] = tmpv;
        }
    }
}
