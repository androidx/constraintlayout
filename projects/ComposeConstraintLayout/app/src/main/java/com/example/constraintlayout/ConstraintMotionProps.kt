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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionLayoutDebugFlags
import androidx.constraintlayout.compose.MotionScene
import androidx.constraintlayout.compose.layoutId
import java.util.*

@Preview
@Composable
fun MotionInConstraints() {

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
            modifier = Modifier.fillMaxSize().background(Color.LightGray),
            motionScene = MotionScene(content = scene),
            debug= EnumSet.of(MotionLayoutDebugFlags.SHOW_ALL),
        ) {
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

