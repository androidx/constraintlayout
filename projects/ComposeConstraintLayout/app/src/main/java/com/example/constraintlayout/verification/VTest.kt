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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import com.example.constraintlayout.R

@Preview
@Composable
fun VTest() {
    ConstraintLayout(
        ConstraintSet("""
            {
               
                g1: { type: 'vGuideline', start: 80 },
                g2: { type: 'vGuideline', end: 80 },
                button: {
                  width: 'spread',
                  top: ['title', 'bottom', 16],
                  start: ['g1', 'start'],
                  end: ['g2', 'end']
                },
                title: {
                  width: { value: 'wrap', max: 300 },
                  centerVertically: 'parent',
                  start: ['g1', 'start'],
                  end: ['g2','end']
                }
            }
        """),
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            modifier = Modifier.layoutId("button"),
            onClick = {},
        ) {
            Text(text = "end of ")
        }
        Text(modifier = Modifier.layoutId("title").background(Color.White),
            text = "We Are Done",
            style = MaterialTheme.typography.body1,
        )
    }
}

@Preview(group = "new")
@Composable
fun VTest1() {
    ConstraintLayout(
        ConstraintSet("""
            {
                Header: { exportAs: 'test1'},
                g1: { type: 'vGuideline', start: 80 },
                g2: { type: 'vGuideline', end: 80 },
                button: {
                  width: 'spread',
                  top: ['title', 'bottom', 16],
                  start: ['g1', 'start'],
                  end: ['g2', 'end']
                },
                title: {
                  width: { value: 'wrap', max: 300 },
                  centerVertically: 'parent',
                  start: ['g1', 'start'],
                  end: ['g2','end']
                }
            }
        """),
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            modifier = Modifier.layoutId("button"),
            onClick = {},
        ) {
            Text(text = stringResource(id = R.string.log_in))
        }
        Text(modifier = Modifier.layoutId("title").background(Color.Red),
            text = "ABC dsa sdfs sdf adfas asdas asdad asdas",// DEF GHI JKL MNO PQR STU VWX YZ ABC DEF",
            style = MaterialTheme.typography.body1,
        )
    }
}

@Preview(group = "new")
@Composable
fun VTest2() {
    ConstraintLayout(
        ConstraintSet("""
            {
                Header: { exportAs: 'test2'},
                g1: { type: 'hGuideline', start: 80 },
                g2: { type: 'hGuideline', end: 80 },
                button: {
                  width: 'spread',
                  start: ['title', 'start', 16],
                  
                  bottom: ['g2', 'bottom']
                },
                title: {
                  width: { value: 'wrap', max: 300 },
                  centerHorizontally: 'parent',
                  top: ['g1', 'top'],
                  bottom: ['g2','bottom']
                }
            }
        """),
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            modifier = Modifier.layoutId("button"),
            onClick = {},
        ) {
            Text(text = stringResource(id = R.string.log_in))
        }
        Text(modifier = Modifier.layoutId("title").background(Color.Red),
            text = "ABC dsa sdfs sdf adfas asdas asdad asdas",// DEF GHI JKL MNO PQR STU VWX YZ ABC DEF",
            style = MaterialTheme.typography.body1,
        )
    }
}
