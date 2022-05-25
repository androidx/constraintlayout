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

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MultiMeasureLayout
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.core.widgets.Optimizer
import java.util.EnumSet
import kotlinx.coroutines.channels.Channel

/**
 * Layout that interpolate its children layout given two sets of constraint and
 * a progress (from 0 to 1)
 */
@ExperimentalMotionApi
@Suppress("NOTHING_TO_INLINE")
@Composable
inline fun MotionLayout(
    start: ConstraintSet,
    end: ConstraintSet,
    transition: Transition? = null,
    progress: Float,
    debug: EnumSet<MotionLayoutDebugFlags> = EnumSet.of(MotionLayoutDebugFlags.NONE),
    modifier: Modifier = Modifier,
    optimizationLevel: Int = Optimizer.OPTIMIZATION_STANDARD,
    crossinline content: @Composable MotionLayoutScope.() -> Unit
) {
    MotionLayout(
        start = start,
        end = end,
        transition = transition,
        progress = progress,
        debug = debug,
        informationReceiver = null,
        modifier = modifier,
        optimizationLevel = optimizationLevel,
        content = content
    )
}

/**
 * Layout that animates the default transition of a [MotionScene] with a progress value (from 0 to
 * 1).
 */
@ExperimentalMotionApi
@Suppress("NOTHING_TO_INLINE")
@Composable
inline fun MotionLayout(
    motionScene: MotionScene,
    progress: Float,
    debug: EnumSet<MotionLayoutDebugFlags> = EnumSet.of(MotionLayoutDebugFlags.NONE),
    modifier: Modifier = Modifier,
    optimizationLevel: Int = Optimizer.OPTIMIZATION_STANDARD,
    crossinline content: @Composable (MotionLayoutScope.() -> Unit),
) {
    MotionLayoutCore(
        motionScene = motionScene,
        progress = progress,
        debug = debug,
        modifier = modifier,
        optimizationLevel = optimizationLevel,
        content = content
    )
}

/**
 * Layout that takes a MotionScene and animates by providing a [constraintSetName] to animate to.
 *
 * During recomposition, MotionLayout will interpolate from whichever ConstraintSet it is currently
 * in, to [constraintSetName].
 *
 * Typically the first value of [constraintSetName] should match the start ConstraintSet in the
 * default transition, or be null.
 *
 * Animation is run by [animationSpec], and will only start another animation once any other ones
 * are finished. Use [finishedAnimationListener] to know when a transition has stopped.
 */
@ExperimentalMotionApi
@Composable
inline fun MotionLayout(
    motionScene: MotionScene,
    constraintSetName: String? = null,
    animationSpec: AnimationSpec<Float> = tween<Float>(),
    debug: EnumSet<MotionLayoutDebugFlags> = EnumSet.of(MotionLayoutDebugFlags.NONE),
    modifier: Modifier = Modifier,
    optimizationLevel: Int = Optimizer.OPTIMIZATION_STANDARD,
    noinline finishedAnimationListener: (() -> Unit)? = null,
    crossinline content: @Composable (MotionLayoutScope.() -> Unit)
) {
    MotionLayoutCore(
        motionScene = motionScene,
        constraintSetName = constraintSetName,
        animationSpec = animationSpec,
        debug = debug,
        modifier = modifier,
        optimizationLevel = optimizationLevel,
        finishedAnimationListener = finishedAnimationListener,
        content = content
    )
}

@ExperimentalMotionApi
@Composable
inline fun MotionLayout(
    start: ConstraintSet,
    end: ConstraintSet,
    transition: androidx.constraintlayout.compose.Transition? = null,
    progress: Float,
    debug: EnumSet<MotionLayoutDebugFlags> = EnumSet.of(MotionLayoutDebugFlags.NONE),
    informationReceiver: LayoutInformationReceiver? = null,
    modifier: Modifier = Modifier,
    optimizationLevel: Int = Optimizer.OPTIMIZATION_STANDARD,
    crossinline content: @Composable MotionLayoutScope.() -> Unit
) {
    MotionLayoutCore(
        start = start,
        end = end,
        transition = transition as? TransitionImpl,
        progress = progress,
        debug = debug,
        informationReceiver = informationReceiver,
        modifier = modifier,
        optimizationLevel = optimizationLevel,
        content = content
    )
}

