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

package com.example.composemail.ui.newmail

import android.annotation.SuppressLint
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.*
import com.example.composemail.ui.theme.ComposeMailTheme
import kotlinx.coroutines.launch
import java.util.*
import kotlin.jvm.internal.Ref

@Preview
@Composable
fun NewMailSkeleton() {
    var nextState by remember { mutableStateOf<String?>(null) }
    val duration = remember { Ref.IntRef().apply { element = 500 } }
    val coroutineScope = rememberCoroutineScope()
    val onFinishedAnimation =
        {
            when (nextState) {
                null -> {
                    duration.element = 500
                    nextState = "minimizing"
                }
                "minimizing" -> {
                    duration.element = 1200
                    nextState = "minimized"
                }
                "minimized" -> {
                    duration.element = 1200
                    nextState = "fab"
                }
                "fab" -> {
                    duration.element = 1200
                    nextState = "expanded"
                }
                else -> {
                    duration.element = 500
                    nextState = null
                }
            }
        }
    Column(
        modifier = Modifier
            .size(300.dp, 500.dp)
            .background(Color.White),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Button(onClick = {
            coroutineScope.launch {
                onFinishedAnimation()
            }

        }) {
            Text(text = "Run")
        }
        MotionLayout(
            modifier = Modifier
                .background(Color.LightGray)
                .weight(1.0f, true)
                .fillMaxWidth(),
            animationSpec = tween(duration.element),
            debug = EnumSet.of(MotionLayoutDebugFlags.NONE),
            constraintSetName = nextState,
            finishedAnimationListener = onFinishedAnimation,
            motionScene = MotionScene("""
{
    ConstraintSets: {
      expanded : {
        container: {
          width: 'spread', height: 'spread',
          center: 'icon'
        },
        icon: {
          width: 'spread', height: 'spread',
          start: ['header', 'start', 0], end: ['close', 'end', 0],
          bottom: ['parent', 'bottom', 0], top: ['close', 'top', 0],
          alpha: 0.0,
        },
        close: {
          width: 40, height: 40,
          bottom: ['contentBox', 'top', 0],
          end: ['parent', 'end', 0]
        },
        minOrExpand: {
          width: 40, height: 40,
          top: ['close', 'top', 0],
          end: ['close', 'start', 16],
          rotationZ: 180
        },
        header: {
          width: 'spread', height: 'wrap',
          bottom: ['close', 'bottom', 0],
          start: ['parent', 'start', 0],
          end: ['minOrExpand', 'start', 16],
        },
        contentBox: {
          width: 'spread', height: 334,
          centerHorizontally: 'parent',
          bottom: ['parent', 'bottom', 0]
        }
      },
      minimizing: {
        Extends: 'expanded',
        close: {
          clear: ['constraints'],
          end: ['parent', 'end', 0],
          bottom: ['parent', 'bottom', 0]
        },
        minOrExpand: {
          clear: ['transforms'],
          rotationZ: 0
        },
        contentBox: {
          clear: ['constraints'],
          centerHorizontally: 'parent',
          top: ['parent', 'bottom', 16]
        }
      },
      minimized: {
        Extends: 'minimizing',
        header: {
          clear: ['constraints'],
          bottom: ['close', 'bottom', 0],
          end: ['minOrExpand', 'start', 16]
        },
      },
      fab: {
        Extends: 'minimized',
        icon: {
          clear: ['dimensions', 'constraints'],
          width: 50, height: 50,
          bottom: ['parent', 'bottom', 0],
          end: ['parent', 'end', 0],
          alpha: 1.0
        },
        close: {
          clear: ['constraints'],
          bottom: ['minOrExpand', 'bottom', 0],
          start: ['minOrExpand', 'end', 0]
        },
        minOrExpand: {
          clear: ['constraints'],
          top: ['header', 'top', 0],
          start: ['header', 'end', 16],
        },
        header: {
          clear: ['constraints'],
          bottom: ['parent', 'bottom', 0],
          start: ['parent', 'end', 0]
        },
      }
    },
    Transitions: {
      default: {
        from: 'expanded',
        to: 'minimizing'
      }
    }
}
""".trimIndent())
        ) {
            Box(modifier = Modifier
                .layoutId("icon")
                .background(Color.Cyan))
            Text(modifier = Modifier
                .layoutId("header")
                .background(Color.Blue), text = "MyText")
            Box(modifier = Modifier
                .layoutId("minOrExpand")
                .background(Color.Green))
            Box(modifier = Modifier
                .layoutId("close")
                .background(Color.Red))
            Box(modifier = Modifier
                .layoutId("contentBox")
                .background(Color(0x92FFEB3B)))
            Box(modifier = Modifier
                .layoutId("container")
                .background(Color(0x72FFAAFF)))
        }
    }
}