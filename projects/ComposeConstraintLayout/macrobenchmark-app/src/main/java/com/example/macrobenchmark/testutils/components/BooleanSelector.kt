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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun BooleanSelectorPreview() {
    var myCondition by remember { mutableStateOf(true) }
    BooleanSelector(
        title = "Toggle",
        positiveSelectionTitle = "Positive",
        negativeSelectionTitle = "Negative",
        onSelection = { myCondition = it },
        selectionState = myCondition
    )
}

@Composable
fun BooleanSelector(
    title: String,
    positiveSelectionTitle: String,
    negativeSelectionTitle: String,
    onSelection: (Boolean) -> Unit,
    selectionState: Boolean
) {
    Column {
        Text(title)
        Row {
            RadioButton(
                modifier = Modifier.testTag(title + "_" + positiveSelectionTitle),
                selected = selectionState,
                onClick = { onSelection(true) }
            )
            Text(positiveSelectionTitle)
        }
        Row {
            RadioButton(
                modifier = Modifier.testTag(title + "_" + negativeSelectionTitle),
                selected = !selectionState,
                onClick = { onSelection(false) }
            )
            Text(negativeSelectionTitle)
        }
    }
}