package androidx.constraintLayout.desktop.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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


    public static void rememberPosition(JFrame frame, Rectangle defaultPos) {
        Rectangle pos;
        if (defaultPos != null) {
            pos = defaultPos;
        } else {
            pos = new Rectangle(100, 100, 1200, 800);
        }
        Preferences pref = Preferences.userNodeForPackage(frame.getContentPane().getClass());
        String prefix = frame.getContentPane().getClass().getSimpleName() + ".";
        if (pref != null && pref.getInt(prefix + "base_x", -1) != -1) {
            pos.x = pref.getInt(prefix + "base_x", pos.x);
            pos.y = pref.getInt(prefix + "base_y", pos.y);
            pos.width = pref.getInt(prefix + "base_width", pos.width);
            pos.height = pref.getInt(prefix + "base_height", pos.height);
            System.out.println(pos);
        }
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
                pref.putInt(prefix + "base_x", posNew.x);
                pref.putInt(prefix + "base_y", posNew.y);
                pref.putInt(prefix + "base_width", posNew.width);
                pref.putInt(prefix + "base_height", posNew.height);
                try {
                    pref.flush();
                } catch (BackingStoreException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
