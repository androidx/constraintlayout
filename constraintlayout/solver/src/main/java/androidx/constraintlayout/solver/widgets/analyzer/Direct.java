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
package androidx.constraintlayout.solver.widgets.analyzer;

import androidx.constraintlayout.solver.LinearSystem;
import androidx.constraintlayout.solver.widgets.Barrier;
import androidx.constraintlayout.solver.widgets.ChainHead;
import androidx.constraintlayout.solver.widgets.ConstraintAnchor;
import androidx.constraintlayout.solver.widgets.ConstraintWidget;
import androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer;
import androidx.constraintlayout.solver.widgets.Guideline;

import java.util.ArrayList;

import static androidx.constraintlayout.solver.widgets.ConstraintWidget.GONE;
import static androidx.constraintlayout.solver.widgets.ConstraintWidget.HORIZONTAL;
import static androidx.constraintlayout.solver.widgets.ConstraintWidget.VERTICAL;

/**
 * Direct resolution engine
 *
 * This walks through the graph of dependencies and infer final position. This allows
 * us to skip the linear solver in many situations, as well as skipping intermediate measure passes.
 *
 * Widgets are solved independently in horizontal and vertical. Any widgets not fully resolved
 * will be computed later on by the linear solver.
 */
public class Direct {

    private static final boolean DEBUG = LinearSystem.FULL_DEBUG;
    private static final boolean APPLY_MATCH_PARENT = false;
    private static BasicMeasure.Measure measure = new BasicMeasure.Measure();

    /**
     * Walk the dependency graph and solves it.
     *
     * @param layout the container we want to optimize
     * @param measurer the measurer used to measure the widget
     */
    public static void solvingPass(ConstraintWidgetContainer layout, BasicMeasure.Measurer measurer) {
        ConstraintWidget.DimensionBehaviour horizontal = layout.getHorizontalDimensionBehaviour();
        ConstraintWidget.DimensionBehaviour vertical = layout.getVerticalDimensionBehaviour();
        if (DEBUG) {
            System.out.println("#### SOLVING PASS " + horizontal + " ####");
        }
        layout.resetFinalResolution();
        ArrayList<ConstraintWidget> children = layout.getChildren();
        final int count = children.size();
        for (int i = 0; i < count; i++) {
            ConstraintWidget child = children.get(i);
            child.resetFinalResolution();
        }

        // First, let's solve the horizontal dependencies, as it's a lot more common to have
        // a container with a fixed horizontal dimension (e.g. match_parent) than the opposite.

        // If we know our size, we can fully set the entire dimension, but if not we can
        // still solve what we can starting from the left.
        if (horizontal == ConstraintWidget.DimensionBehaviour.FIXED) {
            layout.setFinalHorizontal(0, layout.getWidth());
        } else {
            layout.setFinalLeft(0);
        }

        // Then let's first try to solve horizontal guidelines, as they only depends on the container
        boolean hasGuideline = false;
        boolean hasBarrier = false;
        for (int i = 0; i < count; i++) {
            ConstraintWidget child = children.get(i);
            if (child instanceof Guideline) {
                Guideline guideline = (Guideline) child;
                if (guideline.getOrientation() == Guideline.VERTICAL) {
                    if (guideline.getRelativeBegin() != -1) {
                        guideline.setFinalValue(guideline.getRelativeBegin());
                    } else if (guideline.getRelativeEnd() != -1 && layout.isResolvedHorizontally()) {
                        guideline.setFinalValue(layout.getWidth() - guideline.getRelativeEnd());
                    } else if (layout.isResolvedHorizontally()) {
                        int position = (int) (0.5f + guideline.getRelativePercent() * layout.getWidth());
                        guideline.setFinalValue(position);
                    }
                    hasGuideline = true;
                }
            } else if (child instanceof Barrier) {
                Barrier barrier = (Barrier) child;
                if (barrier.getOrientation() == HORIZONTAL) {
                    hasBarrier = true;
                }
            }
        }
        if (hasGuideline) {
            for (int i = 0; i < count; i++) {
                ConstraintWidget child = children.get(i);
                if (child instanceof Guideline) {
                    Guideline guideline = (Guideline) child;
                    if (guideline.getOrientation() == Guideline.VERTICAL) {
                        horizontalSolvingPass(guideline, measurer);
                    }
                }
            }
        }

        // Now let's resolve what we can in the dependencies of the container
        horizontalSolvingPass(layout, measurer);

        // Finally, let's go through barriers, as they depends on widgets that may have been solved.
        if (hasBarrier) {
            for (int i = 0; i < count; i++) {
                ConstraintWidget child = children.get(i);
                if (child instanceof Barrier) {
                    Barrier barrier = (Barrier) child;
                    if (barrier.getOrientation() == HORIZONTAL) {
                        solveBarrier(barrier, measurer, HORIZONTAL);
                    }
                }
            }
        }

        // Now we are done with the horizontal axis, let's see what we can do vertically
        if (vertical == ConstraintWidget.DimensionBehaviour.FIXED) {
            layout.setFinalVertical(0, layout.getHeight());
        } else {
            layout.setFinalTop(0);
        }

        // Same thing as above -- let's start with guidelines...
        hasGuideline = false;
        hasBarrier = false;
        for (int i = 0; i < count; i++) {
            ConstraintWidget child = children.get(i);
            if (child instanceof Guideline) {
                Guideline guideline = (Guideline) child;
                if (guideline.getOrientation() == Guideline.HORIZONTAL) {
                    if (guideline.getRelativeBegin() != -1) {
                        guideline.setFinalValue(guideline.getRelativeBegin());
                    } else if (guideline.getRelativeEnd() != -1 && layout.isResolvedVertically()) {
                        guideline.setFinalValue(layout.getHeight() - guideline.getRelativeEnd());
                    } else if (layout.isResolvedVertically()) {
                        int position = (int) (0.5f + guideline.getRelativePercent() * layout.getHeight());
                        guideline.setFinalValue(position);
                    }
                    hasGuideline = true;
                }
            } else if (child instanceof Barrier) {
                Barrier barrier = (Barrier) child;
                if (barrier.getOrientation() == ConstraintWidget.VERTICAL) {
                    hasBarrier = true;
                }
            }
        }
        if (hasGuideline) {
            for (int i = 0; i < count; i++) {
                ConstraintWidget child = children.get(i);
                if (child instanceof Guideline) {
                    Guideline guideline = (Guideline) child;
                    if (guideline.getOrientation() == Guideline.HORIZONTAL) {
                        verticalSolvingPass(guideline, measurer);
                    }
                }
            }
        }

        // ...then solve the vertical dependencies...
        verticalSolvingPass(layout, measurer);

        // ...then deal with any barriers left.
        if (hasBarrier) {
            for (int i = 0; i < count; i++) {
                ConstraintWidget child = children.get(i);
                if (child instanceof Barrier) {
                    Barrier barrier = (Barrier) child;
                    if (barrier.getOrientation() == ConstraintWidget.VERTICAL) {
                        solveBarrier(barrier, measurer, VERTICAL);
                    }
                }
            }
        }

        // We can do a last pass to see any widget that could still be measured
        for (int i = 0; i < count; i++) {
            ConstraintWidget child = children.get(i);
            if (child.isMeasureRequested() && canMeasure(child)) {
                ConstraintWidgetContainer.measure(child, measurer, measure, false);
                horizontalSolvingPass(child, measurer);
                verticalSolvingPass(child, measurer);
            }
        }
    }

