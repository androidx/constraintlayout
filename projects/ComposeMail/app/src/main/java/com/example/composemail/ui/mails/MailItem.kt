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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import coil.compose.rememberImagePainter
import com.example.composemail.model.data.MailEntryInfo
import com.example.composemail.ui.theme.textBackgroundColor

@Composable
fun MailItem(
    modifier: Modifier = Modifier,
    state: MailItemState = MailItemState(-1) { _, _ -> },
    info: MailEntryInfo?
) {
    val shouldAnimate = remember { info == null }
    var csTarget: String? by remember { mutableStateOf(null) }
    MotionLayoutMail(
        modifier = modifier,
        info = info ?: MailEntryInfo.Default,
        startsEmpty = shouldAnimate,
        targetId = csTarget,
        onSelectedMail = {
            state.setSelected(!state.isSelected)
        }
    )
    if (shouldAnimate) {
        if (info != null && csTarget == null) {
            SideEffect {
                csTarget = "normal"
            }
        }
    } else {
        if (info != null) {
            val nextState = if (state.isSelected) {
                "flipped"
            } else {
                "normal"
            }
            SideEffect {
                csTarget = nextState
            }
        }
    }
}

const val ANIMATION_DURATION: Int = 400

@Suppress("NOTHING_TO_INLINE", "EXPERIMENTAL_API_USAGE")
@Composable
inline fun MotionLayoutMail(
    modifier: Modifier = Modifier,
    info: MailEntryInfo,
    startsEmpty: Boolean,
    targetId: String?,
    crossinline onSelectedMail: (id: Int) -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (targetId == "flipped") {
            MaterialTheme.colors.textBackgroundColor
        } else {
            MaterialTheme.colors.background
        },
        animationSpec = tween<Color>(ANIMATION_DURATION)
    )
    val startId = if (startsEmpty) "empty" else "normal"
    val endId = if (startsEmpty) "normal" else "empty"

    val motionSceneContent = remember(startId, endId) {
        //language=json5
        """
{
  ConstraintSets: {
    normal: {
      picture: {
        width: 60, height: 60,
        centerVertically: 'parent',
        start: ['parent', 'start', 0],
      },
      check: {
        width: 60, height: 60,
        centerVertically: 'parent',
        start: ['parent', 'start', 0],
        rotationY: 180,
        alpha: 0.0
      },
      content: {
        width: 'spread', height: 60,
        top: ['picture', 'top', 0],
        start: ['picture', 'end', 8], end: ['parent', 'end', 8],
      },
      loading: {
        width: 60, height: 60,
        centerVertically: 'parent',
        end: ['parent', 'start', 32]
      }
    },
    flipped: {
      Extends: 'normal',
      picture: {
        rotationY: -180,
        alpha: 0.0
      },
      check: {
        rotationY: 0,
        alpha: 1.0
      }
    },
    empty: {
      picture: {
        width: 60, height: 60,
        top: ['content', 'top', 0],
        start: ['parent', 'end', 8]
      },
      check: {
        width: 60, height: 60,
        top: ['content', 'top', 0],
        start: ['parent', 'end', 8],
        rotationY: 180,
        alpha: 0.0
      },
      content: {
        width: 120, height: 60,
        centerVertically: 'parent',
        start: ['picture', 'end', 32],
      },
      loading: {
        width: 60, height: 60,
        center: 'parent',
      }
    }
  },
  Transitions: {
    default: {
      from: '$startId',
      to: '$endId',
    },  
    flip: {
      from: 'normal',
      to: 'flipped',
      KeyFrames: {
        KeyAttributes: [
          {
            target: ['picture', 'check'],
            frames: [50],
            scaleX: [0.6],
            scaleY: [0.6]
          },
        ]
      }
    }
  }
}"""
    }
    MotionLayout(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .fillMaxSize()
            .clip(RectangleShape)
            .padding(8.dp),
        constraintSetName = targetId,
        animationSpec = tween<Float>(ANIMATION_DURATION),
        motionScene = MotionScene(content = motionSceneContent)
    ) {
        Image(
            modifier = Modifier
                .layoutId("picture")
                .clip(RoundedCornerShape(10.dp))
                .clickable { onSelectedMail(info.id) },
            painter = rememberImagePainter(data = info.from.profilePic.toString()),
            contentDescription = null
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
            modifier = Modifier.layoutId("content"),
            info = info
        )
        Box(
            modifier = Modifier.layoutId("loading"),
            contentAlignment = Alignment.Center
        ) {
            // TODO: Find a good way of not composing this when animation ends
            CircularProgressIndicator(
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Suppress("NOTHING_TO_INLINE")
@Composable
inline fun MailContent(
    modifier: Modifier = Modifier,
    info: MailEntryInfo
) {
    Column(
        modifier = modifier.padding(vertical = 4.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                modifier = Modifier.weight(1.0f, true),
                text = info.from.name,
                style = MaterialTheme.typography.body1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = info.timestamp,
                style = MaterialTheme.typography.body2,
            )
        }
        Text(
            text = info.shortContent,
            style = MaterialTheme.typography.body2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview
@Composable
private fun PreviewConversationLoading() {
    var info: MailEntryInfo? by remember { mutableStateOf(null) }
    Column(
        modifier = Modifier.size(300.dp, 200.dp)
    ) {
        Button(onClick = { info = MailEntryInfo.Default }) {
            Text("Run")
        }
        MailItem(
            info = info
        )
    }
}

@Preview
@Composable
private fun PreviewConversation() {
    MailItem(
        info = MailEntryInfo.Default
    )
}