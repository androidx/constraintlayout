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

import androidx.constraintLayout.desktop.constraintRendering.layout3d.CheckLayout3d
import androidx.constraintLayout.desktop.scan.KeyFrameNodes
import androidx.constraintLayout.desktop.scan.LayoutConstraints
import androidx.constraintLayout.desktop.scan.WidgetFrameUtils
import androidx.constraintLayout.desktop.utils.ScenePicker
import androidx.constraintLayout.desktop.utils.ScenePicker.HitElementListener
import androidx.constraintLayout.desktop.utils.WidgetAttributes
import androidx.constraintlayout.core.parser.CLKey
import androidx.constraintlayout.core.parser.CLObject
import androidx.constraintlayout.core.parser.CLParser
import androidx.constraintlayout.core.state.WidgetFrame
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.geom.AffineTransform
import java.awt.geom.Path2D
import java.util.*
import javax.swing.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


open class LayoutView(inspector: LayoutInspector) : JPanel(BorderLayout()) {
    private var attDisplay: WidgetAttributes? = null
    private var display3d: CheckLayout3d? = null
    protected var widgets = ArrayList<Widget>()
    var startLayoutMap = HashMap<String, LayoutConstraints>()
    var endLayoutMap = HashMap<String, LayoutConstraints>()
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
    protected var overWidgets = HashSet<String>()
    protected var selectWidgets = HashSet<String>()
    protected var primarySelected: String? = null

    init {
        val mouseAdapter: MouseAdapter = object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                selectWidgets.clear()
                picker.setSelectListener(
                    HitElementListener { over, dist -> if (over is WidgetFrame) selectWidgets.add(over.name) })
                picker.find(e.x, e.y)
                for (s in selectWidgets) {
                    if (s == "root") {
                        continue
                    }
                    if (primarySelected == null) {
                        primarySelected = s
                        break
                    }
                    if (primarySelected != s) {
                        primarySelected = s
                        break;
                    }
                }
                if (e.isPopupTrigger)
                    rightMouse(e)
                else {
                    val s = primarySelected
                    if (s != null) {
                        inspector.main.selectKey(s)
                    }
                }
                repaint();

        }

        override fun mouseReleased(e: MouseEvent) {

            inspector.main.clearSelectedKey();
            repaint();
            if (e.isPopupTrigger) {
                rightMouse(e)
            }
        }

        override fun mouseDragged(e: MouseEvent) {
        }

        override fun mouseMoved(e: MouseEvent?) {
            if (e == null) {
                return
            }

            overWidgets.clear()
            picker.setSelectListener(
                HitElementListener { over, dist -> if (over is WidgetFrame) overWidgets.add(over.name) })
            picker.find(e.x, e.y)

            repaint()
        }
    }
    addMouseListener(mouseAdapter)
    addMouseMotionListener(mouseAdapter)
}

data class Widget(val id: String, val key: CLKey) {
    var interpolated = WidgetFrame()
    var start = WidgetFrame()
    var end = WidgetFrame()
    var endLayout = LayoutConstraints()
    var startLayout = LayoutConstraints()
    var name = "unknown";
    var path = Path2D.Float()
    val drawFont = Font("Helvetica", Font.ITALIC, 32)
    val keyFrames = KeyFrameNodes()
    var isGuideline = false

