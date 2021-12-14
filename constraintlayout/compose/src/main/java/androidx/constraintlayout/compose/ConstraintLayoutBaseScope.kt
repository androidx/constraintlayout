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

import androidx.annotation.FloatRange
import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.core.state.ConstraintReference
import androidx.constraintlayout.core.widgets.ConstraintWidget

/**
 * Common scope for [ConstraintLayoutScope] and [ConstraintSetScope], the content being shared
 * between the inline DSL API and the ConstraintSet-based API.
 */
abstract class ConstraintLayoutBaseScope {
    protected val tasks = mutableListOf<(State) -> Unit>()

    fun applyTo(state: State) = tasks.forEach { it(state) }

    open fun reset() {
        tasks.clear()
        helperId = HelpersStartId
        helpersHashCode = 0
    }

    @PublishedApi internal var helpersHashCode: Int = 0
    private fun updateHelpersHashCode(value: Int) {
        helpersHashCode = (helpersHashCode * 1009 + value) % 1000000007
    }

    private val HelpersStartId = 1000
    private var helperId = HelpersStartId
    private fun createHelperId() = helperId++

    /**
     * Represents a vertical anchor (e.g. start/end of a layout, guideline) that layouts
     * can link to in their `Modifier.constrainAs` or `constrain` blocks.
     */
    @Stable
    data class VerticalAnchor internal constructor(internal val id: Any, internal val index: Int)

    /**
     * Represents a horizontal anchor (e.g. top/bottom of a layout, guideline) that layouts
     * can link to in their `Modifier.constrainAs` or `constrain` blocks.
     */
    @Stable
    data class HorizontalAnchor internal constructor(internal val id: Any, internal val index: Int)

    /**
     * Represents a horizontal anchor corresponding to the [FirstBaseline] of a layout that other
     * layouts can link to in their `Modifier.constrainAs` or `constrain` blocks.
     */
    // TODO(popam): investigate if this can be just a HorizontalAnchor
    @Stable
    data class BaselineAnchor internal constructor(internal val id: Any)

    /**
     * Creates a guideline at a specific offset from the start of the [ConstraintLayout].
     */
    fun createGuidelineFromStart(offset: Dp): VerticalAnchor {
        val id = createHelperId()
        tasks.add { state ->
            state.verticalGuideline(id).apply {
                if (state.layoutDirection == LayoutDirection.Ltr) start(offset) else end(offset)
            }
        }
        updateHelpersHashCode(1)
        updateHelpersHashCode(offset.hashCode())
        return VerticalAnchor(id, 0)
    }

    /**
     * Creates a guideline at a specific offset from the left of the [ConstraintLayout].
     */
    fun createGuidelineFromAbsoluteLeft(offset: Dp): VerticalAnchor {
        val id = createHelperId()
        tasks.add { state -> state.verticalGuideline(id).apply { start(offset) } }
        updateHelpersHashCode(2)
        updateHelpersHashCode(offset.hashCode())
        return VerticalAnchor(id, 0)
    }

    /**
     * Creates a guideline at a specific offset from the start of the [ConstraintLayout].
     * A [fraction] of 0f will correspond to the start of the [ConstraintLayout], while 1f will
     * correspond to the end.
     */
    fun createGuidelineFromStart(fraction: Float): VerticalAnchor {
        val id = createHelperId()
        tasks.add { state ->
            state.verticalGuideline(id).apply {
                if (state.layoutDirection == LayoutDirection.Ltr) {
                    percent(fraction)
                } else {
                    percent(1f - fraction)
                }
            }
        }
        updateHelpersHashCode(3)
        updateHelpersHashCode(fraction.hashCode())
        return VerticalAnchor(id, 0)
    }

    /**
     * Creates a guideline at a width fraction from the left of the [ConstraintLayout].
     * A [fraction] of 0f will correspond to the left of the [ConstraintLayout], while 1f will
     * correspond to the right.
     */
    // TODO(popam, b/157781990): this is not really percenide
    fun createGuidelineFromAbsoluteLeft(fraction: Float): VerticalAnchor {
        val id = createHelperId()
        tasks.add { state -> state.verticalGuideline(id).apply { percent(fraction) } }
        updateHelpersHashCode(4)
        updateHelpersHashCode(fraction.hashCode())
        return VerticalAnchor(id, 0)
    }

