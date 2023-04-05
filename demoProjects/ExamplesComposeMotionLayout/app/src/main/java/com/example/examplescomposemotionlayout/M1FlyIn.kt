package com.example.examplescomposemotionlayout

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.*
import kotlin.math.abs

// A simple fly in effect
@SuppressLint("Range")
@OptIn(ExperimentalMotionApi::class)
@Preview(group = "motion101")
@Composable
fun M1FlyIn() {
    val imgId = "image"
    val id = arrayOf<String>("w1", "w2", "w3", "w4", "w5", "w6")
    val emojis = "😀 🙂 🤨 😐 😒 😬".split(' ')

    var scene = MotionScene() {
        val refs = id.map { createRefFor(it) }.toTypedArray()
        val imgRef = createRefFor("image")
        val start1 = constraintSet {
            constrain(imgRef) {
                width = Dimension.value(10.dp)
                height = Dimension.value(10.dp)
                alpha = 0f
                centerHorizontallyTo(parent)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom, 100.dp)
                customFloat("sat", 0f)
                customFloat("bright", 0f)
                customFloat("rot", -360f)
            }
            for (i in refs.indices) {
                constrain(refs[i]) {
                    width = Dimension.value(32.dp)
                    height = Dimension.value(32.dp)
                    bottom.linkTo(parent.bottom, (abs(i * 2 - refs.size + 1) * 200).dp)
                    centerHorizontallyTo(parent, (i % 2) * 4 - 2f)

                }
            }
        }

        val end1 = constraintSet {
            constrain(imgRef) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
                centerHorizontallyTo(parent)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom, 100.dp)
                customFloat("sat", 1f)
                customFloat("bright", 1f)
                customFloat("rot", 0f)
            }
            createHorizontalChain(elements = refs)
            for (i in refs.indices) {
                constrain(refs[i]) {
                    width = Dimension.value(32.dp)
                    height = Dimension.value(32.dp)
                    bottom.linkTo(parent.bottom, 16.dp)
                }
            }
        }
        transition(start1, end1, "default") {
              motionArc = Arc.StartHorizontal

            keyPositions(*refs) {
                frame(50) {
                    type = RelativePosition.Delta
                    percentY = 0.1f
                }
            }
            keyAttributes(*refs) {
                frame(50) {
                    scaleX = 6f
                    scaleY = 6f
                }
            }
            keyAttributes(imgRef) {
                frame(70) {
                    customFloat("sat", 0f)
                    customFloat("bright", 1.6f)

                }
            }
            keyCycles(imgRef) {
                frame(0) {
                    period = 0f
                    translationY = 0f
                }
                frame(50) {
                    period = 1f
                    translationY = 200f
                }
                frame(100) {
                    period = 0f
                    translationY = 0f
                }
            }
        }
    }
    val painter = painterResource(id = R.drawable.pepper)

    var animateToEnd by remember { mutableStateOf(true) }
    val progress = remember { Animatable(0f) }
    LaunchedEffect(animateToEnd) {
        progress.animateTo(
            if (animateToEnd) 1f else 0f,
            animationSpec = tween(5000)
        )
    }
    MotionLayout(
        modifier = Modifier
            .background(Color(0xFF221010))
            .fillMaxSize()
            .padding(1.dp),
        motionScene = scene,
        progress = progress.value
    ) {
        MotionImage(
            painter = painter,
            brightness = customFloat(imgId, "bright"),
            saturation = customFloat(imgId, "sat"),
            rotate = customFloat(imgId, "rot"),
            modifier = Modifier.layoutId(imgId)
        )
        for (i in id.indices) {
            Box(
                modifier = Modifier
                    .layoutId(id[i])
                    .clip(RoundedCornerShape(20.dp))
            ) {
                Text(text = emojis[i])
            }
        }
    }
}

