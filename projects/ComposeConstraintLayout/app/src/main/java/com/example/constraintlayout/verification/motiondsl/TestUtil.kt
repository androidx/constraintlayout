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

package com.example.constraintlayout.verification.motiondsl

import androidx.annotation.FloatRange
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.semantics
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionLayoutScope
import kotlinx.coroutines.launch

interface MotionTestInfo {
    fun setProgress(
        @FloatRange(from = 0.0, to = 1.0, fromInclusive = true, toInclusive = true)progress: Float
    )

    fun recompose()
}

internal val MotionTestInfoProviderKey = SemanticsPropertyKey<MotionTestInfo>("MotionTestInfo")

internal var SemanticsPropertyReceiver.motionTestProvider by MotionTestInfoProviderKey


@Suppress("NOTHING_TO_INLINE")
@Composable
internal inline fun MotionTestWrapper(
    modifier: Modifier,
    start: ConstraintSet,
    end: ConstraintSet,
    progress: Float,
    crossinline onRecompose: @DisallowComposableCalls () -> Unit,
    crossinline content: @Composable MotionLayoutScope.() -> Unit
) {
    val internalProgress = remember { mutableStateOf(0f) }
    val lastParameterProgress = remember { mutableStateOf(progress) }

    val testProgress = remember { Animatable(initialValue = 0.0f) }
    val animateScope = rememberCoroutineScope()

    if (progress != lastParameterProgress.value) {
        internalProgress.value = progress
        lastParameterProgress.value = progress
    }
    else {
        internalProgress.value = testProgress.value
    }

    val testProvider = remember {
        object : MotionTestInfo {
            override fun setProgress(progress: Float) {
                animateScope.launch {
                    testProgress.animateTo(
                        progress,
                        tween(3000)
                    )
                }
            }

            override fun recompose() {
                onRecompose()
            }
        }
    }

    MotionLayout(
        modifier = modifier.semantics { motionTestProvider = testProvider },
        start = start,
        end = end,
        progress = internalProgress.value,
        content = content
    )
}