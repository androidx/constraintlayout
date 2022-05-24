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
package androidx.constraintlayout.helper.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Pair;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Guideline;
import androidx.constraintlayout.widget.R;
import androidx.constraintlayout.widget.VirtualLayout;
import androidx.core.view.ViewCompat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A helper class that helps arrange widgets in a grid form
 *
 * <h2>Grid</h2>
 * <table summary="Grid attributes">
 *   <tr>
 *     <th>Attributes</th><th>Description</th>
 *   </tr>
 *   <tr>
 *     <td>grid_rows</td>
 *     <td>Indicates the number of rows will be created for the grid form.</td>
 *   </tr>
 *   <tr>
 *     <td>grid_columns</td>
 *     <td>Indicates the number of columns will be created for the grid form.</td>
 *   </tr>
 *   <tr>
 *     <td>grid_rowWeights</td>
 *     <td>Specifies the weight of each row in the grid form (default value is 1).</td>
 *   </tr>
 *   <tr>
 *     <td>grid_columnWeights</td>
 *     <td>Specifies the weight of each column in the grid form (default value is 1).</td>
 *   </tr>
 *   <tr>
 *     <td>grid_spans</td>
 *     <td>Offers the capability to span a widget across multiple rows and columns</td>
 *   </tr>
 *   <tr>
 *     <td>grid_skips</td>
 *     <td>Enables skip certain positions in the grid and leave them empty</td>
 *   </tr>
 *   <tr>
 *     <td>grid_orientation</td>
 *     <td>Defines how the associated widgets will be arranged - vertically or horizontally</td>
 *   </tr>
 *   <tr>
 *     <td>grid_horizontalGaps</td>
 *     <td>Adds margin horizontally between widgets</td>
 *   </tr>
 *   <tr>
 *      <td>grid_verticalGaps</td>
 *     <td>Adds margin vertically between widgets</td>
 *   </tr>
 * </table>
 */
public class Grid extends VirtualLayout {
    private static final String TAG = "Grid";
    private static final String VERTICAL = "vertical";
    private final ConstraintSet mConstraintSet = new ConstraintSet();
    ConstraintLayout mContainer;

    /**
     * number of rows of the grid
     */
    private int mRows;

    /**
     * number of columns of the grid
     */
    private int mColumns;

    /**
     * an Guideline array to store all the vertical guidelines
     */
    private Guideline[] mVerticalGuideLines;

    /**
     * an Guideline array to store all the horizontal guidelines
     */
    private Guideline[] mHorizontalGuideLines;

    /**
     * string format of the input Spans
     */
    private String mStrSpans;

    /**
     * string format of the input Skips
     */
    private String mStrSkips;

    /**
     * string format of the row weight
     */
    private String mStrRowWeights;

    /**
     * string format of the column weight
     */
    private String mStrColumnWeights;

    /**
     * Horizontal gaps in Dp
     */
    private int mHorizontalGaps;

    /**
     * Vertical gaps in Dp
     */
    private int mVerticalGaps;

    /**
     * orientation of the view arrangement - vertical or horizontal
     */
    private String mOrientation;

    /**
     * Indicates what is the next available position to place an widget
     */
    private int mNextAvailableIndex = 0;

    /**
     * Indicates whether the input attributes need to be validated
     */
    private boolean mValidateInputs;

    /**
     * Indicates whether to use RTL layout direction
     */
    private boolean mUseRtl;

    /**
     * A integer matrix that tracks the positions that are occupied by skips and spans
     * true: available position
     * false: non-available position
     */
    private boolean[][] mPositionMatrix;

    /**
     * Store the view ids of handled spans
     */
    Set<Integer> mSpanIds = new HashSet<>();

    public Grid(Context context) {
        super(context);
    }

