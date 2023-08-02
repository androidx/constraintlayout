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

import curves.*;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class SplineTest {

    @Test
    public void testBasic() {
        assertEquals(2, 1 + 1);
    }

    @Test
    public void unit_test_framework_working() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testSpline01() throws Exception {
        float[][] points = {
                {0, 2}, {1, 1}, {2, 0}
        };
        Spline spline = new Spline(Arrays.asList(points));

        assertEquals(1f, spline.getPos(0.5f, 1), 0.001);
        assertEquals(1f, spline.getPos(0.5f, 0), 0.001);
    }

    @Test
    public void testCurveFit01() throws Exception {
        float[][] points = {
                {0, 0}, {1, 1}, {2, 0}
        };
        float[] time = {
                0, 5, 10
        };
        MonoSpline spline = new MonoSpline(time, Arrays.asList(points));
        float value = spline.getPos(5, 0);
        assertEquals(1, value, 0.001);
        value = spline.getPos(7, 0);
        assertEquals(1.4, value, 0.001);
        value = spline.getPos(7, 1);
        assertEquals(0.744, value, 0.001);
    }

    @Test
    public void testCurveFit02() throws Exception {
        float[][] points = {
                {0, 0}, {1, 1}, {2, 0}
        };
        float[] time = {
                0, 5, 10
        };
        LinearCurve spline = new LinearCurve(time, Arrays.asList(points));
        float value = spline.getPos(5, 0);
        assertEquals(1, value, 0.001);
        value = spline.getPos(7, 0);
        assertEquals(1.4, value, 0.001);
        value = spline.getPos(7, 1);
        assertEquals(0.6, value, 0.001);
    }

    @Test
    public void testEasing01() throws Exception {
        float value, diffValue;
        Easing easing = new Easing.CubicEasing("cubic=(1,1,0,0)");
        value = easing.get(0.5f);
        assertEquals(0.5f, value, 0.001);
        diffValue = easing.getDiff(0.5f);
        assertEquals(1, diffValue, 0.001);
        diffValue = easing.getDiff(0.1f);
        assertEquals(1, diffValue, 0.001);
        diffValue = easing.getDiff(0.9f);
        assertEquals(1, diffValue, 0.001);

        easing = new Easing.CubicEasing("cubic=(1,0,0,1)");
        value = easing.get(0.5f);
        assertEquals(0.5, value, 0.001);

        diffValue = easing.getDiff(0.001f);
        assertEquals(0, diffValue, 0.001);
        diffValue = easing.getDiff(0.9999f);
        assertEquals(0, diffValue, 0.001);

        easing = new Easing.CubicEasing("cubic=(0.5,1,0.5,0)");
        value = easing.get(0.5f);
        assertEquals(0.5, value, 0.001);
        diffValue = easing.getDiff(0.5f);
        assertEquals(0, diffValue, 0.001);
        diffValue = easing.getDiff(0.00001f);
        assertEquals(2, diffValue, 0.001);
        diffValue = easing.getDiff(0.99999f);
        assertEquals(2, diffValue, 0.001);

    }

    @Test
    public void testLinearCurveFit01() throws Exception {
        float value;
        float[][] points = {
                {0, 0}, {1, 1}, {2, 0}
        };
        float[] time = {
                0, 5, 10
        };
        LinearCurve lcurve = new LinearCurve(time, Arrays.asList(points));
        value = lcurve.getPos(5, 0);
        assertEquals(1, value, 0.001);
        value = lcurve.getPos(7, 0);
        assertEquals(1.4, value, 0.001);
        value = lcurve.getPos(7, 1);
        assertEquals(0.6, value, 0.001);
    }

    @Test
    public void testOscillator01() throws Exception {
        Cycles o = new Cycles();
        o.setType(Cycles.SQUARE_WAVE, null);
        o.addPoint(0, 0);
        o.addPoint(0.5f, 10);
        o.addPoint(1, 0);
        o.normalize();
        assertEquals(19, countZeroCrossings(o, Cycles.SIN_WAVE));
        assertEquals(19, countZeroCrossings(o, Cycles.SQUARE_WAVE));
        assertEquals(19, countZeroCrossings(o, Cycles.TRIANGLE_WAVE));
        assertEquals(19, countZeroCrossings(o, Cycles.SAW_WAVE));
        assertEquals(19, countZeroCrossings(o, Cycles.REVERSE_SAW_WAVE));
        assertEquals(20, countZeroCrossings(o, Cycles.COS_WAVE));
    }

    private int countZeroCrossings(Cycles o, int type) {
        int n = 1000;
        float last = o.getValue(0, 0);
        int count = 0;
        o.setType(type, null);
        for (int i = 0; i < n; i++) {

            float v = o.getValue(0.0001f + i / (float) n, 0);
            if (v * last < 0) {
                count++;
            }
            last = v;
        }
        return count;
    }
}
