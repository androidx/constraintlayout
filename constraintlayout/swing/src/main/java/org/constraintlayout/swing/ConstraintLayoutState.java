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

package org.constraintlayout.swing;

public class ConstraintLayoutState {
    public ConstraintsState constraints = new ConstraintsState();
    public GuidelinesState guidelines = new GuidelinesState();

    public void clear() {
        constraints.clear();
        guidelines.clear();
    }

    public String serialize() {
        StringBuilder builder = new StringBuilder();
        builder.append(guidelines.serialize());
        builder.append(constraints.serialize());
        return builder.toString();
    }
}
