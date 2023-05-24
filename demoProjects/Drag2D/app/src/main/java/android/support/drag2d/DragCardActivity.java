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
import android.util.Log;

import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
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


public class DragCardActivity extends AppCompatActivity {
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
    private static final String TAG = "MAIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int orientation = getResources().getConfiguration().orientation;
        Log.v(TAG,"orientation =  "+orientation);

        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        LinearLayout topCol = new LinearLayout(this);
        LinearLayout row = new LinearLayout(this);
        LinearLayout col = new LinearLayout(this);
        topCol.setOrientation(LinearLayout.VERTICAL);
        col.setOrientation(LinearLayout.VERTICAL);
        ScrollView scrollView = new ScrollView(this);
        CardMover m = new CardMover(this);
        AppCompatButton[] buttons = new AppCompatButton[sEasingNames.length];

        for (int i = 0; i < sEasingNames.length; i++) {
            AppCompatButton b = new AppCompatButton(this);
            buttons[i] = b;
            b.setText(sEasingNames[i]);
            int mode = i;
            Drawable d = b.getBackground();
            d = d.mutate();
            d.setTint(0xffAA88AA);
            b.setBackgroundDrawable(d);
            b.setPadding(1, 1, 5, 5);
            col.addView(b);
            b.setOnClickListener(c -> {
                for (int j = 0; j < buttons.length; j++) {
                    Drawable draw = buttons[j].getBackground();
                    draw = draw.mutate();
                    draw.setTint((mode == j) ? 0xff328855 : 0xffAA88AA);

                    buttons[j].setBackgroundDrawable(draw);
                }

                m.setEasing(sEasings[mode]);
            });

        }
        String[] name = {"Max V: ", "Max A: ", "time:"};
        float[] min = {400, 400, 0.1f};
        float[] max = {8000, 8000, 10f};
        float[] val = {800, 800, 0.2f};

        DecimalFormat df = new DecimalFormat("##0.0");
        for (int i = 0; i < name.length; i++) {
            int sno = i;
            LinearLayout slidePack = new LinearLayout(this);

            Slider slider = new Slider(this);
            TextView tv = new TextView(this);
            tv.setWidth(340);
            tv.setText(name[sno] + min[sno]);
            slidePack.addView(tv);
            slidePack.addView(slider);

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
            topCol.addView(slidePack);

        }
        AppCompatButton b = new AppCompatButton(this);
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

