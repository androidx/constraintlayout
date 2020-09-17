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
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.constraintlayout.motion.widget.Debug;
import androidx.constraintlayout.utils.widget.MotionTelltales;


public class CustomVerify extends MotionTelltales {
    private static final String TAG = "CustomVerify";


    {
        Log.v(TAG, "<<<<<<<<<<<<<<<<< Created");
    }
    public CustomVerify(Context context) {
        super(context);
    }

    public CustomVerify(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomVerify(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    boolean mTail;
    public boolean getLongTail() {
        return mTail ;
    }

    public void setLongTail(boolean tail) {
        Debug.logStack(TAG, " "+tail, 8);
        Log.v(TAG,Debug.getLoc()+ "---------  set "+tail);
        mTail = tail;
    }
    Rect newRect = new Rect();

    @Override
    public void onDraw(Canvas canvas) {
        if (mTail) {
         canvas.getClipBounds(newRect);
        newRect.inset(0, -20);  //make the rect larger
        canvas.clipRect(newRect, Region.Op.REPLACE);
         }
        super.onDraw(canvas);
    }
}
