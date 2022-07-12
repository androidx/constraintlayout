/*
 * Copyright (C) 2022 The Android Open Source Project
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

package androidx.constraintlayout.core.dsl;

import static org.junit.Assert.assertEquals;

import androidx.constraintlayout.core.parser.CLParser;
import androidx.constraintlayout.core.parser.CLParsingException;
import androidx.constraintlayout.core.state.CorePixelDp;
import androidx.constraintlayout.core.state.TransitionParser;

import org.junit.Test;

public class DslTest {
    //  test structures
    static CorePixelDp dipToDip = new CorePixelDp() {

        @Override
        public float toPixels(float dp) {
            return dp;
        }
    };
    static androidx.constraintlayout.core.state.Transition transitionState =
            new androidx.constraintlayout.core.state.Transition();


    @Test
    public void testTransition01() {
        MotionScene motionScene = new MotionScene();
        motionScene.addTransition(new Transition("start", "end"));
        System.out.println(motionScene);
        String exp = "{\n"
                + "Transitions: { \n"
                + "default:{ \n"
                + "from: 'start',\n"
                + "to: 'end',\n"
                + "}\n"
                + "}\n"
                + "}\n";
        assertEquals(exp, motionScene.toString());
    }

    @Test
    public void testTransition02() {
        MotionScene motionScene = new MotionScene();
        motionScene.addTransition(new Transition("expand", "start", "end"));
        System.out.println(motionScene);
        String exp = "{\n"
                + "Transitions: { \n"
                + "expand:{ \n"
                + "from: 'start',\n"
                + "to: 'end',\n"
                + "}\n"
                + "}\n"
                + "}\n";
        assertEquals(exp, motionScene.toString());
    }

    @Test
    public void testOnSwipe01() throws CLParsingException {
        MotionScene motionScene = new MotionScene();
        Transition transition = new Transition("expand", "start", "end");
        transition.setOnSwipe(new OnSwipe());
        motionScene.addTransition(transition);

        System.out.println(motionScene);
        String exp = "{\n"
                + "Transitions: { \n"
                + "expand:{ \n"
                + "from: 'start',\n"
                + "to: 'end',\n"
                + "OnSwipe:{\n"
                + "}\n"
                + "}\n"
                + "}\n"
                + "}\n";
        assertEquals(exp, motionScene.toString());

        TransitionParser.parse(CLParser.parse(transition.toString()), transitionState, dipToDip);
    }

    @Test
    public void testOnSwipe02() throws CLParsingException {
        MotionScene motionScene = new MotionScene();
        Transition transition = new Transition("expand", "start", "end");
        transition.setOnSwipe(new OnSwipe("button", OnSwipe.Side.RIGHT, OnSwipe.Drag.RIGHT));
        motionScene.addTransition(transition);

        System.out.println(motionScene);
        String exp = "{\n"
                + "Transitions: { \n"
                + "expand:{ \n"
                + "from: 'start',\n"
                + "to: 'end',\n"
                + "OnSwipe:{\n"
                + "anchor: 'button',\n"
                + "direction: 'RIGHT',\n"
                + "side: 'RIGHT',\n"
                + "}\n"
                + "}\n"
                + "}\n"
                + "}\n";

        assertEquals(exp, motionScene.toString());

        TransitionParser.parse(CLParser.parse(transition.toString()), transitionState, dipToDip);
    }

    @Test
    public void testOnKeyPosition01() throws CLParsingException {
        MotionScene motionScene = new MotionScene();
        Transition transition = new Transition("expand", "start", "end");
        transition.setOnSwipe(new OnSwipe("button", OnSwipe.Side.RIGHT, OnSwipe.Drag.RIGHT));
        KeyPosition kp = new KeyPosition("button", 32);
        kp.setPercentX(0.5f);
        transition.setKeyFrames(kp);
        motionScene.addTransition(transition);

        System.out.println(motionScene);
        String exp = "{\n"
                + "Transitions: { \n"
                + "expand:{ \n"
                + "from: 'start',\n"
                + "to: 'end',\n"
                + "OnSwipe:{\n"
                + "anchor: 'button',\n"
                + "direction: 'RIGHT',\n"
                + "side: 'RIGHT',\n"
                + "}\n"
                + "keyFrames: {\n"
                + "KeyPositions:{\n"
                + "target: 'button',\n"
                + "frame: 32,\n"
                + "type: 'CARTESIAN',\n"
                + "percentX: 0.5,\n"
                + "}\n"
                + "}\n"
                + "}\n"
                + "}\n"
                + "}\n";
        assertEquals(exp, motionScene.toString());

        String formattedJson = CLParser.parse(motionScene.toString()).toFormattedJSON();
        String fomatExp = "{\n"
                + "  Transitions: {\n"
                + "    expand: {\n"
                + "      from: 'start',\n"
                + "      to: 'end',\n"
                + "      OnSwipe: { anchor: 'button', direction: 'RIGHT', side: 'RIGHT' },\n"
                + "      keyFrames: {\n"
                + "        KeyPositions: {\n"
                + "          target:           'button',\n"
                + "          frame:           32,\n"
                + "          type:           'CARTESIAN',\n"
                + "          percentX:           0.5\n"
                + "        }\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "}";

        assertEquals(fomatExp, formattedJson);

        TransitionParser.parse(CLParser.parse(transition.toString()), transitionState, dipToDip);
    }

    @Test
    public void testOnKeyPositions0() throws CLParsingException {
        MotionScene motionScene = new MotionScene();
        Transition transition = new Transition("expand", "start", "end");
        transition.setOnSwipe(new OnSwipe("button", OnSwipe.Side.RIGHT, OnSwipe.Drag.RIGHT));
        KeyPositions kp = new KeyPositions(2,"button1","button2");

        transition.setKeyFrames(kp);
        motionScene.addTransition(transition);

        System.out.println(motionScene);
        String exp = "{\n"
                + "Transitions: { \n"
                + "expand:{ \n"
                + "from: 'start',\n"
                + "to: 'end',\n"
                + "OnSwipe:{\n"
                + "anchor: 'button',\n"
                + "direction: 'RIGHT',\n"
                + "side: 'RIGHT',\n"
                + "}\n"
                + "keyFrames: {\n"
                + "KeyPositions:{\n"
                + "target: ['button1','button2'],\n"
                + "frame: [33, 66],\n"
                + "}\n"
                + "}\n"
                + "}\n"
                + "}\n"
                + "}\n";
        assertEquals(exp, motionScene.toString());

        String formattedJson = CLParser.parse(motionScene.toString()).toFormattedJSON();
        String fomatExp = "{\n"
                + "  Transitions: {\n"
                + "    expand: {\n"
                + "      from: 'start',\n"
                + "      to: 'end',\n"
                + "      OnSwipe: { anchor: 'button', direction: 'RIGHT', side: 'RIGHT' },\n"
                + "      keyFrames: { KeyPositions: { target: ['button1', 'button2'], frame: [33,"
                + " 66] } }\n"
                + "    }\n"
                + "  }\n"
                + "}";

        assertEquals(fomatExp, formattedJson);

        TransitionParser.parse(CLParser.parse(transition.toString()), transitionState, dipToDip);
    }

}
