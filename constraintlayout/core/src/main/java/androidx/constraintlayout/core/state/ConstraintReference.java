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

import androidx.constraintlayout.core.state.helpers.Facade;
import androidx.constraintlayout.core.widgets.ConstraintAnchor;
import androidx.constraintlayout.core.widgets.ConstraintWidget;

import java.util.ArrayList;
import java.util.HashMap;

import static androidx.constraintlayout.core.widgets.ConstraintWidget.HORIZONTAL;
import static androidx.constraintlayout.core.widgets.ConstraintWidget.VERTICAL;

public class ConstraintReference implements Reference {

    private Object key;

    public void setKey(Object key) {
        this.key = key;
    }

    public Object getKey() {
        return key;
    }

    public void setTag(String tag) {
        mTag = tag;
    }

    public String getTag() {
        return mTag;
    }

    public interface ConstraintReferenceFactory {
        ConstraintReference create(State state);
    }

    final State mState;

    String mTag = null;

    Facade mFacade = null;

    int mHorizontalChainStyle = ConstraintWidget.CHAIN_SPREAD;
    int mVerticalChainStyle = ConstraintWidget.CHAIN_SPREAD;

    float mHorizontalBias = 0.5f;
    float mVerticalBias = 0.5f;

    int mMarginLeft = 0;
    int mMarginRight = 0;
    protected int mMarginStart = 0;
    protected int mMarginEnd = 0;
    int mMarginTop = 0;
    int mMarginBottom = 0;

    int mMarginLeftGone = 0;
    int mMarginRightGone = 0;
    int mMarginStartGone = 0;
    int mMarginEndGone = 0;
    int mMarginTopGone = 0;
    int mMarginBottomGone = 0;

    float mPivotX = Float.NaN;
    float mPivotY = Float.NaN;

    float mRotationX = Float.NaN;
    float mRotationY = Float.NaN;
    float mRotationZ = Float.NaN;

    float mTranslationX = Float.NaN;
    float mTranslationY = Float.NaN;
    float mTranslationZ = Float.NaN;

    float mAlpha = Float.NaN;

    float mScaleX = Float.NaN;
    float mScaleY = Float.NaN;

    int mVisibility = ConstraintWidget.VISIBLE;

    Object mLeftToLeft = null;
    Object mLeftToRight = null;
    Object mRightToLeft = null;
    Object mRightToRight = null;
    protected Object mStartToStart = null;
    protected Object mStartToEnd = null;
    protected Object mEndToStart = null;
    protected Object mEndToEnd = null;
    protected Object mTopToTop = null;
    protected Object mTopToBottom = null;
    protected Object mBottomToTop = null;
    protected Object mBottomToBottom = null;
    Object mBaselineToBaseline = null;
    Object mCircularConstraint = null;
    private float mCircularAngle;
    private float mCircularDistance;

    State.Constraint mLast = null;

    Dimension mHorizontalDimension = Dimension.Fixed(Dimension.WRAP_DIMENSION);
    Dimension mVerticalDimension = Dimension.Fixed(Dimension.WRAP_DIMENSION);

    private Object mView;
    private ConstraintWidget mConstraintWidget;

    private HashMap<String, WidgetFrame.Color> mCustomColors = null;
    private HashMap<String, Float> mCustomFloats = null;

    public void setView(Object view) {
        mView = view;
        if (mConstraintWidget != null) {
            mConstraintWidget.setCompanionWidget(mView);
        }
    }

    public Object getView() {
        return mView;
    }

    public void setFacade(Facade facade) {
        mFacade = facade;
        if (facade != null) {
            setConstraintWidget(facade.getConstraintWidget());
        }
    }

    public Facade getFacade() { return mFacade; }

    public void setConstraintWidget(ConstraintWidget widget) {
        if (widget == null) {
            return;
        }
        mConstraintWidget = widget;
        mConstraintWidget.setCompanionWidget(mView);
    }

