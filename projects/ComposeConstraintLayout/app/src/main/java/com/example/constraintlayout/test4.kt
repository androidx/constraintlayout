package com.example.constraintlayout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.*

@Preview(group = "new5")
@Composable
public fun ExampleLayout2() {
    ConstraintLayout(
        ConstraintSet {
            val center = createRefFor("center")
            val h1 = createRefFor("h1")
            val h2 = createRefFor("h2")
            val h3 = createRefFor("h3")
            val h4 = createRefFor("h4")
            val h5 = createRefFor("h5")
            val h6 = createRefFor("h6")
            val h7 = createRefFor("h7")
            val h8 = createRefFor("h8")
            val h9 = createRefFor("h9")
            val h10 = createRefFor("h10")
            val h11 = createRefFor("h11")
            val h12 = createRefFor("h12")
            constrain(center) {
                centerTo(parent)
            }
            constrain(h1) {
                circular(parent, 30f, 100.dp)
            }
            constrain(h2) {
                circular(parent, 60f, 100.dp)
            }
            constrain(h3) {
                circular(parent, 90f, 100.dp)
            }
            constrain(h4) {
                circular(parent, 120f, 100.dp)
            }
            constrain(h5) {
                circular(parent, 150f, 100.dp)
            }
            constrain(h6) {
                circular(parent, 180f, 100.dp)
            }
            constrain(h7) {
                circular(parent, 210f, 100.dp)
            }
            constrain(h8) {
                circular(parent, 240f, 100.dp)
            }
            constrain(h9) {
                circular(parent, 270f, 100.dp)
            }
            constrain(h10) {
                circular(parent, 300f, 100.dp)
            }
            constrain(h11) {
                circular(parent, 330f, 100.dp)
            }
            constrain(h12) {
                circular(parent, 0f, 100.dp)
            }
        },
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(modifier = Modifier.layoutId("center"), text = "C")
        Text(modifier = Modifier.layoutId("h1"), text = "1")
        Text(modifier = Modifier.layoutId("h2"), text = "2")
        Text(modifier = Modifier.layoutId("h3"), text = "3")
        Text(modifier = Modifier.layoutId("h4"), text = "4")
        Text(modifier = Modifier.layoutId("h5"), text = "5")
        Text(modifier = Modifier.layoutId("h6"), text = "6")
        Text(modifier = Modifier.layoutId("h7"), text = "7")
        Text(modifier = Modifier.layoutId("h8"), text = "8")
        Text(modifier = Modifier.layoutId("h9"), text = "9")
        Text(modifier = Modifier.layoutId("h10"), text = "10")
        Text(modifier = Modifier.layoutId("h11"), text = "11")
        Text(modifier = Modifier.layoutId("h12"), text = "12")
    }
}

@Preview(group = "new2")
@Composable
public fun ExampleLayout3() {
    ConstraintLayout(modifier = Modifier
        .fillMaxSize()){
        val (center, h1, h2, h3, h4, h5, h6, h7, h8, h9, h10, h11, h12) = createRefs()

        Text(modifier = Modifier.constrainAs(center) {
            centerTo(parent)
        }, text = "C")
        Text(modifier = Modifier.constrainAs(h1) {
            circular(parent, 30f, 100.dp)
        }, text = "1")
        Text(modifier = Modifier.constrainAs(h2) {
            circular(parent, 60f, 100.dp)
        }, text = "2")
        Text(modifier = Modifier.constrainAs(h3) {
            circular(parent, 90f, 100.dp)
        }, text = "3")
        Text(modifier = Modifier.constrainAs(h4) {
            circular(parent, 120f, 100.dp)
        }, text = "4")
        Text(modifier = Modifier.constrainAs(h5) {
            circular(parent, 150f, 100.dp)
        }, text = "5")
        Text(modifier = Modifier.constrainAs(h6) {
            circular(parent, 180f, 100.dp)
        }, text = "6")
        Text(modifier = Modifier.constrainAs(h7) {
            circular(parent, 210f, 100.dp)
        }, text = "7")
        Text(modifier = Modifier.constrainAs(h8) {
            circular(parent, 240f, 100.dp)
        }, text = "8")
        Text(modifier = Modifier.constrainAs(h9) {
            circular(parent, 270f, 100.dp)
        }, text = "9")
        Text(modifier = Modifier.constrainAs(h10) {
            circular(parent, 300f, 100.dp)
        }, text = "10")
        Text(modifier = Modifier.constrainAs(h11) {
            circular(parent, 330f, 100.dp)
        }, text = "11")
        Text(modifier = Modifier.constrainAs(h12) {
            circular(parent, 0f, 100.dp)
        }, text = "12")
    }
}

