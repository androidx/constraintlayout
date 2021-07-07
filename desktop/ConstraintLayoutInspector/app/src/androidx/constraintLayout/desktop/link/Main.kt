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

import androidx.constraintLayout.desktop.scan.CLScan
import androidx.constraintLayout.desktop.scan.SyntaxHighlight
import androidx.constraintlayout.core.motion.utils.Utils
import java.awt.BorderLayout
import java.awt.Font
import java.awt.Rectangle
import java.awt.event.*
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import java.util.prefs.Preferences
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.JTextComponent

class Main : JPanel(BorderLayout()) {
    private var UPDATE_CONTENT = 1
    private var UPDATE_PROGRESS = 2
    private var GET_CURRENT_CONTENT = 3
    private var SET_DRAW_DEBUG = 4
    private var GET_LAYOUT_LIST = 5
    private var GET_CURRENT_LAYOUT = 6

    private var connected = false
    private var drawDebug = false

    var debugName = "test2"
    lateinit var socket : Socket
    lateinit var writer : DataOutputStream
    lateinit var reader : DataInputStream

    private val listModel = DefaultListModel<String>()

    var layoutView : LayoutView? = null
    var editor = JTextPane()
    val textField = JTextField()
    val layoutListPanel = JList<String>()
    val highlight = SyntaxHighlight(editor)

