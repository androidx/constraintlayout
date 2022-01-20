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
package org.constraintlayout.swing.core;

import androidx.constraintlayout.core.motion.utils.TypedBundle;
import androidx.constraintlayout.core.motion.utils.TypedValues;
import androidx.constraintlayout.core.parser.CLArray;
import androidx.constraintlayout.core.parser.CLObject;
import androidx.constraintlayout.core.parser.CLParsingException;
import androidx.constraintlayout.core.state.Transition;

public class ParseKeyFrames {
    TypedBundle data = new TypedBundle();
    private int indexOf(String value, String... types) {
        for (int i = 0; i < types.length; i++) {
            if (value.equals(types[i])) {
                return i;
            }
        }
        return 0;
    }

    private void parseKeyPosition(CLObject keyPosition, Transition transition) throws CLParsingException {
        CLArray targets = keyPosition.getArray("target");
        CLArray frames = keyPosition.getArray("frames");
        CLArray percentX = keyPosition.getArrayOrNull("percentX");
        CLArray percentY = keyPosition.getArrayOrNull("percentY");
        CLArray percentWidth = keyPosition.getArrayOrNull("percentWidth");
        CLArray percentHeight = keyPosition.getArrayOrNull("percentHeight");
        String pathMotionArc = keyPosition.getStringOrNull("pathMotionArc");
        String transitionEasing = keyPosition.getStringOrNull("transitionEasing");
        String curveFit = keyPosition.getStringOrNull("curveFit");
        String type = keyPosition.getStringOrNull("type");
        if (type == null) {
            type = "parentRelative";
        }

        if (percentX != null && frames.size() != percentX.size()) {
            System.err.println(" frames size  != percentX size ");
            return;
        }
        if (percentY != null && frames.size() != percentY.size()) {
            System.err.println(" frames size  != percentY size ");
            return;
        }
        String target;
        for (int i = 0; i < targets.size(); i++) {
            data.clear();
            target = targets.getString(i);
            data.add(TypedValues.PositionType.TYPE_POSITION_TYPE, indexOf(type,
                    "deltaRelative", "pathRelative", "parentRelative"));


            if (curveFit != null) {
                data.add(TypedValues.PositionType.TYPE_CURVE_FIT, indexOf(curveFit, "spline", "linear"));
            }
            data.addIfNotNull(TypedValues.PositionType.TYPE_TRANSITION_EASING, transitionEasing);

            if (pathMotionArc != null) {
                data.add(TypedValues.PositionType.TYPE_PATH_MOTION_ARC,
                        indexOf(curveFit, "none", "startVertical", "startHorizontal", "flip"));
            }
            for (int j = 0; j < frames.size(); j++) {
                int frame = frames.getInt(j);
                data.add(TypedValues.TYPE_FRAME_POSITION, frame);

                if (percentX != null) {
                    data.add(TypedValues.PositionType.TYPE_PERCENT_X, percentX.getFloat(j));
                }
                if (percentY != null) {
                    data.add(TypedValues.PositionType.TYPE_PERCENT_Y, percentY.getFloat(j));
                }
                if (percentWidth != null) {
                    data.add(TypedValues.PositionType.TYPE_PERCENT_WIDTH, percentWidth.getFloat(j));
                }
                if (percentHeight != null) {
                    data.add(TypedValues.PositionType.TYPE_PERCENT_HEIGHT, percentHeight.getFloat(j));
                }

                transition.addKeyPosition(target, data);
            }
        }
    }


    private void parseKeyAttribute(CLObject keyAttribute, Transition transition) throws CLParsingException {
        CLArray targets = keyAttribute.getArray("target");
        CLArray frames = keyAttribute.getArray("frames");
        String transitionEasing = keyAttribute.getStringOrNull("transitionEasing");

        String[] attrNames = {
                TypedValues.AttributesType.S_SCALE_X,
                TypedValues.AttributesType.S_SCALE_Y,
                TypedValues.AttributesType.S_TRANSLATION_X,
                TypedValues.AttributesType.S_TRANSLATION_Y,
                TypedValues.AttributesType.S_TRANSLATION_Z,
                TypedValues.AttributesType.S_ROTATION_X,
                TypedValues.AttributesType.S_ROTATION_Y,
                TypedValues.AttributesType.S_ROTATION_Z,
        };
        int[] attrIds = {
                TypedValues.AttributesType.TYPE_SCALE_X,
                TypedValues.AttributesType.TYPE_SCALE_Y,
                TypedValues.AttributesType.TYPE_TRANSLATION_X,
                TypedValues.AttributesType.TYPE_TRANSLATION_Y,
                TypedValues.AttributesType.TYPE_TRANSLATION_Z,
                TypedValues.AttributesType.TYPE_ROTATION_X,
                TypedValues.AttributesType.TYPE_ROTATION_Y,
                TypedValues.AttributesType.TYPE_ROTATION_Z,
        };

        TypedBundle[] bundles = new TypedBundle[frames.size()];
        for (int i = 0; i < bundles.length; i++) {
            bundles[i] = new TypedBundle();
        }

        for (int k = 0; k < attrNames.length; k++) {
            String attrName = attrNames[k];
            int attrId = attrIds[k];


            CLArray arrayValues = keyAttribute.getArrayOrNull(attrName);

            if (arrayValues != null) {
                if (arrayValues.size() != bundles.length) {

                    throw new CLParsingException("incorrect size for $attrName array, " +
                            "not matching targets array!", keyAttribute);
                }

                for (int i = 0; i < bundles.length; i++) {
                    bundles[i].add(attrId, arrayValues.getFloat(i));
                }
            } else {
                float value = keyAttribute.getFloatOrNaN(attrName);
                if (!Float.isNaN(value)) {
                    for (int i = 0; i < bundles.length; i++) {
                        bundles[i].add(attrId, value);
                    }
                }
            }
        }

        String curveFit = keyAttribute.getStringOrNull("curveFit");
        for (int i = 0; i < targets.size(); i++) {
            String target = targets.getString(i);
            for (int j = 0; j < bundles.length; j++) {
                TypedBundle bundle = bundles[j];
                if (curveFit != null) {
                    bundle.add(TypedValues.PositionType.TYPE_CURVE_FIT, indexOf(curveFit, "spline", "linear"));
                }

                bundle.addIfNotNull(TypedValues.PositionType.TYPE_TRANSITION_EASING, transitionEasing);
                int frame = frames.getInt(j);
                bundle.add(TypedValues.TYPE_FRAME_POSITION, frame);
                transition.addKeyAttribute(target, bundle);
            }
        }
    }


