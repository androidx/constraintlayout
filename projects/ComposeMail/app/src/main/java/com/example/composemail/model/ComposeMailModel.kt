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
import androidx.compose.runtime.Immutable
import androidx.lifecycle.*
import com.example.composemail.model.data.MailEntryInfo
import com.example.composemail.model.repo.MailRepository
import com.example.composemail.model.repo.OfflineRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val LOAD_LIMIT = 15

class ComposeMailModel(application: Application): AndroidViewModel(application) {
    private val mailRepo: MailRepository = OfflineRepository(getApplication<Application>().resources)

    private val _composeMailState = MutableLiveData<ComposeMailState>()
    val composeMailState: LiveData<ComposeMailState> = _composeMailState

    private val conversations = mutableListOf<MailEntryInfo>()

    private val _conversationsLiveData:  MutableLiveData<ConversationsInfo> =
        MutableLiveData(ConversationsInfo(conversations))
    val observableConversations: LiveData<ConversationsInfo> = _conversationsLiveData

    init {
        loadMoreMails()
    }

    fun loadMoreMails() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val newMails = mailRepo.getNextSetOfConversations(LOAD_LIMIT)
                conversations.addAll(newMails)
            }
            _conversationsLiveData.value = ConversationsInfo(conversations.toCollection(mutableListOf<MailEntryInfo?>()).apply { add(null) })
        }
    }
}

data class ComposeMailState(
    val openedMailEntryInfo: MailEntryInfo? = null,
    val isWritingNewMail: Boolean = false
)

@Immutable
data class ConversationsInfo(
    val items: List<MailEntryInfo?>
)