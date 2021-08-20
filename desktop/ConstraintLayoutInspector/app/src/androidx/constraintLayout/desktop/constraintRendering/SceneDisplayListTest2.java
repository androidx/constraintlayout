/*
 * Copyright (C) 2016 The Android Open Source Project
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
package androidx.constraintLayout.desktop.constraintRendering;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;



public class SceneDisplayListTest2 extends JPanel {


     String simpleList = "DrawComponentFrame,0,0,1000,1000,1,1000,1000\n" +
                        "Clip,0,0,1000,1000\n" +
                        "DrawComponentBackground,450,490,100,20,1\n" +
                        "DrawTextRegion,450,490,100,20,0,16,false,false,5,5,28,1.0,\"\"\n" +
                        "DrawComponentFrame,450,490,100,20,1,20,20\n" +
                        "DrawConnection,2,450x490x100x20,0,0x0x1000x1000,0,1,false,0,0,false,0.5,0,0,0\n" +
                        "DrawConnection,2,450x490x100x20,1,0x0x1000x1000,1,1,false,0,0,false,0.5,0,0,0\n" +
                        "DrawConnection,2,450x490x100x20,2,0x0x1000x1000,2,1,false,0,0,false,0.5,0,0,0\n" +
                        "DrawConnection,2,450x490x100x20,3,0x0x1000x1000,3,1,false,0,0,false,0.5,0,0,0\n" +
                        "UNClip\n";
  DisplayList disp;
  SceneDisplayListTest2() {
    disp = DisplayList.getDisplayList(simpleList);

  }
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    disp.paint((Graphics2D) g, SceneContext.get());

  }

  public static void main(String[] args) {

   JFrame frame = new JFrame("test display ");
   frame.setContentPane(new SceneDisplayListTest2());
   frame.setBounds(100,100,1000,1000);
   frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
   frame.setVisible(true);
  }
}