    /**
     * Creates a guideline at a specific offset from the end of the [ConstraintLayout].
     */
    fun createGuidelineFromEnd(offset: Dp): VerticalAnchor {
        val id = createHelperId()
        tasks.add { state ->
            state.verticalGuideline(id).apply {
                if (state.layoutDirection == LayoutDirection.Ltr) end(offset) else start(offset)
            }
        }
        updateHelpersHashCode(5)
        updateHelpersHashCode(offset.hashCode())
        return VerticalAnchor(id, 0)
    }

    /**
     * Creates a guideline at a specific offset from the right of the [ConstraintLayout].
     */
    fun createGuidelineFromAbsoluteRight(offset: Dp): VerticalAnchor {
        val id = createHelperId()
        tasks.add { state -> state.verticalGuideline(id).apply { end(offset) } }
        updateHelpersHashCode(6)
        updateHelpersHashCode(offset.hashCode())
        return VerticalAnchor(id, 0)
    }

    /**
     * Creates a guideline at a width fraction from the end of the [ConstraintLayout].
     * A [fraction] of 0f will correspond to the end of the [ConstraintLayout], while 1f will
     * correspond to the start.
     */
    fun createGuidelineFromEnd(fraction: Float): VerticalAnchor {
        return createGuidelineFromStart(1f - fraction)
    }

    /**
     * Creates a guideline at a width fraction from the right of the [ConstraintLayout].
     * A [fraction] of 0f will correspond to the right of the [ConstraintLayout], while 1f will
     * correspond to the left.
     */
    fun createGuidelineFromAbsoluteRight(fraction: Float): VerticalAnchor {
        return createGuidelineFromAbsoluteLeft(1f - fraction)
    }

    /**
     * Creates a guideline at a specific offset from the top of the [ConstraintLayout].
     */
    fun createGuidelineFromTop(offset: Dp): HorizontalAnchor {
        val id = createHelperId()
        tasks.add { state -> state.horizontalGuideline(id).apply { start(offset) } }
        updateHelpersHashCode(7)
        updateHelpersHashCode(offset.hashCode())
        return HorizontalAnchor(id, 0)
    }

    /**
     * Creates a guideline at a height percenide from the top of the [ConstraintLayout].
     * A [fraction] of 0f will correspond to the top of the [ConstraintLayout], while 1f will
     * correspond to the bottom.
     */
    fun createGuidelineFromTop(fraction: Float): HorizontalAnchor {
        val id = createHelperId()
        tasks.add { state -> state.horizontalGuideline(id).apply { percent(fraction) } }
        updateHelpersHashCode(8)
        updateHelpersHashCode(fraction.hashCode())
        return HorizontalAnchor(id, 0)
    }

    /**
     * Creates a guideline at a specific offset from the bottom of the [ConstraintLayout].
     */
    fun createGuidelineFromBottom(offset: Dp): HorizontalAnchor {
        val id = createHelperId()
        tasks.add { state -> state.horizontalGuideline(id).apply { end(offset) } }
        updateHelpersHashCode(9)
        updateHelpersHashCode(offset.hashCode())
        return HorizontalAnchor(id, 0)
    }

    /**
     * Creates a guideline at a height percenide from the bottom of the [ConstraintLayout].
     * A [fraction] of 0f will correspond to the bottom of the [ConstraintLayout], while 1f will
     * correspond to the top.
     */
    fun createGuidelineFromBottom(fraction: Float): HorizontalAnchor {
        return createGuidelineFromTop(1f - fraction)
    }

    /**
     * Creates and returns a start barrier, containing the specified elements.
     */
    fun createStartBarrier(
        vararg elements: ConstrainedLayoutReference,
        margin: Dp = 0.dp
    ): VerticalAnchor {
        val id = createHelperId()
        tasks.add { state ->
            val direction = if (state.layoutDirection == LayoutDirection.Ltr) {
                SolverDirection.LEFT
            } else {
                SolverDirection.RIGHT
            }
            state.barrier(id, direction).apply {
                add(*(elements.map { it.id }.toTypedArray()))
            }.margin(state.convertDimension(margin))
        }
        updateHelpersHashCode(10)
        elements.forEach { updateHelpersHashCode(it.hashCode()) }
        updateHelpersHashCode(margin.hashCode())
        return VerticalAnchor(id, 0)
    }

