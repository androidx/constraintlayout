package com.example.composemail.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.ConstraintSetScope


@Composable
fun rememberConstraintSet(
    vararg keys: Any = emptyArray(),
    constraints: ConstraintSetScope.() -> Unit
): ConstraintSet {
    val constraintSet = remember(keys) {
        ConstraintSet(constraints)
    }
    return constraintSet
}