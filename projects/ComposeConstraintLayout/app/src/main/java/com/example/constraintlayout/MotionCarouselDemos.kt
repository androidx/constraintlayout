/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.constraintlayout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.MotionCarousel
import androidx.constraintlayout.compose.MotionCarouselScope
import androidx.constraintlayout.compose.MotionLayoutScope
import androidx.constraintlayout.compose.MotionScene
import androidx.constraintlayout.compose.items
import androidx.constraintlayout.compose.itemsWithProperties
import androidx.constraintlayout.compose.layoutId
import androidx.core.math.MathUtils

data class CardInfo(val index : Int, val text: String, val color: Color)

var colors = arrayListOf<Color>(Color(255, 100, 200),
    Color.Green, Color.Blue, Color.Cyan, Color.Yellow, Color.Gray,
    Color.Magenta, Color.LightGray)

val cardsExample = arrayListOf<CardInfo>(
    CardInfo(0, "Card 1", colors[0 % colors.size]),
    CardInfo(1, "Card 2", colors[1 % colors.size]),
    CardInfo(2, "Card 3", colors[2 % colors.size]),
    CardInfo(3, "Card 4", colors[3 % colors.size]),
    CardInfo(4, "Card 5", colors[4 % colors.size]),
    CardInfo(5, "Card 6", colors[5 % colors.size]),
    CardInfo(6, "Card 7", colors[6 % colors.size]),
    CardInfo(7, "Card 8", colors[7 % colors.size]),
    CardInfo(8, "Card 9", colors[8 % colors.size]),
)

@Composable
fun MySimpleCarousel(content: MotionCarouselScope.() -> Unit) {
    val startCs = remember {
        """
                    card0: {
                      width: 100,
                      height: 200,
                      end: ['parent','start', 8],
                      centerVertically: 'parent'
                    },
                    card1: {
                      width: 100,
                      height: 200,
                      centerHorizontally: 'parent',
                      centerVertically: 'parent'
                    }, 
                    card2: {
                      width: 100,
                      height: 200,
                      start: ['parent','end', 8],
                      centerVertically: 'parent'
                    }                     
        """
    }
    val forwardCs = remember {
        """
                    card0: {
                      width: 100,
                      height: 200,
                      end: ['card1','start', 8],
                      centerVertically: 'parent'
                    },
                    card1: {
                      width: 100,
                      height: 200,
                      end: ['parent','start', 8],
                      centerVertically: 'parent'
                    }, 
                    card2: {
                      width: 100,
                      height: 200,
                      centerHorizontally: 'parent',
                      centerVertically: 'parent'
                    }                     
        """
    }
    val backwardCs = remember {
        """
                    card0: {
                      width: 100,
                      height: 200,
                      centerHorizontally: 'parent',
                      centerVertically: 'parent'
                    },
                    card1: {
                      width: 100,
                      height: 200,
                      start: ['parent','end', 8],
                      centerVertically: 'parent'
                    }, 
                    card2: {
                      width: 100,
                      height: 200,
                      start: ['card1','end', 8],
                      centerVertically: 'parent'
                    }                     
        """
    }
    val motionScene = MotionScene(content = """{
                ConstraintSets: {
                  start: { $startCs },
                  forward: { $forwardCs },
                  backward: { $backwardCs }
                },
                Transitions: {
                  forward: {
                    from: 'start',
                    to: 'forward',
                  },
                  backward: {
                    from: 'start',
                    to: 'backward',
                  }
                }    
    }""")
    MotionCarousel(motionScene, 1, 3, content = content)
}

