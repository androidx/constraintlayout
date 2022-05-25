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

package com.example.composemail.ui.mails

import androidx.compose.runtime.mutableStateOf

class MailListState {
    private val conversationStatesById = mutableMapOf<Int, MailItemState>()

    private val selectedTracker = mutableSetOf<Int>()

    private val _selectedCount = mutableStateOf(0)

    val selectedIDs: Collection<Int>
        get() = selectedTracker.toList()

    val selectedCount
        get() = _selectedCount.value

    fun unselectAll() {
        conversationStatesById.values.forEach { it.setSelected(false) }
    }

    /**
     * Returns an instance of [MailItemState] for the given [id].
     *
     * Repeated calls for the same [id] will return the same [MailItemState] instance.
     */
    fun stateFor(id: Int?): MailItemState {
        val nextId = id ?: -1
        return conversationStatesById.computeIfAbsent(nextId) {
            MailItemState(nextId) { id, isSelected ->
                // Track which items are selected
                if (isSelected) {
                    selectedTracker.add(id)
                } else {
                    selectedTracker.remove(id)
                }
                _selectedCount.value = selectedTracker.size
            }
        }
    }
}