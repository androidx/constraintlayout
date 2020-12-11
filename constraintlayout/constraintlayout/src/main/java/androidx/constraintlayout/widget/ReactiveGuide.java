/*
 * Copyright (C) 2015 The Android Open Source Project
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

package androidx.constraintlayout.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.motion.widget.ViewTransition;

/**
 * Utility class representing a Guideline helper object for {@link ConstraintLayout}.
 * Helper objects are not displayed on device (they are marked as {@code View.GONE}) and are only used
 * for layout purposes. They only work within a {@link ConstraintLayout}.
 *<p>
 * A Guideline can be either horizontal or vertical:
 * <ul>
 *     <li>Vertical Guidelines have a width of zero and the height of their {@link ConstraintLayout} parent</li>
 *     <li>Horizontal Guidelines have a height of zero and the width of their {@link ConstraintLayout} parent</li>
 * </ul>
 *<p>
 * Positioning a Guideline is possible in three different ways:
 * <ul>
 *     <li>specifying a fixed distance from the left or the top of a layout ({@code layout_constraintGuide_begin})</li>
 *     <li>specifying a fixed distance from the right or the bottom of a layout ({@code layout_constraintGuide_end})</li>
 *     <li>specifying a percentage of the width or the height of a layout ({@code layout_constraintGuide_percent})</li>
 * </ul>
 * <p>
 * Widgets can then be constrained to a Guideline, allowing multiple widgets to be positioned easily from
 * one Guideline, or allowing reactive layout behavior by using percent positioning.
 * <p>
 * See the list of attributes in {@link ConstraintLayout.LayoutParams} to set a Guideline
 * in XML, as well as the corresponding {@link ConstraintSet#setGuidelineBegin}, {@link ConstraintSet#setGuidelineEnd}
 * and {@link ConstraintSet#setGuidelinePercent} functions in {@link ConstraintSet}.
 * <p>
 * Example of a {@code Button} constrained to a vertical {@code Guideline}:<br>
 * {@sample resources/examples/Guideline.xml
 *          Guideline}
 */
public class ReactiveGuide extends View implements SharedValues.SharedValuesListener {

    public ReactiveGuide(Context context) {
        super(context);
        super.setVisibility(View.GONE);
        init();
    }

    public ReactiveGuide(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setVisibility(View.GONE);
        init();
    }

    public ReactiveGuide(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setVisibility(View.GONE);
        init();
    }

    public ReactiveGuide(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        super.setVisibility(View.GONE);
        init();
    }

    private void init() {
        SharedValues sharedValues = ConstraintLayout.getSharedValues();
        sharedValues.addListener(this);
    }

    /**
     * {@hide
     */
    @Override
    public void setVisibility(int visibility) {
    }

    /**
     * {@hide
     */
    @SuppressLint("MissingSuperCall")
    @Override
    public void draw(Canvas canvas) {
    }

    /**
     * {@hide
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(0, 0);
    }

    /**
     * Set the guideline's distance from the top or left edge.
     *
     * @param margin the distance to the top or left edge
     */
    public void setGuidelineBegin(int margin) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) getLayoutParams();
        params.guideBegin = margin;
        setLayoutParams(params);
    }

    /**
     * Set a guideline's distance to end.
     *
     * @param margin the margin to the right or bottom side of container
     */
    public void setGuidelineEnd(int margin) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) getLayoutParams();
        params.guideEnd = margin;
        setLayoutParams(params);
    }

    /**
     * Set a Guideline's percent.
     * @param ratio the ratio between the gap on the left and right 0.0 is top/left 0.5 is middle
     */
    public void setGuidelinePercent(float ratio) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) getLayoutParams();
        params.guidePercent = ratio;
        setLayoutParams(params);
    }

    @Override
    public void onNewValue(int key, int value) {
        System.out.println("onNewValue " + key + " => " + value);
        setGuidelineBegin(value);
        int id = getId();
        if (id <= 0) {
            return;
        }
        if (getParent() instanceof MotionLayout) {
            MotionLayout motionLayout = (MotionLayout) getParent();
            int currentState = motionLayout.getCurrentState();
            ConstraintSet constraintSet = motionLayout.cloneConstraintSet(currentState);
            constraintSet.setGuidelineEnd(id, value);
            motionLayout.updateStateAnimate(currentState, constraintSet, 1000);
        }
    }
}
