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

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.*
import androidx.constraintlayout.motion.widget.MotionLayout


@Preview(group = "guidelines")
@Composable
public fun ResizeExample2() {
    var animateToEnd by remember { mutableStateOf(false) }

    val progress by animateFloatAsState(
        targetValue = if (animateToEnd) 1f else 0f,
        animationSpec = tween(1000)
    )

    ConstraintLayout(
        ConstraintSet("""
            {
              Header: {
                exportAs: 'ResizeContent'
              },
              content: {
                width: 'spread',
                height: 'spread',
                centerHorizontally: 'parent',
                centerVertically: 'parent'
              } 
            }
"""), modifier = Modifier
            .fillMaxSize()
        //    .background(Color.White)
    ) {
//        BoxWithConstraints(modifier = Modifier.layoutId("content")) {
//            if (maxWidth < 300.dp) {
//                animateToEnd = true
//            } else {
//                animateToEnd = false
//            }
        ConstraintLayout(
            modifier = Modifier
                .layoutId("content")
                .fillMaxSize()
                .background(Color.Black),
            constraintSet = ConstraintSet (
                """{
            box1: {
              start: ['parent', 'start', 16],
              centerVertically: 'parent'
            },
            box2: {
              end: ['parent', 'end', 16],
              centerVertically: 'parent'
            }
          }
"""
            )
        ) {
            Box(
                modifier = Modifier
                    .layoutId("box1")
                    .height(40.dp)
                    .width(100.dp)
                    .background(Color.Gray)
            )

            Box(
                modifier = Modifier
                    .layoutId("box2")
                    .height(40.dp)
                    .width(100.dp)
                    .background(Color.Red)
            )

        }
//        }
    }
}

@Preview(group = "guidelines")
@Composable
public fun ResizeExample3() {
    var animateToEnd by remember { mutableStateOf(false) }

    val progress by animateFloatAsState(
        targetValue = if (animateToEnd) 1f else 0f,
        animationSpec = tween(1000)
    )

    ConstraintLayout(
        ConstraintSet("""
            {
              Header: {
                exportAs: 'ResizeContent'
              },
              content: {
                width: 'spread',
                height: 'spread',
                centerHorizontally: 'parent',
                centerVertically: 'parent'
              } 
            }
"""), modifier = Modifier
        .fillMaxSize()
    //    .background(Color.White)
    ) {
//        BoxWithConstraints(modifier = Modifier.layoutId("content")) {
//            if (maxWidth < 300.dp) {
//                animateToEnd = true
//            } else {
//                animateToEnd = false
//            }
            MotionLayout(
                modifier = Modifier
                    .layoutId("content")
                    .fillMaxSize()
                    .background(Color.Black),
                start = ConstraintSet(
                    """{
                    Header: {
                      exportAs: 'ResizeStart'
                    },
          box1: {
            start: ['parent', 'start', 16],
            centerVertically: 'parent'
          },
          box2: {
            end: ['parent', 'end', 16],
            centerVertically: 'parent'
          }
}
"""
                ),
                end = ConstraintSet(
                    """{
                    Debug: {
                      name: 'ResizeEnd'
                    },
        end: {
          box1: {
            start: ['parent', 'start', 16],
            centerVertically: 'parent'
          },
          box2: {
            start: ['parent', 'start', 16],
            top: ['box1', 'bottom', 16]
          }
        }
}
"""
                ),
                progress = progress
            ) {
                Box(
                    modifier = Modifier
                        .layoutId("box1")
                        .height(40.dp)
                        .width(100.dp)
                        .background(Color.Gray)
                )

                Box(
                    modifier = Modifier
                        .layoutId("box2")
                        .height(40.dp)
                        .width(100.dp)
                        .background(Color.Red)
                )

            }
//        }
    }
}

@Preview(group = "guidelines")
@Composable
public fun ResizeExample1() {
    var animateToEnd by remember { mutableStateOf(false) }

    val progress by animateFloatAsState(
        targetValue = if (animateToEnd) 1f else 0f,
        animationSpec = tween(1000)
    )

    ConstraintLayout(
        ConstraintSet("""
            {
              Header: {
                exportAs: 'ResizeContent'
              },
              content: {
                width: 'spread',
                height: 'spread',
                centerHorizontally: 'parent',
                centerVertically: 'parent'
              } 
            }
"""), modifier = Modifier
            .fillMaxSize()
        //    .background(Color.White)
    ) {
        BoxWithConstraints(modifier = Modifier.layoutId("content")) {
            if (maxWidth < 240.dp) {
                animateToEnd = true
            } else {
                animateToEnd = false
            }
        MotionLayout(
            modifier = Modifier
                .layoutId("content")
                .fillMaxSize()
                .background(Color.Black),
            motionScene = MotionScene(
                """{
                    Header: {
                      exportAs: 'Resize'
                    },
        ConstraintSets: {
        start: {
          box1: {
            start: ['parent', 'start', 16],
            centerVertically: 'parent'
          },
          box2: {
            end: ['parent', 'end', 16],
            centerVertically: 'parent'
          }
        },
        end: {
          box1: {
            start: ['parent', 'start', 16],
            centerVertically: 'parent'
          },
          box2: {
            start: ['parent', 'start', 16],
            top: ['box1', 'bottom', 16]
          }
        }
      }
}
"""
            ),
            progress = progress
        ) {
            Box(
                modifier = Modifier
                    .layoutId("box1")
                    .height(40.dp)
                    .width(100.dp)
                    .background(Color.Gray)
            )

            Box(
                modifier = Modifier
                    .layoutId("box2")
                    .height(40.dp)
                    .width(100.dp)
                    .background(Color.Red)
            )

        }
        }
    }
}