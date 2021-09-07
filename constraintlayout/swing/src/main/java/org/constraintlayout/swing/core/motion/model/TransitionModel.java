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
import androidx.constraintlayout.core.motion.utils.Utils;
import androidx.constraintlayout.core.parser.*;
import androidx.constraintlayout.core.state.Transition;

import java.util.ArrayList;
import java.util.HashMap;

public class TransitionModel extends MotionModel {
    TypedBundle data = new TypedBundle();
    String mName;
    ArrayList<KeyFrame> keyFrames = new ArrayList<>();
    private String mFrom,mTo;
    private Transition mTransition;

    public void updateTransition(Transition transition) {
        mTransition = transition;
        for (KeyFrame keyFrame : keyFrames) {
            keyFrame.updateTransition(transition);
        }
        transition.setTransitionProperties(data);
    }

    TransitionModel(String name, CLObject json) {
        mName = name;
        try {
            parseTransition(json,TypedValues.Transition::getId, TypedValues.Transition::getType );
        } catch (CLParsingException e) {
            e.printStackTrace();
        }
    }

    public static void parse(CLObject json, HashMap<String, TransitionModel> transitions) throws CLParsingException {
        int size = json.size();
        for (int j = 0; j < size; j++) {
            CLKey clkey = ((CLKey) json.get(j));
            String type = clkey.content();
            CLElement value = clkey.getValue();
            transitions.put(type, new TransitionModel(type,(CLObject) value));
        }
    }

    private void parseKeyFrames(CLObject json)throws CLParsingException {
        int n = json.size();
        for (int i = 0; i < n; i++) {
            CLKey clkey = ((CLKey) json.get(i));
            String type = clkey.content();
            CLElement value = clkey.getValue();
            switch (type){
                case "KeyAttributes":
                    if (value instanceof CLArray) {
                        KeyAttributeModel.parse((CLArray)value,keyFrames );
                    } else {
                        KeyAttributeModel.parse((CLObject)value,keyFrames );
                    }
                    break;
                case "KeyCycles":
                    KeyCycleModel.parse((CLObject)value,keyFrames );
                    break;
                case "KeyPositions":
                    KeyPositionModel.parse((CLObject)value,keyFrames );
                    break;
            }
        }
    }

    private void parseTransition(CLObject json, Ids table, DataType dtype)throws CLParsingException {
            int n = json.size();
            for (int i = 0; i < n; i++) {
                CLKey clkey = ((CLKey) json.get(i));
                String type = clkey.content();
                CLElement value = clkey.getValue();

                if ("KeyFrames".equals(type)) {
                    parseKeyFrames((CLObject)value);
                    continue;
                }
                int id = table.get(type);
                Utils.log(">>" + type);
                if (id == -1) {
                    System.err.println("unknown type " + type + " value = " + value.getClass());
                    continue;
                }
                String str;
                switch (dtype.get(id)) {
                    case TypedValues.FLOAT_MASK:
                        data.add(id, value.getFloat());
                        System.out.println("parse " + type + " FLOAT_MASK > " + value.getFloat());
                        break;
                    case TypedValues.STRING_MASK:
                        data.add(id, str = value.content());
                        if (id == TypedValues.Transition.TYPE_FROM) {
                            mFrom = str;
                        } else if (id == TypedValues.Transition.TYPE_TO) {
                            mTo = str;
                        }
                        System.out.println("parse " + type + " STRING_MASK > " + value.content());

                        break;
                    case TypedValues.INT_MASK:
                        data.add(id, value.getInt());
                        System.out.println("parse " + type + " INT_MASK > " + value.getInt());
                        break;
                    case TypedValues.BOOLEAN_MASK:
                        data.add(id, json.getBoolean(i));
                        break;
                }
            }

    }

    public String getFrom() {
       return mFrom;
    }

    public String getTo() {
        return mTo;
    }


}
