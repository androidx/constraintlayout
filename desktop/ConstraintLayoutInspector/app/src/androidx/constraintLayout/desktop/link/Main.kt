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

import androidx.constraintLayout.desktop.link.LayoutView.Companion.showLayoutView
import androidx.constraintLayout.desktop.scan.CLTreeNode
import androidx.constraintLayout.desktop.scan.SyntaxHighlight
import androidx.constraintlayout.core.parser.CLParsingException
import com.formdev.flatlaf.FlatIntelliJLaf
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.util.prefs.BackingStoreException
import java.util.prefs.Preferences
import javax.swing.*
import javax.swing.event.ChangeEvent
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.event.TreeSelectionListener
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

class Main internal constructor() : JPanel(BorderLayout()) {
    var motionLink = MotionLink()
    var mMainText = JTextPane()
    var mMessages = JLabel()
    var layoutListTree = JTree()
    var scrollPaneList = JScrollPane(layoutListTree)
    var highlight = SyntaxHighlight(mMainText)
    var mMainTextScrollPane = JScrollPane(mMainText)
    var mSlider = JSlider()
    var drawDebug = false
    var layoutView: LayoutView? = null

    init {
        val getButton = JButton("Get")
        val connectButton = JButton("Connect")
        val sendButton = JButton("Send")
        val resetProgressButton = JButton("Reset Progress")
        val toggleDrawDebug = JButton("Toggle Debug")
        val showLayout = JButton("Show Layout")
        mMessages.horizontalAlignment = SwingConstants.RIGHT
        val font = Font("Courier", Font.PLAIN, 20)
        mMainText.font = font
        scrollPaneList.preferredSize = Dimension(200, 100)
        val northPanel = JPanel()
        northPanel.add(connectButton)
        northPanel.add(toggleDrawDebug)
        northPanel.add(showLayout)
        northPanel.add(getButton)
        northPanel.add(sendButton)
        val southPanel = JPanel(BorderLayout())
        southPanel.add(mSlider, BorderLayout.CENTER)
        southPanel.add(resetProgressButton, BorderLayout.EAST)
        southPanel.add(mMessages, BorderLayout.SOUTH)
        add(northPanel, BorderLayout.NORTH)
        add(scrollPaneList, BorderLayout.WEST)
        add(mMainTextScrollPane, BorderLayout.CENTER)
        add(southPanel, BorderLayout.SOUTH)
        mSlider.value = 0
        motionLink.addListener { event: MotionLink.Event, link: MotionLink ->
            fromLink(
                event,
                link
            )
        }
        layoutListTree.selectionModel.addTreeSelectionListener(TreeSelectionListener { e ->
            val path = e.path
            println(path)
            if (path.pathCount > 2) {
                val selected = path.lastPathComponent as CLTreeNode
                println("selected " + selected.mKeyStart + "," + selected.mKeyEnd)
                mMainText.select(selected.mKeyStart, selected.mKeyEnd + 1)
                mMainText.requestFocus()
                return@TreeSelectionListener
            }
            val root =
                path.getPathComponent(0) as DefaultMutableTreeNode
            val selected =
                path.getPathComponent(1) as DefaultMutableTreeNode
            val index = root.getIndex(selected)
            motionLink.selectMotionScene(index)
            motionLink.getContent()
        })
        motionLink.getLayoutList()
        mMessages.text = "ok"
        connectButton.addActionListener { e: ActionEvent? -> motionLink.getLayoutList() }
        toggleDrawDebug.addActionListener { e: ActionEvent? ->
            motionLink.setDrawDebug(
                !drawDebug.also { drawDebug = it }
            )
        }
        showLayout.addActionListener { e: ActionEvent? -> motionLink.getLayoutList() }
        resetProgressButton.addActionListener { e: ActionEvent? ->
            motionLink.sendProgress(
                Float.NaN
            )
        }
        getButton.addActionListener { e: ActionEvent? -> motionLink.getContent() }
        mSlider.addChangeListener { e: ChangeEvent? ->
            motionLink.sendProgress(
                mSlider.value / 100f
            )
        }
        mMainText.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent) {
                if (highlight.update) {
                    return
                }
                motionLink.sendContent(mMainText.text)
            }

