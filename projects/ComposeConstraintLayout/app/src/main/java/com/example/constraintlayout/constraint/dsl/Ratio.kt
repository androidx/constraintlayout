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

package com.example.constraintlayout.constraint.dsl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension

@Preview
@Composable
private fun RatioExamplesInDsl() {
    val constraintSet = ConstraintSet {
        constrain(createRefFor("box1")) {
            centerTo(parent)
            width = Dimension.value(100.dp)
            height = Dimension.ratio("4:2")
        }
    }
    ConstraintLayout(
        modifier = Modifier
            .background(Color.Blue)
            .fillMaxSize(),
        constraintSet = constraintSet
    ) {
        Spacer(
            modifier = Modifier
                .background(Color.Red)
                .layoutId("box1"),
        )
    }
}