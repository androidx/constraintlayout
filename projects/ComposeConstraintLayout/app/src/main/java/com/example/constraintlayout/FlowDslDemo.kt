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


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutBaseScope
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.LayoutReference
import java.util.Arrays


@Preview(group = "flow1")
@Composable
public fun FlowDslDemo1() {
    // Currently, we still have problem with positioning the Flow Helper
    // and/or setting the width/height properly.
    ConstraintLayout(

        ConstraintSet {
            val a = createRefFor("1")
            val b = createRefFor("2")
            val c = createRefFor("3")
            val d = createRefFor("4")
             val g1 = createFlow(a,b,c,d)

            constrain(g1) {
                centerVerticallyTo(parent)
                centerHorizontallyTo(parent)
            }
        },

        modifier = Modifier.fillMaxSize()) {
        val numArray = arrayOf("1", "2", "3", "4")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(num),
                onClick = {},
            ) {
                Text(text = num)
            }
        }
    }
}

@Preview(group = "flow2")
@Composable
public fun FlowDslDemo2() {
    ConstraintLayout(
        ConstraintSet {
            val a = createRefFor("1")
            val b = createRefFor("2")
            val c = createRefFor("3")
            val d = createRefFor("4")
            val g1 = createFlow(a,b,c,d,
            vertical = true)

            constrain(g1) {
                centerVerticallyTo(parent)
                centerHorizontallyTo(parent)
            }
        },
        modifier = Modifier.fillMaxSize()) {
        val numArray = arrayOf("1", "2", "3", "4")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(num),
                onClick = {},
            ) {
                Text(text = num)
            }
        }
    }
}
fun bar() {
    val d = arrayOfNulls<String>(3)
    foo(  *d,k=3)
}

fun foo( vararg a: String?,k: Int) {

}

@Preview(group = "flow3")
@Composable
public fun FlowDslDemo3() {
    val numArray = arrayOf("1", "2", "3", "4", "5", "6", "7")

    ConstraintLayout(
        ConstraintSet {
            val elem = arrayOfNulls<LayoutReference>(numArray.size)

            for (i in numArray.indices) {
                elem[i] = createRefFor(numArray[i])
            }

            val g1 = createFlow(
                elements = *elem,
                vertical = true,
                padding = 30.dp,
                wrap = ConstraintLayoutBaseScope.Wrap.WrapChain,
                vFlowBias = 0.1f,
                hFlowBias = 0.8f,
                maxElement = 4,
            )

            constrain(g1) {
                centerVerticallyTo(parent)
                centerHorizontallyTo(parent)
            }
        },
        modifier = Modifier.fillMaxSize()) {
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(num),
                onClick = {},
            ) {
                Text(text = num)
            }
        }
    }
}




@Preview(group = "flow4")
@Composable
public fun FlowDslDemo4() {
    val chArray = arrayOf("a", "b", "c", "d", "e", "f", "g", "h")

    ConstraintLayout(
        ConstraintSet {
            val elem = arrayOfNulls<LayoutReference>(chArray.size)

            for (i in chArray.indices) {
                elem[i] = createRefFor(chArray[i])
            }

            val g1 = createFlow(
                elements = *elem,
                wrap = ConstraintLayoutBaseScope.Wrap.WrapAligned,
                vGap = 32.dp,
                hGap = 32.dp,
                hFlowBias = 0.8f,
                maxElement = 4,
            )

            constrain(g1) {
                centerVerticallyTo(parent)
                centerHorizontallyTo(parent)
            }
        },

        modifier = Modifier.fillMaxSize()) {
        for (ch in chArray) {
            Button(
                modifier = Modifier.layoutId(ch),
                onClick = {},
            ) {
                Text(text = ch)
            }
        }
    }
}









