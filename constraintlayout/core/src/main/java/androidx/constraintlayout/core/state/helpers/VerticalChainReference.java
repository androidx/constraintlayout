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

import androidx.constraintlayout.core.widgets.ConstraintWidget;
import androidx.constraintlayout.core.state.ConstraintReference;
import androidx.constraintlayout.core.state.State;

public class VerticalChainReference extends ChainReference {

    private Object mTopToTop;
    private Object mTopToBottom;
    private Object mBottomToTop;
    private Object mBottomToBottom;

    public VerticalChainReference(State state) {
        super(state, State.Helper.VERTICAL_CHAIN);
    }

    public void apply() {
        ConstraintReference first = null;
        ConstraintReference previous = null;
        for (Object key : mReferences) {
            ConstraintReference reference = mState.constraints(key);
            reference.clearVertical();
        }

        for (Object key : mReferences) {
            ConstraintReference reference = mState.constraints(key);
            if (first == null) {
                first = reference;
                if (mTopToTop != null) {
                    first.topToTop(mTopToTop);
                } else if (mTopToBottom != null) {
                    first.topToBottom(mTopToBottom);
                } else {
                    first.topToTop(State.PARENT);
                }
            }
            if (previous != null) {
                previous.bottomToTop(reference.getKey());
                reference.topToBottom(previous.getKey());
            }
            previous = reference;
        }

        if (previous != null) {
            if (mBottomToTop != null) {
                previous.bottomToTop(mBottomToTop);
            } else if (mBottomToBottom != null) {
                previous.bottomToBottom(mBottomToBottom);
            } else {
                previous.bottomToBottom(State.PARENT);
            }
        }

        if (first != null && mBias != 0.5f) {
            first.verticalBias(mBias);
        }

        switch (mStyle) {
            case SPREAD: {
                first.setVerticalChainStyle(ConstraintWidget.CHAIN_SPREAD);
            } break;
            case SPREAD_INSIDE: {
                first.setVerticalChainStyle(ConstraintWidget.CHAIN_SPREAD_INSIDE);
            } break;
            case PACKED: {
                first.setVerticalChainStyle(ConstraintWidget.CHAIN_PACKED);
            }
        }
    }

    public void topToTop(Object target) { mTopToTop = target; }
    public void topToBottom(Object target) { mTopToBottom = target; }
    public void bottomToTop(Object target) { mBottomToTop = target; }
    public void bottomToBottom(Object target) { mBottomToBottom = target; }
}
