/*
 * Copyright 2019 The Android Open Source Project
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
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateObserver
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.LayoutIdParentData
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.MultiMeasureLayout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.InspectorValueInfo
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import androidx.constraintlayout.core.parser.CLObject
import androidx.constraintlayout.core.parser.CLParser
import androidx.constraintlayout.core.parser.CLParsingException
import androidx.constraintlayout.core.state.ConstraintSetParser
import androidx.constraintlayout.core.state.Dimension.SPREAD_DIMENSION
import androidx.constraintlayout.core.state.Dimension.WRAP_DIMENSION
import androidx.constraintlayout.core.state.Registry
import androidx.constraintlayout.core.state.RegistryCallback
import androidx.constraintlayout.core.state.WidgetFrame
import androidx.constraintlayout.core.widgets.ConstraintWidget
import androidx.constraintlayout.core.widgets.ConstraintWidget.DimensionBehaviour.FIXED
import androidx.constraintlayout.core.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
import androidx.constraintlayout.core.widgets.ConstraintWidget.DimensionBehaviour.MATCH_PARENT
import androidx.constraintlayout.core.widgets.ConstraintWidget.DimensionBehaviour.WRAP_CONTENT
import androidx.constraintlayout.core.widgets.ConstraintWidget.MATCH_CONSTRAINT_SPREAD
import androidx.constraintlayout.core.widgets.ConstraintWidget.MATCH_CONSTRAINT_WRAP
import androidx.constraintlayout.core.widgets.ConstraintWidgetContainer
import androidx.constraintlayout.core.widgets.Guideline
import androidx.constraintlayout.core.widgets.HelperWidget
import androidx.constraintlayout.core.widgets.Optimizer
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure.Measure.TRY_GIVEN_DIMENSIONS
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure.Measure.USE_GIVEN_DIMENSIONS
import kotlinx.coroutines.channels.Channel
import org.intellij.lang.annotations.Language

/**
 * Layout that positions its children according to the constraints between them.
 */
@Composable
inline fun ConstraintLayout(
    modifier: Modifier = Modifier,
    optimizationLevel: Int = Optimizer.OPTIMIZATION_STANDARD,
    crossinline content: @Composable ConstraintLayoutScope.() -> Unit
) {
    val measurer = remember { Measurer() }
    val scope = remember { ConstraintLayoutScope() }
    val remeasureRequesterState = remember { mutableStateOf(false) }
    val (measurePolicy, onHelpersChanged) = rememberConstraintLayoutMeasurePolicy(
        optimizationLevel,
        scope,
        remeasureRequesterState,
        measurer
    )
    @Suppress("Deprecation")
    MultiMeasureLayout(
        modifier = modifier.semantics { designInfoProvider = measurer },
        measurePolicy = measurePolicy,
        content = {
            val previousHelpersHashCode = scope.helpersHashCode
            scope.reset()
            scope.content()
            if (scope.helpersHashCode != previousHelpersHashCode) onHelpersChanged()
        }
    )
}

@Composable
@PublishedApi
internal fun rememberConstraintLayoutMeasurePolicy(
    optimizationLevel: Int,
    scope: ConstraintLayoutScope,
    remeasureRequesterState: MutableState<Boolean>,
    measurer: Measurer
): Pair<MeasurePolicy, () -> Unit> {
    val constraintSet = remember { ConstraintSetForInlineDsl(scope) }

    return remember(optimizationLevel) {
        val measurePolicy = MeasurePolicy { measurables, constraints ->
            val layoutSize = measurer.performMeasure(
                constraints,
                layoutDirection,
                constraintSet,
                measurables,
                optimizationLevel,
                this
            )
            // We read the remeasurement requester state, to request remeasure when the value
            // changes. This will happen when the scope helpers are changing at recomposition.
            remeasureRequesterState.value

            layout(layoutSize.width, layoutSize.height) {
                with(measurer) { performLayout(measurables) }
            }
        }

        val onHelpersChanged = {
            // If the helpers have changed, we need to request remeasurement. To achieve this,
            // we are changing this boolean state that is read during measurement.
            remeasureRequesterState.value = !remeasureRequesterState.value
            constraintSet.knownDirty = true
        }

        measurePolicy to onHelpersChanged
    }
}

private class ConstraintSetForInlineDsl(
    val scope: ConstraintLayoutScope
) : ConstraintSet, RememberObserver {
    private var handler: Handler? = null
    private val observer = SnapshotStateObserver {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            it()
        } else {
            val h = handler ?: Handler(Looper.getMainLooper()).also { h -> handler = h }
            h.post(it)
        }
    }

    override fun applyTo(state: State, measurables: List<Measurable>) {
        scope.applyTo(state)
        previousDatas.clear()
        observer.observeReads(Unit, onCommitAffectingConstrainLambdas) {
            measurables.fastForEach { measurable ->
                val parentData = measurable.parentData as? ConstraintLayoutParentData
                // Run the constrainAs block of the child, to obtain its constraints.
                if (parentData != null) {
                    val constrainScope = ConstrainScope(parentData.ref.id)
                    parentData.constrain(constrainScope)
                    constrainScope.applyTo(state)
                }
                previousDatas.add(parentData)
            }
        }
        knownDirty = false
    }

    var knownDirty = true

    private val onCommitAffectingConstrainLambdas = { _: Unit -> knownDirty = true }

    override fun isDirty(measurables: List<Measurable>): Boolean {
        if (knownDirty || measurables.size != previousDatas.size) return true

        measurables.fastForEachIndexed { index, measurable ->
            if (measurable.parentData as? ConstraintLayoutParentData != previousDatas[index]) {
                return true
            }
        }

        return false
    }

    private val previousDatas = mutableListOf<ConstraintLayoutParentData?>()

    override fun onRemembered() {
        observer.start()
    }

    override fun onForgotten() {
        observer.stop()
        observer.clear()
    }

    override fun onAbandoned() {}
}

/**
 * Layout that positions its children according to the constraints between them.
 *
 * When recomposed with different [constraintSet], you can use the [animateChanges] parameter
 * to animate the layout changes ([animationSpec] and [finishedAnimationListener] attributes can
 * also be useful in this mode). This is only intended for basic transitions, if more control
 * is needed, we recommend using [MotionLayout] instead.
 */
