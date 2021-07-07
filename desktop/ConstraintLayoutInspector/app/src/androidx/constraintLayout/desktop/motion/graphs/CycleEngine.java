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
package androidx.constraintLayout.desktop.motion.graphs;

import androidx.constraintlayout.core.motion.utils.MonotonicCurveFit;

import java.awt.*;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.Arrays;

public class CycleEngine  {

    CycleModel myCycle;

    Color mBackground = new Color(35, 77, 110);
    int ins_top = 30;
    int ins_left = 30;
    int ins_botom = 30;
    int ins_right = 30;
    boolean draw_axis = true;
    boolean draw_grid = true;
    boolean simple_background = true;
    float[][] xPoints = new float[0][];
    float[][] yPoints = new float[0][];
    int[] pointColor = new int[0];
    public static final int RANGE_MODE = 21;

    int mTextGap = 2;
    int fcount = 0;
    Color drawing = new Color(0xAAAAAA);
    Color mGridColor = new Color(0x224422);
    Stroke stroke = new BasicStroke(4f,
            BasicStroke.CAP_ROUND,
            BasicStroke.JOIN_ROUND);

    private int[] pointMode = new int[0];
    public float actual_miny;
    public float actual_maxx;
    public float actual_maxy;
    public float actual_minx;
    private float last_minx;
    private float mTickY;
    private float mTickX;
    private float minx;
    private float last_maxx;
    private float maxx;
    private float maxy;
    private float last_miny;
    private float last_maxy;
    private float miny;
    private int selected_node;
    private int selected_graph;
    Point2D last_click = new Point2D.Double();

    void waveGen(int base,float[]xPoints,float []yPoints) {
        float fb = base / 100f;
        float sc = ((base) % 10000) / 10000f + 1;

        for (int i = 0; i < xPoints.length; i++) {
            float x = (float) ((i + fb) * 10 * Math.PI / xPoints.length);
            xPoints[i] = x;
            yPoints[i] = (float) Math.sin(x) * sc;
        }
        addGraph(0, xPoints, yPoints, 1, 0);
    }

    public CycleEngine(CycleModel cycle) {
        myCycle = cycle;

        calcRange();


    }

    //
    public void addGraph(int n, double[] x, double[] y, int c, int mode) {
        float[] xf = new float[x.length];
        float[] yf = new float[y.length];
        for (int i = 0; i < yf.length; i++) {
            xf[i] = (float) x[i];
            yf[i] = (float) y[i];
        }
        addGraph(n, xf, yf, c, mode);
        calcRange();
    }

    public void addGraph(int n, float[] x, double[] y, int c, int mode) {
        float[] yf = new float[y.length];
        for (int i = 0; i < yf.length; i++) {
            yf[i] = (float) y[i];
        }
        addGraph(n, x, yf, c, mode);
        calcRange();
    }

    public void addGraph(int n, double[] x, float[] y, int c, int mode) {
        float[] xf = new float[x.length];
        for (int i = 0; i < x.length; i++) {
            xf[i] = (float) x[i];
        }
        addGraph(n, xf, y, c, mode);
        calcRange();
    }

    //
    //
    public void addGraph(int n, double[][] p, int c, int mode) {
        float[] xf = new float[p.length];
        float[] yf = new float[p.length];
        for (int i = 0; i < yf.length; i++) {
            xf[i] = (float) p[i][0];
            yf[i] = (float) p[i][1];
        }
        addGraph(n, xf, yf, c, mode);
        calcRange();
    }

    public void addGraph(int n, float[] x, float[] y, int c, int mode) {
        if (xPoints.length <= n) {
            float[][] yp = new float[n + 1][];
            float[][] xp = new float[n + 1][];
            int[] ncol = new int[n + 1];
            int[] m = new int[n + 1];
            for (int i = 0; i < xPoints.length; i++) {
                xp[i] = xPoints[i];
                yp[i] = yPoints[i];
                ncol[i] = pointColor[i];
                m[i] = pointMode[i];
            }
            pointColor = ncol;
            xPoints = xp;
            yPoints = yp;
            pointMode = m;
        }
        xPoints[n] = x;
        yPoints[n] = y;
        pointColor[n] = c;

        pointMode[n] = mode;
        calcRange();
    }

