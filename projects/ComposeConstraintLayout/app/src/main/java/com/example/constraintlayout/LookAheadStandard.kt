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

package com.example.constraintlayout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LookaheadLayout
import androidx.compose.ui.layout.LookaheadLayoutScope
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun StandardLookAheadLayout(
    modifier: Modifier = Modifier,
    content: @Composable LookaheadLayoutScope.() -> Unit
) {
    LookaheadLayout(
        content = content,
        measurePolicy = remember {
            MeasurePolicy { measurables, constraints ->
                val placeables = measurables.map { it.measure(constraints) }
                val maxWidth: Int = placeables.maxOf { it.width }
                val maxHeight = placeables.maxOf { it.height }
                // Position the children.
                layout(maxWidth, maxHeight) {
                    placeables.forEach {
                        it.place(0, 0)
                    }
                }
            }
        },
        modifier = modifier
    )
}