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
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.View;

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

    /**
     * class that stores the relevant span information
     */
    static class Span {
        int mId;
        int mStartRow;
        int mStartColumn;
        int mRowSpan;
        int mColumnSpan;
        String mGravity;

        Span(int id, int startRow, int startColumn,
                    int rowSpan, int columnSpan, String gravity) {
            this.mId = id;
            this.mStartRow = startRow;
            this.mStartColumn = startColumn;
            this.mRowSpan = rowSpan;
            this.mColumnSpan = columnSpan;
            this.mGravity = gravity;
        }

        public int getId() {
            return mId;
        }

        public int getStartRow() {
            return mStartRow;
        }

        public int getStartColumn() {
            return mStartColumn;
        }

        public int getRowSpan() {
            return mRowSpan;
        }

        public int getColumnSpan() {
            return mColumnSpan;
        }

        public String getGravity() {
            return mGravity;
        }
    }

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
                } else if (attr == R.styleable.Grid_grid_orientation) {
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
        createGuidelines(mRows, mColumns);

        if (mStrSkips != null && !mStrSkips.trim().isEmpty()) {
            HashMap<Integer, Pair<Integer, Integer>> mSkipMap = parseSkips(mStrSkips);
            if (mSkipMap != null) {
                handleSkips(mSkipMap);
            }
        }

        if (mStrSpans != null && !mStrSpans.trim().isEmpty()) {
            Span[] mSpans = parseSpans(mStrSpans);
            if (mSpans != null) {
                handleSpans(mSpans);
            }
        }

        arrangeWidgets();
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
     * create vertical and horizontal guidelines based on mRows and mColumns
     * @param rows number of rows is required for grid
     * @param columns number of columns is required for grid
     */
    private void createGuidelines(int rows, int columns) {

        float[] horizontalPositions = getLinspace(0, 1, rows + 1);
        float[] verticalPositions = getLinspace(0, 1, columns + 1);

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
     * @param gravity gravity info, including  top, left, bottom, right, guideline,start,end
     */
    private void connectView(int viewId, int row, int column, int rowSpan, int columnSpan,
                             int horizontalGaps, int verticalGaps, String gravity) {

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

        // handle gravity
        if (!gravity.trim().equals("")) {
            handleGravity(viewId, gravity);
        }

        mConstraintSet.applyTo(mContainer);
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
                    1, 1, mHorizontalGaps, mVerticalGaps, "");
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
     * Handle the gravity. The value could be t, r, b, l, s, e, tl, br, etc.
     * t = top, r = right, b = bottom l = left, s = start, e = end
     * @param viewId the id of a view
     * @param gravity the gravity
     */
    private void handleGravity(int viewId, String gravity) {
        for (int i = 0; i < gravity.length(); i++) {
            // @TODO handle RTL
            switch (gravity.charAt(i)) {
                case 't':
                    mConstraintSet.setVerticalBias(viewId, 0);
                    break;
                case 'r':
                    mConstraintSet.setHorizontalBias(viewId, 1);
                    break;
                case 'b':
                    mConstraintSet.setVerticalBias(viewId, 1);
                    break;
                case 'l':
                    mConstraintSet.setHorizontalBias(viewId, 0);
                    break;
                case 's':
                    mConstraintSet.setHorizontalBias(viewId, 0);
                    break;
                case 'e':
                    mConstraintSet.setHorizontalBias(viewId, 1);
                    break;
                default:
                    Log.w(TAG, "unknown gravity value: " + gravity.charAt(i));
            }
        }
    }

    /**
     * Check if the value of the Spans is valid
     * @param mStrSpans spans in string format
     * @return true if it is valid else false
     */
    private boolean isSpansValid(String mStrSpans) {
        // TODO: check string has a valid format.
        return true;
    }

    /**
     * Parse the spans in the string format into a span object
     * the format of a span is viewId|index:rowSpanxcolumnSpan-gravity
     * viewID - The id of a view in the constraint_referenced_ids list
     * index - the index of the starting position
     * row_span - The number of rows to span
     * col_span- The number of columns to span
     * gravity (optional) - letters t, l, b, r, s ,e = top, left, bottom, right, start, end.
     *  Two letters could be used together (e.g., tl, br, etc.)
     * @param strSpans Grid spans in the string format
     * @return a HashMap contains span information of individual views.
     */
    private Span[] parseSpans(String strSpans) {
        if (!isSpansValid(strSpans)) {
            return null;
        }

        String[] spans = strSpans.split(",");
        Span[] spanArray = new Span[spans.length];

        for (int i = 0; i < spans.length; i++) {
            String[] idAndRest = spans[i].trim().split(":");
            String[] startPositionAndRest = idAndRest[1].split("#");
            String[] rowSpanAndRest = startPositionAndRest[1].split("x");
            String[] colSpanAndGravity = rowSpanAndRest[1].split("-");

            int id = findId(mContainer, idAndRest[0]);
            Pair<Integer, Integer> startPosition =
                    getPositionByIndex(Integer.parseInt(startPositionAndRest[0]));
            int rowSpan = Integer.parseInt(rowSpanAndRest[0]);
            int columnSpan = Integer.parseInt(colSpanAndGravity[0]);
            String gravity = colSpanAndGravity.length > 1 ? colSpanAndGravity[1] : "";

            spanArray[i] = new Span(id, startPosition.first, startPosition.second,
                    rowSpan, columnSpan, gravity);
        }
        return spanArray;
    }

    /**
     * Handle the span use cases
     * @param spans a array of span object
     * @return true if the input spans is valid else false
     */
    private boolean handleSpans(Span[] spans) {
        for (Span span : spans) {
            if (!invalidatePositions(span.mStartRow, span.mStartColumn,
                    span.mRowSpan, span.mColumnSpan)) {
                // Try to place the widget to the skipped space
                return false;
            }
            connectView(span.mId, span.mStartRow, span.mStartColumn, span.mRowSpan,
                    span.mColumnSpan, mHorizontalGaps, mVerticalGaps, span.mGravity);
            mSpanIds.add(span.mId);
        }
        return true;
    }

    /**
     * Check if the value of the skips is valid
     * @param mStrSkips skips in string format
     * @return true if it is valid else false
     */
    private boolean isSkipsValid(String mStrSkips) {
        // TODO: check string has a valid format.
        return true;
    }

    /**
     * parse the skips in the string format into a HashMap<index, row_span, col_span>>
     * the format of the input string is index:row_spanxcol_span.
     * index - the index of the starting position
     * row_span - the number of rows to span
     * col_span- the number of columns to span
     * @param strSkips string format of skips
     * @return a hashmap that contains skip information.
     */
    private HashMap<Integer, Pair<Integer, Integer>> parseSkips(String strSkips) {
        // TODO: check string has a valid format.
        if (!isSkipsValid(strSkips)) {
            return null;
        }

        HashMap<Integer, Pair<Integer, Integer>> skipMap = new HashMap<>();

        String[] skips = strSkips.split(",");
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

    // From ConstraintHelper -> move to a util function
    /**
     * Iterate through the container's children to find a matching id.
     * Slow path, seems necessary to handle dynamic modules resolution...
     *
     * @param container the parent container - a ConstraintLayout in this case
     * @param idString the string format of a view Id
     * @return the actual viewId in Integer
     */
    private int findId(ConstraintLayout container, String idString) {
        if (idString == null || container == null) {
            return 0;
        }
        Resources resources = myContext.getResources();
        if (resources == null) {
            return 0;
        }
        final int count = container.getChildCount();
        for (int j = 0; j < count; j++) {
            View child = container.getChildAt(j);
            if (child.getId() != -1) {
                String res = null;
                try {
                    res = resources.getResourceEntryName(child.getId());
                } catch (android.content.res.Resources.NotFoundException e) {
                    // nothing
                }
                if (idString.equals(res)) {
                    return child.getId();
                }
            }
        }
        return 0;
    }

    /**
     * Generate linearly spaced positions (for the Guideline positioning)
     * @param min min value of the linear spaced positions
     * @param max max value of the linear spaced positions
     * @param positions number of positions in the space
     * @return an float array of the corresponding positions
     */
    private float[] getLinspace(float min, float max, int positions) {
        float[] d = new float[positions];
        for (int i = 0; i < positions; i++) {
            d[i] = min + i * (max - min) / (positions - 1);
        }
        return d;
    }

    public void setStrSpans(String strSpans) {
        mStrSpans = strSpans;
    }

    public String getStrSpans() {
        return mStrSpans;
    }

    public void setStrSkips(String strSkips) {
        mStrSkips = strSkips;
    }

    public String getStrSkips() {
        return mStrSkips;
    }
}
