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

import static androidx.constraintlayout.core.widgets.ConstraintWidget.HORIZONTAL;
import static androidx.constraintlayout.core.widgets.ConstraintWidget.UNKNOWN;
import static androidx.constraintlayout.core.widgets.analyzer.BasicMeasure.AT_MOST;

import androidx.constraintlayout.core.state.ConstraintReference;
import androidx.constraintlayout.core.state.HelperReference;
import androidx.constraintlayout.core.state.State;
import androidx.constraintlayout.core.widgets.ConstraintAnchor;
import androidx.constraintlayout.core.widgets.Flow;
import androidx.constraintlayout.core.widgets.HelperWidget;

import java.util.HashMap;

/**
 * The FlowReference class can be used to store the relevant properties of a Flow Helper
 * when parsing the Flow Helper information in a JSON representation.
 *
 */
abstract public class FlowReference extends HelperReference {

    public static final int HORIZONTAL_ALIGN_START = 0;
    public static final int HORIZONTAL_ALIGN_END = 1;
    public static final int HORIZONTAL_ALIGN_CENTER = 2;

    public static final int VERTICAL_ALIGN_TOP = 0;
    public static final int VERTICAL_ALIGN_BOTTOM = 1;
    public static final int VERTICAL_ALIGN_CENTER = 2;
    public static final int VERTICAL_ALIGN_BASELINE = 3;

    public static final int WRAP_NONE = 0;
    public static final int WRAP_CHAIN = 1;
    public static final int WRAP_ALIGNED = 2;
    public static final int WRAP_CHAIN_NEW = 3;

    protected Flow mFlow;

    protected HashMap<String, Float> mMapWeights;
    protected HashMap<String, Float> mMapPreMargin;
    protected HashMap<String, Float> mMapPostMargin;

    protected int mWrapMode = WRAP_NONE;

    protected int mVerticalStyle = UNKNOWN;
    protected int mFirstVerticalStyle = UNKNOWN;
    protected int mLastVerticalStyle = UNKNOWN;
    protected int mHorizontalStyle = UNKNOWN;
    protected int mFirstHorizontalStyle = UNKNOWN;
    protected int mLastHorizontalStyle = UNKNOWN;

    protected int mVerticalAlign = HORIZONTAL_ALIGN_CENTER;
    protected int mHorizontalAlign = VERTICAL_ALIGN_CENTER;

    protected int mVerticalGap = 0;
    protected int mHorizontalGap = 0;

    protected int mPadding = 0;

    protected int mMaxElementsWrap = UNKNOWN;

    protected int mOrientation = HORIZONTAL;

    protected float mVerticalBias = 0.5f;
    protected float mFirstVerticalBias = 0.5f;
    protected float mLastVerticalBias = 0.5f;
    protected float mHorizontalBias = 0.5f;
    protected float mFirstHorizontalBias = 0.5f;
    protected float mLastHorizontalBias = 0.5f;

    public FlowReference(State state, State.Helper type) {
        super(state, type);
    }

    /**
     * Relate widgets to the FlowReference
     * @param id id of a widget
     * @param weight weight of a widget
     * @param preMargin preMargin of a widget
     * @param postMargin postMargin of a widget
     */
    public void addFlowElement(String id, float weight, float preMargin, float postMargin) {
        super.add(id);
        if (!Float.isNaN(weight)) {
            if (mMapWeights == null) {
                mMapWeights = new HashMap<>();
            }
            mMapWeights.put(id, weight);
        }
        if (!Float.isNaN(preMargin)) {
            if (mMapPreMargin == null) {
                mMapPreMargin = new HashMap<>();
            }
            mMapPreMargin.put(id, preMargin);
        }
        if (!Float.isNaN(postMargin)) {
            if (mMapPostMargin == null) {
                mMapPostMargin = new HashMap<>();
            }
            mMapPostMargin.put(id, postMargin);
        }
    }

    /**
     * Get the weight of a widget
     * @param id id of a widget
     * @return the weight of a widget
     */
    protected float getWeight(String id) {
        if (mMapWeights == null) {
            return UNKNOWN;
        }
        if (mMapWeights.containsKey(id)) {
            return mMapWeights.get(id);
        }
        return UNKNOWN;
    }

    /**
     * Get the post margin of a widget
     * @param id id id of a widget
     * @return the post margin of a widget
     */
    protected float getPostMargin(String id) {
        if (mMapPreMargin != null  && mMapPreMargin.containsKey(id)) {
            return mMapPreMargin.get(id);
        }
        return 0;
    }

