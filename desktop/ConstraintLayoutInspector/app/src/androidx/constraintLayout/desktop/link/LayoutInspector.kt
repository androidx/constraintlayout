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

import androidx.constraintLayout.desktop.ui.adapters.vd.ListIcons
import androidx.constraintLayout.desktop.ui.adapters.vg.VDIcon
import androidx.constraintLayout.desktop.ui.timeline.TimeLinePanel
import androidx.constraintLayout.desktop.ui.ui.MotionEditorSelector
import androidx.constraintLayout.desktop.utils.WidgetAttributes
import androidx.constraintlayout.core.parser.CLObject
import java.awt.*
import javax.swing.*

class LayoutInspector(
    link: MotionLink,
    main: Main
) : JPanel(BorderLayout()) {
    var SHOW3D = false
    val motionLink = link
    val layoutView = LayoutView(this)
    val editorView = LayoutEditor(this)

    val main = main
    var timeLineStart = JButton("TimeLine...")
    var showWidgetAttributes = JButton("Attributes...")
    var show3d = JButton("3D...")

    var addButtonButton = JButton("Button+")
    var addTextButton = JButton("Text+")
    var editing = false
    var mTimeLinePanel: TimeLinePanel? = null
    var mSceneString: String? = null

    init {
        val northPanel = JPanel()
        val westPanel = JPanel(GridBagLayout())
        var gbc = GridBagConstraints()
        gbc.weightx = 1.0
        gbc.fill = GridBagConstraints.HORIZONTAL
        gbc.gridwidth = GridBagConstraints.REMAINDER

        val edit = JButton("Edit")
        val liveConnection = JCheckBox("Live connection")
        val rotate = JToggleButton(VDIcon(ListIcons.getStream("screen_rotation.xml")))
        rotate.preferredSize= Dimension(24,24)
        rotate.isFocusable = false
        rotate.isSelected = false
        liveConnection.isSelected = true

        westPanel.add(addButtonButton,gbc)
        westPanel.add(addTextButton,gbc)
        gbc.weighty = 1.0
        westPanel.add(Box.createGlue(),gbc)
        northPanel.add(timeLineStart)
        northPanel.add(showWidgetAttributes)
        if (SHOW3D) {
            northPanel.add(show3d)
        }


        northPanel.add(edit)
        northPanel.add(liveConnection)
        northPanel.add(rotate)
        add(northPanel, BorderLayout.NORTH)
        add(layoutView, BorderLayout.CENTER)
        add(westPanel, BorderLayout.WEST)


        liveConnection.addChangeListener {
            motionLink.setUpdateLayoutPolling(liveConnection.isSelected)
        }

        addButtonButton.addActionListener { main.addDesign("button") }
        addTextButton.addActionListener {  main.addDesign("text")}

        edit.addActionListener {
            editing = !editing
            updateEditorMode()
        }
        rotate.addActionListener{
            layoutView.mReflectOrientation = rotate.isSelected
        }
        timeLineStart.addActionListener {
            showTimeLine()
        }
        showWidgetAttributes.addActionListener{

           layoutView.displayWidgetAttributes();
        }
        show3d.addActionListener{

            layoutView.display3d();
        }
    }

    private fun updateEditorMode() {
        if (editing) {
            remove(layoutView)
            add(editorView, BorderLayout.CENTER)
        } else {
            remove(editorView)
            add(layoutView, BorderLayout.CENTER)
        }
        revalidate()
        repaint()
    }

    fun setLayoutInformation(layoutInfos: String) {
        layoutView.setLayoutInformation(layoutInfos)
        editorView.setLayoutInformation(layoutInfos)
    }

    fun setModel(jsonModel: CLObject) {
        editorView.setModel(jsonModel)
    }

    fun showTimeLine() {
        if (mTimeLinePanel == null && mSceneString != null) {
            mTimeLinePanel = TimeLinePanel.showTimeline(mSceneString)
            mTimeLinePanel?.addTimeLineListener { cmd: MotionEditorSelector.TimeLineCmd?, pos: Float -> motionLink.sendProgress(pos) }
        } else {
            mTimeLinePanel?.popUp()
        }
    }

    fun hideTimeLine() {
        mTimeLinePanel?.popDown()
    }

    fun setSceneString(str : String) {
        mSceneString = str
        mTimeLinePanel?.updateMotionScene(mSceneString);
    }

    fun resetEdit() {
        editing = false
        updateEditorMode()
    }
}
