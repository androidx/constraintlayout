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
import androidx.constraintlayout.core.parser.*
import androidx.constraintlayout.core.state.ConstraintReference
import androidx.constraintlayout.core.state.Dimension
import androidx.constraintlayout.core.state.Dimension.Fixed
import androidx.constraintlayout.core.state.Dimension.SPREAD_DIMENSION
import androidx.constraintlayout.core.state.State.Chain.*
import androidx.constraintlayout.core.state.Transition
import androidx.constraintlayout.core.state.helpers.GuidelineReference
import androidx.constraintlayout.core.widgets.ConstraintWidget
import java.lang.Long.parseLong

internal val PARSER_DEBUG = false

class LayoutVariables {
    val margins = HashMap<String, Int>()
    val generators = HashMap<String, GeneratedValue>()
    val arrayIds = HashMap<String, ArrayList<String>>()

    fun put(elementName: String, element: Int) {
        margins[elementName] = element
    }

    fun put(elementName: String, start: Float, incrementBy: Float) {
        if (generators.containsKey(elementName)) {
            if (generators[elementName] is OverrideValue) {
                return
            }
        }
        var generator = Generator(start, incrementBy)
        generators[elementName] = generator
    }

    fun putOverride(elementName: String, value: Float) {
        var generator = OverrideValue(value)
        generators[elementName] = generator
    }

    fun get(elementName: Any): Float {
        if (elementName is CLString) {
            val stringValue = elementName.content()
            if (generators.containsKey(stringValue)) {
                val value = generators[stringValue]!!.value()
                return value
            }
            if (margins.containsKey(stringValue)) {
                return margins[stringValue]!!.toFloat()
            }
        } else if (elementName is CLNumber) {
            return elementName.float
        }
        return 0f
    }

    fun getList(elementName: String) : ArrayList<String>? {
        if (arrayIds.containsKey(elementName)) {
            return arrayIds[elementName]
        }
        return null
    }

    fun put(elementName: String, elements: ArrayList<String>) {
        arrayIds[elementName] = elements
    }

}
interface GeneratedValue {
    fun value() : Float
}

class Generator(start: Float, incrementBy: Float) : GeneratedValue {
    var start : Float = start
    var incrementBy: Float = incrementBy
    var current : Float = start
    var stop = false

    override fun value() : Float {
        if (!stop) {
            current += incrementBy
        }
        return current
    }
}

class OverrideValue(value: Float) : GeneratedValue {
    var value : Float = value
    override fun value() : Float {
        return value
    }
}

internal fun parseTransition(content: String, transition: Transition) {
    try {
        val json = CLParser.parse(content)
        val pathMotionArc = json.getStringOrNull("pathMotionArc")
        if (pathMotionArc != null) {
            var bundle = TypedBundle()
            when (pathMotionArc) {
                "none" -> bundle.add(TypedValues.Position.TYPE_PATH_MOTION_ARC, 0)
                "startVertical" -> bundle.add(TypedValues.Position.TYPE_PATH_MOTION_ARC, 1)
                "startHorizontal" -> bundle.add(TypedValues.Position.TYPE_PATH_MOTION_ARC, 2)
                "flip" -> bundle.add(TypedValues.Position.TYPE_PATH_MOTION_ARC, 3)
            }
            transition.setTransitionProperties(bundle)
        }
        val keyframes = json.getObjectOrNull("KeyFrames")
        if (keyframes == null) {
            return
        }
        val keypositions = keyframes.getArrayOrNull("KeyPositions")
        if (keypositions != null) {
            (0 until keypositions.size()).forEach { i ->
                val keyposition = keypositions[i]
                if (keyposition is CLObject) {
                    parseKeyPosition(keyposition, transition)
                }
            }
        }
        val keyattributes = keyframes.getArrayOrNull("KeyAttributes")
        if (keyattributes != null) {
            (0 until keyattributes.size()).forEach { i ->
                val keyattribute = keyattributes[i]
                if (keyattribute is CLObject) {
                    parseKeyAttribute(keyattribute, transition)
                }
            }
        }
    } catch (e: CLParsingException) {
        System.err.println("Error parsing JSON $e")
    }
}