    /**
     * Ask the barrier if it's resolved, and if so do a solving pass
     *
     * @param barrier
     * @param measurer
     */
    private static void solveBarrier(Barrier barrier, BasicMeasure.Measurer measurer, int orientation) {
        if (barrier.allSolved()) {
            if (orientation == HORIZONTAL) {
                horizontalSolvingPass(barrier, measurer);
            } else {
                verticalSolvingPass(barrier, measurer);
            }
        }
    }

    /**
     * Does an horizontal solving pass for the given widget. This will walk through the widget's
     * horizontal dependencies and if they can be resolved directly, do so.
     *
     * @param layout the widget we want to solve the dependencies
     * @param measurer the measurer object to measure the widgets.
     */
    private static void horizontalSolvingPass(ConstraintWidget layout, BasicMeasure.Measurer measurer) {
        if (DEBUG) {
            System.out.println("HORIZONTAL SOLVING PASS ON " + layout.getDebugName());
        }

        if (!(layout instanceof ConstraintWidgetContainer) && layout.isMeasureRequested() && canMeasure(layout)) {
            BasicMeasure.Measure measure = new BasicMeasure.Measure();
            ConstraintWidgetContainer.measure(layout, measurer, measure, false);
        }

        ConstraintAnchor left = layout.getAnchor(ConstraintAnchor.Type.LEFT);
        ConstraintAnchor right = layout.getAnchor(ConstraintAnchor.Type.RIGHT);
        int l = left.getFinalValue();
        int r = right.getFinalValue();

        if (left.getDependents() != null && left.hasFinalValue()) {
            for (ConstraintAnchor first : left.getDependents()) {
                ConstraintWidget widget = first.mOwner;
                int x1 = 0;
                int x2 = 0;
                boolean canMeasure = canMeasure(widget);
                if (widget.isMeasureRequested() && canMeasure) {
                    BasicMeasure.Measure measure = new BasicMeasure.Measure();
                    ConstraintWidgetContainer.measure(widget, measurer, measure, false);
                }

                if (widget.getHorizontalDimensionBehaviour() != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
                        || canMeasure) {
                    if (widget.isMeasureRequested()) {
                        // Widget needs to be measured
                        if (DEBUG) {
                            System.out.println("(L) We didn't measure " + widget.getDebugName() + ", le'ts bail");
                        }
                        continue;
                    }
                    if (first == widget.mLeft && widget.mRight.mTarget == null) {
                        x1 = l + widget.mLeft.getMargin();
                        x2 = x1 + widget.getWidth();
                        widget.setFinalHorizontal(x1, x2);
                        horizontalSolvingPass(widget, measurer);
                    } else if (first == widget.mRight && widget.mLeft.mTarget == null) {
                        x2 = l - widget.mRight.getMargin();
                        x1 = x2 - widget.getWidth();
                        widget.setFinalHorizontal(x1, x2);
                        horizontalSolvingPass(widget, measurer);
                    } else if (first == widget.mLeft && widget.mRight.mTarget != null
                            && widget.mRight.mTarget.hasFinalValue() && !widget.isInHorizontalChain()) {
                        solveHorizontalCenterConstraints(measurer, widget);
                    } else if (APPLY_MATCH_PARENT && widget.getHorizontalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.MATCH_PARENT) {
                        widget.setFinalHorizontal(0, widget.getWidth());
                        horizontalSolvingPass(widget, measurer);
                    }
                } else if (widget.getHorizontalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
                        && (widget.getVisibility() == ConstraintWidget.GONE || ((widget.mMatchConstraintDefaultWidth == ConstraintWidget.MATCH_CONSTRAINT_SPREAD) && widget.getDimensionRatio() == 0))
                        && !widget.isInHorizontalChain() && !widget.isInVirtualLayout()) {
                    boolean bothConnected = (first == widget.mLeft && widget.mRight.mTarget != null && widget.mRight.mTarget.hasFinalValue())
                            || (first == widget.mRight && widget.mLeft.mTarget != null && widget.mLeft.mTarget.hasFinalValue());
                    if (bothConnected && !widget.isInHorizontalChain()) {
                        solveHorizontalMatchConstraint(layout, measurer, widget);
                    }
                }
            }
        }
        if (layout instanceof Guideline) {
            return;
        }
        if (right.getDependents() != null && right.hasFinalValue()) {
            for (ConstraintAnchor first : right.getDependents()) {
                ConstraintWidget widget = first.mOwner;
                boolean canMeasure = canMeasure(widget);
                if (widget.isMeasureRequested() && canMeasure) {
                    BasicMeasure.Measure measure = new BasicMeasure.Measure();
                    ConstraintWidgetContainer.measure(widget, measurer, measure, false);
                }

                int x1 = 0;
                int x2 = 0;
                boolean bothConnected = (first == widget.mLeft && widget.mRight.mTarget != null && widget.mRight.mTarget.hasFinalValue())
                        || (first == widget.mRight && widget.mLeft.mTarget != null && widget.mLeft.mTarget.hasFinalValue());
                if (widget.getHorizontalDimensionBehaviour() != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
                        || canMeasure) {
                    if (widget.isMeasureRequested()) {
                        // Widget needs to be measured
                        if (DEBUG) {
                            System.out.println("(R) We didn't measure " + widget.getDebugName() + ", le'ts bail");
                        }
                        continue;
                    }
                    if (first == widget.mLeft && widget.mRight.mTarget == null) {
                        x1 = r + widget.mLeft.getMargin();
                        x2 = x1 + widget.getWidth();
                        widget.setFinalHorizontal(x1, x2);
                        horizontalSolvingPass(widget, measurer);
                    } else if (first == widget.mRight && widget.mLeft.mTarget == null) {
                        x2 = r - widget.mRight.getMargin();
                        x1 = x2 - widget.getWidth();
                        widget.setFinalHorizontal(x1, x2);
                        horizontalSolvingPass(widget, measurer);
                    } else if (bothConnected && !widget.isInHorizontalChain()) {
                        solveHorizontalCenterConstraints(measurer, widget);
                    }
                } else if (widget.getHorizontalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
                        && (widget.getVisibility() == ConstraintWidget.GONE || ((widget.mMatchConstraintDefaultWidth == ConstraintWidget.MATCH_CONSTRAINT_SPREAD) && widget.getDimensionRatio() == 0))
                        && !widget.isInHorizontalChain() && !widget.isInVirtualLayout()) {
                    if (bothConnected && !widget.isInHorizontalChain()) {
                        solveHorizontalMatchConstraint(layout, measurer, widget);
                    }
                }
            }
        }
    }

