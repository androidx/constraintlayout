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

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import androidx.constraintlayout.compose.layoutId

@Preview(group = "new", device = "spec:shape=Normal,width=480,height=800,unit=dp,dpi=440")
@Composable
fun FunCircles() {

    var scene =
        """
        {
          ConstraintSets: {
            end: {
              main_circle: {
              width: 400,
              height: 400,
              center: 'parent',
              },
              ball0: { width: 40, height: 40,  circular: ['main_circle', 0, 0] },
              ball1: { width: 40, height: 40,  circular: ['main_circle', 22, 0] },
              ball2: { width: 40, height: 40,  circular: ['main_circle', 45, 0] },
              ball3: { width: 40, height: 40,  circular: ['main_circle', 67, 0] },
              ball4: { width: 40, height: 40,  circular: ['main_circle', 90, 0] },
              ball5: { width: 40, height: 40,  circular: ['main_circle', 112, 0] },
              ball6: { width: 40, height: 40,  circular: ['main_circle', 135, 0] },
              ball7: { width: 40, height: 40,  circular: ['main_circle', 157, 0] },
              
              line0: {width: 400, height: 1, center: 'main_circle', rotationZ: 90},
              line1: {width: 400, height: 1, center: 'main_circle', rotationZ: 67.5},
              line2: {width: 400, height: 1, center: 'main_circle', rotationZ: 45},
              line3: {width: 400, height: 1, center: 'main_circle', rotationZ: 22.5},
              line4: {width: 400, height: 1, center: 'main_circle', rotationZ: 0},
              line5: {width: 400, height: 1, center: 'main_circle', rotationZ: 157.5},
              line6: {width: 400, height: 1, center: 'main_circle', rotationZ: 135},
              line7: {width: 400, height: 1, center: 'main_circle', rotationZ: 112.5},
            },
            start: {
             Extends: 'end',

             ball0: {alpha: 0},
             ball1: {alpha: 0},
             ball2: {alpha: 0},
             ball3: {alpha: 0},
             ball4: {alpha: 0},
             ball5: {alpha: 0},
             ball6: {alpha: 0},
             ball7: {alpha: 0},

             line0: {alpha: 0},
             line1: {alpha: 0},
             line2: {alpha: 0},
             line3: {alpha: 0},
             line4: {alpha: 0},
             line5: {alpha: 0},
             line6: {alpha: 0},
             line7: {alpha: 0},

          },
          },
          
          Transitions: {
            default: {
              from: 'start',
              to: 'end',
       
              KeyFrames: {
                 KeyAttributes : [
                 { target:['ball0','line0'], frames:[0,1], alpha:[0, 1] },
                 { target:['ball1','line1'], frames:[2,3], alpha:[0, 1] },
                 { target:['ball2','line2'], frames:[3,4], alpha:[0, 1] },
                 { target:['ball3','line3'], frames:[4,5], alpha:[0, 1] },
                 { target:['ball4','line4'], frames:[5,6], alpha:[0, 1] },
                 { target:['ball5','line5'], frames:[6,7], alpha:[0, 1] },
                 { target:['ball6','line6'], frames:[7,8], alpha:[0, 1] },
                 { target:['ball7','line7'], frames:[8,9], alpha:[0, 1] },
                 ],
                 

                 KeyCycles: [
              { target:['ball0'], frames:[50], period:5, phase:  0.0, translationX:  0, translationY: 180},
              { target:['ball1'], frames:[50], period:5, phase: 22.5, translationX: 69, translationY: 166},
              { target:['ball2'], frames:[50], period:5, phase: 45.0, translationX:127, translationY: 127},
              { target:['ball3'], frames:[50], period:5, phase: 67.5, translationX:166, translationY: 69},
              { target:['ball4'], frames:[50], period:5, phase: 90.0, translationX:180, translationY:  0},
              { target:['ball5'], frames:[50], period:5, phase:112.5, translationX:166, translationY:-69},
              { target:['ball6'], frames:[50], period:5, phase:135.0, translationX:127, translationY:-127},
              { target:['ball7'], frames:[50], period:5, phase:157.5, translationX: 69, translationY:-166},
                     
                     
                      ]
                    }
          },
        }
        }
        """
    var animateToEnd by remember { mutableStateOf(false) }

    val progress by animateFloatAsState(
        targetValue = if (animateToEnd) 1f else 0f,
        animationSpec = tween(100000)
    )
    Column(modifier = Modifier.background(Color.White)) {
        Button(
            onClick = { animateToEnd = !animateToEnd },
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp)
        ) {
            Text(text = "Run")

        }

        MotionLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            motionScene = MotionScene(content = scene),
            progress = progress
        )
        {

            Box(
                modifier = Modifier
                    .background(Color.Red, shape = CircleShape)
                    .layoutId("main_circle")
            )

            for (i in 0..7) {
                Box(
                    modifier = Modifier
                        .background(Color.Black)
                        .layoutId("line$i")
                )

            }

            for (i in 0..7) {
                Box(
                    modifier = Modifier
                        .background(Color.White, shape = CircleShape)
                        .layoutId("ball$i")
                )
            }
        }
    }
}