    /**
     * Creates and returns a left barrier, containing the specified elements.
     */
    fun createAbsoluteLeftBarrier(
        vararg elements: ConstrainedLayoutReference,
        margin: Dp = 0.dp
    ): VerticalAnchor {
        val id = createHelperId()
        tasks.add { state ->
            state.barrier(id, SolverDirection.LEFT).apply {
                add(*(elements.map { it.id }.toTypedArray()))
            }.margin(state.convertDimension(margin))
        }
        updateHelpersHashCode(11)
        elements.forEach { updateHelpersHashCode(it.hashCode()) }
        updateHelpersHashCode(margin.hashCode())
        return VerticalAnchor(id, 0)
    }

    /**
     * Creates and returns a top barrier, containing the specified elements.
     */
    fun createTopBarrier(
        vararg elements: ConstrainedLayoutReference,
        margin: Dp = 0.dp
    ): HorizontalAnchor {
        val id = createHelperId()
        tasks.add { state ->
            state.barrier(id, SolverDirection.TOP).apply {
                add(*(elements.map { it.id }.toTypedArray()))
            }.margin(state.convertDimension(margin))
        }
        updateHelpersHashCode(12)
        elements.forEach { updateHelpersHashCode(it.hashCode()) }
        updateHelpersHashCode(margin.hashCode())
        return HorizontalAnchor(id, 0)
    }

    /**
     * Creates and returns an end barrier, containing the specified elements.
     */
    fun createEndBarrier(
        vararg elements: ConstrainedLayoutReference,
        margin: Dp = 0.dp
    ): VerticalAnchor {
        val id = createHelperId()
        tasks.add { state ->
            val direction = if (state.layoutDirection == LayoutDirection.Ltr) {
                SolverDirection.RIGHT
            } else {
                SolverDirection.LEFT
            }
            state.barrier(id, direction).apply {
                add(*(elements.map { it.id }.toTypedArray()))
            }.margin(state.convertDimension(margin))
        }
        updateHelpersHashCode(13)
        elements.forEach { updateHelpersHashCode(it.hashCode()) }
        updateHelpersHashCode(margin.hashCode())
        return VerticalAnchor(id, 0)
    }

    /**
     * Creates and returns a right barrier, containing the specified elements.
     */
    fun createAbsoluteRightBarrier(
        vararg elements: ConstrainedLayoutReference,
        margin: Dp = 0.dp
    ): VerticalAnchor {
        val id = createHelperId()
        tasks.add { state ->
            state.barrier(id, SolverDirection.RIGHT).apply {
                add(*(elements.map { it.id }.toTypedArray()))
            }.margin(state.convertDimension(margin))
        }
        updateHelpersHashCode(14)
        elements.forEach { updateHelpersHashCode(it.hashCode()) }
        updateHelpersHashCode(margin.hashCode())
        return VerticalAnchor(id, 0)
    }

    /**
     * Creates and returns a bottom barrier, containing the specified elements.
     */
    fun createBottomBarrier(
        vararg elements: ConstrainedLayoutReference,
        margin: Dp = 0.dp
    ): HorizontalAnchor {
        val id = createHelperId()
        tasks.add { state ->
            state.barrier(id, SolverDirection.BOTTOM).apply {
                add(*(elements.map { it.id }.toTypedArray()))
            }.margin(state.convertDimension(margin))
        }
        updateHelpersHashCode(15)
        elements.forEach { updateHelpersHashCode(it.hashCode()) }
        updateHelpersHashCode(margin.hashCode())
        return HorizontalAnchor(id, 0)
    }

    /**
     * Creates a horizontal chain including the referenced layouts.
     */
    // TODO(popam, b/157783937): this API should be improved
    fun createHorizontalChain(
        vararg elements: ConstrainedLayoutReference,
        chainStyle: ChainStyle = ChainStyle.Spread
    ) {
        tasks.add { state ->
            state.horizontalChain(*(elements.map { it.id }.toTypedArray()))
                .also { it.style(chainStyle.style) }
                .apply()
            if (chainStyle.bias != null) {
                state.constraints(elements[0].id).horizontalBias(chainStyle.bias)
            }
        }
        updateHelpersHashCode(16)
        elements.forEach { updateHelpersHashCode(it.hashCode()) }
        updateHelpersHashCode(chainStyle.hashCode())
    }

