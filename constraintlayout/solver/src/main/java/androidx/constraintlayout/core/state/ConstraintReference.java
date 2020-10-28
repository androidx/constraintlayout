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

import androidx.constraintlayout.core.widgets.ConstraintAnchor;
import androidx.constraintlayout.core.widgets.ConstraintWidget;

import java.util.ArrayList;

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

    public interface ConstraintReferenceFactory {
        ConstraintReference create(State state);
    }

    final State mState;

    int mHorizontalChainStyle = ConstraintWidget.CHAIN_SPREAD;
    int mVerticalChainStyle = ConstraintWidget.CHAIN_SPREAD;

    float mHorizontalBias = 0.5f;
    float mVerticalBias = 0.5f;

    int mMarginLeft = 0;
    int mMarginRight = 0;
    int mMarginStart = 0;
    int mMarginEnd = 0;
    int mMarginTop = 0;
    int mMarginBottom = 0;

    int mMarginLeftGone = 0;
    int mMarginRightGone = 0;
    int mMarginStartGone = 0;
    int mMarginEndGone = 0;
    int mMarginTopGone = 0;
    int mMarginBottomGone = 0;

    Object mLeftToLeft = null;
    Object mLeftToRight = null;
    Object mRightToLeft = null;
    Object mRightToRight = null;
    Object mStartToStart = null;
    Object mStartToEnd = null;
    Object mEndToStart = null;
    Object mEndToEnd = null;
    Object mTopToTop = null;
    Object mTopToBottom = null;
    Object mBottomToTop = null;
    Object mBottomToBottom = null;
    Object mBaselineToBaseline = null;

    State.Constraint mLast = null;

    Dimension mHorizontalDimension = Dimension.Fixed(Dimension.WRAP_DIMENSION);
    Dimension mVerticalDimension = Dimension.Fixed(Dimension.WRAP_DIMENSION);

    private Object mView;
    private ConstraintWidget mConstraintWidget;

    public void setView(Object view) {
        mView = view;
        if (mConstraintWidget != null) {
            mConstraintWidget.setCompanionWidget(mView);
        }
    }

    public Object getView() {
        return mView;
    }

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

    class IncorrectConstraintException extends Exception {

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
        }
    }

    public void apply() {
        if (mConstraintWidget == null) {
            return;
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

        if (mHorizontalChainStyle != ConstraintWidget.CHAIN_SPREAD) {
            mConstraintWidget.setHorizontalChainStyle(mHorizontalChainStyle);
        }
        if (mVerticalChainStyle != ConstraintWidget.CHAIN_SPREAD) {
            mConstraintWidget.setVerticalChainStyle(mVerticalChainStyle);
        }

        mConstraintWidget.setHorizontalBiasPercent(mHorizontalBias);
        mConstraintWidget.setVerticalBiasPercent(mVerticalBias);
    }
}
