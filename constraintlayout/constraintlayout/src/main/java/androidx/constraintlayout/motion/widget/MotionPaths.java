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

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.constraintlayout.motion.utils.Easing;
import androidx.constraintlayout.widget.ConstraintAttribute;
import androidx.constraintlayout.widget.ConstraintSet;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Set;

import static androidx.constraintlayout.motion.widget.Key.UNSET;

/**
 * This is used to capture and play back path of the layout.
 * It is used to set the bounds of the view (view.layout(l, t, r, b))
 *
 * @hide
 */
class MotionPaths implements Comparable<MotionPaths> {
    public static final String TAG = "MotionPaths";
    public static final boolean DEBUG = false;
    public static final boolean OLD_WAY = false; // the computes the positions the old way
    static final int OFF_POSITION = 0;
    static final int OFF_X = 1;
    static final int OFF_Y = 2;
    static final int OFF_WIDTH = 3;
    static final int OFF_HEIGHT = 4;
    static final int OFF_PATH_ROTATE = 5;

    // mode and type have same numbering scheme
    static final int PERPENDICULAR = KeyPosition.TYPE_PATH;
    static final int CARTESIAN = KeyPosition.TYPE_CARTESIAN;
    static final int SCREEN =  KeyPosition.TYPE_SCREEN;
    static String[] names = {"position", "x", "y", "width", "height", "pathRotate"};
    Easing mKeyFrameEasing;
    int mDrawPath = 0;
    float time;
    float position;
    float x;
    float y;
    float width;
    float height;
    float mPathRotate = Float.NaN;
    float mProgress = Float.NaN;
    int mPathMotionArc = UNSET;
    int mAnimateRelativeTo = UNSET;
    float mRelativeAngle = Float.NaN;
    MotionController mRelativeToController = null;

    LinkedHashMap<String, ConstraintAttribute> attributes = new LinkedHashMap<>();
    int mMode = 0; // how was this point computed 1=perpendicular 2=deltaRelative
    int mAnimateCircleAngleTo; // since angles loop there are 4 ways we can pic direction

    public MotionPaths() {

    }

    /**
     * set up with Cartesian
     *
     * @param c
     * @param startTimePoint
     * @param endTimePoint
     */
    void initCartesian(KeyPosition c, MotionPaths startTimePoint, MotionPaths endTimePoint) {
        float position = c.mFramePosition / 100f;
        MotionPaths point = this;
        point.time = position;

        mDrawPath = c.mDrawPath;
        float scaleWidth = Float.isNaN(c.mPercentWidth) ? position : c.mPercentWidth;
        float scaleHeight = Float.isNaN(c.mPercentHeight) ? position : c.mPercentHeight;
        float scaleX = endTimePoint.width - startTimePoint.width;
        float scaleY = endTimePoint.height - startTimePoint.height;

        point.position = point.time;

        float path = position;// the position on the path

        float startCenterX = startTimePoint.x + startTimePoint.width / 2;
        float startCenterY = startTimePoint.y + startTimePoint.height / 2;
        float endCenterX = endTimePoint.x + endTimePoint.width / 2;
        float endCenterY = endTimePoint.y + endTimePoint.height / 2;
        float pathVectorX = endCenterX - startCenterX;
        float pathVectorY = endCenterY - startCenterY;
        point.x = (int) (startTimePoint.x + (pathVectorX) * path - scaleX * scaleWidth / 2);
        point.y = (int) (startTimePoint.y + (pathVectorY) * path - scaleY * scaleHeight / 2);
        point.width = (int) (startTimePoint.width + scaleX * scaleWidth);
        point.height = (int) (startTimePoint.height + scaleY * scaleHeight);

        float dxdx = (Float.isNaN(c.mPercentX)) ? position : c.mPercentX;
        float dydx = (Float.isNaN(c.mAltPercentY)) ? 0 : c.mAltPercentY;
        float dydy = (Float.isNaN(c.mPercentY)) ? position : c.mPercentY;
        float dxdy = (Float.isNaN(c.mAltPercentX)) ? 0 : c.mAltPercentX;
        point.mMode = MotionPaths.CARTESIAN;
        point.x = (int) (startTimePoint.x + pathVectorX * dxdx + pathVectorY * dxdy - scaleX * scaleWidth / 2);
        point.y = (int) (startTimePoint.y + pathVectorX * dydx + pathVectorY * dydy - scaleY * scaleHeight / 2);

        point.mKeyFrameEasing = Easing.getInterpolator(c.mTransitionEasing);
        point.mPathMotionArc = c.mPathMotionArc;
    }

