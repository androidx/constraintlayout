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

@file:OptIn(ExperimentalMotionApi::class)

package com.example.composeanimateddraganddrop

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.node.Ref
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionLayoutScope
import kotlinx.coroutines.channels.Channel

/**
 * A ConstraintLayout that animates based on changes made to [constraintSet].
 *
 * This is similar to using `ConstraintLayout(constraintSet, animateChanges = true)`, but this
 * will always animate forward.
 *
 * Make it animate always forward allows for consistency with start/end concepts. Meaning that the
 * given [constraintSet] is always the end state when animating.
 */
@Composable
fun AnimatedConstraintLayout(
    constraintSet: ConstraintSet,
    modifier: Modifier = Modifier,
    animationSpec: AnimationSpec<Float> = tween(),
    content: @Composable MotionLayoutScope.() -> Unit
) {
    var startConstraint by remember { mutableStateOf(constraintSet) }
    var endConstraint by remember { mutableStateOf(constraintSet) }
    val progress = remember { Animatable(0.0f) }
    val channel = remember { Channel<ConstraintSet>(Channel.CONFLATED) }
    val hasAnimated = remember { Ref<Boolean>().apply { value = false } }

    SideEffect {
        channel.trySend(constraintSet)
    }

    LaunchedEffect(channel) {
        for (constraints in channel) {
            val newConstraints = channel.tryReceive().getOrNull() ?: constraints
            val currentConstraints =
                if (hasAnimated.value == false) startConstraint else endConstraint
            if (newConstraints != currentConstraints) {
                startConstraint = currentConstraints
                endConstraint = newConstraints
                progress.snapTo(0f)
                progress.animateTo(1f, animationSpec)
                hasAnimated.value = true
            }
        }
    }
    MotionLayout(
        start = startConstraint,
        end = endConstraint,
        progress = progress.value,
        modifier = modifier,
        content = content
    )
}