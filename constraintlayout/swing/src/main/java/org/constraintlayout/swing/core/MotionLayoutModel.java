package org.constraintlayout.swing.core;

import androidx.constraintlayout.core.motion.utils.TypedBundle;
import androidx.constraintlayout.core.motion.utils.TypedValues;
import androidx.constraintlayout.core.motion.utils.Utils;
import androidx.constraintlayout.core.parser.*;
import androidx.constraintlayout.core.state.Transition;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class MotionLayoutModel {
    String type;
    String name;
    TypedBundle data = new TypedBundle();
    HashMap<String, MotionLayoutModel> children = new HashMap<>();
    static HashMap<String, HashSet<String>> supportedChildren = new HashMap<>();

    static {
        supportedChildren.put("motionScene", new HashSet<>(Arrays.asList("Header", "ConstraintSets", "Transitions")));
        supportedChildren.put("Transitions", new HashSet<>(Arrays.asList("default", "KeyFrames")));
        supportedChildren.put("KeyFrames", new HashSet<>(
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
        Utils.log(">>>>>>>>>>>>>> parse a "+type+" named "+key.content() );
        model.type = type;
        model.name = key.content();
        CLObject object = (CLObject) key.getValue();

        // track.add(type);
        switch (type) {
            case "Header":
                //model.parse(  object, );
                break;
            case "Transitions":
                model.parseTransition(object, TypedValues.Transition::getId, TypedValues.Transition::getType);
                break;
        }

        return model;
    }


    private static MotionLayoutModel parseKey(String type, CLKey key) throws CLParsingException {
        MotionLayoutModel model = new MotionLayoutModel();

        model.type = type;
        model.name = key.content();
        Ids ids = getId(type);
        DataType dataType = getType(type);
        CLElement val = key.getValue();
        CLElement[] elements;
        if (val instanceof CLArray) {
            elements = new CLElement[((CLArray) val).size()];
            for (int i = 0; i < elements.length; i++) {
                elements[i] = ((CLArray) val).get(i);
            }
        } else {
            elements = new CLElement[1];
            elements[0] = val;
        }


        for (int i = 0; i < elements.length; i++) {
            CLElement element = elements[i];


            Utils.log(" parse " + type);
           // track.add(type);
//            switch (type) {
//                case "KeyPositions":
//                    model.parse(object, TypedValues.Position::getId, TypedValues.Position::getType);
//                    break;
//                case "KeyCycles":
//                    model.parse(object, TypedValues.Cycle::getId, TypedValues.Cycle::getType);
//                    break;
//                case "KeyAttributes":
//                    model.parse(object, TypedValues.Attributes::getId, TypedValues.Attributes::getType);
//                    break;
//            }

        }
        return model;
    }


    private static MotionLayoutModel parse(String type, CLObject object) {
        MotionLayoutModel model = new MotionLayoutModel();

        model.type = type;

        model.name = object.content();
        if (supportedChildren.containsKey(type)) {
            Utils.log(" parseTopLevel " + type);
            model.parseTopLevel(supportedChildren.get(type), object);
        } else {
            Utils.log(" parse " + type);
            //track.add(type);
            switch (type) {
                case "Header":
                    //model.parse(  object, );
                    break;
                case "KeyPositions":
                    model.parse(object, TypedValues.Position::getId, TypedValues.Position::getType);
                    break;
                case "KeyCycles":
                    model.parse(object, TypedValues.Cycle::getId, TypedValues.Cycle::getType);
                    break;
                case "KeyAttributes":
                    model.parse(object, TypedValues.Attributes::getId, TypedValues.Attributes::getType);
                    break;
                case "Transitions":
                    model.parse(object, TypedValues.Transition::getId, TypedValues.Transition::getType);
                    break;
            }

        }
        return model;
    }

    static Ids getId(String type) {
        switch (type) {

            case "KeyPositions":
                return TypedValues.Position::getId;
            case "KeyCycles":
                return TypedValues.Cycle::getId;
            case "KeyAttributes":
                return TypedValues.Attributes::getId;
            case "Transitions":
                return TypedValues.Transition::getId;
        }
        return null;
    }

    static DataType getType(String type) {
        switch (type) {
            case "KeyPositions":
                return TypedValues.Position::getType;
            case "KeyCycles":
                return TypedValues.Cycle::getType;
            case "KeyAttributes":
                return TypedValues.Attributes::getType;
            case "Transitions":
                return TypedValues.Transition::getType;
        }
        return null;
    }


    void parseConstraintSet(CLObject obj) throws CLParsingException {
        int size = obj.size();
        for (int j = 0; j < size; j++) {


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
                Utils.log(" xx " + type);
                // track.add(type);
                if (value instanceof CLObject) {
                    switch (type) {
                        case "ConstraintSets":
                            parseConstraintSet((CLObject) value);
                            break;
                        case "Header":
                            parseHeader((CLObject) value);

                            break;
                        case "Transitions":
                            parseTransitions((CLObject) value);

                            break;

                    }
                    CLObject obj = (CLObject) value;

                }
                if (child.contains(type)) {
                    children.put(type, parse(type, (CLObject) value));
                    continue;
                }
                System.err.println("unknown type " + type + " value = " + value.getClass());

            }
        } catch (CLParsingException e) {
            e.printStackTrace();
        }
    }

    private void parseTransition(CLObject parsedContent, Ids table, DataType dtype) {
        data.clear();
        Utils.log("---------------------");
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
                        System.out.println("parse " + type + " FLOAT_MASK > " + value.getFloat());
                        break;
                    case TypedValues.STRING_MASK:
                        data.add(id, value.content());
                        System.out.println("parse " + type + " STRING_MASK > " + value.content());

                        break;
                    case TypedValues.INT_MASK:
                        data.add(id, value.getInt());
                        System.out.println("parse " + type + " INT_MASK > " + value.getInt());
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
        Utils.log("---------------------");
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
                        System.out.println("parse " + type + " FLOAT_MASK > " + value.getFloat());
                        break;
                    case TypedValues.STRING_MASK:
                        data.add(id, value.content());
                        System.out.println("parse " + type + " STRING_MASK > " + value.content());

                        break;
                    case TypedValues.INT_MASK:
                        data.add(id, value.getInt());
                        System.out.println("parse " + type + " INT_MASK > " + value.getInt());
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

    static StackTrack track = new StackTrack();

    static class StackTrack {
        HashMap<String, String> methodNote = new HashMap<>();

        public void add(String str) {
            StackTraceElement[] st = new Throwable().getStackTrace();
//            String stack = ".(" + st[1].getFileName() + ":" + st[1].getLineNumber() + ") " + st[1].getMethodName();
//            stack += "       " + str;
            methodNote.put(st[1].getMethodName(), str);
        }

        public void log(String msg, int n) {
            StackTraceElement[] st = new Throwable().getStackTrace();
            String s = " ";
            n = Math.min(n, st.length - 1);
            for (int i = 1; i <= n; i++) {
                StackTraceElement ste = st[i];
                String stack = ".(" + st[i].getFileName() + ":" + st[i].getLineNumber() + ") " + st[i].getMethodName();
                s += " ";
                String str = (methodNote.containsKey(st[i].getMethodName())) ? st[i].getMethodName() : msg;

                System.out.println(str + s + stack + s);
            }

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