@OptIn(ExperimentalMotionApi::class)
@Suppress("NOTHING_TO_INLINE")
@Composable
inline fun ConstraintLayout(
    constraintSet: ConstraintSet,
    modifier: Modifier = Modifier,
    optimizationLevel: Int = Optimizer.OPTIMIZATION_STANDARD,
    animateChanges: Boolean = false,
    animationSpec: AnimationSpec<Float> = tween<Float>(),
    noinline finishedAnimationListener: (() -> Unit)? = null,
    noinline content: @Composable () -> Unit
) {
    if (animateChanges) {
        var startConstraint by remember { mutableStateOf(constraintSet) }
        var endConstraint by remember { mutableStateOf(constraintSet) }
        val progress = remember { Animatable(0.0f) }
        val channel = remember { Channel<ConstraintSet>(Channel.CONFLATED) }
        val direction = remember { mutableStateOf(1) }

        SideEffect {
            channel.trySend(constraintSet)
        }

        LaunchedEffect(channel) {
            for (constraints in channel) {
                val newConstraints = channel.tryReceive().getOrNull() ?: constraints
                val currentConstraints =
                    if (direction.value == 1) startConstraint else endConstraint
                if (newConstraints != currentConstraints) {
                    if (direction.value == 1) {
                        endConstraint = newConstraints
                    } else {
                        startConstraint = newConstraints
                    }
                    progress.animateTo(direction.value.toFloat(), animationSpec)
                    direction.value = if (direction.value == 1) 0 else 1
                    finishedAnimationListener?.invoke()
                }
            }
        }
        MotionLayout(
            start = startConstraint,
            end = endConstraint,
            progress = progress.value,
            modifier = modifier,
            content = { content() })
    } else {
        val needsUpdate = remember {
            mutableStateOf(0L)
        }

        val measurer = remember { Measurer() }
        val measurePolicy = rememberConstraintLayoutMeasurePolicy(
            optimizationLevel,
            needsUpdate,
            constraintSet,
            measurer
        )
        if (constraintSet is EditableJSONLayout) {
            constraintSet.setUpdateFlag(needsUpdate)
        }
        measurer.addLayoutInformationReceiver(constraintSet as? LayoutInformationReceiver)

        val forcedScaleFactor = measurer.forcedScaleFactor
        if (!forcedScaleFactor.isNaN()) {
            var mod = modifier.scale(measurer.forcedScaleFactor)
            Box {
                @Suppress("DEPRECATION")
                MultiMeasureLayout(
                    modifier = mod.semantics { designInfoProvider = measurer },
                    measurePolicy = measurePolicy,
                    content = {
                        measurer.createDesignElements()
                        content()
                    }
                )
                with(measurer) {
                    drawDebugBounds(forcedScaleFactor)
                }
            }
        } else {
            @Suppress("DEPRECATION")
            MultiMeasureLayout(
                modifier = modifier.semantics { designInfoProvider = measurer },
                measurePolicy = measurePolicy,
                content = {
                    measurer.createDesignElements()
                    content()
                }
            )
        }
    }
}

@Composable
@PublishedApi
internal fun rememberConstraintLayoutMeasurePolicy(
    optimizationLevel: Int,
    needsUpdate: MutableState<Long>,
    constraintSet: ConstraintSet,
    measurer: Measurer
) = remember(optimizationLevel, needsUpdate.value, constraintSet) {
    measurer.parseDesignElements(constraintSet)
    MeasurePolicy { measurables, constraints ->
        val layoutSize = measurer.performMeasure(
            constraints,
            layoutDirection,
            constraintSet,
            measurables,
            optimizationLevel,
            this
        )
        layout(layoutSize.width, layoutSize.height) {
            with(measurer) { performLayout(measurables) }
        }
    }
}

/**
 * Scope used by the inline DSL of [ConstraintLayout].
 */
@LayoutScopeMarker
class ConstraintLayoutScope @PublishedApi internal constructor() : ConstraintLayoutBaseScope() {
    /**
     * Creates one [ConstrainedLayoutReference], which needs to be assigned to a layout within the
     * [ConstraintLayout] as part of [Modifier.constrainAs]. To create more references at the
     * same time, see [createRefs].
     */
    fun createRef(): ConstrainedLayoutReference = childrenRefs.getOrNull(childId++)
        ?: ConstrainedLayoutReference(childId).also { childrenRefs.add(it) }

    /**
     * Convenient way to create multiple [ConstrainedLayoutReference]s, which need to be assigned
     * to layouts within the [ConstraintLayout] as part of [Modifier.constrainAs]. To create just
     * one reference, see [createRef].
     */
    @Stable
    fun createRefs(): ConstraintLayoutScope.ConstrainedLayoutReferences =
        referencesObject ?: ConstrainedLayoutReferences().also { referencesObject = it }

    private var referencesObject: ConstrainedLayoutReferences? = null

    private val ChildrenStartIndex = 0
    private var childId = ChildrenStartIndex
    private val childrenRefs = ArrayList<ConstrainedLayoutReference>()
    override fun reset() {
        super.reset()
        childId = ChildrenStartIndex
    }

    /**
     * Convenience API for creating multiple [ConstrainedLayoutReference] via [createRefs].
     */
    inner class ConstrainedLayoutReferences internal constructor() {
        operator fun component1(): ConstrainedLayoutReference = createRef()
        operator fun component2(): ConstrainedLayoutReference = createRef()
        operator fun component3(): ConstrainedLayoutReference = createRef()
        operator fun component4(): ConstrainedLayoutReference = createRef()
        operator fun component5(): ConstrainedLayoutReference = createRef()
        operator fun component6(): ConstrainedLayoutReference = createRef()
        operator fun component7(): ConstrainedLayoutReference = createRef()
        operator fun component8(): ConstrainedLayoutReference = createRef()
        operator fun component9(): ConstrainedLayoutReference = createRef()
        operator fun component10(): ConstrainedLayoutReference = createRef()
        operator fun component11(): ConstrainedLayoutReference = createRef()
        operator fun component12(): ConstrainedLayoutReference = createRef()
        operator fun component13(): ConstrainedLayoutReference = createRef()
        operator fun component14(): ConstrainedLayoutReference = createRef()
        operator fun component15(): ConstrainedLayoutReference = createRef()
        operator fun component16(): ConstrainedLayoutReference = createRef()
    }

    /**
     * [Modifier] that defines the constraints, as part of a [ConstraintLayout], of the layout
     * element.
     */
    @Stable
    fun Modifier.constrainAs(
        ref: ConstrainedLayoutReference,
        constrainBlock: ConstrainScope.() -> Unit
    ) = this.then(ConstrainAsModifier(ref, constrainBlock))

