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

import androidx.constraintlayout.core.widgets.ConstraintWidget;

import static androidx.constraintlayout.core.widgets.ConstraintWidget.*;

/**
 * Represents a dimension (width or height) of a constrained widget
 */
public class Dimension {

    public static final Object FIXED_DIMENSION = new Object();
    public static final Object WRAP_DIMENSION = new Object();
    public static final Object SPREAD_DIMENSION = new Object();
    public static final Object PARENT_DIMENSION = new Object();
    public static final Object PERCENT_DIMENSION = new Object();
    public static final Object RATIO_DIMENSION = new Object();

    private final int WRAP_CONTENT = -2;

    int mMin = 0;
    int mMax = Integer.MAX_VALUE;
    float mPercent = 1f;
    int mValue = 0;
    String mRatioString = null;
    Object mInitialValue = WRAP_DIMENSION;
    boolean mIsSuggested = false;

    /**
     * Returns true if the dimension is a fixed dimension of
     * the same given value
     *
     * @param value
     * @return
     */
    public boolean equalsFixedValue(int value) {
        if (mInitialValue == null
            && mValue == value) {
            return true;
        }
        return false;
    }

    public enum Type {
        FIXED,
        WRAP,
        MATCH_PARENT,
        MATCH_CONSTRAINT
    }

    private Dimension() {}
    private Dimension(Object type) { mInitialValue = type; }

    public static Dimension Suggested(int value) {
        Dimension dimension = new Dimension();
        dimension.suggested(value);
        return dimension;
    }

    public static Dimension Suggested(Object startValue) {
        Dimension dimension = new Dimension();
        dimension.suggested(startValue);
        return dimension;
    }

    public static Dimension Fixed(int value) {
        Dimension dimension = new Dimension(FIXED_DIMENSION);
        dimension.fixed(value);
        return dimension;
    }

    public static Dimension Fixed(Object value) {
        Dimension dimension = new Dimension(FIXED_DIMENSION);
        dimension.fixed(value);
        return dimension;
    }

    public static Dimension Percent(Object key, float value) {
        Dimension dimension = new Dimension(PERCENT_DIMENSION);
        dimension.percent(key, value);
        return dimension;
    }

    public static Dimension Parent() {
        return new Dimension(PARENT_DIMENSION);
    }

    public static Dimension Wrap() {
        return new Dimension(WRAP_DIMENSION);
    }

    public static Dimension Spread() {
        return new Dimension(SPREAD_DIMENSION);
    }

    public static Dimension Ratio(String ratio) {
        Dimension dimension = new Dimension(RATIO_DIMENSION);
        dimension.ratio(ratio);
        return dimension;
    }

    public Dimension percent(Object key, float value) {
        mPercent = value;
        return this;
    }

    public Dimension min(int value) {
        if (value >= 0) {
            mMin = value;
        }
        return this;
    }

    public Dimension min(Object value) {
        if (value == WRAP_DIMENSION) {
            mMin = WRAP_CONTENT;
        }
        return this;
    }

    public Dimension max(int value) {
        if (mMax >= 0) {
            mMax = value;
        }
        return this;
    }

    public Dimension max(Object value) {
        if (value == WRAP_DIMENSION && mIsSuggested) {
            mInitialValue = WRAP_DIMENSION;
            mMax = Integer.MAX_VALUE;
        }
        return this;
    }

    public Dimension suggested(int value) {
        mIsSuggested = true;
        return this;
    }

    public Dimension suggested(Object value) {
        mInitialValue = value;
        mIsSuggested = true;
        return this;
    }

    public Dimension fixed(Object value) {
        mInitialValue = value;
        if (value instanceof Integer) {
            mValue = (Integer) value;
            mInitialValue = null;
        }
        return this;
    }

    public Dimension fixed(int value) {
        mInitialValue = null;
        mValue = value;
        return this;
    }

    public Dimension ratio(String ratio) { // WxH ratio
        mRatioString = ratio;
        return this;
    }

    void setValue(int value) {
        mIsSuggested = false; // fixed value
        mInitialValue = null;
        mValue = value;
    }

    int getValue() { return mValue; }

    /**
     * Apply the dimension to the given constraint widget
     * @param constraintWidget
     * @param orientation
     */
    public void apply(State state, ConstraintWidget constraintWidget, int orientation) {
        if (mRatioString != null) {
            constraintWidget.setDimensionRatio(mRatioString);
        }
        if (orientation == ConstraintWidget.HORIZONTAL) {
            if (mIsSuggested) {
                constraintWidget.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
                int type = MATCH_CONSTRAINT_SPREAD;
                if (mInitialValue == WRAP_DIMENSION) {
                    type = MATCH_CONSTRAINT_WRAP;
                } else if (mInitialValue == PERCENT_DIMENSION) {
                    type = MATCH_CONSTRAINT_PERCENT;
                }
                constraintWidget.setHorizontalMatchStyle(type, mMin, mMax, mPercent);
            } else { // fixed
                if (mMin > 0) {
                    constraintWidget.setMinWidth(mMin);
                }
                if (mMax < Integer.MAX_VALUE) {
                    constraintWidget.setMaxWidth(mMax);
                }
                if (mInitialValue == WRAP_DIMENSION) {
                    constraintWidget.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
                } else if (mInitialValue == PARENT_DIMENSION) {
                    constraintWidget.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_PARENT);
                } else if (mInitialValue == null) {
                    constraintWidget.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
                    constraintWidget.setWidth(mValue);
                }
            }
        } else {
            if (mIsSuggested) {
                constraintWidget.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
                int type = MATCH_CONSTRAINT_SPREAD;
                if (mInitialValue == WRAP_DIMENSION) {
                    type = MATCH_CONSTRAINT_WRAP;
                } else if (mInitialValue == PERCENT_DIMENSION) {
                    type = MATCH_CONSTRAINT_PERCENT;
                }
                constraintWidget.setVerticalMatchStyle(type, mMin, mMax, mPercent);
            } else { // fixed
                if (mMin > 0) {
                    constraintWidget.setMinHeight(mMin);
                }
                if (mMax < Integer.MAX_VALUE) {
                    constraintWidget.setMaxHeight(mMax);
                }
                if (mInitialValue == WRAP_DIMENSION) {
                    constraintWidget.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
                } else if (mInitialValue == PARENT_DIMENSION) {
                    constraintWidget.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_PARENT);
                } else if (mInitialValue == null) {
                    constraintWidget.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
                    constraintWidget.setHeight(mValue);
                }
            }
        }
    }

}
