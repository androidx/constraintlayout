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


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.*

@Preview(group = "row")
@Composable
fun RowDemo() {
    ConstraintLayout(
        ConstraintSet("""
        {
            grid: { 
                height: "parent",
                width: "parent",
                type: "Row",
                vGap: 10,
                hGap: 10,
                orientation: 0,
                contains: ["btn1", "btn2", "btn3", "btn4", "btn5"],
              },
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val numArray = arrayOf("1", "2", "3", "4", "5")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(String.format("btn%s", num)),
                onClick = {},
            ) {
                Text(text = num, fontSize = 35.sp)
            }
        }
    }
}

@Preview(group = "column")
@Composable
fun ColumnDemo() {
    ConstraintLayout(
        ConstraintSet("""
        {
            grid: { 
                height: "parent",
                width: "parent",
                type: "Column",
                vGap: 10,
                hGap: 10,
                orientation: 0,
                contains: ["btn1", "btn2", "btn3", "btn4", "btn5"],
              },
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val numArray = arrayOf("1", "2", "3", "4", "5")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(String.format("btn%s", num)),
                onClick = {},
            ) {
                Text(text = num, fontSize = 35.sp)
            }
        }
    }
}
