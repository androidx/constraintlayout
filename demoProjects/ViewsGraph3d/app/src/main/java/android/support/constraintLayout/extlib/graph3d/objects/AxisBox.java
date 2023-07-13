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
package android.support.constraintLayout.extlib.graph3d.objects;

import android.support.constraintLayout.extlib.graph3d.Object3D;
import android.support.constraintLayout.extlib.graph3d.Scene3D;
import android.support.constraintLayout.extlib.graph3d.VectorUtil;


/**
 * Draws box along the axis
 */
public class AxisBox extends Object3D {
    int color = 0xFF1010FF;
    public AxisBox() {
   mType = 1;
    }

    public void setRange(float minX, float maxX, float minY, float maxY, float minZ, float maxZ) {
        mMinX = minX;
        mMaxX = maxX;
        mMinY = minY;
        mMaxY = maxY;
        mMinZ = minZ;
        mMaxZ = maxZ;
        buildBox();
    }

    void buildBox() {
        vert = new float[8 * 3]; // cube 8 corners
        tVert = new float[vert.length];
        for (int i = 0; i < 8; i++) {
            vert[i * 3] = ((i & 1) == 0) ? mMinX : mMaxX; // X
            vert[i * 3 + 1] = (((i >> 1) & 1) == 0) ? mMinY : mMaxY; // Y
            vert[i * 3 + 2] = (((i >> 2) & 1) == 0) ? mMinZ : mMaxZ; // Z
        }

 
        index = new short[6 * 2 * 3]; // 6 sides x 2 triangles x 3 points per triangle
        short []sides = {  // pattern of clockwise triangles around cube
                0, 2, 1, 3, 1, 2,
                0, 1, 4, 5, 4, 1,
                0, 4, 2, 6, 2, 4,
                7, 6, 5, 4, 5, 6,
                7, 3, 6, 2, 6, 3,
                7, 5, 3, 1, 3, 5
        };
        index = new short[sides.length];
        for (int i = 0; i < sides.length; i++) {
            index[i] = (short) (3*sides[i]);
        }
    }

    public void render_old(Scene3D s, float[] zbuff, int[] img, int w, int h) {
        for (int i = 0; i < index.length; i += 3) {
            int p1 = index[i];
            int p2 = index[i + 1];
            int p3 = index[i + 2];

            float height = (vert[p1 + 2] + vert[p3 + 2] + vert[p2 + 2]) / 3;
            int val = (int) (255 * Math.abs(height));


            Scene3D.drawline(zbuff, img, color, w, h,
                    tVert[p1], tVert[p1 + 1], tVert[p1 + 2] - 0.01f,
                    tVert[p2], tVert[p2 + 1], tVert[p2 + 2] - 0.01f);
//            Scene3D.drawline(zbuff, img,color, w, h,
//                    tVert[p1], tVert[p1 + 1], tVert[p1 + 2] - 0.01f,
//                    tVert[p3], tVert[p3 + 1], tVert[p3 + 2] - 0.01f);
//            Scene3D.drawline(zbuff, img,color, w, h,
//                    tVert[p2], tVert[p2 + 1], tVert[p2 + 2] - 0.01f,
//                    tVert[p3], tVert[p3 + 1], tVert[p3 + 2] - 0.01f);
        }
    }

