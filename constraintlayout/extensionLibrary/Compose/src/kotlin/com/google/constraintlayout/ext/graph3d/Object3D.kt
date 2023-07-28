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

import android.support.composegraph3d.lib.Scene3D.Companion.trianglePhong
import android.support.composegraph3d.lib.objects.Surface3D

/**
 * This represents 3d Object in this system.
 */
open class Object3D {
    lateinit var vert: FloatArray
    lateinit var normal: FloatArray
    lateinit var index: IntArray
    lateinit var tVert  : FloatArray
    protected var mMinX = 0f
    protected var mMaxX = 0f
    protected var mMinY = 0f
    protected var mMaxY = 0f
     var mMinZ = 0f
     var mMaxZ // bounds in x,y & z
            = 0f
    var type = 4
    var mAmbient = 0.3f
    var mDefuse = 0.7f
    var mSaturation = 0.6f
    fun makeVert(n: Int) {
        vert = FloatArray(n * 3)
        tVert = FloatArray(n * 3)
        normal = FloatArray(n * 3)
    }

    fun makeIndexes(n: Int) {
        index = IntArray(n * 3)
    }

    fun transform(m: Matrix?) {
        var i = 0
        while (i < vert.size) {
            m!!.mult3(vert, i, tVert, i)
            i += 3
        }
    }

    open fun render(s: Scene3D, zbuff: FloatArray, img: IntArray, width: Int, height: Int) {
        when (type) {
            0 -> raster_height(s, zbuff, img, width, height)
            1 -> raster_outline(s, zbuff, img, width, height)
            2 -> raster_color(s, zbuff, img, width, height)
            3 -> raster_lines(s, zbuff, img, width, height)
            4 -> rasterPhong(this,s, zbuff, img, width, height)
        }
    }

    private fun raster_lines(s: Scene3D, zbuff: FloatArray, img: IntArray, w: Int, h: Int) {
        var i = 0
        while (i < index.size) {
            val p1 = index[i]
            val p2 = index[i + 1]
            val p3 = index[i + 2]
            val height = (vert[p1 + 2] + vert[p3 + 2] + vert[p2 + 2]) / 3
            val `val` = (255 * Math.abs(height)).toInt()
            Scene3D.Companion.triangle(
                zbuff, img, 0x10001 * `val` + 0x100 * (255 - `val`), w, h, tVert[p1], tVert[p1 + 1],
                tVert[p1 + 2], tVert[p2], tVert[p2 + 1],
                tVert[p2 + 2], tVert[p3], tVert[p3 + 1],
                tVert[p3 + 2]
            )
            Scene3D.Companion.drawline(
                zbuff, img, s.lineColor, w, h,
                tVert[p1], tVert[p1 + 1], tVert[p1 + 2] - 0.01f,
                tVert[p2], tVert[p2 + 1], tVert[p2 + 2] - 0.01f
            )
            Scene3D.Companion.drawline(
                zbuff, img, s.lineColor, w, h,
                tVert[p1], tVert[p1 + 1], tVert[p1 + 2] - 0.01f,
                tVert[p3], tVert[p3 + 1], tVert[p3 + 2] - 0.01f
            )
            i += 3
        }
    }

    fun raster_height(s: Scene3D?, zbuff: FloatArray, img: IntArray, w: Int, h: Int) {
        var i = 0
        while (i < index.size) {
            val p1 = index[i]
            val p2 = index[i + 1]
            val p3 = index[i + 2]
            var height = (vert[p1 + 2] + vert[p3 + 2] + vert[p2 + 2]) / 3
            height = (height - mMinZ) / (mMaxZ - mMinZ)
            val col: Int = Scene3D.Companion.hsvToRgb(
                height,
                Math.abs(2 * (height - 0.5f)),
                Math.sqrt(height.toDouble()).toFloat()
            )
            Scene3D.Companion.triangle(
                zbuff, img, col, w, h, tVert[p1], tVert[p1 + 1],
                tVert[p1 + 2], tVert[p2], tVert[p2 + 1],
                tVert[p2 + 2], tVert[p3], tVert[p3 + 1],
                tVert[p3 + 2]
            )
            i += 3
        }
    }