    @Stable
    private class ConstrainAsModifier(
        private val ref: ConstrainedLayoutReference,
        private val constrainBlock: ConstrainScope.() -> Unit
    ) : ParentDataModifier, InspectorValueInfo(
        debugInspectorInfo {
            name = "constrainAs"
            properties["ref"] = ref
            properties["constrainBlock"] = constrainBlock
        }
    ) {
        override fun Density.modifyParentData(parentData: Any?) =
            ConstraintLayoutParentData(ref, constrainBlock)

        override fun hashCode() = constrainBlock.hashCode()

        override fun equals(other: Any?) =
            constrainBlock == (other as? ConstrainAsModifier)?.constrainBlock
    }
}

/**
 * Scope used by the [ConstraintSet] DSL.
 */
@LayoutScopeMarker
class ConstraintSetScope internal constructor() : ConstraintLayoutBaseScope() {
    /**
     * Creates one [ConstrainedLayoutReference] corresponding to the [ConstraintLayout] element
     * with [id].
     */
    fun createRefFor(id: Any): ConstrainedLayoutReference = ConstrainedLayoutReference(id)

    /**
     * Specifies the constraints associated to the layout identified with [ref].
     */
    fun constrain(
        ref: ConstrainedLayoutReference,
        constrainBlock: ConstrainScope.() -> Unit
    ): ConstrainScope = ConstrainScope(ref.id).apply {
        constrainBlock()
        this@ConstraintSetScope.tasks.addAll(this.tasks)
    }
}

/**
 * Parent data provided by `Modifier.constrainAs`.
 */
@Stable
private class ConstraintLayoutParentData(
    val ref: ConstrainedLayoutReference,
    val constrain: ConstrainScope.() -> Unit
) : LayoutIdParentData {
    override val layoutId: Any = ref.id

    override fun equals(other: Any?) = other is ConstraintLayoutParentData &&
        ref.id == other.ref.id && constrain == other.constrain

    override fun hashCode() = ref.id.hashCode() * 31 + constrain.hashCode()
}

/**
 * Convenience for creating ids corresponding to layout references that cannot be referred
 * to from the outside of the scope (e.g. barriers, layout references in the modifier-based API,
 * etc.).
 */
internal fun createId() = object : Any() {}

/**
 * Represents a dimension that can be assigned to the width or height of a [ConstraintLayout]
 * [child][ConstrainedLayoutReference].
 */
// TODO(popam, b/157781841): It is unfortunate that this interface is top level in
// `foundation-layout`. This will be ok if we move constraint layout to its own module or at
// least subpackage.
interface Dimension {
    /**
     * A [Dimension] that can be assigned both min and max bounds.
     */
    interface Coercible : Dimension

    /**
     * A [Dimension] that can be assigned a min bound.
     */
    interface MinCoercible : Dimension

    /**
     * A [Dimension] that can be assigned a max bound.
     */
    interface MaxCoercible : Dimension

    companion object {
        /**
         * Links should be specified from both sides corresponding to this dimension, in order for
         * this to work.
         *
         * Creates a [Dimension] such that if the constraints allow it, will have the size given by
         * [dp], otherwise will take the size remaining within the constraints.
         *
         * This is effectively a shorthand for [fillToConstraints] with a max value.
         *
         * To make the value fixed (respected regardless the [ConstraintSet]), [value] should
         * be used instead.
         */
        fun preferredValue(dp: Dp): Dimension.MinCoercible =
            DimensionDescription { state ->
                SolverDimension.createSuggested(state.convertDimension(dp))
                    .suggested(SPREAD_DIMENSION)
            }

        /**
         * Creates a [Dimension] representing a fixed dp size. The size will not change
         * according to the constraints in the [ConstraintSet].
         */
        fun value(dp: Dp): Dimension =
            DimensionDescription { state ->
                SolverDimension.createFixed(state.convertDimension(dp))
            }

        /**
         * Sets the dimensions to be defined as a ratio of the width and height. The assigned
         * dimension will be considered to also be [fillToConstraints].
         *
         * The string to define a ratio is defined by the format: 'W:H'.
         * Where H is the height as a proportion of W (the width).
         *
         * Eg: width = Dimension.ratio('1:2') sets the width to be half as large as the height.
         *
         * Note that only one dimension should be defined as a ratio.
         */
        fun ratio(ratio: String): Dimension =
            DimensionDescription { SolverDimension.createRatio(ratio).suggested(SPREAD_DIMENSION) }

        /**
         * Links should be specified from both sides corresponding to this dimension, in order for
         * this to work.
         *
         * A [Dimension] with suggested wrap content behavior. The wrap content size
         * will be respected unless the constraints in the [ConstraintSet] do not allow it.
         * To make the value fixed (respected regardless the [ConstraintSet]), [wrapContent]
         * should be used instead.
         */
        val preferredWrapContent: Dimension.Coercible
            get() = DimensionDescription { SolverDimension.createSuggested(WRAP_DIMENSION) }

        /**
         * A fixed [Dimension] with wrap content behavior. The size will not change
         * according to the constraints in the [ConstraintSet].
         */
        val wrapContent: Dimension
            get() = DimensionDescription { SolverDimension.createFixed(WRAP_DIMENSION) }

        /**
         * A fixed [Dimension] that matches the dimensions of the root ConstraintLayout. The size
         * will not change accoring to the constraints in the [ConstraintSet].
         */
        val matchParent: Dimension
            get() = DimensionDescription { SolverDimension.createParent() }

        /**
         * Links should be specified from both sides corresponding to this dimension, in order for
         * this to work.
         *
         * A [Dimension] that spreads to match constraints.
         */
        val fillToConstraints: Dimension.Coercible
            get() = DimensionDescription { SolverDimension.createSuggested(SPREAD_DIMENSION) }

        /**
         * A [Dimension] that is a percent of the parent in the corresponding direction.
         */
        fun percent(percent: Float): Dimension =
            // TODO(popam, b/157880732): make this nicer when possible in future solver releases
            DimensionDescription { SolverDimension.createPercent(0, percent).suggested(0) }
    }
}

/**
 * Sets the lower bound of the current [Dimension] to be the wrap content size of the child.
 */
val Dimension.Coercible.atLeastWrapContent: Dimension.MaxCoercible
    get() = (this as DimensionDescription).also { it.minSymbol = WRAP_DIMENSION }

/**
 * Sets the lower bound of the current [Dimension] to a fixed [dp] value.
 */
fun Dimension.Coercible.atLeast(dp: Dp): Dimension.MaxCoercible =
    (this as DimensionDescription).also { it.min = dp }

/**
 * Sets the upper bound of the current [Dimension] to a fixed [dp] value.
 */
fun Dimension.Coercible.atMost(dp: Dp): Dimension.MinCoercible =
    (this as DimensionDescription).also { it.max = dp }

/**
 * Sets the upper bound of the current [Dimension] to be the wrap content size of the child.
 */
