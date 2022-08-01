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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateObserver
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.core.parser.CLArray
import androidx.constraintlayout.core.parser.CLContainer
import androidx.constraintlayout.core.parser.CLNumber
import androidx.constraintlayout.core.parser.CLObject
import androidx.constraintlayout.core.parser.CLString
import androidx.constraintlayout.core.state.CorePixelDp
import kotlin.properties.ObservableProperty
import kotlin.reflect.KProperty

@Composable
fun Transition(
    from: String = "start",
    to: String = "end",
    transitionContent: TransitionScope.() -> Unit
): Transition {
    val dpToPixel = with(LocalDensity.current) { 1.dp.toPx() }
    val transitionScope = remember(from, to) { TransitionScope() }
    val snapshotObserver = remember {
        // We use a Snapshot observer to know when state within the DSL has changed and recompose
        // the transition object
        SnapshotStateObserver {
            it()
        }
    }
    remember {
        object : RememberObserver {
            override fun onAbandoned() {
                // TODO: Investigate if we need to do something here
            }

            override fun onForgotten() {
                snapshotObserver.stop()
                snapshotObserver.clear()
            }

            override fun onRemembered() {
                snapshotObserver.start()
            }
        }
    }
    snapshotObserver.observeReads(currentRecomposeScope, {
        it.invalidate()
    }) {
        transitionScope.reset()
        // Observe state changes within the DSL, to know when to invalidate and update the
        // Transition
        transitionScope.transitionContent()
    }
    return remember {
        TransitionImpl(
            transitionScope.getObject(),
            CorePixelDp { dpValue -> dpValue * dpToPixel }
        )
    }
}

class TransitionScope internal constructor() {
    private val containerObject = CLObject(charArrayOf())

    private val keyFramesObject = CLObject(charArrayOf())
    private val keyAttributesArray = CLArray(charArrayOf())
    private val keyPositionsArray = CLArray(charArrayOf())
    private val keyCyclesArray = CLArray(charArrayOf())

    private val onSwipeObject = CLObject(charArrayOf())

    internal fun reset() {
        containerObject.clear()
        keyFramesObject.clear()
        keyAttributesArray.clear()
        onSwipeObject.clear()
    }

    private fun addKeyAttributesIfMissing() {
        containerObject.put("KeyFrames", keyFramesObject)
        keyFramesObject.put("KeyAttributes", keyAttributesArray)
    }

    private fun addKeyPositionsIfMissing() {
        containerObject.put("KeyFrames", keyFramesObject)
        keyFramesObject.put("KeyPositions", keyPositionsArray)
    }

    private fun addKeyCyclesIfMissing() {
        containerObject.put("KeyFrames", keyFramesObject)
        keyFramesObject.put("KeyCycles", keyCyclesArray)
    }

    var motionArc: Arc = Arc.None

    var onSwipe: OnSwipe? = null

    fun keyAttributes(vararg targets: Any, keyAttributesContent: KeyAttributesScope.() -> Unit) {
        val scope = KeyAttributesScope(*targets)
        keyAttributesContent(scope)
        addKeyAttributesIfMissing()
        keyAttributesArray.add(scope.keyFramePropsObject)
    }

    fun keyPositions(vararg targets: Any, keyPositionsContent: KeyPositionsScope.() -> Unit) {
        val scope = KeyPositionsScope(* targets)
        keyPositionsContent(scope)
        addKeyPositionsIfMissing()
        keyPositionsArray.add(scope.keyFramePropsObject)
    }

    fun keyCycles(vararg targets: Any, keyCyclesContent: KeyCyclesScope.() -> Unit) {
        val scope = KeyCyclesScope(* targets)
        keyCyclesContent(scope)
        addKeyCyclesIfMissing()
        keyCyclesArray.add(scope.keyFramePropsObject)
    }

    internal fun getObject(): CLObject {
        containerObject.putString("pathMotionArc", motionArc.propName)
        onSwipe?.let {
            containerObject.put("onSwipe", onSwipeObject)
            onSwipeObject.putString("direction", it.direction.propName)
            onSwipeObject.putString("mode", it.mode.propName)
            onSwipeObject.putString("anchor", it.anchor.toString())
            onSwipeObject.putString("side", it.side.propName)
            onSwipeObject.putString("touchUp", it.onTouchUp.propName)
        }
        return containerObject
    }
}

