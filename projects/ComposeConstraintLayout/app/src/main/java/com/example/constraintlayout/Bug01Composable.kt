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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.fragment.app.DialogFragment
import java.util.*


private  const  val LOREM_IPSUM_LONG = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."


/**
 * A SIMPLIFIED/minimalist version of the problem I am encountering.
 * Contains a [ConstraintLayout] with two sections:
 * 1. textSection: Section for the text. THIS SECTION KEEPS GETTING UNDESIRABLY CROPPED
 * - When increasing font-size of the device, such that the content gets bigger than the screen,
 *   I would have this section scrollable (currently leaving scroll out for simplicity)
 * 2. buttonSection: Section for sticky buttons anchored to the bottom of the dialog
 * - When increasing font-size of the device, such that the content gets bigger than the screen,
 *   I want this section to stick to the bottom (as opposed to be scrollable)
 * (In my real example, there are more items in each section. Hence, the use of [Box])
 */
@Preview(widthDp = 270, heightDp = 320, backgroundColor = 0xFFFFFF, showBackground = true)
@Composable
fun TestDialogContent() {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val (textSection, buttonSection) = createRefs()
        Column(
            modifier = Modifier.constrainAs(textSection) {
                top.linkTo(parent.top, margin = 48.dp)
                bottom.linkTo(buttonSection.top, margin = 16.dp)
                start.linkTo(parent.start, margin = 24.dp)
                end.linkTo(parent.end, margin = 24.dp)
                width = Dimension.matchParent
                /**
                 * I want this to wrap the height of the entire [Text]
                 * Instead, I'm only seeing a cropped section of the [Text]
                 * I've also tried:
                 * - Dimension.preferredWrapContent.atLeastWrapContent
                 * - Dimension.fillToConstraints.atLeastWrapContent
                 * - Dimension.preferredWrapContent
                 */
                height = Dimension.preferredWrapContent
            }
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                text = "Lorem ipsum dolor sit amet",
                style = MaterialTheme.typography.h5,
                color =  Color.Gray,
                fontWeight = FontWeight.Medium,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = LOREM_IPSUM_LONG,
                style = MaterialTheme.typography.body2,
                color = Color.Gray
            )
        }

        Box(
            modifier = Modifier.constrainAs(buttonSection) {
                bottom.linkTo(parent.bottom, margin = 48.dp)
                start.linkTo(parent.start, margin = 24.dp)
                end.linkTo(parent.end, margin = 24.dp)
                width = Dimension.matchParent
                height = Dimension.wrapContent
            }
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                onClick = {  },
                shape = RoundedCornerShape(30.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
            ) {
                Text(text = "BUTTON", textAlign = TextAlign.Center)
            }
        }
    }
}

