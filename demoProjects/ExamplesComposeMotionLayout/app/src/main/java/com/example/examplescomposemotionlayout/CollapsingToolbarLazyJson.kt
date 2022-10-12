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
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene

/**
 * A demo of using MotionLayout as a collapsing Toolbar using JSON to define the MotionScene
 */
@OptIn(ExperimentalMotionApi::class)
@Preview(group = "scroll", device = "spec:shape=Normal,width=480,height=800,unit=dp,dpi=440")
@Composable
fun ToolBarLazyExample() {
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

    val maxPx = with(LocalDensity.current) { 250.dp.roundToPx().toFloat() }
    val minPx = with(LocalDensity.current) { 50.dp.roundToPx().toFloat() }
    val toolbarHeight = remember { mutableStateOf(maxPx) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val height = toolbarHeight.value;

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

    val progress = 1 - (toolbarHeight.value - minPx) / (maxPx - minPx);

    Column {
        MotionLayout(
            modifier = Modifier.background(Color.Green),
            motionScene = MotionScene(content = scene),
            progress = progress
        ) {
            Image(
                modifier = Modifier.layoutId("image"),
                painter = painterResource(R.drawable.bridge),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .layoutId("image")
                    .background(motionProperties("image").value.color("cover"))
            ) {
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
        Box(
            Modifier
                .fillMaxWidth()
                .nestedScroll(nestedScrollConnection)) {
            LazyColumn() {
                items(100) {
                    Text(text = "item $it", modifier = Modifier.padding(4.dp))
                }
            }
        }
    }
}