val Dimension.Coercible.atMostWrapContent: Dimension.MinCoercible
    get() = (this as DimensionDescription).also { it.maxSymbol = WRAP_DIMENSION }

/**
 * Sets the lower bound of the current [Dimension] to a fixed [dp] value.
 */
@Deprecated(
    message = "Unintended method name, use atLeast(dp) instead",
    replaceWith = ReplaceWith(
        "this.atLeast(dp)",
        "androidx.constraintlayout.compose.atLeast"
    )
)
fun Dimension.MinCoercible.atLeastWrapContent(dp: Dp): Dimension =
    (this as DimensionDescription).also { it.min = dp }

/**
 * Sets the lower bound of the current [Dimension] to a fixed [dp] value.
 */
fun Dimension.MinCoercible.atLeast(dp: Dp): Dimension =
    (this as DimensionDescription).also { it.min = dp }

/**
 * Sets the lower bound of the current [Dimension] to be the wrap content size of the child.
 */
val Dimension.MinCoercible.atLeastWrapContent: Dimension
    get() = (this as DimensionDescription).also { it.minSymbol = WRAP_DIMENSION }

/**
 * Sets the upper bound of the current [Dimension] to a fixed [dp] value.
 */
fun Dimension.MaxCoercible.atMost(dp: Dp): Dimension =
    (this as DimensionDescription).also { it.max = dp }

/**
 * Sets the upper bound of the current [Dimension] to be the [WRAP_DIMENSION] size of the child.
 */
val Dimension.MaxCoercible.atMostWrapContent: Dimension
    get() = (this as DimensionDescription).also { it.maxSymbol = WRAP_DIMENSION }

/**
 * Describes a sizing behavior that can be applied to the width or height of a
 * [ConstraintLayout] child. The content of this class should not be instantiated
 * directly; helpers available in the [Dimension]'s companion object should be used.
 */
internal class DimensionDescription internal constructor(
    private val baseDimension: (State) -> SolverDimension
) : Dimension.Coercible, Dimension.MinCoercible, Dimension.MaxCoercible, Dimension {
    var min: Dp? = null
    var minSymbol: Any? = null
    var max: Dp? = null
    var maxSymbol: Any? = null
    internal fun toSolverDimension(state: State) = baseDimension(state).also {
        if (minSymbol != null) {
            it.min(minSymbol)
        } else if (min != null) {
            it.min(state.convertDimension(min!!))
        }
        if (maxSymbol != null) {
            it.max(maxSymbol)
        } else if (max != null) {
            it.max(state.convertDimension(max!!))
        }
    }
}

/**
 * Parses [content] into a [ConstraintSet] and sets the variables defined in the `Variables` block
 * with the values of [overrideVariables].
 *
 * Eg:
 *
 *  For `Variables: { margin: { from: 'initialMargin', step: 10 } }`
 *
 *  overrideVariables = `"{ 'initialMargin' = 50 }"`
 *
 *  Will create a ConstraintSet where `initialMargin` is 50.
 */
@SuppressLint("ComposableNaming")
@Composable
fun ConstraintSet(
    @Language("json5") content: String,
    @Language("json5") overrideVariables: String? = null
): ConstraintSet {
    val constraintset = remember(content, overrideVariables) {
        JSONConstraintSet(content, overrideVariables)
    }
    return constraintset
}

/**
 * Handles update back to the composable
 */
@PublishedApi
internal abstract class EditableJSONLayout(@Language("json5") content: String) :
    LayoutInformationReceiver {
    private var forcedWidth: Int = Int.MIN_VALUE
    private var forcedHeight: Int = Int.MIN_VALUE
    private var forcedDrawDebug: MotionLayoutDebugFlags =
        MotionLayoutDebugFlags.UNKNOWN
    private var updateFlag: MutableState<Long>? = null
    private var layoutInformationMode: LayoutInfoFlags = LayoutInfoFlags.NONE
    private var layoutInformation = ""
    private var last = System.nanoTime()
    private var debugName: String? = null

    private var currentContent = content

    protected fun initialization() {
        try {
            onNewContent(currentContent)
            if (debugName != null) {
                val callback = object : RegistryCallback {
                    override fun onNewMotionScene(content: String?) {
                        if (content == null) {
                            return
                        }
                        onNewContent(content)
                    }

                    override fun onProgress(progress: Float) {
                        onNewProgress(progress)
                    }

                    override fun onDimensions(width: Int, height: Int) {
                        onNewDimensions(width, height)
                    }

                    override fun currentMotionScene(): String {
                        return currentContent
                    }

                    override fun currentLayoutInformation(): String {
                        return layoutInformation
                    }

                    override fun setLayoutInformationMode(mode: Int) {
                        onLayoutInformation(mode)
                    }

                    override fun getLastModified(): Long {
                        return last
                    }

                    override fun setDrawDebug(debugMode: Int) {
                        onDrawDebug(debugMode)
                    }
                }
                val registry = Registry.getInstance()
                registry.register(debugName, callback)
            }
        } catch (_: CLParsingException) {
        }
    }

    // region Accessors
    override fun setUpdateFlag(needsUpdate: MutableState<Long>) {
        updateFlag = needsUpdate
    }

    protected fun signalUpdate() {
        if (updateFlag != null) {
            updateFlag!!.value = updateFlag!!.value + 1
        }
    }

    fun setCurrentContent(content: String) {
        onNewContent(content)
    }

    fun getCurrentContent(): String {
        return currentContent
    }

    fun setDebugName(name: String?) {
        debugName = name
    }

    fun getDebugName(): String? {
        return debugName
    }

    override fun getForcedDrawDebug(): MotionLayoutDebugFlags {
        return forcedDrawDebug
    }

    override fun getForcedWidth(): Int {
        return forcedWidth
    }

    override fun getForcedHeight(): Int {
        return forcedHeight
    }

    override fun setLayoutInformation(information: String) {
        last = System.nanoTime()
        layoutInformation = information
    }

    fun getLayoutInformation(): String {
        return layoutInformation
    }

    override fun getLayoutInformationMode(): LayoutInfoFlags {
        return layoutInformationMode
    }
    // endregion

    // region on update methods
    protected open fun onNewContent(content: String) {
        currentContent = content
        try {
            val json = CLParser.parse(currentContent)
            if (json is CLObject) {
                val firstTime = debugName == null
                if (firstTime) {
                    val debug = json.getObjectOrNull("Header")
                    if (debug != null) {
                        debugName = debug.getStringOrNull("exportAs")
                        layoutInformationMode = LayoutInfoFlags.BOUNDS
                    }
                }
                if (!firstTime) {
                    signalUpdate()
                }
            }
        } catch (e: CLParsingException) {
            // nothing (content might be invalid, sent by live edit)
        } catch (e: Exception) {
            // nothing (content might be invalid, sent by live edit)
        }
    }

    fun onNewDimensions(width: Int, height: Int) {
        forcedWidth = width
        forcedHeight = height
        signalUpdate()
    }

    protected fun onLayoutInformation(mode: Int) {
        when (mode) {
            LayoutInfoFlags.NONE.ordinal -> layoutInformationMode = LayoutInfoFlags.NONE
            LayoutInfoFlags.BOUNDS.ordinal -> layoutInformationMode = LayoutInfoFlags.BOUNDS
        }
        signalUpdate()
    }

    protected fun onDrawDebug(debugMode: Int) {
        forcedDrawDebug = when (debugMode) {
            MotionLayoutDebugFlags.UNKNOWN.ordinal -> MotionLayoutDebugFlags.UNKNOWN
            MotionLayoutDebugFlags.NONE.ordinal -> MotionLayoutDebugFlags.NONE
            MotionLayoutDebugFlags.SHOW_ALL.ordinal -> MotionLayoutDebugFlags.SHOW_ALL
            -1 -> MotionLayoutDebugFlags.UNKNOWN
            else -> MotionLayoutDebugFlags.UNKNOWN
        }
        signalUpdate()
    }
    // endregion
}