    @Override
    public ConstraintWidget getConstraintWidget() {
        if (mConstraintWidget == null) {
            mConstraintWidget = createConstraintWidget();
            mConstraintWidget.setCompanionWidget(mView);
        }
        return mConstraintWidget;
    }

    public ConstraintWidget createConstraintWidget() {
        return new ConstraintWidget(
                getWidth().getValue(),
                getHeight().getValue());
    }

    static class IncorrectConstraintException extends Exception {

        private final ArrayList<String> mErrors;

        public IncorrectConstraintException(ArrayList<String> errors) {
            mErrors = errors;
        }

        public ArrayList<String> getErrors() { return mErrors; }

        @Override
        public String toString() {
            return "IncorrectConstraintException: " + mErrors.toString();
        }
    }

    /**
     *  Validate the constraints
     */
    public void validate() throws IncorrectConstraintException {
        ArrayList<String> errors = new ArrayList<>();
        if (mLeftToLeft != null && mLeftToRight != null) {
            errors.add("LeftToLeft and LeftToRight both defined");
        }
        if (mRightToLeft != null && mRightToRight != null) {
            errors.add("RightToLeft and RightToRight both defined");
        }
        if (mStartToStart != null && mStartToEnd != null) {
            errors.add("StartToStart and StartToEnd both defined");
        }
        if (mEndToStart != null && mEndToEnd != null) {
            errors.add("EndToStart and EndToEnd both defined");
        }
        if ((mLeftToLeft != null || mLeftToRight != null || mRightToLeft != null || mRightToRight != null)
                && (mStartToStart != null || mStartToEnd != null || mEndToStart != null || mEndToEnd != null)) {
            errors.add("Both left/right and start/end constraints defined");
        }
        if (errors.size() > 0) {
            throw new IncorrectConstraintException(errors);
        }
    }

    private Object get(Object reference) {
        if (reference == null) {
            return null;
        }
        if (!(reference instanceof ConstraintReference)) {
            return mState.reference(reference);
        }
        return reference;
    }

    public ConstraintReference(State state) { mState = state; }

    public void setHorizontalChainStyle(int chainStyle) {
        mHorizontalChainStyle = chainStyle;
    }

    public int getHorizontalChainStyle() {
        return mHorizontalChainStyle;
    }

    public void setVerticalChainStyle(int chainStyle) {
        mVerticalChainStyle = chainStyle;
    }

    public int getVerticalChainStyle(int chainStyle) {
        return mVerticalChainStyle;
    }

    public ConstraintReference clearVertical() {
        top().clear();
        baseline().clear();
        bottom().clear();
        return this;
    }

    public ConstraintReference clearHorizontal() {
        start().clear();
        end().clear();
        left().clear();
        right().clear();
        return this;
    }

    public float getTranslationX() { return mTranslationX; }
    public float getTranslationY() { return mTranslationY; }
    public float getTranslationZ() { return mTranslationZ; }
    public float getScaleX() { return mScaleX; }
    public float getScaleY() { return mScaleY; }
    public float getAlpha() { return mAlpha; }
    public float getPivotX() { return mPivotX; }
    public float getPivotY() { return mPivotY; }
    public float getRotationX() { return mRotationX; }
    public float getRotationY() { return mRotationY; }
    public float getRotationZ() { return mRotationZ; }

    public ConstraintReference pivotX(float x) {
        mPivotX = x;
        return this;
    }

    public ConstraintReference pivotY(float y) {
        mPivotY = y;
        return this;
    }

    public ConstraintReference rotationX(float x) {
        mRotationX = x;
        return this;
    }

    public ConstraintReference rotationY(float y) {
        mRotationY = y;
        return this;
    }

    public ConstraintReference rotationZ(float z) {
        mRotationZ = z;
        return this;
    }

    public ConstraintReference translationX(float x) {
        mTranslationX = x;
        return this;
    }

    public ConstraintReference translationY(float y) {
        mTranslationY = y;
        return this;
    }

    public ConstraintReference translationZ(float z) {
        mTranslationZ = z;
        return this;
    }

    public ConstraintReference scaleX(float x) {
        mScaleX = x;
        return this;
    }

