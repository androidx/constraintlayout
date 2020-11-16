package androidx.constraintlayout.motion.widget;

import android.graphics.Rect;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintSet;

import java.util.ArrayList;
import java.util.HashSet;

public class ViewTransitionController {
    private final MotionScene mMotionScene;
    private final MotionLayout mMotionLayout;
    private ArrayList<ViewTransition> viewTransitions = new ArrayList<>();
    private HashSet<View> mRelatedViews;
    private String TAG = "ViewTransitionController";

    public ViewTransitionController(MotionScene motionScene, MotionLayout layout) {
        mMotionScene = motionScene;
        mMotionLayout = layout;
    }

    public void add(ViewTransition viewTransition) {
        viewTransitions.add(viewTransition);
        mRelatedViews = null;
    }

    public void remove(int id) {
        ViewTransition del = null;
        for (ViewTransition viewTransition : viewTransitions) {
            if (viewTransition.getId() == id) {
                del = viewTransition;
                break;
            }
        }
        if (del != null) {
            mRelatedViews = null;
            viewTransitions.remove(del);
        }
    }

    private void viewTransition(ViewTransition vt, View... view) {
        int currentId = mMotionLayout.getCurrentState();
        if (currentId == -1) {
            Log.w(TAG, "Dont support transition within transition yet");
            return;
        }
        ConstraintSet current = mMotionLayout.getConstraintSet(currentId);
        if (current == null) {
            return;
        }
        vt.applyTransition(this, mMotionLayout, currentId, current, view);

    }

    public void enableViewTransition(int id, boolean enable) {
        for (ViewTransition viewTransition : viewTransitions) {
            if (viewTransition.getId() == id) {
                viewTransition.setEnable(enable);
                break;
            }
        }
    }

    public boolean isViewTransitionEnabled(int id) {
        for (ViewTransition viewTransition : viewTransitions) {
            if (viewTransition.getId() == id) {
                return viewTransition.isEnabled();
            }
        }
        return false;
    }

    public void viewTransition(int id, View... views) {
        ViewTransition vt = null;
        ArrayList<View> list = new ArrayList<>();
        for (ViewTransition viewTransition : viewTransitions) {
            if (viewTransition.getId() == id) {
                vt = viewTransition;
                for (View view : views) {
                    if (viewTransition.checkTags(view)) {
                        list.add(view);
                    }
                }
                if (!list.isEmpty()) {
                    viewTransition(vt, list.toArray(new View[0]));
                    list.clear();
                }
            }
        }
        if (vt == null) {
            Log.e(TAG, " Could not find ViewTransition");
            return;
        }
    }

    public void touchEvent(MotionEvent event) {
        int currentId = mMotionLayout.getCurrentState();
        if (currentId == -1) {
            return;
        }
        if (mRelatedViews == null) {
            mRelatedViews = new HashSet<>();
             for (ViewTransition viewTransition : viewTransitions) {
                int count = mMotionLayout.getChildCount();
                for (int i = 0; i < count; i++) {
                    View view = mMotionLayout.getChildAt(i);
                    if (viewTransition.matchesView(view)) {
                        int id = view.getId();

                        mRelatedViews.add(view);
                    }
                }
            }


        }

        float x = event.getX();
        float y = event.getY();
        Rect rec = new Rect();
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_DOWN:

                ConstraintSet current = mMotionLayout.getConstraintSet(currentId);
                for (ViewTransition viewTransition : viewTransitions) {
                    if (viewTransition.supports(action)) {
                        for (View view : mRelatedViews) {
                            if (!viewTransition.matchesView(view)) {
                                continue;
                            }
                            view.getHitRect(rec);
                            if (rec.contains((int) x, (int) y)) {
                                viewTransition.applyTransition(this, mMotionLayout, currentId, current, view);
                            }

                        }
                    }
                }
                break;
        }
    }

    ArrayList<ViewTransition.Animate> animations;
    ArrayList<ViewTransition.Animate> removeList = new ArrayList<>();

    void addAnimation(ViewTransition.Animate animation) {
        if (animations == null) {
            animations = new ArrayList<>();
        }
        animations.add(animation);
    }

    void removeAnimation(ViewTransition.Animate animation) {
        removeList.add(animation);
    }

    public void animate() {
        if (animations == null) {
            return;
        }
        for (ViewTransition.Animate animation : animations) {
            animation.mutate();
        }
        animations.removeAll(removeList);
        removeList.clear();
        if (animations.isEmpty()) {
            animations = null;
        }
    }

    public void invalidate() {
        mMotionLayout.invalidate();
    }
}