    /**
     * Does an vertical solving pass for the given widget. This will walk through the widget's
     * vertical dependencies and if they can be resolved directly, do so.
     *
     * @param layout the widget we want to solve the dependencies
     * @param measurer the measurer object to measure the widgets.
     */
    private static void verticalSolvingPass(ConstraintWidget layout, BasicMeasure.Measurer measurer) {
        if (DEBUG) {
            System.out.println("VERTICAL SOLVING PASS ON " + layout.getDebugName());
        }

        if (!(layout instanceof ConstraintWidgetContainer) && layout.isMeasureRequested() && canMeasure(layout)) {
            BasicMeasure.Measure measure = new BasicMeasure.Measure();
            ConstraintWidgetContainer.measure(layout, measurer, measure, false);
        }

        ConstraintAnchor top = layout.getAnchor(ConstraintAnchor.Type.TOP);
        ConstraintAnchor bottom = layout.getAnchor(ConstraintAnchor.Type.BOTTOM);
        int t = top.getFinalValue();
        int b = bottom.getFinalValue();

        if (top.getDependents() != null && top.hasFinalValue()) {
            for (ConstraintAnchor first : top.getDependents()) {
                ConstraintWidget widget = first.mOwner;
                int y1 = 0;
                int y2 = 0;
                boolean canMeasure = canMeasure(widget);
                if (widget.isMeasureRequested() && canMeasure) {
                    BasicMeasure.Measure measure = new BasicMeasure.Measure();
                    ConstraintWidgetContainer.measure(widget, measurer, measure, false);
                }

                if (widget.getVerticalDimensionBehaviour() != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
                        || canMeasure) {
                    if (widget.isMeasureRequested()) {
                        // Widget needs to be measured
                        if (DEBUG) {
                            System.out.println("(T) We didn't measure " + widget.getDebugName() + ", le'ts bail");
                        }
                        continue;
                    }
                    if (first == widget.mTop && widget.mBottom.mTarget == null) {
                        y1 = t + widget.mTop.getMargin();
                        y2 = y1 + widget.getHeight();
                        widget.setFinalVertical(y1, y2);
                        verticalSolvingPass(widget, measurer);
                    } else if (first == widget.mBottom && widget.mBottom.mTarget == null) {
                        y2 = t - widget.mBottom.getMargin();
                        y1 = y2 - widget.getHeight();
                        widget.setFinalVertical(y1, y2);
                        verticalSolvingPass(widget, measurer);
                    } else if (first == widget.mTop && widget.mBottom.mTarget != null
                            && widget.mBottom.mTarget.hasFinalValue()) {
                        solveVerticalCenterConstraints(measurer, widget);
                    } else if (APPLY_MATCH_PARENT && widget.getVerticalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.MATCH_PARENT) {
                        widget.setFinalVertical(0, widget.getHeight());
                        verticalSolvingPass(widget, measurer);
                    }
                } else if (widget.getVerticalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
                        && (widget.getVisibility() == ConstraintWidget.GONE || ((widget.mMatchConstraintDefaultHeight == ConstraintWidget.MATCH_CONSTRAINT_SPREAD) && widget.getDimensionRatio() == 0))
                        && !widget.isInVerticalChain() && !widget.isInVirtualLayout()) {
                    boolean bothConnected = (first == widget.mTop && widget.mBottom.mTarget != null && widget.mBottom.mTarget.hasFinalValue())
                            || (first == widget.mBottom && widget.mTop.mTarget != null && widget.mTop.mTarget.hasFinalValue());
                    if (bothConnected && !widget.isInVerticalChain()) {
                        solveVerticalMatchConstraint(layout, measurer, widget);
                    }
                }
            }
        }
        if (layout instanceof Guideline) {
            return;
        }
        if (bottom.getDependents() != null && bottom.hasFinalValue()) {
            for (ConstraintAnchor first : bottom.getDependents()) {
                ConstraintWidget widget = first.mOwner;
                boolean canMeasure = canMeasure(widget);
                if (widget.isMeasureRequested() && canMeasure) {
                    BasicMeasure.Measure measure = new BasicMeasure.Measure();
                    ConstraintWidgetContainer.measure(widget, measurer, measure, false);
                }

                int y1 = 0;
                int y2 = 0;
                boolean bothConnected = (first == widget.mTop && widget.mBottom.mTarget != null && widget.mBottom.mTarget.hasFinalValue())
                        || (first == widget.mBottom && widget.mTop.mTarget != null && widget.mTop.mTarget.hasFinalValue());
                if (widget.getVerticalDimensionBehaviour() != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
                        || canMeasure) {
                    if (widget.isMeasureRequested()) {
                        // Widget needs to be measured
                        if (DEBUG) {
                            System.out.println("(B) We didn't measure " + widget.getDebugName() + ", le'ts bail");
                        }
                        continue;
                    }
                    if (first == widget.mTop && widget.mBottom.mTarget == null) {
                        y1 = b + widget.mTop.getMargin();
                        y2 = y1 + widget.getHeight();
                        widget.setFinalVertical(y1, y2);
                        verticalSolvingPass(widget, measurer);
                    } else if (first == widget.mBottom && widget.mTop.mTarget == null) {
                        y2 = b - widget.mBottom.getMargin();
                        y1 = y2 - widget.getHeight();
                        widget.setFinalVertical(y1, y2);
                        verticalSolvingPass(widget, measurer);
                    } else if (bothConnected && !widget.isInVerticalChain()) {
                        solveVerticalCenterConstraints(measurer, widget);
                    }
                } else if (widget.getVerticalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
                        && (widget.getVisibility() == ConstraintWidget.GONE || ((widget.mMatchConstraintDefaultHeight == ConstraintWidget.MATCH_CONSTRAINT_SPREAD) && widget.getDimensionRatio() == 0))
                        && !widget.isInVerticalChain() && !widget.isInVirtualLayout()) {
                    if (bothConnected && !widget.isInVerticalChain()) {
                        solveVerticalMatchConstraint(layout, measurer, widget);
                    }
                }
            }
        }

        ConstraintAnchor baseline = layout.getAnchor(ConstraintAnchor.Type.BASELINE);
        if (baseline.getDependents() != null && baseline.hasFinalValue()) {
            int baselineValue = baseline.getFinalValue();
            for (ConstraintAnchor first : baseline.getDependents()) {
                ConstraintWidget widget = first.mOwner;
                boolean canMeasure = canMeasure(widget);
                if (widget.isMeasureRequested() && canMeasure) {
                    BasicMeasure.Measure measure = new BasicMeasure.Measure();
                    ConstraintWidgetContainer.measure(widget, measurer, measure, false);
                }
                if (widget.getVerticalDimensionBehaviour() != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
                    || canMeasure) {
                    if (widget.isMeasureRequested()) {
                        // Widget needs to be measured
                        if (DEBUG) {
                            System.out.println("(B) We didn't measure " + widget.getDebugName() + ", le'ts bail");
                        }
                        continue;
                    }
                    if (first == widget.mBaseline) {
                        widget.setFinalBaseline(baselineValue);
                        verticalSolvingPass(widget, measurer);
                    }
                }
            }
        }

    }

