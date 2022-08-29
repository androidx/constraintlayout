/*
 * Copyright (C) 2022 The Android Open Source Project
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
package androidx.constraintlayout.core.state.helpers;

import static androidx.constraintlayout.core.widgets.ConstraintWidget.VERTICAL;

import androidx.constraintlayout.core.state.State;

/**
 * The VerticalFlowReference class can be used to store the relevant properties of a Flow Helper
 * when parsing the Flow Helper information in a JSON representation
 */
public class VerticalFlowReference extends FlowReference {
    public VerticalFlowReference(State state) {
        super(state, State.Helper.VERTICAL_FLOW);
        mOrientation = VERTICAL;
    }
}
