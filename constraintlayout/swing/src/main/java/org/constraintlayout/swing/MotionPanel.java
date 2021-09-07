/*
 * Copyright (C) 2021 The Android Open Source Project
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

package org.constraintlayout.swing;

import androidx.constraintlayout.core.motion.utils.Utils;
import androidx.constraintlayout.core.state.WidgetFrame;
import org.constraintlayout.swing.core.motion.model.MotionEngine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.util.HashMap;

/**
 * Basic implementation of ConstraintLayout as a Swing LayoutManager
 */
public class MotionPanel extends JComponent  {
    MotionLayout  mMotionLayout;
    public MotionPanel() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                mMotionLayout.motionEngine.remeasure();
            }
        });
    }

    @Override
    public void setLayout(LayoutManager mgr) {

    }

    public void setLayoutDescription(@Language("JSON5") String content) {
        super.setLayout(mMotionLayout = new MotionLayout(content));
    }

    public MotionPanel(@Language("JSON5") String content) {
        setLayoutDescription(content);
    }

    public MotionPanel(@Language("JSON5") String content, Runnable runnable) {
         SwingUtilities.invokeLater(runnable);
    }

    public void add(Component comp, Object constraints) {
        super.add(comp, constraints);
    }
    AffineTransform at = new AffineTransform();

    @Override
    public void paint(Graphics g) {
        g.setColor(getBackground());
g.setClip(0,0,getWidth(),getHeight());
        g.fillRect(0,0,getWidth(),getHeight());
        super.paint(g);
    }

    @Override
    protected void paintChildren(Graphics g) {
        Graphics sg = g;

        synchronized(getTreeLock()) {
            int i = getComponentCount() - 1;
            if (i < 0) {
                return;
            }

            Rectangle tmpRect = new Rectangle();

            Rectangle clipBounds = null;


            final Window window = SwingUtilities.getWindowAncestor(this);
            final boolean isWindowOpaque = window == null || window.isOpaque();
            for (; i >= 0 ; i--) {
                Component comp = getComponent(i);
                String id = mMotionLayout.mViewsToIds.get(comp);

                if (comp == null) {
                    continue;
                }

                final boolean isJComponent = comp instanceof JComponent;

                // Enable painting of heavyweights in non-opaque windows.
                // See 6884960

                    Rectangle cr;

                    cr = comp.getBounds(tmpRect);
                     Shape clip =null;//g.getClip();
                    boolean hitClip = clip == null || clip.intersects(cr.x, cr.y, cr.width, cr.height);

                    if (hitClip) {
//                        Graphics2D cg = (Graphics2D) sg.create(cr.x, cr.y, cr.width,
//                                cr.height);
                        Graphics2D cg = (Graphics2D) sg.create(  );
                        WidgetFrame m = mMotionLayout.getInterpolated(id);
                        setupTransform(m);
                        cg.transform(at);
                        cg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        cg.translate(cr.x, cr.y);
                        cg.clip(new Rectangle2D.Float(0,0,cr.width,cr.height));
                        cg.setColor(comp.getForeground());
                        cg.setFont(comp.getFont());
                        boolean shouldSetFlagBack = false;
                        try {
                                    comp.paint(cg);
                        } finally {
                            cg.dispose();
                        }
                    }


            }

        }
    }

    void setupTransform(WidgetFrame frame) {

//        float cx = (frame.left + frame.right) / 2f;
//        float cy = (frame.top + frame.bottom) / 2f;
//        float dx = frame.right - frame.left;
//        float dy = frame.bottom - frame.top;
        float cx = (frame.right-frame.left ) / 2f;
        float cy = (frame.bottom-frame.top) / 2f;
        float dx = frame.right - frame.left;
        float dy = frame.bottom - frame.top;

        float rotationZ = Float.isNaN(frame.rotationZ) ? 0 : frame.rotationZ;
        float pivotX = Float.isNaN(frame.pivotX) ? cx : frame.pivotX * dx + frame.left;
        float pivotY = Float.isNaN(frame.pivotY) ? cy : frame.pivotY * dy + frame.top;
        float rotationX = Float.isNaN(frame.rotationX) ? 0 : frame.rotationX;
        float rotationY = Float.isNaN(frame.rotationY) ? 0 : frame.rotationY;

        float translationX  = Float.isNaN(frame.translationX) ? 0 : frame.translationX;
        float translationY = Float.isNaN(frame.translationY) ? 0 : frame.translationY;
        float translationZ = Float.isNaN(frame.translationZ) ? 0 : frame.translationZ;

        float scaleX = Float.isNaN(frame.scaleX) ? 1 : frame.scaleX;
        float scaleY = Float.isNaN(frame.scaleY) ? 1 : frame.scaleY;

        at.setToIdentity();
        at.translate(translationX, translationY);
        at.translate(pivotX, pivotY);
//        System.out.println(frame.widget.stringId+" rotationZ = "+rotationZ+" rotationZ = "+translationX+" translationX = "+rotationZ+" scaleX = "+scaleX);

        at.rotate(Math.toRadians(rotationZ));

        at.scale(scaleX, scaleY);
        at.translate(-pivotX, -pivotY);
    }

    public void layoutContainer(Container parent) {
        Utils.logStack("???",3);
    }


    public void setProgress(float p) {
        mMotionLayout.setProgress(p);
        doLayout();
        repaint();
    }
}
