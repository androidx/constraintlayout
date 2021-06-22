/*
 * Copyright (C) 2021 The Android Open Source Project
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
package androidx.constraintlayout.core.motion.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Utils {
    public static void log(String tag, String value) {
        System.out.println(tag+" : "+value);
    }
    public static void loge(String tag, String value) {
        System.err.println(tag+" : "+value);
    }

   public static void socketSend(String str) {
       try {
           Socket socket = new Socket("127.0.0.1", 5327);
           OutputStream out = socket.getOutputStream();
           out.write(str.getBytes());
           out.close();
       } catch (IOException e) {
           e.printStackTrace();
       }
   }
}
