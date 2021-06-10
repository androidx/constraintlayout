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
import androidx.constraintlayout.core.state.helpers.GuidelineReference
import androidx.constraintlayout.core.widgets.ConstraintWidget
import org.json.JSONArray
import org.json.JSONObject

internal val PARSER_DEBUG = false

internal fun parseJSON(content: String, state: State) {
    val margins = HashMap<String, Int>()
    val json = JSONObject(content)
    val elements = json.names() ?: return
    (0 until elements.length()).forEach { i ->
        val elementName = elements[i].toString()
        val element = json[elementName]
        if (PARSER_DEBUG) {
            System.out.println("element <$elementName = $element> " + element.javaClass)
        }
        when (elementName) {
            "Variables" -> parseVariables(margins, element)
            "Helpers" -> parseHelpers(state, margins, element)
            else -> {
                if (element is JSONObject) {
                    var type = lookForType(element)
                    if (type != null) {
                        when (type) {
                            "hGuideline" -> parseGuidelineParams(ConstraintWidget.HORIZONTAL, state, elementName, element)
                            "vGuideline" -> parseGuidelineParams(ConstraintWidget.VERTICAL, state, elementName, element)
                            "barrier" -> parseBarrier(state, margins, elementName, element)
                        }
                    } else if (type == null) {
                        parseWidget(state, margins, elementName, element)
                    }
                }
            }
        }
    }
}

fun parseVariables(margins: HashMap<String, Int>, json: Any) {
    if (!(json is JSONObject)) {
        return
    }
    val elements = json.names() ?: return
    (0 until elements.length()).forEach { i ->
        val elementName = elements[i].toString()
        val element = json[elementName]
        if (element is Int) {
            margins[elementName] = element
        }
    }
}

fun parseHelpers(state: State, margins: HashMap<String, Int>, element: Any) {
    if (!(element is JSONArray)) {
        return
    }
    (0 until element.length()).forEach { i ->
        val helper = element[i]
        if (helper is JSONArray && helper.length() > 1) {
            when (helper[0]) {
                "hChain" -> parseChain(ConstraintWidget.HORIZONTAL, state, margins, helper)
                "vChain" -> parseChain(ConstraintWidget.VERTICAL, state, margins, helper)
                "hGuideline" -> parseGuideline(ConstraintWidget.HORIZONTAL, state, margins, helper)
                "vGuideline" -> parseGuideline(ConstraintWidget.VERTICAL, state, margins, helper)
            }
        }
    }
}

fun parseChain(orientation: Int, state: State, margins: java.util.HashMap<String, Int>, helper: JSONArray) {
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

fun parseGuideline(orientation: Int, state: State, margins: java.util.HashMap<String, Int>, helper: JSONArray) {
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

fun parseBarrier(state: State, margins: HashMap<String, Int>,
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
    margins: HashMap<String, Int>,
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
                reference.alpha(element.getDouble(constraintName).toFloat())
            }
            "scaleX" -> {
                reference.scaleX(element.getDouble(constraintName).toFloat())
            }
            "scaleY" -> {
                reference.scaleY(element.getDouble(constraintName).toFloat())
            }
            "translationX" -> {
                reference.translationX(element.getDouble(constraintName).toFloat())
            }
            "translationY" -> {
                reference.translationY(element.getDouble(constraintName).toFloat())
            }
            "rotationX" -> {
                reference.rotationX(element.getDouble(constraintName).toFloat())
            }
            "rotationY" -> {
                reference.rotationY(element.getDouble(constraintName).toFloat())
            }
            "rotationZ" -> {
                reference.rotationZ(element.getDouble(constraintName).toFloat())
            }
            else -> {
                parseConstraint(state, margins, element, reference, constraintName)
            }
        }
    }
}

private fun parseConstraint(
    state: State,
    margins: HashMap<String, Int>,
    element: JSONObject,
    reference: ConstraintReference,
    constraintName: String
) {
    val constraint = element.optJSONArray(constraintName)
    if (constraint != null && constraint.length() > 1) {
        val target = constraint[0]
        val anchor = constraint[1]
        var margin: Int = 0
        if (constraint.length() > 2) {
            if (constraint[2] is String) {
                val resolvedMargin = margins[constraint[2]]
                if (resolvedMargin != null) {
                    margin = resolvedMargin
                }
            } else if (constraint[2] is Int) {
                margin = constraint[2] as Int
            }
        }
        margin = state.convertDimension(Dp(margin.toFloat()))

        val targetReference = if (target.toString().equals("parent")) {
            state.constraints(SolverState.PARENT)
        } else {
            state.constraints(target)
        }
        when (constraintName) {
            "circular" -> {
                var angle = 0f
                if (constraint[1] is Float) {
                    angle = constraint[1] as Float
                }
                if (constraint[1] is Int) {
                    angle = (constraint[1] as Int).toFloat()
                }
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
        reference.margin(margin)
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