    // float mSpec = 0.2f;
    open fun raster_color(s: Scene3D, zbuff: FloatArray, img: IntArray, w: Int, h: Int) {
        var i = 0
        while (i < index.size) {
            val p1 = index[i]
            val p2 = index[i + 1]
            val p3 = index[i + 2]
            VectorUtil.triangleNormal(tVert, p1, p2, p3, s.tmpVec)
            val defuse = VectorUtil.dot(s.tmpVec, s.mTransformedLight)
            var height = (vert[p1 + 2] + vert[p3 + 2] + vert[p2 + 2]) / 3
            height = (height - mMinZ) / (mMaxZ - mMinZ)
            val bright = Math.min(1f, Math.max(0f, mDefuse * defuse + mAmbient))
            val hue = (height - Math.floor(height.toDouble())).toFloat()
            val sat = 0.8f
            val col: Int = Scene3D.Companion.hsvToRgb(hue, sat, bright)
            Scene3D.Companion.triangle(
                zbuff, img, col, w, h, tVert[p1], tVert[p1 + 1],
                tVert[p1 + 2], tVert[p2], tVert[p2 + 1],
                tVert[p2 + 2], tVert[p3], tVert[p3 + 1],
                tVert[p3 + 2]
            )
            i += 3
        }
    }

    private fun color(hue: Float, sat: Float, bright: Float): Int {
        var hue = hue
        var bright = bright
        hue = hue(hue)
        bright = bright(bright)
        return Scene3D.Companion.hsvToRgb(hue, sat, bright)
    }

    private fun hue(hue: Float): Float {
        return (hue - Math.floor(hue.toDouble())).toFloat()
    }

    private fun bright(bright: Float): Float {
        return Math.min(1f, Math.max(0f, bright))
    }

    private fun defuse(normals: FloatArray, off: Int, light: FloatArray?): Float {
        // s.mMatrix.mult3v(normal,off,s.tmpVec);
        return Math.abs(VectorUtil.dot(normal, off, light))
    }