    init {
        val slider = JSlider()
        val getButton = JButton("Get")
        val connectButton = JButton("Connect")
        val sendButton = JButton("Send")
        val resetProgressButton = JButton("Reset Progress")
        val toggleDrawDebug = JButton("Toggle Debug")
        val showLayout = JButton("Show Layout")

        val scrollPaneList = JScrollPane(layoutListPanel)

        val topPanel = JPanel()
        topPanel.layout = BoxLayout(topPanel, BoxLayout.LINE_AXIS)
        topPanel.add(connectButton)
        topPanel.add(textField)
        topPanel.add(toggleDrawDebug)
        topPanel.add(showLayout)
        topPanel.add(getButton)
        topPanel.add(sendButton)

        val bottomPanel = JPanel()
        bottomPanel.layout = BoxLayout(bottomPanel, BoxLayout.LINE_AXIS)
        bottomPanel.add(slider)
        bottomPanel.add(resetProgressButton)

        val scrollPaneEditor = JScrollPane(editor)

        add(topPanel, BorderLayout.NORTH)
        add(scrollPaneList, BorderLayout.WEST)
        add(scrollPaneEditor)
        add(bottomPanel, BorderLayout.SOUTH)

        connectButton.addActionListener {
            getLayoutList()
        }
        toggleDrawDebug.addActionListener {
            drawDebug = !drawDebug
            setDrawDebug(drawDebug)
        }
        showLayout.addActionListener{
            getLayoutInformation()
        }
        resetProgressButton.addActionListener {
            sendProgress(Float.NaN)
        }

        getButton.addActionListener {
            getContent()
        }

        layoutListPanel.model = listModel
        val mouseListener: MouseListener = object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                val index = layoutListPanel.locationToIndex(e.point)
                textField.text = listModel[index]
                getContent()
            }
        }
        layoutListPanel.addMouseListener(mouseListener)

        var font = Font("Courier", Font.PLAIN, 20)
        editor.font = font
        editor.document.addDocumentListener(object: DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) {
                if (highlight.update) {
                    return
                }
                sendContent()
            }

            override fun removeUpdate(e: DocumentEvent?) {
                if (highlight.update) {
                    return
                }
                sendContent()
            }

            override fun changedUpdate(e: DocumentEvent?) {
            }

        })

        slider.value = 0
        slider.minimum = 0
        slider.maximum = 100
        slider.addChangeListener {
            val progress = slider.value / 100f
            sendProgress(progress)
        }

        sendButton.addActionListener {
            sendContent()
        }
        val mouseAdapter: MouseAdapter = object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                Utils.log(e.paramString())
                if (e.isPopupTrigger) popup(e)
            }

            override fun mouseReleased(e: MouseEvent) {
                Utils.log(e.paramString())
                if (e.isPopupTrigger) popup(e)
            }

            fun popup(e: MouseEvent) {
                val menu = PopUpDemo(e, editor)
                menu.show(e.component, e.x, e.y)
            }
        }
        editor.addMouseListener(mouseAdapter)
    }


    internal class PopUpDemo(e: MouseEvent, editor: JTextPane) : JPopupMenu() {
        fun getLineOfOffset(comp: JTextComponent, offset: Int): Int {
            val doc = comp.document
            return if (offset < 0) {
                -1
            } else if (offset > doc.length) {
                -1
            } else {
                val map = doc.defaultRootElement
                map.getElementIndex(offset)
            }
        }

        init {
            var anItem: JMenuItem
            var subMenu: JMenu
            val offset = editor.viewToModel2D(e.point)
            val line_number = getLineOfOffset(editor, offset)
            editor.caretPosition = offset
            anItem = JMenuItem("edit")
            anItem.font = anItem.font.deriveFont(Font.BOLD)
            add(anItem)
            anItem = JMenuItem("line number:$line_number")
            add(anItem)
            add(Separator())
            val scan = CLScan.getTreeAtLocation(editor.text, offset)
            for (i in scan.indices) {
                val pad = String(CharArray(i)).replace(0.toChar(), ' ')
                val name = scan[i].content()
                var toMake = CLScan.creationMap[name]
                if (toMake == null && i > 0) {
                    val prevName = scan[i - 1].content()
                    toMake = CLScan.creationMap["$prevName*"]
                }
                if (toMake == null && i > 1) {
                    val prevName = scan[i - 2].content()
                    toMake = CLScan.creationMap["$prevName**"]
                }
                if (toMake != null) {
                    subMenu = JMenu(pad + name)
                    add(subMenu)
                    for (j in toMake.indices) {
                        var s = toMake[j]
                        if (s == "*") {
                            s = "new"
                            anItem = JMenuItem(s)
                            anItem.font = anItem.font.deriveFont(Font.ITALIC)
                        } else {
                            anItem = JMenuItem(s)
                        }
                        subMenu.add(anItem)
                    }
                    continue
                }
                anItem = JMenuItem(pad + name)
                anItem.isEnabled = false
                add(anItem)
            }
        }
    }


    private fun getLayoutList() {
        try {
            prepareConnection()
            writer.writeInt(GET_LAYOUT_LIST)
            writer.writeUTF(debugName)
            var numLayouts = reader.readInt()
            println("found $numLayouts layouts")
            listModel.clear()
            for (i in 0 until numLayouts) {
                listModel.add(i, reader.readUTF())
            }
            if (!listModel.isEmpty) {
                textField.text = listModel[0]
            }
        } catch (e : Exception) {
            reconnect()
        }
    }

    fun sendProgress(value : Float) {
        try {
            prepareConnection()
            writer.writeInt(UPDATE_PROGRESS)
            writer.writeUTF(debugName)
            writer.writeFloat(value)
            updateLayoutInformation()
        } catch (e : Exception) {
            reconnect()
        }
    }

    fun getContent() {
        try {
            prepareConnection()
            writer.writeInt(GET_CURRENT_CONTENT)
            writer.writeUTF(debugName)
            editor.text = reader.readUTF()
        } catch (e : java.lang.Exception) {
            reconnect()
        }
    }

    fun getLayoutInformation() {
        if (layoutView == null) {
            layoutView = LayoutView()
            val f = JFrame("Layout visualisation")
            f.contentPane = layoutView
            f.setBounds(500, 100, 400, 800)
            f.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
            f.isVisible = true
        }
        updateLayoutInformation()
    }

    fun updateLayoutInformation() {
        try {
            prepareConnection()
            writer.writeInt(GET_CURRENT_LAYOUT)
            writer.writeUTF(debugName)
            var layoutInfos = reader.readUTF()
            layoutView?.setLayoutInformation(layoutInfos)
        } catch (e : java.lang.Exception) {
            reconnect()
        }
    }

    fun sendContent() {
        try {
            prepareConnection()
            writer.writeInt(UPDATE_CONTENT)
            writer.writeUTF(debugName)
            var content = editor.text
            writer.writeUTF(content)
        } catch (e : Exception) {
            reconnect()
        }
    }

    fun setDrawDebug(active: Boolean) {
        try {
            prepareConnection()
            writer.writeInt(SET_DRAW_DEBUG)
            writer.writeUTF(debugName)
            writer.writeBoolean(active)
        } catch (e : Exception) {
            reconnect()
        }
    }

    fun prepareConnection() {
        if (!connected || !socket.isConnected) {
            reconnect()
        }
        debugName = textField.text
    }

    fun reconnect() {
        if (connected) {
            socket.close()
        }
        try {
            socket = Socket("localhost", 9999)
            writer = DataOutputStream(socket.getOutputStream())
            reader = DataInputStream(socket.getInputStream())
            connected = true
        } catch (e : Exception) {
            println("Could not connect to application")
        }
    }

    companion object {

        @JvmStatic
        fun main(vararg args: String) {
            val f = JFrame("ConstraintLayout Live Editor")
            val p: Main =
                Main()
            f.contentPane = p

            val pref = Preferences.userNodeForPackage(Main::class.java)
            val pos = Rectangle(100, 100, 1200, 800)
            if (pref != null && pref.getInt("base_x", -1) != -1) {
                pos.x = pref.getInt("base_x", pos.x)
                pos.y = pref.getInt("base_y", pos.y)
                pos.width = pref.getInt("base_width", pos.width)
                pos.height = pref.getInt("base_height", pos.height)
            }
            f.setBounds(pos)
            f.addComponentListener(object : ComponentAdapter() {
                override fun componentMoved(evt: ComponentEvent) {
                    f.getBounds(pos)
                    pref!!.putInt("base_x", pos.x)
                    pref!!.putInt("base_y", pos.y)
                    pref!!.putInt("base_width", pos.width)
                    pref!!.putInt("base_height", pos.height)
                }
            })
            
            f.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
            f.isVisible = true

        }

    }
}