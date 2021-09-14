
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
package androidx.constraintLayout.desktop.motion;
 
import org.constraintlayout.swing.MotionLayout;
import org.constraintlayout.swing.MotionPanel;

import javax.swing.*;

 
public class SwingDemo5 extends MotionPanel {
    static String motionScene = "{\n" +
            "                 Header: {\n" +
            "                  name: 'RotationZ28'\n" +
            "                },\n" +
            "                ConstraintSets: {\n" +

            "                  start: {\n" +
            "                    b1: {\n" +
            "                      width: 90,\n" +
            "                      height: 40,\n" +
            "                      start: ['parent', 'start', 70],\n" +
            "                      bottom: ['parent', 'bottom', 16]\n" +
            "                    }\n" +
            "                    b2: {\n" +
            "                      width: 90,\n" +
            "                      height: 40,\n" +
            "                      rotationZ: 0,\n" +
            "                      start: ['b1', 'end', 16],\n" +
            "                      bottom: ['b1', 'bottom', 0]\n" +
            "                    }\n" +
            "                  },\n" +
            "                  end: {\n" +
            "                    b1: {\n" +
            "                      width: 90,\n" +
            "                      height: 40,\n" +
            "                      end: ['parent', 'end', 16],\n" +
            "                      top: ['parent', 'top', 16]\n" +
            "                    }\n" +
            "                    b2: {\n" +
            "                      width: 90,\n" +
            "                      height: 40,\n" +
            "                      rotationZ: 0,\n" +
            "                      start: ['b1', 'start', 0],\n" +
            "                      top: ['b1', 'bottom', 16]\n" +
            "                    }\n" +
            "                  }\n" +
            "                },\n" +
            "                Transitions: {\n" +
            "                  default: {\n" +
            "                    from: 'start',\n" +
            "                    to: 'end',\n" +
           "                    pathMotionArc: 'startHorizontal',\n" +
            "                    KeyFrames: {\n" +
            "                      KeyAttributes: [\n" +
            "                        {\n" +
            "                          target: ['b2'],\n" +
            "                          frames: [33, 66],\n" +
            "                          translationX: [32, 0.0 ],\n" +
            "                          \n" +
            "                        }\n" +
            "                      ]\n" +
            "                    }\n" +
            "                  }\n" +
            "                }\n" +
            "            }";
    MotionLayout ml;
    float p = 0;

 
    public SwingDemo5() {
        setLayoutDescription(motionScene);

        JButton button1 = new JButton("widget1");
        JButton button2 = new JButton("widget2");

        add(button1, "widget1");
        add(button2, "widget2");
 

        Timer timer = new Timer(16, (e) -> setProgress(p = (p + 0.01f) % 1));
        timer.setRepeats(true);
        timer.start();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame(SwingDemo5.class.getSimpleName());
        Utils.log(frame.getTitle());
        SwingDemo5 panel = new SwingDemo5();
 
        frame.setContentPane(panel);
        frame.setBounds(100, 100, 400, 500);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