fun parseKeyPosition(keyposition: CLObject, transition: Transition) {
    var bundle = TypedBundle()
    val targets = keyposition.getArray("target")
    val frames = keyposition.getArray("frames")
    val percentX = keyposition.getArrayOrNull("percentX")
    val percentY = keyposition.getArrayOrNull("percentY")
    val percentWidth = keyposition.getArrayOrNull("percentWidth")
    val percentHeight = keyposition.getArrayOrNull("percentHeight")
    val pathMotionArc = keyposition.getStringOrNull("pathMotionArc")
    val transitionEasing = keyposition.getStringOrNull("transitionEasing")
    val curveFit = keyposition.getStringOrNull("curveFit")
    var type = keyposition.getStringOrNull("type") ?: "parentRelative"
    if (percentX != null && frames.size() != percentX.size()) {
        return
    }
    if (percentY != null && frames.size() != percentY.size()) {
        return
    }
    (0 until targets.size()).forEach { i ->
        val target = targets.getString(i)
        bundle.clear();
        if (type != null) {
            bundle.add(
                TypedValues.Position.TYPE_POSITION_TYPE, when (type) {
                    "deltaRelative" -> 0
                    "pathRelative" -> 1
                    "parentRelative" -> 2
                    else -> 0
                }
            )
        }
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
            bundle.add(TypedValues.TYPE_FRAME_POSITION, frame);
            if (percentX != null) {
                bundle.add(TypedValues.Position.TYPE_PERCENT_X, percentX.getFloat(j));
            }
            if (percentY != null) {
                bundle.add(TypedValues.Position.TYPE_PERCENT_Y, percentY.getFloat(j));
            }
            if (percentWidth != null) {
                bundle.add(TypedValues.Position.TYPE_PERCENT_Y, percentWidth.getFloat(j));
            }
            if (percentHeight != null) {
                bundle.add(TypedValues.Position.TYPE_PERCENT_Y, percentHeight.getFloat(j));
            }

            transition.addKeyPosition(target, bundle)
        }
    }
}

fun parseKeyAttribute(keyattribute: CLObject, transition: Transition) {
    val targets = keyattribute.getArray("target")
    val frames = keyattribute.getArray("frames")
    val transitionEasing = keyattribute.getStringOrNull("transitionEasing")

    val types = arrayListOf<String>("scaleX", "scaleY")
    val values = arrayListOf<Int>(
        TypedValues.Attributes.TYPE_SCALE_X,
        TypedValues.Attributes.TYPE_SCALE_Y
    )

    var bundles = ArrayList<TypedBundle>()
    (0 until targets.size()).forEach { i ->
        bundles.add(TypedBundle())
    }

    for (k in 0 .. types.size - 1) {
        var type = types[k]
        val arrayValues = keyattribute.getArrayOrNull(type)
        if (arrayValues != null && arrayValues.size() != targets.size()) {
            throw CLParsingException("incorrect size for $type array, " +
                    "not matching targets array!", keyattribute)
        }
        if (arrayValues != null) {
            (0 until targets.size()).forEach { i ->
                var bundle = bundles.get(i)
                (0 until frames.size()).forEach { j ->
                    bundle.add(values[k], arrayValues.getFloat(j));
                }
            }
        } else {
            val value = keyattribute.getFloatOrNaN(type)
            if (!value.isNaN()) {
                (0 until targets.size()).forEach { i ->
                    var bundle = bundles.get(i)
                    (0 until frames.size()).forEach { j ->
                        bundle.add(values[k], value);
                    }
                }
            }
        }
    }

    val curveFit = keyattribute.getStringOrNull("curveFit")
    (0 until targets.size()).forEach { i ->
        var bundle = bundles.get(i)
        val target = targets.getString(i)
        if (curveFit != null) {
            when (curveFit) {
                "spline" -> bundle.add(TypedValues.Position.TYPE_CURVE_FIT, 0)
                "linear" -> bundle.add(TypedValues.Position.TYPE_CURVE_FIT, 1)
            }
        }
        bundle.addIfNotNull(TypedValues.Position.TYPE_TRANSITION_EASING, transitionEasing)

        (0 until frames.size()).forEach { j ->
            val frame = frames.getInt(j)
            bundle.add(TypedValues.TYPE_FRAME_POSITION, frame);
            transition.addKeyAttribute(target, bundle)
        }
    }
}

