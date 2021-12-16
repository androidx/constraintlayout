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

package com.example.constraintlayout.verification

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet

@Preview(group = "BarrierTest01")
@Composable
public fun BarrierTest01() {
    ConstraintLayout(
        ConstraintSet("""
            {
                Header: { exportAs: 'barriertest01' },
             
                barrier1: {
                  type: 'barrier',
                  direction: 'end',
                  contains: ['button1', 'button2'],
                },
                barrier2: {
                  type: 'barrier',
                  direction: 'start',
                  contains: ['button1', 'button2'],
                },
                barrier3: {
                  type: 'barrier',
                  direction: 'top',
                  contains: ['button1', 'button2']
                },
                barrier4: {
                  type: 'barrier',
                  direction: 'bottom',
                  contains: ['button1', 'button2']
                },
                barrier5: {
                  type: 'barrier',
                  direction: 'left',
                  contains: ['button1', 'button2']
                },
                barrier6: {
                  type: 'barrier',
                  direction: 'right',
                  contains: ['button1', 'button2']
                },
                button1: {
                  height: { value: '5%' },
                  top: ['parent', 'top', 100],
                  start: ['parent', 'start',  100]
                },
                button2: {
                  height: { value: '5%' },
                  centerVertically: 'parent',
                  centerHorizontally: 'parent',
                },
                button3: {
                  height: { value: '5%' },
                  bottom: ['parent', 'bottom', 300],
                  end: ['barrier2', 'start',  20]
                },
                button4: {
                  height: { value: '5%' },
                  start: ['barrier1', 'end', 10]
                },
                button5: {
                  height: { value: '5%' },
                  bottom: ['barrier3', 'top', 5]
                },
                button6: {
                  height: { value: '5%' },
                  top: ['barrier4', 'bottom', 20],
                  end: ['parent', 'end']
                },
                button7: {
                  height: { value: '5%' },
                  right: ['barrier5', 'right', 5],
                  centerVertically: 'parent',
                },
                button8: {
                  height: { value: '5%' },
                  left: ['barrier6', 'left', 20],
                  centerVertically: 'parent',
                },
            }
        """),
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            modifier = Modifier.layoutId("button1"),
            onClick = {},
        ) {
            Text(text = "btn1")
        }
        Button(
            modifier = Modifier.layoutId("button2"),
            onClick = {},
        ) {
            Text(text = "btn2")
        }
        Button(
            modifier = Modifier.layoutId("button3"),
            onClick = {},
        ) {
            Text(text = "btn3")
        }
        Button(
            modifier = Modifier.layoutId("button4"),
            onClick = {},
        ) {
            Text(text = "btn4")
        }
        Button(
            modifier = Modifier.layoutId("button5"),
            onClick = {},
        ) {
            Text(text = "btn5")
        }
        Button(
            modifier = Modifier.layoutId("button6"),
            onClick = {},
        ) {
            Text(text = "btn6")
        }
        Button(
            modifier = Modifier.layoutId("button7"),
            onClick = {},
        ) {
            Text(text = "btn7")
        }
        Button(
            modifier = Modifier.layoutId("button8"),
            onClick = {},
        ) {
            Text(text = "btn8")
        }
    }
}