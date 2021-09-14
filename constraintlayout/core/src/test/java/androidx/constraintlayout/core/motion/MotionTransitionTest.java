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

import androidx.constraintlayout.core.state.Transition;
import androidx.constraintlayout.core.state.WidgetFrame;
import androidx.constraintlayout.core.widgets.ConstraintAnchor;
import androidx.constraintlayout.core.widgets.ConstraintWidget;
import androidx.constraintlayout.core.widgets.ConstraintWidgetContainer;

import org.junit.Test;

public class MotionTransitionTest {


    ConstraintWidgetContainer  makeLayout1() {
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
    ConstraintWidgetContainer  makeLayout2() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(1000, 1000);
        root.setDebugName("root");
        ConstraintWidget button0 = new ConstraintWidget(200, 20);
        button0.setDebugName("button0");
        button0.stringId = "button0";
        ConstraintWidget button1 = new ConstraintWidget(200, 20);
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
        transition.updateFrom(cwc1,Transition.START);

        for (ConstraintWidget child : cwc2.getChildren()) {
            WidgetFrame wf = transition.getEnd(child);
            wf.widget = child;
         }
        transition.updateFrom(cwc2,Transition.END);
        transition.interpolate(cwc1.getWidth(),cwc1.getHeight(),0.5f);
        WidgetFrame s1 = transition.getStart("button1");
        WidgetFrame e1 = transition.getEnd("button1");
        WidgetFrame f1 = transition.getInterpolated("button1");
        assertNotNull(f1);
        assertEquals(s1.top, 20);
        assertEquals(s1.left, 0);
        assertEquals(e1.top, 960);
        assertEquals(e1.left, 800);
        System.out.println(s1.top +" ,"+s1.left + " ----  " +s1.widget.getTop() +" ,"+s1.widget.getLeft());
        System.out.println(e1.top +" ,"+e1.left+ " ----  " +e1.widget.getTop() +" ,"+e1.widget.getLeft());
        System.out.println(f1.top +" ,"+f1.left);

        assertEquals(f1.top, 490);
        assertEquals(f1.left, 400);
        System.out.println(s1.top +" ,"+s1.left + " ----  " +s1.widget.getTop() +" ,"+s1.widget.getLeft());
        System.out.println(e1.top +" ,"+e1.left+ " ----  " +e1.widget.getTop() +" ,"+e1.widget.getLeft());
        System.out.println(f1.top +" ,"+f1.left);
    }

}
