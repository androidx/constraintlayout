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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.*

@Preview(group = "chain")
@Composable
fun ChainDemo() {
    ConstraintLayout(
        ConstraintSet {
            val a = createRefFor("1")
            val b = createRefFor("2")
            val c = createRefFor("3")
            val d = createRefFor("4")
            val e = createRefFor("5")
            val c1 = createHorizontalChain(
                a, b, c, d, e,
                chainStyle = ChainStyle.Spread
            )
            constrain(c1) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
            constrain(a) {
//                width = Dimension.fillToConstraints.atMostWrapContent
//                horizontalChainWeight = 1f
            }
            constrain(b) {
//                width = Dimension.fillToConstraints.atMostWrapContent
//                horizontalChainWeight = 1f
//                top.linkTo(g1.top)
            }
            constrain(c) {
//                width = Dimension.fillToConstraints.atMostWrapContent
//                horizontalChainWeight = 1f
//                bottom.linkTo(g1.bottom)
            }
            constrain(d) {
//                width = Dimension.fillToConstraints.atMostWrapContent
//                horizontalChainWeight = 1f
            }
            constrain(e) {
//                width = Dimension.fillToConstraints.atMostWrapContent
//                horizontalChainWeight = 1f
                top.linkTo(d.baseline)
            }

        },
        modifier = Modifier.fillMaxSize()
    ) {
        val numArray = arrayOf("1", "2", "3", "4")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(num).width(50.dp),
                onClick = {},
            ) {
                Text(text = String.format("btn%s", num))
            }
        }
        Box(modifier = Modifier.size(50.dp).layoutId(5).background(color = Color.Red))
    }
}

@Preview(group = "flow")
@Composable
fun FlowDemo() {
    ConstraintLayout(
        ConstraintSet {
            val a = createRefFor("1")
            val b = createRefFor("2")
            val c = createRefFor("3")
            val d = createRefFor("4")
            val e = createRefFor("5")
            val f1 = createFlow(
                a, b, c, d, e,
                horizontalStyle = FlowStyle.Packed,
                horizontalFlowBias = 0.5f
            )
            constrain(f1) {
                width = Dimension.matchParent
                height = Dimension.matchParent
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
            constrain(a) {
//                width = Dimension.fillToConstraints
//                horizontalChainWeight = 1f
                bottom.linkTo(f1.bottom)
            }
            constrain(b) {
//                width = Dimension.fillToConstraints
//                horizontalChainWeight = 2f
                top.linkTo(f1.top)
            }
            constrain(c) {
//                width = Dimension.fillToConstraints
//                horizontalChainWeight = 1f
                bottom.linkTo(d.baseline)
            }
            constrain(d) {
//                width = Dimension.fillToConstraints
//                horizontalChainWeight = 1f
                top.linkTo(f1.top)
                bottom.linkTo(f1.bottom)
            }
            constrain(e) {
//                width = Dimension.fillToConstraints
//                horizontalChainWeight = 1f
                top.linkTo(d.baseline)
            }

        },
        modifier = Modifier.fillMaxSize()
    ) {
        val numArray = arrayOf("1", "2", "3", "4")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(num).width(50.dp),
                onClick = {},
            ) {
                Text(text = String.format("btn%s", num))
            }
        }
        Box(modifier = Modifier.size(50.dp).layoutId(5).background(color = Color.Red))
    }
}

