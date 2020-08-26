package android.support.constraint.solver;

import android.support.constraint.solver.widgets.ConstraintAnchor;
import android.support.constraint.solver.widgets.ConstraintWidget;
import android.support.constraint.solver.widgets.ConstraintWidgetContainer;
import android.support.constraint.solver.widgets.Flow;
import android.support.constraint.solver.widgets.Guideline;
import android.support.constraint.solver.widgets.Optimizer;
import android.support.constraint.solver.widgets.VirtualLayout;
import android.support.constraint.solver.widgets.analyzer.BasicMeasure;

import org.testng.annotations.Test;

import static android.support.constraint.solver.widgets.analyzer.BasicMeasure.EXACTLY;
import static android.support.constraint.solver.widgets.analyzer.BasicMeasure.FIXED;
import static android.support.constraint.solver.widgets.analyzer.BasicMeasure.UNSPECIFIED;
import static org.testng.Assert.assertEquals;

public class FlowTest {
    static BasicMeasure.Measurer sMeasurer = new BasicMeasure.Measurer() {

        @Override
        public void measure(ConstraintWidget widget, BasicMeasure.Measure measure) {
            ConstraintWidget.DimensionBehaviour horizontalBehavior = measure.horizontalBehavior;
            ConstraintWidget.DimensionBehaviour verticalBehavior = measure.verticalBehavior;
            int horizontalDimension = measure.horizontalDimension;
            int verticalDimension = measure.verticalDimension;

            if (widget instanceof VirtualLayout) {
                VirtualLayout layout = (VirtualLayout) widget;
                int widthMode = UNSPECIFIED;
                int heightMode = UNSPECIFIED;
                int widthSize = 0;
                int heightSize = 0;
                if (layout.getHorizontalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.MATCH_PARENT) {
                    widthSize = layout.getParent() != null ? layout.getParent().getWidth() : 0;
                    widthMode = EXACTLY;
                }
                if (layout.getVerticalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.MATCH_PARENT) {
                    heightSize = layout.getParent() != null ? layout.getParent().getHeight() : 0;
                    heightMode = EXACTLY;
                }
                layout.measure(widthMode, widthSize, heightMode, heightSize);
                measure.measuredWidth = layout.getMeasuredWidth();
                measure.measuredHeight = layout.getMeasuredHeight();
            } else {
                if (horizontalBehavior == ConstraintWidget.DimensionBehaviour.FIXED) {
                    measure.measuredWidth = horizontalDimension;
                }
                if (verticalBehavior == ConstraintWidget.DimensionBehaviour.FIXED) {
                    measure.measuredHeight = verticalDimension;
                }
            }
        }

        @Override
        public void didMeasures() {

        }
    };

    @Test
    public void testFlowBaseline() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1080, 1536);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(20, 15);
        Flow flow = new Flow();

        root.setMeasurer(sMeasurer);

        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        flow.setDebugName("Flow");

        flow.setVerticalAlign(Flow.VERTICAL_ALIGN_BASELINE);
        flow.add(A);
        flow.add(B);
        A.setBaselineDistance(15);

        flow.setHeight(30);
        flow.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        flow.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
        flow.connect(ConstraintAnchor.Type.LEFT,root, ConstraintAnchor.Type.LEFT);
        flow.connect(ConstraintAnchor.Type.RIGHT,root, ConstraintAnchor.Type.RIGHT);
        flow.connect(ConstraintAnchor.Type.TOP,root, ConstraintAnchor.Type.TOP);
        flow.connect(ConstraintAnchor.Type.BOTTOM,root, ConstraintAnchor.Type.BOTTOM);

        root.add(flow);
        root.add(A);
        root.add(B);

        root.measure(Optimizer.OPTIMIZATION_NONE
                , 0, 0, 0, 0, 0, 0, 0, 0);
        root.layout();
        System.out.println("a) root: " + root);
        System.out.println("flow: " + flow);
        System.out.println("A: " + A);
        System.out.println("B: " + B);
        assertEquals(flow.getWidth(), 1080);
        assertEquals(flow.getHeight(), 20);
        assertEquals(flow.getTop(), 758);
        assertEquals(A.getLeft(), 320);
        assertEquals(A.getTop(), 758);
        assertEquals(B.getLeft(), 740);
        assertEquals(B.getTop(), 761);
    }

    @Test
    public void testComplexChain() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1080, 1536);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        ConstraintWidget B = new ConstraintWidget(100, 20);
        ConstraintWidget C = new ConstraintWidget(100, 20);
        Flow flow = new Flow();

        root.setMeasurer(sMeasurer);

        root.setDebugName("root");
        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        flow.setDebugName("Flow");

        flow.setWrapMode(Flow.WRAP_CHAIN);
        flow.setMaxElementsWrap(2);

        flow.add(A);
        flow.add(B);
        flow.add(C);

        root.add(flow);
        root.add(A);
        root.add(B);
        root.add(C);

        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        C.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);

        flow.connect(ConstraintAnchor.Type.LEFT,root, ConstraintAnchor.Type.LEFT);
        flow.connect(ConstraintAnchor.Type.RIGHT,root, ConstraintAnchor.Type.RIGHT);
        flow.connect(ConstraintAnchor.Type.TOP,root, ConstraintAnchor.Type.TOP);
        flow.connect(ConstraintAnchor.Type.BOTTOM,root, ConstraintAnchor.Type.BOTTOM);

        flow.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_PARENT);
        flow.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);

        root.measure(Optimizer.OPTIMIZATION_NONE
                , 0, 0, 0, 0, 0, 0, 0, 0);
        root.layout();
        System.out.println("a) root: " + root);
        System.out.println("flow: " + flow);
        System.out.println("A: " + A);
        System.out.println("B: " + B);
        System.out.println("C: " + C);

        assertEquals(A.getWidth(), 540);
        assertEquals(B.getWidth(), 540);
        assertEquals(C.getWidth(), 1080);
        assertEquals(flow.getWidth(), root.getWidth());
        assertEquals(flow.getHeight(), Math.max(A.getHeight(), B.getHeight()) + C.getHeight());
        assertEquals(flow.getTop(), 748);
    }
}