open class BaseKeyFrameScope internal constructor(vararg targets: Any) {
    internal val keyFramePropsObject = CLObject(charArrayOf()).apply {
        clear()
    }

    private val targetsContainer = CLArray(charArrayOf())
    protected val framesContainer = CLArray(charArrayOf())

    var easing: Easing by addOnPropertyChange(Easing.Standard)

    init {
        with(keyFramePropsObject) {
            put("target", targetsContainer)
            put("frames", framesContainer)
        }
        targets.forEach {
            val stringChars = it.toString().toCharArray()
            targetsContainer.add(CLString(stringChars).apply {
                start = 0
                end = stringChars.size.toLong() - 1
            })
        }
    }

    private fun addOnPropertyChange(initialValue: Easing) =
        object : ObservableProperty<Easing>(initialValue) {
            override fun afterChange(property: KProperty<*>, oldValue: Easing, newValue: Easing) {
                with(keyFramePropsObject) {
                    putString("transitionEasing", easing.propName)
                }
            }
        }
}

class KeyAttributesScope(vararg targets: Any) : BaseKeyFrameScope(*targets) {
    fun frame(frame: Float, keyFrameContent: KeyAttributeScope.() -> Unit) {
        val scope = KeyAttributeScope()
        keyFrameContent(scope)
        framesContainer.add(CLNumber(frame))
        scope.addToContainer(keyFramePropsObject)
    }
}

class KeyPositionsScope(vararg targets: Any) : BaseKeyFrameScope(* targets) {
    // TODO: Move `type` here

    fun frame(frame: Float, keyFrameContent: KeyPositionScope.() -> Unit) {
        val scope = KeyPositionScope()
        keyFrameContent(scope)
        framesContainer.add(CLNumber(frame))
        scope.addToContainer(keyFramePropsObject)
    }
}

class KeyCyclesScope(vararg targets: Any) : BaseKeyFrameScope(* targets) {
    fun frame(frame: Float, keyFrameContent: KeyCycleScope.() -> Unit) {
        val scope = KeyCycleScope()
        keyFrameContent(scope)
        framesContainer.add(CLNumber(frame))
        scope.addToContainer(keyFramePropsObject)
    }
}

abstract class KeyFrameModifierScope internal constructor() {
    protected val userAttributes = mutableMapOf<String, Any>()

    protected fun <T> addOnPropertyChange(initialValue: T, nameOverride: String? = null) =
        object : ObservableProperty<T>(initialValue) {
            override fun afterChange(property: KProperty<*>, oldValue: T, newValue: T) {
                val name = nameOverride ?: property.name
                if (newValue != null) {
                    userAttributes[name] = newValue
                }
            }
        }

    protected fun <E : NamedPropertyOrValue?> addNameOnPropertyChange(
        initialValue: E,
        nameOverride: String? = null
    ) =
        object : ObservableProperty<E>(initialValue) {
            override fun afterChange(property: KProperty<*>, oldValue: E, newValue: E) {
                val name = nameOverride ?: property.name
                if (newValue != null) {
                    userAttributes[name] = newValue.propName
                }
            }
        }

    fun addToContainer(container: CLContainer) {
        userAttributes.forEach { (name, value) ->
            val array = container.getArrayOrCreate(name)
            when (value) {
                is String -> {
                    val stringChars = value.toCharArray()
                    array.add(CLString(stringChars).apply {
                        start = 0
                        end = stringChars.size.toLong() - 1
                    })
                }
                is Number -> {
                    array.add(CLNumber(value.toFloat()))
                }
            }
        }
    }
}

class KeyAttributeScope : KeyFrameModifierScope() {
    var alpha by addOnPropertyChange(1f, "alpha")
    var scaleX by addOnPropertyChange(1f, "scaleX")
    var scaleY by addOnPropertyChange(1f, "scaleY")
    var rotationX by addOnPropertyChange(0f, "rotationX")
    var rotationY by addOnPropertyChange(0f, "rotationY")
    var rotationZ by addOnPropertyChange(0f, "rotationZ")
    var translationX by addOnPropertyChange(0f, "translationX")
    var translationY by addOnPropertyChange(0f, "translationY")
    var translationZ by addOnPropertyChange(0f, "translationZ")
}

