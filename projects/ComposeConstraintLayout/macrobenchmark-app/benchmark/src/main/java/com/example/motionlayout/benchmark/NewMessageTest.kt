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

import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.uiautomator.By

internal fun MacrobenchmarkRule.testNewMessage(
    mode: NewMessageMode
) {
    motionBenchmark(
        mode.composableName
    ) {
        val toFab = device.findObject(By.res("Fab"))
        val toFull = device.findObject(By.res("Full"))
        val toMini = device.findObject(By.res("Mini"))

        toMini.click()
        device.waitForIdle()

        toFab.click()
        device.waitForIdle()

        toFull.click()
        device.waitForIdle()
    }
}

internal enum class NewMessageMode(val composableName: String) {
    Json("NewMessageJson"),
    Dsl("NewMessageDsl")
}