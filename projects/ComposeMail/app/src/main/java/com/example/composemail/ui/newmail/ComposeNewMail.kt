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

package com.example.composemail.ui.newmail

import android.annotation.SuppressLint
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.*
import com.example.composemail.ui.theme.ComposeMailTheme
import kotlinx.coroutines.launch
import java.util.*
import kotlin.jvm.internal.Ref


@Composable
fun ComposeNewMail(
    modifier: Modifier = Modifier
) {
    var nextState by remember { mutableStateOf<String?>("full") }

    fun Modifier.clickFromTo(start: String, end: String): Modifier =
        if (nextState == start) {
            clickable {
                nextState = end
            }
        } else {
            this
        }

    MotionLayout(
        modifier = modifier,
        animationSpec = tween(800),
        debug = EnumSet.of(MotionLayoutDebugFlags.NONE),
        motionScene = MotionScene(
            """
{
                ConstraintSets: {
                  fab: {
                    iconBox: {
                      width: 50,
                      height: 50,
                      end: ['parent', 'end', 8],
                      bottom: ['parent', 'bottom', 8],
                      alpha: 1.0,
                    },
                    mailBox: {
                      width: 50,
                      height: 50,
                      end: ['parent', 'end', 8],
                      bottom: ['parent', 'bottom', 8],
                      alpha: 0.0,
                    },
                    miniBox: {
                      width: 50,
                      height: 50,
                      end: ['parent', 'end', 8],
                      bottom: ['parent', 'bottom', 8],
                      custom: { 
                        mValue: 0.0
                      },
                    }
                  },
                  full : {
                    iconBox: {
                      width: 'spread',
                      height: 'spread',
                      centerHorizontally: 'parent',
                      centerVertically: 'parent',
                      alpha: 0.0,
                    },
                    mailBox: {
                      width: 'spread',
                      height: 'spread',
                      end: ['parent', 'end', 0],
                      start: ['parent', 'start', 0],
                      top: ['parent', 'top', 0],
                      bottom: ['parent', 'bottom', 0],
                      alpha: 1.0,
                    },
                    miniBox: {
                      width: 'spread',
                      height: 50,
                      top: ['parent', 'top', 40],
                      end: ['parent', 'end', 8],
                      start: ['parent', 'start', 8],
                      custom: { 
                        mValue: 0.0
                      },
                    }
                  },
                  minimized: {
                    iconBox: {
                      width: 180,
                      height: 50,
                      end: ['parent', 'end', 8],
                      bottom: ['parent', 'bottom', 8],
                      alpha: 0.0,
                    },
                    mailBox: {
                      width: 180,
                      height: 50,
                      end: ['parent', 'end', 8],
                      bottom: ['parent', 'bottom', 8],
                      alpha: 0.0,
                    },
                    miniBox: {
                      width: 180,
                      height: 50,
                      end: ['parent', 'end', 8],
                      bottom: ['parent', 'bottom', 8],
                      custom: { 
                        mValue: 1.0
                      },
                    }
                  }
                },
                Transitions: {
                  default: {
                    from: 'full',
                    to: 'fab'
                  }
                }
}
"""
        ),
        constraintSetName = nextState,
    ) {
        ComposeMailWidget(
            modifier = Modifier
                .layoutId("mailBox"),
            onMinimize = {
                nextState = "minimized"
            },
            onClose = {
                nextState = "fab"
            }
        )
        Surface(
            color = MaterialTheme.colors.primaryVariant,
            contentColor = MaterialTheme.colors.onPrimary,
            modifier = modifier
                .layoutId("miniBox")
                .alpha(motionProperties(id = "miniBox").value.float("mValue")),
            elevation = 4.dp,
            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    modifier = Modifier.weight(1.0f, true),
                    text = "Subject"
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Minimize Window",
                    modifier = Modifier.clickFromTo("minimized", "full")
                )
                Spacer(Modifier.width(12.dp))
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Close Window",
                    modifier = Modifier.clickFromTo("minimized", "fab")
                )
            }
        }
        withContentColor(MaterialTheme.colors.onPrimary) {
            Row(
                modifier = Modifier
                    .layoutId("iconBox")
                    .sizeIn(minWidth = 32.dp, minHeight = 32.dp)
                    .background(
                        color = MaterialTheme.colors.primary,
                        shape = RoundedCornerShape(12)
                    )
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Compose Mail",
                    modifier = Modifier
                        .clickFromTo("fab", "full")
                        .fillMaxSize()
                )
            }
        }
    }
}