    init {
        name = key.content()
        endLayout.name = name
        startLayout.name = name
        val sections = key.value as CLObject
        val count = sections.size()
        for (i in 0 until count) {
            val sec = sections[i] as CLKey
            when (sec.content()) {
                "start" -> WidgetFrameUtils.deserialize(sec, end, endLayout)
                "end" -> WidgetFrameUtils.deserialize(sec, start, startLayout)
                "interpolated" -> WidgetFrameUtils.deserialize(sec, interpolated, null)
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

    fun draw(
        g: Graphics2D, drawRoot: Boolean, scenePicker: ScenePicker,
        over: HashSet<String>,
        selected: String?
    ) {
        val renderScale = WidgetFrameUtils.getTouchScale(g);
        val END_LOOK = WidgetFrameUtils.OUTLINE or WidgetFrameUtils.DASH_OUTLINE;
        g.color = WidgetFrameUtils.theme.startColor()
        WidgetFrameUtils.render(start, g, null, END_LOOK, null, startLayout);
        keyFrames.render(g)
        g.color = WidgetFrameUtils.theme.endColor()
        WidgetFrameUtils.render(end, g, null, END_LOOK, null, endLayout);

        g.color = WidgetFrameUtils.theme.pathColor()
        WidgetFrameUtils.renderPath(path, g);

        g.color = WidgetFrameUtils.theme.interpolatedColor()
        var style = WidgetFrameUtils.FILL
        interpolated.name = name

        if (drawRoot) {
            g.color = WidgetFrameUtils.theme.rootBackgroundColor()
        } else {
            if (over.contains(interpolated.name)) {

                g.color = WidgetFrameUtils.theme.interpolatedHoverColor()
            }
            if (selected == interpolated.name) {
                g.color = WidgetFrameUtils.theme.interpolatedSelectedColor()
            }
        }
        g.font = drawFont
        style += WidgetFrameUtils.TEXT
        WidgetFrameUtils.render(interpolated, g, scenePicker, style, renderScale, if (drawRoot) endLayout else null);
        if (drawRoot) {
            startLayout.bounds = endLayout.bounds
        }
    }

    fun drawConnections(
        g: Graphics2D,
        picker: ScenePicker,
        startLayoutMap: HashMap<String, LayoutConstraints>,
        endLayoutMap: HashMap<String, LayoutConstraints>,
        root: Widget
    ) {
        startLayout.render(g, startLayoutMap, picker, root.endLayout);
        endLayout.render(g, endLayoutMap, picker, root.endLayout);
    }
}

    fun isRet(graphics: Graphics2D) : Boolean {
        val g = graphics
        val retinaTest = (g.fontRenderContext.transform
                == AffineTransform.getScaleInstance(2.0, 2.0))
        return retinaTest
    }
open fun computeScale(rootWidth: Float, rootHeight: Float) {
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
    g2.setRenderingHint(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON
    );

    g2.color = WidgetFrameUtils.theme.backgroundColor()
    g2.fillRect(0, 0, width, height)

    g2.translate(offX.toDouble(), offY.toDouble())
    g2.scale(scaleX.toDouble(), scaleY.toDouble())

    if (mReflectOrientation && !WidgetFrame.phone_orientation.isNaN()) {
        g2.rotate(-WidgetFrame.phone_orientation.toDouble(), rootWidth / 2.0, rootHeight / 2.0);
    }
    picker.reset()
    endLayoutMap.clear()
    startLayoutMap.clear()
    for (widget in widgets) {
        if (widget.isGuideline) {
            continue
        }

        widget.draw(g2, widget == root, picker, overWidgets, primarySelected)
        endLayoutMap.put(widget.endLayout.name, widget.endLayout)
        startLayoutMap.put(widget.startLayout.name, widget.startLayout)
    }
    for (widget in widgets) {
        if (widget.isGuideline || widget == root) {
            continue
        }
        widget.drawConnections(g2, picker, startLayoutMap, endLayoutMap, root)
    }

}

fun rightMouse(e: MouseEvent) {
    println("popup")
    var menu = JPopupMenu()
    menu.add("Selected")
    for (wId in overWidgets) {
        if (wId == "root") {
            continue
        }
        var sub = JMenu(wId)
        menu.add(sub)
        sub.add(JMenuItem(object : AbstractAction("centerVertically"){
            override fun actionPerformed(e: ActionEvent) {
                inspector.main.addConstraint(wId, "centerVertically: 'parent'")
            }
        }))
        sub.add(JMenuItem(object : AbstractAction("centerHorizontally"){
            override fun actionPerformed(e: ActionEvent) {
                inspector.main.addConstraint(wId, "centerHorizontally: 'parent'")
            }
        }))


    }
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
        if (primarySelected != null && attDisplay != null) {
            for (widget in widgets) {
                if (widget.name == primarySelected) {
                   attDisplay?.setWidgetFrame(widget.interpolated);
                }
            }
        }
        display3d?.update(widgets);
        repaint()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

    fun displayWidgetAttributes() {
        for (widget in widgets) {
            if (widget.name == primarySelected) {
               attDisplay  =  WidgetAttributes.display(widget.interpolated);
            }
        }

    }
    fun display3d() {
       display3d =  CheckLayout3d.create3d(widgets)
    }
}
