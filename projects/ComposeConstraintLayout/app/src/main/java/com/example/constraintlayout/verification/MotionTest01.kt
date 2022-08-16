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

package com.example.constraintlayout.verification

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionLayoutDebugFlags
import androidx.constraintlayout.compose.MotionScene
import androidx.constraintlayout.compose.Transition
import java.util.*

@Preview(group = "test1")
@Composable
public fun MTest01() {
    var animateToEnd by remember { mutableStateOf(false) }

    val progress = remember { Animatable(0f) }

    LaunchedEffect(animateToEnd) {
        progress.animateTo(
            if (animateToEnd) 1f else 0f,
            animationSpec = tween(3000)
        )
    }

    Column(modifier = Modifier.background(Color.White)) {
        val scene2 =
            MotionScene {
                // Create ID references at the top level
                val id1 = createRefFor("id1")
                val id2 = createRefFor("id2")

                // Initial start, end states
                defaultTransition(
                    from = constraintSet {
                        constrain(id1) {
                            width = Dimension.value(40.dp)
                            height = Dimension.value(40.dp)
                            start.linkTo(parent.start, 16.dp)
                            bottom.linkTo(parent.bottom, 16.dp)
                            customColor("background", Color.Red)
                        }
                        constrain(id2) {
                            centerTo(id1)
                        }
                    },
                    to = constraintSet {
                        constrain(id1) {
                            width = Dimension.value(40.dp)
                            height = Dimension.value(40.dp)
                            end.linkTo(parent.end, 16.dp)
                            top.linkTo(parent.top, 16.dp)
                            customColor("background", Color.Blue)
                        }
                    },
                ) {
                    keyAttributes(id2) {
                        // KeyAttribute Content
                    }
                }

                // Other ConstraintSets and Transitions
                val cSetA = constraintSet("A") {
                    // ConstraintSet content
                }
                val cSetB = constraintSet("B") {
                    // ConstraintSet content
                }

                transition(name = "aToB", from = cSetA, to = cSetB) {
                    // Transition content
                }

                // Could also be declared inline, with no explicit names:
                transition(
                    from = constraintSet {
                        // ConstraintSet content
                    },
                    to = constraintSet {
                        // ConstraintSet content
                    }
                ) {
                    // Transition content
                }
            }

        val scene1 = MotionScene(
            """
            {
                Header: { exportAs: 'mtest01'},
                
                ConstraintSets: {
                  start: {
                    id1: {
                      width: 40, height: 40,
                      start:  ['parent', 'start' , 16],
                      bottom: ['parent', 'bottom', 16]
                    }
                  },
                  
                  end: {
                    id1: {
                      width: 40, height: 40,
                      end: ['parent', 'end', 16],
                      top: ['parent', 'top', 16]
                    }
                  }
                },
                
                Transitions: {
                  default: {
                    from: 'start',   to: 'end',
                  }
                }
            }
            """
        )

        MotionLayout(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            motionScene = scene1,
            progress = progress.value,
            debug = EnumSet.of(MotionLayoutDebugFlags.SHOW_ALL)
        ) {
            Box(
                modifier = Modifier
                    .layoutId("id1")
                    .background(Color.Red)
            )
        }

        Button(
            onClick = { animateToEnd = !animateToEnd },
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp)
        ) {
            Text(text = "Run")
        }
    }
}

