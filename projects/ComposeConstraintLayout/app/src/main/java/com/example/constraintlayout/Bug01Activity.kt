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

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.google.accompanist.coil.rememberCoilPainter


class Bug01Activity : AppCompatActivity() {
    private var mFrameLayout: FrameLayout? = null
    private var composeNum = 45

    init {
        defineDesignElements()

    }

    private fun defineDesignElements() {
        DesignElements.define("text-material") { id, params ->
            val text = params["text"] ?: "text"
            Text(
                modifier = Modifier.layoutId(id),
                text = text
            )
        }
        DesignElements.define("button-material") { id, params ->
            val text = params["text"] ?: "text"
            Button(
                modifier = Modifier.layoutId(id),
                onClick = {},
            ) {
                Text(text = text)
            }
        }
        DesignElements.define("image-coil") { id, params ->
            val url = params["url"] ?: "url"
            val description = params["description"] ?: "Image Description"
            Image(
                modifier = Modifier.layoutId(id),
                painter = rememberCoilPainter(url),
                contentDescription = description
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {

        }
        setContentView(R.layout.activity_main)
        mFrameLayout = findViewById<FrameLayout>(R.id.frame)
        var com = ComposeView(this);
        mFrameLayout!!.addView(com)
        show(com)
    }


    private fun show(com: ComposeView) {
        com.setContent { DisplayResults(); }
    }

    @Composable
    fun DisplayResults() {
        Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
            Button(
                onClick = {
                    TestDialogFragment().show(supportFragmentManager, "TestDialogFragment")
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)

            ) {
                Text("Display dialog", modifier = Modifier.wrapContentSize())
            }

        }
    }

    /**
     * This is an example of a DialogFragment that has a [ComposeView] with a [ConstraintLayout]
     * It serves as a demonstration that there may be something wrong with the behavior.
     * Expectation: Create a Dialog that fits all the content correctly.
     * Result: Creates a Dialog that crops out some of its content.
     */
    class TestDialogFragment : DialogFragment() {
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            return ComposeView(requireContext()).apply {
                setContent {

                    TestDialogContent(  )

                }
            }
        }
    }

    /**
     * Similar to [TestDialogFragment], but demonstrated when using a [Dialog] Composable.
     * Has similar undesired behavior where some the the text content is getting cropped out.
     */
    class TestDialogComposableFragment : Fragment() {
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            return ComposeView(requireContext()).apply {
                setContent {
//                    MdcTheme {
                    Dialog(onDismissRequest = {}) {
                        Box(
                            modifier = Modifier
                                .wrapContentSize()
                                .background(Color.White)
                        ) {
                            TestDialogContent( )
                        }
                    }
//                    }
                }
            }
        }

    }
}
