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

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionLayoutDebugFlags
import androidx.constraintlayout.compose.MotionScene
import androidx.constraintlayout.compose.layoutId
import androidx.constraintlayout.compose.rememberMotionLayoutState

@Preview
@Composable
fun OnSwipeExperiment() {
    var mode by remember {
        mutableStateOf("spring")
    }
    var toEnd by remember { mutableStateOf(true) }
    val motionLayoutState = rememberMotionLayoutState(key = mode)

    val motionSceneContent = remember(mode) {
        // language=json5
        """
       {
         Header: { exportAs: 'swipeExpr' },
         ConstraintSets: {
           start: {
             box: {
               width: 50, height: 50,
               bottom: ['parent', 'bottom', 10],
               start: ['parent', 'start', 10],
               custom: {
                 bColor: '#ff0000'
               }
             }
           },
           end: {
             Extends: 'start',
             box: {
               clear: ['constraints'],
                  width: 100, height: 400,
               top: ['parent', 'top', 10],
               end: ['parent', 'end', 10],
               custom: {
                 bColor: '#0000ff'
               }
             }
           }
         },
         Transitions: {
           default: {
              from: 'start',
              to: 'end',
              KeyFrames: {
                KeyPositions: [{
                  target: ['box'],
                  frames: [25, 50, 75],
                  percentX: [0.25, 0.5, 0.75],
                  percentY: [0.25, 0.5, 0.75]
                }],
              },
              onSwipe: {
                anchor: 'box',
                direction: 'end',
                side: 'start',
                mode: '$mode',
                springMass: 1,
                springDamping: 10,
                springStiffness: 70,
              }
           }
         }
       }
        """.trimIndent()
    }
    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = { motionLayoutState.snapTo(0f) }) {
                Text(text = "Reset")
            }
            Button(onClick = {
                val target = if (toEnd) 1f else 0f
                motionLayoutState.animateTo(target, tween(2000))
                toEnd = !toEnd
            }) {
                Text(text = if (toEnd) "End" else "Start")
            }
            Button(onClick = {
                if (motionLayoutState.isInDebugMode) {
                    motionLayoutState.setDebugMode(MotionLayoutDebugFlags.NONE)
                } else {
                    motionLayoutState.setDebugMode(MotionLayoutDebugFlags.SHOW_ALL)
                }
            }) {
                Text("Debug")
            }
            Button(onClick = {
                mode = when (mode) {
                    "spring" -> "velocity"
                    else -> "spring"
                }
            }) {
                Text(text = "Mode: $mode")
            }
        }
        MotionLayout(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.0f, fill = true),
            motionLayoutState = motionLayoutState,
            motionScene = MotionScene(content = motionSceneContent)
        ) {
            Box(
                modifier = Modifier
                    .background(motionProperties(id = "box").value.color("bColor"))
                    .layoutId("box")
            )
        }
        Text(text = "Current progress: ${motionLayoutState.currentProgress}")
    }
}


@Preview
@Composable
fun OnSwipeSample1() {

    var scene =
        """
       {
         ConstraintSets: {
           start: {
             box: {
               width: 50, height: 50,
               bottom: ['parent', 'bottom', 10],
               start: ['parent', 'start', 10],
             }
           },
           end: {
       
             box: {
                width: 50, height: 50,
                top: ['parent', 'top', 10],
                end: ['parent', 'end', 10],
             }
           }
         },
         Transitions: {
           default: {
              from: 'start',
              to: 'end',
              onSwipe: {
                anchor: 'box',
                maxVelocity: 4.2,
                maxAccel: 3,
                direction: 'end',
                side: 'start',
                mode: 'velocity'
              }
           }
         }
       }
        """.trimIndent()

    Column {
        MotionLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            motionScene = MotionScene(content = scene),
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Red)
                    .layoutId("box")
            )
        }
    }
}


@Preview
@Composable
fun OnSwipeSample2() {

    var scene =
        """
       {
         ConstraintSets: {
           start: {
             box: {
               width: 50, height: 50,
               bottom: ['parent', 'bottom', 70],
               start: ['parent', 'start', 70],
             }
           },
           end: {
       
             box: {
                width: 50, height: 50,
                top: ['parent', 'top', 70],
                end: ['parent', 'end', 70],
             }
           }
         },
         Transitions: {
           default: {
              from: 'start',
              to: 'end',
              onSwipe: {
                anchor: 'box',
                direction: 'end',
                side: 'start',
                mode: 'spring'
              }
           }
         }
       }
        """.trimIndent()

    Column {
        MotionLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            motionScene = MotionScene(content = scene),
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Green)
                    .layoutId("box")
            )
        }
    }
}

@Preview
@Composable
fun MultiSwipe() {

    val mode = arrayOf("velocity", "spring")
    val touchUp = arrayOf(
        "autocomplete", "toStart",
        "toEnd", "stop", "decelerate",
        "neverCompleteStart", "neverCompleteEnd"
    )
    val endWidth = arrayOf(50, 200)
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        for (i in 0..28) {
            Box(
                modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth()
                    .background(Color.LightGray)
            )
            SimpleSwipe(
                mode = mode[i % 2],
                endWidth = endWidth[(i / 2) % 2],
                touchUp = touchUp[(i / 4) % 7]
            )
        }
    }
}


@Composable
fun SimpleSwipe(mode: String, endWidth: Int, touchUp: String) {
    var title = "($mode $endWidth $touchUp)"
    var scene =
        """
       {
         ConstraintSets: {
           start: {
           title: {
               width: 'wrap', height: 50,
                start: ['parent', 'start', 0],
                bottom: ['parent', 'bottom', 0],
                top: ['parent', 'top', 0],
                end: ['parent', 'end', 0],
                 custom: { 
                        mValue: 0.0,
                        back: '#FFFFFF'
                      },
           },
             box: {
               width: 50, height: 50,
               bottom: ['parent', 'bottom', 0],
               start: ['parent', 'start', 70],
               top: ['parent', 'top', 0],
               rotationZ: 0,
               custom: { 
                         boxColor: '#FF00FFFF'
                      },
             }
           },
           end: {
               title: {
                 width: 'wrap', height: 50,
                 start: ['parent', 'start', 0],
                bottom: ['parent', 'bottom', 0],
                top: ['parent', 'top', 0],
                end: ['parent', 'end', 0],
                custom: { 
                        mValue: 100.0,
                         back: '#FF88FF'
                      },
           },
             box: {
                width: $endWidth, height: 50,
                bottom: ['parent', 'bottom', 0],
                top: ['parent', 'top', 0],
                end: ['parent', 'end', 70],
                rotationZ: 360,

                 custom: { 
                         boxColor: '#FF00FF00'
                      },
                
             }
           }
         },
         Transitions: {
           default: {
              from: 'start',
              to: 'end',
              onSwipe: {
                anchor: 'box',
                direction: 'end',
                side: 'start',
                touchUp: '$touchUp',
                mode: '$mode'
              }
           }
         }
       }
        """.trimIndent()

    MotionLayout(
        modifier = Modifier
            .height(70.dp)
            .fillMaxWidth()
            .background(Color.White),
        motionScene = MotionScene(content = scene)
    ) {
        var prop = motionProperties("title")
        val progress = prop.value.float("mValue")
        val col = prop.value.color("back")

        Text(
            text = "$title  $progress", modifier = Modifier
                .layoutId("title")
                .background(col), textAlign = TextAlign.Center
        )
        Box(
            modifier = Modifier
                .background(motionProperties("box").value.color("boxColor"))
                .layoutId("box")
        )
    }
}


