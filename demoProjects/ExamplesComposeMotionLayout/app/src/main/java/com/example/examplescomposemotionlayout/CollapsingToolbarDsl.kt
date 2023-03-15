package com.example.examplescomposemotionlayout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import java.lang.Float.min

/**
 * A demo of using MotionLayout as a collapsing Toolbar using the DSL to define the MotionScene.
 *
 * This is based on using
 *
 * ```
 * Column(
 *  horizontalAlignment = Alignment.CenterHorizontally,
 *  modifier = Modifier.verticalScroll(scroll)
 * )
 * ```
 * The Column's modifier  Modifier.verticalScroll(scroll) will modify scroll.value as it scrolls.
 * We can use this value with a little math to calculate the appropriate progress.
 *
 * When the Column is at the start the MotionLayout sits on top of the Spacer. As the user scrolls
 * up the MotionLayout shrinks with the scrolling Spacer then, stops.
 */
@OptIn(ExperimentalMotionApi::class)
@Preview(group = "scroll", device = "spec:shape=Normal,width=480,height=800,unit=dp,dpi=440")
@Composable
fun ToolBarExampleDsl() {
    val scroll = rememberScrollState(0)
    val big = 250.dp
    val small = 50.dp
    val scene = MotionScene {
        val (title, image, icon) = createRefsFor("title", "image", "icon")

        val start1 = constraintSet {
            constrain(title) {
                bottom.linkTo(image.bottom)
                start.linkTo(image.start)
            }
            constrain(image) {
                width = Dimension.matchParent
                height = Dimension.value(big)
                top.linkTo(parent.top)
                customColor("cover", Color(0x000000FF))
            }
            constrain(icon) {
                top.linkTo(image.top, 16.dp)
                start.linkTo(image.start, 16.dp)
                alpha = 0f
            }
        }
        val end1 = constraintSet {
            constrain(title) {
                bottom.linkTo(image.bottom)
                start.linkTo(icon.end)
                centerVerticallyTo(image)
                scaleX = 0.7f
                scaleY = 0.7f
            }
            constrain(image) {
                width = Dimension.matchParent
                height = Dimension.value(small)
                top.linkTo(parent.top)
                customColor("cover", Color(0xFF0000FF))
            }
            constrain(icon) {
                top.linkTo(image.top, 16.dp)
                start.linkTo(image.start, 16.dp)
            }
        }
        transition(start1, end1, "default") {}
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.verticalScroll(scroll)
    ) {
        Spacer(Modifier.height(big))
        repeat(5) {
            Text(
                text = LoremIpsum(222).values.first(),
                modifier = Modifier
                    .background(Color.White)
                    .padding(16.dp)
            )
        }
    }
    val gap = with(LocalDensity.current) { big.toPx() - small.toPx() }
    val progress = minOf(scroll.value / gap, 1f)

    MotionLayout(
        modifier = Modifier.fillMaxSize(),
        motionScene = scene,
        progress = progress
    ) {
        Image(
            modifier = Modifier
                .layoutId("image")
                .background(customColor("image", "cover")),
            painter = painterResource(R.drawable.bridge),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Image(
            modifier = Modifier.layoutId("icon"),
            painter = painterResource(R.drawable.menu),
            contentDescription = null
        )
        Text(
            modifier = Modifier.layoutId("title"),
            text = "San Francisco",
            fontSize = 30.sp,
            color = Color.White
        )
    }
}