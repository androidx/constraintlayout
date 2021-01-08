package androidx.constraintlayout.core;

import androidx.constraintlayout.core.widgets.ConstraintAnchor;
import androidx.constraintlayout.core.widgets.ConstraintWidget;
import androidx.constraintlayout.core.widgets.ConstraintWidgetContainer;
import androidx.constraintlayout.core.widgets.Guideline;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class GuidelineTest {

    @Test
    public void testWrapGuideline() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 800, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        Guideline guidelineRight = new Guideline();
        guidelineRight.setOrientation(Guideline.VERTICAL);
        Guideline guidelineBottom = new Guideline();
        guidelineBottom.setOrientation(Guideline.HORIZONTAL);
        guidelineRight.setGuidePercent(0.64f);
        guidelineBottom.setGuideEnd(60);
        root.setDebugName("Root");
        A.setDebugName("A");
        guidelineRight.setDebugName("GuidelineRight");
        guidelineBottom.setDebugName("GuidelineBottom");
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, guidelineRight, ConstraintAnchor.Type.RIGHT);
        A.connect(ConstraintAnchor.Type.BOTTOM, guidelineBottom, ConstraintAnchor.Type.TOP);
        root.add(A);
        root.add(guidelineRight);
        root.add(guidelineBottom);
        root.layout();
        System.out.println("a) root: " + root + " guideline right: " + guidelineRight + " guideline bottom: " + guidelineBottom + " A: " + A);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.layout();
        System.out.println("b) root: " + root + " guideline right: " + guidelineRight + " guideline bottom: " + guidelineBottom + " A: " + A);
        assertEquals(root.getHeight(), 80);
    }

    @Test
    public void testWrapGuideline2() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 800, 600);
        ConstraintWidget A = new ConstraintWidget(100, 20);
        Guideline guideline = new Guideline();
        guideline.setOrientation(Guideline.VERTICAL);
        guideline.setGuideBegin(60);
        root.setDebugName("Root");
        A.setDebugName("A");
        guideline.setDebugName("Guideline");
        A.connect(ConstraintAnchor.Type.LEFT, guideline, ConstraintAnchor.Type.LEFT, 5);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, 5);
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        root.add(A);
        root.add(guideline);
//        root.layout();
        System.out.println("a) root: " + root + " guideline: " + guideline + " A: " + A);
        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.layout();
        System.out.println("b) root: " + root + " guideline: " + guideline + " A: " + A);
        assertEquals(root.getWidth(), 70);
    }
}
