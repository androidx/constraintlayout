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

import  android.support.constraintLayout.extlib.graph3d.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 * The JPanel that draws the Scene and handles mouse input
 */
public class Graph3dPanel extends JPanel {
   class ImageAdapter implements Graph.ImageSupport {
        private BufferedImage mImage;
        private int[] mImageBuff;

        @Override
        public void makeImage(int width, int height) {
            mImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            mImageBuff = ((DataBufferInt) (mImage.getRaster().getDataBuffer())).getData();
        }

        @Override
        public int[] getBacking() {
            return mImageBuff;
        }

        public BufferedImage getmImage() {
            return mImage;
        }
    }
    ImageAdapter image = new ImageAdapter();
    Graph graph = new Graph(image);
    Timer animationTimer;
    boolean animated = false;


    public Graph3dPanel() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                onSizeChanged(e);
            }

        });
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                onKeyTyped(e);
            }
        });
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                onMouseUP(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                requestFocus();
                onMouseDown(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                onMouseDrag(e);
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                onMouseWheel(e);
            }
        };
        addMouseWheelListener(mouseAdapter);
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    public void onKeyTyped(KeyEvent e) {
       char c=  e.getKeyChar();
        System.out.println(c);
        switch (c) {
            case  ' ':
                toggleAnimation();
        }
    }

    void toggleAnimation() {
        animated = !animated;
        if (!animated) {
            animationTimer.stop();
            animationTimer = null;
            return;
        }
        graph.setStartTime();
        graph.buildSurface(Graph.BLACK_HOLE_MERGE);

        animationTimer = new Timer(7, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long now = System.nanoTime();
                graph.tick(now);
                repaint();
            }
        });
        animationTimer.start();
    }

    public void onSizeChanged(ComponentEvent c) {
        int width = getWidth();
        int height = getHeight();

        if (width == 0 || height == 0) {
            return;
        }
        graph.resize(width,height);
    }

    public void onMouseDown(MouseEvent ev) {
graph.trackDown(ev.getX(),ev.getY());
    }

    public void onMouseDrag(MouseEvent ev) {
graph.trackDrag(ev.getX(),ev.getY());
        repaint();
    }

    public void onMouseUP(MouseEvent ev) {
        graph.trackDone();
    }


    public void onMouseWheel(MouseWheelEvent ev) {
       boolean  control = ev.isControlDown();
       float rotation = ev.getWheelRotation();
       graph.wheel(rotation, control);
        repaint();
    }

    long previous = System.nanoTime();
    int count = 0;
    public void paintComponent(Graphics g) {
        int w = getWidth();
        int h = getHeight();
       graph.render();
        if (image.getmImage() == null) {
            return;
        }
        g.drawImage(image.getmImage(), 0, 0, null);
        count++;
        long now = System.nanoTime();
        if (now -previous > 1000000000) {
          //  System.out.println(time+" fps "+count/((now-previous)*1E-9f));
            count = 0;
            previous = now;
        }
    }


}
