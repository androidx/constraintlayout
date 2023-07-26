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

/**
 * This generates variable frequency oscillation curves
 *
 *
 */
class Cycles {
    private var mPeriod = floatArrayOf()
    private var mPosition = floatArrayOf()
    private lateinit var mArea: FloatArray
    private var mCustomType: String? = null
    private var mCustomCurve: MonoSpline? = null
    private var mType = 0
    private var mPI2 = Math.PI.toFloat() * 2
    private var mNormalized = false
    override fun toString(): String {
        return "pos =" + Arrays.toString(mPosition) + " period=" + Arrays.toString(mPeriod)
    }

    // @TODO: add description
    fun setType(type: Int, customType: String) {
        mType = type
        mCustomType = customType
        if (mCustomType != null) {
            mCustomCurve = buildWave(customType)
        }
    }

    // @TODO: add description
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

    fun getP(time: Float): Float {
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
            p = mArea[index - 1] + (mPeriod[index - 1] - m * mPosition[index - 1]) * (t - mPosition[index - 1]) + m * (t * t - mPosition[index - 1] * mPosition[index - 1]) / 2
        }
        return p
    }

    // @TODO: add description
    fun getValue(time: Float, phase: Float): Float {
        val angle = phase + getP(time) // angle is / by 360
        return when (mType) {
            SIN_WAVE -> Math.sin((mPI2 * angle).toDouble()).toFloat()
            SQUARE_WAVE -> Math.signum(0.5 - angle % 1).toFloat()
            TRIANGLE_WAVE -> 1 - Math.abs((angle * 4 + 1) % 4 - 2)
            SAW_WAVE -> (angle * 2 + 1) % 2 - 1
            REVERSE_SAW_WAVE -> 1 - (angle * 2 + 1) % 2
            COS_WAVE -> Math.cos((mPI2 * (phase + angle)).toDouble()).toFloat()
            BOUNCE -> {
                val x = 1 - Math.abs(angle * 4 % 4 - 2)
                1 - x * x
            }

            CUSTOM -> mCustomCurve!!.getPos((angle % 1), 0)
            else -> Math.sin((mPI2 * angle).toDouble()).toFloat()
        }
    }

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
            SIN_WAVE -> (mPI2 * dangle_dtime * Math.cos((mPI2 * angle).toDouble())).toFloat()
            SQUARE_WAVE -> 0f
            TRIANGLE_WAVE -> 4 * dangle_dtime * Math.signum((angle * 4 + 3) % 4 - 2)
            SAW_WAVE -> dangle_dtime * 2
            REVERSE_SAW_WAVE -> -dangle_dtime * 2
            COS_WAVE -> (-mPI2 * dangle_dtime * Math.sin((mPI2 * angle).toDouble())).toFloat()
            BOUNCE -> 4 * dangle_dtime * ((angle * 4 + 2) % 4 - 2)
            CUSTOM -> mCustomCurve!!.getSlope((angle % 1), 0)
            else -> (mPI2 * dangle_dtime * Math.cos((mPI2 * angle).toDouble())).toFloat()
        }
    }

    companion object {
        var TAG = "Oscillator"
        const val SIN_WAVE = 0 // theses must line up with attributes
        const val SQUARE_WAVE = 1
        const val TRIANGLE_WAVE = 2
        const val SAW_WAVE = 3
        const val REVERSE_SAW_WAVE = 4
        const val COS_WAVE = 5
        const val BOUNCE = 6
        const val CUSTOM = 7

        /**
         * This builds a monotonic spline to be used as a wave function
         */
        fun buildWave(configString: String): MonoSpline {
            // done this way for efficiency
            val values = FloatArray(configString.length / 2)
            var start = configString.indexOf('(') + 1
            var off1 = configString.indexOf(',', start)
            var count = 0
            while (off1 != -1) {
                val tmp = configString.substring(start, off1).trim { it <= ' ' }
                values[count++] = tmp.toFloat()
                off1 = configString.indexOf(',', off1 + 1.also { start = it })
            }
            off1 = configString.indexOf(')', start)
            val tmp = configString.substring(start, off1).trim { it <= ' ' }
            values[count++] = tmp.toFloat()
            return buildWave(Arrays.copyOf(values, count))
        }

        private fun buildWave(values: FloatArray): MonoSpline {
            val length = values.size * 3 - 2
            val len = values.size - 1
            val gap = 1.0f / len
            val points = ArrayList<FloatArray>(length)
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
}