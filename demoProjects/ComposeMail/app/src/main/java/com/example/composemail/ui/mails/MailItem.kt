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

package com.example.composemail.ui.mails

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import com.example.composemail.model.data.MailInfoPeek
import com.example.composemail.ui.components.ContactImage
import com.example.composemail.ui.components.OneLineText
import com.example.composemail.ui.theme.Selection
import com.example.composemail.ui.utils.toHourMinutes

/**
 * Composable to display a mail entry, given by [MailInfoPeek].
 *
 * When [info] is null, it will display a loading indicator, if the info
 * then changes to not-null it will animate the transition to show the info and
 * dismiss the loading indicator.
 *
 * @see PreviewConversationLoading
 */
@Composable
fun MailItem(
    modifier: Modifier = Modifier,
    state: MailItemState = MailItemState(-1) { _, _ -> },
    info: MailInfoPeek?,
    onMailOpen: (id: Int) -> Unit
) {
    // The layout (as a ConstraintSet ID) we want the Composable to take,
    // MotionLayout will animate the transition to that layout
    val targetState: MotionMailState =
        when {
            // No info to display, show a Loading state
            info == null -> MotionMailState.Loading
            // The item is selected, show as selected
            state.isSelected -> MotionMailState.Selected
            // The 'normal' state that just displays the given info
            else -> MotionMailState.Normal
        }
    MotionLayoutMail(
        modifier = modifier,
        info = info ?: MailInfoPeek.Default,
        targetState = targetState,
        onToggledMail = {
            // Toggle selection
            state.setSelected(!state.isSelected)
        },
        onOpenedMail = onMailOpen
    )
}

const val ANIMATION_DURATION: Int = 400

/**
 * An enum that represents the different layout states of the Composable.
 *
 * Each corresponds to a ConstraintSet in the MotionScene.
 */
enum class MotionMailState(val tag: String) {
    Loading("empty"),
    Normal("normal"),
    Selected("flipped")
}

@Suppress("EXPERIMENTAL_API_USAGE")
@Composable
fun MotionLayoutMail(
    modifier: Modifier = Modifier,
    info: MailInfoPeek,
    targetState: MotionMailState,
    onToggledMail: (id: Int) -> Unit,
    onOpenedMail: (id: Int) -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = when (targetState) {
            MotionMailState.Selected -> Selection.backgroundColor
            else -> MaterialTheme.colors.background
        },
        animationSpec = tween<Color>(ANIMATION_DURATION)
    )
    val initialStart = remember { targetState }
    val initialEnd = remember {
        when (initialStart) {
            MotionMailState.Loading -> MotionMailState.Normal
            else -> MotionMailState.Loading
        }
    }
    val motionScene = remember {
        MotionScene {
            val (pictureRef, checkRef, contentRef, loadingRef) = createRefsFor(
                "picture",
                "check",
                "content",
                "loading"
            )
            val normalCSet = constraintSet(MotionMailState.Normal.tag) {
                constrain(pictureRef) {
                    width = 60.dp.asDimension
                    height = 60.dp.asDimension
                    centerVerticallyTo(parent)
                    start.linkTo(parent.start)
                }
                constrain(checkRef) {
                    width = 60.dp.asDimension
                    height = 60.dp.asDimension
                    centerVerticallyTo(parent)
                    start.linkTo(parent.start)

                    rotationY = 180f
                    alpha = 0.0f
                }
                constrain(contentRef) {
                    width = Dimension.fillToConstraints
                    height = 60.dp.asDimension
                    top.linkTo(pictureRef.top)
                    start.linkTo(pictureRef.end, 8.dp)
                    end.linkTo(parent.end, 8.dp)
                }
                constrain(loadingRef) {
                    width = 60.dp.asDimension
                    height = 60.dp.asDimension
                    centerVerticallyTo(parent)
                    end.linkTo(parent.start, 32.dp)
                }
            }
            val selectedCSet = constraintSet(MotionMailState.Selected.tag, normalCSet) {
                constrain(pictureRef) {
                    rotationY = -180f
                    alpha = 0.0f
                }
                constrain(checkRef) {
                    rotationY = 0f
                    alpha = 1f
                }
            }
            val loadingCSet = constraintSet(MotionMailState.Loading.tag) {
                constrain(pictureRef) {
                    width = 60.dp.asDimension
                    height = 60.dp.asDimension
                    top.linkTo(contentRef.top)
                    start.linkTo(parent.end, 8.dp)
                }
                constrain(checkRef) {
                    width = 60.dp.asDimension
                    height = 60.dp.asDimension
                    top.linkTo(contentRef.top)
                    start.linkTo(parent.end, 8.dp)

                    rotationY = 180f
                    alpha = 0.0f
                }
                constrain(contentRef) {
                    width = 120.dp.asDimension
                    height = 60.dp.asDimension
                    centerVerticallyTo(parent)
                    start.linkTo(pictureRef.end, 32.dp)
                }
                constrain(loadingRef) {
                    width = 60.dp.asDimension
                    height = 60.dp.asDimension
                    centerTo(parent)
                }
            }
            val initialStartCSet = when (initialStart) {
                MotionMailState.Normal -> normalCSet
                MotionMailState.Loading -> loadingCSet
                MotionMailState.Selected -> selectedCSet
            }
            val initialEndCSet = when (initialStart) {
                MotionMailState.Normal -> normalCSet
                MotionMailState.Loading -> loadingCSet
                MotionMailState.Selected -> selectedCSet
            }
            defaultTransition(initialStartCSet, initialEndCSet) {
                // Do nothing
            }
        }
    }

