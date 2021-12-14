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

package com.example.constraintlayout.verification.dsl

import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension

val TwoBoxConstraintSet = ConstraintSet {
    val box1 = createRefFor("box1")
    val box2 = createRefFor("box2")
    constrain(box1) {
        centerTo(parent)
        width = Dimension.value(30.dp)
        height = Dimension.value(100.dp)
    }
    constrain(box2) {
        width = Dimension.value(50.dp)
        height = Dimension.value(50.dp)
        centerHorizontallyTo(parent)
        top.linkTo(box1.bottom, 8.dp)
    }
}