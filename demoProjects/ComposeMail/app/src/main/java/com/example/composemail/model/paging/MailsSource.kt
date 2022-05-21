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
import com.example.composemail.model.data.MailEntryInfo
import com.example.composemail.model.repo.MailRepository

class MailsSource(private val mailRepo: MailRepository) : PagingSource<Int, MailEntryInfo>() {
    override fun getRefreshKey(state: PagingState<Int, MailEntryInfo>): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MailEntryInfo> {
        val nextPage = params.key ?: 0
        val nextMails = mailRepo.getNextSetOfConversations(params.loadSize)
        return LoadResult.Page(
            data = nextMails.conversations,
            prevKey = if (nextPage == 0) null else nextMails.page - 1,
            nextKey = nextMails.page + 1,
            // An additional item that will work as a loading placeholder
            // while the next page is produced
            itemsAfter = 1
        )
    }
}