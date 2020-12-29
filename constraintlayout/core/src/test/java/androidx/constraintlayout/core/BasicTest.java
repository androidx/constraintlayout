package androidx.constraintlayout.core;

import androidx.constraintlayout.core.widgets.Barrier;
import androidx.constraintlayout.core.widgets.ConstraintAnchor;
import androidx.constraintlayout.core.widgets.ConstraintWidget;
import androidx.constraintlayout.core.widgets.ConstraintWidgetContainer;
import androidx.constraintlayout.core.widgets.Guideline;
import androidx.constraintlayout.core.widgets.Optimizer;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;

import org.testng.annotations.Test;

import java.util.ArrayList;

import static androidx.constraintlayout.core.widgets.analyzer.BasicMeasure.EXACTLY;
import static androidx.constraintlayout.core.widgets.analyzer.BasicMeasure.WRAP_CONTENT;
import static org.testng.Assert.assertEquals;

public class BasicTest {

    static BasicMeasure.Measurer sMeasurer = new BasicMeasure.Measurer() {

        @Override
        public void measure(ConstraintWidget widget, BasicMeasure.Measure measure) {
            ConstraintWidget.DimensionBehaviour horizontalBehavior = measure.horizontalBehavior;
            ConstraintWidget.DimensionBehaviour verticalBehavior = measure.verticalBehavior;
            int horizontalDimension = measure.horizontalDimension;
            int verticalDimension = measure.verticalDimension;

            if (horizontalBehavior == ConstraintWidget.DimensionBehaviour.FIXED) {
                measure.measuredWidth = horizontalDimension;
            } else if (horizontalBehavior == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                measure.measuredWidth = horizontalDimension;
            }
            if (verticalBehavior == ConstraintWidget.DimensionBehaviour.FIXED) {
                measure.measuredHeight = verticalDimension;
            }
        }

        @Override
        public void didMeasures() {

        }
    };

    @Test
    public void testWrapPercent() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 800);
        ConstraintWidget A = new ConstraintWidget(100, 30);
        root.setDebugName("root");
        A.setDebugName("A");

        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_PERCENT, WRAP_CONTENT, 0, 0.5f);

        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);

        root.add(A);

        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.layout();

        System.out.println("root: " + root);
        System.out.println("A: " + A);
        assertEquals(A.getWidth(), 100);
        assertEquals(root.getWidth(), A.getWidth() * 2);
    }

    @Test
    public void testMiddleSplit() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 800);
        root.setMeasurer(sMeasurer);
        ConstraintWidget A = new ConstraintWidget(400, 30);
        ConstraintWidget B = new ConstraintWidget(400, 60);
        Guideline guideline = new Guideline();
        ConstraintWidget divider = new ConstraintWidget(100, 30);

        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        guideline.setDebugName("guideline");
        divider.setDebugName("divider");

        root.add(A);
        root.add(B);
        root.add(guideline);
        root.add(divider);

        guideline.setOrientation(Guideline.VERTICAL);
        guideline.setGuidePercent(0.5f);

        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_WRAP,0,0,0);
        B.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_WRAP,0,0,0);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, guideline, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.LEFT, guideline, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        divider.setWidth(1);
        divider.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        divider.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        divider.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);

        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
//        root.layout();
        root.updateHierarchy();
        root.measure(Optimizer.OPTIMIZATION_NONE, EXACTLY, 600, EXACTLY, 800, 0, 0, 0, 0);
        System.out.println("root: " + root);
        System.out.println("A: " + A);
        System.out.println("B: " + B);
        System.out.println("guideline: " + guideline);
        System.out.println("divider: " + divider);

        assertEquals(A.getWidth(), 300);
        assertEquals(B.getWidth(), 300);
        assertEquals(A.getLeft(), 0);
        assertEquals(B.getLeft(), 300);
        assertEquals(divider.getHeight(), 60);
        assertEquals(root.getWidth(), 600);
        assertEquals(root.getHeight(), 60);
    }

    @Test
    public void testSimpleConstraint() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 800);
        root.setMeasurer(sMeasurer);
        ConstraintWidget A = new ConstraintWidget( 100, 30);

        root.setDebugName("root");
        A.setDebugName("A");

        root.add(A);

        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);

        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 8);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 8);

        root.setOptimizationLevel(Optimizer.OPTIMIZATION_GRAPH);
        root.measure(Optimizer.OPTIMIZATION_GRAPH, 0, 0, 0, 0, 0, 0, 0, 0);
