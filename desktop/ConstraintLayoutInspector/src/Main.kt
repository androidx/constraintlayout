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
import java.awt.BorderLayout
import java.awt.Font
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class Main : JPanel(BorderLayout()) {

    private var UPDATE_CONTENT = 1
    private var UPDATE_PROGRESS = 2
    private var GET_CURRENT_CONTENT = 3
    private var SET_DRAW_DEBUG = 4

    private var connected = false
    private var drawDebug = false

    var debugName = "test2"
    lateinit var socket : Socket
    lateinit var writer : DataOutputStream
    lateinit var reader : DataInputStream
    var editor = JEditorPane()
    val textField = JTextField()

    init {
        val slider = JSlider()
        val getButton = JButton("Get")
        val sendButton = JButton("Send")
        val resetProgressButton = JButton("Reset Progress")
        val toggleDrawDebug = JButton("Toggle Debug")

        val topPanel = JPanel()
        topPanel.layout = BoxLayout(topPanel, BoxLayout.LINE_AXIS)
        topPanel.add(textField)
        topPanel.add(toggleDrawDebug)
        topPanel.add(getButton)
        topPanel.add(sendButton)

        val bottomPanel = JPanel()
        bottomPanel.layout = BoxLayout(bottomPanel, BoxLayout.LINE_AXIS)
        bottomPanel.add(slider)
        bottomPanel.add(resetProgressButton)

        add(topPanel, BorderLayout.NORTH)
        add(editor)
        add(bottomPanel, BorderLayout.SOUTH)

        toggleDrawDebug.addActionListener {
            drawDebug = !drawDebug
            setDrawDebug(drawDebug)
        }
        resetProgressButton.addActionListener {
            sendProgress(Float.NaN)
        }

        getButton.addActionListener {
            getContent()
        }


        var font = Font("Courier", Font.PLAIN, 20)
        editor.font = font
        editor.document.addDocumentListener(object: DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) {
                println("UPDATE!!")
                sendContent()
            }

            override fun removeUpdate(e: DocumentEvent?) {
                println("UPDATE!!")
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
    }

    fun sendProgress(value : Float) {
        try {
            prepareConnection()
            writer.writeInt(UPDATE_PROGRESS)
            writer.writeUTF(debugName)
            writer.writeFloat(value)
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
            val f = JFrame("Live Editor")
            val p: Main = Main()
            f.contentPane = p
            f.setBounds(100, 100, 1500, 800)
            f.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
            f.isVisible = true
        }

    }
}