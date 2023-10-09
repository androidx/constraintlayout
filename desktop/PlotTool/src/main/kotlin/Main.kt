import androidx.constraintlayout.desktop.graph.GraphEngine
import androidx.constraintlayout.desktop.graph.Utils
import java.awt.Color
import kotlin.math.cos
import kotlin.math.sin

fun main(args: Array<String>) {
    val frame = Utils.smartFrame("Simple k Plot")
    val p = GraphEngine.setupFrameWidthControls(frame, "wave")
    val min = -Math.PI
    val max = Math.PI
    p.addFunction("sin", min, max, Color.BLUE) { x -> sin(x) }
    p.addVelocity("dsin", min, max, Color.GREEN) { x -> sin(2 * x) }
    p.addFunction2("cos", min, max, Color.RED) { a -> cos(a) }
    p.addFunction2d("circle", min, max, Color.PINK,
        { t -> Math.cos(t) },
        { t -> Math.sin(t*2) })
    val x = DoubleArray(20)
    val y = DoubleArray(x.size)
    for (i in y.indices) {
        val t = min + i * (max - min) / y.size
        x[i] = Math.sin(t)
        y[i] = 2 * Math.cos(t)
    }
    p.addPoints("points", Color.WHITE, x, y)
    frame.isVisible = true
}