    public ConstraintReference scaleY(float y) {
        mScaleY = y;
        return this;
    }

    public ConstraintReference alpha(float alpha) {
        mAlpha = alpha;
        return this;
    }

    public ConstraintReference visibility(int visibility) {
        mVisibility = visibility;
        return this;
    }

    public ConstraintReference left() {
        if (mLeftToLeft != null) {
            mLast = State.Constraint.LEFT_TO_LEFT;
        } else {
            mLast = State.Constraint.LEFT_TO_RIGHT;
        }
        return this;
    }

    public ConstraintReference right() {
        if (mRightToLeft != null) {
            mLast = State.Constraint.RIGHT_TO_LEFT;
        } else {
            mLast = State.Constraint.RIGHT_TO_RIGHT;
        }
        return this;
    }

    public ConstraintReference start() {
        if (mStartToStart != null) {
            mLast = State.Constraint.START_TO_START;
        } else {
            mLast = State.Constraint.START_TO_END;
        }
        return this;
    }

    public ConstraintReference end() {
        if (mEndToStart != null) {
            mLast = State.Constraint.END_TO_START;
        } else {
            mLast = State.Constraint.END_TO_END;
        }
        return this;
    }

    public ConstraintReference top() {
        if (mTopToTop != null) {
            mLast = State.Constraint.TOP_TO_TOP;
        } else {
            mLast = State.Constraint.TOP_TO_BOTTOM;
        }
        return this;
    }

    public ConstraintReference bottom() {
        if (mBottomToTop != null) {
            mLast = State.Constraint.BOTTOM_TO_TOP;
        } else {
            mLast = State.Constraint.BOTTOM_TO_BOTTOM;
        }
        return this;
    }

    public ConstraintReference baseline() {
        mLast = State.Constraint.BASELINE_TO_BASELINE;
        return this;
    }

    public void addCustomColor(String name, float r, float g, float b, float a) {
        WidgetFrame.Color color = new WidgetFrame.Color(r, g, b, a);
        if (mCustomColors == null) {
            mCustomColors = new HashMap<>();
        }
        mCustomColors.put(name, color);
    }

    public void addCustomFloat(String name, float value) {
        if (mCustomFloats == null) {
            mCustomFloats = new HashMap<>();
        }
        mCustomFloats.put(name, value);
    }

    private void dereference() {
        mLeftToLeft = get(mLeftToLeft);
        mLeftToRight = get(mLeftToRight);
        mRightToLeft = get(mRightToLeft);
        mRightToRight = get(mRightToRight);
        mStartToStart = get(mStartToStart);
        mStartToEnd = get(mStartToEnd);
        mEndToStart = get(mEndToStart);
        mEndToEnd = get(mEndToEnd);
        mTopToTop = get(mTopToTop);
        mTopToBottom = get(mTopToBottom);
        mBottomToTop = get(mBottomToTop);
        mBottomToBottom = get(mBottomToBottom);
        mBaselineToBaseline = get(mBaselineToBaseline);
    }

    public ConstraintReference leftToLeft(Object reference) {
        mLast = State.Constraint.LEFT_TO_LEFT;
        mLeftToLeft = reference;
        return this;
    }

    public ConstraintReference leftToRight(Object reference) {
        mLast = State.Constraint.LEFT_TO_RIGHT;
        mLeftToRight = reference;
        return this;
    }

    public ConstraintReference rightToLeft(Object reference) {
        mLast = State.Constraint.RIGHT_TO_LEFT;
        mRightToLeft = reference;
        return this;
    }

    public ConstraintReference rightToRight(Object reference) {
        mLast = State.Constraint.RIGHT_TO_RIGHT;
        mRightToRight = reference;
        return this;
    }

    public ConstraintReference startToStart(Object reference) {
        mLast = State.Constraint.START_TO_START;
        mStartToStart = reference;
        return this;
    }

    public ConstraintReference startToEnd(Object reference) {
        mLast = State.Constraint.START_TO_END;
        mStartToEnd = reference;
        return this;
    }