    /**
     * Get the pre margin of a widget
     * @param id id id of a widget
     * @return the pre margin of a widget
     */
    protected float getPreMargin(String id) {
        if (mMapPostMargin != null  && mMapPostMargin.containsKey(id)) {
            return mMapPostMargin.get(id);
        }
        return 0;
    }

    /**
     * Get wrap mode
     * @return wrap mode
     */
    public int getWrapMode() {
        return mWrapMode;
    }

    /**
     * Set wrap Mode
     * @param wrap wrap Mode
     * @return FlowReference (this)
     */
    public FlowReference wrapMode(int wrap) {
        this.mWrapMode = wrap;
        return this;
    }

    /**
     * Get padding
     * @return padding value
     */
    public int getPadding() {
        return mPadding;
    }

    /**
     * Set padding
     * @param padding padding value
     * @return FlowReference (this)
     */
    public FlowReference padding(int padding) {
        this.mPadding = padding;
        return this;
    }

    /**
     * Get vertical style
     * @return vertical style
     */
    public int getVerticalStyle() {
        return mVerticalStyle;
    }

    /**
     * set vertical sytle
     * @param verticalStyle Flow vertical style
     * @return FlowReference (this)
     */
    public FlowReference verticalStyle(int verticalStyle) {
        this.mVerticalStyle = verticalStyle;
        return this;
    }

    /**
     * Get first vertical style
     * @return first vertical style
     */
    public int getFirstVerticalStyle() {
        return mFirstVerticalStyle;
    }

    /**
     * Set first vertical style
     * @param firstVerticalStyle Flow first vertical style
     * @return FlowReference (this)
     */
    public FlowReference firstVerticalStyle(int firstVerticalStyle) {
        this.mFirstVerticalStyle = firstVerticalStyle;
        return this;
    }

    /**
     * Get last vertical style
     * @return last vertical style
     */
    public int getLastVerticalStyle() {
        return mLastVerticalStyle;
    }

    /**
     * Set last vertical style
     * @param lastVerticalStyle Flow last vertical style
     * @return FlowReference (this)
     */
    public FlowReference lastVerticalStyle(int lastVerticalStyle) {
        this.mLastVerticalStyle = lastVerticalStyle;
        return this;
    }

    /**
     * Get horizontal style
     * @return horizontal style
     */
    public int getHorizontalStyle() {
        return mHorizontalStyle;
    }

    /**
     * Set horizontal style
     * @param horizontalStyle Flow horizontal style
     * @return FlowReference (this)
     */
    public FlowReference horizontalStyle(int horizontalStyle) {
        this.mHorizontalStyle = horizontalStyle;
        return this;
    }

    /**
     * Get first horizontal style
     * @return first horizontal style
     */
    public int getFirstHorizontalStyle() {
        return mFirstHorizontalStyle;
    }

    /**
     * Set first horizontal style
     * @param firstHorizontalStyle Flow first horizontal style
     * @return FlowReference (this)
     */
    public FlowReference firstHorizontalStyle(int firstHorizontalStyle) {
        this.mFirstHorizontalStyle = firstHorizontalStyle;
        return this;
    }

    /**
     * Get last horizontal style
     * @return last horizontal style
     */
    public int getLastHorizontalStyle() {
        return mLastHorizontalStyle;
    }

    /**
     * Set last horizontal style
     * @param lastHorizontalStyle Flow last horizontal style
     * @return FlowReference (this)
     */
    public FlowReference lastHorizontalStyle(int lastHorizontalStyle) {
        this.mLastHorizontalStyle = lastHorizontalStyle;
        return this;
    }

    /**
     * Get vertical align
     * @return vertical align value
     */
    public int getVerticalAlign() {
        return mVerticalAlign;
    }

    /**
     * Set vertical align
     * @param verticalAlign vertical align value
     * @return FlowReference (this)
     */
    public FlowReference verticalAlign(int verticalAlign) {
        this.mVerticalAlign = verticalAlign;
        return this;
    }

    /**
     * Get horizontal align
     * @return horizontal align value
     */
    public int getHorizontalAlign() {
        return mHorizontalAlign;
    }

    /**
     * Set horizontal align
     * @param horizontalAlign horizontal align value
     * @return FlowReference (this)
     */
    public FlowReference horizontalAlign(int horizontalAlign) {
        this.mHorizontalAlign = horizontalAlign;
        return this;
    }

    /**
     * Get vertical gap
     * @return vertical gap value
     */
    public int getVerticalGap() {
        return mVerticalGap;
    }

