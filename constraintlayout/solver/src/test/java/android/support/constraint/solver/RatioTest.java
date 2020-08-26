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
import android.support.constraint.solver.widgets.Optimizer;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class RatioTest {

    @Test
    public void testWrapRatio() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 700, 1920);
        ConstraintWidget A = new ConstraintWidget(231, 126);
        ConstraintWidget B = new ConstraintWidget(231, 126);
        ConstraintWidget C = new ConstraintWidget(231, 126);

        root.setDebugName("root");
        root.add(A);
        root.add(B);
        root.add(C);

        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");

        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setDimensionRatio("1:1");
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, B, ConstraintAnchor.Type.TOP);
        A.setHorizontalChainStyle(ConstraintWidget.CHAIN_PACKED);
        A.setHorizontalBiasPercent(0.3f);
        A.setVerticalChainStyle(ConstraintWidget.CHAIN_SPREAD_INSIDE);

        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.LEFT, 171);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM);
        B.connect(ConstraintAnchor.Type.BOTTOM, C, ConstraintAnchor.Type.TOP);


        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.LEFT);
        C.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.RIGHT);
        C.connect(ConstraintAnchor.Type.TOP, B, ConstraintAnchor.Type.BOTTOM);
        C.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);

        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.layout();
        System.out.println("root: " + root);
        System.out.println("A: " + A);
        System.out.println("B: " + B);
        System.out.println("C: " + C);

        assertEquals(A.getLeft() >= 0, true);
        assertEquals(A.getWidth(), A.getHeight());
        assertEquals(A.getWidth(), 402);
        assertEquals(root.getWidth(), 402);
        assertEquals(root.getHeight(), 654);
        assertEquals(A.getLeft(), 0);
        assertEquals(B.getTop(), 402);
        assertEquals(B.getLeft(), 171);
        assertEquals(C.getTop(), 528);
        assertEquals(C.getLeft(), 171);
    }

    @Test
    public void testGuidelineRatioChainWrap() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 700, 1920);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        Guideline guideline = new Guideline();
        guideline.setOrientation(Guideline.HORIZONTAL);
        guideline.setGuideBegin(100);

        root.setDebugName("root");
        root.add(A);
        root.add(B);
        root.add(C);
        root.add(guideline);

        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");

        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setDimensionRatio("1:1");
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, guideline, ConstraintAnchor.Type.TOP);

        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setDimensionRatio("1:1");
        B.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM);
        B.connect(ConstraintAnchor.Type.BOTTOM, C, ConstraintAnchor.Type.TOP);


        C.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        C.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        C.setDimensionRatio("1:1");
        C.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        C.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        C.connect(ConstraintAnchor.Type.TOP, B, ConstraintAnchor.Type.BOTTOM);
        C.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);

        root.setHeight(0);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.layout();
        System.out.println("root: " + root);
        System.out.println("A: " + A);
        System.out.println("B: " + B);
        System.out.println("C: " + C);

        assertEquals(root.getHeight(), 1500);

        assertEquals(A.getWidth(), 100);
        assertEquals(A.getHeight(), 100);

        assertEquals(B.getWidth(), 700);
        assertEquals(B.getHeight(), 700);

        assertEquals(C.getWidth(), 700);
        assertEquals(C.getHeight(), 700);

        assertEquals(A.getTop(), 0);
        assertEquals(B.getTop(), A.getBottom());
        assertEquals(C.getTop(), B.getBottom());

        assertEquals(A.getLeft(), 300);
        assertEquals(B.getLeft(), 0);
        assertEquals(C.getLeft(), 0);

        root.setWidth(0);
        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.layout();
        System.out.println("root: " + root);
        System.out.println("A: " + A);
        System.out.println("B: " + B);
        System.out.println("C: " + C);

        assertEquals(root.getWidth(), 100);
        assertEquals(root.getHeight(), 300);
        assertEquals(A.getWidth(), 100);
        assertEquals(A.getHeight(), 100);
        assertEquals(B.getWidth(), 100);
        assertEquals(B.getHeight(), 100);
        assertEquals(C.getWidth(), 100);
        assertEquals(C.getHeight(), 100);
    }

    @Test
    public void testComplexRatioChainWrap() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 700, 1920);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        ConstraintWidget D = new ConstraintWidget(100, 40);
        ConstraintWidget X = new ConstraintWidget(100, 20);
        ConstraintWidget Y = new ConstraintWidget(100, 20);
        ConstraintWidget Z = new ConstraintWidget(100, 40);

        root.setDebugName("root");
        root.add(A);
        root.add(B);
        root.add(C);
        root.add(D);
        root.add(X);
        root.add(Y);
        root.add(Z);

        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        D.setDebugName("D");
        X.setDebugName("X");
        Y.setDebugName("Y");
        Z.setDebugName("Z");

        X.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        X.connect(ConstraintAnchor.Type.BOTTOM, Y, ConstraintAnchor.Type.TOP);
        X.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        X.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        X.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        X.setHeight(40);

        Y.connect(ConstraintAnchor.Type.TOP, X, ConstraintAnchor.Type.BOTTOM);
        Y.connect(ConstraintAnchor.Type.BOTTOM, Z, ConstraintAnchor.Type.TOP);
        Y.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        Y.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        Y.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        Y.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        Y.setDimensionRatio("1:1");

        Z.connect(ConstraintAnchor.Type.TOP, Y, ConstraintAnchor.Type.BOTTOM);
        Z.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        Z.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        Z.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        Z.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        Z.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        Z.setDimensionRatio("1:1");

        root.setWidth(700);
        root.setHeight(0);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.layout();
        System.out.println("root: " + root);
        System.out.println("X: " + X);
        System.out.println("Y: " + Y);
        System.out.println("Z: " + Z);

        assertEquals(root.getWidth(), 700);
        assertEquals(root.getHeight(), 1440);

        assertEquals(X.getLeft(), 0);
        assertEquals(X.getTop(), 0);
        assertEquals(X.getWidth(), 700);
        assertEquals(X.getHeight(), 40);

        assertEquals(Y.getLeft(), 0);
        assertEquals(Y.getTop(), 40);
        assertEquals(Y.getWidth(), 700);
        assertEquals(Y.getHeight(), 700);

        assertEquals(Z.getLeft(), 0);
        assertEquals(Z.getTop(), 740);
        assertEquals(Z.getWidth(), 700);
        assertEquals(Z.getHeight(), 700);

        A.connect(ConstraintAnchor.Type.TOP, X, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.LEFT, X, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT);
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setDimensionRatio("1:1");

        B.connect(ConstraintAnchor.Type.TOP, X, ConstraintAnchor.Type.TOP);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.RIGHT, C, ConstraintAnchor.Type.LEFT);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setDimensionRatio("1:1");

        C.connect(ConstraintAnchor.Type.TOP, X, ConstraintAnchor.Type.TOP);
        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.RIGHT);
        C.connect(ConstraintAnchor.Type.RIGHT, D, ConstraintAnchor.Type.LEFT);
        C.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        C.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        C.setDimensionRatio("1:1");

        D.connect(ConstraintAnchor.Type.TOP, X, ConstraintAnchor.Type.TOP);
        D.connect(ConstraintAnchor.Type.LEFT, C, ConstraintAnchor.Type.RIGHT);
        D.connect(ConstraintAnchor.Type.RIGHT, X, ConstraintAnchor.Type.RIGHT);
        D.connect(ConstraintAnchor.Type.BOTTOM, X, ConstraintAnchor.Type.BOTTOM);
        D.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        D.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        D.setDimensionRatio("1:1");

        root.setHeight(0);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.layout();
        System.out.println("root: " + root);
        System.out.println("X: " + X);
        System.out.println("Y: " + Y);
        System.out.println("Z: " + Z);

        assertEquals(root.getWidth(), 700);
        assertEquals(root.getHeight(), 1440);

        assertEquals(X.getLeft(), 0);
        assertEquals(X.getTop(), 0);
        assertEquals(X.getWidth(), 700);
        assertEquals(X.getHeight(), 40);

        assertEquals(Y.getLeft(), 0);
        assertEquals(Y.getTop(), 40);
        assertEquals(Y.getWidth(), 700);
        assertEquals(Y.getHeight(), 700);

        assertEquals(Z.getLeft(), 0);
        assertEquals(Z.getTop(), 740);
        assertEquals(Z.getWidth(), 700);
        assertEquals(Z.getHeight(), 700);

    }

    @Test
    public void testRatioChainWrap() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1000, 1000);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        ConstraintWidget D = new ConstraintWidget(100, 40);
        root.setDebugName("root");
        root.add(A);
        root.add(B);
        root.add(C);
        root.add(D);
        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        D.setDebugName("D");

        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);

        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);

        C.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        C.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);

        D.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        D.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);

        D.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        D.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        D.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);

        A.connect(ConstraintAnchor.Type.LEFT, D, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.TOP, D, ConstraintAnchor.Type.TOP);
        A.setDimensionRatio("1:1");

        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.RIGHT, C, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.TOP, D, ConstraintAnchor.Type.TOP);
        B.setDimensionRatio("1:1");

        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.RIGHT);
        C.connect(ConstraintAnchor.Type.RIGHT, D, ConstraintAnchor.Type.RIGHT);
        C.connect(ConstraintAnchor.Type.TOP, D, ConstraintAnchor.Type.TOP);
        C.connect(ConstraintAnchor.Type.BOTTOM, D, ConstraintAnchor.Type.BOTTOM);
        C.setDimensionRatio("1:1");

