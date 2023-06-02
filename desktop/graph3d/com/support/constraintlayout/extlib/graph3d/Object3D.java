package com.support.constraintlayout.extlib.graph3d;

/**
 * This represents 3d Object in this system
 */
public class Object3D {
    protected float[] vert;
    protected int[] index;
    protected float[] tVert; // the vertices transformed into screen space
    float mMinX, mMaxX, mMinY, mMaxY, mMinZ, mMaxZ; // bounds in x,y & z

    public void transform(Matrix m) {
        for (int i = 0; i < vert.length; i += 3) {
            m.mult3(vert, i, tVert, i);
        }
    }

    public void render(SurfaceGen s, int type, float[] zbuff, int[] img, int width, int height) {
        System.out.println(type);
        switch (type) {
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


    private void raster_lines(SurfaceGen s, float[] zbuff, int[] img, int w, int h) {
        for (int i = 0; i < index.length; i += 3) {
            int p1 = index[i];
            int p2 = index[i + 1];
            int p3 = index[i + 2];

            float height = (vert[p1 + 2] + vert[p3 + 2] + vert[p2 + 2]) / 3;
            int val = (int) (255 * Math.abs(height));
            SurfaceGen.triangle(zbuff, img, 0x10001 * val + 0x100 * (255 - val), w, h, tVert[p1], tVert[p1 + 1],
                    tVert[p1 + 2], tVert[p2], tVert[p2 + 1],
                    tVert[p2 + 2], tVert[p3], tVert[p3 + 1],
                    tVert[p3 + 2]);

            SurfaceGen.drawline(zbuff, img, s.lineColor, w, h,
                    tVert[p1], tVert[p1 + 1], tVert[p1 + 2] - 0.01f,
                    tVert[p2], tVert[p2 + 1], tVert[p2 + 2] - 0.01f);
            SurfaceGen.drawline(zbuff, img, s.lineColor, w, h,
                    tVert[p1], tVert[p1 + 1], tVert[p1 + 2] - 0.01f,
                    tVert[p3], tVert[p3 + 1], tVert[p3 + 2] - 0.01f);
        }
    }

    void raster_height(SurfaceGen s, float[] zbuff, int[] img, int w, int h) {
        for (int i = 0; i < index.length; i += 3) {
            int p1 = index[i];
            int p2 = index[i + 1];
            int p3 = index[i + 2];
            float height = (vert[p1 + 2] + vert[p3 + 2] + vert[p2 + 2]) / 3;
            height = (height - mMinZ) / (mMaxZ - mMinZ);
            int col = SurfaceGen.hsvToRgb(height, Math.abs(2 * (height - 0.5f)), (float) Math.sqrt(height));
            SurfaceGen.triangle(zbuff, img, col, w, h, tVert[p1], tVert[p1 + 1],
                    tVert[p1 + 2], tVert[p2], tVert[p2 + 1],
                    tVert[p2 + 2], tVert[p3], tVert[p3 + 1],
                    tVert[p3 + 2]);
        }
    }


    void raster_color(SurfaceGen s, float[] zbuff, int[] img, int w, int h) {
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
            int col = SurfaceGen.hsvToRgb(hue, sat, bright);
            SurfaceGen.triangle(zbuff, img, col, w, h, tVert[p1], tVert[p1 + 1],
                    tVert[p1 + 2], tVert[p2], tVert[p2 + 1],
                    tVert[p2 + 2], tVert[p3], tVert[p3 + 1],
                    tVert[p3 + 2]);
        }
    }

    void raster_outline(SurfaceGen s, float[] zBuff, int[] img, int w, int h) {
        for (int i = 0; i < index.length; i += 3) {
            int p1 = index[i];
            int p2 = index[i + 1];
            int p3 = index[i + 2];

            SurfaceGen.triangle(zBuff, img, s.background, w, h,
                    tVert[p1], tVert[p1 + 1], tVert[p1 + 2],
                    tVert[p2], tVert[p2 + 1], tVert[p2 + 2],
                    tVert[p3], tVert[p3 + 1], tVert[p3 + 2]);

            SurfaceGen.drawline(zBuff, img, s.lineColor, w, h,
                    tVert[p1], tVert[p1 + 1], tVert[p1 + 2],
                    tVert[p2], tVert[p2 + 1], tVert[p2 + 2]);

            SurfaceGen.drawline(zBuff, img, s.lineColor, w, h,
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
        return  (mMaxY + mMinY) / 2;
    }
    public float rangeX() {
        return  (mMaxX - mMinX) / 2;
    }
    public float rangeY() {
        return   (mMaxY - mMinY) / 2;
    }
    public double size() {

        return Math.hypot((mMaxX - mMinX), Math.hypot((mMaxY - mMinY), (mMaxZ - mMinZ))) / 2;
    }

    static class Surface extends Object3D {
        final int SIZE = 100; // the number of point on the side total points = SIZE*SIZE
        private Function mFunction;
        private float mZoomZ = 1;

        public interface Function {
            float eval(float x, float y);
        }

        public Surface(boolean resetZ, Function func) {
            computeSurface(resetZ, func);
        }

        public void computeSurface(boolean resetZ, Function func) {
            int n = (SIZE + 1) * (SIZE + 1);
            vert = new float[n * 3];
            tVert = new float[n * 3];
            index = new int[SIZE * SIZE * 6];
            float min_x = mMinX;
            float max_x = mMaxX;
            float min_y = mMinY;
            float max_y = mMaxY;
            float min_z = Float.MAX_VALUE;
            float max_z = -Float.MAX_VALUE;

            mFunction = func;
            int count = 0;
            for (int iy = 0; iy <= SIZE; iy++) {
                float y = min_y + iy * (max_y - min_y) / (SIZE);
                for (int ix = 0; ix <= SIZE; ix++) {
                    float x = min_x + ix * (max_x - min_x) / (SIZE);
                    vert[count++] = x;
                    vert[count++] = y;
                    float z = func.eval(x, y);

                    if (Float.isNaN(z) || Float.isInfinite(z)) {
                        float epslonX = 0.000005232f;
                        float epslonY = 0.00000898f;
                        z = func.eval(x + epslonX, y + epslonY);
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
            for (int iy = 0; iy < SIZE; iy++) {
                for (int ix = 0; ix < SIZE; ix++) {
                    int p1 = 3 * (ix + iy * (SIZE + 1));
                    int p2 = 3 * (1 + ix + iy * (SIZE + 1));
                    int p3 = 3 * (ix + (iy + 1) * (SIZE + 1));
                    int p4 = 3 * (1 + ix + (iy + 1) * (SIZE + 1));
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
}
