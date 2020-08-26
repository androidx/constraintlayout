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

import android.support.constraint.solver.widgets.*;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static org.testng.Assert.assertEquals;

public class ChainTest {

    @Test
    public void testCenteringElementsWithSpreadChain() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        ConstraintWidget D = new ConstraintWidget(100, 20);
        ConstraintWidget E = new ConstraintWidget(600, 20);

        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        D.setDebugName("D");
        E.setDebugName("E");
        root.add(A);
        root.add(B);
        root.add(C);
        root.add(D);
        root.add(E);

        A.connect(ConstraintAnchor.Type.LEFT, E, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.RIGHT, E, ConstraintAnchor.Type.RIGHT);

        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);

        C.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.LEFT);
        C.connect(ConstraintAnchor.Type.RIGHT, A, ConstraintAnchor.Type.RIGHT);
        C.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM);

        D.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.LEFT);
        D.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.RIGHT);
        D.connect(ConstraintAnchor.Type.TOP, B, ConstraintAnchor.Type.BOTTOM);

        root.layout();
        System.out.println("A: " + A + " B: " + B + " C: " + C + " D: " + D + " E: " + E);
        assertEquals(A.getWidth(), 300);
        assertEquals(B.getWidth(), A.getWidth());
    }

    @Test
    public void testBasicChainMatch() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        root.add(A);
        root.add(B);
        root.add(C);

        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.RIGHT, C, ConstraintAnchor.Type.LEFT);
        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.RIGHT);
        C.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        B.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        C.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);

        A.setHorizontalChainStyle(ConstraintWidget.CHAIN_SPREAD);
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        C.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setBaselineDistance(8);
        B.setBaselineDistance(8);
        C.setBaselineDistance(8);

        root.setOptimizationLevel(Optimizer.OPTIMIZATION_STANDARD | Optimizer.OPTIMIZATION_CHAIN);
        root.layout();
        System.out.println("A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getLeft(), 0);
        assertEquals(A.getRight(), 200);
        assertEquals(B.getLeft(), 200);
        assertEquals(B.getRight(), 400);
        assertEquals(C.getLeft(), 400);
        assertEquals(C.getRight(), 600);
    }

    @Test
    public void testSpreadChainGone() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        root.add(A);
        root.add(B);
        root.add(C);

        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.RIGHT, C, ConstraintAnchor.Type.LEFT);
        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.RIGHT);
        C.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);

        A.setHorizontalChainStyle(ConstraintWidget.CHAIN_SPREAD);
        A.setVisibility(ConstraintWidget.GONE);

        root.layout();
        System.out.println("A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getLeft(), 0);
        assertEquals(A.getRight(), 0);
        assertEquals(B.getLeft(), 133);
        assertEquals(B.getRight(), 233);
        assertEquals(C.getLeft(), 367);
        assertEquals(C.getRight(), 467);
    }

    @Test
    public void testPackChainGone() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        root.add(A);
        root.add(B);
        root.add(C);

        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 100);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.RIGHT, C, ConstraintAnchor.Type.LEFT);
        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.RIGHT);
        C.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, 20);

        A.setHorizontalChainStyle(ConstraintWidget.CHAIN_PACKED);
        B.setGoneMargin(ConstraintAnchor.Type.RIGHT, 100);
        C.setVisibility(ConstraintWidget.GONE);

        root.layout();
        System.out.println("A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getLeft(), 200);
        assertEquals(B.getLeft(), 300);
        assertEquals(C.getLeft(), 500);
        assertEquals(A.getWidth(), 100);
        assertEquals(B.getWidth(), 100);
        assertEquals(C.getWidth(), 0);
    }

    @Test
    public void testSpreadInsideChain2() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        root.add(A);
        root.add(B);
        root.add(C);

        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.RIGHT, C, ConstraintAnchor.Type.LEFT);
        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.RIGHT, 25);
        C.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);

        A.setHorizontalChainStyle(ConstraintWidget.CHAIN_SPREAD_INSIDE);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);

        root.layout();
        System.out.println("A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getLeft(), 0);
        assertEquals(A.getRight(), 100);
        assertEquals(B.getLeft(), 100);
        assertEquals(B.getRight(), 475);
        assertEquals(C.getLeft(), 500);
        assertEquals(C.getRight(), 600);
    }


    @Test
    public void testPackChain2() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        root.add(A);
        root.add(B);
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_NONE);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        A.setHorizontalChainStyle(ConstraintWidget.CHAIN_PACKED);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_WRAP, 0, 0, 1);
        root.layout();
        System.out.println("e) A: " + A + " B: " + B);
        assertEquals(A.getWidth(), 100);
        assertEquals(B.getWidth(), 100);
        assertEquals(A.getLeft(), root.getWidth() - B.getRight());
        assertEquals(B.getLeft(), A.getLeft() + A.getWidth());
        // e) A: id: A (200, 0) - (100 x 20) B: id: B (300, 0) - (100 x 20) - pass
        // e) A: id: A (0, 0) - (100 x 20) B: id: B (100, 0) - (100 x 20)
    }

    @Test
    public void testPackChain() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        root.add(A);
        root.add(B);
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_NONE);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        A.setHorizontalChainStyle(ConstraintWidget.CHAIN_PACKED);
        root.layout();
        System.out.println("a) A: " + A + " B: " + B);
        assertEquals(A.getWidth(), 100);
        assertEquals(B.getWidth(), 100);
        assertEquals(A.getLeft(), root.getWidth() - B.getRight());
        assertEquals(B.getLeft(), A.getLeft() + A.getWidth());
        A.setVisibility(ConstraintWidget.GONE);
        root.layout();
        System.out.println("b) A: " + A + " B: " + B);
        assertEquals(A.getWidth(), 0);
        assertEquals(B.getWidth(), 100);
        assertEquals(A.getLeft(), root.getWidth() - B.getRight());
        assertEquals(B.getLeft(), A.getLeft() + A.getWidth());
        B.setVisibility(ConstraintWidget.GONE);
        root.layout();
        System.out.println("c) A: " + A + " B: " + B);
        assertEquals(A.getWidth(), 0);
        assertEquals(B.getWidth(), 0);
        assertEquals(A.getLeft(), 0);
        assertEquals(B.getLeft(), A.getLeft() + A.getWidth());
        A.setVisibility(ConstraintWidget.VISIBLE);
        A.setWidth(100);
        root.layout();
        System.out.println("d) A: " + A + " B: " + B);
        assertEquals(A.getWidth(), 100);
        assertEquals(B.getWidth(), 0);
        assertEquals(A.getLeft(), root.getWidth() - B.getRight());
        assertEquals(B.getLeft(), A.getLeft() + A.getWidth());
        A.setVisibility(ConstraintWidget.VISIBLE);
        A.setWidth(100);
        A.setHeight(20);
        B.setVisibility(ConstraintWidget.VISIBLE);
        B.setWidth(100);
        B.setHeight(20);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_WRAP, 0, 0, 1);
        root.layout();
        System.out.println("e) A: " + A + " B: " + B);
        assertEquals(A.getWidth(), 100);
        assertEquals(B.getWidth(), 100);
        assertEquals(A.getLeft(), root.getWidth() - B.getRight());
        assertEquals(B.getLeft(), A.getLeft() + A.getWidth());
        B.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_SPREAD, 0, 0, 1);
        root.layout();
        System.out.println("f) A: " + A + " B: " + B);
        assertEquals(A.getWidth(), 100);
        assertEquals(B.getWidth(), 500);
        assertEquals(A.getLeft(), 0);
        assertEquals(B.getLeft(), 100);
        B.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_SPREAD, 0, 50, 1);
        root.layout();
        System.out.println("g) A: " + A + " B: " + B);
        assertEquals(A.getWidth(), 100);
        assertEquals(B.getWidth(), 50);
        assertEquals(A.getLeft(), root.getWidth() - B.getRight());
        assertEquals(B.getLeft(), A.getLeft() + A.getWidth());
        B.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_PERCENT, 0, 0, 0.3f);
        root.layout();
        System.out.println("h) A: " + A + " B: " + B);
        assertEquals(A.getWidth(), 100);
        assertEquals(B.getWidth(), (int) (0.3f * 600));
        assertEquals(A.getLeft(), root.getWidth() - B.getRight());
        assertEquals(B.getLeft(), A.getLeft() + A.getWidth());
        B.setDimensionRatio("16:9");
        B.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_RATIO, 0, 0, 1);
        root.layout();
        System.out.println("i) A: " + A + " B: " + B);
        assertEquals(A.getWidth(), 100);
        assertEquals(B.getWidth(), (int) (16f / 9f * 20), 1);
        assertEquals(A.getLeft(), root.getWidth() - B.getRight(), 1);
        assertEquals(B.getLeft(), A.getLeft() + A.getWidth());
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_SPREAD, 0, 0, 1);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_SPREAD, 0, 0, 1);
        B.setDimensionRatio(0, 0);
        A.setVisibility(ConstraintWidget.VISIBLE);
        A.setWidth(100);
        A.setHeight(20);
        B.setVisibility(ConstraintWidget.VISIBLE);
        B.setWidth(100);
        B.setHeight(20);
        root.layout();
        System.out.println("j) A: " + A + " B: " + B);
        assertEquals(A.getWidth(), B.getWidth());
        assertEquals(A.getWidth() + B.getWidth(), root.getWidth());
        A.setHorizontalWeight(1);
        B.setHorizontalWeight(3);
        root.layout();
        System.out.println("k) A: " + A + " B: " + B);
        assertEquals(A.getWidth() * 3, B.getWidth());
        assertEquals(A.getWidth() + B.getWidth(), root.getWidth());
    }

    /**
     * testPackChain with current Chain Optimizations.
     */
    @Test
    public void testPackChainOpt() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        root.add(A);
        root.add(B);
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_DIRECT | Optimizer.OPTIMIZATION_BARRIER
                | Optimizer.OPTIMIZATION_CHAIN);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        A.setHorizontalChainStyle(ConstraintWidget.CHAIN_PACKED);
        root.layout();
        System.out.println("a) A: " + A + " B: " + B);
        assertEquals(A.getWidth(), 100);
        assertEquals(B.getWidth(), 100);
        assertEquals(A.getLeft(), root.getWidth() - B.getRight());
        assertEquals(B.getLeft(), A.getLeft() + A.getWidth());
        A.setVisibility(ConstraintWidget.GONE);
        root.layout();
        System.out.println("b) A: " + A + " B: " + B);
        assertEquals(A.getWidth(), 0);
        assertEquals(B.getWidth(), 100);
        assertEquals(A.getLeft(), root.getWidth() - B.getRight());
        assertEquals(B.getLeft(), A.getLeft() + A.getWidth());
        B.setVisibility(ConstraintWidget.GONE);
        root.layout();
        System.out.println("c) A: " + A + " B: " + B);
        assertEquals(A.getWidth(), 0);
        assertEquals(B.getWidth(), 0);
        assertEquals(A.getLeft(), 0);
        assertEquals(B.getLeft(), A.getLeft() + A.getWidth());
        A.setVisibility(ConstraintWidget.VISIBLE);
        A.setWidth(100);
        root.layout();
        System.out.println("d) A: " + A + " B: " + B);
        assertEquals(A.getWidth(), 100);
        assertEquals(B.getWidth(), 0);
        assertEquals(A.getLeft(), root.getWidth() - B.getRight());
        assertEquals(B.getLeft(), A.getLeft() + A.getWidth());
        A.setVisibility(ConstraintWidget.VISIBLE);
        A.setWidth(100);
        A.setHeight(20);
        B.setVisibility(ConstraintWidget.VISIBLE);
        B.setWidth(100);
        B.setHeight(20);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_WRAP, 0, 0, 1);
        root.layout();
        System.out.println("e) A: " + A + " B: " + B);
        assertEquals(A.getWidth(), 100);
        assertEquals(B.getWidth(), 100);
        assertEquals(A.getLeft(), root.getWidth() - B.getRight());
        assertEquals(B.getLeft(), A.getLeft() + A.getWidth());
        B.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_SPREAD, 0, 0, 1);
        root.layout();
        System.out.println("f) A: " + A + " B: " + B);
        assertEquals(A.getWidth(), 100);
        assertEquals(B.getWidth(), 500);
        assertEquals(A.getLeft(), 0);
        assertEquals(B.getLeft(), 100);
        B.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_SPREAD, 0, 50, 1);
        root.layout();
        System.out.println("g) A: " + A + " B: " + B);
        assertEquals(A.getWidth(), 100);
        assertEquals(B.getWidth(), 50);
        assertEquals(A.getLeft(), root.getWidth() - B.getRight());
        assertEquals(B.getLeft(), A.getLeft() + A.getWidth());
        B.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_PERCENT, 0, 0, 0.3f);
        root.layout();
        System.out.println("h) A: " + A + " B: " + B);
        assertEquals(A.getWidth(), 100);
        assertEquals(B.getWidth(), (int) (0.3f * 600));
        assertEquals(A.getLeft(), root.getWidth() - B.getRight());
        assertEquals(B.getLeft(), A.getLeft() + A.getWidth());
        B.setDimensionRatio("16:9");
        B.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_RATIO, 0, 0, 1);
        root.layout();
        System.out.println("i) A: " + A + " B: " + B);
        assertEquals(A.getWidth(), 100);
        assertEquals(B.getWidth(), (int) (16f / 9f * 20), 1);
        assertEquals(A.getLeft(), root.getWidth() - B.getRight(), 1);
        assertEquals(B.getLeft(), A.getLeft() + A.getWidth());
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_SPREAD, 0, 0, 1);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_SPREAD, 0, 0, 1);
        B.setDimensionRatio(0, 0);
        A.setVisibility(ConstraintWidget.VISIBLE);
        A.setWidth(100);
        A.setHeight(20);
        B.setVisibility(ConstraintWidget.VISIBLE);
        B.setWidth(100);
        B.setHeight(20);
        root.layout();
        System.out.println("j) A: " + A + " B: " + B);
        assertEquals(A.getWidth(), B.getWidth());
        assertEquals(A.getWidth() + B.getWidth(), root.getWidth());
        A.setHorizontalWeight(1);
        B.setHorizontalWeight(3);
        root.layout();
        System.out.println("k) A: " + A + " B: " + B);
        assertEquals(A.getWidth() * 3, B.getWidth());
        assertEquals(A.getWidth() + B.getWidth(), root.getWidth());
    }

    @Test
    public void testSpreadChain() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        root.add(A);
        root.add(B);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        A.setHorizontalChainStyle(ConstraintWidget.CHAIN_SPREAD);
        root.layout();
        System.out.println("a) A: " + A + " B: " + B);
        assertEquals(A.getWidth(), 100);
        assertEquals(B.getWidth(), 100);
        assertEquals(A.getLeft(), B.getLeft() - A.getRight(), 1);
        assertEquals(B.getLeft() - A.getRight(), root.getWidth() - B.getRight(), 1);
        B.setVisibility(ConstraintWidget.GONE);
        root.layout();
        System.out.println("b) A: " + A + " B: " + B);
    }

    @Test
    public void testSpreadInsideChain() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        root.add(A);
        root.add(B);
        root.add(C);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        A.setHorizontalChainStyle(ConstraintWidget.CHAIN_SPREAD_INSIDE);
        root.layout();
        System.out.println("a) A: " + A + " B: " + B);
        assertEquals(A.getWidth(), 100);
        assertEquals(B.getWidth(), 100);
        assertEquals(B.getRight(), root.getWidth());

        B.reset();
        root.add(B);
        B.setDebugName("B");
        B.setWidth(100);
        B.setHeight(20);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.RIGHT, C, ConstraintAnchor.Type.LEFT);
        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.RIGHT);
        C.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        root.layout();
        System.out.println("b) A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getWidth(), 100);
        assertEquals(B.getWidth(), 100);
        assertEquals(C.getWidth(), 100);
        assertEquals(B.getLeft() - A.getRight(), C.getLeft() - B.getRight());
        int gap = (root.getWidth() - A.getWidth() - B.getWidth() - C.getWidth()) / 2;
        assertEquals(B.getLeft(), A.getRight() + gap);
    }

    @Test
    public void testBasicChain() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        ArrayList<ConstraintWidget> widgets = new ArrayList<ConstraintWidget>();
        widgets.add(A);
        widgets.add(B);
        widgets.add(root);
        root.add(A);
        root.add(B);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        root.layout();
        System.out.println("a) A: " + A + " B: " + B);
        assertEquals(A.getWidth(), B.getWidth(), 1);
        assertEquals(A.getLeft() - root.getLeft(), root.getRight() - B.getRight(), 1);
        assertEquals(A.getLeft() - root.getLeft(), B.getLeft() - A.getRight(), 1);
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        root.layout();
        System.out.println("b) A: " + A + " B: " + B);
        assertEquals(A.getWidth(), root.getWidth() - B.getWidth());
        assertEquals(B.getWidth(), 100);
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        A.setWidth(100);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        root.layout();
        System.out.println("a) A: " + A + " B: " + B);
        assertEquals(B.getWidth(), root.getWidth() - A.getWidth());
        assertEquals(A.getWidth(), 100);
    }

    @Test
    public void testBasicVerticalChain() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        A.setDebugName("A");
        B.setDebugName("B");
        ArrayList<ConstraintWidget> widgets = new ArrayList<ConstraintWidget>();
        widgets.add(A);
        widgets.add(B);
        widgets.add(root);
        root.add(A);
        root.add(B);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, B, ConstraintAnchor.Type.TOP);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM);
        B.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        root.layout();
        System.out.println("a) A: " + A + " B: " + B);
        assertEquals(A.getHeight(), B.getHeight(), 1);
        assertEquals(A.getTop() - root.getTop(), root.getBottom() - B.getBottom(), 1);
        assertEquals(A.getTop() - root.getTop(), B.getTop() - A.getBottom(), 1);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        root.layout();
        System.out.println("b) A: " + A + " B: " + B);
        assertEquals(A.getHeight(), root.getHeight() - B.getHeight());
        assertEquals(B.getHeight(), 20);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        A.setHeight(20);
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        root.layout();
        System.out.println("c) A: " + A + " B: " + B);
        assertEquals(B.getHeight(), root.getHeight() - A.getHeight());
        assertEquals(A.getHeight(), 20);
    }

    @Test
    public void testBasicChainThreeElements1() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 800, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        int marginL = 7;
        int marginR = 27;
        root.setDebugSolverName(root.getSystem(), "root");
        A.setDebugSolverName(root.getSystem(), "A");
        B.setDebugSolverName(root.getSystem(), "B");
        C.setDebugSolverName(root.getSystem(), "C");
        ArrayList<ConstraintWidget> widgets = new ArrayList<ConstraintWidget>();
        widgets.add(A);
        widgets.add(B);
        widgets.add(C);
        widgets.add(root);
        root.add(A);
        root.add(B);
        root.add(C);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 0);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT, 0);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT, 0);
        B.connect(ConstraintAnchor.Type.RIGHT, C, ConstraintAnchor.Type.LEFT, 0);
        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.RIGHT, 0);
        C.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, 0);
        root.layout();
        System.out.println("a) A: " + A + " B: " + B + " C: " + C);
        // all elements spread equally
        assertEquals(A.getWidth(), B.getWidth(), 1);
        assertEquals(B.getWidth(), C.getWidth(), 1);
        assertEquals(A.getLeft() - root.getLeft(), root.getRight() - C.getRight(), 1);
        assertEquals(A.getLeft() - root.getLeft(), B.getLeft() - A.getRight(), 1);
        assertEquals(B.getLeft() - A.getRight(), C.getLeft() - B.getRight(), 1);
        // a) A: id: A (125, 0) - (100 x 20) B: id: B (350, 0) - (100 x 20) C: id: C (575, 0) - (100 x 20)
        // a) A: id: A (0, 0) - (100 x 20) B: id: B (100, 0) - (100 x 20) C: id: C (450, 0) - (100 x 20)
    }

    @Test
    public void testBasicChainThreeElements() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 800, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        int marginL = 7;
        int marginR = 27;
        root.setDebugSolverName(root.getSystem(), "root");
        A.setDebugSolverName(root.getSystem(), "A");
        B.setDebugSolverName(root.getSystem(), "B");
        C.setDebugSolverName(root.getSystem(), "C");
        ArrayList<ConstraintWidget> widgets = new ArrayList<ConstraintWidget>();
        widgets.add(A);
        widgets.add(B);
        widgets.add(C);
        widgets.add(root);
        root.add(A);
        root.add(B);
        root.add(C);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 0);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT, 0);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT, 0);
        B.connect(ConstraintAnchor.Type.RIGHT, C, ConstraintAnchor.Type.LEFT, 0);
        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.RIGHT, 0);
        C.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, 0);
        root.layout();
        System.out.println("a) A: " + A + " B: " + B + " C: " + C);
        // all elements spread equally
        assertEquals(A.getWidth(), B.getWidth(), 1);
        assertEquals(B.getWidth(), C.getWidth(), 1);
        assertEquals(A.getLeft() - root.getLeft(), root.getRight() - C.getRight(), 1);
        assertEquals(A.getLeft() - root.getLeft(), B.getLeft() - A.getRight(), 1);
        assertEquals(B.getLeft() - A.getRight(), C.getLeft() - B.getRight(), 1);
        // A marked as 0dp, B == C, A takes the rest
        A.getAnchor(ConstraintAnchor.Type.LEFT).setMargin(marginL);
        A.getAnchor(ConstraintAnchor.Type.RIGHT).setMargin(marginR);
        B.getAnchor(ConstraintAnchor.Type.LEFT).setMargin(marginL);
        B.getAnchor(ConstraintAnchor.Type.RIGHT).setMargin(marginR);
        C.getAnchor(ConstraintAnchor.Type.LEFT).setMargin(marginL);
        C.getAnchor(ConstraintAnchor.Type.RIGHT).setMargin(marginR);
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        root.layout();
        System.out.println("b) A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getLeft() - root.getLeft() - marginL, root.getRight() - C.getRight() - marginR);
        assertEquals(C.getLeft() - B.getRight(), B.getLeft() - A.getRight());
        int matchWidth = root.getWidth() - B.getWidth() - C.getWidth() - marginL - marginR - 4 * (B.getLeft() - A.getRight());
        assertEquals(A.getWidth(), 498);
        assertEquals(B.getWidth(), C.getWidth());
        assertEquals(B.getWidth(), 100);
        checkPositions(A, B, C);
        // B marked as 0dp, A == C, B takes the rest
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        A.setWidth(100);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        root.layout();
        System.out.println("c) A: " + A + " B: " + B + " C: " + C);
        assertEquals(B.getWidth(), 498);
        assertEquals(A.getWidth(), C.getWidth());
        assertEquals(A.getWidth(), 100);
        checkPositions(A, B, C);
        // C marked as 0dp, A == B, C takes the rest
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        B.setWidth(100);
        C.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        root.layout();
        System.out.println("d) A: " + A + " B: " + B + " C: " + C);
        assertEquals(C.getWidth(), 498);
        assertEquals(A.getWidth(), B.getWidth());
        assertEquals(A.getWidth(), 100);
        checkPositions(A, B, C);
        // A & B marked as 0dp, C == 100
        C.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        C.setWidth(100);
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        root.layout();
        System.out.println("e) A: " + A + " B: " + B + " C: " + C);
        assertEquals(C.getWidth(), 100);
        assertEquals(A.getWidth(), B.getWidth()); // L
        assertEquals(A.getWidth(), 299);
        checkPositions(A, B, C);
        // A & C marked as 0dp, B == 100
        C.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        B.setWidth(100);
        root.layout();
        System.out.println("f) A: " + A + " B: " + B + " C: " + C);
        assertEquals(B.getWidth(), 100);
        assertEquals(A.getWidth(), C.getWidth());
        assertEquals(A.getWidth(), 299);
        checkPositions(A, B, C);
        // B & C marked as 0dp, A == 100
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        A.setWidth(100);
        root.layout();
        System.out.println("g) A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getWidth(), 100);
        assertEquals(B.getWidth(), C.getWidth());
        assertEquals(B.getWidth(), 299);
        checkPositions(A, B, C);
        // A == 0dp, B & C == 100, C is gone
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setWidth(100);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        B.setWidth(100);
        C.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        C.setWidth(100);
        C.setVisibility(ConstraintWidget.GONE);
        root.layout();
        System.out.println("h) A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getWidth(), 632);
        assertEquals(B.getWidth(), 100);
        assertEquals(C.getWidth(), 0);
        checkPositions(A, B, C);
    }

    private void checkPositions(ConstraintWidget A, ConstraintWidget B, ConstraintWidget C) {
        assertEquals(A.getLeft() <= A.getRight(), true);
        assertEquals(A.getRight() <= B.getLeft(), true);
        assertEquals(B.getLeft() <= B.getRight(), true);
        assertEquals(B.getRight() <= C.getLeft(), true);
        assertEquals(C.getLeft() <= C.getRight(), true);
    }

    @Test
    public void testBasicVerticalChainThreeElements() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 800, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        int marginT = 7;
        int marginB = 27;
        root.setDebugSolverName(root.getSystem(), "root");
        A.setDebugSolverName(root.getSystem(), "A");
        B.setDebugSolverName(root.getSystem(), "B");
        C.setDebugSolverName(root.getSystem(), "C");
        ArrayList<ConstraintWidget> widgets = new ArrayList<ConstraintWidget>();
        widgets.add(A);
        widgets.add(B);
        widgets.add(C);
        widgets.add(root);
        root.add(A);
        root.add(B);
        root.add(C);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 0);
        A.connect(ConstraintAnchor.Type.BOTTOM, B, ConstraintAnchor.Type.TOP, 0);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM, 0);
        B.connect(ConstraintAnchor.Type.BOTTOM, C, ConstraintAnchor.Type.TOP, 0);
        C.connect(ConstraintAnchor.Type.TOP, B, ConstraintAnchor.Type.BOTTOM, 0);
        C.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM, 0);
        root.layout();
        System.out.println("a) A: " + A + " B: " + B + " C: " + C);
        // all elements spread equally
        assertEquals(A.getHeight(), B.getHeight(), 1);
        assertEquals(B.getHeight(), C.getHeight(), 1);
        assertEquals(A.getTop() - root.getTop(), root.getBottom() - C.getBottom(), 1);
        assertEquals(A.getTop() - root.getTop(), B.getTop() - A.getBottom(), 1);
        assertEquals(B.getTop() - A.getBottom(), C.getTop() - B.getBottom(), 1);
        // A marked as 0dp, B == C, A takes the rest
        A.getAnchor(ConstraintAnchor.Type.TOP).setMargin(marginT);
        A.getAnchor(ConstraintAnchor.Type.BOTTOM).setMargin(marginB);
        B.getAnchor(ConstraintAnchor.Type.TOP).setMargin(marginT);
        B.getAnchor(ConstraintAnchor.Type.BOTTOM).setMargin(marginB);
        C.getAnchor(ConstraintAnchor.Type.TOP).setMargin(marginT);
        C.getAnchor(ConstraintAnchor.Type.BOTTOM).setMargin(marginB);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        root.layout();
        System.out.println("b) A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getTop(), 7);
        assertEquals(C.getBottom(), 573);
        assertEquals(B.getBottom(), 519);
        assertEquals(A.getHeight(), 458);
        assertEquals(B.getHeight(), C.getHeight());
        assertEquals(B.getHeight(), 20);
        checkVerticalPositions(A, B, C);
        // B marked as 0dp, A == C, B takes the rest
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        A.setHeight(20);
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        root.layout();
        System.out.println("c) A: " + A + " B: " + B + " C: " + C);
        assertEquals(B.getHeight(), 458);
        assertEquals(A.getHeight(), C.getHeight());
        assertEquals(A.getHeight(), 20);
        checkVerticalPositions(A, B, C);
        // C marked as 0dp, A == B, C takes the rest
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        B.setHeight(20);
        C.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        root.layout();
        System.out.println("d) A: " + A + " B: " + B + " C: " + C);
        assertEquals(C.getHeight(), 458);
        assertEquals(A.getHeight(), B.getHeight());
        assertEquals(A.getHeight(), 20);
        checkVerticalPositions(A, B, C);
        // A & B marked as 0dp, C == 20
        C.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        C.setHeight(20);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        root.layout();
        System.out.println("e) A: " + A + " B: " + B + " C: " + C);
        assertEquals(C.getHeight(), 20);
        assertEquals(A.getHeight(), B.getHeight()); // L
        assertEquals(A.getHeight(), 239);
        checkVerticalPositions(A, B, C);
        // A & C marked as 0dp, B == 20
        C.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        B.setHeight(20);
        root.layout();
        System.out.println("f) A: " + A + " B: " + B + " C: " + C);
        assertEquals(B.getHeight(), 20);
        assertEquals(A.getHeight(), C.getHeight());
        assertEquals(A.getHeight(), 239);
        checkVerticalPositions(A, B, C);
        // B & C marked as 0dp, A == 20
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        A.setHeight(20);
        root.layout();
        System.out.println("g) A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getHeight(), 20);
        assertEquals(B.getHeight(), C.getHeight());
        assertEquals(B.getHeight(), 239);
        checkVerticalPositions(A, B, C);
        // A == 0dp, B & C == 20, C is gone
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setHeight(20);
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        B.setHeight(20);
        C.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        C.setHeight(20);
        C.setVisibility(ConstraintWidget.GONE);
        root.layout();
        System.out.println("h) A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getHeight(), 512);
        assertEquals(B.getHeight(), 20);
        assertEquals(C.getHeight(), 0);
        checkVerticalPositions(A, B, C);
    }

    private void checkVerticalPositions(ConstraintWidget A, ConstraintWidget B, ConstraintWidget C) {
        assertEquals(A.getTop() <= A.getBottom(), true);
        assertEquals(A.getBottom() <= B.getTop(), true);
        assertEquals(B.getTop() <= B.getBottom(), true);
        assertEquals(B.getBottom() <= C.getTop(), true);
        assertEquals(C.getTop() <= C.getBottom(), true);
    }

    @Test
    public void testHorizontalChainWeights() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 800, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        int marginL = 7;
        int marginR = 27;
        root.setDebugSolverName(root.getSystem(), "root");
        A.setDebugSolverName(root.getSystem(), "A");
        B.setDebugSolverName(root.getSystem(), "B");
        C.setDebugSolverName(root.getSystem(), "C");
        ArrayList<ConstraintWidget> widgets = new ArrayList<>();
        widgets.add(A);
        widgets.add(B);
        widgets.add(C);
        widgets.add(root);
        root.add(A);
        root.add(B);
        root.add(C);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, marginL);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT, marginR);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT, marginL);
        B.connect(ConstraintAnchor.Type.RIGHT, C, ConstraintAnchor.Type.LEFT, marginR);
        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.RIGHT, marginL);
        C.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, marginR);
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        C.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setHorizontalWeight(1);
        B.setHorizontalWeight(1);
        C.setHorizontalWeight(1);
        root.layout();
        System.out.println("a) A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getWidth(), B.getWidth(), 1);
        assertEquals(B.getWidth(), C.getWidth(), 1);
        A.setHorizontalWeight(1);
        B.setHorizontalWeight(2);
        C.setHorizontalWeight(1);
        root.layout();
        System.out.println("b) A: " + A + " B: " + B + " C: " + C);
        assertEquals(2 * A.getWidth(), B.getWidth(), 1);
        assertEquals(A.getWidth(), C.getWidth(), 1);
    }

    @Test
    public void testVerticalChainWeights() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 800, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        int marginT = 7;
        int marginB = 27;
        root.setDebugSolverName(root.getSystem(), "root");
        A.setDebugSolverName(root.getSystem(), "A");
        B.setDebugSolverName(root.getSystem(), "B");
        C.setDebugSolverName(root.getSystem(), "C");
        ArrayList<ConstraintWidget> widgets = new ArrayList<>();
        widgets.add(A);
        widgets.add(B);
        widgets.add(C);
        widgets.add(root);
        root.add(A);
        root.add(B);
        root.add(C);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, marginT);
        A.connect(ConstraintAnchor.Type.BOTTOM, B, ConstraintAnchor.Type.TOP, marginB);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM, marginT);
        B.connect(ConstraintAnchor.Type.BOTTOM, C, ConstraintAnchor.Type.TOP, marginB);
        C.connect(ConstraintAnchor.Type.TOP, B, ConstraintAnchor.Type.BOTTOM, marginT);
        C.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM, marginB);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        C.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalWeight(1);
        B.setVerticalWeight(1);
        C.setVerticalWeight(1);
        root.layout();
        System.out.println("a) A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getHeight(), B.getHeight(), 1);
        assertEquals(B.getHeight(), C.getHeight(), 1);
        A.setVerticalWeight(1);
        B.setVerticalWeight(2);
        C.setVerticalWeight(1);
        root.layout();
        System.out.println("b) A: " + A + " B: " + B + " C: " + C);
        assertEquals(2 * A.getHeight(), B.getHeight(), 1);
        assertEquals(A.getHeight(), C.getHeight(), 1);
    }

    @Test
    public void testHorizontalChainPacked() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 800, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        int marginL = 7;
        int marginR = 27;
        root.setDebugSolverName(root.getSystem(), "root");
        A.setDebugSolverName(root.getSystem(), "A");
        B.setDebugSolverName(root.getSystem(), "B");
        C.setDebugSolverName(root.getSystem(), "C");
        ArrayList<ConstraintWidget> widgets = new ArrayList<>();
        widgets.add(A);
        widgets.add(B);
        widgets.add(C);
        widgets.add(root);
        root.add(A);
        root.add(B);
        root.add(C);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, marginL);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT, marginR);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT, marginL);
        B.connect(ConstraintAnchor.Type.RIGHT, C, ConstraintAnchor.Type.LEFT, marginR);
        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.RIGHT, marginL);
        C.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, marginR);
        A.setHorizontalChainStyle(ConstraintWidget.CHAIN_PACKED);
        root.layout();
        System.out.println("a) A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getLeft() - root.getLeft() - marginL, root.getRight() - marginR - C.getRight(), 1);
    }

    @Test
    public void testVerticalChainPacked() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 800, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        int marginT = 7;
        int marginB = 27;
        root.setDebugSolverName(root.getSystem(), "root");
        A.setDebugSolverName(root.getSystem(), "A");
        B.setDebugSolverName(root.getSystem(), "B");
        C.setDebugSolverName(root.getSystem(), "C");
        ArrayList<ConstraintWidget> widgets = new ArrayList<>();
        widgets.add(A);
        widgets.add(B);
        widgets.add(C);
        widgets.add(root);
        root.add(A);
        root.add(B);
        root.add(C);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, marginT);
        A.connect(ConstraintAnchor.Type.BOTTOM, B, ConstraintAnchor.Type.TOP, marginB);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM, marginT);
        B.connect(ConstraintAnchor.Type.BOTTOM, C, ConstraintAnchor.Type.TOP, marginB);
        C.connect(ConstraintAnchor.Type.TOP, B, ConstraintAnchor.Type.BOTTOM, marginT);
        C.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM, marginB);
        A.setVerticalChainStyle(ConstraintWidget.CHAIN_PACKED);
        root.layout();
        System.out.println("a) A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getTop() - root.getTop() - marginT, root.getBottom() - marginB - C.getBottom(), 1);
    }

    @Test
    public void testHorizontalChainComplex() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1000, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        ConstraintWidget D = new ConstraintWidget(50, 20);
        ConstraintWidget E = new ConstraintWidget(50, 20);
        ConstraintWidget F = new ConstraintWidget(50, 20);
        int marginL = 7;
        int marginR = 19;
        root.setDebugSolverName(root.getSystem(), "root");
        A.setDebugSolverName(root.getSystem(), "A");
        B.setDebugSolverName(root.getSystem(), "B");
        C.setDebugSolverName(root.getSystem(), "C");
        D.setDebugSolverName(root.getSystem(), "D");
        E.setDebugSolverName(root.getSystem(), "E");
        F.setDebugSolverName(root.getSystem(), "F");
        root.add(A);
        root.add(B);
        root.add(C);
        root.add(D);
        root.add(E);
        root.add(F);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, marginL);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT, marginR);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT, marginL);
        B.connect(ConstraintAnchor.Type.RIGHT, C, ConstraintAnchor.Type.LEFT, marginR);
        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.RIGHT, marginL);
        C.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, marginR);
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        C.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        D.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.LEFT, 0);
        D.connect(ConstraintAnchor.Type.RIGHT, A, ConstraintAnchor.Type.RIGHT, 0);
        E.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.LEFT, 0);
        E.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.RIGHT, 0);
        F.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.LEFT, 0);
        F.connect(ConstraintAnchor.Type.RIGHT, A, ConstraintAnchor.Type.RIGHT, 0);
        root.layout();
        System.out.println("a) A: " + A + " B: " + B + " C: " + C);
        System.out.println("a) D: " + D + " E: " + E + " F: " + F);
        assertEquals(A.getWidth(), B.getWidth(), 1);
        assertEquals(B.getWidth(), C.getWidth(), 1);
        assertEquals(A.getWidth(), 307, 1);
    }

    @Test
    public void testVerticalChainComplex() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1000, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        ConstraintWidget D = new ConstraintWidget(50, 20);
        ConstraintWidget E = new ConstraintWidget(50, 20);
        ConstraintWidget F = new ConstraintWidget(50, 20);
        int marginT = 7;
        int marginB = 19;
        root.setDebugSolverName(root.getSystem(), "root");
        A.setDebugSolverName(root.getSystem(), "A");
        B.setDebugSolverName(root.getSystem(), "B");
        C.setDebugSolverName(root.getSystem(), "C");
        D.setDebugSolverName(root.getSystem(), "D");
        E.setDebugSolverName(root.getSystem(), "E");
        F.setDebugSolverName(root.getSystem(), "F");
        root.add(A);
        root.add(B);
        root.add(C);
        root.add(D);
        root.add(E);
        root.add(F);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, marginT);
        A.connect(ConstraintAnchor.Type.BOTTOM, B, ConstraintAnchor.Type.TOP, marginB);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM, marginT);
        B.connect(ConstraintAnchor.Type.BOTTOM, C, ConstraintAnchor.Type.TOP, marginB);
        C.connect(ConstraintAnchor.Type.TOP, B, ConstraintAnchor.Type.BOTTOM, marginT);
        C.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM, marginB);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        C.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        D.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.TOP, 0);
        D.connect(ConstraintAnchor.Type.BOTTOM, A, ConstraintAnchor.Type.BOTTOM, 0);
        E.connect(ConstraintAnchor.Type.TOP, B, ConstraintAnchor.Type.TOP, 0);
        E.connect(ConstraintAnchor.Type.BOTTOM, B, ConstraintAnchor.Type.BOTTOM, 0);
        F.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.TOP, 0);
        F.connect(ConstraintAnchor.Type.BOTTOM, A, ConstraintAnchor.Type.BOTTOM, 0);
        root.layout();
        System.out.println("a) A: " + A + " B: " + B + " C: " + C);
        System.out.println("a) D: " + D + " E: " + E + " F: " + F);
        assertEquals(A.getHeight(), B.getHeight(), 1);
        assertEquals(B.getHeight(), C.getHeight(), 1);
        assertEquals(A.getHeight(), 174, 1);
    }


    @Test
    public void testHorizontalChainComplex2() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 379, 591);
        ConstraintWidget A = new ConstraintWidget(100, 185);
        ConstraintWidget B = new ConstraintWidget(100, 185);
        ConstraintWidget C = new ConstraintWidget(100, 185);
        ConstraintWidget D = new ConstraintWidget(53, 17);
        ConstraintWidget E = new ConstraintWidget(42, 17);
        ConstraintWidget F = new ConstraintWidget(47, 17);
        int marginL = 0;
        int marginR = 0;
        root.setDebugSolverName(root.getSystem(), "root");
        A.setDebugSolverName(root.getSystem(), "A");
        B.setDebugSolverName(root.getSystem(), "B");
        C.setDebugSolverName(root.getSystem(), "C");
        D.setDebugSolverName(root.getSystem(), "D");
        E.setDebugSolverName(root.getSystem(), "E");
        F.setDebugSolverName(root.getSystem(), "F");
        root.add(A);
        root.add(B);
        root.add(C);
        root.add(D);
        root.add(E);
        root.add(F);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 16);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM, 16);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, marginL);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT, marginR);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT, marginL);
        B.connect(ConstraintAnchor.Type.RIGHT, C, ConstraintAnchor.Type.LEFT, marginR);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.TOP, 0);
        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.RIGHT, marginL);
        C.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, marginR);
        C.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.TOP, 0);
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        C.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        D.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.LEFT, 0);
        D.connect(ConstraintAnchor.Type.RIGHT, A, ConstraintAnchor.Type.RIGHT, 0);
        D.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM, 0);
        E.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.LEFT, 0);
        E.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.RIGHT, 0);
        E.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM, 0);
        F.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.LEFT, 0);
        F.connect(ConstraintAnchor.Type.RIGHT, A, ConstraintAnchor.Type.RIGHT, 0);
        F.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM, 0);
        root.layout();
        System.out.println("a) A: " + A + " B: " + B + " C: " + C);
        System.out.println("a) D: " + D + " E: " + E + " F: " + F);
        assertEquals(A.getWidth(), B.getWidth(), 1);
        assertEquals(B.getWidth(), C.getWidth(), 1);
        assertEquals(A.getWidth(), 126);
    }

    @Test
    public void testVerticalChainBaseline() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 800, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        root.add(A);
        root.add(B);
        root.setDebugSolverName(root.getSystem(), "root");
        A.setDebugSolverName(root.getSystem(), "A");
        B.setDebugSolverName(root.getSystem(), "B");
        C.setDebugSolverName(root.getSystem(), "C");
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 0);
        A.connect(ConstraintAnchor.Type.BOTTOM, B, ConstraintAnchor.Type.TOP, 0);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM, 0);
        B.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM, 0);
        root.layout();
        System.out.println("a) root: " + root + " A: " + A + " B: " + B);
        int Ay = A.getTop();
        int By = B.getTop();
        assertEquals(A.getTop() - root.getTop(), root.getBottom() - B.getBottom(), 1);
        assertEquals(B.getTop() - A.getBottom(), A.getTop() - root.getTop(), 1);
        root.add(C);
        A.setBaselineDistance(7);
        C.setBaselineDistance(7);
        C.connect(ConstraintAnchor.Type.BASELINE, A, ConstraintAnchor.Type.BASELINE, 0);
        root.layout();
        System.out.println("b) root: " + root + " A: " + A + " B: " + B + " C: " + C);
        assertEquals(Ay, C.getTop(), 1);
        A.setVerticalChainStyle(ConstraintWidget.CHAIN_PACKED);
        root.layout();
        System.out.println("c) root: " + root + " A: " + A + " B: " + B + " C: " + C);
    }

    @Test
    public void testWrapHorizontalChain() {
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
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM, 0);
        B.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 0);
        B.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM, 0);
        C.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 0);
        C.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM, 0);

        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 0);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT, 0);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT, 0);
        B.connect(ConstraintAnchor.Type.RIGHT, C, ConstraintAnchor.Type.LEFT, 0);
        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.RIGHT, 0);
        C.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, 0);
        root.layout();
        System.out.println("a) root: " + root + " A: " + A + " B: " + B + " C: " + C);
        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.layout();
        System.out.println("b) root: " + root + " A: " + A + " B: " + B + " C: " + C);
        assertEquals(root.getHeight(), A.getHeight());
        assertEquals(root.getHeight(), B.getHeight());
        assertEquals(root.getHeight(), C.getHeight());
        assertEquals(root.getWidth(), A.getWidth() + B.getWidth() + C.getWidth());
    }

    @Test
    public void testWrapVerticalChain() {
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
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, 0);
        B.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 0);
        B.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, 0);
        C.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 0);
        C.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, 0);

        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 0);
        A.connect(ConstraintAnchor.Type.BOTTOM, B, ConstraintAnchor.Type.TOP, 0);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM, 0);
        B.connect(ConstraintAnchor.Type.BOTTOM, C, ConstraintAnchor.Type.TOP, 0);
        C.connect(ConstraintAnchor.Type.TOP, B, ConstraintAnchor.Type.BOTTOM, 0);
        C.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM, 0);
        root.layout();
        System.out.println("a) root: " + root + " A: " + A + " B: " + B);
        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.layout();
        System.out.println("b) root: " + root + " A: " + A + " B: " + B);
        assertEquals(root.getWidth(), A.getWidth());
        assertEquals(root.getWidth(), B.getWidth());
        assertEquals(root.getWidth(), C.getWidth());
        assertEquals(root.getHeight(), A.getHeight() + B.getHeight() + C.getHeight());
    }

    @Test
    public void testPackWithBaseline() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 411, 603);
        ConstraintWidget A = new ConstraintWidget(118, 93, 88, 48);
        ConstraintWidget B = new ConstraintWidget(206, 93, 88, 48);
        ConstraintWidget C = new ConstraintWidget(69, 314, 88, 48);
        ConstraintWidget D = new ConstraintWidget(83, 458, 88, 48);
        root.add(A);
        root.add(B);
        root.add(C);
        root.add(D);
        root.setDebugSolverName(root.getSystem(), "root");
        A.setDebugSolverName(root.getSystem(), "A");
        B.setDebugSolverName(root.getSystem(), "B");
        C.setDebugSolverName(root.getSystem(), "C");
        D.setDebugSolverName(root.getSystem(), "D");
        A.setBaselineDistance(29);
        B.setBaselineDistance(29);
        C.setBaselineDistance(29);
        D.setBaselineDistance(29);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 100);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.BASELINE, A, ConstraintAnchor.Type.BASELINE);
        C.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        C.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        D.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        D.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        C.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM);
        C.connect(ConstraintAnchor.Type.BOTTOM, D, ConstraintAnchor.Type.TOP);
        D.connect(ConstraintAnchor.Type.TOP, C, ConstraintAnchor.Type.BOTTOM);
        D.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        A.setHorizontalChainStyle(ConstraintWidget.CHAIN_PACKED);
        C.setVerticalChainStyle(ConstraintWidget.CHAIN_PACKED);
        root.layout();
        System.out.println("a) root: " + root + " A: " + A + " B: " + B);
        System.out.println("a) root: " + root + " C: " + C + " D: " + D);
        C.getAnchor(ConstraintAnchor.Type.TOP).reset();
        root.layout();
        C.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM);
        root.layout();
        System.out.println("a) root: " + root + " A: " + A + " B: " + B);
        System.out.println("a) root: " + root + " C: " + C + " D: " + D);
        assertEquals(C.getBottom(), D.getTop());
    }

    @Test
    public void testBasicGoneChain() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        ConstraintWidget D = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        D.setDebugName("D");
        root.add(A);
        root.add(B);
        root.add(C);
        root.add(D);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.RIGHT, C, ConstraintAnchor.Type.LEFT);
        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.RIGHT);
        C.connect(ConstraintAnchor.Type.RIGHT, D, ConstraintAnchor.Type.LEFT);
        D.connect(ConstraintAnchor.Type.LEFT, C, ConstraintAnchor.Type.RIGHT);
        D.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        A.setHorizontalChainStyle(ConstraintWidget.CHAIN_SPREAD_INSIDE);
        B.setVisibility(ConstraintWidget.GONE);
        root.layout();
        System.out.println("a) A: " + A + " B: " + B + " C: " + C + " D: " + D);
        assertEquals(A.getLeft(), 0);
        assertEquals(C.getLeft(), 250);
        assertEquals(D.getLeft(), 500);
        B.setVisibility(ConstraintWidget.VISIBLE);
        D.setVisibility(ConstraintWidget.GONE);
        root.layout();
        System.out.println("b) A: " + A + " B: " + B + " C: " + C + " D: " + D);
    }

    @Test
    public void testGonePackChain() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        Guideline guideline = new Guideline();
        ConstraintWidget D = new ConstraintWidget(100, 20);
        guideline.setOrientation(Guideline.VERTICAL);
        guideline.setGuideBegin(200);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        guideline.setDebugName("guideline");
        D.setDebugName("D");
        root.add(A);
        root.add(B);
        root.add(guideline);
        root.add(D);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.RIGHT, guideline, ConstraintAnchor.Type.LEFT);
        D.connect(ConstraintAnchor.Type.LEFT, guideline, ConstraintAnchor.Type.RIGHT);
        D.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        A.setHorizontalChainStyle(ConstraintWidget.CHAIN_PACKED);
        A.setVisibility(ConstraintWidget.GONE);
        B.setVisibility(ConstraintWidget.GONE);
        root.layout();
        System.out.println("a) A: " + A + " B: " + B + " guideline: " + guideline + " D: " + D);
        assertEquals(A.getWidth(), 0);
        assertEquals(B.getWidth(), 0);
        assertEquals(guideline.getLeft(), 200);
        assertEquals(D.getLeft(), 350);
        A.setHorizontalChainStyle(ConstraintWidget.CHAIN_SPREAD);
        root.layout();
        System.out.println("b) A: " + A + " B: " + B + " guideline: " + guideline + " D: " + D);
        assertEquals(A.getWidth(), 0);
        assertEquals(B.getWidth(), 0);
        assertEquals(guideline.getLeft(), 200);
        assertEquals(D.getLeft(), 350);
        A.setHorizontalChainStyle(ConstraintWidget.CHAIN_SPREAD_INSIDE);
        root.layout();
        System.out.println("c) A: " + A + " B: " + B + " guideline: " + guideline + " D: " + D);
        assertEquals(A.getWidth(), 0);
        assertEquals(B.getWidth(), 0);
        assertEquals(guideline.getLeft(), 200);
        assertEquals(D.getLeft(), 350);
    }

    @Test
    public void testVerticalGonePackChain() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        Guideline guideline = new Guideline();
        ConstraintWidget D = new ConstraintWidget(100, 20);
        guideline.setOrientation(Guideline.HORIZONTAL);
        guideline.setGuideBegin(200);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        guideline.setDebugName("guideline");
        D.setDebugName("D");
        root.add(A);
        root.add(B);
        root.add(guideline);
        root.add(D);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, B, ConstraintAnchor.Type.TOP);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM);
        B.connect(ConstraintAnchor.Type.BOTTOM, guideline, ConstraintAnchor.Type.TOP);
        D.connect(ConstraintAnchor.Type.TOP, guideline, ConstraintAnchor.Type.BOTTOM);
        D.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        A.setVerticalChainStyle(ConstraintWidget.CHAIN_PACKED);
        A.setVisibility(ConstraintWidget.GONE);
        B.setVisibility(ConstraintWidget.GONE);
        root.layout();
        System.out.println("a) A: " + A + " B: " + B + " guideline: " + guideline + " D: " + D);
        assertEquals(A.getHeight(), 0);
        assertEquals(B.getHeight(), 0);
        assertEquals(guideline.getTop(), 200);
        assertEquals(D.getTop(), 390);
        A.setVerticalChainStyle(ConstraintWidget.CHAIN_SPREAD);
        root.layout();
        System.out.println("b) A: " + A + " B: " + B + " guideline: " + guideline + " D: " + D);
        assertEquals(A.getHeight(), 0);
        assertEquals(B.getHeight(), 0);
        assertEquals(guideline.getTop(), 200);
        assertEquals(D.getTop(), 390);
        A.setVerticalChainStyle(ConstraintWidget.CHAIN_SPREAD_INSIDE);
        root.layout();
        System.out.println("c) A: " + A + " B: " + B + " guideline: " + guideline + " D: " + D);
        assertEquals(A.getHeight(), 0);
        assertEquals(B.getHeight(), 0);
        assertEquals(guideline.getTop(), 200);
        assertEquals(D.getTop(), 390);
    }

    @Test
    public void testVerticalDanglingChain() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 1000);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        root.add(A);
        root.add(B);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, B, ConstraintAnchor.Type.TOP, 7);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM, 9);
        root.layout();
        System.out.println("a) A: " + A + " B: " + B);
        assertEquals(A.getTop(), 0);
        assertEquals(B.getTop(), A.getHeight() + Math.max(7, 9));
    }

    @Test
    public void testHorizontalWeightChain() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 1000);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        Guideline guidelineLeft = new Guideline();
        Guideline guidelineRight = new Guideline();

        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        guidelineLeft.setDebugName("guidelineLeft");
        guidelineRight.setDebugName("guidelineRight");
        root.add(A);
        root.add(B);
        root.add(C);
        root.add(guidelineLeft);
        root.add(guidelineRight);

        guidelineLeft.setOrientation(Guideline.VERTICAL);
        guidelineRight.setOrientation(Guideline.VERTICAL);
        guidelineLeft.setGuideBegin(20);
        guidelineRight.setGuideEnd(20);

        A.connect(ConstraintAnchor.Type.LEFT, guidelineLeft, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.RIGHT, C, ConstraintAnchor.Type.LEFT);
        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.RIGHT);
        C.connect(ConstraintAnchor.Type.RIGHT, guidelineRight, ConstraintAnchor.Type.RIGHT);
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        C.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setHorizontalWeight(1);
        B.setHorizontalWeight(1);
        C.setHorizontalWeight(1);
        root.layout();
        System.out.println("a) A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getLeft(), 20);
        assertEquals(B.getLeft(), 207);
        assertEquals(C.getLeft(), 393);
        assertEquals(A.getWidth(), 187);
        assertEquals(B.getWidth(), 186);
        assertEquals(C.getWidth(), 187);
        C.setVisibility(ConstraintWidget.GONE);
        root.layout();
        System.out.println("b) A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getLeft(), 20);
        assertEquals(B.getLeft(), 300);
        assertEquals(C.getLeft(), 580);
        assertEquals(A.getWidth(), 280);
        assertEquals(B.getWidth(), 280);
        assertEquals(C.getWidth(), 0);
    }

    @Test
    public void testVerticalGoneChain() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        ArrayList<ConstraintWidget> widgets = new ArrayList<ConstraintWidget>();
        widgets.add(A);
        widgets.add(B);
        widgets.add(root);
        root.add(A);
        root.add(B);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 16);
        A.connect(ConstraintAnchor.Type.BOTTOM, B, ConstraintAnchor.Type.TOP);
        A.getAnchor(ConstraintAnchor.Type.BOTTOM).setGoneMargin(16);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM);
        B.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM, 16);
        A.setVerticalChainStyle(ConstraintWidget.CHAIN_PACKED);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.layout();
        System.out.println("a) root: " + root + " A: " + A + " B: " + B);
        assertEquals(A.getHeight(), B.getHeight(), 1);
        assertEquals(A.getTop() - root.getTop(), root.getBottom() - B.getBottom(), 1);
        assertEquals(A.getBottom(), B.getTop());

        B.setVisibility(ConstraintWidget.GONE);
        root.layout();
        System.out.println("b) root: " + root + " A: " + A + " B: " + B);
        assertEquals(A.getTop() - root.getTop(), root.getBottom() - A.getBottom());
        assertEquals(root.getHeight(), 52);
    }

    @Test
    public void testVerticalGoneChain2() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        root.add(A);
        root.add(B);
        root.add(C);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 16);
        A.connect(ConstraintAnchor.Type.BOTTOM, B, ConstraintAnchor.Type.TOP);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM);
        B.connect(ConstraintAnchor.Type.BOTTOM, C, ConstraintAnchor.Type.TOP);
        B.getAnchor(ConstraintAnchor.Type.TOP).setGoneMargin(16);
        B.getAnchor(ConstraintAnchor.Type.BOTTOM).setGoneMargin(16);
        C.connect(ConstraintAnchor.Type.TOP, B, ConstraintAnchor.Type.BOTTOM);
        C.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM, 16);
        A.setVerticalChainStyle(ConstraintWidget.CHAIN_PACKED);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.layout();
        System.out.println("a) root: " + root + " A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getTop() - root.getTop(), root.getBottom() - C.getBottom(), 1);
        assertEquals(A.getBottom(), B.getTop());

        A.setVisibility(ConstraintWidget.GONE);
        C.setVisibility(ConstraintWidget.GONE);
        root.layout();
        System.out.println("b) root: " + root + " A: " + A + " B: " + B + " C: " + C);
        assertEquals(B.getTop() - root.getTop(), root.getBottom() - B.getBottom());
        assertEquals(root.getHeight(), 52);
    }

    @Test
    public void testVerticalSpreadInsideChain() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        root.add(A);
        root.add(B);
        root.add(C);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 16);
        A.connect(ConstraintAnchor.Type.BOTTOM, B, ConstraintAnchor.Type.TOP);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM);
        B.connect(ConstraintAnchor.Type.BOTTOM, C, ConstraintAnchor.Type.TOP);
        C.connect(ConstraintAnchor.Type.TOP, B, ConstraintAnchor.Type.BOTTOM);
        C.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM, 16);

        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        C.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);

        A.setVerticalChainStyle(ConstraintWidget.CHAIN_SPREAD_INSIDE);
        root.layout();
        System.out.println("a) root: " + root + " A: " + A + " B: " + B + " C: " + C);

        assertEquals(A.getHeight(), B.getHeight(), 1);
        assertEquals(B.getHeight(), C.getHeight(), 1);
        assertEquals(A.getHeight(), (root.getHeight() - 32) / 3, 1);
    }

    @Test
    public void testHorizontalSpreadMaxChain() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        root.add(A);
        root.add(B);
        root.add(C);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.RIGHT, C, ConstraintAnchor.Type.LEFT);
        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.RIGHT);
        C.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);

        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        C.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);

        A.setVerticalChainStyle(ConstraintWidget.CHAIN_SPREAD_INSIDE);
        root.layout();
        System.out.println("a) root: " + root + " A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getWidth(), B.getWidth(), 1);
        assertEquals(B.getWidth(), C.getWidth(), 1);
        assertEquals(A.getWidth(), 200, 1);

        A.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_SPREAD, 0, 50, 1);
        B.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_SPREAD, 0, 50, 1);
        C.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_SPREAD, 0, 50, 1);
        root.layout();
        System.out.println("b) root: " + root + " A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getWidth(), B.getWidth(), 1);
        assertEquals(B.getWidth(), C.getWidth(), 1);
        assertEquals(A.getWidth(), 50, 1);
    }

    @Test
    public void testPackCenterChain() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        root.add(A);
        root.add(B);
        root.add(C);

        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 16);
        B.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 16);
        C.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, 16);

        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, B, ConstraintAnchor.Type.TOP);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM);
        B.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        C.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        C.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);

        A.setVerticalChainStyle(ConstraintWidget.CHAIN_PACKED);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.setMinHeight(300);
        root.layout();
        System.out.println("a) root: " + root + " A: " + A + " B: " + B + " C: " + C);
        assertEquals(root.getHeight(), 300);
        assertEquals(C.getTop(), (root.getHeight() - C.getHeight()) / 2);
        assertEquals(A.getTop(), (root.getHeight() - A.getHeight() - B.getHeight()) / 2);
    }

    @Test
    public void testPackCenterChainGone() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        root.add(A);
        root.add(B);
        root.add(C);

        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 16);
        B.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 16);
        C.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, 16);

        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, B, ConstraintAnchor.Type.TOP);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM);
        B.connect(ConstraintAnchor.Type.BOTTOM, C, ConstraintAnchor.Type.TOP);
        C.connect(ConstraintAnchor.Type.TOP, B, ConstraintAnchor.Type.BOTTOM);
        C.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);

        A.setVerticalChainStyle(ConstraintWidget.CHAIN_PACKED);
        root.layout();
        System.out.println("a) root: " + root + " A: " + A + " B: " + B + " C: " + C);
        assertEquals(600, root.getHeight());
        assertEquals(20, A.getHeight());
        assertEquals(20, B.getHeight());
        assertEquals(20, C.getHeight());
        assertEquals(270, A.getTop());
        assertEquals(290, B.getTop());
        assertEquals(310, C.getTop());

        A.setVisibility(ConstraintWidget.GONE);
        root.layout();
        System.out.println("a) root: " + root + " A: " + A + " B: " + B + " C: " + C);
        assertEquals(600, root.getHeight());
        assertEquals(0, A.getHeight());
        assertEquals(20, B.getHeight());
        assertEquals(20, C.getHeight()); // todo not done
        assertEquals(A.getTop(), B.getTop());
        assertEquals((600 - 40) / 2, B.getTop());
        assertEquals(B.getTop() + B.getHeight(), C.getTop());
    }

    @Test
    public void testSpreadInsideChainWithMargins() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        root.add(A);
        root.add(B);
        root.add(C);

        int marginOut = 0;

        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, marginOut);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.RIGHT, C, ConstraintAnchor.Type.LEFT);
        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.RIGHT);
        C.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, marginOut);
        A.setHorizontalChainStyle(ConstraintWidget.CHAIN_SPREAD_INSIDE);
        root.layout();
        System.out.println("a) root: " + root + " A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getLeft(), marginOut);
        assertEquals(C.getRight(), root.getWidth() - marginOut);
        assertEquals(B.getLeft(), A.getRight() + (C.getLeft() - A.getRight() - B.getWidth()) / 2);

        marginOut = 20;
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, marginOut);
        C.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, marginOut);
        root.layout();
        System.out.println("b) root: " + root + " A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getLeft(), marginOut);
        assertEquals(C.getRight(), root.getWidth() - marginOut);
        assertEquals(B.getLeft(), A.getRight() + (C.getLeft() - A.getRight() - B.getWidth()) / 2);
    }


}
