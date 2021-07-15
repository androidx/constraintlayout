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

package androidx.constraintLayout.desktop.link

import androidx.constraintLayout.desktop.scan.KeyFrameNodes
import androidx.constraintLayout.desktop.scan.WidgetFrameUtils
import androidx.constraintLayout.desktop.utils.ScenePicker
import androidx.constraintLayout.desktop.utils.ScenePicker.HitElementListener
import androidx.constraintlayout.core.parser.CLKey
import androidx.constraintlayout.core.parser.CLObject
import androidx.constraintlayout.core.parser.CLParser
import androidx.constraintlayout.core.state.WidgetFrame
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.geom.Path2D
import javax.swing.JMenuItem
import javax.swing.JPanel
import javax.swing.JPopupMenu

open class LayoutView(inspector: LayoutInspector) : JPanel(BorderLayout()) {
    protected var widgets = ArrayList<Widget>()
    var zoom = 0.85f
    var picker = ScenePicker()
    private var rootWidth: Float = 0f
    private var rootHeight: Float = 0f
    private var inspector = inspector
    protected var lastRootWidth: Float = 0f
    protected var lastRootHeight: Float = 0f
    var mReflectOrientation = false

    protected var scaleX = 0f
    protected var scaleY = 0f
    protected var offX = 0.0f
    protected var offY = 0.0f
    protected var overWidgets = ArrayList<WidgetFrame>()
    protected var selected : WidgetFrame? = null

 init {
     val mouseAdapter: MouseAdapter = object : MouseAdapter() {
         override fun mousePressed(e: MouseEvent) {
             overWidgets.clear()
             picker.setSelectListener(
                 HitElementListener { over, dist -> if (over is WidgetFrame) overWidgets.add(over) })
             picker.find(e.x, e.y)
             if (e.isPopupTrigger)
               rightMouse(e)
         }

         override fun mouseReleased(e: MouseEvent) {
             if (e.isPopupTrigger)
                 rightMouse(e)
             else {
                 for(a in overWidgets) {
                     if ("root".equals(a.name)) {
                         continue
                     }
                     if (selected != null && a == selected) {
                         continue
                     }
                     selected = a;
                     inspector.main.selectKey(a.name)

                 }
             }
         }

         override fun mouseDragged(e: MouseEvent) {

         }

         override fun mouseMoved(e: MouseEvent?) {

         }
     }
     addMouseListener(mouseAdapter)
     addMouseMotionListener(mouseAdapter)
 }
    data class Widget(val id: String, val key: CLKey) {
        var interpolated = WidgetFrame()
        var start = WidgetFrame()
        var end = WidgetFrame()
        var name = "unknown";
        var path = Path2D.Float()
        val drawFont = Font("Helvetica", Font.ITALIC, 32)
        val keyFrames = KeyFrameNodes()
        var isGuideline = false

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
                    "path" -> WidgetFrameUtils.getPath(sec, path)
                    "keyPos" -> keyFrames.setKeyFramesPos(sec)
                    "keyTypes" -> keyFrames.setKeyFramesTypes(sec)
                    "keyFrames" -> keyFrames.setKeyFramesProgress(sec)
                }
            }
        }

        fun width(): Int {
            return interpolated.width()
        }

        fun height(): Int {
            return interpolated.height()
        }

        fun draw(g: Graphics2D, drawRoot: Boolean, scenePicker: ScenePicker) {

            val END_LOOK = WidgetFrameUtils.OUTLINE or WidgetFrameUtils.DASH_OUTLINE;
            g.color = WidgetFrameUtils.theme.startColor()
            WidgetFrameUtils.render(start, g, null,END_LOOK);
            keyFrames.render(g)
            g.color = WidgetFrameUtils.theme.endColor()
            WidgetFrameUtils.render(end, g, null, END_LOOK);

            g.color = WidgetFrameUtils.theme.pathColor()
            WidgetFrameUtils.renderPath(path, g);

            g.color = WidgetFrameUtils.theme.interpolatedColor()
            var style = WidgetFrameUtils.FILL
            if (drawRoot) {
                g.color = WidgetFrameUtils.theme.rootBackgroundColor()
            }
            g.font = drawFont
            style += WidgetFrameUtils.TEXT
            interpolated.name = name
            WidgetFrameUtils.render(interpolated, g, scenePicker, style);

        }
    }

    open fun computeScale(rootWidth : Float, rootHeight: Float) {
        lastRootWidth = rootWidth
        lastRootHeight = rootHeight
        scaleX = width / rootWidth
        scaleY = height / rootHeight

        scaleX *= zoom
        scaleY *= zoom

        if (scaleX < scaleY) {
            scaleY = scaleX
        } else {
            scaleX = scaleY
        }

        offX = (width - rootWidth * scaleX) / 2
        offY = (height - rootHeight * scaleY) / 2

    }

    override fun paint(g: Graphics?) {
        super.paint(g)
        if (widgets.size == 0) {
            return
        }
        val root = widgets[0]
        rootWidth = root.width().toFloat()
        rootHeight = root.height().toFloat()
        computeScale(rootWidth, rootHeight)

        val g2 = g!!.create() as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

        g2.color = WidgetFrameUtils.theme.backgroundColor()
        g2.fillRect(0, 0, width, height)

        g2.translate(offX.toDouble(), offY.toDouble())
        g2.scale(scaleX.toDouble(), scaleY.toDouble())

        if (mReflectOrientation && !WidgetFrame.phone_orientation.isNaN()) {
            g2.rotate(-WidgetFrame.phone_orientation.toDouble(), rootWidth/2.0,rootHeight/2.0);
        }
        picker.reset()
        
        for (widget in widgets) {
            if (widget.isGuideline) {
                continue
            }
            widget.draw(g2, widget == root, picker)
        }
    }

    fun rightMouse(e : MouseEvent) {
        println("popup")
        var menu = JPopupMenu()
        menu.add("Selected")
        for (a in overWidgets)
        menu.add(JMenuItem(a.name))
        menu.show(e.component, e.x, e.y)

    }

    fun setLayoutInformation(information: String) {
        if (information.trim().isEmpty()) {
            return
        }

        try {
            val list = CLParser.parse(information)
            widgets.clear()
            var pos = Float.NaN
            for (i in 0 until list.size()) {
                val widget = list[i]

                if (widget is CLKey) {
                    val widgetId = widget.content()
                    var w = Widget(widgetId, widget)
                    widgets.add(w)
                    if (!(w.interpolated.interpolatedPos.isNaN())) {
                        pos = w.interpolated.interpolatedPos
                    }
                }
                if (!(pos.isNaN())) {
                    inspector.mTimeLinePanel?.setMotionProgress(pos)
                }
            }

            repaint()
        } catch (e : Exception) { e.printStackTrace() }
    }
}
