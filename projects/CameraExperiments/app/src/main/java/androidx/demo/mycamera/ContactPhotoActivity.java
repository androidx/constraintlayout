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
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;

import java.util.ArrayList;

/**
 * Simulates a camera app that handles rotation gracefully.
 */
public class ContactPhotoActivity extends AppCompatActivity {
    private static final String TAG = "FullscreenActivity";
    static final float D_FACTOR = 10;
    MotionLayout mMotionLayout;
    CameraPreview mCam;
    OrientationListener mGravityListener = new OrientationListener();
    int mCurrentTransitionType = -1;
    int mHold = 0;
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ensure it is full screen
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.hide();
        }
        setContentView(R.layout.contact_photo);


        mMotionLayout = findViewById(R.id.motionLayout);
        listenToOrientation();
        mCam = findViewById(R.id.camera);
        mCam.on();
    }

    // ==================== Camera Lifecycle ==============================
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mCam.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCam != null) {
            mCam.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCam != null) {
            mCam.resume();
        }
    }

    public void orientation() {

        float p = mGravityListener.getAngle();
        int  end  = (p > 0) ? R.id.portrait_L90: R.id.portrait_r90;
        float progress = pmap(Math.abs(p));
        if (end != mCurrentTransitionType ) {
            mMotionLayout.setTransition(R.id.portrait, end);
            mCurrentTransitionType = end;
        }
        mMotionLayout.setProgress(progress);
    }

    float pmap(float p) {
        float t = p - 0.5f;
        t *= 1 + mHold / 20f;
        return Math.max(0, Math.min(1, t + 0.5f));
    }

    void listenToOrientation() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            sensorManager.registerListener(mGravityListener, sensor, 10000);
        }
    }
    /**
     * Listen to accelerometers and perform some rudimentary smoothing of values
     */
    class OrientationListener implements SensorEventListener {
        float dampX, dampY;
        float prevX, prevY;
        float[] mAccValues;
        float mAngle;

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

       private float curve(float accX, float accY) {
            float ang = (float) Math.atan2(accX, accY);
            ang /= (Math.PI / 2);
            return ang;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }

        public float getAngle() {
            return mAngle;
        }
    };
}