@Preview(group = "new4")
@Composable
public fun ExampleLayout4() {
    ConstraintLayout(
        ConstraintSet("""
            {
                center: {
                  center: 'parent'
                },
                h1: { circular: ['center', 30, 100] },
                h2: { circular: ['center', 60, 100] },
                h3: { circular: ['center', 90, 100] },
                h4: { circular: ['center', 120, 100] },
                h5: { circular: ['center', 150, 100] },
                h6: { circular: ['center', 180, 100] },
                h7: { circular: ['center', 210, 100] },
                h8: { circular: ['center', 240, 100] },
                h9: { circular: ['center', 270, 100] },
                h10: { circular: ['center', 300, 100] },
                h11: { circular: ['center', 330, 100] },
                h12: { circular: ['center', 0, 100] }
            }
        """),
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(modifier = Modifier.layoutId("center"), text = "C")
        Text(modifier = Modifier.layoutId("h1"), text = "1")
        Text(modifier = Modifier.layoutId("h2"), text = "2")
        Text(modifier = Modifier.layoutId("h3"), text = "3")
        Text(modifier = Modifier.layoutId("h4"), text = "4")
        Text(modifier = Modifier.layoutId("h5"), text = "5")
        Text(modifier = Modifier.layoutId("h6"), text = "6")
        Text(modifier = Modifier.layoutId("h7"), text = "7")
        Text(modifier = Modifier.layoutId("h8"), text = "8")
        Text(modifier = Modifier.layoutId("h9"), text = "9")
        Text(modifier = Modifier.layoutId("h10"), text = "10")
        Text(modifier = Modifier.layoutId("h11"), text = "11")
        Text(modifier = Modifier.layoutId("h12"), text = "12")
    }
}

@Preview(group = "new2")
@Composable
public fun ExampleLayout5() {
    ConstraintLayout(modifier = Modifier
        .fillMaxSize()){
        val center = createRef()
        Text(modifier = Modifier.constrainAs(center) {
            centerTo(parent)
        }, text = "C")
        for (i in 0 until 12) {
            var h = createRef()
            Text(modifier = Modifier.constrainAs(h) {
                circular(parent, 30f + i * 30f, 100.dp)
            }, text = "${i+1}")
        }
    }
}

@Preview(group = "new6")
@Composable
public fun ExampleLayout6() {
    ConstraintLayout(modifier = Modifier
        .fillMaxSize()){
        val center = createRef()
        Text(modifier = Modifier.constrainAs(center) {
            centerTo(parent)
        }, text = "C")
        for (i in 0 until 12) {
            var h = createRef()
            Text(modifier = Modifier.constrainAs(h) {
                circular(parent, 30f + i * 30f, 100.dp)
            }, text = "${i+1}")
        }
    }
}

@Preview(group = "new7")
@Composable
public fun ExampleLayout7() {
    var baseConstraintSet = """
            {
                Variables: {
                  angle: { from: 0, step: 10 },
                  rotation: { from: 'startRotation', step: 10 },
                  distance: 100,
                  mylist: { tag: 'box' }
                },
                Generate: {
                  mylist: {
                    width: 200,
                    height: 40,
                    circular: ['parent', 'angle', 'distance'],
                    pivotX: 0.1,
                    pivotY: 0.1,
                    translationX: 225,
                    rotationZ: 'rotation'
                  }
                }
            }
        """

    ConstraintLayout(
        ConstraintSet("""
          {
            Variables: {
              list: { from: 1, to: 12, prefix: 'h' },
              angle: { from: 0, step: 30 }
            },
            Generate: {
              list: { circular: ['parent', 'angle', 100] }
            },
            center: { center: 'parent' }
          } 
        """),
        modifier = Modifier.fillMaxSize()){
        Text(modifier = Modifier.layoutId("center"), text = "C")

        for (i in 0 until 12) {
            Text(modifier = Modifier.layoutId("h${i+1}"),
                text = "${i+1}")
        }
    }
}