    /**
     * takes the new keyPosition
     *
     * @param c
     * @param startTimePoint
     * @param endTimePoint
     */
    public MotionPaths(int parentWidth, int parentHeight, KeyPosition c, MotionPaths startTimePoint, MotionPaths endTimePoint) {
        if (startTimePoint.mAnimateRelativeTo != UNSET) {
            initPolar(parentWidth, parentHeight, c, startTimePoint, endTimePoint);
            return;
        }
        switch (c.mPositionType) {
            case KeyPosition.TYPE_SCREEN:
                initScreen(parentWidth, parentHeight, c, startTimePoint, endTimePoint);
                return;
            case KeyPosition.TYPE_PATH:
                initPath(c, startTimePoint, endTimePoint);
                return;
            default:
            case KeyPosition.TYPE_CARTESIAN:
                initCartesian(c, startTimePoint, endTimePoint);
                return;
        }
    }

    void initPolar(int parentWidth, int parentHeight, KeyPosition c, MotionPaths s, MotionPaths e) {
        float position = c.mFramePosition / 100f;
        this.time = position;
        mDrawPath = c.mDrawPath;
        this.mMode = c.mPositionType; // mode and type have same numbering scheme
        float scaleWidth = Float.isNaN(c.mPercentWidth) ? position : c.mPercentWidth;
        float scaleHeight = Float.isNaN(c.mPercentHeight) ? position : c.mPercentHeight;
        float scaleX = e.width - s.width;
        float scaleY = e.height - s.height;
        this.position = this.time;
        width = (int) (s.width + scaleX * scaleWidth);
        height = (int) (s.height + scaleY * scaleHeight);
        float startfactor = 1-position;
        float endfactor = position;
        switch (c.mPositionType) {
            case KeyPosition.TYPE_SCREEN:
                this.x = Float.isNaN(c.mPercentX)?(position*(e.x-s.x)+s.x):c.mPercentX*Math.min(scaleHeight,scaleWidth);
                this.y = Float.isNaN(c.mPercentY)? (position*(e.y-s.y)+s.y):c.mPercentY;
                break;

            case KeyPosition.TYPE_PATH:
                this.x = (Float.isNaN(c.mPercentX)?position:c.mPercentX)*(e.x-s.x)+s.x;
                this.y = (Float.isNaN(c.mPercentY)?position: c.mPercentY)*(e.y-s.y)+s.y;
                break;
            default:
            case KeyPosition.TYPE_CARTESIAN:
                this.x = (Float.isNaN(c.mPercentX)?position:c.mPercentX)*(e.x-s.x)+s.x;
                this.y = (Float.isNaN(c.mPercentY)?position: c.mPercentY)*(e.y-s.y)+s.y;
                break;
        }

        this.mAnimateRelativeTo = s.mAnimateRelativeTo;
        this.mKeyFrameEasing = Easing.getInterpolator(c.mTransitionEasing);
        this.mPathMotionArc = c.mPathMotionArc;
    }

    public void setupRelative(MotionController mc, MotionPaths relative) {
        double dx = x + width / 2 - relative.x - relative.width / 2;
        double dy = y + height / 2 - relative.y - relative.height / 2;
        mRelativeToController = mc;

        x = (float) Math.hypot(dy, dx);
        if (Float.isNaN(mRelativeAngle)) {
            y = (float) (Math.atan2(dy, dx) + Math.PI / 2);
        } else {
            y = (float) Math.toRadians(mRelativeAngle);

        }
    }

