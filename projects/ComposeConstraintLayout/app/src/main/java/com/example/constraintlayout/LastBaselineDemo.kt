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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.*

@Preview(group = "baseline")
@Composable
fun LastBaselineDSL() {
    ConstraintLayout(
        ConstraintSet {
            val a = createRefFor("num1")
            val b = createRefFor("num2")
            constrain(a) {
                start.linkTo(parent.start)
                end.linkTo(b.start)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
            constrain(b) {
                top.linkTo(a.lastBaseline)
                start.linkTo(a.end)
                end.linkTo(parent.end)
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            modifier = Modifier.layoutId("num1").width(100.dp),
            onClick = {},
        ) {
            Text(text = String.format("Text-%s", 1), fontSize = 30.sp)
        }

        Button(
            modifier = Modifier.layoutId("num2").size(200.dp),
            onClick = {},
        ) {
            Text(text = String.format("Text-%s", 2), fontSize = 30.sp)
        }
    }
}

@Preview(group = "baseline1")
@Composable
fun LastBaselineJSON() {
    ConstraintLayout(
        androidx.constraintlayout.compose.ConstraintSet(
            """
        {
            num1 : {
              left: ['parent', 'left'],
              right: ['num2', 'left'],
              lastBaseline: ['num2', 'top']
            },
            num2 : {
              top: ['parent', 'top'],
              bottom: ['parent', 'bottom'],
              left: ['num1', 'right'],
              right: ['parent', 'right'],
            }

        }
        """.trimIndent()
        ),
        modifier = Modifier.fillMaxSize()) {
        val numArray = arrayOf("1", "2")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId("num$num").width(100.dp),
                onClick = {},
            ) {
                Text(text = String.format("Text-%s", num), fontSize = 30.sp)
            }
        }
    }
}