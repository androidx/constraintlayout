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

import androidx.constraintlayout.core.parser.*;

import java.util.HashMap;

public class MotionSceneModel extends MotionModel {
    Header header;
    HashMap<String, TransitionModel> transitions = new HashMap<>();
    HashMap<String, ConstraintSetModel> constraintSets = new HashMap<>();
    TransitionModel mCurrentTransition;

    public TransitionModel getCurrentTransition() {
        return mCurrentTransition;
    }

    public ConstraintSetModel getConstraintSet(String name) {
        return constraintSets.get(name);
    }
    private TransitionModel getDefaultTransition() {
        if (transitions.containsKey("default")) {
            return transitions.get("default");
        }
        if (transitions.isEmpty()) {
            return null;
        }
        return transitions.values().iterator().next();
    }

    public ConstraintSetModel getFirstConstraintSet() {
        TransitionModel transitionModel = getDefaultTransition();
        if (transitionModel != null) {
            String str = transitionModel.getFrom();
            return constraintSets.get(str);
        }
        return constraintSets.values().iterator().next();
    }

    public void parse(String jsonString) {
        try {
            parse(CLParser.parse(jsonString));
        } catch (CLParsingException e) {
            throw new RuntimeException(e);
        }
    }

    public void parse(CLObject json) throws CLParsingException {
        int size = json.size();

        for (int j = 0; j < size; j++) {
            CLKey clkey = ((CLKey) json.get(j));
            String type = clkey.content();
            CLElement value = clkey.getValue();
            switch (type) {
                case "Header":
                    header = new Header((CLObject) value);
                    break;
                case "ConstraintSets":
                    ConstraintSetModel.parse((CLObject) value, constraintSets);

                    break;
                case "Transitions":
                    TransitionModel.parse((CLObject) value, transitions);
                    break;
            }
        }
        mCurrentTransition = getDefaultTransition();
    }

    public static void main(String[] arg) throws CLParsingException {
        String jsonStr = "{\n" +
                "                 Header: {\n" +
                "                  name: 'RotationZ28'\n" +
                "                },\n" +
                "                ConstraintSets: {\n" +
                "                  start: {\n" +
                "                    a: {\n" +
                "                      width: 40,\n" +
                "                      height: 40,\n" +
                "                      start: ['parent', 'start', 16],\n" +
                "                      bottom: ['parent', 'bottom', 16]\n" +
                "                    }\n" +
                "                  },\n" +
                "                  end: {\n" +
                "                    a: {\n" +
                "                      width: 40,\n" +
                "                      height: 40,\n" +
                "                      //rotationZ: 390,\n" +
                "                      end: ['parent', 'end', 16],\n" +
                "                      top: ['parent', 'top', 16]\n" +
                "                    }\n" +
                "                  }\n" +
                "                },\n" +
                "                Transitions: {\n" +
                "                  default: {\n" +
                "                    from: 'start',\n" +
                "                    to: 'end',\n" +
                "                    pathMotionArc: 'startHorizontal',\n" +
                "                    KeyFrames: {\n" +
                "                      KeyAttributes: [\n" +
                "                        {\n" +
                "                          target: ['a'],\n" +
                "                          frames: [33, 66],\n" +
                "                          rotationZ: [90, -90],\n" +
                "                          \n" +
                "                        }\n" +
                "                      ]\n" +
                "                    }\n" +
                "                  }\n" +
                "                }\n" +
                "            }";

        MotionSceneModel m = new MotionSceneModel();
        CLObject json = CLParser.parse(jsonStr);
        m.parse(json);
        System.out.println(m);
    }


}
