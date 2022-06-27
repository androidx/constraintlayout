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

import android.annotation.SuppressLint;
import androidx.constraintlayout.core.utils.GridEngine;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.R;
import androidx.constraintlayout.widget.VirtualLayout;

import java.util.Arrays;

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
    private static final boolean DEBUG_BOXES = false;
    public static final int VERTICAL = 1;
    public static final int HORIZONTAL = 0;
    private final int MAX_ROWS = 50; // maximum number of rows can be specified.
    private final int MAX_COLUMNS = 50; // maximum number of columns can be specified.
    private final ConstraintSet mConstraintSet = new ConstraintSet();
    private View[] mBoxViews;
    ConstraintLayout mContainer;

    /**
     * number of rows of the grid
     */
    private int mRows;

    /**
     * number of rows set by the XML or API
     */
    private int mRowsSet;

    /**
     * number of columns of the grid
     */
    private int mColumns;

    /**
     * number of columns set by the XML or API
     */
    private int mColumnsSet;

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
     * Indicates whether the input attributes need to be validated
     */
    private boolean mValidateInputs;

    /**
     * Indicates whether to use RTL layout direction
     */
    private boolean mUseRtl;

    /**
     * Ids of the boxViews
     */
    private int[] mBoxViewIds;

    /**
     * Grid engine
     */
    private GridEngine mEngine;

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
                    mRowsSet = a.getInteger(attr, 0);
                } else if (attr == R.styleable.Grid_grid_columns) {
                    mColumnsSet = a.getInteger(attr, 0);
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

            updateActualRowsAndColumns();
            initGridEngine();
            a.recycle();
        }
    }

    /**
     * Compute the actual rows and columns given what was set
     * if 0,0 find the most square rows and columns that fits
     * if 0,n or n,0 scale to fit
     */
    private void updateActualRowsAndColumns() {
        if (mRowsSet == 0 || mColumnsSet == 0) {
            if (mColumnsSet > 0) {
                mColumns = mColumnsSet;
                mRows = (mCount + mColumns -1) / mColumnsSet; // round up
            } else  if (mRowsSet > 0) {
                mRows = mRowsSet;
                mColumns= (mCount + mRowsSet -1) / mRowsSet; // round up
            } else { // as close to square as possible favoring more rows
                mRows = (int)  (1.5 + Math.sqrt(mCount));
                mColumns = (mCount + mRows -1) / mRows;
            }
        } else {
            mRows = mRowsSet;
            mColumns = mColumnsSet;
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
     */
    private void generateGrid() {
        if (mContainer == null || mConstraintSet == null || mRows < 1 || mColumns < 1) {
            return;
        }

        // create viewBoxes to constrain widgets
        buildBoxes();

        // setup the Grid engine
        if (mStrSkips != null) {
            mEngine.setSkips(mStrSkips);
        }
        if (mStrSpans != null) {
            mEngine.setSpans(mStrSpans);
        }
        mEngine.setup();

        // Add constraints to each widget
        constrainWidgets();

        mConstraintSet.applyTo(mContainer);
    }

    /**
     * Initiate the Grid engine
     */
    private void initGridEngine() {
        mEngine = new GridEngine(mRows, mColumns, mCount);
        if (mOrientation == 1) {
            mEngine.setOrientation(mOrientation);
        }
    }

    /**
     * parse the weights/pads in the string format into a float array
     *
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
     * Arrange the widgets in the constraint_referenced_ids
     */
    private void constrainWidgets() {
        for (int i = 0; i < mCount; i++) {

            // connect left of the view
            mConstraintSet.connect(mIds[i], ConstraintSet.LEFT,
                    mBoxViewIds[mEngine.leftOfWidget(i)], ConstraintSet.LEFT);

            // connect Top of the view
            mConstraintSet.connect(mIds[i], ConstraintSet.TOP,
                    mBoxViewIds[mEngine.topOfWidget(i)], ConstraintSet.TOP);

            // connect right of the view
            mConstraintSet.connect(mIds[i], ConstraintSet.RIGHT,
                    mBoxViewIds[mEngine.rightOfWidget(i)], ConstraintSet.RIGHT);

            // connect Bottom of the view
            mConstraintSet.connect(mIds[i], ConstraintSet.BOTTOM,
                    mBoxViewIds[mEngine.bottomOfWidget(i)], ConstraintSet.BOTTOM);
        }
    }

    /**
     * Check if the value of the rowWeights or columnsWeights is valid
     *
     * @param str rowWeights/columnsWeights in string format
     * @return true if it is valid else false
     */
    private boolean isWeightsValid(String str) {
        if (str == null || str.isEmpty() || str.trim().isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Visualize the boxViews that are used to constraint widgets.
     *
     * @param canvas canvas to visualize the boxViews
     */
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Visualize the viewBoxes if isInEditMode() is true
        if (!isInEditMode()) {
            return;
        }
        @SuppressLint("DrawAllocation")
        Paint paint = new Paint(); // only used in design time

        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        int myTop = getTop();
        int myLeft = getLeft();
        int myBottom = getBottom();
        int myRight = getRight();
        for (View box : mBoxViews) {
            int l = box.getLeft() - myLeft;
            int t = box.getTop() - myTop;
            int r = box.getRight() - myLeft;
            int b = box.getBottom() - myTop;
            canvas.drawRect(l, 0, r, myBottom - myTop, paint);
            canvas.drawRect(0, t, myRight - myLeft, b, paint);
        }
    }

    /**
     * Set chain between boxView horizontally
     */
    private void setBoxViewHorizontalChains() {
        int gridId = getId();
        int maxVal = Math.max(mRows, mColumns);
        int minVal = Math.min(mRows, mColumns);
        float[] columnWeights = parseWeights(mColumns, mStrColumnWeights);

        // chain all the views on the longer side (either horizontal or vertical)
        if (mColumns == 1) {
            mConstraintSet.center(mBoxViewIds[0], gridId, ConstraintSet.LEFT, 0, gridId,
                    ConstraintSet.RIGHT, 0, 0.5f);
            return;
        }
        if (maxVal == mColumns) {
            mConstraintSet.createHorizontalChain(gridId, ConstraintSet.LEFT, gridId,
                    ConstraintSet.RIGHT, mBoxViewIds, columnWeights,
                    ConstraintSet.CHAIN_SPREAD_INSIDE);
            for (int i = 1; i < mBoxViews.length; i++) {
                mConstraintSet.setMargin(mBoxViewIds[i], ConstraintSet.LEFT, (int) mHorizontalGaps);
            }
            return;
        }

        // chain partial veriws on the shorter side (either horizontal or vertical)
        // add constraints to the parent for the non-chained views
        mConstraintSet.createHorizontalChain(gridId, ConstraintSet.LEFT, gridId,
                ConstraintSet.RIGHT, Arrays.copyOf(mBoxViewIds, minVal), columnWeights,
                ConstraintSet.CHAIN_SPREAD_INSIDE);

        for (int i = 1; i < mBoxViews.length; i++) {
            if (i < minVal) {
                mConstraintSet.setMargin(mBoxViewIds[i], ConstraintSet.LEFT, (int) mHorizontalGaps);
            } else {
                mConstraintSet.connect(mBoxViewIds[i], ConstraintSet.LEFT,
                        gridId, ConstraintSet.LEFT);
                mConstraintSet.connect(mBoxViewIds[i], ConstraintSet.RIGHT,
                        gridId, ConstraintSet.RIGHT);
            }
        }
    }

    /**
     * Set chain between boxView vertically
     */
    private void setBoxViewVerticalChains() {
        int gridId = getId();
        int maxVal = Math.max(mRows, mColumns);
        int minVal = Math.min(mRows, mColumns);
        float[] rowWeights = parseWeights(mRows, mStrRowWeights);

        // chain all the views on the longer side (either horizontal or vertical)
        if (mRows == 1) {
            mConstraintSet.center(mBoxViewIds[0], gridId, ConstraintSet.TOP, 0, gridId,
                    ConstraintSet.BOTTOM, 0, 0.5f);
            return;
        }
        if (maxVal == mRows) {
            mConstraintSet.createVerticalChain(gridId, ConstraintSet.TOP, gridId,
                    ConstraintSet.BOTTOM, mBoxViewIds, rowWeights,
                    ConstraintSet.CHAIN_SPREAD_INSIDE);
            for (int i = 1; i < mBoxViews.length; i++) {
                mConstraintSet.setMargin(mBoxViewIds[i], ConstraintSet.TOP, (int) mVerticalGaps);
            }
            return;
        }

        // chain partial views on the shorter side (either horizontal or vertical)
        // add constraints to the parent for the non-chained views
        mConstraintSet.createVerticalChain(gridId, ConstraintSet.TOP, gridId,
                ConstraintSet.BOTTOM, Arrays.copyOf(mBoxViewIds, minVal), rowWeights,
                ConstraintSet.CHAIN_SPREAD_INSIDE);
        for (int i = 1; i < mBoxViews.length; i++) {
            if (i < minVal) {
                mConstraintSet.setMargin(mBoxViewIds[i], ConstraintSet.TOP, (int) mVerticalGaps);
            } else {
                mConstraintSet.connect(mBoxViewIds[i], ConstraintSet.TOP,
                        gridId, ConstraintSet.TOP);
                mConstraintSet.connect(mBoxViewIds[i], ConstraintSet.BOTTOM,
                        gridId, ConstraintSet.BOTTOM);
            }
        }
    }

    private View makeNewView() {
        View v = new View(getContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            v.setId(View.generateViewId());
        }
        v.setVisibility(INVISIBLE);
        if (DEBUG_BOXES) {
            v.setVisibility(VISIBLE);
            v.setBackgroundColor(0xFF880088);
        }
        ConstraintLayout.LayoutParams params =
                new ConstraintLayout.LayoutParams(0, 0);

        mContainer.addView(v, params);
        return v;
    }

    /**
     * create boxViews for constraining widgets
     */
    private void buildBoxes() {
        int boxCount = Math.max(mRows, mColumns);
        if (mBoxViews == null) { // no box views build all
            mBoxViews = new View[boxCount];
            for (int i = 0; i < mBoxViews.length; i++) {
                mBoxViews[i] = makeNewView(); // need to remove old Views
            }
        } else {
            if (boxCount != mBoxViews.length) {
                View[] temp = new View[boxCount];
                for (int i = 0; i < boxCount; i++) {
                    if (i < mBoxViews.length) { // use old one
                        temp[i] = mBoxViews[i];
                    } else { // make new one
                        temp[i] = makeNewView();
                    }
                }
                // remove excess
                for (int j = boxCount; j < mBoxViews.length; j++) {
                    View view = mBoxViews[j];
                    mContainer.removeView(view);
                }
                mBoxViews = temp;
            }
        }

        mBoxViewIds = new int[boxCount];
        for (int i = 0; i < mBoxViews.length; i++) {
            mBoxViewIds[i] = mBoxViews[i].getId();
        }

        setBoxViewVerticalChains();
        setBoxViewHorizontalChains();
    }

    /**
     * get the value of rows
     *
     * @return the value of rows
     */
    public int getRows() {
        return mRowsSet;
    }

    /**
     * set new rows value and also invoke initVariables and invalidate
     *
     * @param rows new rows value
     */
    public void setRows(int rows) {
        if (rows > MAX_ROWS) {
            return;
        }

        if (mRowsSet == rows) {
            return;
        }

        mRowsSet = rows;
        updateActualRowsAndColumns();

        mEngine.setRows(mRows);
        generateGrid();
        invalidate();
    }

    /**
     * get the value of columns
     * @return the value of columns
     */
    public int getColumns() {
        return mColumnsSet;
    }

    /**
     * set new columns value and also invoke initVariables and invalidate
     *
     * @param columns new rows value
     */
    public void setColumns(int columns) {
        if (columns > MAX_COLUMNS) {
            return;
        }

        if (mColumnsSet == columns) {
            return;
        }

        mColumnsSet = columns;
        updateActualRowsAndColumns();

        mEngine.setColumns(mColumns);
        generateGrid();
        invalidate();
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
     *
     * @param orientation new orientation value
     */
    public void setOrientation(int orientation) {
        if (!(orientation == HORIZONTAL || orientation == VERTICAL)) {
            return;
        }

        if (mOrientation == orientation) {
            return;
        }

        mOrientation = orientation;
        mEngine.setOrientation(mOrientation);
        generateGrid();
        invalidate();
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
     *
     * @param spans new spans value
     */
    public void setSpans(String spans) {
        if (mStrSpans != null && mStrSpans.equals(spans)) {
            return;
        }

        mStrSpans = spans;
        mEngine.setSpans(mStrSpans);
        generateGrid();
        invalidate();
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
     *
     * @param skips new spans value
     */
    public void setSkips(String skips) {
        if (mStrSkips != null && mStrSkips.equals(skips)) {
            return;
        }

        mStrSkips = skips;
        mEngine.setSkips(mStrSkips);
        generateGrid();
        invalidate();
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
     *
     * @param rowWeights new rowWeights value
     */
    public void setRowWeights(String rowWeights) {
        if (!isWeightsValid(rowWeights)) {
            return;
        }

        if (mStrRowWeights != null && mStrRowWeights.equals(rowWeights)) {
            return;
        }

        mStrRowWeights = rowWeights;
        generateGrid();
        invalidate();
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
     *
     * @param columnWeights new columnWeights value
     */
    public void setColumnWeights(String columnWeights) {
        if (!isWeightsValid(columnWeights)) {
            return;
        }

        if (mStrColumnWeights != null && mStrColumnWeights.equals(columnWeights)) {
            return;
        }

        mStrColumnWeights = columnWeights;
        generateGrid();
        invalidate();
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
     *
     * @param horizontalGaps new horizontalGaps value
     */
    public void setHorizontalGaps(float horizontalGaps) {
        if (horizontalGaps < 0) {
            return;
        }

        if (mHorizontalGaps == horizontalGaps) {
            return;
        }

        mHorizontalGaps = horizontalGaps;
        generateGrid();
        invalidate();
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
     *
     * @param verticalGaps new verticalGaps value
     */
    public void setVerticalGaps(float verticalGaps) {
        if (verticalGaps < 0) {
            return;
        }

        if (mVerticalGaps == verticalGaps) {
            return;
        }

        mVerticalGaps = verticalGaps;
        generateGrid();
        invalidate();
    }
}
