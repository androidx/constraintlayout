/*
 * Copyright 2021 The Android Open Source Project
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
package androidx.constraintlayout.compose

import androidx.compose.ui.unit.Dp
import androidx.constraintlayout.core.motion.utils.TypedBundle
import androidx.constraintlayout.core.motion.utils.TypedValues
import androidx.constraintlayout.core.parser.CLArray
import androidx.constraintlayout.core.parser.CLElement
import androidx.constraintlayout.core.parser.CLKey
import androidx.constraintlayout.core.parser.CLNumber
import androidx.constraintlayout.core.parser.CLObject
import androidx.constraintlayout.core.parser.CLParser
import androidx.constraintlayout.core.parser.CLParsingException
import androidx.constraintlayout.core.parser.CLString
import androidx.constraintlayout.core.state.ConstraintReference
import androidx.constraintlayout.core.state.Dimension
import androidx.constraintlayout.core.state.Dimension.*
import androidx.constraintlayout.core.state.State.Chain.*
import androidx.constraintlayout.core.state.Transition
import androidx.constraintlayout.core.state.helpers.GuidelineReference
import androidx.constraintlayout.core.widgets.ConstraintWidget
import java.lang.Long.parseLong

internal const val PARSER_DEBUG = false

internal class LayoutVariables {
    private val margins = HashMap<String, Int>()
    private val generators = HashMap<String, GeneratedValue>()
    private val arrayIds = HashMap<String, ArrayList<String>>()

    fun put(elementName: String, element: Int) {
        margins[elementName] = element
    }

    fun put(elementName: String, start: Float, incrementBy: Float) {
        if (generators.containsKey(elementName)) {
            if (generators[elementName] is OverrideValue) {
                return
            }
        }
        val generator = Generator(start, incrementBy)
        generators[elementName] = generator
    }

    fun put(
        elementName: String,
        from: Float,
        to: Float,
        step: Float,
        prefix: String,
        postfix: String
    ) {
        if (generators.containsKey(elementName)) {
            if (generators[elementName] is OverrideValue) {
                return
            }
        }
        val generator = FiniteGenerator(from, to, step, prefix, postfix)
        generators[elementName] = generator
        arrayIds[elementName] = generator.array()
    }

    fun putOverride(elementName: String, value: Float) {
        val generator = OverrideValue(value)
        generators[elementName] = generator
    }

    fun get(elementName: Any): Float {
        if (elementName is CLString) {
            val stringValue = elementName.content()
            if (generators.containsKey(stringValue)) {
                return generators[stringValue]!!.value()
            }
            if (margins.containsKey(stringValue)) {
                return margins[stringValue]!!.toFloat()
            }
        } else if (elementName is CLNumber) {
            return elementName.float
        }
        return 0f
    }

    fun getList(elementName: String): ArrayList<String>? {
        if (arrayIds.containsKey(elementName)) {
            return arrayIds[elementName]
        }
        return null
    }

    fun put(elementName: String, elements: ArrayList<String>) {
        arrayIds[elementName] = elements
    }

}

internal interface GeneratedValue {
    fun value(): Float
}

internal class Generator(start: Float, private var incrementBy: Float) : GeneratedValue {
    private var current: Float = start
    private var stop = false

    override fun value(): Float {
        if (!stop) {
            current += incrementBy
        }
        return current
    }
}

internal class FiniteGenerator(
    from: Float, to: Float,
    private var step: Float = 1f, private var prefix: String = "",
    private var postfix: String = ""
) : GeneratedValue {
    private var current: Float = from
    private var stop = false
    private var initial = from
    private var max = to

    override fun value(): Float {
        if (current >= max) {
            stop = true
        }
        if (!stop) {
            current += step
        }
        return current
    }

    fun array(): ArrayList<String> {
        val array = arrayListOf<String>()
        var value = initial.toInt()
        for (i in initial.toInt()..max.toInt()) {
            array.add(prefix + value + postfix)
            value += step.toInt()
        }
        return array
    }

}

internal class OverrideValue(private var value: Float) : GeneratedValue {
    override fun value(): Float {
        return value
    }
}

internal fun parseTransition(json: CLObject, transition: Transition) {
    val pathMotionArc = json.getStringOrNull("pathMotionArc")
    if (pathMotionArc != null) {
        val bundle = TypedBundle()
        when (pathMotionArc) {
            "none" -> bundle.add(TypedValues.Position.TYPE_PATH_MOTION_ARC, 0)
            "startVertical" -> bundle.add(TypedValues.Position.TYPE_PATH_MOTION_ARC, 1)
            "startHorizontal" -> bundle.add(TypedValues.Position.TYPE_PATH_MOTION_ARC, 2)
            "flip" -> bundle.add(TypedValues.Position.TYPE_PATH_MOTION_ARC, 3)
        }
        transition.setTransitionProperties(bundle)
    }
    val keyframes = json.getObjectOrNull("KeyFrames") ?: return
    val keyPositions = keyframes.getArrayOrNull("KeyPositions")
    if (keyPositions != null) {
        (0 until keyPositions.size()).forEach { i ->
            val keyPosition = keyPositions[i]
            if (keyPosition is CLObject) {
                parseKeyPosition(keyPosition, transition)
            }
        }
    }
    val keyAttributes = keyframes.getArrayOrNull("KeyAttributes")
    if (keyAttributes != null) {
        (0 until keyAttributes.size()).forEach { i ->
            val keyAttribute = keyAttributes[i]
            if (keyAttribute is CLObject) {
                parseKeyAttribute(keyAttribute, transition)
            }
        }
    }
    val keyCycles = keyframes.getArrayOrNull("KeyCycles")
    if (keyCycles != null) {
        (0 until keyCycles.size()).forEach { i ->
            val keyCycle = keyCycles[i]
            if (keyCycle is CLObject) {
                parseKeyCycle(keyCycle, transition)
            }
        }
    }
}

internal fun parseKeyPosition(keyPosition: CLObject, transition: Transition) {
    val bundle = TypedBundle()
    val targets = keyPosition.getArray("target")
    val frames = keyPosition.getArray("frames")
    val percentX = keyPosition.getArrayOrNull("percentX")
    val percentY = keyPosition.getArrayOrNull("percentY")
    val percentWidth = keyPosition.getArrayOrNull("percentWidth")
    val percentHeight = keyPosition.getArrayOrNull("percentHeight")
    val pathMotionArc = keyPosition.getStringOrNull("pathMotionArc")
    val transitionEasing = keyPosition.getStringOrNull("transitionEasing")
    val curveFit = keyPosition.getStringOrNull("curveFit")
    val type = keyPosition.getStringOrNull("type") ?: "parentRelative"
    if (percentX != null && frames.size() != percentX.size()) {
        return
    }
    if (percentY != null && frames.size() != percentY.size()) {
        return
    }
    (0 until targets.size()).forEach { i ->
        val target = targets.getString(i)
        bundle.clear()
        bundle.add(
            TypedValues.Position.TYPE_POSITION_TYPE, when (type) {
                "deltaRelative" -> 0
                "pathRelative" -> 1
                "parentRelative" -> 2
                else -> 0
            }
        )
        if (curveFit != null) {
            when (curveFit) {
                "spline" -> bundle.add(TypedValues.Position.TYPE_CURVE_FIT, 0)
                "linear" -> bundle.add(TypedValues.Position.TYPE_CURVE_FIT, 1)
            }
        }
        bundle.addIfNotNull(TypedValues.Position.TYPE_TRANSITION_EASING, transitionEasing)

        if (pathMotionArc != null) {
            when (pathMotionArc) {
                "none" -> bundle.add(TypedValues.Position.TYPE_PATH_MOTION_ARC, 0)
                "startVertical" -> bundle.add(TypedValues.Position.TYPE_PATH_MOTION_ARC, 1)
                "startHorizontal" -> bundle.add(TypedValues.Position.TYPE_PATH_MOTION_ARC, 2)
                "flip" -> bundle.add(TypedValues.Position.TYPE_PATH_MOTION_ARC, 3)
            }
        }

        (0 until frames.size()).forEach { j ->
            val frame = frames.getInt(j)
            bundle.add(TypedValues.TYPE_FRAME_POSITION, frame)
            if (percentX != null) {
                bundle.add(TypedValues.Position.TYPE_PERCENT_X, percentX.getFloat(j))
            }
            if (percentY != null) {
                bundle.add(TypedValues.Position.TYPE_PERCENT_Y, percentY.getFloat(j))
            }
            if (percentWidth != null) {
                bundle.add(TypedValues.Position.TYPE_PERCENT_WIDTH, percentWidth.getFloat(j))
            }
            if (percentHeight != null) {
                bundle.add(TypedValues.Position.TYPE_PERCENT_HEIGHT, percentHeight.getFloat(j))
            }

            transition.addKeyPosition(target, bundle)
        }
    }
}

internal fun parseKeyAttribute(keyAttribute: CLObject, transition: Transition) {
    val targets = keyAttribute.getArrayOrNull("target") ?: return
    val frames = keyAttribute.getArrayOrNull("frames") ?: return
    val transitionEasing = keyAttribute.getStringOrNull("transitionEasing")

    val attrNames = arrayListOf(
        TypedValues.Attributes.S_SCALE_X,
        TypedValues.Attributes.S_SCALE_Y,
        TypedValues.Attributes.S_TRANSLATION_X,
        TypedValues.Attributes.S_TRANSLATION_Y,
        TypedValues.Attributes.S_TRANSLATION_Z,
        TypedValues.Attributes.S_ROTATION_X,
        TypedValues.Attributes.S_ROTATION_Y,
        TypedValues.Attributes.S_ROTATION_Z,
        TypedValues.Attributes.S_ALPHA,
    )
    val attrIds = arrayListOf(
        TypedValues.Attributes.TYPE_SCALE_X,
        TypedValues.Attributes.TYPE_SCALE_Y,
        TypedValues.Attributes.TYPE_TRANSLATION_X,
        TypedValues.Attributes.TYPE_TRANSLATION_Y,
        TypedValues.Attributes.TYPE_TRANSLATION_Z,
        TypedValues.Attributes.TYPE_ROTATION_X,
        TypedValues.Attributes.TYPE_ROTATION_Y,
        TypedValues.Attributes.TYPE_ROTATION_Z,
        TypedValues.Attributes.TYPE_ALPHA,
    )

    val bundles = ArrayList<TypedBundle>()
    (0 until frames.size()).forEach { _ ->
        bundles.add(TypedBundle())
    }

    for (k in 0 until attrNames.size) {
        val attrName = attrNames[k]
        val attrId = attrIds[k]

        val arrayValues = keyAttribute.getArrayOrNull(attrName)
        // array must contain one per frame
        if (arrayValues != null && arrayValues.size() != bundles.size) {
            throw CLParsingException(
                "incorrect size for $attrName array, " +
                        "not matching targets array!", keyAttribute
            )
        }
        if (arrayValues != null) {
            (0 until bundles.size).forEach { i ->
                bundles[i].add(attrId, arrayValues.getFloat(i))
            }
        } else {
            val value = keyAttribute.getFloatOrNaN(attrName)
            if (!value.isNaN()) {
                (0 until bundles.size).forEach { i ->
                    bundles[i].add(attrId, value)
                }
            }
        }
    }
    val curveFit = keyAttribute.getStringOrNull("curveFit")
    (0 until targets.size()).forEach { i ->
        (0 until bundles.size).forEach { j ->
            val target = targets.getString(i)

            val bundle = bundles[j]

            if (curveFit != null) {
                when (curveFit) {
                    "spline" -> bundle.add(TypedValues.Position.TYPE_CURVE_FIT, 0)
                    "linear" -> bundle.add(TypedValues.Position.TYPE_CURVE_FIT, 1)
                }
            }
            bundle.addIfNotNull(TypedValues.Position.TYPE_TRANSITION_EASING, transitionEasing)


            val frame = frames.getInt(j)
            bundle.add(TypedValues.TYPE_FRAME_POSITION, frame)
            transition.addKeyAttribute(target, bundle)
        }
    }
}

internal fun parseKeyCycle(keyCycleData: CLObject, transition: Transition) {
    val targets = keyCycleData.getArray("target")
    val frames = keyCycleData.getArray("frames")
    val transitionEasing = keyCycleData.getStringOrNull("transitionEasing")

    val attrNames = arrayListOf<String>(
        TypedValues.Cycle.S_SCALE_X,
        TypedValues.Cycle.S_SCALE_Y,
        TypedValues.Cycle.S_TRANSLATION_X,
        TypedValues.Cycle.S_TRANSLATION_Y,
        TypedValues.Cycle.S_TRANSLATION_Z,
        TypedValues.Cycle.S_ROTATION_X,
        TypedValues.Cycle.S_ROTATION_Y,
        TypedValues.Cycle.S_ROTATION_Z,
        TypedValues.Cycle.S_ALPHA,
        TypedValues.Cycle.S_WAVE_PERIOD,
        TypedValues.Cycle.S_WAVE_OFFSET,
        TypedValues.Cycle.S_WAVE_PHASE,
    )
    val attrIds = arrayListOf<Int>(
        TypedValues.Cycle.TYPE_SCALE_X,
        TypedValues.Cycle.TYPE_SCALE_Y,
        TypedValues.Cycle.TYPE_TRANSLATION_X,
        TypedValues.Cycle.TYPE_TRANSLATION_Y,
        TypedValues.Cycle.TYPE_TRANSLATION_Z,
        TypedValues.Cycle.TYPE_ROTATION_X,
        TypedValues.Cycle.TYPE_ROTATION_Y,
        TypedValues.Cycle.TYPE_ROTATION_Z,
        TypedValues.Cycle.TYPE_ALPHA,
        TypedValues.Cycle.TYPE_WAVE_PERIOD,
        TypedValues.Cycle.TYPE_WAVE_OFFSET,
        TypedValues.Cycle.TYPE_WAVE_PHASE,
    )

// TODO S_WAVE_SHAPE S_CUSTOM_WAVE_SHAPE
    var bundles = ArrayList<TypedBundle>()
    (0 until frames.size()).forEach { i ->
        bundles.add(TypedBundle())
    }

    for (k in 0..attrNames.size - 1) {
        var attrName = attrNames[k]
        var attrId = attrIds[k];

        val arrayValues = keyCycleData.getArrayOrNull(attrName)
        // array must contain one per frame
        if (arrayValues != null && arrayValues.size() != bundles.size) {
            throw CLParsingException(
                "incorrect size for $attrName array, " +
                        "not matching targets array!", keyCycleData
            )
        }
        if (arrayValues != null) {
            (0 until bundles.size).forEach { i ->
                bundles.get(i).add(attrId, arrayValues.getFloat(i));
            }
        } else {
            val value = keyCycleData.getFloatOrNaN(attrName)
            if (!value.isNaN()) {
                (0 until bundles.size).forEach { i ->
                    bundles.get(i).add(attrId, value);
                }
            }
        }
    }
    val curveFit = keyCycleData.getStringOrNull(TypedValues.Cycle.S_CURVE_FIT)
    val easing = keyCycleData.getStringOrNull(TypedValues.Cycle.S_EASING)
    val waveShape = keyCycleData.getStringOrNull(TypedValues.Cycle.S_WAVE_SHAPE)
    val customWave = keyCycleData.getStringOrNull(TypedValues.Cycle.S_CUSTOM_WAVE_SHAPE)
    (0 until targets.size()).forEach { i ->
        (0 until bundles.size).forEach { j ->
            val target = targets.getString(i)
            var bundle = bundles.get(j)


            if (curveFit != null) {
                when (curveFit) {
                    "spline" -> bundle.add(TypedValues.Cycle.TYPE_CURVE_FIT, 0)
                    "linear" -> bundle.add(TypedValues.Cycle.TYPE_CURVE_FIT, 1)
                }
            }
            bundle.addIfNotNull(TypedValues.Position.TYPE_TRANSITION_EASING, transitionEasing)
            if (easing != null) {
                bundle.add(TypedValues.Cycle.TYPE_EASING, easing)
            }
            if (waveShape != null) {
                bundle.add(TypedValues.Cycle.TYPE_WAVE_SHAPE, waveShape)
            }
            if (customWave != null) {
                bundle.add(TypedValues.Cycle.TYPE_CUSTOM_WAVE_SHAPE, customWave)
            }

            val frame = frames.getInt(j)
            bundle.add(TypedValues.TYPE_FRAME_POSITION, frame);
            transition.addKeyCycle(target, bundle)

        }
    }
}


internal fun parseJSON(content: String, transition: Transition, state: Int) {
    try {
        val json = CLParser.parse(content)
        val elements = json.names() ?: return
        (0 until elements.size).forEach { i ->
            val elementName = elements[i]
            val element = json[elementName]
            if (element is CLObject) {
                val customProperties = element.getObjectOrNull("custom")
                if (customProperties != null) {
                    val properties = customProperties.names() ?: return
                    (0 until properties.size).forEach { j ->
                        val property = properties[j]
                        val value = customProperties.get(property)
                        if (value is CLNumber) {
                            transition.addCustomFloat(
                                state,
                                elementName,
                                property,
                                value.getFloat()
                            )
                        } else if (value is CLString) {
                            parseColorString(value.content())?.let { color ->
                                transition.addCustomColor(state, elementName, property, color)
                            }
                        }
                    }
                }
            }
        }
    } catch (e: CLParsingException) {
        System.err.println("Error parsing JSON $e")
    }
}

internal fun parseMotionSceneJSON(scene: MotionScene, content: String) {
    try {
        val json = CLParser.parse(content)
        val elements = json.names() ?: return
        (0 until elements.size).forEach { i ->
            val elementName = elements[i]
            val element = json[elementName]
            when (elementName) {
                "ConstraintSets" -> parseConstraintSets(scene, element)
                "Transitions" -> parseTransitions(scene, element)
                "Header" -> parseHeader(scene, element)
            }
        }
    } catch (e: CLParsingException) {
        System.err.println("Error parsing JSON $e")
    }
}

/**
 * For the given [json] parses all ConstraintSets into the [scene].
 */
