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

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.movableContentWithReceiverOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LookaheadLayout
import androidx.compose.ui.layout.LookaheadLayoutScope
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.node.Ref
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.round
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import androidx.constraintlayout.core.state.Transition.WidgetState
import androidx.constraintlayout.core.widgets.ConstraintWidget
import kotlin.math.abs
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
inline fun MotionLayout(
    modifier: Modifier = Modifier,
    transition: Transition,
    crossinline content: @Composable MovableMotionLayoutScope.() -> Unit
) {
    val policy = remember {
        MeasurePolicy { measurables, constraints ->
            val placeables = measurables.fastMap { it.measure(constraints) }
            val maxWidth = placeables.maxOf { it.width }
            val maxHeight = placeables.maxOf { it.height }
            layout(maxWidth, maxHeight) {
                placeables.fastForEach { it.place(0, 0) }
            }
        }
    }
    LookaheadLayout(
        modifier = modifier,
        content = {
            val scope = remember {
                MovableMotionLayoutScope(
                    lookaheadLayoutScope = this,
                    transition = transition as TransitionImpl
                )
            }
            scope.content()
        },
        measurePolicy = policy
    )
}

@OptIn(ExperimentalComposeUiApi::class)
class MovableMotionLayoutScope(
    lookaheadLayoutScope: LookaheadLayoutScope,
    transition: Transition
) : LookaheadLayoutScope by lookaheadLayoutScope {
    private var nextId: Int = 1000
    private val transitionImpl: TransitionImpl = transition as TransitionImpl
    private val transitionState = kotlin.run {
        androidx.constraintlayout.core.state.Transition().apply {
            transitionImpl.applyAllTo(this, 0)
        }
    }
    private var lastId: Int = nextId

    @Suppress("NOTHING_TO_INLINE")
    @SuppressLint("ComposableNaming") // it's easier to understand as a regular extension function
    @Composable
    inline fun List<@Composable MovableMotionLayoutScope.(index: Int) -> Unit>.emit() {
        forEachIndexed { index, content ->
            content(index)
        }
    }

    /**
     * Animate an item within a scrollable list.
     *
     * Will not trigger animations if the position only changes in one axis. To avoid animating
     * while scrolling the list.
     */
    fun Modifier.motionItem(): Modifier = motionId(layoutId = nextId++, ignoreAxisChanges = true)

    fun Modifier.motionId(layoutId: Any): Modifier = motionId(
        layoutId = layoutId,
        ignoreAxisChanges = false
    )

    private fun Modifier.motionId(layoutId: Any, ignoreAxisChanges: Boolean = false): Modifier =
        composed {
            val startWidget =
                remember { ConstraintWidget().apply { stringId = layoutId.toString() } }
            val endWidget = remember { ConstraintWidget().apply { stringId = layoutId.toString() } }
            val widgetState: WidgetState = remember {
                transitionState.getWidgetState(layoutId.toString(), null, 0).apply {
                    update(startWidget, 0)
                    update(endWidget, 1)
                }
            }
            // TODO: Optimize all animated items at a time under a single Animatable. E.g: If after
            //  a state change, 10 different items changed, animate them using one Animatable
            //  object, as opposed to running 10 separate Animatables doing the same thing,
            //  measure/layout calls in the LookAheadLayout MeasurePolicy might provide the clue to
            //  understand the lifecycle of intermediateLayout calls across multiple Measurables.
            val progressAnimation = remember { Animatable(0f) }
            var targetBounds: IntRect by remember { mutableStateOf(IntRect.Zero) }

            fun commitLookAheadChanges(position: IntOffset, size: IntSize) {
                targetBounds = IntRect(position, size)
            }

            var placementOffset: IntOffset by remember { mutableStateOf(IntOffset.Zero) }
            var targetOffset: IntOffset? by remember { mutableStateOf(null) }
            var targetSize: IntSize? by remember { mutableStateOf(null) }
            val lastSize: Ref<IntSize> = remember { Ref<IntSize>().apply { value = null } }
            val parentSize: Ref<IntSize> =
                remember { Ref<IntSize>().apply { value = IntSize.Zero } }
            val lastPosition: Ref<IntOffset> = remember { Ref<IntOffset>().apply { value = null } }

            LaunchedEffect(Unit) {
                launch {
                    snapshotFlow {
                        targetBounds
                    }.collect { target ->
                        if (target != IntRect.Zero) {
                            if (nextId != lastId) {
                                transitionImpl.count = nextId - lastId
                                lastId = nextId
                                transitionImpl.applyAllTo(transitionState, 0)
                            }
//                        if (lastSize.value != null && lastSize.value != IntSize.Zero) {
                            // I forgot :)
                            if (lastSize.value != null) {
                                endWidget.applyBounds(target)
                                widgetState.update(startWidget, 0)
                                widgetState.update(endWidget, 1)
                                val newPosition = target.topLeft
                                val xAxisChanged: Boolean
                                val yAxisChanged: Boolean
                                var skipAnimation = false
                                if (ignoreAxisChanges) {
                                    val positionDelta = newPosition - lastPosition.value!!
                                    xAxisChanged = positionDelta.x != 0
                                    yAxisChanged = positionDelta.y != 0
                                    skipAnimation = xAxisChanged xor yAxisChanged
                                }
                                if (!skipAnimation) {
                                    if (progressAnimation.targetValue == 1f) {
                                        progressAnimation.animateTo(0f, tween(2000))
                                    } else {
                                        progressAnimation.animateTo(1f, tween(2000))
                                    }
                                }
                            }
                            lastSize.value = target.size
                            lastPosition.value = target.topLeft
                            startWidget.applyBounds(target)
                        } else {
                            startWidget.applyBounds(target)
                        }
                    }
                }
            }
            this
                .onPlaced { lookaheadScopeCoordinates, layoutCoordinates ->
                    parentSize.value = lookaheadScopeCoordinates.size
                    val localPosition = lookaheadScopeCoordinates
                        .localPositionOf(
                            layoutCoordinates,
                            Offset.Zero
                        )
                        .round()
                    val lookAheadPosition = lookaheadScopeCoordinates
                        .localLookaheadPositionOf(
                            layoutCoordinates
                        )
                        .round()
                    targetOffset = lookAheadPosition
                    placementOffset = localPosition
                    commitLookAheadChanges(targetOffset!!, targetSize!!)
                }
                .intermediateLayout { measurable, constraints, lookaheadSize ->
                    targetSize = lookaheadSize
                    if (targetBounds == IntRect.Zero) {
                        // Unset, this is first measure
                        val newConstraints =
                            Constraints.fixed(lookaheadSize.width, lookaheadSize.height)
                        val placeable = measurable.measure(newConstraints)
                        layout(placeable.width, placeable.height) {
                            placeable.place(targetOffset!! - placementOffset)
                        }
                    } else {
                        // Following measures
                        val width: Int
                        val height: Int
                        if (progressAnimation.isRunning) {
                            val fraction =
                                1.0f - abs(progressAnimation.value - progressAnimation.targetValue)
                            widgetState.interpolate(
                                parentSize.value!!.width,
                                parentSize.value!!.height,
                                fraction,
                                transitionState
                            )
                            width = widgetState
                                .getFrame(2)
                                .width()
                            height = widgetState
                                .getFrame(2)
                                .height()
                        } else {
                            width = targetBounds.width
                            height = targetBounds.height
                        }
                        val animatedConstraints = Constraints.fixed(width, height)
                        val placeable = measurable.measure(animatedConstraints)
                        layout(placeable.width, placeable.height) {
                            if (progressAnimation.isRunning) {
                                placeWithFrameTransform(
                                    placeable,
                                    widgetState.getFrame(2),
                                    placementOffset
                                )
                            } else {
                                val (x, y) = lastPosition.value!! - placementOffset
                                placeable.place(x, y)
                            }
                        }
                    }
                }
        }
}

internal fun ConstraintWidget.applyBounds(rect: IntRect) {
    val position = rect.topLeft
    x = position.x
    y = position.y
    width = rect.width
    height = rect.height
}

@Composable
fun rememberMotionMovable(content: @Composable MovableMotionLayoutScope.() -> Unit): @Composable MovableMotionLayoutScope.() -> Unit {
    return remember {
        movableContentOf(content)
    }
}

@Composable
fun rememberMotionMovableListItems(
    count: Int,
    content: @Composable MovableMotionLayoutScope.(index: Int) -> Unit
): List<@Composable MovableMotionLayoutScope.(index: Int) -> Unit> {
    val items = remember(count) {
        val list = mutableListOf<@Composable MovableMotionLayoutScope.(index: Int) -> Unit>()
        for (i in 0 until count) {
            list.add(movableContentWithReceiverOf<MovableMotionLayoutScope, Int>(content))
        }
        return@remember list
    }
    return items
}