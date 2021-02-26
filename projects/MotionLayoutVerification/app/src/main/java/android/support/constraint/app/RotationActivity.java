package android.support.constraint.app;

import android.content.Context;
import android.content.res.Configuration;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.Debug;

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
        boolean landscape = (getWindowManager().getDefaultDisplay().getRotation() & 1) == 1;
        mMotionLayout.setState(landscape ? R.id.landscape : R.id.portrait, -1, -1);
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

    int previous_rotation;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onConfigurationChanged(@NonNull @NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int layout = getLayoutForOrientation();
        Log.v(TAG, Debug.getLoc() + " (@) " + Debug.getName(getApplicationContext(), layout));
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        mMotionLayout.rotateTo(layout, 400);
        previous_rotation = rotation;
    }

    private int getLayoutForOrientation() {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        Log.v(TAG, Debug.getLoc() + " " + rotation);
        boolean has_right = null != mMotionLayout.getConstraintSet(R.id.landscape_right);
        if (has_right) {
            int[] id = {R.id.portrait, R.id.landscape, R.id.portrait, R.id.landscape_right};
            return id[rotation];
        } else {
            int[] id = {R.id.portrait, R.id.landscape, R.id.portrait, R.id.landscape};
            return id[rotation];
        }

    }

}
