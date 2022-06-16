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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionLayoutDebugFlags
import androidx.constraintlayout.compose.MotionScene
import androidx.constraintlayout.compose.layoutId
import java.util.*

@Preview
@Composable
fun MotionArc() {

    var scene =
        """
       {
         ConstraintSets: {
           start: {
             box1: {
               width: 50, height: 50,
               bottom: ['parent', 'bottom', 10],
               start: ['parent', 'start', 10],
             },
               title: {
                   top: ['parent', 'top', 10],
               start: ['parent', 'start', 10],
                end: ['parent', 'end', 10],
             },
               box2: {
               width: 50, height: 50,
               bottom: ['parent', 'bottom', 10],
               start: ['parent', 'start', 10],
               motion: {
                  pathArc : 'startHorizontal'
               }
             }
           },
           end: {
               box1: {
                       width: 50, height: 50,
                       top: ['parent', 'top', 10],
                       end: ['parent', 'end', 10],
                    },
              title: {
                 top: ['parent', 'top', 10],
               start: ['parent', 'start', 10],
                end: ['parent', 'end', 10],
             },
             box2: {
                width: 50, height: 50,
                top: ['parent', 'top', 40],
                end: ['parent', 'end', 10],
             }
           }
         },
         Transitions: {
           default: {
              from: 'start',
              to: 'end',
               pathMotionArc : 'none',
              onSwipe: {
                anchor: 'box1',
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
                .background(Color.LightGray),
            motionScene = MotionScene(content = scene),
            debug= EnumSet.of(MotionLayoutDebugFlags.SHOW_ALL),
        ) {
            Text(text = "pathArc motion attributes",
                modifier = Modifier.layoutId("title"))
            Box(
                modifier = Modifier
                    .background(Color.Red)
                    .layoutId("box1")
            )
            Box(
                modifier = Modifier
                    .background(Color.Green)
                    .layoutId("box2")
            )
        }
    }
}


@Preview
@Composable
fun MotionEasing() {

    var scene =
        """
       {
         ConstraintSets: {
           start: {
             box1: {
               width: 50, height: 50,
               bottom: ['parent', 'bottom', 10],
               start: ['parent', 'start', 10],
             },
               title: {
                   top: ['parent', 'top', 10],
               start: ['parent', 'start', 10],
                end: ['parent', 'end', 10],
             },
               box2: {
               width: 50, height: 50,
               bottom: ['parent', 'bottom', 10],
               start: ['parent', 'start', 10],
               motion: {
                  pathArc : 'startHorizontal',
                  easing: 'overshoot'
               }
             }
           },
           end: {
               box1: {
                       width: 50, height: 50,
                       top: ['parent', 'top', 60],
                       end: ['parent', 'end', 60],
                    },
              title: {
                 top: ['parent', 'top', 10],
               start: ['parent', 'start', 10],
                end: ['parent', 'end', 10],
             },
             box2: {
                width: 50, height: 50,
                top: ['parent', 'top', 60],
                end: ['parent', 'end', 10],
             }
           }
         },
         Transitions: {
           default: {
              from: 'start',
              to: 'end',
               pathMotionArc : 'none',
              onSwipe: {
                anchor: 'box1',
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
                .background(Color.LightGray),
            motionScene = MotionScene(content = scene),
            debug= EnumSet.of(MotionLayoutDebugFlags.SHOW_ALL),
        ) {
            Text(text = " easing motion attributes ",
                modifier = Modifier.layoutId("title"))
            Box(
                modifier = Modifier
                    .background(Color.Red)
                    .layoutId("box1")
            )
            Box(
                modifier = Modifier
                    .background(Color.Green)
                    .layoutId("box2")
            )
        }
    }
}


@Preview
@Composable
fun MotionQuantize1() {

    var scene =
        """
       {
         ConstraintSets: {
           start: {
             box1: {
               width: 50, height: 50,
               bottom: ['parent', 'bottom', 10],
               start: ['parent', 'start', 10],
             },
               title: {
                   top: ['parent', 'top', 10],
               start: ['parent', 'start', 10],
                end: ['parent', 'end', 10],
             },
               box2: {
               width: 50, height: 50,
               bottom: ['parent', 'bottom', 10],
               start: ['parent', 'start', 10],
               motion: {
                  pathArc : 'startHorizontal',
                  quantize: 12
               }
             }
           },
           end: {
               box1: {
                       width: 50, height: 50,
                       top: ['parent', 'top', 60],
                       end: ['parent', 'end', 60],
                    },
              title: {
                 top: ['parent', 'top', 10],
               start: ['parent', 'start', 10],
                end: ['parent', 'end', 10],
             },
             box2: {
                width: 50, height: 50,
                top: ['parent', 'top', 60],
                end: ['parent', 'end', 10],
             }
           }
         },
         Transitions: {
           default: {
              from: 'start',
              to: 'end',
               pathMotionArc : 'none',
              onSwipe: {
                anchor: 'box1',
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
                .background(Color.LightGray),
            motionScene = MotionScene(content = scene),
            debug= EnumSet.of(MotionLayoutDebugFlags.SHOW_ALL),
        ) {
            Text(text = " Quantize Constraint motion ",
                modifier = Modifier
                    .layoutId("title")
                    .background(Color.Gray))
            Box(
                modifier = Modifier
                    .background(Color.Red)
                    .layoutId("box1")
            )
            Box(
                modifier = Modifier
                    .background(Color.Green)
                    .layoutId("box2")
            )
        }
    }
}

@Preview
@Composable
fun MotionQuantize2() {
    var currentState by remember { mutableStateOf<String?>("start") }
    var scene =
        """
       {
         ConstraintSets: {
           start: {
             box1: {
               width: 50, height: 50,
               bottom: ['parent', 'bottom', 10],
               start: ['parent', 'start', 10],
             },
               title: {
                   height: 50,
                   top: ['parent', 'top', 10],
               start: ['parent', 'start', 10],
                end: ['parent', 'end', 10],
             },
               box2: {
               width: 50, height: 50,
               bottom: ['parent', 'bottom', 10],
               start: ['parent', 'start', 10],
               motion: {
                  pathArc : 'startHorizontal',
                  quantize: [6, 'overshoot', 32]
               }
             }
           },
           end: {
               box1: {
                       width: 50, height: 50,
                       top: ['parent', 'top', 60],
                       end: ['parent', 'end', 60],
                    },
              title: {
                 height: 50,
                 top: ['parent', 'top', 10],
               start: ['parent', 'start', 10],
                end: ['parent', 'end', 10],
             },
             box2: {
                width: 50, height: 50,
                top: ['parent', 'top', 60],
                end: ['parent', 'end', 10],
             }
           }
         },
         Transitions: {
           default: {
              from: 'start',
              to: 'end',
              duration: 2000,
               pathMotionArc : 'none',
              onSwipe: {
                anchor: 'box1',
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
            animationSpec = tween<Float>(2000),
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray),
            constraintSetName = currentState,
            motionScene = MotionScene(content = scene),
            debug= EnumSet.of(MotionLayoutDebugFlags.SHOW_ALL),
        ) {
            Button( onClick = { currentState = if (currentState == "start") "end" else "start"  },

                modifier = Modifier
                    .layoutId("title")
                    .background(Color.Gray)) {
                Text(text =" Complex Quantize Constraint motion ", )
            }
            Box(
                modifier = Modifier
                    .background(Color.Red)
                    .layoutId("box1")
            )
            Box(
                modifier = Modifier
                    .background(Color.Green)
                    .layoutId("box2")
            )
        }
    }
}


@Preview
@Composable
fun MotionStagger1() {
    var currentState by remember { mutableStateOf<String?>("start") }
    var scene =
        """
       {
         ConstraintSets: {
           start: {
             box1: {
               width: 50, height: 50,
               bottom: ['parent', 'bottom', 10],
               start: ['parent', 'start', 10],
             },
               title: {
                   height: 50,
                   top: ['parent', 'top', 10],
               start: ['parent', 'start', 10],
                end: ['parent', 'end', 10],
             },
               box2: {
               width: 50, height: 50,
               bottom: ['parent', 'bottom', 10],
               start: ['parent', 'start', 10],
               motion: {
                  pathArc : 'startHorizontal',
                  stagger: 1,
               }
             },
             box3: {
               width: 50, height: 50,
               bottom: ['parent', 'bottom', 10],
               start: ['parent', 'start', 10],
               motion: {
                  pathArc : 'startHorizontal',
                  stagger: 1,
         
               }
             },
             box4: {
               width: 50, height: 50,
               bottom: ['parent', 'bottom', 10],
               start: ['parent', 'start', 10],
               motion: {
                  pathArc : 'startHorizontal',
                  stagger: 2,
         
               }
             },
             
             box5: {
               width: 50, height: 50,
               bottom: ['parent', 'bottom', 10],
               start: ['parent', 'start', 10],
               motion: {
                  pathArc : 'startHorizontal',
                  stagger: 2,
         
               }
             },
              box6: {
               width: 50, height: 50,
               bottom: ['parent', 'bottom', 10],
               start: ['parent', 'start', 10],
               motion: {
                  pathArc : 'startHorizontal',
                  stagger: 3,
         
               }
             }
           },
           end: {
               box1: {
                       width: 50, height: 50,
                       top: ['parent', 'top', 60],
                       end: ['parent', 'end', 60],
                    },
              title: {
                 height: 50,
                 top: ['parent', 'top', 10],
               start: ['parent', 'start', 10],
                end: ['parent', 'end', 10],
             },
             box2: {
                width: 50, height: 50,
                top: ['parent', 'top', 60],
                end: ['parent', 'end', 10],
             },
             box3: {
                width: 50, height: 50,
                top: ['parent', 'top', 110],
                end: ['parent', 'end', 10],
             },
             box4: {
                width: 50, height: 50,
                top: ['parent', 'top', 160],
                end: ['parent', 'end', 10],
             },
             box5: {
                width: 50, height: 50,
                top: ['parent', 'top', 210],
                end: ['parent', 'end', 10],
             },
             box6: {
                width: 50, height: 50,
                top: ['parent', 'top', 260],
                end: ['parent', 'end', 10],
             }
           }
         },
         Transitions: {
           default: {
              from: 'start',
              to: 'end',
              duration: 2000,
               pathMotionArc : 'none',
               staggered: 0.4,
              onSwipe: {
                anchor: 'box1',
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
            animationSpec = tween<Float>(2000),
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray),
            constraintSetName = currentState,
            motionScene = MotionScene(content = scene),
            debug= EnumSet.of(MotionLayoutDebugFlags.SHOW_ALL),
        ) {
            Button( onClick = { currentState = if (currentState == "start") "end" else "start"  },

                modifier = Modifier
                    .layoutId("title")
                    .background(Color.Gray)) {
                Text(text =" Custom Stagger (1, 2, 2) ", )
            }
            Box(
                modifier = Modifier
                    .background(Color.Red)
                    .layoutId("box1")
            )
            for (i in 2..6) {
                val c = Color(0f, 0.4f+i/10f, i/10f)
                Box(
                    modifier = Modifier
                        .background(c)
                        .layoutId("box$i")
                )
            }

        }
    }
}

@Preview
@Composable
fun MotionStagger2() {
    var currentState by remember { mutableStateOf<String?>("start") }
    var scene =
        """
       {
         ConstraintSets: {
           start: {
             box1: {
               width: 50, height: 50,
               bottom: ['parent', 'bottom', 10],
               start: ['parent', 'start', 10],
             },
               title: {
                   height: 50,
                   top: ['parent', 'top', 10],
               start: ['parent', 'start', 10],
                end: ['parent', 'end', 10],
             },
               box2: {
               width: 50, height: 50,
               bottom: ['parent', 'bottom', 10],
               start: ['parent', 'start', 10],
               motion: {
                  pathArc : 'startHorizontal',
               }
             },
             box3: {
               width: 50, height: 50,
               bottom: ['parent', 'bottom', 60],
               start: ['parent', 'start', 10],
               motion: {
                  pathArc : 'startHorizontal',
               }
             },
             box4: {
               width: 50, height: 50,
               bottom: ['parent', 'bottom', 110],
               start: ['parent', 'start', 10],
               motion: {
                  pathArc : 'startHorizontal',         
               }
             },
             
             box5: {
               width: 50, height: 50,
               bottom: ['parent', 'bottom',  160],
               start: ['parent', 'start', 10],
               motion: {
                  pathArc : 'startHorizontal',         
               }
             },
              box6: {
               width: 50, height: 50,
               bottom: ['parent', 'bottom', 210],
               start: ['parent', 'start', 10],
               motion: {
                  pathArc : 'startHorizontal',         
               }
             }
           },
           end: {
               box1: {
                       width: 50, height: 50,
                       top: ['parent', 'top', 60],
                       end: ['parent', 'end', 60],
                    },
              title: {
                 height: 50,
                 top: ['parent', 'top', 10],
               start: ['parent', 'start', 10],
                end: ['parent', 'end', 10],
             },
             box2: {
                width: 50, height: 50,
                top: ['parent', 'top', 60],
                end: ['parent', 'end', 10],
             },
             box3: {
                width: 50, height: 50,
                top: ['parent', 'top', 110],
                end: ['parent', 'end', 10],
             },
             box4: {
                width: 50, height: 50,
                top: ['parent', 'top', 160],
                end: ['parent', 'end', 10],
             },
             box5: {
                width: 50, height: 50,
                top: ['parent', 'top', 210],
                end: ['parent', 'end', 10],
             },
             box6: {
                width: 50, height: 50,
                top: ['parent', 'top', 260],
                end: ['parent', 'end', 10],
             }
           }
         },
         Transitions: {
           default: {
              from: 'start',
              to: 'end',
              duration: 2000,
               pathMotionArc : 'none',
               staggered: -0.4,
              onSwipe: {
                anchor: 'box1',
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
            animationSpec = tween<Float>(2000),
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray),
            constraintSetName = currentState,
            motionScene = MotionScene(content = scene),
            debug= EnumSet.of(MotionLayoutDebugFlags.SHOW_ALL),
        ) {
            Button( onClick = { currentState = if (currentState == "start") "end" else "start"  },

                modifier = Modifier
                    .layoutId("title")
                    .background(Color.Gray)) {
                Text(text =" Default Stagger with arch mode ", )
            }
            Box(
                modifier = Modifier
                    .background(Color.Red)
                    .layoutId("box1")
            )
            for (i in 2..6) {
                val c = Color(0f, 0.4f+i/10f, i/10f)
                Box(
                    modifier = Modifier
                        .background(c)
                        .layoutId("box$i")
                )
            }

        }
    }
}



@Preview
@Composable
fun MotionOrbit1() {

    var scene =
        """
       {
         ConstraintSets: {
           start: {
             box1: {
               width: 50, height: 50,
               bottom: ['parent', 'bottom', 10],
               start: ['parent', 'start', 80],
             },
               title: {
                   top: ['parent', 'top', 10],
               start: ['parent', 'start', 10],
                end: ['parent', 'end', 10],
             },
               box2: {
               width: 50, height: 50,
               bottom: ['parent', 'bottom', 10],
               start: ['parent', 'start', 10],
               motion: {
                  relativeTo: 'box1'
               }
             }
           },
           end: {
               box1: {
                       width: 50, height: 50,
                       top: ['parent', 'top', 60],
                       end: ['parent', 'end', 80],
                    },
              title: {
                 top: ['parent', 'top', 10],
               start: ['parent', 'start', 10],
                end: ['parent', 'end', 10],
             },
             box2: {
                width: 50, height: 50,
                top: ['parent', 'top', 60],
                end: ['parent', 'end', 10],
             }
           }
         },
         Transitions: {
           default: {
              from: 'start',
              to: 'end',
               pathMotionArc : 'none',
              onSwipe: {
                anchor: 'box1',
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
                .background(Color.LightGray),
            motionScene = MotionScene(content = scene),
            debug= EnumSet.of(MotionLayoutDebugFlags.SHOW_ALL),
        ) {
            Text(text = " Orbit the red ",
                modifier = Modifier
                    .layoutId("title")
                    .background(Color.Gray))
            Box(
                modifier = Modifier
                    .background(Color.Red)
                    .layoutId("box1")
            )
            Box(
                modifier = Modifier
                    .background(Color.Green)
                    .layoutId("box2")
            )
        }
    }
}
