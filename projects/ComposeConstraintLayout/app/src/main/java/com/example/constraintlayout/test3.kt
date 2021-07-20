package com.example.constraintlayout

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import java.util.*

@Preview(group = "new")
@Composable
public fun ExampleLayout() {
    ConstraintLayout(
        ConstraintSet("""
            {
                Header: { exportAs: 'example 3'},
                g1: { type: 'vGuideline', start: 80 },
                g2: { type: 'vGuideline', end: 80 },
                button: {
                  width: 'spread',
                  top: ['title', 'bottom', 16],
                  start: ['g1', 'start'],
                  end: ['g2', 'end']
                },
                title: {
                  width: { value: 'wrap', max: 300 },
                  centerVertically: 'parent',
                  start: ['g1', 'start'],
                  end: ['g2','end']
                }
            }
        """),
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            modifier = Modifier.layoutId("button"),
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
