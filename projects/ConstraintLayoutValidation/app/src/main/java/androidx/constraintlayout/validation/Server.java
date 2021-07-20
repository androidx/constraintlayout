/*
 * Copyright (C) 2019 The Android Open Source Project
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
package androidx.constraintlayout.validation;

import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.RequiresApi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server {

    private int mPort;
    private Requests mRequestsHandler;
    private Handler handler;
    private Boolean sync = new Boolean(false);

    public void waitOnNotify() {
        synchronized (sync) {
            try {
                sync.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void notifyUIDone() {
        System.out.println("notifyUIDone");
        synchronized (sync) {
            sync.notify();
        }
        System.out.println("notifyUIDone - done");
    }

    public interface Requests {
        void command(Server server, String command, Reader scanner, OutputStream out);
    }

    class Reader {
        DataInputStream stream;
        Reader(InputStream stream) {
            this.stream = new DataInputStream(stream);
        }

        @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
        String nextLine() {
            try {
                return stream.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }
    }

    static class Writer {
        DataOutputStream stream;

        Writer(OutputStream stream) {
            this.stream = new DataOutputStream(stream);
        }

        @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
        void println(String text) {
            try {
                stream.writeUTF(text);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        void write(byte[] buffer) {
            try {
                stream.write(buffer);
                stream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Server(int port) {
        mPort = port;
    }

    public void setRequestsHandler(Requests requestsHandler) {
        mRequestsHandler = requestsHandler;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket listener = new ServerSocket(mPort);
                    Looper.prepare();
                    handler = new Handler();
                    while (true) {
                        Socket socket = listener.accept();
                        try {
                            Reader scanner = new Reader(socket.getInputStream());
                            String command = scanner.nextLine();
                            boolean valid = true;
                            while (valid) {
                                mRequestsHandler.command(Server.this, command, scanner, socket.getOutputStream());
                                command = scanner.nextLine();
                                if (command == null) {
                                    valid = false;
                                }
                            }
                        } catch (Exception e) {
                            // nothing
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private final Lock lock = new ReentrantLock();
    private final Condition uiTaskDone = lock.newCondition();

    public void runOnUiAndWait(Activity activity, Runnable runnable) {
        lock.lock();
        try {
            activity.runOnUiThread(() -> {
                lock.lock();
                try {
                    runnable.run();
                    uiTaskDone.signal();
                } finally {
                    lock.unlock();
                }
            });
            uiTaskDone.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

}
