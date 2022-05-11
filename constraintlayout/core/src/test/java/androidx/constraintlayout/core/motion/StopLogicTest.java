/*
 * Copyright (C) 2022 The Android Open Source Project
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

package androidx.constraintlayout.core.motion;

import static org.junit.Assert.assertEquals;

import androidx.constraintlayout.core.motion.utils.StopLogicEngine;

import org.junit.Test;

import java.util.Arrays;

public class StopLogicTest {

    @Test
    public void cruseDecelerate() {
        //cruse decelerate
        StopLogicEngine stop = new StopLogicEngine();
        float position = 0.9f;
        float destination = 1;
        float currentVelocity = 0.2f;
        float maxTime = 0.9f;
        float maxAcceleration = 3.2f;
        float maxVelocity = 3.2f;
        stop.config(position, destination, currentVelocity, maxTime, maxAcceleration, maxVelocity);
        System.out.println(stop.debug("check1", 0));
        String expect = ""
                + "|*                                                           | 0.0\n"
                + "|                                                            |\n"
                + "|                                                            |\n"
                + "|                                                            |\n"
                + "|                                                            |\n"
                + "|                                                            | 0.357\n"
                + "|                                                            |\n"
                + "|                                                            |\n"
                + "|                                                            |\n"
                + "|                                                            |\n"
                + "|                                                            | 0.714\n"
                + "|                                                            |\n"
                + "|**********                                                  |\n"
                + "|          *************************                         |\n"
                + "|                                   *********************** *| 1.0\n"
                + "0.0                                                      0.885\n";
        assertEquals(expect, verify(stop, position, maxTime));
    }

    @Test
    public void backwardAccelerateDecelerate() {
        // backward accelerate, decelerate
        StopLogicEngine stop = new StopLogicEngine();
        float position = 0.9f;
        float destination = 1;
        float currentVelocity = -0.2f;
        float maxTime = 0.9f;
        float maxAcceleration = 3.2f;
        float maxVelocity = 3.2f;
        stop.config(position, destination, currentVelocity, maxTime, maxAcceleration, maxVelocity);
        System.out.println(stop.debug("check1", 0));
        String expect = ""
                + "|*                                                           | 0.0\n"
                + "|                                                            |\n"
                + "|                                                            |\n"
                + "|                                                            |\n"
                + "|                                                            |\n"
                + "|                                                            | 0.357\n"
                + "|                                                            |\n"
                + "|                                                            |\n"
                + "|                                                            |\n"
                + "|                                                            |\n"
                + "|                                                            | 0.714\n"
                + "|                                                            |\n"
                + "|**************                                              |\n"
                + "|              **************                                |\n"
                + "|                            ****************************** *| 1.0\n"
                + "0.0                                                      0.885\n";
        assertEquals(expect, verify(stop, position, maxTime));
    }

    @Test
    public void hardStop() {
        StopLogicEngine stop = new StopLogicEngine();
        float position = 0.9f;
        float destination = 1;
        float currentVelocity = 1.8f;
        float maxTime = 0.9f;
        float maxAcceleration = 3.2f;
        float maxVelocity = 3.2f;
        stop.config(position, destination, currentVelocity, maxTime, maxAcceleration, maxVelocity);
        System.out.println(stop.debug("check1", 0));
        String expect = ""
                + "|*                                                           | 0.0\n"
                + "|                                                            |\n"
                + "|                                                            |\n"
                + "|                                                            |\n"
                + "|                                                            |\n"
                + "|                                                            | 0.357\n"
                + "|                                                            |\n"
                + "|                                                            |\n"
                + "|                                                            |\n"
                + "|                                                            |\n"
                + "|                                                            | 0.714\n"
                + "|                                                            |\n"
                + "|**                                                          |\n"
                + "|  ******                                                    |\n"
                + "|        ************************************************** *| 1.0\n"
                + "0.0                                                      0.885\n";
        assertEquals(expect, verify(stop, position, maxTime));
    }

    @Test
    public void accelerateCruseDecelerate() {
        StopLogicEngine stop = new StopLogicEngine();
        float position = 0.3f;
        float destination = 1;
        float currentVelocity = 0.1f;
        float maxTime = 0.9f;
        float maxAcceleration = 3.2f;
        float maxVelocity = 1.2f;
        stop.config(position, destination, currentVelocity, maxTime, maxAcceleration, maxVelocity);
        System.out.println(stop.debug("check1", 0));
        String expect = ""
                + "|*                                                           | 0.0\n"
                + "|                                                            |\n"
                + "|                                                            |\n"
                + "|                                                            |\n"
                + "|***********                                                 |\n"
                + "|           ******                                           | 0.356\n"
                + "|                 *****                                      |\n"
                + "|                      ****                                  |\n"
                + "|                          ***                               |\n"
                + "|                             ****                           |\n"
                + "|                                 ****                       | 0.712\n"
                + "|                                     *****                  |\n"
                + "|                                          *****             |\n"
                + "|                                               ***********  |\n"
                + "|                                                           *| 0.997\n"
                + "0.0                                                      0.885\n";
        assertEquals(expect, verify(stop, position, maxTime));
    }

    @Test
    public void accelerateDecelerate() {
        StopLogicEngine stop = new StopLogicEngine();
        float position = 0.3f;
        float destination = 1;
        float currentVelocity = 0.2f;
        float maxTime = 0.9f;
        float maxAcceleration = 3.2f;
        float maxVelocity = 3.2f;
        stop.config(position, destination, currentVelocity, maxTime, maxAcceleration, maxVelocity);
        System.out.println(stop.debug("check1", 0));
        String expect = ""
                + "|*                                                           | 0.0\n"
                + "|                                                            |\n"
                + "|                                                            |\n"
                + "|                                                            |\n"
                + "|*********                                                   |\n"
                + "|         ******                                             | 0.357\n"
                + "|               *****                                        |\n"
                + "|                    ****                                    |\n"
                + "|                        ***                                 |\n"
                + "|                           ***                              |\n"
                + "|                              ****                          | 0.714\n"
                + "|                                  ****                      |\n"
                + "|                                      ******                |\n"
                + "|                                            **************  |\n"
                + "|                                                           *| 1.0\n"
                + "0.0                                                      0.885\n";
        assertEquals(expect, verify(stop, position, maxTime));
    }

    @Test
    public void backwardAccelerateCruseDecelerate() {
        StopLogicEngine stop = new StopLogicEngine();
        float position = 0.5f;
        float destination = 1;
        float currentVelocity = -0.6f;
        float maxTime = 0.9f;
        float maxAcceleration = 5.2f;
        float maxVelocity = 1.2f;
        stop.config(position, destination, currentVelocity, maxTime, maxAcceleration, maxVelocity);
        System.out.println(stop.debug("check1", 0));
        String expect = ""
                + "|*                                                           | 0.0\n"
                + "|                                                            |\n"
                + "|                                                            |\n"
                + "|                                                            |\n"
                + "|                                                            |\n"
                + "|                                                            | 0.357\n"
                + "| ***************                                            |\n"
                + "|*               *****                                       |\n"
                + "|                     ****                                   |\n"
                + "|                         ****                               |\n"
                + "|                             ****                           | 0.714\n"
                + "|                                 ****                       |\n"
                + "|                                     *****                  |\n"
                + "|                                          **********        |\n"
                + "|                                                    ****** *| 1.0\n"
                + "0.0                                                      0.885\n";
        assertEquals(expect, verify(stop, position, maxTime));
    }

    private static String verify(StopLogicEngine stop, float position, float maxTime) {
        float p = stop.getInterpolation(0);
        assertEquals(p, position, 0.0001);
        int count = 60;
        float step = maxTime / (count - 1);
        float[] x = new float[count];
        float[] y = new float[count];
        int c = 0;
        for (float t = 0; t < maxTime; t += step) {
            p = stop.getInterpolation(t);
            x[c] = t;
            y[c] = p;
            c++;
        }
        String ret = textDraw(count, count / 4, x, y, false);
        System.out.println(ret);
        return ret;
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