    /**
     * Solve horizontal centering constraints
     *
     * @param measurer
     * @param widget
     */
    private static void solveHorizontalCenterConstraints(BasicMeasure.Measurer measurer, ConstraintWidget widget) {
        int x1;
        int x2;
        float bias = widget.getHorizontalBiasPercent();
        int start = widget.mLeft.mTarget.getFinalValue();
        int end = widget.mRight.mTarget.getFinalValue();
        int s1 = start + widget.mLeft.getMargin();
        int s2 = end - widget.mRight.getMargin();
        if (start == end) {
            bias = 0.5f;
            s1 = start;
            s2 = end;
        }
        int width = widget.getWidth();
        int distance = s2 - s1 - width;
        if (s1 > s2) {
            distance = s1 - s2 - width;
        }
        if (((ConstraintWidgetContainer) widget.getParent()).isRtl()) {
            bias = 1 - bias;
        }
        int d1 = (int) (0.5f + bias * distance);
        x1 = s1 + d1;
        x2 = x1 + width;
        if (s1 > s2) {
            x1 = s1 + d1;
            x2 = x1 - width;
        }
        widget.setFinalHorizontal(x1, x2);
        horizontalSolvingPass(widget, measurer);
    }

    /**
     * Solve vertical centering constraints
     *
     * @param measurer
     * @param widget
     */
    private static void solveVerticalCenterConstraints(BasicMeasure.Measurer measurer, ConstraintWidget widget) {
        int y1;
        int y2;
        float bias = widget.getVerticalBiasPercent();
        int start = widget.mTop.mTarget.getFinalValue();
        int end = widget.mBottom.mTarget.getFinalValue();
        int s1 = start + widget.mTop.getMargin();
        int s2 = end - widget.mBottom.getMargin();
        if (start == end) {
            bias = 0.5f;
            s1 = start;
            s2 = end;
        }
        int height = widget.getHeight();
        int distance = s2 - s1 - height;
        if (s1 > s2) {
            distance = s1 - s2 - height;
        }
        int d1 = (int) (0.5f + bias * distance);
        y1 = s1 + d1;
        y2 = y1 + height;
        if (s1 > s2) {
            y1 = s1 - d1;
            y2 = y1 - height;
        }
        widget.setFinalVertical(y1, y2);
        verticalSolvingPass(widget, measurer);
    }

