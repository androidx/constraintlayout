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
import androidx.constraintlayout.core.state.Dimension
import androidx.constraintlayout.core.state.helpers.GuidelineReference
import org.json.JSONObject

internal val DEBUG = false

internal fun parseJSON(content: String, state: State) {
    val margins = HashMap<String, Int>()
    val json = JSONObject(content)
    val elements = json.names() ?: return
    (0 until elements.length()).forEach { i ->
        val elementName = elements[i].toString()
        val element = json[elementName]
        if (DEBUG) {
            System.out.println("element <$elementName = $element> " + element.javaClass)
        }
        if (element is Int) {
            margins[elementName] = element
        } else if (element is JSONObject) {
            var type = lookForType(element)
            if (type != null && type.equals("guideline")) {
                parseGuideline(state, elementName, element)
            } else if (type == null) {
                parseWidget(state, margins, elementName, element)
            }
        }
    }
}

fun parseGuideline(state: State, elementName: String, element: JSONObject) {
    val reference = state.constraints(elementName)
    val constraints = element.names() ?: return
    state.verticalGuideline(elementName)
    var guidelineReference = reference.facade as GuidelineReference
    (0 until constraints.length()).forEach { i ->
        val constraintName = constraints[i].toString()
        when (constraintName) {
            "start" -> {
                var margin = state.convertDimension(
                    Dp(
                        element.getInt(constraintName).toFloat()
                    )
                )
                guidelineReference.start(margin)
            }
            "end" -> {
                var margin = state.convertDimension(
                    Dp(
                        element.getInt(constraintName).toFloat()
                    )
                )
                guidelineReference.end(margin)
            }
            "percent" -> {
                guidelineReference.percent(
                    element.getDouble(
                        constraintName
                    ).toFloat()
                )
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
                reference.width = Dimension.Fixed(
                    state.convertDimension(
                        Dp(
                            element.getInt(constraintName).toFloat()
                        )
                    )
                )
            }
            "height" -> {
                reference.height = Dimension.Fixed(
                    state.convertDimension(
                        Dp(
                            element.getInt(constraintName).toFloat()
                        )
                    )
                )
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
        }
        val constraint = element.optJSONArray(constraintName)
        if (constraint != null) {
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
            System.out.println("margin used $margin")

            val targetReference = if (target.toString().equals("parent")) {
                state.constraints(SolverState.PARENT)
            } else {
                state.constraints(target)
            }
            when (constraintName) {
                "centerHorizontally" -> {

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
            // direct value?
        }

    }
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
