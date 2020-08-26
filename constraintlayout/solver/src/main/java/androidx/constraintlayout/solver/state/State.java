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

package androidx.constraintlayout.solver.state;

import androidx.constraintlayout.solver.state.Reference;

import androidx.constraintlayout.solver.widgets.Barrier;
import androidx.constraintlayout.solver.widgets.ConstraintWidget;
import androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer;
import androidx.constraintlayout.solver.widgets.Flow;
import androidx.constraintlayout.solver.widgets.HelperWidget;
import androidx.constraintlayout.solver.state.helpers.AlignHorizontallyReference;
import androidx.constraintlayout.solver.state.helpers.AlignVerticallyReference;
import androidx.constraintlayout.solver.state.helpers.BarrierReference;
import androidx.constraintlayout.solver.state.helpers.GuidelineReference;
import androidx.constraintlayout.solver.state.helpers.VerticalChainReference;
import androidx.constraintlayout.solver.state.helpers.HorizontalChainReference;

import java.util.HashMap;

/**
 * Represents a full state of a ConstraintLayout
 */
public class State {

    protected HashMap<Object, Reference> mReferences = new HashMap<>();
    protected HashMap<Object, HelperReference> mHelperReferences = new HashMap<>();

    final static int UNKNOWN = -1;
    final static int CONSTRAINT_SPREAD = 0;
    final static int CONSTRAINT_WRAP = 1;
    final static int CONSTRAINT_RATIO = 2;

    public final static Integer PARENT = 0;

    public final ConstraintReference mParent = new ConstraintReference(this);

    public enum Constraint {
        LEFT_TO_LEFT,
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT,
        RIGHT_TO_RIGHT,
        START_TO_START,
        START_TO_END,
        END_TO_START,
        END_TO_END,
        TOP_TO_TOP,
        TOP_TO_BOTTOM,
        BOTTOM_TO_TOP,
        BOTTOM_TO_BOTTOM,
        BASELINE_TO_BASELINE,
        CENTER_HORIZONTALLY,
        CENTER_VERTICALLY
    }

    public enum Direction {
        LEFT,
        RIGHT,
        START,
        END,
        TOP,
        BOTTOM
    }

    public enum Helper {
        HORIZONTAL_CHAIN,
        VERTICAL_CHAIN,
        ALIGN_HORIZONTALLY,
        ALIGN_VERTICALLY,
        BARRIER,
        LAYER,
        FLOW
    }

    public enum Chain {
        SPREAD,
        SPREAD_INSIDE,
        PACKED
    }

    public State() {
        mReferences.put(PARENT, mParent);
    }

    public void reset() {
        mHelperReferences.clear();
    }

    /**
     * Implements a conversion function for values, returning int.
     * This can be used in case values (e.g. margins) are represented
     * via an object, not directly an int.
     *
     * @param value the object to convert from
     * @return
     */
    public int convertDimension(Object value) {
        if (value instanceof Float) {
            return ((Float) value).intValue();
        }
        if (value instanceof Integer) {
            return ((Integer) value).intValue();
        }
        return 0;
    }

    /**
     * Create a new reference given a key.
     *
     * @param key
     * @return
     */
    public ConstraintReference createConstraintReference(Object key) {
        return new ConstraintReference(this);
    }

    public State width(Dimension dimension) {
        return setWidth(dimension);
    }

    public State height(Dimension dimension) {
        return setHeight(dimension);
    }

    public State setWidth(Dimension dimension) {
        mParent.setWidth(dimension);
        return this;
    }

    public State setHeight(Dimension dimension) {
        mParent.setHeight(dimension);
        return this;
    }

    Reference reference(Object key) {
        return mReferences.get(key);
    }

    public ConstraintReference constraints(Object key) {
        Reference reference = mReferences.get(key);
        if (reference == null) {
            reference = createConstraintReference(key);
            mReferences.put(key, reference);
            reference.setKey(key);
        }
        if (reference instanceof ConstraintReference) {
            return (ConstraintReference) reference;
        }
        return null;
    }

    private int numHelpers = 0;
    private String createHelperKey() {
        return "__HELPER_KEY_" + numHelpers++ + "__";
    }

