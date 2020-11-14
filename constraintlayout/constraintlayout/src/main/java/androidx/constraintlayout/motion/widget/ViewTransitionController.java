package androidx.constraintlayout.motion.widget;

import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintSet;

import java.util.ArrayList;

public class ViewTransitionController {
    private final MotionScene mMotionScene;
    private final MotionLayout mMotionLayout;
    ArrayList<ViewTransition> viewTransitions = new ArrayList<>();
    private String TAG = "ViewTransitionController";

    public ViewTransitionController(MotionScene motionScene, MotionLayout layout) {
        mMotionScene = motionScene;
        mMotionLayout = layout;
    }

    public void add(ViewTransition viewTransition) {
        viewTransitions.add(viewTransition);
    }


    private void viewTransition(ViewTransition vt, View... view) {
        int currentId = mMotionLayout.getCurrentState();
        if (currentId == -1) {
            Log.v(TAG, "Dont support transition within transition yet");
            return;
        }
        ConstraintSet current = mMotionLayout.getConstraintSet(currentId);
        if (current == null) {
            Log.v(TAG, "constraintSet == null");
            return;
        }

        vt.applyTransition(mMotionLayout, currentId, current, view);

    }

    public void viewTransition(int id, View... view) {
        ViewTransition vt = null;
        for (ViewTransition viewTransition : viewTransitions) {
            Log.v(TAG, Debug.getLoc() + " vt = " + Debug.getName(mMotionLayout.getContext(), viewTransition.getId()));
            if (viewTransition.getId() == id) {
                vt = viewTransition;
            }
        }
        if (vt == null) {
            Log.e(TAG, " Could not find ViewTransition");
            return;
        }
        viewTransition(vt, view);

    }

    public void touchEvent(MotionEvent event) {
        int count = mMotionLayout.getChildCount();
        float x = event.getX();
        float y = event.getY();
        Rect rec = new Rect();
        for (int i = 0; i < count; i++) {
            View view = mMotionLayout.getChildAt(i);
            view.getHitRect(rec);
            if (rec.contains((int) x, (int) y)) {
                Log.v(TAG, Debug.getLoc() + " hit " + Debug.getName(view));
            }
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                Log.v(TAG, Debug.getLoc() + " " + event.toString());
                break;
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_OUTSIDE:
        }
    }
}
