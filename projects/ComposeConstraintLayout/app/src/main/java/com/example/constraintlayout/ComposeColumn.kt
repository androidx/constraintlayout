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

@Preview(group = "EqualWeight")
@Composable
fun ColumnEqualWeight() {
    ConstraintLayout(
        ConstraintSet {
            val a = createRefFor("1")
            val b = createRefFor("2")
            val c = createRefFor("3")
            val d = createRefFor("4")

            val g1 = createColumn(
                a, b, c, d,
                verticalChainStyle = ChainStyle.Spread,
                verticalChainBias = 1f
            )
            constrain(g1) {
                width = Dimension.matchParent
                height = Dimension.matchParent
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }

            constrain(a) {
                height = Dimension.fillToConstraints
                verticalChainWeight = 1f
            }
            constrain(b) {
                height = Dimension.fillToConstraints
                verticalChainWeight = 1f
            }
            constrain(c) {
                height = Dimension.fillToConstraints
                verticalChainWeight = 1f
            }
            constrain(d) {
                height = Dimension.fillToConstraints
                verticalChainWeight = 1f
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        val numArray = arrayOf("1", "2", "3", "4")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(num).size(50.dp),
                onClick = {},
            ) {
                Text(text = String.format("%s", num))
            }
        }
    }
}

@Preview(group = "SpaceEvenly")
@Composable
fun ColumnSpaceEvenly() {
    ConstraintLayout(
        ConstraintSet {
            val a = createRefFor("1")
            val b = createRefFor("2")
            val c = createRefFor("3")
            val d = createRefFor("4")

            val g1 = createColumn(
                a, b, c, d,
                verticalChainStyle = ChainStyle.Spread,
            )
            constrain(g1) {
                width = Dimension.matchParent
                height = Dimension.matchParent
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        val numArray = arrayOf("1", "2", "3", "4")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(num).size(50.dp),
                onClick = {},
            ) {
                Text(text = String.format("%s", num))
            }
        }
    }
}

@Preview(group = "SpaceAround")
@Composable
fun ColumnSpaceAround() {
    ConstraintLayout(
        ConstraintSet {
            val a = createRefFor("1")
            val b = createRefFor("2")
            val c = createRefFor("3")
            val d = createRefFor("4")

            val g1 = createColumn(
                a, b, c, d,
            )
            constrain(g1) {
                width = Dimension.matchParent
                height = Dimension.matchParent
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        val numArray = arrayOf("1", "2", "3", "4")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(num).size(50.dp),
                onClick = {},
            ) {
                Text(text = String.format("%s", num))
            }
        }
    }
}

@Preview(group = "SpaceBetween")
@Composable
fun ColumnSpaceBetween() {
    ConstraintLayout(
        ConstraintSet {
            val a = createRefFor("1")
            val b = createRefFor("2")
            val c = createRefFor("3")
            val d = createRefFor("4")

            val g1 = createColumn(
                a, b, c, d,
                verticalChainStyle = ChainStyle.SpreadInside,
            )
            constrain(g1) {
                width = Dimension.matchParent
                height = Dimension.matchParent
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        val numArray = arrayOf("1", "2", "3", "4")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(num).size(50.dp),
                onClick = {},
            ) {
                Text(text = String.format("%s", num))
            }
        }
    }
}

@Preview(group = "Center")
@Composable
fun ColumnCenter() {
    ConstraintLayout(
        ConstraintSet {
            val a = createRefFor("1")
            val b = createRefFor("2")
            val c = createRefFor("3")
            val d = createRefFor("4")

            val g1 = createColumn(
                a, b, c, d,
                verticalChainStyle = ChainStyle.Packed,
                verticalChainBias = 0.5f
            )
            constrain(g1) {
                width = Dimension.matchParent
                height = Dimension.matchParent
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        val numArray = arrayOf("1", "2", "3", "4")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(num).size(50.dp),
                onClick = {},
            ) {
                Text(text = String.format("%s", num))
            }
        }
    }
}

@Preview(group = "TopCenter")
@Composable
fun ColumnTopCenter() {
    ConstraintLayout(
        ConstraintSet {
            val a = createRefFor("1")
            val b = createRefFor("2")
            val c = createRefFor("3")
            val d = createRefFor("4")

            val g1 = createColumn(
                a, b, c, d,
                verticalChainStyle = ChainStyle.Packed,
                verticalChainBias = 0f
            )
            constrain(g1) {
                width = Dimension.matchParent
                height = Dimension.matchParent
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        val numArray = arrayOf("1", "2", "3", "4")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(num).size(50.dp),
                onClick = {},
            ) {
                Text(text = String.format("%s", num))
            }
        }
    }
}

@Preview(group = "BottomCenter")
@Composable
fun ColumnBottomCenter() {
    ConstraintLayout(
        ConstraintSet {
            val a = createRefFor("1")
            val b = createRefFor("2")
            val c = createRefFor("3")
            val d = createRefFor("4")

            val g1 = createColumn(
                a, b, c, d,
                verticalChainStyle = ChainStyle.Packed,
                verticalChainBias = 1f
            )
            constrain(g1) {
                width = Dimension.matchParent
                height = Dimension.matchParent
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        val numArray = arrayOf("1", "2", "3", "4")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(num).size(50.dp),
                onClick = {},
            ) {
                Text(text = String.format("%s", num))
            }
        }
    }
}

@Preview(group = "WeightFilled")
@Composable
fun ColumnWeightFilled() {
    ConstraintLayout(
        ConstraintSet {
            val a = createRefFor("1")
            val b = createRefFor("2")
            val c = createRefFor("3")
            val d = createRefFor("4")

            val g1 = createColumn(
                a, b, c, d,
                verticalChainStyle = ChainStyle.Spread,
                verticalChainBias = 1f
            )
            constrain(g1) {
                width = Dimension.matchParent
                height = Dimension.matchParent
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }

            constrain(a) {
                height = Dimension.fillToConstraints
                verticalChainWeight = 1f
            }
            constrain(b) {
                height = Dimension.fillToConstraints
                verticalChainWeight = 2f
            }
            constrain(c) {
                height = Dimension.fillToConstraints
                verticalChainWeight = 1f
            }
            constrain(d) {
                height = Dimension.fillToConstraints
                verticalChainWeight = 1f
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        val numArray = arrayOf("1", "2", "3", "4")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(num).size(50.dp),
                onClick = {},
            ) {
                Text(text = String.format("%s", num))
            }
        }
    }
}



