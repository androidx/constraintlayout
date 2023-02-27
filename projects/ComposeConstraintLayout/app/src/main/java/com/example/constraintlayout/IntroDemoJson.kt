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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.*

@Preview(group = "intro")
@Composable
fun JsonIntro() {
    // create a constraintSet variable to be passed to ConstraintLayout
    val constraintSet = ConstraintSet("""
        {
          title: {
            top: ['parent', 'top'],
            bottom: ['parent', 'bottom'],
            start: ['parent', 'start'],
            end: ['btn', 'start']
          },
          btn: {
            top: ['parent', 'top'],
            bottom: ['parent', 'bottom'],
            start: ['title', 'end'],
            end: ['parent', 'end'],
          }
        }
    """.trimIndent())
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
fun MotionJsonIntro() {
    // Create a MotionScene to be passed to MotionLayout
    val motionScene = MotionScene("""
        {
          ConstraintSets: {
            startLayout: {
              title: {
                top: ['parent', 'top'],
                start: ['parent', 'start']
              }
            },
            endLayout: {
              title: {
                  bottom: ['parent', 'bottom'],
                  end: ['parent', 'end']
              }
            }
          },
            Transitions: {
              default: {
                from: 'startLayout',
                to: 'endLayout'
              }
            }
        }
    """.trimIndent())
    // Pass the MotionScence variable to MotionLayout and create Text
    MotionLayout(motionScene,
        progress = 0f, // progress when the transition starts (can be 0f - 1f)
        modifier = Modifier.fillMaxSize()) {
        Text(text = "Hello World", modifier = Modifier.layoutId("title"))
    }
}

@Preview(group = "kf")
@Composable
fun KeyFramesJsonIntro() {
    // Create a MotionScene to be passed to MotionLayout
    val motionScene = MotionScene("""
        {
          ConstraintSets: {
            startLayout: {
              title: {
                top: ['parent', 'top'],
                start: ['parent', 'start']
              }
            },
            endLayout: {
              title: {
                  bottom: ['parent', 'bottom'],
                  end: ['parent', 'end']
              }
            }
          },
            Transitions: {
              default: {
                // specify the starting layout
                from: 'startLayout',
                // specify the ending layout
                to: 'endLayout',
                // specify the Keyframes
                KeyFrames: {
                  // specify the KeyPositions
                  // change x position between 20% - 80% of the progress
                  KeyPositions: [
                    {
                        target: ['title'],
                        frames: [20, 80],
                        percentX: [0.6, 0.3],
                    }
                  ],
                  // specify the KeyAttributes:
                  // change alpha value between 20% - 80% of the progress
                  KeyAttributes: [
                    {
                        target: ['title'],
                        frames: [20, 80],
                        alpha: [0.2, 0.8],
                    }
                  ]
                }
              }
           }
        }
    """.trimIndent())
    // Pass the MotionScence variable to MotionLayout and create Text
    MotionLayout(motionScene,
        progress = 0.7f, // progress when the transition starts (can be 0f - 1f)
        modifier = Modifier.fillMaxSize()) {
        Text(text = "Hello World", modifier = Modifier.layoutId("title"))
    }
}

@Preview
@Composable
fun OnSwipeSample23() {

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
            Text(
                text = "on Swipe example \n" +
                    "  onSwipe: {\n" +
                    "                anchor: 'box',\n" +
                    "                direction: 'end',\n" +
                    "                side: 'start',\n" +
                    "                mode: 'spring'\n" +
                    "              }"
            )
            Box(
                modifier = Modifier
                    .background(Color.Green)
                    .layoutId("box")
            )
        }
    }
}