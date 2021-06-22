/*
 * Copyright (C) 2021 The Android Open Source Project
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
package androidx.constraintlayout.core.motion;

import androidx.constraintlayout.core.motion.utils.Easing;
import androidx.constraintlayout.core.motion.utils.Rect;
import androidx.constraintlayout.core.motion.utils.SplineSet;
import androidx.constraintlayout.core.motion.utils.TypedValues;
import androidx.constraintlayout.core.motion.utils.Utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

/**
 * All the parameter it extracts from a ConstraintSet/View
 *
 * @hide
 */
class MotionConstrainedPoint implements Comparable<MotionConstrainedPoint> {
    public static final String TAG = "MotionPaths";
    public static final boolean DEBUG = false;

    private float alpha = 1;
    int mVisibilityMode = MotionWidget.VISIBILITY_MODE_NORMAL;
    int visibility;
    private boolean applyElevation = false;
    private float elevation = 0;
    private float rotation = 0;
    private float rotationX = 0;
    public float rotationY = 0;
    private float scaleX = 1;
    private float scaleY = 1;
    private float mPivotX = Float.NaN;
    private float mPivotY = Float.NaN;
    private float translationX = 0;
    private float translationY = 0;
    private float translationZ = 0;
    private Easing mKeyFrameEasing;
    private int mDrawPath = 0;
    private float position;
    private float x;
    private float y;
    private float width;
    private float height;
    private float mPathRotate = Float.NaN;
    private float mProgress = Float.NaN;
    private int mAnimateRelativeTo = -1;

    static final int PERPENDICULAR = 1;
    static final int CARTESIAN = 2;
    static String[] names = {"position", "x", "y", "width", "height", "pathRotate"};

    LinkedHashMap<String, CustomVariable> mCustomVariable = new LinkedHashMap<>();
    int mMode = 0; // how was this point computed 1=perpendicular 2=deltaRelative

    public MotionConstrainedPoint() {

    }

    private boolean diff(float a, float b) {
        if (Float.isNaN(a) || Float.isNaN(b)) {
            return Float.isNaN(a) != Float.isNaN(b);
        }
        return Math.abs(a - b) > 0.000001f;
    }

    /**
     * Given the start and end points define Keys that need to be built
     *
     * @param points
     * @param keySet
     */
    void different(MotionConstrainedPoint points, HashSet<String> keySet) {
        if (diff(alpha, points.alpha)) {
            keySet.add(TypedValues.Attributes.S_ALPHA);
        }
        if (diff(elevation, points.elevation)) {
            keySet.add(TypedValues.Attributes.S_TRANSLATION_Z);
        }
        if (visibility != points.visibility
                && mVisibilityMode == MotionWidget.VISIBILITY_MODE_NORMAL
                && (visibility == MotionWidget.VISIBLE
                || points.visibility == MotionWidget.VISIBLE)) {
            keySet.add(TypedValues.Attributes.S_ALPHA);
        }
        if (diff(rotation, points.rotation)) {
            keySet.add(TypedValues.Attributes.S_ROTATION_Z);
        }
        if (!(Float.isNaN(mPathRotate) && Float.isNaN(points.mPathRotate))) {
            keySet.add(TypedValues.Attributes.S_PATH_ROTATE);
        }
        if (!(Float.isNaN(mProgress) && Float.isNaN(points.mProgress))) {
            keySet.add(TypedValues.Attributes.S_PROGRESS);
        }
        if (diff(rotationX, points.rotationX)) {
            keySet.add(TypedValues.Attributes.S_ROTATION_X);
        }
        if (diff(rotationY, points.rotationY)) {
            keySet.add(TypedValues.Attributes.S_ROTATION_Y);
        }
        if (diff(mPivotX, points.mPivotX)) {
            keySet.add(TypedValues.Attributes.S_PIVOT_X);
        }
        if (diff(mPivotY, points.mPivotY)) {
            keySet.add(TypedValues.Attributes.S_PIVOT_Y);
        }
        if (diff(scaleX, points.scaleX)) {
            keySet.add(TypedValues.Attributes.S_SCALE_X);
        }
        if (diff(scaleY, points.scaleY)) {
            keySet.add(TypedValues.Attributes.S_SCALE_Y);
        }
        if (diff(translationX, points.translationX)) {
            keySet.add(TypedValues.Attributes.S_TRANSLATION_X);
        }
        if (diff(translationY, points.translationY)) {
            keySet.add(TypedValues.Attributes.S_TRANSLATION_Y);
        }
        if (diff(translationZ, points.translationZ)) {
            keySet.add(TypedValues.Attributes.S_TRANSLATION_Z);
        }
        if (diff(elevation, points.elevation)) {
            keySet.add(TypedValues.Attributes.S_ELEVATION);
        }
    }

