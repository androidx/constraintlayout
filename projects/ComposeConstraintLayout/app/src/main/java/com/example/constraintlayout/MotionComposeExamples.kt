package com.example.constraintlayout

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.*

@Preview(group = "motion1")
@Composable
public fun ScreenExample14() {
    var animateToEnd by remember { mutableStateOf(false) }
    val progress by animateFloatAsState(
        targetValue = if (animateToEnd) 1f else 0f,
        animationSpec = tween(1000)
    )


    var baseConstraintSetStart = """
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

    var baseConstraintSetEnd = """
            {
                Variables: {
                  angle: { start: 0, increment: 10 },
                  rotation: { start: 0, increment: 10 },
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

    var cs1 = ConstraintSet(baseConstraintSetStart)
    var cs2 = ConstraintSet(baseConstraintSetEnd)


    Column {
        Button(onClick = { animateToEnd = !animateToEnd }) {
            Text(text = "Run")
        }
        MotionLayout(
            cs1, cs2,
            progress = progress,
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
public fun ScreenExample15() {
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
            ConstraintSet(
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
                top: ['description', 'bottom', 16]
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
                text = "Demo screen 15",
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
public fun ScreenExample16() {
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
                      color: "#ffffff"
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
                      color: "#000000"
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
                text = "Pop or subtle. Day or Night. \\n Customize your interface",
                modifier = Modifier.layoutId("description"),
                color = motionProperties("title").value.color("color"),
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