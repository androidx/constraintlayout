package android.support.viewsgraph3d;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.Debug;

import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v(TAG, Debug.getLoc()+">>>> onCreate ");

    }
}