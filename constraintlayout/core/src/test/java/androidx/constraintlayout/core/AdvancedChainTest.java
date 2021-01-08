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
import androidx.constraintlayout.core.widgets.ConstraintAnchor.Type;
import androidx.constraintlayout.core.widgets.ConstraintWidget;
import androidx.constraintlayout.core.widgets.ConstraintWidget.DimensionBehaviour;
import androidx.constraintlayout.core.widgets.ConstraintWidgetContainer;
import androidx.constraintlayout.core.widgets.Optimizer;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static org.testng.Assert.assertEquals;

public class AdvancedChainTest {

    @Test
    public void testComplexChainWeights() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 800, 800);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);

        root.setDebugSolverName(root.getSystem(), "root");
        A.setDebugSolverName(root.getSystem(), "A");
        B.setDebugSolverName(root.getSystem(), "B");

        A.connect(Type.LEFT, root, Type.LEFT);
        A.connect(Type.RIGHT, root, Type.RIGHT);
        B.connect(Type.LEFT, root, Type.LEFT);
        B.connect(Type.RIGHT, root, Type.RIGHT);

        A.connect(Type.TOP, root, Type.TOP, 0);
        A.connect(Type.BOTTOM, B, Type.TOP, 0);

        B.connect(Type.TOP, A, Type.BOTTOM, 0);
        B.connect(Type.BOTTOM, root, Type.BOTTOM, 0);

        root.add(A);
        root.add(B);

        A.setHorizontalDimensionBehaviour(DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalDimensionBehaviour(DimensionBehaviour.MATCH_CONSTRAINT);
        B.setHorizontalDimensionBehaviour(DimensionBehaviour.MATCH_CONSTRAINT);
        B.setVerticalDimensionBehaviour(DimensionBehaviour.MATCH_CONSTRAINT);

        root.setOptimizationLevel(Optimizer.OPTIMIZATION_NONE);
        root.layout();

        System.out.println("root: " + root);
        System.out.println("A: " + A);
        System.out.println("B: " + B);

        assertEquals(A.getWidth(), 800);
        assertEquals(B.getWidth(), 800);
        assertEquals(A.getHeight(), 400);
        assertEquals(B.getHeight(), 400);
        assertEquals(A.getTop(), 0);
        assertEquals(B.getTop(), 400);

        A.setDimensionRatio("16:3");

        root.layout();

        System.out.println("root: " + root);
        System.out.println("A: " + A);
        System.out.println("B: " + B);

        assertEquals(A.getWidth(), 800);
        assertEquals(B.getWidth(), 800);
        assertEquals(A.getHeight(), 150);
        assertEquals(B.getHeight(), 150);
        assertEquals(A.getTop(), 167);
        assertEquals(B.getTop(), 483);

        B.setVerticalWeight(1);

        root.layout();

        System.out.println("root: " + root);
        System.out.println("A: " + A);
        System.out.println("B: " + B);

        assertEquals(A.getWidth(), 800);
        assertEquals(B.getWidth(), 800);
        assertEquals(A.getHeight(), 150);
        assertEquals(B.getHeight(), 650);
        assertEquals(A.getTop(), 0);
        assertEquals(B.getTop(), 150);

        A.setVerticalWeight(1);

        root.layout();

        System.out.println("root: " + root);
        System.out.println("A: " + A);
        System.out.println("B: " + B);

        assertEquals(A.getWidth(), 800);
        assertEquals(B.getWidth(), 800);
        assertEquals(A.getHeight(), 150);
        assertEquals(B.getHeight(), 150);
        assertEquals(A.getTop(), 167);
        assertEquals(B.getTop(), 483);
    }

    @Test
    public void testTooSmall() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 800, 800);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);

        root.setDebugSolverName(root.getSystem(), "root");
        A.setDebugSolverName(root.getSystem(), "A");
        B.setDebugSolverName(root.getSystem(), "B");
        C.setDebugSolverName(root.getSystem(), "C");

        root.add(A);
        root.add(B);
        root.add(C);

        A.connect(Type.LEFT, root, Type.LEFT);
        A.connect(Type.TOP, root, Type.TOP);
        A.connect(Type.BOTTOM, root, Type.BOTTOM);

        B.connect(Type.LEFT, A, Type.RIGHT, 100);
        C.connect(Type.LEFT, A, Type.RIGHT, 100);

        B.connect(Type.TOP, A, Type.TOP);
        B.connect(Type.BOTTOM, C, Type.TOP);
        C.connect(Type.TOP, B, Type.BOTTOM);
        C.connect(Type.BOTTOM, A, Type.BOTTOM);

        root.setOptimizationLevel(Optimizer.OPTIMIZATION_NONE);
        root.layout();

        System.out.println("A: " + A);
        System.out.println("B: " + B);
        System.out.println("C: " + C);
        assertEquals(A.getTop(), 390);
        assertEquals(B.getTop(), 380);
        assertEquals(C.getTop(), 400);
    }

    @Test
    public void testChainWeights() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 800, 800);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);

        root.setDebugSolverName(root.getSystem(), "root");
        A.setDebugSolverName(root.getSystem(), "A");
        B.setDebugSolverName(root.getSystem(), "B");

        A.connect(Type.LEFT, root, Type.LEFT, 0);
        A.connect(Type.RIGHT, B, Type.LEFT, 0);

        B.connect(Type.LEFT, A, Type.RIGHT, 0);
        B.connect(Type.RIGHT, root, Type.RIGHT, 0);

        root.add(A);
        root.add(B);

        A.setHorizontalDimensionBehaviour(DimensionBehaviour.MATCH_CONSTRAINT);
        B.setHorizontalDimensionBehaviour(DimensionBehaviour.MATCH_CONSTRAINT);
        A.setHorizontalWeight(1);
        B.setHorizontalWeight(0);

        root.setOptimizationLevel(Optimizer.OPTIMIZATION_NONE);
        root.layout();

        System.out.println("A: " + A);
        System.out.println("B: " + B);
        assertEquals(A.getWidth(), 800, 1);
        assertEquals(B.getWidth(), 0, 1);
        assertEquals(A.getLeft(), 0, 1);
        assertEquals(B.getLeft(), 800, 1);
    }

    @Test
    public void testChain3Weights() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 800, 800);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);

        root.setDebugSolverName(root.getSystem(), "root");
        A.setDebugSolverName(root.getSystem(), "A");
        B.setDebugSolverName(root.getSystem(), "B");
        C.setDebugSolverName(root.getSystem(), "C");

        A.connect(Type.LEFT, root, Type.LEFT, 0);
        A.connect(Type.RIGHT, B, Type.LEFT, 0);

        B.connect(Type.LEFT, A, Type.RIGHT, 0);
        B.connect(Type.RIGHT, C, Type.LEFT, 0);

        C.connect(Type.LEFT, B, Type.RIGHT, 0);
        C.connect(Type.RIGHT, root, Type.RIGHT, 0);

        root.add(A);
        root.add(B);
        root.add(C);

        A.setHorizontalDimensionBehaviour(DimensionBehaviour.MATCH_CONSTRAINT);
        B.setHorizontalDimensionBehaviour(DimensionBehaviour.MATCH_CONSTRAINT);
        C.setHorizontalDimensionBehaviour(DimensionBehaviour.MATCH_CONSTRAINT);

        A.setHorizontalWeight(1);
        B.setHorizontalWeight(0);
        C.setHorizontalWeight(1);

        root.setOptimizationLevel(Optimizer.OPTIMIZATION_NONE);
        root.layout();

        System.out.println("A: " + A);
        System.out.println("B: " + B);
        System.out.println("C: " + C);

        assertEquals(A.getWidth(), 400);
        assertEquals(B.getWidth(), 0);
        assertEquals(C.getWidth(), 400);
        assertEquals(A.getLeft(), 0);
        assertEquals(B.getLeft(), 400);
        assertEquals(C.getLeft(), 400);
    }

    @Test
    public void testChainLastGone() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 800, 800);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        ConstraintWidget D = new ConstraintWidget(100, 20);
        root.setDebugSolverName(root.getSystem(), "root");
        A.setDebugSolverName(root.getSystem(), "A");
        B.setDebugSolverName(root.getSystem(), "B");
        C.setDebugSolverName(root.getSystem(), "C");
        D.setDebugSolverName(root.getSystem(), "D");
        root.add(A);
        root.add(B);
        root.add(C);
        root.add(D);

        A.connect(Type.LEFT, root, Type.LEFT, 0);
        A.connect(Type.RIGHT, root, Type.RIGHT, 0);

        B.connect(Type.LEFT, root, Type.LEFT, 0);
        B.connect(Type.RIGHT, root, Type.RIGHT, 0);

        C.connect(Type.LEFT, root, Type.LEFT, 0);
        C.connect(Type.RIGHT, root, Type.RIGHT, 0);

        D.connect(Type.LEFT, root, Type.LEFT, 0);
        D.connect(Type.RIGHT, root, Type.RIGHT, 0);

        A.connect(Type.TOP, root, Type.TOP, 0);
        A.connect(Type.BOTTOM, B, Type.TOP, 0);
        B.connect(Type.TOP, A, Type.BOTTOM, 0);
        B.connect(Type.BOTTOM, C, Type.TOP, 0);
        C.connect(Type.TOP, B, Type.BOTTOM, 0);
        C.connect(Type.BOTTOM, D, Type.TOP, 0);
        D.connect(Type.TOP, C, Type.BOTTOM, 0);
        D.connect(Type.BOTTOM, root, Type.BOTTOM, 0);

        B.setVisibility(ConstraintWidget.GONE);
        D.setVisibility(ConstraintWidget.GONE);

        root.setOptimizationLevel(Optimizer.OPTIMIZATION_NONE);
        root.layout();

        System.out.println("A: " + A);
        System.out.println("B: " + B);
        System.out.println("C: " + C);
        System.out.println("D: " + D);

        assertEquals(A.getTop(), 253);
        assertEquals(C.getTop(), 527);
    }

    @Test
    public void testRatioChainGone() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 800, 800);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        ConstraintWidget ratio = new ConstraintWidget(100, 20);

        root.setDebugSolverName(root.getSystem(), "root");
        A.setDebugSolverName(root.getSystem(), "A");
        B.setDebugSolverName(root.getSystem(), "B");
        C.setDebugSolverName(root.getSystem(), "C");
        ratio.setDebugSolverName(root.getSystem(), "ratio");

        root.add(A);
        root.add(B);
        root.add(C);
        root.add(ratio);

        A.connect(Type.LEFT, root, Type.LEFT, 0);
        A.connect(Type.RIGHT, root, Type.RIGHT, 0);

        B.connect(Type.LEFT, root, Type.LEFT, 0);
        B.connect(Type.RIGHT, root, Type.RIGHT, 0);

        C.connect(Type.LEFT, root, Type.LEFT, 0);
        C.connect(Type.RIGHT, root, Type.RIGHT, 0);

        ratio.connect(Type.TOP, root, Type.TOP, 0);
        ratio.connect(Type.LEFT, root, Type.LEFT, 0);
        ratio.connect(Type.RIGHT, root, Type.RIGHT, 0);

        A.connect(Type.TOP, root, Type.TOP, 0);
        A.connect(Type.BOTTOM, B, Type.TOP, 0);
        B.connect(Type.TOP, A, Type.BOTTOM, 0);
        B.connect(Type.BOTTOM, ratio, Type.BOTTOM, 0);
        C.connect(Type.TOP, B, Type.TOP, 0);
        C.connect(Type.BOTTOM, ratio, Type.BOTTOM, 0);

        A.setHorizontalDimensionBehaviour(DimensionBehaviour.MATCH_CONSTRAINT);
        B.setHorizontalDimensionBehaviour(DimensionBehaviour.MATCH_CONSTRAINT);
        C.setHorizontalDimensionBehaviour(DimensionBehaviour.MATCH_CONSTRAINT);
        ratio.setHorizontalDimensionBehaviour(DimensionBehaviour.MATCH_CONSTRAINT);

        A.setVerticalDimensionBehaviour(DimensionBehaviour.MATCH_CONSTRAINT);
        B.setVerticalDimensionBehaviour(DimensionBehaviour.MATCH_CONSTRAINT);
        C.setVerticalDimensionBehaviour(DimensionBehaviour.MATCH_CONSTRAINT);
        ratio.setVerticalDimensionBehaviour(DimensionBehaviour.MATCH_CONSTRAINT);
        ratio.setDimensionRatio("4:3");

        B.setVisibility(ConstraintWidget.GONE);
        C.setVisibility(ConstraintWidget.GONE);

        root.setOptimizationLevel(Optimizer.OPTIMIZATION_NONE);
        root.layout();

        System.out.println("A: " + A);
        System.out.println("B: " + B);
        System.out.println("C: " + C);
        System.out.println("ratio: " + ratio);

        assertEquals(A.getHeight(), 600);

        root.setVerticalDimensionBehaviour(DimensionBehaviour.WRAP_CONTENT);

        root.layout();

        System.out.println("A: " + A);
        System.out.println("B: " + B);
        System.out.println("C: " + C);
        System.out.println("ratio: " + ratio);
        System.out.println("root: " + root);

        assertEquals(A.getHeight(), 600);
    }

    @Test
    public void testSimpleHorizontalChainPacked() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 800, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);

        root.setDebugSolverName(root.getSystem(), "root");
        A.setDebugSolverName(root.getSystem(), "A");
        B.setDebugSolverName(root.getSystem(), "B");
        ArrayList<ConstraintWidget> widgets = new ArrayList<>();
        widgets.add(A);
        widgets.add(B);
        widgets.add(root);
        root.add(A);
        root.add(B);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 20);
        B.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 20);

        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 0);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT, 0);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT, 0);
        B.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, 0);
        A.setHorizontalChainStyle(ConstraintWidget.CHAIN_PACKED);
        root.layout();
        System.out.println("a) A: " + A + " B: " + B );
        assertEquals(A.getLeft() - root.getLeft(), root.getRight() - B.getRight(), 1);
        assertEquals(B.getLeft() - A.getRight(), 0, 1);
    }

    @Test
    public void testSimpleVerticalTChainPacked() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 800, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);

        root.setDebugSolverName(root.getSystem(), "root");
        A.setDebugSolverName(root.getSystem(), "A");
        B.setDebugSolverName(root.getSystem(), "B");
        ArrayList<ConstraintWidget> widgets = new ArrayList<>();
        widgets.add(A);
        widgets.add(B);
        widgets.add(root);
        root.add(A);
        root.add(B);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 20);
        B.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 20);

        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 0);
        A.connect(ConstraintAnchor.Type.BOTTOM, B, ConstraintAnchor.Type.TOP, 0);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM, 0);
        B.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM, 0);
        A.setVerticalChainStyle(ConstraintWidget.CHAIN_PACKED);
        root.layout();
        System.out.println("a) A: " + A + " B: " + B );
        assertEquals(A.getTop() - root.getTop(), root.getBottom() - B.getBottom(), 1);
        assertEquals(B.getTop() - A.getBottom(), 0, 1);
    }

    @Test
    public void testHorizontalChainStyles() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 800, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        root.add(A);
        root.add(B);
        root.add(C);
        root.setDebugSolverName(root.getSystem(), "root");
        A.setDebugSolverName(root.getSystem(), "A");
        B.setDebugSolverName(root.getSystem(), "B");
        C.setDebugSolverName(root.getSystem(), "C");
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 0);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT, 0);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT, 0);
        B.connect(ConstraintAnchor.Type.RIGHT, C, ConstraintAnchor.Type.LEFT, 0);
        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.RIGHT, 0);
        C.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, 0);
        root.layout();
        System.out.println("       spread) root: " + root + " A: " + A + " B: " + B + " C: " + C);
        int gap = (root.getWidth() - A.getWidth() - B.getWidth() - C.getWidth()) / 4;
        int size = 100;
        assertEquals(A.getWidth(), size);
        assertEquals(B.getWidth(), size);
        assertEquals(C.getWidth(), size);
        assertEquals(gap, A.getLeft());
        assertEquals(A.getRight() + gap, B.getLeft());
        assertEquals(root.getWidth() - gap - C.getWidth(), C.getLeft());
        A.setHorizontalChainStyle(ConstraintWidget.CHAIN_SPREAD_INSIDE);
        root.layout();
        System.out.println("spread inside) root: " + root + " A: " + A + " B: " + B + " C: " + C);
        gap = (root.getWidth() - A.getWidth() - B.getWidth() - C.getWidth()) / 2;
        assertEquals(A.getWidth(), size);
        assertEquals(B.getWidth(), size);
        assertEquals(C.getWidth(), size);
        assertEquals(A.getLeft(), 0);
        assertEquals(A.getRight() + gap, B.getLeft());
        assertEquals(root.getWidth(), C.getRight());
        A.setHorizontalChainStyle(ConstraintWidget.CHAIN_PACKED);
        root.layout();
        System.out.println("       packed) root: " + root + " A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getWidth(), size);
        assertEquals(B.getWidth(), size);
        assertEquals(C.getWidth(), size);
        assertEquals(A.getLeft(), gap);
        assertEquals(root.getWidth() - gap, C.getRight());
    }

    @Test
    public void testVerticalChainStyles() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 800, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        root.add(A);
        root.add(B);
        root.add(C);
        root.setDebugSolverName(root.getSystem(), "root");
        A.setDebugSolverName(root.getSystem(), "A");
        B.setDebugSolverName(root.getSystem(), "B");
        C.setDebugSolverName(root.getSystem(), "C");
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 0);
        A.connect(ConstraintAnchor.Type.BOTTOM, B, ConstraintAnchor.Type.TOP, 0);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM, 0);
        B.connect(ConstraintAnchor.Type.BOTTOM, C, ConstraintAnchor.Type.TOP, 0);
        C.connect(ConstraintAnchor.Type.TOP, B, ConstraintAnchor.Type.BOTTOM, 0);
        C.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM, 0);
        root.layout();
        System.out.println("       spread) root: " + root + " A: " + A + " B: " + B + " C: " + C);
        int gap = (root.getHeight() - A.getHeight() - B.getHeight() - C.getHeight()) / 4;
        int size = 20;
        assertEquals(A.getHeight(), size);
        assertEquals(B.getHeight(), size);
        assertEquals(C.getHeight(), size);
        assertEquals(gap, A.getTop());
        assertEquals(A.getBottom() + gap, B.getTop());
        assertEquals(root.getHeight() - gap - C.getHeight(), C.getTop());
        A.setVerticalChainStyle(ConstraintWidget.CHAIN_SPREAD_INSIDE);
        root.layout();
        System.out.println("spread inside) root: " + root + " A: " + A + " B: " + B + " C: " + C);
        gap = (root.getHeight() - A.getHeight() - B.getHeight() - C.getHeight()) / 2;
        assertEquals(A.getHeight(), size);
        assertEquals(B.getHeight(), size);
        assertEquals(C.getHeight(), size);
        assertEquals(A.getTop(), 0);
        assertEquals(A.getBottom() + gap, B.getTop());
        assertEquals(root.getHeight(), C.getBottom());
        A.setVerticalChainStyle(ConstraintWidget.CHAIN_PACKED);
        root.layout();
        System.out.println("       packed) root: " + root + " A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getHeight(), size);
        assertEquals(B.getHeight(), size);
        assertEquals(C.getHeight(), size);
        assertEquals(A.getTop(), gap);
        assertEquals(root.getHeight() - gap, C.getBottom());
    }

    @Test
    public void testPacked() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 800, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        root.add(A);
        root.add(B);
        root.setDebugSolverName(root.getSystem(), "root");
        A.setDebugSolverName(root.getSystem(), "A");
        B.setDebugSolverName(root.getSystem(), "B");
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 0);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT, 0);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT, 0);
        B.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, 0);
        int gap = (root.getWidth() - A.getWidth() - B.getWidth()) / 2;
        int size = 100;
        A.setHorizontalChainStyle(ConstraintWidget.CHAIN_PACKED);
        root.layout();
        root.setOptimizationLevel(0);
        System.out.println("       packed) root: " + root + " A: " + A + " B: " + B);
        assertEquals(A.getWidth(), size);
        assertEquals(B.getWidth(), size);
        assertEquals(A.getLeft(), gap);
    }
}
