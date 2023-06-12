/*
 * Copyright (C) 2023 The Android Open Source Project
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

package android.support.drag2d.compose

import android.support.drag2d.lib.MaterialEasing
import android.support.drag2d.lib.MaterialVelocity
import android.support.drag2d.lib.Velocity2D
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.AnimationVector3D
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.FloatTweenSpec
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.VectorizedFiniteAnimationSpec
import kotlin.math.roundToLong

// TODO: Consider changing to composition locals, so that users can use their desired defaults
//  across their codebase
private const val DEFAULT_MAX_VELOCITY = 1000f
private const val DEFAULT_MAX_ACCELERATION = 1000f
private val defaultEasing: MaterialVelocity.Easing by lazy(LazyThreadSafetyMode.NONE) {
    MaterialEasing.EASE_OUT_BACK
}

fun <T> materialVelocity2D(
    durationMs: Int = AnimationConstants.DefaultDurationMillis,
    maxVelocity: Float = DEFAULT_MAX_VELOCITY,
    maxAcceleration: Float = DEFAULT_MAX_ACCELERATION,
    easing: MaterialVelocity.Easing = defaultEasing
): AnimationSpec<T> {
    return Material2DAnimationSpec(
        desiredDurationMs = durationMs,
        maxVelocityA = maxVelocity,
        maxVelocityB = maxVelocity,
        maxAccelerationA = maxAcceleration,
        maxAccelerationB = maxAcceleration,
        materialEasingA = easing,
        materialEasingB = easing
    )
}

fun <T> materialVelocity2D(
    durationMs: Int,
    maxVelocityA: Float,
    maxAccelerationA: Float,
    easingA: MaterialVelocity.Easing,
    maxVelocityB: Float,
    maxAccelerationB: Float,
    easingB: MaterialVelocity.Easing
): AnimationSpec<T> {
    return Material2DAnimationSpec(
        desiredDurationMs = durationMs,
        maxVelocityA = maxVelocityA,
        maxVelocityB = maxVelocityB,
        maxAccelerationA = maxAccelerationA,
        maxAccelerationB = maxAccelerationB,
        materialEasingA = easingA,
        materialEasingB = easingB
    )
}

/**
 * Animation spec that uses a [Velocity2D] for animating 2-dimensional values.
 *
 * Everything else is animated using a fallback [FloatTweenSpec] with similar attributes.
 */
class Material2DAnimationSpec<T>(
    private val desiredDurationMs: Int = AnimationConstants.DefaultDurationMillis,
    private val maxVelocityA: Float = DEFAULT_MAX_VELOCITY,
    private val maxVelocityB: Float = DEFAULT_MAX_VELOCITY,
    private val maxAccelerationA: Float = DEFAULT_MAX_ACCELERATION,
    private val maxAccelerationB: Float = DEFAULT_MAX_ACCELERATION,
    private val materialEasingA: MaterialVelocity.Easing = defaultEasing,
    private val materialEasingB: MaterialVelocity.Easing = defaultEasing,
) : FiniteAnimationSpec<T> {
    private val velocity2DA: Velocity2D = Velocity2D()
    private val velocity2DB: Velocity2D = Velocity2D()

    override fun <V : AnimationVector> vectorize(converter: TwoWayConverter<T, V>): VectorizedFiniteAnimationSpec<V> =
        VectorizedMaterial2DAnimationSpec(
            velocity2DA = velocity2DA,
            velocity2DB = velocity2DB,
            desiredDurationMs = desiredDurationMs,
            maxVelocityA = maxVelocityA,
            maxVelocityB = maxVelocityB,
            maxAccelerationA = maxAccelerationA,
            maxAccelerationB = maxAccelerationB,
            materialEasingA = materialEasingA,
            materialEasingB = materialEasingB
        )
}

