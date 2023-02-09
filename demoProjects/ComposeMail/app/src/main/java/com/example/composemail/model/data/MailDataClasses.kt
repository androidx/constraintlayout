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

package com.example.composemail.model.data

import android.net.Uri
import java.time.Instant

data class MailInfoPeek(
    val id: Int,
    val from: Contact,
    val timestamp: Instant,
    val subject: String,
    val shortContent: String
) {
    companion object {
        val Default = MailInfoPeek(
            id = -1,
            from = Contact.Default,
            timestamp = Instant.now(),
            subject = "Subject",
            shortContent = "Brief content of mail"
        )
    }
}

data class MailInfoFull(
    val id: Int,
    val from: Contact,
    val to: List<Contact>,
    val timestamp: Instant,
    val subject: String,
    val content: String,
    val previousMailId: Int?,
    val attachments: List<Attachment>
) {
    // IDs are guaranteed to be unique, no need to use everything else for equals/hash
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MailInfoFull

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }

    companion object {
        val Default = MailInfoFull(
            id = -1,
            from = Contact.Default,
            to = listOf(Contact.Me),
            timestamp = Instant.now(),
            subject = "Subject",
            content = "Full mail content.",
            previousMailId = null,
            attachments = listOf(
                Attachment(
                    fileName = "myFile.png",
                    uri = Uri.EMPTY
                )
            )
        )
    }
}

data class Attachment(
    val fileName: String,
    val uri: Uri
) {
    val nameWithoutExtension: String = fileName.substringBefore(".")

    val extension: String = fileName.substringAfter(".")
}

data class Contact(
    val name: String,
    val profilePic: Uri,
    val email: String,
    val phone: String
) {
    companion object {
        val Default = Contact(
            name = "John Doe",
            profilePic = Uri.parse("android.resource://com.example.composemail/drawable/avatar_1"),
            email = "johndoe@example.com",
            phone = "123 456 789"
        )

        val Me = Contact(
            name = "Me",
            profilePic = Uri.parse("android.resource://com.example.composemail/drawable/ic_no_profile_pic"),
            email = "me@example.com",
            phone = "987 654 321"
        )
    }
}