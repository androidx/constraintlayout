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
package com.google.constraintlayout.ext.graph3d

import java.text.DecimalFormat
import java.util.*

/**
 * Matrix math class.  (For the purposes of this application it is more efficient as has no JNI)
 */
open class Matrix {
    var m: DoubleArray
    fun makeRotation() {
        run {
            val v = doubleArrayOf(m[0], m[4], m[8])
            VectorUtil.normalize(v)
            m[0] = v[0]
            m[4] = v[1]
            m[8] = v[2]
        }
        run {
            val v = doubleArrayOf(m[1], m[5], m[9])
            VectorUtil.normalize(v)
            m[1] = v[0]
            m[5] = v[1]
            m[9] = v[2]
        }
        run {
            val v = doubleArrayOf(m[2], m[6], m[10])
            VectorUtil.normalize(v)
            m[2] = v[0]
            m[6] = v[1]
            m[10] = v[2]
        }
    }

    open fun print() {
        val df = DecimalFormat("      ##0.000")
        for (i in 0..3) {
            for (j in 0..3) {
                print(
                    (if (j == 0) "[ " else " , ") + trim(
                        df.format(
                            m[i * 4 + j]
                        )
                    )
                )
            }
            println("]")
        }
    }

    constructor() {
        m = DoubleArray(4 * 4)
        setToUnit()
    }

    constructor(matrix: Matrix) : this(Arrays.copyOf(matrix.m, matrix.m.size)) {}
    protected constructor(m: DoubleArray) {
        this.m = m
    }

    fun setToUnit() {
        for (i in 1 until m.size) {
            m[i] = 0.0
        }
        m[0] = 1.0
        m[5] = 1.0
        m[10] = 1.0
        m[15] = 1.0
    }

    fun mult4(src: FloatArray, dest: FloatArray) {
        for (i in 0..3) {
            val col = i * 4
            var sum = 0.0
            for (j in 0..3) {
                sum += m[col + j] * src[j]
            }
            dest[i] = sum.toFloat()
        }
    }

    fun mult3(src: FloatArray, dest: FloatArray) {
        for (i in 0..2) {
            val col = i * 4
            var sum = m[col + 3]
            for (j in 0..2) {
                sum += m[col + j] * src[j]
            }
            dest[i] = sum.toFloat()
        }
    }

    fun mult3v(src: FloatArray, dest: FloatArray) {
        for (i in 0..2) {
            val col = i * 4
            var sum = 0.0
            for (j in 0..2) {
                sum += m[col + j] * src[j]
            }
            dest[i] = sum.toFloat()
        }
    }

    fun mult3v(src: FloatArray, off: Int, dest: FloatArray) {
        for (i in 0..2) {
            val col = i * 4
            var sum = 0.0
            for (j in 0..2) {
                sum += m[col + j] * src[off + j]
            }
            dest[i] = sum.toFloat()
        }
    }

    fun mult4(src: DoubleArray, dest: DoubleArray) {
        for (i in 0..3) {
            val col = i * 4
            var sum = 0.0
            for (j in 0..3) {
                sum += m[col + j] * src[j]
            }
            dest[i] = sum.toFloat().toDouble()
        }
    }

    fun mult3(src: DoubleArray, dest: DoubleArray) {
        for (i in 0..2) {
            val col = i * 4
            var sum = m[col + 3]
            for (j in 0..2) {
                sum += m[col + j] * src[j]
            }
            dest[i] = sum.toFloat().toDouble()
        }
    }

    fun mult3v(src: DoubleArray?, dest: DoubleArray) {
        for (i in 0..2) {
            val col = i * 4
            var sum = 0.0
            for (j in 0..2) {
                sum += m[col + j] * src!![j]
            }
            dest[i] = sum.toFloat().toDouble()
        }
    }

    fun vecmult(src: DoubleArray?): DoubleArray {
        val ret = DoubleArray(3)
        mult3v(src, ret)
        return ret
    }

    fun mult3(src: FloatArray, off1: Int, dest: FloatArray, off2: Int) {
        var col = 0 * 4
        var sum = m[col + 3]
        for (j in 0..2) {
            sum += m[col + j] * src[j + off1]
        }
        val v0 = sum.toFloat()
        col = 1 * 4
        sum = m[col + 3]
        for (j in 0..2) {
            sum += m[col + j] * src[j + off1]
        }
        val v1 = sum.toFloat()
        col = 2 * 4
        sum = m[col + 3]
        for (j in 0..2) {
            sum += m[col + j] * src[j + off1]
        }
        val v2 = sum.toFloat()
        dest[off2] = v0
        dest[1 + off2] = v1
        dest[2 + off2] = v2
    }