//        root.layout();
//        System.out.println("a) root: " + root + " D: " + D + " A: " + A + " B: " + B + " C: " + C);
//
//        root.setWidth(0);
        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.layout();
        System.out.println("b) root: " + root + " D: " + D + " A: " + A + " B: " + B + " C: " + C);

        assertEquals(root.getWidth(), 120);
        assertEquals(D.getWidth(), 120);
        assertEquals(A.getWidth(), 40);
        assertEquals(A.getHeight(), 40);
        assertEquals(B.getWidth(), 40);
        assertEquals(B.getHeight(), 40);
        assertEquals(C.getWidth(), 40);
        assertEquals(C.getHeight(), 40);
    }

    @Test
    public void testRatioChainWrap2() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1000, 1536);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        ConstraintWidget D = new ConstraintWidget(100, 40);
        ConstraintWidget E = new ConstraintWidget(100, 40);
        ConstraintWidget F = new ConstraintWidget(100, 40);
        root.setDebugName("root");
        root.add(A);
        root.add(B);
        root.add(C);
        root.add(D);
        root.add(E);
        root.add(F);
        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        D.setDebugName("D");
        E.setDebugName("E");
        F.setDebugName("F");

        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);

        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);

        C.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        C.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);

        D.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        D.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);

        E.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        E.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);

        F.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        F.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);

        D.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        D.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        D.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        D.connect(ConstraintAnchor.Type.BOTTOM, E, ConstraintAnchor.Type.TOP);

        A.connect(ConstraintAnchor.Type.LEFT, D, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.TOP, D, ConstraintAnchor.Type.TOP);
        A.setDimensionRatio("1:1");

        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.RIGHT, C, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.TOP, D, ConstraintAnchor.Type.TOP);
        B.setDimensionRatio("1:1");

        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.RIGHT);
        C.connect(ConstraintAnchor.Type.RIGHT, D, ConstraintAnchor.Type.RIGHT);
        C.connect(ConstraintAnchor.Type.TOP, D, ConstraintAnchor.Type.TOP);
        C.connect(ConstraintAnchor.Type.BOTTOM, D, ConstraintAnchor.Type.BOTTOM);
        C.setDimensionRatio("1:1");

        E.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        E.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        E.connect(ConstraintAnchor.Type.TOP, D, ConstraintAnchor.Type.BOTTOM);
        E.connect(ConstraintAnchor.Type.BOTTOM, F, ConstraintAnchor.Type.TOP);
        E.setDimensionRatio("1:1");

        F.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        F.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        F.connect(ConstraintAnchor.Type.TOP, E, ConstraintAnchor.Type.BOTTOM);
        F.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        F.setDimensionRatio("1:1");

        root.layout();
        System.out.println("a) root: " + root + " D: " + D + " A: " + A + " B: " + B + " C: " + C + " D: " + D + " E: " + E + " F: " + F);

        root.setWidth(0);
        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.layout();
        System.out.println("b) root: " + root + " D: " + D + " A: " + A + " B: " + B + " C: " + C + " D: " + D + " E: " + E + " F: " + F);

        //assertEquals(root.getWidth(), 748);
        assertEquals(D.getWidth(), root.getWidth());
        assertEquals(A.getWidth(), D.getHeight());
        assertEquals(A.getHeight(), D.getHeight());
        assertEquals(B.getWidth(), D.getHeight());
        assertEquals(B.getHeight(), D.getHeight());
        assertEquals(C.getWidth(), D.getHeight());
        assertEquals(C.getHeight(), D.getHeight());
    }

    @Test
    public void testRatioMax() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1000, 1000);
        ConstraintWidget A = new ConstraintWidget(100, 100);
        root.setDebugName("root");
        root.add(A);
        A.setDebugName("A");

        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);

        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);

        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_RATIO, 0, 150, 0);
        A.setDimensionRatio("W,16:9");

        root.setOptimizationLevel(Optimizer.OPTIMIZATION_NONE);
        root.layout();

        System.out.println("a) root: " + root + " A: " + A);
        assertEquals(A.getWidth(), 267);
        assertEquals(A.getHeight(), 150);
        assertEquals(A.getTop(), 425);
    }

    @Test
    public void testRatioMax2() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1000, 1000);
        ConstraintWidget A = new ConstraintWidget(100, 100);
        root.setDebugName("root");
        root.add(A);
        A.setDebugName("A");

        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);

        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_RATIO, 0, 150, 0);
        A.setDimensionRatio("16:9");

        root.setOptimizationLevel(Optimizer.OPTIMIZATION_NONE);
        root.layout();

        System.out.println("a) root: " + root + " A: " + A);
        assertEquals(A.getWidth(), 267, 1);
        assertEquals(A.getHeight(), 150);
        assertEquals(A.getTop(), 425);
    }

    @Test
    public void testRatioSingleTarget() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1000, 1000);
        ConstraintWidget A = new ConstraintWidget(100, 100);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        root.add(A);
        root.add(B);
        A.setDebugName("A");
        B.setDebugName("B");

        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);

        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM);
        B.connect(ConstraintAnchor.Type.BOTTOM, A, ConstraintAnchor.Type.BOTTOM);
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setDimensionRatio("2:3");
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.LEFT, 50);

        root.setOptimizationLevel(Optimizer.OPTIMIZATION_NONE);
        root.layout();

        System.out.println("a) root: " + root + " A: " + A + " B: " + B);
        assertEquals(B.getHeight(), 150);
        assertEquals(B.getTop(), A.getBottom() - B.getHeight() / 2);
    }

    @Test
    public void testSimpleWrapRatio() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1000, 1000);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        root.add(A);
        A.setDebugName("A");

        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);


        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);

        A.setDimensionRatio("1:1");
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_NONE);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.layout();

        System.out.println("a) root: " + root + " A: " + A);
        assertEquals(root.getWidth(), 1000);
        assertEquals(root.getHeight(), 1000);
        assertEquals(A.getWidth(), 1000);
        assertEquals(A.getHeight(), 1000);
    }

    @Test
    public void testSimpleWrapRatio2() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1000, 1000);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        root.add(A);
        A.setDebugName("A");

        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);


        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);

        A.setDimensionRatio("1:1");
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_NONE);
        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.layout();

        System.out.println("a) root: " + root + " A: " + A);
        assertEquals(root.getWidth(), 1000);
        assertEquals(root.getHeight(), 1000);
        assertEquals(A.getWidth(), 1000);
        assertEquals(A.getHeight(), 1000);
    }

    @Test
    public void testNestedRatio() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1000, 1000);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        root.add(A);
        root.add(B);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, B, ConstraintAnchor.Type.TOP);

        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.RIGHT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM);
        B.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);

        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);

        A.setDimensionRatio("1:1");
        B.setDimensionRatio("1:1");

        root.setOptimizationLevel(Optimizer.OPTIMIZATION_NONE);
        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.layout();

        System.out.println("a) root: " + root + " A: " + A + " B: " + B);
        assertEquals(root.getWidth(), 500);
        assertEquals(A.getWidth(), 500);
        assertEquals(B.getWidth(), 500);
        assertEquals(root.getHeight(), 1000);
        assertEquals(A.getHeight(), 500);
        assertEquals(B.getHeight(), 500);
    }

    @Test
    public void testNestedRatio2() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 700, 1200);
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
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);

        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.RIGHT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.TOP);
        B.connect(ConstraintAnchor.Type.BOTTOM, A, ConstraintAnchor.Type.BOTTOM);

        C.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.LEFT);
        C.connect(ConstraintAnchor.Type.RIGHT, A, ConstraintAnchor.Type.RIGHT);
        C.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.TOP);
        C.connect(ConstraintAnchor.Type.BOTTOM, A, ConstraintAnchor.Type.BOTTOM);

        D.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.LEFT);
        D.connect(ConstraintAnchor.Type.RIGHT, A, ConstraintAnchor.Type.RIGHT);
        D.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.TOP);
        D.connect(ConstraintAnchor.Type.BOTTOM, A, ConstraintAnchor.Type.BOTTOM);

        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);

        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setVerticalBiasPercent(0);

        C.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        C.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        C.setVerticalBiasPercent(0.5f);

        D.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        D.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        D.setVerticalBiasPercent(1);

        A.setDimensionRatio("1:1");
        B.setDimensionRatio("4:1");
        C.setDimensionRatio("4:1");
        D.setDimensionRatio("4:1");

        root.layout();

        System.out.println("a) root: " + root + " A: " + A + " B: " + B + " C: " + C + " D: " + D);
        assertEquals(A.getWidth(), 700);
        assertEquals(A.getHeight(), 700);
        assertEquals(B.getWidth(), A.getWidth());
        assertEquals(B.getHeight(), B.getWidth() / 4);
        assertEquals(B.getTop(), A.getTop());
        assertEquals(C.getWidth(), A.getWidth());
        assertEquals(C.getHeight(), C.getWidth() / 4);
        assertEquals(C.getTop(), (root.getHeight() - C.getHeight()) / 2, 1);
        assertEquals(D.getWidth(), A.getWidth());
        assertEquals(D.getHeight(), D.getWidth() / 4);
        assertEquals(D.getTop(), A.getBottom() - D.getHeight());

        root.setWidth(300);
        root.layout();

        System.out.println("b) root: " + root + " A: " + A + " B: " + B + " C: " + C + " D: " + D);
        assertEquals(A.getWidth(), root.getWidth());
        assertEquals(A.getHeight(), root.getWidth());
        assertEquals(B.getWidth(), A.getWidth());
        assertEquals(B.getHeight(), B.getWidth() / 4);
        assertEquals(B.getTop(), A.getTop());
        assertEquals(C.getWidth(), A.getWidth());
        assertEquals(C.getHeight(), C.getWidth() / 4);
        assertEquals(C.getTop(), (root.getHeight() - C.getHeight()) / 2, 1);
        assertEquals(D.getWidth(), A.getWidth());
        assertEquals(D.getHeight(), D.getWidth() / 4);
        assertEquals(D.getTop(), A.getBottom() - D.getHeight());

        root.setWidth(0);
        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.layout();

        System.out.println("c) root: " + root + " A: " + A + " B: " + B + " C: " + C + " D: " + D);
        assertEquals(root.getWidth() > 0, true, "root width should be bigger than zero");
        assertEquals(A.getWidth(), root.getWidth());
        assertEquals(A.getHeight(), root.getWidth());
        assertEquals(B.getWidth(), A.getWidth());
        assertEquals(B.getHeight(), B.getWidth() / 4);
        assertEquals(B.getTop(), A.getTop());
        assertEquals(C.getWidth(), A.getWidth());
        assertEquals(C.getHeight(), C.getWidth() / 4);
        assertEquals(C.getTop(), (root.getHeight() - C.getHeight()) / 2, 1);
        assertEquals(D.getWidth(), A.getWidth());
        assertEquals(D.getHeight(), D.getWidth() / 4);
        assertEquals(D.getTop(), A.getBottom() - D.getHeight());

        root.setWidth(700);
        root.setHeight(0);
        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.layout();

        System.out.println("d) root: " + root + " A: " + A + " B: " + B + " C: " + C + " D: " + D);
        assertEquals(root.getHeight() > 0, true, "root width should be bigger than zero");
        assertEquals(A.getWidth(), root.getWidth());
        assertEquals(A.getHeight(), root.getWidth());
        assertEquals(B.getWidth(), A.getWidth());
        assertEquals(B.getHeight(), B.getWidth() / 4);
        assertEquals(B.getTop(), A.getTop());
        assertEquals(C.getWidth(), A.getWidth());
        assertEquals(C.getHeight(), C.getWidth() / 4, 1);
        assertEquals(C.getTop(), (root.getHeight() - C.getHeight()) / 2, 1);
        assertEquals(D.getWidth(), A.getWidth());
        assertEquals(D.getHeight(), D.getWidth() / 4);
        assertEquals(D.getTop(), A.getBottom() - D.getHeight());
    }

    @Test
    public void testNestedRatio3() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1080, 1536);
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

        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setDimensionRatio("1:1");

        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setDimensionRatio("3.5:1");

        C.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        C.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        C.setDimensionRatio("5:2");

        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);

        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.RIGHT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.TOP);
        B.connect(ConstraintAnchor.Type.BOTTOM, A, ConstraintAnchor.Type.BOTTOM);
        B.setVerticalBiasPercent(0.9f);

        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.LEFT);
        C.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.RIGHT);
        C.connect(ConstraintAnchor.Type.TOP, B, ConstraintAnchor.Type.TOP);
        C.connect(ConstraintAnchor.Type.BOTTOM, B, ConstraintAnchor.Type.BOTTOM);
        C.setVerticalBiasPercent(0.9f);

