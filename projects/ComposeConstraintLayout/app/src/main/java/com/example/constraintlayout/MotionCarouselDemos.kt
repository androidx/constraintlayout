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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.MotionCarousel
import androidx.constraintlayout.compose.MotionCarouselScope
import androidx.constraintlayout.compose.MotionLayoutScope
import androidx.constraintlayout.compose.MotionScene
import androidx.constraintlayout.compose.items
import androidx.constraintlayout.compose.itemsWithProperties
import androidx.constraintlayout.compose.layoutId
import androidx.core.math.MathUtils

/**
 * This file contains several MotionCarousel demos, walking you through simple ones
 * to more complex ones, using JSON or DSL MotionScene and advanced animations/transitions.
 *
 * It's recommended to go through the examples in order to get a good overview of MotionCarousel.
 *
 * CarouselDemo1: basic self-contained carousel with 3 slots (in JSON)
 * CarouselDemo2: expanding on demo1, adding a transition and some keyframes
 * CarouselDemo3: factorizing MotionCarousel of Demo1 & 2 in a reusable MySimpleCarousel composable
 * CarouselDemo4: rewriting Demo3 using a MotionScene DSL instead of the JSON description
 * CarouselDemo5: simple refactoring of the DSL from Demo4 using derived constraintsets
 * CarouselDemo6: implementing a more complex carousel with 5 slots in both JSON and DSL
 * CarouselDemo7: using Demo6 carousel but with animated cards in the carousel
 */
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

/**
 * Helper function
 */
inline val Dp.asDimension : Dimension
    get() = Dimension.value(this)

/**
 * Simple Carousel demo
 * 
 * We are using a motionScene with 3 states positioning 3 slots,
 * slot0, slot1 and slot2, with the "initial" slot being slot1 positioned
 * in the center of the screen, slot0 on its left, slot2 on its right.
 * As slot1 is the "initial" slot, initialSlotIndex is 1 and numSlots is 3.
 *
 * ```
 * next        [slot0] [slot1] | [slot2] |
 * start               [slot0] | [slot1] | [slot2]
 * previous                    | [slot0] | [slot1] [slot2]
 * ```
 *
 * We are using the above data (cardsExample) that we pass to the
 * items() function, which leads to the item passed to be a CardInfo.
 *
 * We then use the CardInfo to display a colored card.
 */
@Preview
@Composable
fun CarouselDemo1() {
    val motionScene = MotionScene(content = """
{
  ConstraintSets: {
    start: {
      slot0: {
        width: 200,
        height: 400,
        end: [ 'parent', 'start', 8 ],
        centerVertically: 'parent'
      },
      slot1: {
        width: 200,
        height: 400,
        center: 'parent'
      },
      slot2: {
        width: 200,
        height: 400,
        start: [ 'parent', 'end', 8 ],
        centerVertically: 'parent'
      }
    },
    next: {
      slot0: {
        width: 200,
        height: 400,
        end: [ 'slot1', 'start', 8 ],
        centerVertically: 'parent'
      },
      slot1: {
        width: 200,
        height: 400,
        end: [ 'parent', 'start', 8 ],
        centerVertically: 'parent'
      },
      slot2: {
        width: 200,
        height: 400,
        center: 'parent'
      }
    },
    previous: {
      slot0: {
        width: 200,
        height: 400,
        center: 'parent'
      },
      slot1: {
        width: 200,
        height: 400,
        start: [ 'parent', 'end', 8 ],
        centerVertically: 'parent'
      },
      slot2: {
        width: 200,
        height: 400,
        start: [ 'slot1', 'end', 8 ],
        centerVertically: 'parent'
      }
    }
  },
  Transitions: {
    forward: {
      from: 'start',
      to: 'next',
    },
    backward: {
      from: 'start',
      to: 'previous',
    }
  }
}        
""")
    MotionCarousel(motionScene, 1, 3) {
        items(cardsExample) { card ->
            val color = card.color
            val label = card.text
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .background(color)
                    .padding(4.dp)
            ) {
                Column(modifier = Modifier
                    .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$label")
                }
            }
        }
    }
}