    public void addRange(int n, float[] x, float[] y1, float[] y2, int c) {
        if (xPoints.length <= n) {
            float[][] yp = new float[n + 2][];
            float[][] xp = new float[n + 2][];
            int[] ncol = new int[n + 2];
            int[] m = new int[n + 2];
            for (int i = 0; i < xPoints.length; i++) {
                xp[i] = xPoints[i];
                yp[i] = yPoints[i];
                ncol[i] = pointColor[i];
                m[i] = pointMode[i];
            }
            pointColor = ncol;
            xPoints = xp;
            yPoints = yp;
            pointMode = m;
        }
        xPoints[n] = x;
        yPoints[n] = y1;
        pointColor[n] = c;
        pointMode[n] = -1;

        xPoints[n + 1] = x;
        yPoints[n + 1] = y2;
        pointColor[n + 1] = c;
        pointMode[n + 1] = RANGE_MODE;
        calcRange();
    }

    public void calcRange() {
        actual_minx = Float.MAX_VALUE;
        actual_miny = Float.MAX_VALUE;
        actual_maxx = -Float.MAX_VALUE;
        actual_maxy = -Float.MAX_VALUE;
        for (int g = 0; g < xPoints.length; g++) {
            if (xPoints[g] == null | yPoints[g] == null) {
                continue;
            }
            for (int i = 0; i < xPoints[g].length; i++) {
                float x = xPoints[g][i];
                float y = yPoints[g][i];
                actual_minx = Math.min(actual_minx, x);
                actual_miny = Math.min(actual_miny, y);
                actual_maxx = Math.max(actual_maxx, x);
                actual_maxy = Math.max(actual_maxy, y);
            }
        }
    }

    static double frac(double x) {
        return x - Math.floor(x);
    }


    static class Oscillator {

        float[] mPeriod = {};
        double[] mPosition = {};
        double[] mArea;

        public static final int SIN_WAVE = 0;
        public static final int SQUARE_WAVE = 1;
        public static final int TRIANGLE_WAVE = 2;
        public static final int SAW_WAVE = 3;
        public static final int REVERSE_SAW_WAVE = 4;
        public static final int COS_WAVE = 5;
        public static final int BOUNCE = 6;

        private int mType;
        double PI2 = Math.PI * 2;

        public Oscillator() {
        }

        public void setType(int type) {
            mType = type;
        }

        public void addPoint(double position, float period) {
            int len = mPeriod.length + 1;

            int j = Arrays.binarySearch(mPosition, position);
            if (j < 0) {
                j = -j - 1;
            }
            mPosition = Arrays.copyOf(mPosition, len);
            mPeriod = Arrays.copyOf(mPeriod, len);
            mArea = new double[len];
            System.arraycopy(mPosition, j, mPosition, j + 1, len - j - 1);
            mPosition[j] = position;
            mPeriod[j] = period;

        }

        public void normalize() {

            double totalArea = 0;
            double totalCount = 0;
            for (int i = 0; i < mPeriod.length; i++) {
                totalCount += mPeriod[i];
            }
            for (int i = 1; i < mPeriod.length; i++) {
                float h = (mPeriod[i - 1] + mPeriod[i]) / 2;
                double w = mPosition[i] - mPosition[i - 1];
                totalArea = totalArea + w * h;
            }
            // scale periods to normalize it
            for (int i = 0; i < mPeriod.length; i++) {
                mPeriod[i] *= totalCount / totalArea;
            }

            mArea[0] = 0;
            for (int i = 1; i < mPeriod.length; i++) {
                float h = (mPeriod[i - 1] + mPeriod[i]) / 2;
                double w = mPosition[i] - mPosition[i - 1];
                mArea[i] = mArea[i - 1] + w * h;
            }

        }

        double getP(double time) {
            int index = Arrays.binarySearch(mPosition, time);
            double p = 0;
            if (index > 0) {
                p = 1;
            } else if (index != 0) {
                index = -index - 1;
                double t = time;
                double m =
                        (mPeriod[index] - mPeriod[index - 1]) / (mPosition[index] - mPosition[index - 1]);
                p = mArea[index - 1]
                        + (mPeriod[index - 1] - m * mPosition[index - 1]) * (t - mPosition[index - 1])
                        + m * (t * t - mPosition[index - 1] * mPosition[index - 1]) / 2;
            }
            return p;
        }

