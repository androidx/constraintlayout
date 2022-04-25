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

package androidx.constraintlayout.core.state;

import androidx.constraintlayout.core.state.helpers.AlignHorizontallyReference;
import androidx.constraintlayout.core.state.helpers.AlignVerticallyReference;
import androidx.constraintlayout.core.state.helpers.BarrierReference;
import androidx.constraintlayout.core.state.helpers.GuidelineReference;
import androidx.constraintlayout.core.state.helpers.HorizontalChainReference;
import androidx.constraintlayout.core.state.helpers.VerticalChainReference;
import androidx.constraintlayout.core.widgets.ConstraintWidget;
import androidx.constraintlayout.core.widgets.ConstraintWidgetContainer;
import androidx.constraintlayout.core.widgets.HelperWidget;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a full state of a ConstraintLayout
 */
public class State {
    private CorePixelDp mDpToPixel;
    protected HashMap<Object, Reference> mReferences = new HashMap<>();
    protected HashMap<Object, HelperReference> mHelperReferences = new HashMap<>();
    HashMap<String, ArrayList<String>> mTags = new HashMap<>();

    static final int UNKNOWN = -1;
    static final int CONSTRAINT_SPREAD = 0;
    static final int CONSTRAINT_WRAP = 1;
    static final int CONSTRAINT_RATIO = 2;

    public static final Integer PARENT = 0;

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
        BASELINE_TO_TOP,
        BASELINE_TO_BOTTOM,
        CENTER_HORIZONTALLY,
        CENTER_VERTICALLY,
        CIRCULAR_CONSTRAINT
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

    CorePixelDp getDpToPixel() {
        return mDpToPixel;
    }

    /**
     * Set the function that converts dp to Pixels
     * @param dpToPixel
     */
    public void setDpToPixel(CorePixelDp dpToPixel) {
        this.mDpToPixel = dpToPixel;
    }

