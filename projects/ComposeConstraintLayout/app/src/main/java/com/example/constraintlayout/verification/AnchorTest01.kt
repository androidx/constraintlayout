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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import com.example.constraintlayout.R

@Preview(group = "AnchorTest01")
@Composable
public fun AnchorTest01() {
    ConstraintLayout(
        ConstraintSet("""
            {
                Header: { exportAs: 'anchortest01' },
             
                button1: {
                  height: { value: '20%' },
                  centerVertically: 'parent',
                  start: ['title', 'start']
                },
                button2: {
                  height: { value: '20%' },
                  centerVertically: 'parent',
                  start: ['title', 'end']
                },
                button3: {
                  height: { value: '20%' },
                  centerVertically: 'parent',
                  start: ['title', 'start', 100]
                },
                button4: {
                  height: { value: '20%' },
                  visibility: 'gone',
                  top: ['title', 'bottom']
                },
                button5: {
                  height: { value: '20%' },
                  top: ['title', 'bottom'],
                  start: ['button4', 'start', 200, 100]
                },
                title: {
                  height: '10%',
                  top: ['parent', 'top']
                }
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
        Text(modifier = Modifier.layoutId("title").background(Color.Red),
            text = "ABC dsa sdfs sdf ABC dsa sdfs sdf",// DEF GHI JKL MNO PQR STU VWX YZ ABC DEF",
            style = MaterialTheme.typography.body1,
        )
    }
}

@Preview(group = "AnchorTest01")
@Composable
fun AnchorTest02() {
    ConstraintLayout(
        ConstraintSet("""
            {
                Header: { exportAs: 'anchortest02' },
             
                button1: {
                  height: { value: '20%' },
                  centerVertically: 'parent',
                  end: ['title', 'start']
                },
                button2: {
                  height: { value: '20%' },
                  centerVertically: 'parent',
                  end: ['title', 'end']
                },
                button3: {
                  height: { value: '20%' },
                  centerVertically: 'parent',
                  end: ['title', 'end', 100]
                },
                button4: {
                  height: { value: '20%' },
                  visibility: 'gone',
                  end: ['parent', 'end'],
                  top: ['title', 'bottom']
                },
                button5: {
                  height: { value: '20%' },
                  top: ['title', 'bottom'],
                  end: ['button4', 'end', 300, 100]
                },
                title: {
                  height: '10%',
                  top: ['parent', 'top'],
                  end: ['parent', 'end']
                }
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
        Text(modifier = Modifier.layoutId("title").background(Color.Red),
            text = "ABC dsa sdfs sdf ABC dsa sdfs sdf",// DEF GHI JKL MNO PQR STU VWX YZ ABC DEF",
            style = MaterialTheme.typography.body1,
        )
    }
}

@Preview(group = "AnchorTest01")
@Composable
fun AnchorTest03() {
    ConstraintLayout(
        ConstraintSet("""
            {
                Header: { exportAs: 'anchortest03' },
             
                button1: {
                  height: { value: '5%' }, 
                  end: ['parent', 'end'],
                  top: ['title', 'top']
                },
                button2: {
                  height: { value: '5%' },
                  top: ['title', 'bottom']
                },
                button3: {
                  height: { value: '5%' },
                  top: ['title', 'bottom', 100]
                },
                button4: {
                  height: { value: '5%' },
                  visibility: 'gone',
                  end: ['title', 'end'],
                  top: ['title', 'bottom', 100]
                },
                button5: {
                  height: { value: '5%' },
                  end: ['title', 'end'],
                  top: ['button4', 'bottom', 300, 100]
                },
                title: {
                  height: '10%',
                  top: ['parent', 'top']
                }
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
        Text(modifier = Modifier.layoutId("title").background(Color.Red),
            text = "ABC dsa sdfs sdf ABC dsa sdfs sdf",// DEF GHI JKL MNO PQR STU VWX YZ ABC DEF",
            style = MaterialTheme.typography.body1,
        )
    }
}

@Preview(group = "AnchorTest01")
@Composable
fun AnchorTest04() {
    ConstraintLayout(
        ConstraintSet("""
            {
                Header: { exportAs: 'anchortest04' },
             
                button1: {
                  height: { value: '5%' }, 
                  end: ['parent', 'end'],
                  bottom: ['title', 'top']
                },
                button2: {
                  height: { value: '5%' },
                  start: ['title', 'end'],
                  bottom: ['title', 'bottom']
                },
                button3: {
                  height: { value: '5%' },
                  bottom: ['title', 'bottom', 100]
                },
                button4: {
                  height: { value: '5%' },
                  visibility: 'gone',
                  end: ['title', 'end'],
                  bottom: ['title', 'bottom', 100]
                },
                button5: {
                  height: { value: '5%' },
                  end: ['title', 'end'],
                  bottom: ['button4', 'bottom', 300, 100]
                },
                title: {
                  height: '10%',
                  centerVertically: 'parent',
                }
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
        Text(modifier = Modifier.layoutId("title").background(Color.Red),
            text = "ABC dsa sdfs sdf ABC dsa sdfs sdf",// DEF GHI JKL MNO PQR STU VWX YZ ABC DEF",
            style = MaterialTheme.typography.body1,
        )
    }
}

@Preview(group = "AnchorTest01")
@Composable
fun AnchorTest05() {
    ConstraintLayout(
        ConstraintSet("""
            {
                Header: { exportAs: 'anchortest05' },
             
                button1: {
                  height: { value: '10%' }, 
                  circular: ['title']
                },
                button2: {
                  height: { value: '20%' },
                  circular: ['title',50]
                },
                button3: {
                  height: { value: '20%' },
                  circular: ['title', 90, 200]
                },
                title: {
                  height: '10%',
                  centerVertically: 'parent',
                  centerHorizontally: 'parent'
                }
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
        Text(modifier = Modifier.layoutId("title").background(Color.Red),
            text = "ABC dsa sdfs sdf ABC dsa sdfs sdf",// DEF GHI JKL MNO PQR STU VWX YZ ABC DEF",
            style = MaterialTheme.typography.body1,
        )
    }
}
