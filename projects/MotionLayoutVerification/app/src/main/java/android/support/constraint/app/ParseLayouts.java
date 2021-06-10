
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.Debug;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.io.IOException;
import java.io.StringWriter;

// used with verification_057.xml
public class ParseLayouts extends AppCompatActivity {
    private static final String TAG = "Bug010";
    String layout_name;
    MotionLayout mMotionLayout;
    FrameLayout mFrameLayout;
    String[] mLayoutNames;
    int mCurrentLayout = 0;
    String mLayoutName;
    View mInflatedView;
    TextView title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extra = getIntent().getExtras();
        String prelayout = extra.getString(Utils.KEY);
        Context ctx = getApplicationContext();
        int id = ctx.getResources().getIdentifier(prelayout, "layout", ctx.getPackageName());
        setContentView(id);

        title = findViewById(R.id.layoutName);
        mFrameLayout = findViewById(R.id.frame);
        mLayoutNames = Utils.getLayouts("v.*_.*", false, "");
        mCurrentLayout = mLayoutNames.length / 2;
        loadLayout(mLayoutNames[mCurrentLayout]);

    }

    private void loadLayout(String layout_name) {
        Context ctx = getApplicationContext();

        if (mFrameLayout.getChildCount() > 0) {
            mFrameLayout.removeAllViews();
        }

        int id = ctx.getResources().getIdentifier(layout_name, "layout", ctx.getPackageName());

        // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());

        View view = inflater.inflate(id, null);
        if (view instanceof MotionLayout) {
            MotionLayout ml = (MotionLayout) view;
            ml.postDelayed(() -> write(ml), 200);
        }
        mFrameLayout.addView(view);
        mLayoutName = layout_name;
    }

    private void write(MotionLayout ml) {

        ConstraintSet cs = new ConstraintSet();
        cs.clone(ml);
        StringWriter writer = new StringWriter();
        if (cs == null) {
            Log.v(TAG, Debug.getLoc() + " " + layout_name);
            return;
        }
        try {
            cs.writeState(writer, ml, 0);
            logBigString(writer.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void logBigString(String str) {
        int len = str.length();
        for (int i = 0; i < len; i++) {
            int k = str.indexOf("\n", i);
            if (k == -1) {
                Log.v(TAG, str.substring(i));
                break;
            }
            Log.v(TAG, str.substring(i, k));
            i = k;
        }
    }

    public void next(View v) {
        Log.v(TAG, Debug.getLoc() + " " + layout_name);
        String str = mLayoutNames[++mCurrentLayout];
        title.setText(str);
        loadLayout(str);
    }

    public void prev(View v) {
        Log.v(TAG, Debug.getLoc() + " " + layout_name);
        String str = mLayoutNames[--mCurrentLayout];
        title.setText(str);
        loadLayout(str);
    }
}
