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

@file:JvmName("DslVerificationKt")
@file:JvmMultifileClass

package com.example.constraintlayout.verification.dsl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.*

@Preview
@Composable
fun Test7() {
    val constraintSet = ConstraintSet(TwoBoxConstraintSet) {
        val box1 = createRefFor("box1")
        val box2 = createRefFor("box2")
        constrain(box1) {
            width = Dimension.fillToConstraints.atMost(20.dp)
            height = Dimension.fillToConstraints.atMost(30.dp)
        }
        constrain(box2) {
            width = Dimension.preferredWrapContent.atLeast(10.dp)
            height = Dimension.value(50.dp)
            centerHorizontallyTo(box1)
        }
    }
    Column {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = true),
            constraintSet = constraintSet
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Red)
                    .layoutId("box1")
            )
            Box(
                modifier = Modifier
                    .background(Color.Blue)
                    .layoutId("box2")
            )
        }
        Button(onClick = { }) {
            Text(text = "Run")
        }
    }
}

@Preview
@Composable
fun Test8() {
    val constraintSet = ConstraintSet(TwoBoxConstraintSet) {
        val box1 = createRefFor("box1")
        val box2 = createRefFor("box2")
        constrain(box1) {
            width = Dimension.preferredValue(30.dp)
            height = Dimension.preferredValue(30.dp)
        }
        constrain(box2) {
            width = Dimension.fillToConstraints.atLeastWrapContent
            height = Dimension.fillToConstraints.atMostWrapContent
            centerHorizontallyTo(box1)
        }
    }
    Column {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = true),
            constraintSet = constraintSet
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Red)
                    .layoutId("box1")
            )
            Box(
                modifier = Modifier
                    .background(Color.Blue)
                    .layoutId("box2")
            ) {
                Text(text = "Hello, World!")
            }
        }
        Button(onClick = { }) {
            Text(text = "Run")
        }
    }
}

@Preview
@Composable
fun Test9() {
    val constraintSet = ConstraintSet(TwoBoxConstraintSet) {
        val box1 = createRefFor("box1")
        val box2 = createRefFor("box2")
        constrain(box1) {
            width = Dimension.matchParent
            height = Dimension.percent(0.5f)
        }
        constrain(box2) {
            width = Dimension.fillToConstraints.atLeastWrapContent
            height = Dimension.fillToConstraints.atMostWrapContent
            centerHorizontallyTo(box1)
        }
    }
    Column {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = true),
            constraintSet = constraintSet
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Red)
                    .layoutId("box1")
            )
            Box(
                modifier = Modifier
                    .background(Color.Blue)
                    .layoutId("box2")
            ) {
                Text(text = "Hello, World!")
            }
        }
        Button(onClick = { }) {
            Text(text = "Run")
        }
    }
}