internal fun parseConstraintSets(scene: MotionScene, json: Any) {
    if (json !is CLObject) {
        return
    }
    val constraintSetNames = json.names() ?: return
    (0 until constraintSetNames.size).forEach { i ->
        val csName = constraintSetNames[i]
        val constraintSet = json.getObject(csName)
        var added = false
        val extends = constraintSet.getStringOrNull("Extends")
        if (extends != null && extends.isNotEmpty()) {
            val base = scene.getConstraintSet(extends)
            if (base != null) {
                val baseJson = CLParser.parse(base)
                val widgetsOverride = constraintSet.names()
                if (widgetsOverride != null) {
                    (0 until widgetsOverride.size).forEach { j ->
                        val widgetOverrideName = widgetsOverride[j]
                        val value = constraintSet[widgetOverrideName]
                        if (value is CLObject) {
                            override(baseJson, widgetOverrideName, value)
                        }
                    }
                    scene.setConstraintSetContent(csName, baseJson.toJSON())
                    added = true
                }
            }
        }
        if (!added) {
            scene.setConstraintSetContent(csName, constraintSet.toJSON())
        }
    }
}

internal fun override(baseJson: CLObject, name: String, overrideValue: CLObject) {
    if (!baseJson.has(name)) {
        baseJson.put(name, overrideValue)
    } else {
        val base = baseJson.getObject(name)
        val keys = overrideValue.names()
        for (key in keys) {
            if (key.equals("clear")) {
                val toClear = overrideValue.getArray("clear")
                (0 until toClear.size()).forEach { i ->
                    val clearedKey = toClear.getStringOrNull(i)
                    if (clearedKey is String) {
                        when (clearedKey) {
                            "dimensions" -> {
                                base.remove("width")
                                base.remove("height")
                            }
                            "constraints" -> {
                                base.remove("start")
                                base.remove("end")
                                base.remove("top")
                                base.remove("bottom")
                                base.remove("baseline")
                                base.remove("center")
                                base.remove("centerHorizontally")
                                base.remove("centerVertically")
                            }
                            "transforms" -> {
                                base.remove("visibility")
                                base.remove("alpha")
                                base.remove("pivotX")
                                base.remove("pivotY")
                                base.remove("rotationX")
                                base.remove("rotationY")
                                base.remove("rotationZ")
                                base.remove("scaleX")
                                base.remove("scaleY")
                                base.remove("translationX")
                                base.remove("translationY")
                            }
                            else -> base.remove(clearedKey)
                        }
                    }
                }
            } else {
                base.put(key, overrideValue.get(key))
            }
        }
    }
}

