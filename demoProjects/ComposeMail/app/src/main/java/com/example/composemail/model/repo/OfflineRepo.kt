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

import android.content.ContentResolver
import android.content.res.Resources
import android.net.Uri
import android.util.Log
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import com.example.composemail.R
import com.example.composemail.model.data.Attachment
import com.example.composemail.model.data.Contact
import com.example.composemail.model.data.MailInfoFull
import com.example.composemail.model.data.MailInfoPeek
import kotlinx.coroutines.delay
import java.time.Instant

private const val TAG = "OfflineRepo"

private const val DELAY_PER_MAIL_MS = 100L

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

private val fileExtensions = listOf(
    "png",
    "mp3",
    "mp4",
    "pdf"
)

/**
 * LoremIpsum deconstructed into words without line breaks.
 */
private val loremIpsumWords = LoremIpsum(100).values.first().filter { it != '\n' }.split(" ")

class OfflineRepository(
    private val resources: Resources
) : MailRepository {
    private val loadedMails: MutableMap<Int, MailInfoFull> = mutableMapOf()

    private var isFirstRequest = true

    private var currentId = 0

    private var lastTime = Instant.now().epochSecond

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
        // Consider it something similar as to establishing a connection with a Mail API, where you
        // might need to authenticate or verify tokens, start a session, etc.
        TODO("Not yet implemented")
    }

    private var pageCounter = 0

    override suspend fun getNextSetOfConversations(amount: Int): MailConversationsResponse {
        val conversations = ArrayList<MailInfoPeek>(amount)

        for (i in 0..amount) {
            val newMail = createNewMailWithThread()
            loadedMails[newMail.id] = newMail
            conversations.add(i, newMail.toMailInfoPeek())
            delay(DELAY_PER_MAIL_MS)
        }
        isFirstRequest = false
        return MailConversationsResponse(
            conversations,
            pageCounter++
        )
    }

    @Suppress("RedundantNullableReturnType") // Inherited nullability
    override suspend fun getFullMail(id: Int): MailInfoFull? {
        // TODO: Add delay?
        return loadedMails[id] ?: kotlin.run {
            Log.w(TAG, "findMail: no mails with id = $id")
            MailInfoFull.Default
        }
    }

    private fun createNewTimestamp(): Instant {
        val range = IntRange(1800, 3600 * 4)
        lastTime -= range.random()
        return Instant.ofEpochSecond(lastTime)
    }

    private fun createNewMailWithThread(): MailInfoFull {
        val previousMailId: Int? = if (loadedMails.size > 1) {
            null // TODO: Add a logic to create Threads between Mails
        } else {
            null
        }
        val name = names.random()
        val attachments = mutableListOf<Attachment>()
        for (i in 0 until IntRange(0, 4).random()) {
            attachments.add(
                Attachment(
                    fileName = "myFile" + (i + 1) + "." + fileExtensions.random(),
                    uri = Uri.EMPTY
                )
            )
        }
        return MailInfoFull(
            id = currentId++,
            from = Contact(
                name = "$name Smith",
                profilePic = randomSampleImageUri(),
                email = "$name@smith.com",
                phone = "123 456 789"
            ),
            to = listOf(Contact.Me),
            timestamp = createNewTimestamp(),
            subject = "Mail Subject",
            content = generateRandomContent(),
            previousMailId = previousMailId,
            attachments = attachments
        )
    }

    private fun generateRandomContent(): String {
        // 10 to 200 words
        val wordCount = IntRange(10, (200).coerceAtMost(loremIpsumWords.size)).random()

        // Pick a random offset from available words
        val wordOffset = IntRange(0, loremIpsumWords.size - wordCount).random()

        // Rebuild it into a continuous String
        return loremIpsumWords.subList(wordOffset, wordOffset + wordCount).joinToString(" ")
    }

    private fun randomSampleImageUri(): Uri {
        val pictureId = samplePictures.random()
        return Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(resources.getResourcePackageName(pictureId))
            .appendPath(resources.getResourceTypeName(pictureId))
            .appendPath(resources.getResourceEntryName(pictureId))
            .build()
    }

    private fun MailInfoFull.toMailInfoPeek(): MailInfoPeek =
        MailInfoPeek(
            id = this.id,
            from = this.from,
            timestamp = this.timestamp,
            subject = this.subject,
            // Shorten to up to 20 words
            shortContent = this.content.split(" ").take(20).joinToString(" ")
        )
}