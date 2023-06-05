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

package com.support.constraintlayout.extlib.graph3d;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This renders 3Dimensional Objects.
 */
public class Scene3D {
    private static final String TAG = "SurfaceGen";
    ViewMatrix mMatrix = new ViewMatrix();
    Matrix mInverse = new Matrix();
    Object3D mObject3D;
    ArrayList<Object3D> mPreObjects = new ArrayList();
    ArrayList<Object3D> mPostObjects = new ArrayList();
    float[] zBuff;
    int[] img;

    private float[] light = {0, -0.3f, 1}; // The direction of the light source
    public float[] mTransformedLight = {0, -1, -1}; // The direction of the light source

    int width, height;
    public float[] tmpVec = new float[3];
    int lineColor = 0xFF000000;
    private final float epslonX = 0.000005232f;
    private final float epslonY = 0.00000898f;
    private Function mFunction;
    private float mZoomZ = 1;
    int background;
    private boolean mLightMovesWithCamera = true;

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    class Box {
        float[][] m_box = {{1, 1, 1}, {2, 3, 2}};
        int[] m_x1 = {0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0};
        int[] m_y1 = {0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 1};
        int[] m_z1 = {0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1};
        int[] m_x2 = {0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1};
        int[] m_y2 = {0, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1};
        int[] m_z2 = {1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1};
        float[] m_point1 = new float[3];
        float[] m_point2 = new float[3];
        float[] m_draw1 = new float[3];
        float[] m_draw2 = new float[3];

        void drawLines(LineRender r) {
            for (int i = 0; i < 12; i++) {
                m_point1[0] = m_box[m_x1[i]][0];
                m_point1[1] = m_box[m_y1[i]][1];
                m_point1[2] = m_box[m_z1[i]][2];
                m_point2[0] = m_box[m_x2[i]][0];
                m_point2[1] = m_box[m_y2[i]][1];
                m_point2[2] = m_box[m_z2[i]][2];

                mInverse.mult3(m_point1, m_draw1);
                mInverse.mult3(m_point2, m_draw2);

                r.draw((int) m_draw1[0], (int) m_draw1[1], (int) m_draw2[0], (int) m_draw2[1]);
            }
        }
    }


    {
        VectorUtil.normalize(light);
    }

    public void transformTriangles() {
        transform();
    }

    public void transform() {
        Matrix m = mInverse;
        if (mLightMovesWithCamera) {
            m.mult3v(light, mTransformedLight);
            VectorUtil.normalize(mTransformedLight);
        } else {
            System.arraycopy(light, 0, mTransformedLight, 0, 3);
        }

        mObject3D.transform(m);
        for (Object3D obj : mPreObjects) {
            obj.transform(m);
        }
        for (Object3D obj : mPostObjects) {
            obj.transform(m);
        }
    }

    public double getScreenWidth() {
        return mMatrix.getScreenWidth();
    }

    public void setScreenWidth(double sw) {
        mMatrix.setScreenWidth(sw);
        mMatrix.calcMatrix();
        mMatrix.invers(mInverse);
        transform();
    }

    public void trackBallDown(float x, float y) {
        mMatrix.trackBallDown(x, y);
        mMatrix.invers(mInverse);
    }

    public void trackBallMove(float x, float y) {
        mMatrix.trackBallMove(x, y);
        mMatrix.invers(mInverse);
        transform();
    }

    public void trackBallUP(float x, float y) {
        mMatrix.trackBallUP(x, y);
        mMatrix.invers(mInverse);
    }

    public void update() {
        mMatrix.invers(mInverse);
        transform();
    }

    public void panDown(float x, float y) {
        mMatrix.panDown(x, y);
        mMatrix.invers(mInverse);
    }

    public void panMove(float x, float y) {
        mMatrix.panMove(x, y);
        mMatrix.invers(mInverse);
        transform();
    }

    public void panUP() {
        mMatrix.panUP();
    }

    public String getLookPoint() {
        return Arrays.toString(mMatrix.getLookPoint());
    }

