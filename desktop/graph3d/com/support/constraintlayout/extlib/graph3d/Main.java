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

/**
 * Simple driver for the Graph3dPanel
 */
public class Main {
    public static void main(String[] args) {
        JFrame frame =  new JFrame("3d Plot");
        Graph3dPanel  p = new Graph3dPanel();
        frame.setContentPane(p);
        frame.setBounds(100,100,500,500);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }
}
