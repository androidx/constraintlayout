/*
 * Copyright (C) 2022 The Android Open Source Project
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

package androidx.constraintlayout.core.state;

import static androidx.constraintlayout.core.motion.utils.TypedValues.MotionType.TYPE_QUANTIZE_INTERPOLATOR_TYPE;
import static androidx.constraintlayout.core.motion.utils.TypedValues.MotionType.TYPE_QUANTIZE_MOTIONSTEPS;
import static androidx.constraintlayout.core.motion.utils.TypedValues.MotionType.TYPE_QUANTIZE_MOTION_PHASE;

import androidx.constraintlayout.core.motion.utils.TypedBundle;
import androidx.constraintlayout.core.motion.utils.TypedValues;
import androidx.constraintlayout.core.parser.CLArray;
import androidx.constraintlayout.core.parser.CLElement;
import androidx.constraintlayout.core.parser.CLKey;
import androidx.constraintlayout.core.parser.CLNumber;
import androidx.constraintlayout.core.parser.CLObject;
import androidx.constraintlayout.core.parser.CLParser;
import androidx.constraintlayout.core.parser.CLParsingException;
import androidx.constraintlayout.core.parser.CLString;
import androidx.constraintlayout.core.state.helpers.BarrierReference;
import androidx.constraintlayout.core.state.helpers.ChainReference;
import androidx.constraintlayout.core.state.helpers.GuidelineReference;
import androidx.constraintlayout.core.widgets.ConstraintWidget;

import java.util.ArrayList;
import java.util.HashMap;

public class ConstraintSetParser {

    private static final boolean PARSER_DEBUG = false;

    public static class DesignElement {
        String mId;
        String mType;
        HashMap<String, String> mParams;

        public String getId() {
            return mId;
        }

        public String getType() {
            return mType;
        }

        public HashMap<String, String> getParams() {
            return mParams;
        }

        DesignElement(String id,
                String type,
                HashMap<String, String> params) {
            mId = id;
            mType = type;
            mParams = params;
        }
    }

    /**
     * Provide the storage for managing Variables in the system.
     * When the json has a variable:{   } section this is used.
     */
    public static class LayoutVariables {
        HashMap<String, Integer> mMargins = new HashMap<>();
        HashMap<String, GeneratedValue> mGenerators = new HashMap<>();
        HashMap<String, ArrayList<String>> mArrayIds = new HashMap<>();

        void put(String elementName, int element) {
            mMargins.put(elementName, element);
        }

        void put(String elementName, float start, float incrementBy) {
            if (mGenerators.containsKey(elementName)) {
                if (mGenerators.get(elementName) instanceof OverrideValue) {
                    return;
                }
            }
            mGenerators.put(elementName, new Generator(start, incrementBy));
        }

        void put(String elementName,
                float from,
                float to,
                float step,
                String prefix,
                String postfix) {
            if (mGenerators.containsKey(elementName)) {
                if (mGenerators.get(elementName) instanceof OverrideValue) {
                    return;
                }
            }
            FiniteGenerator generator =
                    new FiniteGenerator(from, to, step, prefix, postfix);
            mGenerators.put(elementName, generator);
            mArrayIds.put(elementName, generator.array());

        }

        /**
         * insert an override variable
         *
         * @param elementName the name
         * @param value       the value a float
         */
        public void putOverride(String elementName, float value) {
            GeneratedValue generator = new OverrideValue(value);
            mGenerators.put(elementName, generator);
        }

        float get(Object elementName) {
            if (elementName instanceof CLString) {
                String stringValue = ((CLString) elementName).content();
                if (mGenerators.containsKey(stringValue)) {
                    return mGenerators.get(stringValue).value();
                }
                if (mMargins.containsKey(stringValue)) {
                    return mMargins.get(stringValue).floatValue();
                }
            } else if (elementName instanceof CLNumber) {
                return ((CLNumber) elementName).getFloat();
            }
            return 0f;
        }

        ArrayList<String> getList(String elementName) {
            if (mArrayIds.containsKey(elementName)) {
                return mArrayIds.get(elementName);
            }
            return null;
        }

        void put(String elementName, ArrayList<String> elements) {
            mArrayIds.put(elementName, elements);
        }

    }

    interface GeneratedValue {
        float value();
    }

    /**
     * Generate a floating point value
     */
    static class Generator implements GeneratedValue {
        float mStart = 0;
        float mIncrementBy = 0;
        float mCurrent = 0;
        boolean mStop = false;

        Generator(float start, float incrementBy) {
            mStart = start;
            mIncrementBy = incrementBy;
            mCurrent = start;
        }

        @Override
        public float value() {
            if (!mStop) {
                mCurrent += mIncrementBy;
            }
            return mCurrent;
        }
    }

    /**
     * Generate values like button1, button2 etc.
     */
    static class FiniteGenerator implements GeneratedValue {
        float mFrom = 0;
        float mTo = 0;
        float mStep = 0;
        boolean mStop = false;
        String mPrefix;
        String mPostfix;
        float mCurrent = 0;
        float mInitial;
        float mMax;

        FiniteGenerator(float from,
                float to,
                float step,
                String prefix,
                String postfix) {
            mFrom = from;
            mTo = to;
            mStep = step;
            mPrefix = (prefix == null) ? "" : prefix;
            mPostfix = (postfix == null) ? "" : postfix;
            mMax = to;
            mInitial = from;
        }

        @Override
        public float value() {
            if (mCurrent >= mMax) {
                mStop = true;
            }
            if (!mStop) {
                mCurrent += mStep;
            }
            return mCurrent;
        }

        public ArrayList<String> array() {
            ArrayList<String> array = new ArrayList<>();
            int value = (int) mInitial;
            int maxInt = (int) mMax;
            for (int i = value; i <= maxInt; i++) {
                array.add(mPrefix + value + mPostfix);
                value += mStep;
            }
            return array;

        }

    }

    static class OverrideValue implements GeneratedValue {
        float mValue;

        OverrideValue(float value) {
            mValue = value;
        }

        @Override
        public float value() {
            return mValue;
        }
    }
