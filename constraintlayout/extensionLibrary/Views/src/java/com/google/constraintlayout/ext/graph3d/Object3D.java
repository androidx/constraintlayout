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

package android.support.constraintLayout.extlib.graph3d;

/**
 * This represents 3d Object in this system.
 */
public class Object3D {
    protected float[] vert;
    protected float[] normal;
 
    protected short[] index;
 
    protected float[] tVert; // the vertices transformed into screen space
    protected float mMinX, mMaxX, mMinY, mMaxY, mMinZ, mMaxZ; // bounds in x,y & z
    protected int mType = 4;

    float mAmbient = 0.3f;
    float mDefuse = 0.7f;
    public float mSaturation = 0.6f;

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        this.mType = type;
    }

    public void makeVert(int n) {
        vert = new float[n * 3];
        tVert = new float[n * 3];
        normal = new float[n * 3];
    }

    public void makeIndexes(int n) {
        index = new short[n * 3];
    }

    public void transform(Matrix m) {
        for (int i = 0; i < vert.length; i += 3) {
            m.mult3(vert, i, tVert, i);
        }
    }

    public void render(Scene3D s, float[] zbuff, int[] img, int width, int height) {
        switch (mType) {
            case 0:
                raster_height(s, zbuff, img, width, height);
                break;
            case 1:
                raster_outline(s, zbuff, img, width, height);
                break;
            case 2:
                raster_color(s, zbuff, img, width, height);
                break;
            case 3:
                raster_lines(s, zbuff, img, width, height);
                break;
            case 4:
                raster_phong(s, zbuff, img, width, height);
                break;

        }
    }


    private void raster_lines(Scene3D s, float[] zbuff, int[] img, int w, int h) {
        for (int i = 0; i < index.length; i += 3) {
            int p1 = index[i];
            int p2 = index[i + 1];
            int p3 = index[i + 2];

            float height = (vert[p1 + 2] + vert[p3 + 2] + vert[p2 + 2]) / 3;
            int val = (int) (255 * Math.abs(height));
            Scene3D.triangle(zbuff, img, 0x10001 * val + 0x100 * (255 - val), w, h, tVert[p1], tVert[p1 + 1],
                    tVert[p1 + 2], tVert[p2], tVert[p2 + 1],
                    tVert[p2 + 2], tVert[p3], tVert[p3 + 1],
                    tVert[p3 + 2]);

            Scene3D.drawline(zbuff, img, s.lineColor, w, h,
                    tVert[p1], tVert[p1 + 1], tVert[p1 + 2] - 0.01f,
                    tVert[p2], tVert[p2 + 1], tVert[p2 + 2] - 0.01f);
            Scene3D.drawline(zbuff, img, s.lineColor, w, h,
                    tVert[p1], tVert[p1 + 1], tVert[p1 + 2] - 0.01f,
                    tVert[p3], tVert[p3 + 1], tVert[p3 + 2] - 0.01f);
        }
    }

    void raster_height(Scene3D s, float[] zbuff, int[] img, int w, int h) {
        for (int i = 0; i < index.length; i += 3) {
            int p1 = index[i];
            int p2 = index[i + 1];
            int p3 = index[i + 2];
            float height = (vert[p1 + 2] + vert[p3 + 2] + vert[p2 + 2]) / 3;
            height = (height - mMinZ) / (mMaxZ - mMinZ);
            int col = Scene3D.hsvToRgb(height, Math.abs(2 * (height - 0.5f)), (float) Math.sqrt(height));
            Scene3D.triangle(zbuff, img, col, w, h, tVert[p1], tVert[p1 + 1],
                    tVert[p1 + 2], tVert[p2], tVert[p2 + 1],
                    tVert[p2 + 2], tVert[p3], tVert[p3 + 1],
                    tVert[p3 + 2]);
        }
    }

    // float mSpec = 0.2f;
    void raster_color(Scene3D s, float[] zbuff, int[] img, int w, int h) {
        for (int i = 0; i < index.length; i += 3) {
            int p1 = index[i];
            int p2 = index[i + 1];
            int p3 = index[i + 2];

            VectorUtil.triangleNormal(tVert, p1, p2, p3, s.tmpVec);
            float defuse = VectorUtil.dot(s.tmpVec, s.mTransformedLight);

            float height = (vert[p1 + 2] + vert[p3 + 2] + vert[p2 + 2]) / 3;
            height = (height - mMinZ) / (mMaxZ - mMinZ);
            float bright = Math.min(1, Math.max(0, mDefuse * defuse + mAmbient));
            float hue = (float) (height - Math.floor(height));
            float sat = 0.8f;
            int col = Scene3D.hsvToRgb(hue, sat, bright);
            Scene3D.triangle(zbuff, img, col, w, h, tVert[p1], tVert[p1 + 1],
                    tVert[p1 + 2], tVert[p2], tVert[p2 + 1],
                    tVert[p2 + 2], tVert[p3], tVert[p3 + 1],
                    tVert[p3 + 2]);
        }
    }

    private int color(float hue, float sat, float bright) {
        hue = hue(hue);
        bright = bright(bright);
        return Scene3D.hsvToRgb(hue, sat, bright);
    }

    private float hue(float hue) {
        return (float) (hue - Math.floor(hue));
    }

    private float bright(float bright) {
        return Math.min(1, Math.max(0, bright));
    }

    private float defuse(float[] normals, int off, float[] light) {
        // s.mMatrix.mult3v(normal,off,s.tmpVec);
        return Math.abs(VectorUtil.dot(normal, off, light));
    }

    void raster_phong(Scene3D s, float[] zbuff, int[] img, int w, int h) {
        for (int i = 0; i < index.length; i += 3) {
            int p1 = index[i];
            int p2 = index[i + 1];
            int p3 = index[i + 2];

            //    VectorUtil.triangleNormal(tVert, p1, p2, p3, s.tmpVec);


//            float defuse1 = VectorUtil.dot(normal, p1, s.mTransformedLight);
//            float defuse2 = VectorUtil.dot(normal, p2, s.mTransformedLight);
//            float defuse3 = VectorUtil.dot(normal, p3, s.mTransformedLight);
            float defuse1 = defuse(normal, p1, s.mTransformedLight);
            float defuse2 = defuse(normal, p2, s.mTransformedLight);
            float defuse3 = defuse(normal, p3, s.mTransformedLight);
            float col1_hue = hue((vert[p1 + 2] - mMinZ) / (mMaxZ - mMinZ));
            float col2_hue = hue((vert[p2 + 2] - mMinZ) / (mMaxZ - mMinZ));
            float col3_hue = hue((vert[p3 + 2] - mMinZ) / (mMaxZ - mMinZ));
            float col1_bright = bright(mDefuse * defuse1 + mAmbient);
            float col2_bright = bright(mDefuse * defuse2 + mAmbient);
            float col3_bright = bright(mDefuse * defuse3 + mAmbient);

            Scene3D.trianglePhong(zbuff, img,
                    col1_hue, col1_bright,
                    col2_hue, col2_bright,
                    col3_hue, col3_bright,
                    mSaturation,
                    w, h,
                    tVert[p1], tVert[p1 + 1], tVert[p1 + 2],
                    tVert[p2], tVert[p2 + 1], tVert[p2 + 2],
                    tVert[p3], tVert[p3 + 1], tVert[p3 + 2]);
        }
    }

    void raster_outline(Scene3D s, float[] zBuff, int[] img, int w, int h) {
        for (int i = 0; i < index.length; i += 3) {
            int p1 = index[i];
            int p2 = index[i + 1];
            int p3 = index[i + 2];

            Scene3D.triangle(zBuff, img, s.background, w, h,
                    tVert[p1], tVert[p1 + 1], tVert[p1 + 2],
                    tVert[p2], tVert[p2 + 1], tVert[p2 + 2],
                    tVert[p3], tVert[p3 + 1], tVert[p3 + 2]);

            Scene3D.drawline(zBuff, img, s.lineColor, w, h,
                    tVert[p1], tVert[p1 + 1], tVert[p1 + 2],
                    tVert[p2], tVert[p2 + 1], tVert[p2 + 2]);

            Scene3D.drawline(zBuff, img, s.lineColor, w, h,
                    tVert[p1], tVert[p1 + 1], tVert[p1 + 2],
                    tVert[p3], tVert[p3 + 1], tVert[p3 + 2]);
        }
    }

    public double[] center() {
        double[] look_point = {
                (mMinX + mMaxX) / 2, (mMinY + mMaxY) / 2, (mMinZ + mMaxZ) / 2
        };
        return look_point;
    }

    public float centerX() {
        return (mMaxX + mMinX) / 2;
    }

    public float centerY() {
        return (mMaxY + mMinY) / 2;
    }

    public float rangeX() {
        return (mMaxX - mMinX) / 2;
    }

    public float rangeY() {
        return (mMaxY - mMinY) / 2;
    }

    public double size() {

        return Math.hypot((mMaxX - mMinX), Math.hypot((mMaxY - mMinY), (mMaxZ - mMinZ))) / 2;
    }

}
