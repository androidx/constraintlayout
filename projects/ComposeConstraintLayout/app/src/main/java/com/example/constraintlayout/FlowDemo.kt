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


@Preview(group = "flow")
@Composable
public fun FlowDemo1() {
    // Currently, we still have problem with positioning the Flow Helper
    // and/or setting the width/height properly.
    ConstraintLayout(
        ConstraintSet("""
        {
            flow1: { 
                width: 'match_parent',
                height: 'match_parent',
                type: 'hFlow',
                wrap: 'none',
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

@Preview(group = "flow")
@Composable
public fun FlowDemo2() {
    ConstraintLayout(
        ConstraintSet("""
        {
            flow1: { 
                width: 'match_parent',
                height: 'match_parent',
                type: 'vFlow',
                wrap: 'none',
                contains: ['1', '2', '3', '4'],
                centerVertically: 'parent',
                centerHorizontally: 'parent',
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

@Preview(group = "flow")
@Composable
public fun FlowDemo3() {
    ConstraintLayout(
        ConstraintSet("""
        {
            flow1: { 
                width: 'match_parent',
                height: 'match_parent',
                type: 'hFlow',
                wrap: 'chain',
                vFlowBias: 0.1,
                hFlowBias: 0.8,
                maxElement: 4,
                contains: ['1', '2', '3', '4', '5', '6', '7'],
                start: ['parent', 'start'],
                end: ['parent', 'end'],
                top: ['parent', 'top'],
                bottom: ['parent', 'bottom'],
              }
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val numArray = arrayOf("1", "2", "3", "4", "5", "6", "7")
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
public fun FlowDemo4() {
    ConstraintLayout(
        ConstraintSet("""
        {
            flow1: { 
                width: 'match_parent',
                height: 'match_parent',
                type: 'vFlow',
                vGap: 32,
                hGap: 32,
                wrap: 'aligned',
                center: 'parent',
                maxElement: 3,
                contains: ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'],
              }
        }
        """.trimIndent()),
        modifier = Modifier.fillMaxSize()) {
        val chArray = arrayOf("a", "b", "c", "d", "e", "f", "g", "h")
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









