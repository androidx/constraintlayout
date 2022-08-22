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

package com.example.macrobenchmark.testutils.components

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

/**
 * Button with text.
 *
 * [text] is also applied to [Modifier.testTag] so that it's addressable by UI Automator.
 */
@Suppress("NOTHING_TO_INLINE") // Composable wrapper, inline simplifies recomposition
@Composable
internal inline fun TestableButton(
    noinline onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
) {
    Button(
        modifier = modifier.testTag(text),
        onClick = onClick
    ) {
        Text(text = text)
    }
}