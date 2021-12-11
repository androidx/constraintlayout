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



@Preview(group = "VTest02d")
@Composable
fun VTest02a() {
    ConstraintLayout(
        ConstraintSet("""
            {
                Header: { exportAs: 'test1'},
                guid1: { type: 'vGuideline', start: 80 },
                guide2: { type: 'vGuideline', end: 80 },
                button1: {
                  width: 'spread',
                  top: ['title', 'bottom', 16],
                  start: ['guid1', 'start'],
                  end: ['guide2', 'end']
                },
                title: {
                  width: { value: 'wrap', max: 300 },
                  centerVertically: 'parent',
                  start: ['guid1', 'start'],
                  end: ['guide2','end']
                }
            }
        """),
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            modifier = Modifier.layoutId("button1"),
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

@Preview(group = "VTest02d")
@Composable
fun VTest02b() {
    ConstraintLayout(
        ConstraintSet("""
            {
                Header: { exportAs: 'test2'},
                gl1: { type: 'hGuideline', start: 80 },
                gl2: { type: 'hGuideline', end: 80 },
                button2: {
                  width: 'spread',
                  start: ['title', 'start', 16],
                  bottom: ['gl2', 'bottom'],
                  rotationZ: 32,
                },
                title: {
                  width: { value: 'wrap', max: 300 },
                  centerHorizontally: 'parent',
                  top: ['gl1', 'top'],
                  bottom: ['gl2','bottom']
                }
            }
        """),
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            modifier = Modifier.layoutId("button2"),
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


@Preview(group = "VTest02d")
@Composable
public fun VTest02c() {
    ConstraintLayout(
        ConstraintSet("""
            {
                Header: { exportAs: 'test3'},
             
                button3: {
                  width: 'spread',
                  centerHorizontally:  'parent',
                  centerVertically: 'parent',
              
                },
                title: {
                  
                  centerHorizontally: 'button3',
                  top: ['button3', 'bottom'],
                
                }
            }
        """),
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            modifier = Modifier.layoutId("button3"),
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

@Preview(group = "VTest02d")
@Composable
public fun VTest02d() {
    ConstraintLayout(
        ConstraintSet("""
            {
                Header: { exportAs: 'test4'},
             
                button4: {
                  width: 'wrap',
                  centerHorizontally:  'parent',
                  centerVertically: 'parent',
              
                },
                title: {
                   width: '200',
                  centerHorizontally: 'button4',
                  top: ['button4', 'bottom',32],
                
                }
            }
        """),
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            modifier = Modifier.layoutId("button4"),
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
public fun VTest02e() {
    ConstraintLayout(
        ConstraintSet("""
            {
                Header: { exportAs: 'test5'},
             
                button5: {
                  width: 'wrap',
                  centerHorizontally:  'parent',
                  centerVertically: 'parent',
              
                },
                title: {
                   width: 'spread',
                  centerHorizontally: 'button5',
                  bottom: ['parent5', 'bottom',20],
                
                }
            }
        """),
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            modifier = Modifier.layoutId("button5"),
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
