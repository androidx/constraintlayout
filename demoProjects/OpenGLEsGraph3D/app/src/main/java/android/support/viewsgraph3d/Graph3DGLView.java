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
package android.support.viewsgraph3d;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.support.constraintLayout.extlib.graph3d.ViewMatrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.constraintlayout.motion.widget.Debug;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class
Graph3DGLView extends GLSurfaceView {
    public Graph3DGLView(Context context) {
        super(context);
        init(context, null);
    }


    // ================== touch =================
    private float mLastTouchX0 = Float.NaN;
    private float mLastTouchY0;
    private float mLastTrackBallX;
    private float mLastTrackBallY;
    double mDownScreenWidth;
    ViewMatrix mMatrix = new ViewMatrix();

    public void setUpMatrix() {
        int width = getWidth();
        int height = getHeight();
        boolean resetOrientation = true;
        double[] look_point = {0, 0, 0};

        double cameraDistance = 22;

        mMatrix.setLookPoint(look_point);
        if (resetOrientation) {
            double[] eye_point = {look_point[0] - cameraDistance, look_point[1] - cameraDistance, look_point[2] + cameraDistance};
            mMatrix.setEyePoint(eye_point);
            double[] up_vector = {0, 0, 1};
            mMatrix.setUpVector(up_vector);
        } else {
            mMatrix.fixUpPoint();
        }
        double screenWidth = cameraDistance * 2;
        mMatrix.setScreenWidth(screenWidth);
        mMatrix.setScreenDim(width, height);
        mMatrix.calcMatrix();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                trackDown(event.getX(), event.getY());
                return true;
            case MotionEvent.ACTION_UP:
                trackDone();
                return true;
            case MotionEvent.ACTION_MOVE:
                trackDrag(event.getX(), event.getY());
                return true;
        }
        return true;
    }

    public void trackDown(float x, float y) {
        mDownScreenWidth = getWidth();
        mLastTouchX0 = x;
        mLastTouchY0 = y;
        mMatrix.trackBallDown(mLastTouchX0, mLastTouchY0);
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
            mMatrix.trackBallMove(x, y);
        }
        mLastTrackBallX = tx;
        mLastTrackBallY = ty;
    }

    public void trackDone() {
        mLastTouchX0 = Float.NaN;
        mLastTouchY0 = Float.NaN;
    }

    // ========================= end touch ====================
    public Graph3DGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        setRenderer(new DemoRenderer());
    }

    public static SurfaceGL DEFAULT = new SurfaceGL((x, y, t) -> {
        double d = Math.sqrt(x * x + y * y);
        return (float) Math.cos(d * 2);
        // return 0.3f * (float) (Math.cos(d) * (y * y - x * x) / (1 + d));
    });

    public static final SurfaceGL BLACK_HOLE_MERGE = new SurfaceGL((x, y, t) -> {
        float d = (float) Math.sqrt(x * x + y * y);
        float d2 = (float) Math.pow(x * x + y * y, 0.125);
        float angle = (float) Math.atan2(y, x);
        float s = (float) Math.sin(d + angle - t * 5);
        float s2 = (float) Math.sin(t);
        float c = (float) Math.cos(d + angle - t * 5);
        return (s2 * s2 + 0.1f) * d2 * 5 * (s + c) / (1 + d * d / 20);
        //  return  (float) (s*s+0.1) * (float) (Math.cos(d-time*5) *(y*y-x*x) /(1+d*d));
    });

    public class DemoRenderer implements Renderer {
        long nanoTime = System.nanoTime();
        float time;
        private SurfaceGL surface = BLACK_HOLE_MERGE;
        //private SurfaceGL surface = DEFAULT;

        public void setStartTime() {
            nanoTime = System.nanoTime();
        }

        public void tick(long now) {
            time += (now - nanoTime) * 1E-9f;
            nanoTime = now;
            surface.calcSurface(time, false);
            surface.fillBuffers();
        }

        private float rotation;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            setUpMatrix();
            gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
            gl.glClearDepthf(1.0f);
            gl.glEnable(GL10.GL_DEPTH_TEST);
            gl.glDepthFunc(GL10.GL_LEQUAL);
            gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
            setStartTime();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            gl.glViewport(0, 0, width, height);

            look(gl, width, height);
        }


        public void look(GL10 gl, int width, int height) {
            gl.glMatrixMode(GL10.GL_PROJECTION);
            gl.glLoadIdentity();
            GLU.gluPerspective(gl, 60.0f, (float) width / (float) height, 0.1f, 100.0f);

            double[] lp = mMatrix.getLookPoint();
            double[] ep = mMatrix.getEyePoint();
            double[] up = mMatrix.getUpVector();

            GLU.gluLookAt(gl,
                    (float) ep[0], (float) ep[1], (float) ep[2],
                    (float) lp[0], (float) lp[1], (float) lp[2],
                    (float) up[0], (float) up[1], (float) up[2]);
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            look(gl, getWidth(), getHeight());
            gl.glLoadIdentity();
            drawLights(gl);
            surface.draw(gl);
            gl.glLoadIdentity();
            rotation -= 0.15f;
        }

        private void drawLights(GL10 gl) {
            // Point Light
            double[] ep = mMatrix.getEyePoint();
            double[] up = mMatrix.getUpVector();
            double d = -5;
            float lx = (float) (ep[0] + up[0] * d);
            float ly = (float) (ep[1] + up[1] * d);
            float lz = (float) (ep[2] + up[2] * d);
            float[] position = {lx, ly, lz, 1};
            float[] diffuse = {.6f, .6f, .9f, 1f};
            float[] specular = {1, 1, 1, 1};
            float[] ambient = {.4f, .4f, .4f, 1};

            gl.glEnable(GL10.GL_LIGHTING);
            gl.glEnable(GL10.GL_LIGHT0);
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();

            gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, position, 0);
            gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, diffuse, 0);
            gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, ambient, 0);
            gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, specular, 0);
            tick(System.nanoTime());
        }
    }


}
