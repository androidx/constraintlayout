package com.example.examplescomposemotionlayout

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.*

// A simple fly in effect
@SuppressLint("Range")
@OptIn(ExperimentalMotionApi::class)
@Preview(group = "motion101")
@Composable
fun M3MultiState() {
    val titleId = "title"


    var scene = MotionScene() {
        val titleRef = createRefFor(titleId)
        val a = constraintSet {
            constrain(titleRef) {
                centerHorizontallyTo(parent,0f)
                centerVerticallyTo(parent,0f)
            }
        }

        val b = constraintSet {
            constrain(titleRef) {
                centerHorizontallyTo(parent,1f)
                 centerVerticallyTo(parent,0f)
            }
        }
        val c = constraintSet {
            constrain(titleRef) {
                centerHorizontallyTo(parent,1f)
                centerVerticallyTo(parent,1f)
            }
        }
        transition(  a,b,"loading") {
            keyAttributes(titleRef){
                frame(50) {
                    rotationY = 50f
                }
            }
        }
        transition(  b,c,"normal") {
            keyAttributes(titleRef){
                frame(50) {
                    rotationZ = 90f
                }
            }
        }
    }
    val painter = painterResource(id = R.drawable.pepper)

    var transitionName by remember {
        mutableStateOf("loading")
    }
    var animateToEnd by remember { mutableStateOf(true) }
    val progress = remember { Animatable(0f) }
    LaunchedEffect(animateToEnd) {
        val result = progress.animateTo(
            if (animateToEnd) 1f else 0f,
            animationSpec = tween(5000)
        )
        transitionName = "normal"
        progress.snapTo(0f)
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
        transitionName = transitionName,
        progress = progress.value
    ) {

        Text(
            modifier = Modifier.layoutId(titleId),
            text = transitionName,
            fontSize = 30.sp,
            color = Color.White
        )

    }
}