    /**
     * Set vertical gap
     * @param verticalGap vertical gap value
     * @return FlowReference (this)
     */
    public FlowReference verticalGap(int verticalGap) {
        this.mVerticalGap = verticalGap;
        return this;
    }

    /**
     * Get horizontal gap
     * @return horizontal gap value
     */
    public int getHorizontalGap() {
        return mHorizontalGap;
    }

    /**
     * Set horizontal gap
     * @param horizontalGap horizontal gap value
     * @return FlowReference (this)
     */
    public FlowReference horizontalGap(int horizontalGap) {
        mHorizontalGap = horizontalGap;
        return this;
    }

    /**
     * Get max element wrap
     * @return max element wrap value
     */
    public int getMaxElementsWrap() {
        return mMaxElementsWrap;
    }

    /**
     * Set max element wrap
     * @param maxElementsWrap max element wrap value
     * @return FlowReference (this)
     */
    public FlowReference maxElementsWrap(int maxElementsWrap) {
        this.mMaxElementsWrap = maxElementsWrap;
        return this;
    }

    /**
     * Get the orientation of a Flow
     * @return orientation value
     */
    public int getOrientation() {
        return mOrientation;
    }

    /**
     * Set the orientation of a Flow
     * @param mOrientation orientation value
     * @return FlowReference (this)
     */
    public FlowReference orientation(int mOrientation) {
        this.mOrientation = mOrientation;
        return this;
    }

    /**
     * Get vertical bias
     * @return vertical bias value
     */
    public float getVerticalBias() {
        return mVerticalBias;
    }

    /**
     * Set vertical bias value
     * @param verticalBias vertical bias value
     * @return FlowReference (this)
     */
    public FlowReference verticalBias(float verticalBias) {
        this.mVerticalBias = verticalBias;
        return this;
    }

    /**
     * Get first vertical bias
     * @return first vertical bias value
     */
    public float getFirstVerticalBias() {
        return mFirstVerticalBias;
    }

    /**
     * Set first vertical bias
     * @param firstVerticalBias first vertical bias value
     * @return FlowReference (this)
     */
    public FlowReference firstVerticalBias(float firstVerticalBias) {
        this.mFirstVerticalBias = firstVerticalBias;
        return this;
    }

    /**
     * Get last vertical bias
     * @return last vertical bias
     */
    public float getLastVerticalBias() {
        return mLastVerticalBias;
    }

    /**
     * Set last vertical bias
     * @param lastVerticalBias last vertical bias value
     * @return FlowReference (this)
     */
    public FlowReference lastVerticalBias(float lastVerticalBias) {
        this.mLastVerticalBias = lastVerticalBias;
        return this;
    }

    /**
     * Get horizontal bias
     * @return horizontal bias value
     */
    public float getHorizontalBias() {
        return mHorizontalBias;
    }

    /**
     * Set horizontal bias
     * @param horizontalBias horizontal bias value
     * @return FlowReference (this)
     */
    public FlowReference horizontalBias(float horizontalBias) {
        this.mHorizontalBias = horizontalBias;
        return this;
    }

    /**
     * Get first horizontal bias
     * @return first horizontal bias
     */
    public float getFirstHorizontalBias() {
        return mFirstHorizontalBias;
    }

    /**
     * Set first horizontal bias
     * @param firstHorizontalBias first horizontal bias value
     * @return FlowReference (this)
     */
    public FlowReference firstHorizontalBias(float firstHorizontalBias) {
        this.mFirstHorizontalBias = firstHorizontalBias;
        return this;
    }

    /**
     * Get last horizontal bias
     * @return last horizontal bias value
     */
    public float getLastHorizontalBias() {
        return mLastHorizontalBias;
    }

    /**
     * Set last horizontal bias
     * @param lastHorizontalBias last horizontal bias value
     * @return FlowReference (this)
     */
    public FlowReference lastHorizontalBias(float lastHorizontalBias) {
        this.mLastHorizontalBias = lastHorizontalBias;
        return this;
    }

    @Override
    public HelperWidget getHelperWidget() {
        if (mFlow == null) {
            mFlow = new Flow();
        }
        return mFlow;
    }

    @Override
    public void setHelperWidget(HelperWidget widget) {
        if (widget instanceof Flow) {
            mFlow = (Flow) widget;
        } else {
            mFlow = null;
        }
    }

