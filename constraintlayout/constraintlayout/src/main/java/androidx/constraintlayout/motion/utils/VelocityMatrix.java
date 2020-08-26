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

package androidx.constraintlayout.motion.utils;

import androidx.constraintlayout.motion.widget.KeyCycleOscillator;
import androidx.constraintlayout.motion.widget.SplineSet;

/**
 * This is used to calculate the related velocity matrix for a post layout matrix
 * @hide
 */
public class VelocityMatrix {
    float mDScaleX, mDScaleY, mDTranslateX, mDTranslateY, mDRotate;
    float mRotate;
    private static String TAG = "VelocityMatrix";
    public void clear() {
        mDScaleX = mDScaleY = mDTranslateX = mDTranslateY = mDRotate = 0;
    }

    public void setRotationVelocity(SplineSet rot, float position) {
        if (rot != null) {
            mDRotate = rot.getSlope(position);
            mRotate = rot.get(position);
        }
    }

    public void setTranslationVelocity(SplineSet trans_x, SplineSet trans_y, float position) {

        if (trans_x != null) {
            mDTranslateX = trans_x.getSlope(position);
        }
        if (trans_y != null) {
            mDTranslateY = trans_y.getSlope(position);
        }
    }

    public void setScaleVelocity(SplineSet scale_x, SplineSet scale_y, float position) {

        if (scale_x != null) {
            mDScaleX = scale_x.getSlope(position);
        }
        if (scale_y != null) {
            mDScaleY = scale_y.getSlope(position);
        }
    }

    public void setRotationVelocity(KeyCycleOscillator osc_r, float position) {
        if (osc_r != null) {
            mDRotate = osc_r.getSlope(position);
        }
    }

    public void setTranslationVelocity(KeyCycleOscillator osc_x, KeyCycleOscillator osc_y, float position) {

        if (osc_x != null) {
            mDTranslateX = osc_x.getSlope(position);
        }

        if (osc_y != null) {
            mDTranslateY = osc_y.getSlope(position);
        }
    }

    public void setScaleVelocity(KeyCycleOscillator osc_sx, KeyCycleOscillator osc_sy, float position) {

        if (osc_sx == null && osc_sy == null) {
            return;
        }
        if (osc_sx == null) {
            mDScaleX = osc_sx.getSlope(position);
        }
        if (osc_sy == null) {
            mDScaleY = osc_sy.getSlope(position);
        }
    }

    /**
     * Apply the transform a velocity vector
     *
     * @param locationX
     * @param locationY
     * @param width
     * @param height
     * @param mAnchorDpDt
     * @hide
     */
    public void applyTransform(float locationX, float locationY, int width, int height, float[] mAnchorDpDt) {
        float dx = mAnchorDpDt[0];
        float dy = mAnchorDpDt[1];
        float offx = 2 * (locationX - 0.5f);
        float offy = 2 * (locationY - 0.5f);
        dx += mDTranslateX;
        dy += mDTranslateY;
        dx += offx * mDScaleX;
        dy += offy * mDScaleY;
        float r = (float) Math.toRadians(mRotate);
        float dr = (float)Math.toRadians(mDRotate);
        dx +=  dr*(float) (-width*offx*Math.sin(r)-height*offy*Math.cos(r));
        dy +=   dr*(float) (width*offx*Math.cos(r)-height*offy*Math.sin(r));
        mAnchorDpDt[0] = dx;
        mAnchorDpDt[1] = dy;
    }
}