/**
 * Simple Carousel demo, take 2
 *
 * Based on the previous demo, we simply add a transition defining
 * a couple of keyframes applying a scale factor on the slots. We could
 * create any kind of keyframes here to make the transition more interesting.
 */
@Preview
@Composable
fun CarouselDemo2() {
    val motionScene = MotionScene(content = """
{
  ConstraintSets: {
    start: {
      slot0: {
        width: 200,
        height: 400,
        end: [ 'parent', 'start', 8 ],
        centerVertically: 'parent'
      },
      slot1: {
        width: 200,
        height: 400,
        center: 'parent'
      },
      slot2: {
        width: 200,
        height: 400,
        start: [ 'parent', 'end', 8 ],
        centerVertically: 'parent'
      }
    },
    next: {
      slot0: {
        width: 200,
        height: 400,
        end: [ 'slot1', 'start', 8 ],
        centerVertically: 'parent'
      },
      slot1: {
        width: 200,
        height: 400,
        end: [ 'parent', 'start', 8 ],
        centerVertically: 'parent'
      },
      slot2: {
        width: 200,
        height: 400,
        center: 'parent'
      }
    },
    previous: {
      slot0: {
        width: 200,
        height: 400,
        center: 'parent'
      },
      slot1: {
        width: 200,
        height: 400,
        start: [ 'parent', 'end', 8 ],
        centerVertically: 'parent'
      },
      slot2: {
        width: 200,
        height: 400,
        start: [ 'slot1', 'end', 8 ],
        centerVertically: 'parent'
      }
    }
  },
  Transitions: {
    forward: {
      from: 'start',
      to: 'next',
      KeyFrames: {
        KeyAttributes: [
          {
            target: ['slot0', 'slot1', 'slot2'],
            frames: [50],
            scaleX: .3,
            scaleY: .3
          }
        ]
      }
    },
    backward: {
      from: 'start',
      to: 'previous',
      KeyFrames: {
        KeyAttributes: [
          {
            target: ['slot0', 'slot1', 'slot2'],
            frames: [50],
            scaleX: .3,
            scaleY: .3
          }
        ]
      }
    }
  }
}        
""")
    MotionCarousel(motionScene, 1, 3) {
        items(cardsExample) { card ->
            val i = card.index
            val color = card.color
            val label = card.text
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .background(color)
                    .padding(4.dp)
            ) {
                Column(modifier = Modifier
                    .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$label")
                }
            }        }
    }
}

/**
 * The previous examples were all self-contained, but in practice
 * you likely will want to encapsulate your Carousel in its own
 * composable, to be able to reuse it more easily.
 *
 * We do that here by defining a MySimpleCarousel function that takes
 * a MotionCarouselScope, and reusing the same motionScene
 * as in CarouselDemo2.
 */
@Composable
fun MySimpleCarousel(content: MotionCarouselScope.() -> Unit) {
    val motionScene = MotionScene(content = """
{
  ConstraintSets: {
    start: {
      slot0: {
        width: 200,
        height: 400,
        end: [ 'parent', 'start', 8 ],
        centerVertically: 'parent'
      },
      slot1: {
        width: 200,
        height: 400,
        center: 'parent'
      },
      slot2: {
        width: 200,
        height: 400,
        start: [ 'parent', 'end', 8 ],
        centerVertically: 'parent'
      }
    },
    next: {
      slot0: {
        width: 200,
        height: 400,
        end: [ 'slot1', 'start', 8 ],
        centerVertically: 'parent'
      },
      slot1: {
        width: 200,
        height: 400,
        end: [ 'parent', 'start', 8 ],
        centerVertically: 'parent'
      },
      slot2: {
        width: 200,
        height: 400,
        center: 'parent'
      }
    },
    previous: {
      slot0: {
        width: 200,
        height: 400,
        center: 'parent'
      },
      slot1: {
        width: 200,
        height: 400,
        start: [ 'parent', 'end', 8 ],
        centerVertically: 'parent'
      },
      slot2: {
        width: 200,
        height: 400,
        start: [ 'slot1', 'end', 8 ],
        centerVertically: 'parent'
      }
    }
  },
  Transitions: {
    forward: {
      from: 'start',
      to: 'next',
      KeyFrames: {
        KeyAttributes: [
          {
            target: ['slot0', 'slot1', 'slot2'],
            frames: [50],
            scaleX: .3,
            scaleY: .3
          }
        ]
      }
    },
    backward: {
      from: 'start',
      to: 'previous',
      KeyFrames: {
        KeyAttributes: [
          {
            target: ['slot0', 'slot1', 'slot2'],
            frames: [50],
            scaleX: .3,
            scaleY: .3
          }
        ]
      }
    }
  }
}        
""")

    MotionCarousel(motionScene, 1, 3, content = content)
}

