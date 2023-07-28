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

package com.google.constraintlayout.ext.variableplot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class VpGraphCore {
    private static final String FPS_STRING = "onDraw";

    List<VpGraphCore.Data> mPlots = new ArrayList<>();
    final static int MAX_BUFF = 2000;
    private long[] mTime = new long[MAX_BUFF];
    private float[] mValue = new float[MAX_BUFF];
    float duration = 10; // seconds
    float mMinY = 0;
    float mMaxY = 1;
    float mMinX = 0;
    float mMaxX = duration + mMinX;
    float axisLeft = 100;
    float axisTop = 100;
    float axisRight = 100;
    float axisBottom = 100;
    Paint mAxisPaint = new Paint();
    Paint mLinePaint = new Paint();
    Paint mGridPaint = new Paint();
    Rect mBounds = new Rect();
    long start = -1; // show the latest;
    boolean mGraphSelfFps = false;
    int sampleDelay = 15; // ms between samples.
    private boolean mLiveSample = true;
    private long mStartTime;
    private float mLineX = Float.NaN;
    public boolean debug = false;
    static class Data {
        float[] mX = new float[MAX_BUFF];
        float[] mY = new float[MAX_BUFF];
        Paint paint = new Paint();
        Path path = new Path();
        int mLength;
        String mTitle;
        float lastLabelYPos = Float.NaN;
        float lastLabelXPos = Float.NaN;

        Data(String title) {
            mTitle = title;
            mLength = -1;
            paint.setStyle(Paint.Style.STROKE);
        }

        void plot(Canvas canvas, VpGraphCore graph, int w, int h) {
            path.reset();
            float scaleX = graph.getScaleX(w, h);
            float scaleY = graph.getScaleY(w, h);
            float offX = graph.getOffsetX(w, h);
            float offY = graph.getOffsetY(w, h);
            boolean first = true;
            for (int i = 0; i < mLength; i++) {
                if ((i == mLength - 1 || mX[i + 1] >= graph.mMinX) && mX[i]<=graph.mMaxY) {

                    float x = mX[i] * scaleX + offX;
                    float y = mY[i] * scaleY + offY;
                    if (first) {
                        path.moveTo(x, y);
                        first = false;
                    } else {
                        path.lineTo(x, y);
                    }
                }
            }
            canvas.drawPath(path, paint);
        }

        public int findClosestX(float x) {
            int low = 0;
            int high = mLength - 1;
            int pos = -1;
            while (low <= high) {
                pos = low + (high - low) / 2;
                if (mX[pos] == x)
                    return pos;

                if (mX[pos] < x)
                    low = pos + 1;
                else
                    high = pos - 1;
            }
            return pos;
        }


    }

    private final UiDelegate mUiDelegate;

    public VpGraphCore(UiDelegate uiDelegate) {
        mUiDelegate = uiDelegate;
        init();
        mAxisPaint.setColor(Color.BLACK);
    }

    public void init() {
        mUiDelegate.post(this::listenToChannels);
        mAxisPaint.setTextSize(32);
        mAxisPaint.setStrokeWidth(3);
        mAxisPaint.setColor(Color.BLUE);
        mGridPaint.setTextSize(32);
        mGridPaint.setStrokeWidth(1);
        mLinePaint.setColor(Color.RED);
        mLinePaint.setStrokeWidth(3);
        mLinePaint.setTextSize(64);

    }
    float mDownX;

    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        int count = event.getPointerCount();
        // Log.v("Main", ">>>> count " + count + " " +event);
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                if (count == 2) {
                    float drag =  event.getX(0) + event.getX(1);
                    drag = (drag+mDownX)/2;
                    mStartTime += drag / getScaleX(mUiDelegate.getWidth(), mUiDelegate.getHeight());
                    listenToChannels();
                    Log.v("Main", ">>>> drag  "+ drag );
                    mLineX = Float.NaN;
                } else {
                    mLineX = event.getX();
                    Log.v("Main", ">>>> ACTION_MOVE " + event.getX() + " ");
                    mUiDelegate.invalidate();
                }
                break;
            case MotionEvent.ACTION_DOWN:
                if (count == 2) {
                    mLiveSample = false;
                    mDownX =  (event.getX(0) + event.getX(1));
                    Log.v("Main", count+">>>> drag on" +mDownX + " ");

                    mLineX = Float.NaN;
                } else {
                    mLineX = event.getX();
                    Log.v("Main", count+">>>> ACTION_DOWN " + event.getX());
                    mUiDelegate.invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.v("Main", ">>>> ACTION_UP " + event.getX());

                mLineX = Float.NaN;
                for (VpGraphCore.Data mPlot : mPlots) {
                    mPlot.lastLabelYPos = Float.NaN;
                    mPlot.lastLabelXPos = Float.NaN;
                }
                mUiDelegate.invalidate();
                break;
            case  MotionEvent.ACTION_POINTER_DOWN:
                if (count == 2) {

                    mLiveSample = false;
                    mDownX =  (event.getX(0) + event.getX(1));
                    Log.v("Main", count+">>>> ACTION_POINTER_DOWN" +mDownX + " "+ mLiveSample);

                    mLineX = Float.NaN;
                }
                break;
            case  MotionEvent.ACTION_POINTER_UP:
                Log.v("Main", ">>>> ACTION_POINTER_UP" + event.getX());
                if (event.getEventTime() - event.getDownTime() < 400) {
                    Log.v("Main", ">>>> false" );
                    mLiveSample = true;
                    listenToChannels();
                }

            default:
                Log.v("Main", ">>>> def " + event.getEventTime());
        }
        return true;
        //return super.onTouchEvent(event);
    }

    public void addChannel(String str) {
        mPlots.add(new VpGraphCore.Data(str));
    }

    public void setGraphFPS(boolean on) {
        mGraphSelfFps = on;
        if (on) {
            mPlots.add(new VpGraphCore.Data(FPS_STRING));
            //  mPlots.add(new Data("read"));
        } else {
            VpGraphCore.Data remove = null;
            for (int i = 0; i < mPlots.size(); i++) {
                if (mPlots.get(i).mTitle == FPS_STRING) {
                    remove = mPlots.get(i);
                    break;
                }
            }
            if (remove != null) {
                mPlots.remove(remove);
            }
        }
    }


    private void listenToChannels() {
        //  Vp.fps("read");
        if (mLiveSample) {
            listenLive();
            return;
        }
        int count = mPlots.size();

        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE;
        float maxY = -Float.MAX_VALUE;
        for (int i = 0; i < count; i++) {
            VpGraphCore.Data p = mPlots.get(i);
            String channel = p.mTitle;


            p.mLength = Vp.getAfter(channel, mStartTime, mTime, mValue);

            if (p.mLength == -1) {
                continue;
            }

            for (int j = 0; j < p.mLength; j++) {
                float x = (mTime[j] - mStartTime) * 1E-9f;
                p.mX[j] = x;
                minX = Math.min(x, minX);
                maxX = Math.max(x, maxX);

                float y = mValue[j];
                minY = Math.min(y, minY);
                maxY = Math.max(y, maxY);
                p.mY[j] = y;
            }
            Log.v("main", p.mTitle+"  "+ minX+" -> "+maxX);
        }

        minX = 0;

        maxX = minX + duration;
        Log.v("main",  "Total  "+ minX+" -> "+maxX);

        updateDataRange(minX, maxX, minY, maxY);

        mUiDelegate.invalidate();
    }

    private void listenLive() {

        int count = mPlots.size();

        mStartTime = System.nanoTime() - (long) (((double) duration) * 1000000000L);

        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE;
        float maxY = -Float.MAX_VALUE;
        for (int i = 0; i < count; i++) {
            VpGraphCore.Data p = mPlots.get(i);
            String channel = p.mTitle;

            p.mLength = Vp.getLatest(channel, mTime, mValue);

            if (p.mLength == -1) {
                continue;
            }

            for (int j = 0; j < p.mLength; j++) {
                float x = (mTime[j] - mStartTime) * 1E-9f;
                p.mX[j] = x;
                minX = Math.min(x, minX);
                maxX = Math.max(x, maxX);

                float y = mValue[j];
                minY = Math.min(y, minY);
                maxY = Math.max(y, maxY);
                p.mY[j] = y;
            }
        }
        if  (minX == Float.MAX_VALUE || Float.isNaN(minX)) {
            updateDataRange(0, 10, -1, 1);
        } else {
            updateDataRange(minX, maxX, minY, maxY);
        }

        mUiDelegate.invalidate();
        mUiDelegate.postDelayed(this::listenToChannels, sampleDelay);
    }

    private void updateDataRange(float minX, float maxX, float minY, float maxY) {
        minX = maxX - duration;
        // fast to expand slow to contract
        float factor = 10;
        mMaxY = (mMaxY > maxY) ? (mMaxY + maxY) / 2 : (mMaxY * factor + maxY) / (factor + 1);
        mMinY = (mMinY < minY) ? (mMinY + minY) / 2 : (mMinY * factor + minY) / (factor + 1);
        mMinX = (mMinX + minX) / 2;
        mMaxX = duration + mMinX;
    }

    public void onDraw(Canvas canvas) {
        int w = mUiDelegate.getWidth();
        int h = mUiDelegate.getHeight();
        drawAxis(canvas, w, h);
        drawGrid(canvas, w, h);
        for (VpGraphCore.Data p : mPlots) {
            p.plot(canvas, this, w, h);
        }
        if (mGraphSelfFps || debug) {
            Vp.fps(FPS_STRING);
        }

        drawTouchLine(canvas, w, h);
    }

    private void drawGrid(Canvas canvas, int w, int h) {
        double ticksX = calcTick(w, mMaxX - mMinX);
        double ticksY = calcTick(h, mMaxY - mMinY);
        float minX = (float) (ticksX * Math.ceil((mMinX + ticksX / 100) / ticksX));
        float maxX = (float) (ticksX * Math.floor(mMaxX / ticksX));
        //  Log.v("MAIN", " X " + ticksX + "  " + minX + " - " + maxX);
        int count = 0;
        float scaleX = getScaleX(w, h);
        float offX = getOffsetX(w, h);
        int txtPad = 4;

        for (float x = minX; x <= maxX; x += ticksX) {
            float xp = scaleX * x + offX;
            canvas.drawLine(xp, axisTop, xp, h - axisBottom, mGridPaint);
            if (x==(int)x) {
                String str = df.format(x);
                mAxisPaint.getTextBounds(str, 0, str.length(), mBounds);
                canvas.drawText(str, xp - mBounds.width()/2 , h-axisBottom+txtPad+mBounds.height(), mGridPaint);
            }
            count++;
        }

        float minY = (float) (ticksY * Math.ceil((mMinY + ticksY / 100) / ticksY));
        float maxY = (float) (ticksY * Math.floor(mMaxY / ticksY));
        //  Log.v("MAIN", " Y                                  " + ticksY + "  " + minY + " - " + maxY);

        float offY = getOffsetY(w, h);
        float scaleY = getScaleY(w, h);
          count = 0;

        for (float y = minY; y <= maxY; y += ticksY) {
            float yp = scaleY * y + offY;
            canvas.drawLine(axisLeft, yp, w - axisRight, yp, mGridPaint);

            if ((count & 1) == 1 && (y + ticksY) < maxY) {
                String str = df.format(y);
                mAxisPaint.getTextBounds(str, 0, str.length(), mBounds);
                canvas.drawText(str, axisLeft - mBounds.width() - txtPad*2, yp, mGridPaint);
            }
            count++;
        }
    }

    DecimalFormat df = new DecimalFormat("0.0");

    void drawTouchLine(Canvas canvas, int w, int h) {
        if (Float.isNaN(mLineX)) {
            return;
        }
        if (mLineX < axisLeft) {
            mLineX = axisLeft;
        } else if (mLineX > (w - axisRight)) {
            mLineX = w - axisRight;
        }
        float dataPos = (mLineX - getOffsetX(w, h)) / getScaleX(w, h);
        float yOffset = getOffsetY(w, h);
        float yScale = getScaleY(w, h);
        float rad = 10;
        canvas.drawLine(mLineX, axisTop, mLineX, h - axisBottom, mLinePaint);
        int bottom_count = 0;
        int top_count = 0;
        int pad = 5;
        boolean right = (mLineX < w / 2);
        for (VpGraphCore.Data plot : mPlots) {
            int index = plot.findClosestX(dataPos);
            if (index == -1) continue;
            float value = plot.mY[index];
            float y = yScale * value + yOffset;
            canvas.drawRoundRect(mLineX - rad, y - rad, mLineX + rad, y + rad, rad, rad, mLinePaint);
            String vString = plot.mTitle+":"+df.format(value);
            mLinePaint.getTextBounds(vString, 0, vString.length(), mBounds);
            float yPos = w / 2;
            int gap = 60;
            if (y > h / 2) {
                yPos = y - gap;
                bottom_count++;
            } else {
                top_count++;
                yPos = y + gap;
            }
            float xPos = (right) ? mLineX + gap : mLineX  - gap;
            if (Float.isNaN(plot.lastLabelYPos)) {
                plot.lastLabelYPos = yPos;
                plot.lastLabelXPos = xPos;
            } else {
                plot.lastLabelYPos = (plot.lastLabelYPos * 100 + yPos) / 101;
                plot.lastLabelXPos = (plot.lastLabelXPos * 100 + xPos) / 101;;

            }
            xPos = plot.lastLabelXPos;
            canvas.drawLine(mLineX, y,  xPos , plot.lastLabelYPos, mLinePaint);
            canvas.drawText(vString, right ? xPos :xPos-mBounds.width()-pad,  plot.lastLabelYPos, mLinePaint);
        }
    }

    void drawAxis(Canvas canvas, int w, int h) {
        int txtPad = 4;
        canvas.drawRGB(200, 230, 255);
        canvas.drawLine(axisLeft, axisTop, axisLeft, h - axisBottom, mAxisPaint);
        canvas.drawLine(axisLeft, h - axisBottom, w - axisRight, h - axisBottom, mAxisPaint);
        float y0 = getOffsetY(w, h);
        canvas.drawLine(axisLeft, y0, w - axisRight, y0, mAxisPaint);
        String str = df.format(mMaxY);
        mAxisPaint.getTextBounds(str, 0, str.length(), mBounds);
        canvas.drawText(str, axisLeft - mBounds.width() - txtPad, axisTop, mAxisPaint);
        str = df.format(mMinY);
        mAxisPaint.getTextBounds(str, 0, str.length(), mBounds);
        canvas.drawText(str, axisLeft - mBounds.width() - txtPad, h - axisBottom, mAxisPaint);
    }

    public float getScaleX(int w, int h) {
        float rangeX = mMaxX - mMinX;
        float graphSpanX = w - axisLeft - axisRight;
        return graphSpanX / rangeX;
    }

    public float getScaleY(int w, int h) {
        float rangeY = mMaxY - mMinY;
        float graphSpanY = h - axisTop - axisBottom;
        return -graphSpanY / rangeY;
    }

    public float getOffsetX(int w, int h) {
        return axisLeft - mMinX * getScaleX(w, h);
    }

    public float getOffsetY(int w, int h) {
        return h - axisBottom - mMinY * getScaleY(w, h);
    }

    static public double calcTick(int scr, double range) {
        int aprox_x_ticks = scr / 100;
        int type = 1;
        double best = Math.log10(range / (aprox_x_ticks));
        double n = Math.log10(range / (aprox_x_ticks * 2));
        if (fraction(n) < fraction(best)) {
            best = n;
            type = 2;
        }
        n = Math.log10(range / (aprox_x_ticks * 5));
        if (fraction(n) < fraction(best)) {
            best = n;
            type = 5;
        }
        return type * Math.pow(10, Math.floor(best));
    }

    static double fraction(double x) {
        return x - Math.floor(x);
    }
}
