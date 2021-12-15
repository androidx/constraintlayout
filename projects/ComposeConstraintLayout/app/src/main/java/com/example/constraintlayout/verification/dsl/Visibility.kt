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

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Visibility
import com.example.constraintlayout.verification.dsl.DslVerification.TwoBoxConstraintSet
import com.example.constraintlayout.verification.dsl.DslVerification.TwoBoxLayout

@Preview
@Composable
fun Test() {
    var hide by remember { mutableStateOf(true) }
    val constraintSet = ConstraintSet(TwoBoxConstraintSet) {
        val box1 = createRefFor("box1")
        constrain(box1) {
            alpha = 0.5f
            visibility = if (hide) Visibility.Gone else Visibility.Visible
        }
    }

    TwoBoxLayout(constraintSet) {
        Button(onClick = { hide = !hide }) {
            Text(text = "Run")
        }
    }
}

@Preview
@Composable
fun Test15() {
    var hide by remember { mutableStateOf(true) }
    val constraintSet = ConstraintSet(TwoBoxConstraintSet) {
        val box1 = createRefFor("box1")
        val box2 = createRefFor("box2")
        constrain(box1) {
            alpha = 0.5f
            visibility = if (hide) Visibility.Gone else Visibility.Visible
        }
        constrain(box2) {
            // Due to gone margin, the box should be off-center when box1 is Gone
            top.linkTo(box1.bottom, margin = 8.dp, goneMargin = 100.dp)
        }
    }
    TwoBoxLayout(constraintSet) {
        Button(onClick = { hide = !hide }) {
            Text(text = "Run")
        }
    }
}