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

package com.example.composemail.ui.home

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintSetScope
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composemail.model.ComposeMailModel
import com.example.composemail.model.data.MailInfoFull
import com.example.composemail.ui.compositionlocal.LocalFoldableInfo
import com.example.composemail.ui.compositionlocal.LocalWidthSizeClass
import com.example.composemail.ui.mails.MailList
import com.example.composemail.ui.mails.MailListState
import com.example.composemail.ui.newmail.NewMailButton
import com.example.composemail.ui.newmail.NewMailLayoutState
import com.example.composemail.ui.newmail.rememberNewMailState
import com.example.composemail.ui.viewer.MailToolbar
import com.example.composemail.ui.viewer.MailViewer

private enum class HomeState(val tag: String) {
    ListOnly("listOnlyCompactAndExpanded"),
    MailOpenCompact("mailOpenCompact"),
    MailOpenExpanded("mailOpenExpanded"),
    MailOpenHalf("mailOpenHalf")
}

private val homeMotionScene = MotionScene {
    val (listRef, toolbarRef, viewerRef, newMailButtonRef, mailToolbarRef) = createRefsFor(
        "list",
        "toolbar",
        "viewer",
        "newMailButton",
        "mailToolbar",
    )

    // Constant constraints across ConstraintSets
    val setToolbarConstraints: ConstraintSetScope.() -> Unit = {
        constrain(toolbarRef) {
            width = Dimension.matchParent
            // Toolbar has unstable vertical wrap content, so we pick a size that works for both
            // components supported in the toolbar
            height = Dimension.value(60.dp)

            top.linkTo(parent.top)
            start.linkTo(parent.start)
        }
    }

    // Mail toolbar constraints for whenever a Mail is open
    val setVisibleMailToolbarConstraints: ConstraintSetScope.() -> Unit = {
        constrain(mailToolbarRef) {
            end.linkTo(parent.end, 16.dp)
            bottom.linkTo(parent.bottom, 16.dp)
        }
    }

    val listOnlyCSet = constraintSet(HomeState.ListOnly.tag) {
        constrain(listRef, viewerRef) {
            width = Dimension.percent(1f)
            height = Dimension.fillToConstraints

            top.linkTo(toolbarRef.bottom)
            bottom.linkTo(parent.bottom)
        }
        constrain(listRef) {
            start.linkTo(parent.start)
        }
        constrain(viewerRef) {
            start.linkTo(parent.end)
        }

        constrain(mailToolbarRef) {
            top.linkTo(parent.bottom, 16.dp)
            end.linkTo(parent.end)
        }

        constrain(newMailButtonRef) {
            width = Dimension.matchParent
            height = Dimension.matchParent

            top.linkTo(parent.top)
            start.linkTo(parent.start)
        }

        setToolbarConstraints()
    }

    val mailCompactCSet = constraintSet(HomeState.MailOpenCompact.tag) {
        constrain(listRef, viewerRef) {
            width = Dimension.percent(1f)
            height = Dimension.fillToConstraints

            top.linkTo(toolbarRef.bottom)
            bottom.linkTo(parent.bottom)
        }
        constrain(listRef) {
            end.linkTo(parent.start)
        }

        constrain(viewerRef) {
            start.linkTo(parent.start)
        }

        constrain(newMailButtonRef) {
            width = Dimension.matchParent
            height = Dimension.fillToConstraints

            start.linkTo(parent.start)
            top.linkTo(parent.top)
            bottom.linkTo(mailToolbarRef.top, 8.dp)
        }

        setToolbarConstraints()
        setVisibleMailToolbarConstraints()
    }

    constraintSet(HomeState.MailOpenExpanded.tag) {
        val midGuideline = createGuidelineFromAbsoluteLeft(0.5f)

        constrain(listRef, viewerRef) {
            width = Dimension.fillToConstraints
            height = Dimension.fillToConstraints

            top.linkTo(toolbarRef.bottom)
            bottom.linkTo(parent.bottom)
        }

        constrain(listRef) {
            start.linkTo(parent.start)
            end.linkTo(midGuideline)
        }
        constrain(viewerRef) {
            start.linkTo(midGuideline)
            end.linkTo(parent.end)
        }

        constrain(newMailButtonRef) {
            width = Dimension.matchParent
            height = Dimension.fillToConstraints

            start.linkTo(parent.start)
            top.linkTo(parent.top)
            bottom.linkTo(mailToolbarRef.top, 8.dp)
        }

        setToolbarConstraints()
        setVisibleMailToolbarConstraints()
    }

    constraintSet(HomeState.MailOpenHalf.tag) {
        val midGuideline = createGuidelineFromTop(0.5f)

        constrain(viewerRef) {
            width = Dimension.matchParent
            height = Dimension.fillToConstraints

            start.linkTo(parent.start)
            top.linkTo(parent.top)
            bottom.linkTo(midGuideline)
        }

        constrain(toolbarRef) {
            width = Dimension.matchParent
            height = Dimension.wrapContent

            top.linkTo(midGuideline)
            start.linkTo(parent.start)
        }

        constrain(listRef) {
            width = Dimension.matchParent
            height = Dimension.fillToConstraints

            start.linkTo(parent.start)
            top.linkTo(toolbarRef.bottom)
            bottom.linkTo(parent.bottom)
        }

        constrain(newMailButtonRef) {
            width = Dimension.matchParent
            height = Dimension.fillToConstraints

            start.linkTo(parent.start)
            top.linkTo(parent.top)
            bottom.linkTo(mailToolbarRef.top, 8.dp)
        }
        setVisibleMailToolbarConstraints()
    }
    defaultTransition(listOnlyCSet, mailCompactCSet) {
        // Do nothing
    }
}

