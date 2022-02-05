package android.support.constraint.app;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.core.ConstraintDelta;
import androidx.constraintlayout.motion.widget.Debug;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;

public class CheckCLPlugin extends AppCompatActivity {
    private static final String TAG = "Bug010";
    String layout_name;
    ConstraintLayout mConstraintLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extra = getIntent().getExtras();
        String prelayout = extra.getString(Utils.KEY);
        setTitle(layout_name = prelayout);
        Context ctx = getApplicationContext();
        int id = ctx.getResources().getIdentifier(prelayout, "layout", ctx.getPackageName());
        setContentView(id);
        mConstraintLayout  = Utils.findConstraintLayout(this);
        mConstraintLayout.addValueModifier(new ConstraintLayout.ValueModifier() {

            public void update(int width, int height, ConstraintDelta delta) {
                Log.v(TAG, Debug.getLoc()+ " "+width+", "+height);
                delta.setValue(R.id.view, ConstraintDelta.LAYOUT_WIDTH, height/3);
                delta.setValue(R.id.view, ConstraintDelta.START_MARGIN, -width/3);
            }
        });

    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

    }
}
