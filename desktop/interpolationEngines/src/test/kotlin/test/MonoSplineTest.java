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
package test;

import curves.MonoSpline;
import curves.Spline;
import org.junit.Test;
import utils.*;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class MonoSplineTest {

    @Test
    public void testBasic() {
        assertEquals(2, 1 + 1);
    }

    @Test
    public void unit_test_framework_working() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testCurveFit01() throws Exception {
        double[][] points = {
                {0, 0}, {1, 1}, {2, 0}
        };
        double[] time = {
                0, 5, 10
        };
        MonoSpline spline = new MonoSpline( time, Arrays.asList(points));
        double value = spline.getPos(5, 0);
        assertEquals(1, value, 0.001);
        value = spline.getPos(7, 0);
        assertEquals(1.4, value, 0.001);
        value = spline.getPos(7, 1);
        assertEquals(0.744, value, 0.001);
    }

    @Test
    public void testMonoSpline() throws Exception {
        double[][] points = {
                {0, 0},
                {1, 1},
                {1, 0},
                {2, 0},
                {2, 0},
                {3, 0},
        };
        double[] time = {
                0,1,2,3,4,5
        };
        MonoSpline mspline = new MonoSpline( time, Arrays.asList(points));


        assertEquals(1.0, mspline.getPos(1, 0), 0.001);
        assertEquals(1.0, mspline.getPos(1.1, 0), 0.001);
        assertEquals(1.0, mspline.getPos(1.3, 0), 0.001);
        assertEquals(1.0, mspline.getPos(1.6, 0), 0.001);
        assertEquals(1.0, mspline.getPos(1.9, 0), 0.001);
        assertEquals(2.0, mspline.getPos(3.5, 0), 0.001);
        String s  = plotMonoSpline(mspline, 0,  0f, 5);
        String expect = "|***                                                         | 0.0\n" +
                "|   **                                                       |\n" +
                "|     **                                                     |\n" +
                "|       **                                                   |\n" +
                "|         ****************                                   |\n" +
                "|                          **                                | 1.071\n" +
                "|                            **                              |\n" +
                "|                              **                            |\n" +
                "|                                **                          |\n" +
                "|                                  ****************          |\n" +
                "|                                                   **       | 2.143\n" +
                "|                                                     **     |\n" +
                "|                                                       **   |\n" +
                "|                                                         ** |\n" +
                "|                                                           *| 3.0\n" +
                "0.0                                                        5.0\n";
        assertEquals(expect,s);

    }
    @Test
    public void testSpline() throws Exception {
        double[][] points = {
                {0, 0},
                {1, 1},
                {1, 1},
                {2, 2},
                {2, 2},
                {3, 3},
        };

        Spline mspline = new Spline( Arrays.asList(points));


        assertEquals(0.0, mspline.getPos(0, 0), 0.001);
        assertEquals(0.440, mspline.getPos(.1, 0), 0.001);
        assertEquals(1, mspline.getPos(.3, 0), 0.001);
        assertEquals(1.874, mspline.getPos(.6, 0), 0.001);
        assertEquals(2.56, mspline.getPos(.9, 0), 0.001);
        assertEquals(3, mspline.getPos(1, 0), 0.001);
        String s  = plotSpline(mspline, 0,  0f, 1);
        String expect =
                "|***                                                         | 0.0\n" +
                "|   ***                                                      |\n" +
                "|      ***                                                   |\n" +
                "|         *****                                              |\n" +
                "|              *********                                     |\n" +
                "|                       ****                                 | 1.071\n" +
                "|                           ***                              |\n" +
                "|                              * *                           |\n" +
                "|                                 ****                       |\n" +
                "|                                     *********              |\n" +
                "|                                              *****         | 2.143\n" +
                "|                                                   ***      |\n" +
                "|                                                      ***   |\n" +
                "|                                                         ** |\n" +
                "|                                                           *| 3.0\n" +
                "0.0                                                        1.0\n";
        assertEquals(expect,s);

    }

    String plotMonoSpline(MonoSpline spline, int splineNo, float start, float end) {
        int count = 60;
        float step = (end-start) / (count - 1);
        float[] x = new float[count];
        float[] y = new float[count];
        int c = 0;
        for (int i = 0; i < count; i++) {
            float t = start + (end-start)*i/(count-1);
            x[c] = t;
            y[c] = (float) spline.getPos(t, splineNo);;
            c++;
        }

        return textDraw(count, count / 4, x, y, false);
    }
    String plotSpline(Spline spline, int splineNo, float start, float end) {
        int count = 60;
        float step = (end-start) / (count - 1);
        float[] x = new float[count];
        float[] y = new float[count];
        int c = 0;
        for (int i = 0; i < count; i++) {
            float t = start + (end-start)*i/(count-1);
            x[c] = t;
            y[c] = (float) spline.getPos(t, splineNo);;
            c++;
        }

        return textDraw(count, count / 4, x, y, false);
    }

    static String textDraw(int dimx, int dimy, float[] x, float[] y, boolean flip) {
        float minX = x[0], maxX = x[0], minY = y[0], maxY = y[0];
        String ret = "";
        for (int i = 0; i < x.length; i++) {
            minX = Math.min(minX, x[i]);
            maxX = Math.max(maxX, x[i]);
            minY = Math.min(minY, y[i]);
            maxY = Math.max(maxY, y[i]);
        }
        char[][] c = new char[dimy][dimx];
        for (int i = 0; i < dimy; i++) {
            Arrays.fill(c[i], ' ');
        }
        int dimx1 = dimx - 1;
        int dimy1 = dimy - 1;
        for (int j = 0; j < x.length; j++) {
            int xp = (int) (dimx1 * (x[j] - minX) / (maxX - minX));
            int yp = (int) (dimy1 * (y[j] - minY) / (maxY - minY));

            c[flip ? dimy - yp - 1 : yp][xp] = '*';
        }

        for (int i = 0; i < c.length; i++) {
            float v;
            if (flip) {
                v = (minY - maxY) * (i / (c.length - 1.0f)) + maxY;
            } else {
                v = (maxY - minY) * (i / (c.length - 1.0f)) + minY;
            }
            v = ((int) (v * 1000 + 0.5)) / 1000.f;
            if (i % 5 == 0 || i == c.length - 1) {
                ret += "|" + new String(c[i]) + "| " + v + "\n";
            } else {
                ret += "|" + new String(c[i]) + "|\n";
            }
        }
        String minStr = Float.toString(((int) (minX * 1000 + 0.5)) / 1000.f);
        String maxStr = Float.toString(((int) (maxX * 1000 + 0.5)) / 1000.f);
        String s = minStr + new String(new char[dimx]).replace('\0', ' ');
        s = s.substring(0, dimx - maxStr.length() + 2) + maxStr + "\n";
        return ret + s;
    }

}
