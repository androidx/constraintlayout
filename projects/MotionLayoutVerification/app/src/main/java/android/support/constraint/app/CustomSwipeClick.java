/*
 * Copyright (C) 2021 The Android Open Source Project
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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.Debug;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.motion.widget.MotionScene;
import androidx.constraintlayout.motion.widget.OnSwipe;

public class CustomSwipeClick extends AppCompatActivity {
    private static final String TAG = "CustomSwipeClick";
    String layout_name;
    MotionLayout mMotionLayout;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extra = getIntent().getExtras();
        String prelayout = extra.getString(Utils.KEY);
        layout_name = prelayout;
        Context ctx = getApplicationContext();
        int id = ctx.getResources().getIdentifier(prelayout, "layout", ctx.getPackageName());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.hide();
        }
        setContentView(id);
        mMotionLayout = Utils.findMotionLayout(this);

    }
   public void addSwipeRight(View v) {
        MotionScene.Transition transition = mMotionLayout.getTransition(R.id.transition1);
       transition.setOnSwipe(new OnSwipe().setDragDirection(OnSwipe.DRAG_RIGHT));
       Log.v(TAG, Debug.getLoc()+" ");

   }
    public void addSwipeCircle(View v) {
        Log.v(TAG, Debug.getLoc()+" ");
        MotionScene.Transition transition = mMotionLayout.getTransition(R.id.transition1);
        OnSwipe swipe = new OnSwipe().setDragDirection(OnSwipe.DRAG_CLOCKWISE).setRotateCenter(R.id.imageView9);
        transition.setOnSwipe(swipe);

    }
    public void addSwipeUp(View v) {
        Log.v(TAG, Debug.getLoc()+" ");
        MotionScene.Transition transition = mMotionLayout.getTransition(R.id.transition1);
        transition.setOnSwipe(new OnSwipe().setDragDirection(OnSwipe.DRAG_UP));

    }
}