//        root.layout();

        System.out.println("1) A: " + A);
    }

    @Test
    public void testSimpleWrapConstraint9() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 800);
        root.setMeasurer(sMeasurer);
        ConstraintWidget A = new ConstraintWidget( 100, 30);

        root.setDebugName("root");
        A.setDebugName("A");

        root.add(A);

        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);

        int margin = 8;
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, margin);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, margin);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM, margin);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, margin);


        root.setOptimizationLevel(Optimizer.OPTIMIZATION_GRAPH_WRAP);
        root.measure(Optimizer.OPTIMIZATION_GRAPH_WRAP, 0, 0, 0, 0, 0, 0, 0, 0);
//        root.layout();

        System.out.println("root: " + root);
        System.out.println("1) A: " + A);

        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.measure(Optimizer.OPTIMIZATION_GRAPH_WRAP, 0, 0, 0, 0, 0, 0, 0, 0);

        System.out.println("root: " + root);
        System.out.println("1) A: " + A);
        assertEquals(root.getWidth(), 116);
        assertEquals(root.getHeight(), 46);
    }

    @Test
    public void testSimpleWrapConstraint10() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 800);
        root.setMeasurer(sMeasurer);
        ConstraintWidget A = new ConstraintWidget( 100, 30);

        root.setDebugName("root");
        A.setDebugName("A");

        root.add(A);

        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);

        int margin = 8;
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, margin);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, margin);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM, margin);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, margin);


        //root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);

        root.measure(Optimizer.OPTIMIZATION_NONE, 0, 0, 0, 0, 0, 0, 0, 0);
        root.layout();

        System.out.println("root: " + root);
        System.out.println("1) A: " + A);

        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.measure(Optimizer.OPTIMIZATION_GRAPH, BasicMeasure.WRAP_CONTENT, 0, EXACTLY, 800, 0, 0, 0, 0);

        System.out.println("root: " + root);
        System.out.println("1) A: " + A);
        assertEquals(root.getWidth(), 116);
        assertEquals(root.getHeight(), 800);
        assertEquals(A.getLeft(), 8);
        assertEquals(A.getTop(), 385);
        assertEquals(A.getWidth(), 100);
        assertEquals(A.getHeight(), 30);
    }

    @Test
    public void testSimpleWrapConstraint11() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 800);
        root.setMeasurer(sMeasurer);
        ConstraintWidget A = new ConstraintWidget( 10, 30);
        ConstraintWidget B = new ConstraintWidget( 800, 30);
        ConstraintWidget C = new ConstraintWidget( 10, 30);
        ConstraintWidget D = new ConstraintWidget( 800, 30);

        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        D.setDebugName("D");

        root.add(A);
        root.add(B);
        root.add(C);
        root.add(D);

        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        B.setHorizontalMatchStyle(ConstraintWidget.MATCH_CONSTRAINT_WRAP, 0, 0, 0);


        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        B.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        C.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        D.connect(ConstraintAnchor.Type.TOP, B, ConstraintAnchor.Type.BOTTOM);

        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.RIGHT, C, ConstraintAnchor.Type.LEFT);
        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.RIGHT);
        C.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        D.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        D.connect(ConstraintAnchor.Type.RIGHT, C, ConstraintAnchor.Type.LEFT);

        root.layout();

        System.out.println("root: " + root);
        System.out.println("1) A: " + A);
        System.out.println("1) B: " + B);
        System.out.println("1) C: " + C);
        System.out.println("1) D: " + D);

        assertEquals(A.getLeft(), 0);
        assertEquals(A.getWidth(), 10);
        assertEquals(C.getWidth(), 10);
        assertEquals(B.getLeft(), A.getRight());
        assertEquals(B.getWidth(), root.getWidth() - A.getWidth() - C.getWidth());
        assertEquals(C.getLeft(), root.getWidth() - C.getWidth());
        assertEquals(D.getWidth(), 800);
        assertEquals(D.getLeft(), -99);
    }

    @Test
    public void testSimpleWrapConstraint() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 800);
        root.setMeasurer(sMeasurer);
        ConstraintWidget A = new ConstraintWidget( 100, 30);
        ConstraintWidget B = new ConstraintWidget( 100, 60);

        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");

        root.add(A);
        root.add(B);

        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);

        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 8);
        B.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM, 8);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 8);
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT, 8);

        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.measure(Optimizer.OPTIMIZATION_STANDARD, BasicMeasure.WRAP_CONTENT, 0, BasicMeasure.WRAP_CONTENT, 0, 0, 0, 0, 0);

        System.out.println("0) root: " + root);
        System.out.println("1) A: " + A);
        System.out.println("2) B: " + B);
        assertEquals(root.getWidth(), 216);
        assertEquals(root.getHeight(), 68);
        assertEquals(A.getLeft(), 8);
        assertEquals(A.getTop(), 8);
        assertEquals(A.getWidth(), 100);
        assertEquals(A.getHeight(), 30);
        assertEquals(B.getLeft(), 116);
        assertEquals(B.getTop(), 0);
        assertEquals(B.getWidth(), 100);
        assertEquals(B.getHeight(), 60);

        root.measure(Optimizer.OPTIMIZATION_GRAPH, BasicMeasure.WRAP_CONTENT, 0, BasicMeasure.WRAP_CONTENT, 0, 0, 0, 0, 0);
        System.out.println("0) root: " + root);
        System.out.println("1) A: " + A);
        System.out.println("2) B: " + B);
        assertEquals(root.getWidth(), 216);
        assertEquals(root.getHeight(), 68);
        assertEquals(A.getLeft(), 8);
        assertEquals(A.getTop(), 8);
        assertEquals(A.getWidth(), 100);
        assertEquals(A.getHeight(), 30);
        assertEquals(B.getLeft(), 116);
        assertEquals(B.getTop(), 0);
        assertEquals(B.getWidth(), 100);
        assertEquals(B.getHeight(), 60);
    }


    @Test
    public void testSimpleWrapConstraint2() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 800);
        root.setMeasurer(sMeasurer);
        ConstraintWidget A = new ConstraintWidget( 100, 30);
        ConstraintWidget B = new ConstraintWidget( 120, 60);

        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");

        root.add(A);
        root.add(B);

        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);

        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 8);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM, 8);
        B.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM, 8);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 8);
        B.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 8);

        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.measure(Optimizer.OPTIMIZATION_STANDARD, BasicMeasure.WRAP_CONTENT, 0, BasicMeasure.WRAP_CONTENT, 0, 0, 0, 0, 0);
