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

package com.example.composemail.model.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.composemail.model.data.MailInfoPeek
import com.example.composemail.model.repo.MailRepository

private const val PROVIDE_INITIAL_PLACEHOLDER = false

class MailPagingSource(private val mailRepo: MailRepository) : PagingSource<Int, MailInfoPeek>() {
    override fun getRefreshKey(state: PagingState<Int, MailInfoPeek>): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MailInfoPeek> {
        val nextPage = params.key ?: 0

        if (PROVIDE_INITIAL_PLACEHOLDER && params.key == null) {
            // You can provide initial placeholders by returning an empty List for the initial request.
            // Note that doing this consumes the request that corresponds to `initialLoadSize` in
            // PagingConfig().
            return LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = 0,
                // Provide a single placeholder item for initial request, alternatively, use
                // params.loadSize to have a placeholder for each item which is typically better for
                // shimmer-like placeholders.
                itemsAfter = 1,
            )
        }
        val nextMails = mailRepo.getNextSetOfConversations(params.loadSize)
        return LoadResult.Page(
            data = nextMails.conversations,
            prevKey = if (nextPage == 0) null else nextMails.page - 1,
            // In this case, we assume infinite amount of pages
            nextKey = nextMails.page + 1,
            // An additional item that will work as a loading placeholder
            // while the next page is produced
            itemsAfter = 1
        )
    }
}