@Preview(group = "row")
@Composable
fun ChainRowDemo() {
    ConstraintLayout(
        ConstraintSet {
            val a = createRefFor("1")
            val b = createRefFor("2")
            val c = createRefFor("3")
            val d = createRefFor("4")
            val e = createRefFor("5")

            val g1 = createRow(
                a, b, c, d, e,
                horizontalChainStyle = ChainStyle.SpreadInside,
//                horizontalChainBias = 1f
            )
            constrain(g1) {
                width = Dimension.matchParent
                height = Dimension.matchParent
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
            constrain(a) {
//                width = Dimension.fillToConstraints
//                horizontalChainWeight = 1f
            }
            constrain(b) {
//                width = Dimension.fillToConstraints
//                horizontalChainWeight = 1f
//                top.linkTo(g1.top)
            }
            constrain(c) {
//                width = Dimension.fillToConstraints
//                horizontalChainWeight = 1f
//                bottom.linkTo(g1.bottom)
            }
            constrain(d) {
//                width = Dimension.fillToConstraints
//                horizontalChainWeight = 1f
                top.linkTo(g1.top)
                bottom.linkTo(g1.bottom)
            }
            constrain(e) {
//                width = Dimension.fillToConstraints
//                horizontalChainWeight = 1f
                top.linkTo(d.baseline)
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        val numArray = arrayOf("1", "2", "3", "4")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(num).width(50.dp),
                onClick = {},
            ) {
                Text(text = String.format("%s", num))
            }
        }
        Box(modifier = Modifier.size(50.dp).layoutId(5).background(color = Color.Red))
    }
}

@Preview(group = "compose")
@Composable
fun ComposeRow() {
    Row (modifier = Modifier.fillMaxHeight(0.5f).fillMaxWidth()) {
        Box(modifier = Modifier.size(50.dp)
            .background(color = Color.Red)
            .alignBy { it.measuredHeight }
        )
        Text("Hello World",
            color = Color.Blue,
            fontSize = 30.sp,
            modifier = Modifier.alignBy(FirstBaseline)
        )
        Box(modifier = Modifier.size(50.dp)
            .background(color = Color.Red)
            .alignBy { it.measuredHeight / 2 }
        )
        Text("Hello World line3",
            color = Color.Blue,
            fontSize = 30.sp,
            modifier = Modifier.alignBy(LastBaseline)
        )
    }
    Row(modifier = Modifier.fillMaxSize(),
        Arrangement.SpaceBetween,
        Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.size(50.dp)
            .background(color = Color.Blue)
            .weight(1f, fill = false)
        )
        Box(modifier = Modifier.size(50.dp)
            .background(color = Color.Red)
            .weight(2f, fill = false)
        )
        Box(modifier = Modifier.size(50.dp)
            .background(color = Color.Green)
            .weight(1f, fill = false)
        )
        Box(modifier = Modifier.size(50.dp)
            .background(color = Color.Green)
            .weight(1f, fill = false)
        )
    }
}

@Preview(group = "column")
@Composable
fun ChainColumnDemo() {
    ConstraintLayout(
        ConstraintSet {
            val a = createRefFor("1")
            val b = createRefFor("2")
            val c = createRefFor("3")
            val d = createRefFor("4")
            val g1 = createColumn(
                a, b, c, d,
                verticalChainStyle = ChainStyle.SpreadInside
            )
            constrain(g1) {
                width = Dimension.matchParent
                height = Dimension.matchParent
//                start.linkTo(parent.start)
//                end.linkTo(parent.end)
//                top.linkTo(parent.top)
//                bottom.linkTo(parent.bottom)
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        val numArray = arrayOf("1", "2", "3", "4")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(num).width(50.dp),
                onClick = {},
            ) {
                Text(text = String.format("btn%s", num))
            }
        }
    }
}

@Preview(group = "chain2")
@Composable
fun ChainColumnDemo1() {
    ConstraintLayout(
        ConstraintSet {
            val a = createRefFor("1")
            val b = createRefFor("2")
            val c = createRefFor("3")
            val d = createRefFor("4")
            val e = createRefFor("5")
            val c1 = createVerticalChain(
                a, b, c, d, e,
                chainStyle = ChainStyle.Packed
            )
            constrain(c1) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }

        },
        modifier = Modifier.fillMaxSize()
    ) {
        val numArray = arrayOf("1", "2", "3", "4")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(num).width(50.dp),
                onClick = {},
            ) {
                Text(text = String.format("btn%s", num))
            }
        }
        Box(modifier = Modifier.size(50.dp).layoutId(5).background(color = Color.Red))
    }
}