internal fun parseJSON(content: String, transition: Transition,
                       state: Int, layoutVariables: LayoutVariables) {
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
                    (0 until properties.size).forEach { i ->
                        val property = properties[i]
                        val value = customProperties.get(property)
                        if (value is CLNumber) {
                            transition.addCustomFloat(state, elementName, property, value.getFloat())
                        } else if (value is CLString) {
                            var stringValue = value.content()
                            if (stringValue.startsWith('#')) {
                                var r = 0f
                                var g = 0f
                                var b = 0f
                                var a = 1f
                                if (stringValue.length == 7 || stringValue.length == 9) {
                                    var hr = Integer.valueOf(stringValue.substring(1, 3), 16)
                                    var hg = Integer.valueOf(stringValue.substring(3, 5), 16)
                                    var hb = Integer.valueOf(stringValue.substring(5, 7), 16)
                                    r = hr.toFloat() / 255f
                                    g = hg.toFloat() / 255f
                                    b = hb.toFloat() / 255f
                                }
                                if (stringValue.length == 9) {
                                    var ha = Integer.valueOf(stringValue.substring(5, 7), 16)
                                    a = ha.toFloat() / 255f
                                }
                                var color = Integer.valueOf(stringValue.substring(1),16)
                                if (stringValue.length == 7) {
                                    color = color or 0xFF000000.toInt()
                                }
                                transition.addCustomColor(state, elementName, property, color);
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
            }
        }
    } catch (e: CLParsingException) {
        System.err.println("Error parsing JSON $e")
    }
}

fun parseConstraintSets(scene: MotionScene, json: Any) {
    if (!(json is CLObject)) {
        return
    }
    val elements = json.names() ?: return
    (0 until elements.size).forEach { i ->
        val elementName = elements[i]
        val element = json.getObject(elementName)
        var added = false
        val extends = element.getStringOrNull("Extends")
        if (extends != null && extends.length > 0) {
            val base = scene.getConstraintSet(extends)
            if (base != null) {
                val baseJson = CLParser.parse(base)
                val widgetsOverride = element.names()
                if (widgetsOverride != null) {
                    (0 until widgetsOverride.size).forEach { j ->
                        val widgetOverrideName = widgetsOverride[j]
                        val value = element[widgetOverrideName]
                        if (value is CLObject) {
                            override(baseJson, widgetOverrideName, value)
                        }
                    }
                    scene.setConstraintSetContent(elementName, baseJson.toJSON())
                    added = true
                }
            }
        }
        if (!added) {
            scene.setConstraintSetContent(elementName, element.toJSON())
        }
    }
}

