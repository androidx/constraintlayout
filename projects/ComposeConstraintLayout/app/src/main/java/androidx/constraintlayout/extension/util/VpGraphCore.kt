package androidx.constraintlayout.extension.util

import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import java.text.DecimalFormat
import kotlin.math.floor

class VpGraphCore(private val mUiDelegate: UiDelegate) {
    private var mPlots = ArrayList<Data>()
    private val mTime = LongArray(MAX_BUFF)
    private val mValue = FloatArray(MAX_BUFF)
    var duration = 10f // seconds
    var mMinY = 0f
    var mMaxY = 1f
    var mMinX = 0f
    var mMaxX = duration + mMinX
    var axisLeft = 100f
    var axisTop = 100f
    var axisRight = 100f
    var axisBottom = 100f
    var mAxisPaint = Paint()
    var mLinePaint = Paint()
    var mGridPaint = Paint()
    var mBounds = Rect()
    var start: Long = -1 // show the latest;
    var mGraphSelfFps = false
    var sampleDelay = 15 // ms between samples.
    private var mLiveSample = true
    private var mStartTime: Long = 0
    private var mLineX = Float.NaN
    var debug = false

    internal class Data(var mTitle: String) {
        var mX = FloatArray(MAX_BUFF)
        var mY = FloatArray(MAX_BUFF)
        var paint = Paint()
        var path = Path()
        var mLength: Int
        var lastLabelYPos = Float.NaN

        init {
            mLength = -1
            paint.style = Paint.Style.STROKE
        }

        fun plot(canvas: Canvas, graph: VpGraphCore, w: Int, h: Int) {
            path.reset()
            val scaleX = graph.getScaleX(w, h)
            val scaleY = graph.getScaleY(w, h)
            val offX = graph.getOffsetX(w, h)
            val offY = graph.getOffsetY(w, h)
            var first = true
            for (i in 0 until mLength) {
                if ((i == mLength - 1 || mX[i + 1] >= graph.mMinX) && mX[i] <= graph.mMaxX) {
                    val x = mX[i] * scaleX + offX
                    val y = mY[i] * scaleY + offY
                    if (first) {
                        path.moveTo(x, y)
                        first = false
                    } else {
                        path.lineTo(x, y)
                    }
                }
            }
            canvas.drawPath(path, paint)
        }

        fun findClosestX(x: Float): Int {
            var low = 0
            var high = mLength - 1
            var pos = -1
            while (low <= high) {
                pos = low + (high - low) / 2
                if (mX[pos] == x) return pos
                if (mX[pos] < x) low = pos + 1 else high = pos - 1
            }
            return pos
        }
    }

    fun init() {
        mUiDelegate.post { listenToChannels() }
        mAxisPaint.textSize = 32f
        mAxisPaint.strokeWidth = 3f
        mAxisPaint.color = Color.BLUE
        mGridPaint.textSize = 32f
        mGridPaint.strokeWidth = 1f
        mLinePaint.color = Color.RED
        mLinePaint.strokeWidth = 3f
        mLinePaint.textSize = 64f
    }

