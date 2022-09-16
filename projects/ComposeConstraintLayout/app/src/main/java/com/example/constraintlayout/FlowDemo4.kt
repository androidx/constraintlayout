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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.*

// test fixed value for width/height
@Preview(group = "flow")
@Composable
fun FlowDemo1() {
    ConstraintLayout(
        ConstraintSet("""
        {
            flow1: { 
                width: 350,
                height: 350,
                type: 'hFlow',
                contains: ['btn1', 'btn2'],
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

// test percentage for width/height
@Preview(group = "flow")
@Composable
fun FlowDemo2() {
    ConstraintLayout(
        ConstraintSet("""
        {
            flow: { 
                width: '80%',
                height: '50%',
                type: 'vFlow',
                contains: ['1', '2', '3', '4'],
              }
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val numArray = arrayOf("1", "2", "3", "4")
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

// test wrap for width/height
@Preview(group = "flow")
@Composable
fun FlowDemo3() {
    ConstraintLayout(
        ConstraintSet("""
        {
            flow: { 
                width: 'wrap',
                height: 'wrap',
                type: 'hFlow',
                contains: ['1', '2', '3', '4'],
              }
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val numArray = arrayOf("1", "2", "3", "4")
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

// test wrap for width/height
@Preview(group = "flow")
@Composable
fun FlowDemo4() {
    ConstraintLayout(
        ConstraintSet("""
        {
            flow: { 
                width: 'wrap',
                height: 'wrap',
                type: 'vFlow',
                contains: ['1', '2', '3', '4'],
              }
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val numArray = arrayOf("1", "2", "3", "4")
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

// test spread for width
@Preview(group = "flow")
@Composable
fun FlowDemo5() {
    ConstraintLayout(
        ConstraintSet("""
        {
            flow1: { 
                width: 'spread',
                height: 'parent',
                type: 'hFlow',
                wrap: 'chain',
                maxElement: 3,
                contains: ['1', '2', '3', '4', '5', '6'],
                start: ['parent', 'start', 40],
                end: ['parent', 'end', 40]
              },
            box: {
              width: 'spread',
              height: 'parent',
              start: ['parent', 'start', 40],
              end: ['parent', 'end', 40]
            }
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.layoutId("box").background(Color.Green).alpha(0.2f),
        ) {

        }
        val numArray = arrayOf("1", "2", "3", "4", "5", "6")
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

// test spread for height
@Preview(group = "flow")
@Composable
fun FlowDemo6() {
    ConstraintLayout(
        ConstraintSet("""
        {
            flow1: { 
                width: 'parent',
                height: 'spread',
                type: 'vFlow',
                wrap: 'chain',
                maxElement: 3,
                contains: ['1', '2', '3', '4', '5', '6'],
                top: ['parent', 'top', 100],
                bottom: ['parent', 'bottom', 100]
              },
            box: {
              width: 'parent',
              height: 'spread',
              top: ['parent', 'top', 100],
              bottom: ['parent', 'bottom', 100]
            }
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.layoutId("box").background(Color.Green).alpha(0.2f),
        ) {

        }
        val numArray = arrayOf("1", "2", "3", "4", "5", "6")
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

// test horizontal alignment
@Preview(group = "flow")
@Composable
fun FlowDemo7() {
    ConstraintLayout(
        ConstraintSet("""
        {
            flow: { 
                width: 'parent',
                height: 'parent',
                hAlign: 'end',
                type: 'vFlow',
                contains: ['1', '2', '3', '4'],
              }
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        Button(
            modifier = Modifier.layoutId("1").width(100.dp),
            onClick = {},
        ) {
            Text(text = "1")
        }
        Button(
            modifier = Modifier.layoutId("2").width(150.dp),
            onClick = {},
        ) {
            Text(text = "2")
        }
        Button(
            modifier = Modifier.layoutId("3").width(200.dp),
            onClick = {},
        ) {
            Text(text = "3")
        }
        Button(
            modifier = Modifier.layoutId("4").width(250.dp),
            onClick = {},
        ) {
            Text(text = "4")
        }
    }
}

// test vertical alignment
@Preview(group = "flow")
@Composable
fun FlowDemo8() {
    ConstraintLayout(
        ConstraintSet("""
        {
            flow: { 
                width: 'parent',
                height: 'parent',
                vAlign: 'top',
                type: 'hFlow',
                contains: ['1', '2', '3', '4'],
              }
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        Button(
            modifier = Modifier.layoutId("1").height(100.dp),
            onClick = {},
        ) {
            Text(text = "1")
        }
        Button(
            modifier = Modifier.layoutId("2").height(150.dp),
            onClick = {},
        ) {
            Text(text = "2")
        }
        Button(
            modifier = Modifier.layoutId("3").height(200.dp),
            onClick = {},
        ) {
            Text(text = "3")
        }
        Button(
            modifier = Modifier.layoutId("4").height(250.dp),
            onClick = {},
        ) {
            Text(text = "4")
        }
    }
}

// test padding
@Preview(group = "flow")
@Composable
fun FlowDemo9() {
    ConstraintLayout(
        ConstraintSet("""
        {
            flow: { 
                width: 'parent',
                height: 'parent',
                type: 'hFlow',
                wrap: 'chain',
                hStyle: 'spread_inside',
                vStyle: 'spread_inside',
                maxElement: 2,
                paddingLeft: 50,
                paddingRight: 100,
                paddingTop: 150,
                paddingBottom: 200,
                contains: ['1', '2', '3', '4', '5', '6'],
              }
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val numArray = arrayOf("1", "2", "3", "4", "5", "6")
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
