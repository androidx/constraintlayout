package org.constraintlayout.swing;

import androidx.constraintlayout.core.motion.utils.Utils;
import androidx.constraintlayout.core.state.Registry;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;

/**
 * This provides the socket comunication to use the link server
 */
public class LinkServer {
    private ServerSocket server;
    private int port = 9999;

    final int UPDATE_CONTENT = 1;
    final int UPDATE_PROGRESS = 2;
    final int GET_CURRENT_CONTENT = 3;
    final int SET_DRAW_DEBUG = 4;
    final int GET_LAYOUT_LIST = 5;
    final int GET_CURRENT_LAYOUT = 6;
    final int UPDATE_LAYOUT_DIMENSIONS = 7;

    enum MotionLayoutDebugFlags {
        NONE,
        SHOW_ALL,
        UNKNOWN
    }

    private void init() {
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LinkServer() {
        init();
        start();
    }

    public LinkServer(int port) {
        this.port = port;
        init();
        start();
    }

    private void start() {
        System.out.println("Starting server");
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    Socket client = server.accept();
                    Thread acceptThread = new Thread(() -> handleRequest(client));
                    acceptThread.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.setName("Link Debug Thread");
        t.start();
    }

    void handleRequest(Socket socket) {
        boolean running = true;
        DataInputStream reader;
        DataOutputStream writer;
        try {
            reader = new DataInputStream(socket.getInputStream());
            writer = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Registry registry = Registry.getInstance();
        while (running) {
            try {
                int type = reader.readInt();
                String name = reader.readUTF();
                switch (type) {
                    case UPDATE_CONTENT:
                        String content = reader.readUTF();
                        registry.updateContent(name, content);
                        break;
                    case UPDATE_PROGRESS:
                        float progress = reader.readFloat();
                        registry.updateProgress(name, progress);
                        break;
                    case GET_CURRENT_CONTENT:
                        content = registry.currentContent(name);
                        if (content == null) {
                            content = "{ error: '$name not found' }";
                        }
                        writer.writeUTF(content);
                        break;
                    case SET_DRAW_DEBUG:
                        boolean drawDebug = reader.readBoolean();
                        Utils.log("Read drawDebug $drawDebug");
                        MotionLayoutDebugFlags debugMode = (drawDebug) ?
                                MotionLayoutDebugFlags.SHOW_ALL :
                                MotionLayoutDebugFlags.NONE;
                        registry.setDrawDebug(name, debugMode.ordinal());
                        break;
                    case GET_LAYOUT_LIST:
                        Set<String> list = registry.getLayoutList();
                        writer.writeInt(list.size());
                        for (String layout : list) {
                          //  System.out.println("layout: " + layout);
                            writer.writeUTF(layout);
                            writer.writeLong(registry.getLastModified(layout));
                        }
                        break;
                    case GET_CURRENT_LAYOUT:
                        registry.setLayoutInformationMode(name, 1);
                        content = registry.currentLayoutInformation(name);
                        if (content == null) {
                            content = "{ error: '$name not found' }";
                        }
                        writer.writeUTF(content);
                        break;
                    case UPDATE_LAYOUT_DIMENSIONS:
                        int width = reader.readInt();
                        int height = reader.readInt();
                        registry.updateDimensions(name, width, height);
                        break;
                }

            } catch (Exception e) {

                System.out.println("Exception $e");
                e.printStackTrace();
                closeConnection(socket);
                running = false;
            }
        }
        System.out.println("Client disconnected");
    }

    private void closeConnection(Socket socket) {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
