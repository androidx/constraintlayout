@file:OptIn(ExperimentalMotionApi::class)

package androidx.demo.motiondemos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionLayoutDebugFlags
import androidx.constraintlayout.compose.MotionScene
import androidx.demo.motiondemos.ui.theme.MotionDemosTheme
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MotionDemosTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Carsoul( )
                }
            }
        }
    }
}



@Preview(group = "motion8")
@Composable
public fun Carsoul() {
    var animateToEnd by remember { mutableStateOf(false) }
    var offset by remember { mutableStateOf(0.1f) }

    val progress by animateFloatAsState(
        targetValue = if (animateToEnd) 1f else 0f,
        animationSpec = tween(6000)
    )
    Column {
        MotionLayout(

            modifier = Modifier.clipToBounds()
                .width(300.dp)
                .height(300.dp)
                .background(Color.White),
            motionScene = MotionScene("""{
                   Debug: {
                  name: 'Cycle30'
                },
                ConstraintSets: {
                  onLeft: {
                    c1: {
                      width: 50,
                      height: 50,
                      start: ['parent', 'start', 16],
                      bottom: ['parent', 'bottom', 16],
                         end: ['parent', 'end', 16],
                      top: ['parent', 'top', 16],
                      hBias : -0.50,
                      custom: {
                         pos: 0.0
                      }
                    },
                     c2: {
                      width: 50,
                      height: 50,
                      start: ['parent', 'start', 16],
                      bottom: ['parent', 'bottom', 16],
                         end: ['parent', 'end', 16],
                      top: ['parent', 'top', 16],
                      hBias : 0.0,
                    },
                     c3: {
                      width: 50,
                      height: 50,
                      start: ['parent', 'start', 16],
                      bottom: ['parent', 'bottom', 16],
                         end: ['parent', 'end', 16],
                      top: ['parent', 'top', 16],
                      hBias : 0.50,
                    },
                   c4: {
                      width: 50,
                      height: 50,
                      start: ['parent', 'start', 16],
                      bottom: ['parent', 'bottom', 16],
                         end: ['parent', 'end', 16],
                      top: ['parent', 'top', 16],
                      hBias : 1,
                    },
  
                    },
                  toRight: {
                    c1: {
                      width: 50,
                      height: 50,
                      start: ['parent', 'start', 16],
                      bottom: ['parent', 'bottom', 16],
                         end: ['parent', 'end', 16],
                      top: ['parent', 'top', 16],
                      hBias : 0,
                      custom: {
                         pos: 0.0
                      }
                    },
                     c2: {
                      width: 50,
                      height: 50,
                      start: ['parent', 'start', 16],
                      bottom: ['parent', 'bottom', 16],
                         end: ['parent', 'end', 16],
                      top: ['parent', 'top', 16],
                      hBias : 0.50,
                    },
                     c3: {
                      width: 50,
                      height: 50,
                      start: ['parent', 'start', 16],
                      bottom: ['parent', 'bottom', 16],
                         end: ['parent', 'end', 16],
                      top: ['parent', 'top', 16],
                      hBias : 1,
                    },
                   c4: {
                      width: 50,
                      height: 50,
                      start: ['parent', 'start', 16],
                      bottom: ['parent', 'bottom', 16],
                         end: ['parent', 'end', 16],
                      top: ['parent', 'top', 16],
                      hBias : 1.50,
                    },
                 }
                },
                Transitions: {
                  default: {
                    from: 'start',
                    to: 'end',
                  onSwipe: {
                  mode: 'spring',
                direction: 'right',
                   anchor: 'c3',
                side: 'top',
                springBoundary: 'down',
                springStiffness: 800,
                springDamping: 32
                  },
                  
                  }
                }
            }"""),
            debug = EnumSet.of(MotionLayoutDebugFlags.SHOW_ALL),
            progress = progress) {
            val f =  motionProperties("c1").value.float("pos")

            for (i in 1..4) {
                Button(modifier = Modifier
                    .layoutId("c$i"),
                    onClick = { /*TODO*/ },
                    shape = RoundedCornerShape(40)
                ) {
                    Text(text = "$i")

                }

            }


        }

        Button(onClick = { animateToEnd = !animateToEnd }) {
            Text(text = "Run")
        }
    }
}
