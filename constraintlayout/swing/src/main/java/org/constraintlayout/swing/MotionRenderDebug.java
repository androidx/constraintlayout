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
package org.constraintlayout.swing;

import androidx.constraintlayout.core.motion.Motion;
import androidx.constraintlayout.core.motion.MotionPaths;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

public class MotionRenderDebug {
    public static final int DEBUG_SHOW_NONE = 0;
    public static final int DEBUG_SHOW_PROGRESS = 1;
    public static final int DEBUG_SHOW_PATH = 2;

    static final int MAX_KEY_FRAMES = 50;
    private static final int DEBUG_PATH_TICKS_PER_MS = 16;
    float[] mPoints;
    int[] mPointsX;
    int[] mPointsY;
    int[] mPathMode;
    float[] mKeyFramePoints;
    Path2D mPath;

    Color mColor;
    Stroke mStroke;
    Color mColorKeyframes;
    Color mColorGraph;
    Color mTextColor;
    Color mFillPColor;

    Stroke mDash;
    final int RED_COLOR = 0xFFFFAA33;
    final int KEYFRAME_COLOR = 0xffe0759a;
    final int GRAPH_COLOR = 0xFF33AA00;
    final int SHADOW_COLOR = 0x77000000;
    final int DIAMOND_SIZE = 10;
    int mKeyFrameCount;
    Rectangle2D mBounds = new Rectangle2D.Float();
    boolean mPresentationMode = false;
    int mShadowTranslate = 1;
    float mTextSize;
    float[]mRectangle = new float[8];
    public MotionRenderDebug(float textSize) {

        mColor = new Color(RED_COLOR);
        mStroke = new BasicStroke(2);
        mColorKeyframes = new Color(KEYFRAME_COLOR);
        mColorGraph = new Color(GRAPH_COLOR);
        mTextColor = new Color(GRAPH_COLOR);
        mTextSize = mTextSize;
        mDash = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1, new float[]{4, 8}, 0);

    }

    public void draw(Graphics2D g2d,
                     HashMap<String, Motion> frameArrayList,
                     int duration, int debugPath,
                     int layoutWidth, int layoutHeight) {
        if (frameArrayList == null || frameArrayList.size() == 0) {
            return;
        }
        Graphics2D g = (Graphics2D) g2d.create();

        for (Motion motionController : frameArrayList.values()) {
            draw(g, motionController, duration, debugPath,
                    layoutWidth, layoutHeight);
        }
        //  g2d.restore();
    }

    public void draw(Graphics2D g2d,
                     Motion motionController,
                     int duration, int debugPath,
                     int layoutWidth, int layoutHeight) {
        int mode = motionController.getDrawPath();
        if (debugPath > 0 && mode == Motion.DRAW_PATH_NONE) {
            mode = Motion.DRAW_PATH_BASIC;
        }
        if (mode == Motion.DRAW_PATH_NONE) { // do not draw path
            return;
        }

        mKeyFrameCount = motionController.buildKeyFrames(mKeyFramePoints, mPathMode, null);

        if (mode >= Motion.DRAW_PATH_BASIC) {

            int frames = duration / DEBUG_PATH_TICKS_PER_MS;
            if (mPoints == null || mPoints.length != frames * 2) {
                mPoints = new float[frames * 2];
                mPath = new Path2D.Float();
                mPointsX = new int[frames];
                mPointsY = new int[frames];
            }

            g2d.translate(mShadowTranslate, mShadowTranslate);

            g2d.setColor(Color.BLACK);
            motionController.buildPath(mPoints, frames);
            for (int i = 0; i < frames; i++) {
                mPointsX[i] = (int) mPoints[i * 2];
                mPointsY[i] = (int) mPoints[i * 2 + 1];

            }
            drawAll(g2d, mode, mKeyFrameCount, motionController, layoutWidth, layoutHeight);
            g2d.setColor(mColor);

            g2d.translate(-mShadowTranslate, -mShadowTranslate);
            drawAll(g2d, mode, mKeyFrameCount, motionController, layoutWidth, layoutHeight);
            if (mode == Motion.DRAW_PATH_RECTANGLE) {
                drawRectangle(g2d, motionController);
            }
        }
    }

    public void drawAll(Graphics2D g2d, int mode, int keyFrames, Motion motionController,
                        int layoutWidth, int layoutHeight) {
        if (mode == Motion.DRAW_PATH_AS_CONFIGURED) {
            drawPathAsConfigured(g2d);
        }
        if (mode == Motion.DRAW_PATH_RELATIVE) {
            drawPathRelative(g2d);
        }
        if (mode == Motion.DRAW_PATH_CARTESIAN) {
            drawPathCartesian(g2d);
        }
        drawBasicPath(g2d);
        drawTicks(g2d, mode, keyFrames, motionController, layoutWidth, layoutHeight);
    }

    private void drawBasicPath(Graphics2D g2d) {
        g2d.drawPolyline(mPointsX, mPointsY, mPointsX.length);
    }

    private void drawTicks(Graphics2D g2d, int mode, int keyFrames, Motion motionController,
                           int layoutWidth, int layoutHeight) {
        int viewWidth = 0;
        int viewHeight = 0;
        if (motionController.getView() != null) {
            viewWidth = motionController.getView().getWidth();
            viewHeight = motionController.getView().getHeight();
        }
        for (int i = 1; i < keyFrames - 1; i++) {
            if (mode == Motion.DRAW_PATH_AS_CONFIGURED
                    && mPathMode[i - 1] == Motion.DRAW_PATH_NONE) {
                continue;

            }
            float x = mKeyFramePoints[i * 2];
            float y = mKeyFramePoints[i * 2 + 1];
            mPath.reset();
            mPath.moveTo(x, y + DIAMOND_SIZE);
            mPath.lineTo(x + DIAMOND_SIZE, y);
            mPath.lineTo(x, y - DIAMOND_SIZE);
            mPath.lineTo(x - DIAMOND_SIZE, y);
            mPath.closePath();

            MotionPaths framePoint = motionController.getKeyFrame(i - 1);
            float dx = 0;//framePoint.translationX;
            float dy = 0;//framePoint.translationY;
            if (mode == Motion.DRAW_PATH_AS_CONFIGURED) {

                if (mPathMode[i - 1] == MotionPaths.PERPENDICULAR) {
                    drawPathRelativeTicks(g2d, x - dx, y - dy);
                } else if (mPathMode[i - 1] == MotionPaths.CARTESIAN) {
                    drawPathCartesianTicks(g2d, x - dx, y - dy);
                } else if (mPathMode[i - 1] == MotionPaths.SCREEN) {
                    drawPathScreenTicks(g2d, x - dx, y - dy, viewWidth, viewHeight, layoutWidth, layoutHeight);
                }

                g2d.draw(mPath);
            }
            if (mode == Motion.DRAW_PATH_RELATIVE) {
                drawPathRelativeTicks(g2d, x - dx, y - dy);
            }
            if (mode == Motion.DRAW_PATH_CARTESIAN) {
                drawPathCartesianTicks(g2d, x - dx, y - dy);
            }
            if (mode == Motion.DRAW_PATH_SCREEN) {
                drawPathScreenTicks(g2d, x - dx, y - dy, viewWidth, viewHeight, layoutWidth, layoutHeight);
            }
            if (dx != 0 || dy != 0) {
                drawTranslation(g2d, x - dx, y - dy, x, y);
            } else {
                g2d.draw(mPath);
            }
        }
        if (mPoints.length > 1) {
            // Draw the starting and ending circle
            g2d.drawOval((int) mPoints[0]-4, (int) mPoints[1]-4, 8, 8);
            g2d.drawOval((int) mPoints[mPoints.length - 2]-4,
                    (int) mPoints[mPoints.length - 1]-4, 8, 8);
        }
    }

    private void drawTranslation(Graphics2D g2d, float x1, float y1, float x2, float y2) {
        g2d.drawRect((int) x1, (int) y1, (int) x2, (int) y2);
        g2d.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
    }

    private void drawPathRelative(Graphics2D g2d) {
        g2d.drawLine((int) mPoints[0], (int) mPoints[1],
                (int) mPoints[mPoints.length - 2], (int) mPoints[mPoints.length - 1]);
    }

    private void drawPathAsConfigured(Graphics2D g2d) {
        boolean path = false;
        boolean cart = false;
        for (int i = 0; i < mKeyFrameCount; i++) {
            if (mPathMode[i] == MotionPaths.PERPENDICULAR) {
                path = true;
            }
            if (mPathMode[i] == MotionPaths.CARTESIAN) {
                cart = true;
            }
        }
        if (path) {
            drawPathRelative(g2d);
        }
        if (cart) {
            drawPathCartesian(g2d);
        }
    }

    private void drawPathRelativeTicks(Graphics2D g2d, float x, float y) {
        float x1 = mPoints[0];
        float y1 = mPoints[1];
        float x2 = mPoints[mPoints.length - 2];
        float y2 = mPoints[mPoints.length - 1];
        float dist = (float) Math.hypot(x1 - x2, y1 - y2);
        float t = ((x - x1) * (x2 - x1) + (y - y1) * (y2 - y1)) / (dist * dist);
        float xp = x1 + t * (x2 - x1);
        float yp = y1 + t * (y2 - y1);

        Path2D path = new Path2D.Float();
        path.moveTo(x, y);
        path.lineTo(xp, yp);
        float len = (float) Math.hypot(xp - x, yp - y);
        String text = "" + ((int) (100 * len / dist)) / 100.0f;
        getTextBounds(text, g2d);
        float off = len / 2 - (float) mBounds.getWidth() / 2;
        //   g2d.drawTextOnPath(text, path, off, -20, mTextPaint);
        g2d.drawLine((int) x, (int) y, (int) xp, (int) yp);
    }

    void getTextBounds(String text, Graphics2D g) {
        FontMetrics fm = g.getFontMetrics();
        mBounds.setFrame(fm.getStringBounds(text, g));
    }

    private void drawPathCartesian(Graphics2D g2d) {
        float x1 = mPoints[0];
        float y1 = mPoints[1];
        float x2 = mPoints[mPoints.length - 2];
        float y2 = mPoints[mPoints.length - 1];

        g2d.drawLine((int) Math.min(x1, x2), (int) Math.max(y1, y2),
                (int) Math.max(x1, x2), (int) Math.max(y1, y2));
        g2d.drawLine((int) Math.min(x1, x2), (int) Math.min(y1, y2),
                (int) Math.min(x1, x2), (int) Math.max(y1, y2));
    }

    private void drawPathCartesianTicks(Graphics2D g2d, float x, float y) {
        float x1 = mPoints[0];
        float y1 = mPoints[1];
        float x2 = mPoints[mPoints.length - 2];
        float y2 = mPoints[mPoints.length - 1];
        float minx = Math.min(x1, x2);
        float maxy = Math.max(y1, y2);
        float xgap = x - Math.min(x1, x2);
        float ygap = Math.max(y1, y2) - y;
        // Horizontal line
        String text = "" + ((int) (0.5 + 100 * xgap / Math.abs(x2 - x1))) / 100.0f;
        getTextBounds(text, g2d);
        float off = xgap / 2 - (float) mBounds.getWidth() / 2;
        g2d.drawString(text, off + minx, y - 20);
        g2d.drawLine((int) x, (int) y, (int) Math.min(x1, x2), (int) y);

        // Vertical line
        text = "" + ((int) (0.5 + 100 * ygap / Math.abs(y2 - y1))) / 100.0f;
        getTextBounds(text, g2d);
        off = ygap / 2 - (float) mBounds.getHeight() / 2;
        g2d.drawString(text, x + 5, maxy - off);
        g2d.drawLine((int) x, (int) y, (int) x, (int) Math.max(y1, y2));
    }

    private void drawPathScreenTicks(Graphics2D g2d, float x, float y, int viewWidth, int viewHeight,
                                     int layoutWidth, int layoutHeight) {
        float x1 = 0;
        float y1 = 0;
        float x2 = 1;
        float y2 = 1;
        float minx = 0;
        float maxy = 0;
        float xgap = x;
        float ygap = y;
        // Horizontal line
        String text = "" + ((int) (0.5 + 100 * (xgap - viewWidth / 2) / (layoutWidth - viewWidth))) / 100.0f;
        getTextBounds(text, g2d);
        float off = xgap / 2 - (float) mBounds.getWidth() / 2;
        g2d.drawString(text, off + minx, y - 20);
        g2d.drawLine((int) x, (int) y,
                (int) Math.min(x1, x2), (int) y);

        // Vertical line
        text = "" + ((int) (0.5 + 100 * (ygap - viewHeight / 2) / (layoutHeight - viewHeight))) / 100.0f;
        getTextBounds(text, g2d);
        off = ygap / 2 - (float) mBounds.getHeight() / 2;
        g2d.drawString(text, x + 5, maxy - off);
        g2d.drawLine((int) x, (int) y, (int) x, (int) Math.max(y1, y2));
    }

    private void drawRectangle(Graphics2D g2d, Motion motionController) {
        mPath.reset();
        int rectFrames = 50;
        for (int i = 0; i <= rectFrames; i++) {
            float p = i / (float) rectFrames;
            motionController.buildRect(p, mRectangle, 0);
            mPath.moveTo(mRectangle[0], mRectangle[1]);
            mPath.lineTo(mRectangle[2], mRectangle[3]);
            mPath.lineTo(mRectangle[4], mRectangle[5]);
            mPath.lineTo(mRectangle[6], mRectangle[7]);
            mPath.closePath();
        }
        g2d.setColor(new Color(0x44000000, true));
        g2d.translate(2, 2);
        g2d.draw(mPath);

        g2d.translate(-2, -2);

        g2d.setColor(new Color(0xFFFF0000, true));
        g2d.draw(mPath);
    }

}
