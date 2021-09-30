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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

@Preview(group = "motion_2")
@Composable
public fun MotionDemo2() {
    var animateToEnd by remember { mutableStateOf(false) }

    val progress  = remember { Animatable(0f) }

    LaunchedEffect(animateToEnd) {
        progress.animateTo(if (animateToEnd) 1f else 0f,
        animationSpec = tween(3000))
    }

    Column( modifier = Modifier.background(Color.White)) {

        val scene1 = MotionScene("""
            {
                Header: {
                  name: 'motion26'
                },
                
                ConstraintSets: {
                  start: {
                    id1: {
                      width: 40, height: 40,
                      start:  ['parent', 'start' , 16],
                      bottom: ['parent', 'bottom', 16]
                    }
                  },
                  
                  end: {
                    id1: {
                      width: 40, height: 40,
                      end: ['parent', 'end', 16],
                      top: ['parent', 'top', 16]
                    }
                  }
                },
                
                Transitions: {
                  default: {
                    from: 'start',   to: 'end',
                  }
                }
            }
            """)

        MotionLayout(
            modifier    = Modifier
                .fillMaxWidth()
                .height(400.dp),
            motionScene = scene1,
            progress   = progress.value) {

            Box(modifier = Modifier
                .layoutId("id1")
                .background(Color.Red))
        }

        Button(onClick = { animateToEnd = !animateToEnd },
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp)) {
            Text(text = "Run")
        }
    }
}
