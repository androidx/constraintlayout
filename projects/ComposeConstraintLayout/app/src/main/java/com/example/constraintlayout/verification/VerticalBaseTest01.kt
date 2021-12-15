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



@Preview(group = "VerticalTestBase01")
@Composable
fun VerticalTestBase01() {
    ConstraintLayout(
        ConstraintSet("""
            {
                Header: { exportAs: 'vbasetest01' },
             
                button: {
                  height: 99,
                  centerVertically: 'parent',
                  centerHorizontally: 'parent',
                  start: ['title', 'start']
                },
                title: {
                  height: '32%',
                  start: ['button1', 'start']
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

@Preview(group = "VerticalTestBase01")
@Composable
fun VerticalTestBase02() {
    ConstraintLayout(
        ConstraintSet("""
            {
                Header: { exportAs: 'vbasetest02' },
             
                button: {
                  height: 'parent',
                  centerVertically: 'parent',
                  start: ['title', 'end']
                },
                title: {
                  height: 'wrap',
                  start: ['parent', 'start']
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


@Preview(group = "VerticalTestBase01")
@Composable
public fun VerticalTestBase03() {
    ConstraintLayout(
        ConstraintSet("""
            {
                Header: { exportAs: 'vbasetest03' },
             
                button: {
                  height: 'spread',
                  centerVertically: 'parent',
                  top: ['title', 'top'],
                  bottom: ['title', 'bottom'],
                  start: ['title', 'end']
                },
                title: {
                  height: '50%',
                  top: ['parent', 'top']
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

@Preview(group = "VerticalTestBase01")
@Composable
public fun VerticalTestBase04() {
    ConstraintLayout(
        ConstraintSet("""
            {
                Header: { exportAs: 'vbasetest04' },
             
                button: {
                  height: 'preferWrap',
                  centerVertically: 'parent',
                  top: ['title', 'top'],
                  bottom: ['title', 'bottom'],
                  start: ['title', 'end']
                },
                title: {
                  height: '50%',
                  top: ['parent', 'top']
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

@Preview(group = "VerticalTestBase01")
@Composable
public fun VerticalTestBase05() {
    ConstraintLayout(
        ConstraintSet("""
            {
                Header: { exportAs: 'vbasetest05' },
             
                button: {
                  height: { value: 'parent' },
                  start: ['title', 'end']
                },
                title: {
                  height: { value: '32%' },
                  bottom: ['parent', 'bottom']
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