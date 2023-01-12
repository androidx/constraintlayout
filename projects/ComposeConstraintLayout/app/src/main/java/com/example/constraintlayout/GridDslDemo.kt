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

package com.example.constraintlayout


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutBaseScope
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.FlowStyle
import androidx.constraintlayout.compose.LayoutReference
import androidx.constraintlayout.compose.Wrap
import java.util.Arrays

@Preview(group = "grid1")
@Composable
public fun GridDslDemo1() {
    // Currently, we still have problem with positioning the Flow Helper
    // and/or setting the width/height properly.
    ConstraintLayout(

        ConstraintSet {
            val a = createRefFor("btn1")
            val b = createRefFor("btn2")
            val c = createRefFor("btn3")
            val d = createRefFor("btn4")
            val e = createRefFor("btn5")
            val f = createRefFor("btn6")
            val g = createRefFor("btn7")
            val h = createRefFor("btn8")
            val i = createRefFor("btn9")
            val j = createRefFor("btn0")
            val k = createRefFor("box")

            val g1 = createGrid(
                k, a, b, c, d, e, f, g, h, i, j, k,
                rows = 5,
                columns = 3,
                verticalGap = 25.dp,
                horizontalGap = 25.dp,
                spans = "0:1x3",
                skips = "12:1x1",
                rowWeights = "3,2,2,2,2",
            )


            constrain(g1) {
                width = Dimension.matchParent
                height = Dimension.matchParent
            }
            constrain(a) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(b) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(c) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(d) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(e) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(f) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(g) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(h) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(i) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(j) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(k) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
        },

        modifier = Modifier.fillMaxSize()
    ) {
        val numArray = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(String.format("btn%s", num)).width(120.dp),
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
public fun GridDslDemo2() {
    ConstraintLayout(
        ConstraintSet {
            val a = createRefFor("1")
            val b = createRefFor("2")
            val c = createRefFor("3")
            val d = createRefFor("4")
            val e = createRefFor("5")
            val g1 = createGrid(
                a, b, c, d, e,
                verticalGap = 10.dp,
                horizontalGap = 10.dp,
                columns = 1,
            )

            constrain(g1) {
                width = Dimension.matchParent
                height = Dimension.matchParent
            }
            constrain(a) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(b) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(c) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(d) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(e) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        val numArray = arrayOf("1", "2", "3", "4", "5")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(num).width(120.dp),
                onClick = {},
            ) {
                Text(text = String.format("btn%s", num))
            }
        }
    }
}

@Preview(group = "grid3")
@Composable
public fun GridDslDemo3() {
    ConstraintLayout(
        ConstraintSet {
            val a = createRefFor("1")
            val b = createRefFor("2")
            val c = createRefFor("3")
            val d = createRefFor("4")
            val e = createRefFor("5")
            val g1 = createGrid(
                a, b, c, d, e,
                verticalGap = 10.dp,
                horizontalGap = 10.dp,
                rows = 1,
            )

            constrain(g1) {
                width = Dimension.matchParent
                height = Dimension.matchParent
            }
            constrain(a) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(b) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(c) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(d) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(e) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        val numArray = arrayOf("1", "2", "3", "4", "5")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(num).width(120.dp),
                onClick = {},
            ) {
                Text(text = String.format("btn%s", num))
            }
        }
    }
}

@Preview(group = "grid4")
@Composable
public fun GridDslDemo4() {
    ConstraintLayout(
        ConstraintSet {
            val a = createRefFor("1")
            val b = createRefFor("2")
            val c = createRefFor("3")
            val d = createRefFor("4")
            val e = createRefFor("5")
            val f = createRefFor("6")
            val g = createRefFor("7")
            val h = createRefFor("8")
            val g1 = createGrid(
                e, f, g, h,
                rows = 3,
                columns = 3,
                skips= "0:1x2,4:1x1,6:1x1",
            )

            val g2 = createGrid(
                g1, a, b, c, d,
                rows = 3,
                columns = 3,
                skips = "1:1x1,4:1x1,6:1x1",
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
                modifier = Modifier.layoutId(num).width(40.dp),
                onClick = {},
            ) {
                Text(text = num)
            }
        }
    }
}

@Preview(group = "grid5")
@Composable
public fun GridDslDemo5() {
    ConstraintLayout(
        ConstraintSet {
            val a = createRefFor("box0")
            val b = createRefFor("box1")
            val c = createRefFor("box2")
            val d = createRefFor("box3")
            val g1 = createGrid(
                a, b, c, d,
                rows = 2,
                columns = 2,
                verticalGap = 0.dp,
                horizontalGap = 0.dp,
            )
            constrain(g1) {
                width = Dimension.matchParent
                height = Dimension.matchParent
            }
        },
        modifier = Modifier.size(200.dp),
    ) {
        val ids = (0 until 4).map { "box$it" }.toTypedArray()
        ids.forEach { id ->
            Box(
                Modifier
                    .layoutId(id)
                    .background(Color.Red)
                    .testTag(id)
                    .size(10.dp)
            )
        }
    }
}
