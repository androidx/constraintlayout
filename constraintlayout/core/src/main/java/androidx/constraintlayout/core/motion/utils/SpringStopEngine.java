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

package androidx.constraintlayout.core.motion.utils;

/**
 * This contains the class to provide the logic for an animation to come to a stop using a spring
 * model.
 *
 * @hide
 */
public class SpringStopEngine implements StopEngine {
    double mDamping = 0.5f;
    private static final double UNSET = Double.MAX_VALUE;
    private boolean mInitialized = false;
    private double mStiffness;
    private double mTargetPos;
    private double mLastVelocity;
    private float mLastTime;
    private float mPos;
    private float mV;
    private float mMass;
    private float mStopThreshold;
    private int mBoundaryMode = 0;

    @Override
    public String debug(String desc, float time) {
        return null;
    }

    void log(String str) {
        StackTraceElement s = new Throwable().getStackTrace()[1];
        String line = ".(" + s.getFileName() + ":" + s.getLineNumber() + ") " + s.getMethodName() + "() ";
        System.out.println(line + str);
    }

    public void springConfig(float currentPos, float target, float currentVelocity, float mass,
                             float stiffness, float damping, float stopThreshold, int boundaryMode) {
        mTargetPos = target;
        mDamping = damping;
        mInitialized = false;
        mPos = currentPos;
        mLastVelocity = currentVelocity;
        mStiffness = stiffness;
        mMass = mass;
        mStopThreshold = stopThreshold;
        mBoundaryMode = boundaryMode;
        mLastTime = 0;
    }

    @Override
    public float getVelocity(float t) {
        return (float) mV;
    }

    @Override
    public float getInterpolation(float time) {
        compute(time - mLastTime);
        mLastTime = time;
        return (float) (mPos);
    }

    public float getAcceleration() {
        double k = mStiffness;
        double c = mDamping;
        double x = (mPos - mTargetPos);
        return (float) (-k * x - c * mV) / mMass;
    }

    @Override
    public float getVelocity() {
        return 0;
    }

    @Override
    public boolean isStopped() {
        double x = (mPos - mTargetPos);
        double k = mStiffness;
        double v = mV;
        double m = mMass;
        double energy = v * v * m + k * x * x;
        double max_def = Math.sqrt(energy / k);
        return max_def <= mStopThreshold;
    }

    private void compute(double dt) {
        double x = (mPos - mTargetPos);
        double a = getAcceleration();
        double dv = a * dt;
        double avgV = mV + dv / 2;
        mV += dv / 2;
        mPos += avgV * dt;
        if (mBoundaryMode > 0) {
            if (mPos < 0 && ((mBoundaryMode & 1) == 1)) {
                mPos = -mPos;
                mV = -mV;
            }
            if (mPos > 1 && ((mBoundaryMode & 2) == 2)) {
                mPos = 2 - mPos;
                mV = -mV;
            }
        }
    }
}
