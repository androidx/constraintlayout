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
package android.support.viewsgraph3d;

import android.support.constraintLayout.extlib.graph3d.objects.Surface3D;

class SurfaceGL extends Object3DGL {
    private Surface3D.Function mFunction;
    private float mZoomZ = 1;
    int mSize = 100;
    float time = 0;

    public void setRange(float minX, float maxX, float minY, float maxY, float minZ, float maxZ) {
        mMinX = minX;
        mMaxX = maxX;
        mMinY = minY;
        mMaxY = maxY;
        mMinZ = minZ;
        mMaxZ = maxZ;
        computeSurface(time, Float.isNaN(mMinZ));
    }

    public void setArraySize(int size) {
        mSize = size;
        computeSurface(time, false);
    }

    public interface Function {
        float eval(float x, float y, float t);
    }

    public SurfaceGL(Surface3D.Function func) {
        mFunction = func;
        setRange(-20, 20, -20, 20, -2, 10);
        computeSurface(0, true);

    }

    public void computeSurface(float t, boolean resetZ) {
        int n = (mSize + 1) * (mSize + 1);
        makeVert(n);
        makeIndexes(mSize * mSize * 2);
        calcSurface(t, resetZ);
        setupGLBuffers();
        fillBuffers();
    }

    public void calcSurface(float t, boolean resetZ) {
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
                float delta = 0.001f;
                float dx = (mFunction.eval(x + delta, y, t) - mFunction.eval(x - delta, y, t)) / (2 * delta);
                float dy = (mFunction.eval(x, y + delta, t) - mFunction.eval(x, y - delta, t)) / (2 * delta);
                float dz = 0.99f;
                float norm =  (float) Math.sqrt(dz * dz + dx * dx + dy * dy);

                dx /= norm;
                dy /= norm;
                dz /= norm;
                int color_p = 4*(count/3);
                normal[count] = dx;
                vert[count++] = x;
                normal[count] = dy;
                vert[count++] = y;
                normal[count] = dz;
                float z = mFunction.eval(x, y, t);

                if (Float.isNaN(z) || Float.isInfinite(z)) {
                    float epslonX = 0.000005232f;
                    float epslonY = 0.00000898f;
                    z = mFunction.eval(x + epslonX, y + epslonY, t);
                }
                vert[count++] = z;
                z *= z;
                colors[color_p] = 0f;
                colors[color_p + 1] = z;
                colors[color_p + 2] = 1;
                colors[color_p + 3] = 1.0f;
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
//        float xrange = mMaxX - mMinX;
//        float yrange = mMaxY - mMinY;
//        float zrange = mMaxZ - mMinZ;
//        if (zrange != 0 && resetZ) {
//            float xyrange = (xrange + yrange) / 2;
//            float scalez = xyrange / zrange;
//
//            for (int i = 0; i < vert.length; i += 3) {
//                float z = vert[i + 2];
//                if (Float.isNaN(z) || Float.isInfinite(z)) {
//                    if (i > 3) {
//                        z = vert[i - 1];
//                    } else {
//                        z = vert[i + 5];
//                    }
//                }
//                vert[i + 2] = z * scalez * mZoomZ;
//            }
//            if (resetZ) {
//                mMinZ *= scalez;
//                mMaxZ *= scalez;
//            }
//        }
        count = 0;
        for (int iy = 0; iy < mSize; iy++) {
            for (int ix = 0; ix < mSize; ix++) {
                int p1 = (ix + iy * (mSize + 1));
                int p2 = (1 + ix + iy * (mSize + 1));
                int p3 = (ix + (iy + 1) * (mSize + 1));
                int p4 = (1 + ix + (iy + 1) * (mSize + 1));
                index[count++] = (short) p1;
                index[count++] = (short) p3;
                index[count++] = (short) p2;

                index[count++] = (short) p4;
                index[count++] = (short) p3;
                index[count++] = (short) p2;
            }
        }
    }


}