    void initScreen(int parentWidth, int parentHeight, KeyPosition c, MotionPaths startTimePoint, MotionPaths endTimePoint) {
        float position = c.mFramePosition / 100f;
        MotionPaths point = this;
        point.time = position;

        mDrawPath = c.mDrawPath;
        float scaleWidth = Float.isNaN(c.mPercentWidth) ? position : c.mPercentWidth;
        float scaleHeight = Float.isNaN(c.mPercentHeight) ? position : c.mPercentHeight;

        float scaleX = endTimePoint.width - startTimePoint.width;
        float scaleY = endTimePoint.height - startTimePoint.height;

        point.position = point.time;

        float path = position;// the position on the path

        float startCenterX = startTimePoint.x + startTimePoint.width / 2;
        float startCenterY = startTimePoint.y + startTimePoint.height / 2;
        float endCenterX = endTimePoint.x + endTimePoint.width / 2;
        float endCenterY = endTimePoint.y + endTimePoint.height / 2;
        float pathVectorX = endCenterX - startCenterX;
        float pathVectorY = endCenterY - startCenterY;
        point.x = (int) (startTimePoint.x + (pathVectorX) * path - scaleX * scaleWidth / 2);
        point.y = (int) (startTimePoint.y + (pathVectorY) * path - scaleY * scaleHeight / 2);
        point.width = (int) (startTimePoint.width + scaleX * scaleWidth);
        point.height = (int) (startTimePoint.height + scaleY * scaleHeight);

        point.mMode = MotionPaths.SCREEN;
        if (!Float.isNaN(c.mPercentX)) {
            parentWidth -= point.width;
            point.x = (int) (c.mPercentX * parentWidth);
        }
        if (!Float.isNaN(c.mPercentY)) {
            parentHeight -= point.height;
            point.y = (int) (c.mPercentY * parentHeight);
        }

        point.mAnimateRelativeTo = mAnimateRelativeTo;
        point.mKeyFrameEasing = Easing.getInterpolator(c.mTransitionEasing);
        point.mPathMotionArc = c.mPathMotionArc;
    }

    void initPath(KeyPosition c, MotionPaths startTimePoint, MotionPaths endTimePoint) {

        float position = c.mFramePosition / 100f;
        MotionPaths point = this;
        point.time = position;

        mDrawPath = c.mDrawPath;
        float scaleWidth = Float.isNaN(c.mPercentWidth) ? position : c.mPercentWidth;
        float scaleHeight = Float.isNaN(c.mPercentHeight) ? position : c.mPercentHeight;

        float scaleX = endTimePoint.width - startTimePoint.width;
        float scaleY = endTimePoint.height - startTimePoint.height;

        point.position = point.time;

        float path = Float.isNaN(c.mPercentX) ? position : c.mPercentX; // the position on the path

        float startCenterX = startTimePoint.x + startTimePoint.width / 2;
        float startCenterY = startTimePoint.y + startTimePoint.height / 2;
        float endCenterX = endTimePoint.x + endTimePoint.width / 2;
        float endCenterY = endTimePoint.y + endTimePoint.height / 2;
        float pathVectorX = endCenterX - startCenterX;
        float pathVectorY = endCenterY - startCenterY;
        point.x = (int) (startTimePoint.x + (pathVectorX) * path - scaleX * scaleWidth / 2);
        point.y = (int) (startTimePoint.y + (pathVectorY) * path - scaleY * scaleHeight / 2);
        point.width = (int) (startTimePoint.width + scaleX * scaleWidth);
        point.height = (int) (startTimePoint.height + scaleY * scaleHeight);
        float perpendicular = Float.isNaN(c.mPercentY) ? 0 : c.mPercentY;// the position on the path
        float perpendicularX = -pathVectorY;
        float perpendicularY = pathVectorX;

        float normalX = perpendicularX * perpendicular;
        float normalY = perpendicularY * perpendicular;
        point.mMode = MotionPaths.PERPENDICULAR;
        point.x = (int) (startTimePoint.x + (pathVectorX) * path - scaleX * scaleWidth / 2);
        point.y = (int) (startTimePoint.y + (pathVectorY) * path - scaleY * scaleHeight / 2);
        point.x += normalX;
        point.y += normalY;

        point.mAnimateRelativeTo = mAnimateRelativeTo;
        point.mKeyFrameEasing = Easing.getInterpolator(c.mTransitionEasing);
        point.mPathMotionArc = c.mPathMotionArc;
    }

