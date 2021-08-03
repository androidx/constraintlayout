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

import androidx.constraintlayout.core.state.Registry;
import androidx.constraintlayout.core.state.RegistryCallback;
import androidx.constraintlayout.core.widgets.ConstraintWidget;
import androidx.constraintlayout.core.widgets.ConstraintWidgetContainer;
import androidx.constraintlayout.core.widgets.Guideline;
import androidx.constraintlayout.core.widgets.Optimizer;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import org.intellij.lang.annotations.Language;

import java.awt.*;
import java.util.HashMap;

/**
 * Naive implementation of ConstraintLayout as a Swing LayoutManager
 */
public class ConstraintLayout implements LayoutManager2, BasicMeasure.Measurer {

    private ConstraintWidgetContainer mLayout = new ConstraintWidgetContainer();
    private HashMap<Component, ConstraintWidget> mViewsToConstraints = new HashMap<>();
    private ConstraintSetParser parser = new ConstraintSetParser();
    private ConstraintLayoutState state = new ConstraintLayoutState();

    private HashMap<Component, String> viewsToIds = new HashMap<>();
    private HashMap<String, ConstraintWidget> idsToConstraintWidgets = new HashMap<>();
    Container parentContainer;

    public ConstraintLayout() {}

    public ConstraintLayout(@Language("JSON5") String content) {
        parse(content);
    }

    public void parse(@Language("JSON5") String content) {
        parser.parse(content, state);
        idsToConstraintWidgets.put("parent", mLayout);
        System.out.println("state:\n" + state.serialize());
        mLayout.setMeasurer(this);

        if (parser.getExportedName() != null) {
                Registry registry = Registry.getInstance();
                registry.register(parser.getExportedName(), new RegistryCallback() {
                    @Override
                    public void onNewMotionScene(String content) {
                        try {
                            parser.parse(content, state);
                            if (parentContainer != null) {
                                parentContainer.revalidate();
                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onProgress(float progress) {

                    }

                    @Override
                    public void onDimensions(int width, int height) {

                    }

                    @Override
                    public String currentMotionScene() {
                        return content;
                    }

                    @Override
                    public void setDrawDebug(int debugMode) {

                    }

                    @Override
                    public String currentLayoutInformation() {
                        String layout = getSerializedLayout();
                        System.out.println("layout:\n" + layout);
                        return layout;
                    }

                    @Override
                    public void setLayoutInformationMode(int layoutInformationMode) {

                    }

                    @Override
                    public long getLastModified() {
                        return 0;
                    }
                });
        }
    }

    private void serializeWidget(StringBuilder builder, ConstraintWidget widget) {
        builder.append(widget.stringId);
        builder.append(": {");
        if (widget instanceof Guideline) {
            Guideline guideline = (Guideline) widget;
            if (guideline.getOrientation() == Guideline.HORIZONTAL) {
                builder.append("type: 'hGuideline',");
            } else {
                builder.append("type: 'vGuideline',");
            }
        }
        builder.append(" interpolated: { ");
        builder.append(" left: ");
        builder.append(widget.getLeft());
        builder.append(", top: ");
        builder.append(widget.getTop());
        builder.append(", ");
        builder.append("right: ");
        builder.append(widget.getRight());
        builder.append(", bottom: ");
        builder.append(widget.getBottom());
        builder.append("}}, ");
    }

    public String getSerializedLayout() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        mLayout.stringId = "root";
        serializeWidget(builder, mLayout);

        for (ConstraintWidget widget : mLayout.getChildren()) {
            serializeWidget(builder, widget);
        }

        builder.append("}");
        return builder.toString();
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

        System.out.println("Current system is:\n" + state.serialize());

        for (String id : idsToConstraintWidgets.keySet()) {
            if (id.equals("parent")) {
                continue;
            }
            // ask the state to create a widget
            ConstraintWidget constraintWidget = idsToConstraintWidgets.get(id);
            if (constraintWidget instanceof Guideline) {
                continue;
            }
            System.out.println("applying " + id);
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
                component.setBounds(child.getX(), child.getY(), child.getWidth(), child.getHeight());
            }
        }
    }

    public ConstraintWidgetContainer getRoot() {
        return mLayout;
    }

    @Override
    public void measure(ConstraintWidget constraintWidget, BasicMeasure.Measure measure) {
        Component component = (Component) constraintWidget.getCompanionWidget();
        int measuredWidth = constraintWidget.getWidth();
        int measuredHeight = constraintWidget.getHeight();
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

    @Override
    public void didMeasures() {

    }
}
