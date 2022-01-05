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

/**
 * Test transitionToState bug
 */
public class CheckSetProgress extends AppCompatActivity {
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
        setContentView(id);
        mMotionLayout = Utils.findMotionLayout(this);
        mMotionLayout.transitionToState(R.id.end);
        mMotionLayout.setTransitionListener(new TransitionAdapter() {
            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                Log.v(TAG, Debug.getLoc()+" ");
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {
                Log.v(TAG, Debug.getLoc()+" "+progress);
                if (progress <= 0.001 || progress >= 0.999) {
                    Debug.logStack(TAG, "",19);
                }
            }
        });
    }

    public void progressP0(View v) {
        mMotionLayout.setProgress(0);
    }
    public void progressP5(View v){
        mMotionLayout.setProgress(0.5f);
    }
    public void progressP1(View v){
        mMotionLayout.setProgress(1f);
    }
    public void progressP0V0(View v) {
        mMotionLayout.setProgress(0,0);

    }
    public void progressP0V1(View v) {
        mMotionLayout.setProgress(0,1);

    }
    public void progressP5V_1(View v){
        mMotionLayout.setProgress(0.5f,-0.1f);

    }
    public void progressP5V1(View v){
        mMotionLayout.setProgress(0.5f,0.1f);

    }
    public void progressP1V0(View v){
        mMotionLayout.setProgress(1f,0f);

    }
    public void progressP1V_1(View v){
        mMotionLayout.setProgress(1f,-0.1f);

    }
    public void progressP50(View v){
        mMotionLayout.setProgress((float) Math.random(),0f);

    }

}