    private static final float xRotate(float sin, float cos, float cx, float cy, float x, float y) {
        x = x - cx;
        y = y - cy;
        return x * cos - y * sin + cx;
    }

    private static final float yRotate(float sin, float cos, float cx, float cy, float x, float y) {
        x = x - cx;
        y = y - cy;
        return x * sin + y * cos + cy;
    }

    private boolean diff(float a, float b) {
        if (Float.isNaN(a) || Float.isNaN(b)) {
            return Float.isNaN(a) != Float.isNaN(b);
        }
        return Math.abs(a - b) > 0.000001f;
    }

    void different(MotionPaths points, boolean[] mask, String[] custom, boolean arcMode) {
        int c = 0;
        boolean diffx = diff(x, points.x);
        boolean diffy = diff(y, points.y);
        mask[c++] |= diff(position, points.position);
        mask[c++] |= diffx | diffy | arcMode;
        mask[c++] |= diffx | diffy | arcMode;
        mask[c++] |= diff(width, points.width);
        mask[c++] |= diff(height, points.height);

    }

    void getCenter(double p, int[] toUse, double[] data, float[] point, int offset) {
        float v_x = x;
        float v_y = y;
        float v_width = width;
        float v_height = height;
        float translationX = 0, translationY = 0;
        for (int i = 0; i < toUse.length; i++) {
            float value = (float) data[i];

            switch (toUse[i]) {
                case OFF_X:
                    v_x = value;
                    break;
                case OFF_Y:
                    v_y = value;
                    break;
                case OFF_WIDTH:
                    v_width = value;
                    break;
                case OFF_HEIGHT:
                    v_height = value;
                    break;
            }
        }
        if (mRelativeToController != null) {
            float[] pos = new float[2];
            float[] vel = new float[2];

            mRelativeToController.getCenter(p, pos,vel);
            float rx = pos[0];
            float ry =  pos[1];
            float radius = v_x;
            float angle = v_y;
            // TODO Debug angle
            v_x = (float) (rx + radius*Math.sin(angle) - v_width/2);
            v_y = (float) (ry - radius*Math.cos(angle) - v_height/2);
        }

        point[offset] = v_x + v_width / 2 + translationX;
        point[offset + 1] = v_y + v_height / 2 + translationY;
    }

    void getCenter(double p, int[] toUse, double[] data, float[] point, double[] vdata, float []velocity) {
        float v_x = x;
        float v_y = y;
        float v_width = width;
        float v_height = height;
        float dv_x = 0;
        float dv_y = 0;
        float dv_width = 0;
        float dv_height = 0;

        float translationX = 0, translationY = 0;
        for (int i = 0; i < toUse.length; i++) {
            float value = (float) data[i];
            float dvalue = (float) vdata[i];

            switch (toUse[i]) {
                case OFF_X:
                    v_x = value;
                    dv_x = dvalue;
                    break;
                case OFF_Y:
                    v_y = value;
                    dv_y = dvalue;
                    break;
                case OFF_WIDTH:
                    v_width = value;
                    dv_width = dvalue;
                    break;
                case OFF_HEIGHT:
                    v_height = value;
                    dv_height = dvalue;
                    break;
            }
        }
        float dpos_x =  dv_x + dv_width / 2;
        float dpos_y =  dv_y + dv_height / 2;

        if (mRelativeToController != null) {
            float[] pos = new float[2];
            float[] vel = new float[2];
            mRelativeToController.getCenter(p, pos, vel);
            float rx = pos[0];
            float ry =  pos[1];
            float radius = v_x;
            float angle = v_y;
            float dradius = dv_x;
            float dangle = dv_y;
            float drx  = vel[0];
            float dry = vel[1];
            // TODO Debug angle
            v_x = (float) (rx + radius*Math.sin(angle) - v_width/2);
            v_y = (float) (ry - radius*Math.cos(angle) - v_height/2);
             dpos_x = (float) (drx + dradius*Math.sin(angle)+  Math.cos(angle)*dangle  );
             dpos_y = (float) (dry - dradius*Math.cos(angle) +  Math.sin(angle)*dangle  );
        }

        point[0] = v_x + v_width / 2 + translationX;
        point[1] = v_y + v_height / 2 + translationY;
        velocity[0] = dpos_x;
        velocity[1] = dpos_y;
    }
    void getCenterVelocity(double p, int[] toUse, double[] data, float[] point, int offset) {
        float v_x = x;
        float v_y = y;
        float v_width = width;
        float v_height = height;
        float translationX = 0, translationY = 0;
        for (int i = 0; i < toUse.length; i++) {
            float value = (float) data[i];

            switch (toUse[i]) {
                case OFF_X:
                    v_x = value;
                    break;
                case OFF_Y:
                    v_y = value;
                    break;
                case OFF_WIDTH:
                    v_width = value;
                    break;
                case OFF_HEIGHT:
                    v_height = value;
                    break;
            }
        }
        if (mRelativeToController != null) {
            float[] pos = new float[2];
            float[] vel = new float[2];
            mRelativeToController.getCenter(p, pos, vel);
            float rx = pos[0];
            float ry =  pos[1];
            float radius = v_x;
            float angle = v_y;
            // TODO Debug angle
            v_x = (float) (rx + radius*Math.sin(angle) - v_width/2);
            v_y = (float) (ry - radius*Math.cos(angle) - v_height/2);
        }

        point[offset] = v_x + v_width / 2 + translationX;
        point[offset + 1] = v_y + v_height / 2 + translationY;
    }

