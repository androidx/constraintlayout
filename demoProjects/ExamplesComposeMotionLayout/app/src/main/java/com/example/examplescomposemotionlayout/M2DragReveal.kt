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
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.*
import kotlin.math.abs

// A simple fly in effect
@SuppressLint("Range")
@OptIn(ExperimentalMotionApi::class)
@Preview(group = "motion101")
@Composable
fun M1DragReveal() {
    val imageId = "image"
    val titleId = "title"
    val wordsId = "words"

    var scene = MotionScene() {
        val imageRef = createRefFor(imageId)
        val titleRef = createRefFor(titleId)
        val wordsRef = createRefFor(wordsId)


        val start1 = constraintSet {
            constrain(imageRef) {
                width = Dimension.fillToConstraints
                height = Dimension.value(400.dp)
                alpha = 1f
                centerHorizontallyTo(parent)
                top.linkTo(parent.top)
                customFloat("sat", 1f)
                customFloat("bright", 1f)
            }
            constrain(titleRef) {
                width =  Dimension.wrapContent
                height =  Dimension.wrapContent
                centerHorizontallyTo(parent)
                top.linkTo(imageRef.bottom,2.dp)
            }
            constrain(wordsRef) {
                width =  Dimension.fillToConstraints
                height =  Dimension.wrapContent
                centerHorizontallyTo(parent)
                top.linkTo(titleRef.bottom)
            }
        }

        val end1 = constraintSet {
            constrain(imageRef) {
                width = Dimension.value(80.dp)
                height =Dimension.value(80.dp)
                centerHorizontallyTo(parent,0f)
                top.linkTo(parent.top)
                customFloat("sat", 0.8f)
                customFloat("bright", 0.8f)
            }
            constrain(titleRef) {
                width =  Dimension.wrapContent
                height =  Dimension.wrapContent
                centerHorizontallyTo(parent)
                top.linkTo(imageRef.top)
                bottom.linkTo(imageRef.bottom)
            }
            constrain(wordsRef) {
                width =  Dimension.fillToConstraints
                height =  Dimension.wrapContent
                centerHorizontallyTo(parent)
                top.linkTo(imageRef.bottom)
            }
        }
        transition(  start1,end1,"default") {

             onSwipe =  OnSwipe(
                side = SwipeSide.Top,
                direction = SwipeDirection.Up,
                anchor = wordsRef,
                 mode = SwipeMode.Spring(damping = 40f),
            )
            keyPositions(titleRef){
                frame(40){
                    percentY = 0.3f
                    type = RelativePosition.Path
                }
            }
            keyAttributes(imageRef) {
                frame(70) {
                    customFloat("sat", 0f)
                    customFloat("bright", 1.6f)

                }
            }

        }
    }
    val painter = painterResource(id = R.drawable.pepper)


    MotionLayout(
        modifier = Modifier
            .background(Color(0xFF221010))
            .fillMaxSize()
            .padding(1.dp),
        motionScene = scene,

    ) {
        MotionImage(
            painter = painter,
            modifier = Modifier.layoutId(imageId),
            brightness = customFloat(imageId, "bright"),
            saturation = customFloat(imageId, "sat"),
        )
        Text(
            modifier = Modifier.layoutId(titleId),
            text = "Pepper",
            fontSize = 30.sp,
            color = Color.White
        )
        Box(
            modifier = Modifier
                .layoutId(wordsId) .clip(RoundedCornerShape(20.dp))
        ) {
            Text(
                text = LoremIpsum(222).values.first(),
                modifier = Modifier
                    .background(Color.White).padding(8.dp)
                    .layoutId("title"),
            )
        }
    }
}
