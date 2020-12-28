/*
 * Copyright (C) 2020 The Android Open Source Project
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
package android.support.constraint.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.text.DecimalFormat;
import java.util.Vector;

public class Graph2D extends View {
    private static final String TAG = "Graph2D";
    Margins mMargins = new Margins();
    Vector<DrawItem> myDrawItems = new Vector<DrawItem>();
    private static final int NPOINTS = 500;
    Plot plot = new Plot();
    Ticks mTicks = new Ticks();
    final float DEF_MIN_X = -10;
    final float DEF_MAX_X = 10;
    float min_y = 0;
    float max_y = 1;
    float min_x = DEF_MIN_X;
    float max_x = DEF_MAX_X;
    CalcEngine.Symbolic mEquation;
    CalcEngine.Stack mTmpStack = new CalcEngine.Stack();
    private float mLastTouchX0 = Float.NaN;
    private float mLastTouchY0;
    private float mLastTouchX1 = Float.NaN;
    private float mLastTouchY1;
    float down_min_y, down_max_y, down_min_x, down_max_x;

    static class Margins {
        int myInsTop = 30;
        int myInsLeft = 200;
        int myInsBottom = 30;
        int myInsRight = 30;
    }

    public Graph2D(Context context) {
        super(context);
        init();
    }

    public Graph2D(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Graph2D(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        myDrawItems.add(mAxis);
        myDrawItems.add(plot);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                if (!Float.isNaN(mLastTouchX0)) {
                    mLastTouchX1 = ev.getX(0);
                    mLastTouchY1 = ev.getY(0);
                    break;
                }
                if (ev.getPointerCount() == 2) {
                    mLastTouchX1 = ev.getX(1);
                    mLastTouchY1 = ev.getY(1);

                    break;
                }
                mLastTouchX0 = ev.getX(0);
                mLastTouchY0 = ev.getY(0);

                down_min_y = min_y;
                down_max_y = max_y;
                down_min_x = min_x;
                down_max_x = max_x;
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                float x1 = 0, y1 = 0;
                if (ev.getPointerCount() == 2) {
                    if (Float.isNaN(mLastTouchX1)) {
                        mLastTouchX1 = ev.getX(1);
                        mLastTouchY1 = ev.getY(1);
                    }
                    x1 = ev.getX(1);
                    y1 = ev.getY(1);
                }
                if (ev.getPointerCount() == 1 && !Float.isNaN(mLastTouchX0)) {
                    return true;
                }
                final float x0 = ev.getX(0);
                final float y0 = ev.getY(0);

                float dx = x0 - mLastTouchX0;
                float dy = y0 - mLastTouchY0;
                float tscalex = 1, tscaley = 1;
                if (!Float.isNaN(mLastTouchX1)) {
                    float cx = mLastTouchX0 + mLastTouchX1;
                    float cy = mLastTouchY0 + mLastTouchY1;
                    dx = (x0 + x1 - cx) / 2;
                    dy = (y0 + y1 - cy) / 2;
                    tscalex = Math.abs((mLastTouchX1 - mLastTouchX0) / (x1 - x0));
                    tscaley = Math.abs((mLastTouchY1 - mLastTouchY0) / (y1 - y0));
                }

                float scalex = (down_max_x - down_min_x) / getWidth();
                float scaley = (down_max_y - down_min_y) / getHeight();
                float ycenter = (down_max_y + down_min_y) / 2 + dy * scaley;
                float xcenter = (down_max_x + down_min_x) / 2 - dx * scalex;
                float yoff = tscaley * (down_max_y - down_min_y) / 2;
                float xoff = tscalex * (down_max_x - down_min_x) / 2;
                min_y = ycenter - yoff;
                max_y = ycenter + yoff;
                min_x = xcenter - xoff;
                max_x = xcenter + xoff;
                calPlot();

                invalidate();
                break;
            }
            case MotionEvent.ACTION_UP: {
                mLastTouchX0 = Float.NaN;
                mLastTouchX1 = Float.NaN;
            }
        }

        return true;
    }

    public void plot(CalcEngine.Symbolic equ) {
        resetPlot();
        mEquation = equ;
        getYRange();

        mTicks.calcRangeTicks(getWidth(), getHeight());
        calPlot();
    }

    public CalcEngine.Symbolic getPlot() {
        return mEquation;
    }

    private boolean isFinite(double x) {
        return !(Double.isNaN(x) || Double.isInfinite(x));
    }

    private void calPlot() {
        plot.reset();
        float step = (float) ((max_x - min_x) / NPOINTS);
        for (int i = 0; i < NPOINTS; i++) {
            float x = step * i + min_x;
            double y = getY(x);
            if (isFinite(y)) {
                plot.addPoint(x, (float) y);
            }
        }
    }

    private double getY(double x) {
        return mEquation.eval(x, 0, mTmpStack);
    }

    void getYRange() {
        int samples = NPOINTS;
        double step = (max_x - min_x) / samples;
        double[] v = new double[samples + 1];
        double miny = Double.MAX_VALUE, maxy = -Double.MAX_VALUE;
        mTmpStack.clear();
        int count = 0;
        for (int i = 0; i < NPOINTS; i++) {
            double x = step * i + min_x;
            double y = getY(x);
            if (isFinite(y)) {
                v[count++] = y;
                miny = Math.min(miny, y);
                maxy = Math.max(maxy, y);
            }
        }
        int[] bins = new int[20];

        for (int j = 0; j < count; j++) {
            bins[(int) (0.5 + (bins.length - 1) * (v[j] - miny) / (maxy - miny))]++;
        }

        int mode = 0;
        for (int i = 0; i < bins.length; i++) {
            if (bins[i] > bins[mode]) {
                mode = i;
            }
        }
        int high = mode, low = mode;
        for (int total = bins[mode]; total < (count * 4) / 5; ) {
            if (low > 0 && high < bins.length - 1) {
                if (bins[low - 1] >= bins[high + 1]) {
                    total += bins[--low];
                } else {
                    total += bins[++high];
                }
            } else if (low > 0) {
                total += bins[--low];
            } else if (high < bins.length - 1) {
                total += bins[++high];
            } else {
                break;
            }
        }

        double fmin = miny + (maxy - miny) * (low) / (float) bins.length;
        double fmax = miny + (maxy - miny) * (high + 1) / (float) bins.length;
        double range = fmax - fmin;

        fmin -= range * .2;
        fmax += range * .2;

        max_y = (float) fmax;
        min_y = (float) fmin;
    }

    public void resetPlot() {
        plot.reset();
        min_y = -1;
        max_y = 1;
        min_x = DEF_MIN_X;
        max_x = DEF_MAX_X;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getWidth();
        int h = getHeight();
        mTicks.calcRangeTicks(w, h);

        for (DrawItem drawItem : myDrawItems) {
            drawItem.paint(canvas, mMargins, w, h);
        }
    }

    interface DrawItem {
        public boolean paint(Canvas c, Margins m, int w, int h);
    }

    // =======================================  graph ======================================
    class Plot implements DrawItem {
        Path path = new Path();
        Paint paint = new Paint();
        Paint vpaint = new Paint();
        float[] mValueY = new float[1000];
        float[] mValueX = new float[mValueY.length];
        int number_of_points = 0;

        public void reset() {
            number_of_points = 0;
        }

        {
            number_of_points = 100;
            for (int i = 0; i < number_of_points; i++) {
                float y = i / (float) (number_of_points);
                mValueY[i] = y * y;
                mValueX[i] = i;
            }

            paint.setStrokeWidth(4);
            paint.setColor(0xFF0000FF);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStyle(Paint.Style.STROKE);

            vpaint.setStrokeWidth(4);
            vpaint.setColor(0xFF0000FF);
            vpaint.setStrokeJoin(Paint.Join.ROUND);
            vpaint.setStrokeCap(Paint.Cap.ROUND);
            vpaint.setStyle(Paint.Style.STROKE);
            vpaint.setPathEffect(new DashPathEffect(new float[]{20f, 20f}, 0f));

        }

        public void addPoint(float x, float y) {
            mValueY[number_of_points] = y;
            mValueX[number_of_points] = x;
            number_of_points++;
        }

        @Override
        public boolean paint(Canvas c, Margins m, int w, int h) {
             if (number_of_points == 0) {
                return false;
            }

            int cw = w - m.myInsLeft - m.myInsRight;
            int ch = h - m.myInsTop - m.myInsBottom;
            float maxTime = mValueX[number_of_points - 1];
            float offx = m.myInsLeft;
            float scalex = cw / (float) maxTime;
            boolean windowed = false;

            scalex = cw / (max_x - min_x);

            offx = m.myInsLeft - scalex * (maxTime - (max_x - min_x));
            windowed = true;

            float scaley, offy;
            scaley = -ch / (max_y - min_y);
            offy = h - m.myInsBottom;
            path.reset();
            float x = 0, y = 0;
            for (int i = 0; i < number_of_points; i++) {
                y = (mValueY[i] - min_y) * scaley + offy;
                x = mValueX[i] * scalex + offx;
                if (i == 0) {
                    paint.setColor(0xFFF88800);
                    path.moveTo(x, y);
                } else {
                    path.lineTo(x, y);

                }
            }
            paint.setStyle(Paint.Style.STROKE);
            c.drawPath(path, paint);
            paint.setStyle(Paint.Style.FILL);
            int s = 10;
            return windowed;
        }
    }

    // ============================ axis =================================
    DrawItem mAxis = new DrawItem() {
        DecimalFormat df = new DecimalFormat("##.0");
        Paint mAxisPaint = new Paint();

        {
            mAxisPaint.setTextSize(48);
            mAxisPaint.setStrokeWidth(4);
        }

        @Override
        public boolean paint(Canvas c, Margins m, int w, int h) {

            int xpos = (int) (m.myInsLeft + (w - m.myInsLeft - m.myInsRight) * (0 - min_x) / (max_x - min_x));
            c.drawLine(xpos, m.myInsTop, xpos, h - m.myInsBottom, mAxisPaint);
            int ypos = h - m.myInsBottom;
            if (min_y != 0.0f) {
                ypos = (int) (h - m.myInsBottom - (h - m.myInsBottom - m.myInsTop) * (0 - min_y) / (max_y - min_y));
            }

            float mt;
            String s;
            c.drawLine(m.myInsLeft, ypos, w - m.myInsRight, ypos, mAxisPaint);
            s = df.format(max_y);
            mt = 8 + mAxisPaint.measureText(s);

            c.drawText(s, m.myInsLeft - mt, m.myInsTop + 10, mAxisPaint);
            s = df.format(min_y);
            mt = 8 + mAxisPaint.measureText(s);

            c.drawText(s, m.myInsLeft - mt, h - m.myInsBottom, mAxisPaint);
            String label = "y";
            mt = 8 + mAxisPaint.measureText(label);
            c.drawText(label, m.myInsLeft - mt, (h - m.myInsBottom - m.myInsTop) / 2 + m.myInsTop, mAxisPaint);
            return false;
        }
    };

    static class Ticks implements DrawItem {
        float myActualMinx, myActualMiny, myActualMaxx, myActualMaxy;
        float myLastMinx, myLastMiny, myLastMaxx, myLastMaxy;
        float myMinx, myMiny, myMaxx, myMaxy;
        float myTickX;
        float myTickY;
        int myTextGap = 2;

        void calcRangeTicks(int width, int height) {
            double dx = myActualMaxx - myActualMinx;
            double dy = myActualMaxy - myActualMiny;
            int sw = width;
            int sh = height;

            double border = 1.09345;

            if (Math.abs(myLastMinx - myActualMinx)
                    + Math.abs(myLastMaxx - myActualMaxx) > 0.1 * (myActualMaxx - myActualMinx)) {
                myTickX = (float) calcTick(sw, dx);
                dx = myTickX * Math.ceil(border * dx / myTickX);
                double tx = (myActualMinx + myActualMaxx - dx) / 2;
                tx = myTickX * Math.floor(tx / myTickX);
                myMinx = (float) tx;
                tx = (myActualMinx + myActualMaxx + dx) / 2;
                tx = myTickX * Math.ceil(tx / myTickX);
                myMaxx = (float) tx;

                myLastMinx = myActualMinx;
                myLastMaxx = myActualMaxx;
            }
            if (Math.abs(myLastMiny - myActualMiny)
                    + Math.abs(myLastMaxy - myActualMaxy) > 0.1 * (myActualMaxy - myActualMiny)) {
                myTickY = (float) calcTick(sh, dy);
                dy = myTickY * Math.ceil(border * dy / myTickY);
                double ty = (myActualMiny + myActualMaxy - dy) / 2;
                ty = myTickY * Math.floor(ty / myTickY);
                myMiny = (float) ty;
                ty = (myActualMiny + myActualMaxy + dy) / 2;
                ty = myTickY * Math.ceil(ty / myTickY);
                myMaxy = (float) ty;

                myLastMiny = myActualMiny;
                myLastMaxy = myActualMaxy;
            }

            // TODO: cleanup
//            myMinx = 0;
//            myMiny = 0;
//            myMaxx = 1;
//            myMaxy = 1;
        }

        public void rangeReset() {
            myActualMinx = Float.MAX_VALUE;
            myActualMiny = Float.MAX_VALUE;
            myActualMaxx = -Float.MAX_VALUE;
            myActualMaxy = -Float.MAX_VALUE;
        }

        public void rangeIncludePoint(float x, float y) {
            myActualMinx = Math.min(myActualMinx, x);
            myActualMiny = Math.min(myActualMiny, y);
            myActualMaxx = Math.max(myActualMaxx, x);
            myActualMaxy = Math.max(myActualMaxy, y);
        }

        static private double calcTick(int scr, double range) {
            int aprox_x_ticks = scr / 50;
            int type = 1;
            double best = Math.log10(range / ((double) aprox_x_ticks));
            double n = Math.log10(range / ((double) aprox_x_ticks * 2));
            if (frac(n) < frac(best)) {
                best = n;
                type = 2;
            }
            n = Math.log10(range / (aprox_x_ticks * 5));
            if (frac(n) < frac(best)) {
                best = n;
                type = 5;
            }
            return type * Math.pow(10, Math.floor(best));
        }

        static private double frac(double x) {
            return x - Math.floor(x);
        }

        DecimalFormat df = new DecimalFormat("###.#");
        Paint mPaint = new Paint();
        Rect bounds = new Rect();

        @Override
        public boolean paint(Canvas c, Margins m, int w, int h) {

            int draw_width = w - m.myInsLeft - m.myInsRight;
            float e = 0.0001f * (myMaxx - myMinx);
            Paint.FontMetrics fm = mPaint.getFontMetrics();
            float ascent = fm.ascent;

            for (float i = myMinx; i <= myMaxx + e; i += myTickX) {
                int ix = (int) (draw_width * (i - myMinx) / (myMaxx - myMinx) + m.myInsLeft);
                c.drawLine(ix, m.myInsTop, ix, h - m.myInsBottom, mPaint);
                String str = df.format(i);
                mPaint.getTextBounds(str, 0, str.length(), bounds);
                int sw = bounds.width();

                c.drawText(str, ix - sw, h - m.myInsBottom + ascent + myTextGap, mPaint);
            }
            int draw_height = h - m.myInsTop - m.myInsLeft;
            e = 0.0001f * (myMaxy - myMiny);
            float hightoff = -fm.descent + fm.ascent / 2 + ascent;
            for (float i = myMiny; i <= myMaxy + e; i += myTickY) {
                int iy = (int) (draw_height * (1 - (i - myMiny) / (myMaxy - myMiny)) + m.myInsTop);
                c.drawLine(m.myInsLeft, iy, w - m.myInsRight, iy, mPaint);
                String str = df.format(i);
                mPaint.getTextBounds(str, 0, str.length(), bounds);
                int sw = bounds.width();

                c.drawText(str, m.myInsLeft - sw - myTextGap, iy + hightoff, mPaint);

            }
            return false;
        }

    }
    public String getEquation() {
        if (mEquation == null) {
            return "sin(x)/x";
        }
        return mEquation.toString();
    }
}
