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
import java.util.*

/**
 * This calculates the matrix that transforms triangles from world space to screen space.
 */
class ViewMatrix : Matrix() {
    var lookPoint: DoubleArray? = null
    var eyePoint: DoubleArray? = null
    var upVector: DoubleArray? = null
    var screenWidth = 0.0
    var mScreenDim: IntArray? = null
    var mTmp1 = DoubleArray(3)
    override fun print() {
        println("mLookPoint  :" + toStr(lookPoint))
        println("mEyePoint   :" + toStr(eyePoint))
        println("mUpVector   :" + toStr(upVector))
        println("mScreenWidth:" + toStr(screenWidth))
        println("mScreenDim  :[" + mScreenDim!![0] + "," + mScreenDim!![1] + "]")
    }

    fun setScreenDim(x: Int, y: Int) {
        mScreenDim = intArrayOf(x, y)
    }

    fun makeUnit() {}
    fun fixUpPoint() {
        val zv = doubleArrayOf(
            eyePoint!![0] - lookPoint!![0],
            eyePoint!![1] - lookPoint!![1],
            eyePoint!![2] - lookPoint!![2]
        )
        VectorUtil.normalize(zv)
        val rv = DoubleArray(3)
        VectorUtil.cross(zv, upVector, rv)
        VectorUtil.cross(zv, rv, upVector)
        VectorUtil.normalize(upVector)
        VectorUtil.mult(upVector, -1.0, upVector)
    }

    fun calcMatrix() {
        if (mScreenDim == null) {
            return
        }
        val scale = screenWidth / mScreenDim!![0]
        val zv = doubleArrayOf(
            lookPoint!![0] - eyePoint!![0],
            lookPoint!![1] - eyePoint!![1],
            lookPoint!![2] - eyePoint!![2]
        )
        VectorUtil.normalize(zv)
        val m = DoubleArray(16)
        m[2] = zv[0] * scale
        m[6] = zv[1] * scale
        m[10] = zv[2] * scale
        m[14] = 0.0
        calcRight(zv, upVector, zv)
        m[0] = zv[0] * scale
        m[4] = zv[1] * scale
        m[8] = zv[2] * scale
        m[12] = 0.0
        m[1] = -upVector!![0] * scale
        m[5] = -upVector!![1] * scale
        m[9] = -upVector!![2] * scale
        m[13] = 0.0
        val sw = mScreenDim!![0] / 2 - 0.5
        val sh = mScreenDim!![1] / 2 - 0.5
        val sz = -0.5
        m[3] = eyePoint!![0] - (m[0] * sw + m[1] * sh + m[2] * sz)
        m[7] = eyePoint!![1] - (m[4] * sw + m[5] * sh + m[6] * sz)
        m[11] = eyePoint!![2] - (m[8] * sw + m[9] * sh + m[10] * sz)
        m[15] = 1.0
        this.m = m
    }

    private fun calcLook(tri: Object3D, voxelDim: FloatArray, w: Int, h: Int) {
        var minx = Float.MAX_VALUE
        var miny = Float.MAX_VALUE
        var minz = Float.MAX_VALUE
        var maxx = -Float.MAX_VALUE
        var maxy = -Float.MAX_VALUE
        var maxz = -Float.MAX_VALUE
        var i = 0
        while (i < tri.vert.size) {
            maxx = Math.max(tri.vert[i], maxx)
            minx = Math.min(tri.vert[i], minx)
            maxy = Math.max(tri.vert[i + 1], maxy)
            miny = Math.min(tri.vert[i + 1], miny)
            maxz = Math.max(tri.vert[i + 2], maxz)
            minz = Math.min(tri.vert[i + 2], minz)
            i += 3
        }
        lookPoint = doubleArrayOf(
            (voxelDim[0] * (maxx + minx) / 2).toDouble(),
            (voxelDim[1] * (maxy + miny) / 2).toDouble(),
            (voxelDim[2] * (maxz + minz) / 2).toDouble()
        )
        screenWidth = (Math.max(
            voxelDim[0] * (maxx - minx), Math.max(
                voxelDim[1] * (maxy - miny), voxelDim[2] * (maxz - minz)
            )
        ) * 2).toDouble()
    }

