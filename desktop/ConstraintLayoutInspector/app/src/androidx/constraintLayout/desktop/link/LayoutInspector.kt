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

import androidx.constraintLayout.desktop.ui.timeline.TimeLinePanel
import androidx.constraintLayout.desktop.ui.ui.MotionEditorSelector
import androidx.constraintlayout.core.parser.CLObject
import java.awt.BorderLayout
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JPanel

class LayoutInspector(link: MotionLink) : JPanel(BorderLayout()) {
    val layoutView = LayoutView()
    val editorView = LayoutEditor()
    val motionLink = link
    var forceDimension = false
    var timeLineStart = JButton("TimeLine...")
    var editing = false

    var mTimeLinePanel: TimeLinePanel? = null
    var mSceneString: String? = null

    init {
        val northPanel = JPanel()
        val toggle = JButton("link resize")
        val edit = JButton("Edit")
        val liveConnection = JCheckBox("Live connection")
        liveConnection.isSelected = true

        northPanel.add(timeLineStart)
        northPanel.add(toggle)
        northPanel.add(edit)
        northPanel.add(liveConnection)

        add(northPanel, BorderLayout.NORTH)
        add(layoutView, BorderLayout.CENTER)

        liveConnection.addChangeListener {
            motionLink.setUpdateLayoutPolling(liveConnection.isSelected)
        }

        addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) {
                if (forceDimension) {
                    motionLink.sendLayoutDimensions(width * 3, height * 3)
                }
            }
        })

        toggle.addActionListener {
            forceDimension = !forceDimension
            if (forceDimension) {
                motionLink.sendLayoutDimensions(width * 3, height * 3)
                toggle.text = "Driving root dimension..."
            } else {
                motionLink.sendLayoutDimensions(Int.MIN_VALUE, Int.MIN_VALUE)
                toggle.text = "link resize"
            }
        }

        edit.addActionListener {
            if (!editing) {
                remove(layoutView)
                add(editorView, BorderLayout.CENTER)
            } else {
                remove(editorView)
                add(layoutView, BorderLayout.CENTER)
            }
            revalidate()
            repaint()
            editing = !editing
        }

        timeLineStart.addActionListener {
            showTimeLine()
        }

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
}