//        root.layout();

        System.out.println("0) root: " + root);
        System.out.println("1) A: " + A);
        System.out.println("2) B: " + B);
        assertEquals(root.getWidth(), 128);
        assertEquals(root.getHeight(), 114);
        assertEquals(A.getLeft(), 8);
        assertEquals(A.getTop(), 8);
        assertEquals(A.getWidth(), 100);
        assertEquals(A.getHeight(), 30);
        assertEquals(B.getLeft(), 8);
        assertEquals(B.getTop(), 46);
        assertEquals(B.getWidth(), 120);
        assertEquals(B.getHeight(), 60);

        root.measure(Optimizer.OPTIMIZATION_GRAPH, BasicMeasure.WRAP_CONTENT, 0, BasicMeasure.WRAP_CONTENT, 0, 0, 0, 0, 0);
        System.out.println("0) root: " + root);
        System.out.println("1) A: " + A);
        System.out.println("2) B: " + B);
        assertEquals(root.getWidth(), 128);
        assertEquals(root.getHeight(), 114);
        assertEquals(A.getLeft(), 8);
        assertEquals(A.getTop(), 8);
        assertEquals(A.getWidth(), 100);
        assertEquals(A.getHeight(), 30);
        assertEquals(B.getLeft(), 8);
        assertEquals(B.getTop(), 46);
        assertEquals(B.getWidth(), 120);
        assertEquals(B.getHeight(), 60);
    }

    @Test
    public void testSimpleWrapConstraint3() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 800);
        root.setMeasurer(sMeasurer);
        ConstraintWidget A = new ConstraintWidget(100, 30);

        root.setDebugName("root");
        A.setDebugName("A");

        root.add(A);

        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);

        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 8);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM, 8);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 8);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, 8);

        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);

        root.measure(Optimizer.OPTIMIZATION_STANDARD, BasicMeasure.WRAP_CONTENT, 0, BasicMeasure.WRAP_CONTENT, 0, 0, 0, 0, 0);

        System.out.println("0) root: " + root);
        System.out.println("1) A: " + A);
        assertEquals(root.getWidth(), 116);
        assertEquals(root.getHeight(), 46);
        assertEquals(A.getLeft(), 8);
        assertEquals(A.getTop(), 8);
        assertEquals(A.getWidth(), 100);
        assertEquals(A.getHeight(), 30);

        root.measure(Optimizer.OPTIMIZATION_GRAPH, BasicMeasure.WRAP_CONTENT, 0, BasicMeasure.WRAP_CONTENT, 0, 0, 0, 0, 0);
        System.out.println("0) root: " + root);
        System.out.println("1) A: " + A);
        assertEquals(root.getWidth(), 116);
        assertEquals(root.getHeight(), 46);
        assertEquals(A.getLeft(), 8);
        assertEquals(A.getTop(), 8);
        assertEquals(A.getWidth(), 100);
        assertEquals(A.getHeight(), 30);

    }

    @Test
    public void testSimpleWrapConstraint4() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 800);
        root.setMeasurer(sMeasurer);
        ConstraintWidget A = new ConstraintWidget(100, 30);
        ConstraintWidget B = new ConstraintWidget(100, 30);
        ConstraintWidget C = new ConstraintWidget(100, 30);
        ConstraintWidget D = new ConstraintWidget(100, 30);

        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        D.setDebugName("D");

        root.add(A);
        root.add(B);
        root.add(C);
        root.add(D);

        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        C.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        C.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        D.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        D.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);

        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 8);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 8);

        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM, 8);
        B.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 8);
        B.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, 8);

        C.connect(ConstraintAnchor.Type.BOTTOM, B, ConstraintAnchor.Type.TOP, 8);
        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.RIGHT, 8);

        D.connect(ConstraintAnchor.Type.BOTTOM, C, ConstraintAnchor.Type.TOP, 8);
        D.connect(ConstraintAnchor.Type.LEFT, C, ConstraintAnchor.Type.RIGHT, 8);

        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);


        root.measure(Optimizer.OPTIMIZATION_STANDARD, BasicMeasure.WRAP_CONTENT, 0, BasicMeasure.WRAP_CONTENT, 0, 0, 0, 0, 0);

        System.out.println("0) root: " + root);
        System.out.println("1) A: " + A);
        System.out.println("2) B: " + B);
        System.out.println("3) C: " + C);
        System.out.println("4) D: " + D);
        assertEquals(root.getWidth(), 532);
        assertEquals(root.getHeight(), 76);
        assertEquals(A.getLeft(), 8);
        assertEquals(A.getTop(), 8);
        assertEquals(A.getWidth(), 100);
        assertEquals(A.getHeight(), 30);
        assertEquals(B.getLeft(), 216);
        assertEquals(B.getTop(), 46);
        assertEquals(B.getWidth(), 100);
        assertEquals(B.getHeight(), 30);
        assertEquals(C.getLeft(), 324);
        assertEquals(C.getTop(), 8);
        assertEquals(C.getWidth(), 100);
        assertEquals(C.getHeight(), 30);
        assertEquals(D.getLeft(), 432);
        assertEquals(D.getTop(), -28, 2);
        assertEquals(D.getWidth(), 100);
        assertEquals(D.getHeight(),  30);

        root.measure(Optimizer.OPTIMIZATION_GRAPH, BasicMeasure.WRAP_CONTENT, 0, BasicMeasure.WRAP_CONTENT, 0, 0, 0, 0, 0);
        System.out.println("0) root: " + root);
        System.out.println("1) A: " + A);
        System.out.println("2) B: " + B);
        System.out.println("3) C: " + C);
        System.out.println("4) D: " + D);
        assertEquals(root.getWidth(), 532);
        assertEquals(root.getHeight(), 76);
        assertEquals(A.getLeft(), 8);
        assertEquals(A.getTop(), 8);
        assertEquals(A.getWidth(), 100);
        assertEquals(A.getHeight(), 30);
        assertEquals(B.getLeft(), 216);
        assertEquals(B.getTop(), 46);
        assertEquals(B.getWidth(), 100);
        assertEquals(B.getHeight(), 30);
        assertEquals(C.getLeft(), 324);
        assertEquals(C.getTop(), 8);
        assertEquals(C.getWidth(), 100);
        assertEquals(C.getHeight(), 30);
        assertEquals(D.getLeft(), 432);
        assertEquals(D.getTop(), -28, 2);
        assertEquals(D.getWidth(), 100);
        assertEquals(D.getHeight(),  30);
    }

    @Test
    public void testSimpleWrapConstraint5() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 800);
        root.setMeasurer(sMeasurer);
        ConstraintWidget A = new ConstraintWidget(100, 30);
        ConstraintWidget B = new ConstraintWidget(100, 30);
        ConstraintWidget C = new ConstraintWidget(100, 30);
        ConstraintWidget D = new ConstraintWidget(100, 30);

        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        D.setDebugName("D");

        root.add(A);
        root.add(B);
        root.add(C);
        root.add(D);

        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        C.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        C.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        D.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        D.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);

        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 8);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 8);

        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM, 8);
        B.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 8);
        B.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, 8);
        B.setHorizontalBiasPercent(0.2f);

        C.connect(ConstraintAnchor.Type.BOTTOM, B, ConstraintAnchor.Type.TOP, 8);
        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.RIGHT, 8);

        D.connect(ConstraintAnchor.Type.BOTTOM, C, ConstraintAnchor.Type.TOP, 8);
        D.connect(ConstraintAnchor.Type.LEFT, C, ConstraintAnchor.Type.RIGHT, 8);

        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);


        root.measure(Optimizer.OPTIMIZATION_STANDARD, BasicMeasure.WRAP_CONTENT, 0, BasicMeasure.WRAP_CONTENT, 0, 0, 0, 0, 0);

        System.out.println("0) root: " + root);
        System.out.println("1) A: " + A);
        System.out.println("2) B: " + B);
        System.out.println("3) C: " + C);
        System.out.println("4) D: " + D);
        assertEquals(root.getWidth(), 376);
        assertEquals(root.getHeight(), 76);
        assertEquals(A.getLeft(), 8);
        assertEquals(A.getTop(), 8);
        assertEquals(A.getWidth(), 100);
        assertEquals(A.getHeight(), 30);
        assertEquals(B.getLeft(), 60);
        assertEquals(B.getTop(), 46);
        assertEquals(B.getWidth(), 100);
        assertEquals(B.getHeight(), 30);
        assertEquals(C.getLeft(), 168);
        assertEquals(C.getTop(), 8);
        assertEquals(C.getWidth(), 100);
        assertEquals(C.getHeight(), 30);
        assertEquals(D.getLeft(), 276);
        assertEquals(D.getTop(), -28, 2);
        assertEquals(D.getWidth(), 100);
        assertEquals(D.getHeight(),  30);

        root.measure(Optimizer.OPTIMIZATION_GRAPH, BasicMeasure.WRAP_CONTENT, 0, BasicMeasure.WRAP_CONTENT, 0, 0, 0, 0, 0);
        System.out.println("0) root: " + root);
        System.out.println("1) A: " + A);
        System.out.println("2) B: " + B);
        System.out.println("3) C: " + C);
        System.out.println("4) D: " + D);
        assertEquals(root.getWidth(), 376);
        assertEquals(root.getHeight(), 76);
        assertEquals(A.getLeft(), 8);
        assertEquals(A.getTop(), 8);
        assertEquals(A.getWidth(), 100);
        assertEquals(A.getHeight(), 30);
        assertEquals(B.getLeft(), 60);
        assertEquals(B.getTop(), 46);
        assertEquals(B.getWidth(), 100);
        assertEquals(B.getHeight(), 30);
        assertEquals(C.getLeft(), 168);
        assertEquals(C.getTop(), 8);
        assertEquals(C.getWidth(), 100);
        assertEquals(C.getHeight(), 30);
        assertEquals(D.getLeft(), 276);
        assertEquals(D.getTop(), -28, 2);
        assertEquals(D.getWidth(), 100);
        assertEquals(D.getHeight(),  30);
    }

    @Test
    public void testSimpleWrapConstraint6() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 800);
        root.setMeasurer(sMeasurer);
        ConstraintWidget A = new ConstraintWidget(100, 30);
        ConstraintWidget B = new ConstraintWidget(100, 30);
        ConstraintWidget C = new ConstraintWidget(100, 30);
        ConstraintWidget D = new ConstraintWidget(100, 30);

        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        D.setDebugName("D");

        root.add(A);
        root.add(B);
        root.add(C);
        root.add(D);

        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        C.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        C.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        D.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        D.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);

        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 8);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 8);

        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM, 8);
        B.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 33);
        B.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, 16);
        B.setHorizontalBiasPercent(0.15f);

        C.connect(ConstraintAnchor.Type.BOTTOM, B, ConstraintAnchor.Type.TOP, 8);
        C.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.RIGHT, 12);

        D.connect(ConstraintAnchor.Type.BOTTOM, C, ConstraintAnchor.Type.TOP, 8);
        D.connect(ConstraintAnchor.Type.LEFT, C, ConstraintAnchor.Type.RIGHT, 8);

        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);


        root.measure(Optimizer.OPTIMIZATION_STANDARD, BasicMeasure.WRAP_CONTENT, 0, BasicMeasure.WRAP_CONTENT, 0, 0, 0, 0, 0);

        System.out.println("0) root: " + root);
        System.out.println("1) A: " + A);
        System.out.println("2) B: " + B);
        System.out.println("3) C: " + C);
        System.out.println("4) D: " + D);
        assertEquals(root.getWidth(), 389);
        assertEquals(root.getHeight(), 76);
        assertEquals(A.getLeft(), 8);
        assertEquals(A.getTop(), 8);
        assertEquals(A.getWidth(), 100);
        assertEquals(A.getHeight(), 30);
        assertEquals(B.getLeft(), 69);
        assertEquals(B.getTop(), 46);
        assertEquals(B.getWidth(), 100);
        assertEquals(B.getHeight(), 30);
        assertEquals(C.getLeft(), 181);
        assertEquals(C.getTop(), 8);
        assertEquals(C.getWidth(), 100);
        assertEquals(C.getHeight(), 30);
        assertEquals(D.getLeft(), 289);
        assertEquals(D.getTop(), -28, 2);
        assertEquals(D.getWidth(), 100);
        assertEquals(D.getHeight(),  30);

        root.measure(Optimizer.OPTIMIZATION_GRAPH, BasicMeasure.WRAP_CONTENT, 0, BasicMeasure.WRAP_CONTENT, 0, 0, 0, 0, 0);
        System.out.println("0) root: " + root);
        System.out.println("1) A: " + A);
        System.out.println("2) B: " + B);
        System.out.println("3) C: " + C);
        System.out.println("4) D: " + D);
        assertEquals(root.getWidth(), 389);
        assertEquals(root.getHeight(), 76);
        assertEquals(A.getLeft(), 8);
        assertEquals(A.getTop(), 8);
        assertEquals(A.getWidth(), 100);
        assertEquals(A.getHeight(), 30);
        assertEquals(B.getLeft(), 69);
        assertEquals(B.getTop(), 46);
        assertEquals(B.getWidth(), 100);
        assertEquals(B.getHeight(), 30);
        assertEquals(C.getLeft(), 181);
        assertEquals(C.getTop(), 8);
        assertEquals(C.getWidth(), 100);
        assertEquals(C.getHeight(), 30);
        assertEquals(D.getLeft(), 289);
        assertEquals(D.getTop(), -28, 2);
        assertEquals(D.getWidth(), 100);
        assertEquals(D.getHeight(),  30);
    }

    @Test
    public void testSimpleWrapConstraint7() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 800);
        root.setMeasurer(sMeasurer);
        ConstraintWidget A = new ConstraintWidget(100, 30);

        root.setDebugName("root");
        A.setDebugName("A");

        root.add(A);

        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);

        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 8);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 8);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, 8);

        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);

        root.measure(Optimizer.OPTIMIZATION_STANDARD, BasicMeasure.WRAP_CONTENT, 0, BasicMeasure.WRAP_CONTENT, 0, 0, 0, 0, 0);

        System.out.println("0) root: " + root);
        System.out.println("1) A: " + A);
        assertEquals(root.getWidth(), 16);
        assertEquals(root.getHeight(), 38);
        assertEquals(A.getLeft(), 8);
        assertEquals(A.getTop(), 8);
        assertEquals(A.getWidth(), 0);
        assertEquals(A.getHeight(), 30);

        root.measure(Optimizer.OPTIMIZATION_GRAPH, BasicMeasure.WRAP_CONTENT, 0, BasicMeasure.WRAP_CONTENT, 0, 0, 0, 0, 0);
        System.out.println("0) root: " + root);
        System.out.println("1) A: " + A);
        assertEquals(root.getWidth(), 16);
        assertEquals(root.getHeight(), 38);
        assertEquals(A.getLeft(), 8);
        assertEquals(A.getTop(), 8);
        assertEquals(A.getWidth(), 0);
        assertEquals(A.getHeight(), 30);

    }


    @Test
    public void testSimpleWrapConstraint8() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 800);
        root.setMeasurer(sMeasurer);
        ConstraintWidget A = new ConstraintWidget( 100, 30);
        ConstraintWidget B = new ConstraintWidget( 10, 30);
        ConstraintWidget C = new ConstraintWidget( 10, 30);
        ConstraintWidget D = new ConstraintWidget( 100, 30);

        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        D.setDebugName("D");

        root.add(A);
        root.add(B);
        root.add(C);
        root.add(D);

        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        C.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        D.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);

        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        C.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        D.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);

        applyChain(ConstraintWidget.HORIZONTAL, A, B, C, D);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        D.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);

        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);

        root.measure(Optimizer.OPTIMIZATION_STANDARD, BasicMeasure.WRAP_CONTENT, 0, BasicMeasure.WRAP_CONTENT, 0, 0, 0, 0, 0);

        System.out.println("0) root: " + root);
        System.out.println("1) A: " + A);
        System.out.println("2) B: " + B);
        System.out.println("3) C: " + C);
        System.out.println("4) D: " + D);
        assertEquals(root.getWidth(), 110);
        assertEquals(root.getHeight(), 30);

        root.measure(Optimizer.OPTIMIZATION_GRAPH, BasicMeasure.WRAP_CONTENT, 0, BasicMeasure.WRAP_CONTENT, 0, 0, 0, 0, 0);
        System.out.println("0) root: " + root);
        System.out.println("1) A: " + A);
        System.out.println("2) B: " + B);
        System.out.println("3) C: " + C);
        System.out.println("4) D: " + D);
        assertEquals(root.getWidth(), 110);
        assertEquals(root.getHeight(), 30);

    }


    @Test
    public void testSimpleCircleConstraint() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 800);
        root.setMeasurer(sMeasurer);
        ConstraintWidget A = new ConstraintWidget( 100, 30);
        ConstraintWidget B = new ConstraintWidget( 100, 30);

        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");

        root.add(A);
        root.add(B);

        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);

        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 8);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 8);
        B.connectCircularConstraint(A, 30, 50);

        root.setOptimizationLevel(Optimizer.OPTIMIZATION_GRAPH);
        root.measure(Optimizer.OPTIMIZATION_GRAPH, EXACTLY, 600, EXACTLY, 800, 0, 0, 0, 0);
