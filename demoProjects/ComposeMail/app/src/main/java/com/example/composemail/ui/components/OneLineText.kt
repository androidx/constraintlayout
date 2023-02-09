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

package com.example.composemail.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * [Text] Composable constrained to one line with default Clip overflow behavior for better
 * animation performance.
 */
@Composable
fun OneLineText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    overflow: TextOverflow = TextOverflow.Clip
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        style = style,
        maxLines = 1,
        overflow = overflow,
    )
}

@Preview
@Composable
private fun OneLineTextPreview() {
    Column(Modifier.fillMaxSize()) {
        Text(text = "Normal")
        Column(
            Modifier
                .width(40.dp)
                .background(Color.LightGray)
        ) {
            Text(text = "Hello \nWorld!")
            Text(text = "This is a very very long text")
        }
        Text(text = "Cheap")
        Column(
            Modifier
                .width(40.dp)
                .background(Color.LightGray)
        ) {
            OneLineText(text = "Hello \nWorld!")
            OneLineText(text = "This is a very very long text")
        }
    }
}