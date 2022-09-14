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
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.*

@Preview(group = "flow-aligned")
@Composable
fun FlowAlignedDemo1() {
    ConstraintLayout(
        ConstraintSet("""
        {
            flow1: { 
                width: 'parent',
                height: 'parent',
                type: 'vFlow',
                wrap: 'aligned',
                center: 'parent',
                maxElement: 3,
                contains: ['1', '2', '3', '4', '5'],
              }
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val chArray = arrayOf("1", "2", "3", "4", "5")
        for (ch in chArray) {
            Button(
                modifier = Modifier.layoutId(ch),
                onClick = {},
            ) {
                Text(text = ch)
            }
        }
    }
}

@Preview(group = "flow-aligned")
@Composable
fun FlowAlignedDemo2() {
    ConstraintLayout(
        ConstraintSet("""
        {
            flow1: { 
                width: 'parent',
                height: 'parent',
                type: 'vFlow',
                wrap: 'aligned',
                center: 'parent',
                maxElement: 3,
                vStyle: 'packed',
                contains: ['1', '2', '3', '4', '5'],
              }
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val chArray = arrayOf("1", "2", "3", "4", "5")
        for (ch in chArray) {
            Button(
                modifier = Modifier.layoutId(ch),
                onClick = {},
            ) {
                Text(text = ch)
            }
        }
    }
}

@Preview(group = "flow-aligned")
@Composable
fun FlowAlignedDemo3() {
    ConstraintLayout(
        ConstraintSet("""
        {
            flow1: { 
                width: 'parent',
                height: 'parent',
                type: 'hFlow',
                wrap: 'aligned',
                center: 'parent',
                maxElement: 3,
                contains: ['1', '2', '3', '4', '5'],
              }
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val chArray = arrayOf("1", "2", "3", "4", "5")
        for (ch in chArray) {
            Button(
                modifier = Modifier.layoutId(ch),
                onClick = {},
            ) {
                Text(text = ch)
            }
        }
    }
}

@Preview(group = "flow-aligned")
@Composable
fun FlowAlignedDemo4() {
    ConstraintLayout(
        ConstraintSet("""
        {
            flow1: { 
                width: 'parent',
                height: 'parent',
                type: 'hFlow',
                wrap: 'aligned',
                center: 'parent',
                maxElement: 3,
                hStyle: 'spread_inside',
                contains: ['1', '2', '3', '4', '5'],
              }
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val chArray = arrayOf("1", "2", "3", "4", "5")
        for (ch in chArray) {
            Button(
                modifier = Modifier.layoutId(ch),
                onClick = {},
            ) {
                Text(text = ch)
            }
        }
    }
}
