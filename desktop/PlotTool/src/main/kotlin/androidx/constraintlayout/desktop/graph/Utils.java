/*
 * Copyright (C) 2023 The Android Open Source Project
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
package androidx.constraintlayout.desktop.graph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.prefs.Preferences;

public class Utils {

    private static final String PREFS_NODE_NAME = "FramePositionSaver";
    private static final String PREF_X = "x";
    private static final String PREF_Y = "y";
    private static final String PREF_WIDTH = "width";
    private static final String PREF_HEIGHT = "height";


    public static JFrame smartFrame(String name) {
        JFrame frame = new JFrame(name);
        String fName = name.replace(' ', '_');
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setBounds(100, 10, 500, 500);
        restoreFramePosition(frame, fName);
        frame.addComponentListener(new ComponentAdapter() {

            public void componentMoved(ComponentEvent e) {
                Utils.saveFramePosition(frame, fName);
            }

            @Override
            public void componentResized(ComponentEvent e) {
                Utils.saveFramePosition(frame, fName);
            }
        });
        return frame;
    }

    public static void saveFramePosition(JFrame frame) {
        saveFramePosition(frame, PREFS_NODE_NAME);
    }

    public static void saveFramePosition(JFrame frame, String name) {
        Preferences prefs = Preferences.userRoot().node(name);
        prefs.putInt(PREF_X, frame.getX());
        prefs.putInt(PREF_Y, frame.getY());
        prefs.putInt(PREF_WIDTH, frame.getWidth());
        prefs.putInt(PREF_HEIGHT, frame.getHeight());
    }

    public static void restoreFramePosition(JFrame frame) {
        restoreFramePosition(frame, PREFS_NODE_NAME);
    }

    public static void restoreFramePosition(JFrame frame, String name) {
        Preferences prefs = Preferences.userRoot().node(name);
        int x = prefs.getInt(PREF_X, 100);
        int y = prefs.getInt(PREF_Y, 100);
        int width = prefs.getInt(PREF_WIDTH, 500);
        int height = prefs.getInt(PREF_HEIGHT, 500);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if (x + width > screenSize.width) {
            x = screenSize.width - width;
        }
        if (y + height > screenSize.height) {
            y = screenSize.height - height;
        }
        frame.setBounds(x, y, width, height);

    }

    public static void log(String msg) {
        StackTraceElement ste = new Throwable().getStackTrace()[1];
        String stack = ".(" + ste.getFileName() + ":" + ste.getLineNumber() + ") " + ste.getMethodName();
        System.out.println(stack + " " + msg);
    }


    public static void logStack(String msg, int n) {
        StackTraceElement[] st = new Throwable().getStackTrace();
        String s = " ";
        n = Math.min(n, st.length - 1);
        for (int i = 1; i <= n; i++) {
            StackTraceElement ste = st[i];
            String stack = ".(" + ste.getFileName() + ":"
                    + ste.getLineNumber() + ") " + ste.getMethodName();
            s += " ";
            System.out.println(msg + s + stack + s);
        }
    }
}
