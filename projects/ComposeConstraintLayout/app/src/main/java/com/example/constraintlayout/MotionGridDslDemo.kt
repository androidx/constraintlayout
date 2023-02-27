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

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene


@Preview(group = "grid1")
@Composable
public fun MotionGridDslDemo() {
    val numArray = arrayOf("1", "2", "3", "4", "5", "6")
    var animateToEnd by remember { mutableStateOf(false) }
    val progress = remember { Animatable(0f) }

    LaunchedEffect(animateToEnd) {
        progress.animateTo(if (animateToEnd) 1f else 0f,
            animationSpec = tween(3000))
    }

    Column(modifier = Modifier.background(Color.White)) {
        val scene1 = MotionScene {
            val elem = Array(numArray.size) { i -> createRefFor(i) }
            for (i in numArray.indices) {
                elem[i] = createRefFor(String.format("btn%s", numArray[i]))
            }
            // basic "default" transition
            defaultTransition(
                // specify the starting layout
                from = constraintSet { // this: ConstraintSetScope
                    val grid = createGrid(
                        elements = *elem,
                        rows = 2,
                        columns = 3,
                    )
                    constrain(grid) {
                        width = Dimension.matchParent
                        height = Dimension.matchParent
                    }
                },
                // specify the ending layout
                to = constraintSet { // this: ConstraintSetScope
                    val grid = createGrid(
                        elements = *elem,
                        rows = 3,
                        columns = 2,
                    )
                    constrain(grid) {
                        width = Dimension.matchParent
                        height = Dimension.matchParent
                    }
                }
            )
        }

        MotionLayout(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            motionScene = scene1,
            progress = progress.value) {
            for (num in numArray) {
                Button(
                    modifier = Modifier.layoutId(String.format("btn%s", num)),
                    onClick = {},
                ) {
                    Text(text = num, fontSize = 35.sp)
                }
            }
        }
        Button(onClick = { animateToEnd = !animateToEnd },
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp)) {
            Text(text = "Run")
        }
    }
}

@Preview(group = "grid2")
@Composable
public fun MotionDslDemo2() {
    val numArray = arrayOf("1", "2", "3", "4", "5", "6", "7")
    var animateToEnd by remember { mutableStateOf(false) }
    val progress = remember { Animatable(0f) }

    LaunchedEffect(animateToEnd) {
        progress.animateTo(if (animateToEnd) 1f else 0f,
            animationSpec = tween(3000))
    }

    Column(modifier = Modifier.background(Color.White)) {
        val scene1 = MotionScene {
            val btn1 = createRefFor("btn1")
            val btn2 = createRefFor("btn2")
            val btn3 = createRefFor("btn3")
            val btn4 = createRefFor("btn4")
            val btn5 = createRefFor("btn5")
            val btn6 = createRefFor("btn6")
            val btn7 = createRefFor("btn7")

            // basic "default" transition
            defaultTransition(
                // specify the starting layout
                from = constraintSet { // this: ConstraintSetScope
                    val row = createRow(
                        btn5, btn6, btn7,
                        horizontalGap = 10.dp
                    )
                    val column = createColumn(
                        btn1, btn2, row, btn3, btn4,
                        verticalGap = 10.dp
                    )
                    constrain(row) {
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
                    constrain(column) {
                        width = Dimension.matchParent
                        height = Dimension.matchParent
                    }
                    constrain(btn1) {
                        width = Dimension.fillToConstraints
                    }
                    constrain(btn2) {
                        width = Dimension.fillToConstraints
                    }
                    constrain(btn3) {
                        width = Dimension.fillToConstraints
                    }
                    constrain(btn4) {
                        width = Dimension.fillToConstraints
                    }
                    constrain(btn5) {
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
                    constrain(btn6) {
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
                    constrain(btn7) {
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
                },
                // specify the ending layout
                to = constraintSet { // this: ConstraintSetScope
                    val column = createColumn(
                        btn5, btn6, btn7,
                        verticalGap = 10.dp
                    )
                    val row = createRow(
                        btn1, btn2, column, btn3, btn4,
                        horizontalGap = 10.dp
                    )
                    constrain(row) {
                        width = Dimension.matchParent
                        height = Dimension.matchParent
                    }
                    constrain(column) {
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
                    constrain(btn1) {
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
                    constrain(btn2) {
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
                    constrain(btn3) {
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
                    constrain(btn4) {
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
                    constrain(btn5) {
                        height = Dimension.fillToConstraints
                    }
                    constrain(btn6) {
                        height = Dimension.fillToConstraints
                    }
                    constrain(btn7) {
                        height = Dimension.fillToConstraints
                    }
                }
            )
        }

        MotionLayout(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            motionScene = scene1,
            progress = progress.value) {
            for (num in numArray) {
                Button(
                    modifier = Modifier.layoutId(String.format("btn%s", num)),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Gray.copy(
                            alpha = 0.1F,
                        ),
                    ),
                    onClick = {},
                ) {
                    Text(text = num, fontSize = 35.sp)
                }
            }
        }

        Button(onClick = { animateToEnd = !animateToEnd },
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp)) {
            Text(text = "Run")
        }
    }
}