        public double getValue(double time) {

            switch (mType) {
                default:
                case SIN_WAVE:
                    return Math.sin(PI2 * getP(time));
                case SQUARE_WAVE:
                    return Math.signum(0.5 - getP(time) % 1);
                case TRIANGLE_WAVE:
                    return 1 - Math.abs(((getP(time)) * 4 + 1) % 4 - 2);
                case SAW_WAVE:
                    return ((getP(time) * 2 + 1) % 2) - 1;
                case REVERSE_SAW_WAVE:
                    return (1 - ((getP(time) * 2 + 1) % 2));
                case COS_WAVE:
                    return Math.cos(PI2 * getP(time));
                case BOUNCE:
                    double x = 1 - Math.abs(((getP(time)) * 4) % 4 - 2);
                    return 1 - x * x;
            }
        }
    }

    MonotonicCurveFit mMonotoneSpline;
    Oscillator mOscillator;

    public void setCycle(int n, double[] pos, double[] period, double[] amplitude, double[] offset,
                         int selected, int curveType) {
        double[] t = new double[pos.length];
        double[][] v = new double[pos.length][2];

        for (int i = 0; i < pos.length; i++) {
            t[i] = pos[i];
            v[i][0] = amplitude[i];
            v[i][1] = offset[i];
        }
        MonotonicCurveFit ms = new MonotonicCurveFit(t, v);
        Oscillator osc = new Oscillator();

        osc.mType = curveType;
        for (int i = 0; i < pos.length; i++) {

            osc.addPoint(pos[i], (float) period[i]);

        }
        osc.normalize();

        mMonotoneSpline = ms;
        mOscillator = osc;
        float[] x = new float[400];
        double[] y1 = new double[x.length];
        float[] yMax = new float[x.length];
        float[] yMin = new float[x.length];
        for (int i = 0; i < x.length; i++) {
            x[i] = (float) (i / (x.length - 1.0f));
            double amp = mMonotoneSpline.getPos(x[i], 0);
            double off = mMonotoneSpline.getPos(x[i], 1);
            y1[i] = mOscillator.getValue(x[i]) * amp + off;
            yMax[i] = (float) (amp + off);
            yMin[i] = (float) (-amp + off);

        }
        n = n * 4;
        addGraph(n, x, y1, 3, 0);
        addRange(n + 1, x, yMin, yMax, 1);
        addGraph(n + 3, pos, offset, 2, 1);
        selected_node = selected;
        selected_graph = n + 3;
    }

    float getComputedValue(float v) {
        if (mMonotoneSpline == null) {
            return 0;
        }
        double amp = mMonotoneSpline.getPos(v, 0);
        double off = mMonotoneSpline.getPos(v, 1);
        return (float) (mOscillator.getValue(v) * amp + off);
    }

    enum Prop {
        PATH_ROTATE,
        ALPHA,
        ELEVATION,
        ROTATION,
        ROTATION_X,
        ROTATION_Y,
        SCALE_X,
        SCALE_Y,
        TRANSLATION_X,
        TRANSLATION_Y,
        TRANSLATION_Z,
        PROGRESS
    }

    static class MainAttribute {

        static String[] Names = {
                "motion:transitionPathRotate",
                "android:alpha",
                "android:elevation",
                "android:rotation",
                "android:rotationX",
                "android:rotationY",
                "android:scaleX",
                "android:scaleY",
                "android:translationX",
                "android:translationY",
                "android:translationZ",
                "motion:progress",
        };
        static String[] ShortNames = {
                "PathRotate",
                "alpha",
                "elevation",
                "rotation",
                "rotationX",
                "rotationY",
                "scaleX",
                "scaleY",
                "translationX",
                "translationY",
                "translationZ",
                "progress",
        };
        static float[][] typicalRange = {
                {-360, 360},
                {0, 1},
                {0, 100},
                {-360, 360},
                {-360, 360},
                {-360, 360},
                {0, 10},
                {0, 10},
                {-200, 200},
                {-200, 200},
                {0, 100},
                {0, 1}
        };
        static boolean[] mapTo100 = {
                false,
                true,
                false,
                false,
                false,
                false,
                true,
                true,
                false,
                false,
                false,
                true,
        };
        static boolean[] makeInt = {
                true,
                false,
                true,
                true,
                true,
                true,
                false,
                false,
                true,
                true,
                true,
                false
        };

        static boolean[] isDp = {
                false,
                false,
                true,
                false,
                false,
                false,
                false,
                false,
                true,
                true,
                true,
                false
        };

        public static String process(double amp, int mAttrIndex) {
            if (isDp[mAttrIndex]) {
                return ((int) amp) + "dp";
            }
            DecimalFormat format = new DecimalFormat("###.####");
            if (typicalRange[mAttrIndex][1] == 1.0f) {

            }
            return format.format(amp);

        }

    }

}