@Composable
fun MyCarousel(content: MotionCarouselScope.() -> Unit) {
    val startCs = remember {
        """
                    label: {
                      start: ['parent', 'start', 8],
                      top: ['parent', 'top', 8],
                    },
                    card0: {
                      width: 100,
                      height: 200,
                      end: ['parent','start', 8],
                      centerVertically: 'parent',
                      custom: {
                        mainColor: '#0000FF'
                      }  
                    }, 
                    card1: {
                      width: 100,
                      height: 200,
                      start: ['parent','start', 8],
                      centerVertically: 'parent',
                      custom: {
                        mainColor: '#0000FF'
                      }  
                    },
                    card2: {
                      width: 150,
                      height: 250,
                      centerHorizontally: 'parent',
                      centerVertically: 'parent',
                      rotationX: 5,
                      rotationY: 360,
                      custom: {
                          mainColor: '#FF0000',
                          main: 1.0
                      }
                    },
                    card3: {
                      width: 100,
                      height: 200,
                      end: ['parent','end', 8],
                      centerVertically: 'parent',
                      custom: {
                        mainColor: '#0000FF'
                      }  
                    },
                    card4: {
                      width: 100,
                      height: 200,
                      start: ['parent','end', 8],
                      centerVertically: 'parent',
                      custom: {
                        mainColor: '#0000FF'
                      }  
                    },             
        """

    }
    val forwardCs = remember {"""
                    label: {
                      start: ['parent', 'start', 8],
                      top: ['parent', 'top', 8]
                    },
                    card0: {
                      width: 100,
                      height: 200,
                      end: ['card1','start', 8],
                      centerVertically: 'parent',
                      custom: {
                        mainColor: '#0000FF'
                      }  
                    }, 
                    card1: {
                      width: 100,
                      height: 200,
                      end: ['parent','start', 8],
                      centerVertically: 'parent',
                      custom: {
                        mainColor: '#0000FF'
                      }  
                    },
                    card2: {
                      width: 100,
                      height: 200,
                      start: ['parent','start',8],
                      centerVertically: 'parent',
                      custom: {
                        mainColor: '#0000FF'
                      }  
                    },
                    card3: {
                      width: 150,
                      height: 250,
                      centerHorizontally: 'parent',
                      centerVertically: 'parent',
                      rotationX: 5,
                      //rotationY: 360,
                      custom: {
                          mainColor: '#FF0000',
                          main: 1.0
                      }
                    },
                    card4: {
                      width: 100,
                      height: 200,
                      end: ['parent','end', 8],
                      centerVertically: 'parent',
                      custom: {
                        mainColor: '#0000FF'
                      }  
                    } 
        """}
    val backwardCs = remember {"""
                    label: {
                      start: ['parent', 'start', 8],
                      top: ['parent', 'top', 8]
                    },
                    card0: {
                      width: 100,
                      height: 200,
                      start: ['parent','start', 8],
                      centerVertically: 'parent',
                      custom: {
                        mainColor: '#0000FF'
                      }  
                    }, 
                    card1: {
                      width: 150,
                      height: 250,
                      centerHorizontally: 'parent',
                      centerVertically: 'parent',
                      rotationX: 5,
                      //rotationY: 360,
                      custom: {
                          mainColor: '#FF0000',
                          main: 1.0
                      }
                    },
                    card2: {
                      width: 100,
                      height: 200,
                      end: ['parent','end', 8],
                      centerVertically: 'parent',
                      custom: {
                        mainColor: '#0000FF'
                      }  
                    },
                    card3: {
                      width: 100,
                      height: 200,
                      start: ['parent','end', 8],
                      centerVertically: 'parent',
                      custom: {
                        mainColor: '#0000FF'
                      }  
                    },
                    card4: {
                      width: 100,
                      height: 200,
                      start: ['card3','end', 8],
                      centerVertically: 'parent',
                      custom: {
                        mainColor: '#0000FF'
                      }  
                    },             
        """}
    val motionScene = MotionScene("""{
                Header: {
                  exportAs: 'carousel'
                },
                ConstraintSets: {
                  start: { $startCs },
                  forward: { $forwardCs },
                  backward: { $backwardCs }
                },
                Transitions: {
                  forward: {
                    from: 'start',
                    to: 'forward',
                  },
                  backward: {
                    from: 'start',
                    to: 'backward',
                  }
            }""")
    MotionCarousel(motionScene, 2, 5, content = content, showSlots = false)
}

@Preview
@Composable
fun CarouselDemo1() {
    MySimpleCarousel() {
        items(cardsExample) { card ->
            NewCard(card)
        }
    }
}

@Preview
@Composable
fun CarouselDemo2() {
    MyCarousel() {
        items(cardsExample) { card ->
            NewCard(card)
        }
    }
}

@Preview
@Composable
fun CarouselDemo3() {
    MyCarousel() {
        itemsWithProperties(cardsExample) { card, properties ->
            AnimatedCard(properties, card)
        }
    }
}

@Composable
fun NewCard(item: CardInfo) {
    val i = item.index
    val color = item.color
    val label = item.text
    println("blah NewCard with $item")
    Box(
        modifier = Modifier
            .fillMaxSize()
            .layoutId("card$i", "box")
            .padding(4.dp)
            .background(color)
            .padding(4.dp)
    ) {
        Column(modifier = Modifier
            .width(100.dp)
            .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$label")
        }
    }
}

@Preview(widthDp = 200, heightDp = 400)
@Composable
fun PreviewCard() {
    NewCard2(item = cardsExample[0], false)
}

@Composable
fun NewCard2(item: CardInfo, isMain: Boolean) {
    val i = item.index
    val color = item.color //if (isMain) item.color else Color.DarkGray
    val label = item.text

    val gradient = Brush.verticalGradient(0f to color, 1f to Color.White)
    ConstraintLayout(constraintSet = ConstraintSet("""
    {
      label : { center: 'parent' }
    }    
    """), modifier = Modifier
        .fillMaxSize()
        .clip(RoundedCornerShape(20.dp))
        .background(gradient)
        .layoutId("card$i")) {
        Text(modifier = Modifier.layoutId("label"), text = "$label")
    }
}

@Composable
fun AnimatedCard(properties: State<MotionLayoutScope.MotionProperties>, item: CardInfo) {
    val i = item.index
    val label = item.text

    var v = properties.value.float("main")
    if (v == Float.NaN) {
        v = 0f
    }
    val color = Color(
        MathUtils.clamp(item.color.red * v, 0f, 1f),
        MathUtils.clamp(item.color.green * v, 0f, 1f),
        MathUtils.clamp(item.color.blue * v, 0f, 1f)
    )
    val endColor = properties.value.color("mainColor")
    val gradient = Brush.verticalGradient(0f to endColor,
        0.5f to color, 1f to endColor)
    val textColor = Color(
        MathUtils.clamp( 1 - v, 0f, 1f),
        MathUtils.clamp( 1 - v, 0f, 1f),
        MathUtils.clamp( 1 - v, 0f, 1f)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .layoutId("card$i", "box")
            .padding(4.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(gradient)
            .padding(4.dp)
    ) {
        Column(modifier = Modifier
            .width(100.dp)
            .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(color = textColor, text = "$label")
        }
    }
}