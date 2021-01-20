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

package androidx.constraintlayout.validation;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

public class BoxPlotView extends JPanel {

    private ArrayList<Main.Layout> mLayouts = new ArrayList<>();
    private ArrayList<BoxPlotAnalysis> mBoxPlots = new ArrayList<>();
    private long mMin;
    private long mMax;

    class BoxPlotAnalysis {

        private final boolean isReference;
        private final boolean isOptimized;
        long count;
        long minimum;
        long maximum;
        long Q1;
        long median;
        long Q3;
        int type;

        public BoxPlotAnalysis(int type, ArrayList<Main.Layout> layouts, int j) {
            this.type = type;
            this.isReference = j == 0;
            this.isOptimized = j == 2;
            count = layouts.size();
            minimum = Long.MAX_VALUE;
            maximum = 0;
            long[] values = new long[(int) count];
            int i = 0;
            count = 0;
            for (Main.Layout layout : layouts) {
                if (type == 0) {
                    if (layout.m_m.optimizedDuration == 0) {
                        break;
                    }
                    values[i] = layout.m_m.referenceDuration;
                    if (j == 1) {
                        values[i] = layout.m_m.duration;
                    } else if (j == 2) {
                        values[i] = layout.m_m.optimizedDuration;
                    }
                } else if (type == 1) {
                    if (layout.m_w.optimizedDuration == 0) {
                        break;
                    }
                    values[i] = layout.m_w.referenceDuration;
                    if (j == 1) {
                        values[i] = layout.m_w.duration;
                    } else if (j == 2) {
                        values[i] = layout.m_w.optimizedDuration;
                    }
                } else if (type == 2) {
                    if (layout.w_m.optimizedDuration == 0) {
                        break;
                    }
                    values[i] = layout.w_m.referenceDuration;
                    if (j == 1) {
                       values[i] = layout.w_m.duration;
                    } else if (j == 2) {
                        values[i] = layout.w_m.optimizedDuration;
                    }
                } else if (type == 3) {
                    if (layout.w_w.optimizedDuration == 0) {
                        break;
                    }
                    values[i] = layout.w_w.referenceDuration;
                    if (j == 1) {
                        values[i] = layout.w_w.duration;
                    } else if (j == 2) {
                        values[i] = layout.w_w.optimizedDuration;
                    }
                }
                i++;
            }
            count = i;

            long[] sortedValues = new long[(int) count];
            for (i = 0; i < count; i++) {
                minimum = Math.min(minimum, values[i]);
                maximum = Math.max(maximum, values[i]);
                sortedValues[i] = values[i];
            }

            if (count >= 4) {
                Arrays.sort(sortedValues);
                Q1 = sortedValues[(int) (count / 4)];
                median = sortedValues[(int) (count / 2)];
                Q3 = sortedValues[(int) (count / 2 + count / 4)];
            }
        }

        public void print() {
            if (type == 0) {
                System.out.println("Analysis on MxM");
            } else if (type == 1) {
                System.out.println("Analysis on MxW");
            } else if (type == 2) {
                System.out.println("Analysis on WxM");
            } else if (type == 3) {
                System.out.println("Analysis on WxW");
            }
            System.out.println("For " + count + " layouts:");
            System.out.println(" - minimum: " + minimum);
            System.out.println(" - maximum: " + maximum);
            System.out.println(" - Q1: " + Q1);
            System.out.println(" - median (Q2): " + median);
            System.out.println(" - Q3: " + Q3);
        }

        public void paint(Graphics2D g, int x, int y, int w, int h) {
            float scale = (float) h / (float) mMax;
            g.setColor(Color.BLACK);
            int by1 = h - (int) (y + minimum * scale);
            int by2 = h - (int) (y + maximum * scale);

            g.drawLine(x, by1, x + w, by1);
            g.drawLine(x, by2, x + w, by2);

            int q1 = h - (int) (y + Q1 * scale);
            int q2 = h - (int) (y + median * scale);
            int q3 = h - (int) (y + Q3 * scale);
            int bh = (int) ((Q3 - Q1) * scale);
            g.drawLine(x + w/2, by1, x + w/2, q1);
            g.drawLine(x + w/2, q3, x + w/2, by2);
            if (isReference) {
                g.setColor(new Color(115, 118, 255));
            } else if (isOptimized) {
                g.setColor(new Color(197, 255, 153));
            } else {
                g.setColor(new Color(255, 88, 119));
            }
            g.fillRect(x, q3, w, bh);
            g.setColor(Color.BLACK);
            g.drawRect(x, q3, w, bh);
            g.drawLine(x, q2, x + w, q2);
        }
    }

    public void setLayoutMeasures(ArrayList<Main.Layout> layouts) {
        mLayouts.clear();
        mLayouts.addAll(layouts);
        mBoxPlots.clear();
        long min = Long.MAX_VALUE;
        long max = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                BoxPlotAnalysis analysis = new BoxPlotAnalysis(i, layouts, j);
                if (j == 0) { // only look at the reference data (j == 0) to ease comparisons
                    min = Math.min(analysis.minimum, min);
                    max = Math.max(analysis.Q3, max); // get the top quartile instead of max, that's more useful.
                }
                mBoxPlots.add(analysis);
            }
        }
        mMin = min;
        mMax = (long) (max * 1.3); // add a bit of margin...
        repaint();
    }

    @Override
    public void paint(Graphics gc) {
        int w = getWidth();
        int h = getHeight();

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

        Color alternateBackground1 = new Color(224, 233, 243);
        Color alternateBackground2 = new Color(233, 242, 252);
        for (int i = 0; i < 4; i++) {
            if (i % 2 == 0) {
                g.setColor(alternateBackground1);
            } else {
                g.setColor(alternateBackground2);
            }
            int x = i * w / 4;
            g.fillRect(x, 0, w/4, h);
            String text = "";
            if (i == 0) {
                text = "MxM";
            } else if (i == 1) {
                text = "MxW";
            } else if (i == 2) {
                text = "WxM";
            } else if (i == 3) {
                text = "WxW";
            }
            g.setColor(Color.BLACK);
            g.drawString(text, x + 8, getHeight() - 8);
        }
        int textHeight = 10;
        int margin = 10;
        int count = mBoxPlots.size();
        int boxWidth = (w - (count + 1) * margin) / count;
        int x = margin;

        for (BoxPlotAnalysis boxPlot : mBoxPlots) {
            boxPlot.paint(g, x, margin, boxWidth, h - 2*margin - textHeight);
            x += boxWidth + margin;
        }
    }

}
