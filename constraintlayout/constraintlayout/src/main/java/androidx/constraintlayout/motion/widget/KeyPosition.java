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
import androidx.constraintlayout.widget.R;
import androidx.constraintlayout.motion.utils.Easing;

import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

/**
 * Provide the passive data structure to get KeyPosition information form XML
 *
 * @hide
 */

public class KeyPosition extends KeyPositionBase {
    private static final String TAG = "KeyPosition";
    static final String NAME = "KeyPosition";

    @Nullable
    String mTransitionEasing = null;
    int mPathMotionArc = UNSET; // -1 means not set
    int mDrawPath = 0;
    float mPercentWidth = Float.NaN;
    float mPercentHeight = Float.NaN;
    float mPercentX = Float.NaN;
    float mPercentY = Float.NaN;
    float mAltPercentX = Float.NaN;
    float mAltPercentY = Float.NaN;
    public static final int TYPE_SCREEN = 2;
    public static final int TYPE_PATH = 1;
    public static final int TYPE_CARTESIAN = 0;
    int mPositionType = TYPE_CARTESIAN;
    public static final String TRANSITION_EASING = "transitionEasing";
    public static final String DRAWPATH = "drawPath";
    public static final String PERCENT_WIDTH = "percentWidth";
    public static final String PERCENT_HEIGHT = "percentHeight";
    public static final String SIZE_PERCENT = "sizePercent";
    public static final String PERCENT_X = "percentX";
    public static final String PERCENT_Y = "percentY";
    private float mCalculatedPositionX = Float.NaN;
    private float mCalculatedPositionY = Float.NaN;
    static final int KEY_TYPE = 2;

    {
        mType = KEY_TYPE;
    }