    void different(MotionConstrainedPoint points, boolean[] mask, String[] custom) {
        int c = 0;
        mask[c++] |= diff(position, points.position);
        mask[c++] |= diff(x, points.x);
        mask[c++] |= diff(y, points.y);
        mask[c++] |= diff(width, points.width);
        mask[c++] |= diff(height, points.height);
    }

    double[] mTempValue = new double[18];
    double[] mTempDelta = new double[18];

    void fillStandard(double[] data, int[] toUse) {
        float[] set = {position, x, y, width, height, alpha, elevation, rotation, rotationX, rotationY,
                scaleX, scaleY, mPivotX, mPivotY, translationX, translationY, translationZ, mPathRotate};
        int c = 0;
        for (int i = 0; i < toUse.length; i++) {
            if (toUse[i] < set.length) {
                data[c++] = set[toUse[i]];
            }
        }
    }

    boolean hasCustomData(String name) {
        return mCustomVariable.containsKey(name);
    }

    int getCustomDataCount(String name) {
        return mCustomVariable.get(name).numberOfInterpolatedValues();
    }

    int getCustomData(String name, double[] value, int offset) {
        CustomVariable a = mCustomVariable.get(name);
        if (a.numberOfInterpolatedValues() == 1) {
            value[offset] = a.getValueToInterpolate();
            return 1;
        } else {
            int N = a.numberOfInterpolatedValues();
            float[] f = new float[N];
            a.getValuesToInterpolate(f);
            for (int i = 0; i < N; i++) {
                value[offset++] = f[i];
            }
            return N;
        }
    }

    void setBounds(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        width = w;
        height = h;
    }

    @Override
    public int compareTo(MotionConstrainedPoint o) {
        return Float.compare(position, o.position);
    }

    public void applyParameters(MotionWidget view) {

        this.visibility = view.getVisibility();
        this.alpha = (view.getVisibility() != MotionWidget.VISIBLE) ? 0.0f : view.getAlpha();
        this.applyElevation = false; // TODO figure a way to cache parameters

        this.rotation = view.getRotationZ();
        this.rotationX = view.getRotationX();
        this.rotationY = view.getRotationY();
        this.scaleX = view.getScaleX();
        this.scaleY = view.getScaleY();
        this.mPivotX = view.getPivotX();
        this.mPivotY = view.getPivotY();
        this.translationX = view.getTranslationX();
        this.translationY = view.getTranslationY();
        this.translationZ = view.getTranslationZ();

    }