/**
 * Similarly, we can extract the content that we put in the Carousel
 * slots to its own composable, MyCard
 */
@Composable
fun MyCard(item: CardInfo) {
    val i = item.index
    val color = item.color
    val label = item.text
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
            .background(color)
            .padding(4.dp)
    ) {
        Column(modifier = Modifier
            .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$label")
        }
    }
}

/**
 * We now can rewrite CarouselDemo2 by using MySimpleCarousel instead, referencing MyCard,
 * resulting in a very readable usage.
 */
@Preview
@Composable
fun CarouselDemo3() {
    MySimpleCarousel() {
        items(cardsExample) { card ->
            MyCard(card)
        }
    }
}

/**
 * Of course, we can use the new MotionLayout DSL instead of JSON to write the Carousel :)
 * Let's rewrite MySimpleCarousel with the DSL.
 */
@Composable
fun MySimpleCarouselDSL(content: MotionCarouselScope.() -> Unit) {

    val motionScene = MotionScene {
        val slot0 = createRefFor("slot0")
        val slot1 = createRefFor("slot1")
        val slot2 = createRefFor("slot2")
        val startState = constraintSet("start") {
            constrain(slot0) {
                width = 200.dp.asDimension
                height = 400.dp.asDimension
                end.linkTo(parent.start, 8.dp)
                centerVerticallyTo(parent)
            }
            constrain(slot1) {
                width = 200.dp.asDimension
                height = 400.dp.asDimension
                centerTo(parent)
            }
            constrain(slot2) {
                width = 200.dp.asDimension
                height = 400.dp.asDimension
                start.linkTo(parent.end, 8.dp)
                centerVerticallyTo(parent)
            }
        }
        val nextState = constraintSet("next") {
            constrain(slot0) {
                width = 200.dp.asDimension
                height = 400.dp.asDimension
                end.linkTo(slot1.start, 8.dp)
                centerVerticallyTo(parent)
            }
            constrain(slot1) {
                width = 200.dp.asDimension
                height = 400.dp.asDimension
                end.linkTo(parent.start, 8.dp)
                centerVerticallyTo(parent)
            }
            constrain(slot2) {
                width = 200.dp.asDimension
                height = 400.dp.asDimension
                centerTo(parent)
            }
        }
        val previousState = constraintSet("previous") {
            constrain(slot0) {
                width = 200.dp.asDimension
                height = 400.dp.asDimension
                centerTo(parent)
            }
            constrain(slot1) {
                width = 200.dp.asDimension
                height = 400.dp.asDimension
                start.linkTo(parent.end, 8.dp)
                centerVerticallyTo(parent)
            }
            constrain(slot2) {
                width = 200.dp.asDimension
                height = 400.dp.asDimension
                start.linkTo(slot1.end, 8.dp)
                centerVerticallyTo(parent)
            }
        }
        transition("forward", startState, nextState) {
            keyAttributes(slot0, slot1, slot2) {
                frame(50f) {
                    scaleX = .3f
                    scaleY = .3f
                }
            }
        }
        transition("backward", startState, previousState) {
            keyAttributes(slot0, slot1, slot2) {
                frame(50f) {
                    scaleX = .3f
                    scaleY = .3f
                }
            }
        }
    }
    MotionCarousel(motionScene, 1, 3, content = content)
}