    void getBounds(int[] toUse, double[] data, float[] point, int offset) {
        float v_x = x;
        float v_y = y;
        float v_width = width;
        float v_height = height;
        float translationX = 0, translationY = 0;
        for (int i = 0; i < toUse.length; i++) {
            float value = (float) data[i];

            switch (toUse[i]) {
                case OFF_X:
                    v_x = value;
                    break;
                case OFF_Y:
                    v_y = value;
                    break;
                case OFF_WIDTH:
                    v_width = value;
                    break;
                case OFF_HEIGHT:
                    v_height = value;
                    break;
            }
        }
        point[offset] = v_width;
        point[offset + 1] = v_height;
    }

    double[] mTempValue = new double[18];
    double[] mTempDelta = new double[18];

    // Called on the start Time Point
    void setView(float position, View view, int[] toUse, double[] data, double[] slope, double[] cycle) {
        float v_x = x;
        float v_y = y;
        float v_width = width;
        float v_height = height;
        float dv_x = 0;
        float dv_y = 0;
        float dv_width = 0;
        float dv_height = 0;
        float delta_path = 0;
        float path_rotate = Float.NaN;
        String mod;

        if (DEBUG) {
            mod = view.getContext().getResources().getResourceEntryName(view.getId()) + " <- ";
        }
        if (toUse.length != 0 && mTempValue.length <= toUse[toUse.length - 1]) {
            int scratch_data_length = toUse[toUse.length - 1] + 1;
            mTempValue = new double[scratch_data_length];
            mTempDelta = new double[scratch_data_length];
        }
        Arrays.fill(mTempValue, Double.NaN);
        for (int i = 0; i < toUse.length; i++) {
            mTempValue[toUse[i]] = data[i];
            mTempDelta[toUse[i]] = slope[i];
        }

        for (int i = 0; i < mTempValue.length; i++) {
            if (Double.isNaN(mTempValue[i]) && (cycle == null || cycle[i] == 0.0)) {
                continue;
            }
            double deltaCycle = (cycle != null) ? cycle[i] : 0.0;
            float value = (float) (Double.isNaN(mTempValue[i]) ? deltaCycle : mTempValue[i] + deltaCycle);
            float dvalue = (float) mTempDelta[i];
            if (DEBUG) {
                Log.v(TAG, Debug.getName(view) + " set " + names[i]);
            }
            switch (i) {
                case OFF_POSITION:
                    delta_path = value;
                    break;
                case OFF_X:
                    v_x = value;
                    dv_x = dvalue;

                    break;
                case OFF_Y:
                    v_y = value;
                    dv_y = dvalue;
                    break;
                case OFF_WIDTH:
                    v_width = value;
                    dv_width = dvalue;
                    break;
                case OFF_HEIGHT:
                    v_height = value;
                    dv_height = dvalue;
                    break;
                case OFF_PATH_ROTATE:
                    path_rotate = value;
                    break;
            }
        }

        if (mRelativeToController != null) {
            float[] pos = new float[2];
            float[] vel = new float[2];

            mRelativeToController.getCenter(position, pos, vel);
            float rx = pos[0];
            float ry = pos[1];
            float radius = v_x;
            float angle = v_y;
            float dradius = dv_x;
            float dangle = dv_y;
            float drx = vel[0];
            float dry = vel[1];

            // TODO Debug angle
            float pos_x = (float) (rx + radius * Math.sin(angle) - v_width / 2);
            float pos_y = (float) (ry - radius * Math.cos(angle) - v_height / 2);
            float dpos_x = (float) (drx + dradius * Math.sin(angle) + radius*Math.cos(angle) * dangle);
            float dpos_y = (float) (dry - dradius * Math.cos(angle) + radius*Math.sin(angle) * dangle);
            dv_x = dpos_x;
            dv_y = dpos_y;
            v_x = pos_x;
            v_y = pos_y;
            if (slope.length >= 2) {
                slope[0] = dpos_x;
                slope[1] = dpos_y;
            }
            if (!Float.isNaN(path_rotate)) {
                float rot = (float) (path_rotate + Math.toDegrees(Math.atan2(dv_y, dv_x)));
                view.setRotation(rot);
            }

        } else {

            if (!Float.isNaN(path_rotate)) {
                float rot = 0;
                float dx = dv_x + dv_width / 2;
                float dy = dv_y + dv_height / 2;
                if (DEBUG) {
                    Log.v(TAG, "dv_x       =" + dv_x);
                    Log.v(TAG, "dv_y       =" + dv_y);
                    Log.v(TAG, "dv_width   =" + dv_width);
                    Log.v(TAG, "dv_height  =" + dv_height);
                }
                rot += path_rotate + Math.toDegrees(Math.atan2(dy, dx));
                view.setRotation(rot);
                if (DEBUG) {
                    Log.v(TAG, "Rotated " + rot + "  = " + dx + "," + dy);
                }
            }
        }

        if (view instanceof FloatLayout) {
            float l = v_x;
            float t = v_y;
            float r = v_x + v_width;
            float b = v_y + v_height;
            ((FloatLayout) view).layout(l, t, r, b);
            return;
        }
        int l = (int) (0.5f + v_x);
        int t = (int) (0.5f + v_y);
        int r = (int) (0.5f + v_x + v_width);
        int b = (int) (0.5f + v_y + v_height);
        int i_width = r - l;
        int i_height = b - t;
        if (OLD_WAY) { // This way may produce more stable with and height but risk gaps
            l = (int) (v_x);
            t = (int) (v_y);
            i_width = (int) (v_width);
            i_height = (int) (v_height);
            r = l + i_width;
            b = t + i_height;
        }

        boolean remeasure = i_width != view.getMeasuredWidth() || i_height != view.getMeasuredHeight();

        if (remeasure) {
            int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(i_width, View.MeasureSpec.EXACTLY);
            int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(i_height, View.MeasureSpec.EXACTLY);

            view.measure(widthMeasureSpec, heightMeasureSpec);
        }

        view.layout(l, t, r, b);
        if (DEBUG) {
            if (toUse.length > 0) {
                Log.v(TAG, "setView " + mod);
            }
        }
    }