internal fun parseTransitions(scene: MotionScene, json: Any) {
    if (json !is CLObject) {
        return
    }
    val elements = json.names() ?: return
    (0 until elements.size).forEach { i ->
        val elementName = elements[i]
        val element = json.getObject(elementName)
        scene.setTransitionContent(elementName, element.toJSON())
    }
}

internal fun parseHeader(scene: MotionScene, json: Any) {
    if (json !is CLObject) {
        return
    }
    val name = json.getStringOrNull("export")
    if (name != null) {
        scene.setDebugName(name)
    }
}

internal fun parseJSON(content: String, state: State, layoutVariables: LayoutVariables) {
    try {
        val json = CLParser.parse(content)
        val elements = json.names() ?: return
        (0 until elements.size).forEach { i ->
            val elementName = elements[i]
            val element = json[elementName]
            if (PARSER_DEBUG) {
                println("element <$elementName = $element> " + element.javaClass)
            }
            when (elementName) {
                "Variables" -> parseVariables(state, layoutVariables, element)
                "Helpers" -> parseHelpers(state, layoutVariables, element)
                "Generate" -> parseGenerate(state, layoutVariables, element)
                else -> {
                    if (element is CLObject) {
                        val type = lookForType(element)
                        if (type != null) {
                            when (type) {
                                "hGuideline" -> parseGuidelineParams(
                                    ConstraintWidget.HORIZONTAL,
                                    state,
                                    elementName,
                                    element
                                )
                                "vGuideline" -> parseGuidelineParams(
                                    ConstraintWidget.VERTICAL,
                                    state,
                                    elementName,
                                    element
                                )
                                "barrier" -> parseBarrier(state, elementName, element)
                            }
                        } else {
                            parseWidget(state, layoutVariables, elementName, element)
                        }
                    } else if (element is CLNumber) {
                        layoutVariables.put(elementName, element.int)
                    }
                }
            }
        }
    } catch (e: CLParsingException) {
        System.err.println("Error parsing JSON $e")
    }
}

