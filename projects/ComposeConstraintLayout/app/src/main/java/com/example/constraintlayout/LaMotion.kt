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

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Motion
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionLayoutDebugFlags
import androidx.constraintlayout.compose.MotionScene
import androidx.constraintlayout.compose.RelativePosition
import androidx.constraintlayout.compose.rememberMotionContent
import java.util.*
import kotlin.collections.ArrayList

@Preview(group = "LookAheadMotion")
@Composable
public fun LaMotion01() {
    var vert: Boolean by remember { mutableStateOf(true) }
    var words = "This is a test of motionLayout".split(' ')
    val animationSpec: AnimationSpec<Float> = tween(2000)
    val s = rememberMotionContent {
        for (word in words) {
            Text(modifier = Modifier
                .padding(2.dp)
                .motion(animationSpec) {
                    keyPositions {
                        frame(50f) {
                            type = RelativePosition.Delta
                            percentX = if (vert) 0f else 1.0f

                        }
                    }
                }, text = word)
        }
    }

    Motion(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()) {
        Column(modifier = Modifier.background(Color.LightGray)) {
            Button(onClick = { vert = !vert }) {
                Text(modifier = Modifier.motion(), text = "Click")
            }
            if (vert) {
                Row() {
                    s()
                }
            } else Column() {
                s()
            }


        }
    }

   }


