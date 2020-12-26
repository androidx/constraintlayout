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
    private Bitmap image;
    private int[] imgbuff;
    Paint paint;
    int mGraphType = 1;
    private float mLastTouchX0 = Float.NaN;
    private float mLastTouchY0;
    private float mLastTouchX1 = Float.NaN;
    private float mLastTouchY1;
    private double mLastScale = 1;

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
        Log.v(TAG, Debug.getLoc() + " calcSurface ");

        surfaceGen.calcSurface(-20, 20, -20, 20, (x, y) -> {
             double d =  Math.sqrt(x * x + y * y);
            return 10*((d == 0)?1f :(float) (Math.sin(d) / d));
        });
    }


    public void plot(CalcEngine.Symbolic s) {
        CalcEngine.Stack mTmpStack = new CalcEngine.Stack();
        long time = System.nanoTime();
        surfaceGen.calcSurface(-6, 6, -6, 6, (x, y) -> {
            float v =  (float) s.eval(x, y, mTmpStack);
             return v;
        });
        setUpMatrix();
        Log.v(TAG, Debug.getLoc() + " " +(System.nanoTime()-time)*1E-6f);
        invalidate();
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        super.onSizeChanged(xNew, yNew, xOld, yOld);
        image = Bitmap.createBitmap(xNew,yNew, Bitmap.Config.ARGB_8888);
        imgbuff = new int[xNew*yNew];
        surfaceGen.setScreenDim(xNew, yNew, imgbuff, 0x00000099);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        float x = event.getX();
//        float y = event.getY();
//        int action = event.getAction();
//        if (action == MotionEvent.ACTION_DOWN) {
//            surfaceGen.trackBallDown(x, y);
//
//
//        } else if (action == MotionEvent.ACTION_MOVE) {
//            surfaceGen.trackBallMove(x, y);
//            invalidate();
//
//        } else if (action == MotionEvent.ACTION_UP) {
//            surfaceGen.trackBallUP(x, y);
//        }
//        return true;
//    }
    GestureDetectorCompat mGesture = new GestureDetectorCompat(this.getContext(), new GestureDetector.SimpleOnGestureListener() {

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        mGraphType = (mGraphType+1)%3;
        invalidate();
        return super.onDoubleTap(e);
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }


    @Override
    public void onLongPress(MotionEvent motionEvent) {
        Log.v(TAG,Debug.getLoc());
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }
});
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mGesture.onTouchEvent(ev);
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mLastScale = 1;
                if (!Float.isNaN(mLastTouchX0)) {
                    mLastTouchX1 = ev.getX(0);
                    mLastTouchY1 = ev.getY(0);
                    surfaceGen.panDown((mLastTouchX1+mLastTouchX0)/2,(mLastTouchY1+mLastTouchY0)/2);
                    break;
                }
                if (ev.getPointerCount() == 2) {
                    mLastTouchX1 = ev.getX(1);
                    mLastTouchY1 = ev.getY(1);

                    break;
                }
                mLastTouchX0 = ev.getX(0);
                mLastTouchY0 = ev.getY(0);
                surfaceGen.trackBallDown(mLastTouchX0, mLastTouchY0);
                 break;
            }

            case MotionEvent.ACTION_MOVE: {
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
                    surfaceGen.trackBallMove(ev.getX(0), ev.getY(0));
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
                    tscalex = Math.abs((x1 - x0)/(mLastTouchX1 - mLastTouchX0)  );
                    tscaley = Math.abs( (y1 - y0)/(mLastTouchY1 - mLastTouchY0));

                    surfaceGen.panMove(dx,dy);
                    double scale = 1/Math.hypot(tscalex,tscaley);
                    surfaceGen.zoom(scale/mLastScale-1);
                    mLastScale = scale;
                }


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




    void setUpMatrix() {
        Log.v(TAG, Debug.getLoc() + " w,h "+getWidth()+" , "+ getHeight());
        surfaceGen.setUpMatrix(getWidth(), getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        if (paint == null) {
            paint = new Paint();
        }
        if (surfaceGen.notSetUp()) {
            surfaceGen.setUpMatrix(getWidth(), getHeight());
        }

        int w = getWidth();
        int h = getHeight();
        long time = System.nanoTime();

        surfaceGen.render(mGraphType);

        image.setPixels(imgbuff,0,w,0,0,w,h);
        canvas.drawBitmap(image,0,0,paint);
        time = System.nanoTime()-time;
     }

}
