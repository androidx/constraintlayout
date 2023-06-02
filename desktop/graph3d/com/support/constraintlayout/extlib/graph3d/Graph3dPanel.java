package com.support.constraintlayout.extlib.graph3d;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;


public class Graph3dPanel extends JPanel {

    SurfaceGen mSurfaceGen = new SurfaceGen();
    private BufferedImage mImage;
    private int[] mImageBuff;
    int mGraphType = 2;
    private float mLastTouchX0 = Float.NaN;
    private float mLastTouchY0;
    private float mLastTrackBallX;
    private float mLastTrackBallY;
    double mDownScreenWidth;

    public Graph3dPanel() {

        mSurfaceGen.calcSurface(-20, 20, -20, 20, true, new SurfaceGen.Function() {
            public float eval(float x, float y) {
                double d = Math.sqrt(x * x + y * y);
                return 10 * ((d == 0) ? 1f : (float) (Math.sin(d) / d));
            }
        });

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
        };
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
        mSurfaceGen.setScreenDim(width, height, mImageBuff, 0x00FFEEFF);
    }

    public void onMouseDown(MouseEvent ev) {
        mDownScreenWidth = mSurfaceGen.getScreenWidth();
        mLastTouchX0 = ev.getX();
        mLastTouchY0 = ev.getY();
        mSurfaceGen.trackBallDown(mLastTouchX0, mLastTouchY0);
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
            mSurfaceGen.trackBallMove(tx, ty);
        }
        mLastTrackBallX = tx;
        mLastTrackBallY = ty;
        repaint();
    }

    public void onMouseUP(MouseEvent ev) {
        mLastTouchX0 = Float.NaN;
        mLastTouchY0 = Float.NaN;
    }

    public void paintComponent(Graphics g) {
        int w = getWidth();
        int h = getHeight();
        if (mSurfaceGen.notSetUp()) {
            System.out.println("setup");
            mSurfaceGen.setUpMatrix(w, h);
        }

        mSurfaceGen.render(mGraphType);
        if (mImage == null) {
            return;
        }
        g.drawImage(mImage, 0, 0, null);
    }


}
