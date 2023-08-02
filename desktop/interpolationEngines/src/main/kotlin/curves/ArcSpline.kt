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
package curves

import java.util.*
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

/**
 * This provides a curve fit system that stitches the x,y path together with
 * quarter ellipses
 */
class ArcSpline(arcModes: IntArray, val timePoints: FloatArray, y: List<FloatArray>) {
    var mArcs: Array<Arc?>
    private val mExtrapolate = true

    init {
        mArcs = arrayOfNulls(timePoints.size - 1)
        var mode = START_VERTICAL
        var last = START_VERTICAL

        mArcs = Array(timePoints.size - 1) {it->
            val i  = it
            when (arcModes[i]) {
                ARC_START_VERTICAL -> {
                    mode = START_VERTICAL
                    last = mode
                }

                ARC_START_HORIZONTAL -> {
                    mode = START_HORIZONTAL
                    last = mode
                }

                ARC_START_FLIP -> {
                    mode = if (last == START_VERTICAL) START_HORIZONTAL else START_VERTICAL
                    last = mode
                }

                ARC_START_LINEAR -> mode = START_LINEAR
                ARC_ABOVE -> mode = UP_ARC
                ARC_BELOW -> mode = DOWN_ARC
            }
           Arc(mode, timePoints[i], timePoints[i + 1], y[i][0], y[i][1], y[i + 1][0], y[i + 1][1])
        }
    }

    /**
     * get the values of the at t point in time.
     */
    fun getPos(t: Float, v: FloatArray) {
        var t = t
        if (mExtrapolate) {
            if (t < mArcs[0]!!.mTime1) {
                val t0 = mArcs[0]!!.mTime1
                val dt = t - mArcs[0]!!.mTime1
                val p = 0
                if (mArcs[p]!!.mLinear) {
                    v[0] = mArcs[p]!!.getLinearX(t0) + dt * mArcs[p]!!.getLinearDX(t0)
                    v[1] = mArcs[p]!!.getLinearY(t0) + dt * mArcs[p]!!.getLinearDY(t0)
                } else {
                    mArcs[p]!!.setPoint(t0)
                    v[0] = mArcs[p]!!.calcX() + dt * mArcs[p]!!.calcDX()
                    v[1] = mArcs[p]!!.calcY() + dt * mArcs[p]!!.calcDY()
                }
                return
            }
            if (t > mArcs[mArcs.size - 1]!!.mTime2) {
                val t0 = mArcs[mArcs.size - 1]!!.mTime2
                val dt = t - t0
                val p = mArcs.size - 1
                if (mArcs[p]!!.mLinear) {
                    v[0] = mArcs[p]!!.getLinearX(t0) + dt * mArcs[p]!!.getLinearDX(t0)
                    v[1] = mArcs[p]!!.getLinearY(t0) + dt * mArcs[p]!!.getLinearDY(t0)
                } else {
                    mArcs[p]!!.setPoint(t)
                    v[0] = mArcs[p]!!.calcX() + dt * mArcs[p]!!.calcDX()
                    v[1] = mArcs[p]!!.calcY() + dt * mArcs[p]!!.calcDY()
                }
                return
            }
        } else {
            if (t < mArcs[0]!!.mTime1) {
                t = mArcs[0]!!.mTime1
            }
            if (t > mArcs[mArcs.size - 1]!!.mTime2) {
                t = mArcs[mArcs.size - 1]!!.mTime2
            }
        }
        for (i in mArcs.indices) {
            if (t <= mArcs[i]!!.mTime2) {
                if (mArcs[i]!!.mLinear) {
                    v[0] = mArcs[i]!!.getLinearX(t)
                    v[1] = mArcs[i]!!.getLinearY(t)
                    return
                }
                mArcs[i]!!.setPoint(t)
                v[0] = mArcs[i]!!.calcX()
                v[1] = mArcs[i]!!.calcY()
                return
            }
        }
    }

    /**
     * Get the differential which of the curves at point t
     */
    fun getSlope(t: Float, v: FloatArray) {
        var t = t
        if (t < mArcs[0]!!.mTime1) {
            t = mArcs[0]!!.mTime1
        } else if (t > mArcs[mArcs.size - 1]!!.mTime2) {
            t = mArcs[mArcs.size - 1]!!.mTime2
        }
        for (i in mArcs.indices) {
            if (t <= mArcs[i]!!.mTime2) {
                if (mArcs[i]!!.mLinear) {
                    v[0] = mArcs[i]!!.getLinearDX(t)
                    v[1] = mArcs[i]!!.getLinearDY(t)
                    return
                }
                mArcs[i]!!.setPoint(t)
                v[0] = mArcs[i]!!.calcDX()
                v[1] = mArcs[i]!!.calcDY()
                return
            }
        }
    }

