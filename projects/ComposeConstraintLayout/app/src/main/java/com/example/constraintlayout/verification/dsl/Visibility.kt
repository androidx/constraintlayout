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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Visibility

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
    Column {
        ConstraintLayout(modifier = Modifier
            .fillMaxWidth()
            .weight(1f, fill = true), constraintSet = constraintSet) {
            Box(modifier = Modifier
                .background(Color.Red)
                .layoutId("box1"))
            Box(modifier = Modifier
                .background(Color.Blue)
                .layoutId("box2"))
        }
        Button(onClick = { hide = !hide }) {
            Text(text = "Run")
        }
    }
}