package com.example.composemail.ui.mails

import androidx.compose.runtime.mutableStateOf

class MailItemState(val id: Int, private val onSelected: (Int, Boolean) -> Unit) {
    private val _isSelected = mutableStateOf(false)
    val isSelected
        get() = _isSelected.value

    fun setSelected(isSelected: Boolean) {
        onSelected(id, isSelected)
        _isSelected.value = isSelected
    }
}