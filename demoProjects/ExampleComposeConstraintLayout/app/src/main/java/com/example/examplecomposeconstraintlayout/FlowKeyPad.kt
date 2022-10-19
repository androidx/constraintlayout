package com.example.examplecomposeconstraintlayout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Wrap


@Preview(group = "flow1")
@Composable
public fun FlowPad() {
    val names = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "*", "0", "#")
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        ConstraintLayout(
            ConstraintSet {
                val keys = names.map { createRefFor(it) }.toTypedArray()
                val flow = createFlow(
                    elements = keys,
                    maxElement = 3,
                    wrapMode = Wrap.Aligned,
                    verticalGap = 8.dp,
                    horizontalGap = 8.dp
                )
                constrain(flow) {
                    centerTo(parent)
                }
            },
            modifier = Modifier
                .background(Color(0xff323222))
                .padding(8.dp)
        ) {

            for (num in names) {
                Button(
                    modifier = Modifier.layoutId(num),
                    onClick = {},
                ) {
                    Text(text = num)
                }
            }
        }

    }
}