    /**
     * Creates a vertical chain including the referenced layouts.
     */
    // TODO(popam, b/157783937): this API should be improved
    fun createVerticalChain(
        vararg elements: ConstrainedLayoutReference,
        chainStyle: ChainStyle = ChainStyle.Spread
    ) {
        tasks.add { state ->
            state.verticalChain(*(elements.map { it.id }.toTypedArray()))
                .also { it.style(chainStyle.style) }
                .apply()
            if (chainStyle.bias != null) {
                state.constraints(elements[0].id).verticalBias(chainStyle.bias)
            }
        }
        updateHelpersHashCode(17)
        elements.forEach { updateHelpersHashCode(it.hashCode()) }
        updateHelpersHashCode(chainStyle.hashCode())
    }
}

/**
 * Represents a layout within a [ConstraintLayout].
 */
@Stable
class ConstrainedLayoutReference(val id: Any) {
    /**
     * The start anchor of this layout. Represents left in LTR layout direction, or right in RTL.
     */
    @Stable
    val start = ConstraintLayoutBaseScope.VerticalAnchor(id, -2)

    /**
     * The left anchor of this layout.
     */
    @Stable
    val absoluteLeft = ConstraintLayoutBaseScope.VerticalAnchor(id, 0)

    /**
     * The top anchor of this layout.
     */
    @Stable
    val top = ConstraintLayoutBaseScope.HorizontalAnchor(id, 0)

    /**
     * The end anchor of this layout. Represents right in LTR layout direction, or left in RTL.
     */
    @Stable
    val end = ConstraintLayoutBaseScope.VerticalAnchor(id, -1)

    /**
     * The right anchor of this layout.
     */
    @Stable
    val absoluteRight = ConstraintLayoutBaseScope.VerticalAnchor(id, 1)

    /**
     * The bottom anchor of this layout.
     */
    @Stable
    val bottom = ConstraintLayoutBaseScope.HorizontalAnchor(id, 1)

    /**
     * The baseline anchor of this layout.
     */
    @Stable
    val baseline = ConstraintLayoutBaseScope.BaselineAnchor(id)
}

/**
 * The style of a horizontal or vertical chain.
 */
@Immutable
class ChainStyle internal constructor(
    internal val style: SolverChain,
    internal val bias: Float? = null
) {
    companion object {
        /**
         * A chain style that evenly distributes the contained layouts.
         */
        @Stable
        val Spread = ChainStyle(SolverChain.SPREAD)

        /**
         * A chain style where the first and last layouts are affixed to the constraints
         * on each end of the chain and the rest are evenly distributed.
         */
        @Stable
        val SpreadInside = ChainStyle(SolverChain.SPREAD_INSIDE)

        /**
         * A chain style where the contained layouts are packed together and placed to the
         * center of the available space.
         */
        @Stable
        val Packed = Packed(0.5f)

        /**
         * A chain style where the contained layouts are packed together and placed in
         * the available space according to a given [bias].
         */
        @Stable
        fun Packed(bias: Float) = ChainStyle(SolverChain.PACKED, bias)
    }
}

/**
 * The overall visibility of a widget in a [ConstraintLayout].
 */
@Immutable
class Visibility internal constructor(
    internal val solverValue: Int
) {
    companion object {
        /**
         * Indicates that the widget will be painted in the [ConstraintLayout]. All render-time
         * transforms will apply normally.
         */
        @Stable
        val Visible = Visibility(ConstraintWidget.VISIBLE)

        /**
         * The widget will not be painted in the [ConstraintLayout] but its dimensions and constraints
         * will still apply.
         *
         * Equivalent to forcing the alpha to 0.0.
         */
        @Stable
        val Invisible = Visibility(ConstraintWidget.INVISIBLE)

        /**
         * Like [Invisible], but the dimensions of the widget will collapse to (0,0), the
         * constraints will still apply.
         */
        @Stable
        val Gone = Visibility(ConstraintWidget.GONE)
    }
}

/**
 * Scope that can be used to constrain a layout.
 *
 * Used within `Modifier.constrainAs` from the inline DSL API. And within `constrain` from the
 * ConstraintSet-based API.
 */
