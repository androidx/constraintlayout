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
package androidx.constraintLayout.desktop.utils;


import androidx.constraintLayout.desktop.ui.utils.Debug;
import androidx.constraintlayout.core.motion.utils.Rect;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Desk {

    public static JMenuBar createTopMenu() {
        Action[] edit = {
                new EmptyAction("Edit"),
                new EmptyAction("Cut"),
                new EmptyAction("Copy"),
                new EmptyAction("Paste"),
        };
        Action[] file = {
                new EmptyAction("File"),
                new EmptyAction("New"),
                new EmptyAction("Open"),
                new EmptyAction("Save"),
        };

        return createTopMenu(file, edit);
    }


    static class EmptyAction extends AbstractAction {

        public EmptyAction(String str) {
            super(str);
        }

        @Override
        public boolean accept(Object sender) {
            return true;
        }

        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }

    public static JMenuBar createTopMenu(Action[]... top) {
        JMenuBar menuBar = new JMenuBar();
        for (int i = 0; i < top.length; i++) {
            Action[] actions = top[i];
            menuBar.add(createMenu(actions));
        }
        return menuBar;
    }

    private static JMenu createMenu(Action[] top) {
        JMenu editMenu = new JMenu(top[0]);
        for (int i = 1; i < top.length; i++) {
            JMenuItem item = new JMenuItem(top[i]);
            editMenu.add(item);
        }


        return editMenu;
    }

    // ========================================LAYOUT MANAGEMENT ========================================
    private static final String ALL_LAYOUT_NAMES = "allNames";
    private static final String DEFAULT_LAYOUT_NAME = "default";
    private static HashSet<String> ourCurrentUniqueNames = new HashSet<String>();
    private static HashMap<String,String> ourUniqueToNormalNames = new HashMap<>();
    private static Preferences ourLayoutPrefs = Preferences.userNodeForPackage(Desk.class);
    private static Layouts ourCurrentLayout = new Layouts();
    private static String ourCurrentLayoutName = DEFAULT_LAYOUT_NAME;
    private static HashMap<String, JFrame> ounKnownFrames = new HashMap<>();

    static {
        readAllLayoutS();
        System.out.println(" " + ourLayoutPrefs);
        ourCurrentLayout.readDefault(ourLayoutPrefs);
    }


    private static class Layouts {
        HashMap<String, Rectangle> layouts = new HashMap<>();
        String mName;
        String uniqueName = "";

        public void save(Preferences pref) {

            pref.put(uniqueName + "_name", mName);
            int size = layouts.size();
            pref.putInt(uniqueName + "_layoutCount", size);

            int count = 0;
            for (String name : layouts.keySet()) {
                Rectangle rec = layouts.get(name);
                count++;
                String prefix = uniqueName + "_layout" + count;
                pref.put(prefix, name);
                pref.putInt(prefix + "base_x", rec.x);
                pref.putInt(prefix + "base_y", rec.y);
                pref.putInt(prefix + "base_width", rec.width);
                pref.putInt(prefix + "base_height", rec.height);
            }
        }

        public void dump() {
            StackTraceElement s = new Throwable().getStackTrace()[1];
            System.out.println(".(" + s.getFileName() + ":" + s.getLineNumber() + ") dumping layouts");
            System.out.println("================ mName =================" + mName);
            System.out.println("uniqueName" + mName);
            for (String s1 : layouts.keySet()) {
                System.out.println(s1 + " = " + layouts.get(s1));
            }
            System.out.println("========================================" + mName);
        }

        public void loadIfPresent(String name, Rectangle pos) {

            if (layouts.containsKey(name)) {
                Rectangle tmp = ourCurrentLayout.layouts.get(name);
                pos.setBounds(tmp);
            } else {
                Debug.log("name +" + name + " Not found");
                dump();
            }
        }

        public void readDefault(Preferences pref) {
            uniqueName = "default";
            mName = pref.get(uniqueName + "_name", "default");
            int count = pref.getInt(uniqueName + "_layoutCount", 0);
            for (int i = 0; i < count; i++) {
                String prefix = uniqueName + "_layout" + (i + 1);
                String name = pref.get(prefix, "Unknown");
                Rectangle rec = new Rectangle();
                rec.x = pref.getInt(prefix + "base_x", rec.x);
                rec.y = pref.getInt(prefix + "base_y", rec.y);
                rec.width = pref.getInt(prefix + "base_width", rec.width);
                rec.height = pref.getInt(prefix + "base_height", rec.height);
                layouts.put(name, rec);
            }
        }

        public void read(Preferences pref, String uniqueName) {
            mName = pref.get(uniqueName + "_name", "UNKNOWN");
            int count = pref.getInt(uniqueName + "_layoutCount", 0);
            for (int i = 0; i < count; i++) {
                String prefix = uniqueName + "_layout" + (i + 1);
                String name = pref.get(prefix, "Unknown");
                Rectangle rec = new Rectangle();
                rec.x = pref.getInt(prefix + "base_x", rec.x);
                rec.y = pref.getInt(prefix + "base_y", rec.y);
                rec.width = pref.getInt(prefix + "base_width", rec.width);
                rec.height = pref.getInt(prefix + "base_height", rec.height);
                layouts.put(name, rec);
            }
        }
    }

    static void readAllLayoutS() {

        String[] str = ourLayoutPrefs.get(ALL_LAYOUT_NAMES, DEFAULT_LAYOUT_NAME).split(",");

        for (String unique_names : str) {
            String  realname = ourLayoutPrefs.get(unique_names+"_name", "unknown") ;
            ourUniqueToNormalNames.put(unique_names,realname);
        }
        ourCurrentUniqueNames.clear();
        ourCurrentUniqueNames.addAll(Arrays.asList(str));

    }

    static void saveAllLayoutNames() throws BackingStoreException {
        String[] str = ourCurrentUniqueNames.toArray(new String[0]);
        String allNames = "";
        for (int i = 0; i < str.length; i++) {
            allNames += (i == 0) ? str[i] : "," + str[i];
        }
        ourLayoutPrefs.put("allNames", allNames);
        ourLayoutPrefs.flush();
    }


    static void saveLayoutAs(String name) {
        ourCurrentLayout.mName = name;
        ourCurrentLayout.uniqueName = nameToUniqueName(name);
        ourCurrentUniqueNames.add(ourCurrentLayout.uniqueName);

        ourCurrentLayout.save(ourLayoutPrefs);
        try {
            saveAllLayoutNames();
            ourLayoutPrefs.flush();
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }
    }

    static void saveCurrentLayout() {
        ourCurrentLayout.save(ourLayoutPrefs);
        try {
            ourLayoutPrefs.flush();
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }
    }


    private static String nameToUniqueName(String name) {
        String ret = name.replaceAll("[^a-zA-Z0-9]", "");
        ret += System.currentTimeMillis() % 10000000; // chance of repeating is small risk is low
        return ret;
    }


    private static void setup() {
        if (ourCurrentUniqueNames == null) {
            ourCurrentUniqueNames = new HashSet<>();
        }
    }


    public static void makeLayoutCurrent(String newCurrent) {
//        ounKnownFrames;

        Layouts layout = new Layouts();
        layout.read(ourLayoutPrefs,newCurrent);
        ourCurrentLayout = layout;

        for (Object name : ounKnownFrames.keySet()) {
            JFrame frame = ounKnownFrames.get(name);
            Rectangle rect = ourCurrentLayout.layouts.get(name);
            if (rect!= null) {
                frame.setBounds(rect);
            }

        }
    }

    public static void saveLayoutAs(JMenu viewMenu) {

        String name = JOptionPane.showInputDialog(viewMenu, "Save Layout As");
        if (name != null) {
            saveLayoutAs(name);
            setupMenu(viewMenu);
        }
    }

    public static void setupMenu(JMenu viewMenu) {
        viewMenu.removeAll();
        viewMenu.add(new AbstractAction("Save Current Layout...") {

            @Override
            public void actionPerformed(ActionEvent e) {
                Desk.saveLayoutAs(viewMenu);
            }
        });
        viewMenu.add(new JSeparator());
        for (String name : ourCurrentUniqueNames) {
            viewMenu.add(new AbstractAction(ourUniqueToNormalNames.get(name)) {

                @Override
                public void actionPerformed(ActionEvent e) {
                    makeLayoutCurrent(name);
                }
            });
        }
    }

    public static void rememberPosition(JFrame frame, Rectangle defaultPos) {
        Rectangle pos;

        if (defaultPos != null) {
            pos = defaultPos;
        } else {
            pos = new Rectangle(100, 100, 1200, 800);
        }
        String name = frame.getContentPane().getClass().getSimpleName();
        ounKnownFrames.put(name, frame);

        ourCurrentLayout.loadIfPresent(name, pos);
        frame.setBounds(pos);
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resize();
            }

            public void componentMoved(ComponentEvent evt) {
                resize();
            }

            void resize() {

                Rectangle posNew = frame.getBounds();
                ourCurrentLayout.layouts.put(name, posNew);

                saveCurrentLayout();

            }
        });
    }


}
