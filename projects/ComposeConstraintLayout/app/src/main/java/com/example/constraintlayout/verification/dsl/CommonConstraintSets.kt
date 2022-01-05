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
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension

@Suppress("NOTHING_TO_INLINE")
object DslVerification {
    internal val TwoBoxConstraintSet = ConstraintSet {
        val box1 = createRefFor("box1")
        val box2 = createRefFor("box2")
        constrain(box1) {
            centerTo(parent)
            width = Dimension.value(30.dp)
            height = Dimension.value(100.dp)
        }
        constrain(box2) {
            width = Dimension.value(50.dp)
            height = Dimension.value(50.dp)
            centerHorizontallyTo(parent)
            top.linkTo(box1.bottom, 8.dp)
        }
    }


    @Composable
    internal inline fun TwoBoxLayout(
        constraintSet: ConstraintSet,
        noinline content: @Composable () -> Unit = {}
    ) {
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
            content()
        }
    }
}