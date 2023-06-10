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
package android.support.composegraph3d.lib

/**
 * This is a class that represents a Quaternion
 * Used to implement a virtual trackball with no "gimbal lock"
 * see https://en.wikipedia.org/wiki/Quaternion
 */
class Quaternion(x0: Double, x1: Double, x2: Double, x3: Double) {
    private val x = DoubleArray(4) // w,x,y,z,
    operator fun set(w: Double, x: Double, y: Double, z: Double) {
        this.x[0] = w
        this.x[1] = x
        this.x[2] = y
        this.x[3] = z
    }

    operator fun set(v1: DoubleArray, v2: DoubleArray) {
        val vec1 = normal(v1)
        val vec2 = normal(v2)
        val axis = normal(cross(vec1, vec2))
        val angle = Math.acos(dot(vec1, vec2))
        set(angle, axis)
    }

    operator fun set(angle: Double, axis: DoubleArray?) {
        x[0] = Math.cos(angle / 2)
        val sin = Math.sin(angle / 2)
        x[1] = axis!![0] * sin
        x[2] = axis[1] * sin
        x[3] = axis[2] * sin
    }

    init {
        x[0] = x0
        x[1] = x1
        x[2] = x2
        x[3] = x3
    }

    fun conjugate(): Quaternion {
        return Quaternion(x[0], -x[1], -x[2], -x[3])
    }

    operator fun plus(b: Quaternion): Quaternion {
        val a = this
        return Quaternion(a.x[0] + b.x[0], a.x[1] + b.x[1], a.x[2] + b.x[2], a.x[3] + b.x[3])
    }

    operator fun times(b: Quaternion): Quaternion {
        val a = this
        val y0 = a.x[0] * b.x[0] - a.x[1] * b.x[1] - a.x[2] * b.x[2] - a.x[3] * b.x[3]
        val y1 = a.x[0] * b.x[1] + a.x[1] * b.x[0] + a.x[2] * b.x[3] - a.x[3] * b.x[2]
        val y2 = a.x[0] * b.x[2] - a.x[1] * b.x[3] + a.x[2] * b.x[0] + a.x[3] * b.x[1]
        val y3 = a.x[0] * b.x[3] + a.x[1] * b.x[2] - a.x[2] * b.x[1] + a.x[3] * b.x[0]
        return Quaternion(y0, y1, y2, y3)
    }

    fun inverse(): Quaternion {
        val d = x[0] * x[0] + x[1] * x[1] + x[2] * x[2] + x[3] * x[3]
        return Quaternion(x[0] / d, -x[1] / d, -x[2] / d, -x[3] / d)
    }

    fun divides(b: Quaternion): Quaternion {
        val a = this
        return a.inverse().times(b)
    }

    fun rotateVec(v: DoubleArray?): DoubleArray {
        val v0 = v!![0]
        val v1 = v[1]
        val v2 = v[2]
        val s = x[1] * v0 + x[2] * v1 + x[3] * v2
        val n0 = 2 * (x[0] * (v0 * x[0] - (x[2] * v2 - x[3] * v1)) + s * x[1]) - v0
        val n1 = 2 * (x[0] * (v1 * x[0] - (x[3] * v0 - x[1] * v2)) + s * x[2]) - v1
        val n2 = 2 * (x[0] * (v2 * x[0] - (x[1] * v1 - x[2] * v0)) + s * x[3]) - v2
        return doubleArrayOf(n0, n1, n2)
    }

    fun matrix() {
        val xx = x[1] * x[1]
        val xy = x[1] * x[2]
        val xz = x[1] * x[3]
        val xw = x[1] * x[0]
        val yy = x[2] * x[2]
        val yz = x[2] * x[3]
        val yw = x[2] * x[0]
        val zz = x[3] * x[3]
        val zw = x[3] * x[0]
        val m = DoubleArray(16)
        m[0] = 1 - 2 * (yy + zz)
        m[1] = 2 * (xy - zw)
        m[2] = 2 * (xz + yw)
        m[4] = 2 * (xy + zw)
        m[5] = 1 - 2 * (xx + zz)
        m[6] = 2 * (yz - xw)
        m[8] = 2 * (xz - yw)
        m[9] = 2 * (yz + xw)
        m[10] = 1 - 2 * (xx + yy)
        m[14] = 0.0
        m[13] = m[14]
        m[12] = m[13]
        m[11] = m[12]
        m[7] = m[11]
        m[3] = m[7]
        m[15] = 1.0
    }

    companion object {
        private fun cross(a: DoubleArray, b: DoubleArray): DoubleArray {
            val out0 = a[1] * b[2] - b[1] * a[2]
            val out1 = a[2] * b[0] - b[2] * a[0]
            val out2 = a[0] * b[1] - b[0] * a[1]
            return doubleArrayOf(out0, out1, out2)
        }

        private fun dot(a: DoubleArray, b: DoubleArray): Double {
            return a[0] * b[0] + a[1] * b[1] + a[2] * b[2]
        }

        private fun normal(a: DoubleArray): DoubleArray {
            val norm = Math.sqrt(dot(a, a))
            return doubleArrayOf(a[0] / norm, a[1] / norm, a[2] / norm)
        }

        fun calcAngle(v1: DoubleArray, v2: DoubleArray): Double {
            val vec1 = normal(v1)
            val vec2 = normal(v2)
            return Math.acos(dot(vec1, vec2))
        }

        fun calcAxis(v1: DoubleArray, v2: DoubleArray): DoubleArray {
            val vec1 = normal(v1)
            val vec2 = normal(v2)
            return normal(cross(vec1, vec2))
        }
    }
}