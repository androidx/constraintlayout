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

package androidx.constraintlayout.helper.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.motion.widget.MotionHelper;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.motion.widget.MotionScene;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.R;

import java.util.ArrayList;

public class Carousel extends MotionHelper {
    private static final boolean DEBUG = false;
    private static final String TAG = "Carousel";
    private Adapter mAdapter = null;
    private ArrayList<View> mList = new ArrayList<>();
    private int mPreviousIndex = 0;
    private int mIndex = 0;
    private MotionLayout mMotionLayout;
    private int firstViewReference = -1;
    private boolean infiniteCarousel = false;
    private boolean firstShow = true;
    private int backwardTransition = -1;
    private int forwardTransition = -1;
    private int previousState = -1;
    private int nextState = -1;
    private float dampening = 0.9f;
    private int startIndex = 0;
    private int emptyViewBehavior = INVISIBLE;

    public static final int TOUCH_UP_IMMEDIATE_STOP = 1;
    public static final int TOUCH_UP_CARRY_ON = 2;

    private int touchUpMode = TOUCH_UP_IMMEDIATE_STOP;
    private float velocityThreshold = 2f;

    public interface Adapter {
        int count();
        void populate(View view, int index);
        void onNewItem(int mIndex);
    }

    public Carousel(Context context) {
        super(context);
    }

