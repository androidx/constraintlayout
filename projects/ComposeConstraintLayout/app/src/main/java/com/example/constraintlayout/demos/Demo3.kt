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
package com.example.constraintlayout.demos
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene

@Preview(group = "new", device = "spec:shape=Normal,width=480,height=800,unit=dp,dpi=440")
@Composable
fun DemoCompose3() {

    var scene =
        """
        {
          ConstraintSets: {
            start: {
              title: {
                bottom: ['box2', 'top', 10],
                start: [ 'box2', 'start',10 ],
                end: ['box2','end',10],
                custom: {
                  sliderValue: 0.0,
                },
              },
              box1: {
                width: 50,
                height: 50,
                centerVertically: 'parent',
                start: ['parent','start', 10 ],
              },
              box2: {
                width: 50,
                height: 50,
                centerVertically: 'parent',
                start: [ 'parent','start', 50],
              },
            },
            end: {
              title: {
                bottom: ['box2','top',10 ],
                start: ['box2', 'start',10],
                end: ['box2','end',10],
                custom: {
                  sliderValue: 100.0,
                },
              },
              box1: {
                width: 'spread',
                height: 20,
                centerVertically: 'parent',
                end: ['parent','end',10 ],
                start: ['parent','start', 10 ],
              },
              box2: {
                width: 50,
                height: 50,
                centerVertically: 'parent',
                end: [ 'parent', 'end',0],
                rotationZ: 720,
              },
            },
          },
          Transitions: {
            default: {
              from: 'start',
              to: 'end',
              onSwipe: {
                anchor: 'box1',
                maxVelocity: 4.2,
                maxAccel: 3,
                direction: 'end',
                side: 'end',
                mode: 'spring',
              },
            },
          },
        }
        """

    MotionLayout(
        modifier = Modifier.fillMaxSize().background(Color.DarkGray),
        motionScene = MotionScene(content = scene)
    )
    {
        val value = motionProperties(id = "title").value.float("sliderValue").toInt() / 10f;

        Text(
            text = value.toString(),
            modifier = Modifier.layoutId("title"),
            color = Color.Magenta
        )

        val gradColors = listOf(Color.White, Color.Gray, Color.Magenta)

        Canvas(
            modifier = Modifier
                .layoutId("box1")
        ) {

            val spring = Path().apply {
                moveTo(0f, 0f);
                for (i in 1..9) {
                    lineTo(
                        i * size.width / 10f,
                        if (i % 2 == 0) 0f else size.height
                    )
                }
                lineTo(size.width, size.height / 2)
            }
            drawPath(
                spring,
                brush = Brush.linearGradient(colors = gradColors),
                style = Stroke(width = 15f, cap = StrokeCap.Butt)
            )
        }
        Canvas(modifier = Modifier.layoutId("box2")) {
            drawCircle(brush = Brush.linearGradient(colors = gradColors))
        }

    }
}

//
//@Composable
// fun Simple() {
//    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
//        Button(modifier = Modifier.constrainAs(createRef()) {
//            centerTo(parent)
//        }) {
//            Text(text = "hello")
//        }
//    }
//}
//
//




