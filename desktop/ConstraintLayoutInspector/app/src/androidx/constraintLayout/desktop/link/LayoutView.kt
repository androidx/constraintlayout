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
import androidx.constraintlayout.core.parser.CLKey
import androidx.constraintlayout.core.parser.CLObject
import androidx.constraintlayout.core.parser.CLParser
import androidx.constraintlayout.core.state.WidgetFrame
import java.awt.BorderLayout
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.geom.Path2D
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.WindowConstants

class LayoutView : JPanel(BorderLayout()) {
    var widgets = ArrayList<Widget>()
    var zoom = 0.9f

    data class Widget(val id: String, val key: CLKey) {
        var interpolated = WidgetFrame()
        var start = WidgetFrame()
        var end = WidgetFrame()
        var name = "unknown";
        var path = Path2D.Float()

        init {
            name = key.content()

            val sections = key.value as CLObject
            val count = sections.size()

            for (i in 0 until count) {
                val sec = sections[i] as CLKey
                when (sec.content()) {
                    "start" -> WidgetFrameUtils.deserialize(sec, end)
                    "end" -> WidgetFrameUtils.deserialize(sec, start)
                    "interpolated" -> WidgetFrameUtils.deserialize(sec, interpolated)
                    "path" -> WidgetFrameUtils.getPath(sec, path);

                }
            }
        }

        fun width(): Int {
            return interpolated.width()
        }

        fun height(): Int {
            return interpolated.height()
        }

        fun draw(g: Graphics2D) {
            val END_LOOK = WidgetFrameUtils.OUTLINE or WidgetFrameUtils.DASH_OUTLINE;

            g.color = WidgetFrameUtils.START_COLOR
            WidgetFrameUtils.render(start, g, END_LOOK);
            g.color = WidgetFrameUtils.END_COLOR
            WidgetFrameUtils.render(end, g, END_LOOK);
            WidgetFrameUtils.renderPath(path, g);
            g.color = WidgetFrameUtils.INTERPOLATED_COLOR
            WidgetFrameUtils.render(interpolated, g, WidgetFrameUtils.FILL);
        }
    }

    override fun paint(g: Graphics?) {
        super.paint(g)
        if (widgets.size == 0) {
            return
        }
        val root = widgets[0]
        var rootWidth = root.width().toFloat()
        var rootHeight = root.height().toFloat()
        var scaleX = width / rootWidth
        var scaleY = height / rootHeight
        var offX = 0.0f
        var offY = 0.0f
        if (scaleX < scaleY) {
            scaleY = scaleX

        } else {
            scaleX = scaleY
        }
        scaleX *= zoom
        scaleY *= zoom
        offX = (width - root.width().toFloat() * scaleX) / 2
        offY = (height - root.height().toFloat() * scaleY) / 2
        val g2 = g as Graphics2D
        g2.translate(offX.toDouble(), offY.toDouble())
        g2.scale(scaleX.toDouble(), scaleY.toDouble())



        for (widget in widgets) {
            widget.draw(g2)
        }
    }


    fun setLayoutInformation(information: String) {
        if (information.trim().isEmpty()) {
            return
        }
        widgets.clear()
        val list = CLParser.parse(information)

        for (i in 0 until list.size()) {
            val widget = list[i]
            if (widget is CLKey) {
                val widgetId = widget.content()
                widgets.add(Widget(widgetId, widget))
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