/*
 * Copyright (C) 2023 The Android Open Source Project
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

package android.support.drag2d

import android.support.drag2d.compose.Material2DAnimationSpec
import android.support.drag2d.lib.MaterialEasing
import android.support.drag2d.lib.MaterialVelocity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.util.addPointerInputChange
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Preview
@Composable
fun Material2DMotionPreview() {
    val duration = remember { mutableStateOf(1200f) }
    val maxVelocity = remember { mutableStateOf(2000f) }
    val maxAcceleration = remember { mutableStateOf(2000f) }
    val currentEasing = remember { mutableStateOf("EaseOutBack") }
    val nameToEasing: Map<String, MaterialVelocity.Easing> = remember {
        mapOf(
            "Decelerate" to MaterialEasing.DECELERATE,
            "Linear" to MaterialEasing.LINEAR,
            "Overshoot" to MaterialEasing.OVERSHOOT,
            "EaseOutSine" to MaterialEasing.EASE_OUT_SINE,
            "EaseOutCubic" to MaterialEasing.EASE_OUT_CUBIC,
            "EaseOutQuint" to MaterialEasing.EASE_OUT_QUINT,
            "EaseOutCirc" to MaterialEasing.EASE_OUT_CIRC,
            "EaseOutQuad" to MaterialEasing.EASE_OUT_QUAD,
            "EaseOutQuart" to MaterialEasing.EASE_OUT_QUART,
            "EaseOutExpo" to MaterialEasing.EASE_OUT_EXPO,
            "EaseOutBack" to MaterialEasing.EASE_OUT_BACK,
            "EaseOutElastic" to MaterialEasing.EASE_OUT_ELASTIC
        )
    }
    Column(
        Modifier
            .fillMaxWidth()
    ) {
        val touchUpIndex = remember { mutableStateOf(Integer.MAX_VALUE) }
        val accumulator = remember { arrayListOf<Offset>() }
        val offset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
        val referenceOffset = remember { mutableStateOf(Offset.Zero) }

        // Box that includes the draggable item and touch input drawing
        Box(
            Modifier
                .fillMaxWidth()
                .weight(1f, true)
                .drawWithContent {
                    drawContent()
                    // Draw recorded touch input points to reflect the behavior
                    // TODO: Draw curves from Velocity2D
                    offset.value // Trigger recomposition

                    if (accumulator.size < 1) {
                        return@drawWithContent
                    }
                    for (i in 0 until accumulator.size) {
                        drawLine(
                            color = if (i > touchUpIndex.value) Color.Red else Color.Blue,
                            start = (accumulator.getOrNull(i - 1)
                                ?: Offset.Zero) + referenceOffset.value,
                            end = accumulator[i] + referenceOffset.value,
                            strokeWidth = 2f
                        )
                    }
                },
            contentAlignment = { size, space, _ ->
                Offset(
                    (space.width / 2f) - (size.width / 2f),
                    (space.height / 2f) - (size.height / 2f)
                ).round()
            }
        ) {
            val color = remember { Color.hsv(IntRange(0, 360).random().toFloat(), 0.5f, 0.8f) }
            val velocityTracker = remember { VelocityTracker() }
            val scope = rememberCoroutineScope()
            Box(
                modifier = Modifier
                    .onPlaced { layoutCoordinates ->
                        val parentSize = layoutCoordinates.parentCoordinates?.size ?: IntSize.Zero
                        referenceOffset.value = Offset(
                            parentSize.width / 2f,
                            parentSize.height / 2f
                        )
                    }
                    .offset { offset.value.round() }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {
                                touchUpIndex.value = Integer.MAX_VALUE
                                accumulator.clear()
                            },
                            onDragEnd = {
                                scope.launch {
                                    touchUpIndex.value = accumulator.size - 1
                                    val initialVelocity = velocityTracker.calculateVelocity()

                                    // Velocity tracker has a tendency to fail measuring velocity
                                    offset.animateTo(
                                        targetValue = Offset.Zero,
                                        animationSpec = Material2DAnimationSpec(
                                            duration.value.roundToInt(),
                                            maxVelocity.value,
                                            maxAcceleration.value,
                                            nameToEasing[currentEasing.value]
                                                ?: nameToEasing.values.first()
                                        ),
                                        initialVelocity = Offset(
                                            initialVelocity.x,
                                            initialVelocity.y
                                        )
                                    ) {
                                        accumulator.add(this.value)
                                    }
                                    velocityTracker.resetTracking()
                                }
                            }
                        ) { change, dragAmount ->
                            velocityTracker.addPointerInputChange(change)
                            val position = offset.value + dragAmount
                            accumulator.add(position)
                            scope.launch {
                                offset.snapTo(position)
                            }
                        }
                    }
                    .size(80.dp)
                    .background(color, CardDefaults.shape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Hello")
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Duration: ${duration.value.roundToInt()}ms")
            Spacer(Modifier.width(8.dp))
            Slider(
                value = duration.value,
                onValueChange = { duration.value = it },
                valueRange = 100f..4000f,
                steps = ((4000f - 100f) / 100f).roundToInt() - 1,
                modifier = Modifier.weight(1f, true)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "MaxVelocity: ${maxVelocity.value.roundToInt()}")
            Spacer(Modifier.width(8.dp))
            Slider(
                value = maxVelocity.value,
                onValueChange = { maxVelocity.value = it },
                valueRange = remember { 100f..4000f },
                steps = remember { ((4000f - 100f) / 100f).roundToInt() - 1 },
                modifier = Modifier.weight(1f, true)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "MaxAcceleration: ${maxAcceleration.value.roundToInt()}")
            Spacer(Modifier.width(8.dp))
            Slider(
                value = maxAcceleration.value,
                onValueChange = { maxAcceleration.value = it },
                valueRange = remember { 100f..4000f },
                steps = remember { ((4000f - 100f) / 100f).roundToInt() - 1 },
                modifier = Modifier.weight(1f, true)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            val isExpanded = remember { mutableStateOf(false) }
            Button(onClick = {
                isExpanded.value = true
            }) {
                Text(text = "Easing: ${currentEasing.value}")
            }
            DropdownMenu(
                expanded = isExpanded.value,
                onDismissRequest = { isExpanded.value = false }
            ) {
                nameToEasing.keys.forEach { name ->
                    DropdownMenuItem(
                        text = { Text(text = name) },
                        onClick = {
                            currentEasing.value = name
                        }
                    )
                }
            }
        }
    }
}