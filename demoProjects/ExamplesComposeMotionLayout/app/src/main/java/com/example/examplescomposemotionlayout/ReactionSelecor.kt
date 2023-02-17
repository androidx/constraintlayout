package com.example.examplescomposemotionlayout

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene

/**
 * A demo of using MotionLayout to create a complex animated emoji selector
 */
@OptIn(ExperimentalMotionApi::class)
@Preview(group = "scroll")
@Composable
fun ReactionSelector() {
    var selected by remember { mutableStateOf(3) }
    val transitionName = remember { mutableStateOf("transition1") }
    val emojis = "😀 🙂 🤨 😐 😒 😬".split(' ')
    val emojiNames = listOf<String>(
        "Grinning Face",
        "Slightly Smiling Face",
        "Face with Raised Eyebrow",
        "Neutral Face",
        "Unamused Face",
        "Grimacing Face"
    )

    var scene = MotionScene() {
        val emojiIds = emojis.map { createRefFor(it) }.toTypedArray()
        val titleIds = emojiNames.map { createRefFor(it) }.toTypedArray()

        val start1 = constraintSet {
            createHorizontalChain(elements = emojiIds)
            emojiIds.map {
                constrain(it) {
                    top.linkTo(parent.top, 10.dp)
                }
            }
            titleIds.mapIndexed { index, title ->
                constrain(title) {
                    top.linkTo(emojiIds[0].bottom, 10.dp)
                    start.linkTo(emojiIds[index].start)
                    end.linkTo(emojiIds[index].end)
                    bottom.linkTo(parent.bottom, 10.dp)
                    scaleX = 0.1f
                    alpha = 0f
                }
            }
        }
        val ends = titleIds.map {
            constraintSet(extendConstraintSet = start1) {
                constrain(it) {
                    scaleX = 1f
                    alpha = 1f
                }
            }
        }
        ends.mapIndexed { index, end ->
            transition(start1, end,"transition$index") {
            }
        }
    }

    val progress = remember { Animatable(0f) }
    LaunchedEffect(selected) {
        progress.snapTo(0f)
        transitionName.value = "transition$selected"
        progress.animateTo(
            1f,
            animationSpec = tween(800)
        )
    }

    Column {
        MotionLayout(
            modifier = Modifier
                .background(Color(0xff334433))
                .fillMaxWidth(),
            motionScene = scene,
            transitionName = transitionName.value,
            progress = progress.value
        ) {
            emojis.mapIndexed { index, icon ->
                Text(text = icon, modifier = Modifier
                    .layoutId(icon)
                    .clickable() {
                        selected = index
                    })
            }
            emojiNames.mapIndexed { index, name ->
                Text(
                    text = name, color = Color.White,
                    modifier = Modifier.layoutId(name)
                )
            }
        }
    }
}
