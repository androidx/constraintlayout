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
package android.support.constraint.calc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.constraintlayout.motion.widget.Debug;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class Graph2D extends View {
    private static final String TAG = "Graph2D";
    Margins mMargins = new Margins();
    Vector<DrawItem> myDrawItems = new Vector<DrawItem>();
    private static final int NPOINTS = 500;
    Plot mPlot = new Plot();
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
    static final long TRIGGER_DELAY = 1000;
    long mLastMove;
    Handler handler;
    private boolean mAnimated = false;
    double mStartTime = 0;

    private Runnable mCallback = new Runnable() {
        @Override
        public void run() {
            long delay = System.currentTimeMillis() - mLastMove;
            if (delay > TRIGGER_DELAY) {
                mPlot.highlightInterestingPoints(mEquation);
                invalidate();
            }

        }
    };

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
        myDrawItems.add(mPlot);
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
                if (handler == null) {
                    handler = new Handler(Looper.getMainLooper());
                }
                handler.removeCallbacks(mCallback);
                handler.postDelayed(mCallback, 1000);
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
        mAnimated =  4 == (equ.dimensions()&4);
        mTicks.calcRangeTicks(getWidth(), getHeight());
        calPlot();
        mStartTime = getTime(0);
    }

     private double getTime(double last) {
        return  System.nanoTime()*1E-9 -last;
     }

    public CalcEngine.Symbolic getPlot() {
        return mEquation;
    }

    private boolean isFinite(double x) {
        return !(Double.isNaN(x) || Double.isInfinite(x));
    }

    private void calPlot() {
        mPlot.reset();
        double time = getTime(mStartTime);
        float step = (float) ((max_x - min_x) / NPOINTS);
        for (int i = 0; i < NPOINTS; i++) {
            float x = step * i + min_x;
            double y = getY(x,time);
            if (isFinite(y)) {
                mPlot.addPoint(x, (float) y);
            }
        }
    }

    private double getY(double x, double time) {
        return mEquation.eval(mTmpStack, 0, x, time);
    }

    void getYRange() {
        int samples = NPOINTS;
        double step = (max_x - min_x) / samples;
        double[] v = new double[samples + 1];
        double miny = Double.MAX_VALUE, maxy = -Double.MAX_VALUE;
        mTmpStack.clear();
        int count = 0;
        double time = getTime(mStartTime);
        for (int i = 0; i < NPOINTS; i++) {
            double x = step * i + min_x;
            double y = getY(x, time);
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
        mPlot.reset();
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
        if (mAnimated) {
            calPlot();
            invalidate();
        }
    }

    interface DrawItem {
        public boolean paint(Canvas c, Margins m, int w, int h);
    }

    // =======================================  graph ======================================
    class Plot implements DrawItem {
        Path path = new Path();
        Paint paint = new Paint();
        Paint txtPaint = new Paint();
        float[] mValueY = new float[1000];
        float[] mValueX = new float[mValueY.length];
        int number_of_points = 0;
        float keyPointX = Float.NaN;
        float keyPointY = Float.NaN;


        public void reset() {
            number_of_points = 0;
            keyPointX = Float.NaN;
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

            txtPaint.setColor(0xFF0000FF);
            txtPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                    24, getResources().getDisplayMetrics()));

        }

        public void addPoint(float x, float y) {
            mValueY[number_of_points] = y;
            mValueX[number_of_points] = x;
            number_of_points++;
        }

        public void highlightInterestingPoints(CalcEngine.Symbolic mEquation) {
            int crossings = 0;
            int cross = 0;
            float lastP = mValueY[0];
            for (int i = 1; i < number_of_points; i++) {

                if (lastP * mValueY[i] < 0) {
                    cross = i; //if crossing > 1 we wont use anyway
                    crossings++;
                }
                if (mValueY[i] != 0f) {
                    lastP = mValueY[i];
                }
            }
            if (crossings == 1) {
                keyPointX = getCrossingPoint(mEquation, mValueX[cross], mValueX[1] - mValueX[0]);
                keyPointY = Float.NaN;
                return;
            }
            float min = mValueY[0];
            int minI = 0;
            int maxI = 0;
            float max = mValueY[0];
            for (int i = 1; i < number_of_points; i++) {

                if (mValueY[i] > max) {
                    maxI = i;
                    max = mValueY[i];
                } else if (mValueY[i] < min) {
                    minI = i;
                    min = mValueY[i];
                }
            }
            boolean minima = (minI > 10 && minI < number_of_points - 10);
            boolean maxima = (maxI > 10 && maxI < number_of_points - 10);
            if (maxima && minima) {
                return;
            }

            if (maxima) {
                double x = findMax(mEquation, mValueX[maxI], mValueX[1] - mValueX[0]);
                keyPointY = (float) mEquation.eval(mTmpStack, 0, x, 0 );
                keyPointX = (float) x;
                return;
            }
            if (minima) {
                double x = findMin(mEquation, mValueX[minI], mValueX[1] - mValueX[0]);
                keyPointY = (float) mEquation.eval(mTmpStack, 0, x, 0);
                keyPointX = (float) x;
                return;
            }
        }

        private double findMax(CalcEngine.Symbolic eq, float mValueX, float dx) {
            mTmpStack.clear();
            dx /= 10;
            double x = mValueX;
            double y1 = eq.eval(mTmpStack, 0, mValueX, 0);

            double dy;
            do {
                double y2 = eq.eval(mTmpStack, 0, x + dx, 0);
                dy = y2 - y1;
                x = x + dy / dx;
                dx *= 0.9;
                y1 = y2;
            } while (dy > 0.00001);
            return x;
        }

        private double findMin(CalcEngine.Symbolic eq, float mValueX, float dx) {
            mTmpStack.clear();

            dx /= 10;
            double x = mValueX;
            double y1 = eq.eval(mTmpStack, 0, mValueX, 0);

            double dy;
            do {
                double y2 = eq.eval(mTmpStack, 0, x + dx, 0);
                dy = y2 - y1;
                x = x + dy / dx;
                dx *= 0.9;
                y1 = y2;
            } while (dy > 0.00001);
            return x;
        }

        private float getCrossingPoint(CalcEngine.Symbolic eq, float x, double dx) {
            mTmpStack.clear();
            double x1 = x - dx;
            double x2 = x;
            double error = 0.00001;
            while (true) {
                double y1 = eq.eval(mTmpStack, 0, x1, 0);
                double y2 = eq.eval(mTmpStack, 0, x2, 0);
                if (Math.abs(y1) < error) {
                    return (float) x1;
                }
                if (Math.abs(y2) < error) {
                    return (float) x2;
                }
                if (Double.isNaN(y2) || Double.isNaN(y1)) {
                    return Float.NaN;
                }
                Log.v(TAG, Debug.getLoc() + "zerro   " + x1 + "," + y1 + " - " + x2 + "," + y2);

                double shift = -0.5 * y1 * (x2 - x1) / (y2 - y1);
                x1 = x1 + shift;
                x2 = x1 + Math.abs(x2 - x1) / 2 + 0.000001;
            }
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

            if (!Float.isNaN(keyPointX)) {
                Rect tmpr = new Rect();
                DecimalFormat df = new DecimalFormat("#.###");
                String zpoint = df.format(keyPointX);
                if (!Float.isNaN(keyPointY)) {
                    zpoint += " , " + df.format(keyPointY);
                }
                float ky = Float.isNaN(keyPointY) ? 0 : keyPointY;
                y = (ky - min_y) * scaley + offy;
                x = keyPointX * scalex + offx;
                Paint.FontMetrics metrics = txtPaint.getFontMetrics();
                txtPaint.getTextBounds(zpoint, 0, zpoint.length(), tmpr);
                if (!Float.isNaN(keyPointY)) {
                    RectF rectF = new RectF();
                    float size = 5;
                    rectF.set(x - size, y - size, x + size, y + size);
                    c.drawRoundRect(rectF, size * 2, size * 2, txtPaint);

                    x -= tmpr.width() / 2;
                }
                float ty = (y > h / 2) ? y - metrics.descent - 2 : y - metrics.ascent + 2;
                c.drawText(zpoint, x, ty, txtPaint);
            }

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

    public void serialize(ObjectOutputStream stream) throws IOException {
        getPlot().serialize(stream);
        stream.writeFloat(min_x);
        stream.writeFloat(max_x);
        stream.writeFloat(min_y);
        stream.writeFloat(max_y);
    }

    public void deserializeSymbolic(CalcEngine.Symbolic sym, ObjectInputStream stream) throws IOException, ClassNotFoundException {
        plot(sym);
        min_x = stream.readFloat();
        max_x = stream.readFloat();
        min_y = stream.readFloat();
        max_y = stream.readFloat();
        mTicks.calcRangeTicks(getWidth(), getHeight());
        calPlot();
    }
}
