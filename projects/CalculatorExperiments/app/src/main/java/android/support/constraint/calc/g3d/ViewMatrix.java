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

package android.support.constraint.calc.g3d;

import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * This calculates the matrix that transforms triangles from world space to screen space.
 */
public class ViewMatrix extends Matrix {
    double[] mLookPoint;
    double[] mEyePoint;
    double[] mUpVector;
    double mScreenWidth;
    int[] mScreenDim;
    double[] mTmp1 = new double[3];
    public final static char UP_AT = 0x001;
    public final static char DOWN_AT = 0x002;
    public final static char RIGHT_AT = 0x010;
    public final static char LEFT_AT = 0x020;
    public final static char FORWARD_AT = 0x100;
    public final static char BEHIND_AT = 0x200;

    private static String toStr(double d) {
        String s = "       " + df.format(d);
        return s.substring(s.length() - 8);
    }

    private static String toStr(double[] d) {
        String s = "[";
        for (int i = 0; i < d.length; i++) {
            s += toStr(d[i]);
        }

        return s + "]";
    }

    private static DecimalFormat df = new DecimalFormat("##0.000");

    @Override
    public void print() {
        System.out.println("mLookPoint  :" + toStr(mLookPoint));
        System.out.println("mEyePoint   :" + toStr(mEyePoint));
        System.out.println("mUpVector   :" + toStr(mUpVector));
        System.out.println("mScreenWidth:" + toStr(mScreenWidth));
        System.out.println("mScreenDim  :[" + mScreenDim[0] + "," + mScreenDim[1] + "]");
    }

    public ViewMatrix() {

    }

    public void setScreenDim(int x, int y) {
        mScreenDim = new int[]{x, y};
    }

    public double[] getLookPoint() {
        return mLookPoint;
    }

    public void setLookPoint(double[] mLookPoint) {
        this.mLookPoint = mLookPoint;
    }

    public double[] getEyePoint() {
        return mEyePoint;
    }

    public void setEyePoint(double[] mEyePoint) {
        this.mEyePoint = mEyePoint;
    }

    public double[] getUpVector() {
        return mUpVector;
    }

    public void setUpVector(double[] mUpVector) {
        this.mUpVector = mUpVector;
    }

    public double getScreenWidth() {
        return mScreenWidth;
    }

    public void setScreenWidth(double screenWidth) {
        this.mScreenWidth = screenWidth;
    }

    public void makeUnit() {

    }

    public void fixUpPoint() {
        double[] zv = {
                mEyePoint[0] - mLookPoint[0],
                mEyePoint[1] - mLookPoint[1],
                mEyePoint[2] - mLookPoint[2]
        };
        VectorUtil.normalize(zv);
        double[] rv = new double[3];
        VectorUtil.cross(zv, mUpVector, rv);

        VectorUtil.cross(zv, rv, mUpVector);
        VectorUtil.normalize(mUpVector);
        VectorUtil.mult(mUpVector, -1, mUpVector);

    }

    public void calcMatrix() {
        if (mScreenDim == null) {
            return;
        }
        double scale = mScreenWidth / mScreenDim[0];
        double[] zv = {
                mLookPoint[0] - mEyePoint[0],
                mLookPoint[1] - mEyePoint[1],
                mLookPoint[2] - mEyePoint[2]
        };
        VectorUtil.normalize(zv);


        double[] m = new double[16];
        m[2] = zv[0] * scale;
        m[6] = zv[1] * scale;
        m[10] = zv[2] * scale;
        m[14] = 0;

        calcRight(zv, mUpVector, zv);
        double[] right = zv;

        m[0] = right[0] * scale;
        m[4] = right[1] * scale;
        m[8] = right[2] * scale;
        m[12] = 0;

        m[1] = -mUpVector[0] * scale;
        m[5] = -mUpVector[1] * scale;
        m[9] = -mUpVector[2] * scale;
        m[13] = 0;
        double sw = mScreenDim[0] / 2 - 0.5;
        double sh = mScreenDim[1] / 2 - 0.5;
        double sz = -0.5;
        m[3] = mEyePoint[0] - (m[0] * sw + m[1] * sh + m[2] * sz);
        m[7] = mEyePoint[1] - (m[4] * sw + m[5] * sh + m[6] * sz);
        m[11] = mEyePoint[2] - (m[8] * sw + m[9] * sh + m[10] * sz);

        m[15] = 1;
        this.m = m;
    }

