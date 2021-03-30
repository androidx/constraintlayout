/*
 * Copyright (C) 2018 The Android Open Source Project
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

package androidx.constraintlayout.motion.utils;

import androidx.constraintlayout.core.motion.utils.SpringStopEngine;
import androidx.constraintlayout.core.motion.utils.StopEngine;
import androidx.constraintlayout.core.motion.utils.StopLogicEngine;
import androidx.constraintlayout.motion.widget.MotionInterpolator;

/**
 * This contains the class to provide the logic for an animation to come to a stop.
 * The setup defines a series of velocity gradients that gets to the desired position
 * ending at 0 velocity.
 * The path is computed such that the velocities are continuous
 *
 * @hide
 */
public class StopLogic extends MotionInterpolator {
    private StopLogicEngine mStopLogicEngine = new StopLogicEngine();
    private SpringStopEngine mSpringStopEngine;
    private StopEngine engine = mStopLogicEngine;

    /**
     * Debugging logic to log the state.
     *
     * @param desc Description to pre append
     * @param time Time during animation
     * @return string useful for debugging the state of the StopLogic
     */

    public String debug(String desc, float time) {
        return engine.debug(desc, time);
    }

    public float getVelocity(float x) {
        return engine.getVelocity(x);
    }

    public void config(float currentPos, float destination, float currentVelocity, float maxTime, float maxAcceleration, float maxVelocity) {
        engine = mStopLogicEngine;
        mStopLogicEngine.config(currentPos, destination, currentVelocity, maxTime, maxAcceleration, maxVelocity);
    }

    public void springConfig(float currentPos, float destination, float currentVelocity,
                             float mass, float stiffness, float damping, float stopThreshold,
                             int boundaryMode) {
        if (mSpringStopEngine == null) {
            mSpringStopEngine = new SpringStopEngine();
        }
        engine = mSpringStopEngine;
        mSpringStopEngine.springConfig(currentPos, destination, currentVelocity, mass, stiffness, damping, stopThreshold, boundaryMode);
    }

    @Override
    public float getInterpolation(float v) {
        return engine.getInterpolation(v);
    }

    @Override
    public float getVelocity() {
        return engine.getVelocity();
    }

    public boolean isStopped() {
        return engine.isStopped();
    }
}
