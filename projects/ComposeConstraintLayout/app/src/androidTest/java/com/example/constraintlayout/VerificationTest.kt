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
import junit.framework.TestCase

@MediumTest
@RunWith(AndroidJUnit4::class)
class  VerificationTest {
    @get:Rule
    val rule = createComposeRule()

    val invocator = ComposableInvocator()

    @Test
    fun verifyComposables() {
        val results = HashMap<String, String>()
        var composableIndex by mutableStateOf(0) // Observable state that we'll use to change the content in a recomposition
        var fqComposable = ""
        var baselineRaw = ""
        rule.setContent {
            // Set the content to the Composable at the given index
            fqComposable = invocator.invokeComposable(composableIndex, currentComposer)
            // We can only get the Resources in this context
            baselineRaw =
                LocalContext.current.resources.openRawResource(R.raw.results).bufferedReader().readText()
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
            val result = nodeInteration.fetchSemanticsNode().config[DesignInfoDataKey].getDesignInfo(0, 0, "")

            // Save the result in a composable->result map
            results[fqComposable] = result
        }
        val baselineResults = parseBaselineResults(baselineRaw)
        checkTest(baselineResults, results)
    }

    private fun parseBaselineResults(rawString: String): MutableMap<String, String> {
        return rawString.split(";").mapNotNull {
            val nameValue = it.takeIf { it.isNotBlank() }?.trimIndent()?.split("=")
            if (nameValue?.size == 2) {
                Pair(nameValue[0], nameValue[1])
            }
            else {
                null
            }
        }.toMap(mutableMapOf())
    }

    private fun checkTest(baselineResults: MutableMap<String, String>, results: Map<String, String>) {
        var failed = false
        for (result in results) {
            if (baselineResults.contains(result.key)) {
                if (baselineResults[result.key] != result.value) {
                    println("----------")
                    println("Error in Composable: ${result.key}")
                    println("Expected: ${baselineResults[result.key]}")
                    println("Was: ${result.value}")
                    println("----------")
                    failed = true
                }
                baselineResults.remove(result.key)
            }
            else {
                println("----------")
                println("New Composable Result: ${result.key}")
                println("----------")
                failed = true
            }
        }
        for (baseline in baselineResults) {
            println("----------")
            println("Missing result for: ${baseline.key}")
            println("----------")
            failed = true
        }
        if (failed) {
            println("----------")
            println("New Baseline:")
            val base = results.map { "${it.key}=${it.value}" }.joinToString(";\n")
            // TODO: Find a better way to output the result, so that it's easy to update the
            //  baseline (results.txt), alternatively, reduce the amount of text in the file
            TestCase.assertEquals("", base)
            println("----------")
        }
    }
}