//    val motionSceneContent = remember {
//        //language=json5
//        """
//{
//  ConstraintSets: {
//    ${MotionMailState.Normal.tag}: {
//      picture: {
//        width: 60, height: 60,
//        centerVertically: 'parent',
//        start: ['parent', 'start', 0],
//      },
//      check: {
//        width: 60, height: 60,
//        centerVertically: 'parent',
//        start: ['parent', 'start', 0],
//        rotationY: 180,
//        alpha: 0.0
//      },
//      content: {
//        width: 'spread', height: 60,
//        top: ['picture', 'top', 0],
//        start: ['picture', 'end', 8], end: ['parent', 'end', 8],
//      },
//      loading: {
//        width: 60, height: 60,
//        centerVertically: 'parent',
//        end: ['parent', 'start', 32]
//      }
//    },
//    ${MotionMailState.Selected.tag}: {
//      Extends: '${MotionMailState.Normal.tag}',
//      picture: {
//        rotationY: -180,
//        alpha: 0.0
//      },
//      check: {
//        rotationY: 0,
//        alpha: 1.0
//      }
//    },
//    ${MotionMailState.Loading.tag}: {
//      picture: {
//        width: 60, height: 60,
//        top: ['content', 'top', 0],
//        start: ['parent', 'end', 8]
//      },
//      check: {
//        width: 60, height: 60,
//        top: ['content', 'top', 0],
//        start: ['parent', 'end', 8],
//        rotationY: 180,
//        alpha: 0.0
//      },
//      content: {
//        width: 120, height: 60,
//        centerVertically: 'parent',
//        start: ['picture', 'end', 32],
//      },
//      loading: {
//        width: 60, height: 60,
//        center: 'parent',
//      }
//    }
//  },
//  Transitions: {
//    default: {
//      from: '${initialStart.tag}',
//      to: '${initialEnd.tag}',
//    },
//    flip: {
//      from: '${MotionMailState.Normal.tag}',
//      to: '${MotionMailState.Selected.tag}',
//      KeyFrames: {
//        KeyAttributes: [
//          {
//            target: ['picture', 'check'],
//            frames: [50],
//            scaleX: [0.6],
//            scaleY: [0.6]
//          },
//        ]
//      }
//    }
//  }
//}"""
//    }
    val interactionSource = remember { MutableInteractionSource() }
    MotionLayout(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
            .background(color = backgroundColor)
            .indication(
                interactionSource, // Consume MailContent's interactions
                rememberRipple(bounded = true)
            )
            .padding(8.dp),
        constraintSetName = targetState.tag,
        animationSpec = tween<Float>(ANIMATION_DURATION),
        motionScene = motionScene
    ) {
        ContactImage(
            modifier = Modifier.layoutId("picture"),
            uri = info.from.profilePic,
            onClick = { onToggledMail(info.id) }
        )
        Image(
            modifier = Modifier
                .layoutId("check")
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colors.secondary),
            imageVector = Icons.Default.Check,
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onSecondary),
            contentDescription = null
        )
        MailContent(
            modifier = Modifier
                .layoutId("content")
                .clickable(
                    interactionSource = interactionSource,
                    indication = null, // Show no indication, delegate it to the parent
                    onClick = { onOpenedMail(info.id) }
                ),
            info = info
        )
        Box(
            modifier = Modifier.layoutId("loading"),
            contentAlignment = Alignment.Center
        ) {
            // TODO: Consider leaving it until the transition from Loading to anything else finishes
            if (targetState == MotionMailState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}

@Composable
fun MailContent(
    modifier: Modifier = Modifier,
    info: MailInfoPeek
) {
    Column(
        modifier = modifier.padding(vertical = 4.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            OneLineText(
                modifier = Modifier.weight(1.0f, true),
                text = info.from.name,
                style = MaterialTheme.typography.body1,
                overflow = TextOverflow.Ellipsis
            )
            OneLineText(
                text = info.timestamp.toHourMinutes(),
                style = MaterialTheme.typography.body2,
            )
        }
        OneLineText(
            text = info.shortContent,
            style = MaterialTheme.typography.body2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview
@Composable
private fun PreviewConversationLoading() {
    var info: MailInfoPeek? by remember { mutableStateOf(null) }
    Column(
        modifier = Modifier.size(300.dp, 200.dp)
    ) {
        Button(onClick = { info = MailInfoPeek.Default }) {
            Text("Run")
        }
        MailItem(
            info = info,
            onMailOpen = { /* Do nothing */ }
        )
    }
}

@Preview
@Composable
private fun PreviewConversation() {
    MailItem(
        info = MailInfoPeek.Default,
        onMailOpen = { /* Do nothing */ }
    )
}