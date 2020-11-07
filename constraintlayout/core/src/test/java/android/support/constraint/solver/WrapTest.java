package android.support.constraint.solver;

import android.support.constraint.solver.widgets.Barrier;
import android.support.constraint.solver.widgets.ConstraintAnchor;
import android.support.constraint.solver.widgets.ConstraintWidget;
import android.support.constraint.solver.widgets.ConstraintWidgetContainer;
import android.support.constraint.solver.widgets.Guideline;
import android.support.constraint.solver.widgets.Optimizer;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Basic wrap test
 */
public class WrapTest {

    @Test
    public void testBasic() {
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

        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);

        root.layout();
        System.out.println("a) root: " + root + " A: " + A);

        A.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_SPREAD, 0, 100, 0);
        A.setVerticalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_SPREAD, 0, 60, 0);
        root.layout();
        System.out.println("b) root: " + root + " A: " + A);
    }

    @Test
    public void testBasic2() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1000, 600);
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
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.RIGHT, C, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM);
        B.connect(ConstraintAnchor.Type.BOTTOM, C, ConstraintAnchor.Type.TOP);
        C.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        C.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_SPREAD, 0, 100, 1);
        B.setVerticalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_SPREAD, 0, 60, 1);

        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);

        root.setOptimizationLevel(Optimizer.OPTIMIZATION_NONE);
        root.layout();
        System.out.println("root: " + root + " A: " + A + " B: " + B + " C: " + C);
        assertEquals(root.getWidth(), 200);
        assertEquals(root.getHeight(), 40);

        B.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_SPREAD, 20, 100, 1);
        B.setVerticalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_SPREAD, 30, 60, 1);
        root.setWidth(0);
        root.setHeight(0);
        root.layout();
        System.out.println("root: " + root + " A: " + A + " B: " + B + " C: " + C);
        assertEquals(root.getWidth(), 220);
        assertEquals(root.getHeight(), 70);
    }

    @Test
    public void testRatioWrap() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 100, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        root.add(A);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);

        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setDimensionRatio("1:1");

        root.setHeight(0);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);

        root.setOptimizationLevel(Optimizer.OPTIMIZATION_NONE);
        root.layout();
        System.out.println("root: " + root + " A: " + A);
        assertEquals(root.getWidth(), 100);
        assertEquals(root.getHeight(), 100);

        root.setHeight(600);
        root.setWidth(0);
        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        root.layout();
        System.out.println("root: " + root + " A: " + A);
        assertEquals(root.getWidth(), 600);
        assertEquals(root.getHeight(), 600);

        root.setWidth(100);
        root.setHeight(600);
        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);

        root.layout();
        System.out.println("root: " + root + " A: " + A);
        assertEquals(root.getWidth(), 0);
        assertEquals(root.getHeight(), 0);
    }

    @Test
    public void testRatioWrap2() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1000, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        root.add(A);
        root.add(B);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        B.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM);
        B.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);

        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setDimensionRatio("1:1");

        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);

        root.setOptimizationLevel(Optimizer.OPTIMIZATION_NONE);
        root.layout();
        System.out.println("root: " + root + " A: " + A + " B: " + B);
        assertEquals(root.getWidth(), 100);
        assertEquals(root.getHeight(), 120);
    }

    @Test
    public void testRatioWrap3() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 500, 600);
        ConstraintWidget A = new ConstraintWidget(100, 60);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        root.add(A);
        root.add(B);
        root.add(C);

        A.setBaselineDistance(100);
        B.setBaselineDistance(10);
        C.setBaselineDistance(10);

        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT);
        A.setVerticalBiasPercent(0);

        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.BASELINE, A, ConstraintAnchor.Type.BASELINE);
        B.connect(ConstraintAnchor.Type.RIGHT, C, ConstraintAnchor.Type.LEFT);

        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.RIGHT);
        C.connect(ConstraintAnchor.Type.BASELINE, B, ConstraintAnchor.Type.BASELINE);
        C.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);

        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setDimensionRatio("1:1");

        root.setOptimizationLevel(Optimizer.OPTIMIZATION_NONE);
        root.layout();
        System.out.println("root: " + root + " A: " + A + " B: " + B + " C: " + C);

        assertEquals(A.getWidth(), 300);
        assertEquals(A.getHeight(), 300);
        assertEquals(B.getLeft(), 300);
        assertEquals(B.getTop(), 90);
        assertEquals(B.getWidth(), 100);
        assertEquals(B.getHeight(), 20);
        assertEquals(C.getLeft(), 400);
        assertEquals(C.getTop(), 90);
        assertEquals(C.getWidth(), 100);
        assertEquals(C.getHeight(), 20);

        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        A.setBaselineDistance(10);

        root.layout();
        System.out.println("root: " + root + " A: " + A + " B: " + B + " C: " + C);
        assertEquals(root.getWidth(), 220);
        assertEquals(root.getHeight(), 20);
    }

    @Test
    public void testGoneChainWrap() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 500, 600);
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
        B.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        C.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        D.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);

        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, B, ConstraintAnchor.Type.TOP);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM);
        B.connect(ConstraintAnchor.Type.BOTTOM, C, ConstraintAnchor.Type.TOP);
        C.connect(ConstraintAnchor.Type.TOP, B, ConstraintAnchor.Type.BOTTOM);
        C.connect(ConstraintAnchor.Type.BOTTOM, D, ConstraintAnchor.Type.TOP);
        D.connect(ConstraintAnchor.Type.TOP, C, ConstraintAnchor.Type.BOTTOM);
        D.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);

        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        D.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);

        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_NONE);
        root.layout();
        System.out.println("root: " + root + " A: " + A + " B: " + B + " C: " + C + " D: " + D);
        assertEquals(root.getHeight(), 40);

        A.setVerticalChainStyle(ConstraintWidget.CHAIN_PACKED);
        root.layout();
        System.out.println("root: " + root + " A: " + A + " B: " + B + " C: " + C + " D: " + D);
        assertEquals(root.getHeight(), 40);
    }

    @Test
    public void testWrap() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 500, 600);
        ConstraintWidget A = new ConstraintWidget(100, 0);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        ConstraintWidget D = new ConstraintWidget(100, 40);
        ConstraintWidget E = new ConstraintWidget(100, 20);
        root.setDebugName("root");
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

        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        C.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        D.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);

        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.connect(ConstraintAnchor.Type.TOP, B, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, C, ConstraintAnchor.Type.BOTTOM);
        B.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        C.connect(ConstraintAnchor.Type.TOP, B, ConstraintAnchor.Type.BOTTOM);
        D.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM);

        E.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        E.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        E.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);

        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_NONE);
        root.layout();
        System.out.println("root: " + root + " A: " + A + " B: " + B + " C: " + C + " D: " + D + " E: " + E);
        assertEquals(root.getHeight(), 80);
        assertEquals(E.getTop(), 30);
    }

    @Test
    public void testWrap2() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 500, 600);
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
        B.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        C.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        D.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        C.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        D.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        A.connect(ConstraintAnchor.Type.TOP, C, ConstraintAnchor.Type.BOTTOM, 30);
        A.connect(ConstraintAnchor.Type.BOTTOM, D, ConstraintAnchor.Type.TOP, 40);
        B.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        B.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.layout();
        System.out.println("root: " + root + " A: " + A + " B: " + B + " C: " + C + " D: " + D);
        assertEquals(C.getTop(), 0);
        assertEquals(A.getTop(), C.getBottom() + 30);
        assertEquals(D.getTop(), A.getBottom() + 40);
        assertEquals(root.getHeight(), 20 + 30 + 20 + 40 + 20);

    }

    @Test
    public void testWrap3() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 500, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        root.add(A);
        root.add(B);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, 200);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.LEFT, 250);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM);
        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.layout();
        System.out.println("root: " + root + " A: " + A + " B: " + B);
        assertEquals(root.getWidth(), A.getWidth() + 200);
        assertEquals(A.getLeft(), 0);
        assertEquals(B.getLeft(), 250);
        assertEquals(B.getWidth(), 100);
        assertEquals(B.getRight() > root.getWidth(), true);
    }

    @Test
    public void testWrap4() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 500, 600);
        ConstraintWidget A = new ConstraintWidget(80, 80);
        ConstraintWidget B = new ConstraintWidget(60, 60);
        ConstraintWidget C = new ConstraintWidget(50, 100);
        Barrier barrier1 = new Barrier();
        barrier1.setBarrierType(Barrier.BOTTOM);
        Barrier barrier2 = new Barrier();
        barrier2.setBarrierType(Barrier.BOTTOM);

        barrier1.add(A);
        barrier1.add(B);

        barrier2.add(C);

        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        barrier1.setDebugName("B1");
        barrier2.setDebugName("B2");

        root.add(A);
        root.add(B);
        root.add(C);
        root.add(barrier1);
        root.add(barrier2);

        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, barrier1, ConstraintAnchor.Type.BOTTOM);
        B.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        B.connect(ConstraintAnchor.Type.BOTTOM, barrier1, ConstraintAnchor.Type.BOTTOM);

        C.connect(ConstraintAnchor.Type.TOP, barrier1, ConstraintAnchor.Type.BOTTOM);
        C.connect(ConstraintAnchor.Type.BOTTOM, barrier2, ConstraintAnchor.Type.TOP);

        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);

        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.layout();
        System.out.println("root: " + root);
        System.out.println("A: " + A);
        System.out.println("B: " + B);
        System.out.println("C: " + C);
        System.out.println("B1: " + barrier1);
        System.out.println("B2: " + barrier2);
        assertEquals(A.getTop() >= 0, true);
        assertEquals(B.getTop() >= 0, true);
        assertEquals(C.getTop() >= 0, true);
        assertEquals(root.getHeight(), Math.max(A.getHeight(), B.getHeight()) + C.getHeight());

    }

    @Test
    public void testWrap5() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 500, 600);
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

        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 8);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 8);

        B.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, 8);
        B.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM, 8);

        C.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        C.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT);
        C.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM);
        C.connect(ConstraintAnchor.Type.BOTTOM, C, ConstraintAnchor.Type.TOP);
        D.setHorizontalBiasPercent(0.557f);
        D.setVerticalBiasPercent(0.8f);

        D.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        D.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT);
        D.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM);
        D.connect(ConstraintAnchor.Type.BOTTOM, C, ConstraintAnchor.Type.TOP);
        D.setHorizontalBiasPercent(0.557f);
        D.setVerticalBiasPercent(0.28f);

        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.layout();
        System.out.println("root: " + root);
        System.out.println("A: " + A);
        System.out.println("B: " + B);
        System.out.println("C: " + C);
        System.out.println("D: " + D);
    }

    @Test
    public void testWrap6() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 500, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        Guideline guideline = new Guideline();
        guideline.setOrientation(ConstraintWidget.VERTICAL);
        guideline.setGuidePercent(0.5f);
        root.setDebugName("root");
        A.setDebugName("A");
        guideline.setDebugName("guideline");

        root.add(A);
        root.add(guideline);

        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 8);
        A.connect(ConstraintAnchor.Type.LEFT, guideline, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_WRAP, 0, 0, 0);

        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.layout();
        System.out.println("root: " + root);
        System.out.println("A: " + A);
        System.out.println("guideline: " + guideline);

        assertEquals(root.getWidth(), A.getWidth() * 2);
        assertEquals(root.getHeight(), A.getHeight() + 8);
        assertEquals((float) guideline.getLeft(), root.getWidth() / 2f);
        assertEquals(A.getWidth(), 100);
        assertEquals(A.getHeight(), 20);
    }

    @Test
    public void testWrap7() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 500, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget divider = new ConstraintWidget(1, 20);
        Guideline guideline = new Guideline();
        guideline.setOrientation(ConstraintWidget.VERTICAL);
        guideline.setGuidePercent(0.5f);
        root.setDebugName("root");
        A.setDebugName("A");
        divider.setDebugName("divider");
        guideline.setDebugName("guideline");

        root.add(A);
        root.add(divider);
        root.add(guideline);

        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);

        A.connect(ConstraintAnchor.Type.LEFT, guideline, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);

        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_WRAP, 0, 0, 0);

        divider.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        divider.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        divider.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        divider.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        divider.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);

        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.layout();
        System.out.println("root: " + root);
        System.out.println("A: " + A);
        System.out.println("divider: " + divider);
        System.out.println("guideline: " + guideline);

        assertEquals(root.getWidth(), A.getWidth() * 2);
        assertEquals(root.getHeight(), A.getHeight());
        assertEquals((float) guideline.getLeft(), root.getWidth() / 2f);
        assertEquals(A.getWidth(), 100);
        assertEquals(A.getHeight(), 20);
    }

    @Test
    public void testWrap8() {
        // check_048
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1080, 1080);
        ConstraintWidget button56 = new ConstraintWidget(231, 126);
        ConstraintWidget button60 = new ConstraintWidget(231, 126);
        ConstraintWidget button63 = new ConstraintWidget(368, 368);
        ConstraintWidget button65 = new ConstraintWidget(231, 126);

        button56.setDebugName("button56");
        button60.setDebugName("button60");
        button63.setDebugName("button63");
        button65.setDebugName("button65");

        root.add(button56);
        root.add(button60);
        root.add(button63);
        root.add(button65);

        button56.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 42);
        button56.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 42);
        //button56.setBaselineDistance(77);

        button60.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, 42);
        button60.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM, 79);
        //button60.setBaselineDistance(77);

        button63.connect(ConstraintAnchor.Type.LEFT, button56, ConstraintAnchor.Type.RIGHT, 21);
        button63.connect(ConstraintAnchor.Type.RIGHT, button60, ConstraintAnchor.Type.LEFT, 21);
        button63.connect(ConstraintAnchor.Type.TOP, button56, ConstraintAnchor.Type.BOTTOM, 21);
        button63.connect(ConstraintAnchor.Type.BOTTOM, button60, ConstraintAnchor.Type.TOP, 21);
        //button63.setBaselineDistance(155);
        button63.setVerticalBiasPercent(0.8f);

        button65.connect(ConstraintAnchor.Type.LEFT, button56, ConstraintAnchor.Type.RIGHT, 21);
        button65.connect(ConstraintAnchor.Type.RIGHT, button60, ConstraintAnchor.Type.LEFT, 21);
        button65.connect(ConstraintAnchor.Type.TOP, button56, ConstraintAnchor.Type.BOTTOM, 21);
        button65.connect(ConstraintAnchor.Type.BOTTOM, button60, ConstraintAnchor.Type.TOP, 21);
        //button65.setBaselineDistance(77);
        button65.setVerticalBiasPercent(0.28f);

        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.layout();
        System.out.println("root: " + root);
        System.out.println("button56: " + button56);
        System.out.println("button60: " + button60);
        System.out.println("button63: " + button63);
        System.out.println("button65: " + button65);

        assertEquals(root.getWidth(), 1080);
        assertEquals(root.getHeight(), 783);
        assertEquals(button56.getLeft(), 42);
        assertEquals(button56.getTop(), 42);
        assertEquals(button60.getLeft(), 807);
        assertEquals(button60.getTop(), 578);
        assertEquals(button63.getLeft(), 356);
        assertEquals(button63.getTop(), 189);
        assertEquals(button65.getLeft(), 425);
        assertEquals(button65.getTop(), 257);
    }

    @Test
    public void testWrap9() {
        // b/161826272
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1080, 1080);
        ConstraintWidget text = new ConstraintWidget(270, 30);
        ConstraintWidget view = new ConstraintWidget(10, 10);

        root.setDebugName("root");
        text.setDebugName("text");
        view.setDebugName("view");

        root.add(text);
        root.add(view);

        text.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        text.connect(ConstraintAnchor.Type.TOP, view, ConstraintAnchor.Type.TOP);

        view.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        view.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        view.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        view.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);

        view.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        view.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        view.setVerticalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_PERCENT, 0, 0, 0.2f);
        view.setDimensionRatio("1:1");

        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);

        root.layout();
        System.out.println("root: " + root);
        System.out.println("text: " + text);
        System.out.println("view: " + view);

        assertEquals(view.getWidth(), view.getHeight());
        assertEquals(view.getHeight(), (int) (0.2 * root.getHeight()));
        assertEquals(root.getWidth(), Math.max(text.getWidth(), view.getWidth()));
    }

    @Test
    public void testBarrierWrap() {
        // b/165028374

        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1080, 1080);
        ConstraintWidget view = new ConstraintWidget(200, 200);
        ConstraintWidget space = new ConstraintWidget(50, 50);
        ConstraintWidget button = new ConstraintWidget(100, 80);
        ConstraintWidget text = new ConstraintWidget(90, 30);

        Barrier barrier = new Barrier();
        barrier.setBarrierType(Barrier.BOTTOM);
        barrier.add(button);
        barrier.add(space);

        root.setDebugName("root");
        view.setDebugName("view");
        space.setDebugName("space");
        button.setDebugName("button");
        text.setDebugName("text");
        barrier.setDebugName("barrier");

        root.add(view);
        root.add(space);
        root.add(button);
        root.add(text);
        root.add(barrier);

        view.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        space.connect(ConstraintAnchor.Type.TOP, view, ConstraintAnchor.Type.BOTTOM);
        button.connect(ConstraintAnchor.Type.TOP, view, ConstraintAnchor.Type.BOTTOM);
        button.connect(ConstraintAnchor.Type.BOTTOM, text, ConstraintAnchor.Type.TOP);
        text.connect(ConstraintAnchor.Type.TOP, barrier, ConstraintAnchor.Type.BOTTOM);
        text.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);

        button.setVerticalBiasPercent(1f);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);

        root.layout();
        System.out.println("root: " + root);
        System.out.println("view: " + view);
        System.out.println("space: " + space);
        System.out.println("button: " + button);
        System.out.println("barrier: " + barrier);
        System.out.println("text: " + text);

        assertEquals(view.getTop(), 0);
        assertEquals(view.getBottom(), 200);
        assertEquals(space.getTop(), 200);
        assertEquals(space.getBottom(), 250);
        assertEquals(button.getTop(), 200);
        assertEquals(button.getBottom(), 280);
        assertEquals(barrier.getTop(), 280);
        assertEquals(text.getTop(), barrier.getTop());
    }

}
