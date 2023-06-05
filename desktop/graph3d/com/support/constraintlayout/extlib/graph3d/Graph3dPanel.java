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

import com.support.constraintlayout.extlib.graph3d.objects.AxisBox;
import com.support.constraintlayout.extlib.graph3d.objects.Surface3D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 * The JPanel that draws the Scene and handles mouse input
 */
public class Graph3dPanel extends JPanel {

    Scene3D mScene3D = new Scene3D();
    private BufferedImage mImage;
    private int[] mImageBuff;
    int mGraphType = 2;
    private float mLastTouchX0 = Float.NaN;
    private float mLastTouchY0;
    private float mLastTrackBallX;
    private float mLastTrackBallY;
    double mDownScreenWidth;
    Surface3D mSurface;
    AxisBox mAxisBox;
    float range = 20;
    float minZ = -6;
    float maxZ  = 40;
    float mZoomFactor = 1;

    public void buildSurface() {

        mSurface = new Surface3D((x, y) -> {
            double d = Math.sqrt(x * x + y * y);
            return 10 * ((d == 0) ? 1f : (float) (Math.sin(d)/(1+d) ));
        });
        mSurface.setRange(-range, range, -range, range);
        mScene3D.setObject(mSurface);
        mScene3D.resetCamera();
        mAxisBox = new AxisBox();
        mAxisBox.setRange(-range, range, -range, range, -2, 20);
        mScene3D.addPostObject(mAxisBox);
    }

    public Graph3dPanel() {

        buildSurface();

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                onSizeChanged(e);
            }

        });
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                onMouseUP(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
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

    public void onSizeChanged(ComponentEvent c) {
        int width = getWidth();
        int height = getHeight();

        if (width == 0 || height == 0) {
            return;
        }
        mImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        mImageBuff = ((DataBufferInt) (mImage.getRaster().getDataBuffer())).getData();
        mScene3D.setScreenDim(width, height, mImageBuff, 0x00AAAAAA);
    }

    public void onMouseDown(MouseEvent ev) {
        mDownScreenWidth = mScene3D.getScreenWidth();
        mLastTouchX0 = ev.getX();
        mLastTouchY0 = ev.getY();
        mScene3D.trackBallDown(mLastTouchX0, mLastTouchY0);
        mLastTrackBallX = mLastTouchX0;
        mLastTrackBallY = mLastTouchY0;
    }

    public void onMouseDrag(MouseEvent ev) {
        if (Float.isNaN(mLastTouchX0)) {
            return;
        }
        float tx = ev.getX();
        float ty = ev.getY();
        float moveX = (mLastTrackBallX - tx);
        float moveY = (mLastTrackBallY - ty);
        if (moveX * moveX + moveY * moveY < 4000f) {
            mScene3D.trackBallMove(tx, ty);
        }
        mLastTrackBallX = tx;
        mLastTrackBallY = ty;
        repaint();
    }

    public void onMouseUP(MouseEvent ev) {
        mLastTouchX0 = Float.NaN;
        mLastTouchY0 = Float.NaN;
    }


    public void onMouseWheel(MouseWheelEvent ev) {
        if (ev.isControlDown()) {
            mZoomFactor *= (float) Math.pow(1.01, ev.getWheelRotation());
            mScene3D.setZoom(mZoomFactor);
            mScene3D.setUpMatrix(getWidth(),getHeight());
            mScene3D.update();
        }  else {

            range = range * (float) Math.pow(1.01, ev.getWheelRotation());
            mSurface.setArraySize((int) (range * 5));
            System.out.println(range);
            mSurface.setRange(-range, range, -range, range);
            mAxisBox.setRange(-range, range, -range, range, minZ, maxZ);
            mScene3D.update();
        }
        repaint();
    }

    public void paintComponent(Graphics g) {
        int w = getWidth();
        int h = getHeight();
        if (mScene3D.notSetUp()) {
            mScene3D.setUpMatrix(w, h);
        }

        mScene3D.render(mGraphType);
        if (mImage == null) {
            return;
        }
        g.drawImage(mImage, 0, 0, null);
    }


}