internal fun parseVariables(state: State, layoutVariables: LayoutVariables, json: Any) {
    if (json !is CLObject) {
        return
    }
    val elements = json.names() ?: return
    (0 until elements.size).forEach { i ->
        val elementName = elements[i]
        val element = json.get(elementName)
        if (element is CLNumber) {
            layoutVariables.put(elementName, element.int)
        } else if (element is CLObject) {
            if (element.has("from") && element.has("to")) {
                val from = layoutVariables.get(element["from"])
                val to = layoutVariables.get(element["to"])
                val prefix = element.getStringOrNull("prefix") ?: ""
                val postfix = element.getStringOrNull("postfix") ?: ""
                layoutVariables.put(elementName, from, to, 1f, prefix, postfix)
            } else if (element.has("from") && element.has("step")) {
                val start = layoutVariables.get(element["from"])
                val increment = layoutVariables.get(element["step"])
                layoutVariables.put(elementName, start, increment)
            } else if (element.has("ids")) {
                val ids = element.getArray("ids")
                val arrayIds = arrayListOf<String>()
                for (j in 0 until ids.size()) {
                    arrayIds.add(ids.getString(j))
                }
                layoutVariables.put(elementName, arrayIds)
            } else if (element.has("tag")) {
                val arrayIds = state.getIdsForTag(element.getString("tag"))
                layoutVariables.put(elementName, arrayIds)
            }
        }
    }
}

