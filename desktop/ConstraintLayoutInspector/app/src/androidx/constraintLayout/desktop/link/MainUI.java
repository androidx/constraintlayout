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

package androidx.constraintLayout.desktop.link;

import androidx.constraintlayout.core.parser.CLParsingException;

import javax.swing.text.BadLocationException;

public interface MainUI {
    void addDesign(String s)  throws CLParsingException, BadLocationException;

    void selectKey(String s) throws CLParsingException;

    void clearSelectedKey();

    void addConstraint(String wId, String s);
}