//        root.layout();
//        System.out.println("A: " + A);
//        System.out.println("B: " + B);
//        System.out.println("C: " + C);
//
//        assertEquals((float)A.getWidth() / A.getHeight(), 1f, .1f);
//        assertEquals((float)B.getWidth() / B.getHeight(), 3.5f, .1f);
//        assertEquals((float)C.getWidth() / C.getHeight(), 2.5f, .1f);
//        assertEquals(B.getTop() >= A.getTop(), true);
//        assertEquals(B.getTop() <= A.getBottom(), true);
//        assertEquals(C.getTop() >= B.getTop(), true);
//        assertEquals(C.getBottom() <= B.getBottom(), true);

        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.layout();
        System.out.println("\nA: " + A);
        System.out.println("B: " + B);
        System.out.println("C: " + C);
        assertEquals((float) A.getWidth() / A.getHeight(), 1f, .1f);
        assertEquals((float) B.getWidth() / B.getHeight(), 3.5f, .1f);
        assertEquals((float) C.getWidth() / C.getHeight(), 2.5f, .1f);
    }

    @Test
    public void testNestedRatio4() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1000, 600);
        ConstraintWidget A = new ConstraintWidget(264, 144);
        ConstraintWidget B = new ConstraintWidget(264, 144);

        Guideline verticalGuideline = new Guideline();
        verticalGuideline.setGuidePercent(0.34f);
        verticalGuideline.setOrientation(Guideline.VERTICAL);

        Guideline horizontalGuideline = new Guideline();
        horizontalGuideline.setGuidePercent(0.66f);
        horizontalGuideline.setOrientation(Guideline.HORIZONTAL);

        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        horizontalGuideline.setDebugName("hGuideline");
        verticalGuideline.setDebugName("vGuideline");

        root.add(A);
        root.add(B);
        root.add(verticalGuideline);
        root.add(horizontalGuideline);

        A.setWidth(200);
        A.setHeight(200);
        A.connect(ConstraintAnchor.Type.BOTTOM, horizontalGuideline, ConstraintAnchor.Type.BOTTOM);
        A.connect(ConstraintAnchor.Type.LEFT, verticalGuideline, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, verticalGuideline, ConstraintAnchor.Type.RIGHT);
        A.connect(ConstraintAnchor.Type.TOP, horizontalGuideline, ConstraintAnchor.Type.TOP);

        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);

        B.setDimensionRatio("H,1:1");
        B.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_PERCENT, 0, 0, 0.3f);
        B.connect(ConstraintAnchor.Type.BOTTOM, horizontalGuideline, ConstraintAnchor.Type.BOTTOM);
        B.connect(ConstraintAnchor.Type.LEFT, verticalGuideline, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.RIGHT, verticalGuideline, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.TOP, horizontalGuideline, ConstraintAnchor.Type.TOP);

        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.layout();
        System.out.println("\nroot: " + root);
        System.out.println("A: " + A);
        System.out.println("B: " + B);
        System.out.println("hG: " + horizontalGuideline);
        System.out.println("vG: " + verticalGuideline);

        assertEquals(verticalGuideline.getLeft(), 0.34f * root.getWidth(), 1);
        assertEquals(horizontalGuideline.getTop(), 0.66f * root.getHeight(), 1);
        assertTrue(A.getLeft() >= 0);
        assertTrue(B.getLeft() >= 0);
        assertEquals(A.getLeft(), verticalGuideline.getLeft() - A.getWidth() / 2);
        assertEquals(A.getTop(), horizontalGuideline.getTop() - A.getHeight() / 2);

        assertEquals(B.getLeft(), verticalGuideline.getLeft() - B.getWidth() / 2);
        assertEquals(B.getTop(), horizontalGuideline.getTop() - B.getHeight() / 2);

    }

    @Test
    public void testBasicCenter() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1000, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        root.add(A);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_NONE);
        root.layout();
        System.out.println("a) root: " + root + " A: " + A);
        assertEquals(A.getLeft(), 450);
        assertEquals(A.getTop(), 290);
        assertEquals(A.getWidth(), 100);
        assertEquals(A.getHeight(), 20);
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_STANDARD);
        root.layout();
        System.out.println("b) root: " + root + " A: " + A);
        assertEquals(A.getLeft(), 450);
        assertEquals(A.getTop(), 290);
        assertEquals(A.getWidth(), 100);
        assertEquals(A.getHeight(), 20);
    }

    @Test
    public void testBasicCenter2() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1000, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        root.add(A);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_RATIO, 0, 150, 0);
        A.setDimensionRatio("W,16:9");
        root.layout();
        System.out.println("a) root: " + root + " A: " + A);
        assertEquals(A.getLeft(), 0);
        assertEquals((float) A.getWidth() / A.getHeight(), 16f / 9f, .1f);
        assertEquals(A.getHeight(), 150);
        assertEquals((float) A.getTop(), (root.getHeight() - A.getHeight()) / 2f);
    }

    @Test
    public void testBasicRatio() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 1000);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        root.add(A);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        A.setVerticalBiasPercent(0);
        A.setHorizontalBiasPercent(0);
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setDimensionRatio("1:1");
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_NONE);
        root.layout();
        System.out.println("a) root: " + root + " A: " + A);
        assertEquals(A.getLeft(), 0);
        assertEquals(A.getTop(), 0);
        assertEquals(A.getWidth(), 600);
        assertEquals(A.getHeight(), 600);
        A.setVerticalBiasPercent(1);
        root.layout();
        System.out.println("b) root: " + root + " A: " + A);
        assertEquals(A.getLeft(), 0);
        assertEquals(A.getTop(), 400);
        assertEquals(A.getWidth(), 600);
        assertEquals(A.getHeight(), 600);

        A.setVerticalBiasPercent(0);
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_STANDARD);
        root.layout();
        System.out.println("c) root: " + root + " A: " + A);
        assertEquals(A.getLeft(), 0);
        assertEquals(A.getTop(), 0);
        assertEquals(A.getWidth(), 600);
        assertEquals(A.getHeight(), 600);
    }

    @Test
    public void testBasicRatio2() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1000, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        root.add(A);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setDimensionRatio("1:1");
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_NONE);
        root.layout();
        System.out.println("a) root: " + root + " A: " + A);
        assertEquals(A.getLeft(), 450);
        assertEquals(A.getTop(), 250);
        assertEquals(A.getWidth(), 100);
        assertEquals(A.getHeight(), 100);
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_STANDARD);
        root.layout();
        System.out.println("b) root: " + root + " A: " + A);
        assertEquals(A.getLeft(), 450);
        assertEquals(A.getTop(), 250);
        assertEquals(A.getWidth(), 100);
        assertEquals(A.getHeight(), 100);
    }

    @Test
    public void testSimpleRatio() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 200, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        root.add(A);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setDimensionRatio("3:2");
        root.layout();
        System.out.println("a) root: " + root + " A: " + A);
        assertEquals((float) A.getWidth() / A.getHeight(), 3.f / 2.f, .1f);
        assertEquals(A.getTop() >= 0, true, "A.top > 0");
        assertEquals(A.getLeft() >= 0, true, "A.left > 0");
        assertEquals(A.getTop(), root.getHeight() - A.getBottom(), "A vertically centered");
        assertEquals(A.getLeft(), root.getRight() - A.getRight(), "A horizontally centered");
        A.setDimensionRatio("1:2");
        root.layout();
        System.out.println("b) root: " + root + " A: " + A);
        assertEquals((float) A.getWidth() / A.getHeight(), 1.f / 2.f, .1f);
        assertEquals(A.getTop() >= 0, true, "A.top > 0");
        assertEquals(A.getLeft() >= 0, true, "A.left > 0");
        assertEquals(A.getTop(), root.getHeight() - A.getBottom(), "A vertically centered");
        assertEquals(A.getLeft(), root.getRight() - A.getRight(), "A horizontally centered");
    }

    @Test
    public void testRatioGuideline() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 400, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        Guideline guideline = new Guideline();
        guideline.setOrientation(ConstraintWidget.VERTICAL);
        guideline.setGuideBegin(200);
        root.setDebugName("root");
        A.setDebugName("A");
        root.add(A);
        root.add(guideline);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, guideline, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setDimensionRatio("3:2");
        root.layout();
        System.out.println("a) root: " + root + " guideline: " + guideline + " A: " + A);
        assertEquals(A.getWidth() / A.getHeight(), 3 / 2);
        assertEquals(A.getTop() >= 0, true, "A.top > 0");
        assertEquals(A.getLeft() >= 0, true, "A.left > 0");
        assertEquals(A.getTop(), root.getHeight() - A.getBottom(), "A vertically centered");
        assertEquals(A.getLeft(), guideline.getLeft() - A.getRight(), "A horizontally centered");
        A.setDimensionRatio("1:2");
        root.layout();
        System.out.println("b) root: " + root + " guideline: " + guideline + " A: " + A);
        assertEquals(A.getWidth() / A.getHeight(), 1 / 2);
        assertEquals(A.getTop() >= 0, true, "A.top > 0");
        assertEquals(A.getLeft() >= 0, true, "A.left > 0");
        assertEquals(A.getTop(), root.getHeight() - A.getBottom(), "A vertically centered");
        assertEquals(A.getLeft(), guideline.getLeft() - A.getRight(), "A horizontally centered");
    }

    @Test
    public void testRatioWithMinimum() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1000, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        root.add(A);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setDimensionRatio("16:9");
        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_NONE);
        root.setWidth(0);
        root.setHeight(0);
        root.layout();
        System.out.println("a) root: " + root + " A: " + A);
        assertEquals(root.getWidth(), 0);
        assertEquals(root.getHeight(), 0);
        A.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_SPREAD, 100, 0, 0);
        root.setWidth(0);
        root.setHeight(0);
        root.layout();
        System.out.println("b) root: " + root + " A: " + A);
        assertEquals(root.getWidth(), 100);
        assertEquals(root.getHeight(), 56);
        A.setVerticalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_SPREAD, 100, 0, 0);
        root.setWidth(0);
        root.setHeight(0);
        root.layout();
        System.out.println("c) root: " + root + " A: " + A);
        assertEquals(root.getWidth(), 178);
        assertEquals(root.getHeight(), 100);
    }

    @Test
    public void testRatioWithPercent() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 1000);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        root.add(A);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setDimensionRatio("1:1");
        A.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_PERCENT, 0, 0, 0.7f);
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_NONE);
        root.layout();
        System.out.println("a) root: " + root + " A: " + A);
        int w = (int) (0.7 * root.getWidth());
        assertEquals(A.getWidth(), w);
        assertEquals(A.getHeight(), w);
        assertEquals(A.getLeft(), (root.getWidth() - w) / 2);
        assertEquals(A.getTop(), (root.getHeight() - w) / 2);

        root.setOptimizationLevel(Optimizer.OPTIMIZATION_STANDARD);
        root.layout();
        System.out.println("b) root: " + root + " A: " + A);
        assertEquals(A.getWidth(), w);
        assertEquals(A.getHeight(), w);
        assertEquals(A.getLeft(), (root.getWidth() - w) / 2);
        assertEquals(A.getTop(), (root.getHeight() - w) / 2);
    }

    @Test
    public void testRatio() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1000, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        root.add(A);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setDimensionRatio("16:9");
        root.layout();
        System.out.println("a) root: " + root + " A: " + A);
        assertEquals(A.getWidth(), 1067);
        assertEquals(A.getHeight(), 600);
    }

    @Test
    public void testRatio2() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1080, 1920);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        root.add(A);
        root.add(B);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalBiasPercent(0.9f);
        A.setDimensionRatio("3.5:1");

        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.RIGHT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.TOP);
        B.connect(ConstraintAnchor.Type.BOTTOM, A, ConstraintAnchor.Type.BOTTOM);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setHorizontalBiasPercent(0.5f);
        B.setVerticalBiasPercent(0.9f);
        B.setDimensionRatio("4:2");

        root.layout();
        System.out.println("a) root: " + root + " A: " + A + " B: " + B);
        // A: id: A (0, 414) - (600 x 172) B: (129, 414) - (342 x 172)
        assertEquals(A.getWidth() / (float) A.getHeight(), 3.5f, 0.1);
        assertEquals(B.getWidth() / (float) B.getHeight(), 2f, 0.1);
        assertEquals(A.getWidth(), 1080, 1);
        assertEquals(A.getHeight(), 309, 1);
        assertEquals(B.getWidth(), 618, 1);
        assertEquals(B.getHeight(), 309, 1);
        assertEquals(A.getLeft(), 0);
        assertEquals(A.getTop(), 1450);
        assertEquals(B.getLeft(), 231);
        assertEquals(B.getTop(), A.getTop());
    }

    @Test
    public void testRatio3() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1080, 1920);
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
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalBiasPercent(0.5f);
        A.setDimensionRatio("1:1");

        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.RIGHT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.TOP);
        B.connect(ConstraintAnchor.Type.BOTTOM, A, ConstraintAnchor.Type.BOTTOM);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setHorizontalBiasPercent(0.5f);
        B.setVerticalBiasPercent(0.9f);
        B.setDimensionRatio("3.5:1");

        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.LEFT);
        C.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.RIGHT);
        C.connect(ConstraintAnchor.Type.TOP, B, ConstraintAnchor.Type.TOP);
        C.connect(ConstraintAnchor.Type.BOTTOM, B, ConstraintAnchor.Type.BOTTOM);
        C.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        C.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        C.setHorizontalBiasPercent(0.5f);
        C.setVerticalBiasPercent(0.9f);
        C.setDimensionRatio("5:2");

        root.layout();
        System.out.println("a) root: " + root + " A: " + A + " B: " + B + " C: " + C);
        // A: id: A (0, 414) - (600 x 172) B: (129, 414) - (342 x 172)
        assertEquals(A.getWidth() / (float) A.getHeight(), 1.0f, 0.1);
        assertEquals(B.getWidth() / (float) B.getHeight(), 3.5f, 0.1);
        assertEquals(C.getWidth() / (float) C.getHeight(), 2.5f, 0.1);
        assertEquals(A.getWidth(), 1080, 1);
        assertEquals(A.getHeight(), 1080, 1);
        assertEquals(B.getWidth(), 1080, 1);
        assertEquals(B.getHeight(), 309, 1);
        assertEquals(C.getWidth(), 772, 1);
        assertEquals(C.getHeight(), 309, 1);
        assertEquals(A.getLeft(), 0);
        assertEquals(A.getTop(), 420);
        assertEquals(B.getTop(), 1114);
        assertEquals(C.getLeft(), 154);
        assertEquals(C.getTop(), B.getTop());
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
        A.setDimensionRatio("1:1");