    /**
     * Solve horizontal match constraints
     *
     * @param measurer
     * @param widget
     */
    private static void solveHorizontalMatchConstraint(ConstraintWidget layout, BasicMeasure.Measurer measurer, ConstraintWidget widget) {
        int x1;
        int x2;
        float bias = widget.getHorizontalBiasPercent();
        int s1 = widget.mLeft.mTarget.getFinalValue() + widget.mLeft.getMargin();
        int s2 = widget.mRight.mTarget.getFinalValue() - widget.mRight.getMargin();
        if (s2 >= s1) {
            int width = widget.getWidth();
            if (widget.getVisibility() != ConstraintWidget.GONE) {
                if (widget.mMatchConstraintDefaultWidth == ConstraintWidget.MATCH_CONSTRAINT_PERCENT) {
                    int parentWidth = 0;
                    if (layout instanceof ConstraintWidgetContainer) {
                        parentWidth = layout.getWidth();
                    } else {
                        parentWidth = layout.getParent().getWidth();
                    }
                    width = (int) (0.5f * widget.getHorizontalBiasPercent() * parentWidth);
                } else if (widget.mMatchConstraintDefaultWidth == ConstraintWidget.MATCH_CONSTRAINT_SPREAD) {
                    width = s2 - s1;
                }
                width = Math.max(widget.mMatchConstraintMinWidth, width);
                if (widget.mMatchConstraintMaxWidth > 0) {
                    width = Math.min(widget.mMatchConstraintMaxWidth, width);
                }
            }
            int distance = s2 - s1 - width;
            int d1 = (int) (0.5f + bias * distance);
            x1 = s1 + d1;
            x2 = x1 + width;
            widget.setFinalHorizontal(x1, x2);
            horizontalSolvingPass(widget, measurer);
        }
    }

