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

package org.constraintlayout.swing.core.motion.model;

import androidx.constraintlayout.core.parser.CLElement;
import androidx.constraintlayout.core.parser.CLKey;
import androidx.constraintlayout.core.parser.CLObject;
import androidx.constraintlayout.core.parser.CLParsingException;
import org.constraintlayout.swing.core.ConstraintLayoutState;
import org.constraintlayout.swing.core.ConstraintSetParser;

import java.util.HashMap;

public class ConstraintSetModel {
    String mName;
    ConstraintLayoutState mState;
    private ConstraintSetModel(String name , ConstraintLayoutState state) {
        mState = state;
        mName = name;
    }
    public static void parse(CLObject json, HashMap<String, ConstraintSetModel> constraintSets) throws CLParsingException {
        int size = json.size();

        ConstraintSetParser parser = new ConstraintSetParser();
        for (int j = 0; j < size; j++) {
            CLKey clkey = ((CLKey) json.get(j));
            String type = clkey.content();
            CLElement value = clkey.getValue();
            ConstraintLayoutState state = new  ConstraintLayoutState();
            parser.parse((CLObject)value,state);
            constraintSets.put(type, new ConstraintSetModel(type, state));
        }

    }
}