    public ConstraintReference endToStart(Object reference) {
        mLast = State.Constraint.END_TO_START;
        mEndToStart = reference;
        return this;
    }

    public ConstraintReference endToEnd(Object reference) {
        mLast = State.Constraint.END_TO_END;
        mEndToEnd = reference;
        return this;
    }

    public ConstraintReference topToTop(Object reference) {
        mLast = State.Constraint.TOP_TO_TOP;
        mTopToTop = reference;
        return this;
    }

    public ConstraintReference topToBottom(Object reference) {
        mLast = State.Constraint.TOP_TO_BOTTOM;
        mTopToBottom = reference;
        return this;
    }

    public ConstraintReference bottomToTop(Object reference) {
        mLast = State.Constraint.BOTTOM_TO_TOP;
        mBottomToTop = reference;
        return this;
    }

    public ConstraintReference bottomToBottom(Object reference) {
        mLast = State.Constraint.BOTTOM_TO_BOTTOM;
        mBottomToBottom = reference;
        return this;
    }

    public ConstraintReference baselineToBaseline(Object reference) {
        mLast = State.Constraint.BASELINE_TO_BASELINE;
        mBaselineToBaseline = reference;
        return this;
    }

    public ConstraintReference centerHorizontally(Object reference) {
        Object ref = get(reference);
        mStartToStart = ref;
        mEndToEnd = ref;
        mLast = State.Constraint.CENTER_HORIZONTALLY;
        mHorizontalBias = 0.5f;
        return this;
    }

    public ConstraintReference centerVertically(Object reference) {
        Object ref = get(reference);
        mTopToTop = ref;
        mBottomToBottom = ref;
        mLast = State.Constraint.CENTER_VERTICALLY;
        mVerticalBias = 0.5f;
        return this;
    }

    public ConstraintReference circularConstraint(Object reference, float angle, float distance) {
        Object ref = get(reference);
        mCircularConstraint = ref;
        mCircularAngle = angle;
        mCircularDistance = distance;
        mLast = State.Constraint.CIRCULAR_CONSTRAINT;
        return this;
    }

    public ConstraintReference width(Dimension dimension) {
        return setWidth(dimension);
    }

    public ConstraintReference height(Dimension dimension) {
        return setHeight(dimension);
    }

    public Dimension getWidth() { return mHorizontalDimension; }

    public ConstraintReference setWidth(Dimension dimension) {
        mHorizontalDimension = dimension;
        return this;
    }

    public Dimension getHeight() { return mVerticalDimension; }
    public ConstraintReference setHeight(Dimension dimension) {
        mVerticalDimension = dimension;
        return this;
    }

    public ConstraintReference margin(Object marginValue) {
        return margin(mState.convertDimension(marginValue));
    }

    public ConstraintReference margin(int value) {
        if (mLast != null) {
            switch (mLast) {
                case LEFT_TO_LEFT:
                case LEFT_TO_RIGHT: {
                    mMarginLeft = value;
                } break;
                case RIGHT_TO_LEFT:
                case RIGHT_TO_RIGHT: {
                    mMarginRight = value;
                } break;
                case START_TO_START:
                case START_TO_END: {
                    mMarginStart = value;
                } break;
                case END_TO_START:
                case END_TO_END: {
                    mMarginEnd = value;
                } break;
                case TOP_TO_TOP:
                case TOP_TO_BOTTOM: {
                    mMarginTop = value;
                } break;
                case BOTTOM_TO_TOP:
                case BOTTOM_TO_BOTTOM: {
                    mMarginBottom = value;
                } break;
                case BASELINE_TO_BASELINE: {
                    // nothing
                } break;
                case CIRCULAR_CONSTRAINT: {
                    mCircularDistance = value;
                }
            }
        } else {
            mMarginLeft = value;
            mMarginRight = value;
            mMarginStart = value;
            mMarginEnd = value;
            mMarginTop = value;
            mMarginBottom = value;
        }
        return this;
    }

