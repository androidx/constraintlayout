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
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet

@Preview(group = "testTwoByTwo")
@Composable
fun testTwoByTwoDemo() {
    ConstraintLayout(
        ConstraintSet("""
        {
            grid: { 
                width: 200,
                height: 200,
                type: "Grid",
                boxesCount: 4,
                orientation: 0,
                rows: 2,
                columns: 2,
                hGap: 0,
                vGap: 0,
                spans: "",
                skips: "",
                rowWeights: "",
                columnWeights: "",
                contains: ["box1", "box2", "box3", "box4"],
              },
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val numArray = arrayOf("box1", "box2", "box3", "box4")
        for (num in numArray) {
            Box(
                Modifier
                    .layoutId(num)
                    .size(30.dp)
                    .background(Color.Red)
                    .testTag(num)
            )
        }
    }
}

@Preview(group = "testOrientation")
@Composable
fun testOrientationDemo() {
    ConstraintLayout(
        ConstraintSet("""
        {
            grid: { 
                width: 200,
                height: 200,
                type: "Grid",
                boxesCount: 4,
                orientation: 1,
                rows: 2,
                columns: 2,
                hGap: 0,
                vGap: 0,
                spans: "",
                skips: "",
                rowWeights: "",
                columnWeights: "",
                contains: ["box1", "box2", "box3", "box4"],
              },
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val numArray = arrayOf("box1", "box2", "box3", "box4")
        for (num in numArray) {
            Box(
                Modifier
                    .layoutId(num)
                    .size(30.dp)
                    .background(Color.Red)
                    .testTag(num)
            )
        }
    }
}

@Preview(group = "testRows")
@Composable
fun testRowsDemo() {
    ConstraintLayout(
        ConstraintSet("""
        {
            grid: { 
                width: 200,
                height: 200,
                type: "Grid",
                boxesCount: 4,
                orientation: 0,
                rows: 0,
                columns: 1,
                hGap: 0,
                vGap: 0,
                spans: "",
                skips: "",
                rowWeights: "",
                columnWeights: "",
                contains: ["box1", "box2", "box3", "box4"],
              },
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val numArray = arrayOf("box1", "box2", "box3", "box4")
        for (num in numArray) {
            Box(
                Modifier
                    .layoutId(num)
                    .size(30.dp)
                    .background(Color.Red)
                    .testTag(num)
            )
        }
    }
}

@Preview(group = "testColumns")
@Composable
fun testColumnsDemo() {
    ConstraintLayout(
        ConstraintSet("""
        {
            grid: { 
                width: 200,
                height: 200,
                type: "Grid",
                boxesCount: 4,
                orientation: 0,
                rows: 1,
                columns: 0,
                hGap: 0,
                vGap: 0,
                spans: "",
                skips: "",
                rowWeights: "",
                columnWeights: "",
                contains: ["box1", "box2", "box3", "box4"],
              },
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val numArray = arrayOf("box1", "box2", "box3", "box4")
        for (num in numArray) {
            Box(
                Modifier
                    .layoutId(num)
                    .size(30.dp)
                    .background(Color.Red)
                    .testTag(num)
            )
        }
    }
}

@Preview(group = "testSpans")
@Composable
fun testSpansDemo() {
    ConstraintLayout(
        ConstraintSet("""
        {
            grid: { 
                height: 200,
                width: 200,
                type: "Grid",
                orientation: 0,
                rows: 2,
                columns: 2,
                skips: "",
                spans: "0:1x2",
                rowWeights: "",
                columnWeights: "",
                contains: ["box1", "box2", "box3",],
              },
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val numArray = arrayOf("box1", "box2", "box3")
        for (num in numArray) {
            Box(
                Modifier
                    .layoutId(num)
                    .size(30.dp)
                    .background(Color.Red)
                    .testTag(num)
            )
        }
    }
}

@Preview(group = "testSkips")
@Composable
fun testSkipsDemo() {
    ConstraintLayout(
        ConstraintSet("""
        {
            grid: { 
                height: 200,
                width: 200,
                type: "Grid",
                orientation: 0,
                rows: 2,
                columns: 2,
                skips: "0:1x1",
                spans: "",
                rowWeights: "",
                columnWeights: "",
                contains: ["box1", "box2", "box3",],
              },
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val numArray = arrayOf("box1", "box2", "box3")
        for (num in numArray) {
            Box(
                Modifier
                    .layoutId(num)
                    .size(30.dp)
                    .background(Color.Red)
                    .testTag(num)
            )
        }
    }
}

@Preview(group = "testRowWeights")
@Composable
fun testRowWeightsDemo() {
    ConstraintLayout(
        ConstraintSet("""
        {
            grid: { 
                height: 200,
                width: 200,
                type: "Grid",
                orientation: 0,
                rows: 0,
                columns: 1,
                vGap: 0,
                hGap: 0,
                skips: "",
                spans: "",
                rowWeights: "1,3",
                columnWeights: "",
                contains: ["box1", "box2"],
              },
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val numArray = arrayOf("box1", "box2")
        for (num in numArray) {
            Box(
                Modifier
                    .layoutId(num)
                    .size(30.dp)
                    .background(Color.Red)
                    .testTag(num)
            )
        }
    }
}

@Preview(group = "testColumnWeights")
@Composable
fun testColumnWeightsDemo() {
    ConstraintLayout(
        ConstraintSet("""
        {
            grid: { 
                height: 200,
                width: 200,
                type: "Grid",
                orientation: 0,
                rows: 1,
                columns: 0,
                vGap: 0,
                hGap: 0,
                skips: "",
                spans: "",
                rowWeights: "",
                columnWeights: "1,3",
                contains: ["box1", "box2"],
              },
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val numArray = arrayOf("box1", "box2")
        for (num in numArray) {
            Box(
                Modifier
                    .layoutId(num)
                    .size(30.dp)
                    .background(Color.Red)
                    .testTag(num)
            )
        }
    }
}

@Preview(group = "testGaps")
@Composable
fun testGapsDemo() {
    val density = LocalDensity.current
    // convert dp to px
    val vGapPx = with(density) { 20.dp.toPx() }
    val hGapPx = with(density) { 10.dp.toPx() }
    ConstraintLayout(
        ConstraintSet("""
        {
            grid: { 
                height: 200,
                width: 200,
                type: "Grid",
                orientation: 0,
                rows: 2,
                columns: 2,
                vGap: $vGapPx,
                hGap: $hGapPx,
                contains: ["box1", "box2", "box3", "box4"],
              },
              box1: {
                width: 'spread',
                height: 'spread',
              },
              box2: {
                width: 'spread',
                height: 'spread',
              },
              box3: {
                width: 'spread',
                height: 'spread',
              },
              box4: {
                width: 'spread',
                height: 'spread',
              }
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val numArray = arrayOf("box1", "box2", "box3", "box4")
        for (num in numArray) {
            Box(
                Modifier
                    .layoutId(num)
                    .size(30.dp)
                    .background(Color.Red)
                    .testTag(num)
            )
        }
    }
}