/*
 TODO: Use and save a data class that tracks the state of the Composable, also make it so that the
    constraints follow the container instead of the container following the constraints. The states
    should be decoupled into an observable Enum. Eg: Enum { Fab, Minimized, Expanded }. And it
    should handle middle transitions internally ("minimizing").
 */
@Composable
fun MotionLayoutMail(
    modifier: Modifier
) {
    val coroutineScope = rememberCoroutineScope()
//    val lastState = remember { targetState }
    var nextState: String? by remember {
//        val actualTarget = if (targetState == "minimized") {
//            if (lastState == "expanded") {
//                "minimizing"
//            } else {
//                targetState
//            }
//        } else if (targetState == "expanded") {
//            if (lastState == "minimized") {
//                "minimizing"
//            } else {
//                targetState
//            }
//        } else {
//            targetState
//        }
        mutableStateOf("fab")
    }
    val states = remember { listOf(null, "expanded", "minimized", "fab") }
    val lastState = remember { Ref.IntRef().apply { element = 0 } }
    val duration = remember { Ref.IntRef().apply { element = 400 } }

    fun Modifier.clickFromTo(start: String, end: String): Modifier =
        if (nextState == start) {
            clickable {
                lastState.element = states.indexOf(start)
                nextState = end
            }
        } else {
            this
        }

    val onFinishedAnimation = {
        when (nextState) {
            "minimizing" -> {
                if (lastState.element == states.indexOf("fab") || lastState.element == states.indexOf("minimized")) {
                    duration.element = 80
                    nextState = "expanded"
                    lastState.element = states.indexOf("expanded")
                } else{
                    duration.element = 80
                    nextState = "minimized"
                    lastState.element = states.indexOf("minimized")
                }

            }
            else -> {
                duration.element = 200
            }
        }
    }
    MotionLayout(
        modifier = modifier,
        animationSpec = tween(duration.element),
        debug = EnumSet.of(MotionLayoutDebugFlags.NONE),
        constraintSetName = nextState,
        finishedAnimationListener = onFinishedAnimation,
        motionScene = MotionScene(
            """
{
    ConstraintSets: {
      expanded : {
        container: {
          width: 'spread', height: 'spread',
          center: 'icon'
        },
        icon: {
          width: 'spread', height: 'spread',
          start: ['header', 'start', 0], end: ['close', 'end', 0],
          bottom: ['parent', 'bottom', 0], top: ['close', 'top', 0],
          alpha: 0.0,
        },
        close: {
          width: 40, height: 40,
          bottom: ['contentBox', 'top', 16],
          end: ['parent', 'end', 0]
        },
        minOrExpand: {
          width: 40, height: 40,
          top: ['close', 'top', 0],
          end: ['close', 'start', 16],
          rotationZ: 180
        },
        header: {
          width: 'spread', height: 40,
          bottom: ['close', 'bottom', 0],
          start: ['parent', 'start', 0],
          end: ['minOrExpand', 'start', 16],
        },
        contentBox: {
          width: 'spread', height: 334,
          centerHorizontally: 'parent',
          bottom: ['parent', 'bottom', 0]
        }
      },
      minimizing: {
        Extends: 'expanded',
        close: {
          clear: ['constraints'],
          end: ['parent', 'end', 0],
          bottom: ['parent', 'bottom', 0]
        },
        minOrExpand: {
          clear: ['transforms'],
          rotationZ: 0
        },
        contentBox: {
          clear: ['constraints'],
          centerHorizontally: 'parent',
          top: ['parent', 'bottom', 16]
        }
      },
      minimized: {
        Extends: 'minimizing',
        header: {
          clear: ['constraints'],
          bottom: ['close', 'bottom', 0],
          end: ['minOrExpand', 'start', 16]
        },
      },
      fab: {
        Extends: 'minimized',
        icon: {
          clear: ['dimensions', 'constraints'],
          width: 50, height: 50,
          bottom: ['parent', 'bottom', 0],
          end: ['parent', 'end', 0],
          alpha: 1.0
        },
        close: {
          clear: ['constraints'],
          bottom: ['minOrExpand', 'bottom', 0],
          start: ['minOrExpand', 'end', 0]
        },
        minOrExpand: {
          clear: ['constraints'],
          top: ['header', 'top', 0],
          start: ['header', 'end', 16],
        },
        header: {
          clear: ['constraints'],
          bottom: ['parent', 'bottom', 0],
          start: ['parent', 'end', 0]
        },
      }
    },
    Transitions: {
      default: {
        from: 'fab',
        to: 'minimizing'
      }
    }
}
""".trimIndent()
        )
    ) {
        Box(
            modifier = Modifier
                .layoutId("container")
                .background(Color(0xFFF0F0F0))
        )
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Compose Mail",
            modifier = Modifier
                .padding(end = 8.dp)
                .layoutId("icon")
                .clickFromTo("fab", "minimizing")
        )
        Box(
            modifier = Modifier.layoutId("header"),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                modifier = Modifier
                    .padding(start = 8.dp),
                text = "Subject"
            )
        }
        Icon(
            modifier = Modifier
                .layoutId("minOrExpand")
                .clickFromTo("expanded", "minimizing")
                .clickFromTo("minimized", "minimizing"),
            contentDescription = "Minimize or Expand",
            imageVector = Icons.Default.KeyboardArrowUp
        )
        Icon(
            modifier = Modifier
                .layoutId("close")
                .clickFromTo("expanded", "fab")
                .clickFromTo("minimized", "fab"),
            contentDescription = "Cancel New Mail",
            imageVector = Icons.Default.Close
        )
        Column(
            modifier = Modifier
                .layoutId("contentBox")
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = "",
                onValueChange = {},
                placeholder = { Text(text = "Recipient") }
            )
            Spacer(Modifier.height(16.dp))
            TextField(
                modifier = Modifier.weight(1.0f, true).fillMaxWidth(),
                value = "",
                onValueChange = {},
                placeholder = { Text(text = "Message") }
            )
        }
    }
}

