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

package android.support.drag2d;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.drag2d.lib.Velocity2D;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new BallMover(getApplicationContext()));
    }

    static class BallMover extends View {
        Drawable ball;
        VelocityTracker velocityTracker = VelocityTracker.obtain();
        Velocity2D velocity2D = new Velocity2D();
        int ballX;
        int ballY;
        int ballW = 64;
        int ballH = 64;

        float[] points = new float[10000];
        Paint paint = new Paint();


        public BallMover(Context context) {
            super(context);
            setup(context);
        }

        public BallMover(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            setup(context);
        }

        public BallMover(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            setup(context);
        }


        void setup(Context context) {
            ball = ResourcesCompat.getDrawable(context.getResources(), R.drawable.volleyball, null);
            paint.setStrokeWidth(3);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (startAnimationTime != 0) {

                long timeMillis = SystemClock.uptimeMillis() - startAnimationTime;
                float time = timeMillis / 1000f;
                ballX = (int) velocity2D.getX(time);
                ballY = (int) velocity2D.getY(time);

                if (velocity2D.isStillMoving(time)) {
                    invalidate();
                } else {
                    startAnimationTime = 0;
                }
                canvas.drawLines(points, paint);
            }
            ball.setBounds(ballX, ballY, ballW + ballX, ballH + ballY);
            ball.draw(canvas);
            ball.setBounds(getWidth() / 2, getHeight() / 2, ballW + getWidth() / 2, ballH + getHeight() / 2);
            ball.draw(canvas);


        }

        float touchDownX;
        float touchDownY;
        float touchDeltaX, touchDeltaY;
        int ballDownX, ballDownY;
        long startAnimationTime;


        @Override
        public boolean onTouchEvent(MotionEvent event) {
            velocityTracker.addMovement(event);
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    System.out.println("------- down ---------");

                    startAnimationTime = 0;
                    touchDownX = event.getX();
                    touchDownY = event.getY();
                    ballDownX = ballX;
                    ballDownY = ballY;
                    break;

                case MotionEvent.ACTION_MOVE:
                    System.out.println("------- move ---------");

                    touchDeltaX = event.getX() - touchDownX;
                    touchDeltaY = event.getY() - touchDownY;
                    ballX = (int) (ballDownX + touchDeltaX);
                    ballY = (int) (ballDownY + touchDeltaY);
                    invalidate();

                    break;
                case MotionEvent.ACTION_UP:
                    System.out.println("------- UP ---------");

                    velocityTracker.computeCurrentVelocity(1000);
                    float velocityX = velocityTracker.getXVelocity();
                    float velocityY = velocityTracker.getYVelocity();
                    System.out.println("initial velocity " + velocityX + "," + velocityY);
                    startAnimationTime = event.getEventTime();
                    velocity2D.configure(ballX, ballY, velocityX, velocityY, getWidth() / 2, getHeight() / 2, 4, 1000, 1000, null);
                    velocity2D.getCurves(points, getWidth(), getHeight());
                    invalidate();
                    break;

            }
            return true;
        }
    }
}