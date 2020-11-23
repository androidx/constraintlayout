package android.support.constraint.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.Debug;
import androidx.constraintlayout.motion.widget.MotionLayout;

import java.util.Random;

public class Demo00vt extends AppCompatActivity {
    MotionLayout motionLayout;
    Handler handler = new Handler(Looper.getMainLooper());
    Random random = new Random();
    final int CONNECTED = 1;
    final int DISCONNECTED = 0;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_0_vt);
        motionLayout = findViewById(R.id.motionLayout);
        randomEventGenerator();
        Log.v("Demo00vt", Debug.getLoc() + " ");

    }

    private void randomEventGenerator() {

        handler.postDelayed(() -> {
            networkStatus(random.nextBoolean() ? CONNECTED : DISCONNECTED);
            randomEventGenerator();
        }, random.nextInt(5000) + 1000);
    }

    void networkStatus(int status) {
        View faceView = findViewById(R.id.face);

        if (status == CONNECTED) {
            motionLayout.viewTransition(R.id.connect, faceView);
        } else {
            motionLayout.viewTransition(R.id.disconnect, faceView);
        }

    }
}
