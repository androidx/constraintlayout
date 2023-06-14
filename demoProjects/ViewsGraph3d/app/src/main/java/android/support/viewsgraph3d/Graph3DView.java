package android.support.viewsgraph3d;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.constraintLayout.extlib.graph3d.Graph;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Graph3DView extends View {
    private boolean animated;

    class ImageInterface implements Graph.ImageSupport {
        Bitmap bitmap;
        int[] data;

        @Override
        public void makeImage(int w, int h) {
            bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            data = new int[w * h];
        }

        @Override
        public int[] getBacking() {
            return data;
        }

        Bitmap getImage() {
            bitmap.setPixels(data, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
            return bitmap;
        }
    }

    ImageInterface image = new ImageInterface();
    Graph graph = new Graph(image);
    Paint paint = new Paint();

    public Graph3DView(Context context) {
        super(context);
        init(context, null);

    }

    public Graph3DView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);

    }

    public Graph3DView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    void init(Context context, AttributeSet attr) {
        addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                graph.resize(right - left, bottom - top);
            }
        });
    }
    GestureDetector gd = new GestureDetector(getContext(), new SimpleOnGestureListener(){
        @Override
        public boolean onDoubleTap(@NonNull MotionEvent e) {
            toggleAnimation();
            return super.onDoubleTap(e);
        }
    });
    void toggleAnimation() {
        animated = !animated;
        if (!animated) {
            return;
        }
        graph.setStartTime();
        graph.buildSurface(Graph.BLACK_HOLE_MERGE);
        postDelayed(this::tick,8);
    }
    public void tick() {
        graph.tick(System.nanoTime());
        invalidate();
        if (animated) {
            postDelayed(this::tick,8);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gd.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                graph.trackDown(event.getX(), event.getY());
                return true;
            case MotionEvent.ACTION_UP:
                graph.trackDone();
                return true;
            case MotionEvent.ACTION_MOVE:
                graph.trackDrag(event.getX(), event.getY());
                invalidate();
                return true;
        }
        return false;
    }

    int count = 0;
    long last = System.nanoTime();
    @Override
    protected void onDraw(Canvas canvas) {
        graph.render();
        canvas.drawBitmap(image.getImage(), 0, 0, paint);
        count++;
        long now = System.nanoTime();
        if ((now-last)>1000000000L) {
            int w = getWidth();
            int h = getHeight();
            System.out.println(w+"x "+h+" fps:"+(count/((now-last)*1E-9f)));
            last = now;
            count =0;
        }
    }
}
