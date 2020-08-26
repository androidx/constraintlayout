/*
 * Copyright (C) 2016 The Android Open Source Project
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
package android.support.constraint.solver;

import android.support.constraint.solver.widgets.ConstraintAnchor;
import android.support.constraint.solver.widgets.ConstraintWidget;
import android.support.constraint.solver.widgets.ConstraintWidgetContainer;
import android.support.constraint.solver.widgets.Guideline;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Basic visibility behavior test in the solver
 */
public class VisibilityTest {

    @Test
    public void testGoneSingleConnection() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 800, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        root.setDebugSolverName(root.getSystem(), "root");
        A.setDebugSolverName(root.getSystem(), "A");
        B.setDebugSolverName(root.getSystem(), "B");

        int margin = 175;
        int goneMargin = 42;
        root.add(A);
        root.add(B);

        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, margin);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, margin);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT, margin);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM, margin);

        root.layout();
        System.out.println("a) A: " + A + " B: " + B);
        assertEquals(root.getWidth(), 800);
        assertEquals(root.getHeight(), 600);
        assertEquals(A.getWidth(), 100);
        assertEquals(A.getHeight(), 20);
        assertEquals(B.getWidth(), 100);
        assertEquals(B.getHeight(), 20);
        assertEquals(A.getLeft(), root.getLeft() + margin);
        assertEquals(A.getTop(), root.getTop() + margin);
        assertEquals(B.getLeft(), A.getRight() + margin);
        assertEquals(B.getTop(), A.getBottom() + margin);

        A.setVisibility(ConstraintWidget.GONE);
        root.layout();
        System.out.println("b) A: " + A + " B: " + B);
        assertEquals(root.getWidth(), 800);
        assertEquals(root.getHeight(), 600);
        assertEquals(A.getWidth(), 0);
        assertEquals(A.getHeight(), 0);
        assertEquals(B.getWidth(), 100);
        assertEquals(B.getHeight(), 20);
        assertEquals(A.getLeft(), root.getLeft());
        assertEquals(A.getTop(), root.getTop());
        assertEquals(B.getLeft(), A.getRight() + margin);
        assertEquals(B.getTop(), A.getBottom() + margin);

        B.setGoneMargin(ConstraintAnchor.Type.LEFT, goneMargin);
        B.setGoneMargin(ConstraintAnchor.Type.TOP, goneMargin);

        root.layout();
        System.out.println("c) A: " + A + " B: " + B);
        assertEquals(root.getWidth(), 800);
        assertEquals(root.getHeight(), 600);
        assertEquals(A.getWidth(), 0);
        assertEquals(A.getHeight(), 0);
        assertEquals(B.getWidth(), 100);
        assertEquals(B.getHeight(), 20);
        assertEquals(A.getLeft(), root.getLeft());
        assertEquals(A.getTop(), root.getTop());
        assertEquals(B.getLeft(), A.getRight() + goneMargin);
        assertEquals(B.getTop(), A.getBottom() + goneMargin);
    }

    @Test
    public void testGoneDualConnection() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 800, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        Guideline guideline = new Guideline();
        guideline.setGuidePercent(0.5f);
        guideline.setOrientation(ConstraintWidget.HORIZONTAL);
        root.setDebugSolverName(root.getSystem(), "root");
        A.setDebugSolverName(root.getSystem(), "A");
        B.setDebugSolverName(root.getSystem(), "B");

        root.add(A);
        root.add(B);
        root.add(guideline);

        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, guideline, ConstraintAnchor.Type.TOP);
        B.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM);
        B.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);

        root.layout();
        System.out.println("a) A: " + A + " B: " + B + " guideline " + guideline);
        assertEquals(root.getWidth(), 800);
        assertEquals(root.getHeight(), 600);
        assertEquals(A.getLeft(), root.getLeft());
        assertEquals(A.getRight(), root.getRight());
        assertEquals(B.getLeft(), root.getLeft());
        assertEquals(B.getRight(), root.getRight());
        assertEquals(guideline.getTop(), root.getHeight() / 2);
        assertEquals(A.getTop(), root.getTop());
        assertEquals(A.getBottom(), guideline.getTop());
        assertEquals(B.getTop(), A.getBottom());
        assertEquals(B.getBottom(), root.getBottom());

        A.setVisibility(ConstraintWidget.GONE);
        root.layout();
        System.out.println("b) A: " + A + " B: " + B + " guideline " + guideline);
        assertEquals(root.getWidth(), 800);
        assertEquals(root.getHeight(), 600);
        assertEquals(A.getWidth(), 0);
        assertEquals(A.getHeight(), 0);
        assertEquals(A.getLeft(), 400);
        assertEquals(A.getRight(), 400);
        assertEquals(B.getLeft(), root.getLeft());
        assertEquals(B.getRight(), root.getRight());
        assertEquals(guideline.getTop(), root.getHeight() / 2);
        assertEquals(A.getTop(), 150);
        assertEquals(A.getBottom(), 150);
        assertEquals(B.getTop(), 150);
        assertEquals(B.getBottom(), root.getBottom());
    }
}