@LayoutScopeMarker
@Stable
class ConstrainScope internal constructor(internal val id: Any) {
    internal val tasks = mutableListOf<(State) -> Unit>()
    internal fun applyTo(state: State) = tasks.forEach { it(state) }

    /**
     * Reference to the [ConstraintLayout] itself, which can be used to specify constraints
     * between itself and its children.
     */
    val parent = ConstrainedLayoutReference(SolverState.PARENT)

    /**
     * The start anchor of the layout - can be constrained using [VerticalAnchorable.linkTo].
     */
    val start = VerticalAnchorable(id, -2)

    /**
     * The left anchor of the layout - can be constrained using [VerticalAnchorable.linkTo].
     */
    val absoluteLeft = VerticalAnchorable(id, 0)

    /**
     * The top anchor of the layout - can be constrained using [HorizontalAnchorable.linkTo].
     */
    val top = HorizontalAnchorable(id, 0)

    /**
     * The end anchor of the layout - can be constrained using [VerticalAnchorable.linkTo].
     */
    val end = VerticalAnchorable(id, -1)

    /**
     * The right anchor of the layout - can be constrained using [VerticalAnchorable.linkTo].
     */
    val absoluteRight = VerticalAnchorable(id, 1)

    /**
     * The bottom anchor of the layout - can be constrained using [HorizontalAnchorable.linkTo].
     */
    val bottom = HorizontalAnchorable(id, 1)

    /**
     * The [FirstBaseline] of the layout - can be constrained using [BaselineAnchorable.linkTo].
     */
    val baseline = BaselineAnchorable(id)

    /**
     * The width of the [ConstraintLayout] child.
     */
    var width: Dimension = Dimension.wrapContent
        set(value) {
            field = value
            tasks.add { state ->
                state.constraints(id).width(
                    (value as DimensionDescription).toSolverDimension(state)
                )
            }
        }

    /**
     * The height of the [ConstraintLayout] child.
     */
    var height: Dimension = Dimension.wrapContent
        set(value) {
            field = value
            tasks.add { state ->
                state.constraints(id).height(
                    (value as DimensionDescription).toSolverDimension(state)
                )
            }
        }

    /**
     * The overall visibility of the [ConstraintLayout] child.
     *
     * [Visibility.Visible] by default.
     */
    var visibility: Visibility = Visibility.Visible
        set(value) {
            field = value
            tasks.add { state ->
                with(state.constraints(id)) {
                    visibility(value.solverValue)
                    if (value == Visibility.Invisible) {
                        // A bit of a hack, this behavior is not defined in :core
                        // Invisible should override alpha
                        alpha(0f)
                    }
                }
            }
        }

    /**
     * The transparency value when rendering the content.
     */
    @FloatRange(from = 0.0, to = 1.0)
    var alpha: Float = 1.0f
        set(value) {
            field = value
            addTransform {
                if (visibility != Visibility.Invisible) {
                    // A bit of a hack, this behavior is not defined in :core
                    // Invisible should override alpha
                    alpha(value)
                }
            }
        }

    /**
     * The percent scaling value on the horizontal axis. Where 1 is 100%.
     */
    var scaleX: Float = 1.0f
        set(value) {
            field = value
            addTransform { scaleX(value) }
        }

    /**
     * The percent scaling value on the vertical axis. Where 1 is 100%.
     */
    var scaleY: Float = 1.0f
        set(value) {
            field = value
            addTransform { scaleY(value) }
        }


    /**
     * The degrees to rotate the content over the horizontal axis.
     */
    var rotationX: Float = 0.0f
        set(value) {
            field = value
            addTransform { rotationX(value) }
        }

    /**
     * The degrees to rotate the content over the vertical axis.
     */
    var rotationY: Float = 0.0f
        set(value) {
            field = value
            addTransform { rotationY(value) }
        }

    /**
     * The degrees to rotate the content on the screen plane.
     */
    var rotationZ: Float = 0.0f
        set(value) {
            field = value
            addTransform { rotationZ(value) }
        }

    /**
     * The distance to offset the content over the X axis.
     */
    var translationX: Dp = 0.dp
        set(value) {
            field = value
            addFloatTransformFromDp(value) { floatValue -> translationX(floatValue) }
        }