    public void parseKeyCycle(CLObject keyCycleData, Transition transition) throws CLParsingException {
        CLArray targets = keyCycleData.getArray("target");
        CLArray frames = keyCycleData.getArray("frames");
        String transitionEasing = keyCycleData.getStringOrNull("transitionEasing");

        String[] attrNames = {
                TypedValues.CycleType.S_SCALE_X,
                TypedValues.CycleType.S_SCALE_Y,
                TypedValues.CycleType.S_TRANSLATION_X,
                TypedValues.CycleType.S_TRANSLATION_Y,
                TypedValues.CycleType.S_TRANSLATION_Z,
                TypedValues.CycleType.S_ROTATION_X,
                TypedValues.CycleType.S_ROTATION_Y,
                TypedValues.CycleType.S_ROTATION_Z,
                TypedValues.CycleType.S_WAVE_PERIOD,
                TypedValues.CycleType.S_WAVE_OFFSET,
                TypedValues.CycleType.S_WAVE_PHASE,
        };
        int[] attrIds = {
                TypedValues.CycleType.TYPE_SCALE_X,
                TypedValues.CycleType.TYPE_SCALE_Y,
                TypedValues.CycleType.TYPE_TRANSLATION_X,
                TypedValues.CycleType.TYPE_TRANSLATION_Y,
                TypedValues.CycleType.TYPE_TRANSLATION_Z,
                TypedValues.CycleType.TYPE_ROTATION_X,
                TypedValues.CycleType.TYPE_ROTATION_Y,
                TypedValues.CycleType.TYPE_ROTATION_Z,
                TypedValues.CycleType.TYPE_WAVE_PERIOD,
                TypedValues.CycleType.TYPE_WAVE_OFFSET,
                TypedValues.CycleType.TYPE_WAVE_PHASE,
        };

// TODO S_WAVE_SHAPE S_CUSTOM_WAVE_SHAPE

        TypedBundle[] bundles = new TypedBundle[frames.size()];
        for (int i = 0; i < bundles.length; i++) {
            TypedBundle bundle = bundles[i] = new TypedBundle();
        }
        for (int k = 0; k < attrNames.length; k++) {
            String attrName = attrNames[k];
            int attrId = attrIds[k];
            CLArray arrayValues = keyCycleData.getArrayOrNull(attrName);
            // array must contain one per frame
            if (arrayValues != null)
                if (arrayValues.size() != bundles.length) {
                    throw new CLParsingException("incorrect size for $attrName array, " +
                            "not matching targets array!", keyCycleData);
                }
            if (arrayValues != null) {
                for (int i = 0; i < bundles.length; i++) {
                    bundles[i].add(attrId, arrayValues.getFloat(i));
                }
            } else {
                float value = keyCycleData.getFloatOrNaN(attrName);
                if (!Float.isNaN(value)) {
                    for (int i = 0; i < bundles.length; i++) {
                        bundles[i].add(attrId, value);
                    }
                }
            }
        }
        String curveFit = keyCycleData.getStringOrNull(TypedValues.CycleType.S_CURVE_FIT);
        String easing = keyCycleData.getStringOrNull(TypedValues.CycleType.S_EASING);
        String waveShape = keyCycleData.getStringOrNull(TypedValues.CycleType.S_WAVE_SHAPE);
        String customWave = keyCycleData.getStringOrNull(TypedValues.CycleType.S_CUSTOM_WAVE_SHAPE);
        for (int i = 0; i < targets.size(); i++) {
            String target = targets.getString(i);

            for (int j = 0; j < bundles.length; j++) {
                TypedBundle bundle = bundles[j];

                if (curveFit != null) {
                    bundle.add(TypedValues.CycleType.TYPE_CURVE_FIT, indexOf(curveFit, "spline", "linear"));
                }
                bundle.addIfNotNull(TypedValues.PositionType.TYPE_TRANSITION_EASING, transitionEasing);
                if (easing != null) {
                    bundle.add(TypedValues.CycleType.TYPE_EASING, easing);
                }
                if (waveShape != null) {
                    bundle.add(TypedValues.CycleType.TYPE_WAVE_SHAPE, waveShape);
                }
                if (customWave != null) {
                    bundle.add(TypedValues.CycleType.TYPE_CUSTOM_WAVE_SHAPE, customWave);
                }

                int frame = frames.getInt(j);
                bundle.add(TypedValues.TYPE_FRAME_POSITION, frame);
                transition.addKeyCycle(target, bundle);


            }
        }
    }





}
