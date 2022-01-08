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
import androidx.constraintlayout.widget.ConstraintLayout;

// used with verification_057.xml
public class CheckDumpJson extends AppCompatActivity {
    private static final String TAG = "CheckDumpJson";
    String layout_name;
    ConstraintLayout mLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int id = R.layout.basic_cl_001;
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            String prelayout = extra.getString(Utils.KEY);
            setTitle(layout_name = prelayout);
            Context ctx = getApplicationContext();
              id = ctx.getResources().getIdentifier(prelayout, "layout", ctx.getPackageName());

        }
        setContentView(id);
        mLayout = Utils.findConstraintLayout(this);


    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mLayout.postDelayed(()-> dumpJson(), 2000);
    }

    private void dumpJson() {
       Log.v(TAG, Debug.getLoc() +"\n"+mLayout.getSceneString() );
    }
}
