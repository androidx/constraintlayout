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

@file:JvmName("DslVerificationKt")
@file:JvmMultifileClass

package com.example.dsl_verification.constraint

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.dsl_verification.R


@Preview
@Composable
fun Test16() {
    LoginPage()
}

@Composable
private fun LoginPage() {
    ConstraintLayout(
        modifier = Modifier.fillMaxSize(),
    ) {
        val (user, pass, userField, passField, image, cancel, login) = createRefs()
        val g1 = createGuidelineFromStart(12.dp)
        val g2 = createGuidelineFromEnd(12.dp)
        val userBarrier = createBottomBarrier(user, userField)
        val labelsBarrier = createEndBarrier(user, pass)
        val contentBarrier = createBottomBarrier(pass, passField)
        Image(
            painter = painterResource(id = R.drawable.ic_login),
            contentDescription = null,
            modifier = Modifier.constrainAs(image) {
                width = Dimension.value(120.dp)
                height = Dimension.ratio("1:1")
                centerHorizontallyTo(parent)
                top.linkTo(parent.top, 24.dp)
            }
        )
        Text(text = "User", Modifier.constrainAs(user) {
            centerVerticallyTo(userField)
            start.linkTo(g1)
        })
        TextField(value = "", onValueChange = {}, Modifier.constrainAs(userField) {
            width = Dimension.fillToConstraints
            top.linkTo(image.bottom, 12.dp)
            linkTo(start = labelsBarrier, startMargin = 8.dp, end = g2)
        })
        Text(text = "Password", Modifier.constrainAs(pass) {
            centerVerticallyTo(passField)
            start.linkTo(g1)
        })
        TextField(value = "", onValueChange = {}, Modifier.constrainAs(passField) {
            width = Dimension.fillToConstraints
            top.linkTo(userBarrier, 12.dp)
            linkTo(start = labelsBarrier, startMargin = 8.dp, end = g2)
        })
        Button(onClick = { /*TODO*/ }, Modifier.constrainAs(login) {
            top.linkTo(contentBarrier, 12.dp)
            end.linkTo(g2)
        }) { Text("Login") }
        TextButton(onClick = { /*TODO*/ }, Modifier.constrainAs(cancel) {
            centerVerticallyTo(login)
            end.linkTo(login.start, 8.dp)
        }) { Text("Cancel") }
    }
}