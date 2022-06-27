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

package com.example.composemail.ui.home.toptoolbar

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MarkAsUnread
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.composemail.ui.theme.textBackgroundColor
import com.example.composemail.ui.utils.rememberConstraintSet

@Composable
fun TopToolbar(
    modifier: Modifier = Modifier,
    selectionCount: Int,
    onUnselectAll: () -> Unit
) {
    val isInSelection = remember(selectionCount) { selectionCount > 0 }
    AnimatedContent(targetState = isInSelection) { isSelected ->
        if (isSelected) {
            SelectionToolbar(
                modifier = modifier,
                selectionCount = selectionCount,
                onUnselectAll = onUnselectAll
            )
        } else {
            SearchToolbar(modifier = modifier.padding(4.dp))
        }
    }
}

@Composable
fun SearchToolbar(modifier: Modifier) {
    val constraintSet = rememberConstraintSet {
        val searchBar = createRefFor("searchBar")
        val profileB = createRefFor("profileButton")
        val sideMargin = 0.dp
        val topMargin = 0.dp
        val barSize = 50.dp
        val topG = createGuidelineFromTop(topMargin)
        val bottomG = createGuidelineFromTop(barSize + topMargin)
        val hChain = createHorizontalChain(searchBar, profileB, chainStyle = ChainStyle.Spread)
        constrain(hChain) {
            start.linkTo(parent.start, sideMargin)
            end.linkTo(parent.end, sideMargin)
        }

        constrain(profileB) {
            height = Dimension.ratio("1:1")
            width = Dimension.value(40.dp)
            top.linkTo(topG)
            bottom.linkTo(bottomG)
        }
        constrain(searchBar) {
            width = Dimension.fillToConstraints
            height = Dimension.fillToConstraints
            top.linkTo(topG)
            bottom.linkTo(bottomG)
        }
    }
    ConstraintLayout(
        modifier = modifier,
        constraintSet = constraintSet
    ) {
        SearchBar(Modifier.layoutId("searchBar"))
        ProfileButton(Modifier.layoutId("profileButton"))
    }
}

@Composable
fun SelectionToolbar(
    modifier: Modifier,
    selectionCount: Int,
    onUnselectAll: () -> Unit
) {
    Row(
        modifier = modifier
            .heightIn(min = 40.dp)
            .background(MaterialTheme.colors.textBackgroundColor)
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
        Text(text = selectionCount.toString())
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
    Column(modifier = Modifier.fillMaxSize()) {
        TopToolbar(
            modifier = Modifier.fillMaxWidth(),
            selectionCount = 0
        ) {}
        TopToolbar(
            modifier = Modifier.fillMaxWidth(),
            selectionCount = 1
        ) {}
    }
}