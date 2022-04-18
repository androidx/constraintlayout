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

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import androidx.constraintlayout.compose.layoutId


@Composable
fun Example(
modifier : Modifier = Modifier
) {
    ConstraintLayout(modifier = modifier
        .fillMaxSize()
        .background(color = Color.Blue)
    ) {
        val (title, pWrap,normWrap) = createRefs()

        Text(text = "Demonstrates effect of wrap vs preferWrap", modifier = Modifier.background(color = Color.Red).constrainAs(title) {
            top.linkTo(parent.top)
            height = Dimension.wrapContent
            width = Dimension.wrapContent
        })

        Box(modifier = Modifier
            .constrainAs(pWrap) {
                width = Dimension.fillToConstraints
                linkTo(start = parent.start, end = parent.end)
                linkTo(top = title.bottom, bottom = parent.bottom, bias = 0.0f)

                height =     Dimension.preferredWrapContent
            }
            .background(color = Color.Yellow)) {
            var buttonTitle by remember { mutableStateOf("preferredWrapContent") }
            var textContent by remember { mutableStateOf("Lorem Ipsum ") }
            Column(modifier = modifier.background(Color.Gray)) {
                Column() {
                    Button(onClick = {  buttonTitle += "\n" }) {
                        Text(text = buttonTitle)
                    }
                    Button(onClick = { textContent += "\n"+textContent + textContent.length
                        Log.d("CL",textContent)}) {
                        Text(text = "Increase Content")
                    }
                    Text(text=textContent)
                }
            }
        }
        Box(modifier = Modifier
            .constrainAs(normWrap) {
                width = Dimension.fillToConstraints
                linkTo(start = parent.start, end = parent.end)
                linkTo(top = pWrap.bottom, bottom = parent.bottom, bias = 1.0f)
                height =   Dimension.wrapContent // Dimension.preferredWrapContent

            }
            .background(color = Color.Yellow)) {

            var textContent by remember { mutableStateOf("Lorem Ipsum ") }
            Column(modifier = modifier.background(Color.Gray)) {
                Column() {
                    Button(onClick = {  Log.d("CL", " chcek" ) }) {
                        Text(text = "wrapContent")
                    }
                    Button(onClick = { textContent += "\n"+textContent + textContent.length
                        Log.d("CL",textContent)}) {
                        Text(text = "Increase Content")
                    }
                    Text(text=textContent)
                }
            }
        }

    }
}


@Composable
fun RowColExample(
    modifier : Modifier = Modifier
) {
    Column(modifier = modifier
        .fillMaxSize()
        .background(color = Color.Blue)
    ) {


        Text(text = "Row Col Example")

        Box(modifier = Modifier
            .heightIn(150.dp, 232.dp) //mention max height here
            .widthIn(0.dp, 232.dp) //mention max width here
            .background(color = Color.Yellow)) {
            var buttonTitle by remember { mutableStateOf("preferredWrapContent") }
            var textContent by remember { mutableStateOf("Lorem Ipsum ") }
            Column(modifier = modifier.background(Color.Gray)) {
                Column() {
                    Button(onClick = {  buttonTitle += "\n" }) {
                        Text(text = buttonTitle)
                    }
                    Button(onClick = { textContent += "\n"+textContent + textContent.length
                        Log.d("CL",textContent)}) {
                        Text(text = "Increase Content")
                    }
                    Text(text=textContent)
                }
            }
        }
        Box(modifier = Modifier
            .heightIn(150.dp, 232.dp) //mention max height here
            .widthIn(0.dp, 232.dp) //mention max width here
            .background(color = Color.Yellow)) {

            var textContent by remember { mutableStateOf("Lorem Ipsum ") }
            Column(modifier = modifier.background(Color.LightGray)) {
                Column() {
                    Button(onClick = {  Log.d("CL", " chcek" ) }) {
                        Text(text = "wrapContent")
                    }
                    Button(onClick = { textContent += "\n"+textContent + textContent.length
                        Log.d("CL",textContent)}) {
                        Text(text = "Increase Content")
                    }
                    Text(text=textContent)
                }
            }
        }

    }
}