/**
 * As you can see, we can easily change CarouselDemo3 to reference MySimpleCarouselDSL instead
 * for a similar result.
 */
@Preview
@Composable
fun CarouselDemo4() {
    MySimpleCarouselDSL() {
        items(cardsExample) { card ->
            MyCard(card)
        }
    }
}

/**
 * Note, we can also refactor the DSL a little bit to share common constraints,
 * by creating a base state and having the other constraintsets be derived from it
 * (This trick also works in JSON, incidentally)
 */
@Composable
fun MySimpleCarouselDSL2(content: MotionCarouselScope.() -> Unit) {

    val motionScene = MotionScene {
        val slot0 = createRefFor("slot0")
        val slot1 = createRefFor("slot1")
        val slot2 = createRefFor("slot2")
        val baseState = constraintSet("base") {
            constrain(slot0) {
                width = 200.dp.asDimension
                height = 400.dp.asDimension
            }
            constrain(slot1) {
                width = 200.dp.asDimension
                height = 400.dp.asDimension
            }
            constrain(slot2) {
                width = 200.dp.asDimension
                height = 400.dp.asDimension
            }
        }
        val startState = constraintSet("start", baseState) {
            constrain(slot0) {
                end.linkTo(parent.start, 8.dp)
                centerVerticallyTo(parent)
            }
            constrain(slot1) {
                centerTo(parent)
            }
            constrain(slot2) {
                start.linkTo(parent.end, 8.dp)
                centerVerticallyTo(parent)
            }
        }
        val nextState = constraintSet("next", baseState) {
            constrain(slot0) {
                end.linkTo(slot1.start, 8.dp)
                centerVerticallyTo(parent)
            }
            constrain(slot1) {
                end.linkTo(parent.start, 8.dp)
                centerVerticallyTo(parent)
            }
            constrain(slot2) {
                centerTo(parent)
            }
        }
        val previousState = constraintSet("previous", baseState) {
            constrain(slot0) {
                centerTo(parent)
            }
            constrain(slot1) {
                start.linkTo(parent.end, 8.dp)
                centerVerticallyTo(parent)
            }
            constrain(slot2) {
                start.linkTo(slot1.end, 8.dp)
                centerVerticallyTo(parent)
            }
        }
        transition("forward", startState, nextState) {
            keyAttributes(slot0, slot1, slot2) {
                frame(50f) {
                    scaleX = .3f
                    scaleY = .3f
                }
            }
        }
        transition("backward", startState, previousState) {
            keyAttributes(slot0, slot1, slot2) {
                frame(50f) {
                    scaleX = .3f
                    scaleY = .3f
                }
            }
        }
    }
    MotionCarousel(motionScene, 1, 3, content = content)
}

/**
 * As you can see, we can easily change CarouselDemo3 to reference MySimpleCarouselDSL instead
 * for a similar result.
 */
@Preview
@Composable
fun CarouselDemo5() {
    MySimpleCarouselDSL2() {
        items(cardsExample) { card ->
            MyCard(card)
        }
    }
}

/**
 * Let's do another example with a more complex MotionScene using 5 slots,
 * encapsulated in its own MyCarouselDSL function as well
 */
