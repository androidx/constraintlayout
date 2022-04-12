package androidx.demo.composedemos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionLayoutDebugFlags
import androidx.constraintlayout.compose.MotionScene
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.demo.composedemos.ui.theme.ComposeDemosTheme
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeDemosTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Login("Android")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeDemosTheme {
        Login("Android")
    }
}

@OptIn(ExperimentalMotionApi::class)
@Preview(group = "motion8")
@Composable
public fun  Login(name: String) {
    var animateToEnd by remember { mutableStateOf(false) }

    val progress by animateFloatAsState(
        targetValue = if (animateToEnd) 1f else 0f,
        animationSpec = tween(6000)
    )
    Column {
        MotionLayout(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .background(Color.White),
            motionScene = MotionScene("""{
                ConstraintSets: {
                  start: {
                
                    name: {
                      width: 'spread',
                      start: ['parent', 'start', 56],
                       end: ['parent', 'end', 56],
                      bottom: ['parent', 'bottom', 56]
                    },
                   password: {
                      width: {value:'spread', max:100},
                      hBias: 0,
                      //rotationZ: 390,
                       start: ['name', 'start', 0],
                        end: ['parent', 'end', 56],
                      bottom: ['parent', 'bottom', 16]
                    }
                  },
                  end: {
                    name: {
                      width: 'spread',
                   
                      //rotationZ: 390,
                      end: ['parent', 'end', 56],
                      top: ['parent', 'top', 56]
                    },
                  password: {
                     
                      //rotationZ: 390,
                      end: ['parent', 'end', 16],
                      top: ['parent', 'top', 16]
                    }
                  }
                },
                Transitions: {
                  default: {
                    from: 'start',
                    to: 'end',
                    pathMotionArc: 'startHorizontal',
                    KeyFrames: {
                      KeyAttributes: [
                        {
                          target: ['name'],
                          frames: [25, 50],
                          scaleX: 3,
                          scaleY: .3
                        }
                      ]
                    }
                  }
                }
            }"""),
            debug = EnumSet.of(MotionLayoutDebugFlags.SHOW_ALL),
            progress = progress) {
            var name by remember { mutableStateOf(TextFieldValue("Name ")) }
            var password by remember { mutableStateOf(TextFieldValue("Password")) }

             BasicTextField(modifier = Modifier.layoutId("name").background(Color.Gray),
                 value = name,
                 onValueChange = {   name = it })

            BasicTextField(modifier = Modifier.layoutId("password").background(Color.Gray),
                value = password,
                onValueChange = {  password = it  })

        }

        Button(onClick = { animateToEnd = !animateToEnd }) {
            Text(text = "Run")
        }
    }
}
