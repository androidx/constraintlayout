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

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import coil.compose.rememberImagePainter
import com.example.composemail.model.data.MailEntryInfo

@Composable
fun MailItem(
    modifier: Modifier = Modifier,
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
            csTarget = if (csTarget == "flipped") "normal" else "flipped"
        }
    )

    if (shouldAnimate && info != null && csTarget == null) {
        SideEffect {
            csTarget = "normal"
        }
    }
}

@Suppress("NOTHING_TO_INLINE", "EXPERIMENTAL_API_USAGE")
@Composable
inline fun MotionLayoutMail(
    modifier: Modifier = Modifier,
    info: MailEntryInfo,
    startsEmpty: Boolean,
    targetId: String?,
    crossinline onSelectedMail: (Int) -> Unit
) {
    val startId = if(startsEmpty) "empty" else "normal"
    val endId = if(startsEmpty) "normal" else "empty"
    MotionLayout(
        modifier = modifier
            .fillMaxSize()
            .clip(RectangleShape)
            .padding(8.dp),
        constraintSetName = targetId,
        animationSpec = tween(400),
        motionScene = MotionScene(content = """
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
    }
  }
}
    """.trimIndent())
    ) {
        Image(
            modifier = Modifier
                .layoutId("picture")
                .clip(RoundedCornerShape(10.dp))
                .clickable { onSelectedMail(info.id) },
            painter = rememberImagePainter(data = (info.from.profilePic).toString()),
            contentDescription = null
        )
        Image(
            modifier = Modifier
                .layoutId("check")
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colors.secondary),
            imageVector = Icons.Default.Check,
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
fun PreviewConversation() {
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