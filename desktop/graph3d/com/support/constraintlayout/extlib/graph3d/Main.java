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

package com.support.constraintlayout.extlib.graph3d;

import javax.swing.*;
import java.awt.*;

/**
 * Simple driver for the Graph3dPanel
 */
public class Main {
    static class Controls extends JPanel {
        Graph3dPanel gp;
        Controls(Graph3dPanel gp) {
            this.gp = gp;
            JCheckBox cb1 = new JCheckBox("camera Light",false);
            cb1.addActionListener((e)->{
                gp.mScene3D.mLightMovesWithCamera = cb1.isSelected();
                System.out.println(gp.mScene3D.mLightMovesWithCamera);
            });
            add(cb1);
            JSlider sl1 = new JSlider();
            sl1.getModel().addChangeListener((e)->{
              gp.mSurface.mSaturation = sl1.getValue()/100f;
              gp.mScene3D.update();
              gp.repaint();
            });
            add(sl1);
        }
    }

    public static void main(String[] args) {
        JFrame frame =  new JFrame("3d Plot");
        JPanel  p = new JPanel(new BorderLayout());
        Graph3dPanel  gp = new Graph3dPanel();
        p.add(gp);
        p.add(new Controls(gp),BorderLayout.SOUTH);
        frame.setContentPane(p);
        frame.setBounds(100,100,500,500);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }
}
