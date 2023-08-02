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

import curves.Cycles;
import curves.MonoSpline;
import org.junit.Test;

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
    public void cycleTest1() {
        Cycles cycles = new Cycles();
        cycles.addPoint(0, 0);
        cycles.addPoint(0.5f, 1);
        cycles.addPoint(1, 0);
        cycles.normalize();

        String expect;

        System.out.println("");
        int count = 200;
        float[] xp = new float[count];
        float[] yp = new float[count];


        cycles.setType(Cycles.SIN_WAVE, null);
        getValues(cycles, xp, yp);
        expect = "" +
                "|                                   *******                  | -0.999\n" +
                "|                                  *      **                 |\n" +
                "|                                 **        **               |\n" +
                "|                                **          **              |\n" +
                "|                                *            ***            |\n" +
                "|                               *               **           | -0.473\n" +
                "|                              **                ***         |\n" +
                "|                              *                   ***       |\n" +
                "|                             **                     ****    |\n" +
                "|****                         *                         *****|\n" +
                "|   ****                     **                              | 0.053\n" +
                "|      ***                   *                               |\n" +
                "|        ***                **                               |\n" +
                "|          **               *                                |\n" +
                "|           ***            *                                 |\n" +
                "|             **          **                                 | 0.579\n" +
                "|              **        **                                  |\n" +
                "|                **      *                                   |\n" +
                "|                 *******                                    |\n" +
                "|                    *                                       | 1.0\n" +
                "0.0                                                        1.0\n";
        assertEquals(expect, textDraw(60, 20, xp, yp, false));
        cycles.setType(Cycles.TRIANGLE_WAVE, null);
        getValues(cycles, xp, yp);
        expect = "" +
                "|                                     ***                    | -0.989\n" +
                "|                                    *  **                   |\n" +
                "|                                   *    **                  |\n" +
                "|                                  *       *                 |\n" +
                "|                                 *         **               |\n" +
                "|                                *           ***             | -0.468\n" +
                "|                               **             ***           |\n" +
                "|                              **                ***         |\n" +
                "|                             **                   ****      |\n" +
                "|*****                        *                        ******|\n" +
                "|     ****                   **                              | 0.052\n" +
                "|        ***                **                               |\n" +
                "|          ***             **                                |\n" +
                "|            ***           *                                 |\n" +
                "|              **         *                                  |\n" +
                "|                *       *                                   | 0.573\n" +
                "|                 **    *                                    |\n" +
                "|                  **  *                                     |\n" +
                "|                   ***                                      |\n" +
                "|                    *                                       | 0.99\n" +
                "0.0                                                        1.0\n";
        assertEquals(expect, textDraw(60, 20, xp, yp, false));

        cycles.setType(Cycles.SQUARE_WAVE, null);
        getValues(cycles, xp, yp);
        expect = "" +
                "|                             ****************************** | -0.999\n" +
                "|                                                            |\n" +
                "|                                                            |\n" +
                "|                                                            |\n" +
                "|                                                            |\n" +
                "|                                                            | -0.473\n" +
                "|                                                            |\n" +
                "|                                                            |\n" +
                "|                                                            |\n" +
                "|                                                            |\n" +
                "|                                                            | 0.053\n" +
                "|                                                            |\n" +
                "|                                                            |\n" +
                "|                                                            |\n" +
                "|                                                            |\n" +
                "|                                                            | 0.579\n" +
                "|                                                            |\n" +
                "|                                                            |\n" +
                "|                                                            |\n" +
                "|******************************                             *| 1.0\n" +
                "0.0                                                        1.0\n";
        assertEquals(expect, textDraw(60, 20, xp, yp, false));
        cycles.clearPoints();
        cycles.addPoint(0, 0);
        cycles.addPoint(0.5f, 3);
        cycles.addPoint(1, 0);
        cycles.normalize();
        cycles.setType(Cycles.SIN_WAVE, null);
        getValues(cycles, xp, yp);
        expect = "" +
                "|                   ***         **            ****           | -0.999\n" +
                "|                   * **        ***          **  **          |\n" +
                "|                  **  *       *  *          *     *         |\n" +
                "|                  *   *       *            *      **        |\n" +
                "|                  *              *         *       **       |\n" +
                "|                  *    *      *   *        *        **      | -0.472\n" +
                "|                 *     *     *    *       *          *      |\n" +
                "|                 *     *                  *           **    |\n" +
                "|                 *           *    *       *            **   |\n" +
                "|***             *       *         *       *             ****|\n" +
                "|  **            *       *    *           *                  | 0.053\n" +
                "|   **           *                  *     *                  |\n" +
                "|     *          *       *    *     *     *                  |\n" +
                "|     **        *        *   *      *    *                   |\n" +
                "|      **       *         *              *                   |\n" +
                "|       **      *            *       *   *                   | 0.579\n" +
                "|        *     *          *  *       *  **                   |\n" +
                "|         **  **          ***        ** *                    |\n" +
                "|          ****            **         ***                    |\n" +
                "|                          *                                 | 1.0\n" +
                "0.0                                                        1.0\n";
        assertEquals(expect, textDraw(60, 20, xp, yp, false));


    }

    @Test
    public void cycleTest2() {
        Cycles cycles1 = new Cycles();
        Cycles cycles2 = new Cycles();
        cycles1.addPoint(0, 0);
        cycles1.addPoint(0.5f, 3);
        cycles1.addPoint(1, 0);
        cycles1.normalize();
        cycles2.addPoint(0, 0);
        cycles2.addPoint(0.5f, 3);
        cycles2.addPoint(1, 0);
        cycles2.normalize();

        float[][] f = {{0, 0.25f}, {0, 0.25f}, {0, 0.25f}};
        MonoSpline spline = new MonoSpline(new float[]{0, 0.5f, 1}, Arrays.asList(f));

        String expect;

        System.out.println("");
        int count = 200;
        float[] xp = new float[count];
        float[] yp = new float[count];


        cycles1.setType(Cycles.SIN_WAVE, null);
        for (int i = 0; i < xp.length; i++) {
            float p = i / (float) (xp.length - 1);
            xp[i] = (float) cycles1.getValue(p, spline.getPos(p, 0));
            yp[i] = (float) cycles2.getValue(p, spline.getPos(p, 1));
        }
        expect = "" +
                "|                 **  *  * *** *** *  *  **                  | -0.998\n" +
                "|           * ** *                         * ** *            |\n" +
                "|         * *                                   * *          |\n" +
                "|     ***                                           ***      |\n" +
                "|    *                                                 *     |\n" +
                "|  *                                                     *   | -0.472\n" +
                "| *                                                       *  |\n" +
                "|*                                                         * |\n" +
                "|*                                                         * |\n" +
                "|*                                                         **|\n" +
                "|*                                                         * | 0.053\n" +
                "|*                                                         * |\n" +
                "| *                                                       *  |\n" +
                "|  **                                                   **   |\n" +
                "|   ***                                               ***    |\n" +
                "|     ***                                           ***      | 0.579\n" +
                "|        ***                                     ***         |\n" +
                "|           ** **                           ** **            |\n" +
                "|                ***************************                 |\n" +
                "|                             *                              | 1.0\n" +
                "-0.999                                                     1.0\n";
        assertEquals(expect, textDraw(60, 20, xp, yp, false));


    }

    private void getValues(Cycles cycles, float[] xp, float[] yp) {
        for (int i = 0; i < xp.length; i++) {
            float p = i / (float) (xp.length - 1);
            xp[i] = p;
            yp[i] = (float) cycles.getValue(p, 0);
        }
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
