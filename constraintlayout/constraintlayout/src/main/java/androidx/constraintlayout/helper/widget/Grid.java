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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import androidx.constraintlayout.motion.widget.Debug;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.R;
import androidx.constraintlayout.widget.VirtualLayout;

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
    public static final int VERTICAL = 1;
    public static final int HORIZONTAL = 0;
    private final int mMaxRows = 50; // maximum number of rows can be specified.
    private final int mMaxColumns = 50; // maximum number of columns can be specified.
    private final ConstraintSet mConstraintSet = new ConstraintSet();
    private Paint mPaint = new Paint();
    private View[] mBoxViews;
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
    private float mHorizontalGaps;

    /**
     * Vertical gaps in Dp
     */
    private float mVerticalGaps;

    /**
     * orientation of the view arrangement - vertical or horizontal
     */
    private int mOrientation;

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
     * Ids boxViews where to specify constraints
     */
    private int[] mAnchorIds;

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
                    mOrientation = a.getInt(attr, 0);
                } else if (attr == R.styleable.Grid_grid_horizontalGaps) {
                    mHorizontalGaps = a.getDimension(attr, 0);
                } else if (attr == R.styleable.Grid_grid_verticalGaps) {
                    mVerticalGaps = a.getDimension(attr, 0);
                } else if (attr == R.styleable.Grid_grid_validateInputs) {
                    // @TODO handle validation
                    mValidateInputs = a.getBoolean(attr, false);
                }  else if (attr == R.styleable.Grid_grid_useRtl) {
                    // @TODO handle RTL
                    mUseRtl = a.getBoolean(attr, false);
                }
            }
            Log.v(TAG, " >>>>>>>>>>> col = " + mColumns);
            initVariables();
            a.recycle();
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        mContainer = (ConstraintLayout) getParent();
        mConstraintSet.clone(mContainer);

        generateGrid(false);
    }

    /**
     * generate the Grid form based on the input attributes
     * @param isUpdate whether to update the existing grid (true) or create a new one (false)
     * @return true if all the inputs are valid else false
     */
    private boolean generateGrid(boolean isUpdate) {
        if (mContainer == null || mConstraintSet == null) {
            return false;
        }

        if (isUpdate) {
            for (int i = 0; i < mPositionMatrix.length; i++) {
                for (int j = 0; j < mPositionMatrix[0].length; j++) {
                    mPositionMatrix[i][j] = true;
                }
            }
            mSpanIds.clear();
        }

        mNextAvailableIndex = 0;
        boolean isSuccess = true;

        buildBoxes(isUpdate);

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
     * Connect the view to the corresponding viewBoxes based on the input params
     * @param viewId the Id of the view
     * @param row row position to place the view
     * @param column column position to place the view
     */
    private void connectView(int viewId, int row, int column, int rowSpan, int columnSpan,
                             float horizontalGaps, float verticalGaps) {

        // @TODO handle RTL
        int leftSide = column == 0 ? ConstraintSet.START : ConstraintSet.END;
        int topSide = row == 0 ? ConstraintSet.TOP : ConstraintSet.BOTTOM;
        int rightSide = column + columnSpan == mColumns ? ConstraintSet.END : ConstraintSet.START;
        int bottomSide = row + rowSpan == mRows ? ConstraintSet.BOTTOM : ConstraintSet.TOP;

        // connect Start of the view
        mConstraintSet.connect(viewId, ConstraintSet.START,
                mAnchorIds[column], leftSide, (int) horizontalGaps / 2);

        // connect Top of the view
        mConstraintSet.connect(viewId, ConstraintSet.TOP,
                mAnchorIds[row], topSide, (int) verticalGaps / 2);

        // connect End of the view
        int rightIndex = rightSide
                == ConstraintSet.END ? mAnchorIds.length - 1 : column + columnSpan;
        mConstraintSet.connect(viewId, ConstraintSet.END,
                mAnchorIds[rightIndex], rightSide, (int) horizontalGaps / 2);

        // connect Bottom of the view
        int bottomIndex = bottomSide
                == ConstraintSet.BOTTOM ? mAnchorIds.length - 1 : row + rowSpan;
        mConstraintSet.connect(viewId, ConstraintSet.BOTTOM,
                mAnchorIds[bottomIndex], bottomSide, (int) verticalGaps / 2);
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

        if (mOrientation == 1) {
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
     * Check if the value of the spans/skips is valid
     * @param str spans/skips in string format
     * @return true if it is valid else false
     */
    private boolean isSpansValid(String str) {
        // TODO: check string has a valid format.
        return true;
    }

    /**
     * Check if the value of the rowWeights or columnsWeights is valid
     * @param str rowWeights/columnsWeights in string format
     * @return true if it is valid else false
     */
    private boolean isWeightsValid(String str) {
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
     * @return true if we could properly invalidate the positions else false
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
     * Generate line positions (for the viewBoxes positioning)
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
     * Visualize the boxViews that are used to constraint widgets.
     * @param canvas canvas to visualize the boxViews
     */
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Visualize the viewBoxes if isInEditMode() is true
//        if (!isInEditMode()) {
//            return;
//        }
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL);
        int myTop = getTop();
        int myLeft = getLeft();
        int myBottom = getBottom();
        int myRight = getRight();
        for (int i = 0; i < mBoxViews.length; i++) {
            View box = mBoxViews[i];
            int l = box.getLeft() - myLeft;
            int t = box.getTop() - myTop;
            int r = box.getRight() - myLeft;
            int b = box.getBottom() - myTop;
            canvas.drawRect(l, 0, r, myBottom - myTop, mPaint);
            canvas.drawRect(0, t, myRight - myLeft, b, mPaint);
        }
    }

    /**
     * create boxViews for constraining widgets
     * @param isUpdate whether to update existing boxViews (true) or create new ones (false)
     */
    private void buildBoxes(boolean isUpdate) {
        float[] rowWeights = parseWeights(mRows, mStrRowWeights);
        float[] columnWeights = parseWeights(mColumns, mStrColumnWeights);

        float[] verticalPositions = getLinePositions(0, 1,
                mRows + 1, rowWeights);
        float[] horizontalPositions = getLinePositions(0, 1,
                mColumns + 1, columnWeights);

        // if the attr update dosen't invovle rows or columns, we only need to
        // update the positions of the boxVies.
        if (isUpdate) {
            for (int i = 0; i < mBoxViews.length; i++) {
                int row = Math.min(mRows - 2, i);
                int col = Math.min(mColumns - 2, i);
                updateBoxPosition(mBoxViews[i], horizontalPositions[col + 1],
                        verticalPositions[row + 1]);
            }
            return;
        }

        int boxCount = Math.max(mRows - 1, mColumns - 1);
        mAnchorIds = new int[boxCount + 2];
        int gridId = getId();
        mAnchorIds[0] = gridId;
        mAnchorIds[mAnchorIds.length - 1] = gridId;

        if (mBoxViews == null || !isUpdate) {
            mBoxViews = new View[boxCount];
            for (int i = 0; i < mBoxViews.length; i++) {
                mBoxViews[i] = new View(getContext()); // need to remove old Views
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    mBoxViews[i].setId(View.generateViewId());
                }
                ConstraintLayout.LayoutParams params =
                        new ConstraintLayout.LayoutParams(1, 1);
                int row = Math.min(mRows - 2, i);
                int col = Math.min(mColumns - 2, i);

                params.leftToLeft = gridId;
                params.topToTop = gridId;
                params.bottomToBottom = gridId;
                params.rightToRight = gridId;
                params.horizontalBias = horizontalPositions[col + 1];
                params.verticalBias = verticalPositions[row + 1];
                mContainer.addView(mBoxViews[i], params);
                mAnchorIds[i + 1] = mBoxViews[i].getId();
            }
        }
    }

    /**
     * Update the horizontal and vertical postion of a box view
     * @param box box View to be updated
     * @param horizontalPosition horizontal position
     * @param verticalPosition vertical position
     */
    private void updateBoxPosition(View box,
                                   float horizontalPosition, float verticalPosition) {
        ConstraintLayout.LayoutParams params =
                (ConstraintLayout.LayoutParams) box.getLayoutParams();
        params.horizontalBias = horizontalPosition;
        params.verticalBias = verticalPosition;
        box.setLayoutParams(params);
    }

    /**
     * get the value of rows
     * @return the value of rows
     */
    public int getRows() {
        return mRows;
    }

    /**
     * set new rows value and also invoke initVariables and invalidate
     * @param rows new rows value
     * @return true if it succeeds otherwise false
     */
    public boolean setRows(int rows) {
        if (rows < 2 || rows > mMaxRows) {
            return false;
        }

        if (mRows == rows) {
            return true;
        }

        mRows = rows;
        initVariables();
        generateGrid(false);
        invalidate();
        return true;
    }

    /**
     * get the value of columns
     * @return the value of columns
     */
    public int getColumns() {
        return mColumns;
    }

    /**
     * set new columns value and also invoke initVariables and invalidate
     * @param columns new rows value
     * @return true if it succeeds otherwise false
     */
    public boolean setColumns(int columns) {
        Debug.logStack(TAG, " >>>>>>>>>>>>> col " + columns, 5);
        if (columns < 2 || columns > mMaxColumns) {
            return false;
        }

        if (mColumns == columns) {
            return true;
        }

        mColumns = columns;
        initVariables();
        generateGrid(false);
        invalidate();
        return true;
    }


    /**
     * get the value of orientation
     * @return the value of orientation
     */
    public int getOrientation() {
        return mOrientation;
    }

    /**
     * set new orientation value and also invoke invalidate
     * @param orientation new orientation value
     * @return true if it succeeds otherwise false
     */
    public boolean setOrientation(int orientation) {
        if (!(orientation == HORIZONTAL || orientation == VERTICAL)) {
            return false;
        }

        if (mOrientation == orientation) {
            return true;
        }

        mOrientation = orientation;
        generateGrid(true);
        invalidate();
        return true;
    }

    /**
     * get the string value of spans
     * @return the string value of spans
     */
    public String getSpans() {
        return mStrSpans;
    }

    /**
     * set new spans value and also invoke invalidate
     * @param spans new spans value
     * @return true if it succeeds otherwise false
     */
    public Boolean setSpans(String spans) {
        if (!isSpansValid(spans)) {
            return false;
        }

        if (mStrSpans != null && mStrSpans.equals(spans)) {
            return true;
        }

        mStrSpans = spans;
        generateGrid(true);
        invalidate();
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
     * set new skips value and also invoke invalidate
     * @param skips new spans value
     * @return true if it succeeds otherwise false
     */
    public Boolean setSkips(String skips) {
        if (!isSpansValid(skips)) {
            return false;
        }

        if (mStrSkips != null && mStrSkips.equals(skips)) {
            return true;
        }

        mStrSkips = skips;
        generateGrid(true);
        invalidate();
        return true;
    }

    /**
     * get the string value of rowWeights
     * @return the string value of rowWeights
     */
    public String getRowWeights() {
        return mStrRowWeights;
    }

    /**
     * set new rowWeights value and also invoke invalidate
     * @param rowWeights new rowWeights value
     * @return true if it succeeds otherwise false
     */
    public Boolean setRowWeights(String rowWeights) {
        if (!isWeightsValid(rowWeights)) {
            return false;
        }

        if (mStrRowWeights != null && mStrRowWeights.equals(rowWeights)) {
            return true;
        }

        mStrRowWeights = rowWeights;
        generateGrid(true);
        invalidate();
        return true;
    }

    /**
     * get the string value of columnWeights
     * @return the string value of columnWeights
     */
    public String getColumnWeights() {
        return mStrColumnWeights;
    }

    /**
     * set new columnWeights value and also invoke invalidate
     * @param columnWeights new columnWeights value
     * @return true if it succeeds otherwise false
     */
    public Boolean setColumnWeights(String columnWeights) {
        if (!isWeightsValid(columnWeights)) {
            return false;
        }

        if (mStrColumnWeights != null && mStrColumnWeights.equals(columnWeights)) {
            return true;
        }

        mStrColumnWeights = columnWeights;
        generateGrid(true);
        invalidate();
        return true;
    }

    /**
     * get the value of horizontalGaps
     * @return the value of horizontalGaps
     */
    public float getHorizontalGaps() {
        return mHorizontalGaps;
    }

    /**
     *  set new horizontalGaps value and also invoke invalidate
     * @param horizontalGaps new horizontalGaps value
     * @return true if it succeeds otherwise false
     */
    public boolean setHorizontalGaps(float horizontalGaps) {
        if (horizontalGaps < 0) {
            return false;
        }

        if (mHorizontalGaps == horizontalGaps) {
            return true;
        }

        mHorizontalGaps = horizontalGaps;
        generateGrid(true);
        invalidate();
        return true;
    }

    /**
     * get the value of verticalGaps
     * @return the value of verticalGaps
     */
    public float getVerticalGaps() {
        return mVerticalGaps;
    }

    /**
     * set new verticalGaps value and also invoke invalidate
     * @param verticalGaps new verticalGaps value
     * @return true if it succeeds otherwise false
     */
    public boolean setVerticalGaps(float verticalGaps) {
        if (verticalGaps < 0) {
            return false;
        }

        if (mVerticalGaps == verticalGaps) {
            return true;
        }

        mVerticalGaps = verticalGaps;
        generateGrid(true);
        invalidate();
        return true;
    }
}