    /**
     * The distance to offset the content over the Y axis.
     */
    var translationY: Dp = 0.dp
        set(value) {
            field = value
            addFloatTransformFromDp(value) { floatValue -> translationY(floatValue) }
        }

    /**
     * The distance to offset the content over the Z axis.
     */
    var translationZ: Dp = 0.dp
        set(value) {
            field = value
            addFloatTransformFromDp(value) { floatValue -> translationZ(floatValue) }
        }

    /**
     * The X axis offset percent where the content is rotated and scaled.
     *
     * @see [TransformOrigin]
     */
    var pivotX: Float = 0.5f
        set(value) {
            field = value
            addTransform { pivotX(value) }
        }

    /**
     * The Y axis offset percent where the content is rotated and scaled.
     *
     * @see [TransformOrigin]
     */
    var pivotY: Float = 0.5f
        set(value) {
            field = value
            addTransform { pivotY(value) }
        }

    /**
     * Whenever the width is not fixed, this weight may be used by an horizontal Chain to decide how
     * much space assign to this widget.
     */
    var horizontalChainWeight: Float = Float.NaN
        set(value) {
            field = value
            tasks.add { state ->
                state.constraints(id).horizontalChainWeight = value
            }
        }

    /**
     * Whenever the height is not fixed, this weight may be used by a vertical Chain to decide how
     * much space assign to this widget.
     */
    var verticalChainWeight: Float = Float.NaN
        set(value) {
            field = value
            tasks.add { state ->
                state.constraints(id).verticalChainWeight = value
            }
        }

    private fun addTransform(change: ConstraintReference.() -> Unit) =
        tasks.add { state -> change(state.constraints(id)) }

    private fun addFloatTransformFromDp(dpValue: Dp, change: ConstraintReference.(Float) -> Unit) =
        tasks.add { state ->
            (state as? State)?.also {
                state.constraints(id).change(state.convertDimension(dpValue).toFloat())
            }
        }


    /**
     * Represents a vertical side of a layout (i.e start and end) that can be anchored using
     * [linkTo] in their `Modifier.constrainAs` blocks.
     */
    inner class VerticalAnchorable internal constructor(
        internal val id: Any,
        internal val index: Int
    ) {
        /**
         * Adds a link towards a [ConstraintLayoutBaseScope.VerticalAnchor].
         */
        // TODO(popam, b/158069248): add parameter for gone margin
        fun linkTo(anchor: ConstraintLayoutBaseScope.VerticalAnchor, margin: Dp = 0.dp) {
            tasks.add { state ->
                with(state.constraints(id)) {
                    val layoutDirection = state.layoutDirection
                    val index1 = verticalAnchorIndexToFunctionIndex(index, layoutDirection)
                    val index2 = verticalAnchorIndexToFunctionIndex(anchor.index, layoutDirection)
                    verticalAnchorFunctions[index1][index2]
                        .invoke(this, anchor.id, state.layoutDirection)
                        .margin(margin)
                }
            }
        }
    }

    /**
     * Represents a horizontal side of a layout (i.e top and bottom) that can be anchored using
     * [linkTo] in their `Modifier.constrainAs` blocks.
     */
    inner class HorizontalAnchorable internal constructor(
        internal val tag: Any,
        internal val index: Int
    ) {
        /**
         * Adds a link towards a [ConstraintLayoutBaseScope.HorizontalAnchor].
         */
        // TODO(popam, b/158069248): add parameter for gone margin
        fun linkTo(anchor: ConstraintLayoutBaseScope.HorizontalAnchor, margin: Dp = 0.dp) {
            tasks.add { state ->
                with(state.constraints(id)) {
                    horizontalAnchorFunctions[index][anchor.index]
                        .invoke(this, anchor.id)
                        .margin(margin)
                }
            }
        }
    }

    /**
     * Represents the [FirstBaseline] of a layout that can be anchored
     * using [linkTo] in their `Modifier.constrainAs` blocks.
     */
    inner class BaselineAnchorable internal constructor(internal val id: Any) {
        /**
         * Adds a link towards a [ConstraintLayoutBaseScope.BaselineAnchor].
         */
        // TODO(popam, b/158069248): add parameter for gone margin
        fun linkTo(anchor: ConstraintLayoutBaseScope.BaselineAnchor, margin: Dp = 0.dp) {
            tasks.add { state ->
                (state as? State)?.let {
                    it.baselineNeededFor(id)
                    it.baselineNeededFor(anchor.id)
                }
                with(state.constraints(id)) {
                    baselineAnchorFunction.invoke(this, anchor.id).margin(margin)
                }
            }
        }
    }

