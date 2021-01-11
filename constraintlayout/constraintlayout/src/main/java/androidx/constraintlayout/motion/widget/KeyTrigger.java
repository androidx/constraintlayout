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
import android.content.res.TypedArray;
import android.graphics.RectF;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintAttribute;
import androidx.constraintlayout.widget.R;

import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Defines container for a key frame of for storing KeyAttributes.
 * KeyAttributes change post layout values of a view.
 *
 * @hide
 */

public class KeyTrigger extends Key {
    static final String NAME = "KeyTrigger";
    private static final String TAG = "KeyTrigger";
    public static final String VIEW_TRANSITION_ON_CROSS = "viewTransitionOnCross";
    public static final String VIEW_TRANSITION_ON_POSITIVE_CROSS = "viewTransitionOnPositiveCross";
    public static final String VIEW_TRANSITION_ON_NEGATIVE_CROSS = "viewTransitionOnNegativeCross";
    public static final String POST_LAYOUT = "postLayout";
    public static final String TRIGGER_SLACK = "triggerSlack";
    public static final String TRIGGER_COLLISION_VIEW = "triggerCollisionView";
    public static final String TRIGGER_COLLISION_ID = "triggerCollisionId";
    public static final String TRIGGER_ID = "triggerID";
    public static final String POSITIVE_CROSS = "positiveCross";
    public static final String NEGATIVE_CROSS = "negativeCross";
    public static final String TRIGGER_RECEIVER = "triggerReceiver";
    public static final String CROSS = "CROSS";

    private int mCurveFit = -1;
    @Nullable
    private String mCross = null;
    private int mTriggerReceiver = UNSET;
    @Nullable
    private String mNegativeCross = null;
    @Nullable
    private String mPositiveCross = null;
    private int mTriggerID = UNSET;
    private int mTriggerCollisionId = UNSET;
    @Nullable
    private View mTriggerCollisionView = null;
    float mTriggerSlack = .1f;
    private boolean mFireCrossReset = true;
    private boolean mFireNegativeReset = true;
    private boolean mFirePositiveReset = true;
    private float mFireThreshold = Float.NaN;
    private float mFireLastPos;
    private boolean mPostLayout = false;
    int mViewTransitionOnNegativeCross = UNSET;
    int mViewTransitionOnPositiveCross = UNSET;
    int mViewTransitionOnCross = UNSET;

    @NonNull
    RectF mCollisionRect = new RectF();
    @NonNull
    RectF mTargetRect = new RectF();
    @NonNull
    HashMap<String, Method> mMethodHashMap = new HashMap<>();
    public static final int KEY_TYPE = 5;

    {
        mType = KEY_TYPE;
        mCustomConstraints = new HashMap<>();
    }

