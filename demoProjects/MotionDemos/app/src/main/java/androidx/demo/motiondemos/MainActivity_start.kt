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

class MainActivity_start : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MotionDemosTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    CycleScale( )
                }
            }
        }
    }
}



@Preview(group = "motion8")
@Composable
public fun CycleScale() {
    var animateToEnd by remember { mutableStateOf(false) }

    val progress by animateFloatAsState(
        targetValue = if (animateToEnd) 1f else 0f,
        animationSpec = tween(6000)
    )
    Column {
        MotionLayout(
            modifier = Modifier
                .width(300.dp)
                .height(300.dp)
                .background(Color.White),
            motionScene = MotionScene("""{
                   Debug: {
                  name: 'Cycle30'
                },
                ConstraintSets: {
                  start: {
                    cover: {
                      width: 'spread',
                      height: 'spread',
                      start: ['parent', 'start', 16],
                      bottom: ['parent', 'bottom', 16],
                         end: ['parent', 'end', 16],
                      top: ['parent', 'top', 16]
                    },
                 run: {
                      width: 'spread',
                      height: 'spread',
                      start: ['parent', 'start', 64],
                      bottom: ['parent', 'bottom', 64],
                         end: ['parent', 'end', 64],
                      top: ['parent', 'top', 64]
                    },
                  edge: {
                     width: 'spread',
                      height: 14,
                      start: ['parent', 'start', 16],
                      bottom: ['parent', 'bottom', 16],
                         end: ['parent', 'end', 16],
                        alpha: 0,
                    },
                    },
                  end: {
                    cover: {
                      width: 'spread',
                      height: 'spread',
                      rotationX: -90,
                      pivotX: 0.5,
                      pivotY: 0,
                      end: ['parent', 'end', 16],
                      top: ['parent', 'top', 16],
                      start: ['parent', 'start', 16],
                      bottom: ['parent', 'bottom', 16],
                    },
                     run: {
                      width: 'spread',
                      height: 'spread',
                      start: ['parent', 'start', 64],
                      bottom: ['parent', 'bottom', 64],
                         end: ['parent', 'end', 64],
                      top: ['parent', 'top', 64]
                    },
                  edge: {
                       width: '50%',
                      height: 14,
                      start: ['parent', 'start', 16],
                    
                         end: ['parent', 'end', 16],
                          top: ['parent', 'top', 16],
                          alpha: 0,
                  }
                  }
                },
                Transitions: {
                  default: {
                    from: 'start',
                    to: 'end',
                  onSwipe: {
                  mode: 'spring',
                direction: 'up',
                   anchor: 'edge',
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
            Button(modifier = Modifier
                .layoutId("run"),
                onClick = { /*TODO*/ },
                        shape = RoundedCornerShape(40)
            ) {
                Text(text = "Start\nEngine")

            }
            Box(modifier = Modifier
                .layoutId("cover")
                .clip(RoundedCornerShape(
                bottomEnd = 32.dp, bottomStart = 32.dp))
                .background(Color.Red))

            Box(modifier = Modifier
                .layoutId("edge")
                .background(Color.Green))

        }

        Button(onClick = { animateToEnd = !animateToEnd }) {
            Text(text = "Run")
        }
    }
}
