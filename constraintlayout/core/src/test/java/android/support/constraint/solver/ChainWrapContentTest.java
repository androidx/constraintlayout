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
import android.support.constraint.solver.widgets.Optimizer;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class ChainWrapContentTest {

    @Test
    public void testVertWrapContentChain() {
        testVertWrapContentChain(Optimizer.OPTIMIZATION_NONE);
        testVertWrapContentChain(Optimizer.OPTIMIZATION_STANDARD);
    }

    public void testVertWrapContentChain(int directResolution) {
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
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 10);
        A.connect(ConstraintAnchor.Type.BOTTOM, B, ConstraintAnchor.Type.TOP);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM);
        B.connect(ConstraintAnchor.Type.BOTTOM, C, ConstraintAnchor.Type.TOP);
        C.connect(ConstraintAnchor.Type.TOP, B, ConstraintAnchor.Type.BOTTOM);
        C.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM, 32);
        root.layout();
        System.out.println("res: " + directResolution + " root: " + root
                + " A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getTop(), 10);
        assertEquals(B.getTop(), 30);
        assertEquals(C.getTop(), 30);
        assertEquals(root.getHeight(), 82);
    }

    @Test
    public void testHorizWrapContentChain() {
        testHorizWrapContentChain(Optimizer.OPTIMIZATION_NONE);
        testHorizWrapContentChain(Optimizer.OPTIMIZATION_STANDARD);
    }

    public void testHorizWrapContentChain(int directResolution) {
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
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 10);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.RIGHT, C, ConstraintAnchor.Type.LEFT);
        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.RIGHT);
        C.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, 32);
        root.layout();
        System.out.println("1/ res: " + directResolution + " root: " + root
                + " A: " + A + " B: " + B + " C: " + C);
        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.layout();
        System.out.println("2/ res: " + directResolution + " root: " + root
                + " A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getLeft(), 10);
        assertEquals(B.getLeft(), 110);
        assertEquals(C.getLeft(), 110);
        assertEquals(root.getWidth(), 242);
        root.setMinWidth(400);
        root.layout();
        System.out.println("3/ res: " + directResolution + " root: " + root
                + " A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getLeft(), 10);
        assertEquals(B.getLeft(), 110);
        assertEquals(C.getLeft(), 268);
        assertEquals(root.getWidth(), 400);
    }

    @Test
    public void testVertWrapContentChain3Elts() {
        testVertWrapContentChain3Elts(Optimizer.OPTIMIZATION_NONE);
        testVertWrapContentChain3Elts(Optimizer.OPTIMIZATION_STANDARD);
    }

    public void testVertWrapContentChain3Elts(int directResolution) {
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
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        C.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 10);
        A.connect(ConstraintAnchor.Type.BOTTOM, B, ConstraintAnchor.Type.TOP);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM);
        B.connect(ConstraintAnchor.Type.BOTTOM, C, ConstraintAnchor.Type.TOP);
        C.connect(ConstraintAnchor.Type.TOP, B, ConstraintAnchor.Type.BOTTOM);
        C.connect(ConstraintAnchor.Type.BOTTOM, D, ConstraintAnchor.Type.TOP);
        D.connect(ConstraintAnchor.Type.TOP, C, ConstraintAnchor.Type.BOTTOM);
        D.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM, 32);
        root.layout();
        System.out.println("res: " + directResolution + " root: " + root
                + " A: " + A + " B: " + B + " C: " + C + " D: " + D);
        assertEquals(A.getTop(), 10);
        assertEquals(B.getTop(), 30);
        assertEquals(C.getTop(), 30);
        assertEquals(D.getTop(), 30);
        assertEquals(root.getHeight(), 82);
        root.setMinHeight(300);
        root.layout();
        System.out.println("res: " + directResolution + " root: " + root
                + " A: " + A + " B: " + B + " C: " + C + " D: " + D);
        assertEquals(A.getTop(), 10);
        assertEquals(B.getTop(), 30);
        assertEquals(C.getTop(), 139);
        assertEquals(D.getTop(), 248);
        assertEquals(root.getHeight(), 300);
        root.setHeight(600);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        root.layout();
        System.out.println("res: " + directResolution + " root: " + root
                + " A: " + A + " B: " + B + " C: " + C + " D: " + D);
        assertEquals(A.getTop(), 10);
        assertEquals(B.getTop(), 30);
        assertEquals(C.getTop(), 289);
        assertEquals(D.getTop(), 548);
        assertEquals(root.getHeight(), 600);
    }

    @Test
    public void testHorizWrapContentChain3Elts() {
        testHorizWrapContentChain3Elts(Optimizer.OPTIMIZATION_NONE);
        testHorizWrapContentChain3Elts(Optimizer.OPTIMIZATION_STANDARD);
    }

    public void testHorizWrapContentChain3Elts(int directResolution) {
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
        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        C.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 10);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.RIGHT, C, ConstraintAnchor.Type.LEFT);
        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.RIGHT);
        C.connect(ConstraintAnchor.Type.RIGHT, D, ConstraintAnchor.Type.LEFT);
        D.connect(ConstraintAnchor.Type.LEFT, C, ConstraintAnchor.Type.RIGHT);
        D.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, 32);
        root.layout();
        System.out.println("res: " + directResolution + " root: " + root
                + " A: " + A + " B: " + B + " C: " + C + " D: " + D);
        assertEquals(A.getLeft(), 10);
        assertEquals(B.getLeft(), 110);
        assertEquals(C.getLeft(), 110);
        assertEquals(D.getLeft(), 110);
        assertEquals(root.getWidth(), 242);
        root.setMinWidth(300);
        root.layout();
        System.out.println("res: " + directResolution + " root: " + root
                + " A: " + A + " B: " + B + " C: " + C + " D: " + D);
        assertEquals(A.getLeft(), 10);
        assertEquals(B.getLeft(), 110);
        assertEquals(C.getLeft(), 139);
        assertEquals(D.getLeft(), 168);
        assertEquals(root.getWidth(), 300);
        root.setWidth(600);
        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        root.layout();
        System.out.println("res: " + directResolution + " root: " + root
                + " A: " + A + " B: " + B + " C: " + C + " D: " + D);
        assertEquals(A.getLeft(), 10);
        assertEquals(B.getLeft(), 110);
        assertEquals(C.getLeft(), 289);
        assertEquals(D.getLeft(), 468);
        assertEquals(root.getWidth(), 600);
    }

    @Test
    public void testHorizontalWrapChain() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 1000);
        ConstraintWidget A = new ConstraintWidget(20, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(20, 20);
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
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_WRAP, 0, 0, 0);
        B.setWidth(600);
        root.layout();
        System.out.println("a) A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getLeft(), 0);
        assertEquals(B.getLeft(), 20);
        assertEquals(C.getLeft(), 580);
        A.setHorizontalChainStyle(ConstraintWidget.CHAIN_PACKED);
        B.setWidth(600);
        root.layout();
        System.out.println("b) A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getLeft(), 0);
        assertEquals(B.getLeft(), 20);
        assertEquals(C.getLeft(), 580); // doesn't expand beyond
        B.setWidth(100);
        root.layout();
        System.out.println("c) A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getLeft(), 230);
        assertEquals(B.getLeft(), 250);
        assertEquals(C.getLeft(), 350);
        B.setWidth(600);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        B.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        C.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        root.layout();
        System.out.println("d) root: " + root + " A: " + A + " B: " + B + " C: " + C);
        assertEquals(root.getHeight(), 20);
        assertEquals(A.getLeft(), 0);
        assertEquals(B.getLeft(), 20);
        assertEquals(C.getLeft(), 580);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        B.setWidth(600);
        root.setWidth(0);
        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.layout();
        System.out.println("e) root: " + root + " A: " + A + " B: " + B + " C: " + C);
        assertEquals(root.getHeight(), 20);
        assertEquals(A.getLeft(), 0);
        assertEquals(B.getLeft(), 20);
        assertEquals(C.getLeft(), 620);
    }

    @Test
    public void testWrapChain() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1440, 1944);
        ConstraintWidget A = new ConstraintWidget(308, 168);
        ConstraintWidget B = new ConstraintWidget(308, 168);
        ConstraintWidget C = new ConstraintWidget(308, 168);
        ConstraintWidget D = new ConstraintWidget(308, 168);
        ConstraintWidget E = new ConstraintWidget(308, 168);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        D.setDebugName("D");
        E.setDebugName("E");
        root.add(E);
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
        E.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        E.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        E.connect(ConstraintAnchor.Type.TOP, C, ConstraintAnchor.Type.BOTTOM);
        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.layout();
        System.out.println("a) root: " + root + " A: " + A + " B: " + B + " C: " + C + " D: " + D + " E: " + E);
        assertEquals(root.getWidth(), 1440);
        assertEquals(root.getHeight(), 336);
    }

    @Test
    public void testWrapDanglingChain() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1440, 1944);
        ConstraintWidget A = new ConstraintWidget(308, 168);
        ConstraintWidget B = new ConstraintWidget(308, 168);
        ConstraintWidget C = new ConstraintWidget(308, 168);
        ConstraintWidget D = new ConstraintWidget(308, 168);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        root.add(A);
        root.add(B);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.layout();
        System.out.println("a) root: " + root + " A: " + A + " B: " + B);
        assertEquals(root.getWidth(), 616);
        assertEquals(root.getHeight(), 168);
        assertEquals(A.getLeft(), 0);
        assertEquals(B.getLeft(), 308);
        assertEquals(A.getWidth(), 308);
        assertEquals(B.getWidth(), 308);
    }
}
