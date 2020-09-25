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

package android.constraintlayout.validation;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

public class Display extends JPanel {

    private JSONObject mLayout;
    private JSONObject mReferenceLayout;
    private float mScale = 0.3f;
    private JSONObject mSelectedView;
    private Image mImage;
    private boolean mShowImage = true;
    private boolean mShowReferenceBounds = true;
    private boolean mShowCurrentBounds = true;
    private String mLayoutName = "None";
    private Main.LayoutType mLayoutType = Main.LayoutType.MATCH_MATCH;

    @Override
    public void paint(Graphics gc) {
        int margin = 20;
        int w = getWidth() - 2 * margin;
        int h = getHeight() - 2 * margin;

        Graphics2D g = (Graphics2D) gc;
        g.setColor(Color.RED);
        g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

        if (mLayout == null) {
            return;
        }

        g.setColor(new Color(255, 255, 255));
        g.fillRect(0, 0, getWidth(), getHeight());

        JSONObject bounds = mLayout.getJSONObject("bounds");
        int l = bounds.getInt("left");
        int t = bounds.getInt("top");
        int r = bounds.getInt("right");
        int b = bounds.getInt("bottom");
        int layoutWidth = r - l;
        int layoutHeight = b - t;
        float scaleWidth = w / (float) layoutWidth;
        float scaleHeight = h / (float) layoutHeight;
        mScale = Math.min(scaleWidth, scaleHeight);
        int lw = (int) (mScale * layoutWidth);
        int lh = (int) (mScale * layoutHeight);

        int tx = margin + (w - lw) / 2;
        int ty = margin + (h - lh) / 2 - 4;
        g.setColor(Color.BLACK);
        g.drawString(mLayoutName + " : " + mLayoutType.name(), tx, ty);

        g.translate(margin + (w - lw) / 2, margin + (h - lh) / 2);
        g.scale(mScale, mScale);

        if (mImage != null && mShowImage) {
            int shrinkFactor = 1;
            g.scale(shrinkFactor, shrinkFactor);
            g.drawImage(mImage, 0, 0, null);
            g.scale(1f / shrinkFactor, 1f / shrinkFactor);
        }
        if (mShowCurrentBounds) {
            drawRoot(g, Color.RED, mLayout);
        }
        if (mReferenceLayout != null && mShowReferenceBounds) {
            drawRoot(g, Color.GREEN, mReferenceLayout);
        }

        g.scale(1f/mScale, 1f/mScale);

        g.setColor(Color.black);
        g.drawRect(0, 0, lw - 1, lh - 1);
    }

    private void drawRect(Graphics2D g, int px, int py, JSONObject bounds, boolean fill, Color color) {
        int l = bounds.getInt("left");
        int t = bounds.getInt("top");
        int r = bounds.getInt("right");
        int b = bounds.getInt("bottom");
        int x = l + px;
        int y = t + py;
        int w = r - l;
        int h = b - t;

        if (fill) {
            g.setColor(new Color(200, 200, 200, 100));
            g.fillRect(x, y, w, h);
        }

        g.setColor(color);
        g.drawRect(x, y, w + 1, h + 1);
        if (fill) {
            g.drawLine(x, y, x + w, y + h);
            g.drawLine(x, y + h , x + w, y);
        }
    }

    private void drawGuideline(Graphics2D g, int px, int py, JSONObject bounds, Color color) {
        int l = bounds.getInt("left");
        int t = bounds.getInt("top");
        int r = bounds.getInt("right");
        int b = bounds.getInt("bottom");
        int x = l + px;
        int y = t + py;

        g.setColor(color);
        Graphics2D g2 = (Graphics2D) g.create();
        Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
        g2.setStroke(dashed);

        if (x > 0) {
            g2.drawLine(x, y, x, (int) (y + getHeight() / mScale));
        } else {
            g2.drawLine(x, y, (int) (x + getWidth() / mScale), y);
        }
        g2.dispose();
    }

    private void drawRoot(Graphics2D g, Color color, JSONObject layout) {
        drawLayout(g, color,0, 0, false, layout);
    }

    private void drawLayout(Graphics2D g, Color color, int x, int y, boolean fill, JSONObject layout) {
        JSONObject bounds = layout.getJSONObject("bounds");
        int l = bounds.getInt("left");
        int t = bounds.getInt("top");
        int r = bounds.getInt("right");
        int b = bounds.getInt("bottom");

        boolean isSelected = layout == mSelectedView;
        if (isSelected) {
            color = Color.BLUE;
        }
        if (layout.getString("class").equals("Guideline")) {
            drawGuideline(g, x, y, bounds, color);
        }
        drawRect(g, x, y, bounds, fill, color);
        int lx = l + x;
        int ly = t + y;
        if (layout.has("children")) {
            JSONArray children = layout.getJSONArray("children");
            for (int i = 0; i < children.length(); i++) {
                JSONObject child = children.getJSONObject(i);
                drawLayout(g, color, lx, ly,false, child);
            }
        }
    }

    public void setValue(JSONObject jsonObject) {
        if (jsonObject != null) {
//        System.out.println("json: " + jsonObject.toString());
            mLayout = jsonObject.getJSONObject("layout"); //.getJSONObject("check_LAYOUT_optimizer");
            repaint();
        }
    }

    public void setReferenceValue(JSONObject jsonObject) {
        if (jsonObject != null) {
            mReferenceLayout = jsonObject.getJSONObject("layout");
            repaint();
        }
    }

    public void setSelectedView(Object nodeInfo) {
        if (nodeInfo instanceof JSONObject) {
            mSelectedView = (JSONObject) nodeInfo;
        } else {
            mSelectedView = null;
        }
        repaint();
    }

    public void setImage(Image image) {
        mImage = image;
    }

    public void setShowImage(boolean selected) {
        mShowImage = selected;
        repaint();
    }

    public void setShowReferenceBounds(boolean selected) {
        mShowReferenceBounds = selected;
        repaint();
    }

    public void setShowCurrentBounds(boolean selected) {
        mShowCurrentBounds = selected;
        repaint();
    }

    public void setLayoutInfo(String name, Main.LayoutType mode) {
        mLayoutName = name;
        mLayoutType = mode;
        repaint();
    }
}