    /**
     * Solve vertical match constraints
     *
     * @param measurer
     * @param widget
     */
    private static void solveVerticalMatchConstraint(ConstraintWidget layout, BasicMeasure.Measurer measurer, ConstraintWidget widget) {
        int y1;
        int y2;
        float bias = widget.getVerticalBiasPercent();
        int s1 = widget.mTop.mTarget.getFinalValue() + widget.mTop.getMargin();
        int s2 = widget.mBottom.mTarget.getFinalValue() - widget.mBottom.getMargin();
        if (s2 >= s1) {
            int height = widget.getHeight();
            if (widget.getVisibility() != ConstraintWidget.GONE) {
                if (widget.mMatchConstraintDefaultHeight == ConstraintWidget.MATCH_CONSTRAINT_PERCENT) {
                    int parentHeight = 0;
                    if (layout instanceof ConstraintWidgetContainer) {
                        parentHeight = layout.getHeight();
                    } else {
                        parentHeight = layout.getParent().getHeight();
                    }
                    height = (int) (0.5f * bias * parentHeight);
                } else if (widget.mMatchConstraintDefaultHeight == ConstraintWidget.MATCH_CONSTRAINT_SPREAD) {
                    height = s2 - s1;
                }
                height = Math.max(widget.mMatchConstraintMinHeight, height);
                if (widget.mMatchConstraintMaxHeight > 0) {
                    height = Math.min(widget.mMatchConstraintMaxHeight, height);
                }
            }
            int distance = s2 - s1 - height;
            int d1 = (int) (0.5f + bias * distance);
            y1 = s1 + d1;
            y2 = y1 + height;
            widget.setFinalVertical(y1, y2);
            verticalSolvingPass(widget, measurer);
        }
    }

