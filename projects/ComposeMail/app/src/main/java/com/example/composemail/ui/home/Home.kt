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

package com.example.composemail.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composemail.model.ComposeMailModel
import com.example.composemail.ui.mails.MailList
import com.example.composemail.ui.newmail.ComposeNewMail
import com.example.composemail.ui.newmail.MotionLayoutMail

@Composable
fun ComposeMailHome() {
    val mailModel: ComposeMailModel = viewModel()
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        SearchBar(Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Inbox",
            style = MaterialTheme.typography.h6
        )
        MailList(
            modifier = Modifier
                .weight(1.0f, true)
                .fillMaxWidth(),
            observableConversationsInfo = mailModel.observableConversations,
            onRequestMoreConversations = mailModel::loadMoreMails
        )
    }

    // TODO: MotionLayoutMail Composable still needs some work
//    MotionLayoutMail(modifier = Modifier.fillMaxSize())
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier
) {
    var placeholder: String by remember { mutableStateOf("Search in mails") }
    OutlinedTextField(
        modifier = modifier
            .clip(RoundedCornerShape(32.dp))
            .background(Color(0x14003C96))
            .onFocusChanged {
                placeholder = if (it.isFocused) {
                    "I'm not implemented yet!"
                } else {
                    "Search in mails"
                }
            },
        value = "",
        onValueChange = { newText ->

        },
        placeholder = {
            Text(text = placeholder)
        },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = null)
        },
        singleLine = true,
        shape = MaterialTheme.shapes.small,
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}