internal data class DesignElement(
    var id: String,
    var type: String,
    var params: HashMap<String, String>
)

/**
 * Parses the given JSON5 into a [ConstraintSet].
 *
 * See the official [Github Wiki](https://github.com/androidx/constraintlayout/wiki/ConstraintSet-JSON5-syntax) to learn the syntax.
 */
fun ConstraintSet(@Language(value = "json5") jsonContent: String): ConstraintSet =
    JSONConstraintSet(content = jsonContent)

/**
 * Creates a [ConstraintSet] from a [jsonContent] string that extends the changes applied by
 * [extendConstraintSet].
 */
fun ConstraintSet(
    extendConstraintSet: ConstraintSet,
    @Language(value = "json5") jsonContent: String
): ConstraintSet =
    JSONConstraintSet(content = jsonContent, extendFrom = extendConstraintSet)

/**
 * Creates a [ConstraintSet].
 */
fun ConstraintSet(description: ConstraintSetScope.() -> Unit): ConstraintSet =
    DslConstraintSet(description)

/**
 * Creates a [ConstraintSet] that extends the changes applied by [extendConstraintSet].
 */
fun ConstraintSet(
    extendConstraintSet: ConstraintSet,
    description: ConstraintSetScope.() -> Unit
): ConstraintSet =
    DslConstraintSet(description, extendConstraintSet)

/**
 * The state of the [ConstraintLayout] solver.
 */
class State(val density: Density) : SolverState() {
    var rootIncomingConstraints: Constraints = Constraints()
    lateinit var layoutDirection: LayoutDirection

    init {
        setDpToPixel { dp -> density.density * dp }
    }

    override fun convertDimension(value: Any?): Int {
        return if (value is Dp) {
            with(density) { value.roundToPx() }
        } else {
            super.convertDimension(value)
        }
    }

    override fun reset() {
        super.reset()
    }

    internal fun getKeyId(helperWidget: HelperWidget): Any? {
        return mHelperReferences.entries.firstOrNull { it.value.helperWidget == helperWidget }?.key
    }
}

interface LayoutInformationReceiver {
    fun setLayoutInformation(information: String)
    fun getLayoutInformationMode(): LayoutInfoFlags
    fun getForcedWidth(): Int
    fun getForcedHeight(): Int
    fun setUpdateFlag(needsUpdate: MutableState<Long>)
    fun getForcedDrawDebug(): MotionLayoutDebugFlags

    /**
     * reset the force progress flag
     */
    fun resetForcedProgress()

    /**
     * Get the progress of the force progress
     */
    fun getForcedProgress(): Float

    fun onNewProgress(progress: Float)
}

@PublishedApi
internal open class Measurer : BasicMeasure.Measurer, DesignInfoProvider {
    private var computedLayoutResult: String = ""
    protected var layoutInformationReceiver: LayoutInformationReceiver? = null
    protected val root = ConstraintWidgetContainer(0, 0).also { it.measurer = this }
    protected val placeables = mutableMapOf<Measurable, Placeable>()
    private val lastMeasures = mutableMapOf<Measurable, Array<Int>>()
    protected val frameCache = mutableMapOf<Measurable, WidgetFrame>()

    protected lateinit var density: Density
    protected lateinit var measureScope: MeasureScope
    protected val state by lazy(LazyThreadSafetyMode.NONE) { State(density) }

    private val widthConstraintsHolder = IntArray(2)
    private val heightConstraintsHolder = IntArray(2)

    var forcedScaleFactor = Float.NaN
    val layoutCurrentWidth: Int
        get() = root.width
    val layoutCurrentHeight: Int
        get() = root.height

    /**
     * Method called by Compose tooling. Returns a JSON string that represents the Constraints
     * defined for this ConstraintLayout Composable.
     */
    override fun getDesignInfo(startX: Int, startY: Int, args: String) =
        parseConstraintsToJson(root, state, startX, startY, args)

