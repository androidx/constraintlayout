/*
 * Copyright (C) 2019 The Android Open Source Project
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

package androidx.constraintlayout.validation;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

/**
 * Simple graph view
 */
public class GraphView extends JPanel {
    private ArrayList<Main.Layout> mLayouts = new ArrayList<>();
    private int selectionIndex = -1;
    private boolean mShowNumberOfWidgets;

    int margin = 50;
    float scaleX = 1;
    float scaleY = 1;
    int width = 1;
    int height = 1;
    private boolean mUseLog10 = false;

    public void setLayoutMeasures(ArrayList<Main.Layout> layouts) {
        this.mLayouts.clear();
        this.mLayouts.addAll(layouts);
        repaint();
    }

    public void setSelection(int n) {
        selectionIndex = n;
        repaint();
    }

    public void setShowNumberOfWidgets(boolean value) {
        mShowNumberOfWidgets = value;
        repaint();
    }

    public void useLog10(boolean selected) {
        mUseLog10 = selected;
        repaint();
    }

    @Override
    public void paint(Graphics gc) {
        int w = getWidth();
        int h = getHeight();

        width = w - 2*margin;
        height = h - 2*margin;

        Graphics2D g = (Graphics2D) gc;
        g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

        g.setColor(new Color(255, 255, 255));
        g.fillRect(0, 0, w, h);

        paintLayout(w, h, g, mLayouts);
    }

    private void paintLayout(int w, int h, Graphics2D g, ArrayList<Main.Layout> layouts) {
        if (layouts == null) {
            return;
        }

        int n = layouts.size();
        if (n == 0) {
            return;
        }

        int textLegendX = 50;
        int textLegendY = h - 20;
        g.setColor(Color.BLUE);
        g.drawLine(textLegendX, textLegendY - 3, textLegendX + 30, textLegendY - 3);
        g.setColor(Color.BLACK);
        g.drawString("Baseline performances", textLegendX + 40, textLegendY);
        g.setColor(Color.RED);
        g.drawLine(textLegendX + 200, textLegendY - 3, textLegendX + 230, textLegendY - 3);
        g.setColor(Color.BLACK);
        g.drawString("Current performances", textLegendX + 240, textLegendY);

        long max = 0;
        long maxAxis2 = 0;
        for (int i = 0; i < layouts.size(); i++) {
            Main.Layout layout = layouts.get(i);
            max = Math.max(max, layout.m_m.duration);
            max = Math.max(max, layout.m_m.referenceDuration);
            maxAxis2 = Math.max(maxAxis2, layout.m_m.numWidgets);
        }

        int nLog = (int) Math.log10(max);
        int logRange = (int) Math.pow(10, nLog);
        int increment = nLog > 2 ? (int) (Math.pow(10, nLog) / 2f) : 10;
        while (logRange < max) {
            logRange += increment;
        }
        max = logRange;

        float intermediateStep = (max / 10.0f);
        float stepAxis = intermediateStep;

        g.drawString("ms", 20, 20);

        float barWidth = width / (float) (n -1);
        float scaleHeight = height / (float) max;
        if (mUseLog10) {
            scaleHeight = height / (float) Math.log10(max);
        }
        float axis2Height = max / (float) maxAxis2;

        scaleY = scaleHeight;
        scaleX = barWidth;

        // draw axis

        float axisLabels = 0;
        int lastStringY = 0;
        while (max > axisLabels) {
            String value = "" + ((int) (axisLabels / 1E3)) / 1000f;
            FontMetrics metrics = g.getFontMetrics(g.getFont());
            int stringHeight = metrics.getHeight();
            int stringWidth = metrics.stringWidth(value);
            if (fY(axisLabels) < lastStringY || lastStringY == 0) {
                g.setColor(Color.BLACK);
                g.drawString(value, margin - stringWidth - 5, fY(axisLabels));
                lastStringY = fY(axisLabels) - stringHeight;
            }
            g.setColor(Color.LIGHT_GRAY);
            drawLine(g, 0, axisLabels, n, axisLabels);
            axisLabels += stepAxis;
        }

        // draw lines
        int prex = 0;
        int prey = 0;
        int preyOpt = 0;
        int preyRef = 0;
        float preAxis2Y = 0;
        float axis2Y = 0;
        for (int i = 0; i < layouts.size(); i++) {
            Main.Layout layout = layouts.get(i);
            int numWidgets = layout.m_m.numWidgets;
            int barHeight = (int) (layout.m_m.duration);
            int barHeightOptimized = (int) (layout.m_m.optimizedDuration);
            int yRef = (int) (layout.m_m.referenceDuration);
            axis2Y = (numWidgets * axis2Height); // just draw num widgets scaled up in Y
            if (i > 0) {
                if (selectionIndex == i) {
                    g.setColor(Color.LIGHT_GRAY);
                    fillRect(g, prex, 0, 1, (int) max);
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(Color.RED);
                }
                drawLine(g, prex, prey, i, barHeight);
                g.setColor(Color.GREEN);
                drawLine(g, prex, preyOpt, i, barHeightOptimized);
                if (selectionIndex == i) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(Color.BLUE);
                }
                drawLine(g, prex, preyRef, i, yRef);
                if (mShowNumberOfWidgets) {
                    g.setColor(Color.lightGray);
                    drawLine(g, prex, preAxis2Y, i, axis2Y);
                }
            }
            prex = i;
            prey = barHeight;
            preyOpt = barHeightOptimized;
            preyRef = yRef;
            preAxis2Y = axis2Y;
        }

        // draw frame
        g.setColor(Color.BLACK);
        drawRect(g,0, 0, n, (int) max);
    }

    private void fillRect(Graphics2D g, int x, int y, int w, int h) {
        int fH = 0;
        if (mUseLog10) {
            fH = (int) (scaleY * Math.log10(h));
        } else {
            fH = (int) (scaleY * h);
        }
        g.fillRect(fX(x), fY(y) - fH, (int) (scaleX * w), fH);
    }

    private void drawRect(Graphics2D g, int x, int y, int w, int h) {
        int fH = 0;
        if (mUseLog10) {
            fH = (int) (scaleY * Math.log10(h));
        } else {
            fH = (int) (scaleY * h);
        }
        g.drawRect(fX(x), fY(y) - fH, (int) (scaleX * w), fH);
    }

    private void drawLine(Graphics2D g, int x1, float y1, int x2, float y2) {
        g.drawLine(fX(x1), fY(y1), fX(x2), fY(y2));
    }

    private int fH(float h) {
        return (int) (- scaleY * h) * height;
    }

    private int fX(float x) {
        return (int) (margin + (scaleX * x));
    }

    private int fY(float y) {
        if (mUseLog10) {
            if (y == 0) {
                return (int) (height + margin);
            }
            return (int) (height + margin - scaleY * Math.log10(y));
        } else {
            return (int) (height + margin - scaleY * y);
        }
    }

}
