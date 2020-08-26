/*
 * Copyright (C) 2015 The Android Open Source Project
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
import android.support.constraint.solver.widgets.ConstraintAnchor.Type;
import android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class OptimizationsTest {
    @Test
    public void testGoneMatchConstraint() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 800);
        ConstraintWidget A = new ConstraintWidget( 0, 10);

        root.setDebugName("root");
        A.setDebugName("A");

        root.add(A);

        A.connect(Type.TOP, root, Type.TOP, 8);
        A.connect(Type.LEFT, root, Type.LEFT, 8);
        A.connect(Type.RIGHT, root, Type.RIGHT, 8);
        A.connect(Type.BOTTOM, root, Type.BOTTOM, 8);
        A.setVerticalBiasPercent(0.2f);
        A.setHorizontalBiasPercent(0.2f);
        A.setHorizontalDimensionBehaviour(DimensionBehaviour.MATCH_CONSTRAINT);

        Metrics metrics = new Metrics();
        root.fillMetrics(metrics);
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_STANDARD);
        root.layout();

        System.out.println("1) A: " + A);
        assertEquals(A.getLeft(), 8);
        assertEquals(A.getTop(), 163);
        assertEquals(A.getRight(), 592);
        assertEquals(A.getBottom(), 173);

        A.setVisibility(ConstraintWidget.GONE);
        root.layout();

        System.out.println("2) A: " + A);
        assertEquals(A.getLeft(), 120);
        assertEquals(A.getTop(), 160);
        assertEquals(A.getRight(), 120);
        assertEquals(A.getBottom(), 160);
    }

    @Test
    public void test3EltsChain() {
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

        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        B.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        C.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);

        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 40);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.RIGHT, C, ConstraintAnchor.Type.LEFT);
        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.RIGHT);
        C.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, 30);

        Metrics metrics = new Metrics();
        root.fillMetrics(metrics);
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_STANDARD);
//        root.setOptimizationLevel(Optimizer.OPTIMIZATION_NONE);
        A.setHorizontalChainStyle(ConstraintWidget.CHAIN_SPREAD_INSIDE);
        root.layout();
        System.out.println("1) root: " + root + " A: " + A + " B: " + B + " C: " + C);
        System.out.println(metrics);
        assertEquals(A.getLeft(), 40);
        assertEquals(B.getLeft(), 255);
        assertEquals(C.getLeft(), 470);

        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        C.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        root.layout();
        System.out.println("2) root: " + root + " A: " + A + " B: " + B + " C: " + C);
        System.out.println(metrics);
        assertEquals(A.getLeft(), 40);
        assertEquals(B.getLeft(), 217,1);
        assertEquals(C.getLeft(), 393);
        assertEquals(A.getWidth(), 177, 1);
        assertEquals(B.getWidth(), 176, 1);
        assertEquals(C.getWidth(), 177, 1);

        A.setHorizontalChainStyle(ConstraintWidget.CHAIN_SPREAD);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT, 7);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT, 3);
        B.connect(ConstraintAnchor.Type.RIGHT, C, ConstraintAnchor.Type.LEFT, 7);
        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.RIGHT, 3);

        root.layout();
        System.out.println("3) root: " + root + " A: " + A + " B: " + B + " C: " + C);
        System.out.println(metrics);

        assertEquals(A.getLeft(), 40);
        assertEquals(B.getLeft(), 220);
        assertEquals(C.getLeft(), 400, 1);
        assertEquals(A.getWidth(), 170, 1);
        assertEquals(B.getWidth(), 170, 1);
        assertEquals(C.getWidth(), 170, 1);

        A.setHorizontalChainStyle(ConstraintWidget.CHAIN_SPREAD_INSIDE);

        A.setVisibility(ConstraintWidget.GONE);
        root.layout();
        System.out.println("4) root: " + root + " A: " + A + " B: " + B + " C: " + C);
        System.out.println(metrics);
        assertEquals(A.getLeft(), 0);
        assertEquals(B.getLeft(), 3);
        assertEquals(C.getLeft(), 292,1 );
        assertEquals(A.getWidth(), 0);
        assertEquals(B.getWidth(), 279, 1);
        assertEquals(C.getWidth(), 278, 1);
    }

    @Test
    public void testBasicChain() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        root.add(A);
        root.add(B);

        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        B.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);

        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);

        Metrics metrics = new Metrics();
        root.fillMetrics(metrics);
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_STANDARD);
        root.layout();
        System.out.println("1) root: " + root + " A: " + A + " B: " + B);
        System.out.println(metrics);
        assertEquals(A.getLeft(), 133);
        assertEquals(B.getLeft(), 367, 1);

        ConstraintWidget C = new ConstraintWidget(100, 20);
        C.setDebugName("C");
        root.add(C);
        C.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.RIGHT);
        root.layout();
        System.out.println("2) root: " + root + " A: " + A + " B: " + B);
        System.out.println(metrics);
        assertEquals(A.getLeft(), 133);
        assertEquals(B.getLeft(), 367, 1);
        assertEquals(C.getLeft(), B.getRight());

        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 40);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT, 100);
        A.setHorizontalChainStyle(ConstraintWidget.CHAIN_PACKED);

        root.layout();
        System.out.println("3) root: " + root + " A: " + A + " B: " + B);
        System.out.println(metrics);
        assertEquals(A.getLeft(), 170);
        assertEquals(B.getLeft(), 370);

        A.setHorizontalBiasPercent(0);
        root.layout();
        System.out.println("4) root: " + root + " A: " + A + " B: " + B);
        System.out.println(metrics);
        assertEquals(A.getLeft(), 40);
        assertEquals(B.getLeft(), 240);

        A.setHorizontalBiasPercent(0.5f);
        A.setVisibility(ConstraintWidget.GONE);
//        root.setOptimizationLevel(Optimizer.OPTIMIZATION_NONE);
        root.layout();
        System.out.println("5) root: " + root + " A: " + A + " B: " + B);
        System.out.println(metrics);
        assertEquals(A.getLeft(), 250);
        assertEquals(B.getLeft(), 250);
    }

    @Test
    public void testBasicChain2() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        root.add(A);
        root.add(B);

        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        B.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);

        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);

        ConstraintWidget C = new ConstraintWidget(100, 20);
        C.setDebugName("C");
        root.add(C);
        C.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.RIGHT);

        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 40);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT, 100);
        A.setHorizontalChainStyle(ConstraintWidget.CHAIN_PACKED);

        A.setHorizontalBiasPercent(0.5f);
        A.setVisibility(ConstraintWidget.GONE);
//        root.setOptimizationLevel(Optimizer.OPTIMIZATION_NONE);
        root.layout();
        System.out.println("5) root: " + root + " A: " + A + " B: " + B);
        assertEquals(A.getLeft(), 250);
        assertEquals(B.getLeft(), 250);
    }

    @Test
    public void testBasicRatio() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        root.add(A);
        root.add(B);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.TOP);
        B.connect(ConstraintAnchor.Type.BOTTOM, A, ConstraintAnchor.Type.BOTTOM);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setDimensionRatio("1:1");
        Metrics metrics = new Metrics();
        root.fillMetrics(metrics);
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_STANDARD);
        root.layout();
        System.out.println("1) root: " + root + " A: " + A + " B: " + B);
        System.out.println(metrics);
        assertEquals(A.getHeight(), A.getWidth());
        assertEquals(B.getTop(), (A.getHeight() - B.getHeight()) / 2);
    }

    @Test
    public void testBasicBaseline() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        A.setBaselineDistance(8);
        B.setBaselineDistance(8);
        root.add(A);
        root.add(B);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.BASELINE, A, ConstraintAnchor.Type.BASELINE);
        Metrics metrics = new Metrics();
        root.fillMetrics(metrics);
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_STANDARD);
        root.layout();
        System.out.println("1) root: " + root + " A: " + A + " B: " + B);
        System.out.println(metrics);
        assertEquals(A.getTop(), 290);
        assertEquals(B.getTop(), A.getTop());
    }

    @Test
    public void testBasicMatchConstraints() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        root.add(A);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        Metrics metrics = new Metrics();
        root.fillMetrics(metrics);
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_STANDARD);
        root.layout();
        System.out.println("1) root: " + root + " A: " + A);
        System.out.println(metrics);
        assertEquals(A.getLeft(), 0);
        assertEquals(A.getTop(), 0);
        assertEquals(A.getRight(), root.getWidth());
        assertEquals(A.getBottom(), root.getHeight());
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 10);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM, 20);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 30);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, 40);
        root.layout();
        System.out.println("2) root: " + root + " A: " + A);
        System.out.println(metrics);
        assertEquals(A.getLeft(), 30);
        assertEquals(A.getTop(), 10);
        assertEquals(A.getRight(), root.getWidth() - 40);
        assertEquals(A.getBottom(), root.getHeight() - 20);
    }

    @Test
    public void testBasicCenteringPositioning() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        root.add(A);
        long time = System.nanoTime();
        Metrics metrics = new Metrics();
        root.fillMetrics(metrics);
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_STANDARD);
        root.layout();
        time = System.nanoTime() - time;
        System.out.println("A) execution time: " + time);
        System.out.println("1) root: " + root + " A: " + A);
        System.out.println(metrics);
        assertEquals(A.getLeft(), (root.getWidth() - A.getWidth()) / 2);
        assertEquals(A.getTop(), (root.getHeight() - A.getHeight()) / 2);
        A.setHorizontalBiasPercent(0.3f);
        A.setVerticalBiasPercent(0.3f);
        root.layout();
        System.out.println("2) root: " + root + " A: " + A);
        System.out.println(metrics);
        assertEquals(A.getLeft(), (int) ((root.getWidth() - A.getWidth()) * 0.3f));
        assertEquals(A.getTop(), (int) ((root.getHeight() - A.getHeight()) * 0.3f));
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 10);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, 30);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 50);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM, 20);
        root.layout();
        System.out.println("3) root: " + root + " A: " + A);
        System.out.println(metrics);
        assertEquals(A.getLeft(), (int) ((root.getWidth() - A.getWidth() - 40) * 0.3f) + 10);
        assertEquals(A.getTop(), (int) ((root.getHeight() - A.getHeight() - 70) * 0.3f) + 50);
    }

    @Test
    public void testBasicVerticalPositioning() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        int margin = 13;
        int marginR = 27;

        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 31);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 27);
        B.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 27);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM, 104);
        root.add(A);
        root.add(B);
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_STANDARD);
        long time = System.nanoTime();
//        root.layout();
//        time = System.nanoTime() - time;
//        System.out.println("A) execution time: " + time);
//        System.out.println("a - root: " + root + " A: " + A + " B: " + B);
//
//        assertEquals(A.getLeft(), 27);
//        assertEquals(A.getTop(), 31);
//        assertEquals(B.getLeft(), 27);
//        assertEquals(B.getTop(), 155);

        A.setVisibility(ConstraintWidget.GONE);
        Metrics metrics = new Metrics();
        root.fillMetrics(metrics);
        root.layout();
        System.out.println("b - root: " + root + " A: " + A + " B: " + B);
        System.out.println(metrics);
        assertEquals(A.getLeft(), 0);
        assertEquals(A.getTop(), 0);
        assertEquals(B.getLeft(), 27);
        assertEquals(B.getTop(), 104);
        // root: id: root (0, 0) - (600 x 600) wrap: (0 x 0) A: id: A (27, 31) - (100 x 20) wrap: (0 x 0) B: id: B (27, 155) - (100 x 20) wrap: (0 x 0)

    }

    @Test
    public void testBasicVerticalGuidelinePositioning() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        Guideline guidelineA = new Guideline();
        guidelineA.setOrientation(Guideline.HORIZONTAL);
        guidelineA.setGuideEnd(67);
        root.setDebugName("root");
        A.setDebugName("A");
        guidelineA.setDebugName("guideline");
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 31);
        A.connect(ConstraintAnchor.Type.BOTTOM, guidelineA, ConstraintAnchor.Type.TOP, 12);
        root.add(A);
        root.add(guidelineA);
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_STANDARD);
        long time = System.nanoTime();
        root.layout();
        time = System.nanoTime() - time;
        System.out.println("A) execution time: " + time);
        System.out.println("root: " + root + " A: " + A + " guide: " +guidelineA);
        assertEquals(A.getTop(), 266);
        assertEquals(guidelineA.getTop(), 533);
    }

    @Test
    public void testSimpleCenterPositioning() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        int margin = 13;
        int marginR = 27;
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, margin);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM, -margin);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, margin);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, -marginR);
        root.add(A);
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_STANDARD);
        long time = System.nanoTime();
        root.layout();
        time = System.nanoTime() - time;
        System.out.println("A) execution time: " + time);
        System.out.println("root: " + root + " A: " + A);
        assertEquals(A.getLeft(), 257, 1);
        assertEquals(A.getTop(), 297, 1);
    }

    @Test
    public void testSimpleGuideline() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        Guideline guidelineA = new Guideline();
        ConstraintWidget A = new ConstraintWidget(100, 20);
        guidelineA.setOrientation(Guideline.VERTICAL);
        guidelineA.setGuideBegin(100);
        root.setDebugName("root");
        A.setDebugName("A");
        guidelineA.setDebugName("guidelineA");
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 32);
        A.connect(ConstraintAnchor.Type.LEFT, guidelineA, ConstraintAnchor.Type.LEFT, 2);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, 7);
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        root.add(guidelineA);
        root.add(A);
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_STANDARD);
        Metrics metrics = new Metrics();
        root.fillMetrics(metrics);
        long time = System.nanoTime();
        root.layout();
        assertEquals(A.getLeft(), 102);
        assertEquals(A.getTop(), 32);
        assertEquals(A.getWidth(), 491);
        assertEquals(A.getHeight(), 20);
        assertEquals(guidelineA.getLeft(), 100);
        time = System.nanoTime() - time;
        System.out.println("A) execution time: " + time);
        System.out.println("root: " + root + " A: " + A + " guideline: " + guidelineA);
        System.out.println(metrics);
        root.setWidth(700);
        time = System.nanoTime();
        root.layout();
        time = System.nanoTime() - time;
        System.out.println("B) execution time: " + time);
        System.out.println("root: " + root + " A: " + A + " guideline: " + guidelineA);
        System.out.println(metrics);
        assertEquals(A.getLeft(), 102);
        assertEquals(A.getTop(), 32);
        assertEquals(A.getWidth(), 591);
        assertEquals(A.getHeight(), 20);
        assertEquals(guidelineA.getLeft(), 100);
    }

    @Test
    public void testSimple() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");

        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 10);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 20);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT, 10);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM, 20);
        C.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT, 30);
        C.connect(ConstraintAnchor.Type.TOP, B, ConstraintAnchor.Type.BOTTOM, 20);
        root.add(A);
        root.add(B);
        root.add(C);
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_STANDARD);

        long time = System.nanoTime();
        root.layout();
        time = System.nanoTime() - time;
        System.out.println("execution time: " + time);
        System.out.println("root: " + root + " A: " + A + " B: " + B + " C: " + C);

        assertEquals(A.getLeft(), 10);
        assertEquals(A.getTop(), 20);
        assertEquals(B.getLeft(), 120);
        assertEquals(B.getTop(), 60);
        assertEquals(C.getLeft(), 140);
        assertEquals(C.getTop(), 100);
    }

    @Test
    public void testGuideline() {
        testVerticalGuideline(Optimizer.OPTIMIZATION_NONE);
        testVerticalGuideline(Optimizer.OPTIMIZATION_STANDARD);
        testHorizontalGuideline(Optimizer.OPTIMIZATION_NONE);
        testHorizontalGuideline(Optimizer.OPTIMIZATION_STANDARD);
    }

    public void testVerticalGuideline(int directResolution) {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        root.setOptimizationLevel(directResolution);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        Guideline guideline = new Guideline();
        guideline.setOrientation(Guideline.VERTICAL);
        root.setDebugName("root");
        A.setDebugName("A");
        guideline.setDebugName("guideline");
        root.add(A);
        root.add(guideline);
        A.connect(ConstraintAnchor.Type.LEFT, guideline, ConstraintAnchor.Type.LEFT, 16);
        guideline.setGuideBegin(100);
        root.layout();
        System.out.println("res: " + directResolution + " root: " + root + " A: " + A + " guideline: " + guideline);
        assertEquals(guideline.getLeft(), 100);
        assertEquals(A.getLeft(), 116);
        assertEquals(A.getWidth(), 100);
        assertEquals(A.getHeight(), 20);
        assertEquals(A.getTop(), 0);
        guideline.setGuidePercent(0.5f);
        root.layout();
        System.out.println("res: " + directResolution + " root: " + root + " A: " + A + " guideline: " + guideline);
        assertEquals(guideline.getLeft(), root.getWidth() / 2);
        assertEquals(A.getLeft(), 316);
        assertEquals(A.getWidth(), 100);
        assertEquals(A.getHeight(), 20);
        assertEquals(A.getTop(), 0);
        guideline.setGuideEnd(100);
        root.layout();
        System.out.println("res: " + directResolution + " root: " + root + " A: " + A + " guideline: " + guideline);
        assertEquals(guideline.getLeft(), 500);
        assertEquals(A.getLeft(), 516);
        assertEquals(A.getWidth(), 100);
        assertEquals(A.getHeight(), 20);
        assertEquals(A.getTop(), 0);
    }

    public void testHorizontalGuideline(int directResolution) {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        root.setOptimizationLevel(directResolution);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        Guideline guideline = new Guideline();
        guideline.setOrientation(Guideline.HORIZONTAL);
        root.setDebugName("root");
        A.setDebugName("A");
        guideline.setDebugName("guideline");
        root.add(A);
        root.add(guideline);
        A.connect(ConstraintAnchor.Type.TOP, guideline, ConstraintAnchor.Type.TOP, 16);
        guideline.setGuideBegin(100);
        root.layout();
        System.out.println("res: " + directResolution + " root: " + root + " A: " + A + " guideline: " + guideline);
        assertEquals(guideline.getTop(), 100);
        assertEquals(A.getTop(), 116);
        assertEquals(A.getWidth(), 100);
        assertEquals(A.getHeight(), 20);
        assertEquals(A.getLeft(), 0);
        guideline.setGuidePercent(0.5f);
        root.layout();
        System.out.println("res: " + directResolution + " root: " + root + " A: " + A + " guideline: " + guideline);
        assertEquals(guideline.getTop(), root.getHeight() / 2);
        assertEquals(A.getTop(), 316);
        assertEquals(A.getWidth(), 100);
        assertEquals(A.getHeight(), 20);
        assertEquals(A.getLeft(), 0);
        guideline.setGuideEnd(100);
        root.layout();
        System.out.println("res: " + directResolution + " root: " + root + " A: " + A + " guideline: " + guideline);
        assertEquals(guideline.getTop(), 500);
        assertEquals(A.getTop(), 516);
        assertEquals(A.getWidth(), 100);
        assertEquals(A.getHeight(), 20);
        assertEquals(A.getLeft(), 0);
    }

    @Test
    public void testBasicCentering() {
        testBasicCentering(Optimizer.OPTIMIZATION_NONE);
        testBasicCentering(Optimizer.OPTIMIZATION_STANDARD);
    }

    public void testBasicCentering(int directResolution) {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        root.setOptimizationLevel(directResolution);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        root.add(A);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 10);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 10);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, 10);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM, 10);
        root.layout();
        System.out.println("res: " + directResolution + " root: " + root + " A: " + A);
        assertEquals(A.getLeft(), 250);
        assertEquals(A.getTop(), 290);
    }

    @Test
    public void testPercent() {
        testPercent(Optimizer.OPTIMIZATION_NONE);
        testPercent(Optimizer.OPTIMIZATION_STANDARD);
    }

    public void testPercent(int directResolution) {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        root.setOptimizationLevel(directResolution);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        root.add(A);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 10);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 10);
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_PERCENT, 0, 0, 0.5f);
        A.setVerticalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_PERCENT, 0, 0, 0.5f);
        root.layout();
        System.out.println("res: " + directResolution + " root: " + root + " A: " + A);
        assertEquals(A.getLeft(), 10);
        assertEquals(A.getTop(), 10);
        assertEquals(A.getWidth(), 300);
        assertEquals(A.getHeight(), 300);
    }

    @Test
    public void testDependency() {
        testDependency(Optimizer.OPTIMIZATION_NONE);
        testDependency(Optimizer.OPTIMIZATION_STANDARD);
    }

    public void testDependency(int directResolution) {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        root.setOptimizationLevel(directResolution);
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
        A.setBaselineDistance(8);
        B.setBaselineDistance(8);
        C.setBaselineDistance(8);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 10);
        A.connect(ConstraintAnchor.Type.BASELINE, B, ConstraintAnchor.Type.BASELINE);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT, 16);
        B.connect(ConstraintAnchor.Type.BASELINE, C, ConstraintAnchor.Type.BASELINE);
        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.RIGHT, 48);
        C.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 32);
        root.layout();
        System.out.println("res: " + directResolution + " root: " + root
            + " A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getLeft(), 10);
        assertEquals(A.getTop(), 32);
        assertEquals(B.getLeft(), 126);
        assertEquals(B.getTop(), 32);
        assertEquals(C.getLeft(), 274);
        assertEquals(C.getTop(), 32);
    }

    @Test
    public void testDependency2() {
        testDependency2(Optimizer.OPTIMIZATION_NONE);
        testDependency2(Optimizer.OPTIMIZATION_STANDARD);
    }

    public void testDependency2(int directResolution) {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        root.setOptimizationLevel(directResolution);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        root.add(A);
        root.add(B);
        root.add(C);
        A.setBaselineDistance(8);
        B.setBaselineDistance(8);
        C.setBaselineDistance(8);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        A.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.BOTTOM, A, ConstraintAnchor.Type.TOP);
        B.connect(ConstraintAnchor.Type.LEFT, C, ConstraintAnchor.Type.LEFT);
        C.connect(ConstraintAnchor.Type.BOTTOM, B, ConstraintAnchor.Type.TOP);
        C.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 12);
        root.layout();
        System.out.println("res: " + directResolution + " root: " + root
                + " A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getLeft(), 12);
        assertEquals(A.getTop(), 580);
        assertEquals(B.getLeft(), 12);
        assertEquals(B.getTop(), 560);
        assertEquals(C.getLeft(), 12);
        assertEquals(C.getTop(), 540);
    }

    @Test
    public void testDependency3() {
        testDependency3(Optimizer.OPTIMIZATION_NONE);
        testDependency3(Optimizer.OPTIMIZATION_STANDARD);
    }

    public void testDependency3(int directResolution) {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        root.setOptimizationLevel(directResolution);
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
        A.setBaselineDistance(8);
        B.setBaselineDistance(8);
        C.setBaselineDistance(8);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 10);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 20);
        B.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 30);
        B.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM, 60);
        B.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, 10);
        C.connect(ConstraintAnchor.Type.BOTTOM, B, ConstraintAnchor.Type.TOP);
        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.RIGHT, 20);
        root.layout();
        System.out.println("res: " + directResolution + " root: " + root
                + " A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getLeft(), 10);
        assertEquals(A.getTop(), 20);
        assertEquals(B.getLeft(), 260);
        assertEquals(B.getTop(), 520);
        assertEquals(C.getLeft(), 380);
        assertEquals(C.getTop(), 500);
    }

    @Test
    public void testDependency4() {
        testDependency4(Optimizer.OPTIMIZATION_NONE);
        testDependency4(Optimizer.OPTIMIZATION_STANDARD);
    }

    public void testDependency4(int directResolution) {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        root.setOptimizationLevel(directResolution);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        root.add(A);
        root.add(B);
        A.setBaselineDistance(8);
        B.setBaselineDistance(8);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 10);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 20);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, 10);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM, 20);
        B.connect(ConstraintAnchor.Type.RIGHT, A, ConstraintAnchor.Type.RIGHT, 30);
        B.connect(ConstraintAnchor.Type.BOTTOM, A, ConstraintAnchor.Type.BOTTOM, 60);
        root.layout();
        System.out.println("res: " + directResolution + " root: " + root
                + " A: " + A + " B: " + B);
        assertEquals(A.getLeft(), 250);
        assertEquals(A.getTop(), 290);
        assertEquals(B.getLeft(), 220);
        assertEquals(B.getTop(), 230);
    }

    @Test
    public void testDependency5() {
        testDependency5(Optimizer.OPTIMIZATION_NONE);
        testDependency5(Optimizer.OPTIMIZATION_STANDARD);
    }

    public void testDependency5(int directResolution) {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        root.setOptimizationLevel(directResolution);
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
        A.setBaselineDistance(8);
        B.setBaselineDistance(8);
        C.setBaselineDistance(8);
        D.setBaselineDistance(8);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 10);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, 10);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 20);
        A.connect(ConstraintAnchor.Type.BOTTOM, B, ConstraintAnchor.Type.TOP);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM);
        B.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM, 10);
        C.connect(ConstraintAnchor.Type.TOP, B, ConstraintAnchor.Type.BOTTOM);
        C.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.RIGHT, 20);
        D.connect(ConstraintAnchor.Type.TOP, C, ConstraintAnchor.Type.BOTTOM);
        D.connect(ConstraintAnchor.Type.RIGHT, C, ConstraintAnchor.Type.RIGHT, 20);
        root.layout();
        System.out.println("res: " + directResolution + " root: " + root
                + " A: " + A + " B: " + B + " C: " + C + " D: " + D);
        assertEquals(A.getLeft(), 250);
        assertEquals(A.getTop(), 197);
        assertEquals(B.getLeft(), 250);
        assertEquals(B.getTop(), 393);
        assertEquals(C.getLeft(), 230);
        assertEquals(C.getTop(), 413);
        assertEquals(D.getLeft(), 210);
        assertEquals(D.getTop(), 433);
    }

    @Test
    public void testUnconstrainedDependency() {
        testUnconstrainedDependency(Optimizer.OPTIMIZATION_NONE);
        testUnconstrainedDependency(Optimizer.OPTIMIZATION_STANDARD);
    }

    public void testUnconstrainedDependency(int directResolution) {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        root.setOptimizationLevel(directResolution);
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
        A.setBaselineDistance(8);
        B.setBaselineDistance(8);
        C.setBaselineDistance(8);
        A.setFrame(142, 96, 242, 130);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT, 10);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.TOP, 100);
        C.connect(ConstraintAnchor.Type.RIGHT, A, ConstraintAnchor.Type.LEFT);
        C.connect(ConstraintAnchor.Type.BASELINE, A, ConstraintAnchor.Type.BASELINE);
        root.layout();
        System.out.println("res: " + directResolution + " root: " + root
                + " A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getLeft(), 142);
        assertEquals(A.getTop(), 96);
        assertEquals(A.getWidth(), 100);
        assertEquals(A.getHeight(), 34);
        assertEquals(B.getLeft(), 252);
        assertEquals(B.getTop(), 196);
        assertEquals(C.getLeft(), 42);
        assertEquals(C.getTop(), 96);
    }

    @Test
    public void testFullLayout() {
        testFullLayout(Optimizer.OPTIMIZATION_NONE);
        testFullLayout(Optimizer.OPTIMIZATION_STANDARD);
    }

    public void testFullLayout(int directResolution) {
        // Horizontal :
        // r <- A
        // r <- B <- C <- D
        //      B <- E
        // r <- F
        // r <- G
        // Vertical:
        // r <- A <- B <- C <- D <- E
        // r <- F <- G
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        root.setOptimizationLevel(directResolution);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        ConstraintWidget D = new ConstraintWidget(100, 20);
        ConstraintWidget E = new ConstraintWidget(100, 20);
        ConstraintWidget F = new ConstraintWidget(100, 20);
        ConstraintWidget G = new ConstraintWidget(100, 20);
        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        D.setDebugName("D");
        E.setDebugName("E");
        F.setDebugName("F");
        G.setDebugName("G");
        root.add(G);
        root.add(A);
        root.add(B);
        root.add(E);
        root.add(C);
        root.add(D);
        root.add(F);
        A.setBaselineDistance(8);
        B.setBaselineDistance(8);
        C.setBaselineDistance(8);
        D.setBaselineDistance(8);
        E.setBaselineDistance(8);
        F.setBaselineDistance(8);
        G.setBaselineDistance(8);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 20);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM, 40);
        B.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 16);
        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.RIGHT, 16);
        C.connect(ConstraintAnchor.Type.BASELINE, B, ConstraintAnchor.Type.BASELINE);
        D.connect(ConstraintAnchor.Type.TOP, C, ConstraintAnchor.Type.BOTTOM);
        D.connect(ConstraintAnchor.Type.LEFT, C, ConstraintAnchor.Type.LEFT);
        E.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.RIGHT);
        E.connect(ConstraintAnchor.Type.BASELINE, D, ConstraintAnchor.Type.BASELINE);
        F.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        F.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        G.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 16);
        G.connect(ConstraintAnchor.Type.BASELINE, F, ConstraintAnchor.Type.BASELINE);
        root.layout();

        System.out.println(" direct: " + directResolution + " -> A: " + A + " B: " + B + " C: " + C + " D: " + D + " E: " + E + " F: " + F + " G: " + G);
        assertEquals(A.getLeft(), 250);
        assertEquals(A.getTop(), 20);
        assertEquals(B.getLeft(), 16);
        assertEquals(B.getTop(), 80);
        assertEquals(C.getLeft(), 132);
        assertEquals(C.getTop(), 80);
        assertEquals(D.getLeft(), 132);
        assertEquals(D.getTop(), 100);
        assertEquals(E.getLeft(), 16);
        assertEquals(E.getTop(), 100);
        assertEquals(F.getLeft(), 500);
        assertEquals(F.getTop(), 580);
        assertEquals(G.getLeft(), 16);
        assertEquals(G.getTop(), 580);
    }
}
