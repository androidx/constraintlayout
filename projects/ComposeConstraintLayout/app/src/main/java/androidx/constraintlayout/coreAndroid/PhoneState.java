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
package androidx.constraintlayout.coreAndroid;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;

import androidx.constraintlayout.core.state.WidgetFrame;

public class PhoneState {
    final float D_FACTOR = 10;
    float[] mAccValues;
    public static float phoneOrientation;

    public PhoneState(Activity activity) {
        SensorManager sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            sensorManager.registerListener(gravity_listener, sensor, 10000);
        }
    }

    SensorEventListener gravity_listener = new SensorEventListener() {
        float dampX, dampY;
        float prevX, prevY;

        float curve(float accX, float accY) {
            float ang = (float) Math.atan2(accX, accY);
            ang /= (Math.PI / 2);
            return ang;
        }
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
            phoneOrientation = curve(dampX, dampY);
            WidgetFrame.phone_orientation = phoneOrientation; // define a global state in core
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };

}
