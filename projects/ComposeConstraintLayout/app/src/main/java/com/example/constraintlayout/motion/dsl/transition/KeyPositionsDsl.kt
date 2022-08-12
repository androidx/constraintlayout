/*
 * Copyright (C) 2022 The Android Open Source Project
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

package com.example.constraintlayout.motion.dsl.transition

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.OnSwipe
import androidx.constraintlayout.compose.RelativePosition
import androidx.constraintlayout.compose.SwipeDirection
import androidx.constraintlayout.compose.SwipeMode
import androidx.constraintlayout.compose.SwipeSide
import androidx.constraintlayout.compose.Transition
import androidx.constraintlayout.compose.TransitionScope

@Preview
@Composable
fun KeyPositionsSimpleDslExample() {
    TwoItemLayout(transition = Transition {
        onSwipe = OnSwipe(
            anchor = "img1",
            side = SwipeSide.Right,
            direction = SwipeDirection.Right,
            mode = SwipeMode.Spring
        )
        keyPositions("img1") {
            type = RelativePosition.Delta
            frame(50f) {
                percentX = -0.2f
            }
        }
        keyPositions("img2") {
            type = RelativePosition.Parent
            frame(25f) {
                percentX = 0.2f
            }
            frame(50f) {
                percentX = 0.5f

            }
            frame(75f) {
                percentX = 0.2f
            }
        }
    })
}