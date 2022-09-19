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

package com.example.constraintlayout

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene

@Preview(group = "motion")
@Composable
public fun MotionFlowDemo() {
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
                  name: 'flowDemo1'
                },
                
                ConstraintSets: {
                  start: {
                    flow1: { 
                        width: 'parent',
                        height: 'parent',
                        type: 'hFlow',
                        vGap: 20,
                        hGap: 20,
                        contains: ['1', '2', '3', '4'],
                      }
                  },
                  
                  end: {
                    flow2: { 
                        width: 'match_parent',
                        height: 'match_parent',
                        type: 'vFlow',
                        wrap: 'none',
                        vGap: 20,
                        hGap: 20,
                        contains: ['1', '2', '3', '4'],
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
            val numArray = arrayOf("1", "2", "3", "4")
            for (num in numArray) {
                Button(
                    modifier = Modifier.layoutId(num),
                    onClick = {},
                ) {
                    Text(text = num)
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

@Preview(group = "motion")
@Composable
public fun MotionFlowDemo2() {
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
                  name: 'flowDemo2'
                },
                
                ConstraintSets: {
                  start: {
                    flow1: { 
                        width: 'parent',
                        height: 'parent',
                        type: 'hFlow',
                        vGap: 20,
                        hGap: 20,
                        contains: ['1', '2', '3', '4', '5'],
                      }
                  },
                  
                  end: {
                    flow2: { 
                        width: 300,
                        height: 300,
                        type: 'vFlow',
                        wrap: 'aligned',
                        maxElement: 3,
                        vGap: 20,
                        hGap: 20,
                        contains: ['1', '2', '3', '4', '5'],
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
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            motionScene = scene1,
            progress = progress.value) {
            val numArray = arrayOf("1", "2", "3", "4", "5")
            for (num in numArray) {
                Button(
                    modifier = Modifier.layoutId(num),
                    onClick = {},
                ) {
                    Text(text = num)
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


@Preview(group = "motion")
@Composable
public fun MotionFlowDemo3() {
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
                  name: 'flowDemo3'
                },
                
                ConstraintSets: {
                  start: {
                    flow1: { 
                        width: 'parent',
                        height: 'parent',
                        type: 'vFlow',
                        wrap: 'chain',
                        maxElement: 3,
                        vStyle: ['spread', 'packed', 'spread_inside'],
                        vGap: 20,
                        hGap: 20,
                        contains: ['1', '2', '3', '4', '5', '6', '7', '8'],
                      }
                  },
                  
                  end: {
                    flow2: { 
                        width: 'wrap',
                        height: 'wrap',
                        type: 'hFlow',
                        wrap: 'chain',
                        maxElement: 3,
                        vGap: 20,
                        hGap: 20,
                        contains: ['1', '2', '3', '4', '5', '6', '7', '8'],
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
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp),
            motionScene = scene1,
            progress = progress.value) {
            val numArray = arrayOf("1", "2", "3", "4", "5", "6", "7", "8")
            for (num in numArray) {
                Button(
                    modifier = Modifier.layoutId(num),
                    onClick = {},
                ) {
                    Text(text = num)
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

@Preview(group = "motion")
@Composable
public fun MotionFlowDemo4() {
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
                  name: 'flowDemo4'
                },
                
                ConstraintSets: {
                  start: {
                    flow1: { 
                        width: 'parent',
                        height: 'parent',
                        type: 'hFlow',
                        wrap: 'chain',
                        maxElement: 3,
                        padding: 20,
                        hStyle: ['spread', 'packed', 'spread_inside'],
                        contains: ['1', '2', '3', '4', '5', '6', '7', '8'],
                      }
                  },
                  
                  end: {
                    flow2: { 
                        width: 'wrap',
                        height: 'wrap',
                        type: 'vFlow',
                        wrap: 'chain',
                        vGap: 10,
                        hGap: 10,
                        vBias: 0.1,
                        hBias: 0.9,
                        maxElement: 3,
                        contains: ['1', '2', '3', '4', '5', '6', '7', '8'],
                        start: ['parent', 'start', 20],
                        top: ['parent', 'top', 20],
                        end: ['parent', 'end', 20],
                        bottom: ['parent', 'bottom', 20]
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
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp),
            motionScene = scene1,
            progress = progress.value) {
            val numArray = arrayOf("1", "2", "3", "4", "5", "6", "7", "8")
            for (num in numArray) {
                Button(
                    modifier = Modifier.layoutId(num),
                    onClick = {},
                ) {
                    Text(text = num)
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