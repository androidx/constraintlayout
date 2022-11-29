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

import androidx.constraintlayout.core.state.HelperReference;
import androidx.constraintlayout.core.state.State;
import androidx.constraintlayout.core.utils.GridCore;
import androidx.constraintlayout.core.widgets.Flow;
import androidx.constraintlayout.core.widgets.HelperWidget;

/**
 * A HelperReference of a Grid Helper that helps enable Grid in Compose
 */
public class GridReference extends HelperReference {

    public GridReference(State state, State.Helper type) {
        super(state, type);
    }

    /**
     * The Grid Object
     */
    protected GridCore mGrid;

    /**
     * The orientation of the widgets arrangement horizontally or vertically
     */
    protected int mOrientation;

    /**
     * Number of rows of the Grid
     */
    protected int mRowsSet;

    /**
     * Number of columns of the Grid
     */
    protected int mColumnsSet;

    /**
     * The horizontal gaps between widgets
     */
    protected float mHorizontalGaps;

    /**
     * The vertical gaps between widgets
     */
    protected float mVerticalGaps;

    /**
     * The weight of each widget in a row
     */
    protected String mStrRowWeights;

    /**
     * The weight of each widget in a column
     */
    protected String mStrColumnWeights;

    /**
     * Specify the spanned areas of widgets
     */
    protected String mStrSpans;

    /**
     * Specify the positions to be skipped in a Grid
     */
    protected String mStrSkips;

    /**
     * Get the number of rows
     * @return the number of rows
     */
    public int getRowsSet() {
        return mRowsSet;
    }

    /**
     * Set the number of rows
     * @param rowsSet the number of rows
     */
    public void setRowsSet(int rowsSet) {
        mRowsSet = rowsSet;
    }

    /**
     * Get the number of columns
     * @return the number of columns
     */
    public int getColumnsSet() {
        return mColumnsSet;
    }

    /**
     * Set the number of columns
     * @param columnsSet the number of columns
     */
    public void setColumnsSet(int columnsSet) {
        mColumnsSet = columnsSet;
    }

    /**
     * Get the horizontal gaps
     * @return the horizontal gaps
     */
    public float getHorizontalGaps() {
        return mHorizontalGaps;
    }

    /**
     * Set the horizontal gaps
     * @param horizontalGaps the horizontal gaps
     */
    public void setHorizontalGaps(float horizontalGaps) {
        mHorizontalGaps = horizontalGaps;
    }

    /**
     * Get the vertical gaps
     * @return the vertical gaps
     */
    public float getVerticalGaps() {
        return mVerticalGaps;
    }

    /**
     * Set the vertical gaps
     * @param verticalGaps  the vertical gaps
     */
    public void setVerticalGaps(float verticalGaps) {
        mVerticalGaps = verticalGaps;
    }

    /**
     * Get the row weights
     * @return the row weights
     */
    public String getStrRowWeights() {
        return mStrRowWeights;
    }

    /**
     * Set the row weights
     * @param strRowWeights the row weights
     */
    public void setStrRowWeights(String strRowWeights) {
        mStrRowWeights = strRowWeights;
    }

    /**
     * Get the column weights
     * @return the column weights
     */
    public String getStrColumnWeights() {
        return mStrColumnWeights;
    }

    /**
     * Set the column weights
     * @param strColumnWeights the column weights
     */
    public void setStrColumnWeights(String strColumnWeights) {
        mStrColumnWeights = strColumnWeights;
    }

    /**
     * Get the spans
     * @return the spans
     */
    public String getStrSpans() {
        return mStrSpans;
    }

    /**
     * Set the spans
     * @param strSpans the spans
     */
    public void setStrSpans(String strSpans) {
        mStrSpans = strSpans;
    }

    /**
     * Get the skips
     * @return the skips
     */
    public String getStrSkips() {
        return mStrSkips;
    }

    /**
     * Set the skips
     * @param strSkips the skips
     */
    public void setStrSkips(String strSkips) {
        mStrSkips = strSkips;
    }

    /**
     * Get the helper widget (Grid)
     * @return the helper widget (Grid)
     */
    @Override
    public HelperWidget getHelperWidget() {
        if (mGrid == null) {
            mGrid = new GridCore();
        }
        return mGrid;
    }

    /**
     * Set the helper widget (Grid)
     * @param widget the helper widget (Grid)
     */
    @Override
    public void setHelperWidget(HelperWidget widget) {
        if (widget instanceof Flow) {
            mGrid = (GridCore) widget;
        } else {
            mGrid = null;
        }
    }

    /**
     * Get the Orientation
     * @return the Orientation
     */
    public int getOrientation() {
        return mOrientation;
    }

    /**
     * Set the Orientation
     * @param orientation the Orientation
     */
    public void setOrientation(int orientation) {
        mOrientation = orientation;

    }

    /**
     * Apply all the attributes to the helper widget (Grid)
     */
    @Override
    public void apply() {
        getHelperWidget();

        mGrid.setOrientation(mOrientation);

        if (mRowsSet != 0) {
            mGrid.setRows(mRowsSet);
        }

        if (mColumnsSet != 0) {
            mGrid.setColumns(mColumnsSet);
        }

        if (mHorizontalGaps != 0) {
            mGrid.setHorizontalGaps(mHorizontalGaps);
        }

        if (mVerticalGaps != 0) {
            mGrid.setVerticalGaps(mVerticalGaps);
        }

        if (mStrRowWeights != null && !mStrRowWeights.equals("")) {
            mGrid.setRowWeights(mStrRowWeights);
        }

        if (mStrColumnWeights != null && !mStrColumnWeights.equals("")) {
            mGrid.setColumnWeights(mStrColumnWeights);
        }

        if (mStrSpans != null && !mStrSpans.equals("")) {
            mGrid.setSpans(mStrSpans);
        }

        if (mStrSkips != null && !mStrSkips.equals("")) {
            mGrid.setSkips(mStrSkips);
        }

        // General attributes of a widget
        applyBase();
    }
}