@OptIn(ExperimentalMotionApi::class)
@PublishedApi
@Composable
internal inline fun MotionLayoutCore(
    motionScene: MotionScene,
    constraintSetName: String? = null,
    animationSpec: AnimationSpec<Float> = tween<Float>(),
    debug: EnumSet<MotionLayoutDebugFlags> = EnumSet.of(MotionLayoutDebugFlags.NONE),
    modifier: Modifier = Modifier,
    optimizationLevel: Int = Optimizer.OPTIMIZATION_STANDARD,
    noinline finishedAnimationListener: (() -> Unit)? = null,
    crossinline content: @Composable (MotionLayoutScope.() -> Unit)
) {
    val needsUpdate = remember {
        mutableStateOf(0L)
    }
    motionScene.setUpdateFlag(needsUpdate)

    var usedDebugMode = debug
    if (motionScene.getForcedDrawDebug() != MotionLayoutDebugFlags.UNKNOWN) {
        usedDebugMode = EnumSet.of(motionScene.getForcedDrawDebug())
    }

    val transitionContent = remember(motionScene, needsUpdate.value) {
        motionScene.getTransition("default")
    }

    val transition: androidx.constraintlayout.compose.Transition? =
        transitionContent?.let { Transition(it) }

    val startId = transition?.getStartConstraintSetId() ?: "start"
    val endId = transition?.getEndConstraintSetId() ?: "end"

    val startContent = remember(motionScene, needsUpdate.value) {
        motionScene.getConstraintSet(startId) ?: motionScene.getConstraintSet(0)
    }
    val endContent = remember(motionScene, needsUpdate.value) {
        motionScene.getConstraintSet(endId) ?: motionScene.getConstraintSet(1)
    }

    val targetEndContent = remember(motionScene, constraintSetName) {
        constraintSetName?.let { motionScene.getConstraintSet(constraintSetName) }
    }

    if (startContent == null || endContent == null) {
        return
    }

    var start: ConstraintSet by remember(motionScene) {
        mutableStateOf(ConstraintSet(jsonContent = startContent))
    }
    var end: ConstraintSet by remember(motionScene) {
        mutableStateOf(ConstraintSet(jsonContent = endContent))
    }
    val targetConstraintSet = targetEndContent?.let {
        ConstraintSet(jsonContent = targetEndContent)
    }

    val progress = remember { Animatable(0f) }

    var animateToEnd by remember(motionScene) { mutableStateOf(true) }

    val channel = remember { Channel<ConstraintSet>(Channel.CONFLATED) }

    if (targetConstraintSet != null) {
        SideEffect {
            channel.trySend(targetConstraintSet)
        }

        LaunchedEffect(motionScene, channel) {
            for (constraints in channel) {
                val newConstraintSet = channel.tryReceive().getOrNull() ?: constraints
                val animTargetValue = if (animateToEnd) 1f else 0f
                val currentSet = if (animateToEnd) start else end
                if (newConstraintSet != currentSet) {
                    if (animateToEnd) {
                        end = newConstraintSet
                    } else {
                        start = newConstraintSet
                    }
                    progress.animateTo(animTargetValue, animationSpec)
                    animateToEnd = !animateToEnd
                    finishedAnimationListener?.invoke()
                }
            }
        }
    }

    val lastOutsideProgress = remember { mutableStateOf(0f) }
    val forcedProgress = motionScene.getForcedProgress()

    val currentProgress =
        if (!forcedProgress.isNaN() && lastOutsideProgress.value == progress.value) {
            forcedProgress
        } else {
            motionScene.resetForcedProgress()
            progress.value
        }

    lastOutsideProgress.value = progress.value

    MotionLayout(
        start = start,
        end = end,
        transition = transition,
        progress = currentProgress,
        debug = usedDebugMode,
        informationReceiver = motionScene as? JSONMotionScene,
        modifier = modifier,
        optimizationLevel = optimizationLevel,
        content = content
    )
}

