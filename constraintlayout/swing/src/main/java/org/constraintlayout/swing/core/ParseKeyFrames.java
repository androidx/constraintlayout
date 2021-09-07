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
            data.add(TypedValues.Position.TYPE_POSITION_TYPE, indexOf(type,
                    "deltaRelative", "pathRelative", "parentRelative"));


            if (curveFit != null) {
                data.add(TypedValues.Position.TYPE_CURVE_FIT, indexOf(curveFit, "spline", "linear"));
            }
            data.addIfNotNull(TypedValues.Position.TYPE_TRANSITION_EASING, transitionEasing);

            if (pathMotionArc != null) {
                data.add(TypedValues.Position.TYPE_PATH_MOTION_ARC,
                        indexOf(curveFit, "none", "startVertical", "startHorizontal", "flip"));
            }
            for (int j = 0; j < frames.size(); j++) {
                int frame = frames.getInt(j);
                data.add(TypedValues.TYPE_FRAME_POSITION, frame);

                if (percentX != null) {
                    data.add(TypedValues.Position.TYPE_PERCENT_X, percentX.getFloat(j));
                }
                if (percentY != null) {
                    data.add(TypedValues.Position.TYPE_PERCENT_Y, percentY.getFloat(j));
                }
                if (percentWidth != null) {
                    data.add(TypedValues.Position.TYPE_PERCENT_WIDTH, percentWidth.getFloat(j));
                }
                if (percentHeight != null) {
                    data.add(TypedValues.Position.TYPE_PERCENT_HEIGHT, percentHeight.getFloat(j));
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
                TypedValues.Attributes.S_SCALE_X,
                TypedValues.Attributes.S_SCALE_Y,
                TypedValues.Attributes.S_TRANSLATION_X,
                TypedValues.Attributes.S_TRANSLATION_Y,
                TypedValues.Attributes.S_TRANSLATION_Z,
                TypedValues.Attributes.S_ROTATION_X,
                TypedValues.Attributes.S_ROTATION_Y,
                TypedValues.Attributes.S_ROTATION_Z,
        };
        int[] attrIds = {
                TypedValues.Attributes.TYPE_SCALE_X,
                TypedValues.Attributes.TYPE_SCALE_Y,
                TypedValues.Attributes.TYPE_TRANSLATION_X,
                TypedValues.Attributes.TYPE_TRANSLATION_Y,
                TypedValues.Attributes.TYPE_TRANSLATION_Z,
                TypedValues.Attributes.TYPE_ROTATION_X,
                TypedValues.Attributes.TYPE_ROTATION_Y,
                TypedValues.Attributes.TYPE_ROTATION_Z,
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
                    bundle.add(TypedValues.Position.TYPE_CURVE_FIT, indexOf(curveFit, "spline", "linear"));
                }

                bundle.addIfNotNull(TypedValues.Position.TYPE_TRANSITION_EASING, transitionEasing);
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
        String curveFit = keyCycleData.getStringOrNull(TypedValues.Cycle.S_CURVE_FIT);
        String easing = keyCycleData.getStringOrNull(TypedValues.Cycle.S_EASING);
        String waveShape = keyCycleData.getStringOrNull(TypedValues.Cycle.S_WAVE_SHAPE);
        String customWave = keyCycleData.getStringOrNull(TypedValues.Cycle.S_CUSTOM_WAVE_SHAPE);
        for (int i = 0; i < targets.size(); i++) {
            String target = targets.getString(i);

            for (int j = 0; j < bundles.length; j++) {
                TypedBundle bundle = bundles[j];

                if (curveFit != null) {
                    bundle.add(TypedValues.Cycle.TYPE_CURVE_FIT, indexOf(curveFit, "spline", "linear"));
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
                transition.addKeyCycle(target, bundle);


            }
        }
    }





}
