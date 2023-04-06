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

@Preview(group = "EqualWeight")
@Composable
fun RowEqualWeight() {
    ConstraintLayout(
        ConstraintSet {
            val a = createRefFor("1")
            val b = createRefFor("2")
            val c = createRefFor("3")
            val d = createRefFor("4")

            val g1 = createRow(
                a, b, c, d,
                horizontalChainStyle = ChainStyle.Spread,
                horizontalChainBias = 1f
            )
            constrain(g1) {
                width = Dimension.matchParent
                height = Dimension.matchParent
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }

            constrain(a) {
                width = Dimension.fillToConstraints
                horizontalChainWeight = 1f
            }
            constrain(b) {
                width = Dimension.fillToConstraints
                horizontalChainWeight = 1f
            }
            constrain(c) {
                width = Dimension.fillToConstraints
                horizontalChainWeight = 1f
            }
            constrain(d) {
                width = Dimension.fillToConstraints
                horizontalChainWeight = 1f
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
    }
}

@Preview(group = "SpaceEvenly")
@Composable
fun RowSpaceEvenly() {
    ConstraintLayout(
        ConstraintSet {
            val a = createRefFor("1")
            val b = createRefFor("2")
            val c = createRefFor("3")
            val d = createRefFor("4")

            val g1 = createRow(
                a, b, c, d,
                horizontalChainStyle = ChainStyle.Spread,
            )
            constrain(g1) {
                width = Dimension.matchParent
                height = Dimension.matchParent
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
                Text(text = String.format("%s", num))
            }
        }
    }
}

@Preview(group = "SpaceAround")
@Composable
fun RowSpaceAround() {
    ConstraintLayout(
        ConstraintSet {
            val a = createRefFor("1")
            val b = createRefFor("2")
            val c = createRefFor("3")
            val d = createRefFor("4")

            val g1 = createRow(
                a, b, c, d,
            )
            constrain(g1) {
                width = Dimension.matchParent
                height = Dimension.matchParent
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
                Text(text = String.format("%s", num))
            }
        }
    }
}

@Preview(group = "SpaceBetween")
@Composable
fun RowSpaceBetween() {
    ConstraintLayout(
        ConstraintSet {
            val a = createRefFor("1")
            val b = createRefFor("2")
            val c = createRefFor("3")
            val d = createRefFor("4")

            val g1 = createRow(
                a, b, c, d,
                horizontalChainStyle = ChainStyle.SpreadInside,
            )
            constrain(g1) {
                width = Dimension.matchParent
                height = Dimension.matchParent
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
                Text(text = String.format("%s", num))
            }
        }
    }
}

@Preview(group = "Center")
@Composable
fun RowCenter() {
    ConstraintLayout(
        ConstraintSet {
            val a = createRefFor("1")
            val b = createRefFor("2")
            val c = createRefFor("3")
            val d = createRefFor("4")

            val g1 = createRow(
                a, b, c, d,
                horizontalChainStyle = ChainStyle.Packed,
                horizontalChainBias = 0.5f
            )
            constrain(g1) {
                width = Dimension.matchParent
                height = Dimension.matchParent
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
                Text(text = String.format("%s", num))
            }
        }
    }
}

@Preview(group = "StartCenter")
@Composable
fun RowStartCenter() {
    ConstraintLayout(
        ConstraintSet {
            val a = createRefFor("1")
            val b = createRefFor("2")
            val c = createRefFor("3")
            val d = createRefFor("4")

            val g1 = createRow(
                a, b, c, d,
                horizontalChainStyle = ChainStyle.Packed,
                horizontalChainBias = 0f
            )
            constrain(g1) {
                width = Dimension.matchParent
                height = Dimension.matchParent
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
                Text(text = String.format("%s", num))
            }
        }
    }
}

@Preview(group = "EndCenter")
@Composable
fun RowEndCenter() {
    ConstraintLayout(
        ConstraintSet {
            val a = createRefFor("1")
            val b = createRefFor("2")
            val c = createRefFor("3")
            val d = createRefFor("4")

            val g1 = createRow(
                a, b, c, d,
                horizontalChainStyle = ChainStyle.Packed,
                horizontalChainBias = 1f
            )
            constrain(g1) {
                width = Dimension.matchParent
                height = Dimension.matchParent
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
                Text(text = String.format("%s", num))
            }
        }
    }
}

@Preview(group = "Baseline")
@Composable
fun RowBaseline() {
    ConstraintLayout(
        ConstraintSet {
            val a = createRefFor("1")
            val b = createRefFor("2")
            val c = createRefFor("3")
            val d = createRefFor("box")
            val g1 = createRow(
                a, b, c, d,
                horizontalChainStyle = ChainStyle.Spread,
            )
            constrain(g1) {
                width = Dimension.matchParent
                height = Dimension.matchParent
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
            constrain(d) {
                bottom.linkTo(c.baseline)
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        val numArray = arrayOf("1", "2", "3")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(num).width(50.dp),
                onClick = {},
            ) {
                Text(text = String.format("%s", num))
            }
            Box(Modifier.layoutId("box").size(50.dp).background(Color.Blue))
        }
    }
}

@Preview(group = "WeightFilled")
@Composable
fun RowWeightFilled() {
    ConstraintLayout(
        ConstraintSet {
            val a = createRefFor("1")
            val b = createRefFor("2")
            val c = createRefFor("3")
            val d = createRefFor("4")

            val g1 = createRow(
                a, b, c, d,
                horizontalChainStyle = ChainStyle.Spread,
                horizontalChainBias = 1f
            )
            constrain(g1) {
                width = Dimension.matchParent
                height = Dimension.matchParent
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }

            constrain(a) {
                width = Dimension.fillToConstraints
                horizontalChainWeight = 1f
            }
            constrain(b) {
                width = Dimension.fillToConstraints
                horizontalChainWeight = 2f
            }
            constrain(c) {
                width = Dimension.fillToConstraints
                horizontalChainWeight = 1f
            }
            constrain(d) {
                width = Dimension.fillToConstraints
                horizontalChainWeight = 1f
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
    }
}


