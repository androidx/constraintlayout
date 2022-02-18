/*
 * Copyright (C) 2021 The Android Open Source Project
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
package androidx.constraintlayout.core.motion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import androidx.constraintlayout.core.parser.CLObject;
import androidx.constraintlayout.core.parser.CLParser;
import androidx.constraintlayout.core.parser.CLParsingException;
import androidx.constraintlayout.core.state.Transition;
import androidx.constraintlayout.core.state.TransitionParser;
import androidx.constraintlayout.core.state.WidgetFrame;
import androidx.constraintlayout.core.widgets.ConstraintAnchor;
import androidx.constraintlayout.core.widgets.ConstraintWidget;
import androidx.constraintlayout.core.widgets.ConstraintWidgetContainer;

import org.junit.Test;

import jdk.nashorn.internal.parser.JSONParser;

public class MotionTransitionTest {

    ConstraintWidgetContainer makeLayout1() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(1000, 1000);
        root.setDebugName("root");
        root.stringId = "root";
        ConstraintWidget button0 = new ConstraintWidget(200, 20);
        button0.setDebugName("button0");
        button0.stringId = "button0";

        ConstraintWidget button1 = new ConstraintWidget(200, 20);
        button1.setDebugName("button1");
        button1.stringId = "button1";

        button0.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        button0.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        button1.connect(ConstraintAnchor.Type.LEFT, button0, ConstraintAnchor.Type.LEFT);
        button1.connect(ConstraintAnchor.Type.TOP, button0, ConstraintAnchor.Type.BOTTOM);
        button1.connect(ConstraintAnchor.Type.RIGHT, button0, ConstraintAnchor.Type.RIGHT);
        root.add(button0);
        root.add(button1);
        root.layout();
        return root;
    }

    ConstraintWidgetContainer makeLayout2() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(1000, 1000);
        root.setDebugName("root");
        ConstraintWidget button0 = new ConstraintWidget(200, 20);
        button0.setDebugName("button0");
        button0.stringId = "button0";
        ConstraintWidget button1 = new ConstraintWidget(20, 200);
        button1.setDebugName("button1");
        button1.stringId = "button1";
        button0.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT);
        button0.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM);
        button1.connect(ConstraintAnchor.Type.LEFT, button0, ConstraintAnchor.Type.LEFT);
        button1.connect(ConstraintAnchor.Type.BOTTOM, button0, ConstraintAnchor.Type.TOP);
        button1.connect(ConstraintAnchor.Type.RIGHT, button0, ConstraintAnchor.Type.RIGHT);
        root.add(button0);
        root.add(button1);
        root.layout();
        return root;
    }

    @Test
    public void testTransition() {
        Transition transition = new Transition();
        ConstraintWidgetContainer cwc1 = makeLayout1();
        ConstraintWidgetContainer cwc2 = makeLayout2();

        for (ConstraintWidget child : cwc1.getChildren()) {
            WidgetFrame wf = transition.getStart(child);
            wf.widget = child;
        }
        transition.updateFrom(cwc1, Transition.START);

        for (ConstraintWidget child : cwc2.getChildren()) {
            WidgetFrame wf = transition.getEnd(child);
            wf.widget = child;
        }
        transition.updateFrom(cwc2, Transition.END);
        transition.interpolate(cwc1.getWidth(), cwc1.getHeight(), 0.5f);
        WidgetFrame s1 = transition.getStart("button1");
        WidgetFrame e1 = transition.getEnd("button1");
        WidgetFrame f1 = transition.getInterpolated("button1");
        assertNotNull(f1);
        assertEquals(20, s1.top);
        assertEquals(0, s1.left);
        assertEquals(780, e1.top);
        assertEquals(890, e1.left);
        System.out.println(s1.top + " ," + s1.left + " ----  " + s1.widget.getTop() + " ," + s1.widget.getLeft());
        System.out.println(e1.top + " ," + e1.left + " ----  " + e1.widget.getTop() + " ," + e1.widget.getLeft());
        System.out.println(f1.top + " ," + f1.left);

        assertEquals(400, f1.top);
        assertEquals(445, f1.left);

        assertEquals(20, s1.bottom - s1.top);
        assertEquals(200, s1.right - s1.left);
        assertEquals(110, f1.bottom - f1.top);
        assertEquals(110, f1.right - f1.left);
        assertEquals(200, e1.bottom - e1.top);
        assertEquals(20, e1.right - e1.left);

        System.out.println(s1.top + " ," + s1.left + " ----  " + s1.widget.getTop() + " ," + s1.widget.getLeft());
        System.out.println(e1.top + " ," + e1.left + " ----  " + e1.widget.getTop() + " ," + e1.widget.getLeft());
        System.out.println(f1.top + " ," + f1.left);
    }

    @Test
    public void testTransitionJson() {
        Transition transition = new Transition();
        ConstraintWidgetContainer cwc1 = makeLayout1();
        ConstraintWidgetContainer cwc2 = makeLayout2();

        for (ConstraintWidget child : cwc1.getChildren()) {
            WidgetFrame wf = transition.getStart(child);
            wf.widget = child;
        }
        transition.updateFrom(cwc1, Transition.START);

        for (ConstraintWidget child : cwc2.getChildren()) {
            WidgetFrame wf = transition.getEnd(child);
            wf.widget = child;
        }

        transition.updateFrom(cwc2, Transition.END);
        String jstr =
                "                  default: {\n"
                +        "                    from: 'start',   to: 'end',\n"
                +        "                    pathMotionArc: 'startVertical',\n"
                +        "                    KeyFrames: {\n"
                +        "                     KeyPositions: [\n"
                +        "                     {\n"
                +        "                      target: ['button1'],\n"
                +        "                      frames: [25, 50, 75],\n"
                +        "                      percentX: [0.2, 0.3, 0.7],\n"
                +        "                      percentY: [0.4, 0.9, 0.7]\n"
                +        "                      percentHeight: [0.4, 0.9, 0.7]\n"
                +        "                     }\n"
                +        "                     ]\n"
                +        "                  },\n"
                +        "                  }\n";

        try {
            CLObject json = CLParser.parse(jstr);
            TransitionParser.parse(json, transition);
        } catch (CLParsingException e) {
            e.printStackTrace();
        }
        transition.interpolate(cwc1.getWidth(), cwc1.getHeight(), 0.5f);
        WidgetFrame s1 = transition.getStart("button1");
        WidgetFrame e1 = transition.getEnd("button1");
        WidgetFrame f1 = transition.getInterpolated("button1");
        assertNotNull(f1);
        assertEquals(20, s1.top);
        assertEquals(0, s1.left);
        assertEquals(780, e1.top);
        assertEquals(890, e1.left);

        assertEquals(20, s1.bottom - s1.top);
        assertEquals(200, s1.right - s1.left);
        assertEquals(182, f1.bottom - f1.top);   // changed because of keyPosition
        assertEquals(110, f1.right - f1.left);
        assertEquals(200, e1.bottom - e1.top);
        assertEquals(20, e1.right - e1.left);

        System.out.println(s1.top + " ," + s1.left + " ----  " + s1.widget.getTop() + " ," + s1.widget.getLeft());
        System.out.println(e1.top + " ," + e1.left + " ----  " + e1.widget.getTop() + " ," + e1.widget.getLeft());
        System.out.println(f1.top + " ," + f1.left);

        assertEquals(736, f1.top);
        assertEquals(267, f1.left);
        System.out.println(s1.top + " ," + s1.left + " ----  " + s1.widget.getTop() + " ," + s1.widget.getLeft());
        System.out.println(e1.top + " ," + e1.left + " ----  " + e1.widget.getTop() + " ," + e1.widget.getLeft());
        System.out.println(f1.top + " ," + f1.left);
    }
}