    override fun measure(constraintWidget: ConstraintWidget, measure: BasicMeasure.Measure) {
        val measurable = constraintWidget.companionWidget
        if (measurable !is Measurable) return

        if (DEBUG) {
            Log.d(
                "CCL",
                "Measuring ${measurable.layoutId} with: " +
                    constraintWidget.toDebugString() + "\n"
            )
        }

        var constraints: Constraints
        run {
            val measurableLastMeasures = lastMeasures[measurable]
            obtainConstraints(
                measure.horizontalBehavior,
                measure.horizontalDimension,
                constraintWidget.mMatchConstraintDefaultWidth,
                measure.measureStrategy,
                (measurableLastMeasures?.get(1) ?: 0) == constraintWidget.height,
                constraintWidget.isResolvedHorizontally,
                state.rootIncomingConstraints.maxWidth,
                widthConstraintsHolder
            )
            obtainConstraints(
                measure.verticalBehavior,
                measure.verticalDimension,
                constraintWidget.mMatchConstraintDefaultHeight,
                measure.measureStrategy,
                (measurableLastMeasures?.get(0) ?: 0) == constraintWidget.width,
                constraintWidget.isResolvedVertically,
                state.rootIncomingConstraints.maxHeight,
                heightConstraintsHolder
            )

            constraints = Constraints(
                widthConstraintsHolder[0],
                widthConstraintsHolder[1],
                heightConstraintsHolder[0],
                heightConstraintsHolder[1]
            )
        }

        if ((measure.measureStrategy == TRY_GIVEN_DIMENSIONS ||
                measure.measureStrategy == USE_GIVEN_DIMENSIONS) ||
            !(measure.horizontalBehavior == MATCH_CONSTRAINT &&
                constraintWidget.mMatchConstraintDefaultWidth == MATCH_CONSTRAINT_SPREAD &&
                measure.verticalBehavior == MATCH_CONSTRAINT &&
                constraintWidget.mMatchConstraintDefaultHeight == MATCH_CONSTRAINT_SPREAD)
        ) {
            if (DEBUG) {
                Log.d("CCL", "Measuring ${measurable.layoutId} with $constraints")
            }
            val placeable = measurable.measure(constraints).also { placeables[measurable] = it }
            constraintWidget.isMeasureRequested = false
            if (DEBUG) {
                Log.d(
                    "CCL",
                    "${measurable.layoutId} is size ${placeable.width} ${placeable.height}"
                )
            }

            val coercedWidth = placeable.width.coerceIn(
                constraintWidget.mMatchConstraintMinWidth.takeIf { it > 0 },
                constraintWidget.mMatchConstraintMaxWidth.takeIf { it > 0 }
            )
            val coercedHeight = placeable.height.coerceIn(
                constraintWidget.mMatchConstraintMinHeight.takeIf { it > 0 },
                constraintWidget.mMatchConstraintMaxHeight.takeIf { it > 0 }
            )

            var remeasure = false
            if (coercedWidth != placeable.width) {
                constraints = Constraints(
                    minWidth = coercedWidth,
                    minHeight = constraints.minHeight,
                    maxWidth = coercedWidth,
                    maxHeight = constraints.maxHeight
                )
                remeasure = true
            }
            if (coercedHeight != placeable.height) {
                constraints = Constraints(
                    minWidth = constraints.minWidth,
                    minHeight = coercedHeight,
                    maxWidth = constraints.maxWidth,
                    maxHeight = coercedHeight
                )
                remeasure = true
            }
            if (remeasure) {
                if (DEBUG) {
                    Log.d("CCL", "Remeasuring coerced ${measurable.layoutId} with $constraints")
                }
                measurable.measure(constraints).also { placeables[measurable] = it }
                constraintWidget.isMeasureRequested = false
            }
        }

        val currentPlaceable = placeables[measurable]
        measure.measuredWidth = currentPlaceable?.width ?: constraintWidget.width
        measure.measuredHeight = currentPlaceable?.height ?: constraintWidget.height
        val baseline =
            if (currentPlaceable != null && state.isBaselineNeeded(constraintWidget)) {
                currentPlaceable[FirstBaseline]
            } else {
                AlignmentLine.Unspecified
            }
        measure.measuredHasBaseline = baseline != AlignmentLine.Unspecified
        measure.measuredBaseline = baseline
        lastMeasures.getOrPut(measurable, { arrayOf(0, 0, AlignmentLine.Unspecified) })
            .copyFrom(measure)

        measure.measuredNeedsSolverPass = measure.measuredWidth != measure.horizontalDimension ||
            measure.measuredHeight != measure.verticalDimension
    }

    fun addLayoutInformationReceiver(layoutReceiver: LayoutInformationReceiver?) {
        layoutInformationReceiver = layoutReceiver
        layoutInformationReceiver?.setLayoutInformation(computedLayoutResult)
    }

    open fun computeLayoutResult() {
        val json = StringBuilder()
        json.append("{ ")
        json.append("  root: {")
        json.append("interpolated: { left:  0,")
        json.append("  top:  0,")
        json.append("  right:   ${root.width} ,")
        json.append("  bottom:  ${root.height} ,")
        json.append(" } }")

        for (child in root.children) {
            val measurable = child.companionWidget
            if (measurable !is Measurable) {
                if (child is Guideline) {
                    json.append(" ${child.stringId}: {")
                    if (child.orientation == ConstraintWidget.HORIZONTAL) {
                        json.append(" type: 'hGuideline', ")
                    } else {
                        json.append(" type: 'vGuideline', ")
                    }
                    json.append(" interpolated: ")
                    json.append(
                        " { left: ${child.x}, top: ${child.y}, " +
                            "right: ${child.x + child.width}, " +
                            "bottom: ${child.y + child.height} }"
                    )
                    json.append("}, ")
                }
                continue
            }
            if (child.stringId == null) {
                val id = measurable.layoutId ?: measurable.constraintLayoutId
                child.stringId = id?.toString()
            }
            val frame = frameCache[measurable]?.widget?.frame
            if (frame == null) {
                continue
            }
            json.append(" ${child.stringId}: {")
            json.append(" interpolated : ")
            frame.serialize(json, true)
            json.append("}, ")
        }
        json.append(" }")
        computedLayoutResult = json.toString()
        layoutInformationReceiver?.setLayoutInformation(computedLayoutResult)
    }

    /**
     * Calculates the [Constraints] in one direction that should be used to measure a child,
     * based on the solver measure request. Returns `true` if the constraints correspond to a
     * wrap content measurement.
     */
    private fun obtainConstraints(
        dimensionBehaviour: ConstraintWidget.DimensionBehaviour,
        dimension: Int,
        matchConstraintDefaultDimension: Int,
        measureStrategy: Int,
        otherDimensionResolved: Boolean,
        currentDimensionResolved: Boolean,
        rootMaxConstraint: Int,
        outConstraints: IntArray
    ): Boolean = when (dimensionBehaviour) {
        FIXED -> {
            outConstraints[0] = dimension
            outConstraints[1] = dimension
            false
        }
        WRAP_CONTENT -> {
            outConstraints[0] = 0
            outConstraints[1] = rootMaxConstraint
            true
        }
        MATCH_CONSTRAINT -> {
            if (DEBUG) {
                Log.d("CCL", "Measure strategy $measureStrategy")
                Log.d("CCL", "DW $matchConstraintDefaultDimension")
                Log.d("CCL", "ODR $otherDimensionResolved")
                Log.d("CCL", "IRH $currentDimensionResolved")
            }
            val useDimension = currentDimensionResolved ||
                (measureStrategy == TRY_GIVEN_DIMENSIONS ||
                    measureStrategy == USE_GIVEN_DIMENSIONS) &&
                (measureStrategy == USE_GIVEN_DIMENSIONS ||
                    matchConstraintDefaultDimension != MATCH_CONSTRAINT_WRAP ||
                    otherDimensionResolved)
            if (DEBUG) {
                Log.d("CCL", "UD $useDimension")
            }
            outConstraints[0] = if (useDimension) dimension else 0
            outConstraints[1] = if (useDimension) dimension else rootMaxConstraint
            !useDimension
        }
        MATCH_PARENT -> {
            outConstraints[0] = rootMaxConstraint
            outConstraints[1] = rootMaxConstraint
            false
        }
        else -> {
            error("$dimensionBehaviour is not supported")
        }
    }

