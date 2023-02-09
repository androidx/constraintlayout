/*
 * Copyright (C) 2023 The Android Open Source Project
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

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.composemail.model.data.MailInfoPeek
import com.example.composemail.model.repo.MailRepository

private const val INITIAL_LOAD_SIZE = 30
private const val PAGE_SIZE = 15
private const val REFRESH_THRESHOLD = 5

fun createMailPager(mailRepository: MailRepository): Pager<Int, MailInfoPeek> =
    Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            // Enable placeholders when loading indicators are supported, see MailItem.kt
            enablePlaceholders = true,
            prefetchDistance = REFRESH_THRESHOLD,
            initialLoadSize = INITIAL_LOAD_SIZE
        ),
        pagingSourceFactory = { MailPagingSource(mailRepository) }
    )