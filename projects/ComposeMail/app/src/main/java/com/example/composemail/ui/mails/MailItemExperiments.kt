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

package com.example.composemail.ui.mails

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionLayoutDebugFlags
import androidx.constraintlayout.compose.MotionScene
import java.util.*

@Preview
@Composable
fun SimpleMotionLayoutMail() {
    var inLoading by remember { mutableStateOf(true) }

    // For Animation Preview tool
    val transition = updateTransition(targetState = inLoading, label = "MailAnimation")
    val progress by transition.animateFloat(
        label = "progress",
        transitionSpec = { tween(1500) },
        targetValueByState = { if (it) 0f else 1f })
    Column(
        modifier = Modifier.size(300.dp, 150.dp).background(Color.White),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Button(onClick = { inLoading = !inLoading }) {
            Text(text = "Run")
        }
        MotionLayout(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            debug = EnumSet.of(MotionLayoutDebugFlags.SHOW_ALL),
            motionScene = MotionScene("""
{
  ConstraintSets: {
    normal: {
      picture: {
        width: 60, height: 60,
        centerVertically: 'parent',
        start: ['parent', 'start', 0]
      },
      content: {
        width: 'spread', height: 60,
        top: ['picture', 'top', 0],
        start: ['picture', 'end', 8], end: ['parent', 'end', 8],
      },
      loading: {
        width: 60, height: 60,
        centerVertically: 'parent',
        end: ['parent', 'start', 32]
      }
    },
    empty: {
      picture: {
        width: 60, height: 60,
        top: ['content', 'top', 0],
        start: ['parent', 'end', 8]
      },
      content: {
        width: 120, height: 60,
        centerVertically: 'parent',
        start: ['picture', 'end', 32],
      },
      loading: {
        width: 60, height: 60,
        center: 'parent',
      }
    }
  },
  Transitions: {
    default: {
      from: 'empty',
      to: 'normal',
      KeyFrames: {
        KeyAttributes: [
          {
            target: ['picture'],
            frames: [5, 50, 75],
            scaleX: [0.2, 1.3, 1.1],
            scaleY: [0.2, 1.3, 1.1],
          }
        ],
        KeyPositions: [
          {
            target: ['loading'],
            frames: [10],
            percentX: [0.15],
          }
        ],
      }
    }
  }
}
""".trimIndent()),
            progress = progress,
        ) {
            Box(modifier = Modifier.layoutId("picture").background(Color.Cyan))
            Box(modifier = Modifier.layoutId("content").background(Color.LightGray))
            Box(modifier = Modifier.layoutId("loading").background(Color.Red))
        }
    }
}

@Preview
@Composable
fun MotionLayoutMailWithFlip() {
    var nextState by remember { mutableStateOf<String?>("empty") }
    Column(
        modifier = Modifier.size(300.dp, 150.dp).background(Color.White),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Button(onClick = {
            nextState = if (nextState == "empty") "normal" else "empty"
        }) {
            Text(text = "Run")
        }
        MotionLayout(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            debug = EnumSet.of(MotionLayoutDebugFlags.SHOW_ALL),
            constraintSetName = nextState,
            motionScene = MotionScene("""
{
  ConstraintSets: {
    normal: {
      picture: {
        width: 60, height: 60,
        centerVertically: 'parent',
        start: ['parent', 'start', 0],
      },
      check: {
        width: 60, height: 60,
        centerVertically: 'parent',
        start: ['parent', 'start', 0],
        rotationY: 180,
        alpha: 0.0
      },
      content: {
        width: 'spread', height: 60,
        top: ['picture', 'top', 0],
        start: ['picture', 'end', 8], end: ['parent', 'end', 8],
      },
      loading: {
        width: 60, height: 60,
        centerVertically: 'parent',
        end: ['parent', 'start', 32]
      }
    },
    flipped: {
      Extends: 'normal',
      picture: {
        rotationY: -180,
        alpha: 0.0
      },
      check: {
        rotationY: 0,
        alpha: 1.0
      }
    },
    empty: {
      picture: {
        width: 60, height: 60,
        top: ['content', 'top', 0],
        start: ['parent', 'end', 8]
      },
      check: {
        width: 60, height: 60,
        top: ['content', 'top', 0],
        start: ['parent', 'end', 8],
        rotationY: 180,
        alpha: 0.0
      },
      content: {
        width: 120, height: 60,
        centerVertically: 'parent',
        start: ['picture', 'end', 32],
      },
      loading: {
        width: 60, height: 60,
        center: 'parent',
      }
    }
  },
  Transitions: {
    default: {
      from: 'empty',
      to: 'normal',
    }
  }
}
""".trimIndent())
        ) {
            Box(modifier = Modifier.layoutId("picture").background(Color.Cyan).clickable {
                nextState = if (nextState == "normal") "flipped" else "normal"
            })
            Box(modifier = Modifier.layoutId("check").background(Color.Green))
            Box(modifier = Modifier.layoutId("content").background(Color.LightGray))
            Box(modifier = Modifier.layoutId("loading").background(Color.Red))
        }
    }
}