@Composable
fun MyCarouselDSL(content: MotionCarouselScope.() -> Unit) {
    val motionScene = MotionScene {
        val slot0 = createRefFor("slot0")
        val slot1 = createRefFor("slot1")
        val slot2 = createRefFor("slot2")
        val slot3 = createRefFor("slot3")
        val slot4 = createRefFor("slot4")

        val startState = constraintSet("start") {
            constrain(slot0) {
                width = 100.dp.asDimension
                height = 200.dp.asDimension
                end.linkTo(parent.start, 8.dp)
                centerVerticallyTo(parent)
                customColor("mainColor", Color.Blue)
            }
            constrain(slot1) {
                width = 100.dp.asDimension
                height = 200.dp.asDimension
                start.linkTo(parent.start, 8.dp)
                centerVerticallyTo(parent)
                customColor("mainColor", Color.Blue)
            }
            constrain(slot2) {
                width = 150.dp.asDimension
                height = 250.dp.asDimension
                centerTo(parent)
                rotationX = 5f
                rotationY = 360f
                customColor("mainColor", Color.Red)
                customFloat("main", 1.0f)
            }
            constrain(slot3) {
                width = 100.dp.asDimension
                height = 200.dp.asDimension
                end.linkTo(parent.end, 8.dp)
                centerVerticallyTo(parent)
                customColor("mainColor", Color.Blue)
            }
            constrain(slot4) {
                width = 100.dp.asDimension
                height = 200.dp.asDimension
                start.linkTo(parent.end, 8.dp)
                centerVerticallyTo(parent)
                customColor("mainColor", Color.Blue)
            }
        }
        val nextState = constraintSet("next") {
            constrain(slot0) {
                width = 100.dp.asDimension
                height = 200.dp.asDimension
                end.linkTo(slot1.start, 8.dp)
                centerVerticallyTo(parent)
                customColor("mainColor", Color.Blue)
            }
            constrain(slot1) {
                width = 100.dp.asDimension
                height = 200.dp.asDimension
                end.linkTo(parent.start, 8.dp)
                centerVerticallyTo(parent)
                customColor("mainColor", Color.Blue)
            }
            constrain(slot2) {
                width = 100.dp.asDimension
                height = 200.dp.asDimension
                start.linkTo(parent.start, 8.dp)
                centerVerticallyTo(parent)
                customColor("mainColor", Color.Blue)
            }
            constrain(slot3) {
                width = 150.dp.asDimension
                height = 250.dp.asDimension
                centerTo(parent)
                rotationX = 5f
                customColor("mainColor", Color.Red)
                customFloat("main", 1.0f)
            }
            constrain(slot4) {
                width = 100.dp.asDimension
                height = 200.dp.asDimension
                end.linkTo(parent.end, 8.dp)
                centerVerticallyTo(parent)
                customColor("mainColor", Color.Blue)
            }
        }
        val previousState = constraintSet("previous") {
            constrain(slot0) {
                width = 100.dp.asDimension
                height = 200.dp.asDimension
                start.linkTo(parent.start, 8.dp)
                centerVerticallyTo(parent)
                customColor("mainColor", Color.Blue)
            }
            constrain(slot1) {
                width = 150.dp.asDimension
                height = 250.dp.asDimension
                centerTo(parent)
                rotationX = 5f
                customColor("mainColor", Color.Red)
                customFloat("main", 1.0f)
            }
            constrain(slot2) {
                width = 100.dp.asDimension
                height = 200.dp.asDimension
                end.linkTo(parent.end, 8.dp)
                centerVerticallyTo(parent)
                customColor("mainColor", Color.Blue)
            }
            constrain(slot3) {
                width = 100.dp.asDimension
                height = 200.dp.asDimension
                start.linkTo(parent.end, 8.dp)
                centerVerticallyTo(parent)
                customColor("mainColor", Color.Blue)
            }
            constrain(slot4) {
                width = 100.dp.asDimension
                height = 200.dp.asDimension
                start.linkTo(slot3.end, 8.dp)
                centerVerticallyTo(parent)
                customColor("mainColor", Color.Blue)
            }
        }
        transition("forward", startState, nextState) {}
        transition("backward", startState, previousState) {}
    }
    MotionCarousel(motionScene, 2, 5, content = content)
}

/**
 * For completeness' sake, we can write the same Carousel in JSON instead of the DSL
 */
