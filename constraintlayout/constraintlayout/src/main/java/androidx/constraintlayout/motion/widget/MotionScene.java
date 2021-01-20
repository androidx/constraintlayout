/*
 * Copyright (C) 2018 The Android Open Source Project
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

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.util.Xml;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;

import androidx.constraintlayout.core.motion.utils.Easing;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.R;
import androidx.constraintlayout.widget.StateSet;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The information to transition between multiple ConstraintSets
 * This Class is meant to be used from XML
 *
 * @hide
 */

public class MotionScene {
    public static final String TAG = "MotionScene";
    private static final boolean DEBUG = false;
    final static int TRANSITION_BACKWARD = 0;
    final static int TRANSITION_FORWARD = 1;
    private static final int SPLINE_STRING = -1;
    private static final int INTERPOLATOR_REFRENCE_ID = -2;
    public static final int UNSET = -1;
    private final MotionLayout mMotionLayout;
    StateSet mStateSet = null;
    Transition mCurrentTransition = null;
    private boolean mDisableAutoTransition = false;
    private ArrayList<Transition> mTransitionList = new ArrayList<>();
    private Transition mDefaultTransition = null;
    private ArrayList<Transition> mAbstractTransitionList = new ArrayList<>();

    private SparseArray<ConstraintSet> mConstraintSetMap = new SparseArray<>();
    private HashMap<String, Integer> mConstraintSetIdMap = new HashMap<>();
    private SparseIntArray mDeriveMap = new SparseIntArray();
    private boolean DEBUG_DESKTOP = false;
    private int mDefaultDuration = 400;
    private int mLayoutDuringTransition = 0;
    public static final int LAYOUT_IGNORE_REQUEST = 0;
    public static final int LAYOUT_HONOR_REQUEST = 1;
    private MotionEvent mLastTouchDown;
    private boolean mIgnoreTouch = false;
    private boolean mMotionOutsideRegion = false;
    private MotionLayout.MotionTracker mVelocityTracker; // used to support fling
    private boolean mRtl;
    private static final String MOTIONSCENE_TAG = "MotionScene";
    private static final String TRANSITION_TAG = "Transition";
    private static final String ONSWIPE_TAG = "OnSwipe";
    private static final String ONCLICK_TAG = "OnClick";
    private static final String STATESET_TAG = "StateSet";
    private static final String INCLUDE_TAG = "include";
    private static final String KEYFRAMESET_TAG = "KeyFrameSet";
    private static final String CONSTRAINTSET_TAG = "ConstraintSet";
    private static final String VIEW_TRANSITION = "ViewTransition";
    final ViewTransitionController mViewTransitionController;

    /**
     * Set the transition between two constraint set / states.
     * The transition will get created between the two sets
     * if it doesn't exist already.
     *
     * @param beginId id of the start constraint set or state
     * @param endId   id of the end constraint set or state
     */
    void setTransition(int beginId, int endId) {
        int start = beginId;
        int end = endId;
        if (mStateSet != null) {
            int tmp = mStateSet.stateGetConstraintID(beginId, -1, -1);
            if (tmp != -1) {
                start = tmp;
            }
            tmp = mStateSet.stateGetConstraintID(endId, -1, -1);
            if (tmp != -1) {
                end = tmp;
            }
        }
        if (DEBUG) {
            Log.v(TAG, Debug.getLocation() + " setTransition " +
                    Debug.getName(mMotionLayout.getContext(), beginId) + " -> " +
                    Debug.getName(mMotionLayout.getContext(), endId));
        }
        if (mCurrentTransition != null) {
            if (mCurrentTransition.mConstraintSetEnd == endId &&
                    mCurrentTransition.mConstraintSetStart == beginId) {
                return;
            }
        }
        for (Transition transition : mTransitionList) {
            if ((transition.mConstraintSetEnd == end
                    && transition.mConstraintSetStart == start)
                    || (transition.mConstraintSetEnd == endId
                    && transition.mConstraintSetStart == beginId)) {
                if (DEBUG) {
                    Log.v(TAG, Debug.getLocation() + " forund transition  " +
                            Debug.getName(mMotionLayout.getContext(), beginId) + " -> " +
                            Debug.getName(mMotionLayout.getContext(), endId));
                }
                mCurrentTransition = transition;
                if (mCurrentTransition != null && mCurrentTransition.mTouchResponse != null) {
                    mCurrentTransition.mTouchResponse.setRTL(mRtl);
                }
                return;
            }
        }
        // No transition defined for this so we will create one?
        Transition matchTransiton = mDefaultTransition;
        for (Transition transition : mAbstractTransitionList) {
            if (transition.mConstraintSetEnd == endId) {
                matchTransiton = transition;
            }

        }
        Transition t = new Transition(this, matchTransiton);

        t.mConstraintSetStart = start;
        t.mConstraintSetEnd = end;
        if (start != UNSET) {
            mTransitionList.add(t);
        }
        mCurrentTransition = t;
    }

    /**
     * Add a transition to the motion scene. If a transition with the same id already exists
     * in the scene, the new transition will replace the existing one.
     *
     * @throws IllegalArgumentException if the transition does not have an id.
     */
    public void addTransition(Transition transition) {
        int index = getIndex(transition);
        if (index == -1) {
            mTransitionList.add(transition);
        } else {
            mTransitionList.set(index, transition);
        }
    }

    /**
     * Remove the transition with the matching id from the motion scene. If no matching transition
     * is found, it does nothing.
     *
     * @throws IllegalArgumentException if the transition does not have an id.
     */
    public void removeTransition(Transition transition) {
        int index = getIndex(transition);
        if (index != -1) {
            mTransitionList.remove(index);
        }
    }

    /**
     * @return the index in the transition list. -1 if transition wasn't found.
     */
    private int getIndex(Transition transition) {
        int id = transition.mId;
        if (id == UNSET) {
            throw new IllegalArgumentException("The transition must have an id");
        }

        int index = 0;
        for (; index < mTransitionList.size(); index++) {
            if (mTransitionList.get(index).mId == id) {
                return index;
            }
        }

        return -1;
    }

    /**
     * @return true if the layout is valid for the scene. False otherwise. Use it for the debugging
     * purposes.
     */
    public boolean validateLayout(MotionLayout layout) {
        return (layout == mMotionLayout && layout.mScene == this);
    }

    /**
     * Set the transition to be the current transition of the motion scene.
     *
     * @param transition a transition to be set. The transition must exist within the motion scene.
     *                   (e.g. {@link #addTransition(Transition)})
     */
    public void setTransition(Transition transition) {
        mCurrentTransition = transition;
        if (mCurrentTransition != null && mCurrentTransition.mTouchResponse != null) {
            mCurrentTransition.mTouchResponse.setRTL(mRtl);
        }
    }

    private int getRealID(int stateid) {
        if (mStateSet != null) {
            int tmp = mStateSet.stateGetConstraintID(stateid, -1, -1);
            if (tmp != -1) {
                return tmp;
            }
        }
        return stateid;
    }

