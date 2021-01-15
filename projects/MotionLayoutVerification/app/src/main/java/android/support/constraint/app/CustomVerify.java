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
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.motion.widget.Debug;
import androidx.constraintlayout.utils.widget.MotionTelltales;


public class CustomVerify extends View {
    private static final String TAG = "CustomVerify";
    String []lines = new String[12];
    int lcount = 0;
    Context mContext;
    Rect bounds = new Rect();
    Paint mPaint = new Paint();
    {
        mPaint.setTextSize(64);
    }
    public CustomVerify(Context context) {
        super(context);
        mContext = context;
    }

    public CustomVerify(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

    }

    public CustomVerify(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }
    boolean mTail;
    public boolean getLongTail() {
        return mTail ;
    }

    public void setDummyBool(boolean bool) {
       lines[(lcount++)%lines.length] = ((int)(System.nanoTime()%1000000000)/1000000)+" ["+lcount+"] "+bool;
         invalidate();
    }
    public void setDummyRef(int ref) {
        lines[(lcount++)%lines.length] = ((int)(System.nanoTime()%1000000000)/1000000)+" ["+lcount+"] "+Debug.getName(mContext, ref);
         invalidate();
    }
    public void setDummyString(CharSequence ref) {
        lines[(lcount++)%lines.length] = ((int)(System.nanoTime()%1000000000)/1000000)+" ["+lcount+"] "+ref;

        invalidate();
    }
    Rect newRect = new Rect();

    @Override
    public void onDraw(Canvas canvas) {
        int y = 0;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line!=null) {
                mPaint.getTextBounds(line, 0, line.length(),bounds);
                y+=bounds.height();
                canvas.drawText(line, 0,y,mPaint);
            }
        }
    }
}
