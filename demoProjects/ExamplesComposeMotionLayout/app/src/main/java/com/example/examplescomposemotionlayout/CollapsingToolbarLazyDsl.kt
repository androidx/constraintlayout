package com.example.examplescomposemotionlayout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene

/**
 * A demo using MotionLayout as a collapsing Toolbar using the DSL to define the MotionScene, where
 * the scrolling of the LazyColumn is obtained with a NestedScrollConnection.
 *
 * ```
 * LazyColumn(
 *   Modifier
 *     .fillMaxWidth()
 *     .nestedScroll(nestedScrollConnection)
 * ) {
 *   items(100) {
 *     Text(text = "item $it", modifier = Modifier.padding(4.dp))
 *   }
 * }
 * ```
 *
 * A NestedScrollConnection object is passed to a LazyColumn Composable via a modifier
 * (Modifier.nestedScroll(nestedScrollConnection)).
 *
 * When the onPreScroll of the NestedScrollConnection is called It returns the amount of "offset" to
 * absorb and uses the offset to collapse the MotionLayout.
 */
@OptIn(ExperimentalMotionApi::class)
@Preview(group = "scroll", device = "spec:shape=Normal,width=480,height=800,unit=dp,dpi=440")
@Composable
fun ToolBarLazyExampleDsl() {
    val big = 250.dp
    val small = 50.dp
    val scene = MotionScene {
        val title = createRefFor("title")
        val image = createRefFor("image")
        val icon = createRefFor("icon")

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

    val maxPx = with(LocalDensity.current) { big.roundToPx().toFloat() }
    val minPx = with(LocalDensity.current) { small.roundToPx().toFloat() }
    val toolbarHeight = remember { mutableStateOf(maxPx) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val height = toolbarHeight.value

                if (height + available.y > maxPx) {
                    toolbarHeight.value = maxPx
                    return Offset(0f, maxPx - height)
                }

                if (height + available.y < minPx) {
                    toolbarHeight.value = minPx
                    return Offset(0f, minPx - height)
                }

                toolbarHeight.value += available.y
                return Offset(0f, available.y)
            }
        }
    }

    val progress = 1 - (toolbarHeight.value - minPx) / (maxPx - minPx)

    Column {
        MotionLayout(
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
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .nestedScroll(nestedScrollConnection)
        ) {
            items(100) {
                Text(text = "item $it", modifier = Modifier.padding(4.dp))
            }
        }
    }
}
