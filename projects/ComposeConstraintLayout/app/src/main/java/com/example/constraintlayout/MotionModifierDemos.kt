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

package com.example.constraintlayout

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Motion
import androidx.constraintlayout.compose.layoutId
import androidx.constraintlayout.compose.rememberMotionContent
import androidx.constraintlayout.compose.rememberMotionListItems
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale

@Preview
@Composable
fun MotionModifier1() {
    var vertOrHor by remember { mutableStateOf(false) }
    Column {
        // Declare movable content
        val texts = rememberMotionContent {
            Text(text = "Hello", modifier = Modifier.motion())
            Text(
                text = "World",
                modifier = Modifier.motion(tween(2000)) {
                    // KeyFrames applied on every layout change
                    keyAttributes {
                        frame(50f) {
                            rotationZ = 90f
                            scaleY = 2f
                            scaleX = 2f
                        }
                    }
                }
            )
        }
        Motion(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            if (vertOrHor) {
                Column {
                    texts()
                }
            } else {
                Row {
                    texts()
                }
            }
        }
        Button(onClick = { vertOrHor = !vertOrHor }) {
            Text(text = "Toggle")
        }
    }
}

@Preview(group = "card")
@Composable
fun CardTest() {
    val animationSpec: AnimationSpec<Float> = tween(2000)

    // FIXME: Investigate crash when toggling back to true, might be caused by the `image` being out
    //  of bounds during animation from the keyPosition
    var toggleLayout: Boolean by remember { mutableStateOf(true) }
    var toggleImageSize: Boolean by remember { mutableStateOf(true) }
    val time = remember { Instant.now().epochSecond }
    val dateText =
        remember {
            SimpleDateFormat(
                "hh:mma",
                Locale.US
            ).format(Date.from(Instant.ofEpochSecond(time)))
        }

    val image = rememberMotionContent {
        val sizeModifier = if (toggleImageSize) {
            Modifier.fillMaxSize()
        } else {
            Modifier.size(50.dp)
        }
        Image(
            modifier = Modifier
                .motion(animationSpec) {
                    keyAttributes {
                        frame(20f) {
                            translationX = -30f
                            translationY = 0f
                        }
                        frame(50f) {
                            translationX = -50f
                            translationY = -20f
                        }
                        frame(80f) {
                            translationX = -30f
                            translationY = -20f
                        }
                    }
                }
                .then(sizeModifier),
            imageVector = Icons.Default.Person,
            contentDescription = "profile picture"
        )
    }
    val title = rememberMotionContent {
        Text(modifier = Modifier.motion(animationSpec), text = "Card Title", maxLines = 1)
    }
    val date = rememberMotionContent {
        Text(modifier = Modifier.motion(animationSpec), text = dateText, maxLines = 1)
    }

    val description = rememberMotionContent {
        Text(
            modifier = Modifier.motion(animationSpec) {
                keyAttributes {
                    frame(0f) {
                        rotationX = -180f
                    }
                    frame(50f) {
                        rotationX = -100f
                    }
                }
            },
            text = "This is the card's very very very very very very long description."
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Motion(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.0f, true)
        ) {
            if (toggleLayout) {
                Row(
                    Modifier
                        .padding(4.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White)
                        .requiredSize(120.dp, 50.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .widthIn(max = 50.dp)
                                .fillMaxHeight()
                                .aspectRatio(1.0f)
                                .background(Color.LightGray)
                        ) {
                            image()
                        }
                    }
                    Spacer(modifier = Modifier.weight(1.0f, true))
                    Column {
                        date()
                        Spacer(Modifier.weight(1.0f, true))
                        title()
                        Box(modifier = Modifier.size(0.dp)) {
                            description()
                        }
                    }
                }
            } else {
                Column(
                    Modifier
                        .padding(4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .requiredSize(200.dp, 300.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.0f)
                            .background(Color.LightGray)
                    ) {
                        image()
                    }
                    Row(horizontalArrangement = Arrangement.End) {
                        Spacer(Modifier.weight(0.3f))
                        Box(modifier = Modifier.weight(0.3f, true)) {
                            title()
                        }
                        Box(
                            modifier = Modifier.weight(0.3f),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            date()
                        }
                    }
                    description()
                }
            }
        }
        Row {
            Button(onClick = { toggleLayout = !toggleLayout }) {
                Text(text = "Toggle")
            }
            Button(onClick = { toggleImageSize = !toggleImageSize }) {
                Text(text = "Toggle 2")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MotionLookAhead2() {
    var vertOrHor by remember { mutableStateOf(false) }

    // List of movable content
    val movableListItems = rememberMotionListItems(count = 20) { index ->
        MyListItem(
            modifier = Modifier
                .motion(tween(2000), ignoreAxisChanges = true) {
                    keyAttributes {
                        frame(50f) {
                            rotationZ = 40f
                            scaleY = 0.7f
                            scaleX = 0.7f
                        }
                    }
                }
                .size(110.dp, 40.dp),
            count = index
        )
    }
    Column {
        Motion(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            if (vertOrHor) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    movableListItems.emit()
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    movableListItems.emit()
                }
            }
        }
        Button(onClick = { vertOrHor = !vertOrHor }) {
            Text(text = "Toggle")
        }
    }
}

@Preview(group = "Lists")
@Composable
fun MotionLookAhead3() {
    var horVer by remember { mutableStateOf(false) }
    // TODO: Improve animation behavior of scrollable content, only the content shown in the
    //  ViewPort should animate, supporting LazyList might provide a better experience

    // Movable Content is a list of Composables
    val movableListItems = rememberMotionListItems(count = 20) { index ->
        MyListItem(
            modifier = Modifier
                .motion(tween(2000), ignoreAxisChanges = true) {
                    // Move every listed item with this KeyAttribute
                    keyAttributes {
                        frame(50f) {
                            rotationZ = 40f
                            scaleY = 0.7f
                            scaleX = 0.7f
                        }
                    }
                }
                .size(120.dp, 40.dp),
            count = index
        )
    }

    val scrollable = rememberScrollState()
    Column {
        Motion(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            if (horVer) {
                Column(
                    modifier = Modifier.verticalScroll(scrollable, true),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    movableListItems.emit()
                }
            } else {
                Row(
                    modifier = Modifier.horizontalScroll(scrollable, true),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    movableListItems.emit()
                }
            }
        }
        Button(onClick = { horVer = !horVer }) {
            Text(text = "Toggle")
        }
    }
}

private val itemConstraintSet = ConstraintSet {
    val image = createRefFor("image")
    val date = createRefFor("date")
    val title = createRefFor("title")
    constrain(image) {
        centerVerticallyTo(parent)
        start.linkTo(parent.start, 8.dp)
    }
    constrain(date) {
        end.linkTo(parent.end, 0.dp)
        top.linkTo(parent.top, 0.dp)
    }
    constrain(title) {
        bottom.linkTo(parent.bottom)
        start.linkTo(image.end, 0.dp)
    }
}

private const val USE_CL = true

@Composable
private fun MyListItem(modifier: Modifier = Modifier, count: Int) {
    val time = remember { Instant.now().epochSecond }
    val timeOffset = remember { count * IntRange(1800, 3600).random() }
    val dateText =
        remember {
            SimpleDateFormat(
                "hh:mma",
                Locale.US
            ).format(Date.from(Instant.ofEpochSecond(time + timeOffset)))
        }
    if (USE_CL) {
        ConstraintLayout(
            modifier = modifier,
            constraintSet = itemConstraintSet
        ) {
            Image(
                modifier = Modifier.layoutId("image"),
                imageVector = Icons.Default.Person,
                contentDescription = "profile picture"
            )
            Text(modifier = Modifier.layoutId("date"), text = dateText)
            Text(modifier = Modifier.layoutId("title"), text = "Hello, World!")
        }
    } else {
        Row(modifier = modifier) {
            Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                Image(
                    modifier = Modifier.layoutId("image"),
                    imageVector = Icons.Default.Person,
                    contentDescription = "profile picture"
                )
            }
            Column(verticalArrangement = Arrangement.SpaceBetween) {
                Row {
                    Spacer(Modifier.weight(1.0f, true))
                    Text(modifier = Modifier.layoutId("date"), text = dateText, maxLines = 1)
                }
                Spacer(Modifier.weight(1.0f, true))
                Text(modifier = Modifier.layoutId("title"), text = "Hello, World!", maxLines = 1)
            }
        }
    }
}