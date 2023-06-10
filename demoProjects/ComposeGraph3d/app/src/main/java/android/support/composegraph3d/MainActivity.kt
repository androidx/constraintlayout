package android.support.composegraph3d

import android.os.Bundle
import android.support.composegraph3d.lib.Graph
import android.support.composegraph3d.ui.theme.ComposeGraph3dTheme
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.isActive

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeGraph3dTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    val w = 600
    val h = 600
    Column(
        modifier = Modifier
            .background(Color.LightGray)
            .fillMaxSize()
    ) {
        Text(text = "Hello $name!")
        Graph3D(
            Modifier.fillMaxWidth().aspectRatio(1f)

                .background(Color.White))
        Text(text = "Hello $name!")
    }
}


@Composable
fun Graph3D(modifier: Modifier) {
    var graph = remember { Graph() }
    val time = remember { mutableStateOf(System.nanoTime()) }

    LaunchedEffect(Unit) {
        while (isActive) {
            withFrameNanos {
                time.value = System.nanoTime()
            }
        }
    }

    var bitmap = graph.getImageForTime(time.value)

    Canvas(modifier = modifier
        .onPlaced {
            graph.setSize(it.size.width, it.size.width)
        }
        .pointerInput(Unit) {
            detectDragGestures(
                onDragStart = {
                    graph.dragStart(it)
                },
                onDragEnd = {
                    graph.dragStopped()
                },
                onDragCancel = {
                    graph.dragStopped()
                },
                onDrag = { change, dragAmount ->
                    graph.drag(change, dragAmount)
                }
            )
        }) {
        scale(2.0f, pivot = Offset(0f,0f)) {
            drawImage(bitmap)
        }
    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeGraph3dTheme {
        Greeting("Android")
    }
}