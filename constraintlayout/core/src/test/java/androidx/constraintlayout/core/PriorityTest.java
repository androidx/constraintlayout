package androidx.constraintlayout.core;

import androidx.constraintlayout.core.widgets.ConstraintAnchor;
import androidx.constraintlayout.core.widgets.ConstraintWidget;
import androidx.constraintlayout.core.widgets.ConstraintWidgetContainer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PriorityTest {

    @Test
    public void testPriorityChainHorizontal() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1000, 600);
        ConstraintWidget A = new ConstraintWidget(400, 20);
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

        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.RIGHT, C, ConstraintAnchor.Type.LEFT);

        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.RIGHT);
        C.connect(ConstraintAnchor.Type.RIGHT, A, ConstraintAnchor.Type.RIGHT);

        B.setHorizontalChainStyle(ConstraintWidget.CHAIN_PACKED);
        root.layout();
        System.out.println("a) root: " + root + " A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getWidth(), 400);
        assertEquals(B.getWidth(), 100);
        assertEquals(C.getWidth(), 100);
        assertEquals(A.getLeft(), 300);
        assertEquals(B.getLeft(), 400);
        assertEquals(C.getLeft(), 500);

        B.setHorizontalChainStyle(ConstraintWidget.CHAIN_SPREAD);
        root.layout();
        System.out.println("b) root: " + root + " A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getWidth(), 400);
        assertEquals(B.getWidth(), 100);
        assertEquals(C.getWidth(), 100);
        assertEquals(A.getLeft(), 300);
        assertEquals(B.getLeft(), 367);
        assertEquals(C.getLeft(), 533);

        B.setHorizontalChainStyle(ConstraintWidget.CHAIN_SPREAD_INSIDE);
        root.layout();
        System.out.println("c) root: " + root + " A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getWidth(), 400);
        assertEquals(B.getWidth(), 100);
        assertEquals(C.getWidth(), 100);
        assertEquals(A.getLeft(), 300);
        assertEquals(B.getLeft(), 300);
        assertEquals(C.getLeft(), 600);
    }

    @Test
    public void testPriorityChainVertical() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1000, 1000);
        ConstraintWidget A = new ConstraintWidget(400, 400);
        ConstraintWidget B = new ConstraintWidget(100, 100);
        ConstraintWidget C = new ConstraintWidget(100, 100);
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        root.add(A);
        root.add(B);
        root.add(C);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);

        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.TOP);
        B.connect(ConstraintAnchor.Type.BOTTOM, C, ConstraintAnchor.Type.TOP);

        C.connect(ConstraintAnchor.Type.TOP, B, ConstraintAnchor.Type.BOTTOM);
        C.connect(ConstraintAnchor.Type.BOTTOM, A, ConstraintAnchor.Type.BOTTOM);

        B.setVerticalChainStyle(ConstraintWidget.CHAIN_PACKED);
        root.layout();
        System.out.println("a) root: " + root + " A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getHeight(), 400);
        assertEquals(B.getHeight(), 100);
        assertEquals(C.getHeight(), 100);
        assertEquals(A.getTop(), 300);
        assertEquals(B.getTop(), 400);
        assertEquals(C.getTop(), 500);

        B.setVerticalChainStyle(ConstraintWidget.CHAIN_SPREAD);
        root.layout();
        System.out.println("b) root: " + root + " A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getHeight(), 400);
        assertEquals(B.getHeight(), 100);
        assertEquals(C.getHeight(), 100);
        assertEquals(A.getTop(), 300);
        assertEquals(B.getTop(), 367);
        assertEquals(C.getTop(), 533);

        B.setVerticalChainStyle(ConstraintWidget.CHAIN_SPREAD_INSIDE);
        root.layout();
        System.out.println("c) root: " + root + " A: " + A + " B: " + B + " C: " + C);
        assertEquals(A.getHeight(), 400);
        assertEquals(B.getHeight(), 100);
        assertEquals(C.getHeight(), 100);
        assertEquals(A.getTop(), 300);
        assertEquals(B.getTop(), 300);
        assertEquals(C.getTop(), 600);
    }
}

