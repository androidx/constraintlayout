package com.example.examplescomposemotionlayout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import com.example.examplescomposemotionlayout.ui.theme.ExamplesComposeMotionLayoutTheme
import java.lang.Float

class CollapsingToolbarDsl : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExamplesComposeMotionLayoutTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ToolBarExampleDsl()
                }
            }
        }
    }
}




@OptIn(ExperimentalMotionApi::class)
@Preview(group = "scroll", device = "spec:shape=Normal,width=480,height=800,unit=dp,dpi=440")
@Composable
fun ToolBarExampleDsl() {
    val scroll = rememberScrollState(0)

    var scene = """
      {
        ConstraintSets: {
          start: {
            title: {
              bottom: ['image', 'bottom', 16],                
              start: [ 'image','start', 16],
              },
            image: {
              width: 'parent',
              height: 250,
              top: ['parent', 'top', 0],
              custom: {
                cover: '#000000FF'
              }
            },
            icon: {
              top: ['image', 'top', 16],
              start: [ 'image','start', 16],
              alpha: 0,
            },
          },
          end: {
            title: {
              centerVertically: 'image',
              start: ['icon', 'end', 0],
              scaleX: 0.7,
              scaleY: 0.7,
            },
            image: {
              width: 'parent',
              height: 50,
              top: ['parent', 'top', 0],
              custom: {
                cover: '#FF0000FF'
              }
            },
            icon: {
              top: ['image', 'top', 16],
              start: [ 'image','start', 16],
            },
          },
        },
        Transitions: {
          default: {
            from: 'start',
            to: 'end',
            pathMotionArc: 'startHorizontal',
          },
        },
      }
      """


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.verticalScroll(scroll)
    ) {
        Spacer(Modifier.height(250.dp))
        repeat(5) {
            Text(
                text = LoremIpsum(222).values.first(),
                modifier = Modifier
                    .background(Color.White)
                    .padding(16.dp)
            )
        }
    }

    val progress = Float.min(scroll.value / (3f * (250 - 50)), 1f);

    MotionLayout(
        modifier = Modifier.fillMaxSize(),
        motionScene = MotionScene(content = scene),
        progress = progress
    ) {
        Image(
            modifier = Modifier.layoutId("image"),
            painter = painterResource(R.drawable.bridge),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier
            .layoutId("image")
            .background(motionProperties("image").value.color("cover"))) {
        }
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
