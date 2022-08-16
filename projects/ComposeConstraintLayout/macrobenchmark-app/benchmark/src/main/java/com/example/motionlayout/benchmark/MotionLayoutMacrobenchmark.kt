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

package com.example.motionlayout.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val PACKAGE = "com.example.macrobenchmark"
private const val ITERATIONS = 10

@RunWith(AndroidJUnit4::class)
class MotionLayoutMacrobenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    /**
     * Transitions the Layout through its three different ConstraintSets using the MotionScene DSL.
     */
    @Test
    fun messageDsl() = benchmarkRule.testNewMessage(NewMessageMode.Dsl)

    /**
     * Transitions the Layout through its three different ConstraintSets using the MotionScene JSON.
     */
    @Test
    fun messageJson() = benchmarkRule.testNewMessage(NewMessageMode.Json)

    @Test
    fun collapsibleToolbar() = benchmarkRule.testCollapsibleToolbar()
}

/**
 * The base method to benchmark FrameTimings of a Composable from the macrobenchmark-app module.
 *
 * [composableName] should be a registered Composable in **MotionLayoutBenchmarkActivity**
 *
 * The [setupBlock] is run after the activity starts with the given [composableName]. You may use
 * this as a chance to set the UI in the way you wish it to be measured.
 *
 * The [measureBlock] is called after the setup. [FrameTimingMetric] measures UI performance during
 * this block.
 */
internal fun MacrobenchmarkRule.motionBenchmark(
    composableName: String,
    setupBlock: MacrobenchmarkScope.() -> Unit = {},
    measureBlock: MacrobenchmarkScope.() -> Unit
) {
    measureRepeated(
        packageName = PACKAGE,
        metrics = listOf(FrameTimingMetric()),
        compilationMode = CompilationMode.None(),
        iterations = ITERATIONS,
        startupMode = StartupMode.WARM,
        setupBlock = {
            startActivityAndWait {
                it.putExtra("ComposableName", composableName)
            }
            device.waitForIdle()
            setupBlock()
        },
        measureBlock = measureBlock
    )
}