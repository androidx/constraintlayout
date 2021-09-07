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
import androidx.constraintlayout.core.widgets.ConstraintWidget;
import androidx.constraintlayout.core.widgets.ConstraintWidgetContainer;
import androidx.constraintlayout.core.widgets.Guideline;
import androidx.constraintlayout.core.widgets.Optimizer;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import org.constraintlayout.swing.core.ConstraintLayoutState;
import org.constraintlayout.swing.core.ConstraintSetParser;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

/**
 * Basic implementation of ConstraintLayout as a Swing LayoutManager
 */
public class ConstraintLayout implements LayoutManager2 {
    private static final boolean DEBUG = false;
    private final ConstraintWidgetContainer mLayout = new ConstraintWidgetContainer();
    private final HashMap<Component, ConstraintWidget> mViewsToConstraints = new HashMap<>();
    private final ConstraintSetParser parser = new ConstraintSetParser();
    private final ConstraintLayoutState state = new ConstraintLayoutState();
    private final HashMap<Component, String> viewsToIds = new HashMap<>();
    private final HashMap<String, ConstraintWidget> idsToConstraintWidgets = new HashMap<>();
    Container parentContainer;

    public ConstraintLayout() {
    }

    public ConstraintLayout(@Language("JSON5") String content) {
        parse(content);
    }

    public ConstraintLayout(@Language("JSON5") String content, Runnable runnable) {
        parse(content);
        SwingUtilities.invokeLater(runnable);
    }

    public void parse(@Language("JSON5") String content) {
        parser.parse(content, state);
        idsToConstraintWidgets.put("parent", mLayout);
        if (DEBUG) {
            System.out.println("state:\n" + state.serialize());
        }
        mLayout.setMeasurer(new BasicMeasure.Measurer() {

            @Override
            public void measure(ConstraintWidget widget, BasicMeasure.Measure measure) {
                innerMeasure(widget, measure);
            }

            @Override
            public void didMeasures() {

            }
        });

        if (parser.getExportedName() != null) {
            RemoteDebug.debug(parser, content, state, mLayout, this);
        }
    }

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
        if (constraints instanceof String) {
            String id = (String) constraints;
            ConstraintWidget constraintWidget = new ConstraintWidget();
            mLayout.add(constraintWidget);
            constraintWidget.stringId = id;
            constraintWidget.setCompanionWidget(comp);
            idsToConstraintWidgets.put(id, constraintWidget);
            viewsToIds.put(comp,id);
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
        String id = viewsToIds.get(comp);
        ConstraintWidget widget = idsToConstraintWidgets.get(id);
        mLayout.remove(widget);
        idsToConstraintWidgets.remove(id);
        viewsToIds.remove(comp);
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
        parentContainer = parent;

        state.guidelines.apply(mLayout, idsToConstraintWidgets);
        if (DEBUG) {
            Utils.log("Current system is:\n" + state.serialize());
        }
        for (String id : idsToConstraintWidgets.keySet()) {
            if (id.equals("parent")) {
                continue;
            }
            // ask the state to create a widget
            ConstraintWidget constraintWidget = idsToConstraintWidgets.get(id);
            if (constraintWidget instanceof Guideline) {
                continue;
            }
            if (DEBUG) {
                Utils.log("applying " + id);
            }
            state.constraints.apply(idsToConstraintWidgets, constraintWidget);
        }

        mLayout.setWidth(width);
        mLayout.setHeight(height);
        mLayout.layout();
        mLayout.setOptimizationLevel(Optimizer.OPTIMIZATION_STANDARD);
        mLayout.measure(Optimizer.OPTIMIZATION_STANDARD, BasicMeasure.EXACTLY, width, BasicMeasure.EXACTLY, height,
                0, 0, 0, 0);

        for (ConstraintWidget child : mLayout.getChildren()) {
            Component component = (Component) child.getCompanionWidget();
            if (component != null) {
                if (DEBUG) {
                    Utils.log("applying " + child.getWidth()+" "+child.getHeight());
                }
                component.setBounds(child.getX(), child.getY(), child.getWidth(), child.getHeight());
            }
        }
    }

    public ConstraintWidgetContainer getRoot() {
        return mLayout;
    }


    private void innerMeasure(ConstraintWidget constraintWidget, BasicMeasure.Measure measure) {
        Component component = (Component) constraintWidget.getCompanionWidget();
        int measuredWidth = constraintWidget.getWidth();
        int measuredHeight = constraintWidget.getHeight();
        if (DEBUG) {
            Utils.log(" measure " + measuredWidth+" "+measuredHeight);
        }
        if (measure.horizontalBehavior == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
            measuredWidth = component.getMinimumSize().width;
        } else if (measure.horizontalBehavior == ConstraintWidget.DimensionBehaviour.MATCH_PARENT) {
            measuredWidth = mLayout.getWidth();
        }
        if (measure.verticalBehavior == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
            measuredHeight = component.getMinimumSize().height;
        } else if (measure.verticalBehavior == ConstraintWidget.DimensionBehaviour.MATCH_PARENT) {
            measuredHeight = mLayout.getHeight();
        }
        measure.measuredWidth = measuredWidth;
        measure.measuredHeight = measuredHeight;
    }
}
