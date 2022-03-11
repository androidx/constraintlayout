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

package androidx.constraintlayout.core.motion.utils;

public interface StopEngine {
    /**
     * @TODO: add description
     * @param desc
     * @param time
     * @return
     */
    String debug(String desc, float time);

    /**
     * @TODO: add description
     * @param x
     * @return
     */
    float getVelocity(float x);

    /**
     * @TODO: add description
     * @param v
     * @return
     */
    float getInterpolation(float v);

    /**
     * @TODO: add description
     * @return
     */
    float getVelocity();

    /**
     * @TODO: add description
     * @return
     */
    boolean isStopped();
}
