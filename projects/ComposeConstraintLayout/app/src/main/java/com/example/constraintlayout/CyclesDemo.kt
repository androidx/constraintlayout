package com.example.constraintlayout

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionLayoutDebugFlags
import androidx.constraintlayout.compose.MotionScene
import java.util.*

@Preview(group = "motion8")
@Composable
public fun CycleScale() {
    var animateToEnd by remember { mutableStateOf(false) }

    val progress by animateFloatAsState(
        targetValue = if (animateToEnd) 1f else 0f,
        animationSpec = tween(6000)
    )
    Column {
        MotionLayout(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .background(Color.White),
            motionScene = MotionScene("""{
                   Debug: {
                  name: 'Cycle30'
                },
                ConstraintSets: {
                  start: {
                    a: {
                      width: 40,
                      height: 40,
                      start: ['parent', 'start', 16],
                      bottom: ['parent', 'bottom', 16]
                    }
                  },
                  end: {
                    a: {
                      width: 40,
                      height: 40,
                      //rotationZ: 390,
                      end: ['parent', 'end', 16],
                      top: ['parent', 'top', 16]
                    }
                  }
                },
                Transitions: {
                  default: {
                    from: 'start',
                    to: 'end',
                    pathMotionArc: 'startHorizontal',
                    KeyFrames: {
//                      KeyPositions: [
//                        {
//                          target: ['a'],
//                          frames: [25, 50, 75],
////                          percentX: [0.4, 0.8, 0.1],
////                          percentY: [0.4, 0.8, 0.3]
//                        }
//                      ],
                      KeyCycles: [
                        {
                          target: ['a'],
                          frames: [0, 50, 100],
                          period: [0 , 2 , 0],
                          offset: 1,
                          scaleX:  [0 , 3 , 0],
                        }
                      ]
                    }
                  }
                }
            }"""),
            debug = EnumSet.of(MotionLayoutDebugFlags.SHOW_ALL),
            progress = progress) {
            Box(modifier = Modifier
                .layoutId("a")
                .background(Color.Red))
        }

        Button(onClick = { animateToEnd = !animateToEnd }) {
            Text(text = "Run")
        }
    }
}

@Preview(group = "motion8")
@Composable
public fun CycleTranslationXY() {
    var animateToEnd by remember { mutableStateOf(false) }

    val progress by animateFloatAsState(
        targetValue = if (animateToEnd) 1f else 0f,
        animationSpec = tween(6000)
    )
    Column {
        MotionLayout(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .background(Color.White),
            motionScene = MotionScene("""{
               Debug: {
                  name: 'Cycle31'
                },
                ConstraintSets: {
                  start: {
                    a: {
                      width: 40,
                      height: 40,
                      start: ['parent', 'start', 16],
                      bottom: ['parent', 'bottom', 16]
                    }
                  },
                  end: {
                    a: {
                      width: 40,
                      height: 40,
                      //rotationZ: 390,
                      end: ['parent', 'end', 16],
                      top: ['parent', 'top', 16]
                    }
                  }
                },
                Transitions: {
                  default: {
                    from: 'start',
                    to: 'end',
                    pathMotionArc: 'startHorizontal',
                    KeyFrames: {
//                      KeyPositions: [
//                        {
//                          target: ['a'],
//                          frames: [25, 50, 75],
////                          percentX: [0.4, 0.8, 0.1],
////                          percentY: [0.4, 0.8, 0.3]
//                        }
//                      ],
                      KeyCycles: [
                        {
                          target: ['a'],
                          frames: [0, 50, 100],
                          period: [0 , 3 , 0],
                          translationX: 222,
                          translationY: 222
                        }
                      ]
                    }
                  }
                }
            }"""),
            debug = EnumSet.of(MotionLayoutDebugFlags.SHOW_ALL),
            progress = progress) {
            Box(modifier = Modifier
                .layoutId("a")
                .background(Color.Red))
        }

        Button(onClick = { animateToEnd = !animateToEnd }) {
            Text(text = "Run")
        }
    }
}

@Preview(group = "motion8")
@Composable
public fun CycleRotationZ() {
    var animateToEnd by remember { mutableStateOf(false) }

    val progress by animateFloatAsState(
        targetValue = if (animateToEnd) 1f else 0f,
        animationSpec = tween(6000)
    )
    Column {
        MotionLayout(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .background(Color.White),
            motionScene = MotionScene("""{
               Debug: {
                  name: 'Cycle32'
                },
                ConstraintSets: {
                  start: {
                    a: {
                      width: 40,
                      height: 40,
                      start: ['parent', 'start', 16],
                      bottom: ['parent', 'bottom', 16]
                    }
                  },
                  end: {
                    a: {
                      width: 40,
                      height: 40,
                      //rotationZ: 390,
                      end: ['parent', 'end', 16],
                      top: ['parent', 'top', 16]
                    }
                  }
                },
                Transitions: {
                  default: {
                    from: 'start',
                    to: 'end',
                    pathMotionArc: 'startHorizontal',
                    KeyFrames: {
//                      KeyPositions: [
//                        {
//                          target: ['a'],
//                          frames: [25, 50, 75],
////                          percentX: [0.4, 0.8, 0.1],
////                          percentY: [0.4, 0.8, 0.3]
//                        }
//                      ],
                      KeyCycles: [
                        {
                          target: ['a'],
                           frames: [0, 50, 100],
                          period: [0 , 3 , 0],
                          rotationZ: [0, 360, 0],
                          
                        }
                      ]
                    }
                  }
                }
            }"""),
            debug = EnumSet.of(MotionLayoutDebugFlags.SHOW_ALL),
            progress = progress) {
            Box(modifier = Modifier
                .layoutId("a")
                .background(Color.Red))
        }

        Button(onClick = { animateToEnd = !animateToEnd }) {
            Text(text = "Run")
        }
    }
}


@Preview(group = "motion8")
@Composable
public fun CycleRotationXY() {
    var animateToEnd by remember { mutableStateOf(false) }

    val progress by animateFloatAsState(
        targetValue = if (animateToEnd) 1f else 0f,
        animationSpec = tween(6000)
    )
    Column {
        MotionLayout(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .background(Color.White),
            motionScene = MotionScene("""{
                ConstraintSets: {
                  start: {
                    a: {
                      width: 40,
                      height: 40,
                      start: ['parent', 'start', 16],
                      bottom: ['parent', 'bottom', 16]
                    }
                  },
                  end: {
                    a: {
                      width: 40,
                      height: 40,
                      //rotationZ: 390,
                      end: ['parent', 'end', 16],
                      top: ['parent', 'top', 16]
                    }
                  }
                },
                Transitions: {
                  default: {
                    from: 'start',
                    to: 'end',
                    pathMotionArc: 'startHorizontal',
                    KeyFrames: {
//                      KeyPositions: [
//                        {
//                          target: ['a'],
//                          frames: [25, 50, 75],
////                          percentX: [0.4, 0.8, 0.1],
////                          percentY: [0.4, 0.8, 0.3]
//                        }
//                      ],
                      KeyCycles: [
                        {
                          target: ['a'],
                          frames: [0, 50, 100],
                          period: [0 , 2 , 0],
                          rotationX: [0, 45, 0],
                          rotationY: [0, 45, 0], 
                        }
                      ]
                    }
                  }
                }
            }"""),
            debug = EnumSet.of(MotionLayoutDebugFlags.SHOW_ALL),
            progress = progress) {
            Box(modifier = Modifier
                .layoutId("a")
                .background(Color.Red))
        }

        Button(onClick = { animateToEnd = !animateToEnd }) {
            Text(text = "Run")
        }
    }
}
