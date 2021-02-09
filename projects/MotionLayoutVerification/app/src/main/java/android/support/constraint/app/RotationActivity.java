package android.support.constraint.app;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;

import org.jetbrains.annotations.NotNull;

public class RotationActivity extends AppCompatActivity {
    private static final String TAG = "CheckSharedValues";
    String layout_name;
   MotionLayout mMotionLayout;

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

    }

    @Override
    public void onConfigurationChanged(@NonNull @NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int newOrientation = newConfig.orientation;

        switch(newOrientation) {

            case Configuration.ORIENTATION_LANDSCAPE:
                mMotionLayout.setState(R.id.portrait_in_landscape,-1,-1);
                mMotionLayout.transitionToState(R.id.landscape,1000);
                break;

            case Configuration.ORIENTATION_PORTRAIT:
                mMotionLayout.setState(R.id.landscape_in_portrait,-1,-1);
                mMotionLayout.transitionToState(R.id.portrait,1000);

                break;

        }
    }

}