    private fun Array<Int>.copyFrom(measure: BasicMeasure.Measure) {
        this[0] = measure.measuredWidth
        this[1] = measure.measuredHeight
        this[2] = measure.measuredBaseline
    }

    fun performMeasure(
        constraints: Constraints,
        layoutDirection: LayoutDirection,
        constraintSet: ConstraintSet,
        measurables: List<Measurable>,
        optimizationLevel: Int,
        measureScope: MeasureScope
    ): IntSize {
        this.density = measureScope
        this.measureScope = measureScope
        // Define the size of the ConstraintLayout.
        state.width(
            if (constraints.hasFixedWidth) {
                SolverDimension.createFixed(constraints.maxWidth)
            } else {
                SolverDimension.createWrap().min(constraints.minWidth)
            }
        )
        state.height(
            if (constraints.hasFixedHeight) {
                SolverDimension.createFixed(constraints.maxHeight)
            } else {
                SolverDimension.createWrap().min(constraints.minHeight)
            }
        )
        // Build constraint set and apply it to the state.
        state.rootIncomingConstraints = constraints
        state.layoutDirection = layoutDirection
        resetMeasureState()
        if (constraintSet.isDirty(measurables)) {
            state.reset()
            constraintSet.applyTo(state, measurables)
            buildMapping(state, measurables)
            state.apply(root)
        } else {
            buildMapping(state, measurables)
        }
        applyRootSize(constraints)
        root.updateHierarchy()

        if (DEBUG) {
            root.debugName = "ConstraintLayout"
            root.children.forEach { child ->
                child.debugName =
                    (child.companionWidget as? Measurable)?.layoutId?.toString() ?: "NOTAG"
            }
            Log.d("CCL", "ConstraintLayout is asked to measure with $constraints")
            Log.d("CCL", root.toDebugString())
            for (child in root.children) {
                Log.d("CCL", child.toDebugString())
            }
        }

        // No need to set sizes and size modes as we passed them to the state above.
        root.optimizationLevel = optimizationLevel
        root.measure(root.optimizationLevel, 0, 0, 0, 0, 0, 0, 0, 0)

        for (child in root.children) {
            val measurable = child.companionWidget
            if (measurable !is Measurable) continue
            val placeable = placeables[measurable]
            val currentWidth = placeable?.width
            val currentHeight = placeable?.height
            if (child.width != currentWidth || child.height != currentHeight) {
                if (DEBUG) {
                    Log.d(
                        "CCL",
                        "Final measurement for ${measurable.layoutId} " +
                            "to confirm size ${child.width} ${child.height}"
                    )
                }
                measurable.measure(Constraints.fixed(child.width, child.height))
                    .also { placeables[measurable] = it }
            }
        }
        if (DEBUG) {
            Log.d("CCL", "ConstraintLayout is at the end ${root.width} ${root.height}")
        }
        return IntSize(root.width, root.height)
    }

    internal fun resetMeasureState() {
        placeables.clear()
        lastMeasures.clear()
        frameCache.clear()
    }

    protected fun applyRootSize(constraints: Constraints) {
        root.width = constraints.maxWidth
        root.height = constraints.maxHeight
        forcedScaleFactor = Float.NaN
        if (layoutInformationReceiver != null &&
            layoutInformationReceiver?.getForcedWidth() != Int.MIN_VALUE
        ) {
            val forcedWidth = layoutInformationReceiver!!.getForcedWidth()
            if (forcedWidth > root.width) {
                val scale = root.width / forcedWidth.toFloat()
                forcedScaleFactor = scale
            } else {
                forcedScaleFactor = 1f
            }
            root.width = forcedWidth
        }
        if (layoutInformationReceiver != null &&
            layoutInformationReceiver?.getForcedHeight() != Int.MIN_VALUE
        ) {
            val forcedHeight = layoutInformationReceiver!!.getForcedHeight()
            var scaleFactor = 1f
            if (forcedScaleFactor.isNaN()) {
                forcedScaleFactor = 1f
            }
            if (forcedHeight > root.height) {
                scaleFactor = root.height / forcedHeight.toFloat()
            }
            if (scaleFactor < forcedScaleFactor) {
                forcedScaleFactor = scaleFactor
            }
            root.height = forcedHeight
        }
    }

    fun Placeable.PlacementScope.performLayout(measurables: List<Measurable>) {
        if (frameCache.isEmpty()) {
            for (child in root.children) {
                val measurable = child.companionWidget
                if (measurable !is Measurable) continue
                val frame = WidgetFrame(child.frame.update())
                frameCache[measurable] = frame
            }
        }
        measurables.fastForEach { measurable ->
            val frame = frameCache[measurable] ?: return
            val placeable = placeables[measurable] ?: return
            placeWithFrameTransform(placeable, frame)
        }
        if (layoutInformationReceiver?.getLayoutInformationMode() == LayoutInfoFlags.BOUNDS) {
            computeLayoutResult()
        }
    }

    override fun didMeasures() {}