class KeyPositionScope : KeyFrameModifierScope() {
    var percentX by addOnPropertyChange(1f)
    var percentY by addOnPropertyChange(1f)
    var percentWidth by addOnPropertyChange(1f)
    var percentHeight by addOnPropertyChange(0f)
    var curveFit: CurveFit? by addNameOnPropertyChange(null)
    var type: RelativePosition? by addNameOnPropertyChange(null)
}

class KeyCycleScope : KeyFrameModifierScope() {
    var alpha by addOnPropertyChange(1f)
    var scaleX by addOnPropertyChange(1f)
    var scaleY by addOnPropertyChange(1f)
    var rotationX by addOnPropertyChange(0f)
    var rotationY by addOnPropertyChange(0f)
    var rotationZ by addOnPropertyChange(0f)
    var translationX by addOnPropertyChange(0f)
    var translationY by addOnPropertyChange(0f)
    var translationZ by addOnPropertyChange(0f)
    var period by addOnPropertyChange(0f)
    var offset by addOnPropertyChange(0f)
    var phase by addOnPropertyChange(0f)

    // TODO: Add Wave Shape & Custom Wave
}

internal interface NamedPropertyOrValue {
    val propName: String
}

class Easing internal constructor(override val propName: String) : NamedPropertyOrValue {
    companion object {
        val Standard = Easing("standard")
        val Accelerate = Easing("accelerate")
        val Decelerate = Easing("decelerate")
        val Linear = Easing("linear")
        val Anticipate = Easing("anticipate")
        val Overshoot = Easing("overshoot")
        fun Cubic(off0: Float, off1: Float, off2: Float, off3: Float) =
            Easing("cubic($off0, $off1, $off2, $off3)")
    }
}

class Arc internal constructor(internal val propName: String) {
    companion object {
        val None = Arc("none")
        val StartVertical = Arc("startVertical")
        val StartHorizontal = Arc("startHorizontal")
        val Flip = Arc("flip")
    }
}

data class OnSwipe(
    val anchor: Any,
    val side: SwipeSide,
    val direction: SwipeDirection,
    val mode: SwipeMode = SwipeMode.Velocity,
    val onTouchUp: SwipeTouchUp = SwipeTouchUp.AutoComplete
)

class SwipeMode internal constructor(internal val propName: String) {
    companion object {
        val Velocity = SwipeMode("velocity")
        val Spring = SwipeMode("spring")
    }
}

class SwipeTouchUp internal constructor(internal val propName: String) {
    companion object {
        val AutoComplete: SwipeTouchUp = SwipeTouchUp("autocomplete")
        val NeverCompleteStart: SwipeTouchUp = SwipeTouchUp("neverCompleteStart")
    }
}

class SwipeDirection internal constructor(internal val propName: String) {
    companion object {
        val Up: SwipeDirection = SwipeDirection("up")
        val Down: SwipeDirection = SwipeDirection("down")
        val Left: SwipeDirection = SwipeDirection("left")
        val Right: SwipeDirection = SwipeDirection("right")
        val Start: SwipeDirection = SwipeDirection("start")
        val End: SwipeDirection = SwipeDirection("end")
        val ClockWise: SwipeDirection = SwipeDirection("clockwise")
        val AntiClockWise: SwipeDirection = SwipeDirection("anticlockwise")
    }
}

class SwipeSide internal constructor(internal val propName: String) {
    companion object {
        val Top: SwipeSide = SwipeSide("top")
        val Right: SwipeSide = SwipeSide("right")
        val Bottom: SwipeSide = SwipeSide("bottom")
        val Left: SwipeSide = SwipeSide("left")
        val Middle: SwipeSide = SwipeSide("middle")
    }
}

class CurveFit internal constructor(override val propName: String) : NamedPropertyOrValue {
    companion object {
        val Spline: CurveFit = CurveFit("spline")
        val Linear: CurveFit = CurveFit("linear")
    }
}

class RelativePosition internal constructor(override val propName: String) : NamedPropertyOrValue {
    companion object {
        val Delta: RelativePosition = RelativePosition("deltaRelative")
        val Path: RelativePosition = RelativePosition("pathRelative")
        val Parent: RelativePosition = RelativePosition("parentRelative")
    }
}