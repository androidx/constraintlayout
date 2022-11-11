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


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.*

@Preview(group = "flow-basic")
@Composable
fun FlowBasicDemo1() {
    ConstraintLayout(
        ConstraintSet("""
        {
            flow1: { 
                width: 'parent',
                height: 'parent',
                type: 'hFlow',
                contains: ['btn1', 'btn2', 'btn3', 'btn4'],
              }
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val numArray = arrayOf("btn1", "btn2", "btn3", "btn4")
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


// test horizontal style and horizontal Gap
@Preview(group = "flow-basic")
@Composable
fun FlowBasicDemo2() {
    ConstraintLayout(
        ConstraintSet("""
        {
            flow: { 
                width: 'parent',
                height: 'parent',
                type: 'hFlow',
                hStyle: 'packed',
                hGap: 10,
                contains: ['btn1', 'btn2', 'btn3', 'btn4'],
              }
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val numArray = arrayOf("btn1", "btn2", "btn3", "btn4")
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

// test adding constraints to the other widget
@Preview(group = "flow-basic")
@Composable
fun FlowBasicDemo3() {
    ConstraintLayout(
        ConstraintSet("""
        {
            flow: { 
                type: 'hFlow',
                contains: ['btn1', 'btn2', 'btn3', 'btn4'],
                top: ['btn5', 'bottom', 100],
                left: ['btn5', 'right', 50]
              }
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        Button(
            modifier = Modifier.layoutId("btn5").height(500.dp).width(100.dp),
            onClick = {},
        ) {
            Text(text = "btn5")
        }
        val numArray = arrayOf("btn1", "btn2", "btn3", "btn4")
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

@Preview(group = "flow-basic")
@Composable
fun FlowBasicDemo4() {
    ConstraintLayout(
        ConstraintSet("""
        {
            flow: { 
                width: 'parent',
                height: 'parent',
                type: 'vFlow',
                contains: ['btn1', 'btn2', 'btn3', 'btn4'],
              }
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val numArray = arrayOf("btn1", "btn2", "btn3", "btn4")
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

// test vertical style and vertical gap
@Preview(group = "flow-basic")
@Composable
fun FlowBasicDemo5() {
    ConstraintLayout(
        ConstraintSet("""
        {
            flow: { 
                width: 'parent',
                height: 'parent',
                type: 'vFlow',
                vStyle: 'packed',
                vGap: 100,
                contains: ['btn1', 'btn2', 'btn3', 'btn4'],
              }
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val numArray = arrayOf("btn1", "btn2", "btn3", "btn4")
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

// test adding constraints to parent
@Preview(group = "flow-basic")
@Composable
fun FlowBasicDemo6() {
    ConstraintLayout(
        ConstraintSet("""
        {
            flow: { 
                type: 'vFlow',
                contains: ['btn1', 'btn2', 'btn3', 'btn4'],
                start: ['parent', 'start'],
                top: ['parent', 'top'],
                bottom: ['parent', 'bottom'],
                end: ['parent', 'end']
              }
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val numArray = arrayOf("btn1", "btn2", "btn3", "btn4")
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

// test adding constraints to parent
@Preview(group = "split")
@Composable
fun SplitDemo1() {
    ConstraintLayout(
        ConstraintSet("""
        {
            split: { 
                height: 'parent',
                width: 'parent',
                type: 'Split',
                orientation: 0,
                contains: ['btn1', 'btn2'],
              },
             btn1: {
              height: 'spread',
              width: 'spread',
             }
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val numArray = arrayOf("btn1", "btn2")
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

