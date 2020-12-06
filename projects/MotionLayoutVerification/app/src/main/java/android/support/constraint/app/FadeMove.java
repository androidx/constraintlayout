package android.support.constraint.app;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;

import androidx.constraintlayout.motion.widget.Debug;
import androidx.constraintlayout.motion.widget.KeyAttributes;
import androidx.constraintlayout.motion.widget.MotionController;
import androidx.constraintlayout.motion.widget.MotionHelper;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.HashMap;

public class FadeMove extends MotionHelper {
    public static final String TAG = "Fademove";

    public FadeMove(Context context) {
        super(context);
    }

    public FadeMove(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FadeMove(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isDecorator() {
        return true;
    }

    @Override
    public void onPreSetup(MotionLayout motionLayout, HashMap<View, MotionController> mFrameArrayList) {
        Log.v(TAG, Debug.getLoc());
        View[] views = getViews((ConstraintLayout) this.getParent());

        if (views == null) {
            Log.v(TAG, Debug.getLoc() + " views = null");
            return;
        }
        ViewParent parent = getParent();
        KeyAttributes alpha1 = new KeyAttributes();
        KeyAttributes alpha2 = new KeyAttributes();
        alpha1.setValue("alpha", 0);
        alpha2.setValue("alpha", 0);
        alpha1.setFramePosition(20);
        alpha2.setFramePosition(80);

        for (int i = 0; i < views.length; i++) {
            MotionController mc = mFrameArrayList.get(views[i]);
            float x = mc.getFinalX()-mc.getStartX();
            float y =  mc.getFinalY()-mc.getStartY();
            double dist = Math.hypot(y, x);
            if (dist > 20) {
                mc.addKey(alpha1);
                mc.addKey(alpha2);
            }
        }
    }
}
