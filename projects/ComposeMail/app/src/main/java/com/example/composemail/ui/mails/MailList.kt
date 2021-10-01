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

package com.example.composemail.ui.mails

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.*
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.composemail.model.data.MailEntryInfo
import com.example.composemail.model.ConversationsInfo

private const val REFRESH_THRESHOLD = 5

@Composable
fun MailList(
    modifier: Modifier = Modifier,
    observableConversationsInfo: LiveData<ConversationsInfo>,
    onRequestMoreConversations: () -> Unit
) {
    val mailsInfo = observableConversationsInfo.mObserveAsState()
    val mailEntryInfoList: List<MailEntryInfo?> = mailsInfo.value!!.items
    val lastIndex = mailEntryInfoList.lastIndex
    val state = remember { MailsListState(mailEntryInfoList.size) }
    val allowLoading = mailEntryInfoList.size > state.listSize || !state.hasRequestedOnce

    LazyColumn(
        modifier = modifier
    ) {
        itemsIndexed(mailEntryInfoList) { index: Int, item: MailEntryInfo? ->
            MailItem(info = item)
            if (allowLoading) {
                if (index > REFRESH_THRESHOLD && index == (lastIndex - REFRESH_THRESHOLD)) {
                    state.listSize = mailEntryInfoList.size
                    state.hasRequestedOnce = true
                    onRequestMoreConversations()
                }
            }
        }
    }
}

@Composable
private fun <T> LiveData<T>.mObserveAsState(): State<T?> {
    val lifecycleOwner = LocalLifecycleOwner.current
    val state = remember { mutableStateOf(value, neverEqualPolicy()) } // List not compared structurally, this is best anyways, to support the loading indicator.
    DisposableEffect(this, lifecycleOwner) {
        val observer = Observer<T> {
            state.value = it
        }
        observe(lifecycleOwner, observer)
        onDispose {
            removeObserver(observer)
        }
    }
    return state
}

private data class MailsListState(
    var listSize: Int,
    var hasRequestedOnce: Boolean = false
)