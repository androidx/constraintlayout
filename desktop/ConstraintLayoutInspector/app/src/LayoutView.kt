/*
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

import androidx.constraintlayout.core.parser.*
import java.awt.*
import javax.swing.JPanel

class LayoutView : JPanel(BorderLayout()) {
    var widgets = ArrayList<Widget>()

    data class Widget(val id : String,
                      val l: Int, val t: Int,
                      val r: Int, val b: Int) {
        fun width() : Int { return r - l }
        fun height() : Int { return b - t }
        fun draw(g: Graphics2D) {
            g.drawLine(l , t , r , t)
            g.drawLine(r, t, r, b)
            g.drawLine(r, b, l, b)
            g.drawLine(l, b, l, t)
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
        widgets.clear()
        val info = CLParser.parse(information)
        val list = info[0]
        if (list is CLArray) {
            for (i in 0 until list.size()) {
                val widgetInfo = list[i]
                if (widgetInfo is CLObject) {
                    val widget = widgetInfo[0]
                    if (widget is CLKey) {
                        val widgetId = widget.content()
                        val bounds = widget.value
                        if (bounds is CLArray) {
                            val left = (bounds[0] as CLNumber).float.toInt()
                            val top = (bounds[1] as CLNumber).float.toInt()
                            val right = (bounds[2] as CLNumber).float.toInt()
                            val bottom = (bounds[3] as CLNumber).float.toInt()
                            widgets.add(Widget(widgetId,
                                        left, top, right, bottom))
                        }
                    }
                }
            }
        }
        repaint()
    }
}