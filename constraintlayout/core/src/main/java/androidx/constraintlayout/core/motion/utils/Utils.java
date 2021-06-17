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