    fun raster_phong(s: Scene3D, zbuff: FloatArray, img: IntArray, w: Int, h: Int) {
        var i = 0
        println(" render ")

        while (i < index.size) {
            val p1 = index[i]
            val p2 = index[i + 1]
            val p3 = index[i + 2]
            //    VectorUtil.triangleNormal(tVert, p1, p2, p3, s.tmpVec);


//            float defuse1 = VectorUtil.dot(normal, p1, s.mTransformedLight);
//            float defuse2 = VectorUtil.dot(normal, p2, s.mTransformedLight);
//            float defuse3 = VectorUtil.dot(normal, p3, s.mTransformedLight);
            val defuse1 = defuse(normal, p1, s.mTransformedLight)
            val defuse2 = defuse(normal, p2, s.mTransformedLight)
            val defuse3 = defuse(normal, p3, s.mTransformedLight)
            val col1_hue = hue((vert[p1 + 2] - mMinZ) / (mMaxZ - mMinZ))
            val col2_hue = hue((vert[p2 + 2] - mMinZ) / (mMaxZ - mMinZ))
            val col3_hue = hue((vert[p3 + 2] - mMinZ) / (mMaxZ - mMinZ))
            val col1_bright =  bright(mDefuse * defuse1 + mAmbient)
            val col2_bright =  bright(mDefuse * defuse2 + mAmbient)
            val col3_bright =  bright(mDefuse * defuse3 + mAmbient)
            Scene3D.Companion.trianglePhong(
                zbuff, img,
                col1_hue, col1_bright,
                col2_hue, col2_bright,
                col3_hue, col3_bright,
                mSaturation,
                w, h,
                tVert[p1], tVert[p1 + 1], tVert[p1 + 2],
                tVert[p2], tVert[p2 + 1], tVert[p2 + 2],
                tVert[p3], tVert[p3 + 1], tVert[p3 + 2]
            )
            i += 3
        }
    }
    fun rasterPhong(mSurface: Object3D, s: Scene3D, zbuff: FloatArray?, img: IntArray?, w: Int, h: Int) {
        var i = 0
        while (i < mSurface.index.size) {
            val p1: Int = mSurface.index.get(i)
            val p2: Int = mSurface.index.get(i + 1)
            val p3: Int = mSurface.index.get(i + 2)

            //    VectorUtil.triangleNormal(tVert, p1, p2, p3, s.tmpVec);


//            float defuse1 = VectorUtil.dot(normal, p1, s.mTransformedLight);
//            float defuse2 = VectorUtil.dot(normal, p2, s.mTransformedLight);
//            float defuse3 = VectorUtil.dot(normal, p3, s.mTransformedLight);
            val defuse1 = defuse(mSurface.normal, p1, s.mTransformedLight)
            val defuse2 = defuse(mSurface.normal, p2, s.mTransformedLight)
            val defuse3 = defuse(mSurface.normal, p3, s.mTransformedLight)
            val col1_hue =
                hue((mSurface.vert.get(p1 + 2) - mSurface.mMinZ) / (mSurface.mMaxZ - mSurface.mMinZ))
            val col2_hue =
                hue((mSurface.vert.get(p2 + 2) - mSurface.mMinZ) / (mSurface.mMaxZ - mSurface.mMinZ))
            val col3_hue =
                hue((mSurface.vert.get(p3 + 2) - mSurface.mMinZ) / (mSurface.mMaxZ - mSurface.mMinZ))
            val col1_bright = bright(mDefuse * defuse1 + mAmbient)
            val col2_bright = bright(mDefuse * defuse2 + mAmbient)
            val col3_bright = bright(mDefuse * defuse3 + mAmbient)
            trianglePhong(
                zbuff!!, img!!,
                col1_hue, col1_bright,
                col2_hue, col2_bright,
                col3_hue, col3_bright,
                0.6f,
                w, h,
                mSurface.tVert.get(p1), mSurface.tVert.get(p1 + 1), mSurface.tVert.get(p1 + 2),
                mSurface.tVert.get(p2), mSurface.tVert.get(p2 + 1), mSurface.tVert.get(p2 + 2),
                mSurface.tVert.get(p3), mSurface.tVert.get(p3 + 1), mSurface.tVert.get(p3 + 2)
            )
            i += 3
        }
    }

    fun raster_outline(s: Scene3D, zBuff: FloatArray, img: IntArray, w: Int, h: Int) {
        var i = 0
        while (i < index.size) {
            val p1 = index[i]
            val p2 = index[i + 1]
            val p3 = index[i + 2]
            Scene3D.Companion.triangle(
                zBuff, img, s.background, w, h,
                tVert[p1], tVert[p1 + 1], tVert[p1 + 2],
                tVert[p2], tVert[p2 + 1], tVert[p2 + 2],
                tVert[p3], tVert[p3 + 1], tVert[p3 + 2]
            )
            Scene3D.Companion.drawline(
                zBuff, img, s.lineColor, w, h,
                tVert[p1], tVert[p1 + 1], tVert[p1 + 2],
                tVert[p2], tVert[p2 + 1], tVert[p2 + 2]
            )
            Scene3D.Companion.drawline(
                zBuff, img, s.lineColor, w, h,
                tVert[p1], tVert[p1 + 1], tVert[p1 + 2],
                tVert[p3], tVert[p3 + 1], tVert[p3 + 2]
            )
            i += 3
        }
    }

    fun center(): DoubleArray {
        return doubleArrayOf(
            (
                    (mMinX + mMaxX) / 2).toDouble(),
            ((mMinY + mMaxY) / 2).toDouble(),
            ((mMinZ + mMaxZ) / 2
                    ).toDouble()
        )
    }

    fun centerX(): Float {
        return (mMaxX + mMinX) / 2
    }

    fun centerY(): Float {
        return (mMaxY + mMinY) / 2
    }

    fun rangeX(): Float {
        return (mMaxX - mMinX) / 2
    }

    fun rangeY(): Float {
        return (mMaxY - mMinY) / 2
    }

    fun size(): Double {
        return Math.hypot(
            (mMaxX - mMinX).toDouble(),
            Math.hypot((mMaxY - mMinY).toDouble(), (mMaxZ - mMinZ).toDouble())
        ) / 2
    }
}