    @Override
    public void load(@NonNull Context context, @Nullable AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.KeyPosition);
        Loader.read(this, a);
    }

    @Override
    public void addValues(@NonNull HashMap<String, SplineSet> splines) {
    }

    public void setType(int type) {
        mPositionType = type;
    }

    @Override
    void calcPosition(int layoutWidth, int layoutHeight, float start_x, float start_y, float end_x, float end_y) {
        switch (mPositionType) {
            case TYPE_SCREEN:
                calcScreenPosition(layoutWidth, layoutHeight);
                return;

            case TYPE_PATH:
                calcPathPosition(start_x, start_y, end_x, end_y);
                return;
            case TYPE_CARTESIAN:
            default:
                calcCartesianPosition(start_x, start_y, end_x, end_y);
                return;
        }
    }

    // TODO this needs the views dimensions to be accurate
    private void calcScreenPosition(int layoutWidth, int layoutHeight) {
        int viewWidth = 0;
        int viewHeight = 0;
        mCalculatedPositionX = (layoutWidth - viewWidth) * mPercentX + viewWidth / 2;
        mCalculatedPositionY = (layoutHeight - viewHeight) * mPercentX + viewHeight / 2;
    }

    private void calcPathPosition(float start_x, float start_y,
                                  float end_x, float end_y) {
        float pathVectorX = end_x - start_x;
        float pathVectorY = end_y - start_y;
        float perpendicularX = -pathVectorY;
        float perpendicularY = pathVectorX;
        mCalculatedPositionX = start_x + pathVectorX * mPercentX + perpendicularX * mPercentY;
        mCalculatedPositionY = start_y + pathVectorY * mPercentX + perpendicularY * mPercentY;
    }

    private void calcCartesianPosition(float start_x, float start_y,
                                       float end_x, float end_y) {
        float pathVectorX = end_x - start_x;
        float pathVectorY = end_y - start_y;
        float dxdx = (Float.isNaN(mPercentX)) ? 0 : mPercentX;
        float dydx = (Float.isNaN(mAltPercentY)) ? 0 : mAltPercentY;
        float dydy = (Float.isNaN(mPercentY)) ? 0 : mPercentY;
        float dxdy = (Float.isNaN(mAltPercentX)) ? 0 : mAltPercentX;
        mCalculatedPositionX = (int) (start_x + pathVectorX * dxdx + pathVectorY * dxdy);
        mCalculatedPositionY = (int) (start_y + pathVectorX * dydx + pathVectorY * dydy);
    }

    @Override
    float getPositionX() {
        return mCalculatedPositionX;
    }

    @Override
    float getPositionY() {
        return mCalculatedPositionY;
    }

    @Override
    public void positionAttributes(@NonNull View view, @NonNull RectF start, @NonNull RectF end, float x, float y, @NonNull String[] attribute, @NonNull float[] value) {
        switch (mPositionType) {

            case TYPE_PATH:
                positionPathAttributes(start, end, x, y, attribute, value);
                return;
            case TYPE_SCREEN:
                positionScreenAttributes(view, start, end, x, y, attribute, value);
                return;
            case TYPE_CARTESIAN:
            default:
                positionCartAttributes(start, end, x, y, attribute, value);
                return;

        }
    }

    void positionPathAttributes(@NonNull RectF start, @NonNull RectF end, float x, float y, @NonNull String[] attribute, @NonNull float[] value) {
        float startCenterX = start.centerX();
        float startCenterY = start.centerY();
        float endCenterX = end.centerX();
        float endCenterY = end.centerY();
        float pathVectorX = endCenterX - startCenterX;
        float pathVectorY = endCenterY - startCenterY;
        float distance = (float) Math.hypot(pathVectorX, pathVectorY);
        if (distance < 0.0001) {
            System.out.println("distance ~ 0");
            value[0] = 0;
            value[1] = 0;
            return;
        }

        float dx = pathVectorX / distance;
        float dy = pathVectorY / distance;
        float perpendicular = (dx * (y - startCenterY) - (x - startCenterX) * dy) / distance;
        float dist = (dx * (x - startCenterX) + dy * (y - startCenterY)) / distance;
        if (attribute[0] != null) {
            if (PERCENT_X.equals(attribute[0])) {
                value[0] = dist;
                value[1] = perpendicular;
            }
        } else {
            attribute[0] = PERCENT_X;
            attribute[1] = PERCENT_Y;
            value[0] = dist;
            value[1] = perpendicular;
        }
    }

    void positionScreenAttributes(@NonNull View view, @NonNull RectF start, @NonNull RectF end, float x, float y, @NonNull String[] attribute, @NonNull float[] value) {
        float startCenterX = start.centerX();
        float startCenterY = start.centerY();
        float endCenterX = end.centerX();
        float endCenterY = end.centerY();
        float pathVectorX = endCenterX - startCenterX;
        float pathVectorY = endCenterY - startCenterY;
        ViewGroup viewGroup = ((ViewGroup) view.getParent());
        int width = viewGroup.getWidth();
        int height = viewGroup.getHeight();

        if (attribute[0] != null) { // they are saying what to use
            if (PERCENT_X.equals(attribute[0])) {
                value[0] = x / width;
                value[1] = y / height;
            } else {
                value[1] = x / width;
                value[0] = y / height;
            }
        } else { // we will use what we want to
            attribute[0] = PERCENT_X;
            value[0] = x / width;
            attribute[1] = PERCENT_Y;
            value[1] = y / height;
        }
    }

    void positionCartAttributes(@NonNull RectF start, @NonNull RectF end, float x, float y, @NonNull String[] attribute, @NonNull float[] value) {
        float startCenterX = start.centerX();
        float startCenterY = start.centerY();
        float endCenterX = end.centerX();
        float endCenterY = end.centerY();
        float pathVectorX = endCenterX - startCenterX;
        float pathVectorY = endCenterY - startCenterY;
        if (attribute[0] != null) { // they are saying what to use
            if (PERCENT_X.equals(attribute[0])) {
                value[0] = (x - startCenterX) / pathVectorX;
                value[1] = (y - startCenterY) / pathVectorY;
            } else {
                value[1] = (x - startCenterX) / pathVectorX;
                value[0] = (y - startCenterY) / pathVectorY;
            }
        } else { // we will use what we want to
            attribute[0] = PERCENT_X;
            value[0] = (x - startCenterX) / pathVectorX;
            attribute[1] = PERCENT_Y;
            value[1] = (y - startCenterY) / pathVectorY;
        }
    }

    @Override
    public boolean intersects(int layoutWidth, int layoutHeight, @NonNull RectF start, @NonNull RectF end, float x, float y) {
        calcPosition(layoutWidth, layoutHeight, start.centerX(), start.centerY(), end.centerX(), end.centerY());
        if ((Math.abs(x - mCalculatedPositionX) < SELECTION_SLOPE)
                && (Math.abs(y - mCalculatedPositionY) < SELECTION_SLOPE)) {
            return true;
        }
        return false;
    }

    private static class Loader {
        private static final int TARGET_ID = 1;
        private static final int FRAME_POSITION = 2;
        private static final int TRANSITION_EASING = 3;
        private static final int CURVE_FIT = 4;
        private static final int DRAW_PATH = 5;
        private static final int PERCENT_X = 6;
        private static final int PERCENT_Y = 7;
        private static final int SIZE_PERCENT = 8;
        private static final int TYPE = 9;
        private static final int PATH_MOTION_ARC = 10;
        private static final int PERCENT_WIDTH = 11;
        private static final int PERCENT_HEIGHT = 12;

        @NonNull
        private static SparseIntArray mAttrMap = new SparseIntArray();

        static {
            mAttrMap.append(R.styleable.KeyPosition_motionTarget, TARGET_ID);
            mAttrMap.append(R.styleable.KeyPosition_framePosition, FRAME_POSITION);
            mAttrMap.append(R.styleable.KeyPosition_transitionEasing, TRANSITION_EASING);
            mAttrMap.append(R.styleable.KeyPosition_curveFit, CURVE_FIT);
            mAttrMap.append(R.styleable.KeyPosition_drawPath, DRAW_PATH);
            mAttrMap.append(R.styleable.KeyPosition_percentX, PERCENT_X);
            mAttrMap.append(R.styleable.KeyPosition_percentY, PERCENT_Y);
            mAttrMap.append(R.styleable.KeyPosition_keyPositionType, TYPE);
            mAttrMap.append(R.styleable.KeyPosition_sizePercent, SIZE_PERCENT);
            mAttrMap.append(R.styleable.KeyPosition_percentWidth, PERCENT_WIDTH);
            mAttrMap.append(R.styleable.KeyPosition_percentHeight, PERCENT_HEIGHT);
            mAttrMap.append(R.styleable.KeyPosition_pathMotionArc, PATH_MOTION_ARC);
        }

        private static void read(@NonNull KeyPosition c, @NonNull TypedArray a) {
            final int N = a.getIndexCount();
            for (int i = 0; i < N; i++) {
                int attr = a.getIndex(i);
                switch (mAttrMap.get(attr)) {
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
                    case FRAME_POSITION:
                        c.mFramePosition = a.getInt(attr, c.mFramePosition);
                        break;
                    case TRANSITION_EASING:
                        if (a.peekValue(attr).type == TypedValue.TYPE_STRING) {
                            c.mTransitionEasing = a.getString(attr);
                        } else {
                            c.mTransitionEasing = Easing.NAMED_EASING[a.getInteger(attr, 0)];
                        }
                        break;
                    case PATH_MOTION_ARC:
                        c.mPathMotionArc = a.getInt(attr, c.mPathMotionArc);
                        break;
                    case CURVE_FIT:
                        c.mCurveFit = a.getInteger(attr, c.mCurveFit);
                        break;
                    case DRAW_PATH:
                        c.mDrawPath = a.getInt(attr, c.mDrawPath);
                        break;
                    case PERCENT_X:
                        c.mPercentX = a.getFloat(attr, c.mPercentX);
                        break;
                    case PERCENT_Y:
                        c.mPercentY = a.getFloat(attr, c.mPercentY);
                        break;
                    case SIZE_PERCENT:
                        c.mPercentHeight = c.mPercentWidth = a.getFloat(attr, c.mPercentHeight);
                        break;
                    case PERCENT_WIDTH:
                        c.mPercentWidth = a.getFloat(attr, c.mPercentWidth);
                        break;
                    case PERCENT_HEIGHT:
                        c.mPercentHeight = a.getFloat(attr, c.mPercentHeight);
                        break;
                    case TYPE:
                        c.mPositionType = a.getInt(attr, c.mPositionType);
                        break;

                    default:
                        Log.e(TAG, "unused attribute 0x" + Integer.toHexString(attr) + "   " + mAttrMap.get(attr));
                        break;
                }
            }
            if (c.mFramePosition == -1) {
                Log.e(TAG, "no frame position");
            }
        }
    }

    @Override
    public void setValue(@NonNull String tag, @NonNull Object value) {
        switch (tag) {
            case TRANSITION_EASING:
                mTransitionEasing = value.toString();
                break;
            case DRAWPATH:
                mDrawPath = toInt(value);
                break;
            case PERCENT_WIDTH:
                mPercentWidth = toFloat(value);
                break;
            case PERCENT_HEIGHT:
                mPercentHeight = toFloat(value);
                break;
            case SIZE_PERCENT:
                mPercentHeight = mPercentWidth = toFloat(value);
                break;
            case PERCENT_X:
                mPercentX = toFloat(value);
                break;
            case PERCENT_Y:
                mPercentY = toFloat(value);
                break;
        }
    }

    @NonNull
    public Key copy(@NonNull Key src) {
        super.copy(src);
        KeyPosition k = (KeyPosition) src;
        mTransitionEasing = k.mTransitionEasing;
        mPathMotionArc = k.mPathMotionArc;
        mDrawPath = k.mDrawPath;
        mPercentWidth = k.mPercentWidth;
        mPercentHeight = Float.NaN;
        mPercentX = k.mPercentX;
        mPercentY = k.mPercentY;
        mAltPercentX = k.mAltPercentX;
        mAltPercentY = k.mAltPercentY;
        mCalculatedPositionX = k.mCalculatedPositionX;
        mCalculatedPositionY = k.mCalculatedPositionY;
        return this;
    }

    @NonNull
    public Key clone() {
        return new KeyPosition().copy(this);
    }
}
