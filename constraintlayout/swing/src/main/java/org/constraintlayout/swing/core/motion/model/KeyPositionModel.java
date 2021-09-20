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

import static org.constraintlayout.swing.core.motion.model.JsonKeys.*;

public class KeyPositionModel extends KeyFrame {
    String mTarget;
    TypedBundle mData = new TypedBundle();

    private KeyPositionModel(String target, TypedBundle data) {
        mTarget = target;
        data.applyDelta(mData);
    }

    public void updateTransition(Transition transition) {
        transition.addKeyPosition(mTarget, mData);
    }

    public static void parse(CLObject keyPosition, ArrayList<KeyFrame> keyFrames) throws CLParsingException {
        TypedBundle data = new TypedBundle();

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
                    POSITION_TYPE));


            if (curveFit != null) {
                data.add(TypedValues.Position.TYPE_CURVE_FIT, indexOf(curveFit, CURVE_FIT_TYPES));
            }
            data.addIfNotNull(TypedValues.Position.TYPE_TRANSITION_EASING, transitionEasing);

            if (pathMotionArc != null) {
                data.add(TypedValues.Position.TYPE_PATH_MOTION_ARC,
                        indexOf(curveFit, PATH_MOTION_ARC_TYPES));
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

                keyFrames.add(new KeyPositionModel(target, data));
            }
        }

    }
}
