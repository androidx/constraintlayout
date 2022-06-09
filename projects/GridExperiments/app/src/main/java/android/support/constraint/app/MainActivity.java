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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.Grid;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

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
    private int mOrientation = Grid.HORIZONTAL;

    private int[] mGaps = new int[] {0, 20};
    private int[] mSizes = new int[] {3, 4};
    private String[] mThreeWeights = new String[] {"1,1,1", "1,2,2"};
    private String[] mFourWeights = new String[] {"1,1,1,1", "1,2,1,1"};
    private String[] mSkips = new String[] {"", "0:1x2, 4:1x1"};
    private String[] mSpans = new String[] {"", "6:1x2"};
    private static String LAYOUT_TO_USE = "layout";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extra = getIntent().getExtras();
        if (extra == null) {
            LinearLayout linearLayout = new LinearLayout(this,null);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            setContentView(linearLayout);

             int []layouts = getLayouts();// you could { R.layout.responsive, R.layout.activity_main,
            for (int i = 0; i < layouts.length; i++) {
                final int layout = layouts[i];
                Button button = new Button(this,null);
                button.setText(getResources().getResourceEntryName(layout));
                linearLayout.addView(button);
                button.setOnClickListener(v->{
                    Intent intent=  new Intent( this,MainActivity.class);
                    intent.putExtra(LAYOUT_TO_USE , layout );
                    startActivity(intent);
                });
            }
            return;
        }
        int layout = extra.getInt(LAYOUT_TO_USE);
        setContentView(layout);
        if (layout == R.layout.calendar) {
            fillCal(0);
        }
        if (layout == R.layout.responsive) {
            mGrid = findViewById(R.id.grid);
            mGrid.setRows(mSizes[0]);
            mGrid.setColumns(mSizes[0]);
            mGrid.setRowWeights(mThreeWeights[0]);
            mGrid.setColumnWeights(mThreeWeights[0]);
            mGrid.setSkips(mSkips[0]);
            mGrid.setSpans(mSpans[0]);
            mGrid.setVerticalGaps(mGaps[0]);
            mGrid.setHorizontalGaps(mGaps[0]);
            mGrid.setOrientation(mOrientation);
        }
    }
    int mMonth = 0;
    public void nextPrev(View v) {
        if (((TextView)v).getText().equals("<")) {
            fillCal(++mMonth);
        }
        fillCal(--mMonth);
    }
   void  fillCal(int month_offset) {
       String[] days = {"S", "M", "T", "W", "T", "F", "S"};
       Field[] f = R.id.class.getDeclaredFields();
       for (int i = 0; i < f.length; i++) {
           String name = f[i].getName();
           if (name.startsWith("date") && name.length() == "date00".length()) {
               try {
                   TextView tv = findViewById(f[i].getInt(null));
                   if (tv == null) {
                       Log.v("main", " >>>>>>>>>>> " + name);
                       continue;
                   }
                   if (name.startsWith("date0")) {
                       int p = name.substring("date0".length()).charAt(0) - '0';
                       tv.setText(days[p]);
                   } else {
                       int col = name.substring("date".length()).charAt(0) - '1';
                       int row = name.substring("date".length()).charAt(1) - '0';
                       int pos = row + col * 7;
                       Calendar cal = Calendar.getInstance();
                       cal.add(Calendar.MONTH, month_offset);
                       cal.set(Calendar.DAY_OF_MONTH, 1);
                       int offset = cal.get(Calendar.DAY_OF_WEEK) - 1;
                       int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                       if (offset > pos || pos - offset >= lastDay) {
                           tv.setText("");
                       } else {
                           tv.setText("" + (pos - offset + 1));
                       }

                   }
               } catch (IllegalAccessException e) {
                   e.printStackTrace();
               }
           }
       }
   }

    private static int[] getLayouts( ) {
        ArrayList<String> list = new ArrayList<>();
        Field[] f = R.layout.class.getDeclaredFields();

        int []ret = new int[f.length];
         int count = 0;
        for (int i = 0; i < f.length; i++) {
            try {
                String name = f[i].getName();
                if (!name.contains("_") || name.equals("activity_main")) {
                    ret[count++] = f[i].getInt(null);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return Arrays.copyOf(ret,count);
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
        mOrientation = mGrid.getOrientation() == Grid.HORIZONTAL
                ? Grid.VERTICAL : Grid.HORIZONTAL;
        mGrid.setOrientation(mOrientation);
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
        mOrientation = Grid.VERTICAL;

        mGrid = findViewById(R.id.grid);
        mGrid.setRows(mSizes[mRowIndex]);
        mGrid.setColumns(mSizes[mColIndex]);
        mGrid.setRowWeights(mThreeWeights[mRowWeightsIndex]);
        mGrid.setColumnWeights(mThreeWeights[mColumnWeightsIndex]);
        mGrid.setSkips(mSkips[mSkipsIndex]);
        mGrid.setSpans(mSpans[mSpansIndex]);
        mGrid.setVerticalGaps(mGaps[mVerticalGapsIndex]);
        mGrid.setHorizontalGaps(mGaps[mHorizontalGapsIndex]);
        mGrid.setOrientation(mOrientation);
    }
}
