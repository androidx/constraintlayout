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

package com.example.examplecomposegrid

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
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene


@OptIn(ExperimentalMotionApi::class)
@Preview(group = "grid1")
@Composable
public fun MotionGridDemo() {
    var animateToEnd by remember { mutableStateOf(false) }

    val progress = remember { Animatable(0f) }

    LaunchedEffect(animateToEnd) {
        progress.animateTo(if (animateToEnd) 1f else 0f,
            animationSpec = tween(3000))
    }

    Column(modifier = Modifier.background(Color.White)) {

        val scene1 = MotionScene("""
            {
                Header: {
                  name: 'splitDemo1'
                },
                
                ConstraintSets: {
                  start: {
                    split: { 
                        height: 'parent',
                        width: 'parent',
                        type: 'grid',
                        orientation: 0,
                        vGap: 10,
                        hGap: 15,
                        rows: 2,
                        columns: 3,
                        contains: ["btn1", "btn2", "btn3", "btn4", "btn5", "btn6"],
                      },
                  },
                  
                  end: {
                    split: { 
                        height: 'parent',
                        width: 'parent',
                        type: 'grid',
                        orientation: 1,
                        rows: 3,
                        columns: 2,
                        contains: ["btn1", "btn2", "btn3", "btn4", "btn5", "btn6"],
                      },
                  }
                },
                
                Transitions: {
                  default: {
                    from: 'start', to: 'end',
                  }
                }
            }
            """)

        MotionLayout(
            modifier    = Modifier
                .fillMaxWidth()
                .height(400.dp),
            motionScene = scene1,
            progress = progress.value) {
            val numArray = arrayOf("1", "2", "3", "4", "5", "6")
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

@OptIn(ExperimentalMotionApi::class)
@Preview(group = "grid2")
@Composable
public fun MotionGridDemo2() {
    var animateToEnd by remember { mutableStateOf(false) }

    val progress = remember { Animatable(0f) }

    LaunchedEffect(animateToEnd) {
        progress.animateTo(if (animateToEnd) 1f else 0f,
            animationSpec = tween(3000))
    }

    Column(modifier = Modifier.background(Color.White)) {

        val scene1 = MotionScene("""
            {
                Header: {
                  name: 'splitDemo1'
                },
                
                ConstraintSets: {
                  start: {
                    split1: { 
                        height: 'parent',
                        width: 'parent',
                        type: 'grid',
                        orientation: 0,
                        hGap: 10,
                        columns: 1,
                        contains: ["btn1", "btn2", "split2", "btn3", "btn4"],
                      },
                      split2: { 
                        height: 'spread',
                        width: 'spread',
                        type: 'grid',
                        orientation: 0,
                        hGap: 15,
                        rows: 1,
                        contains: ["btn5", "btn6", "btn7"],
                      },
                      btn1: {
                        width: "spread",
                        height: "spread",
                      },
                      btn2: {
                        width: "spread",
                        height: "spread",
                      },
                      btn3: {
                        width: "spread",
                        height: "spread",
                      },
                      btn4: {
                        width: "spread",
                        height: "spread",
                      },
                      btn5: {
                        width: "spread",
                        height: "spread",
                      },
                      btn6: {
                        width: "spread",
                        height: "spread",
                      },
                      btn7: {
                        width: "spread",
                        height: "spread",
                      }
                  },
                  
                  end: {
                    split1: { 
                        height: 'parent',
                        width: 'parent',
                        type: 'grid',
                        orientation: 0,
                        vGap: 10,
                        hGap: 15,
                        rows: 1,
                        contains: ["btn1", "btn2", "split2", "btn3", "btn4"],
                      },
                      split2: { 
                        height: 'spread',
                        width: 'spread',
                        type: 'grid',
                        orientation: 0,
                        hGap: 15,
                        columns: 1,
                        contains: ["btn5", "btn6", "btn7"],
                      },
                      btn1: {
                        width: "spread",
                        height: "spread",
                      },
                      btn2: {
                        width: "spread",
                        height: "spread",
                      },
                      btn3: {
                        width: "spread",
                        height: "spread",
                      },
                      btn4: {
                        width: "spread",
                        height: "spread",
                      },
                      btn5: {
                        width: "spread",
                        height: "spread",
                      },
                      btn6: {
                        width: "spread",
                        height: "spread",
                      },
                      btn7: {
                        width: "spread",
                        height: "spread",
                      }
                  }
                },
                
                Transitions: {
                  default: {
                    from: 'start', to: 'end',
                  }
                }
            }
            """)

        MotionLayout(
            modifier    = Modifier
                .fillMaxWidth()
                .height(400.dp),
            motionScene = scene1,
            progress = progress.value) {
            val numArray = arrayOf("1", "2", "3", "4", "5", "6", "7")
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