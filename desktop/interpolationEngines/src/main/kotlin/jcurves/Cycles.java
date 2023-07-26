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
package jcurves;



import utils.MonotonicCurveFit;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This generates variable frequency oscillation curves
 *
 *
 */
public class Cycles {
    public static String TAG = "Oscillator";
    float[] mPeriod = {};
    float[] mPosition = {};
    float[] mArea;
    public static final int SIN_WAVE = 0; // theses must line up with attributes
    public static final int SQUARE_WAVE = 1;
    public static final int TRIANGLE_WAVE = 2;
    public static final int SAW_WAVE = 3;
    public static final int REVERSE_SAW_WAVE = 4;
    public static final int COS_WAVE = 5;
    public static final int BOUNCE = 6;
    public static final int CUSTOM = 7;
    String mCustomType;
    MonoSpline mCustomCurve;
    int mType;
    float mPI2 = (float) Math.PI * 2;
    @SuppressWarnings("unused") private boolean mNormalized = false;

    public Cycles() {
    }

    @Override
    public String toString() {
        return "pos =" + Arrays.toString(mPosition) + " period=" + Arrays.toString(mPeriod);
    }

    // @TODO: add description
    public void setType(int type, String customType) {
        mType = type;
        mCustomType = customType;
        if (mCustomType != null) {
            mCustomCurve = buildWave(customType);
        }
    }

    // @TODO: add description
    public void addPoint(float position, float period) {
        int len = mPeriod.length + 1;
        int j = Arrays.binarySearch(mPosition, position);
        if (j < 0) {
            j = -j - 1;
        }
        mPosition = Arrays.copyOf(mPosition, len);
        mPeriod = Arrays.copyOf(mPeriod, len);
        mArea = new float[len];
        System.arraycopy(mPosition, j, mPosition, j + 1, len - j - 1);
        mPosition[j] = position;
        mPeriod[j] = period;
        mNormalized = false;
    }

    /**
     * After adding point every thing must be normalized
     */
    public void normalize() {
        float totalArea = 0;
        float totalCount = 0;
        for (int i = 0; i < mPeriod.length; i++) {
            totalCount += mPeriod[i];
        }
        for (int i = 1; i < mPeriod.length; i++) {
            float h = (mPeriod[i - 1] + mPeriod[i]) / 2;
            float w = mPosition[i] - mPosition[i - 1];
            totalArea = totalArea + w * h;
        }
        // scale periods to normalize it
        for (int i = 0; i < mPeriod.length; i++) {
            mPeriod[i] *= (float) (totalCount / totalArea);
        }
        mArea[0] = 0;
        for (int i = 1; i < mPeriod.length; i++) {
            float h = (mPeriod[i - 1] + mPeriod[i]) / 2;
            float w = mPosition[i] - mPosition[i - 1];
            mArea[i] = mArea[i - 1] + w * h;
        }
        mNormalized = true;
    }

    float getP(float time) {
        if (time < 0) {
            time = 0;
        } else if (time > 1) {
            time = 1;
        }
        int index = Arrays.binarySearch(mPosition, time);
        float p = 0;
        if (index > 0) {
            p = 1;
        } else if (index != 0) {
            index = -index - 1;
            float t = time;
            float m = (mPeriod[index] - mPeriod[index - 1])
                    / (mPosition[index] - mPosition[index - 1]);
            p = mArea[index - 1]
                    + (mPeriod[index - 1] - m * mPosition[index - 1]) * (t - mPosition[index - 1])
                    + m * (t * t - mPosition[index - 1] * mPosition[index - 1]) / 2;
        }
        return p;
    }

    // @TODO: add description
    public float getValue(float time, float phase) {
        float angle = phase + getP(time); // angle is / by 360
        switch (mType) {
            default:
            case SIN_WAVE:
                return (float)Math.sin(mPI2 * angle);
            case SQUARE_WAVE:
                return (float)Math.signum(0.5 - angle % 1);
            case TRIANGLE_WAVE:
                return 1 - Math.abs((angle * 4 + 1) % 4 - 2);
            case SAW_WAVE:
                return ((angle * 2 + 1) % 2) - 1;
            case REVERSE_SAW_WAVE:
                return (1 - ((angle * 2 + 1) % 2));
            case COS_WAVE:
                return (float) (Math.cos(mPI2 * (phase + angle)));
            case BOUNCE:
                float x = 1 - Math.abs((angle * 4) % 4 - 2);
                return 1 - x * x;
            case CUSTOM:
                return (float) mCustomCurve.getPos((float) (angle % 1), 0);
        }
    }

    float getDP(float time) {
        if (time <= 0) {
            time = 0.00001f;
        } else if (time >= 1) {
            time = .999999f;
        }
        int index = Arrays.binarySearch(mPosition, time);
        float p = 0;
        if (index > 0) {
            return 0;
        }
        if (index != 0) {
            index = -index - 1;
            float t = time;
            float m = (mPeriod[index] - mPeriod[index - 1])
                    / (mPosition[index] - mPosition[index - 1]);
            p = m * t + (mPeriod[index - 1] - m * mPosition[index - 1]);
        }
        return p;
    }

    // @TODO: add description
    public float getSlope(float time, float phase, float dphase) {
        float angle = phase + getP(time);

        float dangle_dtime = getDP(time) + dphase;
        switch (mType) {
            default:
            case SIN_WAVE:
                return (float) (mPI2 * dangle_dtime * Math.cos(mPI2 * angle));
            case SQUARE_WAVE:
                return 0;
            case TRIANGLE_WAVE:
                return 4 * dangle_dtime * Math.signum((angle * 4 + 3) % 4 - 2);
            case SAW_WAVE:
                return dangle_dtime * 2;
            case REVERSE_SAW_WAVE:
                return -dangle_dtime * 2;
            case COS_WAVE:
                return  (float) (-mPI2 * dangle_dtime * Math.sin(mPI2 * angle));
            case BOUNCE:
                return 4 * dangle_dtime * ((angle * 4 + 2) % 4 - 2);
            case CUSTOM:
                return (float) mCustomCurve.getSlope((float) (angle % 1), 0);
        }
    }

    /**
     * This builds a monotonic spline to be used as a wave function
     */
    public static MonoSpline buildWave(String configString) {
        // done this way for efficiency
        String str = configString;
        float[] values = new float[str.length() / 2];
        int start = configString.indexOf('(') + 1;
        int off1 = configString.indexOf(',', start);
        int count = 0;
        while (off1 != -1) {
            String tmp = configString.substring(start, off1).trim();
            values[count++] = Float.parseFloat(tmp);
            off1 = configString.indexOf(',', start = off1 + 1);
        }
        off1 = configString.indexOf(')', start);
        String tmp = configString.substring(start, off1).trim();
        values[count++] = Float.parseFloat(tmp);

        return buildWave(Arrays.copyOf(values, count));
    }

    private static MonoSpline buildWave(float[] values) {
        int length = values.length * 3 - 2;
        int len = values.length - 1;
        float gap = 1.0f / len;
        ArrayList<float[]> points = new ArrayList<>(length);
        float[] time = new float[length];
        for (int i = 0; i < values.length; i++) {
            float v = values[i];
            points.get( i + len)[0] = v;
            time[i + len] = i * gap;
            if (i > 0) {
                points.get( i + len * 2)[0] = v + 1;
                time[i + len * 2] = i * gap + 1;

                points.get( i - 1)[0] = v - 1 - gap;
                time[i - 1] = i * gap + -1 - gap;
            }
        }

        return null; // todo  new MonoSpline(time,  points);
    }
}
