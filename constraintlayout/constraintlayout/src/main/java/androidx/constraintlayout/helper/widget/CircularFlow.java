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
package androidx.constraintlayout.helper.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.R;
import androidx.constraintlayout.widget.VirtualLayout;
import java.util.Arrays;

public class CircularFlow extends VirtualLayout {
    private static final String TAG = "CircularFlow";
    ConstraintLayout mContainer;
    int mViewCenter;
    private static final int DEFAULT_RADIUS = 0;
    private static final float DEFAULT_ANGLE = 0F;
    /**
     * @hide
     */
    private float[] mAngles = new float[32];

    /**
     * @hide
     */
    private int[] mRadius = new int[32];

    /**
     * @hide
     */
    private int mCountRadius;

    /**
     * @hide
     */
    private int mCountAngle;

    /**
     * @hide
     */
    private String mReferenceAngles;

    /**
     * @hide
     */
    private String mReferenceRadius;

    public CircularFlow(Context context) {
        super(context);
    }

    public CircularFlow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircularFlow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int[] getRadius() {
        return Arrays.copyOf(mRadius, mCountRadius);
    }


    public float[] getAngles() {
        return Arrays.copyOf(mAngles, mCountAngle);
    }


    @Override
    protected void init(AttributeSet attrs) {
        super.init(attrs);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ConstraintLayout_Layout);
            final int N = a.getIndexCount();

            for (int i = 0; i < N; i++) {
                int attr = a.getIndex(i);
                if (attr == R.styleable.ConstraintLayout_Layout_circularflow_viewCenter) {
                    mViewCenter = a.getResourceId(attr, 0);
                } else if (attr == R.styleable.ConstraintLayout_Layout_circularflow_angles) {
                    mReferenceAngles = a.getString(attr);
                    setAngles(mReferenceAngles);
                } else if (attr == R.styleable.ConstraintLayout_Layout_circularflow_radiusInDP) {
                    mReferenceRadius = a.getString(attr);
                    setRadius(mReferenceRadius);
                }
            }
            a.recycle();
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mReferenceAngles != null) {
            setAngles(mReferenceAngles);
        }
        if (mReferenceRadius != null) {
            setRadius(mReferenceRadius);
        }
        anchorReferences();
    }

    private void anchorReferences() {
        mContainer = (ConstraintLayout) getParent();
        ConstraintSet c = new ConstraintSet();
        c.clone(mContainer);
        for (int i = 0; i <= mCount; i++) {
            int id = mIds[i];
            View view = mContainer.getViewById(id);

            if (view != null) {
                int radius = DEFAULT_RADIUS;
                float angle = DEFAULT_ANGLE;

                if (i < getRadius().length){
                    radius = getRadius()[i];
                } else {
                    Log.e("CircularFlow", "Added radius to view with id: " + mMap.get(view.getId()));
                }

                if (i < getAngles().length){
                    angle = getAngles()[i];
                } else {
                    Log.e("CircularFlow", "Added angle to view with id: " + mMap.get(view.getId()));
                }
                c.constrainCircle(view.getId(), mViewCenter, radius, angle);
            }
        }
        c.applyTo(mContainer);
        applyLayoutFeatures();
    }

    /**
     * Add a view to the CircularFlow. The referenced view need to be a child of the container parent.
     * The view also need to have its id set in order to be added.
     * The views previous need to have its radius and angle set in order to be added correctly a new view.
     * @param view
     * @param radius
     * @param angle
     * @return
     */
    public void addViewToCircularFlow(View view, int radius, float angle) {
        addView(view);
        mAngles[mCountAngle] = angle;
        mCountAngle++;
        mRadius[mCountRadius] = (int) (radius * myContext.getResources().getDisplayMetrics().density);
        mCountRadius++;
        anchorReferences();
    }

    /**
     * @hide
     */
    private void setAngles(String idList) {
        if (idList == null) {
            return;
        }
        int begin = 0;
        mCountAngle = 0;
        while (true) {
            int end = idList.indexOf(',', begin);
            if (end == -1) {
                addAngle(idList.substring(begin).trim());
                break;
            }
            addAngle(idList.substring(begin, end).trim());
            begin = end + 1;
        }
    }

    /**
     * @hide
     */
    private void setRadius(String idList) {
        if (idList == null) {
            return;
        }
        int begin = 0;
        mCountRadius = 0;
        while (true) {
            int end = idList.indexOf(',', begin);
            if (end == -1) {
                addRadius(idList.substring(begin).trim());
                break;
            }
            addRadius(idList.substring(begin, end).trim());
            begin = end + 1;
        }
    }

    /**
     * @hide
     */
    private void addAngle(String angleString) {
        if (angleString == null || angleString.length() == 0) {
            return;
        }
        if (myContext == null) {
            return;
        }
        if (mAngles == null) {
            return;
        }

        if (mCountAngle + 1 > mAngles.length) {
            mAngles = Arrays.copyOf(mAngles, mAngles.length * 2);
        }
        mAngles[mCountAngle] = Integer.parseInt(angleString);
        mCountAngle++;
    }

    /**
     * @hide
     */
    private void addRadius(String radiusString) {
        if (radiusString == null || radiusString.length() == 0) {
            return;
        }
        if (myContext == null) {
            return;
        }
        if (mRadius == null) {
            return;
        }

        if (mCountRadius + 1 > mRadius.length) {
            mRadius = Arrays.copyOf(mRadius, mRadius.length * 2);
        }

        mRadius[mCountRadius] = (int) (Integer.parseInt(radiusString) * myContext.getResources().getDisplayMetrics().density);
        mCountRadius++;
    }
}
