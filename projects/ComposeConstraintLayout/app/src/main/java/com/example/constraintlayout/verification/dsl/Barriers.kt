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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.example.constraintlayout.verification.dsl.DslVerification.TwoBoxConstraintSet

@Preview
@Composable
fun Test2() { // Vertical barrier
    val constraintSet = ConstraintSet(TwoBoxConstraintSet) {
        val box1 = createRefFor("box1")
        val box2 = createRefFor("box2")
        val box3 = createRefFor("box3")

        val barr = createBottomBarrier(box1, box2)

        constrain(box3) {
            width = Dimension.value(50.dp)
            height = Dimension.value(50.dp)
            centerHorizontallyTo(parent)
            top.linkTo(barr, 8.dp)
        }
    }

    Column {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = true), constraintSet = constraintSet
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
            Box(
                modifier = Modifier
                    .background(Color.Yellow)
                    .layoutId("box3")
            )
        }
        Button(onClick = { }) {
            Text(text = "Run")
        }
    }
}

@Preview
@Composable
fun Test3() { // Right barrier
    val constraintSet = ConstraintSet(TwoBoxConstraintSet) {
        val box1 = createRefFor("box1")
        val box2 = createRefFor("box2")
        val box3 = createRefFor("box3")
        val barr = createAbsoluteRightBarrier(box1, box2)

        constrain(box2) {
            centerHorizontallyTo(parent, bias = 0.2f)
        }

        constrain(box3) {
            width = Dimension.value(30.dp)
            height = Dimension.value(30.dp)
            top.linkTo(box1.top)
            start.linkTo(barr) // On RTL this will be the right
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = true), constraintSet = constraintSet
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
                Box(
                    modifier = Modifier
                        .background(Color.Yellow)
                        .layoutId("box3")
                )
            }
            Button(onClick = { }) {
                Text(text = "Run")
            }
        }
    }
}