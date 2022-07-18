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

package com.example.dsl_verification

import androidx.compose.runtime.Composer
import com.example.dsl_verification.CommonPreviewUtilsCopy.findComposableMethod
import java.util.Locale

class ComposableInvocator(packageString: String, fileName: String) {
    private val supportedPackages = listOf<String>(
        packageString
    )

    /**
     * File names, will also load any other numbered file.
     * Eg: for "test" it will automatically look for "test1", "test2", ...
     */
    private val supportedFileNames = listOf<String>(
        fileName
    )

    /**
     * The base composable names that may be present in the files, will also look for numbered
     * composables
     *
     * Eg: for "Example" it will also look for "Example1", "Example2", ...
     */
    private val baseComposableNames = listOf<String>(
        "Test"
    )

    private val composablesIndex = mutableListOf<ComposableData>()

    val max: Int
        get() = composablesIndex.size - 1

    init {
        // Build Index
        createClassCombinations().forEach { className ->
            baseComposableNames.forEach { baseComposableName ->
                findAndPopulateIndex(className, baseComposableName)
            }
        }
    }

    /**
     * Invoke the composable at [index].
     *
     * The composer can be obtain in a Composable context with 'currentComposer'.
     *
     * Returns the full qualified name of the Composable as: "fully.qualified.class#MethodName"
     */
    fun invokeComposable(index: Int, composer: Composer): String {
        val composableData = composablesIndex[index]
        CommonPreviewUtilsCopy.invokeComposableViaReflection(
            className = composableData.fqClass,
            methodName = composableData.composableName,
            composer = composer
        )
        return "${composableData.fqClass}#${composableData.composableName}"
    }

    private fun createClassCombinations(): List<String> {
        val classes = mutableListOf<String>()
        supportedPackages.forEach { supportedPackage ->
            supportedFileNames.forEach { fileName ->
                val correctedFileName = fileName.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.US
                    ) else it.toString()
                }
                val fqClassFormat = "$supportedPackage.$correctedFileName%dKt"

                var fileCount = 0
                var className = "$supportedPackage.${correctedFileName}Kt"
                var attempts = 10
                var exists = exists(className = className)
                while (
                    exists
                    ||
                    attempts > 0
                ) {
                    if (exists) {
                        classes.add(className)
                        attempts = 10
                    } else {
                        attempts--
                    }
                    className = fqClassFormat.format(fileCount++)
                    exists = exists(className = className)
                }
            }
        }
        return classes
    }

    private fun findAndPopulateIndex(className: String, baseComposableName: String) {
        var extraAttempts = 10
        var composableNameCount = 0
        var composableName = baseComposableName
        var exists = exists(className = className, composableName = composableName)
        while (
            exists
            ||
            extraAttempts > 0
        ) {
            if (exists) {
                composablesIndex.add(
                    ComposableData(
                        fqClass = className,
                        composableName = composableName
                    )
                )
                extraAttempts = 10
            } else {
                extraAttempts--
            }
            composableName = baseComposableName + composableNameCount++
            exists = exists(className = className, composableName = composableName)
        }
    }

    private fun exists(className: String): Boolean {
        println("Finding class: $className")
        return try {
            Class.forName(className)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun exists(className: String, composableName: String): Boolean {
        println("Finding method: $className.$composableName")
        return try {
            val composableClass = Class.forName(className)
            composableClass.findComposableMethod(composableName)
            true
        } catch (e: Exception) {
            false
        }
    }

    private data class ComposableData(
        val fqClass: String,
        val composableName: String
    )
}