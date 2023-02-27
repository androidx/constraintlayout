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
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.*

@Preview(group = "keypad")
@Composable
fun GridJsonKeypad() {
    ConstraintLayout(
        ConstraintSet("""
        {
            grid: { 
                height: "parent",
                width: "parent",
                type: "grid",
                vGap: 10,
                hGap: 10,
                rows: 5,
                columns: 3,
                spans: "0:1x3",
                skips: "12:1x1",
                rowWeights: "3,2,2,2,2",
                contains: ["box", "btn1", "btn2", "btn3",
                  "btn4", "btn5", "btn6", "btn7", "btn8", "btn9","btn0"],
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

@Preview(group = "calculator")
@Composable
fun GridJsonMediumCalculator() {
    ConstraintLayout(
        ConstraintSet("""
        {
            grid: { 
                height: "parent",
                width: "parent",
                type: "grid",
                vGap: 10,
                hGap: 10,
                rows: 7,
                columns: 4,
                spans: "0:2x4,24:1x2",
                contains: ["box", "btn_0","btn_clear","btn_neg","btn_percent","btn_div","btn_7",
                "btn_8","btn_9","btn_mult","btn_4","btn_5","btn_6","btn_sub","btn_1","btn_2","btn_3",
                "btn_plus","btn_dot","btn_equal"],
              },
             btn_0: {
              height: "spread",
              width: "spread",
             },
             btn_1: {
              height: "spread",
              width: "spread",
             },
             btn_2: {
              height: "spread",
              width: "spread",
             },
             btn_3: {
              height: "spread",
              width: "spread",
             },
             btn_4: {
              height: "spread",
              width: "spread",
             },
             btn_5: {
              height: "spread",
              width: "spread",
             },
             btn_6: {
              height: "spread",
              width: "spread",
             },
             btn_7: {
              height: "spread",
              width: "spread",
             },
             btn_8: {
              height: "spread",
              width: "spread",
             },
             btn_9: {
              height: "spread",
              width: "spread",
             },
             btn_clear: {
              height: "spread",
              width: "spread",
             },
             btn_neg: {
              height: "spread",
              width: "spread",
             },
             btn_percent: {
              height: "spread",
              width: "spread",
             },
             btn_div: {
              height: "spread",
              width: "spread",
             },
             btn_mult: {
              height: "spread",
              width: "spread",
             },
             btn_sub: {
              height: "spread",
              width: "spread",
             },
             box: {
              height: "spread",
              width: "spread",
             },
             btn_plus: {
              height: "spread",
              width: "spread",
             },
             btn_dot: {
              height: "spread",
              width: "spread",
             },
             btn_equal: {
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
        val numArray = arrayOf("0", "clear", "neg", "percent", "div", "7", "8", "9",
            "mult", "4", "5", "6", "sub", "1", "2", "3", "plus", "dot", "equal")
        val symbolMap = mapOf("clear" to "C", "neg" to "+/-", "percent" to "%", "div" to "/",
        "mult" to "*", "sub" to "-", "plus" to "+", "dot" to ".", "equal" to "=")
        var text = ""
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(String.format("btn_%s", num)),
                onClick = {},
            ) {
                text =  if (symbolMap.containsKey(num)) symbolMap[num].toString() else num
                Text(text = text, fontSize = 30.sp)
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

@Preview(group = "row")
@Composable
fun GridJsonRow() {
    ConstraintLayout(
        ConstraintSet("""
        {
            row: { 
                height: "parent",
                width: "parent",
                type: "row",
                hGap: 10,
                contains: ["btn0", "btn1", "btn2", "btn3", "btn4"],
              }
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val numArray = arrayOf("0", "1", "2", "3", "4")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(String.format("btn%s", num)),
                onClick = {},
            ) {
                Text(text = num, fontSize = 30.sp)
            }
        }
    }
}

@Preview(group = "column")
@Composable
fun GridJsonColumn() {
    ConstraintLayout(
        ConstraintSet("""
        {
            column: { 
                height: "parent",
                width: "parent",
                type: "column",
                vGap: 10,
                contains: ["btn0", "btn1", "btn2", "btn3", "btn4"],
              }
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val numArray = arrayOf("0", "1", "2", "3", "4")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(String.format("btn%s", num)),
                onClick = {},
            ) {
                Text(text = num, fontSize = 30.sp)
            }
        }
    }
}

@Preview(group = "nested")
@Composable
fun GridJsonNested() {
    ConstraintLayout(
        ConstraintSet("""
        {
            grid: { 
                height: "parent",
                width: "parent",
                type: "grid",
                skips: "1:1x1,4:1x1,6:1x1",
                rows: 3,
                columns: 3,
                contains: ["grid2", "btn1", "btn2", "btn3", "btn4"],
              },
              grid2: { 
                height: "spread",
                width: "spread",
                type: "grid",
                skips: "0:1x2,4:1x1,6:1x1",
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

@Preview(group = "cinr")
@Composable
fun GridJsonColumnInRow() {
    ConstraintLayout(
        ConstraintSet("""
        {
            row: { 
                height: "parent",
                width: "parent",
                type: "row",
                hGap: 10,
                contains: ["btn0","column", "btn1", "btn2", "btn3"],
              },
              column: { 
                height: "spread",
                width: "spread",
                type: "column",
                vGap: 10,
                contains: ["btn4", "btn5", "btn6"],     
              },
              btn0: {
              height: "spread",
              width: "spread",
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
             }
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val numArray = arrayOf("0", "1", "2", "3", "4", "5", "6")
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