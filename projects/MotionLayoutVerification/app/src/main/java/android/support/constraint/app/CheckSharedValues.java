package android.support.constraint.app;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.Debug;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Arrays;

public class CheckSharedValues extends AppCompatActivity {
    private static final String TAG = "CheckSharedValues";
    Class[] demos = {};//Demo00vt.class};
    String layout_name;
    private SensorManager sensorManager;

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
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        Sensor sensor2 = sensorManager.getDefaultSensor(Sensor.TYPE_HINGE_ANGLE);

        Log.v(TAG, Debug.getLoc());
        MotionLayout ml = Utils.findMotionLayout(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            sensorManager.registerListener(gravity_listener, sensor, 100000);
            sensorManager.registerListener(angle_listener, sensor2, 100000);
        }
    }

    SensorEventListener gravity_listener = new SensorEventListener() {
        boolean lastState = false;

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            boolean state = sensorEvent.values[2] > 8;
            if (state == lastState) {
                return;
            }
            lastState = state;
            Log.v(TAG, Debug.getLoc() + " Acceleration = " + Arrays.toString(sensorEvent.values));
            ConstraintLayout.getSharedValues().fireNewValue(R.id.layFlat, state ? 1 : 0);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }

    };

    SensorEventListener angle_listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Log.v(TAG, Debug.getLoc() + " Angle = " + Arrays.toString(sensorEvent.values));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }

    };

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(gravity_listener);
    }
}
