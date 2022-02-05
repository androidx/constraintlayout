package android.support.constraint.app;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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


            public void update(int width, int height, int id, View view, ConstraintLayout.LayoutParams params) {
                if (id == R.id.view) {
                    params.width = width/3;
                    params.leftMargin = -width/3;
                    params.verticalBias = (System.currentTimeMillis()%10000)/10000f;
                    view.post(()->view.requestLayout());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        params.setMarginStart(-width/3);

                    }
                }
            }

        });

    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

    }
}