    void getRect(int[] toUse, double[] data, float[] path, int offset) {
        float v_x = x;
        float v_y = y;
        float v_width = width;
        float v_height = height;
        float delta_path = 0;
        float rotation = 0;
        float alpha = 0;
        float rotationX = 0;
        float rotationY = 0;
        float scaleX = 1;
        float scaleY = 1;
        float pivotX = Float.NaN;
        float pivotY = Float.NaN;
        float translationX = 0;
        float translationY = 0;

        String mod;

        for (int i = 0; i < toUse.length; i++) {
            float value = (float) data[i];

            switch (toUse[i]) {
                case OFF_POSITION:
                    delta_path = value;
                    break;
                case OFF_X:
                    v_x = value;
                    break;
                case OFF_Y:
                    v_y = value;
                    break;
                case OFF_WIDTH:
                    v_width = value;
                    break;
                case OFF_HEIGHT:
                    v_height = value;
                    break;
            }
        }

        if (mRelativeToController != null) {
            float rx = mRelativeToController.getCenterX();
            float ry = mRelativeToController.getCenterY();
            float radius = v_x;
            float angle = v_y;
            // TODO Debug angle
            v_x = (float) (rx + radius*Math.sin(angle) - v_width/2);
            v_y = (float) (ry - radius*Math.cos(angle) - v_height/2);
        }

        float x1 = v_x;
        float y1 = v_y;

        float x2 = v_x + v_width;
        float y2 = y1;

        float x3 = x2;
        float y3 = v_y + v_height;

        float x4 = x1;
        float y4 = y3;

        float cx = x1 + v_width / 2;
        float cy = y1 + v_height / 2;

        if (!Float.isNaN(pivotX)) {
            cx = x1 + (x2 - x1) * pivotX;
        }
        if (!Float.isNaN(pivotY)) {

            cy = y1 + (y3 - y1) * pivotY;
        }
        if (scaleX != 1) {
            float midx = (x1 + x2) / 2;
            x1 = (x1 - midx) * scaleX + midx;
            x2 = (x2 - midx) * scaleX + midx;
            x3 = (x3 - midx) * scaleX + midx;
            x4 = (x4 - midx) * scaleX + midx;
        }
        if (scaleY != 1) {
            float midy = (y1 + y3) / 2;
            y1 = (y1 - midy) * scaleY + midy;
            y2 = (y2 - midy) * scaleY + midy;
            y3 = (y3 - midy) * scaleY + midy;
            y4 = (y4 - midy) * scaleY + midy;
        }
        if (rotation != 0) {
            float sin = (float) Math.sin(Math.toRadians(rotation));
            float cos = (float) Math.cos(Math.toRadians(rotation));
            float tx1 = xRotate(sin, cos, cx, cy, x1, y1);
            float ty1 = yRotate(sin, cos, cx, cy, x1, y1);
            float tx2 = xRotate(sin, cos, cx, cy, x2, y2);
            float ty2 = yRotate(sin, cos, cx, cy, x2, y2);
            float tx3 = xRotate(sin, cos, cx, cy, x3, y3);
            float ty3 = yRotate(sin, cos, cx, cy, x3, y3);
            float tx4 = xRotate(sin, cos, cx, cy, x4, y4);
            float ty4 = yRotate(sin, cos, cx, cy, x4, y4);
            x1 = tx1;
            y1 = ty1;
            x2 = tx2;
            y2 = ty2;
            x3 = tx3;
            y3 = ty3;
            x4 = tx4;
            y4 = ty4;
        }

        x1 += translationX;
        y1 += translationY;
        x2 += translationX;
        y2 += translationY;
        x3 += translationX;
        y3 += translationY;
        x4 += translationX;
        y4 += translationY;

        path[offset++] = x1;
        path[offset++] = y1;
        path[offset++] = x2;
        path[offset++] = y2;
        path[offset++] = x3;
        path[offset++] = y3;
        path[offset++] = x4;
        path[offset++] = y4;
    }