@PublishedApi
@Composable
internal inline fun MotionLayoutCore(
    motionScene: MotionScene,
    progress: Float,
    debug: EnumSet<MotionLayoutDebugFlags> = EnumSet.of(MotionLayoutDebugFlags.NONE),
    modifier: Modifier = Modifier,
    optimizationLevel: Int = Optimizer.OPTIMIZATION_STANDARD,
    crossinline content: @Composable (MotionLayoutScope.() -> Unit),
) {
    val needsUpdate = remember {
        mutableStateOf(0L)
    }
    motionScene.setUpdateFlag(needsUpdate)

    var usedDebugMode = debug
    if (motionScene.getForcedDrawDebug() != MotionLayoutDebugFlags.UNKNOWN) {
        usedDebugMode = EnumSet.of(motionScene.getForcedDrawDebug())
    }

    val transitionContent = remember(motionScene, needsUpdate.value) {
        motionScene.getTransition("default")
    }

    val transition: androidx.constraintlayout.compose.Transition? =
        transitionContent?.let { Transition(it) }

    val startId = transition?.getStartConstraintSetId() ?: "start"
    val endId = transition?.getEndConstraintSetId() ?: "end"

    val startContent = remember(motionScene, needsUpdate.value) {
        motionScene.getConstraintSet(startId) ?: motionScene.getConstraintSet(0)
    }
    val endContent = remember(motionScene, needsUpdate.value) {
        motionScene.getConstraintSet(endId) ?: motionScene.getConstraintSet(1)
    }

    if (startContent == null || endContent == null) {
        return
    }

    val start = ConstraintSet(startContent)
    val end = ConstraintSet(endContent)

    var lastOutsideProgress by remember {
        mutableStateOf(0f)
    }
    val forcedProgress = motionScene.getForcedProgress()
    var usedProgress = progress
    if (!forcedProgress.isNaN() && lastOutsideProgress == progress) {
        usedProgress = forcedProgress
    } else {
        motionScene.resetForcedProgress()
    }
    @Suppress("UNUSED_VALUE")
    lastOutsideProgress = progress

    MotionLayoutCore(
        start = start,
        end = end,
        transition = transition as? TransitionImpl,
        progress = usedProgress,
        debug = usedDebugMode,
        informationReceiver = motionScene as? LayoutInformationReceiver,
        modifier = modifier,
        optimizationLevel = optimizationLevel,
        content = content
    )
}

@PublishedApi
@Composable
internal inline fun MotionLayoutCore(
    start: ConstraintSet,
    end: ConstraintSet,
    transition: TransitionImpl? = null,
    progress: Float,
    debug: EnumSet<MotionLayoutDebugFlags> = EnumSet.of(MotionLayoutDebugFlags.NONE),
    informationReceiver: LayoutInformationReceiver? = null,
    modifier: Modifier = Modifier,
    optimizationLevel: Int = Optimizer.OPTIMIZATION_STANDARD,
    crossinline content: @Composable MotionLayoutScope.() -> Unit
) {
    val measurer = remember { MotionMeasurer() }
    val progressState = remember { mutableStateOf(0f) }
    val scope = remember { MotionLayoutScope(measurer, progressState) }
    progressState.value = progress
    val measurePolicy =
        rememberMotionLayoutMeasurePolicy(
            optimizationLevel,
            debug,
            0,
            start,
            end,
            transition,
            progressState,
            measurer
        )

    measurer.addLayoutInformationReceiver(informationReceiver)

    val forcedScaleFactor = measurer.forcedScaleFactor

    var debugModifications: Modifier = Modifier
    if (!debug.contains(MotionLayoutDebugFlags.NONE) || !forcedScaleFactor.isNaN()) {
        if (!forcedScaleFactor.isNaN()) {
            debugModifications = debugModifications.scale(forcedScaleFactor)
        }
        debugModifications = debugModifications.drawBehind {
            with(measurer) {
                if (!forcedScaleFactor.isNaN()) {
                    drawDebugBounds(forcedScaleFactor)
                }
                if (!debug.contains(MotionLayoutDebugFlags.NONE)) {
                    drawDebug()
                }
            }
        }
    }
    @Suppress("DEPRECATION")
    (MultiMeasureLayout(
        modifier = modifier
            .then(debugModifications)
            .motionPointerInput(measurePolicy, progressState, measurer)
            .semantics { designInfoProvider = measurer },
        measurePolicy = measurePolicy,
        content = { scope.content() }
    ))
}

