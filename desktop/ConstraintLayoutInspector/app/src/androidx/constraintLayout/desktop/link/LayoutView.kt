package androidx.constraintLayout.desktop.link/*
 * Copyright 2021 The Android Open Source Project
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

import androidx.constraintLayout.desktop.scan.WidgetFrameUtils
import androidx.constraintlayout.core.parser.*
import androidx.constraintlayout.core.state.WidgetFrame
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.lang.StringBuilder
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.WindowConstants

class LayoutView : JPanel(BorderLayout()) {
    var widgets = ArrayList<Widget>()

    data class Widget(val id : String, val key: CLKey) {
      var frame = WidgetFrame()

      init {
          WidgetFrameUtils.deserialize(key, frame)
      }

      fun width(): Int { return frame.width()  }
      fun height(): Int { return frame.height()  }

      fun draw(g: Graphics2D) {
          WidgetFrameUtils.render(frame, g)
      }
    }

    override fun paint(g: Graphics?) {
        super.paint(g)
        if (widgets.size == 0) {
            return
        }
        val root = widgets[0]
        var scaleX = width / root.width().toFloat()
        var scaleY = height / root.height().toFloat()
        if (scaleX < scaleY) {
            scaleY = scaleX
        } else {
            scaleX = scaleY
        }
        val g2 = g as Graphics2D
        g2.scale(scaleX.toDouble(), scaleY.toDouble())
        g2.color = Color.BLUE
        for (widget in widgets) {
            widget.draw(g2)
        }
    }


    fun setLayoutInformation(information: String) {
        // { [{ text0: [ 157, 591, 272, 648 ] } ...
        println(information)
        widgets.clear()
        val list = CLParser.parse(information)

            for (i in 0 until list.size()) {
                val widget = list[i]
                    if (widget is CLKey) {
                        val widgetId = widget.content()
                        widgets.add( Widget(widgetId, widget) )
                    }
        }
        repaint()
    }

    companion object {
        fun showLayoutView(): LayoutView? {
            val frame = JFrame("Layout visualisation")
            val layoutView = LayoutView()
            frame.contentPane = layoutView
            frame.setBounds(100, 100, 1200, 800)
            frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
            frame.isVisible = true
            return layoutView
        }
    }
}