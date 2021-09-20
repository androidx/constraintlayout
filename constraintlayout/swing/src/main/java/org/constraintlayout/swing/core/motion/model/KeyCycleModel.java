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

import androidx.constraintlayout.core.motion.utils.TypedBundle;
import androidx.constraintlayout.core.motion.utils.TypedValues;
import androidx.constraintlayout.core.parser.CLArray;
import androidx.constraintlayout.core.parser.CLObject;
import androidx.constraintlayout.core.parser.CLParsingException;
import androidx.constraintlayout.core.state.Transition;
import java.util.ArrayList;

import static org.constraintlayout.swing.core.motion.model.JsonKeys.CURVE_FIT_TYPES;

public class KeyCycleModel extends KeyFrame {
    String mTarget;
    TypedBundle mData = new TypedBundle();

    private KeyCycleModel(String target, TypedBundle data) {
        mTarget = target;
        data.applyDelta(mData);
    }
    public void updateTransition(Transition transition) {
        transition.addKeyCycle(mTarget, mData);
    }

    public static void parse(CLObject json, ArrayList<KeyFrame> keyFrames) throws CLParsingException {
        CLArray targets = json.getArray("target");
        CLArray frames = json.getArray("frames");
        String transitionEasing = json.getStringOrNull("transitionEasing");

        String[] attrNames = {
                TypedValues.Cycle.S_SCALE_X,
                TypedValues.Cycle.S_SCALE_Y,
                TypedValues.Cycle.S_TRANSLATION_X,
                TypedValues.Cycle.S_TRANSLATION_Y,
                TypedValues.Cycle.S_TRANSLATION_Z,
                TypedValues.Cycle.S_ROTATION_X,
                TypedValues.Cycle.S_ROTATION_Y,
                TypedValues.Cycle.S_ROTATION_Z,
                TypedValues.Cycle.S_WAVE_PERIOD,
                TypedValues.Cycle.S_WAVE_OFFSET,
                TypedValues.Cycle.S_WAVE_PHASE,
        };
        int[] attrIds = {
                TypedValues.Cycle.TYPE_SCALE_X,
                TypedValues.Cycle.TYPE_SCALE_Y,
                TypedValues.Cycle.TYPE_TRANSLATION_X,
                TypedValues.Cycle.TYPE_TRANSLATION_Y,
                TypedValues.Cycle.TYPE_TRANSLATION_Z,
                TypedValues.Cycle.TYPE_ROTATION_X,
                TypedValues.Cycle.TYPE_ROTATION_Y,
                TypedValues.Cycle.TYPE_ROTATION_Z,
                TypedValues.Cycle.TYPE_WAVE_PERIOD,
                TypedValues.Cycle.TYPE_WAVE_OFFSET,
                TypedValues.Cycle.TYPE_WAVE_PHASE,
        };

// TODO S_WAVE_SHAPE S_CUSTOM_WAVE_SHAPE

        TypedBundle[] bundles = new TypedBundle[frames.size()];
        for (int i = 0; i < bundles.length; i++) {
            TypedBundle bundle = bundles[i] = new TypedBundle();
        }
        for (int k = 0; k < attrNames.length; k++) {
            String attrName = attrNames[k];
            int attrId = attrIds[k];
            CLArray arrayValues = json.getArrayOrNull(attrName);
            // array must contain one per frame
            if (arrayValues != null)
                if (arrayValues.size() != bundles.length) {
                    throw new CLParsingException("incorrect size for $attrName array, " +
                            "not matching targets array!", json);
                }
            if (arrayValues != null) {
                for (int i = 0; i < bundles.length; i++) {
                    bundles[i].add(attrId, arrayValues.getFloat(i));
                }
            } else {
                float value = json.getFloatOrNaN(attrName);
                if (!Float.isNaN(value)) {
                    for (int i = 0; i < bundles.length; i++) {
                        bundles[i].add(attrId, value);
                    }
                }
            }
        }
        String curveFit = json.getStringOrNull(TypedValues.Cycle.S_CURVE_FIT);
        String easing = json.getStringOrNull(TypedValues.Cycle.S_EASING);
        String waveShape = json.getStringOrNull(TypedValues.Cycle.S_WAVE_SHAPE);
        String customWave = json.getStringOrNull(TypedValues.Cycle.S_CUSTOM_WAVE_SHAPE);
        for (int i = 0; i < targets.size(); i++) {
            String target = targets.getString(i);

            for (int j = 0; j < bundles.length; j++) {
                TypedBundle bundle = bundles[j];

                if (curveFit != null) {
                    bundle.add(TypedValues.Cycle.TYPE_CURVE_FIT, indexOf(curveFit, CURVE_FIT_TYPES));
                }
                bundle.addIfNotNull(TypedValues.Position.TYPE_TRANSITION_EASING, transitionEasing);
                if (easing != null) {
                    bundle.add(TypedValues.Cycle.TYPE_EASING, easing);
                }
                if (waveShape != null) {
                    bundle.add(TypedValues.Cycle.TYPE_WAVE_SHAPE, waveShape);
                }
                if (customWave != null) {
                    bundle.add(TypedValues.Cycle.TYPE_CUSTOM_WAVE_SHAPE, customWave);
                }

                int frame = frames.getInt(j);
                bundle.add(TypedValues.TYPE_FRAME_POSITION, frame);
                keyFrames.add(new KeyCycleModel(target, bundle));
            }
        }

    }
}