        col.setBackgroundColor(backgroundColor);
        scrollView.addView(col);
        row.addView(scrollView);
        row.addView(m);
        topCol.addView(row);
        setContentView(topCol);

    }

    static class CardMover extends View {
        public boolean mGraphMode = false;
        Drawable ball;
        VelocityTracker velocityTracker = VelocityTracker.obtain();
        Velocity2D velocity2D = new Velocity2D();
        int mCardX = -30;
        int mCardY = 0;
        Paint mCardPaint = new Paint();
        MaterialVelocity.Easing easing = null;
        float[] points = new float[10000];
        float[] mSegTime1 = new float[4];
        float[] mSegTime2 = new float[4];

        Paint paint = new Paint();
        Paint paintDot = new Paint();
        private float mDuration;


        public CardMover(Context context) {
            super(context);
            setup(context);
        }

        public CardMover(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            setup(context);
        }

        public CardMover(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            setup(context);
        }


        void setup(Context context) {
            ball = ResourcesCompat.getDrawable(context.getResources(), R.drawable.volleyball, null);
            paint.setStrokeWidth(3);
            paintDot.setColor(Color.RED);
            mCardPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mCardPaint.setColor(0x55FF8800);
        }

        public void setEasing(MaterialVelocity.Easing easing) {
            this.easing = easing;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawRGB(230, 210, 200);
            int rounding = touchDown ? 128 : 0;
            if (startAnimationTime != 0) {

                long timeMillis = SystemClock.uptimeMillis() - startAnimationTime;
                float time = timeMillis / 1000f;
                mCardX = (int) velocity2D.getX(time);
                mCardY = (int) velocity2D.getY(time);

                if (velocity2D.isStillMoving(time)) {
                    rounding = 48;
                    invalidate();
                } else {
                    startAnimationTime = 0;
                }
                canvas.drawLines(points, paint);


                for (int i = 0; i < mSegTime1.length; i++) {
                    float t = mSegTime1[i];
                    if (Float.isNaN(t)) {
                        break;
                    }
                    int mark = velocity2D.getPointOffsetX(points.length, t / mDuration);
                    float x = points[mark], y = points[mark + 1];
                    canvas.drawRoundRect(x - 10, y - 10, x + 10, y + 10, 20, 20, paint);
                }

                for (int i = 0; i < mSegTime2.length; i++) {
                    float t = mSegTime2[i];
                    if (Float.isNaN(t)) {
                        break;
                    }

                    int mark = velocity2D.getPointOffsetY(points.length, t / mDuration);
                    float x = points[mark], y = points[mark + 1];
                    canvas.drawRoundRect(x - 10, y - 10, x + 10, y + 10, 20, 20, paint);
                }

                int xPos = velocity2D.getPointOffsetX(points.length, time / mDuration);
                int yPos = velocity2D.getPointOffsetY(points.length, time / mDuration);
                float x = points[xPos], y = points[xPos + 1];
                canvas.drawRoundRect(x - 10, y - 10, x + 10, y + 10, 20, 20, paintDot);
                x = points[yPos];
                y = points[yPos + 1];
                canvas.drawRoundRect(x - 10, y - 10, x + 10, y + 10, 20, 20, paintDot);
            }
            // draw card

            canvas.drawRoundRect(mCardX, mCardY, mCardX + getWidth() / 2, getHeight() + mCardY, rounding, rounding, mCardPaint);
            int scale = 128;

            int ballX = mCardX + getWidth() / 4 - scale / 2;
            int ballY = mCardY + getHeight() / 2 - scale / 2;

            ball.setBounds(ballX, ballY, ballX + scale, ballY + scale);
            ball.setTint(Color.CYAN);
            ball.draw(canvas);
        }

        float touchDownX;
        float touchDownY;
        float touchDeltaX, touchDeltaY;
        int ballDownX, ballDownY;
        long startAnimationTime;
        boolean touchDown = false;

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            velocityTracker.addMovement(event);
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    startAnimationTime = 0;
                    touchDownX = event.getX();
                    touchDownY = event.getY();
                    ballDownX = mCardX;
                    ballDownY = mCardY;
                    touchDown = true;
                    break;

                case MotionEvent.ACTION_MOVE:
                    touchDeltaX = event.getX() - touchDownX;
                    touchDeltaY = event.getY() - touchDownY;
                    mCardX = (int) (ballDownX + touchDeltaX);
                    mCardY = (int) (ballDownY + touchDeltaY);
                    int slop = 300;
                    mCardY = Math.max(-slop, Math.min(slop, mCardY));
                    mCardY = (int) softClamp(mCardY, -slop, +slop);
                    invalidate();

                    break;
                case MotionEvent.ACTION_UP:
                    velocityTracker.computeCurrentVelocity(1000);
                    float velocityX = velocityTracker.getXVelocity();
                    float velocityY = velocityTracker.getYVelocity();
                    startAnimationTime = event.getEventTime();
                    touchDown = false;
                    boolean dir = mCardX + velocityX < getWidth() / 4;
                    velocity2D.configure(mCardX, mCardY,
                            velocityX, 0,
                            dir ? 0 : getWidth() / 2, 0,
                            duration, maxV, maxA,
                            easing);
                    velocity2D.getCurves(points, getWidth(), getHeight(), mGraphMode);
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

    public static float softClamp(float x, float min, float max) {
        float halfWidth = (max - min) / 2;
        x = (x - (min + max) / 2) / halfWidth;

        return (max + min) / 2 + halfWidth * (float) (1 / (1 + Math.exp(-x)) - 0.5f);

    }
}
