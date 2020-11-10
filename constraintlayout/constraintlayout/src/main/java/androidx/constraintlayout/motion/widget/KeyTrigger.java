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
    private int mCurveFit = -1;
    private String mCross = null;
    private int mTriggerReceiver = UNSET;
    private String mNegativeCross = null;
    private String mPositiveCross = null;
    private int mTriggerID = UNSET;
    private int mTriggerCollisionId = UNSET;
    private View mTriggerCollisionView = null;
    float mTriggerSlack = .1f;
    private boolean mFireCrossReset = true;
    private boolean mFireNegativeReset = true;
    private boolean mFirePositiveReset = true;
    private float mFireThreshold = Float.NaN;
    private float mFireLastPos;
    private boolean mPostLayout = false;

    RectF mCollisionRect = new RectF();
    RectF mTargetRect = new RectF();
    HashMap<String, Method> mMethodHashMap = new HashMap<>();
    public static final int KEY_TYPE = 5;

    {
        mType = KEY_TYPE;
        mCustomConstraints = new HashMap<>();
    }

    @Override
    public void load(Context context, AttributeSet attrs) {
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
    public void getAttributeNames(HashSet<String> attributes) {
    }

    @Override
    public void addValues(HashMap<String, SplineSet> splines) {
    }

    @Override
    public void setValue(String tag, Object value) {
    }

    private void setUpRect(RectF rect, View child, boolean postLayout) {
        rect.top = child.getTop();
        rect.bottom = child.getBottom();
        rect.left = child.getLeft();
        rect.right = child.getRight();
        if (postLayout) {
            child.getMatrix().mapRect(rect);
        }
    }

    public void conditionallyFire(float pos, View child) {
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

        if (fireNegative && mNegativeCross != null) {
            fire(mNegativeCross, call);
        }
        if (firePositive && mPositiveCross != null) {
            fire(mPositiveCross, call);
        }
        if (fireCross && mCross != null) {
            fire(mCross, call);
        }

    }

    private void fire(String str, View call) {
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

    private void fireCustom(String str, View view) {
        boolean callAll = str.length() == 1;
        if (!callAll) {
            str = str.substring(1).toLowerCase();
        }
        for (String name : mCustomConstraints.keySet()) {
             String lowerCase = name.toLowerCase();
            if (callAll || lowerCase.matches(str)) {
                ConstraintAttribute custom = mCustomConstraints.get(name);
                custom.applyCustom(view);
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
        }

        public static void read(KeyTrigger c, TypedArray a, Context context) {
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
                    default:
                        Log.e(NAME, "unused attribute 0x" + Integer.toHexString(attr) + "   " + mAttrMap.get(attr));
                        break;
                }
            }
        }
    }
}
