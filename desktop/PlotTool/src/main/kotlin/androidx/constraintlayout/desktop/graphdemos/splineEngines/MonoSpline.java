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

package androidx.constraintlayout.desktop.graphdemos.splineEngines;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This performs a spline interpolation in multiple dimensions
 */
public class MonoSpline {
    private static final String TAG = "MonotonicCurveFit";
    private double[] mT;
    ArrayList<double[]> mY;
    ArrayList<double[]> mTangent;
    private boolean mExtrapolate = true;
    double[] mSlopeTemp;
    double distance;

    ArrayList<double[]> makeDoubleArray(int a, int b) {
        ArrayList<double[]> ret = new ArrayList<>();
        for (int i = 0; i < a; i++) {
            ret.add(new double[b]);
        }
        return ret;
    }

    public MonoSpline(double[] time, List<double[]> y) {
        final int n = time.length;
        final int dim = y.get(0).length;
        mSlopeTemp = new double[dim];
        ArrayList<double[]> slope = makeDoubleArray(n - 1, dim); // could optimize this out
        ArrayList<double[]> tangent = makeDoubleArray(n, dim);

        for (int j = 0; j < dim; j++) {
            for (int i = 0; i < n - 1; i++) {
                double dt = time[i + 1] - time[i];
                slope.get(i)[j] = (y.get(i + 1)[j] - y.get(i)[j]) / dt;
                if (i == 0) {
                    tangent.get(i)[j] = slope.get(i)[j];
                } else {
                    tangent.get(i)[j] = (slope.get(i - 1)[j] + slope.get(i)[j]) * 0.5f;
                }
            }
            tangent.get(n - 1)[j] = slope.get(n - 2)[j];
        }

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < dim; j++) {
                if (slope.get(i)[j] == 0.) {
                    tangent.get(i)[j] = 0.;
                    tangent.get(i + 1)[j] = 0.;
                } else {
                    double a = tangent.get(i)[j] / slope.get(i)[j];
                    double b = tangent.get(i + 1)[j] / slope.get(i)[j];
                    double h = Math.hypot(a, b);
                    if (h > 9.0) {
                        double t = 3. / h;
                        tangent.get(i)[j] = t * a * slope.get(i)[j];
                        tangent.get(i + 1)[j] = t * b * slope.get(i)[j];
                    }
                }
            }
        }
        mT = time;
        mY = copyData(y);
        mTangent = tangent;

        double[] distanceMap;
        distance = calcDistances();
        dump();
    }

    private void dump() {
        final int dim = mY.get(0).length;
        double[] pos = new double[dim];
        double[] lastPos = new double[dim];

        for (int i = 0; i < mT.length - 1; i++) {

            double start = mT[i];
            double end = mT[i + 1];
            int steps = 20;
            double[] x = new double[steps-1];
            double[] y = new double[steps-1];
            int count = 0;

            double e = (end - start) / (double) steps;
            System.out.println("\n\n======= curve " + i + " " + steps);
            System.out.println("======= start " + start + " end= " + end);

            getPos(start, lastPos);
            for (int j = 1; j < steps; j++) {

                double p = e * j + start;
                double np = calcPercent(p);
                if (np > start && np < end) {
                    x[count] = p-start;
                    y[count] = (np-start);
                    count++;
                }
            }
            double []nx = Arrays.copyOf(x,count);
            double []ny = Arrays.copyOf(y,count);
            System.out.println(Arrays.toString(nx));
            System.out.println(Arrays.toString(ny));
            double[] poly = LinearRegression.fit(nx, ny, 5);

            System.out.print(" poly [" + i + "]= ");
            if (poly != null)
                System.out.println(LinearRegression.printEq(poly));
            else
                System.out.println(" null ");
        }

    }

    private double calcDistances() {

        final int dim = mY.get(0).length;
        double dist = 0;
        double[] lastPos = new double[dim];
        double[] pos = new double[dim];
        double start = mT[0];
        double end = mT[mT.length - 1];
        double e = (end - start) / 100.;
        System.out.println("start " + start + " end = " + end);
        getPos(start, lastPos);
        for (double t = start + e; t <= end; t += e) {
            getPos(t, pos);
            double sum = 0;
            for (int i = 0; i < pos.length; i++) {
                double d = pos[i] - lastPos[i];
                sum += d * d;
            }
            dist += Math.sqrt(sum);
            System.out.println(t + " " + Math.sqrt(sum) + " dist = " + dist);
            double[] swap = lastPos;
            lastPos = pos;
            pos = swap;
        }

        getPos(end, pos);
        double sum = 0;
        for (int i = 0; i < pos.length; i++) {
            double d = pos[i] - lastPos[i];
            sum += d * d;
        }
        dist += Math.sqrt(sum);
        System.out.println(end + " " + Math.sqrt(sum) + " dist = " + dist);

        return dist;
    }

    boolean debug = false;

    public double calcPercent(double percent) {
        boolean log = percent > 0.5 && debug;
        final int dim = mY.get(0).length;
        double dist = 0;
        double[] lastPos = new double[dim];
        double[] pos = new double[dim];
        double start = mT[0];
        double end = mT[mT.length - 1];
        double pLength = percent * distance;
        if (log) System.out.println(" percent +" + percent + " p length " + pLength);
        double e = (end - start) / 10000.;
        getPos(start, lastPos);
        for (double t = start + e; t <= end; t += e) {
            getPos(t, pos);
            double sum = 0;
            for (int i = 0; i < pos.length; i++) {
                double d = pos[i] - lastPos[i];
                sum += d * d;
            }
            dist += Math.sqrt(sum);
            if (dist > pLength) {
                //d1 - > d2
                //d1 ->   p_length  -> d2
                //t1 ->  ret -> t2
                //  t1+ e*(p_length - d1)/(d2-d1)
                if (log) System.out.println(" percent +" + dist + " t " + t);

                double d1 = dist - Math.sqrt(sum);
                double d2 = dist;
                double t1 = t - e;
                double interp = t1 + e * (pLength - d1) / (Math.sqrt(sum));
                if (log) System.out.println(" percent +" + dist + " t " + t + " " + interp);

                if (log) debug = false;

                // todo interpolate
                return interp;//
            }
            double[] swap = lastPos;
            lastPos = pos;
            pos = swap;
        }
        if (log) debug = false;
        return end;


    }

    private ArrayList<double[]> copyData(List<double[]> y) {
        ArrayList<double[]> ret = new ArrayList<>();
        for (double[] array : y) {
            ret.add(array);
        }
        return ret;
    }

    public void getPos(double t, double[] v) {
        final int n = mT.length;
        final int dim = mY.get(0).length;
        if (mExtrapolate) {
            if (t <= mT[0]) {
                getSlope(mT[0], mSlopeTemp);
                for (int j = 0; j < dim; j++) {
                    v[j] = mY.get(0)[j] + (t - mT[0]) * mSlopeTemp[j];
                }
                return;
            }
            if (t >= mT[n - 1]) {
                getSlope(mT[n - 1], mSlopeTemp);
                for (int j = 0; j < dim; j++) {
                    v[j] = mY.get(n - 1)[j] + (t - mT[n - 1]) * mSlopeTemp[j];
                }
                return;
            }
        } else {
            if (t <= mT[0]) {
                for (int j = 0; j < dim; j++) {
                    v[j] = mY.get(0)[j];
                }
                return;
            }
            if (t >= mT[n - 1]) {
                for (int j = 0; j < dim; j++) {
                    v[j] = mY.get(n - 1)[j];
                }
                return;
            }
        }

        for (int i = 0; i < n - 1; i++) {
            if (t == mT[i]) {
                for (int j = 0; j < dim; j++) {
                    v[j] = mY.get(i)[j];
                }
            }
            if (t < mT[i + 1]) {
                double h = mT[i + 1] - mT[i];
                double x = (t - mT[i]) / h;
                for (int j = 0; j < dim; j++) {
                    double y1 = mY.get(i)[j];
                    double y2 = mY.get(i + 1)[j];
                    double t1 = mTangent.get(i)[j];
                    double t2 = mTangent.get(i + 1)[j];
                    v[j] = interpolate(h, x, y1, y2, t1, t2);
                }
                return;
            }
        }
    }

    public void getPos(double t, float[] v) {
        final int n = mT.length;
        final int dim = mY.get(0).length;
        if (mExtrapolate) {
            if (t <= mT[0]) {
                getSlope(mT[0], mSlopeTemp);
                for (int j = 0; j < dim; j++) {
                    v[j] = (float) (mY.get(0)[j] + (t - mT[0]) * mSlopeTemp[j]);
                }
                return;
            }
            if (t >= mT[n - 1]) {
                getSlope(mT[n - 1], mSlopeTemp);
                for (int j = 0; j < dim; j++) {
                    v[j] = (float) (mY.get(n - 1)[j] + (t - mT[n - 1]) * mSlopeTemp[j]);
                }
                return;
            }
        } else {
            if (t <= mT[0]) {
                for (int j = 0; j < dim; j++) {
                    v[j] = (float) mY.get(0)[j];
                }
                return;
            }
            if (t >= mT[n - 1]) {
                for (int j = 0; j < dim; j++) {
                    v[j] = (float) mY.get(n - 1)[j];
                }
                return;
            }
        }

        for (int i = 0; i < n - 1; i++) {
            if (t == mT[i]) {
                for (int j = 0; j < dim; j++) {
                    v[j] = (float) mY.get(i)[j];
                }
            }
            if (t < mT[i + 1]) {
                double h = mT[i + 1] - mT[i];
                double x = (t - mT[i]) / h;
                for (int j = 0; j < dim; j++) {
                    double y1 = mY.get(i)[j];
                    double y2 = mY.get(i + 1)[j];
                    double t1 = mTangent.get(i)[j];
                    double t2 = mTangent.get(i + 1)[j];
                    v[j] = (float) interpolate(h, x, y1, y2, t1, t2);
                }
                return;
            }
        }
    }

    public double getPos(double time, int j) {
        final int n = mT.length;
        if (mExtrapolate) {
            if (time <= mT[0]) {
                return mY.get(0)[j] + (time - mT[0]) * getSlope(mT[0], j);
            }
            if (time >= mT[n - 1]) {
                return mY.get(n - 1)[j] + (time - mT[n - 1]) * getSlope(mT[n - 1], j);
            }
        } else {
            if (time <= mT[0]) {
                return mY.get(0)[j];
            }
            if (time >= mT[n - 1]) {
                return mY.get(n - 1)[j];
            }
        }

        for (int i = 0; i < n - 1; i++) {
            if (time == mT[i]) {
                return mY.get(i)[j];
            }
            if (time < mT[i + 1]) {
                double h = mT[i + 1] - mT[i];
                double x = (time - mT[i]) / h;
                double y1 = mY.get(i)[j];
                double y2 = mY.get(i + 1)[j];
                double t1 = mTangent.get(i)[j];
                double t2 = mTangent.get(i + 1)[j];
                return interpolate(h, x, y1, y2, t1, t2);

            }
        }
        return 0.0; // should never reach here
    }

    public void getSlope(double time, double[] v) {
        final int n = mT.length;
        int dim = mY.get(0).length;
        if (time <= mT[0]) {
            time = mT[0];
        } else if (time >= mT[n - 1]) {
            time = mT[n - 1];
        }

        for (int i = 0; i < n - 1; i++) {
            if (time <= mT[i + 1]) {
                double h = mT[i + 1] - mT[i];
                double x = (time - mT[i]) / h;
                for (int j = 0; j < dim; j++) {
                    double y1 = mY.get(i)[j];
                    double y2 = mY.get(i + 1)[j];
                    double t1 = mTangent.get(i)[j];
                    double t2 = mTangent.get(i + 1)[j];
                    v[j] = diff(h, x, y1, y2, t1, t2) / h;
                }
                break;
            }
        }
        return;
    }

    public double getSlope(double time, int j) {
        final int n = mT.length;

        if (time < mT[0]) {
            time = mT[0];
        } else if (time >= mT[n - 1]) {
            time = mT[n - 1];
        }
        for (int i = 0; i < n - 1; i++) {
            if (time <= mT[i + 1]) {
                double h = mT[i + 1] - mT[i];
                double x = (time - mT[i]) / h;
                double y1 = mY.get(i)[j];
                double y2 = mY.get(i + 1)[j];
                double t1 = mTangent.get(i)[j];
                double t2 = mTangent.get(i + 1)[j];
                return diff(h, x, y1, y2, t1, t2) / h;
            }
        }
        return 0.0; // should never reach here
    }

    public double[] getTimePoints() {
        return mT;
    }

    /**
     * Cubic Hermite spline
     */
    private static double interpolate(double h,
                                      double x,
                                      double y1,
                                      double y2,
                                      double t1,
                                      double t2) {
        double x2 = x * x;
        double x3 = x2 * x;
        return -2 * x3 * y2 + 3 * x2 * y2 + 2 * x3 * y1 - 3 * x2 * y1 + y1
                + h * t2 * x3 + h * t1 * x3 - h * t2 * x2 - 2 * h * t1 * x2
                + h * t1 * x;
    }


    /**
     * Cubic Hermite spline slope differentiated
     */
    private static double diff(double h, double x, double y1, double y2, double t1, double t2) {
        double x2 = x * x;
        return -6 * x2 * y2 + 6 * x * y2 + 6 * x2 * y1 - 6 * x * y1 + 3 * h * t2 * x2
                + 3 * h * t1 * x2 - 2 * h * t2 * x - 4 * h * t1 * x + h * t1;
    }

}
