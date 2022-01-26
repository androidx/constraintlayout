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

import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.constraintlayout.verification.ComposableInvocator
import com.example.constraintlayout.verification.motiondsl.MotionTestInfoProviderKey
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class MotionVerificationTest {
    @get:Rule
    val rule = createComposeRule()

    val invocator = ComposableInvocator(
        packageString = "com.example.constraintlayout.verification.motiondsl",
        fileName = "MotionDslVerification"
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
                LocalContext.current.resources.openRawResource(R.raw.motion_results)
                    .bufferedReader()
                    .readText()
        }
        for (i in 0..invocator.max) {
            rule.runOnUiThread {
                // Force a recomposition with the next Composable index
                composableIndex = i
            }
            // Wait for the content to settle
            rule.waitForIdle()

            val motionInfoNode =
                rule.onNode(SemanticsMatcher.keyIsDefined(MotionTestInfoProviderKey))
            motionInfoNode.assertExists()
            val motionInfo = motionInfoNode.fetchSemanticsNode().config[MotionTestInfoProviderKey]
            val boundsStart = fetchBounds()

            motionInfo.setProgress(0.5f)
            rule.waitForIdle()
            motionInfo.recompose()
            rule.waitForIdle()
            val boundsMidPoint = fetchBounds()

            motionInfo.setProgress(1.0f)
            rule.waitForIdle()
            val boundsEnd = fetchBounds()

            assert(boundsStart.size == boundsMidPoint.size && boundsMidPoint.size == boundsEnd.size)
            // Save the result in a composable->result map
            results[fqComposable] =
                MotionTestResult(boundsStart, boundsMidPoint, boundsEnd).printString()
        }
        val baselineResults = parseBaselineResults(baselineRaw)
        checkTest(baselineResults, results)
    }

    private fun fetchBounds(): List<Rect> {
        return rule.onAllNodes(hasParent(SemanticsMatcher.keyIsDefined(MotionTestInfoProviderKey)))
            .fetchSemanticsNodes().map { it.boundsInRoot }
    }
}

private data class MotionTestResult(
    val start: List<Rect>,
    val midPoint: List<Rect>,
    val end: List<Rect>
) {
    fun printString(): String {
        val buffer = StringBuffer()
        start.joinTo(buffer, ",") { it.toString() }
        buffer.append(":")
        midPoint.joinTo(buffer, ",") { it.toString() }
        buffer.append(":")
        end.joinTo(buffer, ",") { it.toString() }
        return buffer.toString()
    }
}