    private fun calcLook(triW: Object3D, w: Int, h: Int) {
        var minx = Float.MAX_VALUE
        var miny = Float.MAX_VALUE
        var minz = Float.MAX_VALUE
        var maxx = -Float.MAX_VALUE
        var maxy = -Float.MAX_VALUE
        var maxz = -Float.MAX_VALUE
        var i = 0
        while (i < triW.vert.size) {
            maxx = Math.max(triW.vert[i], maxx)
            minx = Math.min(triW.vert[i], minx)
            maxy = Math.max(triW.vert[i + 1], maxy)
            miny = Math.min(triW.vert[i + 1], miny)
            maxz = Math.max(triW.vert[i + 2], maxz)
            minz = Math.min(triW.vert[i + 2], minz)
            i += 3
        }
        lookPoint = doubleArrayOf(
            ((maxx + minx) / 2).toDouble(),
            ((maxy + miny) / 2).toDouble(),
            ((maxz + minz) / 2).toDouble()
        )
        screenWidth = Math.max(maxx - minx, Math.max(maxy - miny, maxz - minz)).toDouble()
    }

    fun look(dir: Char, tri: Object3D, voxelDim: FloatArray?, w: Int, h: Int) {
        calcLook(tri, w, h)
        var dx = dir.code shr 4 and 0xF
        var dy = dir.code shr 8 and 0xF
        var dz = dir.code shr 0 and 0xF
        if (dx > 1) {
            dx = -1
        }
        if (dy > 1) {
            dy = -1
        }
        if (dz > 1) {
            dz = -1
        }
        eyePoint = doubleArrayOf(
            lookPoint!![0] + 2 * screenWidth * dx,
            lookPoint!![1] + 2 * screenWidth * dy,
            lookPoint!![2] + 2 * screenWidth * dz
        )
        val zv = doubleArrayOf(-dx.toDouble(), -dy.toDouble(), -dz.toDouble())
        val rv =
            doubleArrayOf(if (dx == 0) 1.0 else 0.0, if (dx == 0) 0.0 else 1.0 , 0.0)
        val up = DoubleArray(3)
        VectorUtil.norm(zv)
        VectorUtil.norm(rv)
        VectorUtil.cross(zv, rv, up)
        VectorUtil.cross(zv, up, rv)
        VectorUtil.cross(zv, rv, up)
        upVector = up
        mScreenDim = intArrayOf(w, h)
        calcMatrix()
    }

    fun lookAt(tri: Object3D, voxelDim: FloatArray, w: Int, h: Int) {
        calcLook(tri, voxelDim, w, h)
        eyePoint = doubleArrayOf(
            lookPoint!![0] + screenWidth,
            lookPoint!![1] + screenWidth,
            lookPoint!![2] + screenWidth
        )
        val zv = doubleArrayOf(-1.0, -1.0, -1.0)
        val rv = doubleArrayOf(1.0, 1.0, 0.0)
        val up = DoubleArray(3)
        VectorUtil.norm(zv)
        VectorUtil.norm(rv)
        VectorUtil.cross(zv, rv, up)
        VectorUtil.cross(zv, up, rv)
        VectorUtil.cross(zv, rv, up)
        upVector = up
        mScreenDim = intArrayOf(w, h)
        calcMatrix()
    }

    var mStartx = 0f
    var mStarty = 0f
    var mPanStartX = Float.NaN
    var mPanStartY = Float.NaN
    var mStartMatrix: Matrix? = null
    var mStartV = DoubleArray(3)
    var mMoveToV = DoubleArray(3)
    lateinit var mStartEyePoint: DoubleArray
    lateinit var mStartUpVector: DoubleArray
    var mQ = Quaternion(0.0, 0.0, 0.0, 0.0)
    fun trackBallUP(x: Float, y: Float) {}
    fun trackBallDown(x: Float, y: Float) {
        mStartx = x
        mStarty = y
        ballToVec(x, y, mStartV)
        mStartEyePoint = Arrays.copyOf(eyePoint, m.size)
        mStartUpVector = Arrays.copyOf(upVector, m.size)
        mStartMatrix = Matrix(this)
        mStartMatrix!!.makeRotation()
    }

