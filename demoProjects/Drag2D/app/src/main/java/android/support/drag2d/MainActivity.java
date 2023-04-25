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
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.res.ResourcesCompat;


public class MainActivity extends AppCompatActivity {
    int backgroundColor = 0xFF000000 | (200 * 256 + 250) * 256 + 200;
    static String[] sEasingNames = {
            "DECELERATE",
            "LINEAR",
            "OVERSHOOT",
            "SINE",
            "CUBIC",
            "QUINT",
            "CIRC",
            "QUAD",
            "QUART",
            "EXPO",
            "BACK",
            "ELASTIC",
            "BOUNCE"
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
        AppCompatButton[]buttons = new AppCompatButton[sEasingNames.length];

        for (int i = 0; i < sEasingNames.length; i++) {
            AppCompatButton b = new AppCompatButton(this);
            buttons[i] = b;
            b.setText(sEasingNames[i]);
            int mode = i;
            Drawable d = b.getBackground();
            d = d.mutate();
            d.setTint(0xffAAAAAA);
            b.setBackgroundDrawable(d);
            b.setPadding(1, 1, 5, 5);
            col.addView(b);
            b.setOnClickListener(c -> {
                for (int j = 0; j < buttons.length; j++) {
                    Drawable draw = buttons[j].getBackground();
                    draw = draw.mutate();
                    draw.setTint((mode == j)?Color.CYAN:0xffAAAAAA);
                    buttons[j].setBackgroundDrawable(draw);
                }

                m.setEasing(sEasings[mode]);
            });

        }

        AppCompatButton b = new AppCompatButton(this);
        // mode
        b.setText("plot Position");
        b.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        col.addView(b);
         Drawable d = b.getBackground();
         d = d.mutate();
         d.setTint(0xff328832);
        b.setBackgroundDrawable(d);
        b.setOnClickListener(c -> {
            m.mGraphMode = !m.mGraphMode;
            b.setText("plot " + (m.mGraphMode ? "velocity" : "Position"));
        });
        // mode
        AppCompatButton app = new AppCompatButton(this);
        app.setText("card demo...");
        app.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        col.addView(app);
        app.setOnClickListener(c -> {
            Intent intent = new Intent(this, DragCardActivity.class);
            startActivity(intent);
        });


        col.setBackgroundColor(backgroundColor);
        scrollView.addView(col);
        row.addView(scrollView);
        row.addView(m);
        setContentView(row);

    }

    static class BallMover extends View {
        public boolean mGraphMode = false;
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
        Paint paintDot = new Paint();
        private float mDuration;


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
            paintDot.setColor(Color.RED);
        }

        public void setEasing(MaterialVelocity.Easing easing) {
            this.easing = easing;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawRGB(200, 250, 200);
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
                int xPos = velocity2D.getPointOffsetX(points.length, time / mDuration);
                int yPos = velocity2D.getPointOffsetY(points.length, time / mDuration);
                float x = points[xPos], y = points[xPos + 1];
                canvas.drawRoundRect(x - 10, y - 10, x + 10, y + 10, 20, 20, paintDot);
                x = points[yPos];
                y = points[yPos + 1];
                canvas.drawRoundRect(x - 10, y - 10, x + 10, y + 10, 20, 20, paintDot);

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
                    startAnimationTime = 0;
                    touchDownX = event.getX();
                    touchDownY = event.getY();
                    ballDownX = ballX;
                    ballDownY = ballY;
                    break;

                case MotionEvent.ACTION_MOVE:
                    touchDeltaX = event.getX() - touchDownX;
                    touchDeltaY = event.getY() - touchDownY;
                    ballX = (int) (ballDownX + touchDeltaX);
                    ballY = (int) (ballDownY + touchDeltaY);
                    invalidate();

                    break;
                case MotionEvent.ACTION_UP:
                    velocityTracker.computeCurrentVelocity(1000);
                    float velocityX = velocityTracker.getXVelocity();
                    float velocityY = velocityTracker.getYVelocity();
                    startAnimationTime = event.getEventTime();
                    velocity2D.configure(ballX, ballY,
                            velocityX, velocityY,
                            getWidth() / 2, getHeight() / 2,
                            4, 1000, 1000,
                            easing);
                    velocity2D.getCurves(points, getWidth(), getHeight(), mGraphMode);
                    mDuration = velocity2D.getDuration();
                    invalidate();
                    break;

            }
            return true;
        }
    }
}