internal fun parseDesignElementsJSON(content: String, list: ArrayList<DesignElement>) {
    val json = CLParser.parse(content)
    val elements = json.names() ?: return
    (0 until elements.size).forEach { i ->
        val elementName = elements[i]
        val element = json[elementName]
        if (PARSER_DEBUG) {
            println("element <$elementName = $element> " + element.javaClass)
        }
        when (elementName) {
            "Design" -> {
                val elements = (element as CLObject).names() ?: return
                (0 until elements.size).forEach { i ->
                    val elementName = elements[i]
                    val element = element.get(elementName) as CLObject
                    System.out.printf("element found <$elementName>")
                    val type = element.getStringOrNull("type")
                    if (type != null) {
                        var parameters = HashMap<String, String>()
                        val size = element.size()
                        for (j in 0..size - 1) {
                            val key = element[j] as CLKey
                            val paramName = key.content()
                            val paramValue = key.value?.content()
                            if (paramValue != null) {
                                parameters[paramName] = paramValue
                            }
                        }
                        var designElement = DesignElement(elementName, type, parameters)
                        list.add(designElement)
                    }
                }
            }
        }
    }
}

internal fun parseHelpers(state: State, layoutVariables: LayoutVariables, element: Any) {
    if (element !is CLArray) {
        return
    }
    (0 until element.size()).forEach { i ->
        val helper = element[i]
        if (helper is CLArray && helper.size() > 1) {
            when (helper.getString(0)) {
                "hChain" -> parseChain(ConstraintWidget.HORIZONTAL, state, layoutVariables, helper)
                "vChain" -> parseChain(ConstraintWidget.VERTICAL, state, layoutVariables, helper)
                "hGuideline" -> parseGuideline(ConstraintWidget.HORIZONTAL, state, helper)
                "vGuideline" -> parseGuideline(ConstraintWidget.VERTICAL, state, helper)
            }
        }
    }
}

