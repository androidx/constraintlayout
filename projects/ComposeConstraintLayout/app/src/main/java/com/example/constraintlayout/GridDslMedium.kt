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
public fun GridDslKeypad() {
    // Currently, we still have problem with positioning the Flow Helper
    // and/or setting the width/height properly.
    ConstraintLayout(
        ConstraintSet {
            val btn1 = createRefFor("btn1")
            val btn2 = createRefFor("btn2")
            val btn3 = createRefFor("btn3")
            val btn4 = createRefFor("btn4")
            val btn5 = createRefFor("btn5")
            val btn6 = createRefFor("btn6")
            val btn7 = createRefFor("btn7")
            val btn8 = createRefFor("btn8")
            val btn9 = createRefFor("btn9")
            val btn0 = createRefFor("btn0")
            val box = createRefFor("box")

            val weights = intArrayOf(3, 2, 2, 2)

            val g1 = createGrid(
                box, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn0,
                rows = 5,
                columns = 3,
                verticalGap = 25.dp,
                horizontalGap = 25.dp,
                spans = arrayOf(Span(0, 1, 3)),
                skips = arrayOf(Skip(12, 1, 1)),
                rowWeights = weights,
            )

            constrain(g1) {
                width = Dimension.matchParent
                height = Dimension.matchParent
                top.linkTo(parent.top, 20.dp)
                bottom.linkTo(parent.bottom, 20.dp)
                start.linkTo(parent.start, 20.dp)
                end.linkTo(parent.end, 20.dp)
            }
            constrain(btn1) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(btn2) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(btn3) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(btn4) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(btn5) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(btn6) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(btn7) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(btn8) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(btn9) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(btn0) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(box) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
        },

        modifier = Modifier.fillMaxSize()
    ) {
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
public fun GridDslMediumCalculator() {
    val numArray = arrayOf("0", "clear", "neg", "percent", "div", "7", "8", "9",
        "mult", "4", "5", "6", "sub", "1", "2", "3", "plus", "dot", "equal")
    ConstraintLayout(
        ConstraintSet {
            val elem = Array(numArray.size + 1) { i -> createRefFor(i) }
            elem[0] = createRefFor("box")
            for (i in numArray.indices) {
                elem[i + 1] = createRefFor(String.format("btn_%s", numArray[i]))
            }
            val g1 = createGrid(
                elements = *elem,
                rows = 7,
                columns = 4,
                verticalGap = 10.dp,
                horizontalGap = 10.dp,
                spans = arrayOf(Span(0, 2, 4), Span(24, 1, 2)),
            )

            constrain(g1) {
                width = Dimension.matchParent
                height = Dimension.matchParent
                top.linkTo(parent.top, 20.dp)
                bottom.linkTo(parent.bottom, 20.dp)
                start.linkTo(parent.start, 20.dp)
                end.linkTo(parent.end, 20.dp)
            }
            for (e in elem) {
                constrain(e) {
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
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
public fun GridDslMediumRow() {
    val numArray = arrayOf("0", "1", "2", "3", "4")
    ConstraintLayout(
        ConstraintSet {
            val elem = Array(numArray.size) { i -> createRefFor(i) }
            for (i in numArray.indices) {
                elem[i] = createRefFor(String.format("btn_%s", numArray[i]))
            }
            val g1 = createRow(
                elements = *elem,
                horizontalGap = 10.dp,
            )

            constrain(g1) {
                width = Dimension.matchParent
                height = Dimension.matchParent
                top.linkTo(parent.top, 20.dp)
                bottom.linkTo(parent.bottom, 20.dp)
                start.linkTo(parent.start, 20.dp)
                end.linkTo(parent.end, 20.dp)
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(String.format("btn_%s", num)),
                onClick = {},
            ) {
                Text(text = num, fontSize = 30.sp)
            }
        }
    }
}

@Preview(group = "column")
@Composable
public fun GridDslMediumColumn() {
    val numArray = arrayOf("0", "1", "2", "3", "4")
    ConstraintLayout(
        ConstraintSet {
            val elem = Array(numArray.size) { i -> createRefFor(i) }
            for (i in numArray.indices) {
                elem[i] = createRefFor(String.format("btn_%s", numArray[i]))
            }
            val g1 = createColumn(
                elements = *elem,
                verticalGap = 10.dp,
            )

            constrain(g1) {
                width = Dimension.matchParent
                height = Dimension.matchParent
                top.linkTo(parent.top, 20.dp)
                bottom.linkTo(parent.bottom, 20.dp)
                start.linkTo(parent.start, 20.dp)
                end.linkTo(parent.end, 20.dp)
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(String.format("btn_%s", num)),
                onClick = {},
            ) {
                Text(text = num, fontSize = 30.sp)
            }
        }
    }
}

@Preview(group = "nested")
@Composable
public fun GridDslMediumNested() {
    ConstraintLayout(
        ConstraintSet {
            val btn1 = createRefFor("btn1")
            val btn2 = createRefFor("btn2")
            val btn3 = createRefFor("btn3")
            val btn4 = createRefFor("btn4")
            val btn5 = createRefFor("btn5")
            val btn6 = createRefFor("btn6")
            val btn7 = createRefFor("btn7")
            val btn8 = createRefFor("btn8")
            val g1 = createGrid(
                btn5, btn6, btn7, btn8,
                rows = 3,
                columns = 3,
                skips = arrayOf(Skip(0, 1, 2), Skip(4, 1, 1), Skip(6, 1, 1))
            )

            val g2 = createGrid(
                g1, btn1, btn2, btn3, btn4,
                rows = 3,
                columns = 3,
                skips = arrayOf(Skip(1, 1, 1), Skip(4, 1, 1), Skip(6, 1, 1))
            )

            constrain(g1) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(g2) {
                width = Dimension.matchParent
                height = Dimension.matchParent
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        val numArray = arrayOf("1", "2", "3", "4", "5", "6", "7", "8")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(String.format("btn%s", num)).width(40.dp),
                onClick = {},
            ) {
                Text(text = num, fontSize = 15.sp)
            }
        }
    }
}

@Preview(group = "cinr")
@Composable
public fun GridDslColumnInRow() {
    ConstraintLayout(
        ConstraintSet {
            val btn0 = createRefFor("btn0")
            val btn1 = createRefFor("btn1")
            val btn2 = createRefFor("btn2")
            val btn3 = createRefFor("btn3")
            val btn4 = createRefFor("btn4")
            val btn5 = createRefFor("btn5")
            val btn6 = createRefFor("btn6")
            val column = createColumn(
                btn4, btn5, btn6,
                verticalGap = 10.dp
            )
            val row = createRow(
                btn0, column, btn1, btn2, btn3,
                horizontalGap = 10.dp,
            )

            constrain(column) {
                width = Dimension.fillToConstraints
                height = Dimension.matchParent
            }
            constrain(row) {
                width = Dimension.matchParent
                height = Dimension.matchParent
            }
            constrain(btn0) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(btn1) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(btn2) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(btn3) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(btn4) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(btn5) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(btn6) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        val numArray = arrayOf("0", "1", "2", "3", "4", "5", "6")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(String.format("btn%s", num)),
                onClick = {},
            ) {
                Text(text = num, fontSize = 40.sp)
            }
        }
    }
}