//        root.layout();

        System.out.println("1) A: " + A);
        System.out.println("2) B: " + B);
    }

    public void applyChain(ArrayList<ConstraintWidget> widgets, int direction) {
        ConstraintWidget previous = widgets.get(0);
        for (int i = 1; i < widgets.size(); i++) {
            ConstraintWidget widget = widgets.get(i);
            if (direction == 0) { // horizontal
                widget.connect(ConstraintAnchor.Type.LEFT, previous, ConstraintAnchor.Type.RIGHT);
                previous.connect(ConstraintAnchor.Type.RIGHT, widget, ConstraintAnchor.Type.LEFT);
            } else {
                widget.connect(ConstraintAnchor.Type.TOP, previous, ConstraintAnchor.Type.BOTTOM);
                previous.connect(ConstraintAnchor.Type.BOTTOM, widget, ConstraintAnchor.Type.TOP);
            }
            previous = widget;
        }
    }

    public void applyChain(int direction, ConstraintWidget ... widgets) {
        ConstraintWidget previous = widgets[0];
        for (int i = 1; i < widgets.length; i++) {
            ConstraintWidget widget = widgets[i];
            if (direction == ConstraintWidget.HORIZONTAL) {
                widget.connect(ConstraintAnchor.Type.LEFT, previous, ConstraintAnchor.Type.RIGHT);
                previous.connect(ConstraintAnchor.Type.RIGHT, widget, ConstraintAnchor.Type.LEFT);
            } else {
                widget.connect(ConstraintAnchor.Type.TOP, previous, ConstraintAnchor.Type.BOTTOM);
                previous.connect(ConstraintAnchor.Type.BOTTOM, widget, ConstraintAnchor.Type.TOP);
            }
            previous = widget;
        }
    }

    @Test
    public void testRatioChainConstraint() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 800);
        root.setMeasurer(sMeasurer);
        ConstraintWidget A = new ConstraintWidget( 100, 30);
        ConstraintWidget B = new ConstraintWidget( 0, 30);
        ConstraintWidget C = new ConstraintWidget( 0, 30);
        ConstraintWidget D = new ConstraintWidget( 100, 30);

        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");

        root.add(A);
        root.add(B);
        root.add(C);
        root.add(D);

        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        C.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        D.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);

        B.setDimensionRatio("w,1:1");

        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        C.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        D.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);

        applyChain(ConstraintWidget.HORIZONTAL, A, B, C, D);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        D.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);

        root.setOptimizationLevel(Optimizer.OPTIMIZATION_GRAPH);
        root.measure(Optimizer.OPTIMIZATION_GRAPH, EXACTLY, 600, EXACTLY, 800, 0, 0, 0, 0);
