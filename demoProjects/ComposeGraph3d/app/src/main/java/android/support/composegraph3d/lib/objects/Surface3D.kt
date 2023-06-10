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
package android.support.composegraph3d.lib.objects

import android.support.composegraph3d.lib.Object3D

/**
 * Plots a surface based on Z = f(X,Y)
 */
class Surface3D(private val mFunction: Function) : Object3D() {
    private val mZoomZ = 1f
    var mSize = 100
    fun setRange(minX: Float, maxX: Float, minY: Float, maxY: Float, minZ: Float, maxZ: Float) {
        mMinX = minX
        mMaxX = maxX
        mMinY = minY
        mMaxY = maxY
        mMinZ = minZ
        mMaxZ = maxZ
        computeSurface(java.lang.Float.isNaN(mMinZ))
    }

    fun setArraySize(size: Int) {
        mSize = size
        computeSurface(false)
    }

    interface Function {
        fun eval(x: Float, y: Float): Float
    }

    fun computeSurface(resetZ: Boolean) {
        val n = (mSize + 1) * (mSize + 1)
        makeVert(n)
        makeIndexes(mSize * mSize * 2)
        calcSurface(resetZ)
    }

    fun calcSurface(resetZ: Boolean) {
        val min_x = mMinX
        val max_x = mMaxX
        val min_y = mMinY
        val max_y = mMaxY
        var min_z = Float.MAX_VALUE
        var max_z = -Float.MAX_VALUE
        var count = 0
        for (iy in 0..mSize) {
            val y = min_y + iy * (max_y - min_y) / mSize
            for (ix in 0..mSize) {
                val x = min_x + ix * (max_x - min_x) / mSize
                val delta = 0.001f
                var dx = (mFunction.eval(x + delta, y) - mFunction.eval(x - delta, y)) / (2 * delta)
                var dy = (mFunction.eval(x, y + delta) - mFunction.eval(x, y - delta)) / (2 * delta)
                var dz = 1f
                val norm = Math.sqrt((dz * dz + dx * dx + dy * dy).toDouble()).toFloat()
                dx /= norm
                dy /= norm
                dz /= norm
                normal[count] = dx
                vert[count++] = x
                normal[count] = dy
                vert[count++] = y
                normal[count] = -dz
                var z = mFunction.eval(x, y)
                if (java.lang.Float.isNaN(z) || java.lang.Float.isInfinite(z)) {
                    val epslonX = 0.000005232f
                    val epslonY = 0.00000898f
                    z = mFunction.eval(x + epslonX, y + epslonY)
                }
                vert[count++] = z
                if (java.lang.Float.isNaN(z)) {
                    continue
                }
                if (java.lang.Float.isInfinite(z)) {
                    continue
                }
                min_z = Math.min(z, min_z)
                max_z = Math.max(z, max_z)
            }
            if (resetZ) {
                mMinZ = min_z
                mMaxZ = max_z
            }
        }
        // normalize range in z
        val xrange = mMaxX - mMinX
        val yrange = mMaxY - mMinY
        val zrange = mMaxZ - mMinZ
        if (zrange != 0f && resetZ) {
            val xyrange = (xrange + yrange) / 2
            val scalez = xyrange / zrange
            var i = 0
            while (i < vert.size) {
                var z = vert[i + 2]
                if (java.lang.Float.isNaN(z) || java.lang.Float.isInfinite(z)) {
                    z = if (i > 3) {
                        vert[i - 1]
                    } else {
                        vert[i + 5]
                    }
                }
                vert[i + 2] = z * scalez * mZoomZ
                i += 3
            }
            if (resetZ) {
                mMinZ *= scalez
                mMaxZ *= scalez
            }
        }
        count = 0
        for (iy in 0 until mSize) {
            for (ix in 0 until mSize) {
                val p1 = 3 * (ix + iy * (mSize + 1))
                val p2 = 3 * (1 + ix + iy * (mSize + 1))
                val p3 = 3 * (ix + (iy + 1) * (mSize + 1))
                val p4 = 3 * (1 + ix + (iy + 1) * (mSize + 1))
                index[count++] = p1
                index[count++] = p2
                index[count++] = p3
                index[count++] = p4
                index[count++] = p3
                index[count++] = p2
            }
        }
    }
}