@Preview(group = "test2")
@Composable
public fun MTest02() {
    var animateToEnd by remember { mutableStateOf(true) }

    val progress = remember { Animatable(1f) }

    LaunchedEffect(animateToEnd) {
        progress.animateTo(
            if (animateToEnd) 1f else 0f,
            animationSpec = tween(3000)
        )
    }

    Column(modifier = Modifier.background(Color.White)) {

        val scene1 = MotionScene(
            """
            {
                Header: { exportAs: 'mtest02'},
                
                ConstraintSets: {
                  start: {
                    id1: {
                      width: 40, height: 40,
                      start:  ['parent', 'start' , 16],
                      bottom: ['parent', 'bottom', 16]
                    }
                  },
                  
                  end: {
                    id1: {
                      width: 40, height: 40,
                      end: ['parent', 'end', 16],
                      top: ['parent', 'top', 16]
                    }
                  }
                },
                
                Transitions: {
                  default: {
                    from: 'start',   to: 'end',
                    pathMotionArc: 'startHorizontal',
                  }
                }
            }
            """
        )

        MotionLayout(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            motionScene = scene1,
            progress = progress.value,
            debug = EnumSet.of(MotionLayoutDebugFlags.SHOW_ALL)
        ) {
            Box(
                modifier = Modifier
                    .layoutId("id1")
                    .background(Color.Red)
            )
        }

        Button(
            onClick = { animateToEnd = !animateToEnd },
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp)
        ) {
            Text(text = "Run")
        }
    }
}


@Preview(group = "test3")
@Composable
public fun MTest03() {
    var animateToEnd by remember { mutableStateOf(false) }

    val progress = remember { Animatable(0.5f) }

    LaunchedEffect(animateToEnd) {
        progress.animateTo(
            if (animateToEnd) 1f else 0f,
            animationSpec = tween(3000)
        )
    }

    Column(modifier = Modifier.background(Color.White)) {

        val scene1 = MotionScene(
            """
            {
                Header: { exportAs: 'mtest03'},
                
                ConstraintSets: {
                  start: {
                    id1: {
                      width: 40, height: 40,
                      start:  ['parent', 'start' , 16],
                      bottom: ['parent', 'bottom', 16]
                    }
                  },
                  
                  end: {
                    id1: {
                      width: 40, height: 40,
                      end: ['parent', 'end', 16],
                      top: ['parent', 'top', 16]
                    }
                  }
                },
                
                Transitions: {
                  default: {
                    from: 'start',   to: 'end',
                    pathMotionArc: 'startVertical',
                  }
                }
            }
            """
        )

        MotionLayout(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            motionScene = scene1,
            progress = 0.5f,//progress.value,
            debug = EnumSet.of(MotionLayoutDebugFlags.SHOW_ALL)
        ) {
            Box(
                modifier = Modifier
                    .layoutId("id1")
                    .background(Color.Red)
            )
        }

        Button(
            onClick = { animateToEnd = !animateToEnd },
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp)
        ) {
            Text(text = "Run")
        }
    }
}


@Preview(group = "test4")
@Composable
public fun MTest04() {
    var animateToEnd by remember { mutableStateOf(false) }

    val progress = remember { Animatable(0.0f) }

    LaunchedEffect(animateToEnd) {
        progress.animateTo(
            if (animateToEnd) 1f else 0f,
            animationSpec = tween(3000)
        )
    }

    Column(modifier = Modifier.background(Color.White)) {

        val scene1 = MotionScene(
            """
            {
                Header: { exportAs: 'mtest04'},
                
                ConstraintSets: {
                  start: {
                    id1: {
                      width: 40, height: 40,
                      start:  ['parent', 'start' , 16],
                      bottom: ['parent', 'bottom', 16]
                    }
                  },
                  
                  end: {
                    id1: {
                      width: 40, height: 40,
                      end: ['parent', 'end', 16],
                      top: ['parent', 'top', 16]
                    }
                  }
                },
                
                Transitions: {
                  default: {
                    from: 'start',   to: 'end',
                    pathMotionArc: 'startVertical',
                    KeyFrames: {
                     KeyPositions: [
                     {
                      target: ['id1'],
                      frames: [25, 50, 75],
                      percentX: [0.2, 0.5, 0.7],
                      percentY: [0.2, 0.5, 0.7]
                     }
                     ]
                  },
                  }
                }
            }
            """
        )

        MotionLayout(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            motionScene = scene1,
            progress = progress.value,
            debug = EnumSet.of(MotionLayoutDebugFlags.SHOW_ALL)
        ) {
            Box(
                modifier = Modifier
                    .layoutId("id1")
                    .background(Color.Red)
            )
        }

        Button(
            onClick = { animateToEnd = !animateToEnd },
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp)
        ) {
            Text(text = "Run")
        }
    }
}


