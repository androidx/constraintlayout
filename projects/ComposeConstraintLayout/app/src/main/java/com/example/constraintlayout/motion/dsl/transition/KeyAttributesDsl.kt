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
import androidx.constraintlayout.compose.SwipeDirection
import androidx.constraintlayout.compose.SwipeMode
import androidx.constraintlayout.compose.SwipeSide

@Preview
@Composable
fun KeyAttributesSimpleDslExample() {
    TwoItemLayout(transitionContent = { img1, img2 ->
        onSwipe = OnSwipe(
            anchor = img1,
            side = SwipeSide.Right,
            direction = SwipeDirection.Right,
            mode = SwipeMode.Spring
        )
        keyAttributes(img1) {
            frame(50f) {
                scaleX = 1.5f
                scaleY = 1.5f
            }
        }
        keyAttributes(img2) {
            frame(25f) {
                rotationZ = -45f
            }
            frame(75f) {
                rotationZ = 45f
            }
        }
    })
}