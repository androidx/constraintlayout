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
import android.support.constraint.solver.widgets.analyzer.BasicMeasure;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class OptimizationsTest {
    @Test
    public void testGoneMatchConstraint() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 800);
        ConstraintWidget A = new ConstraintWidget("A", 0, 10);
        ConstraintWidget B = new ConstraintWidget("B", 10, 10);
        root.setDebugName("root");

        root.add(A);
        root.add(B);

        A.connect(Type.TOP, root, Type.TOP, 8);
        A.connect(Type.LEFT, root, Type.LEFT, 8);
        A.connect(Type.RIGHT, root, Type.RIGHT, 8);
        A.connect(Type.BOTTOM, root, Type.BOTTOM, 8);
        A.setVerticalBiasPercent(0.2f);
        A.setHorizontalBiasPercent(0.2f);
        A.setHorizontalDimensionBehaviour(DimensionBehaviour.MATCH_CONSTRAINT);
        B.connect(Type.TOP, A, Type.BOTTOM);

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
        assertEquals(B.getLeft(), 217, 1);
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
        assertEquals(C.getLeft(), 292, 1);
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
        System.out.println("root: " + root + " A: " + A + " guide: " + guidelineA);
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

    static BasicMeasure.Measurer sMeasurer = new BasicMeasure.Measurer() {

        @Override
        public void measure(ConstraintWidget widget, BasicMeasure.Measure measure) {
            ConstraintWidget.DimensionBehaviour horizontalBehavior = measure.horizontalBehavior;
            ConstraintWidget.DimensionBehaviour verticalBehavior = measure.verticalBehavior;
            int horizontalDimension = measure.horizontalDimension;
            int verticalDimension = measure.verticalDimension;
            System.out.println("*** MEASURE " + widget + " ***");

            if (horizontalBehavior == ConstraintWidget.DimensionBehaviour.FIXED) {
                measure.measuredWidth = horizontalDimension;
            } else if (horizontalBehavior == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                measure.measuredWidth = horizontalDimension;
            }
            if (verticalBehavior == ConstraintWidget.DimensionBehaviour.FIXED) {
                measure.measuredHeight = verticalDimension;
                measure.measuredBaseline = 8;
            } else {
                measure.measuredHeight = verticalDimension;
                measure.measuredBaseline = 8;
            }
        }

        @Override
        public void didMeasures() {

        }
    };

    @Test
    public void testComplexLayout() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_GROUPING);
        ConstraintWidget A = new ConstraintWidget(100, 100);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        ConstraintWidget D = new ConstraintWidget(30, 30);
        ConstraintWidget E = new ConstraintWidget(30, 30);
        ConstraintWidget F = new ConstraintWidget(30, 30);
        ConstraintWidget G = new ConstraintWidget(100, 20);
        ConstraintWidget H = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        D.setDebugName("D");
        E.setDebugName("E");
        F.setDebugName("F");
        G.setDebugName("G");
        H.setDebugName("H");
        root.add(G);
        root.add(A);
        root.add(B);
        root.add(E);
        root.add(C);
        root.add(D);
        root.add(F);
        root.add(H);
        B.setBaselineDistance(8);
        C.setBaselineDistance(8);
        D.setBaselineDistance(8);
        E.setBaselineDistance(8);
        F.setBaselineDistance(8);
        G.setBaselineDistance(8);
        H.setBaselineDistance(8);

        A.connect(Type.TOP, root, Type.TOP, 16);
        A.connect(Type.LEFT, root, Type.LEFT, 16);
        A.connect(Type.BOTTOM, root, Type.BOTTOM, 16);

        B.connect(Type.TOP, A, Type.TOP);
        B.connect(Type.LEFT, A, Type.RIGHT, 16);

        C.connect(Type.TOP, root, Type.TOP);
        C.connect(Type.LEFT, A, Type.RIGHT, 16);
        C.connect(Type.BOTTOM, root, Type.BOTTOM);

        D.connect(Type.BOTTOM, A, Type.BOTTOM);
        D.connect(Type.LEFT, A, Type.RIGHT, 16);

        E.connect(Type.BOTTOM, D, Type.BOTTOM);
        E.connect(Type.LEFT, D, Type.RIGHT, 16);

        F.connect(Type.BOTTOM, E, Type.BOTTOM);
        F.connect(Type.LEFT, E, Type.RIGHT, 16);

        G.connect(Type.TOP, root, Type.TOP);
        G.connect(Type.RIGHT, root, Type.RIGHT, 16);
        G.connect(Type.BOTTOM, root, Type.BOTTOM);

        H.connect(Type.BOTTOM, root, Type.BOTTOM, 16);
        H.connect(Type.RIGHT, root, Type.RIGHT, 16);

        root.setMeasurer(sMeasurer);
        root.layout();
        System.out.println(" direct: -> A: " + A + " B: " + B + " C: " + C + " D: " + D + " E: " + E + " F: " + F + " G: " + G + " H: " + H);

        assertEquals(A.getLeft(), 16);
        assertEquals(A.getTop(), 250);

        assertEquals(B.getLeft(), 132);
        assertEquals(B.getTop(), 250);

        assertEquals(C.getLeft(), 132);
        assertEquals(C.getTop(), 290);

        assertEquals(D.getLeft(), 132);
        assertEquals(D.getTop(), 320);

        assertEquals(E.getLeft(), 178);
        assertEquals(E.getTop(), 320);

        assertEquals(F.getLeft(), 224);
        assertEquals(F.getTop(), 320);

        assertEquals(G.getLeft(), 484);
        assertEquals(G.getTop(), 290);

        assertEquals(H.getLeft(), 484);
        assertEquals(H.getTop(), 564);
    }

    @Test
    public void testComplexLayoutWrap() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_DIRECT);
        ConstraintWidget A = new ConstraintWidget(100, 100);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        ConstraintWidget D = new ConstraintWidget(30, 30);
        ConstraintWidget E = new ConstraintWidget(30, 30);
        ConstraintWidget F = new ConstraintWidget(30, 30);
        ConstraintWidget G = new ConstraintWidget(100, 20);
        ConstraintWidget H = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        D.setDebugName("D");
        E.setDebugName("E");
        F.setDebugName("F");
        G.setDebugName("G");
        H.setDebugName("H");
        root.add(G);
        root.add(A);
        root.add(B);
        root.add(E);
        root.add(C);
        root.add(D);
        root.add(F);
        root.add(H);
        B.setBaselineDistance(8);
        C.setBaselineDistance(8);
        D.setBaselineDistance(8);
        E.setBaselineDistance(8);
        F.setBaselineDistance(8);
        G.setBaselineDistance(8);
        H.setBaselineDistance(8);

        A.connect(Type.TOP, root, Type.TOP, 16);
        A.connect(Type.LEFT, root, Type.LEFT, 16);
        A.connect(Type.BOTTOM, root, Type.BOTTOM, 16);

        B.connect(Type.TOP, A, Type.TOP);
        B.connect(Type.LEFT, A, Type.RIGHT, 16);

        C.connect(Type.TOP, root, Type.TOP);
        C.connect(Type.LEFT, A, Type.RIGHT, 16);
        C.connect(Type.BOTTOM, root, Type.BOTTOM);

        D.connect(Type.BOTTOM, A, Type.BOTTOM);
        D.connect(Type.LEFT, A, Type.RIGHT, 16);

        E.connect(Type.BOTTOM, D, Type.BOTTOM);
        E.connect(Type.LEFT, D, Type.RIGHT, 16);

        F.connect(Type.BOTTOM, E, Type.BOTTOM);
        F.connect(Type.LEFT, E, Type.RIGHT, 16);

        G.connect(Type.TOP, root, Type.TOP);
        G.connect(Type.RIGHT, root, Type.RIGHT, 16);
        G.connect(Type.BOTTOM, root, Type.BOTTOM);

        H.connect(Type.BOTTOM, root, Type.BOTTOM, 16);
        H.connect(Type.RIGHT, root, Type.RIGHT, 16);

        root.setMeasurer(sMeasurer);
        root.setVerticalDimensionBehaviour(DimensionBehaviour.WRAP_CONTENT);
        root.layout();

        System.out.println(" direct: -> A: " + A + " B: " + B + " C: " + C + " D: " + D + " E: " + E + " F: " + F + " G: " + G + " H: " + H);

        assertEquals(A.getLeft(), 16);
        assertEquals(A.getTop(), 16);

        assertEquals(B.getLeft(), 132);
        assertEquals(B.getTop(), 16);

        assertEquals(C.getLeft(), 132);
        assertEquals(C.getTop(), 56);

        assertEquals(D.getLeft(), 132);
        assertEquals(D.getTop(), 86);

        assertEquals(E.getLeft(), 178);
        assertEquals(E.getTop(), 86);

        assertEquals(F.getLeft(), 224);
        assertEquals(F.getTop(), 86);

        assertEquals(G.getLeft(), 484);
        assertEquals(G.getTop(), 56);

        assertEquals(H.getLeft(), 484);
        assertEquals(H.getTop(), 96);
    }

    @Test
    public void testChainLayoutWrap() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_GROUPING);
        ConstraintWidget A = new ConstraintWidget(100, 100);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        root.add(A);
        root.add(B);
        root.add(C);
        A.setBaselineDistance(28);
        B.setBaselineDistance(8);
        C.setBaselineDistance(8);

        A.connect(Type.TOP, root, Type.TOP, 16);
        A.connect(Type.LEFT, root, Type.LEFT, 16);
        A.connect(Type.RIGHT, B, Type.LEFT);
        A.connect(Type.BOTTOM, root, Type.BOTTOM, 16);

        B.connect(Type.BASELINE, A, Type.BASELINE);
        B.connect(Type.LEFT, A, Type.RIGHT);
        B.connect(Type.RIGHT, C, Type.LEFT);

        C.connect(Type.BASELINE, B, Type.BASELINE);
        C.connect(Type.LEFT, B, Type.RIGHT);
        C.connect(Type.RIGHT, root, Type.RIGHT, 16);

        root.setMeasurer(sMeasurer);
        //root.setWidth(332);
        root.setHorizontalDimensionBehaviour(DimensionBehaviour.WRAP_CONTENT);
        //root.setVerticalDimensionBehaviour(DimensionBehaviour.WRAP_CONTENT);
        root.layout();

        System.out.println(" direct: -> A: " + A + " B: " + B + " C: " + C);

        assertEquals(A.getLeft(), 16);
        assertEquals(A.getTop(), 250);

        assertEquals(B.getLeft(), 116);
        assertEquals(B.getTop(), 270);

        assertEquals(C.getLeft(), 216);
        assertEquals(C.getTop(), 270);
    }

    @Test
    public void testChainLayoutWrap2() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_GROUPING);
        ConstraintWidget A = new ConstraintWidget(100, 100);
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
        A.setBaselineDistance(28);
        B.setBaselineDistance(8);
        C.setBaselineDistance(8);
        D.setBaselineDistance(8);

        A.connect(Type.TOP, root, Type.TOP, 16);
        A.connect(Type.LEFT, root, Type.LEFT, 16);
        A.connect(Type.RIGHT, B, Type.LEFT);
        A.connect(Type.BOTTOM, root, Type.BOTTOM, 16);

        B.connect(Type.BASELINE, A, Type.BASELINE);
        B.connect(Type.LEFT, A, Type.RIGHT);
        B.connect(Type.RIGHT, C, Type.LEFT);

        C.connect(Type.BASELINE, B, Type.BASELINE);
        C.connect(Type.LEFT, B, Type.RIGHT);
        C.connect(Type.RIGHT, D, Type.LEFT, 16);

        D.connect(Type.RIGHT, root, Type.RIGHT);
        D.connect(Type.BOTTOM, root, Type.BOTTOM);

        root.setMeasurer(sMeasurer);
        //root.setWidth(332);
        root.setHorizontalDimensionBehaviour(DimensionBehaviour.WRAP_CONTENT);
        //root.setVerticalDimensionBehaviour(DimensionBehaviour.WRAP_CONTENT);
        root.layout();

        System.out.println(" direct: -> A: " + A + " B: " + B + " C: " + C + " D: " + D);

        assertEquals(A.getLeft(), 16);
        assertEquals(A.getTop(), 250);

        assertEquals(B.getLeft(), 116);
        assertEquals(B.getTop(), 270);

        assertEquals(C.getLeft(), 216);
        assertEquals(C.getTop(), 270);

        assertEquals(D.getLeft(), 332);
        assertEquals(D.getTop(), 580);
    }

    @Test
    public void testChainLayoutWrapGuideline() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_GROUPING);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        Guideline guideline = new Guideline();
        guideline.setOrientation(Guideline.VERTICAL);
        guideline.setGuideEnd(100);
        root.setDebugName("root");
        A.setDebugName("A");
        guideline.setDebugName("guideline");
        root.add(A);
        root.add(guideline);
        A.setBaselineDistance(28);

        A.connect(Type.LEFT, guideline, Type.LEFT, 16);
        A.connect(Type.BOTTOM, root, Type.BOTTOM, 16);


        root.setMeasurer(sMeasurer);
        //root.setHorizontalDimensionBehaviour(DimensionBehaviour.WRAP_CONTENT);
        root.setVerticalDimensionBehaviour(DimensionBehaviour.WRAP_CONTENT);
        root.layout();

        System.out.println(" direct: -> A: " + A + " guideline: " + guideline);

        assertEquals(A.getLeft(), 516);
        assertEquals(A.getTop(), 0);

        assertEquals(guideline.getLeft(), 500);
    }


    @Test
    public void testChainLayoutWrapGuidelineChain() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_GROUPING);
        ConstraintWidget A = new ConstraintWidget(20, 20);
        ConstraintWidget B = new ConstraintWidget(20, 20);
        ConstraintWidget C = new ConstraintWidget(20, 20);
        ConstraintWidget D = new ConstraintWidget(20, 20);
        ConstraintWidget A2 = new ConstraintWidget(20, 20);
        ConstraintWidget B2 = new ConstraintWidget(20, 20);
        ConstraintWidget C2 = new ConstraintWidget(20, 20);
        ConstraintWidget D2 = new ConstraintWidget(20, 20);
        Guideline guidelineStart = new Guideline();
        Guideline guidelineEnd = new Guideline();
        guidelineStart.setOrientation(Guideline.VERTICAL);
        guidelineEnd.setOrientation(Guideline.VERTICAL);
        guidelineStart.setGuideBegin(30);
        guidelineEnd.setGuideEnd(30);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        D.setDebugName("D");
        A2.setDebugName("A2");
        B2.setDebugName("B2");
        C2.setDebugName("C2");
        D2.setDebugName("D2");
        guidelineStart.setDebugName("guidelineStart");
        guidelineEnd.setDebugName("guidelineEnd");
        root.add(A);
        root.add(B);
        root.add(C);
        root.add(D);
        root.add(A2);
        root.add(B2);
        root.add(C2);
        root.add(D2);
        root.add(guidelineStart);
        root.add(guidelineEnd);

        C.setVisibility(ConstraintWidget.GONE);
        ChainConnect(Type.LEFT, guidelineStart, Type.RIGHT, guidelineEnd, A, B, C, D);
        ChainConnect(Type.LEFT, root, Type.RIGHT, root, A2, B2, C2, D2);


        root.setMeasurer(sMeasurer);
        root.setHorizontalDimensionBehaviour(DimensionBehaviour.WRAP_CONTENT);
        //root.setVerticalDimensionBehaviour(DimensionBehaviour.WRAP_CONTENT);
        root.layout();

        System.out.println(" direct: -> A: " + A + " guideline: " + guidelineStart + " ebnd " + guidelineEnd + " B: " + B +  " C: "  + C + " D: " + D);
        System.out.println(" direct: -> A2: " + A2 + " B2: " + B2 +  " C2: "  + C2 + " D2: " + D2);

        assertEquals(A.getLeft(), 30);
        assertEquals(B.getLeft(), 50);
        assertEquals(C.getLeft(), 70);
        assertEquals(D.getLeft(), 70);
        assertEquals(guidelineStart.getLeft(), 30);
        assertEquals(guidelineEnd.getLeft(), 90);
        assertEquals(A2.getLeft(), 8);
        assertEquals(B2.getLeft(), 36);
        assertEquals(C2.getLeft(), 64);
        assertEquals(D2.getLeft(), 92);
    }

    private void ChainConnect(Type start, ConstraintWidget startTarget, Type end,
                              ConstraintWidget endTarget, ConstraintWidget ... widgets) {
        widgets[0].connect(start, startTarget, start);
        ConstraintWidget previousWidget = null;
        for (int i = 0; i < widgets.length; i++) {
            if (previousWidget != null) {
                widgets[i].connect(start, previousWidget, end);
            }
            if (i < widgets.length - 1) {
                widgets[i].connect(end, widgets[i + 1], start);
            }
            previousWidget = widgets[i];
        }
        if (previousWidget != null) {
            previousWidget.connect(end, endTarget, end);
        }
    }

    @Test
    public void testChainLayoutWrapGuidelineChainVertical() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_GROUPING);
        ConstraintWidget A = new ConstraintWidget(20, 20);
        ConstraintWidget B = new ConstraintWidget(20, 20);
        ConstraintWidget C = new ConstraintWidget(20, 20);
        ConstraintWidget D = new ConstraintWidget(20, 20);
        ConstraintWidget A2 = new ConstraintWidget(20, 20);
        ConstraintWidget B2 = new ConstraintWidget(20, 20);
        ConstraintWidget C2 = new ConstraintWidget(20, 20);
        ConstraintWidget D2 = new ConstraintWidget(20, 20);
        Guideline guidelineStart = new Guideline();
        Guideline guidelineEnd = new Guideline();
        guidelineStart.setOrientation(Guideline.HORIZONTAL);
        guidelineEnd.setOrientation(Guideline.HORIZONTAL);
        guidelineStart.setGuideBegin(30);
        guidelineEnd.setGuideEnd(30);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        D.setDebugName("D");
        A2.setDebugName("A2");
        B2.setDebugName("B2");
        C2.setDebugName("C2");
        D2.setDebugName("D2");
        guidelineStart.setDebugName("guidelineStart");
        guidelineEnd.setDebugName("guidelineEnd");
        root.add(A);
        root.add(B);
        root.add(C);
        root.add(D);
        root.add(A2);
        root.add(B2);
        root.add(C2);
        root.add(D2);
        root.add(guidelineStart);
        root.add(guidelineEnd);

        C.setVisibility(ConstraintWidget.GONE);
        ChainConnect(Type.TOP, guidelineStart, Type.BOTTOM, guidelineEnd, A, B, C, D);
        ChainConnect(Type.TOP, root, Type.BOTTOM, root, A2, B2, C2, D2);


        root.setMeasurer(sMeasurer);
        //root.setHorizontalDimensionBehaviour(DimensionBehaviour.WRAP_CONTENT);
        root.setVerticalDimensionBehaviour(DimensionBehaviour.WRAP_CONTENT);
        root.layout();

        System.out.println(" direct: -> A: " + A + " guideline: " + guidelineStart + " ebnd " + guidelineEnd + " B: " + B +  " C: "  + C + " D: " + D);
        System.out.println(" direct: -> A2: " + A2 + " B2: " + B2 +  " C2: "  + C2 + " D2: " + D2);

        assertEquals(A.getTop(), 30);
        assertEquals(B.getTop(), 50);
        assertEquals(C.getTop(), 70);
        assertEquals(D.getTop(), 70);
        assertEquals(guidelineStart.getTop(), 30);
        assertEquals(guidelineEnd.getTop(), 90);
        assertEquals(A2.getTop(), 8);
        assertEquals(B2.getTop(), 36);
        assertEquals(C2.getTop(), 64);
        assertEquals(D2.getTop(), 92);

        assertEquals(A.getLeft(), 0);
        assertEquals(B.getLeft(), 0);
        assertEquals(C.getLeft(), 0);
        assertEquals(D.getLeft(), 0);
        assertEquals(A2.getLeft(), 0);
        assertEquals(B2.getLeft(), 0);
        assertEquals(C2.getLeft(), 0);
        assertEquals(D2.getLeft(), 0);
    }

    @Test
    public void testChainLayoutWrapRatioChain() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_GROUPING);
        ConstraintWidget A = new ConstraintWidget(20, 20);
        ConstraintWidget B = new ConstraintWidget(20, 20);
        ConstraintWidget C = new ConstraintWidget(20, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        root.add(A);
        root.add(B);
        root.add(C);

        ChainConnect(Type.TOP, root, Type.BOTTOM, root, A, B, C);
        A.connect(Type.LEFT, root, Type.LEFT);
        B.connect(Type.LEFT, root, Type.LEFT);
        C.connect(Type.LEFT, root, Type.LEFT);
        A.connect(Type.RIGHT, root, Type.RIGHT);
        B.connect(Type.RIGHT, root, Type.RIGHT);
        C.connect(Type.RIGHT, root, Type.RIGHT);
        A.setVerticalChainStyle(ConstraintWidget.CHAIN_SPREAD_INSIDE);
        B.setHorizontalDimensionBehaviour(DimensionBehaviour.MATCH_CONSTRAINT);
        B.setVerticalDimensionBehaviour(DimensionBehaviour.MATCH_CONSTRAINT);
        B.setDimensionRatio("1:1");

        root.setMeasurer(sMeasurer);
        //root.setHorizontalDimensionBehaviour(DimensionBehaviour.WRAP_CONTENT);
//        root.layout();
//
//        System.out.println(" direct: -> A: " + A + " B: " + B +  " C: "  + C);
//
//        assertEquals(A.getTop(), 0);
//        assertEquals(B.getTop(), 20);
//        assertEquals(C.getTop(), 580);
//        assertEquals(A.getLeft(), 290);
//        assertEquals(B.getLeft(), 20);
//        assertEquals(C.getLeft(), 290);
//        assertEquals(B.getWidth(), 560);
//        assertEquals(B.getHeight(), B.getWidth());
//
//        //root.setHorizontalDimensionBehaviour(DimensionBehaviour.WRAP_CONTENT);
//        root.setVerticalDimensionBehaviour(DimensionBehaviour.WRAP_CONTENT);
//        root.layout();

        root.setHorizontalDimensionBehaviour(DimensionBehaviour.WRAP_CONTENT);
        root.setVerticalDimensionBehaviour(DimensionBehaviour.FIXED);
        root.setHeight(600);
        root.layout();

        System.out.println(" direct: -> A: " + A + " B: " + B +  " C: "  + C);

        assertEquals(A.getTop(), 0);
        assertEquals(B.getTop(), 290);
        assertEquals(C.getTop(), 580);
        assertEquals(A.getLeft(), 0);
        assertEquals(B.getLeft(), 0);
        assertEquals(C.getLeft(), 0);
        assertEquals(B.getWidth(), 20);
        assertEquals(B.getHeight(), B.getWidth());
    }

    @Test
    public void testLayoutWrapBarrier() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer("root", 600, 600);
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_GROUPING);
        ConstraintWidget A = new ConstraintWidget("A",20, 20);
        ConstraintWidget B = new ConstraintWidget("B",20, 20);
        ConstraintWidget C = new ConstraintWidget("C",20, 20);
        Barrier barrier = new Barrier("Barrier");
        barrier.setBarrierType(Barrier.BOTTOM);
        root.add(A);
        root.add(B);
        root.add(C);
        root.add(barrier);

        A.connect(Type.TOP, root, Type.TOP);
        B.connect(Type.TOP, A, Type.BOTTOM);
        B.setVisibility(ConstraintWidget.GONE);
        C.connect(Type.TOP, barrier, Type.TOP);
        barrier.add(A);
        barrier.add(B);

        root.setMeasurer(sMeasurer);
        root.setVerticalDimensionBehaviour(DimensionBehaviour.WRAP_CONTENT);
        root.layout();

        System.out.println(" direct: -> root: " + root + " A: " + A + " B: " + B +  " C: "  + C + " Barrier: " + barrier.getTop());

        assertEquals(A.getLeft(), 0);
        assertEquals(A.getTop(), 0);
        assertEquals(B.getLeft(), 0);
        assertEquals(B.getTop(), 20);
        assertEquals(C.getLeft(), 0);
        assertEquals(C.getTop(), 20);
        assertEquals(barrier.getTop(), 20);
        assertEquals(root.getHeight(), 40);
    }

    @Test
    public void testLayoutWrapGuidelinesMatch() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer("root", 600, 600);
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_GROUPING);
        //root.setOptimizationLevel(Optimizer.OPTIMIZATION_NONE);
        ConstraintWidget A = new ConstraintWidget("A",20, 20);
        Guideline left = new Guideline(); left.setOrientation(Guideline.VERTICAL); left.setGuideBegin(30); left.setDebugName("L");
        Guideline right = new Guideline(); right.setOrientation(Guideline.VERTICAL); right.setGuideEnd(30); right.setDebugName("R");
        Guideline top = new Guideline(); top.setOrientation(Guideline.HORIZONTAL); top.setGuideBegin(30); top.setDebugName("T");
        Guideline bottom = new Guideline(); bottom.setOrientation(Guideline.HORIZONTAL); bottom.setGuideEnd(30); bottom.setDebugName("B");

        root.add(A);
        root.add(left);
        root.add(right);
        root.add(top);
        root.add(bottom);

        A.setHorizontalDimensionBehaviour(DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalDimensionBehaviour(DimensionBehaviour.MATCH_CONSTRAINT);
        A.connect(Type.LEFT, left, Type.LEFT);
        A.connect(Type.RIGHT, right, Type.RIGHT);
        A.connect(Type.TOP, top, Type.TOP);
        A.connect(Type.BOTTOM, bottom, Type.BOTTOM);

        root.setMeasurer(sMeasurer);
        root.setVerticalDimensionBehaviour(DimensionBehaviour.WRAP_CONTENT);
        root.layout();

        System.out.println(" direct: -> root: " + root + " A: " + A + " L: " + left + " R: " + right
                + " T: " + top + " B: " + bottom);

        assertEquals(root.getHeight(), 60);
        assertEquals(A.getLeft(), 30);
        assertEquals(A.getTop(), 30);
        assertEquals(A.getWidth(), 540);
        assertEquals(A.getHeight(), 0);

    }

    @Test
    public void testLayoutWrapMatch() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer("root", 600, 600);
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_GROUPING);
//        root.setOptimizationLevel(Optimizer.OPTIMIZATION_NONE);
        ConstraintWidget A = new ConstraintWidget("A",50, 20);
        ConstraintWidget B = new ConstraintWidget("B",50, 30);
        ConstraintWidget C = new ConstraintWidget("C",50, 20);

        root.add(A);
        root.add(B);
        root.add(C);

        A.connect(Type.LEFT, root, Type.LEFT);
        A.connect(Type.TOP, root, Type.TOP);
        B.connect(Type.LEFT, A, Type.RIGHT);
        B.connect(Type.RIGHT, C, Type.LEFT);
        B.connect(Type.TOP, A, Type.BOTTOM);
        B.connect(Type.BOTTOM, C, Type.TOP);
        C.connect(Type.RIGHT, root, Type.RIGHT);
        C.connect(Type.BOTTOM, root, Type.BOTTOM);

        B.setHorizontalDimensionBehaviour(DimensionBehaviour.MATCH_CONSTRAINT);
        B.setVerticalDimensionBehaviour(DimensionBehaviour.WRAP_CONTENT);

        root.setMeasurer(sMeasurer);
        root.setVerticalDimensionBehaviour(DimensionBehaviour.WRAP_CONTENT);
        root.layout();

        System.out.println(" direct: -> root: " + root + " A: " + A + " B: " + B + " C: " + C);
        assertEquals(B.getTop(), 20);
        assertEquals(B.getBottom(), 50);
        assertEquals(B.getLeft(), 50);
        assertEquals(B.getRight(), 550);
        assertEquals(root.getHeight(), 70);
    }

    @Test
    public void testLayoutWrapBarrier2() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer("root", 600, 600);
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_GROUPING);
        //root.setOptimizationLevel(Optimizer.OPTIMIZATION_NONE);
        ConstraintWidget A = new ConstraintWidget("A",50, 20);
        ConstraintWidget B = new ConstraintWidget("B",50, 30);
        ConstraintWidget C = new ConstraintWidget("C",50, 20);
        Guideline guideline = new Guideline(); guideline.setDebugName("end"); guideline.setGuideEnd(40); guideline.setOrientation(ConstraintWidget.VERTICAL);
        Barrier barrier = new Barrier();
        barrier.setBarrierType(Barrier.LEFT);
        barrier.setDebugName("barrier");
        barrier.add(B);
        barrier.add(C);

        root.add(A);
        root.add(B);
        root.add(C);
        root.add(barrier);
        root.add(guideline);

        A.connect(Type.LEFT, root, Type.LEFT);
        A.connect(Type.RIGHT, barrier, Type.LEFT);
        B.connect(Type.RIGHT, guideline, Type.RIGHT);
        C.connect(Type.RIGHT, root, Type.RIGHT);

        root.setMeasurer(sMeasurer);
        root.setHorizontalDimensionBehaviour(DimensionBehaviour.WRAP_CONTENT);
        root.layout();

        System.out.println(" direct: -> root: " + root + " A: " + A + " B: " + B + " C: " + C);
        assertEquals(root.getWidth(), 140);
    }

    @Test
    public void testLayoutWrapBarrier3() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer("root", 600, 600);
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_GROUPING);
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_NONE);
        ConstraintWidget A = new ConstraintWidget("A",50, 20);
        ConstraintWidget B = new ConstraintWidget("B",50, 30);
        ConstraintWidget C = new ConstraintWidget("C",50, 20);
        Guideline guideline = new Guideline(); guideline.setDebugName("end"); guideline.setGuideEnd(40); guideline.setOrientation(ConstraintWidget.VERTICAL);
        Barrier barrier = new Barrier();
        barrier.setBarrierType(Barrier.LEFT);
        barrier.setDebugName("barrier");
        barrier.add(B);
        barrier.add(C);

        root.add(A);
        root.add(B);
        root.add(C);
        root.add(barrier);
        root.add(guideline);

        A.connect(Type.LEFT, root, Type.LEFT);
        A.connect(Type.RIGHT, barrier, Type.LEFT);
        B.connect(Type.RIGHT, guideline, Type.RIGHT);
        C.connect(Type.RIGHT, root, Type.RIGHT);

        root.setMeasurer(sMeasurer);
        root.setHorizontalDimensionBehaviour(DimensionBehaviour.WRAP_CONTENT);
        root.layout();

        System.out.println(" direct: -> root: " + root + " A: " + A + " B: " + B + " C: " + C);
        assertEquals(root.getWidth(), 140);
    }

    @Test
    public void testSimpleGuideline2() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer("root", 600, 600);
        Guideline guidelineStart = new Guideline(); guidelineStart.setDebugName("start"); guidelineStart.setGuidePercent(0.1f); guidelineStart.setOrientation(ConstraintWidget.VERTICAL);
        Guideline guidelineEnd = new Guideline(); guidelineEnd.setDebugName("end"); guidelineEnd.setGuideEnd(40); guidelineEnd.setOrientation(ConstraintWidget.VERTICAL);
        ConstraintWidget A = new ConstraintWidget("A",50, 20);
        root.add(A);
        root.add(guidelineStart);
        root.add(guidelineEnd);

        A.setHorizontalDimensionBehaviour(DimensionBehaviour.MATCH_CONSTRAINT);
        A.connect(Type.LEFT, guidelineStart, Type.LEFT);
        A.connect(Type.RIGHT, guidelineEnd, Type.RIGHT);

        root.setMeasurer(sMeasurer);
        //root.setHorizontalDimensionBehaviour(DimensionBehaviour.WRAP_CONTENT);
        root.layout();
        System.out.println(" root: " + root);
        System.out.println("guideline start: " + guidelineStart);
        System.out.println("guideline end: " + guidelineEnd);
    }
}