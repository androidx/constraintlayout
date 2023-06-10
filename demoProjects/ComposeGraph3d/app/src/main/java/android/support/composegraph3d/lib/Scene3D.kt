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

import java.util.*
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * This renders 3Dimensional Objects.
 */
class Scene3D {
    var mMatrix = ViewMatrix()
    var mInverse: Matrix? = Matrix()
    public var mObject3D: Object3D? = null
    var mPreObjects: ArrayList<Object3D> = ArrayList<Object3D>()
    var mPostObjects: ArrayList<Object3D> = ArrayList<Object3D>()
    var zBuff: FloatArray? = null
    lateinit var img: IntArray
    private val light = floatArrayOf(0f, 0f, 1f) // The direction of the light source

    @JvmField
    var mTransformedLight = floatArrayOf(0f, 1f, 1f) // The direction of the light source
    var mLightMovesWithCamera = false
    var width = 0
    var height = 0

    @JvmField
    var tmpVec = FloatArray(3)
    var lineColor = -0x1000000
    private val epslonX = 0.000005232f
    private val epslonY = 0.00000898f
    private val mFunction: Function? = null
    var zoom = 1f
    var background = 0

    internal inner class Box {
        var m_box = arrayOf(floatArrayOf(1f, 1f, 1f), floatArrayOf(2f, 3f, 2f))
        var m_x1 = intArrayOf(0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0)
        var m_y1 = intArrayOf(0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 1)
        var m_z1 = intArrayOf(0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1)
        var m_x2 = intArrayOf(0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1)
        var m_y2 = intArrayOf(0, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1)
        var m_z2 = intArrayOf(1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1)
        var m_point1 = FloatArray(3)
        var m_point2 = FloatArray(3)
        var m_draw1 = FloatArray(3)
        var m_draw2 = FloatArray(3)
        fun drawLines(r: LineRender) {
            for (i in 0..11) {
                m_point1[0] = m_box[m_x1[i]][0]
                m_point1[1] = m_box[m_y1[i]][1]
                m_point1[2] = m_box[m_z1[i]][2]
                m_point2[0] = m_box[m_x2[i]][0]
                m_point2[1] = m_box[m_y2[i]][1]
                m_point2[2] = m_box[m_z2[i]][2]
                mInverse!!.mult3(m_point1, m_draw1)
                mInverse!!.mult3(m_point2, m_draw2)
                r.draw(
                    m_draw1[0].toInt(),
                    m_draw1[1].toInt(),
                    m_draw2[0].toInt(),
                    m_draw2[1].toInt()
                )
            }
        }
    }

    init {
        VectorUtil.normalize(light)
    }

    fun transformTriangles() {
        transform()
    }

    fun transform() {
        val m = mInverse
        if (mLightMovesWithCamera) {
            mMatrix.mult3v(light, mTransformedLight)
            VectorUtil.normalize(mTransformedLight)
        } else {
            System.arraycopy(light, 0, mTransformedLight, 0, 3)
        }
        mObject3D!!.transform(m)
        for (obj in mPreObjects) {
            obj!!.transform(m)
        }
        for (obj in mPostObjects) {
            obj!!.transform(m)
        }
    }

    var screenWidth: Double
        get() = mMatrix.screenWidth
        set(sw) {
            mMatrix.screenWidth = sw
            mMatrix.calcMatrix()
            mMatrix.invers(mInverse!!)
            transform()
        }

    fun trackBallDown(x: Float, y: Float) {
        mMatrix.trackBallDown(x, y)
        mMatrix.invers(mInverse!!)
    }

    fun trackBallMove(x: Float, y: Float) {
        mMatrix.trackBallMove(x, y)
        mMatrix.invers(mInverse!!)
        transform()
    }

    fun trackBallUP(x: Float, y: Float) {
        mMatrix.trackBallUP(x, y)
        mMatrix.invers(mInverse!!)
    }

    fun update() {
        mMatrix.invers(mInverse!!)
        transform()
    }

    fun panDown(x: Float, y: Float) {
        mMatrix.panDown(x, y)
        mMatrix.invers(mInverse!!)
    }

    fun panMove(x: Float, y: Float) {
        mMatrix.panMove(x, y)
        mMatrix.invers(mInverse!!)
        transform()
    }

    fun panUP() {
        mMatrix.panUP()
    }

    val lookPoint: String
        get() = Arrays.toString(mMatrix.lookPoint)

