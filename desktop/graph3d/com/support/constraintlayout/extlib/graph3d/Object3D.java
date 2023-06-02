package com.support.constraintlayout.extlib.graph3d;

/**
 * This represents 3d Object in this system
 */
public class Object3D {
    protected float[] vert;
    protected int[] index;
    protected float[] tVert; // the vertices transformed into screen space
    protected float mMinX, mMaxX, mMinY, mMaxY, mMinZ, mMaxZ; // bounds in x,y & z
    protected int mType  = 2;

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        this.mType = type;
    }


    public void transform(Matrix m) {
        for (int i = 0; i < vert.length; i += 3) {
            m.mult3(vert, i, tVert, i);
        }
    }

    public void render(Scene3D s,  float[] zbuff, int[] img, int width, int height) {
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


    void raster_color(Scene3D s, float[] zbuff, int[] img, int w, int h) {
        for (int i = 0; i < index.length; i += 3) {
            int p1 = index[i];
            int p2 = index[i + 1];
            int p3 = index[i + 2];

            VectorUtil.triangleNormal(tVert, p1, p2, p3, s.tmpVec);
            float defuse = VectorUtil.dot(s.tmpVec, s.light);
            float height = (vert[p1 + 2] + vert[p3 + 2] + vert[p2 + 2]) / 3;
            height = (height - mMinZ) / (mMaxZ - mMinZ);
            float bright = Math.max(0, defuse);
            float hue = (float) Math.sqrt(height);
            float sat = Math.max(0.5f, height);
            int col = Scene3D.hsvToRgb(hue, sat, bright);
            Scene3D.triangle(zbuff, img, col, w, h, tVert[p1], tVert[p1 + 1],
                    tVert[p1 + 2], tVert[p2], tVert[p2 + 1],
                    tVert[p2 + 2], tVert[p3], tVert[p3 + 1],
                    tVert[p3 + 2]);
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
