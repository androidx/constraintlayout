package android.support.constraintLayout.extlib.graph3d;


import android.support.constraintLayout.extlib.graph3d.objects.AxisBox;
import android.support.constraintLayout.extlib.graph3d.objects.Surface3D;

public class Graph {
    Scene3D mScene3D = new Scene3D();

    int mGraphType = 2;
    private float mLastTouchX0 = Float.NaN;
    private float mLastTouchY0;
    private float mLastTrackBallX;
    private float mLastTrackBallY;
    double mDownScreenWidth;
    Surface3D mSurface;
    AxisBox mAxisBox;
    float range = 20;
    float minZ = -10;
    float maxZ = 10;
    float mZoomFactor = 1;
    long nanoTime;
    float time = 0;
    int graphWidth;
    int graphHeight;
    ImageSupport image;

    public interface ImageSupport {
        void makeImage(int w, int h);
        int[] getBacking();
    }

    public Graph(ImageSupport image) {
        this.image = image;
        mAxisBox = new AxisBox();
        mAxisBox.setRange(-range, range, -range, range, minZ, maxZ);
        mScene3D.addPostObject(mAxisBox);
        buildSurface(DEFAULT);
        resetCamera();
    }

    public static Surface3D DEFAULT = new Surface3D((x, y, t) -> {
        double d = Math.sqrt(x * x + y * y);
        return 0.3f * (float) (Math.cos(d) * (y * y - x * x) / (1 + d));
    });

    public void buildSurface(Surface3D surface3D) {
        mSurface = surface3D;
        mSurface.setRange(-range, range, -range, range, minZ, maxZ);
        mScene3D.setObject(mSurface);
    }

    public void resetCamera() {
        mScene3D.resetCamera();
    }

    public void setStartTime() {
        nanoTime = System.nanoTime();
    }

    public void tick(long now) {
        time += (now - nanoTime) * 1E-9f;
        nanoTime = now;
        mSurface.calcSurface(time, false);
        mScene3D.update();
    }

    public void resize(int width, int height) {
        graphHeight = height;
        graphWidth = width;
        image.makeImage(width, height);
        mScene3D.setScreenDim(width, height, image.getBacking(), 0x00AAAAAA);
    }

    public void trackDown(float x, float y) {
        mDownScreenWidth = mScene3D.getScreenWidth();
        mLastTouchX0 = x;
        mLastTouchY0 = y;
        mScene3D.trackBallDown(mLastTouchX0, mLastTouchY0);
        mLastTrackBallX = mLastTouchX0;
        mLastTrackBallY = mLastTouchY0;
    }

    public void trackDrag(float x, float y) {
        if (Float.isNaN(mLastTouchX0)) {
            return;
        }
        float tx = x;
        float ty = y;
        float moveX = (mLastTrackBallX - tx);
        float moveY = (mLastTrackBallY - ty);
        if (moveX * moveX + moveY * moveY < 4000f) {
            mScene3D.trackBallMove(tx, ty);
        }
        mLastTrackBallX = tx;
        mLastTrackBallY = ty;
    }

    public void trackDone() {
        mLastTouchX0 = Float.NaN;
        mLastTouchY0 = Float.NaN;
    }

    public void wheel(float rotation, boolean control) {
        if (control) {
            mZoomFactor *= (float) Math.pow(1.01, rotation);
            mScene3D.setZoom(mZoomFactor);
            mScene3D.setUpMatrix(graphWidth, graphHeight);
            mScene3D.update();
        } else {
            range = range * (float) Math.pow(1.01, rotation);
            mSurface.setArraySize(Math.min(300, (int) (range * 5)));
            mSurface.setRange(-range, range, -range, range, minZ, maxZ);
            mAxisBox.setRange(-range, range, -range, range, minZ, maxZ);
            mScene3D.update();
        }
    }

    public static final Surface3D BLACK_HOLE_MERGE = new Surface3D((x, y, t) -> {
        float d = (float) Math.sqrt(x * x + y * y);
        float d2 = (float) Math.pow(x * x + y * y, 0.125);
        float angle = (float) Math.atan2(y, x);
        float s = (float) Math.sin(d + angle - t * 5);
        float s2 = (float) Math.sin(t);
        float c = (float) Math.cos(d + angle - t * 5);
        return (s2 * s2 + 0.1f) * d2 * 5 * (s + c) / (1 + d * d / 20);
        //  return  (float) (s*s+0.1) * (float) (Math.cos(d-time*5) *(y*y-x*x) /(1+d*d));
    });

    public void render() {
        if (mScene3D.notSetUp()) {
            mScene3D.setUpMatrix(graphWidth, graphHeight);
        }

        mScene3D.render(mGraphType);
    }

    public void lightMovesWithCamera(boolean doesMove) {
        mScene3D.mLightMovesWithCamera = doesMove;
    }

    public void setSaturation(float sat) {
        mSurface.mSaturation = sat;
        mScene3D.update();
    }
}