    public List<Transition> getTransitionsWithState(int stateid) {
        stateid = getRealID(stateid);
        ArrayList<Transition> ret = new ArrayList<>();
        for (Transition transition : mTransitionList) {
            if (transition.mConstraintSetStart == stateid || transition.mConstraintSetEnd == stateid) {
                ret.add(transition);
            }

        }
        return ret;
    }

    public void addOnClickListeners(MotionLayout motionLayout, int currentState) {
        // remove all on clicks listeners
        for (Transition transition : mTransitionList) {
            if (transition.mOnClicks.size() > 0) {
                for (Transition.TransitionOnClick onClick : transition.mOnClicks) {
                    onClick.removeOnClickListeners(motionLayout);
                }
            }
        }
        for (Transition transition : mAbstractTransitionList) {
            if (transition.mOnClicks.size() > 0) {
                for (Transition.TransitionOnClick onClick : transition.mOnClicks) {
                    onClick.removeOnClickListeners(motionLayout);
                }
            }
        }
        // add back all the listeners that are needed
        for (Transition transition : mTransitionList) {
            if (transition.mOnClicks.size() > 0) {
                for (Transition.TransitionOnClick onClick : transition.mOnClicks) {
                    onClick.addOnClickListeners(motionLayout, currentState, transition);
                }
            }
        }
        for (Transition transition : mAbstractTransitionList) {
            if (transition.mOnClicks.size() > 0) {
                for (Transition.TransitionOnClick onClick : transition.mOnClicks) {
                    onClick.addOnClickListeners(motionLayout, currentState, transition);
                }
            }
        }
    }

    public Transition bestTransitionFor(int currentState, float dx, float dy, MotionEvent lastTouchDown) {
        List<Transition> candidates = null;
        if (currentState != -1) {
            candidates = getTransitionsWithState(currentState);
            float max = 0;
            Transition best = null;
            RectF cache = new RectF();
            for (Transition transition : candidates) {
                if (transition.mDisable) {
                    continue;
                }
                if (transition.mTouchResponse != null) {
                    transition.mTouchResponse.setRTL(mRtl);
                    RectF region = transition.mTouchResponse.getTouchRegion(mMotionLayout, cache);
                    if (region != null && lastTouchDown != null && (!region.contains(lastTouchDown.getX(), lastTouchDown.getY()))) {
                        continue;
                    }

                    float val = transition.mTouchResponse.dot(dx, dy);
                    if (transition.mTouchResponse.mIsRotateMode && lastTouchDown != null) {
                        float startX = lastTouchDown.getX() - transition.mTouchResponse.mRotateCenterX;
                        float startY = lastTouchDown.getY() - transition.mTouchResponse.mRotateCenterY;
                        float endX = dx + startX;
                        float endY = dy + startY;
                        double endAngle = Math.atan2(endY, endX);
                        double startAngle = Math.atan2(startX, startY);
                        val = (float) (endAngle - startAngle) * 10;
                    }
                    if (transition.mConstraintSetEnd == currentState) { // flip because this would be backwards
                        val *= -1;
                    } else {
                        val *= 1.1f; // slightly bias towards the transition which is start over end
                    }

                    if (val > max) {
                        max = val;
                        best = transition;
                    }
                }
            }
            if (DEBUG) {
                if (best != null) {
                    Log.v(TAG, Debug.getLocation() + "  ### BEST ----- " + best.debugString(mMotionLayout.getContext()) + " ----");
                } else {
                    Log.v(TAG, Debug.getLocation() + "  ### BEST ----- " + null + " ----");

                }
            }
            return best;
        }
        return mCurrentTransition;
    }

    public ArrayList<Transition> getDefinedTransitions() {
        return mTransitionList;
    }

    public Transition getTransitionById(int id) {
        for (Transition transition : mTransitionList) {
            if (transition.mId == id) {
                return transition;
            }
        }
        return null;
    }

    public int[] getConstraintSetIds() {
        int[] ids = new int[mConstraintSetMap.size()];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = mConstraintSetMap.keyAt(i);
        }
        return ids;
    }

    /**
     * This will launch a transition to another state if an autoTransition is enabled on
     * a Transition that matches the current state.
     *
     * @param motionLayout
     * @param currentState
     * @return
     * @hide
     */
    boolean autoTransition(MotionLayout motionLayout, int currentState) {
        if (isProcessingTouch()) {
            return false;
        }
        if (mDisableAutoTransition) {
            return false;
        }

        for (Transition transition : mTransitionList) {
            if (transition.mAutoTransition == Transition.AUTO_NONE) {
                continue;
            }
            if (mCurrentTransition == transition
                    && mCurrentTransition.isTransitionFlag(Transition.TRANSITION_FLAG_INTRA_AUTO)) {
                continue;
            }
            if (currentState == transition.mConstraintSetStart && (
                    transition.mAutoTransition == Transition.AUTO_ANIMATE_TO_END ||
                            transition.mAutoTransition == Transition.AUTO_JUMP_TO_END)) {
                motionLayout.setState(MotionLayout.TransitionState.FINISHED);
                motionLayout.setTransition(transition);
                if (transition.mAutoTransition == Transition.AUTO_ANIMATE_TO_END) {
                    motionLayout.transitionToEnd();
                    motionLayout.setState(MotionLayout.TransitionState.SETUP);
                    motionLayout.setState(MotionLayout.TransitionState.MOVING);
                } else {
                    motionLayout.setProgress(1);
                    motionLayout.evaluate(true);
                    motionLayout.setState(MotionLayout.TransitionState.SETUP);
                    motionLayout.setState(MotionLayout.TransitionState.MOVING);
                    motionLayout.setState(MotionLayout.TransitionState.FINISHED);
                    motionLayout.onNewStateAttachHandlers();
                }
                return true;
            }
            if (currentState == transition.mConstraintSetEnd && (
                    transition.mAutoTransition == Transition.AUTO_ANIMATE_TO_START ||
                            transition.mAutoTransition == Transition.AUTO_JUMP_TO_START)) {
                motionLayout.setState(MotionLayout.TransitionState.FINISHED);
                motionLayout.setTransition(transition);
                if (transition.mAutoTransition == Transition.AUTO_ANIMATE_TO_START) {
                    motionLayout.transitionToStart();
                    motionLayout.setState(MotionLayout.TransitionState.SETUP);
                    motionLayout.setState(MotionLayout.TransitionState.MOVING);
                } else {
                    motionLayout.setProgress(0);
                    motionLayout.evaluate(true);
                    motionLayout.setState(MotionLayout.TransitionState.SETUP);
                    motionLayout.setState(MotionLayout.TransitionState.MOVING);
                    motionLayout.setState(MotionLayout.TransitionState.FINISHED);
                    motionLayout.onNewStateAttachHandlers();
                }
                return true;
            }
        }
        return false;
    }

    private boolean isProcessingTouch() {
        return (mVelocityTracker != null);
    }

    public void setRtl(boolean rtl) {
        mRtl = rtl;
        if (mCurrentTransition != null && mCurrentTransition.mTouchResponse != null) {
            mCurrentTransition.mTouchResponse.setRTL(mRtl);
        }
    }

    public void viewTransition(int id, View ... view) {
        mViewTransitionController.viewTransition(id, view);
    }

    public void enableViewTransition(int id, boolean enable) {
        mViewTransitionController.enableViewTransition(id, enable);
    }

    public boolean isViewTransitionEnabled(int id) {
        return mViewTransitionController.isViewTransitionEnabled(id);
    }

    public boolean applyViewTransition(int viewTransitionId, MotionController motionController) {
        return mViewTransitionController.applyViewTransition(viewTransitionId, motionController);
    }