    fun invers(ret: Matrix): Matrix? {
        val inv = ret.m
        inv[0] =
            m[5] * m[10] * m[15] - m[5] * m[11] * m[14] - m[9] * m[6] * m[15] + m[9] * m[7] * m[14] + m[13] * m[6] * m[11] -
                    m[13] * m[7] * m[10]
        inv[4] =
            -m[4] * m[10] * m[15] + m[4] * m[11] * m[14] + m[8] * m[6] * m[15] - m[8] * m[7] * m[14] - m[12] * m[6] * m[11] +
                    m[12] * m[7] * m[10]
        inv[8] =
            m[4] * m[9] * m[15] - m[4] * m[11] * m[13] - m[8] * m[5] * m[15] + m[8] * m[7] * m[13] + m[12] * m[5] * m[11] -
                    m[12] * m[7] * m[9]
        inv[12] =
            -m[4] * m[9] * m[14] + m[4] * m[10] * m[13] + m[8] * m[5] * m[14] - m[8] * m[6] * m[13] - m[12] * m[5] * m[10] +
                    m[12] * m[6] * m[9]
        inv[1] =
            -m[1] * m[10] * m[15] + m[1] * m[11] * m[14] + m[9] * m[2] * m[15] - m[9] * m[3] * m[14] - m[13] * m[2] * m[11] +
                    m[13] * m[3] * m[10]
        inv[5] =
            m[0] * m[10] * m[15] - m[0] * m[11] * m[14] - m[8] * m[2] * m[15] + m[8] * m[3] * m[14] + m[12] * m[2] * m[11] -
                    m[12] * m[3] * m[10]
        inv[9] =
            -m[0] * m[9] * m[15] + m[0] * m[11] * m[13] + m[8] * m[1] * m[15] - m[8] * m[3] * m[13] - m[12] * m[1] * m[11] +
                    m[12] * m[3] * m[9]
        inv[13] =
            m[0] * m[9] * m[14] - m[0] * m[10] * m[13] - m[8] * m[1] * m[14] + m[8] * m[2] * m[13] + m[12] * m[1] * m[10] -
                    m[12] * m[2] * m[9]
        inv[2] =
            m[1] * m[6] * m[15] - m[1] * m[7] * m[14] - m[5] * m[2] * m[15] + m[5] * m[3] * m[14] + m[13] * m[2] * m[7] -
                    m[13] * m[3] * m[6]
        inv[6] =
            -m[0] * m[6] * m[15] + m[0] * m[7] * m[14] + m[4] * m[2] * m[15] - m[4] * m[3] * m[14] - m[12] * m[2] * m[7] +
                    m[12] * m[3] * m[6]
        inv[10] =
            m[0] * m[5] * m[15] - m[0] * m[7] * m[13] - m[4] * m[1] * m[15] + m[4] * m[3] * m[13] + m[12] * m[1] * m[7] -
                    m[12] * m[3] * m[5]
        inv[14] =
            -m[0] * m[5] * m[14] + m[0] * m[6] * m[13] + m[4] * m[1] * m[14] - m[4] * m[2] * m[13] - m[12] * m[1] * m[6] +
                    m[12] * m[2] * m[5]
        inv[3] =
            -m[1] * m[6] * m[11] + m[1] * m[7] * m[10] + m[5] * m[2] * m[11] - m[5] * m[3] * m[10] - m[9] * m[2] * m[7] +
                    m[9] * m[3] * m[6]
        inv[7] =
            m[0] * m[6] * m[11] - m[0] * m[7] * m[10] - m[4] * m[2] * m[11] + m[4] * m[3] * m[10] + m[8] * m[2] * m[7] -
                    m[8] * m[3] * m[6]
        inv[11] =
            -m[0] * m[5] * m[11] + m[0] * m[7] * m[9] + m[4] * m[1] * m[11] - m[4] * m[3] * m[9] - m[8] * m[1] * m[7] +
                    m[8] * m[3] * m[5]
        inv[15] =
            m[0] * m[5] * m[10] - m[0] * m[6] * m[9] - m[4] * m[1] * m[10] + m[4] * m[2] * m[9] + m[8] * m[1] * m[6] -
                    m[8] * m[2] * m[5]
        var det: Double
        det = m[0] * inv[0] + m[1] * inv[4] + m[2] * inv[8] + m[3] * inv[12]
        if (det == 0.0) {
            return null
        }
        det = 1.0 / det
        for (i in 0..15) {
            inv[i] = inv[i] * det
        }
        return ret
    }

    fun mult(b: Matrix?): Matrix {
        return Matrix(
            multiply(
                m, b!!.m
            )
        )
    }

    fun premult(b: Matrix): Matrix {
        return Matrix(multiply(b.m, m))
    }

    companion object {
        private fun trim(s: String): String {
            return s.substring(s.length - 7)
        }

        private fun multiply(a: DoubleArray, b: DoubleArray): DoubleArray {
            val resultant = DoubleArray(16)
            for (i in 0..3) {
                for (j in 0..3) {
                    for (k in 0..3) {
                        resultant[i + 4 * j] += a[i + 4 * k] * b[k + 4 * j]
                    }
                }
            }
            return resultant
        }

        @JvmStatic
        fun main(args: Array<String>) {
            val m = Matrix()
            val inv = Matrix()
            m.m[0] = 100.0
            m.m[5] = 12.0
            m.m[10] = 63.0
            m.m[3] = 12.0
            m.m[7] = 34.0
            m.m[11] = 17.0
            println(" matrix ")
            m.print()
            println(" inv ")
            m.invers(inv)!!.print()
            println(" inv*matrix ")
            m.mult(m.invers(inv)).print()
        }
    }
}