/*
 * Copyright 2021 The Android Open Source Project
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

package androidx.constraintLayout.desktop.scan

import java.awt.Color

class Theme(isDarkMode: Boolean) {

    private val darkMode = isDarkMode

    fun backgroundColor(): Color {
        if (darkMode) {
            return Color(0, 0, 60)
        }
        return Color(200, 200, 200)
    }

    fun rootBackgroundColor(): Color {
        if (darkMode) {
            return Color(0, 0, 80)
        }
        return Color(255, 255, 255)
    }

    fun startColor(): Color {
        if (darkMode) {
            return Color(0, 140, 240, 100)
        }
        return Color(0, 50, 100, 100)
    }

    fun endColor(): Color {
        if (darkMode) {
            return Color(0, 200, 240, 100)
        }
        return Color(34, 125, 239, 100)
    }

    fun interpolatedColor(): Color {
        if (darkMode) {
            return Color(140, 140, 240, 100)
        }
        return Color(0, 90, 200, 40)
    }

    fun textColor(): Color {
        if (darkMode) {
            return Color(200, 200, 200)
        }
        return Color(0, 90, 200, 200)
    }

    fun pathColor(): Color {
        if (darkMode) {
            return Color(200, 200, 200)
        }
        return Color(0, 90, 200, 240)
    }
}