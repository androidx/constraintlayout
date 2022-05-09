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

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import androidx.constraintlayout.compose.layoutId

@Preview
@Composable
fun OnSwipeExperiment() {
    var progress by remember {
        mutableStateOf(0.0f)
    }
    var mode by remember {
        mutableStateOf("spring")
    }
    val motionSceneContent = remember(mode) {
        // language=json5
        """
       {
         ConstraintSets: {
           start: {
             box: {
               width: 50, height: 50,
               bottom: ['parent', 'bottom', 10],
               start: ['parent', 'start', 10]
             }
           },
           end: {
             Extends: 'start',
             box: {
               clear: ['constraints'],
                  width: 100, height: 400,
               top: ['parent', 'top', 10],
               end: ['parent', 'end', 10],
             }
           }
         },
         Transitions: {
           default: {
              from: 'start',
              to: 'end',
        KeyFrames: {
                      KeyPositions: [
                        {
                          target: ['box'],
                          frames: [25, 50, 75],
                          percentX: [0.25, 0.5, 0.75],
                          percentY: [0.25, 0.5, 0.75]
                        }
                      ],
                      },
              onSwipe: {
                anchor: 'box',
                direction: 'end',
                side: 'start',
                mode: '$mode'
              }
           }
         }
       }
        """.trimIndent()
    }
    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = {
                // OnSwipe does not update our progress, need a work around to reset
                progress = 0.1f
                progress = 0f
            }) {
                Text(text = "Reset")
            }
            Button(onClick = {
                mode = when (mode) {
                    "spring" -> "linear"
                    else -> "spring"
                }
            }) {
                Text(text = "Change Mode")
            }
            Text(text = "Current: $mode")
        }
        MotionLayout(
            modifier = Modifier.fillMaxSize(),
            motionScene = MotionScene(content = motionSceneContent),
            progress = progress
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
                mode: 'linear'
              }
           }
         }
       }
        """.trimIndent()

    Column {
        MotionLayout(
            modifier = Modifier.fillMaxSize().background(Color.White),
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