    @Override
    public void load(@NonNull Context context, @Nullable AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.KeyTrigger);
        Loader.read(this, a, context);
    }

    /**
     * Gets the curve fit type this drives the interpolation
     *
     * @return
     */
    int getCurveFit() {
        return mCurveFit;
    }

    @Override
    public void getAttributeNames(@NonNull HashSet<String> attributes) {
    }

    @Override
    public void addValues(@NonNull HashMap<String, SplineSet> splines) {
    }

    @Override
    public void setValue(@NonNull String tag, @NonNull Object value) {
        switch (tag) {
            case CROSS:
                mCross = value.toString();
                break;
            case TRIGGER_RECEIVER:
                mTriggerReceiver = toInt(value);
                break;
            case NEGATIVE_CROSS:
                mNegativeCross = value.toString();
                break;
            case POSITIVE_CROSS:
                mPositiveCross = value.toString();
                break;
            case TRIGGER_ID:
                mTriggerID = toInt(value);
                break;
            case TRIGGER_COLLISION_ID:
                mTriggerCollisionId = toInt(value);
                break;
            case TRIGGER_COLLISION_VIEW:
                mTriggerCollisionView = (View) value;
                break;
            case TRIGGER_SLACK:
                mTriggerSlack = toFloat(value);
                break;
            case POST_LAYOUT:
                mPostLayout = toBoolean(value);
                break;
            case VIEW_TRANSITION_ON_NEGATIVE_CROSS:
                mViewTransitionOnNegativeCross = toInt(value);
                break;
            case VIEW_TRANSITION_ON_POSITIVE_CROSS:
                mViewTransitionOnPositiveCross = toInt(value);
                break;
            case VIEW_TRANSITION_ON_CROSS:
                mViewTransitionOnCross = toInt(value);
                break;

        }
    }

    private void setUpRect(@NonNull RectF rect, @NonNull View child, boolean postLayout) {
        rect.top = child.getTop();
        rect.bottom = child.getBottom();
        rect.left = child.getLeft();
        rect.right = child.getRight();
        if (postLayout) {
            child.getMatrix().mapRect(rect);
        }
    }

    public void conditionallyFire(float pos, @NonNull View child) {
        boolean fireCross = false;
        boolean fireNegative = false;
        boolean firePositive = false;

        if (mTriggerCollisionId != UNSET) {
            if (mTriggerCollisionView == null) {
                mTriggerCollisionView = ((ViewGroup) child.getParent()).findViewById(mTriggerCollisionId);
            }

            setUpRect(mCollisionRect, mTriggerCollisionView, mPostLayout);
            setUpRect(mTargetRect, child, mPostLayout);
            boolean in = mCollisionRect.intersect(mTargetRect);
            // TODO scale by mTriggerSlack
            if (in) {
                if (mFireCrossReset) {
                    fireCross = true;
                    mFireCrossReset = false;
                }
                if (mFirePositiveReset) {
                    firePositive = true;
                    mFirePositiveReset = false;
                }
                mFireNegativeReset = true;
            } else {
                if (!mFireCrossReset) {
                    fireCross = true;
                    mFireCrossReset = true;
                }
                if (mFireNegativeReset) {
                    fireNegative = true;
                    mFireNegativeReset = false;
                }
                mFirePositiveReset = true;
            }

        } else {

            // Check for crossing
            if (mFireCrossReset) {

                float offset = pos - mFireThreshold;
                float lastOffset = mFireLastPos - mFireThreshold;

                if (offset * lastOffset < 0) { // just crossed the threshold
                    fireCross = true;
                    mFireCrossReset = false;
                }
            } else {
                if (Math.abs(pos - mFireThreshold) > mTriggerSlack) {
                    mFireCrossReset = true;
                }
            }

            // Check for negative crossing
            if (mFireNegativeReset) {
                float offset = pos - mFireThreshold;
                float lastOffset = mFireLastPos - mFireThreshold;
                if (offset * lastOffset < 0 && offset < 0) { // just crossed the threshold
                    fireNegative = true;
                    mFireNegativeReset = false;
                }
            } else {
                if (Math.abs(pos - mFireThreshold) > mTriggerSlack) {
                    mFireNegativeReset = true;
                }
            }
            // Check for positive crossing
            if (mFirePositiveReset) {
                float offset = pos - mFireThreshold;
                float lastOffset = mFireLastPos - mFireThreshold;
                if (offset * lastOffset < 0 && offset > 0) { // just crossed the threshold
                    firePositive = true;
                    mFirePositiveReset = false;
                }
            } else {
                if (Math.abs(pos - mFireThreshold) > mTriggerSlack) {
                    mFirePositiveReset = true;
                }
            }
        }
        mFireLastPos = pos;

        if (fireNegative || fireCross || firePositive) {
            ((MotionLayout) child.getParent()).fireTrigger(mTriggerID, firePositive, pos);
        }
        View call = (mTriggerReceiver == UNSET) ? child : ((MotionLayout) child.getParent()).findViewById(mTriggerReceiver);

        if (fireNegative) {
            if (mNegativeCross != null) {
                fire(mNegativeCross, call);
            }
            if (mViewTransitionOnNegativeCross != UNSET) {
                ((MotionLayout) child.getParent()).viewTransition(mViewTransitionOnNegativeCross, call);
            }
        }
        if (firePositive) {
            if (mPositiveCross != null) {
                fire(mPositiveCross, call);
            }
            if (mViewTransitionOnPositiveCross != UNSET) {
                ((MotionLayout) child.getParent()).viewTransition(mViewTransitionOnPositiveCross, call);
            }
        }
        if (fireCross) {
            if (mCross != null) {
                fire(mCross, call);
            }
            if (mViewTransitionOnCross != UNSET) {
                ((MotionLayout) child.getParent()).viewTransition(mViewTransitionOnCross, call);
            }
        }

    }

    private void fire(@Nullable String str, @NonNull View call) {
        if (str == null) {
            return;
        }
        if (str.startsWith(".")) {
            fireCustom(str, call);
            return;
        }
        Method method = null;
        if (mMethodHashMap.containsKey(str)) {
            method = mMethodHashMap.get(str);
            if (method == null) { // we looked up and did not find
                return;
            }
        }
        if (method == null) {
            try {
                method = call.getClass().getMethod(str);
                mMethodHashMap.put(str, method);
            } catch (NoSuchMethodException e) {
                mMethodHashMap.put(str, null); // record that we could not get this method
                Log.e(TAG, "Could not find method \"" + str + "\"" + "on class "
                        + call.getClass().getSimpleName() + " " + Debug.getName(call));
                return;
            }
        }
        try {
            method.invoke(call);
        } catch (Exception e) {
            Log.e(TAG, "Exception in call \"" + mCross + "\"" + "on class "
                    + call.getClass().getSimpleName() + " " + Debug.getName(call));
        }
    }

    private void fireCustom(@NonNull String str, @NonNull View view) {
        boolean callAll = str.length() == 1;
        if (!callAll) {
            str = str.substring(1).toLowerCase();
        }
        for (String name : mCustomConstraints.keySet()) {
            String lowerCase = name.toLowerCase();
            if (callAll || lowerCase.matches(str)) {
                ConstraintAttribute custom = mCustomConstraints.get(name);
                if (custom != null) {
                    custom.applyCustom(view);
                }
            }
        }
    }

    private static class Loader {
        private static final int NEGATIVE_CROSS = 1;
        private static final int POSITIVE_CROSS = 2;
        private static final int CROSS = 4;
        private static final int TRIGGER_SLACK = 5;
        private static final int TRIGGER_ID = 6;
        private static final int TARGET_ID = 7;
        private static final int FRAME_POS = 8;
        private static final int COLLISION = 9;
        private static final int POST_LAYOUT = 10;
        private static final int TRIGGER_RECEIVER = 11;
        private static final int VT_CROSS = 12;
        private static final int VT_NEGATIVE_CROSS = 13;
        private static final int VT_POSITIVE_CROSS = 14;

        @NonNull
        private static SparseIntArray mAttrMap = new SparseIntArray();

        static {
            mAttrMap.append(R.styleable.KeyTrigger_framePosition, FRAME_POS);
            mAttrMap.append(R.styleable.KeyTrigger_onCross, CROSS);
            mAttrMap.append(R.styleable.KeyTrigger_onNegativeCross, NEGATIVE_CROSS);
            mAttrMap.append(R.styleable.KeyTrigger_onPositiveCross, POSITIVE_CROSS);
            mAttrMap.append(R.styleable.KeyTrigger_motionTarget, TARGET_ID);
            mAttrMap.append(R.styleable.KeyTrigger_triggerId, TRIGGER_ID);
            mAttrMap.append(R.styleable.KeyTrigger_triggerSlack, TRIGGER_SLACK);
            mAttrMap.append(R.styleable.KeyTrigger_motion_triggerOnCollision, COLLISION);
            mAttrMap.append(R.styleable.KeyTrigger_motion_postLayoutCollision, POST_LAYOUT);
            mAttrMap.append(R.styleable.KeyTrigger_triggerReceiver, TRIGGER_RECEIVER);
            mAttrMap.append(R.styleable.KeyTrigger_viewTransitionOnCross, VT_CROSS);
            mAttrMap.append(R.styleable.KeyTrigger_viewTransitionOnNegativeCross, VT_NEGATIVE_CROSS);
            mAttrMap.append(R.styleable.KeyTrigger_viewTransitionOnPositiveCross, VT_POSITIVE_CROSS);
        }

        public static void read(@NonNull KeyTrigger c, @NonNull TypedArray a, @NonNull Context context) {
            final int N = a.getIndexCount();
            for (int i = 0; i < N; i++) {
                int attr = a.getIndex(i);
                switch (mAttrMap.get(attr)) {
                    case FRAME_POS:
                        c.mFramePosition = a.getInteger(attr, c.mFramePosition);
                        c.mFireThreshold = (c.mFramePosition + .5f) / 100f;
                        break;
                    case TARGET_ID:
                        if (MotionLayout.IS_IN_EDIT_MODE) {
                            c.mTargetId = a.getResourceId(attr, c.mTargetId);
                            if (c.mTargetId == -1) {
                                c.mTargetString = a.getString(attr);
                            }
                        } else {
                            if (a.peekValue(attr).type == TypedValue.TYPE_STRING) {
                                c.mTargetString = a.getString(attr);
                            } else {
                                c.mTargetId = a.getResourceId(attr, c.mTargetId);
                            }
                        }
                        break;
                    case NEGATIVE_CROSS:
                        c.mNegativeCross = a.getString(attr);
                        break;
                    case POSITIVE_CROSS:
                        c.mPositiveCross = a.getString(attr);
                        break;
                    case CROSS:
                        c.mCross = a.getString(attr);
                        break;
                    case TRIGGER_SLACK:
                        c.mTriggerSlack = a.getFloat(attr, c.mTriggerSlack);
                        break;
                    case TRIGGER_ID:
                        c.mTriggerID = a.getResourceId(attr, c.mTriggerID);
                        break;
                    case COLLISION:
                        c.mTriggerCollisionId = a.getResourceId(attr, c.mTriggerCollisionId);
                        break;
                    case POST_LAYOUT:
                        c.mPostLayout = a.getBoolean(attr, c.mPostLayout);
                        break;
                    case TRIGGER_RECEIVER:
                        c.mTriggerReceiver = a.getResourceId(attr, c.mTriggerReceiver);
                        break;
                    case VT_NEGATIVE_CROSS:
                        c.mViewTransitionOnNegativeCross = a.getResourceId(attr, c.mViewTransitionOnNegativeCross);
                        break;
                    case VT_POSITIVE_CROSS:
                        c.mViewTransitionOnPositiveCross = a.getResourceId(attr, c.mViewTransitionOnPositiveCross);
                        break;
                    case VT_CROSS:
                        c.mViewTransitionOnCross = a.getResourceId(attr, c.mViewTransitionOnCross);
                        break;
                    default:
                        Log.e(NAME, "unused attribute 0x" + Integer.toHexString(attr) + "   " + mAttrMap.get(attr));
                        break;
                }
            }
        }
    }

    @NonNull
    public Key copy(@NonNull Key src) {
        super.copy(src);
        KeyTrigger k = (KeyTrigger) src;
        mCurveFit = k.mCurveFit;
        mCross = k.mCross;
        mTriggerReceiver = k.mTriggerReceiver;
        mNegativeCross = k.mNegativeCross;
        mPositiveCross = k.mPositiveCross;
        mTriggerID = k.mTriggerID;
        mTriggerCollisionId = k.mTriggerCollisionId;
        mTriggerCollisionView = k.mTriggerCollisionView;
        mTriggerSlack = k.mTriggerSlack;
        mFireCrossReset = k.mFireCrossReset;
        mFireNegativeReset = k.mFireNegativeReset;
        mFirePositiveReset = k.mFirePositiveReset;
        mFireThreshold = k.mFireThreshold;
        mFireLastPos = k.mFireLastPos;
        mPostLayout = k.mPostLayout;
        mCollisionRect = k.mCollisionRect;
        mTargetRect = k.mTargetRect;
        mMethodHashMap = k.mMethodHashMap;
        return this;
    }

    @NonNull
    public Key clone() {
        return new KeyTrigger().copy(this);
    }
}