    var mDownX = 0f
    fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.action and MotionEvent.ACTION_MASK
        val count = event.pointerCount
        when (action) {
            MotionEvent.ACTION_MOVE -> if (count == 2) {
                var drag = event.getX(0) + event.getX(1)
                drag = (drag + mDownX) / 2
                mStartTime += (drag / getScaleX(mUiDelegate.width, mUiDelegate.height)).toLong()
                listenToChannels()
                Log.v("Main", ">>>> drag  $drag")
                mLineX = Float.NaN
            } else {
                mLineX = event.x
                Log.v("Main", ">>>> ACTION_MOVE " + event.x + " ")
                mUiDelegate.invalidate()
            }
            MotionEvent.ACTION_DOWN -> if (count == 2) {
                mLiveSample = false
                mDownX = event.getX(0) + event.getX(1)
                Log.v("Main", "$count>>>> drag on$mDownX ")
                mLineX = Float.NaN
            } else {
                mLineX = event.x
                Log.v("Main", count.toString() + ">>>> ACTION_DOWN " + event.x)
                mUiDelegate.invalidate()
            }
            MotionEvent.ACTION_UP -> {
                Log.v("Main", ">>>> ACTION_UP " + event.x)
                mLineX = Float.NaN
                for (mPlot in mPlots) {
                    mPlot.lastLabelYPos = Float.NaN
                }
                mUiDelegate.invalidate()
            }
            MotionEvent.ACTION_POINTER_DOWN -> if (count == 2) {
                mLiveSample = false
                mDownX = event.getX(0) + event.getX(1)
                Log.v("Main", "$count>>>> ACTION_POINTER_DOWN$mDownX $mLiveSample")
                mLineX = Float.NaN
            }
            MotionEvent.ACTION_POINTER_UP -> {
                Log.v("Main", ">>>> ACTION_POINTER_UP" + event.x)
                if (event.eventTime - event.downTime < 400) {
                    Log.v("Main", ">>>> false")
                    mLiveSample = true
                    listenToChannels()
                }
                Log.v("Main", ">>>> def " + event.eventTime)
            }
            else -> Log.v("Main", ">>>> def " + event.eventTime)
        }
        return true
        //return super.onTouchEvent(event);
    }

    fun addChannel(str: String) {
        for (plot in mPlots) {
            if (plot.mTitle == str) {
                return
            }
        }
        mPlots.add(Data(str))
    }

    fun setGraphFPS(on: Boolean) {
        mGraphSelfFps = on
        if (on) {
            mPlots.add(Data(FPS_STRING))
            //  mPlots.add(new Data("read"));
        } else {
            var remove: Data? = null
            for (i in mPlots.indices) {
                if (mPlots[i].mTitle === FPS_STRING) {
                    remove = mPlots[i]
                    break
                }
            }
            if (remove != null) {
                mPlots.remove(remove)
            }
        }
    }

    private fun listenToChannels() {
        //  Vp.fps("read");
        if (mLiveSample) {
            listenLive()
            return
        }
        val count = mPlots.size
        var minX = Float.MAX_VALUE
        var minY = Float.MAX_VALUE
        var maxX = -Float.MAX_VALUE
        var maxY = -Float.MAX_VALUE
        for (i in 0 until count) {
            val p = mPlots[i]
            val channel = p.mTitle
            p.mLength = Vp.getAfter(channel, mStartTime, mTime, mValue)
            if (p.mLength == -1) {
                continue
            }
            for (j in 0 until p.mLength) {
                val x = (mTime[j] - mStartTime) * 1E-9f
                p.mX[j] = x
                minX = Math.min(x, minX)
                maxX = Math.max(x, maxX)
                val y = mValue[j]
                minY = Math.min(y, minY)
                maxY = Math.max(y, maxY)
                p.mY[j] = y
            }
            Log.v("main", p.mTitle + "  " + minX + " -> " + maxX)
        }
        minX = 0f
        maxX = minX + duration
        Log.v("main", "Total  $minX -> $maxX")
        updateDataRange(minX, maxX, minY, maxY)
        mUiDelegate.invalidate()
    }

    private fun listenLive() {
        val count = mPlots.size
        mStartTime = System.nanoTime() - (duration.toDouble() * 1000000000L).toLong()
        var minX = Float.MAX_VALUE
        var minY = Float.MAX_VALUE
        var maxX = -Float.MAX_VALUE
        var maxY = -Float.MAX_VALUE
        for (i in 0 until count) {
            val p = mPlots[i]
            val channel = p.mTitle
            p.mLength = Vp.getLatest(channel, mTime, mValue)
            if (p.mLength == -1) {
                continue
            }
            for (j in 0 until p.mLength) {
                val x = (mTime[j] - mStartTime) * 1E-9f
                p.mX[j] = x
                minX = Math.min(x, minX)
                maxX = Math.max(x, maxX)
                val y = mValue[j]
                minY = Math.min(y, minY)
                maxY = Math.max(y, maxY)
                p.mY[j] = y
            }
        }
        if (minX == Float.MAX_VALUE || java.lang.Float.isNaN(minX)) {
            updateDataRange(0f, 10f, -1f, 1f)
        } else {
            updateDataRange(minX, maxX, minY, maxY)
        }
        mUiDelegate.invalidate()
        mUiDelegate.postDelayed({ listenToChannels() }, sampleDelay.toLong())
    }

    private fun updateDataRange(minX: Float, maxX: Float, minY: Float, maxY: Float) {
        var minX = minX
        minX = maxX - duration
        // fast to expand slow to contract
        val factor = 10f
        mMaxY = if (mMaxY > maxY) (mMaxY + maxY) / 2 else (mMaxY * factor + maxY) / (factor + 1)
        mMinY = if (mMinY < minY) (mMinY + minY) / 2 else (mMinY * factor + minY) / (factor + 1)
        mMinX = (mMinX + minX) / 2
        mMaxX = duration + mMinX
    }

    fun onDraw(canvas: Canvas) {
        val w = mUiDelegate.width
        val h = mUiDelegate.height
        drawAxis(canvas, w, h)
        drawGrid(canvas, w, h)
        for (p in mPlots) {
            p.plot(canvas, this, w, h)
        }
        if (mGraphSelfFps || debug) {
            Vp.fps(FPS_STRING)
        }
        drawTouchLine(canvas, w, h)
    }

    private fun drawGrid(canvas: Canvas, w: Int, h: Int) {
        val ticksX = calcTick(w, (mMaxX - mMinX).toDouble())
        val ticksY = calcTick(h, (mMaxY - mMinY).toDouble())
        val minX = (ticksX * Math.ceil((mMinX + ticksX / 100) / ticksX)).toFloat()
        val maxX = (ticksX * Math.floor(mMaxX / ticksX)).toFloat()
        val scaleX = getScaleX(w, h)
        val offX = getOffsetX(w, h)
        var x = minX
        var count = 0
        val txtPad = 4

        while (x <= maxX) {
            val xp = scaleX * x + offX
            canvas.drawLine(xp, axisTop, xp, h - axisBottom, mGridPaint)
            x += ticksX.toFloat()
            if (floor(x) == x) {
                val str = df.format(x)
                mAxisPaint.getTextBounds(str, 0, str.length, mBounds)
                canvas.drawText(
                    str,
                    xp - mBounds.width() / 2,
                    h - axisBottom + txtPad + mBounds.height(),
                    mGridPaint
                )
            }
            count++
        }
        val minY = (ticksY * Math.ceil((mMinY + ticksY / 100) / ticksY)).toFloat()
        val maxY = (ticksY * Math.floor(mMaxY / ticksY)).toFloat()
        val offY = getOffsetY(w, h)
        val scaleY = getScaleY(w, h)
        var y = minY
        while (y <= maxY) {
            val yp = scaleY * y + offY
            canvas.drawLine(axisLeft, yp, w - axisRight, yp, mGridPaint)
            if (count and 1 == 1 && y + ticksY < maxY) {
                val str = df.format(y)
                mAxisPaint.getTextBounds(str, 0, str.length, mBounds)
                canvas.drawText(str, axisLeft - mBounds.width() - txtPad * 2, yp, mGridPaint)
            }
            count++
            y += ticksY.toFloat()
        }
    }

    var df = DecimalFormat("0.0")

    init {
        init()
        mAxisPaint.color = Color.BLACK
    }

    fun drawTouchLine(canvas: Canvas, w: Int, h: Int) {
        if (java.lang.Float.isNaN(mLineX)) {
            return
        }
        if (mLineX < axisLeft) {
            mLineX = axisLeft
        } else if (mLineX > w - axisRight) {
            mLineX = w - axisRight
        }
        val dataPos = (mLineX - getOffsetX(w, h)) / getScaleX(w, h)
        val yOffset = getOffsetY(w, h)
        val yScale = getScaleY(w, h)
        val rad = 10f
        canvas.drawLine(mLineX, axisTop, mLineX, h - axisBottom, mLinePaint)
        var bottom_count = 0
        var top_count = 0
        val pad = 5
        val right = mLineX < w / 2
        val no_of_plots = mPlots.size
        for (i in 0 until no_of_plots) {
            val plot = mPlots[i]
            val index = plot.findClosestX(dataPos)
            if (index == -1) continue
            val value = plot.mY[index]
            val y = yScale * value + yOffset
            canvas.drawRoundRect(mLineX - rad, y - rad, mLineX + rad, y + rad, rad, rad, mLinePaint)
            val vString = plot.mTitle + ":" + df.format(value.toDouble())
            mLinePaint.getTextBounds(vString, 0, vString.length, mBounds)
            var yPos = (w / 2).toFloat()
            val gap = 60
            if (y > h / 2) {
                yPos = y - gap
                bottom_count++
            } else {
                top_count++
                yPos = y + gap
            }
            val xPos = if (right) mLineX + gap else mLineX - gap
            val minVGap = mBounds.height().toFloat()
            var force = 0f
            for (j in 0 until no_of_plots) {
                if (i == j) {
                    continue
                }
                val yOther = mPlots[j].lastLabelYPos
                val dist = Math.abs(plot.lastLabelYPos - yOther)
                if (dist < minVGap * 2) {
                    val dir = Math.signum(yPos - yOther)
                    force = (if (dir > 0) minVGap else -minVGap) + minVGap / (0.1f + dist)
                }
            }
            if (java.lang.Float.isNaN(plot.lastLabelYPos)) {
                plot.lastLabelYPos = yPos
            } else {
                plot.lastLabelYPos = (plot.lastLabelYPos * 49 + yPos + force) / 50
            }
            canvas.drawLine(mLineX, y, xPos, plot.lastLabelYPos, mLinePaint)
            canvas.drawText(
                vString,
                if (right) xPos else xPos - mBounds.width() - pad,
                plot.lastLabelYPos,
                mLinePaint
            )
        }
    }

    fun drawAxis(canvas: Canvas, w: Int, h: Int) {
        val txtPad = 4
        canvas.drawRGB(200, 230, 255)
        canvas.drawLine(axisLeft, axisTop, axisLeft, h - axisBottom, mAxisPaint)
        canvas.drawLine(axisLeft, h - axisBottom, w - axisRight, h - axisBottom, mAxisPaint)
        val y0 = getOffsetY(w, h)
        canvas.drawLine(axisLeft, y0, w - axisRight, y0, mAxisPaint)
        var str = df.format(mMaxY.toDouble())
        mAxisPaint.getTextBounds(str, 0, str.length, mBounds)
        canvas.drawText(str, axisLeft - mBounds.width() - txtPad, axisTop, mAxisPaint)
        str = df.format(mMinY.toDouble())
        mAxisPaint.getTextBounds(str, 0, str.length, mBounds)
        canvas.drawText(str, axisLeft - mBounds.width() - txtPad, h - axisBottom, mAxisPaint)
    }

    fun getScaleX(w: Int, h: Int): Float {
        val rangeX = mMaxX - mMinX
        val graphSpanX = w - axisLeft - axisRight
        return graphSpanX / rangeX
    }

    fun getScaleY(w: Int, h: Int): Float {
        val rangeY = mMaxY - mMinY
        val graphSpanY = h - axisTop - axisBottom
        return -graphSpanY / rangeY
    }

    fun getOffsetX(w: Int, h: Int): Float {
        return axisLeft - mMinX * getScaleX(w, h)
    }

    fun getOffsetY(w: Int, h: Int): Float {
        return h - axisBottom - mMinY * getScaleY(w, h)
    }

    companion object {
        private const val FPS_STRING = "onDraw"
        const val MAX_BUFF = 2000
        fun calcTick(scr: Int, range: Double): Double {
            val aprox_x_ticks = scr / 100
            var type = 1
            var best = Math.log10(range / aprox_x_ticks)
            var n = Math.log10(range / (aprox_x_ticks * 2))
            if (fraction(n) < fraction(best)) {
                best = n
                type = 2
            }
            n = Math.log10(range / (aprox_x_ticks * 5))
            if (fraction(n) < fraction(best)) {
                best = n
                type = 5
            }
            return type * Math.pow(10.0, Math.floor(best))
        }

        fun fraction(x: Double): Double {
            return x - Math.floor(x)
        }
    }
}