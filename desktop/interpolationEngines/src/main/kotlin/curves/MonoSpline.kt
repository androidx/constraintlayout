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

/**
 * This performs a spline interpolation in multiple dimensions
 *
 */
class MonoSpline(time: FloatArray, y: List<FloatArray>) {
    private val timePoints: FloatArray
    var mY: ArrayList<FloatArray>
    private var mTangent: ArrayList<FloatArray>
    private val mExtrapolate = true
    private var mSlopeTemp: FloatArray

    fun makeFloatArray(a: Int, b: Int): ArrayList<FloatArray> {
        val ret = ArrayList<FloatArray>() //new Float[a][b];
        for (i in 0 until a) {
            ret.add(FloatArray(b))
        }
        return ret
    }

    init {
        val n = time.size
        val dim = y[0].size
        mSlopeTemp = FloatArray(dim)
        val slope = makeFloatArray(n - 1, dim) // could optimize this out
        val tangent = makeFloatArray(n, dim)
        for (j in 0 until dim) {
            for (i in 0 until n - 1) {
                val dt = time[i + 1] - time[i]
                slope[i][j] = (y[i + 1][j] - y[i][j]) / dt
                if (i == 0) {
                    tangent[i][j] = slope[i][j]
                } else {
                    tangent[i][j] = (slope[i - 1][j] + slope[i][j]) * 0.5f
                }
            }
            tangent[n - 1][j] = slope[n - 2][j]
        }
        for (i in 0 until n - 1) {
            for (j in 0 until dim) {
                if (slope[i][j] == 0.0f) {
                    tangent[i][j] = 0.0f
                    tangent[i + 1][j] = 0.0f
                } else {
                    val a = tangent[i][j] / slope[i][j]
                    val b = tangent[i + 1][j] / slope[i][j]
                    val h = Math.hypot(a.toDouble(), b.toDouble()).toFloat()
                    if (h > 9.0) {
                        val t = 3.0f / h
                        tangent[i][j] = t * a * slope[i][j]
                        tangent[i + 1][j] = t * b * slope[i][j]
                    }
                }
            }
        }
        timePoints = time
        mY = copyData(y)
        mTangent = tangent
    }

    private fun copyData(y: List<FloatArray>): ArrayList<FloatArray> {
        val ret = ArrayList<FloatArray>()
        for (array in y) {
            ret.add(array)
        }
        return ret
    }

    fun getPos(t: Float, v: FloatArray) {
        val n = timePoints.size
        val dim = mY[0].size
        if (mExtrapolate) {
            if (t <= timePoints[0]) {
                getSlope(timePoints[0], mSlopeTemp)
                for (j in 0 until dim) {
                    v[j] = mY[0][j] + (t - timePoints[0]) * mSlopeTemp[j]
                }
                return
            }
            if (t >= timePoints[n - 1]) {
                getSlope(timePoints[n - 1], mSlopeTemp)
                for (j in 0 until dim) {
                    v[j] = mY[n - 1][j] + (t - timePoints[n - 1]) * mSlopeTemp[j]
                }
                return
            }
        } else {
            if (t <= timePoints[0]) {
                for (j in 0 until dim) {
                    v[j] = mY[0][j]
                }
                return
            }
            if (t >= timePoints[n - 1]) {
                for (j in 0 until dim) {
                    v[j] = mY[n - 1][j]
                }
                return
            }
        }
        for (i in 0 until n - 1) {
            if (t == timePoints[i]) {
                for (j in 0 until dim) {
                    v[j] = mY[i][j]
                }
            }
            if (t < timePoints[i + 1]) {
                val h = timePoints[i + 1] - timePoints[i]
                val x = (t - timePoints[i]) / h
                for (j in 0 until dim) {
                    val y1 = mY[i][j]
                    val y2 = mY[i + 1][j]
                    val t1 = mTangent[i][j]
                    val t2 = mTangent[i + 1][j]
                    v[j] = interpolate(h, x, y1, y2, t1, t2)
                }
                return
            }
        }
    }

