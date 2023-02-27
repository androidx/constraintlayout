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

package com.example.constraintlayout

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.*

@Preview(group = "csl")
@Composable
fun DslIntro() {
    // create a constraintSet variable to be passed to ConstraintLayout
    val constraintSet = ConstraintSet {
        // create a reference for the Text and Button based on the Id
        val titleText = createRefFor("title")
        val button = createRefFor("btn")

        // Specify constrains of Text (titleText) and Button (button)
        constrain(titleText) {
            // add top constrain to parent top
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start)
            // add end constrain to button start
            end.linkTo(button.start)
        }
        constrain(button) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            start.linkTo(titleText.end)
            end.linkTo(parent.end)
        }
    }
    // Pass the constraintSet variable to ConstraintLayout and create widgets
    ConstraintLayout(constraintSet, modifier = Modifier.fillMaxSize()) {
        Button(onClick = {}, modifier = Modifier.layoutId("btn")) {
            Text(text = "button")
        }
        Text(text = "Hello World", modifier = Modifier.layoutId("title"))
    }
}

@Preview(group = "ml")
@Composable
fun MotionDslIntro() {
    // Create a MotionScene to be passed to MotionLayout
    // A basic MotionScene contains two ConstrainSets (can be more) to indicate the starting layout
    // and the ending layout as well as a Transition between the ConstrainSets
    val motionScene = MotionScene {
        val titleText = createRefFor("title")
        // basic "default" transition
        defaultTransition(
            // specify the starting layout
            from = constraintSet { // this: ConstraintSetScope
                constrain(titleText) { // this: ConstrainScope
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
            },
            // specify the ending layout
            to = constraintSet { // this: ConstraintSetScope
                constrain(titleText) { // this: ConstrainScope
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                }
            }
        )
    }
    // Pass the MotionScence variable to MotionLayout and create widgets
    MotionLayout(motionScene,
        progress = 0f, // progress when the transition starts (can be 0f - 1f)
        modifier = Modifier.fillMaxSize()) {
        Text(text = "Hello World", modifier = Modifier.layoutId("title"))
    }
}

@Preview(group = "kf")
@Composable
fun KeyframesDslIntro() {
    // Create a MotionScene to be passed to MotionLayout
    // A basic MotionScene contains two ConstrainSets (can be more) to indicate the starting layout
    // and the ending layout as well as a Transition between the ConstrainSets
    val motionScene = MotionScene {
        val titleText = createRefFor("title")
        // basic "default" transition
        defaultTransition(
            // specify the starting layout
            from = constraintSet { // this: ConstraintSetScope
                constrain(titleText) { // this: ConstrainScope
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
            },
            // specify the ending layout
            to = constraintSet { // this: ConstraintSetScope
                constrain(titleText) { // this: ConstrainScope
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                }
            },
        ) {
            val titleText = createRefFor("title")
            // change the alpha value at 20% and 80% of the progress
            keyAttributes(titleText) {
                frame(20) {
                    alpha = 0.2f
                }
                frame(80) {
                    alpha = 0.8f
                }
            }
            // change the x position at 20% and 80% of the progress
            keyPositions(titleText) {
                frame(20) {
                    percentX = 0.6f
                }
                frame(80) {
                    percentX = 0.3f
                }
            }
        }
    }
    // Pass the MotionScence variable to MotionLayout and create widgets
    MotionLayout(motionScene,
        progress = 0.75f, // progress when the transition starts (can be 0f - 1f)
        modifier = Modifier.fillMaxSize()) {
        Text(text = "Hello World", modifier = Modifier.layoutId("title"))
    }
}