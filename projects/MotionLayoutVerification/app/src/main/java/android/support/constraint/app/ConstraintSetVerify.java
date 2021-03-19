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
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

public class ConstraintSetVerify extends AppCompatActivity {
    private static final String TAG = "ConstraintSetVerify";
    String layout_name;
    ConstraintLayout mConstraintLayout;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extra = getIntent().getExtras();
        String prelayout = extra.getString(Utils.KEY);
        layout_name = prelayout;
        Context ctx = getApplicationContext();
        int id = ctx.getResources().getIdentifier(prelayout, "layout", ctx.getPackageName());
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.hide();
        }
        setContentView(id);
        mConstraintLayout = Utils.findConstraintLayout(this);
        findViewById(R.id.showContent).setOnClickListener( (v)->showContent(v));
        findViewById(R.id.showSplash).setOnClickListener( (v)->showSplash(v));
    }
   public void showSplash(View v) {
       ConstraintSet set = new ConstraintSet();
       set.clone(mConstraintLayout);
       set.setVerticalBias(R.id.logo, 0.5f);
       set.setVisibility(R.id.splashScreen, View.VISIBLE);
       set.setVisibility(R.id.contentGroup, View.GONE);
       set.applyTo(mConstraintLayout);
   }
    public void showContent(View v) {

        ConstraintSet set = new ConstraintSet();
        set.clone(mConstraintLayout);
        set.setVerticalBias(R.id.logo, 0.3f);
        set.setVisibility(R.id.splashScreen, View.GONE);
        set.setVisibility(R.id.contentGroup, View.VISIBLE);
        set.applyTo(mConstraintLayout);

    }

}