class MailAnimation {
    var nextState: String? = null
}

@Suppress("NOTHING_TO_INLINE")
@SuppressLint("ComposableNaming")
@Composable
inline fun withContentColor(color: Color, noinline content: @Composable () -> Unit) {
    CompositionLocalProvider(values = arrayOf(LocalContentColor provides color), content = content)
}

@Composable
fun ComposeMailWidget(
    modifier: Modifier = Modifier,
    onMinimize: () -> Unit,
    onClose: () -> Unit
) {
    Surface(
        modifier = modifier.padding(top = 16.dp, start = 4.dp, end = 4.dp, bottom = 0.dp),
        elevation = 6.dp,
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
    ) {
        ConstraintLayout(
            modifier = Modifier.padding(top = 4.dp, start = 8.dp, end = 8.dp, bottom = 0.dp),
            constraintSet = ConstraintSet(
                """
                {
                    topToolbar: {
                      width: 'spread',
                      top: ['parent', 'top', 0],
                      centerHorizontally: 'parent'
                    },
                    recipient: {
                      top: ['topToolbar', 'bottom', 2],
                      width: 'spread',
                      centerHorizontally: 'parent',
                    },
                    subject: { 
                      top: ['recipient', 'bottom', 4],
                      width: 'spread',
                      centerHorizontally: 'parent',
                    },
                    message: {
                      height: "spread",
                      width: 'spread',
                      centerHorizontally: 'parent',
                      top: ['subject', 'bottom', 4],
                      bottom: ['bottomToolbar', 'top', 4],
                    },
                    bottomToolbar: {
                      width: 'spread',
                      centerHorizontally: 'parent',
                      bottom: ['parent', 'bottom', 16],
                    },
                }
            """.trimIndent()
            )
        ) {
            println("Composing CL")
            Row(
                modifier = Modifier.layoutId("topToolbar"),
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Minimize Window",
                    modifier = Modifier.clickable(onClick = onMinimize)
                )
                Spacer(Modifier.width(12.dp))
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Close Window",
                    modifier = Modifier.clickable(onClick = onClose)
                )
            }
            TextField(
                modifier = Modifier.layoutId("recipient"),
                value = "",
                onValueChange = {},
                placeholder = {
                    Text("Recipients")
                }
            )
            TextField(
                modifier = Modifier.layoutId("subject"),
                value = "",
                onValueChange = {},
                placeholder = {
                    Text("Subject")
                }
            )
            TextField(
                modifier = Modifier
                    .layoutId("message")
                    .fillMaxHeight(),
                value = "",
                onValueChange = {},
                placeholder = {
                    Text("Message")
                }
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.layoutId("bottomToolbar")
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
}

@Preview(group = "states1")
@Composable
fun ComposeMessagePreview() {
    ComposeMailTheme {
        Surface(
            modifier = Modifier.size(400.dp, 700.dp)
        ) {
            ComposeNewMail(modifier = Modifier.fillMaxSize())
        }
    }
}