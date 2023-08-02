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

/**
 * Provide the engine for cubic spline easing
 *
 *
 */
open class Easing {
    var mStr = "identity"

    // @TODO: add description
    open operator fun get(x: Float): Float {
        return x
    }

    // @TODO: add description
    override fun toString(): String {
        return mStr
    }

    // @TODO: add description
    open fun getDiff(x: Float): Float {
        return 1f
    }

    internal class CubicEasing : Easing {
        var mX1 = 0f
        var mY1 = 0f
        var mX2 = 0f
        var mY2 = 0f

        constructor(configString: String) {
            // done this way for efficiency
            mStr = configString
            val start = configString.indexOf('(')
            val off1 = configString.indexOf(',', start)
            mX1 = configString.substring(start + 1, off1).trim { it <= ' ' }.toFloat()
            val off2 = configString.indexOf(',', off1 + 1)
            mY1 = configString.substring(off1 + 1, off2).trim { it <= ' ' }.toFloat()
            val off3 = configString.indexOf(',', off2 + 1)
            mX2 = configString.substring(off2 + 1, off3).trim { it <= ' ' }.toFloat()
            val end = configString.indexOf(')', off3 + 1)
            mY2 = configString.substring(off3 + 1, end).trim { it <= ' ' }.toFloat()
        }

        constructor(x1: Float, y1: Float, x2: Float, y2: Float) {
            setup(x1, y1, x2, y2)
        }

        fun setup(x1: Float, y1: Float, x2: Float, y2: Float) {
            mX1 = x1
            mY1 = y1
            mX2 = x2
            mY2 = y2
        }

        private fun getX(t: Float): Float {
            val t1 = 1 - t
            // no need for because start at 0,0 float f0 = (1 - t) * (1 - t) * (1 - t);
            val f1 = 3 * t1 * t1 * t
            val f2 = 3 * t1 * t * t
            val f3 = t * t * t
            return mX1 * f1 + mX2 * f2 + f3
        }

        private fun getY(t: Float): Float {
            val t1 = 1 - t
            // no need for because start at 0,0 float f0 = (1 - t) * (1 - t) * (1 - t);
            val f1 = 3 * t1 * t1 * t
            val f2 = 3 * t1 * t * t
            val f3 = t * t * t
            return mY1 * f1 + mY2 * f2 + f3
        }

        private fun getDiffX(t: Float): Float {
            val t1 = 1 - t
            return 3 * t1 * t1 * mX1 + 6 * t1 * t * (mX2 - mX1) + 3 * t * t * (1 - mX2)
        }

        private fun getDiffY(t: Float): Float {
            val t1 = 1 - t
            return 3 * t1 * t1 * mY1 + 6 * t1 * t * (mY2 - mY1) + 3 * t * t * (1 - mY2)
        }

        /**
         * binary search for the region
         * and linear interpolate the answer
         */
        override fun getDiff(x: Float): Float {
            var t = 0.5f
            var range = 0.5f
            while (range > sDError) {
                val tx = getX(t)
                range *= 0.5.toFloat()
                if (tx < x) {
                    t += range
                } else {
                    t -= range
                }
            }
            val x1 = getX(t - range)
            val x2 = getX(t + range)
            val y1 = getY(t - range)
            val y2 = getY(t + range)
            return (y2 - y1) / (x2 - x1)
        }

        /**
         * binary search for the region
         * and linear interpolate the answer
         */
        override fun get(x: Float): Float {
            if (x <= 0.0f) {
                return 0f
            }
            if (x >= 1.0f) {
                return 1.0f
            }
            var t = 0.5f
            var range = 0.5f
            while (range > sError) {
                val tx = getX(t)
                range *= 0.5f
                if (tx < x) {
                    t += range
                } else {
                    t -= range
                }
            }
            val x1 = getX(t - range)
            val x2 = getX(t + range)
            val y1 = getY(t - range)
            val y2 = getY(t + range)
            return (y2 - y1) * (x - x1) / (x2 - x1) + y1
        }

        companion object {
            private const val sError = 0.01f
            private const val sDError = 0.0001f
        }
    }

    companion object {
        var sDefault = Easing()
        private const val STANDARD = "cubic(0.4, 0.0, 0.2, 1)"
        private const val ACCELERATE = "cubic(0.4, 0.05, 0.8, 0.7)"
        private const val DECELERATE = "cubic(0.0, 0.0, 0.2, 0.95)"
        private const val LINEAR = "cubic(1, 1, 0, 0)"
        private const val ANTICIPATE = "cubic(0.36, 0, 0.66, -0.56)"
        private const val OVERSHOOT = "cubic(0.34, 1.56, 0.64, 1)"
        private const val DECELERATE_NAME = "decelerate"
        private const val ACCELERATE_NAME = "accelerate"
        private const val STANDARD_NAME = "standard"
        private const val LINEAR_NAME = "linear"
        private const val ANTICIPATE_NAME = "anticipate"
        private const val OVERSHOOT_NAME = "overshoot"
        var NAMED_EASING = arrayOf(STANDARD_NAME, ACCELERATE_NAME, DECELERATE_NAME, LINEAR_NAME)

        // @TODO: add description
        fun getInterpolator(configString: String?): Easing? {
            if (configString == null) {
                return null
            }
            if (configString.startsWith("cubic")) {
                return CubicEasing(configString)
            } else if (configString.startsWith("spline")) {
                return StepCurve(configString)
            } else if (configString.startsWith("Schlick")) {
                return Schlick(configString)
            } else {
                when (configString) {
                    STANDARD_NAME -> return CubicEasing(STANDARD)
                    ACCELERATE_NAME -> return CubicEasing(ACCELERATE)
                    DECELERATE_NAME -> return CubicEasing(DECELERATE)
                    LINEAR_NAME -> return CubicEasing(LINEAR)
                    ANTICIPATE_NAME -> return CubicEasing(ANTICIPATE)
                    OVERSHOOT_NAME -> return CubicEasing(OVERSHOOT)
                    else -> System.err.println(
                        "transitionEasing syntax error syntax:"
                                + "transitionEasing=\"cubic(1.0,0.5,0.0,0.6)\" or "
                                + Arrays.toString(NAMED_EASING)
                    )
                }
            }
            return sDefault
        }
    }
}