    /**
     * mAnchorDpDt
     *
     * @param locationX
     * @param locationY
     * @param mAnchorDpDt
     * @param toUse
     * @param deltaData
     * @param data
     */
    void setDpDt(float locationX, float locationY, float[] mAnchorDpDt, int[] toUse, double[] deltaData, double[] data) {

        float d_x = 0;
        float d_y = 0;
        float d_width = 0;
        float d_height = 0;

        float deltaScaleX = 0;
        float deltaScaleY = 0;

        float mPathRotate = Float.NaN;
        float deltaTranslationX = 0;
        float deltaTranslationY = 0;

        String mod = " dd = ";
        for (int i = 0; i < toUse.length; i++) {
            float deltaV = (float) deltaData[i];
            float value = (float) data[i];
            if (DEBUG) {
                mod += " , D" + names[toUse[i]] + "/Dt= " + deltaV;
            }
            switch (toUse[i]) {
                case OFF_POSITION:
                    break;
                case OFF_X:
                    d_x = deltaV;
                    break;
                case OFF_Y:
                    d_y = deltaV;
                    break;
                case OFF_WIDTH:
                    d_width = deltaV;
                    break;
                case OFF_HEIGHT:
                    d_height = deltaV;
                    break;

            }
        }
        if (DEBUG) {
            if (toUse.length > 0) {
                Log.v(TAG, "setDpDt " + mod);
            }
        }

        float deltaX = d_x - deltaScaleX * d_width / 2;
        float deltaY = d_y - deltaScaleY * d_height / 2;
        float deltaWidth = d_width * (1 + deltaScaleX);
        float deltaHeight = d_height * (1 + deltaScaleY);
        float deltaRight = deltaX + deltaWidth;
        float deltaBottom = deltaY + deltaHeight;
        if (DEBUG) {
            if (toUse.length > 0) {

                Log.v(TAG, "D x /dt           =" + d_x);
                Log.v(TAG, "D y /dt           =" + d_y);
                Log.v(TAG, "D width /dt       =" + d_width);
                Log.v(TAG, "D height /dt      =" + d_height);
                Log.v(TAG, "D deltaScaleX /dt =" + deltaScaleX);
                Log.v(TAG, "D deltaScaleY /dt =" + deltaScaleY);
                Log.v(TAG, "D deltaX /dt      =" + deltaX);
                Log.v(TAG, "D deltaY /dt      =" + deltaY);
                Log.v(TAG, "D deltaWidth /dt  =" + deltaWidth);
                Log.v(TAG, "D deltaHeight /dt =" + deltaHeight);
                Log.v(TAG, "D deltaRight /dt  =" + deltaRight);
                Log.v(TAG, "D deltaBottom /dt =" + deltaBottom);
                Log.v(TAG, "locationX         =" + locationX);
                Log.v(TAG, "locationY         =" + locationY);
                Log.v(TAG, "deltaTranslationX =" + deltaTranslationX);
                Log.v(TAG, "deltaTranslationX =" + deltaTranslationX);
            }
        }

        mAnchorDpDt[0] = deltaX * (1 - locationX) + deltaRight * (locationX) + deltaTranslationX;
        mAnchorDpDt[1] = deltaY * (1 - locationY) + deltaBottom * (locationY) + deltaTranslationY;
    }

