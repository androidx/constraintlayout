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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.example.composemail.model.data.MailInfoPeek
import kotlinx.coroutines.flow.Flow

private const val TRANSITION_DURATION_MS = 600

@Composable
fun MailList(
    modifier: Modifier = Modifier,
    listState: MailListState,
    observableConversations: Flow<PagingData<MailInfoPeek>>,
    onMailOpen: (id: Int) -> Unit
) {
    // The items provided through the model using Pager through a Flow
    val lazyMailItems: LazyPagingItems<MailInfoPeek> =
        observableConversations.collectAsLazyPagingItems()

    // Since there's currently no good way to have initial placeholders from the PagingItems, we'll
    // just animate between a loading indicator and the LazyColumn
    AnimatedContent(
        modifier = modifier,
        targetState = lazyMailItems.itemCount == 0,
        transitionSpec = {
            // Equal duration for a pushing in/out effect
            slideInVertically(
                animationSpec = tween(TRANSITION_DURATION_MS),
                initialOffsetY = { it }
            ) with slideOutVertically(
                animationSpec = tween(TRANSITION_DURATION_MS),
                targetOffsetY = { -it }
            )
        }
    ) { isListEmpty ->
        if (isListEmpty) {
            // MailItem with null info will act as a loading indicator
            MailItem(
                info = null,
                state = listState.stateFor(null),
                onMailOpen = onMailOpen
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(lazyMailItems) { mailInfo ->
                    // The Pager, configured with placeholders may initially provide
                    // a null mailInfo when it reaches the current end of the list,
                    // it will then provide a non-null mailInfo for the same Composable,
                    // MailItem animates the transition from those two values
                    MailItem(
                        info = mailInfo,
                        state = listState.stateFor(mailInfo?.id),
                        onMailOpen = onMailOpen
                    )
                }
            }
        }
    }
}