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

package androidx.constraintlayout.compose

import androidx.constraintlayout.core.parser.CLKey
import androidx.constraintlayout.core.parser.CLParser
import androidx.constraintlayout.core.parser.CLParsingException
import androidx.constraintlayout.core.state.Transition
import org.intellij.lang.annotations.Language
import java.util.ArrayList
import java.util.HashMap

internal class JSONConstraintSet(
    @Language("json5") content: String,
    @Language("json5") overrideVariables: String? = null,
    override val extendFrom: ConstraintSet? = null
) : EditableJSONLayout(content), DerivedConstraintSet {
    private val overridedVariables = HashMap<String, Float>()
    private val overrideVariables = overrideVariables

    init {
        initialization()
    }

    override fun equals(other: Any?): Boolean {
        if (other is JSONConstraintSet) {
            return this.getCurrentContent() == other.getCurrentContent()
        }
        return false
    }

    // Only called by MotionLayout in MotionMeasurer
    override fun applyTo(transition: Transition, type: Int) {
        val layoutVariables = LayoutVariables()
        applyLayoutVariables(layoutVariables)
        parseJSON(getCurrentContent(), transition, type)
    }

    fun emitDesignElements(designElements: ArrayList<DesignElement>) {
        try {
            designElements.clear()
            parseDesignElementsJSON(getCurrentContent(), designElements)
        } catch (e : Exception) {
            // nothing (content might be invalid, sent by live edit)
        }
    }

    // Called by both MotionLayout & ConstraintLayout measurers
    override fun applyToState(state: State) {
        val layoutVariables = LayoutVariables()
        applyLayoutVariables(layoutVariables)
        // TODO: Need to better handle half parsed JSON and/or incorrect states.
        try {
            parseJSON(getCurrentContent(), state, layoutVariables)
        } catch (e : Exception) {
            // nothing (content might be invalid, sent by live edit)
        }
    }

    override fun override(name: String, value: Float): ConstraintSet {
        overridedVariables[name] = value
        return this
    }

    private fun applyLayoutVariables(layoutVariables: LayoutVariables) {
        if (overrideVariables != null) {
            try {
                val variables = CLParser.parse(overrideVariables)
                for (i in 0 until variables.size()) {
                    val key = variables[i] as CLKey
                    val variable = key.value.float
                    // TODO: allow arbitrary override, not just float values
                    layoutVariables.putOverride(key.content(), variable)
                }
            } catch (e: CLParsingException) {
                System.err.println("exception: " + e)
            }
        }
        for (name in overridedVariables.keys) {
            layoutVariables.putOverride(name, overridedVariables[name]!!)
        }
    }
}