    static void calcRight(double[] a, double[] b, double[] out) {
        VectorUtil.cross(a, b, out);
    }

    public static void main(String[] args) {
        double[] up = {0, 0, 1};
        double[] look = {0, 0, 0};
        double[] eye = {-10, 0, 0};
        ViewMatrix v = new ViewMatrix();
        v.setEyePoint(eye);
        v.setLookPoint(look);
        v.setUpVector(up);
        v.setScreenWidth(10);
        v.setScreenDim(512, 512);
        v.calcMatrix();
    }

    private void calcLook(SurfaceGen tri, float[] voxelDim, int w, int h) {
        float minx = Float.MAX_VALUE, miny = Float.MAX_VALUE, minz = Float.MAX_VALUE;
        float maxx = -Float.MAX_VALUE, maxy = -Float.MAX_VALUE, maxz = -Float.MAX_VALUE;

        for (int i = 0; i < tri.vert.length; i += 3) {
            maxx = Math.max(tri.vert[i], maxx);
            minx = Math.min(tri.vert[i], minx);
            maxy = Math.max(tri.vert[i + 1], maxy);
            miny = Math.min(tri.vert[i + 1], miny);
            maxz = Math.max(tri.vert[i + 2], maxz);
            minz = Math.min(tri.vert[i + 2], minz);
        }
        mLookPoint = new double[]{voxelDim[0] * (maxx + minx) / 2, voxelDim[1] * (maxy + miny) / 2, voxelDim[2] * (maxz + minz) / 2};


        mScreenWidth = Math.max(voxelDim[0] * (maxx - minx), Math.max(voxelDim[1] * (maxy - miny), voxelDim[2] * (maxz - minz))) * 2;
    }

    private void calcLook(SurfaceGen triW, int w, int h) {
        float minx = Float.MAX_VALUE, miny = Float.MAX_VALUE, minz = Float.MAX_VALUE;
        float maxx = -Float.MAX_VALUE, maxy = -Float.MAX_VALUE, maxz = -Float.MAX_VALUE;

        for (int i = 0; i < triW.vert.length; i += 3) {
            maxx = Math.max(triW.vert[i], maxx);
            minx = Math.min(triW.vert[i], minx);
            maxy = Math.max(triW.vert[i + 1], maxy);
            miny = Math.min(triW.vert[i + 1], miny);
            maxz = Math.max(triW.vert[i + 2], maxz);
            minz = Math.min(triW.vert[i + 2], minz);
        }
        mLookPoint = new double[]{(maxx + minx) / 2, (maxy + miny) / 2, (maxz + minz) / 2};

        mScreenWidth = Math.max((maxx - minx), Math.max((maxy - miny), (maxz - minz)));
    }

    public void look(char dir, SurfaceGen tri, float[] voxelDim, int w, int h) {
        calcLook(tri, w, h);
        int dx = ((dir >> 4) & 0xF);
        int dy = ((dir >> 8) & 0xF);
        int dz = ((dir >> 0) & 0xF);
        if (dx > 1) {
            dx = -1;
        }
        if (dy > 1) {
            dy = -1;
        }
        if (dz > 1) {
            dz = -1;
        }
        mEyePoint = new double[]{mLookPoint[0] + 2 * mScreenWidth * dx,
                mLookPoint[1] + 2 * mScreenWidth * dy,
                mLookPoint[2] + 2 * mScreenWidth * dz};
        double[] zv = new double[]{-dx, -dy, -dz};
        double[] rv = new double[]{(dx == 0) ? 1 : 0, (dx == 0) ? 0 : 1, 0};
        double[] up = new double[3];
        VectorUtil.norm(zv);
        VectorUtil.norm(rv);

        VectorUtil.cross(zv, rv, up);
        VectorUtil.cross(zv, up, rv);
        VectorUtil.cross(zv, rv, up);
        mUpVector = up;
        mScreenDim = new int[]{w, h};
        calcMatrix();
    }