    public void render(Scene3D s, float[] zbuff, int[] img, int w, int h) {
        raster_color(s, zbuff, img, w, h);
        for (int i = 0; i < index.length; i += 3) {
            int p1 = index[i];
            int p2 = index[i + 1];
            int p3 = index[i + 2];

            boolean front = Scene3D.isBackface(
                    tVert[p1], tVert[p1 + 1], tVert[p1 + 2],
                    tVert[p2], tVert[p2 + 1], tVert[p2 + 2],
                    tVert[p3], tVert[p3 + 1], tVert[p3 + 2]);
            if (front) {
                Scene3D.drawline(zbuff, img, color, w, h,
                        tVert[p1], tVert[p1 + 1], tVert[p1 + 2] - 0.01f,
                        tVert[p2], tVert[p2 + 1], tVert[p2 + 2] - 0.01f);
                Scene3D.drawline(zbuff, img, color, w, h,
                        tVert[p1], tVert[p1 + 1], tVert[p1 + 2] - 0.01f,
                        tVert[p3], tVert[p3 + 1], tVert[p3 + 2] - 0.01f);
                continue;
            }

            Scene3D.drawline(zbuff, img, color, w, h,
                    tVert[p1], tVert[p1 + 1], tVert[p1 + 2] - 0.01f,
                    tVert[p2], tVert[p2 + 1], tVert[p2 + 2] - 0.01f);
            drawTicks(zbuff, img, color, w, h,
                    tVert[p1], tVert[p1 + 1], tVert[p1 + 2] - 0.01f,
                    tVert[p2], tVert[p2 + 1], tVert[p2 + 2] - 0.01f,
                    tVert[p3]-tVert[p1], tVert[p3 + 1]-tVert[p1+1], tVert[p3 + 2] -tVert[p1+2]
            );
            Scene3D.drawline(zbuff, img, color, w, h,
                    tVert[p1], tVert[p1 + 1], tVert[p1 + 2] - 0.01f,
                    tVert[p3], tVert[p3 + 1], tVert[p3 + 2] - 0.01f);
            drawTicks(zbuff, img, color, w, h,
                    tVert[p1], tVert[p1 + 1], tVert[p1 + 2] - 0.01f,
                    tVert[p3], tVert[p3 + 1], tVert[p3 + 2] - 0.01f,
                    tVert[p2]-tVert[p1], tVert[p2 + 1]-tVert[p1+1], tVert[p2 + 2] -tVert[p1+2]);

        }
    }
    public static void drawTicks(float[] zbuff, int[] img, int color, int w, int h,
                                float p1x, float p1y, float p1z,
                                 float p2x, float p2y, float p2z,
                                 float nx, float ny, float nz
                                ) {

        float tx = nx/10;
        float ty = ny/10;
        float tz = nz/10;
        for (float f = 0; f <= 1; f+=0.1f) {
            float px = p1x + f*(p2x-p1x);
            float py = p1y + f*(p2y-p1y);
            float pz = p1z + f*(p2z-p1z);
            Scene3D.drawline(zbuff, img, color, w, h,
                    px,py,pz - 0.01f,
                    px+tx,py+ty,pz+tz - 0.01f);
        }
    }



    float[] screen = {0, 0, -1};

    void raster_color(Scene3D s, float[] zbuff, int[] img, int w, int h) {
        for (int i = 0; i < index.length; i += 3) {
            int p1 = index[i];
            int p2 = index[i + 1];
            int p3 = index[i + 2];
            boolean back = Scene3D.isBackface(
                    tVert[p1], tVert[p1 + 1], tVert[p1 + 2],
                    tVert[p2], tVert[p2 + 1], tVert[p2 + 2],
                    tVert[p3], tVert[p3 + 1], tVert[p3 + 2]);
            if (back) continue;

            VectorUtil.triangleNormal(tVert, p1, p3, p2, s.tmpVec);
            float ss = VectorUtil.dot(s.tmpVec, screen);
            float defuse = VectorUtil.dot(s.tmpVec, s.mTransformedLight);
            float ambient = 0.5f;

            float bright = Math.min(Math.max(0, defuse + ambient), 1);
            float hue = 0.4f;
            float sat = 0.1f;

            int col = Scene3D.hsvToRgb(hue, sat, bright);
            Scene3D.triangle(zbuff, img, col, w, h, tVert[p1], tVert[p1 + 1],
                    tVert[p1 + 2], tVert[p2], tVert[p2 + 1],
                    tVert[p2 + 2], tVert[p3], tVert[p3 + 1],
                    tVert[p3 + 2]);
        }
    }

}