    public ConstraintReference marginGone(int value) {
        if (mLast != null) {
            switch (mLast) {
                case LEFT_TO_LEFT:
                case LEFT_TO_RIGHT: {
                    mMarginLeftGone = value;
                } break;
                case RIGHT_TO_LEFT:
                case RIGHT_TO_RIGHT: {
                    mMarginRightGone = value;
                } break;
                case START_TO_START:
                case START_TO_END: {
                    mMarginStartGone = value;
                } break;
                case END_TO_START:
                case END_TO_END: {
                    mMarginEndGone = value;
                } break;
                case TOP_TO_TOP:
                case TOP_TO_BOTTOM: {
                    mMarginTopGone = value;
                } break;
                case BOTTOM_TO_TOP:
                case BOTTOM_TO_BOTTOM: {
                    mMarginBottomGone = value;
                } break;
                case BASELINE_TO_BASELINE: {
                    // nothing
                } break;
            }
        } else {
            mMarginLeftGone = value;
            mMarginRightGone = value;
            mMarginStartGone = value;
            mMarginEndGone = value;
            mMarginTopGone = value;
            mMarginBottomGone = value;
        }
        return this;
    }

    public ConstraintReference horizontalBias(float value) {
        mHorizontalBias = value;
        return this;
    }

    public ConstraintReference verticalBias(float value) {
        mVerticalBias = value;
        return this;
    }

    public ConstraintReference bias(float value) {
        if (mLast == null) {
            return this;
        }
        switch (mLast) {
            case CENTER_HORIZONTALLY:
            case LEFT_TO_LEFT:
            case LEFT_TO_RIGHT:
            case RIGHT_TO_LEFT:
            case RIGHT_TO_RIGHT:
            case START_TO_START:
            case START_TO_END:
            case END_TO_START:
            case END_TO_END: {
                mHorizontalBias = value;
            } break;
            case CENTER_VERTICALLY:
            case TOP_TO_TOP:
            case TOP_TO_BOTTOM:
            case BOTTOM_TO_TOP:
            case BOTTOM_TO_BOTTOM: {
                mVerticalBias = value;
            } break;
        }
        return this;
    }

    public ConstraintReference clear() {
        if (mLast != null) {
            switch (mLast) {
                case LEFT_TO_LEFT:
                case LEFT_TO_RIGHT: {
                    mLeftToLeft = null;
                    mLeftToRight = null;
                    mMarginLeft = 0;
                    mMarginLeftGone = 0;
                }
                break;
                case RIGHT_TO_LEFT:
                case RIGHT_TO_RIGHT: {
                    mRightToLeft = null;
                    mRightToRight = null;
                    mMarginRight = 0;
                    mMarginRightGone = 0;
                }
                break;
                case START_TO_START:
                case START_TO_END: {
                    mStartToStart = null;
                    mStartToEnd = null;
                    mMarginStart = 0;
                    mMarginStartGone = 0;
                }
                break;
                case END_TO_START:
                case END_TO_END: {
                    mEndToStart = null;
                    mEndToEnd = null;
                    mMarginEnd = 0;
                    mMarginEndGone = 0;
                }
                break;
                case TOP_TO_TOP:
                case TOP_TO_BOTTOM: {
                    mTopToTop = null;
                    mTopToBottom = null;
                    mMarginTop = 0;
                    mMarginTopGone = 0;
                }
                break;
                case BOTTOM_TO_TOP:
                case BOTTOM_TO_BOTTOM: {
                    mBottomToTop = null;
                    mBottomToBottom = null;
                    mMarginBottom = 0;
                    mMarginBottomGone = 0;
                }
                break;
                case BASELINE_TO_BASELINE: {
                    mBaselineToBaseline = null;
                }
                break;
                case CIRCULAR_CONSTRAINT: {
                    mCircularConstraint = null;
                }
            }
        } else {
            mLeftToLeft = null;
            mLeftToRight = null;
            mMarginLeft = 0;
            mRightToLeft = null;
            mRightToRight = null;
            mMarginRight = 0;
            mStartToStart = null;
            mStartToEnd = null;
            mMarginStart = 0;
            mEndToStart = null;
            mEndToEnd = null;
            mMarginEnd = 0;
            mTopToTop = null;
            mTopToBottom = null;
            mMarginTop = 0;
            mBottomToTop = null;
            mBottomToBottom = null;
            mMarginBottom = 0;
            mBaselineToBaseline = null;
            mCircularConstraint = null;
            mHorizontalBias = 0.5f;
            mVerticalBias = 0.5f;
            mMarginLeftGone = 0;
            mMarginRightGone = 0;
            mMarginStartGone = 0;
            mMarginEndGone = 0;
            mMarginTopGone = 0;
            mMarginBottomGone = 0;
        }
        return this;
    }