    /**
     * Clear the state
     */
    public void reset() {
        for (Object ref : mReferences.keySet()) {
            mReferences.get(ref).getConstraintWidget().reset();
        }
        mReferences.clear();
        mReferences.put(PARENT, mParent);
        mHelperReferences.clear();
        mTags.clear();
        mBaselineNeeded.clear();
        mDirtyBaselineNeededWidgets = true;
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
            return (Integer) value;
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

    /**
     * @TODO: add description
     * @param width
     * @return
     */
    public boolean sameFixedWidth(int width) {
        return mParent.getWidth().equalsFixedValue(width);
    }

    /**
     * @TODO: add description
     * @param height
     * @return
     */
    public boolean sameFixedHeight(int height) {
        return mParent.getHeight().equalsFixedValue(height);
    }

    /**
     * @TODO: add description
     * @param dimension
     * @return
     */
    public State width(Dimension dimension) {
        return setWidth(dimension);
    }

    /**
     * @TODO: add description
     * @param dimension
     * @return
     */
    public State height(Dimension dimension) {
        return setHeight(dimension);
    }

    /**
     * @TODO: add description
     * @param dimension
     * @return
     */
    public State setWidth(Dimension dimension) {
        mParent.setWidth(dimension);
        return this;
    }

    /**
     * @TODO: add description
     * @param dimension
     * @return
     */
    public State setHeight(Dimension dimension) {
        mParent.setHeight(dimension);
        return this;
    }

    Reference reference(Object key) {
        return mReferences.get(key);
    }

    /**
     * @TODO: add description
     * @param key
     * @return
     */
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

    private int mNumHelpers = 0;
    private String createHelperKey() {
        return "__HELPER_KEY_" + mNumHelpers++ + "__";
    }

    /**
     * @TODO: add description
     * @param key
     * @param type
     * @return
     */
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
            reference.setKey(key);
            mHelperReferences.put(key, reference);
        }
        return reference;
    }

    /**
     * @TODO: add description
     * @param key
     * @return
     */
    public GuidelineReference horizontalGuideline(Object key) {
        return guideline(key, ConstraintWidget.HORIZONTAL);
    }

    /**
     * @TODO: add description
     * @param key
     * @return
     */
    public GuidelineReference verticalGuideline(Object key) {
        return guideline(key, ConstraintWidget.VERTICAL);
    }

    /**
     * @TODO: add description
     * @param key
     * @param orientation
     * @return
     */
    public GuidelineReference guideline(Object key, int orientation) {
        ConstraintReference reference = constraints(key);
        if (reference.getFacade() == null
                || !(reference.getFacade() instanceof GuidelineReference)) {
            GuidelineReference guidelineReference = new GuidelineReference(this);
            guidelineReference.setOrientation(orientation);
            guidelineReference.setKey(key);
            reference.setFacade(guidelineReference);
        }
        return (GuidelineReference) reference.getFacade();
    }

    /**
     * @TODO: add description
     * @param key
     * @param direction
     * @return
     */
    public BarrierReference barrier(Object key, Direction direction) {
        ConstraintReference reference = constraints(key);
        if (reference.getFacade() == null || !(reference.getFacade() instanceof BarrierReference)) {
            BarrierReference barrierReference = new BarrierReference(this);
            barrierReference.setBarrierDirection(direction);
            reference.setFacade(barrierReference);
        }
        return (BarrierReference) reference.getFacade();
    }

    /**
     * @TODO: add description
     * @return
     */
    public VerticalChainReference verticalChain() {
        return (VerticalChainReference) helper(null, Helper.VERTICAL_CHAIN);
    }

    /**
     * @TODO: add description
     * @param references
     * @return
     */
    public VerticalChainReference verticalChain(Object... references) {
        VerticalChainReference reference =
                (VerticalChainReference) helper(null, State.Helper.VERTICAL_CHAIN);
        reference.add(references);
        return reference;
    }

    /**
     * @TODO: add description
     * @return
     */
    public HorizontalChainReference horizontalChain() {
        return (HorizontalChainReference) helper(null, Helper.HORIZONTAL_CHAIN);
    }

    /**
     * @TODO: add description
     * @param references
     * @return
     */
    public HorizontalChainReference horizontalChain(Object... references) {
        HorizontalChainReference reference =
                (HorizontalChainReference) helper(null, Helper.HORIZONTAL_CHAIN);
        reference.add(references);
        return reference;
    }

    /**
     * @TODO: add description
     * @param references
     * @return
     */
    public AlignHorizontallyReference centerHorizontally(Object... references) {
        AlignHorizontallyReference reference =
                (AlignHorizontallyReference) helper(null, Helper.ALIGN_HORIZONTALLY);
        reference.add(references);
        return reference;
    }

    /**
     * @TODO: add description
     * @param references
     * @return
     */
    public AlignVerticallyReference centerVertically(Object... references) {
        AlignVerticallyReference reference =
                (AlignVerticallyReference) helper(null, Helper.ALIGN_VERTICALLY);
        reference.add(references);
        return reference;
    }

    /**
     * @TODO: add description
     */
    public void directMapping() {
        for (Object key : mReferences.keySet()) {
            Reference ref = constraints(key);
            if (!(ref instanceof ConstraintReference)) {
                continue;
            }
            ConstraintReference reference = (ConstraintReference) ref;
            reference.setView(key);
        }
    }

    /**
     * @TODO: add description
     * @param key
     * @param view
     */
    public void map(Object key, Object view) {
        Reference ref = constraints(key);
        if (ref instanceof ConstraintReference) {
            ConstraintReference reference = (ConstraintReference) ref;
            reference.setView(view);
        }
    }

    /**
     * @TODO: add description
     * @param key
     * @param tag
     */
    public void setTag(String key, String tag) {
        Reference ref = constraints(key);
        if (ref instanceof ConstraintReference) {
            ConstraintReference reference = (ConstraintReference) ref;
            reference.setTag(tag);
            ArrayList<String> list = null;
            if (!mTags.containsKey(tag)) {
                list = new ArrayList<>();
                mTags.put(tag, list);
            } else {
                list = mTags.get(tag);
            }
            list.add(key);
        }
    }

    /**
     * @TODO: add description
     * @param tag
     * @return
     */
    public ArrayList<String> getIdsForTag(String tag) {
        if (mTags.containsKey(tag)) {
            return mTags.get(tag);
        }
        return null;
    }

    /**
     * @TODO: add description
     * @param container
     */
    public void apply(ConstraintWidgetContainer container) {
        container.removeAllChildren();
        mParent.getWidth().apply(this, container, ConstraintWidget.HORIZONTAL);
        mParent.getHeight().apply(this, container, ConstraintWidget.VERTICAL);
        // add helper refrences
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
            if (reference != mParent && reference.getFacade() instanceof HelperReference) {
                HelperWidget helperWidget =
                        ((HelperReference) reference.getFacade()).getHelperWidget();
                if (helperWidget != null) {
                    Reference constraintReference = mReferences.get(key);
                    if (constraintReference == null) {
                        constraintReference = constraints(key);
                    }
                    constraintReference.setConstraintWidget(helperWidget);
                }
            }
        }
        for (Object key : mReferences.keySet()) {
            Reference reference = mReferences.get(key);
            if (reference != mParent) {
                ConstraintWidget widget = reference.getConstraintWidget();
                widget.setDebugName(reference.getKey().toString());
                widget.setParent(null);
                if (reference.getFacade() instanceof GuidelineReference) {
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
            } else {
                reference.apply();
            }
        }
        for (Object key : mReferences.keySet()) {
            Reference reference = mReferences.get(key);
            if (reference != mParent && reference.getFacade() instanceof HelperReference) {
                HelperReference helperReference = (HelperReference) reference.getFacade();
                HelperWidget helperWidget = helperReference.getHelperWidget();
                if (helperWidget != null) {
                    for (Object keyRef : helperReference.mReferences) {
                        Reference constraintReference = mReferences.get(keyRef);
                        if (constraintReference != null) {
                            helperWidget.add(constraintReference.getConstraintWidget());
                        } else if (keyRef instanceof Reference) {
                            helperWidget.add(((Reference) keyRef).getConstraintWidget());
                        } else {
                            System.out.println("couldn't find reference for " + keyRef);
                        }
                    }
                    reference.apply();
                }
            }
        }
        for (Object key : mReferences.keySet()) {
            Reference reference = mReferences.get(key);
            reference.apply();
            ConstraintWidget widget = reference.getConstraintWidget();
            if (widget != null && key != null) {
                widget.stringId = key.toString();
            }
        }
    }

    // ================= add baseline code================================
    ArrayList<Object> mBaselineNeeded = new ArrayList<>();
    ArrayList<ConstraintWidget> mBaselineNeededWidgets = new ArrayList<>();
    boolean mDirtyBaselineNeededWidgets = true;

    /**
     * Baseline is needed for this object
     * @param id
     */
    public void baselineNeededFor(Object id) {
        mBaselineNeeded.add(id);
        mDirtyBaselineNeededWidgets = true;
    }

    public boolean isBaselineNeeded(ConstraintWidget constraintWidget) {
        if (mDirtyBaselineNeededWidgets) {
            mBaselineNeededWidgets.clear();
            for (Object id : mBaselineNeeded) {
                ConstraintWidget widget = mReferences.get(id).getConstraintWidget();
                if (widget != null) mBaselineNeededWidgets.add(widget);
            }

            mDirtyBaselineNeededWidgets = false;
        }
        return mBaselineNeededWidgets.contains(constraintWidget);
    }
}
