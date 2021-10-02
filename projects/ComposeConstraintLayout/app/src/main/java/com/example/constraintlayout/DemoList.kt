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

package com.example.constraintlayout

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionLayoutDebugFlags
import androidx.constraintlayout.compose.MotionScene
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

var names = arrayOf(
    "Aiden", "Amelia", "Aria", "Asher",
    "Ava", "Benjamin", "Camila", "Charlotte",
    "Elijah", "Ella", "Ellie", "Emma",
    "Ethan", "Evelyn", "Gianna", "Grayson",
    "Harper", "Isabella", "Jack", "Jackson",
    "James", "Layla", "Leo", "Leriel",
    "Levi", "Liam", "Logan", "Lucas",
    "Luna", "Mason", "Mateo", "Mia",
    "Mila", "Noah", "Oliver", "Olivia",
    "Ryilee", "Sophia"
);

@Preview(group = "list_2")
@Composable
public fun BigList() {
    val date: Date = Calendar.getInstance().getTime()
    val dateFormat: DateFormat = SimpleDateFormat("M/dd h:mm")

    Column {
        Text(text = "First item")
    LazyColumn(
        modifier = Modifier
            .width(300.dp)
            .height(500.dp),
    ){
        items(12) { index ->
            ListEntry(painterResource(id = R.drawable.pepper),names[index], dateFormat.format(names[index].hashCode()));
        }
        }
        Text(text = "Last item")
    }
}

@Composable
public fun ListEntry(picture : Painter, name: String, dob: String) {
    var animateToEnd by remember { mutableStateOf(false) }

    val progress by animateFloatAsState(
        targetValue = if (animateToEnd) 1f else 0f,
        animationSpec = tween(700)
    )
    Row( modifier = Modifier
        .background(Color.White)
        .fillMaxWidth()) {

        val scene1 = MotionScene("""
{
  Header: {
    name: 'entry'
  },
  ConstraintSets: {
    // END STATE
    start: {
      id1: {
        width: 80, height: 80,
        start: ['parent', 'start', 16],
        bottom: ['parent', 'bottom', 16]
      },
      id2: {
        width: 'spread', height:  'wrap',
        start: ['id1', 'end', 16],
        end: ['parent', 'end', 16],
        top: ['id1', 'top', 16],
    
      },
      id3: {
        width: 'spread', height: 'wrap',
        start: ['id1', 'end', 16],
        end: ['parent', 'end', 16],
        bottom: ['id1', 'bottom', 16],
 
      },
      button: {
        width: 40, height: 'wrap',
        end: ['parent', 'end', 8],
        top: ['parent', 'top', 8],
        bottom: ['parent', 'bottom', 8],

      }
    },
    // END STATE
    end: {
      id1: {
        width: 80, height: 80,
        start: ['parent', 'start', 16],
        top: ['parent', 'top', 16],
        rotationX: 360
      },
      id2: {
        width: 'spread', height:  'wrap',
        start: ['id1', 'end', 16],
        end: ['parent', 'end', 16],
        top: ['parent', 'top', 16],
       bottom: ['parent', 'bottom', 16],
        scaleY: 2,
        scaleX: 2,
        translationX: 200
      },
      id3: {
        width: 'spread', height: 'wrap',
        start: ['id1', 'end', 16],
        end: ['parent', 'end', 16],
        bottom: ['id1', 'bottom', 16],
        alpha: 0
      },
      button: {
        width: 40, height: 'wrap',
        end: ['parent', 'end', 8],
        top: ['parent', 'top', 8],
       bottom: ['parent', 'bottom', 8],
    }
    },
  },
  Transitions: {
    default: {
      from: 'start',
      to: 'end',
      pathMotionArc: 'startHorizontal',
    }
  }
}
            """)

        MotionLayout(
            modifier = Modifier
                .fillMaxWidth( )
                .height(100.dp),
            motionScene = scene1,
            progress    = progress) {

            Image(picture,"face",modifier = Modifier.layoutId("id1"))
            Text(  name, modifier = Modifier.layoutId("id2") )
            Text(dob,modifier = Modifier.layoutId("id3") )
            Button(onClick = { animateToEnd = !animateToEnd },
               modifier = Modifier.layoutId("button") ) {

            Image(painterResource(R.drawable.ic_phone),"")
            }
        }
    }
}