    /**
     * Adds both start and end links towards other [ConstraintLayoutBaseScope.VerticalAnchor]s.
     */
    // TODO(popam, b/158069248): add parameter for gone margin
    fun linkTo(
        start: ConstraintLayoutBaseScope.VerticalAnchor,
        end: ConstraintLayoutBaseScope.VerticalAnchor,
        startMargin: Dp = 0.dp,
        endMargin: Dp = 0.dp,
        @FloatRange(from = 0.0, to = 1.0) bias: Float = 0.5f
    ) {
        this@ConstrainScope.start.linkTo(start, startMargin)
        this@ConstrainScope.end.linkTo(end, endMargin)
        tasks.add { state ->
            val resolvedBias = if (state.layoutDirection == LayoutDirection.Rtl) 1 - bias else bias
            state.constraints(id).horizontalBias(resolvedBias)
        }
    }

    /**
     * Adds both top and bottom links towards other [ConstraintLayoutBaseScope.HorizontalAnchor]s.
     */
    // TODO(popam, b/158069248): add parameter for gone margin
    fun linkTo(
        top: ConstraintLayoutBaseScope.HorizontalAnchor,
        bottom: ConstraintLayoutBaseScope.HorizontalAnchor,
        topMargin: Dp = 0.dp,
        bottomMargin: Dp = 0.dp,
        @FloatRange(from = 0.0, to = 1.0) bias: Float = 0.5f
    ) {
        this@ConstrainScope.top.linkTo(top, topMargin)
        this@ConstrainScope.bottom.linkTo(bottom, bottomMargin)
        tasks.add { state ->
            state.constraints(id).verticalBias(bias)
        }
    }

    /**
     * Adds all start, top, end, bottom links towards
     * other [ConstraintLayoutBaseScope.HorizontalAnchor]s.
     */
    // TODO(popam, b/158069248): add parameter for gone margin
    fun linkTo(
        start: ConstraintLayoutBaseScope.VerticalAnchor,
        top: ConstraintLayoutBaseScope.HorizontalAnchor,
        end: ConstraintLayoutBaseScope.VerticalAnchor,
        bottom: ConstraintLayoutBaseScope.HorizontalAnchor,
        startMargin: Dp = 0.dp,
        topMargin: Dp = 0.dp,
        endMargin: Dp = 0.dp,
        bottomMargin: Dp = 0.dp,
        @FloatRange(from = 0.0, to = 1.0) horizontalBias: Float = 0.5f,
        @FloatRange(from = 0.0, to = 1.0) verticalBias: Float = 0.5f
    ) {
        linkTo(start, end, startMargin, endMargin, horizontalBias)
        linkTo(top, bottom, topMargin, bottomMargin, verticalBias)
    }

    /**
     * Adds all start, top, end, bottom links towards the corresponding anchors of [other].
     * This will center the current layout inside or around (depending on size) [other].
     */
    fun centerTo(other: ConstrainedLayoutReference) {
        linkTo(other.start, other.top, other.end, other.bottom)
    }

    /**
     * Adds start and end links towards the corresponding anchors of [other].
     * This will center horizontally the current layout inside or around (depending on size)
     * [other].
     */
    fun centerHorizontallyTo(
        other: ConstrainedLayoutReference,
        @FloatRange(from = 0.0f.toDouble(), to = 1.0f.toDouble()) bias: Float = 0.5f
    ) {
        linkTo(other.start, other.end, bias = bias)
    }

    /**
     * Adds top and bottom links towards the corresponding anchors of [other].
     * This will center vertically the current layout inside or around (depending on size)
     * [other].
     */
    fun centerVerticallyTo(
        other: ConstrainedLayoutReference,
        @FloatRange(from = 0.0f.toDouble(), to = 1.0f.toDouble()) bias: Float = 0.5f
    ) {
        linkTo(other.top, other.bottom, bias = bias)
    }

