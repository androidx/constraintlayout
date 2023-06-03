/*
 * Copyright (C) 2023 The Android Open Source Project
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
package com.support.constraintlayout.extlib.graph3d.objects;

import com.support.constraintlayout.extlib.graph3d.Object3D;

/**
 * Plots a surface based on Z = f(X,Y)
 */
public class Surface3D extends Object3D {

    private int mSize = 100; // the number of point on the side total points = SIZE*SIZE
    private Function mFunction;
    private float mZoomZ = 1;

    public void setRange(float minX, float maxX, float minY, float maxY) {
        mMinX = minX;
        mMaxX = maxX;
        mMinY = minY;
        mMaxY = maxY;
        computeSurface(true);
    }

    public interface Function {
        float eval(float x, float y);
    }

    public Surface3D(Function func) {
        mFunction = func;
    }


    public int getArraySize() {
        return mSize;
    }

    public void setArraySize(int mSize) {
        this.mSize = mSize;
    }

    public void computeSurface(boolean resetZ) {
        int n = (mSize + 1) * (mSize + 1);
        vert = new float[n * 3];
        tVert = new float[n * 3];
        index = new int[mSize * mSize * 6];
        float min_x = mMinX;
        float max_x = mMaxX;
        float min_y = mMinY;
        float max_y = mMaxY;
        float min_z = Float.MAX_VALUE;
        float max_z = -Float.MAX_VALUE;


        int count = 0;
        for (int iy = 0; iy <= mSize; iy++) {
            float y = min_y + iy * (max_y - min_y) / (mSize);
            for (int ix = 0; ix <= mSize; ix++) {
                float x = min_x + ix * (max_x - min_x) / (mSize);
                vert[count++] = x;
                vert[count++] = y;
                float z = mFunction.eval(x, y);

                if (Float.isNaN(z) || Float.isInfinite(z)) {
                    float epslonX = 0.000005232f;
                    float epslonY = 0.00000898f;
                    z = mFunction.eval(x + epslonX, y + epslonY);
                }
                vert[count++] = z;
                if (Float.isNaN(z)) {
                    continue;
                }

                if (Float.isInfinite(z)) {
                    continue;
                }
                min_z = Math.min(z, min_z);
                max_z = Math.max(z, max_z);
            }
            if (resetZ) {
                mMinZ = min_z;
                mMaxZ = max_z;
            }
        }
        // normalize range in z
        float xrange = mMaxX - mMinX;
        float yrange = mMaxY - mMinY;
        float zrange = max_z - min_z;
        if (zrange != 0) {
            float xyrange = (xrange + yrange) / 2;
            float scalez = xyrange / zrange;

            for (int i = 0; i < vert.length; i += 3) {
                float z = vert[i + 2];
                if (Float.isNaN(z) || Float.isInfinite(z)) {
                    if (i > 3) {
                        z = vert[i - 1];
                    } else {
                        z = vert[i + 5];
                    }
                }
                vert[i + 2] = z * scalez * mZoomZ;
            }
            if (resetZ) {
                mMinZ *= scalez;
                mMaxZ *= scalez;
            }
        }
        count = 0;
        for (int iy = 0; iy < mSize; iy++) {
            for (int ix = 0; ix < mSize; ix++) {
                int p1 = 3 * (ix + iy * (mSize + 1));
                int p2 = 3 * (1 + ix + iy * (mSize + 1));
                int p3 = 3 * (ix + (iy + 1) * (mSize + 1));
                int p4 = 3 * (1 + ix + (iy + 1) * (mSize + 1));
                index[count++] = p1;
                index[count++] = p2;
                index[count++] = p3;

                index[count++] = p4;
                index[count++] = p3;
                index[count++] = p2;
            }
        }
    }

}
