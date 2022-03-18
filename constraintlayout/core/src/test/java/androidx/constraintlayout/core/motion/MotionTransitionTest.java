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
import static org.junit.Assert.assertTrue;

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

import java.util.Arrays;

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
    ConstraintWidgetContainer makeLayout(int w1, int h1, int w2, int h2) {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(1000, 1000);
        root.setDebugName("root");
        root.stringId = "root";
        ConstraintWidget button0 = new ConstraintWidget( w1, h1);
        button0.setDebugName("button0");
        button0.stringId = "button0";

        ConstraintWidget button1 = new ConstraintWidget(w2, h2);
        button1.setDebugName("button1");
        button1.stringId = "button1";

        button0.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT);
        button0.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP);
        button1.connect(ConstraintAnchor.Type.LEFT, button0, ConstraintAnchor.Type.RIGHT);
        button1.connect(ConstraintAnchor.Type.TOP, button0, ConstraintAnchor.Type.BOTTOM);
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
        System.out.println(s1.top + " ," + s1.left + " ----  "
                + s1.widget.getTop() + " ," + s1.widget.getLeft());
        System.out.println(e1.top + " ," + e1.left + " ----  "
                + e1.widget.getTop() + " ," + e1.widget.getLeft());
        System.out.println(f1.top + " ," + f1.left);

        assertEquals(400, f1.top);
        assertEquals(445, f1.left);

        assertEquals(20, s1.bottom - s1.top);
        assertEquals(200, s1.right - s1.left);
        assertEquals(110, f1.bottom - f1.top);
        assertEquals(110, f1.right - f1.left);
        assertEquals(200, e1.bottom - e1.top);
        assertEquals(20, e1.right - e1.left);

        System.out.println(s1.top + " ," + s1.left + " ----  "
                + s1.widget.getTop() + " ," + s1.widget.getLeft());
        System.out.println(e1.top + " ," + e1.left + " ----  "
                + e1.widget.getTop() + " ," + e1.widget.getLeft());
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
                        + "                    from: 'start',   to: 'end',\n"
                        + "                    pathMotionArc: 'startVertical',\n"
                        + "                    onSwipe: { \n"
                        + "                        anchor : 'button1',\n"
                        + "                    side: 'top',\n"
                        + "                        direction: 'up',\n"
                        + "                        scale: 1,\n"
                        + "                        threshold: 10,\n"
                        + "                        mode:  'velocity',\n"
                        + "                        maxVelocity: 4.0,\n"
                        + "                        maxAccel: 4.0,\n"
                        + "                   },      "
                        + "                    KeyFrames: {\n"
                        + "                     KeyPositions: [\n"
                        + "                     {\n"
                        + "                      target: ['button1'],\n"
                        + "                      frames: [25, 50, 75],\n"
                        + "                      percentX: [0.2, 0.3, 0.7],\n"
                        + "                      percentY: [0.4, 0.9, 0.7]\n"
                        + "                      percentHeight: [0.4, 0.9, 0.7]\n"
                        + "                     }\n"
                        + "                     ]\n"
                        + "                  },\n"
                        + "                  }\n";

        try {
            CLObject json = CLParser.parse(jstr);
            TransitionParser.parse(json, transition);
        } catch (CLParsingException e) {
            e.printStackTrace();
        }
        assertTrue(transition.hasOnSwipe());
        // because a drag of 76 pixels (dy) is 1/10 the distance to travel  it returns 0.1
        float progress = transition.dragToProgress(0.5f, 10f, 76);
        assertEquals(0.1f,progress,0.001f);

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

        print("start  ", s1);
        print("end    ", e1);
        print("at(0.5)", f1);
        System.out.println("start   =" + s1.top + " ," + s1.left + " ----  "
                + s1.widget.getTop() + " ," + s1.widget.getLeft());
        System.out.println("end     =" + e1.top + " ," + e1.left + " ----  "
                + e1.widget.getTop() + " ," + e1.widget.getLeft());
        System.out.println("at(0.5) =" + f1.top + " ," + f1.left);

        assertEquals(736, f1.top);
        assertEquals(267, f1.left);
        System.out.println(s1.top + " ," + s1.left + " ----  "
                + s1.widget.getTop() + " ," + s1.widget.getLeft());
        System.out.println(e1.top + " ," + e1.left + " ----  "
                + e1.widget.getTop() + " ," + e1.widget.getLeft());
        System.out.println(f1.top + " ," + f1.left);
    }

    @Test
    public void testTransitionJson2() throws CLParsingException {
        Transition transition = new Transition();
        ConstraintWidgetContainer cwc1 = makeLayout(100,100,100,100);
        ConstraintWidgetContainer cwc2 =  makeLayout(500,900,100,100);
        // button1 move down 800 and to the right 400
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
        String jsonString =
                "                  default: {\n"
                        + "                    from: 'start',   to: 'end',\n"
                        + "                    pathMotionArc: 'startHorizontal',\n"
                        + "                    onSwipe: { \n"
                        + "                        anchor :'button1',\n"
                        + "                        side: 'top',\n"
                        + "                        direction: 'up',\n"
                        + "                        scale: 1,\n"
                        + "                        threshold: 10,\n"
                        + "                        mode:  'velocity',\n"
                        + "                        maxVelocity: 4.0,\n"
                        + "                        maxAccel: 4.0,\n"
                        + "                   },      "
                        + "                    KeyFrames: {\n"
                        + "                     KeyPositions: [\n"
                        + "                     {\n"
                        + "                      target: ['button1'],\n"
                        + "                      frames: [25, 50, 75],\n"
                        + "                      percentX: [0.2, 0.3, 0.7],\n"
                        + "                      percentY: [0.4, 0.9, 0.7]\n"
                        + "                      percentHeight: [0.4, 0.9, 0.7]\n"
                        + "                     }\n"
                        + "                     ]\n"
                        + "                  },\n"
                        + "                  }\n";


        CLObject json = CLParser.parse(jsonString);

        TransitionParser.parse(json, transition);

        assertTrue(transition.hasOnSwipe());
        // because a drag of 80 pixels (dy) is 1/10 the distance to travel  it returns 0.1
        float progress = transition.dragToProgress(0.5f, 10f, 80);
        assertEquals(0.1f,progress,0.001f);
        progress = transition.dragToProgress(0.3f, 10f, 80);
        assertEquals(0.1f,progress,0.001f);

        float[]pos = new float[100];
        float[]ratio = new float[100];
        for (int i = 0; i < ratio.length; i++) {
            pos[i] =  i/(float)(ratio.length-1);
            transition.interpolate(cwc1.getWidth(), cwc1.getHeight(), pos[i]);

            ratio[i] =  transition.dragToProgress(pos[i], 10f, 1);
        }
        transition.interpolate(cwc1.getWidth(), cwc1.getHeight(), 0.5f);

        String delta = textDraw(80,30, pos , ratio);
        System.out.println(delta);
        transition.interpolate(cwc1.getWidth(), cwc1.getHeight(), 0.5f);

        WidgetFrame s1 = transition.getStart("button1");
        WidgetFrame e1 = transition.getEnd("button1");
        WidgetFrame f1 = transition.getInterpolated("button1");

        assertNotNull(f1);
        assertEquals(100, s1.top);
        assertEquals(100, s1.left);
        assertEquals(900, e1.top);
        assertEquals(500, e1.left);

        assertEquals(100, s1.bottom - s1.top);
        assertEquals(100, s1.right - s1.left);
        assertEquals(100, f1.bottom - f1.top);   // changed because of keyPosition
        assertEquals(100, f1.right - f1.left);
        assertEquals(100, e1.bottom - e1.top);
        assertEquals(100, e1.right - e1.left);


        print("start  ", s1);
        print("end    ", e1);
        print("at(0.5)", f1);
        System.out.println("start   =" + s1.top + " ," + s1.left + " ----  "
                + s1.widget.getTop() + " ," + s1.widget.getLeft());
        System.out.println("end     =" + e1.top + " ," + e1.left + " ----  "
                + e1.widget.getTop() + " ," + e1.widget.getLeft());
        System.out.println("at(0.5) =" + f1.top + " ," + f1.left);

        assertEquals(810, f1.top);
        assertEquals(270, f1.left);


        System.out.println(s1.top + " ," + s1.left + " ----  "
                + s1.widget.getTop() + " ," + s1.widget.getLeft());
        System.out.println(e1.top + " ," + e1.left + " ----  "
                + e1.widget.getTop() + " ," + e1.widget.getLeft());
        System.out.println(f1.top + " ," + f1.left);

        float []xp = new float[200];
        float []yp = new float[xp.length];

        for (int i = 0; i < yp.length; i++) {
            float v = yp[i];
            float p = i/(yp.length-1f);

            transition.interpolate(cwc1.getWidth(), cwc1.getHeight(), p);

            WidgetFrame dynamic = transition.getInterpolated("button1");
            xp[i] = (dynamic.left+dynamic.right)/2;
            yp[i] = (dynamic.top+dynamic.bottom)/2;
        }
        String str = textDraw(80,30, xp , yp);
        System.out.println(str);
        String expect =
                  "|**                                                                              |\n"
                + "| **                                                                             |\n"
                + "|  **                                                                            |\n"
                + "|    **                                                                          |\n"
                + "|     **                                                                         |\n"
                + "|      **                                                                        |\n"
                + "|        **                                                                      |\n"
                + "|         **                                                                     |\n"
                + "|          **                                                                    |\n"
                + "|           **                                                                   |\n"
                + "|            *                                                                   |\n"
                + "|             *                                                                  |\n"
                + "|             *                                                                  |\n"
                + "|             **                                                                 |\n"
                + "|              *                                                                 |\n"
                + "|              *                                                                 |\n"
                + "|               *                                                                |\n"
                + "|               *                                                                |\n"
                + "|               *                                                                |\n"
                + "|                *                                                      *********|\n"
                + "|                *                                               ***** *      ** |\n"
                + "|                 *                                         ** **           ***  |\n"
                + "|                 **                                 * ** **              ***    |\n"
                + "|                  ***                          ** **                   **       |\n"
                + "|                    **                   **** *                     ***         |\n"
                + "|                      ************* ****                          ***           |\n"
                + "|                                                               ***              |\n"
                + "|                                                             ***                |\n"
                + "|                                                           **                   |\n"
                + "|                                                          *                     |\n";
        assertEquals(expect,str);
    }

    String textDraw(int dimx,int dimy,float[]x,float[]y) {
        float minX=x[0],maxX=x[0],minY=y[0],maxY=y[0];
        String ret = "";
        for (int i = 0; i < x.length; i++) {
            minX = Math.min(minX,x[i]);
            maxX = Math.max(maxX,x[i]);
            minY = Math.min(minY,y[i]);
            maxY = Math.max(maxY,y[i]);
        }
        char [][]c = new char[dimy][dimx];
        for (int i = 0; i <dimy; i++) {
            Arrays.fill(c[i], ' ');
        }
        int dimx1 = dimx - 1;
        int dimy1 = dimy - 1;
            for (int j = 0; j < x.length; j++) {
                int xp = (int) ( dimx1*(x[j]-minX)/(maxX-minX));
                int yp = (int) ( dimy1*(y[j]-minY)/(maxY-minY));

                 c[yp][xp] = '*';
            }

        for (int i = 0; i < c.length; i++) {
           ret+="|"+new String(c[i])+"|\n";
        }
        return ret;
    }

    void print(String name, WidgetFrame f) {
        System.out.println(name+" "+ fix(f.left)+","+fix(f.top)+","+fix(f.bottom)+","+fix(f.right));
    }
    String fix(int p) {
        String str = "     "+p;
        return str.substring(str.length()-4);
    }
}

