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
package androidx.constraintlayout.core;

import androidx.constraintlayout.core.widgets.ConstraintAnchor;
import androidx.constraintlayout.core.widgets.ConstraintWidget;
import androidx.constraintlayout.core.widgets.ConstraintWidgetContainer;
import androidx.constraintlayout.core.widgets.Guideline;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class MatchConstraintTest {

    @Test
    public void testSimpleMinMatch() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 800, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_SPREAD, 150, 200, 1);
        root.add(A);
        root.add(B);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.layout();
        System.out.println("a) root: " + root + " A: " + A + " B: " + B);
        assertEquals(A.getWidth(), 150);
        assertEquals(B.getWidth(), 100);
        assertEquals(root.getWidth(), 150);
        B.setWidth(200);
        root.setWidth(0);
        root.layout();
        System.out.println("b) root: " + root + " A: " + A + " B: " + B);
        assertEquals(A.getWidth(), 200);
        assertEquals(B.getWidth(), 200);
        assertEquals(root.getWidth(), 200);
        B.setWidth(300);
        root.setWidth(0);
        root.layout();
        System.out.println("c) root: " + root + " A: " + A + " B: " + B);
        assertEquals(A.getWidth(), 200);
        assertEquals(B.getWidth(), 300);
        assertEquals(root.getWidth(), 300);
    }

    @Test
    public void testMinMaxMatch() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 800, 600);
        Guideline guidelineA = new Guideline();
        guidelineA.setOrientation(Guideline.VERTICAL);
        guidelineA.setGuideBegin(100);
        Guideline guidelineB = new Guideline();
        guidelineB.setOrientation(Guideline.VERTICAL);
        guidelineB.setGuideEnd(100);
        root.add(guidelineA);
        root.add(guidelineB);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        A.connect(ConstraintAnchor.Type.LEFT, guidelineA, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, guidelineB, ConstraintAnchor.Type.RIGHT);
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_SPREAD, 150, 200, 1);
        root.add(A);
        root.setDebugName("root");
        guidelineA.setDebugName("guideline A");
        guidelineB.setDebugName("guideline B");
        A.setDebugName("A");
        root.layout();
        System.out.println("a) root: " + root + " guideA: " + guidelineA + " A: " + A + " guideB: " + guidelineB);
        assertEquals(root.getWidth(), 800);
        assertEquals(A.getWidth(), 200);
        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        A.setWidth(100);
        root.layout();
        System.out.println("b) root: " + root + " guideA: " + guidelineA + " A: " + A + " guideB: " + guidelineB);
        assertEquals(root.getWidth(), 350);
        assertEquals(A.getWidth(), 150);

        A.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_WRAP, 150, 200, 1);
        root.layout();
        System.out.println("c) root: " + root + " guideA: " + guidelineA + " A: " + A + " guideB: " + guidelineB);
        assertEquals(root.getWidth(), 350);
        assertEquals(A.getWidth(), 150);
        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        root.setWidth(800);
        root.layout();
        System.out.println("d) root: " + root + " guideA: " + guidelineA + " A: " + A + " guideB: " + guidelineB);
        assertEquals(root.getWidth(), 800);
        assertEquals(A.getWidth(), 150); // because it's wrap
        A.setWidth(250);
        root.layout();
        System.out.println("e) root: " + root + " guideA: " + guidelineA + " A: " + A + " guideB: " + guidelineB);
        assertEquals(root.getWidth(), 800);
        assertEquals(A.getWidth(), 200);

        A.setWidth(700);
        A.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_SPREAD, 150, 0, 1);
        root.layout();
        System.out.println("f) root: " + root + " guideA: " + guidelineA + " A: " + A + " guideB: " + guidelineB);
        assertEquals(root.getWidth(), 800);
        assertEquals(A.getWidth(), 600);
        A.setWidth(700);
        A.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_WRAP, 150, 0, 1);
        root.layout();
        System.out.println("g) root: " + root + " guideA: " + guidelineA + " A: " + A + " guideB: " + guidelineB);
        assertEquals(root.getWidth(), 800);
        assertEquals(A.getWidth(), 600);

        A.setWidth(700);
        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.setWidth(0);
        root.layout();
        System.out.println("h) root: " + root + " guideA: " + guidelineA + " A: " + A + " guideB: " + guidelineB);
        assertEquals(root.getWidth(), 900);
        assertEquals(A.getWidth(), 700);
        A.setWidth(700);
        A.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_SPREAD, 150, 0, 1);
        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.layout();
        assertEquals(root.getWidth(), 350);
        assertEquals(A.getWidth(), 150);
        System.out.println("i) root: " + root + " guideA: " + guidelineA + " A: " + A + " guideB: " + guidelineB);
    }

    @Test
    public void testSimpleHorizontalMatch() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 800, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);

        root.setDebugSolverName(root.getSystem(), "root");
        A.setDebugSolverName(root.getSystem(), "A");
        B.setDebugSolverName(root.getSystem(), "B");
        C.setDebugSolverName(root.getSystem(), "C");

        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 0);
        B.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, 0);
        C.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT, 0);
        C.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT, 0);

        root.add(A);
        root.add(B);
        root.add(C);

        root.layout();
        System.out.println("a) A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getWidth(), 100);
        assertEquals(B.getWidth(), 100);
        assertEquals(C.getWidth(), 100);
        assertTrue(C.getLeft() >= A.getRight());
        assertTrue(C.getRight() <= B.getLeft());
        assertEquals(C.getLeft() - A.getRight(), B.getLeft() - C.getRight());

        C.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        root.layout();
        System.out.println("b) A: " + A + " B: " + B + " C: " + C);

        assertEquals(A.getWidth(), 100);
        assertEquals(B.getWidth(), 100);
        assertEquals(C.getWidth(), 600);
        assertTrue(C.getLeft() >= A.getRight());
        assertTrue(C.getRight() <= B.getLeft());
        assertEquals(C.getLeft() - A.getRight(), B.getLeft() - C.getRight());

        C.setWidth(144);
        C.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_WRAP, 0, 0, 0);
        root.layout();
        System.out.println("c) A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getWidth(), 100);
        assertEquals(B.getWidth(), 100);
        assertEquals(C.getWidth(), 144);
        assertTrue(C.getLeft() >= A.getRight());
        assertTrue(C.getRight() <= B.getLeft());
        assertEquals(C.getLeft() - A.getRight(), B.getLeft() - C.getRight());

        C.setWidth(1000);
        C.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_WRAP, 0, 0, 0);
        root.layout();
        System.out.println("d) A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getWidth(), 100);
        assertEquals(B.getWidth(), 100);
        assertEquals(C.getWidth(), 600);
        assertTrue(C.getLeft() >= A.getRight());
        assertTrue(C.getRight() <= B.getLeft());
        assertEquals(C.getLeft() - A.getRight(), B.getLeft() - C.getRight());
    }


    @Test
    public void testDanglingRatio() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1000, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        root.add(A);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_WRAP, 0, 0, 0);
        root.layout();
        System.out.println("a) root: " + root + " A: " + A);
    }
}
