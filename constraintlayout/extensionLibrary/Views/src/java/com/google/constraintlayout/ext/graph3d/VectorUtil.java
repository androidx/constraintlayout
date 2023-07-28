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
package android.support.constraintLayout.extlib.graph3d;

import java.text.DecimalFormat;

/**
 * A few utilities for vector calculations.
 */
public class VectorUtil {
    public static void sub(double[] a, double[] b, double[] out) {
        out[0] = a[0] - b[0];
        out[1] = a[1] - b[1];
        out[2] = a[2] - b[2];
    }

    public static void mult(double[] a, double b, double[] out) {
        out[0] = a[0] * b;
        out[1] = a[1] * b;
        out[2] = a[2] * b;
    }

    public static double dot(double[] a, double[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }

    public static double norm(double[] a) {
        return Math.sqrt(a[0] * a[0] + a[1] * a[1] + a[2] * a[2]);
    }

    public static double norm(float[] a) {
        return Math.sqrt(a[0] * a[0] + a[1] * a[1] + a[2] * a[2]);
    }

    public static void cross(double[] a, double[] b, double[] out) {
        double out0 = a[1] * b[2] - b[1] * a[2];
        double out1 = a[2] * b[0] - b[2] * a[0];
        double out2 = a[0] * b[1] - b[0] * a[1];
        out[0] = out0;
        out[1] = out1;
        out[2] = out2;
    }

    public static void normalize(double[] a) {
        double norm = norm(a);
        a[0] /= norm;
        a[1] /= norm;
        a[2] /= norm;
    }
    public static void normalize(float[] a) {
        float norm = (float) norm(a);
        a[0] /= norm;
        a[1] /= norm;
        a[2] /= norm;
    }
    public static void add(double[] a, double[] b,
                           double[] out) {
        out[0] = a[0] + b[0];
        out[1] = a[1] + b[1];
        out[2] = a[2] + b[2];
    }
    public static void madd(double[] a, double x, double[] b,
                           double[] out) {
        out[0] = x * a[0] + b[0];
        out[1] = x * a[1] + b[1];
        out[2] = x * a[2] + b[2];
    }
    public static void triangleNormal(float[] vert, int p1, int p2, int p3, float[] norm) {
        float x1 = vert[p2] - vert[p1];
        float y1 = vert[p2 + 1] - vert[p1 + 1];
        float z1 = vert[p2 + 2] - vert[p1 + 2];
        float x2 = vert[p3] - vert[p1];
        float y2 = vert[p3 + 1] - vert[p1 + 1];
        float z2 = vert[p3 + 2] - vert[p1 + 2];

        cross(x1, y1, z1, x2, y2, z2, norm);
        float n = (float) norm(norm);
        norm[0] /= n;
        norm[1] /= n;
        norm[2] /= n;
    }
    public static float dot(float[] a, float[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }
    public static float dot(float[] a,int offset, float[] b) {
        return a[offset] * b[0] + a[1+offset] * b[1] + a[2+offset] * b[2];
    }
    public static void cross(float a0, float a1, float a2, float b0, float b1, float b2, float[] out) {
        float out0 = a1 * b2 - b1 * a2;
        float out1 = a2 * b0 - b2 * a0;
        float out2 = a0 * b1 - b0 * a1;
        out[0] = out0;
        out[1] = out1;
        out[2] = out2;
    }

    private static String trim(String s){
        return  s.substring(s.length()-7);
    }

    public static String vecToString(float[] light) {
        DecimalFormat df =new DecimalFormat("        ##0.000");
        String str = "[";
        for (int i = 0; i < 3; i++) {
            if (Float.isNaN(light[i])) {
                str+=(((i == 0) ? "" : " , ") + trim("           NAN"));
                continue;
            }
            str+=(((i == 0) ? "" : " , ") + trim(df.format(light[i])));
        }
           return str+ "]";

    }
}