    public void setScreenDim(int width, int height, int[] img, int background) {
        mMatrix.setScreenDim(width, height);
        setupBuffers(width, height, img, background);
        setUpMatrix(width, height);
        transform();
    }

    public float getZoom() {
        return mZoomZ;
    }

    public void setZoom(float zoom) {
        this.mZoomZ = zoom;
    }

    public void setUpMatrix(int width, int height) {
        setUpMatrix(width, height, false);
    }

    public void setUpMatrix(int width, int height, boolean resetOrientation) {
        double[] look_point = mObject3D.center();

        double diagonal = mObject3D.size() * mZoomZ;

        mMatrix.setLookPoint(look_point);
        if (resetOrientation) {
            double[] eye_point = {look_point[0] - diagonal, look_point[1] - diagonal, look_point[2] + diagonal};
            mMatrix.setEyePoint(eye_point);
            double[] up_vector = {0, 0, 1};
            mMatrix.setUpVector(up_vector);
        } else {
            mMatrix.fixUpPoint();
        }
        double screenWidth = diagonal * 2;
        mMatrix.setScreenWidth(screenWidth);
        mMatrix.setScreenDim(width, height);
        mMatrix.calcMatrix();
        mMatrix.invers(mInverse);
    }

    public boolean notSetUp() {
        return mInverse == null;
    }

    interface LineRender {
        void draw(int x1, int y1, int x2, int y2);
    }

    public void drawBox(LineRender g) {
    }

    public interface Function {
        float eval(float x, float y);
    }


    public void addPreObject(Object3D obj) {
        mPreObjects.add(obj);
    }

    public void setObject(Object3D obj) {
        mObject3D = obj;
    }

    public void addPostObject(Object3D obj) {
        mPostObjects.add(obj);
    }

    public void resetCamera() {
        setUpMatrix(width, height, true);
        transform();
    }

    private final static int min(int x1, int x2, int x3) {
        return (x1 > x2) ? ((x2 > x3) ? x3 : x2) : ((x1 > x3) ? x3 : x1);
    }

    private final static int max(int x1, int x2, int x3) {
        return (x1 < x2) ? ((x2 < x3) ? x3 : x2) : ((x1 < x3) ? x3 : x1);
    }

    public void setupBuffers(int w, int h, int[] img, int background) {
        width = w;
        height = h;
        this.background = background;
        zBuff = new float[w * h];
        this.img = img;
        Arrays.fill(zBuff, Float.MAX_VALUE);
        Arrays.fill(img, background);
    }

    void render(int type) {
        if (zBuff == null) {
            return;
        }
        Arrays.fill(zBuff, Float.MAX_VALUE);
        Arrays.fill(img, background);

        for (Object3D mPreObject : mPreObjects) {
            mPreObject.render(this, zBuff, img, width, height);
        }
        mObject3D.render(this, zBuff, img, width, height);
        for (Object3D mPreObject : mPostObjects) {
            mPreObject.render(this, zBuff, img, width, height);
        }
    }

    public static int hsvToRgb(float hue, float saturation, float value) {
        int h = (int) (hue * 6);
        float f = hue * 6 - h;
        int p = (int) (0.5f + 255 * value * (1 - saturation));
        int q = (int) (0.5f + 255 * value * (1 - f * saturation));
        int t = (int) (0.5f + 255 * value * (1 - (1 - f) * saturation));
        int v = (int) (0.5f + 255 * value);
        switch (h) {
            case 0:
                return 0XFF000000 | (v << 16) + (t << 8) + p;
            case 1:
                return 0XFF000000 | (q << 16) + (v << 8) + p;
            case 2:
                return 0XFF000000 | (p << 16) + (v << 8) + t;
            case 3:
                return 0XFF000000 | (p << 16) + (q << 8) + v;
            case 4:
                return 0XFF000000 | (t << 16) + (p << 8) + v;
            case 5:
                return 0XFF000000 | (v << 16) + (p << 8) + q;

        }
        return 0;
    }


