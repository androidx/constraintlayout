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

package com.example.constraintlayout.issues

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionLayoutDebugFlags
import androidx.constraintlayout.compose.MotionLayoutState
import androidx.constraintlayout.compose.MotionScene
import androidx.constraintlayout.compose.rememberMotionLayoutState

/**
 * [Github: 507](https://github.com/androidx/constraintlayout/issues/507)
 *
 * There seems to be two issues while animating the text that results in the clipping.
 * - Text bounds not calculated properly during animation
 * - At some point during the animation the location of the starting position seems to collapse
 * to 0,0 instead of staying around the middle of the layout
 */
@Preview
@Composable
private fun Issue507Preview() {
    var toEnd by remember { mutableStateOf(false) }
    val motionState = rememberMotionLayoutState(initialDebugMode = MotionLayoutDebugFlags.SHOW_ALL)
    motionState.animateTo(if (toEnd) 1f else 0f, tween(2500))
    Column {
        Button(onClick = {
            toEnd = !toEnd
        }) {
            Text(text = "Run")
        }
        Issue507(motionState = motionState)
    }
}


@OptIn(ExperimentalMotionApi::class)
@Composable
fun Issue507(motionState: MotionLayoutState) {
    MotionLayout(
        modifier = Modifier
            .background(Color.LightGray)
            .fillMaxWidth()
            .height(lerp(150.dp, 50.dp, motionState.currentProgress)),
        motionScene = MotionScene(
            content = """
                {
                  ConstraintSets: {
                    start: {
                        title: {
                            top: ['parent', 'top'],
                            start: ['parent', 'start'],
                            end: ['parent', 'end'],
                            bottom: ['options', 'top', 24],
                            custom: {
                                fontSize: 32,
                                fontWeight: 400
                            }
                        },
                        options: { 
                            end: ['parent', 'end', 0],
                            bottom: ['content', 'top', 15]
                        },
                        content: {
                            width: 'spread', height: 'wrap',
                            start: ['parent', 'start'],
                            end: ['parent', 'end'],
                            bottom: ['parent', 'bottom', 0]
                        }
                    },
                    end: {
                        title: {                            
                            top: ['parent', 'top', 0],
                            start: ['parent', 'start', 8],
                            bottom: ['content', 'top', 0],
                            custom: {
                                fontSize: 20,
                                fontWeight: 500
                            }
                        },
                        options: { 
                            end: ['parent', 'end', 0],
                            bottom: ['content', 'top', 0],
                            top: ['parent', 'top', 0]
                        },
                        content: {
                            width: 'spread', height: 'wrap',
                            start: ['parent', 'start'],
                            end: ['parent', 'end'],
                            bottom: ['parent', 'bottom', 0]
                        }
                    }
                  },
                  Transitions: {
                    default: {
                      from: 'start',
                      to: 'end'
                    }
                  }
                }
            """.trimIndent()
        ),
        motionLayoutState = motionState
    ) {
        Text(
            text = "This is a very long text",
            modifier = Modifier
                .layoutId("title")
                .wrapContentWidth(unbounded = true), // Seems to measure the text more accurately
            color = MaterialTheme.colors.onSurface,
            maxLines = 1, // maxLines and no softWrap help a bit with clipping
            softWrap = false,
            fontWeight = FontWeight(motionProperties("title").value.int("fontWeight")),
            fontSize = motionProperties("title").value.fontSize("fontSize")
        )
        Options(
            modifier = Modifier
                .layoutId("options")
                .background(Color.Blue)
        )
        Tabs(
            Modifier
                .layoutId("content")
                .background(Color.Red)
        )
    }
}

@Composable
private fun Options(modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Icon(imageVector = Icons.Default.Settings, contentDescription = null)
        Icon(imageVector = Icons.Default.Notifications, contentDescription = null)
        Icon(imageVector = Icons.Default.Filter, contentDescription = null)
        Icon(imageVector = Icons.Default.Search, contentDescription = null)
    }
}


@Composable
private fun Tabs(modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceEvenly) {
        Text(text = "All")
        Text(text = "My")
    }
}