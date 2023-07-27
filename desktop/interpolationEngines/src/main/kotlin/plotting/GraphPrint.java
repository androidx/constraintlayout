/*
 * Copyright (C) 2022 The Android Open Source Project
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

package plotting;


import curves.ArcSpline;
import curves.Cycles;
import curves.MonoSpline;
import utils.ArcCurveFit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.prefs.Preferences;

public class GraphPrint extends JPanel {
    public static String TITLE1 = "Velocity";
    private static String TITLE2 = "Velocity";
    private static final String PREFS_NODE_NAME = "FramePositionSaver";
    private static final String PREF_X = "x";
    private static final String PREF_Y = "y";
    private static final String PREF_WIDTH = "width";
    private static final String PREF_HEIGHT = "height";
    private String mTitle = "Velocity";
    Color AXIS_COLOR = new Color(0x804040);
    Color GRID_COLOR = new Color(0x19543B);
    Color TEXT1_COLOR = new Color(0x6E9DC4);
    Color PERIOD_GRAD_TOP = new Color(0x1A284C);
    Color BACK_COLOR = new Color(0x091515);

//    Color AXIS_COLOR = new Color(0x182142);
//    Color GRID_COLOR = new Color(0x8DCEB3);
//    Color TEXT1_COLOR = new Color(0x223544);
//    Color PERIOD_GRAD_TOP = new Color(0x1A284C);
//    Color BACK_COLOR = Color.WHITE;

    plotInfo plotInfo = new plotInfo();
    ArrayList<DrawItem> baseDraw = new ArrayList<>();
    ArrayList<PlotItem> plotDraw = new ArrayList<>();

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
        repaint();
    }

    public GraphPrint(String title) {
        mTitle = title;
        setBackground(BACK_COLOR);
        baseDraw.add(clear);
        baseDraw.add(mGrid);
        baseDraw.add(mAxis);
    }

    public double getGraphX(float x) {
        int draw_width = getWidth() - plotInfo.ins_left - plotInfo.ins_right;
        return plotInfo.minx + (plotInfo.maxx - plotInfo.minx) * (x - plotInfo.ins_left) / draw_width;
    }

    public double getGraphY(float y) {
        int draw_height = getHeight() - plotInfo.ins_top - plotInfo.ins_left;
        return plotInfo.miny + 1 - (plotInfo.maxy - plotInfo.miny) * (y - plotInfo.ins_top) / draw_height;
    }

    static class plotInfo {
        boolean draw_axis = true;
        boolean draw_grid = true;
        boolean simple_background = true;
        int mTextGap = 2;
        int ins_top = 30;
        int ins_left = 30;
        int ins_botom = 30;
        int ins_right = 30;
        float actual_miny;
        float actual_maxx;
        float actual_maxy;
        float actual_minx;
        private float last_minx;
        private float mTickY;
        private float mTickX;
        private float minx;
        private float last_maxx;
        private float maxx;
        private float maxy;
        private float miny;

        private float last_miny;
        private float last_maxy;
        private boolean mLock = false;

        float getX(float x, int w) {
            int draw_width = w - ins_left - ins_right;
            return draw_width * (x - minx)
                    / (maxx - minx) + ins_left;

        }

        float getY(float y, int h) {
            int draw_height = h - ins_top - ins_botom;

            return draw_height
                    * (1 - (y - miny) / (maxy - miny))
                    + ins_top;
        }

        void calcRangeTicks(int width, int height) {
            double dx = actual_maxx - actual_minx;
            double dy = actual_maxy - actual_miny;
            double border = 1.09345;
            if (Double.isInfinite(dx) || Double.isInfinite(dy)) {
                return;
            }
            if (Math.abs(last_minx - actual_minx)
                    + Math.abs(last_maxx - actual_maxx) > 0.1 * (actual_maxx - actual_minx)) {
                mTickX = (float) calcTick(width, dx);
                if (mTickX == 0.0f) {
                    mTickX = 1;
                }
                dx = mTickX * Math.ceil(border * dx / mTickX);
                double tx = (actual_minx + actual_maxx - dx) / 2;
                tx = mTickX * Math.floor(tx / mTickX);
                minx = (float) tx;
                tx = (actual_minx + actual_maxx + dx) / 2;
                tx = mTickX * Math.ceil(tx / mTickX);
                maxx = (float) tx;

                last_minx = actual_minx;
                last_maxx = actual_maxx;

            }
            if (Math.abs(last_miny - actual_miny)
                    + Math.abs(last_maxy - actual_maxy) > 0.1 * (actual_maxy - actual_miny)) {
                mTickY = (float) calcTick(height, dy);
                if (mTickY == 0.0f) {
                    mTickY = 1;
                }
                dy = mTickY * Math.ceil(border * dy / mTickY);
                double ty = (actual_miny + actual_maxy - dy) / 2;
                ty = mTickY * Math.floor(ty / mTickY);
                miny = (float) ty;
                ty = (actual_miny + actual_maxy + dy) / 2;
                ty = mTickY * Math.ceil(ty / mTickY);
                maxy = (float) ty;

                last_miny = actual_miny;
                last_maxy = actual_maxy;
            }
        }

        private static double frac(double x) {
            return x - Math.floor(x);
        }

        static public double calcTick(int scr, double range) {

            int aprox_x_ticks = scr / 100;
            int type = 1;
            double best = Math.log10(range / (aprox_x_ticks));
            double n = Math.log10(range / (aprox_x_ticks * 2));
            if (frac(n) < frac(best)) {
                best = n;
                type = 2;
            }
            n = Math.log10(range / (aprox_x_ticks * 5));
            if (frac(n) < frac(best)) {
                best = n;
                type = 5;
            }
            return type * Math.pow(10, Math.floor(best));
        }

        public void setRange(float minx, float maxx, float miny, float maxy) {
            actual_maxx = maxx;
            actual_maxy = maxy;
            actual_minx = minx;
            actual_miny = miny;
        }

        public void calcRange(ArrayList<PlotItem> plotDraw) {
            resetRange();


            if (mLock) {
                minx = 0;
                maxy = 100;
                miny = -40;
                maxx = 200;
            } else {
                for (PlotItem plotItem : plotDraw) {
                    measure(plotItem.getX(), plotItem.getY());
                }
            }
            setRange(minx, maxx, miny, maxy);

        }


        public void resetRange() {
            minx = Float.MAX_VALUE;
            miny = Float.MAX_VALUE;
            maxx = -Float.MAX_VALUE;
            maxy = -Float.MAX_VALUE;
        }

        void measure(float[] xPoints, float[] yPoints) {
            if (xPoints == null | yPoints == null) {
                return;
            }
            for (int i = 0; i < xPoints.length; i++) {
                float x = xPoints[i];
                float y = yPoints[i];
                minx = Math.min(minx, x);
                miny = Math.min(miny, y);
                maxx = Math.max(maxx, x);
                maxy = Math.max(maxy, y);
            }
//            maxy = 300;
//            miny = 0;
        }
    }


    interface DrawItem {
        public void paint(Graphics2D g, int w, int h);
    }

    DrawItem mAxis = new DrawItem() {
        Color drawing = new Color(0xAAAAAA);

        @Override
        public void paint(Graphics2D g, int w, int h) {
            if (!plotInfo.draw_axis) {
                return;
            }
            g.setColor(drawing);
            g.setColor(AXIS_COLOR);
            int x = (int) plotInfo.getX(0, w);
            int y = (int) plotInfo.getY(0, h);
            g.drawLine(x, plotInfo.ins_top, x, h - plotInfo.ins_botom);
            g.drawLine(plotInfo.ins_left, y, w - plotInfo.ins_right, y);
        }

    };

    DrawItem mGrid = new DrawItem() {


        private float mPeriodMultiplier = 1;


        DecimalFormat df = new DecimalFormat("###.#");

        @Override
        public void paint(Graphics2D g, int w, int h) {
            if (!plotInfo.draw_grid) {
                return;
            }
            g.setColor(GRID_COLOR);
            int draw_width = w - plotInfo.ins_left - plotInfo.ins_right;
            float e = 0.0001f * (plotInfo.maxx - plotInfo.minx);
            FontMetrics fm = g.getFontMetrics();
            int ascent = fm.getAscent();
            if (plotInfo.mTickX > 0) {
                for (float i = plotInfo.minx + plotInfo.mTickX; i <= plotInfo.maxx - plotInfo.mTickX + e; i += plotInfo.mTickX) {
                    int ix = (int) (draw_width * (i - plotInfo.minx) / (plotInfo.maxx - plotInfo.minx) + plotInfo.ins_left);
                    String str = df.format(i);
                    int sw = fm.stringWidth(str) / 2;
                    g.setColor(TEXT1_COLOR);

                    g.drawString(str, ix - sw, h - plotInfo.ins_botom + ascent + plotInfo.mTextGap);
                }
            }
            g.setColor(GRID_COLOR);

            for (float i = plotInfo.minx; i <= plotInfo.maxx + e; i += plotInfo.mTickX) {
                int ix = (int) (draw_width * (i - plotInfo.minx) / (plotInfo.maxx - plotInfo.minx) + plotInfo.ins_left);
                g.drawLine(ix, plotInfo.ins_top, ix, h - plotInfo.ins_botom);
            }
            int draw_height = h - plotInfo.ins_top - plotInfo.ins_left;
            e = 0.0001f * (plotInfo.maxy - plotInfo.miny);
            int hightoff = -fm.getHeight() / 2 + ascent;


            for (float i = plotInfo.miny; i <= plotInfo.maxy + e; i += plotInfo.mTickY) {
                int iy = (int) (draw_height * (1 - (i - plotInfo.miny) / (plotInfo.maxy - plotInfo.miny)) + plotInfo.ins_top);
                g.setColor(GRID_COLOR);
                g.drawLine(plotInfo.ins_left, iy, w - plotInfo.ins_right, iy);
                String str = df.format(i);
                int sw = fm.stringWidth(str);
                g.setColor(TEXT1_COLOR);

                g.drawString(str, plotInfo.ins_left - sw - plotInfo.mTextGap, iy + hightoff);
                str = df.format(i / mPeriodMultiplier);
                sw = fm.stringWidth(str);
                g.setColor(PERIOD_GRAD_TOP);
                g.drawString(str, w - sw - plotInfo.ins_right + 20, iy + hightoff);
            }
            String str;
            int sw;
            g.setColor(TEXT1_COLOR);
            str = mTitle;
            sw = fm.stringWidth(str);
            g.drawString(str, plotInfo.ins_left - sw / 4, 20);
            str = TITLE2;
//            sw = fm.stringWidth(str);
//            g.setColor(PERIOD_GRAD_TOP);
//            g.drawString(str, w - sw / 2 - plotInfo.ins_right, 20);
        }

    };
    DrawItem clear = new DrawItem() {
        @Override
        public void paint(Graphics2D g, int w, int h) {
            g.setColor(getBackground());
            g.fillRect(0, 0, w, h);
        }
    };

    @Override
    protected void paintComponent(Graphics g) {
        int w = getWidth();
        int h = getHeight();
        plotInfo.calcRangeTicks(w, h);
        g.setColor(getBackground());
        g.fillRect(0, 0, w, h);
        Graphics2D g2d = (Graphics2D) g;
        for (DrawItem drawItem : baseDraw) {
            drawItem.paint(g2d, w, h);
        }
        for (DrawItem drawItem : plotDraw) {
            drawItem.paint(g2d, w, h);
        }

    }

    public void addBasicPlot(float[] x, float[] y, Color c, String title) {
        plotDraw.add(new BasicPlot(x, y, c, title, 0));
        plotInfo.calcRange(plotDraw);
        repaint();
    }

    public interface Function {
        double f(double x);
    }

    public void setFunction(double minx, double maxx, Color c, Function f) {
        float[] x = new float[100];
        float[] y = new float[x.length];
        double last = 0;
        for (int i = 0; i < x.length; i++) {
            double in = minx + maxx * (i / (double) (x.length - 1));
            x[i] = (float) in;
            double value = (float) f.f(in);
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                value = (float) f.f(in + 0.000001);
                if (Double.isNaN(value) || Double.isInfinite(value)) {
                    value = last;
                }
            }
            y[i] = (float) value;
            last = value;
        }
        plotDraw.clear();
        plotDraw.add(new CoolPlot2(x, y, c));
        plotInfo.calcRange(plotDraw);
        repaint();
    }

    public void addData(Color c, float[] x, float[] y) {
        plotDraw.add(new CoolPlot(x, y, c));
        plotInfo.calcRange(plotDraw);
        repaint();
    }


    public PlotItem addFunction(String title, double minx, double maxx, Color c, Function f) {
        float[] x = new float[512];
        float[] y = new float[512];
        double last = 0;
        for (int i = 0; i < x.length; i++) {
            double in = minx + maxx * (i / (double) (x.length - 1));
            x[i] = (float) in;
            double value = (float) f.f(in);
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                value = (float) f.f(in + 0.000001);
                if (Double.isNaN(value) || Double.isInfinite(value)) {
                    value = last;
                }
            }
            y[i] = (float) value;
            last = value;
        }
        PlotItem ret;
        plotDraw.add(ret = new CoolPlot(x, y, c));
        plotInfo.calcRange(plotDraw);
        repaint();
        return ret;
    }

    public void removeFunction(PlotItem plot) {
        plotDraw.remove(plot);
    }

    public PlotItem addFunction2(String title, double minx, double maxx, Color c, Function f) {
        return addFunction2(128, title, minx, maxx, c, f);
    }

    public PlotItem addFunction2(int steps, String title, double minx, double maxx, Color c, Function f) {
        float[] x = new float[steps];
        float[] y = new float[x.length];
        double last = 0;
        for (int i = 0; i < x.length; i++) {
            double in = minx + maxx * (i / (double) (x.length - 1));
            x[i] = (float) in;
            double value = (float) f.f(in);
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                value = (float) f.f(in + 0.000001);
                if (Double.isNaN(value) || Double.isInfinite(value)) {
                    value = last;
                }
            }
            y[i] = (float) value;
            last = value;
        }
        PlotItem ret;

        plotDraw.add(ret = new BasicPlot(x, y, c, title, 0));
        plotInfo.calcRange(plotDraw);
        repaint();
        return ret;

    }

    public PlotItem addFunction2d(String title, double mint, double maxt, Color c, Function fx, Function fy) {
        float[] x = new float[128];
        float[] y = new float[x.length];
        double lastX = 0, lastY = 0;
        for (int i = 0; i < x.length; i++) {
            double t = mint + maxt * (i / (double) (x.length - 1));

            double valueX = (float) fx.f(t);
            double valueY = (float) fy.f(t);
            if (Double.isNaN(valueX) || Double.isInfinite(valueX)) {
                valueX = (float) fx.f(t + 0.000001);
                if (Double.isNaN(valueX) || Double.isInfinite(valueX)) {
                    valueX = lastX;
                }
            }

            if (Double.isNaN(valueY) || Double.isInfinite(valueY)) {
                valueY = (float) fy.f(t + 0.000001);
                if (Double.isNaN(valueY) || Double.isInfinite(valueY)) {
                    valueY = lastY;
                }
            }
            x[i] = (float) valueX;
            y[i] = (float) valueY;

            lastY = valueY;
            lastX = valueX;
        }
        PlotItem ret;
        plotDraw.add(ret = new BasicPlot(x, y, c, title, 0.2f));
        plotInfo.calcRange(plotDraw);
        repaint();
        return ret;
    }

    interface PlotItem extends DrawItem {
        float[] getX();

        float[] getY();
    }

    class BasicPlot implements PlotItem {
        private float old_min_draw_y;
        private int[] tmpX = new int[0];
        private int[] tmpY = new int[0];
        int[] xp = new int[0];
        int[] yp = new int[0];
        Color color = Color.BLACK;
        String title = null;
        Stroke stroke = new BasicStroke(2f);
        float[] xPoints;
        float[] yPoints;
        float titlePos;

        public float[] getX() {
            return xPoints;
        }

        public float[] getY() {
            return yPoints;
        }

        BasicPlot(float[] x, float[] y, Color c, String title, float titlePos) {
            this.title = title;
            color = c;
            xPoints = x;
            yPoints = y;
            this.titlePos = titlePos;
        }

        @Override
        public void paint(Graphics2D g, int w, int h) {
            paintLines(g, w, h);
        }

        public void paintLines(Graphics2D g, int w, int h) {

            if (xPoints.length == 0) {
                return;
            }
            int draw_width = w - plotInfo.ins_left - plotInfo.ins_right;
            int draw_height = h - plotInfo.ins_top - plotInfo.ins_botom;


            if (xp.length < xPoints.length * 2) {
                xp = new int[xPoints.length * 2];
                yp = new int[xPoints.length * 2];
                tmpX = new int[xPoints.length * 2 + 2];
                tmpY = new int[xPoints.length * 2 + 2];
            }

            for (int k = 0; k < xPoints.length; k++) {
                if (xPoints == null || yPoints == null) {
                    continue;
                }
                for (int i = 0; i < xPoints.length; i++) {
                    float x = draw_width * (xPoints[i] - plotInfo.minx)
                            / (plotInfo.maxx - plotInfo.minx) + plotInfo.ins_left;
                    float y = draw_height
                            * (1 - (yPoints[i] - plotInfo.miny) / (plotInfo.maxy - plotInfo.miny))
                            + plotInfo.ins_top;

                    xp[i] = (int) x;
                    yp[i] = (int) y;
                }
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int tp = ((int) (titlePos * xp.length)) % xp.length;

                g.setColor(color);
                g.setStroke(stroke);
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.drawPolyline(xp, yp, xPoints.length);
                if (title != null) {
                    g.setColor(Color.white);
                    g.drawString(title, xp[tp] - 10, yp[tp]);
                }

            }
        }
    }

    class CoolPlot implements PlotItem {
        private float old_min_draw_y;
        private int[] tmpX = new int[0];
        private int[] tmpY = new int[0];
        int[] xp = new int[0];
        int[] yp = new int[0];
        Color color = Color.BLACK;
        Stroke stroke = new BasicStroke(2f);
        float[] xPoints;
        float[] yPoints;

        public float[] getX() {
            return xPoints;
        }

        public float[] getY() {
            return yPoints;
        }

        CoolPlot(float[] x, float[] y, Color c) {
            color = c;
            xPoints = x;
            yPoints = y;
        }

        @Override
        public void paint(Graphics2D g, int w, int h) {

            if (xPoints.length == 0) {
                return;
            }
            int draw_width = w - plotInfo.ins_left - plotInfo.ins_right;
            int draw_height = h - plotInfo.ins_top - plotInfo.ins_botom;


            if (xp.length < xPoints.length * 2) {
                xp = new int[xPoints.length * 2];
                yp = new int[xPoints.length * 2];
                tmpX = new int[xPoints.length * 2 + 2];
                tmpY = new int[xPoints.length * 2 + 2];
            }

            for (int k = 0; k < xPoints.length; k++) {
                if (xPoints == null || yPoints == null) {
                    continue;
                }
                for (int i = 0; i < xPoints.length; i++) {
                    float x = draw_width * (xPoints[i] - plotInfo.minx)
                            / (plotInfo.maxx - plotInfo.minx) + plotInfo.ins_left;
                    float y = draw_height
                            * (1 - (yPoints[i] - plotInfo.miny) / (plotInfo.maxy - plotInfo.miny))
                            + plotInfo.ins_top;

                    xp[i] = (int) x;
                    yp[i] = (int) y;
                    tmpX[i] = (int) x;
                    tmpY[i] = (int) y;
                }
                {
                    float x = draw_width * (xPoints[xPoints.length - 1] - plotInfo.minx)
                            / (plotInfo.maxx - plotInfo.minx) + plotInfo.ins_left;
                    float y = draw_height
                            * (1 - (0 - plotInfo.miny) / (plotInfo.maxy - plotInfo.miny))
                            + plotInfo.ins_top;
                    int i = xPoints.length;
                    tmpX[i] = (int) x;
                    tmpY[i] = (int) y;
                    i++;
                    x = draw_width * (xPoints[0] - plotInfo.minx)
                            / (plotInfo.maxx - plotInfo.minx) + plotInfo.ins_left;
                    y = draw_height
                            * (1 - (0 - plotInfo.miny) / (plotInfo.maxy - plotInfo.miny))
                            + plotInfo.ins_top;
                    tmpX[i] = (int) x;
                    tmpY[i] = (int) y;
                }

                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                float y0 = draw_height
                        * (1 - (0 - plotInfo.miny) / (plotInfo.maxy - plotInfo.miny))
                        + plotInfo.ins_top;
                float y1 = draw_height
                        * (1 - (plotInfo.maxy - plotInfo.miny) / (plotInfo.maxy - plotInfo.miny))
                        + plotInfo.ins_top;
//                g.setPaint(new GradientPaint(0, y0, new Color(0x000000,true), 0, y1,
//                        new Color(color.getRGB(),true), true));


                Point2D start = new Point2D.Float(0, y0);
                Point2D end = new Point2D.Float(0, y1);
                float[] dist = {0.0f, 1.0f};
                Color[] colors = {new Color(0xFFFFFF, true), new Color(color.getRGB(), true)};
                LinearGradientPaint p =
                        new LinearGradientPaint(start, end, dist, colors, MultipleGradientPaint.CycleMethod.REFLECT);
                g.setPaint(p);
                g.setStroke(stroke);
                g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.fillPolygon(tmpX, tmpY, xPoints.length + 2);
                g.setPaint(color);
                g.drawPolyline(xp, yp, xPoints.length);
            }
        }
    }
//////////////////////////////////////////////////////////////////////////////////////////////////

    static void calcTexture_old(int[] data, int w, int h) {
        int queEnd = 0;
        int v = 0;
        int[] que = new int[w * 100];
        for (int i = 0; i < data.length; i++) {
            if (0 != data[i]) {
                que[queEnd] = i;
                queEnd++;
                v = data[i];
            }
        }
        int[] neighboursX = {-1, +1, 0, 0};
        int[] neighboursY = {0, 0, +1, -1};
        int[] neighbours = {-1, +1, +w, -w};
        System.out.println(" > " + queEnd + "   " + v);
        int qStart = 0;
        System.out.println(" qend = " + queEnd);
        while (queEnd - qStart > 0) {
            // pop
            int point = que[(qStart) % que.length];
            qStart++;

            int px = point % w;
            int py = point / w;

            if (data[point] == 0) {
                short minDistX = 244, minDistY = 244;
                int minDistSq = Integer.MAX_VALUE;
                for (int i = 0; i < neighbours.length; i++) {
                    int npx = px + neighboursX[i];
                    int npy = py + neighboursY[i];
                    if (npx >= 0 && npy >= 0 && npy < h && npx < w) {

                        int aside = data[point + neighbours[i]];
                        if (aside == v) {
                            minDistX = (short) neighboursX[i];
                            minDistY = (short) neighboursY[i];

                            break;
                        } else {
                            short distX = 0, distY = 0;

                            distX |= (0xFFFF & aside);
                            ;
                            distY |= (0xFFFF & (aside >> 16));
                            distX += neighboursX[i];
                            distY += neighboursY[i];
                            int sq = distX * (int) distX + distY * (int) distY;
                            if (sq < minDistSq) {
                                minDistSq = sq;
                                minDistX = distX;
                                minDistY = distY;
                            }
                        }

                    }
                }
                data[point] = ((int) minDistY) << 16 | minDistX;
            }


            for (int i = 0; i < neighbours.length; i++) {
                int npx = px + neighboursX[i];
                int npy = py + neighboursY[i];
                if (npx >= 0 && npy >= 0 && npy < h && npx < w) {
                    int np = point + neighbours[i];
                    if (data[np] == 0) {
                        //push
                        que[queEnd % que.length] = np;
                        queEnd++;


                    }
                }
            }

        }
        System.out.println("    qend end = " + queEnd);
        System.out.println(" data.length = " + data.length);
        for (int i = 0; i < data.length; i++) {
            if (v != data[i] && data[i] != 0) {
                int val = data[i];
                short distX = (short) (0xFFFF & val);
                short distY = (short) (0xFFFF & (val >> 16));
                int d = distY * (int) distY + distX * (int) distX;
                if (d != 0) {
                    data[i] = 0x1010101 * Math.max(255 - d, 0);
                }
                data[i] = 0;
            }
        }
    }//////////////////////////////////////////////////////////////////////////////////////////////////

    static void calcTexture(int[] data, int w, int h, float y0) {
        int queEnd = 0;
        int v = 0;
        int[] que = new int[w * 100];
        for (int i = 0; i < data.length; i++) {
            if (0 != data[i]) {
                que[queEnd] = i;
                queEnd++;
                v = data[i];
            }
        }
        int[] neighboursX = {-1, +1, 0, 0, -1, +1, -1, +1,};
        int[] neighboursY = {0, 0, +1, -1, -1, +1, +1, -1};
        int[] neighbours = {-1, +1, +w, -w, -w - 1, w + 1, w - 1, 1 - w};
        System.out.println(" > " + queEnd + "   v=" + v);
        int qStart = 0;
        System.out.println(" qend = " + queEnd);
        while (queEnd - qStart > 0) {
            // pop
            int point = que[(qStart) % que.length];
            qStart++;

            int px = point % w;
            int py = point / w;

            int dist = data[point];
            if (dist > 1024) {
                break;
            }
            for (int i = 0; i < neighbours.length; i++) {
                int npx = px + neighboursX[i];
                int npy = py + neighboursY[i];

                if (npx >= 0 && npy >= 0 && npy < h && npx < w) {
                    int np = point + neighbours[i];
                    int d = i > 3 ? 7 : 5;
                    int newV = (dist == v) ? d : dist + d;
                    int newp = data[np];
                    if (newp == 0 || ((newp != -1) && newp > newV)) {
                        //push
                        que[queEnd % que.length] = np;
                        queEnd++;

                        data[np] = newV;

                    }
                }
            }

        }
        float min = Float.MAX_VALUE, max = -Float.MAX_VALUE;
        float min2 = Float.MAX_VALUE, max2 = -Float.MAX_VALUE;
        System.out.println("    qend end = " + queEnd);
        System.out.println(" data.length = " + data.length);
        for (int i = 0; i < data.length; i++) {

            float val = 50 / (50f + data[i]);
            min = Math.min(min, val);
            max = Math.max(max, val);
            float y = (Math.abs(i / w - y0)) / (h / 2);
            min2 = Math.min(min2, y);
            max2 = Math.max(max2, y);
            y = (float) (200 * Math.pow(y, .25) * val);
            data[i] = (0xFF << 24) | (0x10001 * Math.min(200, Math.max((int) y, 0)));


        }
        System.out.println("min = " + min + " max = " + max);
        System.out.println("min2 = " + min2 + " max2 = " + max2);
    }
//////////////////////////////////////////////////////////////////////////////////////////////////

    class CoolPlot2 implements PlotItem {
        private float old_min_draw_y;
        private int[] tmpX = new int[0];
        private int[] tmpY = new int[0];
        int[] xp = new int[0];
        int[] yp = new int[0];
        Color color = Color.BLACK;
        Stroke stroke = new BasicStroke(2f);
        float[] xPoints;
        float[] yPoints;
        BufferedImage image, tmpImg;
        int[] data;


        public float[] getX() {
            return xPoints;
        }

        public float[] getY() {
            return yPoints;
        }

        CoolPlot2(float[] x, float[] y, Color c) {
            color = c;
            xPoints = x;
            yPoints = y;
        }

        void setup() {
            int w = getWidth();
            int h = getHeight();

            if (w == 0 || h == 0) {
                return;
            }
            if (image != null && image.getWidth() == w && image.getHeight() == h) {
                return;
            }
            image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            data = ((DataBufferInt) (image.getRaster().getDataBuffer())).getData();
            Arrays.fill(data, 0);
            Graphics2D g = image.createGraphics();
            g.setColor(Color.WHITE);
            paintLines(g, w, h, false, false);
            int draw_height = h - plotInfo.ins_top - plotInfo.ins_botom;

            float y0 = draw_height
                    * (1 - (0 - plotInfo.miny) / (plotInfo.maxy - plotInfo.miny))
                    + plotInfo.ins_top;
            calcTexture(data, w, h, y0);

            tmpImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            paintMask(tmpImg.createGraphics(), w, h);
            int[] tmpdata = ((DataBufferInt) (tmpImg.getRaster().getDataBuffer())).getData();

            for (int i = 0; i < data.length; i++) {
                int v = ((tmpdata[i] >> 24) & 0xFF);
                if (v > 0) {
                    v = Math.min((data[i] & 0xFF) * 4, 255) << 24;
                } else {
                    v = 0;
                }
                data[i] = (data[i] & 0xFFFFFF) | (v);

            }
            paintLines(g, w, h, true, false);

        }

        @Override
        public void paint(Graphics2D g, int w, int h) {
            setup();
            g.drawImage(image, 0, 0, null);
            // paintLines(g, w, h,true,false);

        }

        public void paintMask(Graphics2D g, int w, int h) {

            if (xPoints.length == 0) {
                return;
            }
            int draw_width = w - plotInfo.ins_left - plotInfo.ins_right;
            int draw_height = h - plotInfo.ins_top - plotInfo.ins_botom;


            if (xp.length < xPoints.length * 2) {
                xp = new int[xPoints.length * 2];
                yp = new int[xPoints.length * 2];
                tmpX = new int[xPoints.length * 2 + 2];
                tmpY = new int[xPoints.length * 2 + 2];
            }

            for (int k = 0; k < xPoints.length; k++) {
                if (xPoints == null || yPoints == null) {
                    continue;
                }
                for (int i = 0; i < xPoints.length; i++) {
                    float x = draw_width * (xPoints[i] - plotInfo.minx)
                            / (plotInfo.maxx - plotInfo.minx) + plotInfo.ins_left;
                    float y = draw_height
                            * (1 - (yPoints[i] - plotInfo.miny) / (plotInfo.maxy - plotInfo.miny))
                            + plotInfo.ins_top;

                    xp[i] = (int) x;
                    yp[i] = (int) y;
                    tmpX[i] = (int) x;
                    tmpY[i] = (int) y;
                }
                {
                    float x = draw_width * (xPoints[xPoints.length - 1] - plotInfo.minx)
                            / (plotInfo.maxx - plotInfo.minx) + plotInfo.ins_left;
                    float y = draw_height
                            * (1 - (0 - plotInfo.miny) / (plotInfo.maxy - plotInfo.miny))
                            + plotInfo.ins_top;
                    int i = xPoints.length;
                    tmpX[i] = (int) x;
                    tmpY[i] = (int) y;
                    i++;
                    x = draw_width * (xPoints[0] - plotInfo.minx)
                            / (plotInfo.maxx - plotInfo.minx) + plotInfo.ins_left;
                    y = draw_height
                            * (1 - (0 - plotInfo.miny) / (plotInfo.maxy - plotInfo.miny))
                            + plotInfo.ins_top;
                    tmpX[i] = (int) x;
                    tmpY[i] = (int) y;
                }

                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int a = 0x0;
                int b = 0x00FFFFFF;
                int c = 0xFF000000;
                int d = 0xFFFFFFFF;
                g.setColor(Color.WHITE);

                float y0 = draw_height
                        * (1 - (0 - plotInfo.miny) / (plotInfo.maxy - plotInfo.miny))
                        + plotInfo.ins_top;
                float y1 = draw_height
                        * (1 - (plotInfo.maxy - plotInfo.miny) / (plotInfo.maxy - plotInfo.miny))
                        + plotInfo.ins_top;
                // g.setPaint(new TexturePaint(image, new Rectangle2D.Float(0, 0, w, h)));

                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.fillPolygon(tmpX, tmpY, xPoints.length + 2);

                g.setStroke(stroke);


                g.drawPolyline(xp, yp, xPoints.length);
            }
        }

        public void paintLines(Graphics2D g, int w, int h, boolean antialias, boolean fill) {

            if (xPoints.length == 0) {
                return;
            }
            int draw_width = w - plotInfo.ins_left - plotInfo.ins_right;
            int draw_height = h - plotInfo.ins_top - plotInfo.ins_botom;


            if (xp.length < xPoints.length * 2) {
                xp = new int[xPoints.length * 2];
                yp = new int[xPoints.length * 2];
                tmpX = new int[xPoints.length * 2 + 2];
                tmpY = new int[xPoints.length * 2 + 2];
            }

            for (int k = 0; k < xPoints.length; k++) {
                if (xPoints == null || yPoints == null) {
                    continue;
                }
                for (int i = 0; i < xPoints.length; i++) {
                    float x = draw_width * (xPoints[i] - plotInfo.minx)
                            / (plotInfo.maxx - plotInfo.minx) + plotInfo.ins_left;
                    float y = draw_height
                            * (1 - (yPoints[i] - plotInfo.miny) / (plotInfo.maxy - plotInfo.miny))
                            + plotInfo.ins_top;

                    xp[i] = (int) x;
                    yp[i] = (int) y;
                    tmpX[i] = (int) x;
                    tmpY[i] = (int) y;
                }
                {
                    float x = draw_width * (xPoints[xPoints.length - 1] - plotInfo.minx)
                            / (plotInfo.maxx - plotInfo.minx) + plotInfo.ins_left;
                    float y = draw_height
                            * (1 - (0 - plotInfo.miny) / (plotInfo.maxy - plotInfo.miny))
                            + plotInfo.ins_top;
                    int i = xPoints.length;
                    tmpX[i] = (int) x;
                    tmpY[i] = (int) y;
                    i++;
                    x = draw_width * (xPoints[0] - plotInfo.minx)
                            / (plotInfo.maxx - plotInfo.minx) + plotInfo.ins_left;
                    y = draw_height
                            * (1 - (0 - plotInfo.miny) / (plotInfo.maxy - plotInfo.miny))
                            + plotInfo.ins_top;
                    tmpX[i] = (int) x;
                    tmpY[i] = (int) y;
                }
                if (antialias) {
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    if (fill) {
                        g.setComposite(AlphaComposite.SrcOver);

                        float y0 = draw_height
                                * (1 - (0 - plotInfo.miny) / (plotInfo.maxy - plotInfo.miny))
                                + plotInfo.ins_top;
                        float y1 = draw_height
                                * (1 - (plotInfo.maxy - plotInfo.miny) / (plotInfo.maxy - plotInfo.miny))
                                + plotInfo.ins_top;
                        g.setPaint(new TexturePaint(image, new Rectangle2D.Float(0, 0, w, h)));
                        g.setColor(new Color(0, true));
                        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g.fillPolygon(tmpX, tmpY, xPoints.length + 2);
                    }
                    g.setStroke(stroke);
                    g.setPaint(color);
                }

                g.drawPolyline(xp, yp, xPoints.length);
            }
        }
    }


    public static GraphPrint setupFrame(JFrame frame, String title) {
        GraphPrint p = new GraphPrint(title);
        frame.setContentPane(p);
        return p;
    }

    static GraphPrint plot(double minx, double maxx, Color c, Function f) {
        JFrame frame;
        GraphPrint p = setupFrame(frame = new JFrame("Graph"), "velocity");
        p.addFunction(null, minx, maxx, c, f);
        frame.setVisible(true);
        return p;
    }

    static GraphPrint plot(double minx, double maxx, Function... f) {
        JFrame frame;
        GraphPrint p = setupFrame(frame = new JFrame("Graph"), "velocity");
        ColorGen colorGen = new ColorGen();
        for (int i = 0; i < f.length; i++) {
            Function function = f[i];
            p.addFunction(null, minx, maxx, colorGen.getColor(), function);
        }
        frame.setVisible(true);
        return p;
    }

    static class ColorGen {
        double sx, sy, count = 0;

        Color getColor() {
            double angle;
            float hue;
            if (count < 1) {
                hue = (float) Math.random();
                angle = hue * Math.PI * 2;
            } else {
                angle = Math.toRadians(180) + Math.atan2(sy, sx);
                hue = (float) (angle / (Math.PI * 2));
            }
            sx += Math.cos(angle);
            sy += Math.sin(angle);
            count++;
            return Color.getHSBColor(hue, 0.9f, 0.8f);

        }
    }


    public static JFrame smartFrame(String name) {
        JFrame frame = new JFrame(name);
        String fName = name.replace(' ', '_');
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setBounds(100, 10, 500, 500);
        restoreFramePosition(frame, fName);
        frame.addComponentListener(new ComponentAdapter() {

            public void componentMoved(ComponentEvent e) {
                saveFramePosition(frame, fName);
            }

            @Override
            public void componentResized(ComponentEvent e) {
                saveFramePosition(frame, fName);
            }
        });
        return frame;
    }

    public static void restoreFramePosition(JFrame frame, String name) {
        Preferences prefs = Preferences.userRoot().node(name);
        int x = prefs.getInt(PREF_X, 100);
        int y = prefs.getInt(PREF_Y, 100);
        int width = prefs.getInt(PREF_WIDTH, 500);
        int height = prefs.getInt(PREF_HEIGHT, 500);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if (x + width > screenSize.width) {
            x = screenSize.width - width;
        }
        if (y + height > screenSize.height) {
            y = screenSize.height - height;
        }
        frame.setBounds(x, y, width, height);

    }


    public static void saveFramePosition(JFrame frame, String name) {
        Preferences prefs = Preferences.userRoot().node(name);
        prefs.putInt(PREF_X, frame.getX());
        prefs.putInt(PREF_Y, frame.getY());
        prefs.putInt(PREF_WIDTH, frame.getWidth());
        prefs.putInt(PREF_HEIGHT, frame.getHeight());
    }

    public static void main(String[] arg) {
        JFrame frame = smartFrame("Graph");
        JPanel base = new JPanel(new BorderLayout());
        JPanel ctl = new JPanel();
        frame.setContentPane(base);
        base.add(ctl, BorderLayout.SOUTH);
        GraphPrint graph = new GraphPrint("graph");
        base.add(graph);

        Runnable[] cleanup = new Runnable[1];
        JButton b;
        cleanup[0] = displayMonoSpline1(graph);
        b = new JButton(new AbstractAction("Mono Spline") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cleanup[0] != null) {
                    cleanup[0].run();
                    cleanup[0] = null;
                }

                cleanup[0] = displayMonoSpline1(graph);
            }
        });
        ctl.add(b);
        b = new JButton(new AbstractAction("Arc mode") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cleanup[0] != null) {
                    cleanup[0].run();
                    cleanup[0] = null;
                }
                cleanup[0] = displayArc1(graph);
            }
        });
        ctl.add(b);
        b = new JButton(new AbstractAction("custom cycle mode") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cleanup[0] != null) {
                    cleanup[0].run();
                    cleanup[0] = null;
                }
                cleanup[0] = displayCycle1(graph);
            }
        });
        ctl.add(b);
        String[] typeNames = {
                "SIN_WAVE",
                "SQUARE_WAVE",
                "TRIANGLE_WAVE",
                "SAW_WAVE",
                "REVERSE_SAW_WAVE",
                "COS_WAVE",
                "BOUNCE",
        };
        for (int i = 0; i < typeNames.length; i++) {
            int type = i;
            b = new JButton(new AbstractAction(typeNames[i]) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (cleanup[0] != null) {
                        cleanup[0].run();
                        cleanup[0] = null;
                    }
                    cleanup[0] = displayCycle(graph,type);
                }
            });
            ctl.add(b);
        }


//        graphMonoSpline();
        frame.setVisible(true);
    }

    public static Runnable displayMonoSpline1(GraphPrint graph) {
        graph.setTitle("Monotonic Spline");
        float[][] points = {
                {0, 0},
                {1, 1},
                {1, 1},
                {2, 2},
                {2, 2},
                {3, 3},
        };
        float[] time = {
                0, 1, 2, 3, 4, 5
        };

        MonoSpline monoSpline = new MonoSpline(time, Arrays.asList(points));
        Color c = new Color(0xDE68D3);
        PlotItem plot1, plot2;
        plot1 = graph.addFunction2("mono slope", time[0], time[time.length - 1], c, new Function() {
            @Override
            public double f(double x) {
                return monoSpline.getSlope((float) x, 0);
            }
        });
        Color d = new Color(0x0004FF);
        plot2 = graph.addFunction2("mono", time[0], time[time.length - 1], d, new Function() {
            @Override
            public double f(double x) {
                return monoSpline.getPos((float) x, 0);
            }
        });

        return () -> {
            graph.removeFunction(plot1);
            graph.removeFunction(plot2);
        };
    }

    public static Runnable displayArc1(GraphPrint graph) {
        graph.setTitle("Monotonic Spline");
        float[][] points = {
                {0, 0},
                {1, 1},
                {1, 1},
                {2, 2},
                {2, 2},
                {5, 5},
        };
        float[] time = {
                0, 1, 2, 3, 4, 5
        };
        int[] mode = {
                ArcCurveFit.ARC_BELOW,
                ArcCurveFit.ARC_ABOVE,
                ArcCurveFit.ARC_ABOVE,
                ArcCurveFit.ARC_ABOVE,
                ArcCurveFit.ARC_ABOVE,

        };
        ArcSpline arcSpline = new ArcSpline(mode, time, Arrays.asList(points));
        PlotItem p1, p2, p3;

        p1 = graph.addFunction2("arc dx/dt", time[0], time[time.length - 1], new Color(0x2C4F2F),
                x -> arcSpline.getSlope((float) x, 0));
        p2 = graph.addFunction2("arc dy/dt", time[0], time[time.length - 1], new Color(0x4F362E),
                x -> arcSpline.getSlope((float) x, 1));

        p3 = graph.addFunction2d("arc", time[0], time[time.length - 1], new Color(0xECDE44),
                t -> arcSpline.getPos((float) t, 0), t -> arcSpline.getPos((float) t, 1));
        return () -> {
            graph.removeFunction(p1);
            graph.removeFunction(p2);
            graph.removeFunction(p3);
        };
    }

    public static Runnable displayCycle(GraphPrint graph, int type) {

        String[] typeNames = {
                "SIN_WAVE",
                "SQUARE_WAVE",
                "TRIANGLE_WAVE",
                "SAW_WAVE",
                "REVERSE_SAW_WAVE",
                "COS_WAVE",
                "BOUNCE",
        };
        graph.setTitle(typeNames[type]);

        Cycles cycles = new Cycles();
        cycles.setType(type, null);
        cycles.addPoint(0, 1);
        cycles.addPoint(0.25f, 1);
        cycles.addPoint(0.5f, 1);
        cycles.addPoint(0.55f, 6);
        cycles.addPoint(0.7f, 0);
        cycles.addPoint(1f, 0);
        cycles.normalize();
        PlotItem p1, p3;

        p1 = graph.addFunction2("cycle_slope", 0, 1, new Color(0x2C4F2F),
                t -> cycles.getSlope((float) t, 0, 0));


        p3 = graph.addFunction2(512, "cycle", 0, 1, new Color(0xECDE44),
                t -> 100 * cycles.getValue((float) t, 0));
        return () -> {
            graph.removeFunction(p1);

            graph.removeFunction(p3);
        };
    }

    public static Runnable displayCycle1(GraphPrint graph) {
        graph.setTitle("Cycle Spline");

        Cycles cycles = new Cycles();
        cycles.setType(Cycles.CUSTOM, new float[]{0, 0, 0.2f, 0, 0, -0.2f, 1, -0.3f, 0, 0, 0.3f, 0, 0});
        cycles.addPoint(0, 1);
        cycles.addPoint(0.25f, 1);
        cycles.addPoint(0.5f, 1);
        cycles.addPoint(0.55f, 6);
        cycles.addPoint(0.7f, 0);
        cycles.addPoint(1f, 0);
        cycles.normalize();
        PlotItem p1, p2, p3;

        p1 = graph.addFunction2("cycle_slope", 0, 1, new Color(0x2C4F2F),
                t -> cycles.getSlope((float) t, 0, 0));


        p3 = graph.addFunction2(512, "cycle", 0, 1, new Color(0xECDE44),
                t -> 100 * cycles.getValue((float) t, 0));
        return () -> {
            graph.removeFunction(p1);

            graph.removeFunction(p3);
        };
    }

}
