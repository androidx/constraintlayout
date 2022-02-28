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
import androidx.constraintlayout.core.parser.CLParser
import androidx.constraintlayout.core.parser.CLParsingException
import org.intellij.lang.annotations.Language

internal class JSONMotionScene(@Language("json5") content: String) : EditableJSONLayout(content),
    MotionScene {

    private val constraintSetsContent = HashMap<String, String>()
    private val transitionsContent = HashMap<String, CLObject>()
    private var forcedProgress: Float = Float.NaN

    init {
        // call parent init here so that hashmaps are created
        initialization()
    }

    ///////////////////////////////////////////////////////////////////////////
    // Accessors
    ///////////////////////////////////////////////////////////////////////////

    override fun setConstraintSetContent(name: String, content: String) {
        constraintSetsContent[name] = content
    }

    override fun setTransitionContent(name: String, content: String) {
        parseOrNull(content)?.let {
            if (!transitionsContent.containsKey(name)) {
                transitionsContent[name] = it
            }
        }
    }

    override fun setTransitionContentObject(name: String, parsedContent: CLObject) {
        transitionsContent[name] = parsedContent
    }

    override fun getConstraintSet(name: String): String? {
        return constraintSetsContent[name]
    }

    override fun getConstraintSet(index: Int): String? {
        return constraintSetsContent.values.elementAtOrNull(index)
    }

    override fun getTransition(name: String): String? {
        return transitionsContent[name]?.toJSON()
    }

    override fun getTransitionContentObject(name: String): CLObject? {
        return transitionsContent[name]
    }

    override fun getTransitionsNameSet(): Set<String> {
        return transitionsContent.keys.toSet() // return a copy
    }

    override fun getForcedProgress(): Float {
        return forcedProgress;
    }

    override fun resetForcedProgress() {
        forcedProgress = Float.NaN
    }

    ///////////////////////////////////////////////////////////////////////////
    // on update methods
    ///////////////////////////////////////////////////////////////////////////

    override fun onNewContent(content: String) {
        super.onNewContent(content)
        try {
            parseMotionSceneJSON(this, content);
        } catch (e: Exception) {
            // nothing (content might be invalid, sent by live edit)
        }
    }

    override fun onNewProgress(progress: Float) {
        forcedProgress = progress
        signalUpdate()
    }

}