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
package com.google.constraintlayout.ext.graph3d.objects

import android.support.composegraph3d.lib.Object3D
import android.support.composegraph3d.lib.Scene3D
import android.support.composegraph3d.lib.Scene3D.Companion.drawline
import android.support.composegraph3d.lib.Scene3D.Companion.hsvToRgb
import android.support.composegraph3d.lib.Scene3D.Companion.isBackface
import android.support.composegraph3d.lib.Scene3D.Companion.triangle
import android.support.composegraph3d.lib.VectorUtil.dot
import android.support.composegraph3d.lib.VectorUtil.triangleNormal

/**
 * Draws box along the axis
 */
class AxisBox : Object3D() {
    var color = -0xefefdf
    fun setRange(minX: Float, maxX: Float, minY: Float, maxY: Float, minZ: Float, maxZ: Float) {
        mMinX = minX
        mMaxX = maxX
        mMinY = minY
        mMaxY = maxY
        mMinZ = minZ
        mMaxZ = maxZ
        buildBox()
    }

    fun buildBox() {
        vert = FloatArray(8 * 3) // cube 8 corners
        tVert = FloatArray(vert.size)
        for (i in 0..7) {
            vert[i * 3] = if (i and 1 == 0) mMinX else mMaxX // X
            vert[i * 3 + 1] = if (i shr 1 and 1 == 0) mMinY else mMaxY // Y
            vert[i * 3 + 2] = if (i shr 2 and 1 == 0) mMinZ else mMaxZ // Z
        }
        index = IntArray(6 * 2 * 3) // 6 sides x 2 triangles x 3 points per triangle
        val sides = intArrayOf( // pattern of clockwise triangles around cube
            0, 2, 1, 3, 1, 2,
            0, 1, 4, 5, 4, 1,
            0, 4, 2, 6, 2, 4,
            7, 6, 5, 4, 5, 6,
            7, 3, 6, 2, 6, 3,
            7, 5, 3, 1, 3, 5
        )
        index = IntArray(sides.size)
        for (i in sides.indices) {
            index[i] = sides[i] * 3
        }
    }

    fun render_old(s: Scene3D?, zbuff: FloatArray?, img: IntArray?, w: Int, h: Int) {
        var i = 0
        while (i < index.size) {
            val p1 = index[i]
            val p2 = index[i + 1]
            val p3 = index[i + 2]
            val height = (vert[p1 + 2] + vert[p3 + 2] + vert[p2 + 2]) / 3
            val `val` = (255 * Math.abs(height)).toInt()
            drawline(
                zbuff!!, img!!, color, w, h,
                tVert[p1], tVert[p1 + 1], tVert[p1 + 2] - 0.01f,
                tVert[p2], tVert[p2 + 1], tVert[p2 + 2] - 0.01f
            )
            i += 3
        }
    }

    override fun render(s: Scene3D, zbuff: FloatArray, img: IntArray, w: Int, h: Int) {
       // raster_color(s, zbuff, img, w, h)
        var i = 0
        while (i < index.size) {
            val p1 = index[i]
            val p2 = index[i + 1]
            val p3 = index[i + 2]
            val front = isBackface(
                tVert[p1], tVert[p1 + 1], tVert[p1 + 2],
                tVert[p2], tVert[p2 + 1], tVert[p2 + 2],
                tVert[p3], tVert[p3 + 1], tVert[p3 + 2]
            )
            if (front) {
                drawline(
                    zbuff, img, color, w, h,
                    tVert[p1], tVert[p1 + 1], tVert[p1 + 2] - 0.01f,
                    tVert[p2], tVert[p2 + 1], tVert[p2 + 2] - 0.01f
                )
                drawline(
                    zbuff, img, color, w, h,
                    tVert[p1], tVert[p1 + 1], tVert[p1 + 2] - 0.01f,
                    tVert[p3], tVert[p3 + 1], tVert[p3 + 2] - 0.01f
                )
                i += 3
                continue
            }
            drawline(
                zbuff, img, color, w, h,
                tVert[p1], tVert[p1 + 1], tVert[p1 + 2] - 0.01f,
                tVert[p2], tVert[p2 + 1], tVert[p2 + 2] - 0.01f
            )
            drawTicks(
                zbuff, img, color, w, h,
                tVert[p1], tVert[p1 + 1], tVert[p1 + 2] - 0.01f,
                tVert[p2], tVert[p2 + 1], tVert[p2 + 2] - 0.01f,
                tVert[p3] - tVert[p1], tVert[p3 + 1] - tVert[p1 + 1], tVert[p3 + 2] - tVert[p1 + 2]
            )
            drawline(
                zbuff, img, color, w, h,
                tVert[p1], tVert[p1 + 1], tVert[p1 + 2] - 0.01f,
                tVert[p3], tVert[p3 + 1], tVert[p3 + 2] - 0.01f
            )
            drawTicks(
                zbuff, img, color, w, h,
                tVert[p1], tVert[p1 + 1], tVert[p1 + 2] - 0.01f,
                tVert[p3], tVert[p3 + 1], tVert[p3 + 2] - 0.01f,
                tVert[p2] - tVert[p1], tVert[p2 + 1] - tVert[p1 + 1], tVert[p2 + 2] - tVert[p1 + 2]
            )
            i += 3
        }
    }

    var screen = floatArrayOf(0f, 0f, -1f)

    init {
        type = 1
    }

    override fun raster_color(s: Scene3D, zbuff: FloatArray, img: IntArray, w: Int, h: Int) {
        var i = 0
        while (i < index.size) {
            val p1 = index[i]
            val p2 = index[i + 1]
            val p3 = index[i + 2]
            val back = isBackface(
                tVert[p1], tVert[p1 + 1], tVert[p1 + 2],
                tVert[p2], tVert[p2 + 1], tVert[p2 + 2],
                tVert[p3], tVert[p3 + 1], tVert[p3 + 2]
            )
            if (back) {
                i += 3
                continue
            }
            triangleNormal(tVert, p1, p3, p2, s.tmpVec)
            val ss = dot(s.tmpVec, screen)
            val defuse = dot(s.tmpVec, s.mTransformedLight)
            val ambient = 0.5f
            val bright = Math.min(Math.max(0f, defuse + ambient), 1f)
            val hue = 0.4f
            val sat = 0.1f
            val col = hsvToRgb(hue, sat, bright)
            triangle(
                zbuff, img, col, w, h, tVert[p1], tVert[p1 + 1],
                tVert[p1 + 2], tVert[p2], tVert[p2 + 1],
                tVert[p2 + 2], tVert[p3], tVert[p3 + 1],
                tVert[p3 + 2]
            )
            i += 3
        }
    }

    companion object {
        fun drawTicks(
            zbuff: FloatArray?, img: IntArray?, color: Int, w: Int, h: Int,
            p1x: Float, p1y: Float, p1z: Float,
            p2x: Float, p2y: Float, p2z: Float,
            nx: Float, ny: Float, nz: Float
        ) {
            val tx = nx / 10
            val ty = ny / 10
            val tz = nz / 10
            var f = 0f
            while (f <= 1) {
                val px = p1x + f * (p2x - p1x)
                val py = p1y + f * (p2y - p1y)
                val pz = p1z + f * (p2z - p1z)
                drawline(
                    zbuff!!, img!!, color, w, h,
                    px, py, pz - 0.01f,
                    px + tx, py + ty, pz + tz - 0.01f
                )
                f += 0.1f
            }
        }
    }
}