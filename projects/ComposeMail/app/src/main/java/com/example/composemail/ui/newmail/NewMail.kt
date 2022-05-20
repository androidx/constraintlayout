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

package com.example.composemail.ui.newmail

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionLayoutScope
import androidx.constraintlayout.compose.MotionScene
import com.example.composemail.ui.components.CheapText

@Preview
@Composable
fun NewMotionMessagePreview() {
    val newMailState = rememberNewMailState(initialLayoutState = NewMailLayoutState.Full)
    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = newMailState::setToFab) {
                Text("Fab")
            }
            Button(onClick = newMailState::setToFull) {
                Text("Full")
            }
            Button(onClick = newMailState::setToMini) {
                Text("Mini")
            }
        }
        NewMailButton(
            modifier = Modifier.fillMaxSize(),
            state = newMailState
        )
    }
}

@Composable
private fun messageMotionScene(initialState: NewMailLayoutState): MotionScene {
    val startStateName = remember { initialState.name }
    val endStateName = remember {
        when (initialState) {
            NewMailLayoutState.Fab -> NewMailLayoutState.Full
            NewMailLayoutState.Mini -> NewMailLayoutState.Fab
            NewMailLayoutState.Full -> NewMailLayoutState.Fab
        }.name
    }
    val primary = MaterialTheme.colors.primary.toHexString()
    val primaryVariant = MaterialTheme.colors.primaryVariant.toHexString()
    val onPrimary = MaterialTheme.colors.onPrimary.toHexString()
    val surface = MaterialTheme.colors.surface.toHexString()
    val onSurface = MaterialTheme.colors.onSurface.toHexString()

    return MotionScene(
        content =
        """
        {
          ConstraintSets: {
            ${NewMailLayoutState.Fab.name}: {
              box: {
                width: 50, height: 50,
                end: ['parent', 'end', 12],
                bottom: ['parent', 'bottom', 12],
                custom: {
                  background: '#$primary'
                }
              },
              minIcon: {
                width: 40, height: 40,
                end: ['editClose', 'start', 8],
                top: ['editClose', 'top', 0],
                visibility: 'gone',
                custom: {
                  content: '#$onPrimary'
                }
              },
              editClose: {
                width: 40, height: 40,
                centerHorizontally: 'box',
                centerVertically: 'box',
                custom: {
                  content: '#$onPrimary'
                }
              },
              title: {
                width: 'spread',
                top: ['box', 'top', 0],
                bottom: ['editClose', 'bottom', 0],
                start: ['box', 'start', 8],
                end: ['minIcon', 'start', 8],
                custom: {
                  content: '#$onPrimary'
                }
                
                visibility: 'gone'
              },
              content: {
                width: 'spread', height: 'spread',
                start: ['box', 'start', 8],
                end: ['box', 'end', 8],
                
                top: ['editClose', 'bottom', 8],
                bottom: ['box', 'bottom', 8],
                
                visibility: 'gone'
              }
            },
            ${NewMailLayoutState.Full.name}: {
              box: {
                width: 'spread', height: 'spread',
                start: ['parent', 'start', 12],
                end: ['parent', 'end', 12],
                bottom: ['parent', 'bottom', 12],
                top: ['parent', 'top', 40],
                custom: {
                  background: '#$surface'
                }
              },
              minIcon: {
                width: 40, height: 40,
                end: ['editClose', 'start', 8],
                top: ['editClose', 'top', 0],
                custom: {
                  content: '#$onSurface'
                }
              },
              editClose: {
                width: 40, height: 40,
                end: ['box', 'end', 4],
                top: ['box', 'top', 4],
                custom: {
                  content: '#$onSurface'
                }
              },
              title: {
                width: 'spread',
                top: ['box', 'top', 0],
                bottom: ['editClose', 'bottom', 0],
                start: ['box', 'start', 8],
                end: ['minIcon', 'start', 8],
                custom: {
                  content: '#$onSurface'
                }
              },
              content: {
                width: 'spread', height: 'spread',
                start: ['box', 'start', 8],
                end: ['box', 'end', 8],
                
                top: ['editClose', 'bottom', 8],
                bottom: ['box', 'bottom', 8]
              }
            },
            ${NewMailLayoutState.Mini.name}: {
              box: {
                width: 180, height: 50,
                bottom: ['parent', 'bottom', 12],
                end: ['parent', 'end', 12],
                custom: {
                  background: '#$primaryVariant'
                }
              },
              minIcon: {
                width: 40, height: 40,
                end: ['editClose', 'start', 8],
                top: ['editClose', 'top', 0],
                rotationZ: 180,
                custom: {
                  content: '#$onPrimary'
                }
              },
              editClose: {
                width: 40, height: 40,
                end: ['box', 'end', 4],
                top: ['box', 'top', 4],
                custom: {
                  content: '#$onPrimary'
                }
              },
              title: {
                width: 'spread',
                top: ['box', 'top', 0],
                bottom: ['editClose', 'bottom', 0],
                start: ['box', 'start', 8],
                end: ['minIcon', 'start', 8],
                custom: {
                  content: '#$onPrimary'
                }
              },
              content: {
                width: 'spread', height: 'spread',
                start: ['box', 'start', 8],
                end: ['box', 'end', 8],
                
                top: ['editClose', 'bottom', 8],
                bottom: ['box', 'bottom', 8],
                
                visibility: 'gone'
              }
            }
          },
          Transitions: {
            default: {
              from: '$startStateName',
              to: '$endStateName'
            }
          }
        }
    """.trimIndent()
    )
}