//        root.layout();
        System.out.println("a) root: " + root + " A: " + A);
//        assertEquals(A.getWidth(), 1000);
//        assertEquals(A.getHeight(), 1000);
        A.setWidth(100);
        A.setHeight(20);
        A.setDimensionRatio("W,1:1");
        root.layout();
        System.out.println("b) root: " + root + " A: " + A);
        assertEquals(A.getWidth(), 1000);
        assertEquals(A.getHeight(), 1000);
    }

    @Test
    public void testDanglingRatio2() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1000, 600);
        ConstraintWidget A = new ConstraintWidget(300, 200);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        root.add(A);
        B.setDebugName("B");
        root.add(B);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 20);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 100);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.TOP);
        B.connect(ConstraintAnchor.Type.BOTTOM, A, ConstraintAnchor.Type.BOTTOM);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT, 15);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setDimensionRatio("1:1");
        root.layout();
        System.out.println("a) root: " + root + " A: " + A + " B: " + B);
        assertEquals(B.getLeft(), 335);
        assertEquals(B.getTop(), 100);
        assertEquals(B.getWidth(), 200);
        assertEquals(B.getHeight(), 200);
    }

    @Test
    public void testDanglingRatio3() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1000, 600);
        ConstraintWidget A = new ConstraintWidget(300, 200);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        root.add(A);
        B.setDebugName("B");
        root.add(B);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 20);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 100);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setDimensionRatio("h,1:1");
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.TOP);
        B.connect(ConstraintAnchor.Type.BOTTOM, A, ConstraintAnchor.Type.BOTTOM);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT, 15);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setDimensionRatio("w,1:1");
        root.layout();
        System.out.println("a) root: " + root + " A: " + A + " B: " + B);
        assertEquals(A.getLeft(), 20);
        assertEquals(A.getTop(), 100);
        assertEquals(A.getWidth(), 300);
        assertEquals(A.getHeight(), 300);
        assertEquals(B.getLeft(), 335);
        assertEquals(B.getTop(), 100);
        assertEquals(B.getWidth(), 300);
        assertEquals(B.getHeight(), 300);
    }

    @Test
    public void testChainRatio() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1000, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(300, 20);
        ConstraintWidget C = new ConstraintWidget(300, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        root.add(A);
        root.add(B);
        root.add(C);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.RIGHT, C, ConstraintAnchor.Type.LEFT);
        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.RIGHT);
        C.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setDimensionRatio("1:1");
        root.layout();
        System.out.println("a) root: " + root + " A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getLeft(), 0);
        assertEquals(A.getTop(), 100);
        assertEquals(A.getWidth(), 400);
        assertEquals(A.getHeight(), 400);

        assertEquals(B.getLeft(), 400);
        assertEquals(B.getTop(), 0);
        assertEquals(B.getWidth(), 300);
        assertEquals(B.getHeight(), 20);

        assertEquals(C.getLeft(), 700);
        assertEquals(C.getTop(), 0);
        assertEquals(C.getWidth(), 300);
        assertEquals(C.getHeight(), 20);
    }

    @Test
    public void testChainRatio2() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 1000);
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
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.RIGHT, C, ConstraintAnchor.Type.LEFT);
        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.RIGHT);
        C.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setDimensionRatio("1:1");
        root.layout();
        System.out.println("a) root: " + root + " A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getLeft(), 0);
        assertEquals(A.getTop(), 300);
        assertEquals(A.getWidth(), 400);
        assertEquals(A.getHeight(), 400);

        assertEquals(B.getLeft(), 400);
        assertEquals(B.getTop(), 0);
        assertEquals(B.getWidth(), 100);
        assertEquals(B.getHeight(), 20);

        assertEquals(C.getLeft(), 500);
        assertEquals(C.getTop(), 0);
        assertEquals(C.getWidth(), 100);
        assertEquals(C.getHeight(), 20);
    }


    @Test
    public void testChainRatio3() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 1000);
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
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, B, ConstraintAnchor.Type.TOP);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM);
        B.connect(ConstraintAnchor.Type.BOTTOM, C, ConstraintAnchor.Type.TOP);
        C.connect(ConstraintAnchor.Type.TOP, B, ConstraintAnchor.Type.BOTTOM);
        C.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setDimensionRatio("1:1");
        root.layout();
        System.out.println("a) root: " + root + " A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getLeft(), 0);
        assertEquals(A.getTop(), 90);
        assertEquals(A.getWidth(), 600);
        assertEquals(A.getHeight(), 600);

        assertEquals(B.getLeft(), 0);
        assertEquals(B.getTop(), 780);
        assertEquals(B.getWidth(), 100);
        assertEquals(B.getHeight(), 20);

        assertEquals(C.getLeft(), 0);
        assertEquals(C.getTop(), 890);
        assertEquals(C.getWidth(), 100);
        assertEquals(C.getHeight(), 20);
    }

    @Test
    public void testChainRatio4() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1000, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        root.add(A);
        root.add(B);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.TOP, B, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, B, ConstraintAnchor.Type.BOTTOM);
        B.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        B.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setDimensionRatio("4:3");
        root.layout();
        System.out.println("a) root: " + root + " A: " + A + " B: " + B);
        assertEquals(A.getLeft(), 0);
        assertEquals(A.getTop(), 113, 1);
        assertEquals(A.getWidth(), 500);
        assertEquals(A.getHeight(), 375);

        assertEquals(B.getLeft(), 500);
        assertEquals(B.getTop(), 113, 1);
        assertEquals(B.getWidth(), 500);
        assertEquals(B.getHeight(), 375);
    }

    @Test
    public void testChainRatio5() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 700, 1200);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        root.add(B);
        root.add(A);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        B.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        B.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);

        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setDimensionRatio("1:1");
        A.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_RATIO, 60, 0, 0);

        root.layout();

        System.out.println("a) root: " + root + " A: " + A + " B: " + B);
        assertEquals(A.getLeft(), 0);
        assertEquals(A.getTop(), 300);
        assertEquals(A.getWidth(), 600);
        assertEquals(A.getHeight(), 600);

        assertEquals(B.getLeft(), 600);
        assertEquals(B.getTop(), 590);
        assertEquals(B.getWidth(), 100);
        assertEquals(B.getHeight(), 20);

        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_WRAP, 0, 0, 0);

        root.layout();

        System.out.println("b) root: " + root + " A: " + A + " B: " + B);
        assertEquals(A.getLeft(), 0);
        assertEquals(A.getTop(), 300);
        assertEquals(A.getWidth(), 600);
        assertEquals(A.getHeight(), 600);

        assertEquals(B.getLeft(), 600);
        assertEquals(B.getTop(), 590);
        assertEquals(B.getWidth(), 100);
        assertEquals(B.getHeight(), 20);

        root.setWidth(1080);
        root.setHeight(1536);
        A.setWidth(180);
        A.setHeight(180);
        B.setWidth(900);
        B.setHeight(106);
        A.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_RATIO, 180, 0, 0);
        root.layout();
        System.out.println("c) root: " + root + " A: " + A + " B: " + B);
    }

    @Test
    public void testChainRatio6() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1000, 600);
        ConstraintWidget A = new ConstraintWidget(264, 144);
        ConstraintWidget B = new ConstraintWidget(264, 144);
        ConstraintWidget C = new ConstraintWidget(264, 144);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        root.add(A);
        root.add(B);
        root.add(C);
        A.setVerticalChainStyle(ConstraintWidget.CHAIN_SPREAD_INSIDE);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        C.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        C.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);

        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, B, ConstraintAnchor.Type.TOP);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM);
        B.connect(ConstraintAnchor.Type.BOTTOM, C, ConstraintAnchor.Type.TOP);
        C.connect(ConstraintAnchor.Type.TOP, B, ConstraintAnchor.Type.BOTTOM);
        C.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        B.setHorizontalBiasPercent(0.501f);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setDimensionRatio("1:1");
        A.setBaselineDistance(88);
        C.setBaselineDistance(88);
        root.setWidth(1080);
        root.setHeight(2220);
//        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
//        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
//        root.layout();
        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.layout();
        System.out.println("a) root: " + root);
        System.out.println(" A: " + A);
        System.out.println(" B: " + B);
        System.out.println(" C: " + C);
        assertEquals(A.getWidth(), B.getWidth());
        assertEquals(B.getWidth(), B.getHeight());
        assertEquals(root.getWidth(), C.getWidth());
        assertEquals(root.getHeight(), A.getHeight() + B.getHeight() + C.getHeight());
    }

}