fun override(baseJson: CLObject, name: String, overrideValue: CLObject) {
    if (!baseJson.has(name)) {
        baseJson.put(name, overrideValue)
    } else {
        var base = baseJson.getObject(name)
        var keys = overrideValue.names()
        for (key in keys) {
            if (key.equals("clear")) {
                var toClear = overrideValue.getArray("clear")
                (0 until toClear.size()).forEach { i ->
                    var clearedKey = toClear.getStringOrNull(i)
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
                            }
                            "transforms" -> {
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

fun parseTransitions(scene: MotionScene, json: Any) {
    if (!(json is CLObject)) {
        return
    }
    val elements = json.names() ?: return
    (0 until elements.size).forEach { i ->
        val elementName = elements[i]
        val element = json.getObject(elementName)
        scene.setTransitionContent(elementName, element.toJSON())
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
                System.out.println("element <$elementName = $element> " + element.javaClass)
            }
            when (elementName) {
                "Variables" -> parseVariables(state, layoutVariables, element)
                "Helpers" -> parseHelpers(state, layoutVariables, element)
                "Generate" -> parseGenerate(state, layoutVariables, element)
                else -> {
                    if (element is CLObject) {
                        var type = lookForType(element)
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
                        } else if (type == null) {
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

fun parseVariables(state: State, layoutVariables: LayoutVariables, json: Any) {
    if (!(json is CLObject)) {
        return
    }
    val elements = json.names() ?: return
    (0 until elements.size).forEach { i ->
        val elementName = elements[i]
        val element = json.get(elementName);
        if (element is CLNumber) {
            layoutVariables.put(elementName, element.int)
        } else if (element is CLObject) {
            if (element.has("from") && element.has("step")) {
                var start = layoutVariables.get(element["from"])
                var increment = layoutVariables.get(element["step"])
                layoutVariables.put(elementName, start, increment)
            } else if (element.has("ids")) {
                var ids = element.getArray("ids");
                var arrayIds = arrayListOf<String>()
                for (i in 0..ids.size()-1) {
                    arrayIds.add(ids.getString(i))
                }
                layoutVariables.put(elementName, arrayIds)
            } else if (element.has("tag")) {
                var arrayIds = state.getIdsForTag(element.getString("tag"))
                layoutVariables.put(elementName, arrayIds)
            }
        }
    }
}

fun parseHelpers(state: State, layoutVariables: LayoutVariables, element: Any) {
    if (!(element is CLArray)) {
        return
    }
    (0 until element.size()).forEach { i ->
        val helper = element[i]
        if (helper is CLArray && helper.size() > 1) {
            when (helper.getString(0)) {
                "hChain" -> parseChain(ConstraintWidget.HORIZONTAL, state, layoutVariables, helper)
                "vChain" -> parseChain(ConstraintWidget.VERTICAL, state, layoutVariables, helper)
                "hGuideline" -> parseGuideline(ConstraintWidget.HORIZONTAL, state, layoutVariables, helper)
                "vGuideline" -> parseGuideline(ConstraintWidget.VERTICAL, state, layoutVariables, helper)
            }
        }
    }
}

fun parseGenerate(state: State, layoutVariables: LayoutVariables, json: Any) {
    if (!(json is CLObject)) {
        return
    }
    val elements = json.names() ?: return
    (0 until elements.size).forEach { i ->
        val elementName = elements[i]
        val element = json[elementName]
        var arrayIds = layoutVariables.getList(elementName)
        if (arrayIds != null && element is CLObject) {
            for (id in arrayIds) {
                parseWidget(state, layoutVariables, id, element)
            }
        }
    }
}

fun parseChain(orientation: Int, state: State, margins: LayoutVariables, helper: CLArray) {
    var chain = if (orientation == ConstraintWidget.HORIZONTAL) state.horizontalChain() else state.verticalChain()
    var refs = helper[1]
    if (!(refs is CLArray) || refs.size() < 1) {
        return
    }
    (0 until refs.size()).forEach { i ->
        chain.add(refs.getString(i))
    }
    if (helper.size() > 2) { // we have additional parameters
        var params = helper[2]
        if (!(params is CLObject)) {
            return
        }
        val constraints = params.names() ?: return
        (0 until constraints.size).forEach{ i ->
            val constraintName = constraints[i]
            when (constraintName) {
                "style" -> {
                    val styleObject = params[constraintName]
                    val styleValue : String
                    if (styleObject is CLArray && styleObject.size() > 1) {
                        styleValue = styleObject.getString(0)
                        var biasValue = styleObject.getFloat(1)
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
                    parseConstraint(state, margins, params, chain as ConstraintReference, constraintName)
                }
            }
        }
    }
}

fun parseGuideline(orientation: Int, state: State, margins: LayoutVariables, helper: CLArray) {
    var params = helper[1]
    if (!(params is CLObject)) {
        return
    }
    val guidelineId = params.getStringOrNull("id")
    if (guidelineId == null)  {
        return
    }
    parseGuidelineParams(orientation, state, guidelineId, params)
}

private fun parseGuidelineParams(
    orientation: Int,
    state: State,
    guidelineId: String,
    params: CLObject
) {
    val constraints = params.names() ?: return
    var reference = state.constraints(guidelineId)
    if (orientation == ConstraintWidget.HORIZONTAL) {
        state.horizontalGuideline(guidelineId)
    } else {
        state.verticalGuideline(guidelineId)
    }
    var guidelineReference = reference.facade as GuidelineReference
    (0 until constraints.size).forEach { i ->
        val constraintName = constraints[i]
        when (constraintName) {
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

fun parseBarrier(
    state: State,
    elementName: String, element: CLObject) {
    val reference = state.barrier(elementName, androidx.constraintlayout.core.state.State.Direction.END)
    val constraints = element.names() ?: return
    var barrierReference = reference
    (0 until constraints.size).forEach { i ->
        val constraintName = constraints[i]
        when (constraintName) {
            "direction" -> {
                var direction = element.getString(constraintName)
                when (direction) {
                    "start" -> barrierReference.setBarrierDirection(androidx.constraintlayout.core.state.State.Direction.START)
                    "end" -> barrierReference.setBarrierDirection(androidx.constraintlayout.core.state.State.Direction.END)
                    "left" -> barrierReference.setBarrierDirection(androidx.constraintlayout.core.state.State.Direction.LEFT)
                    "right" -> barrierReference.setBarrierDirection(androidx.constraintlayout.core.state.State.Direction.RIGHT)
                    "top" -> barrierReference.setBarrierDirection(androidx.constraintlayout.core.state.State.Direction.TOP)
                    "bottom" -> barrierReference.setBarrierDirection(androidx.constraintlayout.core.state.State.Direction.BOTTOM)
                }
            }
            "contains" -> {
                val list = element.getArrayOrNull(constraintName)
                if (list != null) {
                    for (i in 0..list.size() - 1) {
                        var elementName = list.get(i)
                        val reference = state.constraints(elementName)
                        System.out.println("Add REFERENCE ($elementName = $reference) TO BARRIER ")
                        barrierReference.add(reference)
                    }
                }
            }
        }
    }
}

fun parseWidget(
    state: State,
    layoutVariables: LayoutVariables,
    elementName: String,
    element: CLObject
) {
    val reference = state.constraints(elementName)
    val constraints = element.names() ?: return
    reference.width = Dimension.Wrap()
    reference.height = Dimension.Wrap()
    (0 until constraints.size).forEach { i ->
        val constraintName = constraints[i]
        when (constraintName) {
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
                val value = element.getString(constraintName)
                when(value) {
                    "visible" -> reference.visibility(ConstraintWidget.VISIBLE)
                    "invisible" -> reference.visibility(ConstraintWidget.INVISIBLE)
                    "gone" -> reference.visibility(ConstraintWidget.GONE)
                }
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
    var json = element.getObjectOrNull(constraintName)
    if (json == null) {
        return
    }
    val properties = json.names() ?: return
    (0 until properties.size).forEach { i ->
        val property = properties[i]
        val value = json.get(property)
        if (value is CLNumber) {
            reference.addCustomFloat(property, value.getFloat())
        } else if (value is CLString) {
            var str = value.content().toString()
            if (str.startsWith('#')) {
                str = str.substring(1)
                if(str.length == 6) {
                    str = "FF"+str;
                }
                reference.addCustomColor(property, parseLong(str,16).toInt())
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
                var angle = layoutVariables.get(constraint.get(1))
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
        }
        reference.margin(margin).marginGone(marginGone.toInt())
    } else {
        var target = element.getStringOrNull(constraintName)
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
            }
        }
    }
}

private fun parseDimension(
    element: CLObject,
    constraintName: String,
    state: State
): Dimension {
    var dimensionElement = element.get(constraintName)
    var dimension: Dimension = Fixed(0)
    if (dimensionElement is CLString) {
        var dimensionString = dimensionElement.content()
        when (dimensionString) {
            "wrap" -> dimension = Dimension.Wrap()
            "spread" -> dimension = Dimension.Suggested(SPREAD_DIMENSION)
            "parent" -> dimension = Dimension.Parent()
            else -> {
                if (dimensionString.endsWith('%')) {
                    // parent percent
                    var percentString = dimensionString.substringBefore('%')
                    var percentValue = percentString.toFloat() / 100f
                    dimension = Dimension.Percent(0, percentValue).suggested(0)
                } else if (dimensionString.contains(':')) {
                    dimension = Dimension.Ratio(dimensionString).suggested(0)
                }
            }
        }
    } else if (dimensionElement is CLNumber) {
        dimension = Dimension.Fixed(
            state.convertDimension(
                Dp(
                    element.getFloat(constraintName)
                )
            )
        )
    }
    return dimension
}

fun lookForType(element: CLObject): String? {
    val constraints = element.names() ?: return null
    (0 until constraints.size).forEach { i ->
        val constraintName = constraints[i]
        if (constraintName.equals("type")) {
            return element.getString("type")
        }
    }
    return null
}
