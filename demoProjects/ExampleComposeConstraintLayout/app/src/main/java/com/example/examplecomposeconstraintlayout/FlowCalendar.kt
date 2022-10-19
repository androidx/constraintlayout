package com.example.examplecomposeconstraintlayout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Wrap
import java.util.*

/**
 * A demo of using ConstraintLayout in a LazyColumn to create a  Calendar
 */

@Preview(group = "scroll")
@Composable
fun CalendarList() {
    LazyColumn() {
        items(1000) {
            Box(
                modifier = Modifier
                    .padding(3.dp)
                    .background(Color(0xFFA2A2E0))
            ) {
                DynamicCalendar(it)
            }
        }
    }
}

@Preview(group = "scroll")
@Composable
fun DynamicCalendar(montOffset: Int = 0) {
    val days: ArrayList<String> = ArrayList(listOf("S", "M", "T", "W", "T", "F", "S"))
    val cal: Calendar = Calendar.getInstance()
    val t = cal.timeInMillis

    for (pos in 0..41) {
        cal.timeInMillis = t
        cal.add(Calendar.MONTH, montOffset)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val offset = cal.get(Calendar.DAY_OF_WEEK) - 1
        val lastDay: Int = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        if (offset > pos || pos - offset >= lastDay) {
            days.add("")
        } else {
            days.add((pos - offset + 1).toString())
        }
    }
    cal.timeInMillis = t

    cal.add(Calendar.MONTH, montOffset)
    val calDate = cal.get(Calendar.MONTH).toString() + "/" + cal.get(Calendar.YEAR)
    val refId = days.mapIndexed { index: Int, s: String -> "id" + index + "_$s" }.toTypedArray()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()
    ) {
        ConstraintLayout(
            ConstraintSet {
                val date = createRefFor("date")
                val keys = refId.map { createRefFor(it) }.toTypedArray()
                val flow = createFlow(
                    elements = keys,
                    maxElement = 7,
                    wrapMode = Wrap.Aligned,
                    verticalGap = 8.dp,
                    horizontalGap = 8.dp
                )
                constrain(flow) {
                    top.linkTo(date.bottom, 18.dp)
                    bottom.linkTo(parent.bottom)
                    centerHorizontallyTo(parent)
                }
                constrain(date) {
                    centerHorizontallyTo(parent)
                    top.linkTo(parent.top, 6.dp)
                }
            }
        ) {

            Text(text = calDate, fontSize = 30.sp, modifier = Modifier.layoutId("date"))
            refId.forEachIndexed { index, id ->
                Text(text = days[index], modifier = Modifier.layoutId(id))
            }
        }
    }
}