@Composable
fun MyCarouselJSON(content: MotionCarouselScope.() -> Unit) {
    val motionScene = MotionScene("""{
                Header: {
                  exportAs: 'carousel'
                },
                ConstraintSets: {
                  start: { 
                    slot0: {
                      width: 100,
                      height: 200,
                      end: ['parent','start', 8],
                      centerVertically: 'parent',
                      custom: {
                        mainColor: '#0000FF'
                      }  
                    }, 
                    slot1: {
                      width: 100,
                      height: 200,
                      start: ['parent','start', 8],
                      centerVertically: 'parent',
                      custom: {
                        mainColor: '#0000FF'
                      }  
                    },
                    slot2: {
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
                    slot3: {
                      width: 100,
                      height: 200,
                      end: ['parent','end', 8],
                      centerVertically: 'parent',
                      custom: {
                        mainColor: '#0000FF'
                      }  
                    },
                    slot4: {
                      width: 100,
                      height: 200,
                      start: ['parent','end', 8],
                      centerVertically: 'parent',
                      custom: {
                        mainColor: '#0000FF'
                      }  
                    }                  
                  },
                  next: { 
                    slot0: {
                      width: 100,
                      height: 200,
                      end: ['slot1','start', 8],
                      centerVertically: 'parent',
                      custom: {
                        mainColor: '#0000FF'
                      }  
                    }, 
                    slot1: {
                      width: 100,
                      height: 200,
                      end: ['parent','start', 8],
                      centerVertically: 'parent',
                      custom: {
                        mainColor: '#0000FF'
                      }  
                    },
                    slot2: {
                      width: 100,
                      height: 200,
                      start: ['parent','start',8],
                      centerVertically: 'parent',
                      custom: {
                        mainColor: '#0000FF'
                      }  
                    },
                    slot3: {
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
                    slot4: {
                      width: 100,
                      height: 200,
                      end: ['parent','end', 8],
                      centerVertically: 'parent',
                      custom: {
                        mainColor: '#0000FF'
                      }  
                    }                    
                  },
                  previous: {
                    slot0: {
                      width: 100,
                      height: 200,
                      start: ['parent','start', 8],
                      centerVertically: 'parent',
                      custom: {
                        mainColor: '#0000FF'
                      }  
                    }, 
                    slot1: {
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
                    slot2: {
                      width: 100,
                      height: 200,
                      end: ['parent','end', 8],
                      centerVertically: 'parent',
                      custom: {
                        mainColor: '#0000FF'
                      }  
                    },
                    slot3: {
                      width: 100,
                      height: 200,
                      start: ['parent','end', 8],
                      centerVertically: 'parent',
                      custom: {
                        mainColor: '#0000FF'
                      }  
                    },
                    slot4: {
                      width: 100,
                      height: 200,
                      start: ['slot3','end', 8],
                      centerVertically: 'parent',
                      custom: {
                        mainColor: '#0000FF'
                      }  
                    }                    
                  }
                },
                Transitions: {
                  forward: {
                    from: 'start',
                    to: 'next',
                  },
                  backward: {
                    from: 'start',
                    to: 'previous',
                  },
               }   
            }""")
    MotionCarousel(motionScene, 2, 5, content = content, showSlots = false)
}

/**
 * The resulting demo, still using MyCard() for the content
 */
@Preview
@Composable
fun CarouselDemo6() {
    // or
    // MyCarouselJSON()
    MyCarouselDSL() {
        items(cardsExample) { card ->
            MyCard(card)
        }
    }
}

/**
 * Finally, you might want to have the "selected" item of your carousel
 * being painted or acting differently than the rest -- how could you implement this?
 *
 * One approach is to define custom properties directly in the MotionScene, and
 * retrieving them at runtime.
 *
 * You might have noticed that this is exactly what we did in the MotionScene used for
 * the previous CarouselDemo6 example -- we defined a "mainColor" as well as a "main" property
 * that we assign to our "selected" slot.
 *
 * We can then simply retrieve those properties by using itemsWithProperties() instead of items(),
 * and pass them to a new AnimatedCard composable
 */
@Preview
@Composable
fun CarouselDemo7() {
    MyCarouselDSL() {
        itemsWithProperties(cardsExample) { card, properties ->
            AnimatedCard(properties, card)
        }
    }
}

/**
 * This is using the custom properties we defined in the motion scene to do something a little
 * more interesting -- here each card will have an intrinsic color, but will revert to a default
 * color unless the card is the current selection (recognized by the attribute "main"). While we
 * are at it, let's use a gradient too!
 */
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