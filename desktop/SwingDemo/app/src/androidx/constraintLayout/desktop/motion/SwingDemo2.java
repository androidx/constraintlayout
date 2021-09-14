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
package androidx.constraintLayout.desktop.motion;

import androidx.constraintlayout.core.motion.utils.Utils;
import org.constraintlayout.swing.ConstraintLayout;

import javax.swing.*;

public class SwingDemo2 extends JPanel {
    static String constraintSet  = "{" +
            "      b1: {\n" +
            "        width: 100,\n" +
            "        height: 100,\n" +
            "        start: ['parent', 'start', 16],\n" +
            "        centerVertically: 'parent'\n" +
            "      },\n" +
            "      b2: {\n" +
            "        width: 100,\n" +
            "        height: 100,\n" +
            "        start: ['parent', 'start', 36],\n" +
            "        top: ['b1', 'top', 20],\n" +
            "        translationZ: 30\n" +
            "      }\n" +
            "    }";
    public SwingDemo2() {
        setLayout(new ConstraintLayout(constraintSet,()-> {
            JButton button1 = new JButton("Hello");
            JButton button2 = new JButton("World");

            add(button1, "b1");
            add(button2, "b2");
        })
        );
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame(SwingDemo2.class.getSimpleName());
        Utils.log(frame.getTitle());
        SwingDemo2 panel = new SwingDemo2();

        frame.setContentPane(panel);
        frame.setBounds(100, 100, 400, 500);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
