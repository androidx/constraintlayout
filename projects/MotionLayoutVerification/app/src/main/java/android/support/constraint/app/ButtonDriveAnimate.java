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
public class ButtonDriveAnimate extends AppCompatActivity {
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
        setTitle(layout_name+ " "+this.getClass().getSimpleName());
        mMotionLayout = Utils.findMotionLayout(this);
        mMotionLayout.addTransitionListener(new TransitionAdapter() {
            int count = 0;
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {
                if (count++ > 4){
                    mMotionLayout.removeTransitionListener(this);
                    Log.v(TAG, Debug.getLoc()+"  removing TransitionListener");

                }
                Log.v(TAG, Debug.getLoc()+" "+Debug.getName(getApplicationContext(),endId));
            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                Log.v(TAG, Debug.getLoc()+" "+Debug.getName(getApplicationContext(),currentId));
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {
                Log.v(TAG, Debug.getLoc()+" "+progress+" "+Debug.getName(getApplicationContext(),triggerId));

            }
        });
    }

    public void goLeft(View v) {
        Log.v(TAG, Debug.getLoc()+" ");
        mMotionLayout.transitionToState(R.id.left);

    }
    public void goRight(View v) {
        Log.v(TAG, Debug.getLoc()+" ");
        mMotionLayout.transitionToState(R.id.right);


    }
}
