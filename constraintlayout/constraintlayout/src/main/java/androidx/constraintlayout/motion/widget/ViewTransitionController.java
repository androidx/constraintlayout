/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.constraintlayout.motion.widget;

import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Container for ViewTransitions. It dispatches the run of a ViewTransition.
 * It receives animate calls
 */
public class ViewTransitionController {
    @NonNull
    private final MotionLayout mMotionLayout;
    @NonNull
    private ArrayList<ViewTransition> viewTransitions = new ArrayList<>();
    @Nullable
    private HashSet<View> mRelatedViews;
    private String TAG = "ViewTransitionController";

    public ViewTransitionController(@NonNull MotionLayout layout) {
        mMotionLayout = layout;
    }

    public void add(@NonNull ViewTransition viewTransition) {
        viewTransitions.add(viewTransition);
        mRelatedViews = null;

        if (viewTransition.getStateTransition() == ViewTransition.ONSTATE_SHARED_VALUE_SET) {
            listenForSharedVariable(viewTransition, true);
        } else if (viewTransition.getStateTransition() == ViewTransition.ONSTATE_SHARED_VALUE_UNSET) {
            listenForSharedVariable(viewTransition, false);
        }
    }

    void remove(int id) {
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

    private void viewTransition(@NonNull ViewTransition vt, @NonNull View... view) {
        int currentId = mMotionLayout.getCurrentState();
        if (vt.mViewTransitionMode != ViewTransition.VIEWTRANSITIONMODE_NOSTATE) {
            if (currentId == -1) {
                Log.w(TAG, "Dont support transition within transition yet");
                return;
            }
            ConstraintSet current = mMotionLayout.getConstraintSet(currentId);
            if (current == null) {
                return;
            }
            vt.applyTransition(this, mMotionLayout, currentId, current, view);
        } else {
            vt.applyTransition(this, mMotionLayout, currentId, null, view);
        }
    }

    void enableViewTransition(int id, boolean enable) {
        for (ViewTransition viewTransition : viewTransitions) {
            if (viewTransition.getId() == id) {
                viewTransition.setEnable(enable);
                break;
            }
        }
    }

    boolean isViewTransitionEnabled(int id) {
        for (ViewTransition viewTransition : viewTransitions) {
            if (viewTransition.getId() == id) {
                return viewTransition.isEnabled();
            }
        }
        return false;
    }

    /**
     * Support call from MotionLayout.viewTransition
     *
     * @param id    the id of a ViewTransition
     * @param views the list of views to transition simultaneously
     */
    void viewTransition(int id, @NonNull View... views) {
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

    /**
     * this gets Touch events on the MotionLayout and can fire transitions on down or up
     *
     * @param event
     */
    void touchEvent(@NonNull MotionEvent event) {
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

    /**
     * Called by motionLayout during draw to allow ViewTransitions to asynchronously animate
     */
    void animate() {
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

    void invalidate() {
        mMotionLayout.invalidate();
    }

    boolean applyViewTransition(int viewTransitionId, @NonNull MotionController motionController) {
        for (ViewTransition viewTransition : viewTransitions) {
            if (viewTransition.getId() == viewTransitionId) {
                viewTransition.mKeyFrames.addAllFrames(motionController);
                return true;
            }
        }
        return false;
    }

    private void listenForSharedVariable(@NonNull ViewTransition viewTransition, boolean isSet) {
        int listen_for_id = viewTransition.getSharedValueID();
        int listen_for_value = viewTransition.getSharedValue();


        ConstraintLayout.getSharedValues().addListener((id, value, oldValue) -> {
                    int current_value = viewTransition.getSharedValueCurrent();
                    viewTransition.setSharedValueCurrent(value);
                    if (listen_for_id == id && current_value != value) {
                        if (isSet) {
                            if (listen_for_value == value) {
                                int count = mMotionLayout.getChildCount();

                                for (int i = 0; i < count; i++) {
                                    View view = mMotionLayout.getChildAt(i);
                                    if (viewTransition.matchesView(view)) {
                                        int currentId = mMotionLayout.getCurrentState();
                                        ConstraintSet current = mMotionLayout.getConstraintSet(currentId);
                                        viewTransition.applyTransition(this, mMotionLayout, currentId, current, view);
                                    }
                                }
                            }
                        } else { // not set
                            if (listen_for_value != value) {
                                int count = mMotionLayout.getChildCount();
                                for (int i = 0; i < count; i++) {
                                    View view = mMotionLayout.getChildAt(i);
                                    if (viewTransition.matchesView(view)) {
                                        int currentId = mMotionLayout.getCurrentState();
                                        ConstraintSet current = mMotionLayout.getConstraintSet(currentId);
                                        viewTransition.applyTransition(this, mMotionLayout, currentId, current, view);
                                    }
                                }
                            }
                        }
                    }
                }, viewTransition.getSharedValueID()
        );
    }

}
