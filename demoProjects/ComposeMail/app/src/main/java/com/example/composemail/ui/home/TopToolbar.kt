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

package com.example.composemail.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MarkAsUnread
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.atLeastWrapContent
import com.example.composemail.ui.components.ProfileButton
import com.example.composemail.ui.components.SearchBar
import com.example.composemail.ui.theme.Selection

@Composable
fun TopToolbar(
    modifier: Modifier = Modifier,
    selectionCountProvider: () -> Int, // Lambda to avoid recomposing AnimatedContent on every count change
    onUnselectAll: () -> Unit
) {
    val isInSelection by remember { derivedStateOf { selectionCountProvider() > 0 } }
    AnimatedContent(
        targetState = isInSelection,
        modifier = modifier
    ) { isSelected ->

        if (isSelected) {
            SelectionToolbar(
                modifier = Modifier,
                selectionCountProvider = selectionCountProvider,
                onUnselectAll = onUnselectAll
            )
        } else {
            SearchToolbar(modifier = Modifier.padding(4.dp))
        }
    }
}

private val searchToolbarConstraintSet = ConstraintSet {
    val searchBar = createRefFor("searchBar")
    val profileB = createRefFor("profileButton")

    constrain(searchBar) {
        width = Dimension.fillToConstraints.atLeastWrapContent
        height = Dimension.wrapContent

        centerVerticallyTo(parent)
        start.linkTo(parent.start)
        end.linkTo(profileB.start)
    }

    constrain(profileB) {
        height = Dimension.value(40.dp)
        width = Dimension.value(40.dp)

        centerVerticallyTo(searchBar)
        end.linkTo(parent.end)
    }
}

@Composable
private fun SearchToolbar(modifier: Modifier) {
    ConstraintLayout(
        modifier = modifier,
        constraintSet = searchToolbarConstraintSet
    ) {
        SearchBar(Modifier.layoutId("searchBar"))
        ProfileButton(Modifier.layoutId("profileButton"))
    }
}

@Composable
private fun SelectionToolbar(
    modifier: Modifier,
    selectionCountProvider: () -> Int,
    onUnselectAll: () -> Unit
) {
    Row(
        modifier = modifier
            .heightIn(min = 40.dp)
            .background(Selection.backgroundColor)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            modifier = Modifier.clickable {
                onUnselectAll()
            },
            imageVector = Icons.Default.ArrowBack,
            contentDescription = null
        )
        Text(text = selectionCountProvider().toString())
        Spacer(modifier = Modifier.weight(1.0f, true))
        Icon(imageVector = Icons.Default.Archive, contentDescription = null)
        Icon(imageVector = Icons.Default.Delete, contentDescription = null)
        Icon(imageVector = Icons.Default.MarkAsUnread, contentDescription = null)
        Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
    }
}

@Preview
@Composable
private fun TopToolbarPreview() {
    Column {
        TopToolbar(
            modifier = Modifier.width(IntrinsicSize.Min),
            selectionCountProvider = { 0 }
        ) {}
        TopToolbar(
            modifier = Modifier,
            selectionCountProvider = { 0 }
        ) {}
        TopToolbar(
            modifier = Modifier.fillMaxWidth(),
            selectionCountProvider = { 0 }
        ) {}
        TopToolbar(
            modifier = Modifier.width(IntrinsicSize.Min),
            selectionCountProvider = { 1 }
        ) {}
        TopToolbar(
            modifier = Modifier,
            selectionCountProvider = { 1 }
        ) {}
    }
}