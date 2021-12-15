/*
 * Copyright (C) 2021 The Android Open Source Project
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

package com.example.constraintlayout.verification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import com.example.constraintlayout.R



@Preview(group = "VTest02d")
@Composable
fun VTest02a() {
    ConstraintLayout(
        ConstraintSet("""
            {
                Header: { exportAs: 'test1'},
                guid1: { type: 'vGuideline', start: 80 },
                guide2: { type: 'vGuideline', end: 80 },
                button1: {
                  width: 'spread',
                  top: ['title', 'bottom', 16],
                  start: ['guid1', 'start'],
                  end: ['guide2', 'end']
                },
                title: {
                  width: { value: 'wrap', max: 300 },
                  centerVertically: 'parent',
                  start: ['guid1', 'start'],
                  end: ['guide2','end']
                }
            }
        """),
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            modifier = Modifier.layoutId("button1"),
            onClick = {},
        ) {
            Text(text = stringResource(id = R.string.log_in))
        }
        Text(modifier = Modifier.layoutId("title").background(Color.Red),
            text = "ABC dsa sdfs sdf adfas asdas asdad asdas",// DEF GHI JKL MNO PQR STU VWX YZ ABC DEF",
            style = MaterialTheme.typography.body1,
        )
    }
}

@Preview(group = "VTest02d")
@Composable
fun VTest02b() {
    ConstraintLayout(
        ConstraintSet("""
            {
                Header: { exportAs: 'test2'},
                gl1: { type: 'hGuideline', start: 80 },
                gl2: { type: 'hGuideline', end: 80 },
                button2: {
 
                  width: '38%',
 
                  start: ['title', 'start', 16],
                  bottom: ['gl2', 'bottom'],
                  rotationZ: 32,
                },
 
                title0: {
                  width: 100,
                  centerHorizontally: 'parent',
                  top: ['gl1', 'bottom', 16],
                  
                },
                title1: {
                  width: 'spread',
                  centerHorizontally: 'title3',
                  top: ['title0', 'bottom', 16],
             
                },
                 title2: {
                  width: 'parent',
                  centerHorizontally: 'parent',
                  top: ['title1', 'bottom', 16],
                 
                },
                 title3: {
                  width: '38%'  ,
                  centerHorizontally: 'parent',
                  top: ['title2', 'bottom', 16],
                 

                }
            }
        """),
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            modifier = Modifier.layoutId("button2"),
            onClick = {},
        ) {
            Text(text = stringResource(id = R.string.log_in))
        }

        Text(modifier = Modifier.layoutId("title0").background(Color.Red),
            text = "This is a test of width",// DEF GHI JKL MNO PQR STU VWX YZ ABC DEF",
            style = MaterialTheme.typography.body1,
        )
        Text(modifier = Modifier.layoutId("title1").background(Color.Red),
            text = "This is a test of width",// DEF GHI JKL MNO PQR STU VWX YZ ABC DEF",
            style = MaterialTheme.typography.body1,
        )
        Text(modifier = Modifier.layoutId("title2").background(Color.Red),
            text = "This is a test of width",// DEF GHI JKL MNO PQR STU VWX YZ ABC DEF",
            style = MaterialTheme.typography.body1,
        )
        Text(modifier = Modifier.layoutId("title3").background(Color.Red),
            text = "This is a test of width",// DEF GHI JKL MNO PQR STU VWX YZ ABC DEF",
            style = MaterialTheme.typography.body1,
        )
    }
}


@Preview(group = "VTest02d")
@Composable
public fun VTest02c() {
    ConstraintLayout(
        ConstraintSet("""
            {
                Header: { exportAs: 'test3'},
                gl1: { type: 'hGuideline', start: 80 },
                gl2: { type: 'hGuideline', end: 80 },
                button3: {
                  width: { value: '38%' },
                  start: ['parent', 'start', 50],
                  bottom: ['gl2', 'bottom'],
 
                },
                title0: {
                  width:  { value: 'wrap', min: 60  },
                  centerHorizontally: 'parent',
                  top: ['gl1', 'bottom', 16],
                  
                },
                title1: {
                  width:  { value: 'spread', min: 200  },
                  centerHorizontally: 'title3',
                  top: ['title0', 'bottom', 16],
             
                },
                 title2: {
                  width:  { value: 'parent', min: 400 }, 
                  centerHorizontally: 'parent',
                  top: ['title1', 'bottom', 16],
                 
                },
                 title3: {
                  width: { value: '30%' }  ,
                  centerHorizontally: 'parent',
                  top: ['title2', 'bottom', 16],
                 
                },
                 title4: {
                  width: { value: 'parent', max: 100  }  ,
                  centerHorizontally: 'title3',
                  top: ['title3', 'bottom', 16],
                 
                },
                 title5: {
                  width: { value: 'preferWrap' }  ,
                  centerHorizontally: 'parent',
                  top: ['title4', 'bottom', 16],

                }
            }
        """),
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            modifier = Modifier.layoutId("button3"),
            onClick = {},
        ) {
            Text(text = stringResource(id = R.string.log_in))
        }
        Text(modifier = Modifier.layoutId("title0").background(Color.White),
            text = "a b title0 d e",// DEF GHI JKL MNO PQR STU VWX YZ ABC DEF",
            style = MaterialTheme.typography.body1,
        )
        Text(modifier = Modifier.layoutId("title1").background(Color.White),
            text = "a b c d e f g h j title1 p q r s t u v w x y z",// DEF GHI JKL MNO PQR STU VWX YZ ABC DEF",
            style = MaterialTheme.typography.body1,
        )
        Text(modifier = Modifier.layoutId("title2").background(Color.White),
            text = "a b c d e f g h title2 n o p q r s t u v w x y z",// DEF GHI JKL MNO PQR STU VWX YZ ABC DEF",
            style = MaterialTheme.typography.body1,
        )
        Text(modifier = Modifier.layoutId("title3").background(Color.White),
            text = "a b c d e f g h j k title3 q r s t u v w x y z",// DEF GHI JKL MNO PQR STU VWX YZ ABC DEF",
            style = MaterialTheme.typography.body1,
        )
        Text(modifier = Modifier.layoutId("title4").background(Color.White),
            text = "a b c d e f g h j k l title4 m n o p q r s t u v w x y z",// DEF GHI JKL MNO PQR STU VWX YZ ABC DEF",
            style = MaterialTheme.typography.body1,
        )
        Text(modifier = Modifier.layoutId("title5").background(Color.White),
            text = "a b c d e f g h j k l title5 m n o p q r s t u v w x y z",// DEF GHI JKL MNO PQR STU VWX YZ ABC DEF",
            style = MaterialTheme.typography.body1,
        )
    }
}

@Preview(group = "VTest02d")
@Composable
public fun VTest02d() {
    ConstraintLayout(
        ConstraintSet("""
            {
                Header: { exportAs: 'test4'},
                 lside: { type: 'vGuideline', percent: 0.33 },
                rside: { type: 'vGuideline', percent: 0.66 },
                button4: {
                  width: { value: 'spread'  },
                  start: ['lside', 'start' ],
                  end: ['rside', 'end' ],
                  bottom: ['parent', 'bottom',20],

                },
                title0: {
                  width:  { value: 'spread', min: 200},
                  centerHorizontally: 'button4',
                  top: ['parent', 'top', 20],
                  
                },
                title1: {
                  width:  { value: 'spread',max: 100  },
                  centerHorizontally: 'button4',
                  top: ['title0', 'bottom', 16],
             
                },
                 title2: {
                  width:  { value: 'spread',min: 'wrap' }, 
                  centerHorizontally: 'button4',
                  top: ['title1', 'bottom', 16],
                 
                },
                 title3: {
                  width: { value: 'spread',min: 'wrap'  }  ,
                  centerHorizontally: 'button4',
                  top: ['title2', 'bottom', 16],
                 
                },
                 title4: {
                  width: { value: 'spread',min: 'I'  }  ,
                  centerHorizontally: 'button4',
                  top: ['title3', 'bottom', 16],
                 
                },
                 title5: {
                  width: { value: 'spread' }  ,
                  centerHorizontally: 'button4',
                  top: ['title4', 'bottom', 16],
                 }
            }
        """),
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            modifier = Modifier.layoutId("button4"),
            onClick = {},
        ) {
            Text(text = stringResource(id = R.string.log_in))
        }
        Text(modifier = Modifier.layoutId("title0").background(Color.Red),
            text = "a b c d e f g h j k l m n o p q r s t u v w x y z",// DEF GHI JKL MNO PQR STU VWX YZ ABC DEF",
            style = MaterialTheme.typography.body1,
        )
        Text(modifier = Modifier.layoutId("title1").background(Color.Red),
            text = "a b c d e f g h j k l m n o p q r s t u v w x y z",// DEF GHI JKL MNO PQR STU VWX YZ ABC DEF",
            style = MaterialTheme.typography.body1,
        )
        Text(modifier = Modifier.layoutId("title2").background(Color.Red),
            text = "a b c d e f g h j k l m n o p q r s t u v w x y z",// DEF GHI JKL MNO PQR STU VWX YZ ABC DEF",
            style = MaterialTheme.typography.body1,
        )
        Text(modifier = Modifier.layoutId("title3").background(Color.Red),
            text = "a b c d e f g h j k l m n o p q r s t u v w x y z",// DEF GHI JKL MNO PQR STU VWX YZ ABC DEF",
            style = MaterialTheme.typography.body1,
        )
        Text(modifier = Modifier.layoutId("title4").background(Color.White),
            text = "a b c d e f g h j k l preferWrap m n o p q r s t u v w x y z",// DEF GHI JKL MNO PQR STU VWX YZ ABC DEF",
            style = MaterialTheme.typography.body1,
        )
        Text(modifier = Modifier.layoutId("title5").background(Color.White),
            text = "a b c d e f g h j k l preferWrap m n o p q r s t u v w x y z",// DEF GHI JKL MNO PQR STU VWX YZ ABC DEF",
            style = MaterialTheme.typography.body1,
        )
    }
}

@Preview(group = "new")
@Composable
public fun VTest02e() {
    ConstraintLayout(
        ConstraintSet("""
            {
                Header: { exportAs: 'test5'},
             
                button5: {
                  width: 'wrap',
                  right: ['title','right'],
                  centerVertically: 'parent',
              
                },
                  button5a: {
                  width: 'wrap',
                  left: ['title','left'],
                  bottom: ['button5', 'top',20, 100],
                },
               button5b: {
                  width: 'wrap',
                  start: ['title','start'],
                  bottom: ['button5a', 'top',20, 100],
                },
                button5c: {
                  width: 'wrap',
                  end: ['title','end'],
                  bottom: ['button5b', 'top',20, 100],
                },
                title: {
                   width: 'wrap',
                  centerHorizontally: 'parent',
                  top: ['button5', 'bottom',20, 100],
                
                }
            }
        """),
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            modifier = Modifier.layoutId("button5"),
            onClick = {},
        ) {
            Text(text = "right")
        }
        Button(
            modifier = Modifier.layoutId("button5a"),
            onClick = {},
        ) {
            Text(text = "left")
        }
        Button(
            modifier = Modifier.layoutId("button5b"),
            onClick = {},
        ) {
            Text(text = "start")
        }
        Button(
            modifier = Modifier.layoutId("button5c"),
            onClick = {},
        ) {
            Text(text = "end")
        }
        Text(modifier = Modifier.layoutId("title").background(Color.Red),
            text = "ABC dsa sdfs sdf adfas asdas asdad asdas",// DEF GHI JKL MNO PQR STU VWX YZ ABC DEF",
            style = MaterialTheme.typography.body1,
        )

    }
}

@Preview(group = "new")
@Composable
public fun VTest02f() {
    ConstraintLayout(
        ConstraintSet("""
            {
                Header: { exportAs: 'test6'},
             
                button5: {
                  width: 'wrap',
                  pivotX: 0,
                  pivotY: 0,
                  rotationZ: 20,
                  circular: ['title',0,120],
                },
                  button5a: {
                  width: 'wrap',
                  rotationZ: 90,
                    circular: ['title',90,120],
                },
               button5b: {
                  width: 'wrap',
                  rotationX: 30,
                    circular: ['title',180,120],
                },
                button5c: {
                  width: 'wrap',
                  rotationY: 30,
                      scaleY: 1.4,
                    circular: ['title',270,120],
                },
                title: {
                   width: 'wrap',
                   translationZ: 13,
                   scaleX: 1.4,
                   centerHorizontally: 'parent',
                   centerVertically: 'parent',
                },
              base1: {
                   width: 'wrap',
            
           right: ['title','right'],
           baseline: ['title','top',30],
                 
                
                }
            }
        """),
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            modifier = Modifier.layoutId("button5"),
            onClick = {},
        ) {
            Text(text = "12")
        }
        Button(
            modifier = Modifier.layoutId("button5a"),
            onClick = {},
        ) {
            Text(text = "3")
        }
        Button(
            modifier = Modifier.layoutId("button5b"),
            onClick = {},
        ) {
            Text(text = "6")
        }
        Button(
            modifier = Modifier.layoutId("button5c"),
            onClick = {},
        ) {
            Text(text = "9")
        }
        Text(modifier = Modifier.layoutId("title").background(Color.Red),
            text = "clock",
            style = MaterialTheme.typography.body1,
        )
        Button(
            modifier = Modifier.layoutId("base1"),
            onClick = {},
        ) {
            Text(text = "baseline")
        }
    }
}

@Preview(group = "new")
@Composable
public fun VTest02g() {
    ConstraintLayout(
        ConstraintSet("""
            {
                Header: { exportAs: 'test7'},
             
                button5: {
                  width: 'wrap',
 
                   centerHorizontally: 'parent',
                   centerVertically: 'parent',
                   hBias: 0.1,
                   vBias: 0.1,
                },
                  button5a: {
                  width: 'wrap',
                   centerHorizontally: 'parent',
                   centerVertically: 'parent',
                   hBias: 0.1,
                   vBias: 0.9,
                },
               button5b: {
                  width: 'wrap',
                   centerHorizontally: 'parent',
                   centerVertically: 'parent',
                   hBias: 0.9,
                   vBias: 0.1,
                },
                button5c: {
                  width: 'wrap',
                   centerHorizontally: 'parent',
                   centerVertically: 'parent',
                   hBias: 0.9,
                   vBias: 0.9,
                },
                title: {
                   width: 'wrap',
 
                   centerHorizontally: 'parent',
                   centerVertically: 'parent',
                   hBias: 0.5,
                   vBias: 0.5,
                },
              base1: {
                   width: 'wrap',
            
           right: ['title','right'],
           top: ['button5','bottom',0],
           bottom: ['title','top',0],
                 vBias: 0.1,
                
                }
            }
        """),
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            modifier = Modifier.layoutId("button5"),
            onClick = {},
        ) {
            Text(text = "12")
        }
        Button(
            modifier = Modifier.layoutId("button5a"),
            onClick = {},
        ) {
            Text(text = "3")
        }
        Button(
            modifier = Modifier.layoutId("button5b"),
            onClick = {},
        ) {
            Text(text = "6")
        }
        Button(
            modifier = Modifier.layoutId("button5c"),
            onClick = {},
        ) {
            Text(text = "9")
        }
        Text(modifier = Modifier.layoutId("title").background(Color.Red),
            text = "clock",
            style = MaterialTheme.typography.body1,
        )
        Button(
            modifier = Modifier.layoutId("base1"),
            onClick = {},
        ) {
            Text(text = "baseline")
        }
    }
}

@Preview(group = "new")
@Composable
public fun VTest02h() {
    ConstraintLayout(
        ConstraintSet("""
            {
                Header: { exportAs: 'test8'},
             Helpers: [
                  ['hChain', ['button5','button5a','button5b'], {
                        start: ['title', 'end'], style: 'spread_inside'}],
                  ['vChain', ['button5a','button5b','button5c'], {
                        top: ['title', 'bottom'], style: 'spread'}],
               ],
                button5: {
                   centerVertically: 'parent',
                   vBias: 0.45
                },
                  button5a: {
                   centerVertically: 'parent',
                     height: 'spread',
                      vWeight: 1
                },
               button5b: {
                   centerVertically: 'parent',
                      height: 'spread',
                       vWeight: 2
                },
                button5c: {
                  width: 'wrap',
                  height: 'spread',
                   centerHorizontally: 'parent',
                   centerVertically: 'parent',
                   hBias: 0.9,
                   vWeight: 3
                },
                title: {
                   width: 'wrap',
 
                   centerHorizontally: 'parent',
                   centerVertically: 'parent',
                   hBias: 0.1,
                   vBias: 0.5,
                },
              base1: {
                   width: 'wrap',
            
           start: ['button5','start'],
           top: ['button5','bottom',0],
           bottom: ['parent','bottom',0],
                 vBias: 0.5,
                
                }
            }
        """),
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            modifier = Modifier.layoutId("button5"),
            onClick = {},
        ) {
            Text(text = "1")
        }
        Button(
            modifier = Modifier.layoutId("button5a"),
            onClick = {},
        ) {
            Text(text = "2")
        }
        Button(
            modifier = Modifier.layoutId("button5b"),
            onClick = {},
        ) {
            Text(text = "3")
        }
        Button(
            modifier = Modifier.layoutId("button5c"),
            onClick = {},
        ) {
            Text(text = "4")
        }
        Text(modifier = Modifier.layoutId("title").background(Color.Red),
            text = "chain",
            style = MaterialTheme.typography.body1,
        )
        Button(
            modifier = Modifier.layoutId("base1"),
            onClick = {},
        ) {
            Text(text = "baseline")
        }
    }
}
