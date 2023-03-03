package com.example.examplescomposemotionlayout

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.*

@OptIn(ExperimentalMotionApi::class)
@Preview(group = "scroll", device = "spec:shape=Normal,width=480,height=800,unit=dp,dpi=440")
@Composable
fun Puzzle() {
    val grid = 5
    val blocks = grid * grid
    var data by remember {
        val a = Array(blocks) { it }
        mutableStateOf(a)
    }
    var toggle by remember {
        mutableStateOf(true)
    }

    val refId = data.map { "w$it" }
    val set = remember(data) {
        ConstraintSet() {
            val ref = refId.map { createRefFor(it) }.toTypedArray()
            val flow = createFlow(
                elements = ref,
                maxElement = grid,
                wrapMode = Wrap.Aligned,
            )
            constrain(flow) {
                centerTo(parent)

                height = Dimension.ratio("1:1")
            }
            ref.forEach {
                constrain(it) {
                    width = Dimension.percent(1f / grid)
                    height = Dimension.ratio("1:1")
                }
            }
        }
    }

    Column() {
        // Recreate ids for current Array order

        ConstraintLayout(
            set,
            animateChanges = true,
            animationSpec = tween(1000),
            modifier = Modifier
                .background(Color.Red)
                .clickable {
                    data = data.clone()
                    if (toggle) {
                        data.shuffle()
                    } else {
                        data.sort()
                    }
                    toggle = !toggle
                }
        ) {
            val painter = painterResource(id = R.drawable.pepper)
            data.forEachIndexed { i, id ->
                PuzzlePiece(
                    x = i % grid,
                    y = i / grid,
                    grid = grid,
                    painter,
                    modifier = Modifier.layoutId(refId[id])
                )
            }
        }
    }
}

@OptIn(ExperimentalMotionApi::class)
@Preview(group = "scroll", device = "spec:shape=Normal,width=480,height=800,unit=dp,dpi=440")
@Composable
fun MPuzzle() {
    val grid = 5
    val blocks = grid * grid


    val index = remember {
        val b = Array(blocks) { it }
        b.shuffle()
        b
    }
    Column() {

        val refId = Array(blocks) { "W$it" }
        // Recreate ids for current Array order
        val scene = MotionScene() {
            val ordered = refId.map { createRefFor(it) }.toTypedArray()
            val shuffle = index.map { ordered[it] }.toTypedArray()
            val set1 = constraintSet {
                val flow = createFlow(
                    elements = ordered,
                    maxElement = grid,
                    wrapMode = Wrap.Aligned,
                )
                constrain(flow) {
                    centerTo(parent)
                    width = Dimension.ratio("1:1")
                    height = Dimension.ratio("1:1")
                }
                ordered.forEach {
                    constrain(it) {
                        width = Dimension.percent(1f / grid)
                        height = Dimension.ratio("1:1")
                    }
                }
            }
            val set2 = constraintSet {
                val flow = createFlow(
                    elements = shuffle,
                    maxElement = grid,
                    wrapMode = Wrap.Aligned,
                )
                constrain(flow) {
                    centerTo(parent)
                    width = Dimension.ratio("1:1")
                    height = Dimension.ratio("1:1")
                }
                ordered.forEach {
                    constrain(it) {
                        width = Dimension.percent(1f / grid)
                        height = Dimension.ratio("1:1")

                    }
                }
            }
            transition(set1, set2, "default") {
                motionArc = Arc.StartHorizontal
                keyAttributes(*ordered) {
                    frame(40) {
                        // alpha = 0.0f
                        rotationZ = -90f
                        scaleX = 0.1f
                        scaleY = 0.1f
                    }
                    frame(70) {
                        rotationZ = 90f
                        scaleX = 0.1f
                        scaleY = 0.1f
                    }
                }
            }
        }
        var animateToEnd by remember { mutableStateOf(true) }
        val progress = remember { Animatable(0f) }
        LaunchedEffect(animateToEnd) {
            progress.animateTo(
                if (animateToEnd) 1f else 0f,
                animationSpec = tween(800)
            )
        }

        MotionLayout(scene, modifier = Modifier
            .clickable {
                animateToEnd = !animateToEnd
                index.shuffle()
            }
            .background(Color.Red)
            .fillMaxSize(),
            progress = progress.value) {
            val painter = painterResource(id = R.drawable.pepper)
            index.forEachIndexed { i, id ->
                PuzzlePiece(
                    x = i % grid,
                    y = i / grid,
                    grid = grid,
                    painter,
                    modifier = Modifier.layoutId(refId[id])
                )

            }
        }
    }
}

@OptIn(ExperimentalMotionApi::class)
@Composable
fun PuzzlePiece(
    x: Int = 1,
    y: Int = 1,
    grid: Int = 5,
    painter: Painter,
    modifier: Modifier = Modifier.fillMaxSize()
) {

    Canvas(modifier) {
        clipRect {
            withTransform({
                scale(scaleY = grid.toFloat(), scaleX = grid.toFloat())
                translate(
                    left = -(x - grid / 2) * size.width / grid,
                    top = -(y - grid / 2) * size.height / grid
                )
            })
            {
                with(painter) {
                    draw(size)
                }
            }
        }
    }
}