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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.Debug;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.motion.widget.TransitionAdapter;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintLayoutPerformance;

/**
 * Test transitionToState bug
 */
public class CheckPerformanceMetric extends AppCompatActivity {
    private static final String TAG = "CheckPerformanceMetric";
    String layout_name;
    ConstraintLayout mConstraintLayout;
    ConstraintLayoutPerformance performance;
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extra = getIntent().getExtras();
        String prelayout = extra.getString(Utils.KEY);
        layout_name = prelayout;
        Context ctx = getApplicationContext();
        int id = ctx.getResources().getIdentifier(prelayout, "layout", ctx.getPackageName());
        setContentView(id);
        mConstraintLayout = Utils.findConstraintLayout(this);
        performance = new ConstraintLayoutPerformance(mConstraintLayout);
        mConstraintLayout.addOnLayoutChangeListener(this::foo);
        mConstraintLayout.postDelayed(this::log, 1000);
    }
   public void foo(View v, int left, int top, int right, int bottom,
                   int oldLeft, int oldTop, int oldRight, int oldBottom) {
     log();
   }
   void log(){
       performance.logSummary("CheckPerformanceMetric");
   }
}
