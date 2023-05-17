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


import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

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
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.slider.Slider;

import java.text.DecimalFormat;


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

        boolean landscape = getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE;

        LinearLayout top = new LinearLayout(this);
        top.setOrientation(landscape ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL);
        LinearLayout row = new LinearLayout(this);
        LinearLayout col = new LinearLayout(this);
        LinearLayout controls = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);


        ScrollView scrollView = new ScrollView(this);
        BallMover m = new BallMover(this);

        AppCompatButton[] buttons = new AppCompatButton[sEasingNames.length];

        { // slides
            controls.setOrientation(LinearLayout.VERTICAL);

            String[] name = {"Max V: ", "Max A: ", "time:"};
            float[] min = {400, 400, 0.1f};
            float[] max = {8000, 8000, 10f};
            float[] val = {800, 800, 0.2f};


            DecimalFormat df = new DecimalFormat("##0.0");
            for (int i = 0; i < name.length; i++) {
                int sno = i;
                LinearLayout slidePack = new LinearLayout(this);
                slidePack.setOrientation(LinearLayout.VERTICAL);
                Slider slider = new Slider(this);
                TextView tv = new TextView(this);

                tv.setWidth(340);
                tv.setText(name[sno] + min[sno]);
                slidePack.addView(tv);
                slidePack.addView(slider, 500, ViewGroup.LayoutParams.WRAP_CONTENT);

                slider.setValue(val[sno]);
                slider.setValueTo(max[sno]);
                slider.setValueFrom(min[sno]);
                slider.addOnChangeListener(new Slider.OnChangeListener() {
                    @Override
                    public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                        float v = value;
                        tv.setText(name[sno] + df.format(v));
                        m.setParam(sno, v);
                    }
                });
                controls.addView(slidePack);

            }
        }

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
                    draw.setTint((mode == j) ? Color.CYAN : 0xffAAAAAA);

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
            m.mGraphMode = (m.mGraphMode + 1) % 3;
            String[] s = {"velocity", "Position", "FPS"};
            b.setText("plot " + s[m.mGraphMode]);

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
        top.addView(controls);
        top.addView(row);

        setContentView(top);

    }

    static class BallMover extends View {
        public int mGraphMode = 0;
        Drawable ball;
        int[] mDurationF2F = new int[1200];
        int[] mDurationT = new int[mDurationF2F.length];
        float[] mDurationPoints = new float[mDurationF2F.length * 4 + 4 * 3];
        int mDurationPointCount;
        int mDurationEndPos;
        long mDurationStart;
        VelocityTracker velocityTracker = VelocityTracker.obtain();
        Velocity2D velocity2D = new Velocity2D();
        int ballX;
        int ballY;
        int ballW = 128;
        int ballH = 128;
        MaterialVelocity.Easing easing = null;
        float[] points = new float[10000];
        float[] mSegTime1 = new float[4];
        float[] mSegTime2 = new float[4];
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
//            ball = ResourcesCompat.getDrawable(context.getResources(), R.drawable.volleyball, null);
            ball = ResourcesCompat.getDrawable(context.getResources(), R.drawable.window, null);
            paint.setStrokeWidth(3);
            paintDot.setColor(Color.RED);
            mDurationStart = System.nanoTime();
            mDurationEndPos = 0;
        }

        public void setEasing(MaterialVelocity.Easing easing) {
            this.easing = easing;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawRGB(200, 250, 200);
            int dp = mDurationEndPos % mDurationT.length;
            mDurationT[dp] = (int) (System.nanoTime() - mDurationStart);
            if (mDurationEndPos > 0) {
                mDurationF2F[dp] = mDurationT[dp] - mDurationT[(dp - 1 + mDurationT.length) % mDurationT.length];
            }
            mDurationEndPos++;


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
                if (mGraphMode != 2) {
                    canvas.drawLines(points, paint);

                    for (int i = 0; i < mSegTime1.length; i++) {
                        float t = mSegTime1[i];
                        if (Float.isNaN(t)) {
                            break;
                        }
                        int mark = velocity2D.getPointOffsetX(points.length, t / mDuration);
                        float x = points[mark], y = points[mark + 1];
                        canvas.drawRoundRect(x - 10, y - 10, x + 10, y + 10, 4, 4, paint);
                    }

                    for (int i = 0; i < mSegTime2.length; i++) {
                        float t = mSegTime2[i];
                        if (Float.isNaN(t)) {
                            break;
                        }

                        int mark = velocity2D.getPointOffsetY(points.length, t / mDuration);
                        float x = points[mark], y = points[mark + 1];
                        canvas.drawRoundRect(x - 10, y - 10, x + 10, y + 10, 4, 4, paint);
                    }


                    int xPos = velocity2D.getPointOffsetX(points.length, time / mDuration);
                    int yPos = velocity2D.getPointOffsetY(points.length, time / mDuration);
                    float x = points[xPos], y = points[xPos + 1];
                    canvas.drawRoundRect(x - 10, y - 10, x + 10, y + 10, 20, 20, paintDot);
                    x = points[yPos];
                    y = points[yPos + 1];
                    canvas.drawRoundRect(x - 10, y - 10, x + 10, y + 10, 20, 20, paintDot);
                } else {
                    plotTime(canvas, paint, getWidth(), getHeight(), mDurationF2F, mDurationT, mDurationEndPos, mDurationPoints);
                    canvas.drawLines(mDurationPoints, 0, mDurationPointCount, paint);
                }

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
                    mDurationStart = System.nanoTime();
                    mDurationEndPos = 0;
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
                            duration, maxV, maxA,
                            easing);
                    velocity2D.getCurves(points, getWidth(), getHeight(), mGraphMode == 0);
                    velocity2D.getCurvesSegments(mSegTime1, mSegTime2);
                    mDuration = velocity2D.getDuration();
                    invalidate();
                    break;

            }
            return true;
        }


        float maxV = 800;
        float maxA = 800;
        float duration = 0.2f;

        public void setParam(int param, float v) {
            switch (param) {
                case 0:
                    maxV = v;
                    break;
                case 1:
                    maxA = v;
                    break;
                case 2:
                    duration = v;
            }
        }
    }

    public static void plotTime(Canvas c,
                                Paint paint,
                                int w,
                                int h,
                                int[] mDurationF2F,
                                int[] mDurationT,
                                int end, float[] points) {

        int count = getCurves(points, w, h, end, mDurationT, mDurationF2F);

        c.drawLines(points, 0, count, paint);

    }

    public static int getCurves(float[] points, int w, int h, int end, int[] xp, int[] yp) {
        int len = points.length;

        int lines = (len - 3 * 4) / 4;
        int p = 0;

        int inset = 40;
        int regionW = w - inset * 2;
        int regionH = h - inset * 2;
        points[p++] = inset;
        points[p++] = inset;
        points[p++] = inset;
        points[p++] = inset + regionH;

        points[p++] = inset + regionW;
        points[p++] = inset;
        points[p++] = inset + regionW;
        points[p++] = inset + regionH;

        points[p++] = inset;
        points[p++] = inset + regionH;
        points[p++] = inset + regionW;
        points[p++] = inset + regionH;

        float minx = 0, maxx = 1;
        float miny = 0, maxy = 1;
        float v;


        int steps = Math.min(end, xp.length);
        int start = (end > xp.length) ? xp.length : 0;

        for (int i = 0; i < steps; i++) {
            int k = (start + i) % xp.length;

            minx = Math.min(xp[k], minx);
            maxx = Math.max(xp[k], maxx);
            miny = Math.min(yp[k], miny);
            maxy = Math.max(yp[k], maxy);
        }

        float x2 = regionW * (xp[start] - minx) / (maxx - minx);
        float y2 = regionH * (yp[start] - miny) / (maxy - miny);
        for (int i = 0; i < steps; i++) {
            int k = (start + i) % xp.length;
            float x1 = regionW * (xp[k] - minx) / (maxx - minx);
            float y1 = regionH * (yp[k] - miny) / (maxy - miny);

            points[p++] = inset + x1;
            points[p++] = inset + regionH - y1;
            points[p++] = inset + x2;
            points[p++] = inset + regionH - y2;
            x2 = x1;
            y2 = y1;
        }
        return p;

    }
}