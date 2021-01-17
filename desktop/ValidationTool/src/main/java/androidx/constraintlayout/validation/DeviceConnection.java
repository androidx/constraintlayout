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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.zip.ZipInputStream;

public class DeviceConnection {
    private static final boolean USE_ZIP = true;
    Socket socket;
    Reader reader;
    Writer writer;

    public void close() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class Reader {
        BufferedInputStream stream;
        final int bufferSize = 8192;
        byte[] buffer = new byte[bufferSize];

        Reader(InputStream stream) {
            this.stream = new BufferedInputStream(stream);
        }

        String nextLine() {
            int size = bufferSize;
            int offset = 0;
            while (offset < size) {
                int toRead = size - offset;
                int read = 0;
                try {
                    read = stream.read(buffer, offset, toRead);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (read > 0) {
                    offset += read;
                }
            }
            int indexEnd = 0;
            for (int i = 0; i < size; i++) {
                if (buffer[i] == 0) {
                    indexEnd = i;
                    break;
                }
            }
            String line = new String(buffer, 0, indexEnd, StandardCharsets.UTF_8);
            return line;
        }
    }

    class Writer {
        BufferedOutputStream stream;
        final int bufferSize = 8192;
        byte[] buffer = new byte[bufferSize];

        Writer(OutputStream stream) {
            this.stream = new BufferedOutputStream(stream);
        }

        void println(String text) {
            byte[] byteArr = text.getBytes(StandardCharsets.UTF_8);
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
    }

    public DeviceConnection() {
        try {
            socket = new Socket("localhost", 4242);
            reader = new Reader(socket.getInputStream());
            writer = new Writer(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> listLayoutFiles() {
        ArrayList<String> files = new ArrayList<>();
        if (writer == null) {
            return files;
        }
        writer.println("LIST");
        boolean done = false;
        while (!done) {
            String response = reader.nextLine();
            if (response.equals("DONE")) {
                done = true;
            } else {
                files.add(response);
                System.out.println("Server response: " + response);
            }
        }
        return files;
    }

    public void loadFile(String file) {
        writer.println("LOAD");
        writer.println(file);
        complete();
    }

    public String getLayout(String file, Main.LayoutType mode, int optimization) {
        writer.println("LOAD_MEASURE");
        writer.println(file);
        writer.println(mode.toString());
        writer.println(Integer.toString(optimization));
        String result = reader.nextLine();
        complete();
        return result;
    }

    public String getLayout() {
        writer.println("GET_LAYOUT");
        String result = reader.nextLine();
        complete();
        return result;
    }

    private void complete() {
        String done = reader.nextLine();
        if (!done.equals("DONE")) {
            System.out.println("WTF!!");
        }
    }

    public void matchContent() {
        writer.println("MxM");
        complete();
    }

    public void wrapContent() {
        writer.println("WxW");
        complete();
    }

    public void wrapContentHorizontal() {
        writer.println("WxM");
        complete();
    }

    public void wrapContentVertical() {
        writer.println("MxW");
        complete();
    }

    public Image takePicture() {
        writer.println("TAKE_PICTURE");
        String response = reader.nextLine();
        int w = Integer.parseInt(response);
        response = reader.nextLine();
        int h = Integer.parseInt(response);
        response = reader.nextLine();
        int size = Integer.parseInt(response);
        if (size == 0) {
            complete();
            return null;
        }
        byte[] bytes = new byte[size];
        BufferedImage image = null;
        try {
            BufferedInputStream inputStream = reader.stream;
            int offset = 0;
            while (offset < size) {
                int toRead = size - offset;
                int read = inputStream.read(bytes, offset, toRead);
                if (read > 0) {
                    offset += read;
                }
            }
            if (USE_ZIP) {
                try {
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                    ZipInputStream zipInputStream = new ZipInputStream(byteArrayInputStream);
                    zipInputStream.getNextEntry();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    byte[] buff = new byte[32768];
                    int len = 0;
                    while ((len = zipInputStream.read(buff)) > 0) {
                        byteArrayOutputStream.write(buff, 0, len);
                    }
                    zipInputStream.closeEntry();
                    zipInputStream.close();
                    bytes = byteArrayOutputStream.toByteArray();
                } catch(IOException e){
                    e.printStackTrace();
                }
            }

            image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            IntBuffer intBuffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();
            int[] array = new int[intBuffer.remaining()];
            intBuffer.get(array);
            for (int i = 0; i < array.length; i++) {
                int pixel = array[i];
                int alpha = (pixel >> 24) & 0xff;
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;
                array[i] = (alpha << 24) + (blue << 16) + (green << 8) + red;
            }
            image.getRaster().setDataElements(0, 0, w, h, array);
        } catch (IOException e) {
            e.printStackTrace();
        }
        complete();
        return image;
    }

}