    /**
     * Adds start and end links towards a vertical [anchor].
     * This will center the current layout around the vertical [anchor].
     */
    fun centerAround(anchor: ConstraintLayoutBaseScope.VerticalAnchor) {
        linkTo(anchor, anchor)
    }

    /**
     * Adds top and bottom links towards a horizontal [anchor].
     * This will center the current layout around the horizontal [anchor].
     */
    fun centerAround(anchor: ConstraintLayoutBaseScope.HorizontalAnchor) {
        linkTo(anchor, anchor)
    }

    /**
     * Set a circular constraint relative to the center of [other].
     * This will position the current widget at a relative angle and distance from [other].
     */
    fun circular(other: ConstrainedLayoutReference, angle: Float, distance: Dp) {
        tasks.add { state ->
            state.constraints(id)
                .circularConstraint(other.id, angle, state.convertDimension(distance).toFloat())
        }
    }

    /**
     * Clear the constraints on the horizontal axis (left, right, start, end).
     */
    fun clearHorizontal() {
        tasks.add { state ->
            state.constraints(id).clearHorizontal()
        }
    }

    /**
     * Clear the constraints on the vertical axis (top, bottom, baseline).
     */
    fun clearVertical() {
        tasks.add { state ->
            state.constraints(id).clearVertical()
        }
    }

    /**
     * Clear all constraints (vertical, horizontal, circular).
     */
    fun clearConstraints() {
        tasks.add { state ->
            state.constraints(id).clear()
        }
    }

    internal companion object {
        val verticalAnchorFunctions:
                Array<Array<ConstraintReference.(Any, LayoutDirection) -> ConstraintReference>> =
            arrayOf(
                arrayOf(
                    { other, layoutDirection ->
                        clearLeft(layoutDirection); leftToLeft(other)
                    },
                    { other, layoutDirection ->
                        clearLeft(layoutDirection); leftToRight(other)
                    }
                ),
                arrayOf(
                    { other, layoutDirection ->
                        clearRight(layoutDirection); rightToLeft(other)
                    },
                    { other, layoutDirection ->
                        clearRight(layoutDirection); rightToRight(other)
                    }
                )
            )

        private fun ConstraintReference.clearLeft(layoutDirection: LayoutDirection) {
            leftToLeft(null)
            leftToRight(null)
            when (layoutDirection) {
                LayoutDirection.Ltr -> {
                    startToStart(null); startToEnd(null)
                }
                LayoutDirection.Rtl -> {
                    endToStart(null); endToEnd(null)
                }
            }
        }

        private fun ConstraintReference.clearRight(layoutDirection: LayoutDirection) {
            rightToLeft(null)
            rightToRight(null)
            when (layoutDirection) {
                LayoutDirection.Ltr -> {
                    endToStart(null); endToEnd(null)
                }
                LayoutDirection.Rtl -> {
                    startToStart(null); startToEnd(null)
                }
            }
        }

        /**
         * Converts the index (-2 -> start, -1 -> end, 0 -> left, 1 -> right) to an index in
         * the arrays above (0 -> left, 1 -> right).
         */
        // TODO(popam, b/157886946): this is temporary until we can use CL's own RTL handling
        fun verticalAnchorIndexToFunctionIndex(index: Int, layoutDirection: LayoutDirection) =
            when {
                index >= 0 -> index // already left or right
                layoutDirection == LayoutDirection.Ltr -> 2 + index // start -> left, end -> right
                else -> -index - 1 // start -> right, end -> left
            }

        val horizontalAnchorFunctions:
                Array<Array<ConstraintReference.(Any) -> ConstraintReference>> = arrayOf(
            arrayOf(
                { other -> topToBottom(null); baselineToBaseline(null); topToTop(other) },
                { other -> topToTop(null); baselineToBaseline(null); topToBottom(other) }
            ),
            arrayOf(
                { other -> bottomToBottom(null); baselineToBaseline(null); bottomToTop(other) },
                { other -> bottomToTop(null); baselineToBaseline(null); bottomToBottom(other) }
            )
        )
        val baselineAnchorFunction: ConstraintReference.(Any) -> ConstraintReference =
            { other ->
                topToTop(null)
                topToBottom(null)
                bottomToTop(null)
                bottomToBottom(null)
                baselineToBaseline(other)
            }
    }
}