    public Grid(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Grid(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(AttributeSet attrs) {
        super.init(attrs);

        // Parse the relevant attributes from layout xml
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs,
                    R.styleable.Grid);
            final int n = a.getIndexCount();

            for (int i = 0; i < n; i++) {
                int attr = a.getIndex(i);
                if (attr == R.styleable.Grid_grid_rows) {
                    mRows = a.getInteger(attr, 1);
                } else if (attr == R.styleable.Grid_grid_columns) {
                    mColumns = a.getInteger(attr, 1);
                } else if (attr == R.styleable.Grid_grid_spans) {
                    mStrSpans = a.getString(attr);
                } else if (attr == R.styleable.Grid_grid_skips) {
                    mStrSkips = a.getString(attr);
                }  else if (attr == R.styleable.Grid_grid_rowWeights) {
                    mStrRowWeights = a.getString(attr);
                }  else if (attr == R.styleable.Grid_grid_columnWeights) {
                    mStrColumnWeights = a.getString(attr);
                }  else if (attr == R.styleable.Grid_grid_orientation) {
                    mOrientation = a.getString(attr);
                } else if (attr == R.styleable.Grid_grid_horizontalGaps) {
                    mHorizontalGaps = a.getInteger(attr, 0);
                } else if (attr == R.styleable.Grid_grid_verticalGaps) {
                    mVerticalGaps = a.getInteger(attr, 0);
                } else if (attr == R.styleable.Grid_grid_validateInputs) {
                    // @TODO handle validation
                    mValidateInputs = a.getBoolean(attr, false);
                }  else if (attr == R.styleable.Grid_grid_useRtl) {
                    // @TODO handle RTL
                    mUseRtl = a.getBoolean(attr, false);
                }
            }

            initVariables();
            a.recycle();
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        mContainer = (ConstraintLayout) getParent();
        mConstraintSet.clone(mContainer);

        generateGrid();
    }

    /**
     * generate the Grid form based on the input attributes
     * @return true if all the inputs are valid else false
     */
    private boolean generateGrid() {
        boolean isSuccess = true;

        createGuidelines(mRows, mColumns);

        if (mStrSkips != null && !mStrSkips.trim().isEmpty()) {
            HashMap<Integer, Pair<Integer, Integer>> mSkipMap = parseSpans(mStrSkips);
            if (mSkipMap != null) {
                isSuccess &= handleSkips(mSkipMap);
            }
        }

        if (mStrSpans != null && !mStrSpans.trim().isEmpty()) {
            HashMap<Integer, Pair<Integer, Integer>> mSpans = parseSpans(mStrSpans);
            if (mSpans != null) {
                isSuccess &= handleSpans(mIds, mSpans);
            }
        }
        isSuccess &= arrangeWidgets();

        mConstraintSet.applyTo(mContainer);

        return isSuccess || !mValidateInputs;
    }

    /**
     * Initialize the relevant variables
     */
    private void initVariables() {
        mPositionMatrix = new boolean[mRows][mColumns];
        for (boolean[] row: mPositionMatrix) {
            Arrays.fill(row, true);
        }

        mHorizontalGuideLines = new Guideline[mRows + 1];
        mVerticalGuideLines = new Guideline[mColumns + 1];
    }

    /**
     * parse the weights/pads in the string format into a float array
     * @param size size of the return array
     * @param str  weights/pads in a string format
     * @return a float array with weights/pads values
     */
    private float[] parseWeights(int size, String str) {
        if (str == null || str.trim().isEmpty()) {
            return null;
        }

        String[] values = str.split(",");
        if (values.length != size) {
            return null;
        }

        float[] arr = new float[size];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = Float.parseFloat(values[i].trim());
        }
        return arr;
    }

    /**
     * create vertical and horizontal guidelines based on mRows and mColumns
     * @param rows number of rows is required for grid
     * @param columns number of columns is required for grid
     */
    private void createGuidelines(int rows, int columns) {
        float[] rowWeights = parseWeights(rows, mStrRowWeights);
        float[] columnWeights = parseWeights(columns, mStrColumnWeights);

        float[] horizontalPositions = getLinePositions(0, 1,
                rows + 1, rowWeights);
        float[] verticalPositions = getLinePositions(0, 1,
                columns + 1, columnWeights);

        for (int i = 0; i < mHorizontalGuideLines.length; i++) {
            mHorizontalGuideLines[i] = getNewGuideline(myContext,
                    ConstraintLayout.LayoutParams.HORIZONTAL, horizontalPositions[i]);
            mContainer.addView(mHorizontalGuideLines[i]);
        }
        for (int i = 0; i < mVerticalGuideLines.length; i++) {
            mVerticalGuideLines[i] = getNewGuideline(myContext,
                    ConstraintLayout.LayoutParams.VERTICAL, verticalPositions[i]);
            mContainer.addView(mVerticalGuideLines[i]);
        }
    }

