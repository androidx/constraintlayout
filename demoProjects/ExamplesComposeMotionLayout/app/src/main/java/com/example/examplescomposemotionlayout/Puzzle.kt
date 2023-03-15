package com.example.examplescomposemotionlayout

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.*

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

    val refId = remember(data) { data.map { "w$it" } }
    val set = remember(data) {
        ConstraintSet {
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
                gridSize = grid,
                painter = painter,
                modifier = Modifier.layoutId(refId[id])
            )
        }
    }
}

/**
 * Shows how to animate moving pieces of a puzzle using MotionLayout.
 *
 * &nbsp;
 *
 * The [PuzzlePiece]s are laid out using the [ConstraintLayoutBaseScope.createFlow] helper.
 *
 * And the animation is achieved by creating two ConstraintSets. One providing ordered IDs to Flow,
 * and the other providing a shuffled list of the same IDs.
 *
 * @see PuzzlePiece
 */
@OptIn(ExperimentalMotionApi::class)
@Preview
@Composable
fun MPuzzle() {
    val grid = 5
    val blocks = grid * grid

    var animateToEnd by remember { mutableStateOf(true) }

    val index = remember { Array(blocks) { it }.apply { shuffle() } }
    val refId = remember { Array(blocks) { "W$it" } }

    // Recreate scene when order changes (which is driven by toggling `animateToEnd`)
    val scene = remember(animateToEnd) {
        MotionScene {
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
    }

    val progress by animateFloatAsState(
        targetValue = if (animateToEnd) 1f else 0f,
        animationSpec = tween(800)
    )

    MotionLayout(
        motionScene = scene,
        modifier = Modifier
            .clickable {
                animateToEnd = !animateToEnd
                index.shuffle()
            }
            .background(Color.Red)
            .fillMaxSize(),
        progress = progress
    ) {
        val painter = painterResource(id = R.drawable.pepper)
        index.forEachIndexed { i, id ->
            PuzzlePiece(
                x = i % grid,
                y = i / grid,
                gridSize = grid,
                painter = painter,
                modifier = Modifier.layoutId(refId[id])
            )
        }
    }
}

/**
 * Composable that displays a fragment of the given surface (provided through [painter]) based on
 * the given position ([x], [y]) of a square grid of size [gridSize].
 */
@Composable
fun PuzzlePiece(
    x: Int,
    y: Int,
    gridSize: Int,
    painter: Painter,
    modifier: Modifier = Modifier
) {
    Canvas(modifier.fillMaxSize()) {
        clipRect {
            translate(
                left = -x * size.width,
                top = -y * size.height
            ) {
                with(painter) {
                    draw(size.times(gridSize.toFloat()))
                }
            }
        }
    }
}