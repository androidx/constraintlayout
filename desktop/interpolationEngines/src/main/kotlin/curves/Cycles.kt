/*
 * Copyright (C) 2020 The Android Open Source Project
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
package curves

import java.util.*
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sign
import kotlin.math.sin

/**
 * This generates variable frequency oscillation curves
 *
 */
class Cycles {
    private var mPeriod = floatArrayOf()
    private var mPosition = floatArrayOf()
    private lateinit var mArea: FloatArray
    private var mCustomType: FloatArray? = null
    private var mCustomCurve: MonoSpline? = null
    private var mType = 0
    private var mPI2 = Math.PI.toFloat() * 2
    private var mNormalized = false
    override fun toString(): String {
        return "pos =" + Arrays.toString(mPosition) + " period=" + Arrays.toString(mPeriod)
    }

    // @TODO: add description
    fun setType(type: Int, customType: FloatArray?) {
        mType = type
        mCustomType = customType
        if (customType != null) {
            mCustomCurve = buildWave(customType)
        }
    }

    /**
     * This adds a point in the cycle positions are from 0..1
     * period represents the number of oscillations.
     * The periods should typically:
     * - add up to a whole number.
     * - have a value at 0 and 1.
     * After all points are added call normalize
     */
    fun addPoint(position: Float, period: Float) {
        val len = mPeriod.size + 1
        var j = Arrays.binarySearch(mPosition, position)
        if (j < 0) {
            j = -j - 1
        }
        mPosition = Arrays.copyOf(mPosition, len)
        mPeriod = Arrays.copyOf(mPeriod, len)
        mArea = FloatArray(len)
        System.arraycopy(mPosition, j, mPosition, j + 1, len - j - 1)
        mPosition[j] = position
        mPeriod[j] = period
        mNormalized = false
    }

    /**
     * After adding point every thing must be normalized
     * This must be called adding points
     */
    fun normalize() {
        var totalArea = 0f
        var totalCount = 0f
        for (i in mPeriod.indices) {
            totalCount += mPeriod[i]
        }
        for (i in 1 until mPeriod.size) {
            val h = (mPeriod[i - 1] + mPeriod[i]) / 2
            val w = mPosition[i] - mPosition[i - 1]
            totalArea = totalArea + w * h
        }
        // scale periods to normalize it
        for (i in mPeriod.indices) {
            mPeriod[i] *= (totalCount / totalArea)
        }
        mArea[0] = 0f
        for (i in 1 until mPeriod.size) {
            val h = (mPeriod[i - 1] + mPeriod[i]) / 2
            val w = mPosition[i] - mPosition[i - 1]
            mArea[i] = mArea[i - 1] + w * h
        }
        mNormalized = true
    }

    /**
     * Calculate the phase of the cycle given the accumulation of cycles up to
     * that point in time.
     */
    private fun getP(time: Float): Float {
        var time = time
        if (time < 0) {
            time = 0f
        } else if (time > 1) {
            time = 1f
        }
        var index = Arrays.binarySearch(mPosition, time)
        var p = 0f
        if (index > 0) {
            p = 1f
        } else if (index != 0) {
            index = -index - 1
            val t = time
            val m = ((mPeriod[index] - mPeriod[index - 1])
                    / (mPosition[index] - mPosition[index - 1]))
            p =
                mArea[index - 1] + (mPeriod[index - 1] - m * mPosition[index - 1]) * (t - mPosition[index - 1]) + m * (t * t - mPosition[index - 1] * mPosition[index - 1]) / 2
        }
        return p
    }

    /**
     * Get the value for time. (Time here is typically progress 0..1)
     * Phase typically thought of as an angle is from 0..1 (not 0-360 or 0-2PI)
     * This makes it mathematically more efficient
     */
    fun getValue(time: Float, phase: Float): Float {
        val angle = phase + getP(time) // angle is / by 360
        return when (mType) {
            SIN_WAVE -> sin(mPI2 * angle)
            SQUARE_WAVE -> sign(0.5f - angle % 1)
            TRIANGLE_WAVE -> 1 - abs((angle * 4 + 1) % 4 - 2)
            SAW_WAVE -> (angle * 2 + 1) % 2 - 1
            REVERSE_SAW_WAVE -> 1 - (angle * 2 + 1) % 2
            COS_WAVE -> cos(mPI2 * (angle))
            BOUNCE -> {
                val x = 1 - Math.abs(angle * 4 % 4 - 2)
                1 - x * x
            }

            CUSTOM -> mCustomCurve!!.getPos((angle % 1), 0)
            else -> sin(mPI2 * angle)
        }
    }

    /**
     * Get the differential  dValue/dt
     */
    fun getDP(time: Float): Float {
        var time = time
        if (time <= 0) {
            time = 0.00001f
        } else if (time >= 1) {
            time = .999999f
        }
        var index = Arrays.binarySearch(mPosition, time)
        var p = 0f
        if (index > 0) {
            return 0f
        }
        if (index != 0) {
            index = -index - 1
            val t = time
            val m = ((mPeriod[index] - mPeriod[index - 1])
                    / (mPosition[index] - mPosition[index - 1]))
            p = m * t + (mPeriod[index - 1] - m * mPosition[index - 1])
        }
        return p
    }

    // @TODO: add description
    fun getSlope(time: Float, phase: Float, dphase: Float): Float {
        val angle = phase + getP(time)
        val dangle_dtime = getDP(time) + dphase
        return when (mType) {
            SIN_WAVE -> (mPI2 * dangle_dtime * cos((mPI2 * angle)))
            SQUARE_WAVE -> 0f
            TRIANGLE_WAVE -> 4 * dangle_dtime * Math.signum((angle * 4 + 3) % 4 - 2)
            SAW_WAVE -> dangle_dtime * 2
            REVERSE_SAW_WAVE -> -dangle_dtime * 2
            COS_WAVE -> -mPI2 * dangle_dtime * sin(mPI2 * angle)
            BOUNCE -> 4 * dangle_dtime * ((angle * 4 + 2) % 4 - 2)
            CUSTOM -> mCustomCurve!!.getSlope((angle % 1), 0)
            else -> mPI2 * dangle_dtime * cos((mPI2 * angle))
        }
    }

    companion object {
        const val SIN_WAVE = 0 // theses must line up with attributes
        const val SQUARE_WAVE = 1
        const val TRIANGLE_WAVE = 2
        const val SAW_WAVE = 3
        const val REVERSE_SAW_WAVE = 4
        const val COS_WAVE = 5
        const val BOUNCE = 6
        const val CUSTOM = 7
    }

    /**
     * This builds a monotonic spline to be used as a wave function
     */


    private fun buildWave(values: FloatArray): MonoSpline {
        val length = values.size * 3 - 2
        val len = values.size - 1
        val gap = 1.0f / len
        val points = ArrayList<FloatArray>(length)
        for (i in 0 until length) {
            points.add(FloatArray(1))
        }
        val time = FloatArray(length)
        for (i in values.indices) {
            val v = values[i]
            points[i + len][0] = v
            time[i + len] = i * gap
            if (i > 0) {
                points[i + len * 2][0] = v + 1
                time[i + len * 2] = i * gap + 1
                points[i - 1][0] = v - 1 - gap
                time[i - 1] = i * gap + -1 - gap
            }
        }
        return MonoSpline(time, points)
    }
}
