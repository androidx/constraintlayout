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

package com.example.constraintlayout

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.constraintlayout.compose.DesignInfoDataKey
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.example.constraintlayout.verification.ComposableInvocator

/**
 * Unit test to verify layout results.
 *
 * Currently only for Composables written with the Kotlin DSL. See [ComposableInvocator] for
 * details.
 *
 * Run tests using device: Pixel 3 on API 30.
 */
@MediumTest
@RunWith(AndroidJUnit4::class)
class VerificationTest {
    @get:Rule
    val rule = createComposeRule()

    val invocator = ComposableInvocator(
        packageString = "com.example.constraintlayout.verification.dsl",
        fileName = "DslVerification"
    )

    @Test
    fun verifyComposables() {
        // TODO: Test doesn't work very well with Json, some part in the helper parser is not
        //  stable, either from user-string to binary or binary to result-string
        val results = HashMap<String, String>()
        var composableIndex by mutableStateOf(0) // Observable state that we'll use to change the content in a recomposition
        var fqComposable = ""
        var baselineRaw = ""
        rule.setContent {
            // Set the content to the Composable at the given index
            fqComposable = invocator.invokeComposable(composableIndex, currentComposer)
            // We can only get the Resources in this context
            baselineRaw =
                LocalContext.current.resources.openRawResource(R.raw.results).bufferedReader()
                    .readText()
        }
        for (i in 0..invocator.max) {
            rule.runOnUiThread {
                // Force a recomposition with the next Composable index
                composableIndex = i
            }
            // Wait for the content to settle
            rule.waitForIdle()
            val nodeInteration = rule.onNode(SemanticsMatcher.keyIsDefined(DesignInfoDataKey))
            nodeInteration.assertExists()
            // Get the output from 'getDesignInfo'
            // A json with the constraints and bounds of the widgets in the layout
            val result =
                nodeInteration.fetchSemanticsNode().config[DesignInfoDataKey].getDesignInfo(
                    startX = 0,
                    startY = 0,
                    args = 0b10.toString() // Second bit from the right for Bounds only
                )

            // Save the result in a composable->result map
            results[fqComposable] = result
        }
        checkTest(baselineRaw, results)
    }
}