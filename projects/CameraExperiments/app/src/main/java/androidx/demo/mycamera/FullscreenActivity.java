/*
 * Copyright (C) 2018 The Android Open Source Project
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

package androidx.demo.mycamera;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    private static final String TAG = "FullscreenActivity";
    final float D_FACTOR = 10;
    final int TRANSITION_LEFT_FROM_TOP = 0;
    final int TRANSITION_RIGHT_FROM_TOP = 1;
    final int TRANSITION_RIGHT_FROM_LEFT = 2;
    final int TRANSITION_LEFT_FROM_RIGHT = 3;
    MotionLayout mMotionLayout;
    CameraPreview mCam;
    float mAngle;
    int mCurrentTransitionType = -1;
    int[][] mTransition = {
            {R.id.portrait, R.id.landscape_R90}, //TRANSITION_LEFT_FROM_TOP
            {R.id.portrait, R.id.landscape_right_RN90}, //TRANSITION_RIGHT_FROM_TOP
            {R.id.landscape, R.id.portrait_R90}, //TRANSITION_RIGHT_FROM_LEFT
            {R.id.landscape_right, R.id.portrait_RN90},//TRANSITION_LEFT_FROM_RIGHT
    };
    int count = 0;
    float[] mAccValues;
    int mLastProcessOrientation;
    ArrayList<ActivityCompat.OnRequestPermissionsResultCallback> permissionCallbacks = new ArrayList<>();
    long waitTill = System.currentTimeMillis();
    int mHold = 0;
    SensorEventListener gravity_listener = new SensorEventListener() {
        float dampX, dampY;
        float prevX, prevY;

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            mAccValues = sensorEvent.values;
            float x = mAccValues[0];
            float y = mAccValues[1];
            if (Math.hypot(x, y) < 2) {
                return;
            }
            dampX = x + dampX * D_FACTOR;
            dampY = y + dampY * D_FACTOR;
            dampX /= (1 + D_FACTOR);
            dampY /= (1 + D_FACTOR);

            if ((Math.abs(dampX - prevX) < 0.01 && Math.abs(dampY - prevY) < 0.01)) {
                return;
            }
            prevX = dampX;
            prevY = dampY;
            mAngle = curve(dampX, dampY);
            orientation();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.hide();
        }

        setContentView(R.layout.activity_fullscreen);

        mCam = findViewById(R.id.fake_cam);
        mMotionLayout = findViewById(R.id.motionLayout);
        boolean landscape = (getWindowManager().getDefaultDisplay().getRotation() & 1) == 1;
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        mMotionLayout.setState(landscape ? R.id.landscape : R.id.portrait, -1, -1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            sensorManager.registerListener(gravity_listener, sensor, 10000);
        }
        permissionCallbacks.add(mCam);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (ActivityCompat.OnRequestPermissionsResultCallback subsystem : permissionCallbacks) {
            subsystem.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
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
        boolean landscape = (getWindowManager().getDefaultDisplay().getRotation() & 1) == 1;
        mMotionLayout.setState(getLayoutForOrientation(), -1, -1);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        super.setRequestedOrientation(requestedOrientation);
        int rotationAnimation = WindowManager.LayoutParams.ROTATION_ANIMATION_JUMPCUT;
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        winParams.rotationAnimation = rotationAnimation;
        win.setAttributes(winParams);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        mCurrentTransitionType = -1;
        orientation();
        super.onConfigurationChanged(newConfig);
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

    float curve(float accX, float accY) {
        float ang = (float) Math.atan2(accX, accY);
        ang /= (Math.PI / 2);
        return ang;
    }

    public void orientation() {
        int id = mMotionLayout.getEndState();
        int rotation = getWindowManager().getDefaultDisplay().getRotation();

        boolean set_transition = false;
        if (mLastProcessOrientation != rotation) {
            set_transition = true;
        }
        mLastProcessOrientation = rotation;
        if (mCurrentTransitionType == -1) {
            set_transition = true;
        }
        float p = mAngle;

        int type;
        float progress = 0;
        switch (rotation) {
            case Surface.ROTATION_90:
                type = TRANSITION_RIGHT_FROM_LEFT;
                progress = pmap(Math.max(1 - p, 0));
                break;
            case Surface.ROTATION_270:
                type = TRANSITION_LEFT_FROM_RIGHT;
                progress = pmap(Math.max(1 + p, 0));
                break;
            case Surface.ROTATION_0:
            default:
                if (p > 0) {
                    type = TRANSITION_LEFT_FROM_TOP;
                } else {
                    type = TRANSITION_RIGHT_FROM_TOP;
                }
                progress = pmap(Math.abs(p));
        }

        if (type != mCurrentTransitionType || set_transition) {
            mMotionLayout.setTransition(mTransition[type][0], mTransition[type][1]);
            mCurrentTransitionType = type;
        }

        mMotionLayout.setProgress(progress);
    }

    float pmap(float p) {
        float t = p - 0.5f;
        t *= 1 + mHold / 20f;
        return Math.max(0, Math.min(1, t + 0.5f));
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCam.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCam.resume();
    }

}