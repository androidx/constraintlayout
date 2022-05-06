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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composemail.LocalWidthSizeClass
import com.example.composemail.model.ComposeMailModel
import com.example.composemail.ui.home.toptoolbar.TopToolbar
import com.example.composemail.ui.mails.MailList
import com.example.composemail.ui.mails.MailListState
import com.example.composemail.ui.newmail.NewMailButton
import com.example.composemail.ui.newmail.NewMailLayoutState
import com.example.composemail.ui.newmail.rememberNewMailState

@Composable
fun ComposeMailHome(modifier: Modifier = Modifier) {
    val mailModel: ComposeMailModel = viewModel()
    val listState = remember { MailListState() }
    val newMailState = rememberNewMailState(initialLayoutState = NewMailLayoutState.Fab)
    Row(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                TopToolbar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    selectionCount = listState.selectedCount,
                    onUnselectAll = listState::unselectAll
                )
                Text(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    text = "Inbox",
                    style = MaterialTheme.typography.h6
                )
                MailList(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .weight(1.0f, true)
                        .fillMaxWidth(),
                    listState = listState,
                    observableConversations = mailModel.conversations
                )
            }
            NewMailButton(
                modifier = Modifier.fillMaxSize(),
                state = newMailState
            )
        }
        if (LocalWidthSizeClass.current != WindowWidthSizeClass.Compact) {
            OutlinedButton(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                onClick = { /*TODO*/ }
            ) {
                Text(text = "Hello World!")
            }
        }
    }
}


@Composable
fun Home2(modifier: Modifier = Modifier) {
    when (LocalWidthSizeClass.current) {
        WindowWidthSizeClass.Expanded -> {

        }
        else -> {

        }
    }
}