    fun trackBallMove(x: Float, y: Float) {
        if (mStartx == x && mStarty == y) {
            return
        }
        ballToVec(x, y, mMoveToV)
        val angle: Double = Quaternion.Companion.calcAngle(mStartV, mMoveToV)
        var axis: DoubleArray? = Quaternion.Companion.calcAxis(mStartV, mMoveToV)
        axis = mStartMatrix!!.vecmult(axis)
        mQ[angle] = axis
        VectorUtil.sub(lookPoint, mStartEyePoint, eyePoint)
        eyePoint = mQ.rotateVec(eyePoint)
        upVector = mQ.rotateVec(mStartUpVector)
        VectorUtil.sub(lookPoint, eyePoint, eyePoint)
        calcMatrix()
    }

    fun panDown(x: Float, y: Float) {
        mPanStartX = x
        mPanStartY = y
    }

    fun panMove(x: Float, y: Float) {
        val scale = screenWidth / mScreenDim!![0]
        if (java.lang.Float.isNaN(mPanStartX)) {
            mPanStartX = x
            mPanStartY = y
        }
        val dx = scale * (x - mPanStartX)
        val dy = scale * (y - mPanStartY)
        VectorUtil.sub(eyePoint, lookPoint, mTmp1)
        VectorUtil.normalize(mTmp1)
        VectorUtil.cross(mTmp1, upVector, mTmp1)
        VectorUtil.madd(mTmp1, dx, eyePoint, eyePoint)
        VectorUtil.madd(mTmp1, dx, lookPoint, lookPoint)
        VectorUtil.madd(upVector, dy, eyePoint, eyePoint)
        VectorUtil.madd(upVector, dy, lookPoint, lookPoint)
        mPanStartY = y
        mPanStartX = x
        calcMatrix()
    }

    fun panUP() {
        mPanStartX = Float.NaN
        mPanStartY = Float.NaN
    }

    fun ballToVec(x: Float, y: Float, v: DoubleArray) {
        val ballRadius = Math.min(mScreenDim!![0], mScreenDim!![1]) * .4f
        val cx = mScreenDim!![0] / 2.0
        val cy = mScreenDim!![1] / 2.0
        var dx = (cx - x) / ballRadius
        var dy = (cy - y) / ballRadius
        var scale = dx * dx + dy * dy
        if (scale > 1) {
            scale = Math.sqrt(scale)
            dx = dx / scale
            dy = dy / scale
        }
        val dz = Math.sqrt(Math.abs(1 - (dx * dx + dy * dy)))
        v[0] = dx
        v[1] = dy
        v[2] = dz
        VectorUtil.normalize(v)
    }

    companion object {
        const val UP_AT = 0x001.toChar()
        const val DOWN_AT = 0x002.toChar()
        const val RIGHT_AT = 0x010.toChar()
        const val LEFT_AT = 0x020.toChar()
        const val FORWARD_AT = 0x100.toChar()
        const val BEHIND_AT = 0x200.toChar()
        private fun toStr(d: Double): String {
            val s = "       " + df.format(d)
            return s.substring(s.length - 8)
        }

        private fun toStr(d: DoubleArray?): String {
            var s = "["
            for (i in d!!.indices) {
                s += toStr(d[i])
            }
            return "$s]"
        }

        private val df = DecimalFormat("##0.000")
        fun calcRight(a: DoubleArray, b: DoubleArray?, out: DoubleArray?) {
            VectorUtil.cross(a, b, out)
        }

        @JvmStatic
        fun main(args: Array<String>) {
            val up = doubleArrayOf(0.0, 0.0, 1.0)
            val look = doubleArrayOf(0.0, 0.0, 0.0)
            val eye = doubleArrayOf(-10.0, 0.0, 0.0)
            val v = ViewMatrix()
            v.eyePoint = eye
            v.lookPoint = look
            v.upVector = up
            v.screenWidth = 10.0
            v.setScreenDim(512, 512)
            v.calcMatrix()
        }
    }
}