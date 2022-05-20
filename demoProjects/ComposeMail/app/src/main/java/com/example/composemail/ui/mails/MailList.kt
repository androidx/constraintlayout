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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.example.composemail.model.data.MailEntryInfo
import kotlinx.coroutines.flow.Flow

@Composable
fun MailList(
    modifier: Modifier = Modifier,
    listState: MailListState,
    observableConversations: Flow<PagingData<MailEntryInfo>>
) {
    val lazyMailItems: LazyPagingItems<MailEntryInfo> =
        observableConversations.collectAsLazyPagingItems()

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        itemsIndexed(lazyMailItems) { index, mailItem ->
            MailItem(
                info = mailItem,
                state = listState.stateFor(mailItem?.id)
            )
        }
    }
}