            override fun removeUpdate(e: DocumentEvent) {
                if (highlight.update) {
                    return
                }
                motionLink.sendContent(mMainText.text)
            }

            override fun changedUpdate(e: DocumentEvent) {
                if (highlight.update) {
                    return
                }
                motionLink.sendContent(mMainText.text)
            }
        })
    }


    private fun fromLink(event: MotionLink.Event, link: MotionLink) {
        when (event) {
            MotionLink.Event.ERROR -> {
                mMessages.text = link.errorMessage
                mMessages.foreground = Color.RED.darker()
                link.errorMessage = ""
            }
            MotionLink.Event.STATUS -> {
                mMessages.text = link.statusMessage
                mMessages.foreground = Color.BLACK
                link.errorMessage = ""
            }
            MotionLink.Event.LAYOUT_UPDATE -> {
                if (layoutView == null) {
                    layoutView = showLayoutView(link)
                    link.setUpdateLayoutPolling(true)
                }
                layoutView!!.setLayoutInformation(link.layoutInfos)
            }
            MotionLink.Event.LAYOUT_LIST_UPDATE -> {
                val root = DefaultMutableTreeNode("root")
                val model = DefaultTreeModel(root)
                var i = 0
                while (i < link.layoutNames.size) {
                    root.add(DefaultMutableTreeNode(link.layoutNames[i]))
                    i++
                }
                layoutListTree.isRootVisible = false
                layoutListTree.model = model
            }
            MotionLink.Event.MOTION_SCENE_UPDATE -> {
                mMainText.text = link.motionSceneText
                updateTree()
            }
        }
    }

    private fun updateTree() {
        val model = layoutListTree.model as DefaultTreeModel
        val root = model.root as DefaultMutableTreeNode
        val n = root.childCount
        for (i in 0 until n) {
            val child =
                root.getChildAt(i) as DefaultMutableTreeNode
            child.removeAllChildren()
            if (motionLink.mSelectedIndex == i) {
                println(i)
                try {
                    CLTreeNode.parse(motionLink.motionSceneText, child)
                } catch (e: CLParsingException) {
                    mMessages.text = e.message
                    mMessages.foreground = Color.RED.darker()
                }
            }
        }
        model.reload()
        layoutListTree.expandRow(motionLink.mSelectedIndex)
    }

    companion object {
        @JvmStatic
        fun main(str: Array<String>) {
            FlatIntelliJLaf.install()
            val frame = JFrame("ConstraintLayout Live Editor")
            frame.contentPane = Main()
            val pos = Rectangle(100, 100, 1200, 800)
            val pref =
                Preferences.userNodeForPackage(Main::class.java)
            if (pref != null && pref.getInt("base_x", -1) != -1) {
                pos.x = pref.getInt("base_x", pos.x)
                pos.y = pref.getInt("base_y", pos.y)
                pos.width = pref.getInt("base_width", pos.width)
                pos.height = pref.getInt("base_height", pos.height)
                println(pos)
            }
            frame.bounds = pos
            frame.addComponentListener(object : ComponentAdapter() {
                override fun componentResized(e: ComponentEvent) {
                    resize()
                }

                override fun componentMoved(evt: ComponentEvent) {
                    resize()
                }

                fun resize() {
                    val posNew = frame.bounds
                    pref!!.putInt("base_x", posNew.x)
                    pref.putInt("base_y", posNew.y)
                    pref.putInt("base_width", posNew.width)
                    pref.putInt("base_height", posNew.height)
                    try {
                        pref.flush()
                    } catch (e: BackingStoreException) {
                        e.printStackTrace()
                    }
                }
            })
            frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
            frame.isVisible = true
        }
    }

}