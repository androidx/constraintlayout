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
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
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

@Preview(group = "row")
@Composable
public fun RowDslDemo() {
    ConstraintLayout(
        ConstraintSet {
            val a = createRefFor("1")
            val b = createRefFor("2")
            val c = createRefFor("3")
            val d = createRefFor("4")
            val e = createRefFor("5")
            val g1 = createRow(
                a, b, c, d, e,
                verticalGap = 10.dp,
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

@Preview(group = "row")
@Composable
public fun RowWeightsDslDemo() {
    ConstraintLayout(
        ConstraintSet {
            val a = createRefFor("1")
            val b = createRefFor("2")
            val c = createRefFor("3")
            val d = createRefFor("4")
            val e = createRefFor("5")
            val weights = arrayOf(3, 3, 2, 2, 1)
            val g1 = createRow(
                a, b, c, d, e,
                verticalGap = 10.dp,
                rowWeights = weights,
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

@Preview(group = "column")
@Composable
public fun ColumnDslDemo() {
    ConstraintLayout(
        ConstraintSet {
            val a = createRefFor("1")
            val b = createRefFor("2")
            val c = createRefFor("3")
            val d = createRefFor("4")
            val e = createRefFor("5")
            val g1 = createColumn(
                a, b, c, d, e,
                horizontalGap = 10.dp,
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
@Preview(group = "column")
@Composable
public fun ColumnWeightsDslDemo() {
    ConstraintLayout(
        ConstraintSet {
            val a = createRefFor("1")
            val b = createRefFor("2")
            val c = createRefFor("3")
            val d = createRefFor("4")
            val e = createRefFor("5")
            val weights = arrayOf(3, 3, 2, 2, 1)
            val g1 = createColumn(
                a, b, c, d, e,
                horizontalGap = 10.dp,
                columnWeights = weights,
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