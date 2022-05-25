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

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.sharp.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.*
import java.util.*
import kotlin.math.absoluteValue


@Preview(group = "constraintlayout1")
@Composable
public fun AnimatedConstraintLayoutExample1() {
    var animateToEnd by remember { mutableStateOf(false) }

    val baseConstraintSetStart = """
            {
                box: {
                    width: 'wrap', height: 150,
                    centerHorizontally: 'parent',
                    top: ['parent', 'top', 16],
                    rotationX: 360
                }
            }

        """

    val baseConstraintSetEnd = """
            {
                box: {
                    width: 100, height: 150,
                    end:    ['parent', 'end', 16],
                    bottom: ['parent', 'bottom', 16]
                }
            }
        """

    val cs1 = ConstraintSet(baseConstraintSetStart)
    val cs2 = ConstraintSet(baseConstraintSetEnd)

    val constraints = if (animateToEnd) cs2 else cs1
    Column {
        Button(onClick = { animateToEnd = !animateToEnd }) {
            Text(text = "Run")
        }
        ConstraintLayout(
            constraints,
            animateChanges = true,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            animationSpec = tween<Float>(5000)
        ) {
            var shape = RoundedCornerShape(10.dp)
            Image( painterResource(id = R.drawable.pepper),"",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(shape)
                    .layoutId("box")
                    .fillMaxSize())
        }
    }
}

@Preview(group = "motion1")
@Composable
public fun MotionExample1() {
    var animateToEnd by remember { mutableStateOf(false) }

    val baseConstraintSetStart = """
            {
                Variables: {
                  angle: { start: 0},
                  rotation: { start: 0 },
                  distance: 100,
                  mylist: { tag: 'box' }
                },
                Generate: {
                  mylist: {
                    width: 100,
                    height: 150,
                    circular: ['parent', 'angle', 'distance'],
                    rotationZ: 'rotation'
                  }
                }
            }
        """

    val baseConstraintSetEnd = """
            {
                Variables: {
                  angle: { from: 0, to: 10 },
                  rotation: { from: 0, to: 10 },
                  distance: 100,
                  mylist: { tag: 'box' }
                },
                Generate: {
                  mylist: {
                    width: 100,
                    height: 150,
                    circular: ['parent', 'angle', 'distance'],
                    rotationZ: 'rotation'
                  }
                }
            }
        """

    val cs1 = ConstraintSet(baseConstraintSetStart)
    val cs2 = ConstraintSet(baseConstraintSetEnd)

    val constraints = if (animateToEnd) cs2 else cs1
    Column {
        Button(onClick = { animateToEnd = !animateToEnd }) {
            Text(text = "Play")
        }
        ConstraintLayout(
            constraints,
            animateChanges = true,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            val colors = arrayListOf(Color.Red, Color.Green, Color.Blue, Color.Cyan, Color.Yellow)
            for (i in 1..36) {
                Box(
                    modifier = Modifier
                        .layoutId("h$i", "box")
                        .width(100.dp)
                        .height(150.dp)
                        .background(colors[i % colors.size])
                )
            }
        }
    }
}


@Preview(group = "motion2")
@Composable
public fun MotionExample2() {
    var animateToEnd by remember { mutableStateOf(false) }
    val progress by animateFloatAsState(
        targetValue = if (animateToEnd) 1f else 0f,
        animationSpec = tween(1000)
    )

    Column(Modifier.background(Color.White)) {
        MotionLayout(
            start = ConstraintSet(
                """ {
                    background: { 
                width: "spread",
                height: 60,
                start: ['parent', 'start', 16],
                bottom: ['parent', 'bottom', 16],
                end: ['parent', 'end', 16]
                },
                v1: { 
                width: 100,
                height: 60,
                start: ['parent', 'start', 16],
                bottom: ['parent', 'bottom', 16]
                },
                title: { 
                width: "spread",
                start: ['v1', 'end', 8],
                top: ['v1', 'top', 8],
                end: ['parent', 'end', 8],
                custom: {
                  textSize: 16
                }
                },
                description: { 
                start: ['v1', 'end', 8],
                top: ['title', 'bottom', 0],
                custom: {
                  textSize: 14
                }
                },
                list: { 
                width: "spread",
                height: 0,
                start: ['parent', 'start', 8],
                end: ['parent', 'end', 8],
                top: ['parent', 'bottom', 0]
                },
                play: { 
                end: ['close', 'start', 8],
                top: ['v1', 'top', 0],
                bottom: ['v1', 'bottom', 0]
                },
                close: { 
                end: ['parent', 'end', 24],
                top: ['v1', 'top', 0],
                bottom: ['v1', 'bottom', 0]
                }
            } """
            ),
            end = ConstraintSet(
                """ {
                background: { 
                width: "spread",
                height: 250,
                start: ['parent', 'start', 0],
                end: ['parent', 'end', 0],
                top: ['parent', 'top', 0]
                },
                v1: { 
                width: "spread",
                height: 250,
                start: ['parent', 'start', 0],
                end: ['parent', 'end', 0],
                top: ['parent', 'top', 0]
                },
                title: { 
                width: "spread",
                height: 28,
                start: ['parent', 'start', 16],
                top: ['v1', 'bottom', 16],
                end: ['parent', 'end', 16],
                custom: {
                  textSize: 20
                }
                },
                description: { 
                width: "spread",
                start: ['parent', 'start', 16],
                top: ['title', 'bottom', 8],
                end: ['parent', 'end', 16],
                custom: {
                  textSize: 16
                }
                },
                list: { 
                width: "spread",
                height: 400,
                start: ['parent', 'start', 16],
                end: ['parent', 'end', 16],
                top: ['description', 'bottom', 16],
                },
                play: { 
                start: ['parent', 'end', 8],
                top: ['v1', 'top', 0],
                bottom: ['v1', 'bottom', 0]
                },
                close: { 
                start: ['parent', 'end', 8],
                top: ['v1', 'top', 0],
                bottom: ['v1', 'bottom', 0]
                }
            } """
            ),
            progress = progress,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Box(
                modifier = Modifier
                    .layoutId("background", "box")
                    .background(Color.Cyan)
                    .clickable(onClick = { animateToEnd = !animateToEnd })
            )
            Button(
                onClick = { animateToEnd = !animateToEnd },
                modifier = Modifier
                    .layoutId("v1", "box")
                    .background(Color.Blue)
            ) {}

            Text(
                text = "MotionLayout in Compose",
                modifier = Modifier.layoutId("title"),
                color = Color.Black,
                fontSize = motionProperties("title").value.fontSize("textSize")
            )
            Text(
                text = "Demo screen 17",
                modifier = Modifier.layoutId("description"),
                color = Color.Black,
                fontSize = motionProperties("description").value.fontSize("textSize")
            )
            Box(
                modifier = Modifier
                    .layoutId("list", "box")
                    .background(Color.Gray)
            )
            Icon(
                Icons.Filled.PlayArrow,
                contentDescription = "Play",
                tint = Color.Black,
                modifier = Modifier.layoutId("play")
            )

            Icon(
                Icons.Filled.Close,
                contentDescription = "Close",
                tint = Color.Black,
                modifier = Modifier.layoutId("close")
            )

        }
    }
}