@ExperimentalMotionApi
@Composable
inline fun MotionLayout(
    modifier: Modifier = Modifier,
    optimizationLevel: Int = Optimizer.OPTIMIZATION_STANDARD,
    motionLayoutState: MotionLayoutState,
    motionScene: MotionScene,
    crossinline content: @Composable MotionLayoutScope.() -> Unit
) {
    MotionLayoutCore(
        modifier = modifier,
        optimizationLevel = optimizationLevel,
        motionLayoutState = motionLayoutState as MotionLayoutStateImpl,
        motionScene = motionScene,
        content = content
    )
}

@PublishedApi
@ExperimentalMotionApi
@Composable
internal inline fun MotionLayoutCore(
    modifier: Modifier = Modifier,
    optimizationLevel: Int = Optimizer.OPTIMIZATION_STANDARD,
    motionLayoutState: MotionLayoutStateImpl,
    motionScene: MotionScene,
    crossinline content: @Composable MotionLayoutScope.() -> Unit
) {
    val measurer = remember(motionLayoutState, motionScene) { MotionMeasurer() }
    val scope = remember(motionLayoutState, motionScene) {
        MotionLayoutScope(measurer, motionLayoutState.progressState)
    }

    val transitionContent = remember(motionScene) {
        motionScene.getTransition("default")
    }

    val transition: androidx.constraintlayout.compose.Transition? =
        transitionContent?.let { Transition(it) }

    val startId = transition?.getStartConstraintSetId() ?: "start"
    val endId = transition?.getEndConstraintSetId() ?: "end"

    val startContent = remember(motionScene) {
        motionScene.getConstraintSet(startId) ?: motionScene.getConstraintSet(0)
    }
    val endContent = remember(motionScene) {
        motionScene.getConstraintSet(endId) ?: motionScene.getConstraintSet(1)
    }

    if (startContent == null || endContent == null) {
        return
    }

    val start = ConstraintSet(startContent)
    val end = ConstraintSet(endContent)
    val debug = EnumSet.of(motionLayoutState.debugMode)

    val measurePolicy =
        rememberMotionLayoutMeasurePolicy(
            optimizationLevel,
            debug,
            motionScene,
            start,
            end,
            transition as? TransitionImpl,
            motionLayoutState.progressState,
            measurer
        )

    val forcedScaleFactor = measurer.forcedScaleFactor

    var debugModifications: Modifier = Modifier
    if (!debug.contains(MotionLayoutDebugFlags.NONE) || !forcedScaleFactor.isNaN()) {
        if (!forcedScaleFactor.isNaN()) {
            debugModifications = debugModifications.scale(forcedScaleFactor)
        }
        debugModifications = debugModifications.drawBehind {
            with(measurer) {
                if (!forcedScaleFactor.isNaN()) {
                    drawDebugBounds(forcedScaleFactor)
                }
                if (!debug.contains(MotionLayoutDebugFlags.NONE)) {
                    drawDebug()
                }
            }
        }
    }
    @Suppress("DEPRECATION")
    (MultiMeasureLayout(
        modifier = modifier
            .then(debugModifications)
            .motionPointerInput(measurePolicy, motionLayoutState.motionProgress, measurer)
            .semantics { designInfoProvider = measurer },
        measurePolicy = measurePolicy,
        content = { scope.content() }
    ))
}

