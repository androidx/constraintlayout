package android.support.composegraph3d.lib

import android.support.composegraph3d.lib.objects.AxisBox
import android.support.composegraph3d.lib.objects.Surface3D
import android.support.composegraph3d.lib.objects.Surface3D.Function
import java.util.*
import kotlin.math.*

class FunctionSetup(var mWidth: Int, var mHeight: Int) {
    var mScene3D: Scene3D
    private var mImageBuff: IntArray
    var mGraphType = 2
    private var mLastTouchX0 = Float.NaN
    private var mLastTouchY0 = 0f
    private var mLastTrackBallX = 0f
    private var mLastTrackBallY = 0f
    var mDownScreenWidth = 0.0
    var mSurface: Surface3D? = null
    var mAxisBox: AxisBox? = null
    var range = 20f
    var minZ = -10f
    var maxZ = 10f
    var mZoomFactor = 1f
    var animated = false
    var zBuff: FloatArray = FloatArray(mWidth * mHeight)
    var nanoTime: Long = 0
    var time = 0f

    fun buildSurface() {
        mSurface = Surface3D(mFunction = object : Function {
            override fun eval(x: Float, y: Float): Float {
                val d = Math.sqrt((x * x + y * y).toDouble())
                return 0.3f * (Math.cos(d) * (y * y - x * x) / (1 + d)).toFloat()
            }
        })
        mSurface!!.setRange(-range, range, -range, range, minZ, maxZ)
        mScene3D.setObject(mSurface!!)
        mScene3D.resetCamera()
        mAxisBox = AxisBox()
        mAxisBox!!.setRange(-range, range, -range, range, minZ, maxZ)
        mScene3D.addPostObject(mAxisBox!!)
        return buildAnimatedSurface()
    }


    init {
        mImageBuff = IntArray(mWidth * mHeight)
        // zBuff = new float[w*h];
        mScene3D = Scene3D()
        buildSurface()
        mScene3D.setUpMatrix(mWidth, mHeight)
        mScene3D.setScreenDim(mWidth, mHeight, mImageBuff, 0x00AAAAAA)
    }

    fun buildAnimatedSurface() {
        mSurface = Surface3D(object : Function {
            override fun eval(x: Float, y: Float): Float {
                val d = sqrt((x * x + y * y).toDouble()).toFloat()
                val d2 = (x * x + y * y).toDouble().pow(0.125).toFloat()
                val angle = atan2(y.toDouble(), x.toDouble()).toFloat()
                val s = sin((d + angle - time * 5).toDouble()).toFloat()
                val s2 = sin(time.toDouble()).toFloat()
                val c = cos((d + angle - time * 5).toDouble()).toFloat()
                return (s2 * s2 + 0.1f) * d2 * 5 * (s + c) / (1 + d * d / 20)
            }
        })
        nanoTime = System.nanoTime()
        mScene3D.setObject(mSurface!!)
        mSurface!!.setRange(-range, range, -range, range, minZ, maxZ)
    }

    fun tick(now: Long) {
        time += (now - nanoTime) * 1E-9f
        nanoTime = now
        mSurface!!.calcSurface(false)
        mScene3D.update()
    }

    fun onKeyTyped(c: Long) {
        println(c)
        //        switch ((char) c) {
//            case  ' ':
//                buildAnimatedSurface();
//        }
    }

    fun onMouseDown(x: Float, y: Float) {
        mDownScreenWidth = mScene3D.screenWidth
        mLastTouchX0 = x
        mLastTouchY0 = y
        mScene3D.trackBallDown(mLastTouchX0, mLastTouchY0)
        mLastTrackBallX = mLastTouchX0
        mLastTrackBallY = mLastTouchY0
    }

    fun onMouseDrag(x: Float, y: Float) {
        if (java.lang.Float.isNaN(mLastTouchX0)) {
            return
        }
        val moveX = mLastTrackBallX - x
        val moveY = mLastTrackBallY - y
        if (moveX * moveX + moveY * moveY < 4000f) {
            mScene3D.trackBallMove(x, y)
        }
        mLastTrackBallX = x
        mLastTrackBallY = y
    }

    fun onMouseUP() {
        mLastTouchX0 = Float.NaN
        mLastTouchY0 = Float.NaN
    }

    fun onMouseWheel(rotation: Float, ctlDown: Boolean) {
        if (ctlDown) {
            mZoomFactor *= 1.01.pow(rotation.toDouble()).toFloat()
            mScene3D.zoom = mZoomFactor
            mScene3D.setUpMatrix(mWidth, mHeight)
            mScene3D.update()
        } else {
            range *= 1.01.pow(rotation.toDouble()).toFloat()
            mSurface!!.setArraySize(Math.min(300, (range * 5).toInt()))
            mSurface!!.setRange(-range, range, -range, range, minZ, maxZ)
            mAxisBox!!.setRange(-range, range, -range, range, minZ, maxZ)
            mScene3D.update()
        }
    }

    fun getImageBuff(time: Long): IntArray {
        tick(time)
        if (mScene3D.notSetUp()) {
            mScene3D.setUpMatrix(mWidth, mHeight)
        }
        render(2)
        return mImageBuff
    }

    fun render(type: Int) {
        Arrays.fill(mImageBuff, -0x777778)
        mScene3D.render(2)

        //    Arrays.fill(mScene3D.getZBuff(),Float.MAX_VALUE);

        // mSurface.render(this, zBuff, mImageBuff, mWidth, mHeight);
        //  raster_phong(mSurface,mScene3D,zBuff,mImageBuff,mWidth,mHeight);
    }

    fun setSize(width: Int, height: Int) {
        if (mWidth == width && mHeight == height) {
            return
        }
        println("$width $height")
        mWidth = width
        mHeight = height
        mImageBuff = IntArray(mWidth * mHeight)
        buildSurface()
        mScene3D.setUpMatrix(mWidth, mHeight)
        mScene3D.setScreenDim(mWidth, mHeight, mImageBuff, 0x00AAAAAA)
    } ///////////////////////////////////////
}