    /**
     * get the value of the j'th curve at point in time t
     */
    fun getPos(t: Float, j: Int): Float {
        var t = t
        if (mExtrapolate) {
            if (t < mArcs[0]!!.mTime1) {
                val t0 = mArcs[0]!!.mTime1
                val dt = t - mArcs[0]!!.mTime1
                val p = 0
                return if (mArcs[p]!!.mLinear) {
                    if (j == 0) {
                        mArcs[p]!!.getLinearX(t0) + dt * mArcs[p]!!.getLinearDX(t0)
                    } else mArcs[p]!!.getLinearY(t0) + dt * mArcs[p]!!.getLinearDY(t0)
                } else {
                    mArcs[p]!!.setPoint(t0)
                    if (j == 0) {
                        mArcs[p]!!.calcX() + dt * mArcs[p]!!.calcDX()
                    } else mArcs[p]!!.calcY() + dt * mArcs[p]!!.calcDY()
                }
            }
            if (t > mArcs[mArcs.size - 1]!!.mTime2) {
                val t0 = mArcs[mArcs.size - 1]!!.mTime2
                val dt = t - t0
                val p = mArcs.size - 1
                return if (j == 0) {
                    mArcs[p]!!.getLinearX(t0) + dt * mArcs[p]!!.getLinearDX(t0)
                } else mArcs[p]!!.getLinearY(t0) + dt * mArcs[p]!!.getLinearDY(t0)
            }
        } else {
            if (t < mArcs[0]!!.mTime1) {
                t = mArcs[0]!!.mTime1
            } else if (t > mArcs[mArcs.size - 1]!!.mTime2) {
                t = mArcs[mArcs.size - 1]!!.mTime2
            }
        }
        for (i in mArcs.indices) {
            if (t <= mArcs[i]!!.mTime2) {
                if (mArcs[i]!!.mLinear) {
                    return if (j == 0) {
                        mArcs[i]!!.getLinearX(t)
                    } else mArcs[i]!!.getLinearY(t)
                }
                mArcs[i]!!.setPoint(t)
                return if (j == 0) {
                    mArcs[i]!!.calcX()
                } else mArcs[i]!!.calcY()
            }
        }
        return Float.NaN
    }

    /**
     * Get the slope of j'th curve at time t
     */
    fun getSlope(t: Float, j: Int): Float {
        var t = t
        if (t < mArcs[0]!!.mTime1) {
            t = mArcs[0]!!.mTime1
        }
        if (t > mArcs[mArcs.size - 1]!!.mTime2) {
            t = mArcs[mArcs.size - 1]!!.mTime2
        }
        for (i in mArcs.indices) {
            if (t <= mArcs[i]!!.mTime2) {
                if (mArcs[i]!!.mLinear) {
                    return if (j == 0) {
                        mArcs[i]!!.getLinearDX(t)
                    } else mArcs[i]!!.getLinearDY(t)
                }
                mArcs[i]!!.setPoint(t)
                return if (j == 0) {
                    mArcs[i]!!.calcDX()
                } else mArcs[i]!!.calcDY()
            }
        }
        return Float.NaN
    }

    class Arc internal constructor(mode: Int, t1: Float, t2: Float, x1: Float, y1: Float, x2: Float, y2: Float) {
        var mLut: FloatArray
        var mArcDistance = 0f
        var mTime1: Float
        var mTime2: Float
        var mX1 = 0f
        var mX2 = 0f
        var mY1 = 0f
        var mY2 = 0f
        var mOneOverDeltaTime: Float
        var mEllipseA: Float
        var mEllipseB: Float
        var mEllipseCenterX // also used to cache the slope in the unused center
                : Float
        var mEllipseCenterY // also used to cache the slope in the unused center
                : Float
        var mArcVelocity: Float
        var mTmpSinAngle = 0f
        var mTmpCosAngle = 0f
        var mVertical = false
        var mLinear = false

