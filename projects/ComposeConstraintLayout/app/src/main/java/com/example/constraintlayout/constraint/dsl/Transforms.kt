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

package com.example.constraintlayout.constraint.dsl

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.constraintlayout.R

@Preview
@Composable
private fun DslTransformsExample() {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val (image1, image2, image3, image4) = createRefs()
        createVerticalChain(image1, image2, image3, image4)
        Image(
            modifier = Modifier.constrainAs(image1) {
                centerHorizontallyTo(parent)
                width = Dimension.value(201.dp)
                height = Dimension.value(179.dp)
                alpha = 0.5f
                scaleX = 0.5f
                scaleY = 0.5f
            },
            painter = painterResource(id = R.drawable.intercom_snooze),
            contentDescription = null
        )
        Image(
            modifier = Modifier.constrainAs(image2) {
                centerHorizontallyTo(parent)
                width = Dimension.value(201.dp)
                height = Dimension.value(179.dp)
                rotationX = 45f
                rotationZ = 90f
            },
            painter = painterResource(id = R.drawable.intercom_snooze),
            contentDescription = null
        )
        Image(
            modifier = Modifier.constrainAs(image3) {
                centerHorizontallyTo(parent)
                width = Dimension.value(201.dp)
                height = Dimension.value(179.dp)
                translationX = (-50).dp
                translationY = 50.dp
                translationZ = 5.dp
            },
            painter = painterResource(id = R.drawable.intercom_snooze),
            contentDescription = null
        )
        Image(
            modifier = Modifier.constrainAs(image4) {
                centerHorizontallyTo(parent)
                width = Dimension.value(201.dp)
                height = Dimension.value(179.dp)
                // Rotate the image 90 deg from the right edge of the original bounds
                // The image center should then be at Point(x=width, y=0) and rotated 90 deg,
                // where Point(0,0) is the top left corner of the original bounds.
                pivotX = 1f
                rotationZ = 90f
            },
            painter = painterResource(id = R.drawable.intercom_snooze),
            contentDescription = null
        )
    }
}