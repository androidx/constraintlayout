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

package com.example.composemail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import com.example.composemail.ui.compositionlocal.FoldableInfo
import com.example.composemail.ui.compositionlocal.LocalFoldableInfo
import com.example.composemail.ui.compositionlocal.LocalHeightSizeClass
import com.example.composemail.ui.compositionlocal.LocalWidthSizeClass
import com.example.composemail.ui.home.ComposeMailHome
import com.example.composemail.ui.theme.ComposeMailTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@ExperimentalMaterial3WindowSizeClassApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val windowSizeClass = calculateWindowSizeClass(activity = this)
            val foldableInfo by collectFoldableInfoAsState(activity = this)
            CompositionLocalProvider(
                LocalWidthSizeClass provides windowSizeClass.widthSizeClass,
                LocalHeightSizeClass provides windowSizeClass.heightSizeClass,
                LocalFoldableInfo provides foldableInfo
            ) {
                ComposeMailTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background
                    ) {
                        ComposeMailHome(Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}

@Composable
private fun collectFoldableInfoAsState(activity: ComponentActivity): State<FoldableInfo> {
    val lifecycleScope = LocalLifecycleOwner.current.lifecycleScope
    val foldableInfo = remember { mutableStateOf(FoldableInfo.Default) }

    LaunchedEffect(lifecycleScope, activity) {
        lifecycleScope.launch(Dispatchers.Main) {
            activity.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                WindowInfoTracker.getOrCreate(activity)
                    .windowLayoutInfo(activity)
                    .collect { newLayoutInfo ->
                        foldableInfo.value = FoldableInfo(
                            isHalfOpen = newLayoutInfo.displayFeatures
                                .filterIsInstance<FoldingFeature>()
                                .map { it.state }
                                .contains(FoldingFeature.State.HALF_OPENED)
                        )
                    }
            }
        }
    }
    return foldableInfo
}