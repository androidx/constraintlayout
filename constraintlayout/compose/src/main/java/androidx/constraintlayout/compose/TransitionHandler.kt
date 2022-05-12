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

import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Velocity
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Helper class that handles the interactions between Compose and
 * [androidx.constraintlayout.core.state.Transition].
 */
@PublishedApi
internal class TransitionHandler(
    private val motionMeasurer: MotionMeasurer,
    private val progressState: MutableState<Float>
    ) {
    private val transition: androidx.constraintlayout.core.state.Transition
        get() = motionMeasurer.transition

    private var newProgress: Float = -1f

    /**
     * The [progressState] is updated based on the [Offset] from a single drag event.
     */
    fun updateProgressOnDrag(dragAmount: Offset) {
        val progressDelta = transition.dragToProgress(
            progressState.value,
            motionMeasurer.layoutCurrentWidth,
            motionMeasurer.layoutCurrentHeight,
            dragAmount.x,
            dragAmount.y
        )
        newProgress = progressState.value + progressDelta
        newProgress = max(min(newProgress, 1f), 0f)
        progressState.value = newProgress
    }

    /**
     * Called when a swipe event ends, sets up the underlying Transition with the [velocity] of the
     * swipe at the given [timeNanos].
     */
    fun onTouchUp(timeNanos: Long, velocity: Velocity) {
        transition.setTouchUp(progressState.value, timeNanos, velocity.x, velocity.y)
    }

    /**
     * Call to update the [progressState] after a swipe has ended and as long as there are no other
     * touch gestures.
     */
    fun updateProgressWhileTouchUp(timeNanos: Long) {
        newProgress =  transition.getTouchUpProgress(timeNanos)
        progressState.value = newProgress
    }

    /**
     * Returns true if the progress is still expected to be updated by [updateProgressWhileTouchUp].
     */
    fun pendingProgressWhileTouchUp(): Boolean {
        return transition.isTouchNotDone(newProgress);
    }
}