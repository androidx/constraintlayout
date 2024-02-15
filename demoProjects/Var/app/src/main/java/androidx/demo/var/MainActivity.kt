package androidx.demo.`var`

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionLayoutDebugFlags
import androidx.constraintlayout.compose.MotionScene
import androidx.constraintlayout.compose.layoutId
import androidx.demo.`var`.ui.theme.VarTheme
import androidx.demo.lua.ExpressionEngine
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VarTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    DefaultPreview( )
                }
            }
        }
    }
}


@OptIn(ExperimentalMotionApi::class)
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {

    var scene =
        """
       {
       vars: {
             mWidth: '50',
             mHeight: 'mWidth * 9 /16'
       },
         ConstraintSets: {
           start: {
             box1: {
               width: '#mWidth', height:  '#mHeight',
               bottom: ['parent', 'bottom', 10],
               start: ['parent', 'start', 10],
             },
               title: {
                   top: ['parent', 'top', 10],
               start: ['parent', 'start', 10],
                end: ['parent', 'end', 10],
             },
               box2: {
               width: 50, height: 50,
               bottom: ['parent', 'bottom', 10],
               start: ['parent', 'start', 10],
               motion: {
                  pathArc : 'startHorizontal'
               }
             }
           },
           end: {
               box1: {
                       width: 50, height: 50,
                       top: ['parent', 'top', 10],
                       end: ['parent', 'end', 10],
                    },
              title: {
                 top: ['parent', 'top', 10],
               start: ['parent', 'start', 10],
                end: ['parent', 'end', 10],
             },
             box2: {
                width: 50, height: 50,
                top: ['parent', 'top', 40],
                end: ['parent', 'end', 10],
             }
           }
         },
         Transitions: {
           default: {
              from: 'start',
              to: 'end',
               pathMotionArc : 'none',
              onSwipe: {
                anchor: 'box1',
                maxVelocity: 4.2,
                maxAccel: 3,
                direction: 'end',
                side: 'start',
                mode: 'velocity'
              }
           }
         }
       }
        """.trimIndent()
    var exp = ExpressionEngine()
    scene = exp.process(scene)
    Column {
        MotionLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray),
            motionScene = MotionScene(content = scene),
            debug= EnumSet.of(MotionLayoutDebugFlags.SHOW_ALL),
        ) {
            Text(text = "pathArc motion attributes",
                modifier = Modifier.layoutId("title"))
            Box(
                modifier = Modifier
                    .background(Color.Red)
                    .layoutId("box1")
            )
            Box(
                modifier = Modifier
                    .background(Color.Green)
                    .layoutId("box2")
            )
        }
    }
}