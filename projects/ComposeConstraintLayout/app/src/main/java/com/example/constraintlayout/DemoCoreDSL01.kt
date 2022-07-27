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

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import androidx.constraintlayout.core.dsl.Constraint
import androidx.constraintlayout.core.dsl.ConstraintSet
import androidx.constraintlayout.core.dsl.Transition

@Preview(group = "dsl_1")
@Composable
public fun DemoCoreDSL01() {
    var animateToEnd by remember { mutableStateOf(false) }

    val progress  = remember { Animatable(0f) }

    LaunchedEffect(animateToEnd) {
        progress.animateTo(if (animateToEnd) 1f else 0f,
            animationSpec = tween(3000))
    }

    Column( modifier = Modifier.background(Color.White)) {
        val motionScene = androidx.constraintlayout.core.dsl.MotionScene()
        motionScene.addTransition(Transition("start", "end"))
        val cs1 = ConstraintSet("start")
        val cs2 = ConstraintSet("end")
        val c1 = Constraint("id1")
        val c2 = Constraint("id1")
        c1.linkToStart(Constraint.PARENT.start, 16)
        c1.linkToBottom(Constraint.PARENT.bottom, 16)
        c1.width = 40
        c1.height = 40
        cs1.add(c1)
        c2.linkToEnd(Constraint.PARENT.end, 16)
        c2.linkToTop(Constraint.PARENT.top, 16)
        c2.width = 100
        c2.height = 100
        cs2.add(c2)
        motionScene.addConstraintSet(cs1)
        motionScene.addConstraintSet(cs2)

        val scene1 = MotionScene(motionScene.toString())

        MotionLayout(
            modifier    = Modifier
                .fillMaxWidth()
                .height(400.dp),
            motionScene = scene1,
            progress   = progress.value) {

            Box(modifier = Modifier
                .layoutId("id1")
                .background(Color.Red))
        }

        Button(onClick = { animateToEnd = !animateToEnd },
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp)) {
            Text(text = "Run")
        }
    }
}