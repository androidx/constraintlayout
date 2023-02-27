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

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene


@Preview(group = "rowToColumnGrid")
@Composable
public fun rowToColumnGridDemo() {
    var animateToEnd by remember { mutableStateOf(false) }

    val progress = remember { Animatable(0f) }

    LaunchedEffect(animateToEnd) {
        progress.animateTo(if (animateToEnd) 1f else 0f,
            animationSpec = tween(3000))
    }

    Column(modifier = Modifier.background(Color.White)) {

        val scene1 = MotionScene("""
            {
                ConstraintSets: {
                  start: {
                    grid1: { 
                        width: 200,
                        height: 200,
                        type: "grid",
                        orientation: 0,
                        rows: 0,
                        columns: 1,
                        hGap: 0,
                        vGap: 0,
                        spans: "",
                        skips: "",
                        rowWeights: "",
                        columnWeights: "",
                        contains: ["box1", "box2", "box3", "box4"],
                      }
                  },
                  end: {
                    grid2: { 
                        width: 200,
                        height: 200,
                        type: "grid",
                        orientation: 0,
                        rows: 1,
                        columns: 0,
                        hGap: 0,
                        vGap: 0,
                        spans: "",
                        skips: "",
                        rowWeights: "",
                        columnWeights: "",
                        contains: ["box1", "box2", "box3", "box4"],
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
            val numArray = arrayOf("box1", "box2", "box3", "box4")
            for (num in numArray) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .layoutId(num)
                        .testTag(num)
                        .background(Color.Red)
                )
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

@Preview(group = "wrapToSpreadGrid")
@Composable
public fun wrapToSpreadGridDemo() {
    var animateToEnd by remember { mutableStateOf(false) }

    val progress = remember { Animatable(0f) }
    val hGapVal = 10
    val vGapVal = 20

    LaunchedEffect(animateToEnd) {
        progress.animateTo(if (animateToEnd) 1f else 0f,
            animationSpec = tween(3000))
    }

    Column(modifier = Modifier.background(Color.White)) {

        val scene1 = MotionScene("""
            {
                ConstraintSets: {
                  start: {
                    grid1: { 
                        width: 200,
                        height: 200,
                        type: "grid",
                        orientation: 0,
                        rows: 2,
                        columns: 2,
                        hGap: 0,
                        vGap: 0,
                        spans: "",
                        skips: "",
                        rowWeights: "",
                        columnWeights: "",
                        contains: ["box1", "box2", "box3", "box4"],
                      }
                  },
                  end: {
                    grid2: { 
                        width: 200,
                        height: 200,
                        type: "grid",
                        orientation: 0,
                        rows: 2,
                        columns: 2,
                        hGap: $hGapVal,
                        vGap: $vGapVal,
                        spans: "",
                        skips: "",
                        rowWeights: "",
                        columnWeights: "",
                        contains: ["box1", "box2", "box3", "box4"],
                      },
                      box1: {
                        width: 'spread',
                        height: 'spread',
                      },
                      box2: {
                        width: 'spread',
                        height: 'spread',
                      },
                      box3: {
                        width: 'spread',
                        height: 'spread',
                      },
                      box4: {
                        width: 'spread',
                        height: 'spread',
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
            val numArray = arrayOf("box1", "box2", "box3", "box4")
            for (num in numArray) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .layoutId(num)
                        .testTag(num)
                        .background(Color.Red)
                )
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