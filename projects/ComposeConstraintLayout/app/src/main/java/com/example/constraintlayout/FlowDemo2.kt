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
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.*


@Preview(group = "flow-chain")
@Composable
fun FlowChainDemo1() {
    ConstraintLayout(
        ConstraintSet("""
        {
            flow1: { 
                width: 'parent',
                height: 'parent',
                type: 'hFlow',
                wrap: 'chain',
                maxElement: 3,
                contains: ['1', '2', '3', '4', '5'],
              }
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val numArray = arrayOf("1", "2", "3", "4", "5")
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

@Preview(group = "flow-chain")
@Composable
fun FlowChainDemo2() {
    ConstraintLayout(
        ConstraintSet("""
        {
            flow1: { 
                width: 'parent',
                height: 'parent',
                type: 'hFlow',
                wrap: 'chain',
                maxElement: 2,
                hBias: 0.1,
                contains: ['1', '2', '3', '4', '5'],
              }
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val numArray = arrayOf("1", "2", "3", "4", "5")
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

@Preview(group = "flow-chain")
@Composable
fun FlowChainDemo3() {
    ConstraintLayout(
        ConstraintSet("""
        {
            flow1: { 
                width: 'parent',
                height: 'parent',
                type: 'vFlow',
                wrap: 'chain',
                maxElement: 2,
                vFlowBias: 0.1,
                contains: ['1', '2', '3', '4', '5'],
              }
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val numArray = arrayOf("1", "2", "3", "4", "5")
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

@Preview(group = "flow-chain")
@Composable
fun FlowChainDemo4() {
    ConstraintLayout(
        ConstraintSet("""
        {
            flow1: { 
                width: 'parent',
                height: 'parent',
                vStyle: 'spread_inside',
                hStyle: 'packed',
                type: 'vFlow',
                wrap: 'chain',
                maxElement: 3,
                contains: ['1', '2', '3', '4', '5'],
              }
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val numArray = arrayOf("1", "2", "3", "4", "5")
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

@Preview(group = "flow")
@Composable
fun FlowChainDemo5() {
    ConstraintLayout(
        ConstraintSet("""
        {
            flow1: { 
                width: 'parent',
                height: 'parent',
                type: 'hFlow',
                wrap: 'chain',
                hStyle: ['spread', 'spread_inside', 'packed'],
                maxElement: 2,
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

@Preview(group = "flow-chain")
@Composable
fun FlowChainDemo6() {
    ConstraintLayout(
        ConstraintSet("""
        {
            flow1: { 
                width: 'parent',
                height: 'parent',
                type: 'vFlow',
                wrap: 'chain',
                vStyle: ['spread', 'spread_inside', 'packed'],
                maxElement: 2,
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