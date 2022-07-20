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

package com.example.constraintlayout.motion.dsl.transition

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.Transition
import com.example.constraintlayout.R

@Suppress("NOTHING_TO_INLINE")
@Composable
inline fun TwoItemLayout(transition: Transition) {
    var atStart by remember { mutableStateOf(true) }
    val progress by animateFloatAsState(if (atStart) 0f else 1f, tween(2000))

    val start = remember {
        ConstraintSet {
            val img1 = createRefFor("img1")
            val img2 = createRefFor("img2")
            constrain(img1) {
                width = Dimension.value(100.dp)
                height = Dimension.value(100.dp)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
            }
            constrain(img2) {
                width = Dimension.value(100.dp)
                height = Dimension.value(100.dp)
                start.linkTo(img1.end, 8.dp)
                bottom.linkTo(parent.bottom)
            }
        }
    }
    val end = remember {
        ConstraintSet {
            val img1 = createRefFor("img1")
            val img2 = createRefFor("img2")
            constrain(img2) {
                width = Dimension.value(100.dp)
                height = Dimension.value(100.dp)
                top.linkTo(parent.top)
                end.linkTo(parent.end)
            }
            constrain(img1) {
                width = Dimension.value(100.dp)
                height = Dimension.value(100.dp)
                top.linkTo(parent.top)
                end.linkTo(img2.start, 8.dp)
            }
        }
    }
    Column(Modifier.fillMaxSize()) {
        MotionLayout(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.0f, true),
            start = start,
            end = end,
            transition = transition,
            progress = progress
        ) {
            Image(
                modifier = Modifier.layoutId("img1"),
                painter = painterResource(id = R.drawable.intercom_snooze),
                contentDescription = null
            )
            Image(
                modifier = Modifier.layoutId("img2"),
                painter = painterResource(id = R.drawable.intercom_snooze),
                contentDescription = null
            )
        }
        Button(onClick = { atStart = !atStart }) {
            Text("Run")
        }
    }
}