    void fillStandard(double[] data, int[] toUse) {
        float[] set = {position, x, y, width, height, mPathRotate};
        int c = 0;
        for (int i = 0; i < toUse.length; i++) {
            if (toUse[i] < set.length) {
                data[c++] = set[toUse[i]];
            }
        }
    }

    boolean hasCustomData(String name) {
        return attributes.containsKey(name);
    }

    int getCustomDataCount(String name) {
        ConstraintAttribute a = attributes.get(name);
        if (a == null) {
            return 0;
        }
        return a.noOfInterpValues();
    }

    int getCustomData(String name, double[] value, int offset) {
        ConstraintAttribute a = attributes.get(name);
        if (a == null) {
            return 0;
        } else if (a.noOfInterpValues() == 1) {
            value[offset] = a.getValueToInterpolate();
            return 1;
        } else {
            int N = a.noOfInterpValues();
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
    public int compareTo(@NonNull MotionPaths o) {
        return Float.compare(position, o.position);
    }

    public void applyParameters(ConstraintSet.Constraint c) {
        MotionPaths point = this;
        point.mKeyFrameEasing = Easing.getInterpolator(c.motion.mTransitionEasing);
        point.mPathMotionArc = c.motion.mPathMotionArc;
        point.mAnimateRelativeTo = c.motion.mAnimateRelativeTo;
        point.mPathRotate = c.motion.mPathRotate;
        point.mDrawPath = c.motion.mDrawPath;
        point.mAnimateCircleAngleTo = c.motion.mAnimateCircleAngleTo;
        point.mProgress = c.propertySet.mProgress;
        point.mRelativeAngle = c.layout.circleAngle;
        Set<String> at = c.mCustomConstraints.keySet();
        for (String s : at) {
            ConstraintAttribute attr = c.mCustomConstraints.get(s);
            if (attr != null && attr.isContinuous()) {
                this.attributes.put(s, attr);
            }
        }
    }

    public void configureRelativeTo(MotionController toOrbit) {
       double []p = toOrbit.getPos(mProgress); // get the position in the orbit
    }
}
