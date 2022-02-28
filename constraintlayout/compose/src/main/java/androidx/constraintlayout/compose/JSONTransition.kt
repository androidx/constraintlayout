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

package androidx.constraintlayout.compose

import androidx.constraintlayout.core.parser.CLObject
import androidx.constraintlayout.core.parser.CLParsingException
import androidx.constraintlayout.core.state.TransitionParser

internal class JSONTransition(private val parsedContent: CLObject) : Transition {
    override fun applyTo(transition: androidx.constraintlayout.core.state.Transition, type: Int) {
        try {
            TransitionParser.parse(parsedContent, transition)
        } catch (e: CLParsingException) {
            System.err.println("Error parsing JSON $e")
        }
    }

    override fun getStartConstraintSetId(): String {
        return parsedContent.getStringOrNull("from") ?: "start"
    }

    override fun getEndConstraintSetId(): String {
        return parsedContent.getStringOrNull("to") ?: "end"
    }
}