    public void addValues(HashMap<String, SplineSet> splines, int mFramePosition) {
        for (String s : splines.keySet()) {
            SplineSet ViewSpline = splines.get(s);
            if (DEBUG) {
                Utils.log(TAG, "setPoint" + mFramePosition + "  spline set = " + s);
            }
            switch (s) {
                case TypedValues.Attributes.S_ALPHA:
                    ViewSpline.setPoint(mFramePosition, Float.isNaN(alpha) ? 1 : alpha);
                    break;
                case TypedValues.Attributes.S_ROTATION_Z:
                    ViewSpline.setPoint(mFramePosition, Float.isNaN(rotation) ? 0 : rotation);
                    break;
                case TypedValues.Attributes.S_ROTATION_X:
                    ViewSpline.setPoint(mFramePosition, Float.isNaN(rotationX) ? 0 : rotationX);
                    break;
                case TypedValues.Attributes.S_ROTATION_Y:
                    ViewSpline.setPoint(mFramePosition, Float.isNaN(rotationY) ? 0 : rotationY);
                    break;
                case TypedValues.Attributes.S_PIVOT_X:
                    ViewSpline.setPoint(mFramePosition, Float.isNaN(mPivotX) ? 0 : mPivotX);
                    break;
                case TypedValues.Attributes.S_PIVOT_Y:
                    ViewSpline.setPoint(mFramePosition, Float.isNaN(mPivotY) ? 0 : mPivotY);
                    break;
                case TypedValues.Attributes.S_PATH_ROTATE:
                    ViewSpline.setPoint(mFramePosition, Float.isNaN(mPathRotate) ? 0 : mPathRotate);
                    break;
                case TypedValues.Attributes.S_PROGRESS:
                    ViewSpline.setPoint(mFramePosition, Float.isNaN(mProgress) ? 0 : mProgress);
                    break;
                case TypedValues.Attributes.S_SCALE_X:
                    ViewSpline.setPoint(mFramePosition, Float.isNaN(scaleX) ? 1 : scaleX);
                    break;
                case TypedValues.Attributes.S_SCALE_Y:
                    ViewSpline.setPoint(mFramePosition, Float.isNaN(scaleY) ? 1 : scaleY);
                    break;
                case TypedValues.Attributes.S_TRANSLATION_X:
                    ViewSpline.setPoint(mFramePosition, Float.isNaN(translationX) ? 0 : translationX);
                    break;
                case TypedValues.Attributes.S_TRANSLATION_Y:
                    ViewSpline.setPoint(mFramePosition, Float.isNaN(translationY) ? 0 : translationY);
                    break;
                case TypedValues.Attributes.S_TRANSLATION_Z:
                    ViewSpline.setPoint(mFramePosition, Float.isNaN(translationZ) ? 0 : translationZ);
                    break;
                default:
                    if (s.startsWith("CUSTOM")) {
                        String customName = s.split(",")[1];
                        if (mCustomVariable.containsKey(customName)) {
                            CustomVariable custom = mCustomVariable.get(customName);
                            if (ViewSpline instanceof SplineSet.CustomSpline) {
                                ((SplineSet.CustomSpline) ViewSpline).setPoint(mFramePosition, custom);
                            } else {
                                Utils.loge(TAG, s + " ViewSpline not a CustomSet frame = " +
                                        mFramePosition + ", value" + custom.getValueToInterpolate() +
                                        ViewSpline);

                            }

                        }
                    } else {
                        Utils.loge(TAG, "UNKNOWN spline " + s);
                    }
            }
        }

    }

    public void setState(MotionWidget view) {
        setBounds(view.getX(), view.getY(), view.getWidth(), view.getHeight());
        applyParameters(view);
    }

    /**
     * @param rect     assumes pre rotated
     * @param view
     * @param rotation mode Surface.ROTATION_0,Surface.ROTATION_90...
     */
    public void setState(Rect rect, MotionWidget view, int rotation, float prevous) {
        setBounds(rect.left, rect.top, rect.width(), rect.height());
        applyParameters(view);
        mPivotX = Float.NaN;
        mPivotY = Float.NaN;

        switch (rotation) {
            case MotionWidget.ROTATE_PORTRATE_OF_LEFT:
                this.rotation = prevous + 90;
                break;
            case MotionWidget.ROTATE_PORTRATE_OF_RIGHT:
                this.rotation = prevous - 90;
                break;
        }
    }

//   TODO support Screen Rotation
//    /**
//     * Sets the state of the position given a rect, constraintset, rotation and viewid
//     *
//     * @param cw
//     * @param constraintSet
//     * @param rotation
//     * @param viewId
//     */
//    public void setState(Rect cw, ConstraintSet constraintSet, int rotation, int viewId) {
//        setBounds(cw.left, cw.top, cw.width(), cw.height());
//        applyParameters(constraintSet.getParameters(viewId));
//        switch (rotation) {
//            case ConstraintSet.ROTATE_PORTRATE_OF_RIGHT:
//            case ConstraintSet.ROTATE_RIGHT_OF_PORTRATE:
//                this.rotation -= 90;
//                break;
//            case ConstraintSet.ROTATE_PORTRATE_OF_LEFT:
//            case ConstraintSet.ROTATE_LEFT_OF_PORTRATE:
//                this.rotation += 90;
//                if (this.rotation > 180) this.rotation -= 360;
//                break;
//        }
//    }
}
