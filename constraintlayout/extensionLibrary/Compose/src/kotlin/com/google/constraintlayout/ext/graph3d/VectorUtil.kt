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
package com.google.constraintlayout.ext.graph3d

import java.text.DecimalFormat

/**
 * A few utilities for vector calculations.
 */
object VectorUtil {
    fun sub(a: DoubleArray?, b: DoubleArray?, out: DoubleArray?) {
        out!![0] = a!![0] - b!![0]
        out[1] = a[1] - b[1]
        out[2] = a[2] - b[2]
    }

    fun mult(a: DoubleArray?, b: Double, out: DoubleArray?) {
        out!![0] = a!![0] * b
        out[1] = a[1] * b
        out[2] = a[2] * b
    }

    fun dot(a: DoubleArray, b: DoubleArray): Double {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2]
    }

    fun norm(a: DoubleArray?): Double {
        return Math.sqrt(a!![0] * a[0] + a[1] * a[1] + a[2] * a[2])
    }

    fun norm(a: FloatArray?): Double {
        return Math.sqrt((a!![0] * a[0] + a[1] * a[1] + a[2] * a[2]).toDouble())
    }

    fun cross(a: DoubleArray, b: DoubleArray?, out: DoubleArray?) {
        val out0 = a[1] * b!![2] - b[1] * a[2]
        val out1 = a[2] * b[0] - b[2] * a[0]
        val out2 = a[0] * b[1] - b[0] * a[1]
        out!![0] = out0
        out[1] = out1
        out[2] = out2
    }

    fun normalize(a: DoubleArray?) {
        val norm = norm(a)
        a!![0] /= norm
        a!![1] /= norm
        a!![2] /= norm
    }

    fun normalize(a: FloatArray) {
        val norm = norm(a).toFloat()
        a[0] /= norm
        a[1] /= norm
        a[2] /= norm
    }

    fun add(
        a: DoubleArray, b: DoubleArray,
        out: DoubleArray
    ) {
        out[0] = a[0] + b[0]
        out[1] = a[1] + b[1]
        out[2] = a[2] + b[2]
    }

    fun madd(
        a: DoubleArray?, x: Double, b: DoubleArray?,
        out: DoubleArray?
    ) {
        out!![0] = x * a!![0] + b!![0]
        out[1] = x * a[1] + b[1]
        out[2] = x * a[2] + b[2]
    }

    @JvmStatic
    fun triangleNormal(vert: FloatArray, p1: Int, p2: Int, p3: Int, norm: FloatArray?) {
        val x1 = vert[p2] - vert[p1]
        val y1 = vert[p2 + 1] - vert[p1 + 1]
        val z1 = vert[p2 + 2] - vert[p1 + 2]
        val x2 = vert[p3] - vert[p1]
        val y2 = vert[p3 + 1] - vert[p1 + 1]
        val z2 = vert[p3 + 2] - vert[p1 + 2]
        cross(x1, y1, z1, x2, y2, z2, norm)
        val n = norm(norm).toFloat()
        norm!![0] /= n
        norm!![1] /= n
        norm!![2] /= n
    }

    fun dot(a: FloatArray?, b: FloatArray?): Float {
        return a!![0] * b!![0] + a[1] * b[1] + a[2] * b[2]
    }

    fun dot(a: FloatArray, offset: Int, b: FloatArray?): Float {
        return a[offset] * b!![0] + a[1 + offset] * b[1] + a[2 + offset] * b[2]
    }

    fun cross(a0: Float, a1: Float, a2: Float, b0: Float, b1: Float, b2: Float, out: FloatArray?) {
        val out0 = a1 * b2 - b1 * a2
        val out1 = a2 * b0 - b2 * a0
        val out2 = a0 * b1 - b0 * a1
        out!![0] = out0
        out[1] = out1
        out[2] = out2
    }

    private fun trim(s: String): String {
        return s.substring(s.length - 7)
    }

    fun vecToString(light: FloatArray): String {
        val df = DecimalFormat("        ##0.000")
        var str = "["
        for (i in 0..2) {
            if (java.lang.Float.isNaN(light[i])) {
                str += (if (i == 0) "" else " , ") + trim("           NAN")
                continue
            }
            str += (if (i == 0) "" else " , ") + trim(df.format(light[i].toDouble()))
        }
        return "$str]"
    }
}