    fun setScreenDim(width: Int, height: Int, img: IntArray, background: Int) {
        mMatrix.setScreenDim(width, height)
        setupBuffers(width, height, img, background)
        setUpMatrix(width, height)
        transform()
    }

    fun setUpMatrix(width: Int, height: Int) {
        setUpMatrix(width, height, false)
    }

    fun setUpMatrix(width: Int, height: Int, resetOrientation: Boolean) {
        val look_point = mObject3D!!.center()
        val diagonal = mObject3D!!.size() * zoom
        mMatrix.lookPoint = look_point
        if (resetOrientation) {
            val eye_point = doubleArrayOf(
                look_point!![0] - diagonal,
                look_point[1] - diagonal,
                look_point[2] + diagonal
            )
            mMatrix.eyePoint = eye_point
            val up_vector = doubleArrayOf(0.0, 0.0, 1.0)
            mMatrix.upVector = up_vector
        } else {
            mMatrix.fixUpPoint()
        }
        val screenWidth = diagonal * 2
        mMatrix.screenWidth = screenWidth
        mMatrix.setScreenDim(width, height)
        mMatrix.calcMatrix()
        mMatrix.invers(mInverse!!)
    }

    fun notSetUp(): Boolean {
        return mInverse == null
    }

    interface LineRender {
        fun draw(x1: Int, y1: Int, x2: Int, y2: Int)
    }

    fun drawBox(g: LineRender?) {}
    interface Function {
        fun eval(x: Float, y: Float): Float
    }

    fun addPreObject(obj: Object3D) {
        mPreObjects.add(obj)
    }

    fun setObject(obj: Object3D) {
        mObject3D = obj
    }

    fun addPostObject(obj: Object3D) {
        mPostObjects.add(obj)
    }

    fun resetCamera() {
        setUpMatrix(width, height, true)
        transform()
    }

    fun setupBuffers(w: Int, h: Int, img: IntArray, background: Int) {
        width = w
        height = h
        this.background = background
        zBuff = FloatArray(w * h)
        this.img = img
        Arrays.fill(zBuff, Float.MAX_VALUE)
        Arrays.fill(img, background)
    }

    fun render(type: Int) {
        if (zBuff == null) {
            return
        }
        Arrays.fill(zBuff, Float.MAX_VALUE)
        Arrays.fill(img, background)
        for (mPreObject in mPreObjects) {
            mPreObject!!.render(this, zBuff!!, img, width, height)
        }

        mObject3D!!.render(this, zBuff!!, img, width, height)
        for (mPreObject in mPostObjects) {
            mPreObject!!.render(this, zBuff!!, img, width, height)
        }
    }