///////////////////////////////////////////////////////////////////////////////
// ====================== Transition ==========================================

    /**
     * Transition defines the interaction from one state to another.
     * With out a Transition object Transition between two stats involves strictly linear
     * interpolation
     */
    public static class Transition {
        private int mId = UNSET;
        private boolean mIsAbstract = false;
        private int mConstraintSetEnd = -1;
        private int mConstraintSetStart = -1;
        private int mDefaultInterpolator = 0;
        private String mDefaultInterpolatorString = null;
        private int mDefaultInterpolatorID = -1;
        private int mDuration = 400;
        private float mStagger = 0.0f;
        private final MotionScene mMotionScene;
        private ArrayList<KeyFrames> mKeyFramesList = new ArrayList<>();
        private TouchResponse mTouchResponse = null;
        private ArrayList<TransitionOnClick> mOnClicks = new ArrayList<>();
        private int mAutoTransition = 0;
        public static final int AUTO_NONE = 0;
        public static final int AUTO_JUMP_TO_START = 1;
        public static final int AUTO_JUMP_TO_END = 2;
        public static final int AUTO_ANIMATE_TO_START = 3;
        public static final int AUTO_ANIMATE_TO_END = 4;
        private boolean mDisable = false;
        private int mPathMotionArc = UNSET;
        private int mLayoutDuringTransition = 0;
        private int mTransitionFlags = 0;
        final static int TRANSITION_FLAG_FIRST_DRAW = 1;
        final static int TRANSITION_FLAG_INTRA_AUTO = 2;

        public int getLayoutDuringTransition() {
            return mLayoutDuringTransition;
        }

        public void addOnClick(Context context, XmlPullParser parser) {
            mOnClicks.add(new TransitionOnClick(context, this, parser));
        }

        /**
         * sets the autoTransitionType
         * On reaching a state auto transitions may be run based on
         * one of AUTO_NONE, AUTO_JUMP_TO_START, AUTO_JUMP_TO_END, AUTO_ANIMATE_TO_START, AUTO_ANIMATE_TO_END
         *
         * @return 0=NONE, 1=JUMP_TO_START, 2=JUMP_TO_END, 3=ANIMATE_TO_START, 4=ANIMATE_TO_END
         */
        public void setAutoTransition(int type) {
            mAutoTransition = type;
        }

        /**
         * return the autoTransitionType.
         * one of AUTO_NONE, AUTO_JUMP_TO_START, AUTO_JUMP_TO_END, AUTO_ANIMATE_TO_START, AUTO_ANIMATE_TO_END
         *
         * @return 0=NONE, 1=JUMP_TO_START, 2=JUMP_TO_END, 3=ANIMATE_TO_START, 4=ANIMATE_TO_END
         */
        public int getAutoTransition() {
            return mAutoTransition;
        }

        /**
         * Transitions can be given and ID. If unset it returns UNSET (-1)
         *
         * @return The Id of the Transition set in the MotionScene File or UNSET (-1)
         */
        public int getId() {
            return mId;
        }

        /**
         * Get the id of the constraint set to go to
         *
         * @return
         */
        public int getEndConstraintSetId() {
            return mConstraintSetEnd;
        }

        /**
         * Gets the id of the starting constraint set
         *
         * @return
         */
        public int getStartConstraintSetId() {
            return mConstraintSetStart;
        }

        /**
         * sets the duration of the transition
         *
         * @param duration in milliseconds
         */
        public void setDuration(int duration) {
            this.mDuration = duration;
        }

        /**
         * gets the default transition duration
         *
         * @return duration int milliseconds
         */
        public int getDuration() {
            return mDuration;
        }

        /**
         * Gets the stagger value.
         *
         * @return
         */
        public float getStagger() {
            return mStagger;
        }

        public List<KeyFrames> getKeyFrameList() {
            return mKeyFramesList;
        }

        /*
        *
         */
        public void  addtKeyFrame( KeyFrames keyFrames) {
              mKeyFramesList.add(keyFrames);
        }

        /**
         * Get the onClick handlers.
         *
         * @return list of on click handler
         */
        public List<TransitionOnClick> getOnClickList() {
            return mOnClicks;
        }

        /**
         * Get the Touch response manager
         *
         * @return
         */
        public TouchResponse getTouchResponse() {
            return mTouchResponse;
        }

        /**
         * Sets the stagger value.
         * A Stagger value of zero means no stagger.
         * A Stagger value of 1 means the last view starts moving at .5 progress
         *
         * @param stagger
         */
        public void setStagger(float stagger) {
            mStagger = stagger;
        }

        /**
         * Sets the pathMotionArc for the all motions in this transition.
         * if set to UNSET (default) it reverts to the setting of the constraintSet
         *
         * @param arcMode
         */
        public void setPathMotionArc(int arcMode) {
            mPathMotionArc = arcMode;
        }

        /**
         * gets the pathMotionArc for the all motions in this transition.
         * if set to UNSET (default) it reverts to the setting of the constraintSet
         *
         * @return arcMode
         */
        public int getPathMotionArc() {
            return mPathMotionArc;
        }

        /**
         * Returns true if this Transition can be auto considered for transition
         * Default is enabled
         */
        public boolean isEnabled() {
            return !mDisable;
        }

        /**
         * enable or disable the Transition. If a Transition is disabled it is not eligible
         * for automatically switching to.
         *
         * @param enable
         */
        public void setEnable(boolean enable) {
            mDisable = !enable;
        }

        /**
         * Print a debug string indicating the starting and ending state of the transition
         *
         * @param context
         * @return
         */
        public String debugString(Context context) {

            String ret;
            if (mConstraintSetStart == UNSET) {
                ret = "null";
            } else {
                ret = context.getResources().getResourceEntryName(mConstraintSetStart);
            }
            if (mConstraintSetEnd == UNSET) {
                ret += " -> " + "null";
            } else {
                ret += " -> " + context.getResources().getResourceEntryName(mConstraintSetEnd);
            }
            return ret;
        }

        public boolean isTransitionFlag(int flag) {
            return 0 != (mTransitionFlags & flag);
        }

        public void setOnTouchUp(int touchUpMode) {
            TouchResponse touchResponse = getTouchResponse();
            if (touchResponse != null) {
                touchResponse.setTouchUpMode(touchUpMode);
            }
        }

        static class TransitionOnClick implements View.OnClickListener {
            private final Transition mTransition;
            int mTargetId = UNSET;
            int mMode = 0x11;
            public static final int ANIM_TO_END = 0x0001;
            public static final int ANIM_TOGGLE = 0x0011;
            public static final int ANIM_TO_START = 0x0010;
            public static final int JUMP_TO_END = 0x100;
            public static final int JUMP_TO_START = 0x1000;

            public TransitionOnClick(Context context, Transition transition, XmlPullParser parser) {
                mTransition = transition;
                TypedArray a = context.obtainStyledAttributes(Xml.asAttributeSet(parser), R.styleable.OnClick);
                final int N = a.getIndexCount();
                for (int i = 0; i < N; i++) {
                    int attr = a.getIndex(i);
                    if (attr == R.styleable.OnClick_targetId) {
                        mTargetId = a.getResourceId(attr, mTargetId);
                    } else if (attr == R.styleable.OnClick_clickAction) {
                        mMode = a.getInt(attr, mMode);
                    }
                }
                a.recycle();
            }

            public void addOnClickListeners(MotionLayout motionLayout, int currentState, Transition transition) {
                View v = mTargetId == UNSET ? motionLayout : motionLayout.findViewById(mTargetId);
                if (v == null) {
                    Log.e(TAG, "OnClick could not find id " + mTargetId);
                    return;
                }
                int start = transition.mConstraintSetStart;
                int end = transition.mConstraintSetEnd;
                if (start == UNSET) { // does not require a known end state
                    v.setOnClickListener(this);
                    return;
                }

                boolean listen = ((mMode & ANIM_TO_END) != 0) && currentState == start;
                listen |= ((mMode & JUMP_TO_END) != 0) && currentState == start;
                listen |= ((mMode & ANIM_TO_END) != 0) && currentState == start;
                listen |= ((mMode & ANIM_TO_START) != 0) && currentState == end;
                listen |= ((mMode & JUMP_TO_START) != 0) && currentState == end;

                if (listen) {
                    v.setOnClickListener(this);
                }
            }

            public void removeOnClickListeners(MotionLayout motionLayout) {
                if (mTargetId == UNSET) {
                    return;
                }
                View v = motionLayout.findViewById(mTargetId);
                if (v == null) {
                    Log.e(TAG, " (*)  could not find id " + mTargetId);
                    return;
                }
                v.setOnClickListener(null);
            }

            boolean isTransitionViable(Transition current, MotionLayout tl) {
                if (mTransition == current) {
                    return true;
                }
                int dest = mTransition.mConstraintSetEnd;
                int from = mTransition.mConstraintSetStart;
                if (from == UNSET) {
                    return tl.mCurrentState != dest;
                }
                return (tl.mCurrentState == from) || (tl.mCurrentState == dest);

            }

            @Override
            public void onClick(View view) {
                MotionLayout tl = mTransition.mMotionScene.mMotionLayout;
                if (!tl.isInteractionEnabled()) {
                    return;
                }
                if (mTransition.mConstraintSetStart == UNSET) {
                    int currentState = tl.getCurrentState();
                    if (currentState == UNSET) {
                        tl.transitionToState(mTransition.mConstraintSetEnd);
                        return;
                    }
                    Transition t = new Transition(mTransition.mMotionScene, mTransition);
                    t.mConstraintSetStart = currentState;
                    t.mConstraintSetEnd = mTransition.mConstraintSetEnd;
                    tl.setTransition(t);
                    tl.transitionToEnd();
                    return;
                }
                Transition current = mTransition.mMotionScene.mCurrentTransition;
                boolean forward = ((mMode & ANIM_TO_END) != 0 || (mMode & JUMP_TO_END) != 0);
                boolean backward = ((mMode & ANIM_TO_START) != 0 || (mMode & JUMP_TO_START) != 0);
                boolean bidirectional = forward && backward;
                if (bidirectional) {
                    if (mTransition.mMotionScene.mCurrentTransition != mTransition) {
                        tl.setTransition(mTransition);
                    }
                    if (tl.getCurrentState() == tl.getEndState() || tl.getProgress() > 0.5f) {
                        forward = false;
                    } else {
                        backward = false;
                    }
                }
                if (isTransitionViable(current, tl)) {
                    if (forward && (mMode & ANIM_TO_END) != 0) {
                        tl.setTransition(mTransition);
                        tl.transitionToEnd();
                    } else if (backward && (mMode & ANIM_TO_START) != 0) {
                        tl.setTransition(mTransition);
                        tl.transitionToStart();
                    } else if (forward && (mMode & JUMP_TO_END) != 0) {
                        tl.setTransition(mTransition);
                        tl.setProgress(1);
                    } else if (backward && (mMode & JUMP_TO_START) != 0) {
                        tl.setTransition(mTransition);
                        tl.setProgress(0);
                    }
                }
            }
        }

        Transition(MotionScene motionScene, Transition global) {
            mMotionScene = motionScene;
            if (global != null) {
                mPathMotionArc = global.mPathMotionArc;
                mDefaultInterpolator = global.mDefaultInterpolator;
                mDefaultInterpolatorString = global.mDefaultInterpolatorString;
                mDefaultInterpolatorID = global.mDefaultInterpolatorID;
                mDuration = global.mDuration;
                mKeyFramesList = global.mKeyFramesList;
                mStagger = global.mStagger;
                mLayoutDuringTransition = global.mLayoutDuringTransition;
            }
        }

        /**
         * Create a transition
         *
         * @param id                   a unique id to represent the transition.
         * @param motionScene          the motion scene that the transition will be added to.
         * @param constraintSetStartId id of the ConstraintSet to be used for the start of
         *                             transition
         * @param constraintSetEndId   id of the ConstraintSet to be used for the end of transition
         */
        public Transition(
                int id,
                MotionScene motionScene,
                int constraintSetStartId,
                int constraintSetEndId) {
            mId = id;
            mMotionScene = motionScene;
            mConstraintSetStart = constraintSetStartId;
            mConstraintSetEnd = constraintSetEndId;
            mDuration = motionScene.mDefaultDuration;
            mLayoutDuringTransition = motionScene.mLayoutDuringTransition;
        }

        Transition(MotionScene motionScene, Context context, XmlPullParser parser) {
            mDuration = motionScene.mDefaultDuration;
            mLayoutDuringTransition = motionScene.mLayoutDuringTransition;
            mMotionScene = motionScene;
            fillFromAttributeList(motionScene, context, Xml.asAttributeSet(parser));
        }

        public void setInterpolatorInfo(int interpolator, String interpolatorString, int interpolatorID){
            mDefaultInterpolator = interpolator;
            mDefaultInterpolatorString = interpolatorString;
            mDefaultInterpolatorID = interpolatorID;
        }

        private void fillFromAttributeList(MotionScene motionScene, Context context, AttributeSet attrs) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Transition);
            fill(motionScene, context, a);
            a.recycle();
        }

        private void fill(MotionScene motionScene, Context context, TypedArray a) {
            final int N = a.getIndexCount();
            for (int i = 0; i < N; i++) {
                int attr = a.getIndex(i);
                if (attr == R.styleable.Transition_constraintSetEnd) {
                    mConstraintSetEnd = a.getResourceId(attr, UNSET);
                    String type = context.getResources().getResourceTypeName(mConstraintSetEnd);
                    if ("layout".equals(type)) {
                        ConstraintSet cSet = new ConstraintSet();
                        cSet.load(context, mConstraintSetEnd);
                        motionScene.mConstraintSetMap.append(mConstraintSetEnd, cSet);
                        if (DEBUG) {
                            Log.v(TAG, " constraint Set end loaded from layout " + Debug.getName(context, mConstraintSetEnd));
                        }
                    } else if ("xml".equals(type)) {
                        int id = motionScene.parseInclude(context, mConstraintSetEnd);
                        mConstraintSetEnd = id;
                    }
                } else if (attr == R.styleable.Transition_constraintSetStart) {
                    mConstraintSetStart = a.getResourceId(attr, mConstraintSetStart);
                    String type = context.getResources().getResourceTypeName(mConstraintSetStart);
                    if ("layout".equals(type)) {
                        ConstraintSet cSet = new ConstraintSet();
                        cSet.load(context, mConstraintSetStart);
                        motionScene.mConstraintSetMap.append(mConstraintSetStart, cSet);
                    } else if ("xml".equals(type)) {
                        int id = motionScene.parseInclude(context, mConstraintSetStart);
                        mConstraintSetStart = id;
                    }
                } else if (attr == R.styleable.Transition_motionInterpolator) {
                    TypedValue type = a.peekValue(attr);

                    if (type.type == TypedValue.TYPE_REFERENCE) {
                        mDefaultInterpolatorID = a.getResourceId(attr, -1);
                        if (mDefaultInterpolatorID != -1) {
                            mDefaultInterpolator = INTERPOLATOR_REFRENCE_ID;
                        }
                    } else if (type.type == TypedValue.TYPE_STRING) {
                        mDefaultInterpolatorString = a.getString(attr);
                        if (mDefaultInterpolatorString != null) {
                            if (mDefaultInterpolatorString.indexOf("/") > 0) {
                                mDefaultInterpolatorID = a.getResourceId(attr, -1);
                                mDefaultInterpolator = INTERPOLATOR_REFRENCE_ID;
                            } else {
                                mDefaultInterpolator = SPLINE_STRING;
                            }
                        }
                    } else {
                        mDefaultInterpolator = a.getInteger(attr, mDefaultInterpolator);
                    }

                } else if (attr == R.styleable.Transition_duration) {
                    mDuration = a.getInt(attr, mDuration);
                } else if (attr == R.styleable.Transition_staggered) {
                    mStagger = a.getFloat(attr, mStagger);
                } else if (attr == R.styleable.Transition_autoTransition) {
                    mAutoTransition = a.getInteger(attr, mAutoTransition);
                } else if (attr == R.styleable.Transition_android_id) {
                    mId = a.getResourceId(attr, mId);
                } else if (attr == R.styleable.Transition_transitionDisable) {
                    mDisable = a.getBoolean(attr, mDisable);
                } else if (attr == R.styleable.Transition_pathMotionArc) {
                    mPathMotionArc = a.getInteger(attr, UNSET);
                } else if (attr == R.styleable.Transition_layoutDuringTransition) {
                    mLayoutDuringTransition = a.getInteger(attr, 0);
                } else if (attr == R.styleable.Transition_transitionFlags) {
                    mTransitionFlags = a.getInteger(attr, 0);
                }
            }
            if (mConstraintSetStart == UNSET) {
                mIsAbstract = true;
            }
        }

    }

    /**
     * Create a motion scene.
     *
     * @param layout Motion layout to which the scene will be set.
     */
    public MotionScene(MotionLayout layout) {
        mMotionLayout = layout;
        mViewTransitionController = new ViewTransitionController(layout);
    }

    MotionScene(Context context, MotionLayout layout, int resourceID) {
        mMotionLayout = layout;
        mViewTransitionController = new ViewTransitionController(layout);

        load(context, resourceID);
        mConstraintSetMap.put(R.id.motion_base, new ConstraintSet());
        mConstraintSetIdMap.put("motion_base", R.id.motion_base);
    }

    /**
     * Load a MotionScene   from a MotionScene.xml file
     *
     * @param context    the context for the inflation
     * @param resourceId id of xml file in res/xml/
     */
    private void load(Context context, int resourceId) {

        Resources res = context.getResources();
        XmlPullParser parser = res.getXml(resourceId);
        String document = null;
        String tagName = null;
        try {
            Transition transition = null;
            for (int eventType = parser.getEventType();
                 eventType != XmlResourceParser.END_DOCUMENT;
                 eventType = parser.next()) {
                switch (eventType) {
                    case XmlResourceParser.START_DOCUMENT:
                        document = parser.getName();
                        break;
                    case XmlResourceParser.START_TAG:
                        tagName = parser.getName();
                        if (DEBUG_DESKTOP) {
                            System.out.println("parsing = " + tagName);
                        }
                        if (DEBUG) {
                            Log.v(TAG, "MotionScene ----------- START_TAG " + tagName);
                        }
                        switch (tagName) {
                            case MOTIONSCENE_TAG:
                                parseMotionSceneTags(context, parser);
                                break;
                            case TRANSITION_TAG:
                                mTransitionList.add(transition = new Transition(this, context, parser));
                                if (mCurrentTransition == null && !transition.mIsAbstract) {
                                    mCurrentTransition = transition;
                                    if (mCurrentTransition != null && mCurrentTransition.mTouchResponse != null) {
                                        mCurrentTransition.mTouchResponse.setRTL(mRtl);
                                    }
                                }
                                if (transition.mIsAbstract) { // global transition only one for now
                                    if (transition.mConstraintSetEnd == UNSET) {
                                        mDefaultTransition = transition;
                                    } else {
                                        mAbstractTransitionList.add(transition);
                                    }
                                    mTransitionList.remove(transition);
                                }
                                break;
                            case ONSWIPE_TAG:
                                if (DEBUG || transition == null) {
                                    String name = context.getResources().getResourceEntryName(resourceId);
                                    int line = parser.getLineNumber();
                                    Log.v(TAG, " OnSwipe (" + name + ".xml:" + line + ")");
                                }
                                if (transition != null) {
                                    transition.mTouchResponse = new TouchResponse(context, mMotionLayout, parser);
                                }
                                break;
                            case ONCLICK_TAG:
                                if (transition != null) {
                                    transition.addOnClick(context, parser);
                                }
                                break;
                            case STATESET_TAG:
                                mStateSet = new StateSet(context, parser);
                                break;
                            case CONSTRAINTSET_TAG:
                                parseConstraintSet(context, parser);
                                break;
                            case INCLUDE_TAG:
                                parseInclude(context, parser);
                                break;
                            case KEYFRAMESET_TAG:
                                KeyFrames keyFrames = new KeyFrames(context, parser);
                                if (transition != null) {
                                    transition.mKeyFramesList.add(keyFrames);
                                }
                                break;
                            case VIEW_TRANSITION:
                                ViewTransition viewTransition = new ViewTransition(context, parser);
                                mViewTransitionController.add(viewTransition);
                                break;
                            default:
                                Log.v(TAG, getLine(context, resourceId, parser) + "WARNING UNKNOWN ATTRIBUTE " + tagName);
                                break;
                        }

                        break;
                    case XmlResourceParser.END_TAG:
                        tagName = null;
                        break;
                    case XmlResourceParser.TEXT:
                        break;
                }
            }
        } catch (XmlPullParserException e) {
            Log.v(TAG, getLine(context, resourceId, parser) + " " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.v(TAG, getLine(context, resourceId, parser) + " " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void parseMotionSceneTags(Context context, XmlPullParser parser) {
        AttributeSet attrs = Xml.asAttributeSet(parser);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MotionScene);
        final int count = a.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.MotionScene_defaultDuration) {
                mDefaultDuration = a.getInt(attr, mDefaultDuration);
            } else if (attr == R.styleable.MotionScene_layoutDuringTransition) {
                mLayoutDuringTransition = a.getInteger(attr, LAYOUT_IGNORE_REQUEST);
            }
        }
        a.recycle();
    }

    private int getId(Context context, String idString) {
        int id = UNSET;
        if (idString.contains("/")) {
            String tmp = idString.substring(idString.indexOf('/') + 1);
            id = context.getResources().getIdentifier(tmp, "id", context.getPackageName());
            if (DEBUG_DESKTOP) {
                System.out.println("id getMap res = " + id);
            }
        }
        if (id == UNSET) {
            if (idString != null && idString.length() > 1) {
                id = Integer.parseInt(idString.substring(1));
            } else {
                Log.e(TAG, "error in parsing id");
            }
        }
        return id;
    }

    private void parseInclude(Context context, XmlPullParser mainParser) {
        TypedArray a = context.obtainStyledAttributes(Xml.asAttributeSet(mainParser), R.styleable.include);
        final int N = a.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.include_constraintSet) {
                int resourceId = a.getResourceId(attr, UNSET);
                parseInclude(context, resourceId);
            }
        }
        a.recycle();
    }

    private int parseInclude(Context context, int resourceId) {
        Resources res = context.getResources();
        XmlPullParser includeParser = res.getXml(resourceId);
        try {
            for (int eventType = includeParser.getEventType();
                 eventType != XmlResourceParser.END_DOCUMENT;
                 eventType = includeParser.next()) {
                String tagName = includeParser.getName();
                if (XmlResourceParser.START_TAG == eventType
                        && CONSTRAINTSET_TAG.equals(tagName)) {
                    return parseConstraintSet(context, includeParser);
                }
            }
        } catch (XmlPullParserException e) {
            Log.v(TAG, getLine(context, resourceId, includeParser) + " " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.v(TAG, getLine(context, resourceId, includeParser) + " " + e.getMessage());
            e.printStackTrace();
        }
        return UNSET;
    }

    private int parseConstraintSet(Context context, XmlPullParser parser) {
        ConstraintSet set = new ConstraintSet();
        set.setForceId(false);
        int count = parser.getAttributeCount();
        int id = UNSET;
        int derivedId = UNSET;
        for (int i = 0; i < count; i++) {
            String name = parser.getAttributeName(i);
            String value = parser.getAttributeValue(i);
            if (DEBUG_DESKTOP) {
                System.out.println("id string = " + value);
            }
            switch (name) {
                case "id":
                    id = getId(context, value);
                    mConstraintSetIdMap.put(stripID(value), id);
                    set.mIdString = Debug.getName(context, id);
                    break;
                case "deriveConstraintsFrom":
                    derivedId = getId(context, value);
                    break;
            }
        }
        if (id != UNSET) {
            if (mMotionLayout.mDebugPath != 0) {
                set.setValidateOnParse(true);
            }
            set.load(context, parser);
            if (derivedId != UNSET) {
                mDeriveMap.put(id, derivedId);
            }
            mConstraintSetMap.put(id, set);
        }
        return id;
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    }

    public ConstraintSet getConstraintSet(Context context, String id) {
        if (DEBUG_DESKTOP) {
            System.out.println("id " + id);
            System.out.println("size " + mConstraintSetMap.size());
        }
        for (int i = 0; i < mConstraintSetMap.size(); i++) {
            int key = mConstraintSetMap.keyAt(i);
            String IdAsString = context.getResources().getResourceName(key);
            if (DEBUG_DESKTOP) {
                System.out.println("Id for <" + i + "> is <" + IdAsString + "> looking for <" + id + ">");
            }
            if (id.equals(IdAsString)) {
                return mConstraintSetMap.get(key);
            }
        }
        return null;
    }

    ConstraintSet getConstraintSet(int id) {
        return getConstraintSet(id, -1, -1);
    }

    ConstraintSet getConstraintSet(int id, int width, int height) {
        if (DEBUG_DESKTOP) {
            System.out.println("id " + id);
            System.out.println("size " + mConstraintSetMap.size());
        }
        if (mStateSet != null) {
            int cid = mStateSet.stateGetConstraintID(id, width, height);
            if (cid != -1) {
                id = cid;
            }
        }
        if (mConstraintSetMap.get(id) == null) {
            Log.e(TAG, "Warning could not find ConstraintSet id/" + Debug.getName(mMotionLayout.getContext(), id) + " In MotionScene");
            return mConstraintSetMap.get(mConstraintSetMap.keyAt(0));
        }
        return mConstraintSetMap.get(id);
    }

    /**
     * Maps the Constraint set to the id.
     *
     * @param id  - unique id to represent the ConstraintSet
     * @param set - ConstraintSet to be represented with the id.
     */
    public void setConstraintSet(int id, ConstraintSet set) {
        mConstraintSetMap.put(id, set);
    }

    /**
     * provides the key frames & CycleFrames to the motion view to
     *
     * @param motionController
     */
    public void getKeyFrames(MotionController motionController) {
        if (mCurrentTransition == null) {
            if (mDefaultTransition != null) {
                for (KeyFrames keyFrames : mDefaultTransition.mKeyFramesList) {
                    keyFrames.addFrames(motionController);
                }
            }
            return;
        }
        for (KeyFrames keyFrames : mCurrentTransition.mKeyFramesList) {
            keyFrames.addFrames(motionController);
        }
    }

    /**
     * get key frame
     *
     * @param context
     * @param type
     * @param target
     * @param position
     * @return Key Object
     */
    Key getKeyFrame(Context context, int type, int target, int position) {
        if (mCurrentTransition == null) {
            return null;
        }
        for (KeyFrames keyFrames : mCurrentTransition.mKeyFramesList) {
            for (Integer integer : keyFrames.getKeys()) {
                if (target == integer) {
                    ArrayList<Key> keys = keyFrames.getKeyFramesForView(integer);
                    for (Key key : keys) {
                        if (key.mFramePosition == position) {
                            if (key.mType == type) {
                                return key;
                            }
                        }
                    }
                }
            }

        }
        return null;
    }

    int getTransitionDirection(int stateId) {
        for (Transition transition : mTransitionList) {
            if (transition.mConstraintSetStart == stateId) {
                return TRANSITION_BACKWARD;
            }
        }
        return TRANSITION_FORWARD;
    }

    /**
     * Returns true if the view has a keyframe defined at the given position
     *
     * @param view
     * @param position
     * @return true if a keyframe exists, false otherwise
     */
    boolean hasKeyFramePosition(View view, int position) {
        if (mCurrentTransition == null) {
            return false;
        }
        for (KeyFrames keyFrames : mCurrentTransition.mKeyFramesList) {
            ArrayList<Key> framePoints = keyFrames.getKeyFramesForView(view.getId());
            for (Key framePoint : framePoints) {
                if (framePoint.mFramePosition == position) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setKeyframe(View view, int position, String name, Object value) {
        if (DEBUG) {
            System.out.println("setKeyframe for pos " + position + " name <" + name + "> value: " + value);
        }
        if (mCurrentTransition == null) {
            return;
        }
        for (KeyFrames keyFrames : mCurrentTransition.mKeyFramesList) {
            if (DEBUG) {
                System.out.println("key frame " + keyFrames);
            }
            ArrayList<Key> framePoints = keyFrames.getKeyFramesForView(view.getId());
            if (DEBUG) {
                System.out.println("key frame has " + framePoints.size() + " frame points");
            }
            for (Key framePoint : framePoints) {
                if (DEBUG) {
                    System.out.println("framePoint pos: " + framePoint.mFramePosition);
                }
                if (framePoint.mFramePosition == position) {
                    float v = 0;
                    if (value != null) {
                        v = ((Float) value).floatValue();
                        if (DEBUG) {
                            System.out.println("value: " + v);
                        }
                    } else {
                        if (DEBUG) {
                            System.out.println("value was null!!!");
                        }
                    }
                    if (v == 0) {
                        v = 0.01f;
                    }
                    if (name.equalsIgnoreCase("app:PerpendicularPath_percent")) {
                    }
                }
            }
        }
    }

    public float getPathPercent(View view, int position) {
        return 0;
    }

    //////////////////////////////////////////////////////////
    // touch handling
    ///////////////////////////////////////////////////////////
    boolean supportTouch() {
        for (Transition transition : mTransitionList) {
            if (transition.mTouchResponse != null) {
                return true;
            }
        }
        return mCurrentTransition != null && mCurrentTransition.mTouchResponse != null;
    }

    float mLastTouchX, mLastTouchY;

    void processTouchEvent(MotionEvent event, int currentState, MotionLayout motionLayout) {
        if (DEBUG) {
            Log.v(TAG, Debug.getLocation() + " processTouchEvent");
        }
        RectF cache = new RectF();
        if (mVelocityTracker == null) {
            mVelocityTracker = mMotionLayout.obtainVelocityTracker();
        }
        mVelocityTracker.addMovement(event);
        if (DEBUG) {
            float time = (event.getEventTime() % 100000) / 1000f;
            float x = event.getRawX();
            float y = event.getRawY();
            Log.v(TAG, " " + time + "  processTouchEvent " +
                    "state=" + Debug.getState(motionLayout, currentState)
                    + "  " + Debug.getActionType(event) + " " + x + ", " + y + " \t " + motionLayout.getProgress());
        }

        if (currentState != -1) {
            RectF region;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mLastTouchX = event.getRawX();
                    mLastTouchY = event.getRawY();
                    mLastTouchDown = event;
                    mIgnoreTouch = false;
                    if (mCurrentTransition.mTouchResponse != null) {
                        region = mCurrentTransition.mTouchResponse.getLimitBoundsTo(mMotionLayout, cache);
                        if (region != null && !region.contains(mLastTouchDown.getX(), mLastTouchDown.getY())) {
                            mLastTouchDown = null;
                            mIgnoreTouch = true;
                            return;
                        }
                        region = mCurrentTransition.mTouchResponse.getTouchRegion(mMotionLayout, cache);
                        if (region != null && (!region.contains(mLastTouchDown.getX(), mLastTouchDown.getY()))) {
                            mMotionOutsideRegion = true;
                        } else {
                            mMotionOutsideRegion = false;
                        }
                        mCurrentTransition.mTouchResponse.setDown(mLastTouchX, mLastTouchY);
                    }
                    if (DEBUG) {
                        Log.v(TAG, "----- ACTION_DOWN " + mLastTouchX + "," + mLastTouchY);
                    }
                    return;
                case MotionEvent.ACTION_MOVE:
                    if (mIgnoreTouch) {
                        break;
                    }
                    float dy = event.getRawY() - mLastTouchY;
                    float dx = event.getRawX() - mLastTouchX;
                    if (DEBUG) {
                        Log.v(TAG, "----- ACTION_MOVE " + dx + "," + dy);
                    }
                    if (dx == 0.0 && dy == 0.0 || mLastTouchDown == null) {
                        return;
                    }

                    Transition transition = bestTransitionFor(currentState, dx, dy, mLastTouchDown);
                    if (DEBUG) {
                        Log.v(TAG, Debug.getLocation() + " best Transition For " + dx + "," + dy + " " +
                                ((transition == null) ? null : transition.debugString(mMotionLayout.getContext())));
                    }
                    if (transition != null) {

                        motionLayout.setTransition(transition);
                        region = mCurrentTransition.mTouchResponse.getTouchRegion(mMotionLayout, cache);
                        mMotionOutsideRegion = region != null
                                && (!region.contains(mLastTouchDown.getX(), mLastTouchDown.getY()));
                        mCurrentTransition.mTouchResponse.setUpTouchEvent(mLastTouchX, mLastTouchY);
                    }
            }
        }
        if (mIgnoreTouch) {
            return;
        }
        if (mCurrentTransition != null && mCurrentTransition.mTouchResponse != null && !mMotionOutsideRegion) {
            mCurrentTransition.mTouchResponse.processTouchEvent(event, mVelocityTracker, currentState, this);
        }

        mLastTouchX = event.getRawX();
        mLastTouchY = event.getRawY();

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mVelocityTracker != null) {
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                if (motionLayout.mCurrentState != UNSET) {
                    autoTransition(motionLayout, motionLayout.mCurrentState);
                }
            }
        }
    }

    void processScrollMove(float dx, float dy) {
        if (mCurrentTransition != null && mCurrentTransition.mTouchResponse != null) {
            mCurrentTransition.mTouchResponse.scrollMove(dx, dy);
        }
    }

    void processScrollUp(float dx, float dy) {
        if (mCurrentTransition != null && mCurrentTransition.mTouchResponse != null) {
            mCurrentTransition.mTouchResponse.scrollUp(dx, dy);
        }
    }

    /**
     * Calculate if a drag in this direction results in an increase or decrease in progress.
     *
     * @param dx drag direction in x
     * @param dy drag direction in y
     * @return change in progress given that dx and dy
     */
    float getProgressDirection(float dx, float dy) {
        if (mCurrentTransition != null && mCurrentTransition.mTouchResponse != null) {
            return mCurrentTransition.mTouchResponse.getProgressDirection(dx, dy);
        }
        return 0;
    }

    /////////////////////////////////////////////////////////////

    int getStartId() {
        if (mCurrentTransition == null) {
            return -1;
        }
        return mCurrentTransition.mConstraintSetStart;
    }

    int getEndId() {
        if (mCurrentTransition == null) {
            return -1;
        }
        return mCurrentTransition.mConstraintSetEnd;
    }

    static final int EASE_IN_OUT = 0;
    static final int EASE_IN = 1;
    static final int EASE_OUT = 2;
    static final int LINEAR = 3;
    static final int BOUNCE = 4;
    static final int OVERSHOOT = 5;
    static final int ANTICIPATE = 6;

    public Interpolator getInterpolator() {
        switch (mCurrentTransition.mDefaultInterpolator) {
            case SPLINE_STRING:
                final Easing easing = Easing.getInterpolator(mCurrentTransition.mDefaultInterpolatorString);
                return new Interpolator() {
                    @Override
                    public float getInterpolation(float v) {
                        return (float) easing.get(v);
                    }
                };
            case INTERPOLATOR_REFRENCE_ID:
                return AnimationUtils.loadInterpolator(mMotionLayout.getContext(),
                        mCurrentTransition.mDefaultInterpolatorID);
            case EASE_IN_OUT:
                return new AccelerateDecelerateInterpolator();
            case EASE_IN:
                return new AccelerateInterpolator();
            case EASE_OUT:
                return new DecelerateInterpolator();
            case LINEAR:
                return null;
            case ANTICIPATE:
                return new AnticipateInterpolator();
            case OVERSHOOT:
                return new OvershootInterpolator();
            case BOUNCE:
                return new BounceInterpolator();
        }
        return null;
    }

    /**
     * Get Duration of the current transition.
     *
     * @return duration in milliseconds
     */
    public int getDuration() {
        if (mCurrentTransition != null) {
            return mCurrentTransition.mDuration;
        }
        return mDefaultDuration;
    }

    /**
     * Sets the duration of the current transition or the default if there is no current transition
     *
     * @param duration in milliseconds
     */
    public void setDuration(int duration) {
        if (mCurrentTransition != null) {
            mCurrentTransition.setDuration(duration);
        } else {
            mDefaultDuration = duration;
        }
    }

    public int gatPathMotionArc() {
        return (mCurrentTransition != null) ? mCurrentTransition.mPathMotionArc : UNSET;
    }

    /**
     * Get the staggered value of the current transition.
     * 0 staggerd
     *
     * @return
     */
    public float getStaggered() {
        if (mCurrentTransition != null) {
            return mCurrentTransition.mStagger;
        }
        return 0;
    }

    float getMaxAcceleration() {
        if (mCurrentTransition != null && mCurrentTransition.mTouchResponse != null) {
            return mCurrentTransition.mTouchResponse.getMaxAcceleration();
        }
        return 0;
    }

    float getMaxVelocity() {
        if (mCurrentTransition != null && mCurrentTransition.mTouchResponse != null) {
            return mCurrentTransition.mTouchResponse.getMaxVelocity();
        }
        return 0;
    }

    void setupTouch() {
        if (mCurrentTransition != null && mCurrentTransition.mTouchResponse != null) {
            mCurrentTransition.mTouchResponse.setupTouch();
        }
    }

    boolean getMoveWhenScrollAtTop() {
        if (mCurrentTransition != null && mCurrentTransition.mTouchResponse != null) {
            return mCurrentTransition.mTouchResponse.getMoveWhenScrollAtTop();
        }
        return false;
    }

    /**
     * read the constraints from the inflation of the ConstraintLayout
     * If the constraintset does not contain infomation about a view this information is used
     * as a "fallback" postion.
     *
     * @param motionLayout
     */
    void readFallback(MotionLayout motionLayout) {

        for (int i = 0; i < mConstraintSetMap.size(); i++) {
            int key = mConstraintSetMap.keyAt(i);
            if (hasCycleDependency(key)) {
                Log.e(TAG, "Cannot be derived from yourself");
                return;
            }
            readConstraintChain(key);
        }
        for (int i = 0; i < mConstraintSetMap.size(); i++) {
            ConstraintSet cs = mConstraintSetMap.valueAt(i);
            cs.readFallback(motionLayout);
        }
    }

    /**
     * This is brute force but the number of ConstraintSets is typicall very small (< 5)
     *
     * @param key
     * @return
     */
    private boolean hasCycleDependency(int key) {
        int derived = mDeriveMap.get(key);
        int len = mDeriveMap.size();
        while (derived > 0) {
            if (derived == key) {
                return true;
            }
            if (len-- < 0) {
                return true;
            }
            derived = mDeriveMap.get(derived);
        }
        return false;
    }

    /**
     * @param key
     */
    private void readConstraintChain(int key) {
        int derivedFromId = mDeriveMap.get(key);
        if (derivedFromId > 0) {
            readConstraintChain(mDeriveMap.get(key));
            ConstraintSet cs = mConstraintSetMap.get(key);
            ConstraintSet derivedFrom = mConstraintSetMap.get(derivedFromId);
            if (derivedFrom == null) {
                Log.e(TAG, "ERROR! invalid deriveConstraintsFrom: @id/" +
                        Debug.getName(mMotionLayout.getContext(), derivedFromId));
                return;
            }
            cs.readFallback(derivedFrom);
            mDeriveMap.put(key, -1);
        }
    }

    public static String stripID(String id) {
        if (id == null) {
            return "";
        }
        int index = id.indexOf('/');
        if (index < 0) {
            return id;
        }
        return id.substring(index + 1);
    }

    /**
     * Used at design time
     *
     * @param id
     * @return
     */
    public int lookUpConstraintId(String id) {
        Integer boxed = mConstraintSetIdMap.get(id);
        if (boxed == null) {
            return 0;
        } else {
            return boxed;
        }
    }

    /**
     * used at design time
     *
     * @return
     */
    public String lookUpConstraintName(int id) {
        for (Map.Entry<String, Integer> entry : mConstraintSetIdMap.entrySet()) {
            Integer boxed = entry.getValue();
            if (boxed == null) {
                continue;
            }

            if (boxed == id) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * this allow disabling autoTransitions to prevent design surface from being in undefined states
     *
     * @param disable
     */
    public void disableAutoTransition(boolean disable) {
        mDisableAutoTransition = disable;
    }

    /**
     * Construct a user friendly error string
     *
     * @param context    the context
     * @param resourceId the xml being parsed
     * @param pullParser the XML parser
     * @return
     */
    static String getLine(Context context, int resourceId, XmlPullParser pullParser) {
        return ".(" + Debug.getName(context, resourceId) + ".xml:" + pullParser.getLineNumber() +
                ") \"" + pullParser.getName() + "\"";
    }
}