    fun getPos(t: Float, j: Int): Float {
        val n = timePoints.size
        if (mExtrapolate) {
            if (t <= timePoints[0]) {
                return mY[0][j] + (t - timePoints[0]) * getSlope(timePoints[0], j)
            }
            if (t >= timePoints[n - 1]) {
                return mY[n - 1][j] + (t - timePoints[n - 1]) * getSlope(timePoints[n - 1], j)
            }
        } else {
            if (t <= timePoints[0]) {
                return mY[0][j]
            }
            if (t >= timePoints[n - 1]) {
                return mY[n - 1][j]
            }
        }
        for (i in 0 until n - 1) {
            if (t == timePoints[i]) {
                return mY[i][j]
            }
            if (t < timePoints[i + 1]) {
                val h = timePoints[i + 1] - timePoints[i]
                val x = (t - timePoints[i]) / h
                val y1 = mY[i][j]
                val y2 = mY[i + 1][j]
                val t1 = mTangent[i][j]
                val t2 = mTangent[i + 1][j]
                return interpolate(h, x, y1, y2, t1, t2)
            }
        }
        return 0.0f // should never reach here
    }

    fun getSlope(time: Float, v: FloatArray) {
        var t = time
        val n = timePoints.size
        val dim = mY[0].size
        if (t <= timePoints[0]) {
            t = timePoints[0]
        } else if (t >= timePoints[n - 1]) {
            t = timePoints[n - 1]
        }
        for (i in 0 until n - 1) {
            if (t <= timePoints[i + 1]) {
                val h = timePoints[i + 1] - timePoints[i]
                val x = (t - timePoints[i]) / h
                for (j in 0 until dim) {
                    val y1 = mY[i][j]
                    val y2 = mY[i + 1][j]
                    val t1 = mTangent[i][j]
                    val t2 = mTangent[i + 1][j]
                    v[j] = diff(h, x, y1, y2, t1, t2) / h
                }
                break
            }
        }
        return
    }

    fun getSlope(time: Float, j: Int): Float {
        var t = time
        val n = timePoints.size
        if (t < timePoints[0]) {
            t = timePoints[0]
        } else if (t >= timePoints[n - 1]) {
            t = timePoints[n - 1]
        }
        for (i in 0 until n - 1) {
            if (t <= timePoints[i + 1]) {
                val h = timePoints[i + 1] - timePoints[i]
                val x = (t - timePoints[i]) / h
                val y1 = mY[i][j]
                val y2 = mY[i + 1][j]
                val t1 = mTangent[i][j]
                val t2 = mTangent[i + 1][j]
                return diff(h, x, y1, y2, t1, t2) / h
            }
        }
        return 0.0f // should never reach here
    }

    /**
     * Cubic Hermite spline
     */
    private fun interpolate(
        h: Float,
        x: Float,
        y1: Float,
        y2: Float,
        t1: Float,
        t2: Float
    ): Float {
        val x2 = x * x
        val x3 = x2 * x
        return (-2 * x3 * y2
                + 3 * x2 * y2
                + 2 * x3 * y1
                - 3 * x2 * y1
                + y1 + h * t2 * x3
                + h * t1 * x3
                - h * t2 * x2
                - 2 * h * t1 * x2
                + h * t1 * x)
    }

    /**
     * Cubic Hermite spline slope differentiated
     */
    private fun diff(h: Float, x: Float, y1: Float, y2: Float, t1: Float, t2: Float): Float {
        val x2 = x * x
        return (-6 * x2 * y2
                + 6 * x * y2
                + 6 * x2 * y1
                - 6 * x * y1
                + 3 * h * t2 * x2
                + 3 * h * t1 * x2
                - 2 * h * t2 * x
                - 4 * h * t1 * x + h * t1)
    }

}