internal fun parseGenerate(state: State, layoutVariables: LayoutVariables, json: Any) {
    if (json !is CLObject) {
        return
    }
    val elements = json.names() ?: return
    (0 until elements.size).forEach { i ->
        val elementName = elements[i]
        val element = json[elementName]
        val arrayIds = layoutVariables.getList(elementName)
        if (arrayIds != null && element is CLObject) {
            for (id in arrayIds) {
                parseWidget(state, layoutVariables, id, element)
            }
        }
    }
}

internal fun parseChain(orientation: Int, state: State, margins: LayoutVariables, helper: CLArray) {
    val chain =
        if (orientation == ConstraintWidget.HORIZONTAL) state.horizontalChain() else state.verticalChain()
    val refs = helper[1]
    if (refs !is CLArray || refs.size() < 1) {
        return
    }
    (0 until refs.size()).forEach { i ->
        chain.add(refs.getString(i))
    }
    if (helper.size() > 2) { // we have additional parameters
        val params = helper[2]
        if (params !is CLObject) {
            return
        }
        val constraints = params.names() ?: return
        (0 until constraints.size).forEach { i ->
            when (val constraintName = constraints[i]) {
                "style" -> {
                    val styleObject = params[constraintName]
                    val styleValue: String
                    if (styleObject is CLArray && styleObject.size() > 1) {
                        styleValue = styleObject.getString(0)
                        val biasValue = styleObject.getFloat(1)
                        chain.bias(biasValue)
                    } else {
                        styleValue = styleObject.content()
                    }
                    when (styleValue) {
                        "packed" -> chain.style(PACKED)
                        "spread_inside" -> chain.style(SPREAD_INSIDE)
                        else -> chain.style(SPREAD)
                    }
                }
                else -> {
                    parseConstraint(
                        state,
                        margins,
                        params,
                        chain as ConstraintReference,
                        constraintName
                    )
                }
            }
        }
    }
}

internal fun parseGuideline(orientation: Int, state: State, helper: CLArray) {
    val params = helper[1]
    if (params !is CLObject) {
        return
    }
    val guidelineId = params.getStringOrNull("id") ?: return
    parseGuidelineParams(orientation, state, guidelineId, params)
}

private fun parseGuidelineParams(
    orientation: Int,
    state: State,
    guidelineId: String,
    params: CLObject
) {
    val constraints = params.names() ?: return
    val reference = state.constraints(guidelineId)
    if (orientation == ConstraintWidget.HORIZONTAL) {
        state.horizontalGuideline(guidelineId)
    } else {
        state.verticalGuideline(guidelineId)
    }
    val guidelineReference = reference.facade as GuidelineReference
    (0 until constraints.size).forEach { i ->
        when (val constraintName = constraints[i]) {
            "start" -> {
                val margin = state.convertDimension(
                    Dp(
                        params.getFloat(constraintName)
                    )
                )
                guidelineReference.start(margin)
            }
            "end" -> {
                val margin = state.convertDimension(
                    Dp(
                        params.getFloat(constraintName)
                    )
                )
                guidelineReference.end(margin)
            }
            "percent" -> {
                guidelineReference.percent(
                    params.getFloat(constraintName)
                )
            }
        }
    }
}

internal fun parseBarrier(
    state: State,
    elementName: String, element: CLObject
) {
    val reference =
        state.barrier(elementName, androidx.constraintlayout.core.state.State.Direction.END)
    val constraints = element.names() ?: return
    (0 until constraints.size).forEach { i ->
        when (val constraintName = constraints[i]) {
            "direction" -> {
                when (element.getString(constraintName)) {
                    "start" -> reference.setBarrierDirection(androidx.constraintlayout.core.state.State.Direction.START)
                    "end" -> reference.setBarrierDirection(androidx.constraintlayout.core.state.State.Direction.END)
                    "left" -> reference.setBarrierDirection(androidx.constraintlayout.core.state.State.Direction.LEFT)
                    "right" -> reference.setBarrierDirection(androidx.constraintlayout.core.state.State.Direction.RIGHT)
                    "top" -> reference.setBarrierDirection(androidx.constraintlayout.core.state.State.Direction.TOP)
                    "bottom" -> reference.setBarrierDirection(androidx.constraintlayout.core.state.State.Direction.BOTTOM)
                }
            }
            "contains" -> {
                val list = element.getArrayOrNull(constraintName)
                if (list != null) {
                    for (j in 0 until list.size()) {
                        val elementNameReference = list.get(j).content()
                        val elementReference = state.constraints(elementNameReference)
                        if (PARSER_DEBUG) {
                            println("Add REFERENCE ($elementNameReference = $elementReference) TO BARRIER ")
                        }
                        reference.add(elementReference)
                    }
                }
            }
        }
    }
}