    companion object {
        private const val TAG = "SurfaceGen"
        private fun min(x1: Int, x2: Int, x3: Int): Int {
            return if (x1 > x2) (if (x2 > x3) x3 else x2) else if (x1 > x3) x3 else x1
        }

        private fun max(x1: Int, x2: Int, x3: Int): Int {
            return if (x1 < x2) (if (x2 < x3) x3 else x2) else if (x1 < x3) x3 else x1
        }

        fun hsvToRgb_slow(hue: Float, saturation: Float, value: Float): Int {
            val h = (hue * 6).toInt()
            val f = hue * 6 - h
            val p = (0.5f + 255 * value * (1 - saturation)).toInt()
            val q = (0.5f + 255 * value * (1 - f * saturation)).toInt()
            val t = (0.5f + 255 * value * (1 - (1 - f) * saturation)).toInt()
            val v = (0.5f + 255 * value).toInt()
            when (h) {
                0 -> return -0x1000000 or (v shl 16) + (t shl 8) + p
                1 -> return -0x1000000 or (q shl 16) + (v shl 8) + p
                2 -> return -0x1000000 or (p shl 16) + (v shl 8) + t
                3 -> return -0x1000000 or (p shl 16) + (q shl 8) + v
                4 -> return -0x1000000 or (t shl 16) + (p shl 8) + v
                5 -> return -0x1000000 or (v shl 16) + (p shl 8) + q
            }
            return 0
        }

        fun hsvToRgb(hue: Float, saturation: Float, value: Float): Int {
            val h = (hue * 6).toInt()
            val f = hue * 6 - h
            val p = (0.5f + 255 * value * (1 - saturation)).toInt()
            val q = (0.5f + 255 * value * (1 - f * saturation)).toInt()
            val t = (0.5f + 255 * value * (1 - (1 - f) * saturation)).toInt()
            val v = (0.5f + 255 * value).toInt()
            if (h == 0) {
                return -0x1000000 or (v shl 16) + (t shl 8) + p
            }
            if (h == 1) {
                return -0x1000000 or (q shl 16) + (v shl 8) + p
            }
            if (h == 2) {
                return -0x1000000 or (p shl 16) + (v shl 8) + t
            }
            if (h == 3) {
                return -0x1000000 or (p shl 16) + (q shl 8) + v
            }
            if (h == 4) {
                return -0x1000000 or (t shl 16) + (p shl 8) + v
            }
            if (h == 5) {
                return -0x1000000 or (v shl 16) + (p shl 8) + q
            }

            return 0
        }



        @JvmStatic
        fun drawline(
            zbuff: FloatArray, img: IntArray, color: Int, w: Int, h: Int,
            fx1: Float, fy1: Float, fz1: Float,
            fx2: Float, fy2: Float, fz2: Float
        ) {
            val dx = fx2 - fx1
            val dy = fy2 - fy1
            val dz = fz2 - fz1
            val zang = sqrt(dy * dy + dz * dz)
            val steps = sqrt(dx * dx + zang * zang)
            var t = 0f
            while (t < 1) {
                val px = fx1 + t * dx
                val py = fy1 + t * dy
                val pz = fz1 + t * dz
                val ipx = px.toInt()
                val ipy = py.toInt()
                if (ipx < 0 || ipx >= w || ipy < 0 || ipy >= h) {
                    t += 1 / steps
                    continue
                }
                val point = ipx + w * ipy
                if (zbuff[point] >= pz - 2) {
                    img[point] = color
                }
                t += 1 / steps
            }
        }

        @JvmStatic
        fun isBackface(
            fx3: Float, fy3: Float, fz3: Float,
            fx2: Float, fy2: Float, fz2: Float,
            fx1: Float, fy1: Float, fz1: Float
        ): Boolean {
            return (fx1 - fx2) * (fy3 - fy2) - (fy1 - fy2) * (fx3 - fx2) < 0
        }

        @JvmStatic
        fun triangle(
            zbuff: FloatArray, img: IntArray, color: Int, w: Int, h: Int,
            fx3: Float, fy3: Float, fz3: Float,
            fx2: Float, fy2: Float, fz2: Float,
            fx1: Float, fy1: Float, fz1: Float
        ) {
            var fx2 = fx2
            var fy2 = fy2
            var fz2 = fz2
            var fx1 = fx1
            var fy1 = fy1
            var fz1 = fz1
            if ((fx1 - fx2) * (fy3 - fy2) - (fy1 - fy2) * (fx3 - fx2) < 0) {
                val tmpx = fx1
                val tmpy = fy1
                val tmpz = fz1
                fx1 = fx2
                fy1 = fy2
                fz1 = fz2
                fx2 = tmpx
                fy2 = tmpy
                fz2 = tmpz
            }
            // using maxmima
            // string(solve([x1*dx+y1*dy+zoff=z1,x2*dx+y2*dy+zoff=z2,x3*dx+y3*dy+zoff=z3],[dx,dy,zoff]));
            val d = (fx1 * (fy3 - fy2) - fx2 * fy3 + fx3 * fy2 + ((fx2 - fx3)
                    * fy1)).toDouble()
            if (d == 0.0) {
                return
            }
            val dx = (-(fy1 * (fz3 - fz2) - fy2 * fz3 + fy3 * fz2 + ((fy2 - fy3)
                    * fz1)) / d).toFloat()
            val dy = ((fx1 * (fz3 - fz2) - fx2 * fz3 + fx3 * fz2 + ((fx2 - fx3)
                    * fz1)) / d).toFloat()
            val zoff = (fx1 * (fy3 * fz2 - fy2 * fz3) + (fy1
                    * (fx2 * fz3 - fx3 * fz2)) + (fx3 * fy2 - fx2 * fy3) * fz1 / d).toFloat()

            // 28.4 fixed-point coordinates
            val Y1 = (16.0f * fy1 + .5f).toInt()
            val Y2 = (16.0f * fy2 + .5f).toInt()
            val Y3 = (16.0f * fy3 + .5f).toInt()
            val X1 = (16.0f * fx1 + .5f).toInt()
            val X2 = (16.0f * fx2 + .5f).toInt()
            val X3 = (16.0f * fx3 + .5f).toInt()
            val DX12 = X1 - X2
            val DX23 = X2 - X3
            val DX31 = X3 - X1
            val DY12 = Y1 - Y2
            val DY23 = Y2 - Y3
            val DY31 = Y3 - Y1
            val FDX12 = DX12 shl 4
            val FDX23 = DX23 shl 4
            val FDX31 = DX31 shl 4
            val FDY12 = DY12 shl 4
            val FDY23 = DY23 shl 4
            val FDY31 = DY31 shl 4
            var minx = min(X1, X2, X3) + 0xF shr 4
            var maxx = max(X1, X2, X3) + 0xF shr 4
            var miny = min(Y1, Y2, Y3) + 0xF shr 4
            var maxy = max(Y1, Y2, Y3) + 0xF shr 4
            if (miny < 0) {
                miny = 0
            }
            if (minx < 0) {
                minx = 0
            }
            if (maxx > w) {
                maxx = w
            }
            if (maxy > h) {
                maxy = h
            }
            var off = miny * w
            var C1 = DY12 * X1 - DX12 * Y1
            var C2 = DY23 * X2 - DX23 * Y2
            var C3 = DY31 * X3 - DX31 * Y3
            if (DY12 < 0 || DY12 == 0 && DX12 > 0) {
                C1++
            }
            if (DY23 < 0 || DY23 == 0 && DX23 > 0) {
                C2++
            }
            if ((DY31 < 0 || DY31 == 0) && DX31 > 0) {
                C3++
            }
            var CY1 = C1 + DX12 * (miny shl 4) - DY12 * (minx shl 4)
            var CY2 = C2 + DX23 * (miny shl 4) - DY23 * (minx shl 4)
            var CY3 = C3 + DX31 * (miny shl 4) - DY31 * (minx shl 4)
            for (y in miny until maxy) {
                var CX1 = CY1
                var CX2 = CY2
                var CX3 = CY3
                val p = zoff + dy * y
                for (x in minx until maxx) {
                    if (CX1 > 0 && CX2 > 0 && CX3 > 0) {
                        val point = x + off
                        val zval = p + dx * x
                        if (zbuff[point] > zval) {
                            zbuff[point] = zval
                            img[point] = color
                        }
                    }
                    CX1 -= FDY12
                    CX2 -= FDY23
                    CX3 -= FDY31
                }
                CY1 += FDX12
                CY2 += FDX23
                CY3 += FDX31
                off += w
            }
        }


        fun trianglePhong(
            zbuff: FloatArray, img: IntArray,
            h3: Float, b3: Float,
            h2: Float, b2: Float,
            h1: Float, b1: Float,
            sat: Float,
            w: Int, h: Int,
            fx3: Float, fy3: Float, fz3: Float,
            fx2: Float, fy2: Float, fz2: Float,
            fx1: Float, fy1: Float, fz1: Float
        ) {
            var h2 = h2
            var b2 = b2
            var h1 = h1
            var b1 = b1
            var fx2 = fx2
            var fy2 = fy2
            var fz2 = fz2
            var fx1 = fx1
            var fy1 = fy1
            var fz1 = fz1
            if ((fx1 - fx2) * (fy3 - fy2) - (fy1 - fy2) * (fx3 - fx2) < 0) {
                val tmpx = fx1
                val tmpy = fy1
                val tmpz = fz1
                fx1 = fx2
                fy1 = fy2
                fz1 = fz2
                fx2 = tmpx
                fy2 = tmpy
                fz2 = tmpz
                val tmph = h1
                val tmpb = b1
                h1 = h2
                b1 = b2
                h2 = tmph
                b2 = tmpb
            }
            // using maxmima
            // string(solve([x1*dx+y1*dy+zoff=z1,x2*dx+y2*dy+zoff=z2,x3*dx+y3*dy+zoff=z3],[dx,dy,zoff]));
            val d = (fx1 * (fy3 - fy2) - fx2 * fy3 + fx3 * fy2 + ((fx2 - fx3)
                    * fy1))
            if (d == 0.0f) {
                return
            }
            val dx = (-(fy1 * (fz3 - fz2) - fy2 * fz3 + fy3 * fz2 + ((fy2 - fy3)
                    * fz1)) / d).toFloat()
            val dy = ((fx1 * (fz3 - fz2) - fx2 * fz3 + fx3 * fz2 + ((fx2 - fx3)
                    * fz1)) / d).toFloat()
            val zoff = ((fx1 * (fy3 * fz2 - fy2 * fz3) + (fy1
                    * (fx2 * fz3 - fx3 * fz2)) + (fx3 * fy2 - fx2 * fy3) * fz1) / d).toFloat()
            val dhx = (-(fy1 * (h3 - h2) - fy2 * h3 + fy3 * h2 + ((fy2 - fy3)
                    * h1)) / d).toFloat()
            val dhy = ((fx1 * (h3 - h2) - fx2 * h3 + fx3 * h2 + ((fx2 - fx3)
                    * h1)) / d).toFloat()
            val hoff = ((fx1 * (fy3 * h2 - fy2 * h3) + (fy1
                    * (fx2 * h3 - fx3 * h2)) + (fx3 * fy2 - fx2 * fy3) * h1) / d).toFloat()
            val dbx = (-(fy1 * (b3 - b2) - fy2 * b3 + fy3 * b2 + ((fy2 - fy3)
                    * b1)) / d).toFloat()
            val dby = ((fx1 * (b3 - b2) - fx2 * b3 + fx3 * b2 + ((fx2 - fx3)
                    * b1)) / d).toFloat()
            val boff = ((fx1 * (fy3 * b2 - fy2 * b3) + (fy1
                    * (fx2 * b3 - fx3 * b2)) + (fx3 * fy2 - fx2 * fy3) * b1) / d).toFloat()

            // 28.4 fixed-point coordinates
            val Y1 = (16.0f * fy1 + .5f).toInt()
            val Y2 = (16.0f * fy2 + .5f).toInt()
            val Y3 = (16.0f * fy3 + .5f).toInt()
            val X1 = (16.0f * fx1 + .5f).toInt()
            val X2 = (16.0f * fx2 + .5f).toInt()
            val X3 = (16.0f * fx3 + .5f).toInt()
            val DX12 = X1 - X2
            val DX23 = X2 - X3
            val DX31 = X3 - X1
            val DY12 = Y1 - Y2
            val DY23 = Y2 - Y3
            val DY31 = Y3 - Y1
            val FDX12 = DX12 shl 4
            val FDX23 = DX23 shl 4
            val FDX31 = DX31 shl 4
            val FDY12 = DY12 shl 4
            val FDY23 = DY23 shl 4
            val FDY31 = DY31 shl 4
            var minx = min(X1, X2, X3) + 0xF shr 4
            var maxx = max(X1, X2, X3) + 0xF shr 4
            var miny = min(Y1, Y2, Y3) + 0xF shr 4
            var maxy = max(Y1, Y2, Y3) + 0xF shr 4
            if (miny < 0) {
                miny = 0
            }
            if (minx < 0) {
                minx = 0
            }
            if (maxx > w) {
                maxx = w
            }
            if (maxy > h) {
                maxy = h
            }
            var off = miny * w
            var C1 = DY12 * X1 - DX12 * Y1
            var C2 = DY23 * X2 - DX23 * Y2
            var C3 = DY31 * X3 - DX31 * Y3
            if (DY12 < 0 || DY12 == 0 && DX12 > 0) {
                C1++
            }
            if (DY23 < 0 || DY23 == 0 && DX23 > 0) {
                C2++
            }
            if (DY31 < 0 || DY31 == 0 && DX31 > 0) {
                C3++
            }
            var CY1 = C1 + DX12 * (miny shl 4) - DY12 * (minx shl 4)
            var CY2 = C2 + DX23 * (miny shl 4) - DY23 * (minx shl 4)
            var CY3 = C3 + DX31 * (miny shl 4) - DY31 * (minx shl 4)
            for (y in miny until maxy) {
                var CX1 = CY1
                var CX2 = CY2
                var CX3 = CY3
                val p = zoff + dy * y
                val ph = hoff + dhy * y
                val pb = boff + dby * y
                for (x in minx until maxx) {
                    if (CX1 > 0 && CX2 > 0 && CX3 > 0) {
                        val point = x + off
                        val zval = p + dx * x
                        val hue = ph + dhx * x
                        val bright = pb + dbx * x
                        if (zbuff[point] > zval) {
                            zbuff[point] = zval
                            img[point] = hsvToRgb(hue, sat, bright)
                        }
                    }
                    CX1 -= FDY12
                    CX2 -= FDY23
                    CX3 -= FDY31
                }
                CY1 += FDX12
                CY2 += FDX23
                CY3 += FDX31
                off += w
            }
        }

    }
}