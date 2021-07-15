package androidx.constraintlayout.validation

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout

class MyLinearLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    override fun requestLayout() {
        println("### @@@ REQUEST LAYOUT for $this")
        super.requestLayout()
    }

    override fun forceLayout() {
        println("### @@@ FORCE LAYOUT for $this")
        super.forceLayout()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        println("### @@@ ON LAYOUT $l $t $r $b for $this")
        super.onLayout(changed, l, t, r, b)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        println("### @@@ ON MEASURE for $this")
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}