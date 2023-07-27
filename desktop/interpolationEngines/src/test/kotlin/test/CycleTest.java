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

import curves.ArcSpline;
import curves.Cycles;
import org.junit.Test;
import utils.Oscillator;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class CycleTest {

    @Test
    public void testBasic() {
        assertEquals(2, 1 + 1);
    }

    @Test
    public void unit_test_framework_working() throws Exception {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void arcTest1() {
        float[][] points = {
                {0, 0}, {1, 1}, {2, 0}
        };
        float[] time = {
                0, 5, 10
        };
        int[] mode = {
                ArcSpline.ARC_START_VERTICAL,
                ArcSpline.ARC_START_HORIZONTAL,

        };
        Cycles cycles = new Cycles();
        cycles.setType(Cycles.TRIANGLE_WAVE,null);
        cycles.setType(Oscillator.SQUARE_WAVE, null);
        cycles.addPoint(0, 0);
        cycles.addPoint(0.5f, 10);
        cycles.addPoint(1, 0);
        cycles.normalize();
        for (int i = 0; i < time.length; i++) {
            assertEquals(points[i][0], cycles.g(time[i]), 0.001);
            assertEquals(points[i][1], cycles.getPos(time[i], 1), 0.001);
        }
        assertEquals(0, cycles.getSlope(time[0] + 0.01f, 0), 0.001);
        assertEquals(0, cycles.getSlope(time[1] - 0.01f, 1), 0.001);
        assertEquals(0, cycles.getSlope(time[1] + 0.01f, 1), 0.001);
        double dx = cycles.getSlope((time[0] + time[1]) / 2, 0);
        double dy = cycles.getSlope((time[0] + time[1]) / 2, 1);
        assertEquals(1, dx / dy, 0.001);
        double x = cycles.getPos((time[0] + time[1]) / 2, 0);
        double y = cycles.getPos((time[0] + time[1]) / 2, 1);
        assertEquals(1 - Math.sqrt(0.5), x, 0.001);
        assertEquals(Math.sqrt(0.5), y, 0.001);
    }

    @Test
    public void arcTest2() {
        float[][] points = {
                {0, 0}, {1, 1}, {2, 0}
        };
        float[] time = {
                0, 5, 10
        };
        int[] mode = {
                ArcSpline.ARC_BELOW,
                ArcSpline.ARC_ABOVE,

        };
        ArcSpline spline = new ArcSpline(mode, time, Arrays.asList(points));


        System.out.println("");
        for (int i = 0; i < time.length; i++) {
            assertEquals(points[i][0], spline.getPos(time[i], 0), 0.001);
            assertEquals(points[i][1], spline.getPos(time[i], 1), 0.001);
        }
        assertEquals(0.0f, spline.getSlope(time[0] + 0.01f, 0), 0.001);
        assertEquals(0.0f, spline.getSlope(time[1] - 0.01f, 1), 0.001);
        assertEquals(-0.314f, spline.getSlope(time[1] + 0.01f, 1), 0.001);
        double dx = spline.getSlope((time[0] + time[1]) / 2, 0);
        double dy = spline.getSlope((time[0] + time[1]) / 2, 1);
        assertEquals(1, dx / dy, 0.001);
        double x = spline.getPos((time[0] + time[1]) / 2, 0);
        double y = spline.getPos((time[0] + time[1]) / 2, 1);
        assertEquals(1 - Math.sqrt(0.5), x, 0.001);
        assertEquals(Math.sqrt(0.5), y, 0.001);
    }

    @Test
    public void arcTest3() {
        float[][] points = {
                {0, 0}, {1, 1}, {2, 0}
        };
        float[] time = {
                0, 5, 10
        };
        int[] mode = {
                ArcSpline.ARC_ABOVE,
                ArcSpline.ARC_ABOVE,

        };
         ArcSpline spline = new ArcSpline(mode, time, Arrays.asList(points));

        System.out.println("");
        for (int i = 0; i < time.length; i++) {
            assertEquals(points[i][0], spline.getPos(time[i], 0), 0.001);
            assertEquals(points[i][1], spline.getPos(time[i], 1), 0.001);
        }
        int count = 50;
        float dt = (float) (time[time.length - 1] - time[0]) / count - 0.0001f;
        float[] xp = new float[count];
        float[] yp = new float[count];
        for (int i = 0; i < xp.length; i++) {
            float p = time[0] + i * dt;
            xp[i] = (float) spline.getPos(p, 0);
            yp[i] = (float) spline.getPos(p, 1);
        }
        String expect = ""+
                "|** ** **                                  * ** * *| 0.0\n" +
                "|         **                            * *        |\n" +
                "|            **                       **           |\n" +
                "|              *                    *              |\n" +
                "|                *                 *               |\n" +
                "|                 **              *                | 0.263\n" +
                "|                   *           **                 |\n" +
                "|                    *         *                   |\n" +
                "|                     *       *                    |\n" +
                "|                      *     *                     |\n" +
                "|                      *    *                      | 0.526\n" +
                "|                                                  |\n" +
                "|                       *   *                      |\n" +
                "|                        * *                       |\n" +
                "|                        * *                       |\n" +
                "|                        **                        | 0.789\n" +
                "|                         *                        |\n" +
                "|                         *                        |\n" +
                "|                                                  |\n" +
                "|                         *                        | 0.999\n" +
                "0.0                                            1.936\n";
        assertEquals(expect, textDraw(50, 20, xp, yp, false));
        assertEquals(0, spline.getSlope(time[0] + 0.0001f, 1), 0.001);
        assertEquals(0, spline.getSlope(time[1] - 0.01f, 0), 0.001);
        assertEquals(0, spline.getSlope(time[1] + 0.01f, 0), 0.001);
        double dx = spline.getSlope((time[0] + time[1]) / 2, 0);
        double dy = spline.getSlope((time[0] + time[1]) / 2, 1);
        assertEquals(1, dx / dy, 0.001);
        double x = spline.getPos((time[0] + time[1]) / 2, 1);
        double y = spline.getPos((time[0] + time[1]) / 2, 0);
        assertEquals(1 - Math.sqrt(0.5), x, 0.001);
        assertEquals(Math.sqrt(0.5), y, 0.001);
    }


    private static String textDraw(int dimx, int dimy, float[] x, float[] y, boolean flip) {
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