    public static void drawline(float[] zbuff, int[] img, int color, int w, int h,
                                float fx1, float fy1, float fz1,
                                float fx2, float fy2, float fz2
    ) {
        float dx = fx2 - fx1, dy = fy2 - fy1, dz = fz2 - fz1;
        float steps = (float) Math.hypot(dx, Math.hypot(dy, dz));

        for (float t = 0; t < 1; t += 1 / steps) {
            float px = fx1 + t * dx;
            float py = fy1 + t * dy;
            float pz = fz1 + t * dz;
            int ipx = (int) px;
            int ipy = (int) py;
            if (ipx < 0 || ipx >= w || ipy < 0 || ipy >= h) {
                continue;
            }

            int point = ipx + w * ipy;
            if (zbuff[point] >= pz - 2) {
                img[point] = color;
            }

        }
    }

    public static boolean isBackface(
            float fx3, float fy3, float fz3,
            float fx2, float fy2, float fz2,
            float fx1, float fy1, float fz1) {

        return (((fx1 - fx2) * (fy3 - fy2) - (fy1 - fy2) * (fx3 - fx2)) < 0);

    }


    public static void triangle(float[] zbuff, int[] img, int color, int w, int h,
                                float fx3, float fy3, float fz3,
                                float fx2, float fy2, float fz2,
                                float fx1, float fy1, float fz1) {

        if (((fx1 - fx2) * (fy3 - fy2) - (fy1 - fy2) * (fx3 - fx2)) < 0) {
            float tmpx = fx1;
            float tmpy = fy1;
            float tmpz = fz1;
            fx1 = fx2;
            fy1 = fy2;
            fz1 = fz2;
            fx2 = tmpx;
            fy2 = tmpy;
            fz2 = tmpz;
        }
        // using maxmima
        // string(solve([x1*dx+y1*dy+zoff=z1,x2*dx+y2*dy+zoff=z2,x3*dx+y3*dy+zoff=z3],[dx,dy,zoff]));
        double d = (fx1 * (fy3 - fy2) - fx2 * fy3 + fx3 * fy2 + (fx2 - fx3)
                * fy1);

        if (d == 0) {
            return;
        }
        float dx = (float) (-(fy1 * (fz3 - fz2) - fy2 * fz3 + fy3 * fz2 + (fy2 - fy3)
                * fz1) / d);
        float dy = (float) ((fx1 * (fz3 - fz2) - fx2 * fz3 + fx3 * fz2 + (fx2 - fx3)
                * fz1) / d);
        float zoff = (float) ((fx1 * (fy3 * fz2 - fy2 * fz3) + fy1
                * (fx2 * fz3 - fx3 * fz2) + (fx3 * fy2 - fx2 * fy3) * fz1) / d);

        // 28.4 fixed-point coordinates

        int Y1 = (int) (16.0f * fy1 + .5f);
        int Y2 = (int) (16.0f * fy2 + .5f);
        int Y3 = (int) (16.0f * fy3 + .5f);

        int X1 = (int) (16.0f * fx1 + .5f);
        int X2 = (int) (16.0f * fx2 + .5f);
        int X3 = (int) (16.0f * fx3 + .5f);

        int DX12 = X1 - X2;
        int DX23 = X2 - X3;
        int DX31 = X3 - X1;

        int DY12 = Y1 - Y2;
        int DY23 = Y2 - Y3;
        int DY31 = Y3 - Y1;

        int FDX12 = DX12 << 4;
        int FDX23 = DX23 << 4;
        int FDX31 = DX31 << 4;

        int FDY12 = DY12 << 4;
        int FDY23 = DY23 << 4;
        int FDY31 = DY31 << 4;

        int minx = (min(X1, X2, X3) + 0xF) >> 4;
        int maxx = (max(X1, X2, X3) + 0xF) >> 4;
        int miny = (min(Y1, Y2, Y3) + 0xF) >> 4;
        int maxy = (max(Y1, Y2, Y3) + 0xF) >> 4;

        if (miny < 0) {
            miny = 0;
        }
        if (minx < 0) {
            minx = 0;
        }
        if (maxx > w) {
            maxx = w;
        }
        if (maxy > h) {
            maxy = h;
        }
        int off = miny * w;

        int C1 = DY12 * X1 - DX12 * Y1;
        int C2 = DY23 * X2 - DX23 * Y2;
        int C3 = DY31 * X3 - DX31 * Y3;

        if (DY12 < 0 || (DY12 == 0 && DX12 > 0)) {
            C1++;
        }
        if (DY23 < 0 || (DY23 == 0 && DX23 > 0)) {
            C2++;
        }
        if (DY31 < 0 || (DY31 == 0 && DX31 > 0)) {
            C3++;
        }
        int CY1 = C1 + DX12 * (miny << 4) - DY12 * (minx << 4);
        int CY2 = C2 + DX23 * (miny << 4) - DY23 * (minx << 4);
        int CY3 = C3 + DX31 * (miny << 4) - DY31 * (minx << 4);

        for (int y = miny; y < maxy; y++) {
            int CX1 = CY1;
            int CX2 = CY2;
            int CX3 = CY3;
            float p = zoff + dy * y;
            for (int x = minx; x < maxx; x++) {
                if (CX1 > 0 && CX2 > 0 && CX3 > 0) {
                    int point = x + off;
                    float zval = p + dx * x;
                    if (zbuff[point] > zval) {
                        zbuff[point] = zval;
                        img[point] = color;
                    }
                }
                CX1 -= FDY12;
                CX2 -= FDY23;
                CX3 -= FDY31;
            }
            CY1 += FDX12;
            CY2 += FDX23;
            CY3 += FDX31;
            off += w;
        }
    }

