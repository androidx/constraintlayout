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
import androidx.compose.animation.core.AnimationVector
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.AnimationVector3D
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.FloatTweenSpec
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.VectorizedFiniteAnimationSpec
import androidx.compose.animation.core.VectorizedFloatAnimationSpec
import kotlin.math.roundToLong

/**
 * Animation spec that uses a [Velocity2D] for animating 2-dimensional values.
 *
 * Everything else is animated using a fallback [FloatTweenSpec] with similar attributes.
 */
class Material2DAnimationSpec<T>(
    private val durationMs: Int = AnimationConstants.DefaultDurationMillis,
    private val maxVelocity: Float = 1000f,
    private val maxAcceleration: Float = 1000f,
    private val easing: MaterialVelocity.Easing = MaterialEasing.EASE_OUT_BACK
) : FiniteAnimationSpec<T> {
    private val velocity2D: Velocity2D = Velocity2D()

    override fun <V : AnimationVector> vectorize(converter: TwoWayConverter<T, V>): VectorizedFiniteAnimationSpec<V> =
        VectorizedMaterial2DAnimationSpec(
            velocity2D = velocity2D,
            desiredDurationMs = durationMs,
            maxVelocity = maxVelocity,
            maxAcceleration = maxAcceleration,
            materialEasing = easing
        )
}

private class VectorizedMaterial2DAnimationSpec<V : AnimationVector>(
    private val velocity2D: Velocity2D,
    private val desiredDurationMs: Int,
    private val maxVelocity: Float,
    private val maxAcceleration: Float,
    private val materialEasing: MaterialVelocity.Easing
) : VectorizedFiniteAnimationSpec<V> {
    @Suppress("UNCHECKED_CAST") // Highlighting error? Need to explicitly cast the return type
    private val converter: TwoWayConverter<FloatArray, V> = TwoWayConverter(
        convertToVector = {
            when (it.size) {
                1 -> {
                    AnimationVector1D(it[0]) as V
                }

                2 -> {
                    AnimationVector2D(it[0], it[1]) as V
                }

                3 -> {
                    AnimationVector3D(it[0], it[1], it[2]) as V
                }

                4 -> {
                    AnimationVector4D(it[0], it[1], it[2], it[3]) as V
                }

                else -> AnimationVector1D(0f) as V
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

    private val fallback = VectorizedFloatAnimationSpec<V>(FloatTweenSpec(desiredDurationMs, 0))

    private lateinit var lastInitial: FloatArray
    private lateinit var lastTarget: FloatArray
    private lateinit var lastVelocity: FloatArray

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

            velocity2D.configure(
                initialValue[0],
                initialValue[1],
                initialVelocity[0],
                initialVelocity[1],
                targetValue[0],
                targetValue[1],
                desiredDurationMs / 1000f,
                maxVelocity,
                maxAcceleration,
                materialEasing
            )
        }
    }

    override fun getDurationNanos(initialValue: V, targetValue: V, initialVelocity: V): Long {
        val initialValueAsArray = converter.convertFromVector(initialValue)
        if (initialValueAsArray.size != 2) {
            return fallback.getDurationNanos(initialValue, targetValue, initialVelocity)
        }

        // Guaranteed to return a 2D value
        config(
            initialValue = initialValueAsArray,
            targetValue = converter.convertFromVector(targetValue),
            initialVelocity = converter.convertFromVector(initialVelocity)
        )

        return (velocity2D.duration * 1_000_000_000f).roundToLong()
    }

    override fun getValueFromNanos(
        playTimeNanos: Long,
        initialValue: V,
        targetValue: V,
        initialVelocity: V
    ): V {
        val initialValueAsArray = converter.convertFromVector(initialValue)
        if (initialValueAsArray.size != 2) {
            return fallback.getValueFromNanos(
                playTimeNanos = playTimeNanos,
                initialValue = initialValue,
                targetValue = targetValue,
                initialVelocity = initialVelocity
            )
        }

        // Guaranteed to return a 2D value
        config(
            initialValue = initialValueAsArray,
            targetValue = converter.convertFromVector(targetValue),
            initialVelocity = converter.convertFromVector(initialVelocity)
        )

        val playTimeSecs = playTimeNanos / 1_000_000_000f
        return converter.convertToVector(
            floatArrayOf(
                velocity2D.getX(playTimeSecs),
                velocity2D.getY(playTimeSecs)
            )
        )
    }

    override fun getVelocityFromNanos(
        playTimeNanos: Long,
        initialValue: V,
        targetValue: V,
        initialVelocity: V
    ): V {
        val initialValueAsArray = converter.convertFromVector(initialValue)
        if (initialValueAsArray.size != 2) {
            return fallback.getVelocityFromNanos(
                playTimeNanos = playTimeNanos,
                initialValue = initialValue,
                targetValue = targetValue,
                initialVelocity = initialVelocity
            )
        }

        // Guaranteed to return a 2D value (Offset)
        config(
            initialValue = initialValueAsArray,
            targetValue = converter.convertFromVector(targetValue),
            initialVelocity = converter.convertFromVector(initialVelocity)
        )

        val playTimeSecs = playTimeNanos / 1_000_000_000f
        return converter.convertToVector(
            floatArrayOf(
                velocity2D.getVX(playTimeSecs),
                velocity2D.getVY(playTimeSecs)
            )
        )
    }
}