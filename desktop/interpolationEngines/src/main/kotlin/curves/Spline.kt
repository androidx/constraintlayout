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

import kotlin.math.sqrt

/**
 * Provides spline interpolation code.
 * Currently not used, but it is anticipated that we will be using it in the
 * KeyMotion
 *
 *
 */
class Spline
/**
 * Spline in N dimensions
 *
 * @param points [mPoints][dimensionality]
 */(points: List<FloatArray>) {
    private var mPoints = 0
    private lateinit var mCurve: ArrayList<Array<Cubic>>
    private var mDimensionality = 0
    private lateinit var mCurveLength: FloatArray
    private var mTotalLength = 0.0f
    private lateinit var mCtl: ArrayList<FloatArray>

    init {
        setup(points)
    }


    private fun setup(points: List<FloatArray>) {
        mDimensionality = points[0].size
        mPoints = points.size
        mCtl = ArrayList()
        mCurve = ArrayList()
        for (d in 0 until mDimensionality) {
            val tmp = FloatArray(mPoints)
            mCtl.add(tmp)
            for (p in 0 until mPoints) {
                tmp[p] = points[p][d]
            }
        }
        for (d in 0 until mDimensionality) {
            mCurve.add(calcNaturalCubic(mCtl!![d].size, mCtl[d]))
        }
        mCurveLength = FloatArray(mPoints - 1)
        mTotalLength = 0.0f
        val temp = arrayOfNulls<Cubic>(mDimensionality)
        for (p in mCurveLength.indices) {
            for (d in 0 until mDimensionality) {
                temp[d] = mCurve[d][p]
            }
            mCurveLength[p] = approxLength(temp)
            mTotalLength += mCurveLength[p]
        }
    }

    /**
     * get the velocity
     */
    fun getVelocity(p: Float, v: FloatArray) {
        var pos = p * mTotalLength
        var k = 0
        while (k < mCurveLength.size - 1 && mCurveLength[k] < pos) {
            pos -= mCurveLength[k]
            k++
        }
        for (i in v.indices) {
            v[i] = mCurve[i][k]!!.vel(pos / mCurveLength[k])
        }
    }


    // @TODO: add description
    fun getPos(p: Float, x: FloatArray) {
        var pos = p * mTotalLength
        var k = 0
        while (k < mCurveLength.size - 1 && mCurveLength[k] < pos) {
            pos -= mCurveLength[k]
            k++
        }
        for (i in x.indices) {
            x[i] = mCurve[i][k]!!.eval(pos / mCurveLength[k])
        }
    }

    // @TODO: add description
    fun getPos(p: Float, splineNumber: Int): Float {
        var pos = p * mTotalLength
        var k = 0
        while (k < mCurveLength.size - 1 && mCurveLength[k] < pos) {
            pos -= mCurveLength[k]
            k++
        }
        return mCurve[splineNumber][k]!!.eval(pos / mCurveLength[k])
    }

    // @TODO: add description
    fun approxLength(curve: Array<Cubic?>): Float {
        var sum = 0.0f
        val n = curve.size
        val old = FloatArray(n)
        var i = 0.0f
        while (i < 1) {
            var s = 0.0f
            for (j in 0 until n) {
                var tmp = old[j]
                old[j] = curve[j]!!.eval(i)
                tmp -= old[j]
                s += tmp * tmp
            }
            if (i > 0) {
                sum += sqrt(s)
            }
            i += .1f
        }
        var s = 0.0f
        for (j in 0 until n) {
            var tmp = old[j]
            old[j] = curve[j]!!.eval(1.0f)
            tmp -= old[j]
            s += tmp * tmp
        }
        sum += sqrt(s)
        return sum
    }

    class Cubic(var mA: Float, var mB: Float, var mC: Float, var mD: Float) {
        // @TODO: add description
        fun eval(u: Float): Float {
            return ((mD * u + mC) * u + mB) * u + mA
        }

        // @TODO: add description
        fun vel(v: Float): Float {
            //  (((mD * u) + mC) * u + mB) * u + mA
            //  =  "mA + u*mB + u*u*mC+u*u*u*mD" a cubic expression
            // diff with respect to u = mB + u*mC/2+ u*u*mD/3
            // made efficient (mD*u/3+mC/2)*u+mB;
            return (mD * 3 * v + mC * 2) * v + mB
        }
    }


    private fun calcNaturalCubic(cubic: Int, x: FloatArray): Array<Cubic> {
        var n = cubic
        val gamma = FloatArray(n)
        val delta = FloatArray(n)
        val d = FloatArray(n)
        n -= 1
        gamma[0] = (1.0f / 2.0f)
        for (i in 1 until n) {
            gamma[i] = 1 / (4 - gamma[i - 1])
        }
        gamma[n] = 1 / (2 - gamma[n - 1])
        delta[0] = 3 * (x[1] - x[0]) * gamma[0]
        for (i in 1 until n) {
            delta[i] = (3 * (x[i + 1] - x[i - 1]) - delta[i - 1]) * gamma[i]
        }
        delta[n] = (3 * (x[n] - x[n - 1]) - delta[n - 1]) * gamma[n]
        d[n] = delta[n]
        for (i in n - 1 downTo 0) {
            d[i] = delta[i] - gamma[i] * d[i + 1]
        }
        return Array(n) {i->
             Cubic(
                x[i], d[i], 3 * (x[i + 1] - x[i]) - (2
                        * d[i]) - d[i + 1], 2 * (x[i] - x[i + 1]) + d[i] + d[i + 1]
            )
        }
    }
}
