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
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.Debug;
import androidx.constraintlayout.motion.widget.MotionLayout;

// used with verification_057.xml
public class CheckSetStates extends AppCompatActivity {
    private static final String TAG = "CustomSwipeClick";
    String layout_name;
    MotionLayout mMotionLayout;
    int []states = {R.id.s1,R.id.s2,R.id.s3,R.id.s4,R.id.s5,R.id.s6,R.id.s7,R.id.s8,R.id.s9};

    @Override
    protected void onCreate(@Nullable  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extra = getIntent().getExtras();
        String prelayout = extra.getString(Utils.KEY);
        setTitle(layout_name = prelayout);
        Context ctx = getApplicationContext();
        int id = ctx.getResources().getIdentifier(prelayout, "layout", ctx.getPackageName());
        setContentView(id);
        mMotionLayout = Utils.findMotionLayout(this);

        TextView text = findViewById(R.id.text);
    }


    public void jump1(View view) {
        for (int i = 0; i < states.length; i++) {
            if (mMotionLayout.getCurrentState() == states[i]) {
//                mMotionLayout.setState(states[(i+1)%states.length],-1,-1);
                mMotionLayout.transitionToState(states[(i+1)%states.length]);
                Log.v(TAG, Debug.getLoc()+" "+i );
                return;
            }
        }
    }
    public void jump2(View view) {
        for (int i = 0; i < states.length; i++) {
            if (mMotionLayout.getCurrentState() == states[i]) {
                mMotionLayout.setState(states[(i+1)%states.length],-1,-1);
                 mMotionLayout.setProgress(0);
                Log.v(TAG, Debug.getLoc()+" "+i );
                return;
            }
        }
    }
}
