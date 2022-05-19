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

import android.annotation.SuppressLint
import android.graphics.Matrix
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.Immutable
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.MultiMeasureLayout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach
import androidx.constraintlayout.core.motion.Motion
import androidx.constraintlayout.core.parser.CLObject
import androidx.constraintlayout.core.parser.CLParser
import androidx.constraintlayout.core.parser.CLParsingException
import androidx.constraintlayout.core.state.ConstraintSetParser.parseMotionSceneJSON
import androidx.constraintlayout.core.state.CoreMotionScene
import androidx.constraintlayout.core.state.CorePixelDp
import androidx.constraintlayout.core.state.Dimension
import androidx.constraintlayout.core.state.Transition
import androidx.constraintlayout.core.state.TransitionParser
import androidx.constraintlayout.core.state.WidgetFrame
import androidx.constraintlayout.core.widgets.Optimizer
import java.util.EnumSet
import kotlinx.coroutines.channels.Channel
import org.intellij.lang.annotations.Language

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
    transition: androidx.constraintlayout.compose.Transition? = null,
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
inline fun  MotionLayout(
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

@OptIn(ExperimentalComposeApi::class)
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

@OptIn(ExperimentalComposeApi::class)
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
@OptIn(ExperimentalComposeApi::class)
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

/**
 * Information for MotionLayout to animate between multiple [ConstraintSet]s.
 */
@Immutable
interface MotionScene : CoreMotionScene {
    fun setUpdateFlag(needsUpdate: MutableState<Long>)
    fun getForcedDrawDebug(): MotionLayoutDebugFlags
}

internal class JSONMotionScene(@Language("json5") content: String) : EditableJSONLayout(content),
    MotionScene {

    private val constraintSetsContent = HashMap<String, String>()
    private val transitionsContent = HashMap<String, String>()
    private var forcedProgress: Float = Float.NaN

    init {
        // call parent init here so that hashmaps are created
        initialization()
    }

    // region Accessors
    override fun setConstraintSetContent(name: String, content: String) {
        constraintSetsContent[name] = content
    }

    override fun setTransitionContent(name: String, content: String) {
        transitionsContent[name] = content
    }

    override fun getConstraintSet(name: String): String? {
        return constraintSetsContent[name]
    }

    override fun getConstraintSet(index: Int): String? {
        return constraintSetsContent.values.elementAtOrNull(index)
    }

    override fun getTransition(name: String): String? {
        return transitionsContent[name]
    }

    override fun getForcedProgress(): Float {
        return forcedProgress
    }

    override fun resetForcedProgress() {
        forcedProgress = Float.NaN
    }
    // endregion

    // region On Update Methods
    override fun onNewContent(content: String) {
        super.onNewContent(content)
        try {
            parseMotionSceneJSON(this, content)
        } catch (e: Exception) {
            // nothing (content might be invalid, sent by live edit)
        }
    }

    override fun onNewProgress(progress: Float) {
        forcedProgress = progress
        signalUpdate()
    }
    // endregion
}

/**
 * Parses the given JSON5 into a [MotionScene].
 *
 * See the official [Github Wiki](https://github.com/androidx/constraintlayout/wiki/Compose-MotionLayout-JSON-Syntax) to learn the syntax.
 */
@SuppressLint("ComposableNaming")
@Composable
fun MotionScene(@Language("json5") content: String): MotionScene {
    return remember(content) {
        JSONMotionScene(content)
    }
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

/**
 * Defines interpolation parameters between two [ConstraintSet]s.
 */
@Immutable
interface Transition {
    fun getStartConstraintSetId(): String
    fun getEndConstraintSetId(): String
}

/**
 * Subclass of [Transition] for internal use.
 *
 * Used to reduced the exposed API from [Transition].
 */
internal class TransitionImpl(
    private val parsedTransition: CLObject,
    private val pixelDp: CorePixelDp
) : androidx.constraintlayout.compose.Transition {

    /**
     * Applies all Transition properties to [transition].
     */
    fun applyAllTo(transition: Transition, type: Int) {
        try {
            TransitionParser.parse(parsedTransition, transition, pixelDp)
        } catch (e: CLParsingException) {
            Log.e("CML", "Error parsing JSON $e")
        }
    }

    /**
     * Applies only the KeyFrame related properties (KeyCycles, KeyAttributes, KeyPositions) to
     * [transition], which effectively sets the respective parameters for each WidgetState.
     */
    fun applyKeyFramesTo(transition: Transition) {
        try {
            TransitionParser.parseKeyFrames(parsedTransition, transition)
        } catch (e: CLParsingException) {
            Log.e("CML", "Error parsing JSON $e")
        }
    }

    override fun getStartConstraintSetId(): String {
        return parsedTransition.getStringOrNull("from") ?: "start"
    }

    override fun getEndConstraintSetId(): String {
        return parsedTransition.getStringOrNull("to") ?: "end"
    }
}

/**
 * Parses the given JSON5 into a [Transition].
 *
 * See the official [Github Wiki](https://github.com/androidx/constraintlayout/wiki/Compose-MotionLayout-JSON-Syntax#transitions) to learn the syntax.
 */
@SuppressLint("ComposableNaming")
@Composable
fun Transition(@Language("json5") content: String): androidx.constraintlayout.compose.Transition? {
    val dpToPixel = with(LocalDensity.current) { 1.dp.toPx() }
    val transition = remember(content) {
        val parsed = try {
            CLParser.parse(content)
        } catch (e: CLParsingException) {
            Log.e("CML", "Error parsing JSON $e")
            null
        }
        mutableStateOf(
            if (parsed != null) {
                val pixelDp = CorePixelDp { dpValue -> dpValue * dpToPixel }
                TransitionImpl(parsed, pixelDp)
            } else {
                null
            }
        )
    }
    return transition.value
}

enum class MotionLayoutDebugFlags {
    NONE,
    SHOW_ALL,
    UNKNOWN
}

enum class LayoutInfoFlags {
    NONE,
    BOUNDS
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

@PublishedApi
internal class MotionMeasurer : Measurer() {
    private var motionProgress = 0f
    val transition = Transition()

    fun getProgress(): Float {
        return motionProgress
    }

    // TODO: Explicitly declare `getDesignInfo` so that studio tooling can identify the method, also
    //  make sure that the constraints/dimensions returned are for the start/current ConstraintSet

    private fun measureConstraintSet(
        optimizationLevel: Int,
        constraintSet: ConstraintSet,
        measurables: List<Measurable>,
        constraints: Constraints
    ) {
        state.reset()
        constraintSet.applyTo(state, measurables)
        state.apply(root)
        root.children.fastForEach { it.isAnimated = true }
        applyRootSize(constraints)
        root.updateHierarchy()

        if (DEBUG) {
            root.debugName = "ConstraintLayout"
            root.children.forEach { child ->
                child.debugName =
                    (child.companionWidget as? Measurable)?.layoutId?.toString() ?: "NOTAG"
            }
        }

        root.children.forEach { child ->
            val measurable = (child.companionWidget as? Measurable)
            val id = measurable?.layoutId ?: measurable?.constraintLayoutId
            child.stringId = id?.toString()
        }

        root.optimizationLevel = optimizationLevel
        // No need to set sizes and size modes as we passed them to the state above.
        root.measure(Optimizer.OPTIMIZATION_NONE, 0, 0, 0, 0, 0, 0, 0, 0)
    }

    fun performInterpolationMeasure(
        constraints: Constraints,
        layoutDirection: LayoutDirection,
        constraintSetStart: ConstraintSet,
        constraintSetEnd: ConstraintSet,
        transition: TransitionImpl?,
        measurables: List<Measurable>,
        optimizationLevel: Int,
        progress: Float,
        measureScope: MeasureScope
    ): IntSize {
        this.density = measureScope
        this.measureScope = measureScope

        val needsRemeasure = needsRemeasure(constraints)

        if (motionProgress != progress ||
            (layoutInformationReceiver?.getForcedWidth() != Int.MIN_VALUE &&
                    layoutInformationReceiver?.getForcedHeight() != Int.MIN_VALUE) ||
            needsRemeasure
        ) {
            recalculateInterpolation(
                constraints = constraints,
                layoutDirection = layoutDirection,
                constraintSetStart = constraintSetStart,
                constraintSetEnd = constraintSetEnd,
                transition = transition,
                measurables = measurables,
                optimizationLevel = optimizationLevel,
                progress = progress,
                remeasure = needsRemeasure
            )
        }
        return IntSize(root.width, root.height)
    }

    /**
     * Indicates if the layout requires measuring before computing the interpolation.
     *
     * This might happen if the size of MotionLayout or any of its children changed.
     *
     * MotionLayout size might change from its parent Layout, and in some cases the children size
     * might change (eg: A Text layout has a longer string appended).
     */
    private fun needsRemeasure(constraints: Constraints): Boolean {
        if (this.transition.isEmpty || frameCache.isEmpty()) {
            // Nothing measured (by MotionMeasurer)
            return true
        }

        if ((constraints.hasFixedHeight && !state.sameFixedHeight(constraints.maxHeight)) ||
            (constraints.hasFixedWidth && !state.sameFixedWidth(constraints.maxWidth))
        ) {
            // Layout size changed
            return true
        }

        return root.children.fastAny { child ->
            // Check if measurables have changed their size
            val measurable = (child.companionWidget as? Measurable) ?: return@fastAny false
            val interpolatedFrame = this.transition.getInterpolated(child) ?: return@fastAny false
            val placeable = placeables[measurable] ?: return@fastAny false
            val currentWidth = placeable.width
            val currentHeight = placeable.height

            // Need to recalculate interpolation if the size of any element changed
            return@fastAny currentWidth != interpolatedFrame.width() ||
                    currentHeight != interpolatedFrame.height()
        }
    }

    /**
     * Remeasures based on [constraintSetStart] and [constraintSetEnd] if needed.
     *
     * Runs the interpolation for the given [progress].
     *
     * Finally, updates the [Measurable]s dimension if they changed during interpolation.
     */
    private fun recalculateInterpolation(
        constraints: Constraints,
        layoutDirection: LayoutDirection,
        constraintSetStart: ConstraintSet,
        constraintSetEnd: ConstraintSet,
        transition: TransitionImpl?,
        measurables: List<Measurable>,
        optimizationLevel: Int,
        progress: Float,
        remeasure: Boolean
    ) {
        motionProgress = progress
        if (remeasure) {
            this.transition.clear()
            resetMeasureState()
            state.reset()
            // Define the size of the ConstraintLayout.
            state.width(
                if (constraints.hasFixedWidth) {
                    Dimension.createFixed(constraints.maxWidth)
                } else {
                    Dimension.createWrap().min(constraints.minWidth)
                }
            )
            state.height(
                if (constraints.hasFixedHeight) {
                    Dimension.createFixed(constraints.maxHeight)
                } else {
                    Dimension.createWrap().min(constraints.minHeight)
                }
            )
            // Build constraint set and apply it to the state.
            state.rootIncomingConstraints = constraints
            state.layoutDirection = layoutDirection

            measureConstraintSet(
                optimizationLevel, constraintSetStart, measurables, constraints
            )
            this.transition.updateFrom(root, Transition.START)
            measureConstraintSet(
                optimizationLevel, constraintSetEnd, measurables, constraints
            )
            this.transition.updateFrom(root, Transition.END)
            transition?.applyKeyFramesTo(this.transition)
        }

        this.transition.interpolate(root.width, root.height, progress)

        root.children.fastForEach { child ->
            // Update measurables to the interpolated dimensions
            val measurable = (child.companionWidget as? Measurable) ?: return@fastForEach
            val interpolatedFrame = this.transition.getInterpolated(child) ?: return@fastForEach
            val placeable = placeables[measurable]
            val currentWidth = placeable?.width
            val currentHeight = placeable?.height
            if (placeable == null ||
                currentWidth != interpolatedFrame.width() ||
                currentHeight != interpolatedFrame.height()
            ) {
                measurable.measure(
                    Constraints.fixed(interpolatedFrame.width(), interpolatedFrame.height())
                ).also { newPlaceable ->
                    placeables[measurable] = newPlaceable
                }
            }
            frameCache[measurable] = interpolatedFrame
        }

        if (layoutInformationReceiver?.getLayoutInformationMode() == LayoutInfoFlags.BOUNDS) {
            computeLayoutResult()
        }
    }

    private fun encodeKeyFrames(
        json: StringBuilder,
        location: FloatArray,
        types: IntArray,
        progress: IntArray,
        count: Int
    ) {
        if (count == 0) {
            return
        }
        json.append("keyTypes : [")
        for (i in 0 until count) {
            val m = types[i]
            json.append(" $m,")
        }
        json.append("],\n")

        json.append("keyPos : [")
        for (i in 0 until count * 2) {
            val f = location[i]
            json.append(" $f,")
        }
        json.append("],\n ")

        json.append("keyFrames : [")
        for (i in 0 until count) {
            val f = progress[i]
            json.append(" $f,")
        }
        json.append("],\n ")
    }

    fun encodeRoot(json: StringBuilder) {
        json.append("  root: {")
        json.append("interpolated: { left:  0,")
        json.append("  top:  0,")
        json.append("  right:   ${root.width} ,")
        json.append("  bottom:  ${root.height} ,")
        json.append(" } }")
    }

    override fun computeLayoutResult() {
        val json = StringBuilder()
        json.append("{ ")
        encodeRoot(json)
        val mode = IntArray(50)
        val pos = IntArray(50)
        var key = FloatArray(100)

        for (child in root.children) {
            val start = transition.getStart(child.stringId)
            val end = transition.getEnd(child.stringId)
            val interpolated = transition.getInterpolated(child.stringId)
            val path = transition.getPath(child.stringId)
            val count = transition.getKeyFrames(child.stringId, key, mode, pos)

            json.append(" ${child.stringId}: {")
            json.append(" interpolated : ")
            interpolated.serialize(json, true)

            json.append(", start : ")
            start.serialize(json)

            json.append(", end : ")
            end.serialize(json)
            encodeKeyFrames(json, key, mode, pos, count)
            json.append(" path : [")
            for (point in path) {
                json.append(" $point ,")
            }
            json.append(" ] ")
            json.append("}, ")
        }
        json.append(" }")
        layoutInformationReceiver?.setLayoutInformation(json.toString())
    }

    fun DrawScope.drawDebug() {
        var index = 0
        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        for (child in root.children) {
            val startFrame = transition.getStart(child)
            val endFrame = transition.getEnd(child)
            translate(2f, 2f) {
                drawFrameDebug(
                    size.width,
                    size.height,
                    startFrame,
                    endFrame,
                    pathEffect,
                    Color.White
                )
            }
            drawFrameDebug(
                size.width,
                size.height,
                startFrame,
                endFrame,
                pathEffect,
                Color.Blue
            )
            index++
        }
    }

    private fun DrawScope.drawFrameDebug(
        parentWidth: Float,
        parentHeight: Float,
        startFrame: WidgetFrame,
        endFrame: WidgetFrame,
        pathEffect: PathEffect,
        color: Color
    ) {
        drawFrame(startFrame, pathEffect, color)
        drawFrame(endFrame, pathEffect, color)
        var numKeyPositions = transition.getNumberKeyPositions(startFrame)
        var debugRender = MotionRenderDebug(23f)

        debugRender.draw(
            drawContext.canvas.nativeCanvas, transition.getMotion(startFrame.widget.stringId),
            1000, Motion.DRAW_PATH_BASIC,
            parentWidth.toInt(), parentHeight.toInt()
        )
        if (numKeyPositions == 0) {
//            drawLine(
//                start = Offset(startFrame.centerX(), startFrame.centerY()),
//                end = Offset(endFrame.centerX(), endFrame.centerY()),
//                color = color,
//                strokeWidth = 3f,
//                pathEffect = pathEffect
//            )
        } else {
            var x = FloatArray(numKeyPositions)
            var y = FloatArray(numKeyPositions)
            var pos = FloatArray(numKeyPositions)
            transition.fillKeyPositions(startFrame, x, y, pos)

            for (i in 0..numKeyPositions - 1) {
                var keyFrameProgress = pos[i] / 100f
                var frameWidth =
                    ((1 - keyFrameProgress) * startFrame.width()) +
                            (keyFrameProgress * endFrame.width())
                var frameHeight =
                    ((1 - keyFrameProgress) * startFrame.height()) +
                            (keyFrameProgress * endFrame.height())
                var curX = x[i] * parentWidth + frameWidth / 2f
                var curY = y[i] * parentHeight + frameHeight / 2f
//                drawLine(
//                    start = Offset(prex, prey),
//                    end = Offset(curX, curY),
//                    color = color,
//                    strokeWidth = 3f,
//                    pathEffect = pathEffect
//                )
                var path = Path()
                var pathSize = 20f
                path.moveTo(curX - pathSize, curY)
                path.lineTo(curX, curY + pathSize)
                path.lineTo(curX + pathSize, curY)
                path.lineTo(curX, curY - pathSize)
                path.close()

                var stroke = Stroke(width = 3f)
                drawPath(path, color, 1f, stroke)
            }
//            drawLine(
//                start = Offset(prex, prey),
//                end = Offset(endFrame.centerX(), endFrame.centerY()),
//                color = color,
//                strokeWidth = 3f,
//                pathEffect = pathEffect
//            )
        }
    }

    private fun DrawScope.drawFrame(
        frame: WidgetFrame,
        pathEffect: PathEffect,
        color: Color
    ) {
        if (frame.isDefaultTransform) {
            var drawStyle = Stroke(width = 3f, pathEffect = pathEffect)
            drawRect(
                color, Offset(frame.left.toFloat(), frame.top.toFloat()),
                Size(frame.width().toFloat(), frame.height().toFloat()), style = drawStyle
            )
        } else {
            var matrix = Matrix()
            if (!frame.rotationZ.isNaN()) {
                matrix.preRotate(frame.rotationZ, frame.centerX(), frame.centerY())
            }
            var scaleX = if (frame.scaleX.isNaN()) 1f else frame.scaleX
            var scaleY = if (frame.scaleY.isNaN()) 1f else frame.scaleY
            matrix.preScale(
                scaleX,
                scaleY,
                frame.centerX(),
                frame.centerY()
            )
            var points = floatArrayOf(
                frame.left.toFloat(), frame.top.toFloat(),
                frame.right.toFloat(), frame.top.toFloat(),
                frame.right.toFloat(), frame.bottom.toFloat(),
                frame.left.toFloat(), frame.bottom.toFloat()
            )
            matrix.mapPoints(points)
            drawLine(
                start = Offset(points[0], points[1]),
                end = Offset(points[2], points[3]),
                color = color,
                strokeWidth = 3f,
                pathEffect = pathEffect
            )
            drawLine(
                start = Offset(points[2], points[3]),
                end = Offset(points[4], points[5]),
                color = color,
                strokeWidth = 3f,
                pathEffect = pathEffect
            )
            drawLine(
                start = Offset(points[4], points[5]),
                end = Offset(points[6], points[7]),
                color = color,
                strokeWidth = 3f,
                pathEffect = pathEffect
            )
            drawLine(
                start = Offset(points[6], points[7]),
                end = Offset(points[0], points[1]),
                color = color,
                strokeWidth = 3f,
                pathEffect = pathEffect
            )
        }
    }

    fun getCustomColor(id: String, name: String): Color {
        if (!transition.contains(id)) {
            return Color.Black
        }

        transition.interpolate(root.width, root.height, motionProgress)

        val interpolatedFrame = transition.getInterpolated(id)
        val color = interpolatedFrame.getCustomColor(name)
        return Color(color)
    }

    fun getCustomFloat(id: String, name: String): Float {
        if (!transition.contains(id)) {
            return 0f
        }
        val startFrame = transition.getStart(id)
        val endFrame = transition.getEnd(id)
        val startFloat = startFrame.getCustomFloat(name)
        val endFloat = endFrame.getCustomFloat(name)
        return (1f - motionProgress) * startFloat + motionProgress * endFloat
    }

    fun clearConstraintSets() {
        transition.clear()
        frameCache.clear()
    }

    fun initWith(
        start: ConstraintSet,
        end: ConstraintSet,
        transition: TransitionImpl?,
        progress: Float
    ) {
        clearConstraintSets()
        start.applyTo(this.transition, Transition.START)
        end.applyTo(this.transition, Transition.END)
        this.transition.interpolate(0, 0, progress)
        transition?.applyAllTo(this.transition, 0)
    }
}

private val DEBUG = false