@Composable
fun ComposeMailHome(modifier: Modifier) {
    val mailModel: ComposeMailModel = viewModel()
    val listState = remember { MailListState() }
    val newMailState = rememberNewMailState(initialLayoutState = NewMailLayoutState.Fab)

    val isCompact = LocalWidthSizeClass.current == WindowWidthSizeClass.Compact
    val isHalfOpen = LocalFoldableInfo.current.isHalfOpen

    val currentConstraintSet = resolveConstraintSet(
        isMailOpen = mailModel.isMailOpen(),
        isCompact = isCompact,
        isHalfOpen = isHalfOpen
    )

    MotionLayout(
        motionScene = homeMotionScene,
        constraintSetName = currentConstraintSet.tag,
        animationSpec = tween(400),
        modifier = modifier,
    ) {
        TopToolbar(
            modifier = Modifier
                .layoutId("toolbar"),
            selectionCountProvider = listState::selectedCount,
            onUnselectAll = listState::unselectAll
        )
        Column(Modifier.layoutId("list")) {
            Text(
                modifier = Modifier.padding(horizontal = 4.dp),
                text = "Inbox",
                style = MaterialTheme.typography.h6
            )
            MailList(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .weight(1.0f, true)
                    .fillMaxWidth(),
                listState = listState,
                observableConversations = mailModel.conversations,
                onMailOpen = mailModel::openMail
            )
        }
        MailViewer(
            modifier = Modifier
                .layoutId("viewer")
                .padding(8.dp),
            mailInfoFull = mailModel.openedMail ?: MailInfoFull.Default
        )
        NewMailButton(
            modifier = Modifier.layoutId("newMailButton"),
            state = newMailState
        )
        MailToolbar(
            modifier = Modifier.layoutId("mailToolbar"),
            onCloseMail = mailModel::closeMail
        )
    }
}

@Composable
private fun resolveConstraintSet(
    isMailOpen: Boolean,
    isCompact: Boolean,
    isHalfOpen: Boolean
): HomeState {
    if (isMailOpen) {
        if (isCompact) {
            return HomeState.MailOpenCompact
        } else {
            if (isHalfOpen) {
                return HomeState.MailOpenHalf
            } else {
                return HomeState.MailOpenExpanded
            }
        }
    }
    return HomeState.ListOnly
}