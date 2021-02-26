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
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;

import org.jetbrains.annotations.NotNull;

/**
 * This demonstrates using the api motionLayout.rotateTo
 * It allows you to control the transition between landscape and portrait
 * rotateTo performs an animation between the current state and the
 */
public class RotationUsingRotateTo extends AppCompatActivity {
    private static final String TAG = "CheckSharedValues";
    String layout_name;
    MotionLayout mMotionLayout;
    private int mDuration = 4000;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extra = getIntent().getExtras();
        String prelayout = extra.getString(Utils.KEY);
        layout_name = prelayout;
        Context ctx = getApplicationContext();
        int id = ctx.getResources().getIdentifier(prelayout, "layout", ctx.getPackageName());
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.hide();
        }
        setContentView(id);
        mMotionLayout = Utils.findMotionLayout(this);

        int rotation = getWindowManager().getDefaultDisplay().getRotation();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        int rotationAnimation = WindowManager.LayoutParams.ROTATION_ANIMATION_JUMPCUT;
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        winParams.rotationAnimation = rotationAnimation;
        win.setAttributes(winParams);
        mMotionLayout.transitionToState(getLayoutForOrientation());
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        super.setRequestedOrientation(requestedOrientation);
        int rotationAnimation = WindowManager.LayoutParams.ROTATION_ANIMATION_SEAMLESS;
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        winParams.rotationAnimation = rotationAnimation;
        win.setAttributes(winParams);
    }

    int previous_rotation;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onConfigurationChanged(@NonNull @NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int layout = getLayoutForOrientation();
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        mMotionLayout.rotateTo(layout, mDuration);  // special api to rotate
        previous_rotation = rotation;
    }

    /**
     * Compute the constraint set to transition to.
     *
     * @return
     */
    private int getLayoutForOrientation() {
        switch (getWindowManager().getDefaultDisplay().getRotation()) {
            default:
            case Surface.ROTATION_0:
                return R.id.portrait;
            case Surface.ROTATION_90:
                return R.id.landscape;
            case Surface.ROTATION_180:
                return R.id.portrait;
            case Surface.ROTATION_270:
                if (null != mMotionLayout.getConstraintSet(R.id.landscape_right)) {
                    return R.id.landscape_right;
                }
                return R.id.landscape;
        }
    }

    public void duration(View view) {
        mDuration = Integer.parseInt((String) view.getTag());
    }
}
