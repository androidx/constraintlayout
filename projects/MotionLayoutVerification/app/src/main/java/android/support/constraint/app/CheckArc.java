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

package android.support.constraint.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.constraintlayout.motion.widget.Debug;

import org.jetbrains.annotations.Nullable;

public class CheckArc extends View
{
    int size_w,size_h;
    int msur_w,msur_h;
    Paint paint = new Paint();
    Path path = new Path();
    {
         paint.setStyle(Paint.Style.FILL_AND_STROKE);
         paint.setStrokeWidth(3f);
    }
    private static final String TAG = "DumbArc";

    public CheckArc(Context context) {
        super(context);
    }

    public CheckArc(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckArc(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.v(TAG, Debug.getLoc());
        msur_h = MeasureSpec.getSize(heightMeasureSpec);
        msur_w = MeasureSpec.getSize(widthMeasureSpec);
        Log.v(TAG, Debug.getLoc()+msur_w+", "+msur_h);

    }

    @Override
    protected void onSizeChanged(int viewWidth, int viewHeight, int oldw, int oldh) {
        super.onSizeChanged(viewWidth, viewHeight, oldw, oldh);
        size_w = viewWidth;
        size_h = viewHeight;
        reCreate(size_w,size_h);
    }
        void reCreate(float viewWidth, float viewHeight) {
        float pathInset = 80;
        float FIRST_CONTROL_OFFSET = 0.7f;
        float SECOND_CONTROL_OFFSET = 0.7f;
        Log.v(TAG, Debug.getLoc()+viewWidth+", "+viewHeight);
        path.reset();
        path.moveTo(0F, 0F);
        path.lineTo(viewWidth, 0F);
        path.lineTo(viewWidth, viewHeight - pathInset);
        path.cubicTo(
                viewWidth * FIRST_CONTROL_OFFSET,
                viewHeight,
                viewWidth * SECOND_CONTROL_OFFSET,
                viewHeight,
                0F,
                viewHeight - pathInset);
        path.close();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(0xFFFF00FF);
        canvas.drawPath(path,paint);

        paint.setColor(0xFFFFFFFF);
    //    canvas.drawRect(0,0,msur_w,msur_h, paint);
        canvas.drawLine(0,0,msur_w,msur_h, paint);
        paint.setColor(0xFF000000);
       // canvas.drawRect(0,0,size_w,size_h, paint);
        canvas.drawLine(0,size_h, size_w,0, paint);
    }
}