    /**
     * Returns true if the dimensions of the given widget are computable directly
     *
     * @param layout the widget to check
     * @return true if both dimensions are knowable by a single measure pass
     */
    private static boolean canMeasure(ConstraintWidget layout) {
        ConstraintWidget.DimensionBehaviour horizontalBehaviour = layout.getHorizontalDimensionBehaviour();
        ConstraintWidget.DimensionBehaviour verticalBehaviour = layout.getVerticalDimensionBehaviour();
        ConstraintWidgetContainer parent = layout.getParent() != null ? (ConstraintWidgetContainer) layout.getParent() : null;
        boolean isParentHorizontalFixed = parent != null && parent.getHorizontalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.FIXED;
        boolean isParentVerticalFixed = parent != null && parent.getVerticalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.FIXED;
        boolean isHorizontalFixed = horizontalBehaviour == ConstraintWidget.DimensionBehaviour.FIXED
                || (APPLY_MATCH_PARENT && horizontalBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_PARENT && isParentHorizontalFixed)
                || horizontalBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT
                || (horizontalBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
                        && layout.mMatchConstraintDefaultWidth == ConstraintWidget.MATCH_CONSTRAINT_SPREAD
                        && layout.mDimensionRatio == 0
                        && layout.hasDanglingDimension(HORIZONTAL))
                || layout.isResolvedHorizontally();
        boolean isVerticalFixed = verticalBehaviour == ConstraintWidget.DimensionBehaviour.FIXED
                || (APPLY_MATCH_PARENT && verticalBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_PARENT && isParentVerticalFixed)
                || verticalBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT
                || (verticalBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
                        && layout.mMatchConstraintDefaultHeight == ConstraintWidget.MATCH_CONSTRAINT_SPREAD
                        && layout.mDimensionRatio == 0
                        && layout.hasDanglingDimension(ConstraintWidget.VERTICAL))
                || layout.isResolvedVertically();
        if (layout.mDimensionRatio > 0 && (isHorizontalFixed || isVerticalFixed)) {
            return true;
        }
        if (false) {
            System.out.println("can measure " + layout.getDebugName() + " ? "
                    + isHorizontalFixed + " (" + horizontalBehaviour + ") x "
                    + isVerticalFixed + " (" + verticalBehaviour + ")");
        }
        return isHorizontalFixed && isVerticalFixed;
    }

