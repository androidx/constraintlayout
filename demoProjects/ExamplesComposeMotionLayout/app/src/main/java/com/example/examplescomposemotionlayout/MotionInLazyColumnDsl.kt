package com.example.examplescomposemotionlayout

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene

/**
 * A demo of using MotionLayout in a Lazy Column written using DSL Syntax
 */
@OptIn(ExperimentalMotionApi::class)
@Preview(group = "scroll", device = "spec:shape=Normal,width=480,height=800,unit=dp,dpi=440")
@Composable
fun MotionInLazyColumnDsl() {

    var scene = MotionScene() {
        val title = createRefFor("title")
        val image = createRefFor("image")
        val icon = createRefFor("icon")

        val start1 = constraintSet {
            constrain(title) {
                centerVerticallyTo(icon)
                start.linkTo(icon.end, 16.dp)
            }
            constrain(image) {
                width = Dimension.value(40.dp)
                height = Dimension.value(40.dp)
                centerVerticallyTo(icon)
                end.linkTo(parent.end, 8.dp)
            }
            constrain(icon) {
                top.linkTo(parent.top, 16.dp)
                bottom.linkTo(parent.bottom, 16.dp)
                start.linkTo(parent.start, 16.dp)
            }
        }

        val end1 = constraintSet {
            constrain(title) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                scaleX = 0.7f
                scaleY = 0.7f
            }
            constrain(image) {
                width = Dimension.matchParent
                height = Dimension.value(200.dp)
                centerVerticallyTo(parent)
            }
            constrain(icon) {
                top.linkTo(parent.top, 16.dp)
                start.linkTo(parent.start, 16.dp)
            }
        }
        transition("default", start1, end1) {}
    }

    val model = remember { BooleanArray(100) }

    LazyColumn() {
        items(100) {
            // Text(text = "item $it", modifier = Modifier.padding(4.dp))
            Box(modifier = Modifier.padding(3.dp)) {
                var animateToEnd by remember { mutableStateOf(model[it]) }
                val progress = remember { Animatable(if (model[it]) 1f else 0f) }
                LaunchedEffect(animateToEnd) {
                    progress.animateTo(
                        if (animateToEnd) 1f else 0f,
                        animationSpec = tween(700)
                    )
                }
                MotionLayout(
                    modifier = Modifier
                        .background(Color(0xFF331B1B))
                        .fillMaxWidth()
                        .padding(1.dp),
                    motionScene = scene,
                    progress = progress.value
                ) {
                    Image(
                        modifier = Modifier.layoutId("image"),
                        painter = painterResource(R.drawable.bridge),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                    Image(
                        modifier = Modifier
                            .layoutId("icon")
                            .clickable {
                                animateToEnd = !animateToEnd
                                model[it] = animateToEnd;
                            },
                        painter = painterResource(R.drawable.menu),
                        contentDescription = null
                    )

                    Text(
                        modifier = Modifier.layoutId("title"),
                        text = "San Francisco $it",
                        fontSize = 30.sp,
                        color = Color.White
                    )
                }
            }
        }
    }

}