    public static void trianglePhong(float[] zbuff, int[] img,
                                float h3, float b3,
                                float h2, float b2,
                                float h1, float b1,
                                int w, int h,
                                float fx3, float fy3, float fz3,
                                float fx2, float fy2, float fz2,
                                float fx1, float fy1, float fz1) {

        if (((fx1 - fx2) * (fy3 - fy2) - (fy1 - fy2) * (fx3 - fx2)) < 0) {
            float tmpx = fx1;
            float tmpy = fy1;
            float tmpz = fz1;
            fx1 = fx2;
            fy1 = fy2;
            fz1 = fz2;
            fx2 = tmpx;
            fy2 = tmpy;
            fz2 = tmpz;
            float tmph = h1;
            float tmpb = b1;

            h1 = h2;
            b1 = b2;

            h2 = tmph;
            b2 = tmpb;

        }
        // using maxmima
        // string(solve([x1*dx+y1*dy+zoff=z1,x2*dx+y2*dy+zoff=z2,x3*dx+y3*dy+zoff=z3],[dx,dy,zoff]));
        double d = (fx1 * (fy3 - fy2) - fx2 * fy3 + fx3 * fy2 + (fx2 - fx3)
                * fy1);

        if (d == 0) {
            return;
        }
        float dx = (float) (-(fy1 * (fz3 - fz2) - fy2 * fz3 + fy3 * fz2 + (fy2 - fy3)
                * fz1) / d);
        float dy = (float) ((fx1 * (fz3 - fz2) - fx2 * fz3 + fx3 * fz2 + (fx2 - fx3)
                * fz1) / d);
        float zoff = (float) ((fx1 * (fy3 * fz2 - fy2 * fz3) + fy1
                * (fx2 * fz3 - fx3 * fz2) + (fx3 * fy2 - fx2 * fy3) * fz1) / d);

        float dhx = (float) (-(fy1 * (h3 - h2) - fy2 * h3 + fy3 * h2 + (fy2 - fy3)
                * h1) / d);
        float dhy = (float) ((fx1 * (h3 - h2) - fx2 * h3 + fx3 * h2 + (fx2 - fx3)
                * h1) / d);
        float hoff = (float) ((fx1 * (fy3 * h2 - fy2 * h3) + fy1
                * (fx2 * h3 - fx3 * h2) + (fx3 * fy2 - fx2 * fy3) * h1) / d);


        float dbx = (float) (-(fy1 * (b3 - b2) - fy2 * b3 + fy3 * b2 + (fy2 - fy3)
                * b1) / d);
        float dby = (float) ((fx1 * (b3 - b2) - fx2 * b3 + fx3 * b2 + (fx2 - fx3)
                * b1) / d);
        float boff = (float) ((fx1 * (fy3 * b2 - fy2 * b3) + fy1
                * (fx2 * b3 - fx3 * b2) + (fx3 * fy2 - fx2 * fy3) * b1) / d);

        // 28.4 fixed-point coordinates

        int Y1 = (int) (16.0f * fy1 + .5f);
        int Y2 = (int) (16.0f * fy2 + .5f);
        int Y3 = (int) (16.0f * fy3 + .5f);

        int X1 = (int) (16.0f * fx1 + .5f);
        int X2 = (int) (16.0f * fx2 + .5f);
        int X3 = (int) (16.0f * fx3 + .5f);

        int DX12 = X1 - X2;
        int DX23 = X2 - X3;
        int DX31 = X3 - X1;

        int DY12 = Y1 - Y2;
        int DY23 = Y2 - Y3;
        int DY31 = Y3 - Y1;

        int FDX12 = DX12 << 4;
        int FDX23 = DX23 << 4;
        int FDX31 = DX31 << 4;

        int FDY12 = DY12 << 4;
        int FDY23 = DY23 << 4;
        int FDY31 = DY31 << 4;

        int minx = (min(X1, X2, X3) + 0xF) >> 4;
        int maxx = (max(X1, X2, X3) + 0xF) >> 4;
        int miny = (min(Y1, Y2, Y3) + 0xF) >> 4;
        int maxy = (max(Y1, Y2, Y3) + 0xF) >> 4;

        if (miny < 0) {
            miny = 0;
        }
        if (minx < 0) {
            minx = 0;
        }
        if (maxx > w) {
            maxx = w;
        }
        if (maxy > h) {
            maxy = h;
        }
        int off = miny * w;

        int C1 = DY12 * X1 - DX12 * Y1;
        int C2 = DY23 * X2 - DX23 * Y2;
        int C3 = DY31 * X3 - DX31 * Y3;

        if (DY12 < 0 || (DY12 == 0 && DX12 > 0)) {
            C1++;
        }
        if (DY23 < 0 || (DY23 == 0 && DX23 > 0)) {
            C2++;
        }
        if (DY31 < 0 || (DY31 == 0 && DX31 > 0)) {
            C3++;
        }
        int CY1 = C1 + DX12 * (miny << 4) - DY12 * (minx << 4);
        int CY2 = C2 + DX23 * (miny << 4) - DY23 * (minx << 4);
        int CY3 = C3 + DX31 * (miny << 4) - DY31 * (minx << 4);

        for (int y = miny; y < maxy; y++) {
            int CX1 = CY1;
            int CX2 = CY2;
            int CX3 = CY3;
            float p = zoff + dy * y;
            float ph = hoff + dhy * y;
            float pb = boff + dby * y;
            for (int x = minx; x < maxx; x++) {
                if (CX1 > 0 && CX2 > 0 && CX3 > 0) {
                    int point = x + off;
                    float zval = p + dx * x;
                    float hue = ph + dhx * x;
                    float bright = pb + dbx * x;
                    if (zbuff[point] > zval) {
                        zbuff[point] = zval;
                        img[point] = Scene3D.hsvToRgb(hue, 0.8f, bright);;
                    }
                }
                CX1 -= FDY12;
                CX2 -= FDY23;
                CX3 -= FDY31;
            }
            CY1 += FDX12;
            CY2 += FDX23;
            CY3 += FDX31;
            off += w;
        }
    }


}