@Suppress("NOTHING_TO_INLINE")
@Composable
internal inline fun MotionLayoutScope.MotionMessageContent(
    state: NewMailState
) {
    val currentState = state.currentState
    val focusManager = LocalFocusManager.current
    val dialogName = remember(currentState) {
        when (currentState) {
            NewMailLayoutState.Mini -> "Draft"
            else -> "Message"
        }
    }
    Surface(
        modifier = Modifier.layoutId("box"),
        color = motionColor(id = "box", name = "background"),
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp)
    ) {}
    ColorableIconButton(
        modifier = Modifier.layoutId("editClose"),
        imageVector = when (currentState) {
            NewMailLayoutState.Fab -> Icons.Default.Edit
            else -> Icons.Default.Close
        },
        color = motionColor("editClose", "content"),
        enabled = true
    ) {
        when (currentState) {
            NewMailLayoutState.Fab -> state.setToFull()
            else -> state.setToFab()
        }
    }
    ColorableIconButton(
        modifier = Modifier.layoutId("minIcon"),
        imageVector = Icons.Default.KeyboardArrowDown,
        color = motionColor("minIcon", "content"),
        enabled = true
    ) {
        when (currentState) {
            NewMailLayoutState.Full -> state.setToMini()
            else -> state.setToFull()
        }
    }
    CheapText(
        text = dialogName,
        modifier = Modifier.layoutId("title"),
        color = motionColor("title", "content"),
        style = MaterialTheme.typography.h6
    )
    MessageWidget(modifier = Modifier.layoutId("content"), onDelete = {
        focusManager.clearFocus()
        state.setToFab()
    })
//            MessageWidgetCol(
//                modifier = Modifier
//                    .layoutId("content")
//                    .padding(start = 4.dp, end = 4.dp, bottom = 4.dp)
//            )
}

@Composable
fun NewMailButton(
    modifier: Modifier = Modifier,
    state: NewMailState
) {
    val currentStateName = state.currentState.name
    MotionLayout(
        motionScene = messageMotionScene(state.currentState),
        animationSpec = tween(700),
        constraintSetName = currentStateName,
        modifier = modifier
    ) {
        MotionMessageContent(state = state)
    }
}

@Suppress("NOTHING_TO_INLINE")
@OptIn(ExperimentalMaterialApi::class)
@Composable
internal inline fun ColorableIconButton(
    modifier: Modifier,
    imageVector: ImageVector,
    color: Color,
    enabled: Boolean,
    noinline onClick: () -> Unit
) {
    Surface(
        modifier = modifier,
        color = Color.Transparent,
        contentColor = color,
        onClick = onClick,
        enabled = enabled
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}

// With column
@Suppress("NOTHING_TO_INLINE")
@Composable
internal inline fun MessageWidgetCol(modifier: Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = "",
            onValueChange = {},
            placeholder = {
                Text("Recipients")
            }
        )
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = "",
            onValueChange = {},
            placeholder = {
                Text("Subject")
            }
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .weight(weight = 2.0f, fill = true),
            value = "",
            onValueChange = {},
            placeholder = {
                Text("Message")
            }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Button(onClick = { /*TODO*/ }) {
                Row {
                    Text(text = "Send")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send Mail",
                    )
                }
            }
            Button(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Draft",
                )
            }
        }
    }
}

// With ConstraintLayout
@Preview
@Composable
private fun MessageWidgetPreview() {
    MessageWidget(modifier = Modifier.fillMaxSize())
}

@Suppress("NOTHING_TO_INLINE")
@Composable
internal inline fun MessageWidget(
    modifier: Modifier,
    noinline onDelete: () -> Unit = {}
) {
    val constraintSet = remember {
        ConstraintSet(
            """
                {
                    gl1: { type: 'hGuideline', end: 50 },
                    recipient: {
                      top: ['parent', 'top', 2],
                      width: 'spread',
                      centerHorizontally: 'parent',
                    },
                    subject: { 
                      top: ['recipient', 'bottom', 8],
                      width: 'spread',
                      centerHorizontally: 'parent',
                    },
                    message: {
                      height: 'spread',
                      width: 'spread',
                      centerHorizontally: 'parent',
                      top: ['subject', 'bottom', 8],
                      bottom: ['gl1', 'bottom', 4],
                    },
                    delete: {
                      height: 'spread',
                      top: ['gl1', 'bottom', 0],
                      bottom: ['parent', 'bottom', 4],
                      start: ['parent', 'start', 0]
                    },
                    send: {
                      height: 'spread',
                      top: ['gl1', 'bottom', 0],
                      bottom: ['parent', 'bottom', 4],
                      end: ['parent', 'end', 0]
                    }
                }
            """.trimIndent()
        )
    }
    ConstraintLayout(
        modifier = modifier.padding(top = 4.dp, start = 8.dp, end = 8.dp, bottom = 0.dp),
        constraintSet = constraintSet
    ) {
        OutlinedTextField(
            modifier = Modifier.layoutId("recipient"),
            value = "",
            onValueChange = {},
            label = {
                CheapText("To")
            }
        )
        OutlinedTextField(
            modifier = Modifier.layoutId("subject"),
            value = "",
            onValueChange = {},
            label = {
                CheapText("Subject")
            }
        )
        OutlinedTextField(
            modifier = Modifier
                .layoutId("message")
                .fillMaxHeight(),
            value = "",
            onValueChange = {},
            label = {
                CheapText("Message")
            }
        )
        Button(
            modifier = Modifier.layoutId("send"),
            onClick = onDelete // TODO: Do something different for Send onClick
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CheapText(text = "Send")
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send Mail",
                )
            }
        }
        Button(
            modifier = Modifier.layoutId("delete"),
            onClick = onDelete
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Draft",
            )
        }
    }
}

private fun Color.toHexString() = toArgb().toUInt().toString(16)