    public Carousel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Carousel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Carousel);
            final int N = a.getIndexCount();
            for (int i = 0; i < N; i++) {
                int attr = a.getIndex(i);
                if (attr == R.styleable.Carousel_carousel_firstView) {
                    firstViewReference = a.getResourceId(attr, firstViewReference);
                } else if (attr == R.styleable.Carousel_carousel_backwardTransition) {
                    backwardTransition = a.getResourceId(attr, backwardTransition);
                } else if (attr == R.styleable.Carousel_carousel_forwardTransition) {
                    forwardTransition = a.getResourceId(attr, forwardTransition);
                } else if (attr == R.styleable.Carousel_carousel_emptyViewsBehavior) {
                    emptyViewBehavior = a.getInt(attr, emptyViewBehavior);
                } else if (attr == R.styleable.Carousel_carousel_previousState) {
                    previousState = a.getResourceId(attr, previousState);
                } else if (attr == R.styleable.Carousel_carousel_nextState) {
                    nextState = a.getResourceId(attr, nextState);
                } else if (attr == R.styleable.Carousel_carousel_touchUp_dampeningFactor) {
                    dampening = a.getFloat(attr, dampening);
                } else if (attr == R.styleable.Carousel_carousel_touchUpMode) {
                    touchUpMode = a.getInt(attr, touchUpMode);
                } else if (attr == R.styleable.Carousel_carousel_touchUp_velocityThreshold) {
                    velocityThreshold = a.getFloat(attr, velocityThreshold);
                } else if (attr == R.styleable.Carousel_carousel_infinite) {
                    infiniteCarousel = a.getBoolean(attr, infiniteCarousel);
                }
            }
            a.recycle();
        }
    }

    public void setAdapter(Adapter adapter) { mAdapter = adapter; }

    public void refresh() {
        final int count = mList.size();
        for (int i = 0; i < count; i++) {
            View view = mList.get(i);
            if (mAdapter.count() == 0) {
                updateViewVisibility(view, emptyViewBehavior);
            } else {
                updateViewVisibility(view, VISIBLE);
            }
        }
        mMotionLayout.rebuildScene();
        updateItems();
    }

    @Override
    public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {
        if (DEBUG) {
            System.out.println("onTransitionChange from " + startId + " to " + endId + " progress " + progress);
        }
        mLastStartId = startId;
    }

    int mLastStartId = -1;

    @Override
    public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
        System.out.println("on transition completed");
        mPreviousIndex = mIndex;
        firstShow = false;
        if (currentId == nextState) {
            mIndex++;
            System.out.println("increment index...");
        } else if (currentId == previousState) {
            mIndex--;
            System.out.println("decrement index...");
        }
        if (mIndex >= mAdapter.count()) {
            if (infiniteCarousel) {
                mIndex = 0;
            } else {
                mIndex = mAdapter.count() - 1;
            }
            System.out.println("index capped... " + mIndex);
        }
        if (mIndex < 0) {
            if (infiniteCarousel) {
                mIndex = mAdapter.count() - 1;
            } else {
                mIndex = 0;
            }
            System.out.println("index zeroed... ");
        }

        if (mPreviousIndex != mIndex) {
            mMotionLayout.post(mUpdateRunnable);
        }
    }

    private void enableAllTransitions(boolean enable) {
        ArrayList<MotionScene.Transition> transitions = mMotionLayout.getDefinedTransitions();
        for (MotionScene.Transition transition : transitions) {
            transition.setEnable(enable);
        }
    }

    private boolean enableTransition(int transitionID, boolean enable) {
        if (transitionID == -1) {
            return false;
        }
        if (mMotionLayout == null) {
            return false;
        }
        MotionScene.Transition transition = mMotionLayout.getTransition(transitionID);
        if (transition == null) {
            return false;
        }
        if (enable == transition.isEnabled()) {
            return false;
        }
        transition.setEnable(enable);
        return true;
    }

    Runnable mUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            mMotionLayout.setProgress(0);
            updateItems();
            mAdapter.onNewItem(mIndex);
            float velocity = mMotionLayout.getVelocity();
            if (touchUpMode == TOUCH_UP_CARRY_ON && velocity > velocityThreshold && mIndex < mAdapter.count() - 1) {
                final float v = velocity * dampening;
                if (mIndex == 0 && mPreviousIndex > mIndex) {
                    // don't touch animate when reaching the first item
                    return;
                }
                if (mIndex == mAdapter.count() - 1 && mPreviousIndex < mIndex) {
                    // don't touch animate when reaching the last item
                    return;
                }
                mMotionLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mMotionLayout.touchAnimateTo(MotionLayout.TOUCH_UP_DECELERATE_AND_COMPLETE, 1, v);
                    }
                });
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        MotionLayout container = null;
        if (getParent() instanceof MotionLayout) {
            container = (MotionLayout) getParent();
        } else {
            return;
        }
        for (int i = 0; i < mCount; i++) {
            int id = mIds[i];
            View view = container.getViewById(id);
            if (firstViewReference == id) {
                startIndex = i;
            }
            mList.add(view);
        }
        mMotionLayout = container;
        // set up transitions if needed
        if (touchUpMode == TOUCH_UP_CARRY_ON) {
            MotionScene.Transition forward = mMotionLayout.getTransition(forwardTransition);
            if (forward != null) {
                forward.setOnTouchUp(MotionLayout.TOUCH_UP_DECELERATE_AND_COMPLETE);
            }
            MotionScene.Transition backward = mMotionLayout.getTransition(backwardTransition);
            if (backward != null) {
                backward.setOnTouchUp(MotionLayout.TOUCH_UP_DECELERATE_AND_COMPLETE);
            }
        }
        updateItems();
    }

    /**
     * Update the view visibility on the different constraintsets
     *
     * @param view
     * @param visibility
     * @return
     */
    private boolean updateViewVisibility(View view, int visibility) {
        if (mMotionLayout == null) {
            return false;
        }
        boolean needsMotionSceneRebuild = false;
        int[] constraintsets = mMotionLayout.getConstraintSetIds();
        for (int i = 0; i < constraintsets.length; i++) {
            needsMotionSceneRebuild |= updateViewVisibility(constraintsets[i], view, visibility);
        }
        return needsMotionSceneRebuild;
    }

    private boolean updateViewVisibility(int constraintSetId, View view, int visibility) {
        ConstraintSet constraintSet = mMotionLayout.getConstraintSet(constraintSetId);
        if (constraintSet == null) {
            return false;
        }
        ConstraintSet.Constraint constraint = constraintSet.getConstraint(view.getId());
        if (constraint == null) {
            return false;
        }
        constraint.propertySet.mVisibilityMode = ConstraintSet.VISIBILITY_MODE_IGNORE;
//        if (constraint.propertySet.visibility == visibility) {
//            return false;
//        }
//        constraint.propertySet.visibility = visibility;
        view.setVisibility(visibility);
        return true;
    }

    private void updateItems() {
        if (mAdapter == null) {
            return;
        }
        if (mMotionLayout == null) {
            return;
        }
        if (DEBUG) {
            System.out.println("Update items, index: " + mIndex);
        }
        final int viewCount = mList.size();
        for (int i = 0; i < viewCount; i++) {
            // mIndex should map to i == startIndex
            View view = mList.get(i);
            int index = mIndex + i - startIndex;
            if (index < 0) {
                if (infiniteCarousel) {
                    updateViewVisibility(view, VISIBLE);
                    mAdapter.populate(view, mAdapter.count() + index);
                } else {
                    updateViewVisibility(view, emptyViewBehavior);
                }
            } else if (index >= mAdapter.count()) {
                if (infiniteCarousel) {
                    if (index == mAdapter.count()) {
                        index = 0;
                    } else if (index > mAdapter.count()) {
                        index = index % mAdapter.count() ;
                    }
                    updateViewVisibility(view, VISIBLE);
                    mAdapter.populate(view, index);
                } else {
                    updateViewVisibility(view, emptyViewBehavior);
                }
            } else {
                updateViewVisibility(view, VISIBLE);
                mAdapter.populate(view, index);
            }
        }

        if (backwardTransition == -1 || forwardTransition == -1) {
            Log.w(TAG, "No backward or forward transitions defined for Carousel!");
            return;
        }

        final int count = mAdapter.count();
        if (infiniteCarousel) {
            if (firstShow) {
                if (mIndex == 0) {
                    enableTransition(backwardTransition, false);
                } else {
                    enableTransition(backwardTransition, true);
                    mMotionLayout.setTransition(backwardTransition);
                }
            } else {
                enableTransition(backwardTransition, true);
                mMotionLayout.setTransition(backwardTransition);
            }

        } else {
            if (mIndex == 0) {
                enableTransition(backwardTransition, false);
            } else {
                enableTransition(backwardTransition, true);
                mMotionLayout.setTransition(backwardTransition);
            }
            if (mIndex == count - 1) {
                enableTransition(forwardTransition, false);
            } else {
                enableTransition(forwardTransition, true);
                mMotionLayout.setTransition(forwardTransition);
            }
        }

    }

}
