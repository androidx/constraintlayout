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
import androidx.constraintlayout.core.motion.utils.Utils;
import androidx.constraintlayout.core.parser.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import static org.constraintlayout.swing.core.motion.model.JsonKeys.*;

public class MotionLayoutModel {
    String type;
    String name;
    TypedBundle data = new TypedBundle();
    HashMap<String, MotionLayoutModel> children = new HashMap<>();
    static HashMap<String, HashSet<String>> supportedChildren = new HashMap<>();

    static {
        supportedChildren.put(MOTION_SCENE, new HashSet<>(Arrays.asList(HEADER, CONSTRAINT_SETS, "Transitions")));
        supportedChildren.put(TRANSITIONS, new HashSet<>(Arrays.asList(DEFAULT_TRANSITION, KEY_FRAMES)));
        supportedChildren.put(KEY_FRAMES, new HashSet<>(
                Arrays.asList(TypedValues.Position.NAME,
                        TypedValues.Cycle.NAME, TypedValues.Attributes.NAME)));
    }

    HashSet<String> subGroup = new HashSet<>(Arrays.asList("KeyFrames", "KeyAttributes",
            TypedValues.Position.NAME,
            TypedValues.Cycle.NAME,
            TypedValues.Attributes.NAME));

    void parseMotionScene(String pad, String str) {
        name = "motionScene";
        try {
            CLObject json = CLParser.parse(str);
            parseTopLevel(supportedChildren.get(name), json);

        } catch (CLParsingException e) {
            e.printStackTrace();
        }
    }

    private interface Ids {
        int get(String str);
    }

    private interface DataType {
        int get(int str);
    }

    private static MotionLayoutModel parse(String type, CLKey key) {
        MotionLayoutModel model = new MotionLayoutModel();
        model.type = type;
        model.name = key.content();
        CLObject object = (CLObject) key.getValue();
        switch (type) {
            case HEADER:
                //todo model.parse(  object, );
                break;
            case TRANSITIONS:
                model.parseTransition(object, TypedValues.Transition::getId, TypedValues.Transition::getType);
                break;
        }

        return model;
    }

    private static MotionLayoutModel parse(String type, CLObject object) {
        MotionLayoutModel model = new MotionLayoutModel();
        model.type = type;
        model.name = object.content();
        if (supportedChildren.containsKey(type)) {
            model.parseTopLevel(supportedChildren.get(type), object);
        } else {
            Utils.log(" parse " + type);
            switch (type) {
                case HEADER:
                    // todo parser header ... model.parse(  object, );
                    break;
                case KEY_POSITIONS:
                    model.parse(object, TypedValues.Position::getId, TypedValues.Position::getType);
                    break;
                case KEY_CYCLES:
                    model.parse(object, TypedValues.Cycle::getId, TypedValues.Cycle::getType);
                    break;
                case KEY_ATTRIBUTES:
                    model.parse(object, TypedValues.Attributes::getId, TypedValues.Attributes::getType);
                    break;
                case TRANSITIONS:
                    model.parse(object, TypedValues.Transition::getId, TypedValues.Transition::getType);
                    break;
            }

        }
        return model;
    }

    static Ids getId(String type) {
        switch (type) {

            case KEY_POSITIONS:
                return TypedValues.Position::getId;
            case KEY_CYCLES:
                return TypedValues.Cycle::getId;
            case KEY_ATTRIBUTES:
                return TypedValues.Attributes::getId;
            case TRANSITIONS:
                return TypedValues.Transition::getId;
        }
        return null;
    }

    static DataType getType(String type) {
        switch (type) {
            case KEY_POSITIONS:
                return TypedValues.Position::getType;
            case KEY_CYCLES:
                return TypedValues.Cycle::getType;
            case KEY_ATTRIBUTES:
                return TypedValues.Attributes::getType;
            case TRANSITIONS:
                return TypedValues.Transition::getType;
        }
        return null;
    }

    void parseConstraintSet(CLObject obj) throws CLParsingException {
        int size = obj.size();
        for (int j = 0; j < size; j++) {
            Utils.log("   " + type + " " + obj.get(j).content());
        }
    }

    void parseHeader(CLObject obj) throws CLParsingException {
        int size = obj.size();
        for (int j = 0; j < size; j++) {
            Utils.log("   " + type + " " + obj.get(j).content());
        }
    }