    private ConstraintWidget getTarget(Object target) {
        if (target instanceof Reference) {
            Reference referenceTarget = (Reference) target;
            return referenceTarget.getConstraintWidget();
        }
        return null;
    }

    private void applyConnection(ConstraintWidget widget, Object opaqueTarget, State.Constraint type) {
        ConstraintWidget target = getTarget(opaqueTarget);
        if (target == null) {
            return;
        }
        switch (type) {
            // TODO: apply RTL
        }
        switch (type) {
            case START_TO_START: {
                widget.getAnchor(ConstraintAnchor.Type.LEFT).connect(target.getAnchor(
                        ConstraintAnchor.Type.LEFT), mMarginStart, mMarginStartGone, false);
            } break;
            case START_TO_END: {
                widget.getAnchor(ConstraintAnchor.Type.LEFT).connect(target.getAnchor(
                        ConstraintAnchor.Type.RIGHT), mMarginStart, mMarginStartGone, false);
            } break;
            case END_TO_START: {
                widget.getAnchor(ConstraintAnchor.Type.RIGHT).connect(target.getAnchor(
                        ConstraintAnchor.Type.LEFT), mMarginEnd, mMarginEndGone, false);
            } break;
            case END_TO_END: {
                widget.getAnchor(ConstraintAnchor.Type.RIGHT).connect(target.getAnchor(
                        ConstraintAnchor.Type.RIGHT), mMarginEnd, mMarginEndGone, false);
            } break;
            case LEFT_TO_LEFT: {
                widget.getAnchor(ConstraintAnchor.Type.LEFT).connect(target.getAnchor(
                        ConstraintAnchor.Type.LEFT), mMarginLeft, mMarginLeftGone, false);
            } break;
            case LEFT_TO_RIGHT: {
                widget.getAnchor(ConstraintAnchor.Type.LEFT).connect(target.getAnchor(
                        ConstraintAnchor.Type.RIGHT), mMarginLeft, mMarginLeftGone, false);
            } break;
            case RIGHT_TO_LEFT: {
                widget.getAnchor(ConstraintAnchor.Type.RIGHT).connect(target.getAnchor(
                        ConstraintAnchor.Type.LEFT), mMarginRight, mMarginRightGone, false);
            } break;
            case RIGHT_TO_RIGHT: {
                widget.getAnchor(ConstraintAnchor.Type.RIGHT).connect(target.getAnchor(
                        ConstraintAnchor.Type.RIGHT), mMarginRight, mMarginRightGone, false);
            } break;
            case TOP_TO_TOP: {
                widget.getAnchor(ConstraintAnchor.Type.TOP).connect(target.getAnchor(
                        ConstraintAnchor.Type.TOP), mMarginTop, mMarginTopGone, false);
            } break;
            case TOP_TO_BOTTOM: {
                widget.getAnchor(ConstraintAnchor.Type.TOP).connect(target.getAnchor(
                        ConstraintAnchor.Type.BOTTOM), mMarginTop, mMarginTopGone, false);
            } break;
            case BOTTOM_TO_TOP: {
                widget.getAnchor(ConstraintAnchor.Type.BOTTOM).connect(target.getAnchor(
                        ConstraintAnchor.Type.TOP), mMarginBottom, mMarginBottomGone, false);
            } break;
            case BOTTOM_TO_BOTTOM: {
                widget.getAnchor(ConstraintAnchor.Type.BOTTOM).connect(target.getAnchor(
                        ConstraintAnchor.Type.BOTTOM), mMarginBottom, mMarginBottomGone, false);
            } break;
            case BASELINE_TO_BASELINE: {
                widget.immediateConnect(ConstraintAnchor.Type.BASELINE, target, ConstraintAnchor.Type.BASELINE, 0, 0);
            } break;
            case CIRCULAR_CONSTRAINT: {
                widget.connectCircularConstraint(target, mCircularAngle, (int) mCircularDistance);
            } break;
        }
    }

