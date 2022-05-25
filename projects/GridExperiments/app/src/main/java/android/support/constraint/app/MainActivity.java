/*
 * Copyright (C) 2020 The Android Open Source Project
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

package android.support.constraint.app;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.Grid;

/**
 * MainActivity for Grid helper
 */
public class MainActivity extends AppCompatActivity {

    private Grid mGrid;
    private int mHorizontalGapsIndex = 0;
    private int mVerticalGapsIndex = 0;
    private int mRowSize = 3;
    private int mColSize = 3;
    private int mRowIndex = 0;
    private int mColIndex = 0;
    private int mRowWeightsIndex = 0;
    private int mColumnWeightsIndex = 0;
    private int mSkipsIndex = 0;
    private int mSpansIndex = 0;
    private int mOrientationIndex = 0;

    private int[] mGaps = new int[] {0, 20};
    private int[] mSizes = new int[] {3, 4};
    private String[] mThreeWeights = new String[] {"1,1,1", "1,2,2"};
    private String[] mFourWeights = new String[] {"1,1,1,1", "1,2,1,1"};
    private String[] mSkips = new String[] {"", "0:1x2, 4:1x1"};
    private String[] mSpans = new String[] {"", "6:1x2"};
    private String[] mOrientations = new String[] {"horizontal", "vertical"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.responsive);

        mGrid = findViewById(R.id.grid);
        mGrid.setRows(mSizes[0]);
        mGrid.setColumns(mSizes[0]);
        mGrid.setRowWeights(mThreeWeights[0]);
        mGrid.setColumnWeights(mThreeWeights[0]);
        mGrid.setSkips(mSkips[0]);
        mGrid.setSpans(mSpans[0]);
        mGrid.setVerticalGaps(mGaps[0]);
        mGrid.setHorizontalGaps(mGaps[0]);
        mGrid.setOrientation(mOrientations[0]);
    }

    /**
     * toggle rows value
     * @param v
     */
    public void toggleRows(View v) {
        mRowSize = mSizes[++mRowIndex % 2];
        mGrid.setRows(mRowSize);
    }

    /**
     * toggle columns value
     * @param v
     */
    public void toggleColumns(View v) {
        mColSize = mSizes[++mColIndex % 2];
        mGrid.setColumns(mColSize);
    }

    /**
     * toggle rowWeights value
     * @param v
     */
    public void toggleRowWeights(View v) {
        if (mRowSize == 3) {
            mGrid.setRowWeights(mThreeWeights[++mRowWeightsIndex % 2]);
        } else {
            mGrid.setRowWeights(mFourWeights[++mRowWeightsIndex % 2]);
        }
    }

    /**
     * toggle columnWeigths value
     * @param v
     */
    public void toggleColWeights(View v) {
        if (mColSize == 3) {
            mGrid.setColumnWeights(mThreeWeights[++mColumnWeightsIndex % 2]);
        } else {
            mGrid.setColumnWeights(mFourWeights[++mColumnWeightsIndex % 2]);
        }

    }

    /**
     * toggle verticalGaps value
     * @param v
     */
    public void toggleVerticalGaps(View v) {
        mGrid.setVerticalGaps(mGaps[++mVerticalGapsIndex % 2]);
    }

    /**
     * toggle horizontalGaps value
     * @param v
     */
    public void toggleHorizontalGaps(View v) {
        mGrid.setHorizontalGaps(mGaps[++mHorizontalGapsIndex % 2]);
    }

    /**
     * toggle skips value
     * @param v
     */
    public void toggleSkips(View v) {
        mGrid.setSkips(mSkips[++mSkipsIndex % 2]);
    }

    /**
     * toggle spans value
     * @param v
     */
    public void toggleSpans(View v) {
        mGrid.setSpans(mSpans[++mSpansIndex % 2]);
    }

    /**
     * toggle orientation value
     * @param v
     */
    public void toggleOrientation(View v) {
        mGrid.setOrientation(mOrientations[++mOrientationIndex % 2]);
    }

    /**
     * toggle all value
     * @param v
     */
    public void toggleAll(View v) {
        toggleRows(v);
        toggleColumns(v);
        toggleRowWeights(v);
        toggleColWeights(v);
        toggleVerticalGaps(v);
        toggleHorizontalGaps(v);
        toggleSkips(v);
        toggleSpans(v);
        toggleOrientation(v);
    }

    /**
     * restore to the default value.
     * @param v
     */
    public void restore(View v) {
        mHorizontalGapsIndex = 0;
        mVerticalGapsIndex = 0;
        mRowSize = 3;
        mColSize = 3;
        mRowIndex = 0;
        mColIndex = 0;
        mRowWeightsIndex = 0;
        mColumnWeightsIndex = 0;
        mSkipsIndex = 0;
        mSpansIndex = 0;
        mOrientationIndex = 0;

        mGrid = findViewById(R.id.grid);
        mGrid.setRows(mSizes[mRowIndex]);
        mGrid.setColumns(mSizes[mColIndex]);
        mGrid.setRowWeights(mThreeWeights[mRowWeightsIndex]);
        mGrid.setColumnWeights(mThreeWeights[mColumnWeightsIndex]);
        mGrid.setSkips(mSkips[mSkipsIndex]);
        mGrid.setSpans(mSpans[mSpansIndex]);
        mGrid.setVerticalGaps(mGaps[mVerticalGapsIndex]);
        mGrid.setHorizontalGaps(mGaps[mHorizontalGapsIndex]);
        mGrid.setOrientation(mOrientations[mOrientationIndex]);
    }
}
