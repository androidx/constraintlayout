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
        BufferedInputStream stream;
        final int bufferSize = 8192;
        byte[] buffer = new byte[bufferSize];

        Reader(InputStream stream) {
            this.stream = new BufferedInputStream(stream);
        }

        @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
        String nextLine() {
            int size = bufferSize;
            int offset = 0;
            String line = null;
            while (offset < size) {
                int toRead = size - offset;
                int read = 0;
                try {
                    read = stream.read(buffer, offset, toRead);
                } catch (IOException e) {
                    e.printStackTrace();
                    return line;
                }
                if (read > 0) {
                    offset += read;
                } else {
                    return line;
                }
            }
            int indexEnd = 0;
            for (int i = 0; i < size; i++) {
                if (buffer[i] == 0) {
                    indexEnd = i;
                    break;
                }
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                line = new String(buffer, 0, indexEnd, StandardCharsets.UTF_8);
            }
            return line;
        }
    }

    static class Writer {
        BufferedOutputStream stream;
        final int bufferSize = 8192;
        byte[] buffer = new byte[bufferSize];

        Writer(OutputStream stream) {
            this.stream = new BufferedOutputStream(stream);
        }

        @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
        void println(String text) {
            byte[] byteArr = new byte[0];
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                byteArr = text.getBytes(StandardCharsets.UTF_8);
            }
            int len = Math.min(bufferSize, byteArr.length);
            for (int i = 0; i < len; i++) {
                buffer[i] = byteArr[i];
            }
            buffer[len] = 0;
            try {
                stream.write(buffer);
                stream.flush();
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