    /**
     * Try to directly resolve the chain
     *
     * @param container
     * @param system
     * @param orientation
     * @param offset
     * @param chainHead
     * @param isChainSpread
     * @param isChainSpreadInside
     * @param isChainPacked
     * @return true if fully resolved
     */
    public static boolean solveChain(ConstraintWidgetContainer container, LinearSystem system,
                                     int orientation, int offset, ChainHead chainHead,
                                     boolean isChainSpread, boolean isChainSpreadInside,
                                     boolean isChainPacked) {
        if (LinearSystem.FULL_DEBUG) {
            System.out.println("\n### SOLVE CHAIN ###");
        }
        if (isChainPacked) {
            return false;
        }
        if (orientation == HORIZONTAL) {
            if (!container.isResolvedHorizontally()) {
                return false;
            }
        } else {
            if (!container.isResolvedVertically()) {
                return false;
            }
        }

        ConstraintWidget first = chainHead.getFirst();
        ConstraintWidget last = chainHead.getLast();
        ConstraintWidget firstVisibleWidget = chainHead.getFirstVisibleWidget();
        ConstraintWidget lastVisibleWidget = chainHead.getLastVisibleWidget();
        ConstraintWidget head = chainHead.getHead();

        ConstraintWidget widget = first;
        ConstraintWidget next = null;
        boolean done = false;

        ConstraintAnchor begin = first.mListAnchors[offset];
        ConstraintAnchor end = last.mListAnchors[offset + 1];
        if (begin.mTarget == null || end.mTarget == null) {
            return false;
        }
        if (!begin.mTarget.hasFinalValue() || !end.mTarget.hasFinalValue()) {
            return false;
        }

        if (firstVisibleWidget == null || lastVisibleWidget == null) {
            return false;
        }

        int startPoint = begin.mTarget.getFinalValue() + firstVisibleWidget.mListAnchors[offset].getMargin();
        int endPoint = end.mTarget.getFinalValue() - lastVisibleWidget.mListAnchors[offset+1].getMargin();

        int distance = endPoint - startPoint;
        if (distance <= 0) {
            return false;
        }
        int totalSize = 0;
        BasicMeasure.Measure measure = new BasicMeasure.Measure();

        int numWidgets = 0;
        int numVisibleWidgets = 0;

        while (!done) {
            begin = widget.mListAnchors[offset];
            boolean canMeasure = canMeasure(widget);
            if (!canMeasure) {
                return false;
            }
            if (widget.mListDimensionBehaviors[orientation] == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                return false;
            }

            if (widget.isMeasureRequested()) {
                ConstraintWidgetContainer.measure(widget, container.getMeasurer(), measure, false);
            }

            totalSize += widget.mListAnchors[offset].getMargin();
            if (orientation == HORIZONTAL) {
                totalSize += + widget.getWidth();
            } else {
                totalSize += widget.getHeight();
            }
            totalSize += widget.mListAnchors[offset+1].getMargin();

            numWidgets++;
            if (widget.getVisibility() != ConstraintWidget.GONE) {
                numVisibleWidgets++;
            }


            // go to the next widget
            ConstraintAnchor nextAnchor = widget.mListAnchors[offset + 1].mTarget;
            if (nextAnchor != null) {
                next = nextAnchor.mOwner;
                if (next.mListAnchors[offset].mTarget == null
                        || next.mListAnchors[offset].mTarget.mOwner != widget) {
                    next = null;
                }
            } else {
                next = null;
            }
            if (next != null) {
                widget = next;
            } else {
                done = true;
            }
        }

        if (numVisibleWidgets == 0) {
            return false;
        }

        if (numVisibleWidgets != numWidgets) {
            return false;
        }

        if (distance < totalSize) {
            return false;
        }

        int gap = distance - totalSize;
        if (isChainSpread) {
            gap = gap / (numVisibleWidgets + 1);
        } else if (isChainSpreadInside) {
            if (numVisibleWidgets > 2) {
                gap = gap / numVisibleWidgets - 1;
            }
        }

        if (numVisibleWidgets == 1) {
            float bias = 0.5f;
            if (orientation == ConstraintWidget.HORIZONTAL) {
                bias = head.getHorizontalBiasPercent();
            } else {
                bias = head.getVerticalBiasPercent();
            }
            int p1 = (int) (0.5f + startPoint + gap * bias);
            if (orientation == HORIZONTAL) {
                firstVisibleWidget.setFinalHorizontal(p1, p1 + firstVisibleWidget.getWidth());
            } else {
                firstVisibleWidget.setFinalVertical(p1, p1 + firstVisibleWidget.getHeight());
            }
            Direct.horizontalSolvingPass(firstVisibleWidget, container.getMeasurer());
            return true;
        }

        if (isChainSpread) {
            done = false;

            int current = startPoint + gap;
            widget = first;
            while (!done) {
                begin = widget.mListAnchors[offset];
                if (widget.getVisibility() == GONE) {
                    if (orientation == HORIZONTAL) {
                        widget.setFinalHorizontal(current, current);
                        Direct.horizontalSolvingPass(widget, container.getMeasurer());
                    } else {
                        widget.setFinalVertical(current, current);
                        Direct.verticalSolvingPass(widget, container.getMeasurer());
                    }
                } else {
                    current += widget.mListAnchors[offset].getMargin();
                    if (orientation == HORIZONTAL) {
                        widget.setFinalHorizontal(current, current + widget.getWidth());
                        Direct.horizontalSolvingPass(widget, container.getMeasurer());
                        current += widget.getWidth();
                    } else {
                        widget.setFinalVertical(current, current + widget.getHeight());
                        Direct.verticalSolvingPass(widget, container.getMeasurer());
                        current += widget.getHeight();
                    }
                    current += widget.mListAnchors[offset+1].getMargin();
                    current += gap;
                }

                widget.addToSolver(system, false);

                // go to the next widget
                ConstraintAnchor nextAnchor = widget.mListAnchors[offset + 1].mTarget;
                if (nextAnchor != null) {
                    next = nextAnchor.mOwner;
                    if (next.mListAnchors[offset].mTarget == null
                            || next.mListAnchors[offset].mTarget.mOwner != widget) {
                        next = null;
                    }
                } else {
                    next = null;
                }
                if (next != null) {
                    widget = next;
                } else {
                    done = true;
                }
            }
        } else if (isChainSpreadInside) {
            if (numVisibleWidgets == 2) {
                if (orientation == HORIZONTAL) {
                    firstVisibleWidget.setFinalHorizontal(startPoint, startPoint + firstVisibleWidget.getWidth());
                    lastVisibleWidget.setFinalHorizontal(endPoint - lastVisibleWidget.getWidth(), endPoint);
                    Direct.horizontalSolvingPass(firstVisibleWidget, container.getMeasurer());
                    Direct.horizontalSolvingPass(lastVisibleWidget, container.getMeasurer());
                } else {
                    firstVisibleWidget.setFinalVertical(startPoint, startPoint + firstVisibleWidget.getHeight());
                    lastVisibleWidget.setFinalVertical(endPoint - lastVisibleWidget.getHeight(), endPoint);
                    Direct.verticalSolvingPass(firstVisibleWidget, container.getMeasurer());
                    Direct.verticalSolvingPass(lastVisibleWidget, container.getMeasurer());
                }
                return true;
            }
            return false;
        }
        return true;
    }
}
