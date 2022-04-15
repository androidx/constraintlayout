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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import androidx.constraintlayout.compose.layoutId

@Preview
@Composable
fun OnSwipeExperiment() {
    var progress by remember {
        mutableStateOf(0.0f)
    }
    var mode by remember {
        mutableStateOf("spring")
    }
    val motionSceneContent = remember(mode) {
        // language=json5
        """
       {
         ConstraintSets: {
           start: {
             box: {
               width: 50, height: 50,
               bottom: ['parent', 'bottom', 10],
               start: ['parent', 'start', 10]
             }
           },
           end: {
             Extends: 'start',
             box: {
               clear: ['constraints'],
               top: ['parent', 'top', 10],
               end: ['parent', 'end', 10],
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
                side: 'end',
                mode: '$mode'
              }
           }
         }
       }
        """.trimIndent()
    }
    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = {
                // OnSwipe does not update our progress, need a work around to reset
                progress = 0.1f
                progress = 0f
            }) {
                Text(text = "Reset")
            }
            Button(onClick = {
                mode = when (mode) {
                    "spring" -> "linear"
                    else -> "spring"
                }
            }) {
                Text(text = "Change Mode")
            }
            Text(text = "Current: $mode")
        }
        MotionLayout(
            modifier = Modifier.fillMaxSize(),
            motionScene = MotionScene(content = motionSceneContent),
            progress = progress
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Red)
                    .layoutId("box")
            )
        }
    }
}