@Preview(group = "test5")
@Composable
public fun MTest05() {
    var animateToEnd by remember { mutableStateOf(false) }

    val progress = remember { Animatable(0.0f) }

    LaunchedEffect(animateToEnd) {
        progress.animateTo(
            if (animateToEnd) 1f else 0f,
            animationSpec = tween(3000)
        )
    }

    Column(modifier = Modifier.background(Color.White)) {

        val scene1 = MotionScene(
            """
            {
                Header: { exportAs: 'mtest05'},
                
                ConstraintSets: {
                  start: {
                    id1: {
                      width: 40, height: 40,
                      start:  ['parent', 'start' , 16],
                      bottom: ['parent', 'bottom', 16]
                    }
                  },
                  
                  end: {
                    id1: {
                      width: 40, height: 40,
                      end: ['parent', 'end', 16],
                      top: ['parent', 'top', 16]
                    }
                  }
                },
                
                Transitions: {
                  default: {
                    from: 'start',   to: 'end',
                    pathMotionArc: 'startVertical',
                    KeyFrames: {
                     KeyPositions: [
                     {
                      target: ['id1'],
                      type: 'deltaRelative',
                      frames: [25, 50, 75],
                      percentX: [0.2, 0.5, 0.7],
                      percentY: [0.2, 0.5, 0.7]
                     }
                     ]
                  },
                  }
                }
            }
            """
        )

        MotionLayout(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            motionScene = scene1,
            progress = progress.value,
            debug = EnumSet.of(MotionLayoutDebugFlags.SHOW_ALL)
        ) {
            Box(
                modifier = Modifier
                    .layoutId("id1")
                    .background(Color.Red)
            )
        }

        Button(
            onClick = { animateToEnd = !animateToEnd },
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp)
        ) {
            Text(text = "Run")
        }
    }
}


@Preview(group = "test3")
@Composable
public fun MTest06() {
    var animateToEnd by remember { mutableStateOf(false) }

    val progress = remember { Animatable(0.0f) }

    LaunchedEffect(animateToEnd) {
        progress.animateTo(
            if (animateToEnd) 1f else 0f,
            animationSpec = tween(3000)
        )
    }

    Column(modifier = Modifier.background(Color.White)) {

        val scene1 = MotionScene(
            """
            {
                Header: { exportAs: 'mtest06'},
                
                ConstraintSets: {
                  start: {
                    id1: {
                      width: 40, height: 40,
                      start:  ['parent', 'start' , 16],
                      bottom: ['parent', 'bottom', 16]
                    }
                  },
                  
                  end: {
                    id1: {
                      width: 40, height: 40,
                      end: ['parent', 'end', 16],
                      top: ['parent', 'top', 16]
                    }
                  }
                },
                
                Transitions: {
                  default: {
                    from: 'start',   to: 'end',
                    pathMotionArc: 'startVertical',
                    KeyFrames: {
                     KeyPositions: [
                     {
                      target: ['id1'],
                      frames: [25, 50, 75],
                      percentX: [0.4, 0.8, 0.1],
                      percentY: [0.4, 0.8, 0.3]
                     }
                     ]
                  }
                  }
                }
            }
            """
        )

        MotionLayout(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            motionScene = scene1,
            progress = progress.value,
            debug = EnumSet.of(MotionLayoutDebugFlags.SHOW_ALL)
        ) {
            Box(
                modifier = Modifier
                    .layoutId("id1")
                    .background(Color.Red)
            )
        }

        Button(
            onClick = { animateToEnd = !animateToEnd },
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp)
        ) {
            Text(text = "Run")
        }
    }
}
