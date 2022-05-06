package com.example.composemail.ui.newmail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Suppress("NOTHING_TO_INLINE")
@Composable
inline fun rememberNewMailState(
    vararg keys: Any? = arrayOf(Unit),
    initialLayoutState: NewMailLayoutState
): NewMailState {
    return remember(*keys) { NewMailState(initialLayoutState) }
}

class NewMailState(initialLayoutState: NewMailLayoutState) {
    private var _currentState = mutableStateOf(initialLayoutState)

    val currentState: NewMailLayoutState
        get() = _currentState.value

    fun setToFull() {
        _currentState.value = NewMailLayoutState.Full
    }

    fun setToMini() {
        _currentState.value = NewMailLayoutState.Mini
    }

    fun setToFab() {
        _currentState.value = NewMailLayoutState.Fab
    }
}

enum class NewMailLayoutState {
    Full,
    Mini,
    Fab
}