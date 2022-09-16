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
fun KeyCyclesSimpleDslExample() {
    TwoItemLayout(transitionContent = { img1, img2 ->
        onSwipe = OnSwipe(
            anchor = img1,
            side = SwipeSide.Right,
            direction = SwipeDirection.Right,
            mode = SwipeMode.Spring
        )
        keyCycles(img1) {
            // TODO: Consider supporting common/base values declared at this level, eg: offset = 1
            frame(0) {
                scaleX = 0f
                period = 0f
                offset = 1f
            }
            frame(50) {
                scaleX = 3f
                period = 2f
                offset = 1f
            }
            frame(100) {
                scaleX = 0f
                period = 0f
                offset = 1f
            }
        }
        keyCycles(img2) {
            // TODO: Consider supporting common/base values declared at this level, eg: translationX = 50f
            frame(0) {
                translationX = 50f
                translationY = 50f
                period = 0f
            }
            frame(50) {
                translationX = 50f
                translationY = 50f
                period = 3f
            }
            frame(100) {
                translationX = 50f
                translationY = 50f
                period = 0f
            }
        }
    })
}