        init {
            val dx = x2 - x1
            val dy = y2 - y1
            mVertical = when (mode) {
                START_VERTICAL -> true
                UP_ARC -> dy < 0
                DOWN_ARC -> dy > 0
                else -> false
            }
            mTime1 = t1
            mTime2 = t2
            mOneOverDeltaTime = 1 / (mTime2 - mTime1)
            if (START_LINEAR == mode) {
                mLinear = true
            }
            if (mLinear || Math.abs(dx) < EPSILON || Math.abs(dy) < EPSILON) {
                mLinear = true
                mX1 = x1
                mX2 = x2
                mY1 = y1
                mY2 = y2
                mArcDistance = hypot(dy, dx).toFloat()
                mArcVelocity = mArcDistance * mOneOverDeltaTime
                mEllipseCenterX = dx / (mTime2 - mTime1) // cache the slope in the unused center
                mEllipseCenterY = dy / (mTime2 - mTime1) // cache the slope in the unused center
                mLut = FloatArray(101)
                mEllipseA = Float.NaN
                mEllipseB = Float.NaN
            } else {
                mLut = FloatArray(101)
                mEllipseA = dx * if (mVertical) -1 else 1
                mEllipseB = dy * if (mVertical) 1 else -1
                mEllipseCenterX = if (mVertical) x2 else x1
                mEllipseCenterY = if (mVertical) y1 else y2
                buildTable(x1, y1, x2, y2)
                mArcVelocity = mArcDistance * mOneOverDeltaTime
            }
        }

        fun setPoint(time: Float) {
            val percent = (if (mVertical) mTime2 - time else time - mTime1) * mOneOverDeltaTime
            val angle = Math.PI.toFloat() * 0.5f * lookup(percent)
            mTmpSinAngle = sin(angle)
            mTmpCosAngle = cos(angle)
        }

        fun calcX(): Float {
            return mEllipseCenterX + mEllipseA * mTmpSinAngle
        }

        fun calcY(): Float {
            return mEllipseCenterY + mEllipseB * mTmpCosAngle
        }

        fun calcDX(): Float {
            val vx = mEllipseA * mTmpCosAngle
            val vy = -mEllipseB * mTmpSinAngle
            val norm = mArcVelocity / hypot(vx, vy)
            return if (mVertical) -vx * norm else vx * norm
        }

        fun calcDY(): Float {
            val vx = mEllipseA * mTmpCosAngle
            val vy = -mEllipseB * mTmpSinAngle
            val norm = mArcVelocity / hypot(vx, vy)
            return if (mVertical) -vy * norm else vy * norm
        }

        fun getLinearX(t: Float): Float {
            var t = t
            t = (t - mTime1) * mOneOverDeltaTime
            return mX1 + t * (mX2 - mX1)
        }

        fun getLinearY(t: Float): Float {
            var t = t
            t = (t - mTime1) * mOneOverDeltaTime
            return mY1 + t * (mY2 - mY1)
        }

        fun getLinearDX(t: Float): Float {
            return mEllipseCenterX
        }

        fun getLinearDY(t: Float): Float {
            return mEllipseCenterY
        }

        fun lookup(v: Float): Float {
            if (v <= 0) {
                return 0.0f
            }
            if (v >= 1) {
                return 1.0f
            }
            val pos = v * (mLut.size - 1)
            val iv = pos.toInt()
            val off = pos - pos.toInt()
            return mLut[iv] + off * (mLut[iv + 1] - mLut[iv])
        }

        private fun buildTable(x1: Float, y1: Float, x2: Float, y2: Float) {
            val a = x2 - x1
            val b = y1 - y2
            var lx = 0f
            var ly = 0f
            var dist = 0f
            for (i in sOurPercent.indices) {
                val angle = Math.toRadians(90.0 * i / (sOurPercent.size - 1)).toFloat()
                val s = sin(angle)
                val c = cos(angle)
                val px = a * s
                val py = b * c
                if (i > 0) {
                    dist += hypot((px - lx), (py - ly)).toFloat()
                    sOurPercent[i] = dist
                }
                lx = px
                ly = py
            }
            mArcDistance = dist
            for (i in sOurPercent.indices) {
                sOurPercent[i] /= dist
            }
            for (i in mLut.indices) {
                val pos = i / (mLut.size - 1).toFloat()
                val index = Arrays.binarySearch(sOurPercent, pos)
                if (index >= 0) {
                    mLut[i] = index / (sOurPercent.size - 1).toFloat()
                } else if (index == -1) {
                    mLut[i] = 0f
                } else {
                    val p1 = -index - 2
                    val p2 = -index - 1
                    val ans =
                        (p1 + (pos - sOurPercent[p1]) / (sOurPercent[p2] - sOurPercent[p1])) / (sOurPercent.size - 1)
                    mLut[i] = ans
                }
            }
        }

        companion object {
            private val sOurPercent = FloatArray(91)
            private const val EPSILON = 0.001f
        }
    }

    companion object {
        const val ARC_START_VERTICAL = 1
        const val ARC_START_HORIZONTAL = 2
        const val ARC_START_FLIP = 3
        const val ARC_BELOW = 4
        const val ARC_ABOVE = 5
        const val ARC_START_LINEAR = 0
        private const val START_VERTICAL = 1
        private const val START_HORIZONTAL = 2
        private const val START_LINEAR = 3
        private const val DOWN_ARC = 4
        private const val UP_ARC = 5
    }
}