    /**
     * get a new Guideline based on the specified orientation and position
     * @param context the context
     * @param orientation orientation of a Guideline
     * @param position position of a Guideline
     * @return a Guideline
     */
    private Guideline getNewGuideline(Context context, int orientation, float position) {
        Guideline guideline = new Guideline(context);
        guideline.setId(ViewCompat.generateViewId());
        ConstraintLayout.LayoutParams lp =
                new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT);
        lp.orientation = orientation;
        lp.guidePercent = position;
        guideline.setLayoutParams(lp);

        return guideline;
    }

    /**
     * Connect the view to the corresponding guidelines based on the input params
     * @param viewId the Id of the view
     * @param row row position to place the view
     * @param column column position to place the view
     */
    private void connectView(int viewId, int row, int column, int rowSpan, int columnSpan,
                             int horizontalGaps, int verticalGaps) {

        // @TODO handle RTL
        // connect Start of the view
        mConstraintSet.connect(viewId, ConstraintSet.START,
                mVerticalGuideLines[column].getId(), ConstraintSet.END, horizontalGaps);

        // connect Top of the view
        mConstraintSet.connect(viewId, ConstraintSet.TOP,
                mHorizontalGuideLines[row].getId(), ConstraintSet.BOTTOM, verticalGaps);

        // connect End of the view
        mConstraintSet.connect(viewId, ConstraintSet.END,
                mVerticalGuideLines[column + columnSpan].getId(),
                ConstraintSet.START, horizontalGaps);

        // connect Bottom of the view
        mConstraintSet.connect(viewId, ConstraintSet.BOTTOM,
                mHorizontalGuideLines[row + rowSpan].getId(),
                ConstraintSet.TOP, verticalGaps);
    }

    /**
     * Arrange the views in the constraint_referenced_ids
     * @return true if all the widgets can be arranged properly else false
     */
    private boolean arrangeWidgets() {
        Pair<Integer, Integer> position;

        // @TODO handle RTL
        for (int i = 0; i < mCount; i++) {
            if (mSpanIds.contains(mIds[i])) {
                // skip the viewId that's already handled by handleSpans
                continue;
            }

            position = getNextPosition();
            if (position.first == -1) {
                // no more available position.
                return false;
            }
            connectView(mIds[i], position.first, position.second,
                    1, 1, mHorizontalGaps, mVerticalGaps);
        }
        return true;
    }

    /**
     * Convert a 1D index to a 2D index that has index for row and index for column
     * @param index index in 1D
     * @return a Pair with row and column as its values.
     */
    private Pair<Integer, Integer> getPositionByIndex(int index) {
        // @TODO handle RTL
        int row;
        int col;

        if (mOrientation.equals(VERTICAL)) {
            row = index % mRows;
            col = index / mRows;
        } else {
            row = index / mColumns;
            col = index % mColumns;
        }
        return new Pair<>(row, col);
    }

    /**
     * Get the next available position for widget arrangement.
     * @return Pair<row, column>
     */
    private Pair<Integer, Integer> getNextPosition() {
        Pair<Integer, Integer> position = new Pair<>(0, 0);
        boolean positionFound = false;

        while (!positionFound) {
            if (mNextAvailableIndex >= mRows * mColumns) {
                return new Pair<>(-1,  -1);
            }

            position = getPositionByIndex(mNextAvailableIndex);

            if (mPositionMatrix[position.first][position.second]) {
                mPositionMatrix[position.first][position.second] = false;
                positionFound = true;
            }

            mNextAvailableIndex++;
        }
        return new Pair<>(position.first, position.second);
    }

    /**
     * Check if the value of the skips is valid
     * @param str skips in string format
     * @return true if it is valid else false
     */
    private boolean isSpansValid(String str) {
        // TODO: check string has a valid format.
        return true;
    }

    /**
     * parse the skips/spans in the string format into a HashMap<index, row_span, col_span>>
     * the format of the input string is index:row_spanxcol_span.
     * index - the index of the starting position
     * row_span - the number of rows to span
     * col_span- the number of columns to span
     * @param str string format of skips or spans
     * @return a hashmap that contains skip information.
     */
    private HashMap<Integer, Pair<Integer, Integer>> parseSpans(String str) {
        if (!isSpansValid(str)) {
            return null;
        }

        HashMap<Integer, Pair<Integer, Integer>> skipMap = new HashMap<>();

        String[] skips = str.split(",");
        String[] indexAndSpan;
        String[] rowAndCol;
        for (String skip: skips) {
            indexAndSpan = skip.trim().split(":");
            rowAndCol = indexAndSpan[1].split("x");
            skipMap.put(Integer.parseInt(indexAndSpan[0]),
                    new Pair<>(Integer.parseInt(rowAndCol[0]), Integer.parseInt(rowAndCol[1])));
        }
        return skipMap;
    }

    /**
     * Handle the span use cases
     * @param spansMap a hashmap that contains span information
     * @return true if the input spans is valid else false
     */
    private boolean handleSpans(int[] mId, HashMap<Integer, Pair<Integer, Integer>> spansMap) {
        int mIdIndex = 0;
        Pair<Integer, Integer> startPosition;
        for (Map.Entry<Integer, Pair<Integer, Integer>> entry : spansMap.entrySet()) {
            startPosition = getPositionByIndex(entry.getKey());
            if (!invalidatePositions(startPosition.first, startPosition.second,
                    entry.getValue().first, entry.getValue().second)) {
                return false;
            }
            connectView(mId[mIdIndex], startPosition.first, startPosition.second,
                    entry.getValue().first,  entry.getValue().second,
                    mHorizontalGaps, mVerticalGaps);
            mSpanIds.add(mId[mIdIndex]);
            mIdIndex++;
        }
        return true;
    }

    /**
     * Make positions in the grid unavailable based on the skips attr
     * @param skipsMap a hashmap that contains skip information
     * @return true if all the skips are valid else false
     */
    private boolean handleSkips(HashMap<Integer, Pair<Integer, Integer>> skipsMap) {
        Pair<Integer, Integer> startPosition;
        for (Map.Entry<Integer, Pair<Integer, Integer>> entry : skipsMap.entrySet()) {
            startPosition = getPositionByIndex(entry.getKey());
            if (!invalidatePositions(startPosition.first, startPosition.second,
                     entry.getValue().first, entry.getValue().second)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Make the specified positions in the grid unavailable.
     * @param startRow the row of the staring position
     * @param startColumn the column of the staring position
     * @param rowSpan how many rows to span
     * @param columnSpan how many columns to span
     * @return true if we could properly invalidate the positions esle false
     */
    private boolean invalidatePositions(int startRow, int startColumn,
                                        int rowSpan, int columnSpan) {
        for (int i = startRow; i < startRow + rowSpan; i++) {
            for (int j = startColumn; j < startColumn + columnSpan; j++) {
                if (i >= mPositionMatrix.length || j >= mPositionMatrix[0].length
                        || !mPositionMatrix[i][j]) {
                    // the position is already occupied.
                    return false;
                }
                mPositionMatrix[i][j] = false;
            }
        }
        return true;
    }

    /**
     * Generate line positions (for the Guideline positioning)
     * @param min min value of the linear spaced positions
     *      * @param max max value of the linear spaced positions
     * @param numPositions number of positions is required
     * @param weights a float array for space weights
     * @return a float array of the corresponding positions
     */
    private float[] getLinePositions(float min, float max, int numPositions, float[] weights) {
        if (weights != null && numPositions - 1 != weights.length) {
            return null;
        }

        float[] positions = new float[numPositions];
        int weightSum = 0;
        for (int i = 0; i < numPositions - 1; i++) {
            weightSum += weights != null ? weights[i] : 1;
        }

        float availableSpace = max - min;
        float baseWeight = availableSpace / weightSum;
        positions[0] = min;
        for (int i = 0; i < numPositions - 1; i++) {
            float w = weights != null ? weights[i] : 1;
            positions[i + 1] = positions[i] + w * baseWeight;
        }
        return positions;
    }

    /**
     * get the string value of spans
     * @return the string value of spans
     */
    public String getSpans() {
        return mStrSpans;
    }

    /**
     * set new spans value and also invoke requestLayout
     * @param spans new spans value
     * @return true if it succeeds otherwise false
     */
    public Boolean setSpans(String spans) {
        if (!isSpansValid(spans)) {
            return false;
        }
        mStrSpans = spans;
        requestLayout();
        return true;
    }

    /**
     * get the string value of skips
     * @return the string value of skips
     */
    public String getSkips() {
        return mStrSkips;
    }

    /**
     * set new skips value and also invoke requestLayout
     * @param skips new spans value
     * @return true if it succeeds otherwise false
     */
    public Boolean setSkips(String skips) {
        if (!isSpansValid(skips)) {
            return false;
        }
        mStrSkips = skips;
        requestLayout();
        return true;
    }
}