@Preview(group = "motion3")
@Composable
public fun MotionExample3() {
    var animateToEnd by remember { mutableStateOf(false) }
    val progress by animateFloatAsState(
        targetValue = if (animateToEnd) 1f else 0f,
        animationSpec = tween(1000)
    )
    Column(Modifier.background(Color.White)) {
        MotionLayout(
            ConstraintSet(
                """ {
                background: {
                    custom: {
                      color: '#ffffff'
                    }
                },
                circle: {
                    start: ['parent', 'start', 0],
                    end: ['parent', 'end', 0],
                    top: ['parent','top',100],
                    custom: {
                      color: '#fcb045'
                    }
                },
                title: { 
                    width: "spread",
                    start: ['parent', 'start', 36],
                    top: ['circle', 'bottom', 16],
                    end: ['parent', 'end', 36],
                    custom: {
                      color: "#000000"
                    }
                },
                description: { 
                    width: "spread",
                    start: ['parent', 'start', 36],
                    top: ['title', 'bottom', 16],
                    end: ['parent', 'end', 36],
                    custom: {
                      color: "#000000"
                    }
                },
                backgroundSwitch: { 
                    start: ['parent', 'start', 36],
                    top: ['description', 'bottom', 16],
                    end: ['parent', 'end', 36],
                    custom: {
                      color: "#d2d2d2"
                    }
                },
                moonShadow: { 
                    top: ['circle', 'top', 4],
                    end: ['circle', 'end', 4],
                    alpha: 0.0
                },
                buttonSwitch: { 
                    top: ['backgroundSwitch', 'top', 0],
                    start: ['backgroundSwitch', 'start', 0]
                },
                light: { 
                    top: ['backgroundSwitch', 'top', 0],
                    start: ['backgroundSwitch', 'start', 0],
                    bottom: ['backgroundSwitch', 'bottom', 0]
                },
                dark: { 
                    top: ['backgroundSwitch', 'top', 0],
                    end: ['backgroundSwitch', 'end', 0],
                    bottom: ['backgroundSwitch', 'bottom', 0]
                }
             }"""
            ),
            ConstraintSet(
                """ {
                background: {
                    custom: {
                      color: '#000000'
                    }
                },
                circle: {
                    start: ['parent', 'start', 0],
                    end: ['parent', 'end', 0],
                    top: ['parent','top',100],
                    custom: {
                      color: '#7400ab'
                    }
                },
                title: { 
                    width: "spread",
                    start: ['parent', 'start', 36],
                    top: ['circle', 'bottom', 16],
                    end: ['parent', 'end', 36],
                    custom: {
                      color: "#ffffff"
                    }
                },
                description: { 
                    width: "spread",
                    start: ['parent', 'start', 36],
                    top: ['title', 'bottom', 16],
                    end: ['parent', 'end', 36],
                    custom: {
                      color: "#ffffff"
                    }
                },
                backgroundSwitch: { 
                    start: ['parent', 'start', 36],
                    top: ['description', 'bottom', 16],
                    end: ['parent', 'end', 36],
                    custom: {
                      color: "#343434"
                    }
                },
                moonShadow: { 
                    top: ['circle', 'top', 4],
                    end: ['circle', 'end', 4],
                    alpha: 1.0
                },
                buttonSwitch: { 
                    top: ['backgroundSwitch', 'top', 0],
                    end: ['backgroundSwitch', 'end', 0]
                },
                light: { 
                    top: ['backgroundSwitch', 'top', 0],
                    start: ['backgroundSwitch', 'start', 0],
                    bottom: ['backgroundSwitch', 'bottom', 0]
                },
                dark: { 
                    top: ['backgroundSwitch', 'top', 0],
                    end: ['backgroundSwitch', 'end', 0],
                    bottom: ['backgroundSwitch', 'bottom', 0]
                }
              }"""
            ),
            progress = progress,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Box(
                modifier = Modifier
                    .layoutId("background")
                    .fillMaxSize()
                    .clickable(onClick = { animateToEnd = !animateToEnd })
                    .background(motionProperties("background").value.color("color"))
            )

            Box(
                modifier = Modifier
                    .layoutId("circle")
                    .width(200.dp)
                    .height(200.dp)
                    .clip(CircleShape)
                    .background(motionProperties("circle").value.color("color"))
            )

            Text(
                text = "Chose a style",
                modifier = Modifier.layoutId("title"),
                color = motionProperties("title").value.color("color"),
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Pop or subtle. Day or Night. \n Customize your interface",
                modifier = Modifier.layoutId("description"),
                color = motionProperties("description").value.color("color"),
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
            Box(
                modifier = Modifier
                    .layoutId("backgroundSwitch")
                    .width(300.dp)
                    .height(72.dp)
                    .clip(RoundedCornerShape(36.dp))
                    .background(motionProperties("backgroundSwitch").value.color("color"))
            )

            Box(
                modifier = Modifier
                    .layoutId("moonShadow")
                    .width(170.dp)
                    .height(170.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
            )

            Box(
                modifier = Modifier
                    .layoutId("buttonSwitch")
                    .width(150.dp)
                    .height(72.dp)
                    .clip(RoundedCornerShape(36.dp))
                    .background(Color.Gray)
            )

            Text(
                text = "Light",
                modifier = Modifier
                    .layoutId("light")
                    .width(150.dp),
                color = Color.White,
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Dark",
                modifier = Modifier
                    .layoutId("dark")
                    .width(150.dp),
                color = Color.Black,
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )


        }
    }
}

@Preview(group = "motion4")
@Composable
public fun MotionExample4() {
    var animateToEnd by remember { mutableStateOf(false) }
    val progress by animateFloatAsState(
        targetValue = if (animateToEnd) 1f else 0f,
        animationSpec = tween(4000)
    )
    Column(Modifier.background(Color.White)) {
        MotionLayout(
            motionScene = MotionScene(
                """{
  ConstraintSets: {
    start: {
      background: {
        custom: {
          color: '#ffffff'
        }
      },
      circle: {
        width: 200,
        height: 200,
        start: ['parent', 'start', 0],
        end: ['parent', 'end', 0],
        top: ['parent', 'top', 100],
        custom: {
          color: '#fcb045'
        }
      },
      title: {
        width: "spread",
        start: ['parent', 'start', 36],
        top: ['circle', 'bottom', 16],
        end: ['parent', 'end', 36],
        custom: {
          color: "#000000"
        }
      },
      description: {
        width: "spread",
        start: ['parent', 'start', 36],
        top: ['title', 'bottom', 16],
        end: ['parent', 'end', 36],
        custom: {
          color: "#ffffff"
        }
      },
      backgroundSwitch: {
        width: 300,
        height: 72,
        start: ['parent', 'start', 36],
        top: ['description', 'bottom', 16],
        end: ['parent', 'end', 36],
        custom: {
          color: "#d2d2d2"
        }
      },
      moonShadow: {
        width: 170,
        height: 170,
        top: ['circle', 'top', 4],
        end: ['circle', 'end', 4],
        alpha: 0.0
      },
      buttonSwitch: {
        width: 150,
        height: 72,
        top: ['backgroundSwitch', 'top', 0],
        start: ['backgroundSwitch', 'start', 0]
      },
      light: {
        top: ['backgroundSwitch', 'top', 0],
        start: ['backgroundSwitch', 'start', 0],
        bottom: ['backgroundSwitch', 'bottom', 0]
      },
      dark: {
        top: ['backgroundSwitch', 'top', 0],
        end: ['backgroundSwitch', 'end', 0],
        bottom: ['backgroundSwitch', 'bottom', 0]
      }
    },
    end: {
      Extends: 'start',
      background: {
        custom: {
          color: '#000000'
        }
      },
      circle: {
        custom: {
          color: '#7400ab'
        }
      },
      title: {
        custom: {
          color: "#ffffff"
        }
      },
      description: {
        custom: {
          color: "#000000"
        }
      },
      backgroundSwitch: {
        custom: {
          color: "#343434"
        }
      },
      moonShadow: {
        alpha: 1.0
      },
      buttonSwitch: {
        clear: ['constraints'],
        top: ['backgroundSwitch', 'top', 0],
        end: ['backgroundSwitch', 'end', 0]
      }
    }
  }
}"""
            ),
            progress = progress,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Box(
                modifier = Modifier
                    .layoutId("background")
                    .fillMaxSize()
                    .clickable(onClick = { animateToEnd = !animateToEnd })
                    .background(motionProperties("background").value.color("color"))
            )

            Box(
                modifier = Modifier
                    .layoutId("circle")
                    .clip(CircleShape)
                    .background(motionProperties("circle").value.color("color"))
            )

            Text(
                text = "Chose a style",
                modifier = Modifier.layoutId("title"),
                color = motionProperties("title").value.color("color"),
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Pop or subtle. Day or Night. \n Customize your interface",
                modifier = Modifier.layoutId("description"),
                color = motionProperties("title").value.color("color"),
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
            Box(
                modifier = Modifier
                    .layoutId("backgroundSwitch")
                    .clip(RoundedCornerShape(36.dp))
                    .background(motionProperties("backgroundSwitch").value.color("color"))
            )

            Box(
                modifier = Modifier
                    .layoutId("moonShadow")
                    .clip(CircleShape)
                    .background(Color.Black)
            )

            Box(
                modifier = Modifier
                    .layoutId("buttonSwitch")
                    .clip(RoundedCornerShape(36.dp))
                    .background(Color.Gray)
            )

            Text(
                text = "Light",
                modifier = Modifier
                    .layoutId("light")
                    .width(150.dp),
                color = Color.White,
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Dark",
                modifier = Modifier
                    .layoutId("dark")
                    .width(150.dp),
                color = Color.Black,
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Preview(group = "motion5")
@Composable
public fun MotionExample5() {
    var animateToEnd by remember { mutableStateOf(false) }

    val progress by animateFloatAsState(
        targetValue = if (animateToEnd) 1f else 0f,
        animationSpec = tween(1000)
    )
    Column {
        MotionLayout(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.White),
            motionScene = MotionScene(
                """{
  ConstraintSets: {
    start: {
      backgroundSwipe: {
        start: ['parent', 'start', 2],
        end: ['parent','end',2],
        top: ['parent','top',2],
        bottom: ['parent','bottom',2]
      },
      backgroundButtonSwipe: {
        width: "spread",
        height: "spread",
        start: ['buttonSwipe', 'start', 0],
        end: ['buttonSwipe','end',0],
        top: ['buttonSwipe','top',0],
        bottom: ['backgroundSwipe','bottom',0]
      },
      swipeUp: {
        start: ['buttonSwipe', 'start', 0],
        end: ['buttonSwipe','end',0],
        top: ['buttonSwipe','top',0],
        bottom: ['buttonSwipe','bottom',0]
      },
      buttonSwipe: {
        start: ['backgroundSwipe', 'start', 0],
        end: ['backgroundSwipe','end',0],
        bottom: ['backgroundSwipe','bottom',0],
        custom: {
          color: "#004DBC"
        }
      }
    },
    end: {
      Extends: 'start',
      buttonSwipe: {
        clear: ['constraints'],
        start: ['backgroundSwipe', 'start', 0],
        end: ['backgroundSwipe','end',0],
        top: ['backgroundSwipe','top',2],
        custom: {
          color: "#33D6C1"
        }
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
                    .layoutId("backgroundSwipe")
                    .height(450.dp)
                    .width(80.dp)
                    .clip(shape = RoundedCornerShape(40.dp))
                    .background(Color.LightGray)
                    .border(2.dp, Color.DarkGray, shape = RoundedCornerShape(40.dp))

            )

            Box(
                modifier = Modifier
                    .layoutId("backgroundButtonSwipe")
                    .width(80.dp)
                    .clip(shape = RoundedCornerShape(40.dp))
                    .background(Color(android.graphics.Color.parseColor("#4462D7")))
                    .border(2.dp, Color.DarkGray, shape = RoundedCornerShape(40.dp))
            )

            Box(
                modifier = Modifier
                    .layoutId("buttonSwipe")
                    .height(78.dp)
                    .width(78.dp)
                    .clip(shape = CircleShape)
                    .background(motionProperties("buttonSwipe").value.color("color"))
                    .clickable(onClick = { animateToEnd = !animateToEnd })

            )
            Icon(
                Icons.Sharp.KeyboardArrowUp,
                contentDescription = "SwipeUp",
                tint = Color.White,
                modifier = Modifier
                    .layoutId("swipeUp")
                    .width(40.dp)
                    .height(40.dp)
            )

        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview(group = "motion6")
@Composable
public fun MotionExample6() {
    var componentHeight by remember { mutableStateOf(1000f) }
    val swipeableState = rememberSwipeableState("Bottom")
    val anchors = mapOf(0f to "Bottom", componentHeight to "Top")

    val mprogress = (swipeableState.offset.value / componentHeight)

    MotionLayout(motionScene = MotionScene(
        """{
                Header: { exportAs: 'motion6'},
                ConstraintSets: {
                  start: {
                    Variables: {
                      texts: { tag: 'text' },
                      margin: { from: 0, step: 50 }
                    },
                    Generate: {
                      texts: {
                        top: ['parent', 'top', 'margin'],
                        start: ['parent', 'end', 16 ]
                      }
                    },
                    box: {
                      width: 'spread',
                      height: 64,
                      centerHorizontally: 'parent',
                      bottom: ['parent','bottom']
                    },
                    content: {
                      width: 'spread',
                      height: '400',
                      centerHorizontally: 'parent',
                      top: ['box','bottom', 32]
                    },
                    name: {
                      centerVertically: 'box',
                      start: ['parent', 'start', 16]
                    }
                  },
                  end: {
                    Variables: {
                      texts: { tag: 'text' },
                      margin: { from: 0, step: 50 }
                    },
                    Generate: {
                      texts: {
                        start: ['parent','start', 32],
                        top: ['content', 'top', 'margin']
                      }
                    },
                    box: {
                      width: 'spread',
                      height: 200,
                      centerHorizontally: 'parent',
                      top: ['parent','top']
                    },
                    content: {
                      width: 'spread',
                      height: 'spread',
                      centerHorizontally: 'parent',
                      top: ['box','bottom'],
                      bottom: ['parent', 'bottom']
                    },
                    name: {
                      rotationZ: 90,
                      scaleX: 2,
                      scaleY: 2,
                      end: ['parent', 'end', 16],
                      top: ['parent', 'top', 90]
                    }
                  }
                },
                Transitions: {
                  default: {
                    from: 'start',
                    to: 'end',
                    pathMotionArc: 'startHorizontal',
                    KeyFrames: {
                      KeyAttributes: [
                        {
                          target: ['box','content'],
                          frames: [50],
                          rotationZ: [25],
                          //rotationY: [25], 
                        }
                      ]
                    }
                  }
                }
            }"""
    ),
        progress = mprogress,
        debug = EnumSet.of(MotionLayoutDebugFlags.NONE),
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                // resistance = null,
                reverseDirection = true,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Vertical
            )
            .onSizeChanged { size ->
                componentHeight = size.height.toFloat()
            }
    ) {
        Box(
            modifier = Modifier
                .layoutId("content")
                .background(Color.LightGray)
        )
        Box(
            modifier = Modifier
                .layoutId("box")
                .background(Color.Cyan)
        )
        Text(modifier = Modifier.layoutId("name"), text = "MotionLayout")
        for (i in 0 until 6) {
            Text(modifier = Modifier.layoutId("text$i", "text"), text = "Test $i")
        }
    }

}

@Preview(group = "motion7")
@Composable
public fun MotionExample7() {
    var animateToEnd by remember { mutableStateOf(false) }

    val progress by animateFloatAsState(
        targetValue = if (animateToEnd) 1f else 0f,
        animationSpec = tween(1000)
    )
    Column {
        MotionLayout(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.Black),
            motionScene = MotionScene(
                """{
        ConstraintSets: {
        start: {
          box1: {
            start: ['parent', 'start', 16],
            bottom: ['parent','bottom',16]
          },
          box2: {
            start: ['box1', 'end', 16],
            bottom: ['parent','bottom',16]
          },
          box3: {
            end: ['box2', 'end', 0],
            bottom: ['box2','top',16]
          },
          box4: {
            start: ['parent', 'end', 16],
            bottom: ['box3','bottom',16]
          },
          box5: {
            start: ['parent', 'start', 16],
            top: ['box6','bottom',16]
          },
          box6: {
            start: ['parent', 'start', 16],
            top: ['parent','top',16]
          },
          box7: {
            start: ['box6', 'end', 16],
            top: ['box6','top',0]
          },
          box8: {
            start: ['parent', 'end', 16],
            bottom: ['box7','bottom', 0]
          },
          box9: {
            start: ['parent', 'start', 16],
            top: ['box5','bottom', 16]
          }
        },
        end: {
          box1: {
            end: ['parent','start',0],
            bottom: ['parent','bottom',16]
          },
          box2: {
            width: 120,
            height: 80,
            start: ['parent', 'start', 16],
            bottom: ['parent','bottom',16]
          },
          box3: {
            start: ['box2', 'end', 16],
            bottom: ['parent','bottom',16]
          },
          box4: {
            width: 200,
            height: 160,
            end: ['box3', 'end', 0],
            bottom: ['box3','top',16]
          },
          box5: {
            width: 120,
            height: 280,
            start: ['parent', 'start', 16],
            top: ['box6','bottom',16]
          },
          box6: {
            start: ['parent', 'start', 16],
            top: ['box7','bottom',16]
          },
          box7: {
            width: 120,
            height: 80,
            start: ['parent', 'start', 16],
            top: ['parent','top',16]
          },
          box8: {
            width: 200,
            height: 160,
            start: ['box7', 'end', 16],
            top: ['box7','top', 0]
          },
          box9: {
            end: ['parent', 'start', 0],
            top: ['box5','top',0]
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
                    .height(80.dp)
                    .width(120.dp)
                    .background(Color.Gray)
                    .clip(shape = CircleShape)
                    .clickable(onClick = { animateToEnd = !animateToEnd })
            )

            Box(
                modifier = Modifier
                    .layoutId("box2")
                    .height(160.dp)
                    .width(200.dp)
                    .background(Color.Red)
                    .clip(shape = CircleShape)
                    .clickable(onClick = { animateToEnd = !animateToEnd })
            )

            Box(
                modifier = Modifier
                    .layoutId("box3")
                    .height(160.dp)
                    .width(200.dp)
                    .background(Color.White)
                    .clip(shape = CircleShape)
                    .clickable(onClick = { animateToEnd = !animateToEnd })
            )

            Box(
                modifier = Modifier
                    .layoutId("box4")
                    .height(80.dp)
                    .width(120.dp)
                    .background(Color.Green)
                    .clip(shape = CircleShape)
                    .clickable(onClick = { animateToEnd = !animateToEnd })
            )

            Box(
                modifier = Modifier
                    .layoutId("box5")
                    .height(80.dp)
                    .width(120.dp)
                    .background(Color.Blue)
                    .clip(shape = CircleShape)
                    .clickable(onClick = { animateToEnd = !animateToEnd })
            )

            Box(
                modifier = Modifier
                    .layoutId("box6")
                    .height(80.dp)
                    .width(120.dp)
                    .background(Color.Yellow)
                    .clip(shape = CircleShape)
                    .clickable(onClick = { animateToEnd = !animateToEnd })
            )

            Box(
                modifier = Modifier
                    .layoutId("box7")
                    .height(160.dp)
                    .width(200.dp)
                    .background(Color.Cyan)
                    .clip(shape = CircleShape)
                    .clickable(onClick = { animateToEnd = !animateToEnd })
            )

            Box(
                modifier = Modifier
                    .layoutId("box8")
                    .height(80.dp)
                    .width(120.dp)
                    .background(Color.Magenta)
                    .clip(shape = CircleShape)
                    .clickable(onClick = { animateToEnd = !animateToEnd })
            )

            Box(
                modifier = Modifier
                    .layoutId("box9")
                    .height(280.dp)
                    .width(120.dp)
                    .background(Color.DarkGray)
                    .clip(shape = CircleShape)
                    .clickable(onClick = { animateToEnd = !animateToEnd })
            )

        }
    }
}

@Preview(group = "motion8")
@Composable
public fun MotionExample8() {
    var animateToEnd by remember { mutableStateOf(false) }

    val progress by animateFloatAsState(
        targetValue = if (animateToEnd) 1f else 0f,
        animationSpec = tween(2000)
    )
    Column {
        MotionLayout(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.Black),
            debug = EnumSet.of(MotionLayoutDebugFlags.SHOW_ALL),
            motionScene = MotionScene(
                """
                    {
                ConstraintSets: {
                start: {
                  box1: {
                    rotationZ: 0,
                    translationZ: 4,
                    start: ['parent', 'start', 16],
                    end: ['parent', 'end', 16],
                    bottom: ['parent','bottom',44]
                  },
                  box2: {
                    rotationZ: 20,
                    translationZ: 2,
                    start: ['parent', 'start', 16],
                    end: ['parent', 'end', 16],
                    bottom: ['parent','bottom',44]
                  },
                  box3: {
                    rotationZ: -20,
                    translationZ: 0,
                    start: ['parent', 'start', 16],
                    end: ['parent', 'end', 16],
                    bottom: ['parent','bottom',44]
                  }
                },
                end : {
                  Extends: 'start',
                  box1: {
                    rotationZ: -20,
                    translationZ: 0,
                  },
                  box2: {
                    rotationZ: 0,
                    translationZ: 4,
                  },
                  box3: {
                    rotationZ: 20,
                    translationZ: 2,
                  }
                },
                },
                Transitions: {
                  default: {
                    from: 'start',
                    to: 'end',
                    pathMotionArc: 'startHorizontal',
                    KeyFrames: {
                      KeyPositions: [
                        {
                       type: 'parentRelative',
                           target: ['box1'],
                           frames: [50],
                           percentX: [0.5],
                           percentY: [0.4]
                        }
                      ]
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
                    .height(125.dp)
                    .width(188.dp)
                    .clip(shape = RoundedCornerShape(12.dp))
                    .background(Color.Green)
                    .clickable(onClick = { animateToEnd = !animateToEnd })
            )

            Box(
                modifier = Modifier
                    .layoutId("box2")
                    .height(125.dp)
                    .width(188.dp)
                    .clip(shape = RoundedCornerShape(12.dp))
                    .background(Color.Blue)
                    .clickable(onClick = { animateToEnd = !animateToEnd })
            )

            Box(
                modifier = Modifier
                    .layoutId("box3")
                    .height(125.dp)
                    .width(188.dp)
                    .clip(shape = RoundedCornerShape(12.dp))
                    .background(Color.Red)
                    .clickable(onClick = { animateToEnd = !animateToEnd })
            )

        }
    }
}

@Preview(group = "motion9")
@Composable
fun MotionExample9() {
    var animateToEnd by remember { mutableStateOf(false) }

    val progress by animateFloatAsState(
        targetValue = if (animateToEnd) 1f else 0f,
        animationSpec = tween(2000)
    )
    Column {
        MotionLayout(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.Black),
            debug = EnumSet.of(MotionLayoutDebugFlags.SHOW_ALL),
            motionScene = MotionScene(
                """
{
                ConstraintSets: {
                  csTopLeft: {
                    box1: {
                      start: ['parent', 'start', 16],
                      top: ['parent', 'top', 16]
                    },
                  },
                  csTopRight : {
                    box1: {
                      end: ['parent', 'end', 16],
                      top: ['parent', 'top', 16]
                    },
                  },
                  csBottomLeft : {
                    box1: {
                      start: ['parent', 'start', 16],
                      bottom: ['parent', 'bottom', 16]
                    },
                  },
                  csBottomRight : {
                    box1: {
                      end: ['parent', 'end', 16],
                      bottom: ['parent', 'bottom', 16]
                    },
                  }
                },
                Transitions: {
                  default: {
                    from: 'csBottomRight',
                    to: 'csTopLeft',
                    pathMotionArc: 'startHorizontal',
                    KeyFrames: {
                      KeyPositions: [
                        {
                       type: 'parentRelative',
                           target: ['box1'],
                           frames: [50],
                           percentX: [0.5],
                           percentY: [0.4]
                        }
                      ]
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
                    .height(125.dp)
                    .width(188.dp)
                    .clip(shape = RoundedCornerShape(12.dp))
                    .background(Color.Green)
                    .clickable(onClick = { animateToEnd = !animateToEnd })
            )
        }
    }
}

@Preview(group = "motion10")
@Composable
fun MotionExample10() {
    val states = remember {
        listOf(
        "csTopRight",
        "csBottomLeft",
        "csBottomRight",
        "csTopLeft"
        )
    }

    var currentState by remember {
        mutableStateOf(-1)
    }

    Column {
        Button(
            onClick = {
                currentState = if (currentState >= (states.size - 1)) {
                    0
                } else {
                    currentState + 1
                }
        }) {
            Text("Run")
        }
        MotionLayout(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.Black),
            debug = EnumSet.of(MotionLayoutDebugFlags.SHOW_ALL),
            motionScene = MotionScene(
                """
{
                ConstraintSets: {
                  csTopLeft: {
                    box1: {
                      start: ['parent', 'start', 16],
                      top: ['parent', 'top', 16]
                    },
                  },
                  csTopRight : {
                    box1: {
                      end: ['parent', 'end', 16],
                      top: ['parent', 'top', 16]
                    },
                  },
                  csBottomLeft : {
                    box1: {
                      start: ['parent', 'start', 16],
                      bottom: ['parent', 'bottom', 16]
                    },
                  },
                  csBottomRight : {
                    box1: {
                      end: ['parent', 'end', 16],
                      bottom: ['parent', 'bottom', 16]
                    },
                  }
                },
                Transitions: {
                  default: {
                    from: 'csBottomRight',
                    to: 'csTopLeft',
                    pathMotionArc: 'startHorizontal',
                    KeyFrames: {
                      KeyPositions: [
                        {
                       type: 'parentRelative',
                           target: ['box1'],
                           frames: [50],
                           percentX: [0.5],
                           percentY: [0.4]
                        }
                      ]
                    }
                  }
                }
}
"""
            ),
            constraintSetName = states.getOrNull(currentState),
        ) {
            Box(
                modifier = Modifier
                    .layoutId("box1")
                    .height(125.dp)
                    .width(188.dp)
                    .clip(shape = RoundedCornerShape(12.dp))
                    .background(Color.Green)
            )
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Preview(group = "motion11")
@Composable
public fun MotionExample11() {
    var componentHeight by remember { mutableStateOf(3000f) }
    val swipeableState = rememberSwipeableState("Bottom")
    val anchors = mapOf(0f to "Bottom", componentHeight to "Top")

    val mprogress = (swipeableState.offset.value / componentHeight)

    MotionLayout(motionScene = MotionScene(
        """{
                Header: { exportAs: 'motion6'},
                ConstraintSets: {
                  start: {
                    box: {
                      width: 'spread',
                      height: 220,
                      start: ['parent', 'start'],
                      end: ['parent', 'end'],
                      top: ['parent','top'],
                      translationZ: 0
                    },
                    box1: {
                      width: '60%',
                      height: 120,
                      start: ['parent', 'start'],
                      end: ['parent', 'end'],
                      top: ['box','bottom'],
                      bottom: ['box','bottom'],
                      translationZ: 1
                    },
                      title: {
                      width: 'spread',
                      height: 'wrap',
                      start: ['box1', 'start'],
                      end: ['parent', 'end', 24],
                      bottom: ['box1','top', 4],
                      translationZ: 1
                    },
                    circle: {
                      width: 200,
                      height: 200,
                      start: ['parent', 'end'],
                      end: ['parent', 'end'],
                      top: ['parent','top', 8], 
                      translationZ: 1
                    },
                    content: {
                      width: 'spread',
                      height: 'spread',
                      start: ['parent', 'start'],
                      end: ['parent', 'end'],
                      top: ['box','bottom'],
                      bottom: ['parent','bottom'],
                    }
                  },
                  end: {
                    box: {
                      width: 'spread',
                      height: 120,
                      start: ['parent', 'start'],
                      end: ['parent', 'end'],
                      top: ['parent','top'], 
                      translationZ: 1
                    },
                    circle: {
                      width: 120,
                      height: 120,
                      start: ['parent', 'start'],
                      end: ['parent', 'end'],
                      bottom:  ['parent', 'top'],
                      top: ['parent','top'],
                      translationZ: 1
                    },
                    box1: {
                      width: '60%',
                      height: 120,
                      start: ['parent', 'start'],
                      end: ['parent', 'end'],
                      top: ['parent','top'],
                      translationZ: 0
                    },
                    title: {
                      width: 'spread',
                      height: 'wrap',
                      start: ['box', 'start',16],
                      end: ['parent', 'end', 16],
                      bottom: ['box','bottom', 4],
                      translationZ: 1
                    },content: {
                      width: 'spread',
                      height: 'spread',
                      start: ['parent', 'start'],
                      end: ['parent', 'end'],
                      top: ['box','bottom'],
                      bottom: ['parent','bottom']
                    }
                  }
                },
                Transitions: {
                  default: {
                    from: 'start',
                    to: 'end',
                    pathMotionArc: 'startHorizontal',
                    KeyFrames: {
                      KeyPositions: [
                        {
                       type: 'parentRelative',
                           target: ['box1'],
                           frames: [50],
                           percentX: [0.5],
                           percentY: [0.3]
                        }
                      ]
                    }
                  }
                }
            }"""
    ),
        progress = mprogress,
        debug = EnumSet.of(MotionLayoutDebugFlags.NONE),
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                // resistance = null,
                reverseDirection = true,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Vertical
            )
            .onSizeChanged { size ->
                componentHeight = size.height.toFloat()
            }
    ) {
        Box(
            modifier = Modifier
                .layoutId("content")
                .background(Color.LightGray)
        )
        Box(
            modifier = Modifier
                .layoutId("box")
                .background(Color.Cyan)
        )
        Box(
            modifier = Modifier
                .layoutId("box1")
                .background(Color.Green)
        )

        Text(
            text = "MotionLayout example",
            fontSize = 18.sp,
            modifier = Modifier.layoutId("title")
        )

        Box(
            modifier = Modifier
                .layoutId("circle")
                .clip(CircleShape)
                .background(Color.Red)
        )

    }

}

@Preview(group = "motion12")
@Composable
fun MotionExample12() {
    val motionState = rememberMotionLayoutState()
    val corners = 400 - ((motionState.currentProgress * 100) * 4).coerceAtMost(400f)

    MotionLayout(motionScene = MotionScene(
        """{
                Header: { exportAs: 'motion6'},
                ConstraintSets: {
                  start: {
                    circle: {
                      width: 72,
                      height: 72,
                      end: ['parent', 'end',24],
                      top: ['content','top'],
                      bottom: ['content','top'],
                      translationZ: 1
                    },
                    box1: {
                      width: 150,
                      height: 220,
                      start: ['parent', 'start',36],
                      top: ['parent','top',190],
                      translationZ: 1
                    },
                      title: {
                      width: 'spread',
                      height: 'wrap',
                      start: ['box1', 'end', 16],
                      end: ['parent', 'end', 16],
                      top: ['content','top', 44],
                      translationZ: 1
                    },
                    content: {
                      width: 'spread',
                      height: 'spread',
                      start: ['parent', 'start'],
                      end: ['parent', 'end'],
                      top: ['parent','top', 260],
                      bottom: ['parent','bottom'],
                    }
                  },
                  end: {circle: {
                      width: 0,
                      height: 0,
                      end: ['parent', 'end',82],
                      top: ['content','top'],
                      bottom: ['content','top'],
                      translationZ: 1
                    },
                   box1: {
                      width: 150,
                      height: 220,
                      start: ['parent', 'start',36],
                      top: ['parent','top',90],
                      translationZ: 0
                    },
                    title: {
                      width: 'spread',
                      height: 'wrap',
                      start: ['parent', 'start',16],
                      end: ['parent', 'end', 16],
                      top: ['parent','top', 16],
                      translationZ: 1
                    },content: {
                      width: 'spread',
                      height: 'spread',
                      start: ['parent', 'start'],
                      end: ['parent', 'end'],
                      top: ['box','bottom'],
                      bottom: ['parent','bottom'],
                      translationZ: 1
                    }
                  }
                },
                Transitions: {
                  default: {
                    from: 'start',
                    to: 'end',
                    pathMotionArc: 'startHorizontal',
                    onSwipe: {
                      anchor: 'content',
                      direction: 'up',
                      side: 'top',
                      mode: 'velocity',
                      touchUp: 'decelerateComplete'
                    },
                    KeyFrames: {
                      KeyPositions: [
                        {
                       type: 'parentRelative',
                           target: ['box1'],
                           frames: [40],
                           percentX: [0.0],
                           percentY: [-0.18]
                        }
                      ],
                      KeyAttributes: [
                        {
                          target: ['box1'],
                          frames: [50],
                          rotationZ: [60],
                          //rotationY: [25], 
                        }
                      ]
                    }
                  }
                }
            }"""
    ),
        motionLayoutState = motionState,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Box(
            modifier = Modifier
                .layoutId("content")
                .clip(CutCornerShape(topStart = corners))
                .background(Color.LightGray)
        )
        Box(
            modifier = Modifier
                .layoutId("box1")
                .background(Color.Green)
        )
        Text(
            text = "MotionLayout example",
            fontSize = 18.sp,
            modifier = Modifier.layoutId("title")
        )
        Box(
            modifier = Modifier
                .layoutId("circle")
                .clip(CircleShape)
                .background(Color.Red)
        )
    }
}