    public void lookAt(SurfaceGen tri, float[] voxelDim, int w, int h) {
        calcLook(tri, voxelDim, w, h);

        mEyePoint = new double[]{mLookPoint[0] + mScreenWidth, mLookPoint[1] + mScreenWidth, mLookPoint[2] + mScreenWidth};
        double[] zv = new double[]{-1, -1, -1};
        double[] rv = new double[]{1, 1, 0};
        double[] up = new double[3];
        VectorUtil.norm(zv);
        VectorUtil.norm(rv);

        VectorUtil.cross(zv, rv, up);
        VectorUtil.cross(zv, up, rv);
        VectorUtil.cross(zv, rv, up);
        mUpVector = up;
        mScreenDim = new int[]{w, h};
        calcMatrix();
    }

    float mStartx, mStarty;
    float mPanStartX = Float.NaN, mPanStartY = Float.NaN;
    Matrix mStartMatrix;
    double[] mStartV = new double[3];
    double[] mMoveToV = new double[3];
    double[] mStartEyePoint;
    double[] mStartUpVector;
    Quaternion mQ = new Quaternion(0, 0, 0, 0);

    public void trackBallUP(float x, float y) {

    }

    public void trackBallDown(float x, float y) {
        mStartx = x;
        mStarty = y;
        ballToVec(x, y, mStartV);
        mStartEyePoint = Arrays.copyOf(mEyePoint, m.length);
        mStartUpVector = Arrays.copyOf(mUpVector, m.length);
        mStartMatrix = new Matrix(this);
        mStartMatrix.makeRotation();
    }

    public void trackBallMove(float x, float y) {
        if (mStartx == x && mStarty == y) {
            return;
        }
        ballToVec(x, y, mMoveToV);

        double angle = Quaternion.calcAngle(mStartV, mMoveToV);
        double[] axis = Quaternion.calcAxis(mStartV, mMoveToV);

        axis = mStartMatrix.vecmult(axis);

        mQ.set(angle, axis);

        VectorUtil.sub(mLookPoint, mStartEyePoint, mEyePoint);

        mEyePoint = mQ.rotateVec(mEyePoint);
        mUpVector = mQ.rotateVec(mStartUpVector);

        VectorUtil.sub(mLookPoint, mEyePoint, mEyePoint);
        calcMatrix();

    }

    public void panDown(float x, float y) {
        mPanStartX = x;
        mPanStartY = y;
    }

    public void panMove(float x, float y) {
        double scale = mScreenWidth / mScreenDim[0];
        if (Float.isNaN(mPanStartX)) {
            mPanStartX = x;
            mPanStartY = y;
        }
        double dx = scale * (x - mPanStartX);
        double dy = scale * (y - mPanStartY);
        VectorUtil.sub(mEyePoint, mLookPoint, mTmp1);
        VectorUtil.normalize(mTmp1);
        VectorUtil.cross(mTmp1, mUpVector, mTmp1);
        VectorUtil.madd(mTmp1, dx, mEyePoint, mEyePoint);
        VectorUtil.madd(mTmp1, dx, mLookPoint, mLookPoint);
        VectorUtil.madd(mUpVector, dy, mEyePoint, mEyePoint);
        VectorUtil.madd(mUpVector, dy, mLookPoint, mLookPoint);
        mPanStartY = y;
        mPanStartX = x;
        calcMatrix();
    }

    public void panUP() {
        mPanStartX = Float.NaN;
        mPanStartY = Float.NaN;
    }

    void ballToVec(float x, float y, double[] v) {
        float ballRadius = Math.min(mScreenDim[0], mScreenDim[1]) * .4f;
        double cx = mScreenDim[0] / 2.;
        double cy = mScreenDim[1] / 2.;

        double dx = (cx - x) / ballRadius;
        double dy = (cy - y) / ballRadius;
        double scale = dx * dx + dy * dy;
        if (scale > 1) {
            scale = Math.sqrt(scale);
            dx = dx / scale;
            dy = dy / scale;
        }

        double dz = Math.sqrt(Math.abs(1 - (dx * dx + dy * dy)));
        v[0] = dx;
        v[1] = dy;
        v[2] = dz;
        VectorUtil.normalize(v);
    }


}