//        root.layout();

        System.out.println("1) A: " + A);
        System.out.println("2) B: " + B);
        System.out.println("3) C: " + C);
        System.out.println("4) D: " + D);
    }


    @Test
    public void testCycleConstraints() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 800);
        root.setMeasurer(sMeasurer);
        ConstraintWidget A = new ConstraintWidget( 100, 30);
        ConstraintWidget B = new ConstraintWidget( 40, 20);
        ConstraintWidget C = new ConstraintWidget( 40, 20);
        ConstraintWidget D = new ConstraintWidget( 30, 30);

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

        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM);
        B.connect(ConstraintAnchor.Type.LEFT, C, ConstraintAnchor.Type.LEFT);

        C.connect(ConstraintAnchor.Type.TOP, B, ConstraintAnchor.Type.BOTTOM);
        C.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        C.connect(ConstraintAnchor.Type.LEFT, D, ConstraintAnchor.Type.RIGHT);

        D.connect(ConstraintAnchor.Type.TOP, B, ConstraintAnchor.Type.TOP);
        D.connect(ConstraintAnchor.Type.BOTTOM, C, ConstraintAnchor.Type.BOTTOM);
        D.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);

        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.measure(Optimizer.OPTIMIZATION_NONE, EXACTLY, 600, EXACTLY, 800, 0, 0, 0, 0);

        System.out.println("1) A: " + A);
        System.out.println("2) B: " + B);
        System.out.println("3) C: " + C);
        System.out.println("4) D: " + D);

        assertEquals(A.getTop(), 0);
        assertEquals(B.getTop(), 30);
        assertEquals(C.getTop(), 50);
        assertEquals(D.getTop(), 35);
    }

    @Test
    public void testGoneChain() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 800);
        root.setMeasurer(sMeasurer);
        ConstraintWidget A = new ConstraintWidget(100, 30);
        ConstraintWidget B = new ConstraintWidget(100, 30);
        ConstraintWidget C = new ConstraintWidget(100, 30);
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
        A.setVisibility(ConstraintWidget.GONE);
        C.setVisibility(ConstraintWidget.GONE);

        root.measure(Optimizer.OPTIMIZATION_NONE, EXACTLY, 600, EXACTLY, 800, 0, 0, 0, 0);

        System.out.println("0) root: " + root);
        System.out.println("1) A: " + A);
        System.out.println("2) B: " + B);
        System.out.println("3) C: " + C);

        assertEquals(B.getWidth(), root.getWidth());
    }

    @Test
    public void testGoneChainWithCenterWidget() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 800);
        root.setMeasurer(sMeasurer);
        ConstraintWidget A = new ConstraintWidget(100, 30);
        ConstraintWidget B = new ConstraintWidget(100, 30);
        ConstraintWidget C = new ConstraintWidget(100, 30);
        ConstraintWidget D = new ConstraintWidget(100, 30);
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
        C.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setVisibility(ConstraintWidget.GONE);
        C.setVisibility(ConstraintWidget.GONE);
        D.connect(ConstraintAnchor.Type.LEFT, B, ConstraintAnchor.Type.LEFT);
        D.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.RIGHT);
        D.setVisibility(ConstraintWidget.GONE);

        root.measure(Optimizer.OPTIMIZATION_NONE, EXACTLY, 600, EXACTLY, 800, 0, 0, 0, 0);

        System.out.println("0) root: " + root);
        System.out.println("1) A: " + A);
        System.out.println("2) B: " + B);
        System.out.println("3) C: " + C);
        System.out.println("4) D: " + D);

        assertEquals(B.getWidth(), root.getWidth());
    }

    @Test
    public void testBarrier() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 800);
        root.setMeasurer(sMeasurer);
        ConstraintWidget A = new ConstraintWidget(100, 30);
        ConstraintWidget B = new ConstraintWidget(100, 30);
        ConstraintWidget C = new ConstraintWidget(100, 30);
        ConstraintWidget D = new ConstraintWidget(100, 30);
        Barrier barrier1 = new Barrier();
        //Barrier barrier2 = new Barrier();
        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        D.setDebugName("D");
        barrier1.setDebugName("barrier1");
        //barrier2.setDebugName("barrier2");
        root.add(A);
        root.add(B);
        root.add(C);
        root.add(D);
        root.add(barrier1);
        //root.add(barrier2);

        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        barrier1.add(A);
        barrier1.setBarrierType(Barrier.BOTTOM);

        B.connect(ConstraintAnchor.Type.TOP, barrier1, ConstraintAnchor.Type.BOTTOM);
        //barrier2.add(B);
        //barrier2.setBarrierType(Barrier.TOP);

        C.connect(ConstraintAnchor.Type.TOP, B, ConstraintAnchor.Type.BOTTOM);
        D.connect(ConstraintAnchor.Type.TOP, C, ConstraintAnchor.Type.BOTTOM);
        D.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);

        root.measure(Optimizer.OPTIMIZATION_NONE, EXACTLY, 600, EXACTLY, 800, 0, 0, 0, 0);

        System.out.println("0) root: " + root);
        System.out.println("1) A: " + A);
        System.out.println("2) barrier1: " + barrier1);
        System.out.println("3) B: " + B);
        //System.out.println("4) barrier2: " + barrier2);
        System.out.println("5) C: " + C);
        System.out.println("6) D: " + D);

        assertEquals(A.getTop(), 0);
        assertEquals(B.getTop(), A.getBottom());
        assertEquals(barrier1.getTop(), A.getBottom());
        assertEquals(C.getTop(), B.getBottom());
        assertEquals(D.getTop(), 430);
//        assertEquals(barrier2.getTop(), B.getTop());

    }
}
