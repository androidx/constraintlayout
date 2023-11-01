package com.google.constraintlayout.ext.graph3d

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.input.pointer.PointerInputChange

class Graph {
    var w = 512
    var h = 512
    val scale = 2
    var downX = 0.0f
    var downY = 0.0f
    var last = System.nanoTime()
    var count = 0;
    val showFps = true;
    var graphFunctions = FunctionSetup(w, h)
    var bitmap = ImageBitmap(w, h, ImageBitmapConfig.Argb8888)
    fun setSize(width: Int, height: Int) {
        if (w == width/scale && h == height/scale) {
            return
        }
        w = width/scale
        h = height/scale
        graphFunctions.setSize(w, h)
        bitmap = ImageBitmap(w, h, ImageBitmapConfig.Argb8888)
        println("$w x $h")
    }

    fun getImageForTime(nanoTime: Long): ImageBitmap {
        val pix = graphFunctions.getImageBuff(nanoTime)
        bitmap.asAndroidBitmap().setPixels(pix, 0, w, 0, 0, w, h)
        if (showFps) {
            count++
            val now = System.nanoTime()
            if ((now - last) > 1000000000) {
                println("rate : " + count / ((now - last).toFloat() * 1E-9f) + "f/sec")
                last = now;
                count = 0;
            }
        }
        return bitmap
    }

    fun dragStart(down: Offset) {
        downX = down.x/scale
        downY = down.y/scale
        graphFunctions.onMouseDown(downX, downY)
    }

    fun dragStopped() {
        downX = 0.0f
        downY = 0.0f
    }

    fun drag(change: PointerInputChange, drag: Offset) {
        downX += drag.x/scale
        downY += drag.y/scale
        graphFunctions.onMouseDrag(downX, downY)

    }

}