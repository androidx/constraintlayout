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

import junit.framework.TestCase

internal fun parseBaselineResults(rawString: String): MutableMap<String, String> {
    return rawString.split(";").mapNotNull {
        val nameValue = it.takeIf { it.isNotBlank() }?.trimIndent()?.split("=")
        if (nameValue?.size == 2) {
            Pair(nameValue[0], nameValue[1])
        } else {
            null
        }
    }.toMap(mutableMapOf())
}

internal fun checkTest(
    baselineResults: MutableMap<String, String>,
    results: Map<String, String>
) {
    var failed = false
    var failedCount = 0
    for (result in results) {
        if (baselineResults.contains(result.key)) {
            if (baselineResults[result.key] != result.value) {
                println("----------")
                println("Error in Composable: ${result.key}")
                println("Expected: ${baselineResults[result.key]}")
                println("Was: ${result.value}")
                println("----------")
                failed = true
                failedCount++
            }
            baselineResults.remove(result.key)
        } else {
            println("----------")
            println("New Composable Result: ${result.key}")
            println("----------")
            failed = true
            failedCount++
        }
    }
    for (baseline in baselineResults) {
        println("----------")
        println("Missing result for: ${baseline.key}")
        println("----------")
        failed = true
        failedCount++
    }
    if (failed) {
        println("----------")
        println("$failedCount Composables failed")
        println("New Baseline:")
        val base = results.map { "${it.key}=${it.value}" }.joinToString(";\n")
        // TODO: Find a better way to output the result, so that it's easy to update the
        //  baseline (results.txt), alternatively, reduce the amount of text in the file

        // You can update results.txt by placing a breakpoint here in debugging mode and copying
        // the contents of 'base' into the file.
        TestCase.assertEquals("", base)
        println("----------")
    }
}