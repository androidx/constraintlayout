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
import androidx.constraintlayout.core.state.ConstraintReference
import androidx.constraintlayout.core.state.Dimension
import androidx.constraintlayout.core.state.Dimension.SPREAD_DIMENSION
import androidx.constraintlayout.core.state.State.Chain.*
import androidx.constraintlayout.core.state.Transition
import androidx.constraintlayout.core.state.helpers.GuidelineReference
import androidx.constraintlayout.core.widgets.ConstraintWidget
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

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
        if (elementName is String) {
            if (generators.containsKey(elementName)) {
                val value = generators[elementName]!!.value()
                return value
            }
            if (margins.containsKey(elementName)) {
                return margins[elementName]!!.toFloat()
            }
        } else if (elementName is Int) {
            return elementName.toFloat()
        } else if (elementName is Double) {
            return elementName.toFloat()
        } else if (elementName is Float) {
            return elementName
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

internal fun parseKeyframesJSON(content: String, transition: Transition) {
    try {
        val json = JSONObject(content)
        val keyframes = json.optJSONObject("KeyFrames")
        if (keyframes == null) {
            return
        }
        val keypositions = keyframes.optJSONArray("KeyPositions")
        if (keypositions == null) {
            return
        }
        (0 until keypositions.length()).forEach { i ->
            val keyposition = keypositions[i]
            if (keyposition is JSONObject) {
                parseKeyPosition(keyposition, transition)
            }
        }
    } catch (e: JSONException) {
        System.err.println("Error parsing JSON $e")
    }
}

fun parseKeyPosition(keyposition: JSONObject, transition: Transition) {
    val targets = keyposition.getJSONArray("target")
    val frames = keyposition.getJSONArray("frames")
    val percentX = keyposition.getJSONArray("percentX")
    val percentY = keyposition.getJSONArray("percentY")
    if (frames.length() != percentX.length() || frames.length() != percentY.length()) {
        return
    }
    (0 until targets.length()).forEach { i ->
        val target = targets.getString(i)
        (0 until frames.length()).forEach { j ->
            val frame = frames.getInt(j)
            val x = percentX.getDouble(j).toFloat()
            val y = percentY.getDouble(j).toFloat()
            transition.addKeyPosition(target, frame, 0, x, y)
        }
    }
}

internal fun parseJSON(content: String, transition: Transition,
                       state: Int, layoutVariables: LayoutVariables) {
    try {
        val json = JSONObject(content)
        val elements = json.names() ?: return
        (0 until elements.length()).forEach { i ->
            val elementName = elements[i].toString()
            val element = json[elementName]
            if (element is JSONObject) {
                val customProperties = element.optJSONObject("custom")
                if (customProperties != null) {
                    val properties = customProperties.names() ?: return
                    (0 until properties.length()).forEach { i ->
                        val property = properties[i].toString()
                        val value = customProperties[property]
                        if (value is Int) {
                            transition.addCustomFloat(state, elementName, property, value.toFloat())
                        } else if (value is Float) {
                            transition.addCustomFloat(state, elementName, property, value)
                        } else if (value is String) {
                            if (value.startsWith('#')) {
                                var r = 0f
                                var g = 0f
                                var b = 0f
                                var a = 1f
                                if (value.length == 7 || value.length == 9) {
                                    var hr = Integer.valueOf(value.substring(1, 3), 16)
                                    var hg = Integer.valueOf(value.substring(3, 5), 16)
                                    var hb = Integer.valueOf(value.substring(5, 7), 16)
                                    r = hr.toFloat() / 255f
                                    g = hg.toFloat() / 255f
                                    b = hb.toFloat() / 255f
                                }
                                if (value.length == 9) {
                                    var ha = Integer.valueOf(value.substring(5, 7), 16)
                                    a = ha.toFloat() / 255f
                                }
                                transition.addCustomColor(state, elementName, property, r, g, b, a)
                            }
                        }
                    }
                }
            }
        }
    } catch (e: JSONException) {
        System.err.println("Error parsing JSON $e")
    }
}

internal fun parseMotionSceneJSON(scene: MotionScene, content: String) {
    try {
        val json = JSONObject(content)
        val elements = json.names() ?: return
        (0 until elements.length()).forEach { i ->
            val elementName = elements[i].toString()
            val element = json[elementName]
            when (elementName) {
                "ConstraintSets" -> parseConstraintSets(scene, element)
                "Transitions" -> parseTransitions(scene, element)
            }
        }
    } catch (e: JSONException) {
        System.err.println("Error parsing JSON $e")
    }
}

fun parseConstraintSets(scene: MotionScene, json: Any) {
    if (!(json is JSONObject)) {
        return
    }
    val elements = json.names() ?: return
    (0 until elements.length()).forEach { i ->
        val elementName = elements[i].toString()
        val element = json[elementName]
        var added = false
        if (element is JSONObject) {
            val extends = element.optString("Extends")
            if (extends.length > 0) {
                val base = scene.getConstraintSet(extends)
                if (base != null) {
                    val baseJson = JSONObject(base)
                    val widgetsOverride = element.names()
                    if (widgetsOverride != null) {
                        (0 until widgetsOverride.length()).forEach { j ->
                            val widgetOverrideName = widgetsOverride[j].toString()
                            val value = element[widgetOverrideName]
                            if (value is JSONObject) {
                                override(baseJson, widgetOverrideName, value)
                            }
                        }
                        scene.setConstraintSetContent(elementName, baseJson.toString())
                        added = true
                    }
                }
            }
        }
        if (!added) {
            scene.setConstraintSetContent(elementName, element.toString())
        }
    }
}

fun override(baseJson: JSONObject, name: String, overrideValue: JSONObject) {
    if (!baseJson.has(name)) {
        baseJson.put(name, overrideValue)
    } else {
        var base = baseJson.getJSONObject(name)
        var keys = overrideValue.keys()
        for (key in keys) {
            if (key.equals("clear")) {
                var toClear = overrideValue.getJSONArray("clear")
                (0 until toClear.length()).forEach { i ->
                    var clearedKey = toClear[i]
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
    if (!(json is JSONObject)) {
        return
    }
    val elements = json.names() ?: return
    (0 until elements.length()).forEach { i ->
        val elementName = elements[i].toString()
        val element = json[elementName]
        scene.setTransitionContent(elementName, element.toString())
    }
}

internal fun parseJSON(content: String, state: State, layoutVariables: LayoutVariables) {
    try {
        val json = JSONObject(content)
        val elements = json.names() ?: return
        (0 until elements.length()).forEach { i ->
            val elementName = elements[i].toString()
            val element = json[elementName]
            if (PARSER_DEBUG) {
                System.out.println("element <$elementName = $element> " + element.javaClass)
            }
            when (elementName) {
                "Variables" -> parseVariables(state, layoutVariables, element)
                "Helpers" -> parseHelpers(state, layoutVariables, element)
                "Generate" -> parseGenerate(state, layoutVariables, element)
                else -> {
                    if (element is JSONObject) {
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
                    }
                }
            }
        }
    } catch (e: JSONException) {
        System.err.println("Error parsing JSON $e")
    }
}

fun parseVariables(state: State, layoutVariables: LayoutVariables, json: Any) {
    if (!(json is JSONObject)) {
        return
    }
    val elements = json.names() ?: return
    (0 until elements.length()).forEach { i ->
        val elementName = elements[i].toString()
        val element = json[elementName]
        if (element is Int) {
            layoutVariables.put(elementName, element)
        } else if (element is JSONObject) {
            if (element.has("start") && element.has("increment")) {
                var start = layoutVariables.get(element["start"])
                var increment = layoutVariables.get(element["increment"])
                layoutVariables.put(elementName, start, increment)
            } else if (element.has("ids")) {
                var ids = element.getJSONArray("ids");
                var arrayIds = arrayListOf<String>()
                for (i in 0..ids.length()-1) {
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
    if (!(element is JSONArray)) {
        return
    }
    (0 until element.length()).forEach { i ->
        val helper = element[i]
        if (helper is JSONArray && helper.length() > 1) {
            when (helper[0]) {
                "hChain" -> parseChain(ConstraintWidget.HORIZONTAL, state, layoutVariables, helper)
                "vChain" -> parseChain(ConstraintWidget.VERTICAL, state, layoutVariables, helper)
                "hGuideline" -> parseGuideline(ConstraintWidget.HORIZONTAL, state, layoutVariables, helper)
                "vGuideline" -> parseGuideline(ConstraintWidget.VERTICAL, state, layoutVariables, helper)
            }
        }
    }
}

fun parseGenerate(state: State, layoutVariables: LayoutVariables, json: Any) {
    if (!(json is JSONObject)) {
        return
    }
    val elements = json.names() ?: return
    (0 until elements.length()).forEach { i ->
        val elementName = elements[i].toString()
        val element = json[elementName]
        var arrayIds = layoutVariables.getList(elementName)
        if (arrayIds != null && element is JSONObject) {
            for (id in arrayIds) {
                parseWidget(state, layoutVariables, id, element)
            }
        }
    }
}

fun parseChain(orientation: Int, state: State, margins: LayoutVariables, helper: JSONArray) {
    var chain = if (orientation == ConstraintWidget.HORIZONTAL) state.horizontalChain() else state.verticalChain()
    var refs = helper[1]
    if (!(refs is JSONArray) || refs.length() < 1) {
        return
    }
    (0 until refs.length()).forEach { i ->
        chain.add(refs[i])
    }
    if (helper.length() > 2) { // we have additional parameters
        var params = helper[2]
        if (!(params is JSONObject)) {
            return
        }
        val constraints = params.names() ?: return
        (0 until constraints.length()).forEach{ i ->
            val constraintName = constraints[i].toString()
            when (constraintName) {
                "style" -> {
                    val styleObject = params[constraintName]
                    val styleValue : String
                    if (styleObject is JSONArray && styleObject.length() > 1) {
                        styleValue = styleObject[0].toString()
                        var biasValue = styleObject[1].toString().toFloat()
                        chain.bias(biasValue)
                    } else {
                        styleValue = styleObject.toString()
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

fun parseGuideline(orientation: Int, state: State, margins: LayoutVariables, helper: JSONArray) {
    var params = helper[1]
    if (!(params is JSONObject)) {
        return
    }
    val guidelineId = params.opt("id")
    if (guidelineId == null)  {
        return
    }
    parseGuidelineParams(orientation, state, guidelineId as String, params)
}

private fun parseGuidelineParams(
    orientation: Int,
    state: State,
    guidelineId: String,
    params: JSONObject
) {
    val constraints = params.names() ?: return
    var reference = state.constraints(guidelineId)
    if (orientation == ConstraintWidget.HORIZONTAL) {
        state.horizontalGuideline(guidelineId)
    } else {
        state.verticalGuideline(guidelineId)
    }
    var guidelineReference = reference.facade as GuidelineReference
    (0 until constraints.length()).forEach { i ->
        val constraintName = constraints[i].toString()
        when (constraintName) {
            "start" -> {
                val margin = state.convertDimension(
                    Dp(
                        params.getInt(constraintName).toFloat()
                    )
                )
                guidelineReference.start(margin)
            }
            "end" -> {
                val margin = state.convertDimension(
                    Dp(
                        params.getInt(constraintName).toFloat()
                    )
                )
                guidelineReference.end(margin)
            }
            "percent" -> {
                guidelineReference.percent(
                    params.getDouble(
                        constraintName
                    ).toFloat()
                )
            }
        }
    }
}

fun parseBarrier(
    state: State,
    elementName: String, element: JSONObject) {
    val reference = state.barrier(elementName, androidx.constraintlayout.core.state.State.Direction.END)
    val constraints = element.names() ?: return
    var barrierReference = reference
    (0 until constraints.length()).forEach { i ->
        val constraintName = constraints[i].toString()
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
                val list = element.optJSONArray(constraintName)
                if (list != null) {
                    for (i in 0..list.length() - 1) {
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
    element: JSONObject
) {
    val reference = state.constraints(elementName)
    val constraints = element.names() ?: return
    reference.width = Dimension.Wrap()
    reference.height = Dimension.Wrap()
    (0 until constraints.length()).forEach { i ->
        val constraintName = constraints[i].toString()
        when (constraintName) {
            "width" -> {
                reference.width = parseDimension(element, constraintName, state)
            }
            "height" -> {
                reference.height = parseDimension(element, constraintName, state)
            }
            "center" -> {
                val target = element.getString(constraintName)
                val targetReference = if (target.toString().equals("parent")) {
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
                val targetReference = if (target.toString().equals("parent")) {
                    state.constraints(SolverState.PARENT)
                } else {
                    state.constraints(target)
                }
                reference.startToStart(targetReference)
                reference.endToEnd(targetReference)
            }
            "centerVertically" -> {
                val target = element.getString(constraintName)
                val targetReference = if (target.toString().equals("parent")) {
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
                val value = element[constraintName]
                when(value) {
                    "visible" -> reference.visibility(ConstraintWidget.VISIBLE)
                    "invisible" -> reference.visibility(ConstraintWidget.INVISIBLE)
                    "gone" -> reference.visibility(ConstraintWidget.GONE)
                }
            }
            "zIndex" -> {
                val value = layoutVariables.get(element[constraintName])
                reference.zIndex(value)
            }
            "shadowElevation" -> {
                val value = layoutVariables.get(element[constraintName])
                reference.shadowElevation(value)
            }
            "cameraDistance" -> {
                val value = layoutVariables.get(element[constraintName])
                reference.cameraDistance(value)
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
    element: JSONObject,
    reference: ConstraintReference,
    constraintName: String
) {
    var json = element.optJSONObject(constraintName)
    if (json == null) {
        return
    }
    val properties = json.names() ?: return
    (0 until properties.length()).forEach { i ->
        val property = properties[i].toString()
        val value = json[property]
        if (value is Int) {
            reference.addCustomFloat(property, value.toFloat())
        } else if (value is Float) {
            reference.addCustomFloat(property, value)
        } else if (value is String) {
            if (value.startsWith('#')) {
                var r = 0f
                var g = 0f
                var b = 0f
                var a = 1f
                if (value.length == 7 || value.length == 9) {
                    var hr = Integer.valueOf(value.substring(1, 3), 16)
                    var hg = Integer.valueOf(value.substring(3, 5), 16)
                    var hb = Integer.valueOf(value.substring(5, 7), 16)
                    r = hr.toFloat() / 255f
                    g = hg.toFloat() / 255f
                    b = hb.toFloat() / 255f
                }
                if (value.length == 9) {
                    var ha = Integer.valueOf(value.substring(5, 7), 16)
                    a = ha.toFloat() / 255f
                }
                reference.addCustomColor(property, r, g, b, a)
            }
        }
    }
}

private fun parseConstraint(
    state: State,
    layoutVariables: LayoutVariables,
    element: JSONObject,
    reference: ConstraintReference,
    constraintName: String
) {
    val constraint = element.optJSONArray(constraintName)
    if (constraint != null && constraint.length() > 1) {
        val target = constraint[0]
        val anchor = constraint[1]
        var margin = 0
        var marginGone = 0
        if (constraint.length() > 2) {
            margin = layoutVariables.get(constraint[2]).toInt()
            margin = state.convertDimension(Dp(margin.toFloat()))
        }
        if (constraint.length() > 3) {
            marginGone = layoutVariables.get(constraint[3]).toInt()
            marginGone = state.convertDimension(Dp(marginGone.toFloat()))
        }

        val targetReference = if (target.toString().equals("parent")) {
            state.constraints(SolverState.PARENT)
        } else {
            state.constraints(target)
        }
        when (constraintName) {
            "circular" -> {
                var angle = layoutVariables.get(constraint[1])
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
        reference.margin(margin).marginGone(marginGone)
    } else {
        var target = element.optString(constraintName)
        if (target != null) {
            val targetReference = if (target.toString().equals("parent")) {
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
    element: JSONObject,
    constraintName: String,
    state: State
): Dimension {
    var dimensionString = element.getString(constraintName)
    var dimension: Dimension
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
            } else {
                dimension = Dimension.Fixed(
                    state.convertDimension(
                        Dp(
                            element.getInt(constraintName).toFloat()
                        )
                    )
                )
            }
        }
    }
    return dimension
}

fun lookForType(element: JSONObject): String? {
    val constraints = element.names() ?: return null
    (0 until constraints.length()).forEach { i ->
        val constraintName = constraints[i].toString()
        if (constraintName.equals("type")) {
            return element.getString("type")
        }
    }
    return null
}
