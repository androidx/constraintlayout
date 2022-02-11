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
package androidx.constraintlayout.core.motion;

import static org.junit.Assert.assertEquals;

import androidx.constraintlayout.core.motion.key.MotionKeyTimeCycle;
import androidx.constraintlayout.core.motion.utils.ArcCurveFit;
import androidx.constraintlayout.core.motion.utils.KeyCache;
import androidx.constraintlayout.core.motion.utils.TypedValues;

import org.junit.Test;

public class MotionKeyTimeCycleTest {
    private static final boolean DEBUG = true;
    private static final int SAMPLES = 30;
    private static boolean DISABLE = true;

    void cycleBuilder(Scene s, int type) {
        float[] amp = {0, 50, 0};
        int[] pos = {0, 50, 100};
        float[] period = {0, 2, 0};
        for (int i = 0; i < amp.length; i++) {
            MotionKeyTimeCycle cycle = new MotionKeyTimeCycle();
            cycle.setValue(type, amp[i]);
            cycle.setValue(TypedValues.CycleType.TYPE_WAVE_PERIOD, period[i]);
            cycle.setFramePosition(pos[i]);
            s.motion.addKey(cycle);
        }
    }

    public Scene basicRunThrough(int type) {
        Scene s = new Scene();
        cycleBuilder(s, type);
        s.setup();

        if (DEBUG) {
            s.sample(() -> {
                System.out.println(s.res.getValueAttributes(type));
            });
        }
        s.motion.interpolate(s.res, 0.5f, 1000000 + 1000, s.cache);
        return s;
    }

    @Test
    public void disabled() {
        if (DISABLE) {
            System.out.println(" all test in MotionKeyTimeCycle DISABLE!");
        }
        assertEquals(DISABLE, true);
    }

    @Test
    public void keyCycleRotationX() {
        if (DISABLE) {
            return;
        }

        Scene s = basicRunThrough(TypedValues.CycleType.TYPE_ROTATION_X);
        assertEquals(0.0, s.res.getRotationX(), 0.0001);
    }

    @Test
    public void keyCycleRotationY() {
        if (DISABLE) {
            return;
        }
        Scene s = basicRunThrough(TypedValues.CycleType.TYPE_ROTATION_Y);
        assertEquals(0.0, s.res.getRotationY(), 0.0001);
    }

    @Test
    public void keyCycleRotationZ() {
        if (DISABLE) {
            return;
        }
        Scene s = basicRunThrough(TypedValues.CycleType.TYPE_ROTATION_Z);
        assertEquals(0.0, s.res.getRotationZ(), 0.0001);
    }

    @Test
    public void keyCycleTranslationX() {
        if (DISABLE) {
            return;
        }
        Scene s = basicRunThrough(TypedValues.CycleType.TYPE_TRANSLATION_X);
        assertEquals(0.0, s.res.getTranslationX(), 0.0001);
    }

    @Test
    public void keyCycleTranslationY() {
        if (DISABLE) {
            return;
        }
        Scene s = basicRunThrough(TypedValues.CycleType.TYPE_TRANSLATION_Y);
        assertEquals(0.0, s.res.getTranslationY(), 0.0001);
    }

    @Test
    public void keyCycleTranslationZ() {
        if (DISABLE) {
            return;
        }
        Scene s = basicRunThrough(TypedValues.CycleType.TYPE_TRANSLATION_Z);
        assertEquals(0.0, s.res.getTranslationZ(), 0.0001);
    }

    @Test
    public void keyCycleScaleX() {
        if (DISABLE) {
            return;
        }
        Scene s = basicRunThrough(TypedValues.CycleType.TYPE_SCALE_X);
        assertEquals(0.0, s.res.getScaleX(), 0.0001);
    }

    @Test
    public void keyCycleScaleY() {
        if (DISABLE) {
            return;
        }
        Scene s = basicRunThrough(TypedValues.CycleType.TYPE_SCALE_Y);
        assertEquals(0.0, s.res.getScaleY(), 0.0001);
    }

    class Scene {
        MotionWidget mw1 = new MotionWidget();
        MotionWidget mw2 = new MotionWidget();
        MotionWidget res = new MotionWidget();
        KeyCache cache = new KeyCache();
        Motion motion;

        Scene() {
            motion = new Motion(mw1);
            mw1.setBounds(0, 0, 30, 40);
            mw2.setBounds(400, 400, 430, 440);
            motion.setPathMotionArc(ArcCurveFit.ARC_START_VERTICAL);
        }


        public void setup() {
            motion.setStart(mw1);
            motion.setEnd(mw2);
            motion.setup(1000, 1000, 1, 1000000);
        }

        void sample(Runnable r) {
            for (int p = 0; p <= SAMPLES; p++) {
                motion.interpolate(res, p * 0.1f, 1000000 + (int) (p * 100), cache);
                r.run();
            }
        }
    }
}
