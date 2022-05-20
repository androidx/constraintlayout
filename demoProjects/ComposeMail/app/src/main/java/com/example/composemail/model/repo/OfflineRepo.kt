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

package com.example.composemail.model.repo

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.res.Resources
import android.net.Uri
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import com.example.composemail.R
import com.example.composemail.model.data.Contact
import com.example.composemail.model.data.MailEntryInfo
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date

private val names = listOf(
    "Jacob",
    "Sophia",
    "Noah",
    "Emma",
    "Mason",
    "Isabella",
    "William",
    "Olivia",
    "Ethan",
    "Ava",
    "Liam",
    "Emily",
    "Michael",
    "Abigail",
    "Alexander",
    "Mia",
    "Jayden",
    "Madison",
    "Daniel",
)

private val contentLines = LoremIpsum(100).values.first().filter { it != '\n' }.split(" ")

class OfflineRepository(
    private val resources: Resources
) : MailRepository {
    private var isFirstRequest = true

    private var currentId = 0

    private val startTime = Instant.now().epochSecond

    private var lastTime = startTime

    private val samplePictures: List<Int> =
        listOf(
            R.drawable.avatar_1,
            R.drawable.avatar_2,
            R.drawable.avatar_3,
            R.drawable.avatar_4,
            R.drawable.avatar_5,
            R.drawable.avatar_6,
            R.drawable.avatar_7,
            R.drawable.avatar_8,
            R.drawable.avatar_9,
            R.drawable.avatar_10,
            R.drawable.avatar_11,
            R.drawable.avatar_12,
            R.drawable.avatar_13,
            R.drawable.avatar_14,
            R.drawable.avatar_15,
            R.drawable.avatar_16,
        )

    override suspend fun connect() {
        TODO("Not yet implemented")
    }

    private var pageCounter = 0

    override suspend fun getNextSetOfConversations(amount: Int): MailConversationsResponse {
        val conversations = ArrayList<MailEntryInfo>(amount)
        val delayAmount = if (isFirstRequest) 0L else 200L
        for (i in 0..amount) {
            conversations.add(i, createNewConversation())
            delay(delayAmount)
        }
        isFirstRequest = false
        return MailConversationsResponse(
            conversations,
            pageCounter++
        )
    }

    @SuppressLint("SimpleDateFormat")
    private fun createNewTimestamp(): String {
        val range = IntRange(1800, 3600 * 4)
        lastTime -= range.random()
        return SimpleDateFormat("hh:mma").format(Date.from(Instant.ofEpochSecond(lastTime)))
    }

    private fun createNewConversation(): MailEntryInfo {
        val name = names.random()
        val shortContent = contentLines.shuffled().take(10).joinToString(" ")
        return MailEntryInfo(
            id = currentId++,
            from = Contact(
                name = "$name Smith",
                profilePic = fetchSampleUri(),
                email = "$name@smith.com",
                phone = "123 456 789"
            ),
            timestamp = createNewTimestamp(),
            subject = "Subject of this mail",
            shortContent = shortContent
        )
    }

    private fun fetchSampleUri(): Uri {
        val pictureId = samplePictures.random()
        return Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(resources.getResourcePackageName(pictureId))
            .appendPath(resources.getResourceTypeName(pictureId))
            .appendPath(resources.getResourceEntryName(pictureId))
            .build()
    }
}