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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.Debug;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.motion.widget.MotionScene;
import androidx.constraintlayout.motion.widget.TransitionAdapter;

/**
 * Test transitionToState bug
 */
public class OnCreateTransiton extends AppCompatActivity {
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
            int mode = 0;
            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                Log.v(TAG, Debug.getLoc()+" ");
                MotionScene.Transition tra = mMotionLayout.getTransition(R.id.my_transition);
                if (tra == null) {
                    return;
                }
                mode++;
                switch (mode) {
                    case 0:
                        tra.setInterpolatorInfo(MotionScene.Transition.INTERPOLATE_EASE_IN_OUT,null,0);
                        break;
                    case 1:
                        tra.setInterpolatorInfo(MotionScene.Transition.INTERPOLATE_EASE_IN,null,0);
                        break;
                    case 2:
                        tra.setInterpolatorInfo(MotionScene.Transition.INTERPOLATE_EASE_OUT,null,0);
                        break;
                    case 3:
                        tra.setInterpolatorInfo(MotionScene.Transition.INTERPOLATE_LINEAR,null,0);
                        break;
                    case 4:
                        tra.setInterpolatorInfo(MotionScene.Transition.INTERPOLATE_BOUNCE,null,0);
                        break;
                    case 5:
                        tra.setInterpolatorInfo(MotionScene.Transition.INTERPOLATE_OVERSHOOT,null,0);
                        break;
                    case 6:
                        tra.setInterpolatorInfo(MotionScene.Transition.INTERPOLATE_ANTICIPATE,null,0);
                        break;
                    case 7:
                        tra.setInterpolatorInfo(MotionScene.Transition.INTERPOLATE_REFERENCE_ID,null,R.anim.my_custom);
                        break;
                    case 8:
                        tra.setInterpolatorInfo(MotionScene.Transition.INTERPOLATE_SPLINE_STRING,"cubic(0.34, 1.56, 0.64, 1)",0);
                        break;
                    case 9:
                        tra.setInterpolatorInfo(MotionScene.Transition.INTERPOLATE_SPLINE_STRING,"spline(0.0,0,0,1,1,1)",0);
                        mode = -1;
                        break;

                }
                Log.v(TAG,Debug.getLoc()+" mode = " +mode);

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

}
