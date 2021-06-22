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

import androidx.constraintlayout.core.motion.utils.ArcCurveFit;
import androidx.constraintlayout.core.motion.utils.KeyCache;
import androidx.constraintlayout.core.motion.utils.TypedValues;

import org.junit.Test;

public class MotionCustomAttributesTest {
    private static final boolean DEBUG = false;

    @Test
    public void testBasic() {
        assertEquals(2, 1 + 1);

    }

    class Scene {
        MotionWidget mw1 = new MotionWidget();
        MotionWidget mw2 = new MotionWidget();
        MotionWidget res = new MotionWidget();
        KeyCache cache = new KeyCache();
        Motion motion;
        float pos;

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
            for (int p = 0; p <= 10; p++) {
                pos = p;
                motion.interpolate(res, p * 0.1f, 1000000 + (int) (p * 100), cache);
                r.run();
            }
        }
    }

    @Test
    public void customFloat() {
        Scene s = new Scene();
        s.mw1.setCustomAttribute("bob", TypedValues.Custom.TYPE_FLOAT, 0f);
        s.mw2.setCustomAttribute("bob", TypedValues.Custom.TYPE_FLOAT, 1f);
        s.setup();

        if (DEBUG) {
            s.sample(() -> {
                System.out.println(s.res.getCustomAttribute("bob").getFloatValue());
            });
        }
        s.motion.interpolate(s.res, 0.5f, 1000000 + 1000, s.cache);
        assertEquals(0.5, s.res.getCustomAttribute("bob").getFloatValue(), 0.001);
    }

    @Test
    public void customColor1() {
        Scene s = new Scene();
        s.mw1.setCustomAttribute("fish", TypedValues.Custom.TYPE_COLOR, 0xFF00FF00);
        s.mw2.setCustomAttribute("fish", TypedValues.Custom.TYPE_COLOR, 0xFFFF00FF);
        s.setup();

        if (DEBUG) {
            s.sample(() -> {
                System.out.println(s.pos + " " +
                        Integer.toHexString(
                                s.res.getCustomAttribute("fish")
                                        .getColorValue()));
            });
        }
        s.motion.interpolate(s.res, 0.5f, 1000000 + 1000, s.cache);
        assertEquals(0xffbababa, s.res.getCustomAttribute("fish").getColorValue());
    }

    @Test
    public void customColor2() {
        Scene s = new Scene();
        s.mw1.setCustomAttribute("fish", TypedValues.Custom.TYPE_COLOR, 0xFF000000);
        s.mw2.setCustomAttribute("fish", TypedValues.Custom.TYPE_COLOR, 0x00880088);
        s.setup();

        if (DEBUG) {
            s.sample(() -> {
                System.out.println(s.pos + " " +
                        Integer.toHexString(
                                s.res.getCustomAttribute("fish")
                                        .getColorValue()));
            });
        }
        s.motion.interpolate(s.res, 0.5f, 1000000 + 1000, s.cache);
        assertEquals(0x7f630063, s.res.getCustomAttribute("fish").getColorValue());
    }


}