//==================== end store variables =========================
//==================== MotionScene =========================

    public enum MotionLayoutDebugFlags {
        NONE,
        SHOW_ALL,
        UNKNOWN
    }

    //==================== end Motion Scene =========================

    /**
     * Parse and populate a transition
     *
     * @param content    JSON string to parse
     * @param transition The Transition to be populated
     * @param state      @TODO what is this
     */
    public static void parseJSON(String content, Transition transition, int state) {
        try {
            CLObject json = CLParser.parse(content);
            ArrayList<String> elements = json.names();
            if (elements == null) {
                return;
            }
            for (String elementName : elements) {
                CLElement base_element = json.get(elementName);
                if (base_element instanceof CLObject) {
                    CLObject element = (CLObject) base_element;
                    CLObject customProperties = element.getObjectOrNull("custom");
                    if (customProperties != null) {
                        ArrayList<String> properties = customProperties.names();
                        for (String property : properties) {
                            CLElement value = customProperties.get(property);
                            if (value instanceof CLNumber) {
                                transition.addCustomFloat(
                                        state,
                                        elementName,
                                        property,
                                        value.getFloat()
                                );
                            } else if (value instanceof CLString) {
                                long color = parseColorString(value.content());
                                if (color != -1) {
                                    transition.addCustomColor(state,
                                            elementName, property, (int) color);
                                }
                            }
                        }
                    }
                }

            }
        } catch (CLParsingException e) {
            System.err.println("Error parsing JSON " + e);
        }
    }

    /**
     * Parse and build a motionScene
     *
     * @Todo this should be in a MotionScene / MotionSceneParser
     */
    public static void parseMotionSceneJSON(CoreMotionScene scene, String content) {
        try {
            CLObject json = CLParser.parse(content);
            ArrayList<String> elements = json.names();
            if (elements == null) {
                return;
            }
            for (String elementName : elements) {
                CLElement element = json.get(elementName);
                if (element instanceof CLObject) {
                    CLObject clObject = (CLObject) element;
                    switch (elementName) {
                        case "ConstraintSets":
                            parseConstraintSets(scene, clObject);
                            break;
                        case "Transitions":
                            parseTransitions(scene, clObject);
                            break;
                        case "Header":
                            parseHeader(scene, clObject);
                            break;
                    }
                }
            }
        } catch (CLParsingException e) {
            System.err.println("Error parsing JSON " + e);
        }
    }

    /**
     * Parse ConstraintSets and populate MotionScene
     */
    static void parseConstraintSets(CoreMotionScene scene,
            CLObject json) throws CLParsingException {
        ArrayList<String> constraintSetNames = json.names();
        if (constraintSetNames == null) {
            return;
        }

        for (String csName : constraintSetNames) {
            CLObject constraintSet = json.getObject(csName);
            boolean added = false;
            String ext = constraintSet.getStringOrNull("Extends");
            if (ext != null && !ext.isEmpty()) {
                String base = scene.getConstraintSet(ext);
                if (base == null) {
                    continue;
                }

                CLObject baseJson = CLParser.parse(base);
                ArrayList<String> widgetsOverride = constraintSet.names();
                if (widgetsOverride == null) {
                    continue;
                }

                for (String widgetOverrideName : widgetsOverride) {
                    CLElement value = constraintSet.get(widgetOverrideName);
                    if (value instanceof CLObject) {
                        override(baseJson, widgetOverrideName, (CLObject) value);
                    }
                }

                scene.setConstraintSetContent(csName, baseJson.toJSON());
                added = true;
            }
            if (!added) {
                scene.setConstraintSetContent(csName, constraintSet.toJSON());
            }
        }

    }

    static void override(CLObject baseJson,
            String name, CLObject overrideValue) throws CLParsingException {
        if (!baseJson.has(name)) {
            baseJson.put(name, overrideValue);
        } else {
            CLObject base = baseJson.getObject(name);
            ArrayList<String> keys = overrideValue.names();
            for (String key : keys) {
                if (!key.equals("clear")) {
                    base.put(key, overrideValue.get(key));
                    continue;
                }
                CLArray toClear = overrideValue.getArray("clear");
                for (int i = 0; i < toClear.size(); i++) {
                    String clearedKey = toClear.getStringOrNull(i);
                    if (clearedKey == null) {
                        continue;
                    }
                    switch (clearedKey) {
                        case "dimensions":
                            base.remove("width");
                            base.remove("height");
                            break;
                        case "constraints":
                            base.remove("start");
                            base.remove("end");
                            base.remove("top");
                            base.remove("bottom");
                            base.remove("baseline");
                            base.remove("center");
                            base.remove("centerHorizontally");
                            base.remove("centerVertically");
                            break;
                        case "transforms":
                            base.remove("visibility");
                            base.remove("alpha");
                            base.remove("pivotX");
                            base.remove("pivotY");
                            base.remove("rotationX");
                            base.remove("rotationY");
                            base.remove("rotationZ");
                            base.remove("scaleX");
                            base.remove("scaleY");
                            base.remove("translationX");
                            base.remove("translationY");
                            break;
                        default:
                            base.remove(clearedKey);

                    }
                }
            }
        }
    }

    /**
     * Parse the Transition
     */
    static void parseTransitions(CoreMotionScene scene, CLObject json) throws CLParsingException {
        ArrayList<String> elements = json.names();
        if (elements == null) {
            return;
        }
        for (String elementName : elements) {
            scene.setTransitionContent(elementName, json.getObject(elementName).toJSON());
        }
    }

    /**
     * Used to parse for "export"
     */
    static void parseHeader(CoreMotionScene scene, CLObject json) {
        String name = json.getStringOrNull("export");
        if (name != null) {
            scene.setDebugName(name);
        }
    }

    /**
     * Top leve parsing of the json ConstraintSet supporting
     * "Variables", "Helpers", "Generate", guidelines, and barriers
     *
     * @param content         the JSON string
     * @param state           the state to populate
     * @param layoutVariables the variables to override
     */
    public static void parseJSON(String content, State state,
            LayoutVariables layoutVariables) throws CLParsingException {
        try {
            CLObject json = CLParser.parse(content);
            ArrayList<String> elements = json.names();
            if (elements == null) {
                return;
            }
            for (String elementName : elements) {
                CLElement element = json.get(elementName);
                if (PARSER_DEBUG) {
                    System.out.println("[" + elementName + "] = " + element
                            + " > " + element.getContainer());
                }
                switch (elementName) {
                    case "Variables":
                        if (element instanceof CLObject) {
                            parseVariables(state, layoutVariables, (CLObject) element);
                        }
                        break;
                    case "Helpers":
                        if (element instanceof CLArray) {
                            parseHelpers(state, layoutVariables, (CLArray) element);
                        }
                        break;
                    case "Generate":
                        if (element instanceof CLObject) {
                            parseGenerate(state, layoutVariables, (CLObject) element);
                        }
                        break;
                    default:
                        if (element instanceof CLObject) {
                            String type = lookForType((CLObject) element);
                            if (type != null) {
                                switch (type) {
                                    case "hGuideline":
                                        parseGuidelineParams(
                                                ConstraintWidget.HORIZONTAL,
                                                state,
                                                elementName,
                                                (CLObject) element);
                                        break;
                                    case "vGuideline":
                                        parseGuidelineParams(
                                                ConstraintWidget.VERTICAL,
                                                state,
                                                elementName,
                                                (CLObject) element
                                        );
                                        break;
                                    case "barrier":
                                        parseBarrier(state, elementName, (CLObject) element);
                                        break;
                                    case "vChain":
                                    case "hChain":
                                        parseChainType(
                                                type,
                                                state,
                                                elementName,
                                                layoutVariables,
                                                (CLObject) element
                                        );
                                        break;
                                }
                            } else {
                                parseWidget(state, layoutVariables,
                                        elementName, (CLObject) element);
                            }
                        } else if (element instanceof CLNumber) {
                            layoutVariables.put(elementName, element.getInt());
                        }
                }
            }

        } catch (CLParsingException e) {
            System.err.println("Error parsing JSON " + e);
        }
    }

    private static void parseVariables(State state,
            LayoutVariables layoutVariables,
            CLObject json) throws CLParsingException {
        ArrayList<String> elements = json.names();
        if (elements == null) {
            return;
        }
        for (String elementName : elements) {
            CLElement element = json.get(elementName);
            if (element instanceof CLNumber) {
                layoutVariables.put(elementName, element.getInt());
            } else if (element instanceof CLObject) {
                CLObject obj = (CLObject) element;
                ArrayList<String> arrayIds;
                if (obj.has("from") && obj.has("to")) {
                    float from = layoutVariables.get(obj.get("from"));
                    float to = layoutVariables.get(obj.get("to"));
                    String prefix = obj.getStringOrNull("prefix");
                    String postfix = obj.getStringOrNull("postfix");
                    layoutVariables.put(elementName, from, to, 1f, prefix, postfix);
                } else if (obj.has("from") && obj.has("step")) {
                    float start = layoutVariables.get(obj.get("from"));
                    float increment = layoutVariables.get(obj.get("step"));
                    layoutVariables.put(elementName, start, increment);

                } else if (obj.has("ids")) {
                    CLArray ids = obj.getArray("ids");
                    arrayIds = new ArrayList<>();
                    for (int i = 0; i < ids.size(); i++) {
                        arrayIds.add(ids.getString(i));
                    }
                    layoutVariables.put(elementName, arrayIds);
                } else if (obj.has("tag")) {
                    arrayIds = state.getIdsForTag(obj.getString("tag"));
                    layoutVariables.put(elementName, arrayIds);
                }
            }
        }
    }

    /**
     * parse the Design time elements.
     *
     * @param content the json
     * @param list    output the list of design elements
     */
    public static void parseDesignElementsJSON(
            String content, ArrayList<DesignElement> list) throws CLParsingException {
        CLObject json = CLParser.parse(content);
        ArrayList<String> elements = json.names();
        if (elements == null) {
            return;
        }
        for (int i = 0; i < elements.size(); i++) {
            String elementName = elements.get(i);
            CLElement element = json.get(elementName);
            if (PARSER_DEBUG) {
                System.out.println("[" + element + "] " + element.getClass());
            }
            switch (elementName) {
                case "Design":
                    if (!(element instanceof CLObject)) {
                        return;
                    }
                    CLObject obj = (CLObject) element;
                    elements = obj.names();
                    for (int j = 0; j < elements.size(); j++) {
                        String designElementName = elements.get(j);
                        CLObject designElement =
                                (CLObject) ((CLObject) element).get(designElementName);
                        System.out.printf("element found " + designElementName + "");
                        String type = designElement.getStringOrNull("type");
                        if (type != null) {
                            HashMap<String, String> parameters = new HashMap<String, String>();
                            int size = designElement.size();
                            for (int k = 0; k < size; k++) {

                                CLKey key = (CLKey) designElement.get(j);
                                String paramName = key.content();
                                String paramValue = key.getValue().content();
                                if (paramValue != null) {
                                    parameters.put(paramName, paramValue);
                                }
                            }
                            list.add(new DesignElement(elementName, type, parameters));
                        }
                    }
            }
            break;
        }

    }

    static void parseHelpers(State state,
            LayoutVariables layoutVariables,
            CLArray element) throws CLParsingException {
        for (int i = 0; i < element.size(); i++) {
            CLElement helper = element.get(i);
            if (helper instanceof CLArray) {
                CLArray array = (CLArray) helper;
                if (array.size() > 1) {
                    switch (array.getString(0)) {
                        case "hChain":
                            parseChain(ConstraintWidget.HORIZONTAL, state, layoutVariables, array);
                            break;
                        case "vChain":
                            parseChain(ConstraintWidget.VERTICAL, state, layoutVariables, array);
                            break;
                        case "hGuideline":
                            parseGuideline(ConstraintWidget.HORIZONTAL, state, array);
                            break;
                        case "vGuideline":
                            parseGuideline(ConstraintWidget.VERTICAL, state, array);
                            break;
                    }
                }
            }
        }
    }

    static void parseGenerate(State state,
            LayoutVariables layoutVariables,
            CLObject json) throws CLParsingException {
        ArrayList<String> elements = json.names();
        if (elements == null) {
            return;
        }
        for (String elementName : elements) {
            CLElement element = json.get(elementName);
            ArrayList<String> arrayIds = layoutVariables.getList(elementName);
            if (arrayIds != null && element instanceof CLObject) {
                for (String id : arrayIds) {
                    parseWidget(state, layoutVariables, id, (CLObject) element);
                }
            }
        }
    }

    static void parseChain(int orientation, State state,
            LayoutVariables margins, CLArray helper) throws CLParsingException {
        ChainReference chain = (orientation == ConstraintWidget.HORIZONTAL)
                ? state.horizontalChain() : state.verticalChain();
        CLElement refs = helper.get(1);
        if (!(refs instanceof CLArray) || ((CLArray) refs).size() < 1) {
            return;
        }
        for (int i = 0; i < ((CLArray) refs).size(); i++) {
            chain.add(((CLArray) refs).getString(i));
        }

        if (helper.size() > 2) { // we have additional parameters
            CLElement params = helper.get(2);
            if (!(params instanceof CLObject)) {
                return;
            }
            CLObject obj = (CLObject) params;
            ArrayList<String> constraints = obj.names();
            for (String constraintName : constraints) {
                switch (constraintName) {
                    case "style":
                        CLElement styleObject = ((CLObject) params).get(constraintName);
                        String styleValue;
                        if (styleObject instanceof CLArray && ((CLArray) styleObject).size() > 1) {
                            styleValue = ((CLArray) styleObject).getString(0);
                            float biasValue = ((CLArray) styleObject).getFloat(1);
                            chain.bias(biasValue);
                        } else {
                            styleValue = styleObject.content();
                        }
                        switch (styleValue) {
                            case "packed":
                                chain.style(State.Chain.PACKED);
                                break;
                            case "spread_inside":
                                chain.style(State.Chain.SPREAD_INSIDE);
                                break;
                            default:
                                chain.style(State.Chain.SPREAD);
                                break;
                        }

                        break;
                    default:
                        parseConstraint(
                                state,
                                margins,
                                (CLObject) params,
                                (ConstraintReference) chain,
                                constraintName
                        );
                        break;
                }
            }
        }
    }

    private static float toPix(State state, float dp){
       return state.getDpToPixel().toPixels(dp);
    }
    /**
     * Support parsing Chain in the following manner
     * chainId : {
     *      type:'hChain'  // or vChain
     *      contains: ['id1', 'id2', 'id3' ]
     *      contains: [['id', weight, marginL ,marginR], 'id2', 'id3' ]
     *      start: ['parent', 'start',0],
     *      end: ['parent', 'end',0],
     *      top: ['parent', 'top',0],
     *      bottom: ['parent', 'bottom',0],
     *      style: 'spread'
     * }

     * @throws CLParsingException
     */
    private static void parseChainType(String orientation,
            State state,
            String chainName,
            LayoutVariables margins,
            CLObject object) throws CLParsingException {

        ChainReference chain = (orientation.charAt(0) == 'h')
                ? state.horizontalChain() : state.verticalChain();
        chain.setKey(chainName);

        for (String params : object.names()) {
            switch (params) {
                case "contains":
                    CLElement refs = object.get(params);
                    if (!(refs instanceof CLArray) || ((CLArray) refs).size() < 1) {
                        System.err.println(
                                chainName + " contains should be an array \"" + refs.content()
                                        + "\"");
                        return;
                    }
                    for (int i = 0; i < ((CLArray) refs).size(); i++) {
                        CLElement chainElement = ((CLArray) refs).get(i);
                        if (chainElement instanceof CLArray) {
                            CLArray array = (CLArray) chainElement;
                            if (array.size() > 0) {
                                String id = array.get(0).content();
                                float weight = Float.NaN;
                                float preMargin = Float.NaN;
                                float postMargin = Float.NaN;
                                switch (array.size()) {
                                    case 2: // sets only the weight
                                        weight = array.getFloat(1);
                                        break;
                                    case 3: // sets the pre and post margin to the 2 arg
                                        weight = array.getFloat(1);
                                        postMargin = preMargin = toPix(state, array.getFloat(2));
                                        break;
                                    case 4: // sets the pre and post margin
                                        weight = array.getFloat(1);
                                        preMargin = toPix(state, array.getFloat(2));
                                        postMargin = toPix(state, array.getFloat(3));
                                        break;
                                }
                                chain.addChainElement(id, weight, preMargin, postMargin);
                            }
                        } else {
                            chain.add(chainElement.content());
                        }
                    }
                    break;
                case "start":
                case "end":
                case "top":
                case "bottom":
                case "left":
                case "right":
                    parseConstraint(state, margins, object, chain, params);
                    break;
                case "style":

                    CLElement styleObject = object.get(params);
                    String styleValue;
                    if (styleObject instanceof CLArray && ((CLArray) styleObject).size() > 1) {
                        styleValue = ((CLArray) styleObject).getString(0);
                        float biasValue = ((CLArray) styleObject).getFloat(1);
                        chain.bias(biasValue);
                    } else {
                        styleValue = styleObject.content();
                    }
                    switch (styleValue) {
                        case "packed":
                            chain.style(State.Chain.PACKED);
                            break;
                        case "spread_inside":
                            chain.style(State.Chain.SPREAD_INSIDE);
                            break;
                        default:
                            chain.style(State.Chain.SPREAD);
                            break;
                    }

                    break;
            }
        }
    }

    static void parseGuideline(int orientation,
            State state, CLArray helper) throws CLParsingException {
        CLElement params = helper.get(1);
        if (!(params instanceof CLObject)) {
            return;
        }
        String guidelineId = ((CLObject) params).getStringOrNull("id");
        if (guidelineId == null) return;
        parseGuidelineParams(orientation, state, guidelineId, (CLObject) params);
    }

    static void parseGuidelineParams(
            int orientation,
            State state,
            String guidelineId,
            CLObject params
    ) throws CLParsingException {
        ArrayList<String> constraints = params.names();
        if (constraints == null) return;
        ConstraintReference reference = state.constraints(guidelineId);

        if (orientation == ConstraintWidget.HORIZONTAL) {
            state.horizontalGuideline(guidelineId);
        } else {
            state.verticalGuideline(guidelineId);
        }

        GuidelineReference guidelineReference = (GuidelineReference) reference.getFacade();
        for (String constraintName : constraints) {
            switch (constraintName) {
                case "start":
                    int margin = state.convertDimension(params.getFloat(constraintName));
                    guidelineReference.start(state.getDpToPixel().toPixels(margin));
                    break;
                case "end":
                    margin = state.convertDimension(params.getFloat(constraintName));
                    guidelineReference.end(state.getDpToPixel().toPixels(margin));
                    break;
                case "percent":
                    guidelineReference.percent(params.getFloat(constraintName));
                    break;
            }
        }
    }

    static void parseBarrier(
            State state,
            String elementName, CLObject element
    ) throws CLParsingException {
        BarrierReference reference = state.barrier(elementName, State.Direction.END);
        ArrayList<String> constraints = element.names();
        if (constraints == null) {
            return;
        }
        for (String constraintName : constraints) {
            switch (constraintName) {
                case "direction": {
                    switch ((element.getString(constraintName))) {
                        case "start":
                            reference.setBarrierDirection(State.Direction.START);
                            break;
                        case "end":
                            reference.setBarrierDirection(State.Direction.END);
                            break;
                        case "left":
                            reference.setBarrierDirection(State.Direction.LEFT);
                            break;
                        case "right":
                            reference.setBarrierDirection(State.Direction.RIGHT);
                            break;
                        case "top":
                            reference.setBarrierDirection(State.Direction.TOP);
                            break;
                        case "bottom":
                            reference.setBarrierDirection(State.Direction.BOTTOM);
                            break;
                    }
                }
                break;
                case "margin":
                    float margin = element.getFloatOrNaN(constraintName);
                    if (!Float.isNaN(margin)) {
                        reference.margin(margin); // TODO is this a bug
                    }
                    break;
                case "contains":
                    CLArray list = element.getArrayOrNull(constraintName);
                    if (list != null) {
                        for (int j = 0; j < list.size(); j++) {

                            String elementNameReference = list.get(j).content();
                            ConstraintReference elementReference =
                                    state.constraints(elementNameReference);
                            if (PARSER_DEBUG) {
                                System.out.println(
                                        "Add REFERENCE "
                                                + "($elementNameReference = $elementReference) "
                                                + "TO BARRIER "
                                );
                            }
                            reference.add(elementReference);
                        }
                    }
                    break;
            }
        }
    }

    static void parseWidget(
            State state,
            LayoutVariables layoutVariables,
            String elementName,
            CLObject element
    ) throws CLParsingException {
        float value;
        ConstraintReference reference = state.constraints(elementName);
        if (reference.getWidth() == null) {
            // Default to Wrap when the Dimension has not been assigned
            reference.setWidth(Dimension.createWrap());
        }
        if (reference.getHeight() == null) {
            // Default to Wrap when the Dimension has not been assigned
            reference.setHeight(Dimension.createWrap());
        }
        ArrayList<String> constraints = element.names();
        if (constraints == null) {
            return;
        }
        for (String constraintName : constraints) {
            switch (constraintName) {
                case "width":
                    reference.setWidth(parseDimension(element,
                            constraintName, state, state.getDpToPixel()));
                    break;
                case "height":
                    reference.setHeight(parseDimension(element,
                            constraintName, state, state.getDpToPixel()));
                    break;
                case "center":
                    String target = element.getString(constraintName);

                    ConstraintReference targetReference;
                    if (target.equals("parent")) {
                        targetReference = state.constraints(State.PARENT);
                    } else {
                        targetReference = state.constraints(target);
                    }
                    reference.startToStart(targetReference);
                    reference.endToEnd(targetReference);
                    reference.topToTop(targetReference);
                    reference.bottomToBottom(targetReference);
                    break;
                case "centerHorizontally":
                    target = element.getString(constraintName);
                    targetReference = target.equals("parent")
                            ? state.constraints(State.PARENT) : state.constraints(target);

                    reference.startToStart(targetReference);
                    reference.endToEnd(targetReference);
                    break;
                case "centerVertically":
                    target = element.getString(constraintName);
                    targetReference = target.equals("parent")
                            ? state.constraints(State.PARENT) : state.constraints(target);

                    reference.topToTop(targetReference);
                    reference.bottomToBottom(targetReference);
                    break;
                case "alpha":
                    value = layoutVariables.get(element.get(constraintName));
                    reference.alpha(value);
                    break;
                case "scaleX":
                    value = layoutVariables.get(element.get(constraintName));
                    reference.scaleX(value);
                    break;
                case "scaleY":
                    value = layoutVariables.get(element.get(constraintName));
                    reference.scaleY(value);
                    break;
                case "translationX":
                    value = layoutVariables.get(element.get(constraintName));
                    reference.translationX(value);
                    break;
                case "translationY":
                    value = layoutVariables.get(element.get(constraintName));
                    reference.translationY(value);
                    break;
                case "translationZ":
                    value = layoutVariables.get(element.get(constraintName));
                    reference.translationZ(value);
                    break;
                case "pivotX":
                    value = layoutVariables.get(element.get(constraintName));
                    reference.pivotX(value);
                    break;
                case "pivotY":
                    value = layoutVariables.get(element.get(constraintName));
                    reference.pivotY(value);
                    break;
                case "rotationX":
                    value = layoutVariables.get(element.get(constraintName));
                    reference.rotationX(value);
                    break;
                case "rotationY":
                    value = layoutVariables.get(element.get(constraintName));
                    reference.rotationY(value);
                    break;
                case "rotationZ":
                    value = layoutVariables.get(element.get(constraintName));
                    reference.rotationZ(value);
                    break;
                case "visibility":
                    switch (element.getString(constraintName)) {
                        case "visible":
                            reference.visibility(ConstraintWidget.VISIBLE);
                            break;
                        case "invisible":
                            reference.visibility(ConstraintWidget.INVISIBLE);
                            break;
                        case "gone":
                            reference.visibility(ConstraintWidget.GONE);
                            break;
                    }
                    break;
                case "vBias":
                    value = layoutVariables.get(element.get(constraintName));
                    reference.verticalBias(value);
                    break;
                case "hBias":
                    value = layoutVariables.get(element.get(constraintName));
                    reference.horizontalBias(value);
                    break;
                case "vWeight":
                    value = layoutVariables.get(element.get(constraintName));
                    reference.setVerticalChainWeight(value);
                    break;
                case "hWeight":
                    value = layoutVariables.get(element.get(constraintName));
                    reference.setHorizontalChainWeight(value);
                    break;
                case "custom":
                    parseCustomProperties(element, reference, constraintName);
                    break;
                case "motion":
                    parseMotionProperties(element.get(constraintName), reference);
                    break;
                default:
                    parseConstraint(state, layoutVariables, element, reference, constraintName);

            }
        }
    }

    static void parseCustomProperties(
            CLObject element,
            ConstraintReference reference,
            String constraintName
    ) throws CLParsingException {
        CLObject json = element.getObjectOrNull(constraintName);
        if (json == null) {
            return;
        }
        ArrayList<String> properties = json.names();
        if (properties == null) {
            return;
        }
        for (String property : properties) {
            CLElement value = json.get(property);
            if (value instanceof CLNumber) {
                reference.addCustomFloat(property, value.getFloat());
            } else if (value instanceof CLString) {
                long it = parseColorString(value.content());
                if (it != -1) {
                    reference.addCustomColor(property, (int) it);
                }
            }
        }
    }

    private static int indexOf(String val, String... types) {
        for (int i = 0; i < types.length; i++) {
            if (types[i].equals(val)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * parse the motion section of a constraint
     * <pre>
     * csetName: {
     *   idToConstrain : {
     *       motion: {
     *          pathArc : 'startVertical'
     *          relativeTo: 'id'
     *          easing: 'curve'
     *          stagger: '2'
     *          quantize: steps or [steps, 'interpolator' phase ]
     *       }
     *   }
     * }
     * </pre>
     */
    private static void parseMotionProperties(
            CLElement element,
            ConstraintReference reference
    ) throws CLParsingException {
        if (!(element instanceof CLObject)) {
            return;
        }
        CLObject obj = (CLObject) element;
        TypedBundle bundle = new TypedBundle();
        ArrayList<String> constraints = obj.names();
        if (constraints == null) {
            return;
        }
        for (String constraintName : constraints) {

            switch (constraintName) {
                case "pathArc":
                    String val = obj.getString(constraintName);
                    int ord = indexOf(val, "none", "startVertical", "startHorizontal", "flip");
                    if (ord == -1) {
                        System.err.println(obj.getLine() + " pathArc = '" + val + "'");
                        break;
                    }
                    bundle.add(TypedValues.MotionType.TYPE_PATHMOTION_ARC, ord);
                    break;
                case "relativeTo":
                    bundle.add(TypedValues.MotionType.TYPE_ANIMATE_RELATIVE_TO,
                            obj.getString(constraintName));
                    break;
                case "easing":
                    bundle.add(TypedValues.MotionType.TYPE_EASING, obj.getString(constraintName));
                    break;
                case "stagger":
                    bundle.add(TypedValues.MotionType.TYPE_STAGGER, obj.getFloat(constraintName));
                    break;
                case "quantize":
                    CLElement quant = obj.get(constraintName);
                    if (quant instanceof CLArray) {
                        CLArray array = (CLArray) quant;
                        int len = array.size();
                        if (len > 0) {
                            bundle.add(TYPE_QUANTIZE_MOTIONSTEPS, array.getInt(0));
                            if (len > 1) {
                                bundle.add(TYPE_QUANTIZE_INTERPOLATOR_TYPE, array.getString(1));
                                if (len > 2) {
                                    bundle.add(TYPE_QUANTIZE_MOTION_PHASE, array.getFloat(2));
                                }
                            }
                        }
                    } else {
                        bundle.add(TYPE_QUANTIZE_MOTIONSTEPS, obj.getInt(constraintName));
                    }
                    break;
            }
        }
        reference.mMotionProperties = bundle;
    }

    static void parseConstraint(
            State state,
            LayoutVariables layoutVariables,
            CLObject element,
            ConstraintReference reference,
            String constraintName
    ) throws CLParsingException {
        CLArray constraint = element.getArrayOrNull(constraintName);
        if (constraint != null && constraint.size() > 1) {
            String target = constraint.getString(0);
            String anchor = constraint.getStringOrNull(1);
            float margin = 0f;
            float marginGone = 0f;
            if (constraint.size() > 2) {
                CLElement arg2 = constraint.getOrNull(2);
                margin = layoutVariables.get(arg2);
                margin = state.convertDimension(state.getDpToPixel().toPixels(margin));
            }
            if (constraint.size() > 3) {
                CLElement arg2 = constraint.getOrNull(3);
                marginGone = layoutVariables.get(arg2);
                marginGone = state.convertDimension(state.getDpToPixel().toPixels(margin));
            }

            ConstraintReference targetReference = target.equals("parent")
                    ? state.constraints(State.PARENT) :
                    state.constraints(target);

            switch (constraintName) {
                case "circular":
                    float angle = layoutVariables.get(constraint.get(1));
                    reference.circularConstraint(targetReference, angle, 0f);
                    break;
                case "start":
                    switch (anchor) {
                        case "start":
                            reference.startToStart(targetReference);
                            break;
                        case "end":
                            reference.startToEnd(targetReference);
                    }
                    break;
                case "end":
                    switch (anchor) {
                        case "start":
                            reference.endToStart(targetReference);
                            break;
                        case "end":
                            reference.endToEnd(targetReference);
                    }
                    break;
                case "left":

                    switch (anchor) {
                        case "left":
                            reference.leftToLeft(targetReference);
                            break;
                        case "right":
                            reference.leftToRight(targetReference);
                    }
                    break;
                case "right":
                    switch (anchor) {
                        case "left":
                            reference.rightToLeft(targetReference);
                            break;
                        case "right":
                            reference.rightToRight(targetReference);
                    }
                    break;
                case "top":
                    switch (anchor) {
                        case "top":
                            reference.topToTop(targetReference);
                            break;
                        case "bottom":
                            reference.topToBottom(targetReference);
                    }
                    break;
                case "bottom":
                    switch (anchor) {
                        case "top":
                            reference.bottomToTop(targetReference);
                            break;
                        case "bottom":
                            reference.bottomToBottom(targetReference);
                    }
                    break;
                case "baseline":
                    switch (anchor) {
                        case "baseline":
                            state.baselineNeededFor(reference.getKey());
                            state.baselineNeededFor(targetReference.getKey());
                            reference.baselineToBaseline(targetReference);

                            break;
                        case "top":
                            state.baselineNeededFor(reference.getKey());
                            state.baselineNeededFor(targetReference.getKey());
                            reference.baselineToTop(targetReference);

                            break;
                        case "bottom":
                            state.baselineNeededFor(reference.getKey());
                            state.baselineNeededFor(targetReference.getKey());
                            reference.baselineToBottom(targetReference);

                            break;
                    }
            }

            reference.margin(margin).marginGone(marginGone);
        } else {
            String target = element.getStringOrNull(constraintName);
            if (target != null) {
                ConstraintReference targetReference = target.equals("parent")
                        ? state.constraints(State.PARENT) :
                        state.constraints(target);

                switch (constraintName) {
                    case "start":
                        reference.startToStart(targetReference);
                        break;
                    case "end":
                        reference.endToEnd(targetReference);
                        break;
                    case "top":
                        reference.topToTop(targetReference);
                        break;
                    case "bottom":
                        reference.bottomToBottom(targetReference);
                        break;
                    case "baseline":
                        state.baselineNeededFor(reference.getKey());
                        state.baselineNeededFor(targetReference.getKey());
                        reference.baselineToBaseline(targetReference);
                        break;

                }
            }
        }
    }

    static Dimension parseDimensionMode(String dimensionString) {
        Dimension dimension = Dimension.createFixed(0);
        switch (dimensionString) {
            case "wrap":
                dimension = Dimension.createWrap();
                break;
            case "preferWrap":
                dimension = Dimension.createSuggested(Dimension.WRAP_DIMENSION);
                break;
            case "spread":
                dimension = Dimension.createSuggested(Dimension.SPREAD_DIMENSION);
                break;
            case "parent":
                dimension = Dimension.createParent();
                break;
            default: {
                if (dimensionString.endsWith("%")) {
                    // parent percent
                    String percentString =
                            dimensionString.substring(0, dimensionString.indexOf('%'));
                    float percentValue = Float.parseFloat(percentString) / 100f;
                    dimension = Dimension.createPercent(0, percentValue).suggested(0);
                } else if (dimensionString.contains(":")) {
                    dimension = Dimension.createRatio(dimensionString)
                            .suggested(Dimension.SPREAD_DIMENSION);
                }
            }
        }
        return dimension;
    }

    static Dimension parseDimension(CLObject element,
            String constraintName,
            State state,
            CorePixelDp dpToPixels) throws CLParsingException {
        CLElement dimensionElement = element.get(constraintName);
        Dimension dimension = Dimension.createFixed(0);
        if (dimensionElement instanceof CLString) {
            dimension = parseDimensionMode(dimensionElement.content());
        } else if (dimensionElement instanceof CLNumber) {
            dimension = Dimension.createFixed(
                    state.convertDimension(dpToPixels.toPixels(element.getFloat(constraintName))));

        } else if (dimensionElement instanceof CLObject) {
            CLObject obj = (CLObject) dimensionElement;
            String mode = obj.getStringOrNull("value");
            if (mode != null) {
                dimension = parseDimensionMode(mode);
            }

            CLElement minEl = obj.getOrNull("min");
            if (minEl != null) {
                if (minEl instanceof CLNumber) {
                    float min = ((CLNumber) minEl).getFloat();
                    dimension.min(state.convertDimension(dpToPixels.toPixels(min)));
                } else if (minEl instanceof CLString) {
                    dimension.min(Dimension.WRAP_DIMENSION);
                }
            }
            CLElement maxEl = obj.getOrNull("max");
            if (maxEl != null) {
                if (maxEl instanceof CLNumber) {
                    float max = ((CLNumber) maxEl).getFloat();
                    dimension.max(state.convertDimension(dpToPixels.toPixels(max)));
                } else if (maxEl instanceof CLString) {
                    dimension.max(Dimension.WRAP_DIMENSION);
                }
            }
        }
        return dimension;
    }

    /**
     * parse a color string
     *
     * @return -1 if it cannot parse unsigned long
     */
    static long parseColorString(String value) {
        String str = value;
        if (str.startsWith("#")) {
            str = str.substring(1);
            if (str.length() == 6) {
                str = "FF" + str;
            }
            return Long.parseLong(str, 16);
        } else {
            return -1L;
        }
    }

    static String lookForType(CLObject element) throws CLParsingException {
        ArrayList<String> constraints = element.names();
        for (String constraintName : constraints) {
            if (constraintName.equals("type")) {
                return element.getString("type");
            }
        }

        return null;
    }
}