    @Composable
    fun BoxScope.drawDebugBounds(forcedScaleFactor: Float) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawDebugBounds(forcedScaleFactor)
        }
    }

    fun DrawScope.drawDebugBounds(forcedScaleFactor: Float) {
        val w = layoutCurrentWidth * forcedScaleFactor
        val h = layoutCurrentHeight * forcedScaleFactor
        var dx = (size.width - w) / 2f
        var dy = (size.height - h) / 2f
        var color = Color.White
        drawLine(color, Offset(dx, dy), Offset(dx + w, dy))
        drawLine(color, Offset(dx + w, dy), Offset(dx + w, dy + h))
        drawLine(color, Offset(dx + w, dy + h), Offset(dx, dy + h))
        drawLine(color, Offset(dx, dy + h), Offset(dx, dy))
        dx += 1
        dy += 1
        color = Color.Black
        drawLine(color, Offset(dx, dy), Offset(dx + w, dy))
        drawLine(color, Offset(dx + w, dy), Offset(dx + w, dy + h))
        drawLine(color, Offset(dx + w, dy + h), Offset(dx, dy + h))
        drawLine(color, Offset(dx, dy + h), Offset(dx, dy))
    }

    private var designElements = arrayListOf<ConstraintSetParser.DesignElement>()

    private fun getColor(str: String?, defaultColor: Color = Color.Black): Color {
        if (str != null && str.startsWith('#')) {
            var str2 = str.substring(1)
            if (str2.length == 6) {
                str2 = "FF$str2"
            }
            try {
                return Color(java.lang.Long.parseLong(str2, 16).toInt())
            } catch (e: Exception) {
                return defaultColor
            }
        }
        return defaultColor
    }

    private fun getTextStyle(params: HashMap<String, String>): TextStyle {
        val fontSizeString = params["size"]
        var fontSize = TextUnit.Unspecified
        if (fontSizeString != null) {
            fontSize = fontSizeString.toFloat().sp
        }
        var textColor = getColor(params["color"])
        return TextStyle(fontSize = fontSize, color = textColor)
    }

    @Composable
    fun createDesignElements() {
        for (element in designElements) {
            var id = element.id
            var function = DesignElements.map[element.type]
            if (function != null) {
                function(id, element.params)
            } else {
                when (element.type) {
                    "button" -> {
                        val text = element.params["text"] ?: "text"
                        val colorBackground =
                            getColor(element.params["backgroundColor"], Color.LightGray)
                        BasicText(
                            modifier = Modifier
                                .layoutId(id)
                                .clip(RoundedCornerShape(20))
                                .background(colorBackground)
                                .padding(8.dp),
                            text = text, style = getTextStyle(element.params)
                        )
                    }
                    "box" -> {
                        val text = element.params["text"] ?: ""
                        val colorBackground =
                            getColor(element.params["backgroundColor"], Color.LightGray)
                        Box(
                            modifier = Modifier
                                .layoutId(id)
                                .background(colorBackground)
                        ) {
                            BasicText(
                                modifier = Modifier.padding(8.dp),
                                text = text, style = getTextStyle(element.params)
                            )
                        }
                    }
                    "text" -> {
                        val text = element.params["text"] ?: "text"
                        BasicText(
                            modifier = Modifier.layoutId(id),
                            text = text, style = getTextStyle(element.params)
                        )
                    }
                    "textfield" -> {
                        val text = element.params["text"] ?: "text"
                        BasicTextField(
                            modifier = Modifier.layoutId(id),
                            value = text,
                            onValueChange = {}
                        )
                    }
                    "image" -> {
                        Image(
                            modifier = Modifier.layoutId(id),
                            painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                            contentDescription = "Placeholder Image"
                        )
                    }
                }
            }
        }
    }

    fun parseDesignElements(constraintSet: ConstraintSet) {
        if (constraintSet is JSONConstraintSet) {
            constraintSet.emitDesignElements(designElements)
        }
    }
}

fun Placeable.PlacementScope.placeWithFrameTransform(placeable: Placeable, frame: WidgetFrame, offset: IntOffset = IntOffset.Zero) {
    if (frame.isDefaultTransform) {
        val x = frame.left - offset.x
        val y = frame.top - offset.y
        placeable.place(IntOffset(x, y))
    } else {
        val layerBlock: GraphicsLayerScope.() -> Unit = {
            if (!frame.pivotX.isNaN() || !frame.pivotY.isNaN()) {
                val pivotX = if (frame.pivotX.isNaN()) 0.5f else frame.pivotX
                val pivotY = if (frame.pivotY.isNaN()) 0.5f else frame.pivotY
                transformOrigin = TransformOrigin(pivotX, pivotY)
            }
            if (!frame.rotationX.isNaN()) {
                rotationX = frame.rotationX
            }
            if (!frame.rotationY.isNaN()) {
                rotationY = frame.rotationY
            }
            if (!frame.rotationZ.isNaN()) {
                rotationZ = frame.rotationZ
            }
            if (!frame.translationX.isNaN()) {
                translationX = frame.translationX
            }
            if (!frame.translationY.isNaN()) {
                translationY = frame.translationY
            }
            if (!frame.translationZ.isNaN()) {
                shadowElevation = frame.translationZ
            }
            if (!frame.scaleX.isNaN() || !frame.scaleY.isNaN()) {
                scaleX = if (frame.scaleX.isNaN()) 1f else frame.scaleX
                scaleY = if (frame.scaleY.isNaN()) 1f else frame.scaleY
            }
            if (!frame.alpha.isNaN()) {
                alpha = frame.alpha
            }
        }
        val x = frame.left - offset.x
        val y = frame.top - offset.y
        val zIndex = if (frame.translationZ.isNaN()) 0f else frame.translationZ
        placeable.placeWithLayer(
            x,
            y,
            layerBlock = layerBlock,
            zIndex = zIndex
        )
    }
}

object DesignElements {
    var map = HashMap<String, @Composable (String, HashMap<String, String>) -> Unit>()
    fun define(
        name: String,
        function: @Composable (String, HashMap<String, String>) -> Unit
    ) {
        map[name] = function
    }
}

/**
 * Maps ID and Tag to each compose [Measurable] into [state].
 *
 * The ID could be provided from [androidx.compose.ui.layout.layoutId],
 * [ConstraintLayoutParentData.ref] or [ConstraintLayoutTagParentData.constraintLayoutId].
 *
 * The Tag is set from [ConstraintLayoutTagParentData.constraintLayoutTag].
 */
internal fun buildMapping(state: State, measurables: List<Measurable>) {
    measurables.fastForEach { measurable ->
        val id = measurable.layoutId ?: measurable.constraintLayoutId ?: createId()
        // Map the id and the measurable, to be retrieved later during measurement.
        state.map(id, measurable)
        val tag = measurable.constraintLayoutTag
        if (tag != null && tag is String && id is String) {
            state.setTag(id, tag)
        }
    }
}

internal typealias SolverDimension = androidx.constraintlayout.core.state.Dimension
internal typealias SolverState = androidx.constraintlayout.core.state.State
internal typealias SolverDirection = androidx.constraintlayout.core.state.State.Direction
internal typealias SolverChain = androidx.constraintlayout.core.state.State.Chain

private val DEBUG = false
private fun ConstraintWidget.toDebugString() =
    "$debugName " +
        "width $width minWidth $minWidth maxWidth $maxWidth " +
        "height $height minHeight $minHeight maxHeight $maxHeight " +
        "HDB $horizontalDimensionBehaviour VDB $verticalDimensionBehaviour " +
        "MCW $mMatchConstraintDefaultWidth MCH $mMatchConstraintDefaultHeight " +
        "percentW $mMatchConstraintPercentWidth percentH $mMatchConstraintPercentHeight"

enum class LayoutInfoFlags {
    NONE,
    BOUNDS
}