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

package androidx.constraintlayout.solver.state.helpers;

import androidx.constraintlayout.solver.state.ConstraintReference;
import androidx.constraintlayout.solver.state.HelperReference;
import androidx.constraintlayout.solver.state.State;

public class AlignHorizontallyReference extends HelperReference {

    private float mBias = 0.5f;

    private Object mStartToStart;
    private Object mStartToEnd;
    private Object mEndToStart;
    private Object mEndToEnd;

    public AlignHorizontallyReference(State state) {
        super(state, State.Helper.ALIGN_VERTICALLY);
    }

    public void apply() {
        for (Object key : mReferences) {
            ConstraintReference reference = mState.constraints(key);
            reference.clearHorizontal();
            if (mStartToStart != null) {
                reference.startToStart(mStartToStart);
            } else if (mStartToEnd != null) {
                reference.startToEnd(mStartToEnd);
            } else {
                reference.startToStart(State.PARENT);
            }
            if (mEndToStart != null) {
                reference.endToStart(mEndToStart);
            } else if (mEndToEnd != null) {
                reference.endToEnd(mEndToEnd);
            } else {
                reference.endToEnd(State.PARENT);
            }
            if (mBias != 0.5f) {
                reference.horizontalBias(mBias);
            }
        }
    }

    public void startToStart(Object target) { mStartToStart = target; }
    public void startToEnd(Object target) { mStartToEnd = target; }
    public void endToStart(Object target) { mEndToStart = target; }
    public void endToEnd(Object target) { mEndToEnd = target; }

    public void bias(float bias) {
        mBias = bias;
    }
}
