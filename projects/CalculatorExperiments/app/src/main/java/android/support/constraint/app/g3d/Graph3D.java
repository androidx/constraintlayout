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
package android.support.constraint.app.g3d;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.constraint.app.CalcEngine;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.constraintlayout.motion.widget.Debug;
import androidx.core.view.GestureDetectorCompat;

public class Graph3D extends View {
    static String TAG = "CubeView";
    SurfaceGen surfaceGen = new SurfaceGen();
    private Bitmap Image;
    private int[] mImageBuff;
    Paint mPaint;
    Paint mPaintSidebar;
    int mGraphType = 1;
    private float mLastTouchX0 = Float.NaN;
    private float mLastTouchY0;
    private float mLastTouchX1 = Float.NaN;
    private float mLastTouchY1;
    private float mLastTrackBasllX;
    private float mLastTrackBasllY;
    double mDownScreeenWidth;
    private CalcEngine.Symbolic mEquation;

    public Graph3D(Context context) {
        super(context);
        init();
    }

    public Graph3D(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public Graph3D(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        surfaceGen.calcSurface(-20, 20, -20, 20, true, (x, y) -> {
            double d = Math.sqrt(x * x + y * y);
            return 10 * ((d == 0) ? 1f : (float) (Math.sin(d) / d));
        });
    }

    public void plot(CalcEngine.Symbolic s) {
        CalcEngine.Stack mTmpStack = new CalcEngine.Stack();
        long time = System.nanoTime();
        mEquation = s;
        surfaceGen.setZoomZ(1);
        surfaceGen.calcSurface(-6, 6, -6, 6, true, (x, y) -> {
            float v = (float) s.eval(x, y, mTmpStack);
            return v;
        });
        setUpMatrix();
        invalidate();
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        super.onSizeChanged(xNew, yNew, xOld, yOld);
        if (xNew == 0 || yNew == 0 ){
            return;
        }
        Image = Bitmap.createBitmap(xNew, yNew, Bitmap.Config.ARGB_8888);
        mImageBuff = new int[xNew * yNew];
        surfaceGen.setScreenDim(xNew, yNew, mImageBuff, 0x00000099);
    }

    GestureDetectorCompat mGesture = new GestureDetectorCompat(this.getContext(), new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            mGraphType = (mGraphType + 1) % 3;
            invalidate();
            return super.onDoubleTap(e);
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {
            surfaceGen.rescaleRnge();
            setUpMatrix();
            invalidate();
        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }
    });
    static final int TOUCH_MODE_NONE = 0;
    static final int TOUCH_MODE_ONE = 0;
    static final int TOUCH_MODE_TWO= 0;
    int touch_mode  = TOUCH_MODE_NONE;
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mGesture.onTouchEvent(ev);
        int touchCount = ev.getPointerCount();
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                if (touchCount == 1) {
                    touch_mode = 1;
                } else if (touchCount == 2) {
                    touch_mode = 2;
                }
                if (ev.getX() < getWidth() / 10) {
                    zscaleDown(ev);
                }
                mDownScreeenWidth = surfaceGen.getScreenWidth();

                if (!Float.isNaN(mLastTouchX0)) {
                    mLastTouchX1 = ev.getX(0);
                    mLastTouchY1 = ev.getY(0);

                    surfaceGen.panDown((mLastTouchX1 + mLastTouchX0) / 2, (mLastTouchY1 + mLastTouchY0) / 2);
                    break;
                }
                if (touchCount == 2) {
                    mLastTouchX1 = ev.getX(1);
                    mLastTouchY1 = ev.getY(1);
                    mDownScreeenWidth = surfaceGen.getScreenWidth();

                    break;
                }

                mLastTouchX0 = ev.getX(0);
                mLastTouchY0 = ev.getY(0);
                surfaceGen.trackBallDown(mLastTouchX0, mLastTouchY0);
                mLastTrackBasllX = mLastTouchX0;
                mLastTrackBasllY = mLastTouchY0;
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                dprintev("MOVE", ev);
                if (touch_mode == 2 && ev.getPointerCount() == 1){
                    return true;
                }
                if (mZscaleMode) {
                    zscaleMove(ev);
                    return true;
                }
                if (Float.isNaN(mLastTouchX1) && Float.isNaN(mLastTouchX0)) {

                    break;
                }

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
                    float tx = ev.getX(0);
                    float ty = ev.getY(0);
                    float moveX = (mLastTrackBasllX - tx);
                    float moveY = (mLastTrackBasllY - ty);
                    if (moveX * moveX + moveY * moveY < 4000f) {
                        surfaceGen.trackBallMove(tx, ty);

                    } else {
                        Log.v(TAG, Debug.getLoc() + " reject move " + moveX + " , " + moveY);
                    }
                    mLastTrackBasllX = tx;
                    mLastTrackBasllY = ty;
                    invalidate();

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

                    double scale = Math.hypot(mLastTouchX1 - mLastTouchX0, mLastTouchY1 - mLastTouchY0) / Math.hypot((x1 - x0), (y1 - y0));

                    surfaceGen.panMove(dx, dy);

                    surfaceGen.setScreenWidth(scale * mDownScreeenWidth);

                }

                invalidate();
                break;
            }
            case MotionEvent.ACTION_UP: {
                if (mZscaleMode) {
                    zscaleUp(ev);
                    return true;
                }
                mLastTouchX0 = Float.NaN;
                mLastTouchX1 = Float.NaN;

            }
        }

        return true;
    }

    private void dprintev(String type, MotionEvent ev) {
        int c = ev.getPointerCount();
        String str = type;
        for (int i = 0; i < c; i++) {
            str += "(" + (int)ev.getX(i) + "," + (int) ev.getY(i) + ")";

        }
    }

    float mDownZoomZ;
    float mDownY;
    boolean mZscaleMode = false;

    private void zscaleDown(MotionEvent ev) {
        mZscaleMode = true;
        mDownZoomZ = surfaceGen.getZoomZ();
        mDownY = ev.getY();
        invalidate();
    }

    private void zscaleMove(MotionEvent ev) {
        float dz = ev.getY() - mDownY;
        surfaceGen.setZoomZ(mDownZoomZ - dz / getHeight());
        invalidate();
    }

    private void zscaleUp(MotionEvent ev) {
        mZscaleMode = false;
        invalidate();
    }

    void setUpMatrix() {
        surfaceGen.setUpMatrix(getWidth(), getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        if (mPaint == null) {
            mPaint = new Paint();
            mPaintSidebar = new Paint();
            mPaintSidebar.setColor(0x556633FF);
        }
        if (surfaceGen.notSetUp()) {
            surfaceGen.setUpMatrix(getWidth(), getHeight());
        }

        int w = getWidth();
        int h = getHeight();

        surfaceGen.render(mGraphType);

        Image.setPixels(mImageBuff, 0, w, 0, 0, w, h);
        canvas.drawBitmap(Image, 0, 0, mPaint);
        if (mZscaleMode) {
            canvas.drawRect(0, 0, getWidth() / 10, getHeight(), mPaintSidebar);
        }

    }
    public CalcEngine.Symbolic getPlot() {
        return mEquation;
    }
    public String getEquation() {
        if (mEquation == null) {
            return "default sync function";
        }
        return mEquation.toString();
    }
}
