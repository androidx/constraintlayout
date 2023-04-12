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
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.drag2d.lib.MaterialEasing;
import android.support.drag2d.lib.MaterialVelocity;
import android.support.drag2d.lib.Velocity2D;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.button.MaterialButton;


public class MainActivity extends AppCompatActivity {
    int backgroundColor = 0xFF000000|(200*256+250)*256+200;
    static String []sEasingNames = {
            "DECELERATE",
            "LINEAR",
            "OVERSHOOT",
            "EASE_OUT_SINE",
            "EASE_OUT_CUBIC",
            "EASE_OUT_QUINT",
            "EASE_OUT_CIRC",
            "EASE_OUT_QUAD",
            "EASE_OUT_QUART",
            "EASE_OUT_EXPO",
            "EASE_OUT_BACK",
            "EASE_OUT_ELASTIC",
            "EASE_OUT_BOUNCE"
    };
    static MaterialVelocity.Easing[] sEasings = {
            MaterialEasing.DECELERATE,
            MaterialEasing.LINEAR,
            MaterialEasing.OVERSHOOT,
            MaterialEasing.EASE_OUT_SINE,
            MaterialEasing.EASE_OUT_CUBIC,
            MaterialEasing.EASE_OUT_QUINT,
            MaterialEasing.EASE_OUT_CIRC,
            MaterialEasing.EASE_OUT_QUAD,
            MaterialEasing.EASE_OUT_QUART,
            MaterialEasing.EASE_OUT_EXPO,
            MaterialEasing.EASE_OUT_BACK,
            MaterialEasing.EASE_OUT_ELASTIC,
            MaterialEasing.EASE_OUT_BOUNCE
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        LinearLayout row = new LinearLayout(this);
        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);
        ScrollView scrollView = new ScrollView(this);
        BallMover m = new BallMover(this);
        for (int i = 0; i < sEasingNames.length; i++) {
            AppCompatButton b = new AppCompatButton(this);
            b.setText(sEasingNames[i]);
            MaterialVelocity.Easing easing = sEasings[i];
            b.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            b.setPadding(1,1,5,5);
            col.addView(b);
            b.setOnClickListener(c->{m.setEasing(easing);});

        }
        col.setBackgroundColor(backgroundColor);
        scrollView.addView(col);
        row.addView(scrollView);
        row.addView(m);
        setContentView(row);

    }

    static class BallMover extends View {
        Drawable ball;
        VelocityTracker velocityTracker = VelocityTracker.obtain();
        Velocity2D velocity2D = new Velocity2D();
        int ballX;
        int ballY;
        int ballW = 128;
        int ballH = 128;
         MaterialVelocity.Easing easing = null;
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
        public void setEasing(MaterialVelocity.Easing easing) {
            this.easing = easing;
        }
        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawRGB(200,250,200);
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

            ball.setBounds(getWidth() / 2, getHeight() / 2, ballW + getWidth() / 2, ballH + getHeight() / 2);
            ball.setTint(Color.CYAN);
            ball.draw(canvas);
            ball.setBounds(ballX, ballY, ballW + ballX, ballH + ballY);
            ball.setTint(Color.BLACK);
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
                    velocity2D.configure(ballX, ballY,
                            velocityX, velocityY,
                            getWidth() / 2, getHeight() / 2,
                            4, 1000, 1000,
                            easing);
                    velocity2D.getCurves(points, getWidth(), getHeight());
                    invalidate();
                    break;

            }
            return true;
        }
    }
}