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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class WidgetsPositioningTest {

    LinearSystem s = new LinearSystem();

    @BeforeMethod
    public void setUp() {
        s = new LinearSystem();
        LinearEquation.resetNaming();
    }

    @Test
    public void testCentering() {
        final ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 600, 600);
        final ConstraintWidget A = new ConstraintWidget(100, 20);
        final ConstraintWidget B = new ConstraintWidget(20, 100);
        final ConstraintWidget C = new ConstraintWidget(100, 20);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 200);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.TOP, 0);
        B.connect(ConstraintAnchor.Type.BOTTOM, A, ConstraintAnchor.Type.BOTTOM, 0);
        C.connect(ConstraintAnchor.Type.TOP, B, ConstraintAnchor.Type.TOP, 0);
        C.connect(ConstraintAnchor.Type.BOTTOM, B, ConstraintAnchor.Type.BOTTOM, 0);
        root.add(A);
        root.add(B);
        root.add(C);
        root.layout();
        System.out.println("A: " + A + " B: " + B + " C: " + C);
    }

    @Test
    public void testDimensionRatio() {
        final ConstraintWidget A = new ConstraintWidget(0, 0, 600, 600);
        final ConstraintWidget B = new ConstraintWidget(100, 100);
        ArrayList<ConstraintWidget> widgets = new ArrayList<ConstraintWidget>();
        widgets.add(A);
        widgets.add(B);
        final int margin = 10;
        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.LEFT, margin);
        B.connect(ConstraintAnchor.Type.RIGHT, A, ConstraintAnchor.Type.RIGHT, margin);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.TOP, margin);
        B.connect(ConstraintAnchor.Type.BOTTOM, A, ConstraintAnchor.Type.BOTTOM, margin);
        B.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        B.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        A.setDebugName("A");
        B.setDebugName("B");
        final float ratio = 0.3f;
        // First, let's check vertical ratio
        B.setDimensionRatio(ratio, ConstraintWidget.VERTICAL);
        runTestOnWidgets(widgets, new Runnable() {
            @Override
            public void run() {
                System.out.println("a) A: " + A + " B: " + B);
                assertEquals(B.getWidth(), A.getWidth() - 2 * margin);
                assertEquals(B.getHeight(), (int) (ratio * B.getWidth()));
                assertEquals(B.getTop() - A.getTop(), (int) ((A.getHeight() - B.getHeight()) / 2));
                assertEquals(A.getBottom() - B.getBottom(), (int) ((A.getHeight() - B.getHeight()) / 2));
                assertEquals(B.getTop() - A.getTop(), A.getBottom() - B.getBottom());
            }
        });
        B.setVerticalBiasPercent(1);
        runTestOnWidgets(widgets, new Runnable() {
            @Override
            public void run() {
                System.out.println("b) A: " + A + " B: " + B);
                assertEquals(B.getWidth(), A.getWidth() - 2 * margin);
                assertEquals(B.getHeight(), (int) (ratio * B.getWidth()));
                assertEquals(B.getTop(), A.getHeight() - B.getHeight() - margin);
                assertEquals(A.getBottom(), B.getBottom() + margin);
            }
        });
        B.setVerticalBiasPercent(0);
        runTestOnWidgets(widgets, new Runnable() {
            @Override
            public void run() {
                System.out.println("c) A: " + A + " B: " + B);
                assertEquals(B.getWidth(), A.getWidth() - 2 * margin);
                assertEquals(B.getHeight(), (int) (ratio * B.getWidth()));
                assertEquals(B.getTop(), A.getTop() + margin);
                assertEquals(B.getBottom(), A.getTop() + B.getHeight() + margin);
            }
        });
        // Then, let's check horizontal ratio
        B.setDimensionRatio(ratio, ConstraintWidget.HORIZONTAL);
        runTestOnWidgets(widgets, new Runnable() {
            @Override
            public void run() {
                System.out.println("d) A: " + A + " B: " + B);
                assertEquals(B.getHeight(), A.getHeight() - 2 * margin);
                assertEquals(B.getWidth(), (int) (ratio * B.getHeight()));
                assertEquals(B.getLeft() - A.getLeft(), (int) ((A.getWidth() - B.getWidth()) / 2));
                assertEquals(A.getRight() - B.getRight(), (int) ((A.getWidth() - B.getWidth()) / 2));
            }
        });
        B.setHorizontalBiasPercent(1);
        runTestOnWidgets(widgets, new Runnable() {
            @Override
            public void run() {
                System.out.println("e) A: " + A + " B: " + B);
                assertEquals(B.getHeight(), A.getHeight() - 2 * margin);
                assertEquals(B.getWidth(), (int) (ratio * B.getHeight()));
                assertEquals(B.getRight(), A.getRight() - margin);
                assertEquals(B.getLeft(), A.getRight() - B.getWidth() - margin);
            }
        });
        B.setHorizontalBiasPercent(0);
        runTestOnWidgets(widgets, new Runnable() {
            @Override
            public void run() {
                System.out.println("f) A: " + A + " B: " + B);
                assertEquals(B.getHeight(), A.getHeight() - 2 * margin);
                assertEquals(B.getWidth(), (int) (ratio * B.getHeight()));
                assertEquals(B.getRight(), A.getLeft() + margin + B.getWidth());
                assertEquals(B.getLeft(), A.getLeft() + margin);
            }
        });
    }

    @Test
    public void testCreateManyVariables() {
        final ConstraintWidgetContainer rootWidget = new ConstraintWidgetContainer(0, 0, 600, 400);
        ConstraintWidget previous = new ConstraintWidget(0, 0, 100, 20);
        rootWidget.add(previous);
        for (int i = 0; i < 100; i++) {
            ConstraintWidget w = new ConstraintWidget(0, 0, 100, 20);
            w.connect(ConstraintAnchor.Type.LEFT, previous, ConstraintAnchor.Type.RIGHT, 20);
            w.connect(ConstraintAnchor.Type.RIGHT, rootWidget, ConstraintAnchor.Type.RIGHT, 20);
            rootWidget.add(w);
        }
        rootWidget.layout();
    }

    @Test
    public void testWidgetCenterPositioning() {
        final int x = 20;
        final int y = 30;
        final ConstraintWidget rootWidget = new ConstraintWidget(x, y, 600, 400);
        final ConstraintWidget centeredWidget = new ConstraintWidget(100, 20);
        ArrayList<ConstraintWidget> widgets = new ArrayList<ConstraintWidget>();
        centeredWidget.resetSolverVariables(s.getCache());
        rootWidget.resetSolverVariables(s.getCache());
        widgets.add(centeredWidget);
        widgets.add(rootWidget);

        centeredWidget.setDebugName("A");
        rootWidget.setDebugName("Root");
        centeredWidget.connect(ConstraintAnchor.Type.CENTER_X, rootWidget, ConstraintAnchor.Type.CENTER_X);
        centeredWidget.connect(ConstraintAnchor.Type.CENTER_Y, rootWidget, ConstraintAnchor.Type.CENTER_Y);

        runTestOnWidgets(widgets, new Runnable() {
            @Override
            public void run() {
                System.out.println("\n*** rootWidget: " + rootWidget + " centeredWidget: " + centeredWidget);
                int left = centeredWidget.getLeft();
                int top = centeredWidget.getTop();
                int right = centeredWidget.getRight();
                int bottom = centeredWidget.getBottom();
                assertEquals(left, x + 250);
                assertEquals(right, x + 350);
                assertEquals(top, y + 190);
                assertEquals(bottom, y + 210);
            }
        });
    }

    @Test
    public void testBaselinePositioning() {
        final ConstraintWidget A = new ConstraintWidget(20, 230, 200, 70);
        final ConstraintWidget B = new ConstraintWidget(200, 60, 200, 100);
        ArrayList<ConstraintWidget> widgets = new ArrayList<ConstraintWidget>();
        widgets.add(A);
        widgets.add(B);
        A.setDebugName("A");
        B.setDebugName("B");
        A.setBaselineDistance(40);
        B.setBaselineDistance(60);
        B.connect(ConstraintAnchor.Type.BASELINE, A, ConstraintAnchor.Type.BASELINE);
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1000, 1000);
        root.setDebugName("root");
        root.add(A);
        root.add(B);
        root.layout();
        assertEquals(B.getTop() + B.getBaselineDistance(),
                A.getTop() + A.getBaselineDistance());
        runTestOnWidgets(widgets, new Runnable() {
            @Override
            public void run() {
                assertEquals(B.getTop() + B.getBaselineDistance(),
                        A.getTop() + A.getBaselineDistance());
            }
        });
    }

    //@Test
    public void testAddingWidgets() {
        final ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1000, 1000);
        root.setOptimizationLevel(Optimizer.OPTIMIZATION_NONE);
        ArrayList<ConstraintWidget> widgetsA = new ArrayList();
        ArrayList<ConstraintWidget> widgetsB = new ArrayList();
        for (int i = 0; i < 1000; i++) {
            final ConstraintWidget A = new ConstraintWidget(0, 0, 200, 20);
            final ConstraintWidget B = new ConstraintWidget(0, 0, 200, 20);
            A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
            A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
            A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT);
            B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
            B.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
            B.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
            widgetsA.add(A);
            widgetsB.add(B);
            root.add(A);
            root.add(B);
        }
        root.layout();
        for (ConstraintWidget widget : widgetsA) {
            assertEquals(widget.getLeft(), 200);
            assertEquals(widget.getTop(), 0);
        }
        for (ConstraintWidget widget : widgetsB) {
            assertEquals(widget.getLeft(), 600);
            assertEquals(widget.getTop(), 980);
        }
    }

    @Test
    public void testWidgetTopRightPositioning() {
        // Easy to tweak numbers to test larger systems
        int numLoops = 10;
        int numWidgets = 100;

        for (int j = 0; j < numLoops; j++) {
            s.reset();
            ArrayList<ConstraintWidget> widgets = new ArrayList();
            int w = 100 + j;
            int h = 20 + j;
            ConstraintWidget first = new ConstraintWidget(w, h);
            widgets.add(first);
            ConstraintWidget previous = first;
            int margin = 20;
            for (int i = 0; i < numWidgets; i++) {
                ConstraintWidget widget = new ConstraintWidget(w, h);
                widget.connect(ConstraintAnchor.Type.LEFT, previous, ConstraintAnchor.Type.RIGHT, margin);
                widget.connect(ConstraintAnchor.Type.TOP, previous, ConstraintAnchor.Type.BOTTOM, margin);
                widgets.add(widget);
                previous = widget;
            }
            for (ConstraintWidget widget : widgets) {
                widget.addToSolver(s);
            }
            try {
                s.minimize();
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (int i = 0; i < widgets.size(); i++) {
                ConstraintWidget widget = widgets.get(i);
                widget.updateFromSolver(s);
                int left = widget.getLeft();
                int top = widget.getTop();
                int right = widget.getRight();
                int bottom = widget.getBottom();
                assertEquals(left, i * (w + margin));
                assertEquals(right, i * (w + margin) + w);
                assertEquals(top, i * (h + margin));
                assertEquals(bottom, i * (h + margin) + h);
            }
        }
    }

    @Test
    public void testWrapSimpleWrapContent() {
        final ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1000, 1000);
        final ConstraintWidget A = new ConstraintWidget(0, 0, 200, 20);
        ArrayList<ConstraintWidget> widgets = new ArrayList<ConstraintWidget>();
        widgets.add(root);
        widgets.add(A);

        root.setDebugSolverName(s, "root");
        A.setDebugSolverName(s, "A");

        root.add(A);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);

        runTestOnWidgets(widgets, new Runnable() {
            @Override
            public void run() {
                System.out.println("Simple Wrap: " + root + ", " + A);
                assertEquals(root.getWidth(), A.getWidth());
                assertEquals(root.getHeight(), A.getHeight());
                assertEquals(A.getWidth(), 200);
                assertEquals(A.getHeight(), 20);
            }
        });
    }

    @Test
    public void testMatchConstraint() {
        final ConstraintWidgetContainer root = new ConstraintWidgetContainer(50, 50, 500, 500);
        final ConstraintWidget A = new ConstraintWidget(10, 20, 100, 30);
        final ConstraintWidget B = new ConstraintWidget(150, 200, 100, 30);
        final ConstraintWidget C = new ConstraintWidget(50, 50);
        ArrayList<ConstraintWidget> widgets = new ArrayList<ConstraintWidget>();
        A.setDebugName("A");
        B.setDebugName("B");
        C.setDebugName("C");
        root.setDebugName("root");
        root.add(A);
        root.add(B);
        root.add(C);
        widgets.add(root);
        widgets.add(A);
        widgets.add(B);
        widgets.add(C);

        C.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        C.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
        C.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        C.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT);
        C.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM);
        C.connect(ConstraintAnchor.Type.BOTTOM, B, ConstraintAnchor.Type.TOP);
        runTestOnWidgets(widgets, new Runnable() {
            @Override
            public void run() {
                assertEquals(C.getX(), A.getRight());
                assertEquals(C.getRight(), B.getX());
                assertEquals(C.getY(), A.getBottom());
                assertEquals(C.getBottom(), B.getY());
            }
        });
    }

    // Obsolete @Test
    public void testWidgetStrengthPositioning() {
        final ConstraintWidget root = new ConstraintWidget(400, 400);
        final ConstraintWidget A = new ConstraintWidget(20, 20);
        ArrayList<ConstraintWidget> widgets = new ArrayList<ConstraintWidget>();
        widgets.add(root);
        widgets.add(A);

        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        System.out.println("Widget A centered inside Root");
        runTestOnWidgets(widgets, new Runnable() {
            @Override
            public void run() {
                assertEquals(A.getLeft(), 190);
                assertEquals(A.getRight(), 210);
                assertEquals(A.getTop(), 190);
                assertEquals(A.getBottom(), 210);
            }
        });
        System.out.println("Widget A weak left, should move to the right");
        A.getAnchor(ConstraintAnchor.Type.LEFT);//.setStrength(ConstraintAnchor.Strength.WEAK);
        runTestOnWidgets(widgets, new Runnable() {
            @Override
            public void run() {
                assertEquals(A.getLeft(), 380);
                assertEquals(A.getRight(), 400);
            }
        });
        System.out.println("Widget A weak right, should go back to center");
        A.getAnchor(ConstraintAnchor.Type.RIGHT);//.setStrength(ConstraintAnchor.Strength.WEAK);
        runTestOnWidgets(widgets, new Runnable() {
            @Override
            public void run() {
                assertEquals(A.getLeft(), 190);
                assertEquals(A.getRight(), 210);
            }
        });
        System.out.println("Widget A strong left, should move to the left");
        A.getAnchor(ConstraintAnchor.Type.LEFT);//.setStrength(ConstraintAnchor.Strength.STRONG);
        runTestOnWidgets(widgets, new Runnable() {
            @Override
            public void run() {
                assertEquals(A.getLeft(), 0);
                assertEquals(A.getRight(), 20);
                assertEquals(root.getWidth(), 400);
            }
        });
    }

    @Test
    public void testWidgetPositionMove() {
        final ConstraintWidget A = new ConstraintWidget(0, 0, 100, 20);
        final ConstraintWidget B = new ConstraintWidget(0, 30, 200, 20);
        final ConstraintWidget C = new ConstraintWidget(0, 60, 100, 20);
        ArrayList<ConstraintWidget> widgets = new ArrayList<ConstraintWidget>();
        widgets.add(A);
        widgets.add(B);
        widgets.add(C);
        A.setDebugSolverName(s, "A");
        B.setDebugSolverName(s, "B");
        C.setDebugSolverName(s, "C");

        B.connect(ConstraintAnchor.Type.LEFT, A, ConstraintAnchor.Type.RIGHT);
        C.setOrigin(200, 0);
        B.connect(ConstraintAnchor.Type.RIGHT, C, ConstraintAnchor.Type.RIGHT);

        Runnable check = new Runnable() {
            @Override
            public void run() {
                assertEquals(A.getWidth(), 100);
                assertEquals(B.getWidth(), 200);
                assertEquals(C.getWidth(), 100);
            }
        };
        runTestOnWidgets(widgets, check);
        System.out.println("A: " + A + " B: " + B + " C: " + C);
        C.setOrigin(100, 0);
//        runTestOnUIWidgets(widgets);
        runTestOnWidgets(widgets, check);
        System.out.println("A: " + A + " B: " + B + " C: " + C);
        C.setOrigin(50, 0);
        runTestOnWidgets(widgets, check);
        System.out.println("A: " + A + " B: " + B + " C: " + C);
    }

    @Test
    public void testWrapProblem() {
        final ConstraintWidgetContainer root = new ConstraintWidgetContainer(400, 400);
        final ConstraintWidget A = new ConstraintWidget(80, 300);
        final ConstraintWidget B = new ConstraintWidget(250, 80);
        final ArrayList<ConstraintWidget> widgets = new ArrayList<ConstraintWidget>();
        widgets.add(root);
        widgets.add(B);
        widgets.add(A);
        A.setParent(root);
        B.setParent(root);
        root.setDebugSolverName(s, "root");
        A.setDebugSolverName(s, "A");
        B.setDebugSolverName(s, "B");

        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        B.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        B.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
//        B.getAnchor(ConstraintAnchor.Type.TOP).setStrength(ConstraintAnchor.Strength.WEAK);

        runTestOnWidgets(widgets, new Runnable() {
            @Override
            public void run() {
                assertEquals(A.getWidth(), 80);
                assertEquals(A.getHeight(), 300);
                assertEquals(B.getWidth(), 250);
                assertEquals(B.getHeight(), 80);
                assertEquals(A.getY(), 0);
                assertEquals(B.getY(), 110);
            }
        });
    }

    @Test
    public void testGuideline() {
        final ConstraintWidgetContainer root = new ConstraintWidgetContainer(400, 400);
        final ConstraintWidget A = new ConstraintWidget(100, 20);
        final Guideline guideline = new Guideline();
        root.add(guideline);
        root.add(A);
        guideline.setGuidePercent(0.50f);
        guideline.setOrientation(Guideline.VERTICAL);
        root.setDebugName("root");
        A.setDebugName("A");
        guideline.setDebugName("guideline");

        ArrayList<ConstraintWidget> widgets = new ArrayList<ConstraintWidget>();
        widgets.add(root);
        widgets.add(A);
        widgets.add(guideline);

        A.connect(ConstraintAnchor.Type.LEFT, guideline, ConstraintAnchor.Type.LEFT);
        Runnable check = new Runnable() {
            @Override
            public void run() {
                System.out.println("" + root + " " + A + " " + guideline);
                assertEquals(A.getWidth(), 100);
                assertEquals(A.getHeight(), 20);
                assertEquals(A.getX(), 200);
            }
        };
        runTest(root, check);
        guideline.setGuidePercent(0);
        runTest(root, new Runnable() {
            @Override
            public void run() {
                System.out.println("" + root + " " + A + " " + guideline);
                assertEquals(A.getWidth(), 100);
                assertEquals(A.getHeight(), 20);
                assertEquals(A.getX(), 0);
            }
        });
        guideline.setGuideBegin(150);
        runTest(root, new Runnable() {
            @Override
            public void run() {
                assertEquals(A.getWidth(), 100);
                assertEquals(A.getHeight(), 20);
                assertEquals(A.getX(), 150);
            }
        });
        System.out.println("" + root + " " + A + " " + guideline);
        guideline.setGuideEnd(150);
        runTest(root, new Runnable() {
            @Override
            public void run() {
                assertEquals(A.getWidth(), 100);
                assertEquals(A.getHeight(), 20);
                assertEquals(A.getX(), 250);
            }
        });
        System.out.println("" + root + " " + A + " " + guideline);
        guideline.setOrientation(Guideline.HORIZONTAL);
        A.resetAnchors();
        A.connect(ConstraintAnchor.Type.TOP, guideline, ConstraintAnchor.Type.TOP);
        guideline.setGuideBegin(150);
        runTest(root, new Runnable() {
            @Override
            public void run() {
                System.out.println("" + root + " " + A + " " + guideline);
                assertEquals(A.getWidth(), 100);
                assertEquals(A.getHeight(), 20);
                assertEquals(A.getY(), 150);
            }
        });
        System.out.println("" + root + " " + A + " " + guideline);
        A.resetAnchors();
        A.connect(ConstraintAnchor.Type.TOP, guideline, ConstraintAnchor.Type.BOTTOM);
        runTest(root, new Runnable() {
            @Override
            public void run() {
                assertEquals(A.getWidth(), 100);
                assertEquals(A.getHeight(), 20);
                assertEquals(A.getY(), 150);
            }
        });
        System.out.println("" + root + " " + A + " " + guideline);
    }

    private void runTest(ConstraintWidgetContainer root, Runnable check) {
        root.layout();
        check.run();
    }


    @Test
    public void testGuidelinePosition() {
        final ConstraintWidgetContainer root = new ConstraintWidgetContainer(800, 400);
        final ConstraintWidget A = new ConstraintWidget(100, 20);
        final ConstraintWidget B = new ConstraintWidget(100, 20);
        final Guideline guideline = new Guideline();
        root.add(guideline);
        root.add(A);
        root.add(B);
        guideline.setGuidePercent(0.651f);
        guideline.setOrientation(Guideline.VERTICAL);
        root.setDebugSolverName(s, "root");
        A.setDebugSolverName(s, "A");
        B.setDebugSolverName(s, "B");
        guideline.setDebugSolverName(s, "guideline");

        ArrayList<ConstraintWidget> widgets = new ArrayList<ConstraintWidget>();
        widgets.add(root);
        widgets.add(A);
        widgets.add(B);
        widgets.add(guideline);

        A.connect(ConstraintAnchor.Type.LEFT, guideline, ConstraintAnchor.Type.RIGHT);
        B.connect(ConstraintAnchor.Type.RIGHT, guideline, ConstraintAnchor.Type.RIGHT);
        Runnable check = new Runnable() {
            @Override
            public void run() {
                System.out.println("" + root + " A: " + A + " " + " B: " + B + " guideline: " + guideline);
                assertEquals(A.getWidth(), 100);
                assertEquals(A.getHeight(), 20);
                assertEquals(A.getX(), 521);
                assertEquals(B.getRight(), 521);
            }
        };
        runTestOnWidgets(widgets, check);
    }

    @Test
    public void testWidgetInfeasiblePosition() {
        final ConstraintWidget A = new ConstraintWidget(100, 20);
        final ConstraintWidget B = new ConstraintWidget(100, 20);
        ArrayList<ConstraintWidget> widgets = new ArrayList<ConstraintWidget>();
        widgets.add(B);
        widgets.add(A);
        A.resetSolverVariables(s.getCache());
        B.resetSolverVariables(s.getCache());

        A.connect(ConstraintAnchor.Type.RIGHT, B, ConstraintAnchor.Type.LEFT);
        B.connect(ConstraintAnchor.Type.RIGHT, A, ConstraintAnchor.Type.LEFT);
        runTestOnWidgets(widgets, new Runnable() {
            @Override
            public void run() {
                // TODO: this fail -- need to figure the best way to fix this.
//                assertEquals(A.getWidth(), 100);
//                assertEquals(B.getWidth(), 100);
            }
        });
    }

    @Test
    public void testWidgetMultipleDependentPositioning() {
        final ConstraintWidget root = new ConstraintWidget(400, 400);
        final ConstraintWidget A = new ConstraintWidget(100, 20);
        final ConstraintWidget B = new ConstraintWidget(100, 20);
        root.setDebugSolverName(s, "root");
        A.setDebugSolverName(s, "A");
        B.setDebugSolverName(s, "B");
        ArrayList<ConstraintWidget> widgets = new ArrayList<ConstraintWidget>();
        widgets.add(root);
        widgets.add(B);
        widgets.add(A);

        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 10);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM, 10);
        B.connect(ConstraintAnchor.Type.TOP, A, ConstraintAnchor.Type.BOTTOM);
        B.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);

        root.resetSolverVariables(s.getCache());
        A.resetSolverVariables(s.getCache());
        B.resetSolverVariables(s.getCache());

        runTestOnWidgets(widgets, new Runnable() {
            @Override
            public void run() {
                System.out.println("root: " + root + " A: " + A + " B: " + B);
                assertEquals(root.getHeight(), 400);
                assertEquals(root.getHeight(), 400);
                assertEquals(A.getHeight(), 20);
                assertEquals(B.getHeight(), 20);
                assertEquals(A.getTop() - root.getTop(), root.getBottom() - A.getBottom());
                assertEquals(B.getTop() - A.getBottom(), root.getBottom() - B.getBottom());
            }
        });
    }

    @Test
    public void testMinSize() {
        final ConstraintWidgetContainer root = new ConstraintWidgetContainer(600, 400);
        final ConstraintWidget A = new ConstraintWidget(100, 20);
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        root.setDebugName("root");
        A.setDebugName("A");
        root.add(A);
        root.setOptimizationLevel(0);
        root.layout();
        System.out.println("a) root: " + root + " A: " + A);
        assertEquals(root.getWidth(), 600);
        assertEquals(root.getHeight(), 400);
        assertEquals(A.getWidth(), 100);
        assertEquals(A.getHeight(), 20);
        assertEquals(A.getLeft() - root.getLeft(), root.getRight() - A.getRight());
        assertEquals(A.getTop() - root.getTop(), root.getBottom() - A.getBottom());
        root.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.layout();
        System.out.println("b) root: " + root + " A: " + A);
        assertEquals(root.getHeight(), A.getHeight());
        assertEquals(A.getWidth(), 100);
        assertEquals(A.getHeight(), 20);
        assertEquals(A.getLeft() - root.getLeft(), root.getRight() - A.getRight());
        assertEquals(A.getTop() - root.getTop(), root.getBottom() - A.getBottom());
        root.setMinHeight(200);
        root.layout();
        System.out.println("c) root: " + root + " A: " + A);
        assertEquals(root.getHeight(), 200);
        assertEquals(A.getWidth(), 100);
        assertEquals(A.getHeight(), 20);
        assertEquals(A.getLeft() - root.getLeft(), root.getRight() - A.getRight());
        assertEquals(A.getTop() - root.getTop(), root.getBottom() - A.getBottom());
        root.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        root.layout();
        System.out.println("d) root: " + root + " A: " + A);
        assertEquals(root.getWidth(), A.getWidth());
        assertEquals(root.getHeight(), 200);
        assertEquals(A.getWidth(), 100);
        assertEquals(A.getHeight(), 20);
        assertEquals(A.getLeft() - root.getLeft(), root.getRight() - A.getRight());
        assertEquals(A.getTop() - root.getTop(), root.getBottom() - A.getBottom());
        root.setMinWidth(300);
        root.layout();
        System.out.println("e) root: " + root + " A: " + A);
        assertEquals(root.getWidth(), 300);
        assertEquals(root.getHeight(), 200);
        assertEquals(A.getWidth(), 100);
        assertEquals(A.getHeight(), 20);
        assertEquals(A.getLeft() - root.getLeft(), root.getRight() - A.getRight());
        assertEquals(A.getTop() - root.getTop(), root.getBottom() - A.getBottom());
    }
     /*
     * Insert the widgets in all permutations
     * (to test that the insert order
     * doesn't impact the resolution)
     */

    private void runTestOnWidgets(ArrayList<ConstraintWidget> widgets, Runnable check) {
        ArrayList<Integer> tail = new ArrayList<Integer>();
        for (int i = 0; i < widgets.size(); i++) {
            tail.add(i);
        }
        addToSolverWithPermutation(widgets, new ArrayList<Integer>(), tail, check);
    }

    private void runTestOnUIWidgets(ArrayList<ConstraintWidget> widgets) {
        for (int i = 0; i < widgets.size(); i++) {
            ConstraintWidget widget = widgets.get(i);
            if (widget.getDebugName() != null) {
                widget.setDebugSolverName(s, widget.getDebugName());
            }
            widget.resetSolverVariables(s.getCache());
            widget.addToSolver(s);
        }
        try {
            s.minimize();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int j = 0; j < widgets.size(); j++) {
            ConstraintWidget w = widgets.get(j);
            w.updateFromSolver(s);
            System.out.println(" " + w);
        }
    }

    private void addToSolverWithPermutation(ArrayList<ConstraintWidget> widgets,
            ArrayList<Integer> list, ArrayList<Integer> tail, Runnable check) {
        if (tail.size() > 0) {
            int n = tail.size();
            for (int i = 0; i < n; i++) {
                list.add(tail.get(i));
                ArrayList<Integer> permuted = new ArrayList<Integer>(tail);
                permuted.remove(i);
                addToSolverWithPermutation(widgets, list, permuted, check);
                list.remove(list.size() - 1);
            }
        } else {
//            System.out.print("Adding widgets in order: ");
            s.reset();
            for (int i = 0; i < list.size(); i++) {
                int index = list.get(i);
//                System.out.print(" " + index);
                ConstraintWidget widget = widgets.get(index);
                widget.resetSolverVariables(s.getCache());
            }
            for (int i = 0; i < list.size(); i++) {
                int index = list.get(i);
//                System.out.print(" " + index);
                ConstraintWidget widget = widgets.get(index);
                if (widget.getDebugName() != null) {
                    widget.setDebugSolverName(s, widget.getDebugName());
                }
                widget.addToSolver(s);
            }
//            System.out.println("");
//            s.displayReadableRows();
            try {
                s.minimize();
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (int j = 0; j < widgets.size(); j++) {
                ConstraintWidget w = widgets.get(j);
                w.updateFromSolver(s);
            }
//            try {
                check.run();
//            } catch (AssertionError e) {
//                System.out.println("Assertion error: " + e);
//                runTestOnUIWidgets(widgets);
//            }
        }
    }

}
