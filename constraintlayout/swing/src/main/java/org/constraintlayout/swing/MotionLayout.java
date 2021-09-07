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
import androidx.constraintlayout.core.widgets.ConstraintWidget;
import org.constraintlayout.swing.core.motion.model.MotionEngine;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

/**
 * Basic implementation of ConstraintLayout as a Swing LayoutManager
 */
public class MotionLayout implements LayoutManager2 {
    private static final boolean DEBUG = false;
    MotionEngine motionEngine = new MotionEngine();
    Container mParent;
      final HashMap<Component, String> mViewsToIds = new HashMap<>();
    private final HashMap<String, Component> mIdsToViews = new HashMap<>();

    public MotionLayout() {
    }

    public MotionLayout(@Language("JSON5") String content) {
        parse(content);
    }

    public MotionLayout(@Language("JSON5") String content, Runnable runnable) {
        parse(content);
        SwingUtilities.invokeLater(runnable);
    }

    public void parse(@Language("JSON5") String content) {
        motionEngine.parse(content);
    }

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
        if (constraints instanceof String) {
            String id = (String) constraints;

            motionEngine.add(id);
            mIdsToViews.put(id, comp);
            mViewsToIds.put(comp, id);
        }
    }

    @Override
    public Dimension maximumLayoutSize(Container target) {
        return null;
    }

    @Override
    public float getLayoutAlignmentX(Container target) {
        return 0;
    }

    @Override
    public float getLayoutAlignmentY(Container target) {
        return 0;
    }

    @Override
    public void invalidateLayout(Container target) {
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {

    }

    @Override
    public void removeLayoutComponent(Component comp) {
        String id = mViewsToIds.get(comp);
        if (id == null) {
            return;
        }
        motionEngine.remove(id);
        mIdsToViews.remove(id);
        mViewsToIds.remove(comp);
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        return null;
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return null;
    }

    @Override
    public void layoutContainer(Container parent) {
         int width = parent.getWidth();
        int height = parent.getHeight();
        mParent = parent;
        motionEngine.layout(width, height, (id, child) -> {
            Component component = mIdsToViews.get(id);
            component.setBounds(child.left, child.top, child.right-child.left, child.bottom-child.top);
        });
    }


    public void setProgress(float p) {
    motionEngine.setProgress(p);
    if (mParent != null) {
        layoutContainer(mParent);
    }
    }

    public WidgetFrame getInterpolated(String id) {
        return motionEngine.getInterpolated(id);
    }
}