@LayoutScopeMarker
class MotionLayoutScope @PublishedApi internal constructor(
    measurer: MotionMeasurer,
    private val progressState: State<Float>
) {
    private var myMeasurer = measurer

    class MotionProperties internal constructor(
        id: String,
        tag: String?,
        measurer: MotionMeasurer
    ) {
        private var myId = id
        private var myTag = tag
        private var myMeasurer = measurer

        fun id(): String {
            return myId
        }

        fun tag(): String? {
            return myTag
        }

        fun color(name: String): Color {
            return myMeasurer.getCustomColor(myId, name)
        }

        fun float(name: String): Float {
            return myMeasurer.getCustomFloat(myId, name)
        }

        fun int(name: String): Int {
            return myMeasurer.getCustomFloat(myId, name).toInt()
        }

        fun distance(name: String): Dp {
            return myMeasurer.getCustomFloat(myId, name).dp
        }

        fun fontSize(name: String): TextUnit {
            return myMeasurer.getCustomFloat(myId, name).sp
        }
    }

    @Composable
    fun motionProperties(id: String): State<MotionProperties> =
    // TODO: Make properly observable, passing the progressState as a remember 'key' is a hack
        //  to make it work as observable
        remember(id, progressState.value) {
            mutableStateOf(MotionProperties(id, null, myMeasurer))
        }

    /**
     * FIXME: This implementation is not observable
     */
    fun motionProperties(id: String, tag: String): MotionProperties {
        return MotionProperties(id, tag, myMeasurer)
    }

    /**
     * FIXME: This implementation is not observable
     */
    fun motionColor(id: String, name: String): Color {
        return myMeasurer.getCustomColor(id, name)
    }

    /**
     * FIXME: This implementation is not observable
     */
    fun motionFloat(id: String, name: String): Float {
        return myMeasurer.getCustomFloat(id, name)
    }

    /**
     * FIXME: This implementation is not observable
     */
    fun motionInt(id: String, name: String): Int {
        return myMeasurer.getCustomFloat(id, name).toInt()
    }

    /**
     * FIXME: This implementation is not observable
     */
    fun motionDistance(id: String, name: String): Dp {
        return myMeasurer.getCustomFloat(id, name).dp
    }

    /**
     * FIXME: This implementation is not observable
     */
    fun motionFontSize(id: String, name: String): TextUnit {
        return myMeasurer.getCustomFloat(id, name).sp
    }
}

enum class MotionLayoutDebugFlags {
    NONE,
    SHOW_ALL,
    UNKNOWN
}

@Composable
@PublishedApi
internal fun rememberMotionLayoutMeasurePolicy(
    optimizationLevel: Int,
    debug: EnumSet<MotionLayoutDebugFlags>,
    needsUpdate: Long,
    constraintSetStart: ConstraintSet,
    constraintSetEnd: ConstraintSet,
    transition: TransitionImpl?,
    progress: MutableState<Float>,
    measurer: MotionMeasurer
) = remember(
    optimizationLevel,
    debug,
    needsUpdate,
    constraintSetStart,
    constraintSetEnd,
    transition
) {
    measurer.initWith(constraintSetStart, constraintSetEnd, transition, progress.value)
    MeasurePolicy { measurables, constraints ->
        val layoutSize = measurer.performInterpolationMeasure(
            constraints,
            layoutDirection,
            constraintSetStart,
            constraintSetEnd,
            transition,
            measurables,
            optimizationLevel,
            progress.value,
            this
        )
        layout(layoutSize.width, layoutSize.height) {
            with(measurer) {
                performLayout(measurables)
            }
        }
    }
}

@Composable
@PublishedApi
internal fun rememberMotionLayoutMeasurePolicy(
    optimizationLevel: Int,
    debug: EnumSet<MotionLayoutDebugFlags>,
    motionScene: MotionScene,
    constraintSetStart: ConstraintSet,
    constraintSetEnd: ConstraintSet,
    transition: TransitionImpl?,
    progress: State<Float>,
    measurer: MotionMeasurer
) = remember(
    optimizationLevel,
    debug,
    motionScene,
    constraintSetStart,
    constraintSetEnd,
    transition
) {
    measurer.initWith(constraintSetStart, constraintSetEnd, transition, progress.value)
    MeasurePolicy { measurables, constraints ->
        val layoutSize = measurer.performInterpolationMeasure(
            constraints,
            layoutDirection,
            constraintSetStart,
            constraintSetEnd,
            transition,
            measurables,
            optimizationLevel,
            progress.value,
            this
        )
        layout(layoutSize.width, layoutSize.height) {
            with(measurer) {
                performLayout(measurables)
            }
        }
    }
}
