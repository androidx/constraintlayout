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

package androidx.constraintlayout.compose.motion.statemanager

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.JSONConstraintSet
import androidx.constraintlayout.compose.JSONTransition
import androidx.constraintlayout.compose.MotionScene

/**
 * Manages the layout and animation state of MotionLayout across recompositions.
 *
 * @param scene Defines the possible layouts and animation parameters
 * @param animatableProgress Effectively a callback to drive the animations
 * @param animationSpec Defines the behavior of the progress curve over time
 */
@PublishedApi
internal class StateToStateMotionManager(
    private val scene: MotionScene,
    private val animatableProgress: Animatable<Float, AnimationVector1D>,
    private val animationSpec: AnimationSpec<Float>
) {
    // TODO: Make the Animatable progress a property of this class, expose the value with a Compose
    //  observable delegate

    /**
     * [ConstraintSet] object to name mapping.
     *
     * Meant to be used so that the objects are instantiated once and if/when they are needed.
     */
    // TODO: Change to a more appropriate caching (cheaper, lighter, etc)
    private val lazyConstraintSetsByName = HashMap<String, ConstraintSet>()

    /**
     * Transition object to name mapping.
     *
     * Meant to be used so that the objects are instantiated once and if/when they are needed.
     */
    // TODO: Change to a more appropriate caching (cheaper, lighter, etc)
    private val lazyTransitionsByName =
        HashMap<String, androidx.constraintlayout.compose.Transition>()

    /**
     * Mapping for `from` `to` declarations to their corresponding Transition name.
     *
     * This mapping exists to avoid
     */
    private val targetsToTransitionName =
        HashMap<Pair<String, String>, String>()

    /**
     * True if the current state of the Layout is on the End [ConstraintSet].
     */
    private var atEnd: Boolean = false

    private var transitionsNames: Set<String> = scene.getTransitionsNameSet()
    private val defaultTransition = getTransitionInstance("default")

    private var from: Pair<String, ConstraintSet>
    private var to: Pair<String, ConstraintSet>

    /**
     * Start [ConstraintSet] to be used in MotionLayout
     */
    val startConstraintSet
        get() = from.second

    /**
     * End [ConstraintSet] to be used in MotionLayout.
     */
    val endConstraintSet
        get() = to.second

    /**
     * Transition to be used in MotionLayout.
     */
    var transition: androidx.constraintlayout.compose.Transition

    init {
        transitionsNames.mapNotNull { transitionName ->
            // Map Transition names with their 'from' and 'to' definitions
            val parsedObject =
                scene.getTransitionContentObject(transitionName) ?: return@mapNotNull null
            if (parsedObject.has("from") && parsedObject.has("to")) {
                val fromId = parsedObject.getStringOrNull("from") ?: return@mapNotNull null
                val toId = parsedObject.getStringOrNull("to") ?: return@mapNotNull null
                return@mapNotNull Pair(Pair(fromId, toId), transitionName)
            } else {
                return@mapNotNull null
            }
        }.toMap(targetsToTransitionName)

        // Define the initial state of the Layout
        val fromId = defaultTransition.getStartConstraintSetId()
        val toId = defaultTransition.getEndConstraintSetId()
        from = fromId to getConstraintSetInstance(fromId)
        to = toId to getConstraintSetInstance(toId)
        transition = defaultTransition
    }

    /**
     * Animate to the ConstraintSet defined by [targetStateName].
     *
     * The Transition applied to the animation depends on the from/to definitions of each
     * Transition. The following rules apply on this order:
     * - Transition that exactly matches the current (from) and [targetStateName] (to) ConstraintSet
     * - Transition that matches the current state as the end (to) and the [targetStateName] as the
     * start (from). In this case, the animation is played in reverse (1 -> 0).
     * - Default Transition is used, regardless of from/to values. Animated forwards (0 -> 1).
     */
    suspend fun setTo(targetStateName: String?) {
        if (targetStateName == null) {
            return
        }
        if (atEnd && targetStateName == to.first) {
            return
        } else if (!atEnd && targetStateName == from.first) {
            return
        }

        val targetInstance: ConstraintSet = try {
            getConstraintSetInstance(targetStateName)
        } catch (e: IllegalStateException) {
            System.err.println(e.message) // Fail safely
            return
        }

        val lastFrom = from.first
        val lastTo = to.first

        if (atEnd) {
            from = to
        }
        to = targetStateName to targetInstance

        val mayReverse = lastFrom == to.first && lastTo == from.first

        // If we are at the end, we may potentially animate in reverse, but we need to check
        // that there are no matching Transitions for the next state
        val doReverse =
            atEnd && mayReverse && !targetsToTransitionName.containsKey(Pair(from.first, to.first))

        var progressInitialValue = 0.0f
        var progressTargetValue = 1.0f

        if (doReverse) {
            // Setup to animate on reverse
            val aux = to
            to = from
            from = aux
            progressInitialValue = 1.0f
            progressTargetValue = 0.0f
        }

        // Update transition
        transition =
            getTransitionInstance(targetsToTransitionName[Pair(from.first, to.first)] ?: "default")


        if (animatableProgress.value != progressInitialValue) {
            animatableProgress.snapTo(progressInitialValue)
        }
        animatableProgress.animateTo(
            targetValue = progressTargetValue,
            animationSpec = animationSpec
        )

        // Update indicator after finishing the animation
        atEnd = !doReverse
    }

    private fun getTransitionInstance(transitionName: String): androidx.constraintlayout.compose.Transition {
        return lazyTransitionsByName.getOrPut(transitionName) {
            val parsedContent = scene.getTransitionContentObject(transitionName)
            checkNotNull(parsedContent) { "Issue with content of Transition: $transitionName" }
            JSONTransition(parsedContent)
        }
    }

    private fun getConstraintSetInstance(constraintSetName: String): ConstraintSet {
        return lazyConstraintSetsByName.getOrPut(constraintSetName) {
            val content = scene.getConstraintSet(constraintSetName)
            check(content != null) { "Content missing for ConstraintSet: $constraintSetName" }
            JSONConstraintSet(content)
        }
    }
}