    /**
     * Each element will be of type transition
     * @param obj
     * @throws CLParsingException
     */
    void parseTransitions(CLObject obj) throws CLParsingException {
        int size = obj.size();
        for (int i = 0; i < size; i++) {
            children.put(obj.content(), parse("Transitions", (CLKey) obj.get(i)));
        }
    }

    private void parseTopLevel(HashSet<String> child, CLObject parsedContent) {
        try {
            int n = parsedContent.size();
            for (int i = 0; i < n; i++) {
                CLKey clkey = ((CLKey) parsedContent.get(i));
                String type = clkey.content();
                CLElement value = clkey.getValue();
                if (value instanceof CLObject) {
                    switch (type) {
                        case CONSTRAINT_SETS:
                            parseConstraintSet((CLObject) value);
                            break;
                        case HEADER:
                            parseHeader((CLObject) value);
                            break;
                        case TRANSITIONS:
                            parseTransitions((CLObject) value);
                            break;
                    }
                    CLObject obj = (CLObject) value;

                }
                if (child.contains(type)) {
                    children.put(type, parse(type, (CLObject) value));
                    continue;
                }

            }
        } catch (CLParsingException e) {
            e.printStackTrace();
        }
    }

    private void parseTransition(CLObject parsedContent, Ids table, DataType dtype) {
        data.clear();
        try {
            int n = parsedContent.size();
            for (int i = 0; i < n; i++) {
                CLKey clkey = ((CLKey) parsedContent.get(i));
                String type = clkey.content();
                CLElement value = clkey.getValue();
                if (subGroup.contains(type)) {
                    MotionLayoutModel model = new MotionLayoutModel();
                    model.type = type;
                    model.name = type;
                    model.parseGroup((CLObject) value);
                    continue;
                }
                int id = table.get(type);
                if (id == -1) {
                    System.err.println("unknown type " + type + " value = " + value.getClass());
                    continue;
                }
                switch (dtype.get(id)) {
                    case TypedValues.FLOAT_MASK:
                        data.add(id, value.getFloat());
                        break;
                    case TypedValues.STRING_MASK:
                        data.add(id, value.content());
                        break;
                    case TypedValues.INT_MASK:
                        data.add(id, value.getInt());
                        break;
                    case TypedValues.BOOLEAN_MASK:
                        data.add(id, parsedContent.getBoolean(i));
                        break;
                }
            }
        } catch (CLParsingException e) {
            e.printStackTrace();
        }

    }

    private void parse(CLObject parsedContent, Ids table, DataType dtype) {
        data.clear();
        try {
            int n = parsedContent.size();
            for (int i = 0; i < n; i++) {
                CLKey clkey = ((CLKey) parsedContent.get(i));
                String type = clkey.content();
                CLElement value = clkey.getValue();
                if (subGroup.contains(type)) {
                    Utils.log(">>>>>>>>>>> sub group "+type);
                    MotionLayoutModel model = new MotionLayoutModel();
                    model.type = type;
                    model.name = type;
                    model.parseGroup((CLObject) value);
                    continue;
                }
                int id = table.get(type);
                Utils.log(">>" + type);
                if (id == -1) {
                    System.err.println("unknown type " + type + " value = " + value.getClass());
                    continue;
                }
                switch (dtype.get(id)) {
                    case TypedValues.FLOAT_MASK:
                        data.add(id, value.getFloat());
                        break;
                    case TypedValues.STRING_MASK:
                        data.add(id, value.content());
                        break;
                    case TypedValues.INT_MASK:
                        data.add(id, value.getInt());
                        break;
                    case TypedValues.BOOLEAN_MASK:
                        data.add(id, parsedContent.getBoolean(i));
                        break;
                }
            }
        } catch (CLParsingException e) {
            e.printStackTrace();
        }

    }

    private void parseGroup(CLObject obj) throws CLParsingException {
        int n = obj.size();
        for (int i = 0; i < n; i++) {
            CLKey key = (CLKey) obj.get(i);
            children.put(key.content(), parse(key.content(), key));
        }

    }

    public static void main(String[] arg) {
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
        String jsonStr2 = "{\n" +
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

        MotionLayoutModel mlm = new MotionLayoutModel();
        mlm.parseMotionScene("|", jsonStr2);
    }

}
