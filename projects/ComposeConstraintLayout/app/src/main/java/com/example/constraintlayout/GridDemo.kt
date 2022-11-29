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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.*


// test adding constraints to parent
@Preview(group = "grid")
@Composable
fun GridDemo1() {
    ConstraintLayout(
        ConstraintSet("""
        {
            grid: { 
                height: "parent",
                width: "parent",
                type: "Grid",
                margin: 20,
                orientation: 0,
                vGap: 25,
                hGap: 25,
                rows: 5,
                columns: 3,
                spans: "0:1x3",
                skips: "12:1x1",
                rowWeights: "3,2,2,2,2",
                contains: ["box", "btn1", "btn2", "btn3",
                  "btn4", "btn5", "btn6", "btn7", "btn8", "btn9","btn0"],
                top: ["parent", "top", 20],
                bottom: ["parent", "bottom", 20],
                right: ["parent", "right", 20],
                left: ["parent", "left", 20],
              },
             btn1: {
              height: "spread",
              width: "spread",
             },
             btn2: {
              height: "spread",
              width: "spread",
             },
             btn3: {
              height: "spread",
              width: "spread",
             },
             btn4: {
              height: "spread",
              width: "spread",
             },
             btn5: {
              height: "spread",
              width: "spread",
             },
             btn6: {
              height: "spread",
              width: "spread",
             },
             btn7: {
              height: "spread",
              width: "spread",
             },
             btn8: {
              height: "spread",
              width: "spread",
             },
             btn9: {
              height: "spread",
              width: "spread",
             },
             btn0: {
              height: "spread",
              width: "spread",
             },
             box: {
             height: "spread",
              width: "spread",
             }
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val numArray = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(String.format("btn%s", num)),
                onClick = {},
            ) {
                Text(text = num, fontSize = 45.sp)
            }
        }
        Box(
            modifier = Modifier.background(Color.Gray).layoutId("box"),
            Alignment.BottomEnd
        ) {
            Text("100", fontSize = 80.sp)
        }

    }
}

@Preview(group = "grid2")
@Composable
fun GridDemo2() {
    ConstraintLayout(
        ConstraintSet("""
        {
            grid: { 
                height: "parent",
                width: "parent",
                type: "Grid",
                vGap: 10,
                hGap: 10,
                orientation: 0,
                rows: 1,
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

@Preview(group = "grid3")
@Composable
fun GridDemo3() {
    ConstraintLayout(
        ConstraintSet("""
        {
            grid: { 
                height: "parent",
                width: "parent",
                type: "Grid",
                vGap: 10,
                hGap: 10,
                orientation: 0,
                columns: 1,
                contains: ["btn1", "btn2", "btn3", "btn4", "btn5"],
              },
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val numArray = arrayOf("1", "2", "3", "4", "5")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(String.format("btn%s", num)).width(120.dp),
                onClick = {},
            ) {
                Text(text = num, fontSize = 35.sp)
            }
        }
    }
}

@Preview(group = "grid4")
@Composable
fun GridDemo4() {
    ConstraintLayout(
        ConstraintSet("""
        {
            grid: { 
                height: "parent",
                width: "parent",
                type: "Grid",
                vGap: 10,
                hGap: 10,
                orientation: 0,
                rows: 1,
                contains: ["btn1", "btn2", "grid2", "btn3"],
              },
              grid2: { 
                height: "spread",
                width: "spread",
                vGap: 10,
                hGap: 10,
                type: "Grid",
                orientation: 1,
                columns: 1,
                contains: ["btn4", "grid3", "btn5"],
              },
              grid3: { 
                height: "spread",
                width: "spread",
                vGap: 10,
                hGap: 10,
                type: "Grid",
                orientation:0,
                rows: 1,
                contains: ["btn6", "btn7"],
              },
             btn1: {
              height: "spread",
              width: "spread",
             },
             btn2: {
              height: "spread",
              width: "spread",
             },
             btn3: {
              height: "spread",
              width: "spread",
             },
             btn4: {
              height: "spread",
              width: "spread",
             },
             btn5: {
              height: "spread",
              width: "spread",
             },
             btn6: {
              height: "spread",
              width: "spread",
             },
             btn7: {
              height: "spread",
              width: "spread",
             }
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val numArray = arrayOf("btn1", "btn2", "btn3", "btn4", "btn5", "btn6", "btn7")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(num),
                onClick = {},
            ) {
                Text(text = num)
            }
        }
    }
}

@Preview(group = "grid5")
@Composable
fun GridDemo5() {
    ConstraintLayout(
        ConstraintSet("""
        {
            grid: { 
                height: "parent",
                width: "parent",
                type: "Grid",
                vGap: 10,
                hGap: 10,
                orientation: 0,
                rows: 0,
                columns: 1,
                columnWeights: "",
                rowWeights: "",
                contains: ["btn1", "btn2", "btn3", "btn4"],
                top: ["parent", "top", 10],
                bottom: ["parent", "bottom", 20],
                right: ["parent", "right", 30],
                left: ["parent", "left", 40],
              },
             btn1: {
              height: "spread",
              width: "spread",
             },
             btn2: {
              height: "spread",
              width: "spread",
             },
             btn3: {
              height: "spread",
              width: "spread",
             },
             btn4: {
              height: "spread",
              width: "spread",
             }
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val numArray = arrayOf("1", "2", "3", "4")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(String.format("btn%s", num)).width(120.dp),
                onClick = {},
            ) {
                Text(text = num, fontSize = 35.sp)
            }
        }
    }
}

@Preview(group = "grid6")
@Composable
fun GridDemo6() {
    ConstraintLayout(
        ConstraintSet("""
        {
            grid: { 
                height: "parent",
                width: "parent",
                type: "Grid",
                orientation: 0,
                skips: "1:1x1,4:1x1,6:1x1",
                rows: 3,
                columns: 3,
                contains: ["grid2", "btn1", "btn2", "btn3", "btn4"],
              },
              grid2: { 
                height: "spread",
                width: "spread",
                type: "Grid",
                skips: "0:1x2,4:1x1,6:1x1",
                orientation: 0,
                rows: 3,
                columns: 3,
                contains: ["btn5", "btn6", "btn7", "btn8"],     
              },
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val numArray = arrayOf("1", "2", "3", "4", "5", "6", "7", "8")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(String.format("btn%s", num)).width(50.dp),
                onClick = {},
            ) {
                Text(text = num, fontSize = 20.sp)
            }
        }
    }
}
