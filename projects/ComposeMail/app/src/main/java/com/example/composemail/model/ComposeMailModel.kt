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

package com.example.composemail.model

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.composemail.model.data.MailEntryInfo
import com.example.composemail.model.paging.MailsSource
import com.example.composemail.model.repo.MailRepository
import com.example.composemail.model.repo.OfflineRepository
import kotlinx.coroutines.flow.Flow

private const val LOAD_LIMIT = 15
private const val REFRESH_THRESHOLD = 5

class ComposeMailModel(application: Application) : AndroidViewModel(application) {
    private val mailRepo: MailRepository =
        OfflineRepository(getApplication<Application>().resources)

    private var _openedMail: MutableState<MailEntryInfo?> = mutableStateOf(null)

    val openedMail: MailEntryInfo?
        get() = _openedMail.value

    val conversations: Flow<PagingData<MailEntryInfo>> = Pager(
        config = PagingConfig(
            pageSize = LOAD_LIMIT,
            enablePlaceholders = true,
            prefetchDistance = REFRESH_THRESHOLD,
            initialLoadSize = LOAD_LIMIT
        )
    ) {
        MailsSource(mailRepo)
    }.flow

    fun openMail(id: Int) {
        // TODO: Support opening a conversation
    }

    fun closeMail() {

    }
}