    @Override
    public void apply() {
        getHelperWidget();
        mFlow.setOrientation(mOrientation);
        mFlow.setWrapMode(mWrapMode);

        if (mMaxElementsWrap != UNKNOWN) {
            mFlow.setMaxElementsWrap(mMaxElementsWrap);
        }
        if (mPadding != 0) {
            mFlow.setPadding(mPadding);
        }

        // Gap
        if (mHorizontalGap != 0) {
            mFlow.setHorizontalGap(mHorizontalGap);
        }
        if (mVerticalGap != 0) {
            mFlow.setVerticalGap(mVerticalGap);
        }

        // Bias
        if (mHorizontalBias != 0.5f) {
            mFlow.setHorizontalBias(mHorizontalBias);
        }
        if (mFirstHorizontalBias != 0.5f) {
            mFlow.setFirstHorizontalBias(mFirstHorizontalBias);
        }
        if (mLastHorizontalBias != 0.5f) {
            mFlow.setLastHorizontalBias(mLastHorizontalBias);
        }
        if (mVerticalBias != 0.5f) {
            mFlow.setVerticalBias(mVerticalBias);
        }
        if (mFirstVerticalBias != 0.5f) {
            mFlow.setFirstVerticalBias(mFirstVerticalBias);
        }
        if (mLastVerticalBias != 0.5f) {
            mFlow.setLastVerticalBias(mLastVerticalBias);
        }

        // Align
        if (mHorizontalAlign != HORIZONTAL_ALIGN_CENTER) {
            mFlow.setHorizontalAlign(mHorizontalAlign);
        }
        if (mVerticalAlign != VERTICAL_ALIGN_CENTER) {
            mFlow.setVerticalAlign(mVerticalAlign);
        }

        // Style
        if (mVerticalStyle != UNKNOWN) {
            mFlow.setVerticalStyle(mVerticalStyle);
        }
        if (mFirstVerticalStyle != UNKNOWN) {
            mFlow.setFirstVerticalStyle(mFirstVerticalStyle);
        }
        if (mLastVerticalStyle != UNKNOWN) {
            mFlow.setLastVerticalStyle(mLastVerticalStyle);
        }
        if (mHorizontalStyle != UNKNOWN) {
            mFlow.setHorizontalStyle(mHorizontalStyle);
        }
        if (mFirstHorizontalStyle != UNKNOWN) {
            mFlow.setFirstVerticalStyle(mFirstHorizontalStyle);
        }
        if (mLastVerticalStyle != UNKNOWN) {
            mFlow.setLastVerticalStyle(mLastVerticalStyle);
        }

        // Constraint
        if (mStartToStart != null) {mFlow.connect(ConstraintAnchor.Type.LEFT,
                ((ConstraintReference) mStartToStart).getConstraintWidget(),
                ConstraintAnchor.Type.LEFT);
        }
        if (mStartToEnd != null) {mFlow.connect(ConstraintAnchor.Type.LEFT,
                ((ConstraintReference) mStartToEnd).getConstraintWidget(),
                ConstraintAnchor.Type.RIGHT);
        }
        if (mEndToStart != null) {mFlow.connect(ConstraintAnchor.Type.RIGHT,
                ((ConstraintReference) mEndToStart).getConstraintWidget(),
                ConstraintAnchor.Type.LEFT);
        }
        if (mEndToEnd != null) {mFlow.connect(ConstraintAnchor.Type.RIGHT,
                ((ConstraintReference) mEndToEnd).getConstraintWidget(),
                ConstraintAnchor.Type.RIGHT);
        }
        if (mTopToTop != null) { mFlow.connect(ConstraintAnchor.Type.TOP,
                ((ConstraintReference) mTopToTop).getConstraintWidget(),
                ConstraintAnchor.Type.TOP);
        }
        if (mTopToBottom != null) {mFlow.connect(ConstraintAnchor.Type.TOP,
                ((ConstraintReference) mTopToBottom).getConstraintWidget(),
                ConstraintAnchor.Type.BOTTOM);
        }
        if (mBottomToTop != null) {mFlow.connect(ConstraintAnchor.Type.BOTTOM,
                ((ConstraintReference) mBottomToTop).getConstraintWidget(),
                ConstraintAnchor.Type.TOP);
        }
        if (mBottomToBottom != null) {mFlow.connect(ConstraintAnchor.Type.BOTTOM,
                ((ConstraintReference) mBottomToBottom).getConstraintWidget(),
                ConstraintAnchor.Type.BOTTOM);
        }

        // TODO - Need to figure out how to set these values properly.
        mFlow.measure(AT_MOST, 1000, AT_MOST,1000);
    }
}
