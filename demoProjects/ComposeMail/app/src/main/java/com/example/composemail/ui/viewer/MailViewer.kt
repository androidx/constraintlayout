/*
 * Copyright (C) 2023 The Android Open Source Project
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

package com.example.composemail.ui.viewer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.node.Ref
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composemail.model.data.Attachment
import com.example.composemail.model.data.MailInfoFull
import com.example.composemail.ui.components.ContactImage
import com.example.composemail.ui.utils.toHourMinutes

@Composable
fun MailViewer(
    modifier: Modifier,
    mailInfoFull: MailInfoFull
) {
    val validMailInfo = remember { Ref<MailInfoFull>().apply { value = mailInfoFull } }

    if (mailInfoFull != MailInfoFull.Default) {
        validMailInfo.value = mailInfoFull
    }
    MailViewerComponent(
        modifier = modifier,
        mailInfoFull = validMailInfo.value!!
    )
}

@Composable
private fun MailViewerComponent(
    modifier: Modifier,
    mailInfoFull: MailInfoFull
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = mailInfoFull.subject,
                style = MaterialTheme.typography.h5,
            )
            Text(
                text = mailInfoFull.timestamp.toHourMinutes(),
                style = MaterialTheme.typography.body2
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ContactImage(
                modifier = Modifier,
                uri = mailInfoFull.from.profilePic,
                onClick = {}
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = mailInfoFull.from.name,
                    fontWeight = FontWeight.Bold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "to",
                        style = MaterialTheme.typography.body2
                    )
                    Text(
                        text = mailInfoFull.to.joinToString(separator = ", ") { it.name },
                        style = MaterialTheme.typography.body2
                    )
                }
            }
        }
        if (mailInfoFull.attachments.isNotEmpty()) {
            AttachmentsView(
                modifier = Modifier.height(40.dp),
                attachments = mailInfoFull.attachments
            )
        }
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = mailInfoFull.content,
            style = MaterialTheme.typography.body1
        )
    }
}

@Composable
private fun AttachmentsView(
    modifier: Modifier,
    attachments: List<Attachment>
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        attachments.forEach {
            val isKnownFileType: Boolean

            // Note: using ImageVector since `rememberAsyncPainter` won't paint project
            // VectorDrawables defined with a URI
            val imageVector: ImageVector
            when (it.extension) {
                "png" -> {
                    isKnownFileType = true
                    imageVector = Icons.Default.Image
                }

                "mp3" -> {
                    isKnownFileType = true
                    imageVector = Icons.Default.AudioFile
                }

                "mp4" -> {
                    isKnownFileType = true
                    imageVector = Icons.Default.VideoFile
                }

                else -> {
                    isKnownFileType = false
                    imageVector = Icons.Default.InsertDriveFile
                }
            }
            Row(
                modifier = Modifier
                    .border(
                        width = 2.dp,
                        color = Color.LightGray,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { }
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isKnownFileType) it.nameWithoutExtension else it.fileName,
                    style = MaterialTheme.typography.caption
                )
                Image(
                    imageVector = imageVector,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface)
                )
            }
        }
    }
}

@Preview
@Composable
private fun MailViewerPreview() {
    MailViewer(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        mailInfoFull = MailInfoFull.Default
    )
}