@Suppress("UNCHECKED_CAST")
private class VectorizedMaterial2DAnimationSpec<V : AnimationVector>(
    private val velocity2DA: Velocity2D,
    private val velocity2DB: Velocity2D,
    private val desiredDurationMs: Int,
    private val maxVelocityA: Float,
    private val maxVelocityB: Float,
    private val maxAccelerationA: Float,
    private val maxAccelerationB: Float,
    private val materialEasingA: MaterialVelocity.Easing,
    private val materialEasingB: MaterialVelocity.Easing
) : VectorizedFiniteAnimationSpec<V> {
    private lateinit var lastInitial: FloatArray
    private lateinit var lastTarget: FloatArray
    private lateinit var lastVelocity: FloatArray

    /**
     * Actual duration of [velocity2DA], this is updated by the output of [Velocity2D.getDuration].
     *
     * It's necessary since [velocity2DA] and [velocity2DB] are not guaranteed to have the same
     * duration.
     */
    private var durationASecs = desiredDurationMs / 1000f
    private var durationBSecs = desiredDurationMs / 1000f

    private fun config(
        initialValue: FloatArray,
        targetValue: FloatArray,
        initialVelocity: FloatArray
    ) {
        if (!::lastInitial.isInitialized || !::lastTarget.isInitialized || !::lastVelocity.isInitialized ||
            !lastInitial.contentEquals(initialValue) || !lastTarget.contentEquals(targetValue) || !lastVelocity.contentEquals(
                initialVelocity
            )
        ) {
            lastInitial = initialValue
            lastTarget = targetValue
            lastVelocity = initialVelocity
            val desiredDurationSecs = desiredDurationMs / 1000f

            when (initialValue.size) {
                1, 2, 3, 4 -> {
                    velocity2DA.configure(
                        initialValue.getOrElse(0) { 0f },
                        initialValue.getOrElse(1) { 0f },
                        initialVelocity.getOrElse(0) { 0f },
                        initialVelocity.getOrElse(1) { 0f },
                        targetValue.getOrElse(0) { 0f },
                        targetValue.getOrElse(1) { 0f },
                        desiredDurationSecs,
                        maxVelocityA,
                        maxAccelerationA,
                        materialEasingA
                    )
                    velocity2DB.configure(
                        initialValue.getOrElse(2) { 0f },
                        initialValue.getOrElse(3) { 0f },
                        initialVelocity.getOrElse(2) { 0f },
                        initialVelocity.getOrElse(3) { 0f },
                        targetValue.getOrElse(2) { 0f },
                        targetValue.getOrElse(3) { 0f },
                        desiredDurationSecs,
                        maxVelocityB,
                        maxAccelerationB,
                        materialEasingB
                    )
                }

                else -> {

                }
            }
        }
    }

    override fun getDurationNanos(initialValue: V, targetValue: V, initialVelocity: V): Long {
        return getDurationNanos(
            FloatArrayConverter.convertFromVector(initialValue),
            FloatArrayConverter.convertFromVector(targetValue),
            FloatArrayConverter.convertFromVector(initialVelocity),
        )
    }

    /**
     * The reported duration will correspond to the [Velocity2D] instance that last the longest.
     *
     * That way the animation will be considered finished when both instances are guaranteed to be
     * stopped.
     */
    private fun getDurationNanos(
        initialValue: FloatArray,
        targetValue: FloatArray,
        initialVelocity: FloatArray
    ): Long {
        config(
            initialValue = initialValue,
            targetValue = targetValue,
            initialVelocity = initialVelocity
        )

        durationASecs = velocity2DA.duration
        durationBSecs = velocity2DB.duration
        return (maxOf(durationASecs, durationBSecs) * 1_000_000_000f).roundToLong()
    }

    override fun getValueFromNanos(
        playTimeNanos: Long,
        initialValue: V,
        targetValue: V,
        initialVelocity: V
    ): V {
        return getValueFromNanos(
            playTimeNanos = playTimeNanos,
            initialValue = FloatArrayConverter.convertFromVector(initialValue),
            targetValue = FloatArrayConverter.convertFromVector(targetValue),
            initialVelocity = FloatArrayConverter.convertFromVector(initialVelocity),
        )
    }

    private fun getValueFromNanos(
        playTimeNanos: Long,
        initialValue: FloatArray,
        targetValue: FloatArray,
        initialVelocity: FloatArray
    ): V {
        config(
            initialValue = initialValue,
            targetValue = targetValue,
            initialVelocity = initialVelocity
        )
        val playTimeSecs = playTimeNanos / 1_000_000_000f
        val playTimeSecsA = playTimeSecs.coerceAtMost(durationASecs)
        val playTimeSecsB = playTimeSecs.coerceAtMost(durationBSecs)

        return FloatArrayConverter.convertToVector(
            when (initialValue.size) {
                1 -> {
                    floatArrayOf(velocity2DA.getX(playTimeSecsA))
                }

                2 -> {
                    floatArrayOf(
                        velocity2DA.getX(playTimeSecsA),
                        velocity2DA.getY(playTimeSecsA)
                    )
                }

                3 -> {
                    floatArrayOf(
                        velocity2DA.getX(playTimeSecsA),
                        velocity2DA.getY(playTimeSecsA),
                        velocity2DB.getX(playTimeSecsB)
                    )
                }

                4 -> {
                    floatArrayOf(
                        velocity2DA.getX(playTimeSecsA),
                        velocity2DA.getY(playTimeSecsA),
                        velocity2DB.getX(playTimeSecsB),
                        velocity2DB.getY(playTimeSecsB)
                    )
                }

                else -> {
                    floatArrayOf()
                }
            }
        ) as V
    }

    override fun getVelocityFromNanos(
        playTimeNanos: Long,
        initialValue: V,
        targetValue: V,
        initialVelocity: V
    ): V {
        return getVelocityFromNanos(
            playTimeNanos = playTimeNanos,
            initialValue = FloatArrayConverter.convertFromVector(initialValue),
            targetValue = FloatArrayConverter.convertFromVector(targetValue),
            initialVelocity = FloatArrayConverter.convertFromVector(initialVelocity),
        )
    }

    private fun getVelocityFromNanos(
        playTimeNanos: Long,
        initialValue: FloatArray,
        targetValue: FloatArray,
        initialVelocity: FloatArray
    ): V {
        config(
            initialValue = initialValue,
            targetValue = targetValue,
            initialVelocity = initialVelocity
        )

        val playTimeSecs = playTimeNanos / 1_000_000_000f
        val playTimeSecsA = playTimeSecs.coerceAtMost(durationASecs)
        val playTimeSecsB = playTimeSecs.coerceAtMost(durationBSecs)

        return FloatArrayConverter.convertToVector(
            when (initialValue.size) {
                1 -> {
                    floatArrayOf(velocity2DA.getVX(playTimeSecsA))
                }

                2 -> {
                    floatArrayOf(
                        velocity2DA.getVX(playTimeSecsA),
                        velocity2DA.getVY(playTimeSecsA)
                    )
                }

                3 -> {
                    floatArrayOf(
                        velocity2DA.getVX(playTimeSecsA),
                        velocity2DA.getVY(playTimeSecsA),
                        velocity2DB.getVX(playTimeSecsB)
                    )
                }

                4 -> {
                    floatArrayOf(
                        velocity2DA.getVX(playTimeSecsA),
                        velocity2DA.getVY(playTimeSecsA),
                        velocity2DB.getVX(playTimeSecsB),
                        velocity2DB.getVY(playTimeSecsB)
                    )
                }

                else -> {
                    floatArrayOf()
                }
            }
        ) as V
    }
}

private val FloatArrayConverter: TwoWayConverter<FloatArray, AnimationVector> = TwoWayConverter(
    convertToVector = {
        when (it.size) {
            1 -> {
                AnimationVector1D(it[0])
            }

            2 -> {
                AnimationVector2D(it[0], it[1])
            }

            3 -> {
                AnimationVector3D(it[0], it[1], it[2])
            }

            4 -> {
                AnimationVector4D(it[0], it[1], it[2], it[3])
            }

            else -> AnimationVector1D(0f)
        }
    },
    convertFromVector = {
        when (it) {
            is AnimationVector1D -> {
                floatArrayOf(it.value)
            }

            is AnimationVector2D -> {
                floatArrayOf(it.v1, it.v2)
            }

            is AnimationVector3D -> {
                floatArrayOf(it.v1, it.v2, it.v3)
            }

            is AnimationVector4D -> {
                floatArrayOf(it.v1, it.v2, it.v3, it.v4)
            }

            else -> {
                floatArrayOf()
            }
        }
    }
)