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

@file:JvmName("MotionDslVerificationKt")
@file:JvmMultifileClass

package com.example.dsl_verification.motion

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintSet

/**
 * Text that resizes itself should have correct dimensions during animation
 */
@Preview
@Composable
fun Test1() {
    var animateToEnd by remember { mutableStateOf(false) }
    var textValue by remember { mutableStateOf("My Text") }

    val progress by animateFloatAsState(targetValue = if (animateToEnd) 1.0f else 0.0f, tween(3000))

    val start = remember {
        ConstraintSet {
            val text = createRefFor("text")
            constrain(text) {
                start.linkTo(parent.start, 10.dp)
                bottom.linkTo(parent.bottom, 10.dp)
            }
        }
    }
    val end = remember {
        ConstraintSet {
            val text = createRefFor("text")
            constrain(text) {
                end.linkTo(parent.end, 10.dp)
                top.linkTo(parent.top, 10.dp)
            }
        }
    }
    val onClick = { textValue += " appended" }

    Column {
        Row {
            Button(onClick = { animateToEnd = !animateToEnd }) {
                Text(text = "Run")
            }
            Divider(Modifier.width(10.dp))
            Button(onClick = onClick) {
                Text(text = "Recompose Text")
            }
        }
        MotionTestWrapper(
            modifier = Modifier.fillMaxSize(),
            start = start,
            end = end,
            progress = progress,
            onRecompose = onClick
        ) {
            Text(text = textValue, Modifier.layoutId("text"))
        }
    }
}
