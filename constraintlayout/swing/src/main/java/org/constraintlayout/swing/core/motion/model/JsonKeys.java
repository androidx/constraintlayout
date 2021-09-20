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

package org.constraintlayout.swing.core.motion.model;

public class JsonKeys {
    public static final String MOTION_SCENE = "motionScene";
    public static final String HEADER = "Header";
    public static final String CONSTRAINT_SETS = "ConstraintSets";
    public static final String KEY_FRAMES = "KeyFrames";
    public static final String DEFAULT_TRANSITION = "default";
    public static final String TRANSITIONS = "Transitions";
    public static final String KEY_POSITIONS = "KeyPositions";
    public static final String KEY_CYCLES = "KeyCycles";
    public static final String KEY_ATTRIBUTES = "KeyAttributes";
    public static final String []CURVE_FIT_TYPES = {"spline", "linear"};
    public static final String [] PATH_MOTION_ARC_TYPES = {"none", "startVertical", "startHorizontal", "flip"};
    public static final String [] POSITION_TYPE = {"deltaRelative", "pathRelative", "parentRelative"};

    public static int indexOf(String value, String... types) {
        for (int i = 0; i < types.length; i++) {
            if (value.equals(types[i])) {
                return i;
            }
        }
        return 0;
    }
}
