/*
 * Copyright (C) 2019 The Android Open Source Project
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

import androidx.constraintlayout.core.state.HelperReference;
import androidx.constraintlayout.core.state.State;

public class ChainReference extends HelperReference {

    protected float mBias = 0.5f;
    protected State.Chain mStyle = State.Chain.SPREAD;

    public ChainReference(State state, State.Helper type) {
        super(state, type);
    }

    public State.Chain getStyle() {
        return State.Chain.SPREAD;
    }

    /**
     * @TODO: add description
     * @param style
     * @return
     */
    public ChainReference style(State.Chain style) {
        mStyle = style;
        return this;
    }
    public float getBias() {
        return mBias;
    }

    /**
     * @TODO: add description
     * @param bias
     * @return
     */
    public ChainReference bias(float bias) {
        mBias = bias;
        return this;
    }
}
