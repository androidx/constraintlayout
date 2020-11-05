package androidx.constraintlayout.validation

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class TestView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var paint = Paint()
        canvas.drawColor(Color.BLUE)
        paint.setColor(Color.RED)
        paint.strokeWidth = 4f
        canvas.drawLine(0f, 0f, width.toFloat(), height.toFloat(), paint)
        canvas.drawLine(0f, height.toFloat(), width.toFloat(), 0f, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)
        var widthMode = MeasureSpec.getMode(widthMeasureSpec)
        var widthSize = MeasureSpec.getSize(widthMeasureSpec)
        var width = 0
        var height = 0
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = 200.dp
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(widthSize, 200.dp)
        } else {
            width = widthSize
        }
        if (heightMode == MeasureSpec.UNSPECIFIED) {
            height = 100.dp
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(heightSize, 100.dp)
        } else {
            height = heightSize
        }
        setMeasuredDimension(width, height)
    }

    val Int.px: Int
        get() = (this / Resources.getSystem().displayMetrics.density).toInt()
    val Int.dp: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()
}