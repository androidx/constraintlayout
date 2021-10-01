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

data class MailEntryInfo(
    val id: Int,
    val from: Contact,
    val timestamp: String = "time",
    val subject: String,
    val shortContent: String
) {
    companion object {
        val Default = MailEntryInfo(
            id = -1,
            from = Contact.Default,
            timestamp = "time",
            subject = "Subject",
            shortContent = "Brief content of mail"
        )
    }
}

data class Contact(
    val name: String,
    val profilePic: Uri,
    val email: String,
    val phone: String
) {
    companion object {
        val Default = Contact(
            name = "name",
            profilePic = Uri.parse("android.resource://com.example.composemail/drawable/avatar_1"),
            email = "email",
            phone = "123 456 789"
        )
    }
}