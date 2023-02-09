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
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.composemail.model.data.MailInfoFull
import com.example.composemail.model.data.MailInfoPeek
import com.example.composemail.model.paging.createMailPager
import com.example.composemail.model.repo.MailRepository
import com.example.composemail.model.repo.OfflineRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ComposeMailModel(application: Application) : AndroidViewModel(application) {
    private val mailRepo: MailRepository =
        OfflineRepository(getApplication<Application>().resources)

    private var _openedMail: MutableState<MailInfoFull?> = mutableStateOf(null)

    val openedMail: MailInfoFull?
        get() = _openedMail.value

    val conversations: Flow<PagingData<MailInfoPeek>> = createMailPager(mailRepo).flow

    fun isMailOpen(): Boolean = _openedMail.value != null

    fun openMail(id: Int) {
        viewModelScope.launch {
            var openedMailInfo: MailInfoFull?
            withContext(Dispatchers.IO) {
                openedMailInfo = mailRepo.getFullMail(id)
            }
            _openedMail.value = openedMailInfo
        }
    }

    fun closeMail() {
        _openedMail.value = null
    }
}