internal fun parseWidget(
    state: State,
    layoutVariables: LayoutVariables,
    elementName: String,
    element: CLObject
) {
    val reference = state.constraints(elementName)
    if (reference.width == null) {
        // Default to Wrap when the Dimension has not been assigned
        reference.width = Wrap()
    }
    if (reference.height == null) {
        // Default to Wrap when the Dimension has not been assigned
        reference.height = Wrap()
    }
    val constraints = element.names() ?: return
    (0 until constraints.size).forEach { i ->
        when (val constraintName = constraints[i]) {
            "width" -> {
                reference.width = parseDimension(element, constraintName, state)
            }
            "height" -> {
                reference.height = parseDimension(element, constraintName, state)
            }
            "center" -> {
                val target = element.getString(constraintName)
                val targetReference = if (target.equals("parent")) {
                    state.constraints(SolverState.PARENT)
                } else {
                    state.constraints(target)
                }
                reference.startToStart(targetReference)
                reference.endToEnd(targetReference)
                reference.topToTop(targetReference)
                reference.bottomToBottom(targetReference)
            }
            "centerHorizontally" -> {
                val target = element.getString(constraintName)
                val targetReference = if (target.equals("parent")) {
                    state.constraints(SolverState.PARENT)
                } else {
                    state.constraints(target)
                }
                reference.startToStart(targetReference)
                reference.endToEnd(targetReference)
            }
            "centerVertically" -> {
                val target = element.getString(constraintName)
                val targetReference = if (target.equals("parent")) {
                    state.constraints(SolverState.PARENT)
                } else {
                    state.constraints(target)
                }
                reference.topToTop(targetReference)
                reference.bottomToBottom(targetReference)
            }
            "alpha" -> {
                val value = layoutVariables.get(element[constraintName])
                reference.alpha(value)
            }
            "scaleX" -> {
                val value = layoutVariables.get(element[constraintName])
                reference.scaleX(value)
            }
            "scaleY" -> {
                val value = layoutVariables.get(element[constraintName])
                reference.scaleY(value)
            }
            "translationX" -> {
                val value = layoutVariables.get(element[constraintName])
                reference.translationX(value)
            }
            "translationY" -> {
                val value = layoutVariables.get(element[constraintName])
                reference.translationY(value)
            }
            "translationZ" -> {
                val value = layoutVariables.get(element[constraintName])
                reference.translationZ(value)
            }
            "pivotX" -> {
                val value = layoutVariables.get(element[constraintName])
                reference.pivotX(value)
            }
            "pivotY" -> {
                val value = layoutVariables.get(element[constraintName])
                reference.pivotY(value)
            }
            "rotationX" -> {
                val value = layoutVariables.get(element[constraintName])
                reference.rotationX(value)
            }
            "rotationY" -> {
                val value = layoutVariables.get(element[constraintName])
                reference.rotationY(value)
            }
            "rotationZ" -> {
                val value = layoutVariables.get(element[constraintName])
                reference.rotationZ(value)
            }
            "visibility" -> {
                when (element.getString(constraintName)) {
                    "visible" -> reference.visibility(ConstraintWidget.VISIBLE)
                    "invisible" -> reference.visibility(ConstraintWidget.INVISIBLE)
                    "gone" -> reference.visibility(ConstraintWidget.GONE)
                }
            }
            "vBias" -> {
                val value = layoutVariables.get(element[constraintName])
                reference.verticalBias(value)
            }
            "hBias" -> {
                val value = layoutVariables.get(element[constraintName])
                reference.horizontalBias(value)
            }
            "vWeight" -> {
                val value = layoutVariables.get(element[constraintName])
                reference.verticalChainWeight = value
            }
            "hWeight" -> {
                val value = layoutVariables.get(element[constraintName])
                reference.horizontalChainWeight = value
            }
            "custom" -> {
                parseCustomProperties(element, reference, constraintName)
            }
            else -> {
                parseConstraint(state, layoutVariables, element, reference, constraintName)
            }
        }
    }
}

private fun parseCustomProperties(
    element: CLObject,
    reference: ConstraintReference,
    constraintName: String
) {
    val json = element.getObjectOrNull(constraintName) ?: return
    val properties = json.names() ?: return
    (0 until properties.size).forEach { i ->
        val property = properties[i]
        val value = json.get(property)
        if (value is CLNumber) {
            reference.addCustomFloat(property, value.getFloat())
        } else if (value is CLString) {
            parseColorString(value.content())?.let {
                reference.addCustomColor(property, it)
            }
        }
    }
}

