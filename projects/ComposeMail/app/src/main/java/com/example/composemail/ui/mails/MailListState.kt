package com.example.composemail.ui.mails

import androidx.compose.runtime.mutableStateOf

class MailListState {
    private val conversationStatesById = mutableMapOf<Int, MailItemState>()

    private val selectedTracker = mutableSetOf<Int>()

    private val _selectedCount = mutableStateOf(0)

    val selectedCount
        get() = _selectedCount.value

    fun unselectAll() {
        conversationStatesById.values.forEach { it.setSelected(false) }
    }

    fun stateFor(id: Int?): MailItemState {
        val nextId = id ?: -1
        return conversationStatesById.computeIfAbsent(nextId) {
            MailItemState(nextId) { id, isSelected ->
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