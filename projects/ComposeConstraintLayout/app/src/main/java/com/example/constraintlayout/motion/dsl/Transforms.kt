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

package com.example.constraintlayout.motion.dsl

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.MotionLayout
import com.example.constraintlayout.R

@Preview
@Composable
private fun DslMotionTransformsExample() {
    var animateToEnd by remember { mutableStateOf(false) }
    val progress by animateFloatAsState(
        targetValue = if (animateToEnd) 1f else 0f,
        animationSpec = tween(2000)
    )

    val start = ConstraintSet {
        val image1 = createRefFor("image1")
        val image2 = createRefFor("image2")
        createVerticalChain(image1, image2)
        constrain(image1) {
            width = Dimension.value(200.dp)
            height = Dimension.value(200.dp)
            centerHorizontallyTo(parent)
            rotationZ = 45f
        }
        constrain(image2) {
            width = Dimension.value(200.dp)
            height = Dimension.value(200.dp)
            centerHorizontallyTo(parent)
        }
    }
    val end = ConstraintSet(start) {
        constrain(createRefFor("image1")) {
            rotationZ = -45f
        }
    }

    Column {
        MotionLayout(
            modifier = Modifier.fillMaxWidth().height(500.dp),
            start = start,
            end = end,
            progress = progress) {
            Image(
                modifier = Modifier.layoutId("image1"),
                painter = painterResource(id = R.drawable.intercom_snooze),
                contentDescription = null
            )
            Image(
                modifier = Modifier.layoutId("image2"),
                painter = painterResource(id = R.drawable.intercom_snooze),
                contentDescription = null
            )
        }
        Button(onClick = { animateToEnd = !animateToEnd }) {
            Text("Run")
        }
    }

}