private fun parseConstraint(
    state: State,
    layoutVariables: LayoutVariables,
    element: CLObject,
    reference: ConstraintReference,
    constraintName: String
) {
    val constraint = element.getArrayOrNull(constraintName)
    if (constraint != null && constraint.size() > 1) {
        val target = constraint.getString(0)
        val anchor = constraint.getStringOrNull(1)
        var margin = 0f
        var marginGone = 0f
        if (constraint.size() > 2) {
            margin = layoutVariables.get(constraint.getOrNull(2)!!)
            margin = state.convertDimension(Dp(margin)).toFloat()
        }
        if (constraint.size() > 3) {
            marginGone = layoutVariables.get(constraint.getOrNull(3)!!)
            marginGone = state.convertDimension(Dp(marginGone)).toFloat()
        }

        val targetReference = if (target.equals("parent")) {
            state.constraints(SolverState.PARENT)
        } else {
            state.constraints(target)
        }
        when (constraintName) {
            "circular" -> {
                val angle = layoutVariables.get(constraint.get(1))
                reference.circularConstraint(targetReference, angle, 0f)
            }
            "start" -> {
                when (anchor) {
                    "start" -> {
                        reference.startToStart(targetReference)
                    }
                    "end" -> reference.startToEnd(targetReference)
                }
            }
            "end" -> {
                when (anchor) {
                    "start" -> reference.endToStart(targetReference)
                    "end" -> reference.endToEnd(targetReference)
                }
            }
            "left" -> {
                when (anchor) {
                    "left" -> {
                        reference.leftToLeft(targetReference)
                    }
                    "right" -> reference.leftToRight(targetReference)
                }
            }
            "right" -> {
                when (anchor) {
                    "left" -> reference.rightToLeft(targetReference)
                    "right" -> reference.rightToRight(targetReference)
                }
            }
            "top" -> {
                when (anchor) {
                    "top" -> reference.topToTop(targetReference)
                    "bottom" -> reference.topToBottom(targetReference)
                }
            }
            "bottom" -> {
                when (anchor) {
                    "top" -> {
                        reference.bottomToTop(targetReference)
                    }
                    "bottom" -> {
                        reference.bottomToBottom(targetReference)
                    }
                }
            }
            "baseline" -> {
                when (anchor) {
                    "baseline" -> {
                        state.baselineNeededFor(reference.key)
                        state.baselineNeededFor(targetReference.key)
                        reference.baselineToBaseline(targetReference)
                    }
                    "top" -> {
                        state.baselineNeededFor(reference.key)
                        state.baselineNeededFor(targetReference.key)
                        reference.baselineToTop(targetReference)
                    }
                    "bottom" -> {
                        state.baselineNeededFor(reference.key)
                        state.baselineNeededFor(targetReference.key)
                        reference.baselineToBottom(targetReference)
                    }
                }
            }
        }
        reference.margin(margin).marginGone(marginGone.toInt())
    } else {
        val target = element.getStringOrNull(constraintName)
        if (target != null) {
            val targetReference = if (target.equals("parent")) {
                state.constraints(SolverState.PARENT)
            } else {
                state.constraints(target)
            }
            when (constraintName) {
                "start" -> reference.startToStart(targetReference)
                "end" -> reference.endToEnd(targetReference)
                "top" -> reference.topToTop(targetReference)
                "bottom" -> reference.bottomToBottom(targetReference)
                "baseline" -> {
                    state.baselineNeededFor(reference.key)
                    state.baselineNeededFor(targetReference.key)
                    reference.baselineToBaseline(targetReference)
                }
            }
        }
    }
}

private fun parseDimensionMode(dimensionString: String): Dimension {
    var dimension: Dimension = Fixed(0)
    when (dimensionString) {
        "wrap" -> dimension = Dimension.Wrap()
        "preferWrap" -> dimension = Dimension.Suggested(WRAP_DIMENSION)
        "spread" -> dimension = Dimension.Suggested(SPREAD_DIMENSION)
        "parent" -> dimension = Dimension.Parent()
        else -> {
            if (dimensionString.endsWith('%')) {
                // parent percent
                val percentString = dimensionString.substringBefore('%')
                val percentValue = percentString.toFloat() / 100f
                dimension = Dimension.Percent(0, percentValue).suggested(0)
            } else if (dimensionString.contains(':')) {
                dimension = Dimension.Ratio(dimensionString).suggested(SPREAD_DIMENSION)
            }
        }
    }
    return dimension
}

private fun parseDimension(
    element: CLObject,
    constraintName: String,
    state: State
): Dimension {
    val dimensionElement = element.get(constraintName)
    var dimension: Dimension = Fixed(0)
    if (dimensionElement is CLString) {
        dimension = parseDimensionMode(dimensionElement.content())
    } else if (dimensionElement is CLNumber) {
        dimension = Fixed(
            state.convertDimension(
                Dp(
                    element.getFloat(constraintName)
                )
            )
        )
    } else if (dimensionElement is CLObject) {
        val mode = dimensionElement.getStringOrNull("value")
        if (mode != null) {
            dimension = parseDimensionMode(mode)
        }

        val minEl = dimensionElement.getOrNull("min")
        if (minEl != null) {
            if (minEl is CLNumber) {
                val min = minEl.getFloat()
                dimension.min(state.convertDimension(Dp(min)))
            } else if (minEl is CLString) {
                dimension.min(WRAP_DIMENSION)
            }
        }
        val maxEl = dimensionElement.getOrNull("max")
        if (maxEl != null) {
            if (maxEl is CLNumber) {
                val max = maxEl.getFloat()
                dimension.max(state.convertDimension(Dp(max)))
            } else if (maxEl is CLString) {
                dimension.max(WRAP_DIMENSION)
            }
        }
    }
    return dimension
}

private fun parseColorString(value: String): Int? {
    var str = value
    if (str.startsWith('#')) {
        str = str.substring(1)
        if (str.length == 6) {
            str = "FF$str"
        }
        return parseLong(str, 16).toInt()
    } else {
        return null
    }
}

internal fun lookForType(element: CLObject): String? {
    val constraints = element.names() ?: return null
    (0 until constraints.size).forEach { i ->
        val constraintName = constraints[i]
        if (constraintName.equals("type")) {
            return element.getString("type")
        }
    }
    return null
}