    public void apply() {
        if (mConstraintWidget == null) {
            return;
        }
        if (mFacade != null) {
            mFacade.apply();
        }
        mHorizontalDimension.apply(mState, mConstraintWidget, HORIZONTAL);
        mVerticalDimension.apply(mState, mConstraintWidget, VERTICAL);
        dereference();

        applyConnection(mConstraintWidget, mLeftToLeft, State.Constraint.LEFT_TO_LEFT);
        applyConnection(mConstraintWidget, mLeftToRight, State.Constraint.LEFT_TO_RIGHT);
        applyConnection(mConstraintWidget, mRightToLeft, State.Constraint.RIGHT_TO_LEFT);
        applyConnection(mConstraintWidget, mRightToRight, State.Constraint.RIGHT_TO_RIGHT);
        applyConnection(mConstraintWidget, mStartToStart, State.Constraint.START_TO_START);
        applyConnection(mConstraintWidget, mStartToEnd, State.Constraint.START_TO_END);
        applyConnection(mConstraintWidget, mEndToStart, State.Constraint.END_TO_START);
        applyConnection(mConstraintWidget, mEndToEnd, State.Constraint.END_TO_END);
        applyConnection(mConstraintWidget, mTopToTop, State.Constraint.TOP_TO_TOP);
        applyConnection(mConstraintWidget, mTopToBottom, State.Constraint.TOP_TO_BOTTOM);
        applyConnection(mConstraintWidget, mBottomToTop, State.Constraint.BOTTOM_TO_TOP);
        applyConnection(mConstraintWidget, mBottomToBottom, State.Constraint.BOTTOM_TO_BOTTOM);
        applyConnection(mConstraintWidget, mBaselineToBaseline, State.Constraint.BASELINE_TO_BASELINE);
        applyConnection(mConstraintWidget, mCircularConstraint, State.Constraint.CIRCULAR_CONSTRAINT);

        if (mHorizontalChainStyle != ConstraintWidget.CHAIN_SPREAD) {
            mConstraintWidget.setHorizontalChainStyle(mHorizontalChainStyle);
        }
        if (mVerticalChainStyle != ConstraintWidget.CHAIN_SPREAD) {
            mConstraintWidget.setVerticalChainStyle(mVerticalChainStyle);
        }

        mConstraintWidget.setHorizontalBiasPercent(mHorizontalBias);
        mConstraintWidget.setVerticalBiasPercent(mVerticalBias);

        mConstraintWidget.frame.pivotX = mPivotX;
        mConstraintWidget.frame.pivotY = mPivotY;
        mConstraintWidget.frame.rotationX = mRotationX;
        mConstraintWidget.frame.rotationY = mRotationY;
        mConstraintWidget.frame.rotationZ = mRotationZ;
        mConstraintWidget.frame.translationX = mTranslationX;
        mConstraintWidget.frame.translationY = mTranslationY;
        mConstraintWidget.frame.scaleX = mScaleX;
        mConstraintWidget.frame.scaleY = mScaleY;
        mConstraintWidget.frame.alpha = mAlpha;
        mConstraintWidget.frame.visibility = mVisibility;
        mConstraintWidget.setVisibility(mVisibility);

        mConstraintWidget.frame.mCustomFloats = mCustomFloats;
        mConstraintWidget.frame.mCustomColors = mCustomColors;
    }
}
