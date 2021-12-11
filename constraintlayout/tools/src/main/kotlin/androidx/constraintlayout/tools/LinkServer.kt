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
package androidx.constraintlayout.tools

import androidx.constraintlayout.core.state.Registry
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

class LinkServer {
    private var server: ServerSocket
    private var port = 9999

    private var UPDATE_CONTENT = 1
    private var UPDATE_PROGRESS = 2
    private var GET_CURRENT_CONTENT = 3
    private var SET_DRAW_DEBUG = 4
    private var GET_LAYOUT_LIST = 5
    private var GET_CURRENT_LAYOUT = 6
    private var UPDATE_LAYOUT_DIMENSIONS = 7

    enum class MotionLayoutDebugFlags {
        NONE,
        SHOW_ALL,
        UNKNOWN
    }

    init {
        server = ServerSocket(port)
    }

    fun start() {
        println("Starting server")
        thread {
            while (true) {
                val client = try {
                    server.accept()
                } catch (e: Exception) {
                    return@thread
                } finally {
                }
                println("Client connected")
                thread { handleRequest(client) }
            }
        }
    }

    fun stop() {
        server.close()
    }

    private fun handleRequest(socket: Socket) {
        var running = true
        val reader = DataInputStream(socket.getInputStream())
        val writer = DataOutputStream(socket.getOutputStream())
        val registry = Registry.getInstance()
        while (running) {
            try {
                val type = reader.readInt()
                val name = reader.readUTF()
                when (type) {
                    UPDATE_CONTENT -> {
                        val content = reader.readUTF()
                        registry.updateContent(name, content)
                    }
                    UPDATE_PROGRESS -> {
                        val progress = reader.readFloat()
                        registry.updateProgress(name, progress)
                    }
                    GET_CURRENT_CONTENT -> {
                        var content = registry.currentContent(name)
                        if (content == null) {
                            content = "{ error: '$name not found' }"
                        }
                        writer.writeUTF(content)
                    }
                    SET_DRAW_DEBUG -> {
                        val drawDebug = reader.readBoolean()
                        println("Read drawDebug $drawDebug")
                        val debugMode = if (drawDebug)
                            MotionLayoutDebugFlags.SHOW_ALL else
                            MotionLayoutDebugFlags.NONE
                        registry.setDrawDebug(name, debugMode.ordinal)
                    }
                    GET_LAYOUT_LIST -> {
                        val list = registry.getLayoutList()
                        writer.writeInt(list.size)
                        for (layout in list) {

                            writer.writeUTF(layout)
                            writer.writeLong(registry.getLastModified(layout))
                        }
                    }
                    GET_CURRENT_LAYOUT -> {
                        registry.setLayoutInformationMode(name, 1)
                        var content = registry.currentLayoutInformation(name)
                        if (content == null) {
                            content = "{ error: '$name not found' }"
                        }
                        writer.writeUTF(content)
                    }
                    UPDATE_LAYOUT_DIMENSIONS -> {
                        val width = reader.readInt()
                        val height = reader.readInt()
                        registry.updateDimensions(name, width, height)
                    }
                }
            } catch (e : Exception) {
                println("Exception $e")
                e.printStackTrace()
                closeConnection(socket)
                running = false
            }
        }
        println("Client disconnected")
    }

    private fun closeConnection(socket: Socket) {
        socket.close()
    }
}
