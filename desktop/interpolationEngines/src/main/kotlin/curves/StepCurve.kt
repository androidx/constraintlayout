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
 * This class translates a series of floating point values into a continuous
 * curve for use in an easing function including quantize functions
 * it is used with the "spline(0,0.3,0.3,0.5,...0.9,1)" it should start at 0 and end at one 1
 */
class StepCurve internal constructor(configString: String) : Easing() {
    var mCurveFit: MonoSpline

    init {
        // done this way for efficiency
        mStr = configString
        val values = FloatArray(mStr.length / 2)
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
        mCurveFit = genSpline(Arrays.copyOf(values, count))
    }

    override fun getDiff(x: Float): Float {
        return mCurveFit.getSlope(x, 0)
    }

    override fun get(x: Float): Float {
        return mCurveFit.getPos(x, 0)
    }

    private fun genSpline(str: String): MonoSpline {
        val sp = str.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val values = FloatArray(sp.size)
        for (i in values.indices) {
            values[i] = sp[i].toFloat()
        }
        return genSpline(values)
    }

    private fun genSpline(values: FloatArray): MonoSpline {
        val length = values.size * 3 - 2
        val len = values.size - 1
        val gap = 1.0f / len
        val points = Array(length) { FloatArray(1) }
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

        val ms = MonoSpline(time, listOf(*points))
        println(" 0 " + ms.getPos(0f, 0))
        println(" 1 " + ms.getPos(1f, 0))
        return ms
    }

}