    public HelperReference helper(Object key, State.Helper type) {
        if (key == null) {
            key = createHelperKey();
        }
        HelperReference reference = mHelperReferences.get(key);
        if (reference == null) {
            switch (type) {
                case HORIZONTAL_CHAIN: {
                    reference = new HorizontalChainReference(this);
                } break;
                case VERTICAL_CHAIN: {
                    reference = new VerticalChainReference(this);
                } break;
                case ALIGN_HORIZONTALLY: {
                    reference = new AlignHorizontallyReference(this);
                } break;
                case ALIGN_VERTICALLY: {
                    reference = new AlignVerticallyReference(this);
                } break;
                case BARRIER: {
                    reference = new BarrierReference(this);
                } break;
                default: {
                    reference = new HelperReference(this, type);
                }
            }
            mHelperReferences.put(key, reference);
        }
        return reference;
    }

    public GuidelineReference horizontalGuideline(Object key) {
        return guideline(key, ConstraintWidget.HORIZONTAL);
    }

    public GuidelineReference verticalGuideline(Object key) {
        return guideline(key, ConstraintWidget.VERTICAL);
    }

    public GuidelineReference guideline(Object key, int orientation) {
        Reference reference = mReferences.get(key);
        if (reference == null) {
            GuidelineReference guidelineReference = new GuidelineReference(this);
            guidelineReference.setOrientation(orientation);
            guidelineReference.setKey(key);
            mReferences.put(key, guidelineReference);
            reference = guidelineReference;
        }
        return (GuidelineReference) reference;
    }

    public BarrierReference barrier(Object key, Direction direction) {
        BarrierReference reference = (BarrierReference) helper(key, Helper.BARRIER);
        reference.setBarrierDirection(direction);
        return reference;
    }

    public VerticalChainReference verticalChain(Object... references) {
        VerticalChainReference reference = (VerticalChainReference) helper(null, State.Helper.VERTICAL_CHAIN);
        reference.add(references);
        return reference;
    }

    public HorizontalChainReference horizontalChain(Object... references) {
        HorizontalChainReference reference = (HorizontalChainReference) helper(null, Helper.HORIZONTAL_CHAIN);
        reference.add(references);
        return reference;
    }

    public AlignHorizontallyReference centerHorizontally(Object... references) {
        AlignHorizontallyReference reference = (AlignHorizontallyReference) helper(null, Helper.ALIGN_HORIZONTALLY);
        reference.add(references);
        return reference;
    }

    public AlignVerticallyReference centerVertically(Object... references) {
        AlignVerticallyReference reference = (AlignVerticallyReference) helper(null, Helper.ALIGN_VERTICALLY);
        reference.add(references);
        return reference;
    }

    public void directMapping() {
        for (Object key : mReferences.keySet()) {
            ConstraintReference reference = constraints(key);
            reference.setView(key);
        }
    }

    public void map(Object key, Object view) {
        ConstraintReference reference = constraints(key);
        reference.setView(view);
    }

    public void apply(ConstraintWidgetContainer container) {
        container.removeAllChildren();
        mParent.getWidth().apply(this, container, ConstraintWidget.HORIZONTAL);
        mParent.getHeight().apply(this, container, ConstraintWidget.VERTICAL);
        for (Object key : mHelperReferences.keySet()) {
            HelperReference reference = mHelperReferences.get(key);
            HelperWidget helperWidget = reference.getHelperWidget();
            if (helperWidget != null) {
                Reference constraintReference = mReferences.get(key);
                if (constraintReference == null) {
                    constraintReference = constraints(key);
                }
                constraintReference.setConstraintWidget(helperWidget);
            }
        }
        for (Object key : mReferences.keySet()) {
            Reference reference = mReferences.get(key);
            if (reference != mParent) {
                ConstraintWidget widget = reference.getConstraintWidget();
                widget.setParent(null);
                if (reference instanceof GuidelineReference) {
                    // we apply Guidelines first to correctly setup their ConstraintWidget.
                    reference.apply();
                }
                container.add(widget);
            } else {
                reference.setConstraintWidget(container);
            }
        }
        for (Object key : mHelperReferences.keySet()) {
            HelperReference reference = mHelperReferences.get(key);
            HelperWidget helperWidget = reference.getHelperWidget();
            if (helperWidget != null) {
                for (Object keyRef : reference.mReferences) {
                    Reference constraintReference = mReferences.get(keyRef);
                    reference.getHelperWidget().add(constraintReference.getConstraintWidget());
                }
                reference.apply();
            }
        }
        for (Object key : mReferences.keySet()) {
            Reference reference = mReferences.get(key);
            reference.apply();
        }
    }
}