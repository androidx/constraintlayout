package androidx.constraintlayout.extension.util

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.node.Ref
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


fun vpSend(channel: String, value: Float) {
    Vp.send(channel, value)
}

fun vpSend(channel: String, time: Long, value: Float) {
    Vp.send(channel, time, value)
}

fun vpFps(channel: String) {
    Vp.fps(channel);
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun VpGraph(
    modifier: Modifier = Modifier,
    vararg channels: String,
) {
    val width = remember {
        Ref<Int>().apply { 0 }
    }
    val height = remember {
        Ref<Int>().apply { 0 }
    }

    val invalidator = remember { mutableStateOf(Unit, neverEqualPolicy()) }

    val scope = rememberCoroutineScope()

    val uiDelegate = remember {
        object : UiDelegate {
            override fun post(runnable: Runnable?): Boolean {
                scope.launch {
                    runnable?.run()
                }
                return true
            }

            override fun postDelayed(runnable: Runnable?, delayMillis: Long): Boolean {
                scope.launch {
                    delay(delayMillis)
                    runnable?.run()
                }
                return true
            }

            override fun invalidate() {
                invalidator.value = Unit
            }

            override fun getWidth(): Int {
                return width.value ?: 0
            }

            override fun getHeight(): Int {
                return height.value ?: 0
            }
        }
    }

    val graphCore = remember {
        VpGraphCore(uiDelegate)
    }

    LaunchedEffect(channels) {
        channels.forEach {
            graphCore.addChannel(it)
        }
    }

    Canvas(
        modifier = modifier
            .clipToBounds()
            .onPlaced {
                width.value = it.size.width
                height.value = it.size.height
            }
            .motionEventSpy {
                graphCore.onTouchEvent(it)
            },
        onDraw = {
            invalidator.value
            graphCore.onDraw(this.drawContext.canvas.nativeCanvas)
        }
    )
}
