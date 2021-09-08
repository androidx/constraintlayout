/*
 * Copyright (C) 2021 The Android Open Source Project
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
package org.constraintlayout.swing.core.motion.model;

import androidx.constraintlayout.core.motion.utils.Utils;
import androidx.constraintlayout.core.state.Transition;
import androidx.constraintlayout.core.state.WidgetFrame;
import androidx.constraintlayout.core.widgets.ConstraintWidget;
import androidx.constraintlayout.core.widgets.ConstraintWidgetContainer;
import androidx.constraintlayout.core.widgets.Guideline;
import androidx.constraintlayout.core.widgets.Optimizer;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import org.constraintlayout.swing.core.ConstraintLayoutState;
import org.constraintlayout.swing.core.ConstraintSetParser;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class MotionEngine {
    ArrayList<String> mIds = new ArrayList<>();
    private final ConstraintWidgetContainer mInterLayout = new ConstraintWidgetContainer();
    private final ConstraintWidgetContainer mEndLayout = new ConstraintWidgetContainer();
    private final ConstraintWidgetContainer mStartLayout = new ConstraintWidgetContainer();
    private final ConstraintSetParser parser = new ConstraintSetParser();
    private ConstraintLayoutState mState;
    private final HashMap<String, ConstraintWidget> mInterConstraintMap = new HashMap<>();
    private final HashMap<String, ConstraintWidget> mEndConstraintMap = new HashMap<>();
    private final HashMap<String, ConstraintWidget> mStartConstraintMap = new HashMap<>();
    MotionSceneModel motionSceneModel = new MotionSceneModel();


    private TransitionModel mCurrentTransition;
    private Transition mTransition = new Transition();
    ConstraintSetModel mStartConstraintSet;
    ConstraintSetModel mEndConstraintSet;
    boolean mNeedMeasure = true;
    private float mProgress = 0;

    public void remeasure() {
        mNeedMeasure = true;
    }
    public void parse(String content) {
        motionSceneModel.parse(content);
        mInterConstraintMap.put("parent", mInterLayout);
        mEndConstraintMap.put("parent", mEndLayout);
        mStartConstraintMap.put("parent", mStartLayout);
        mCurrentTransition = motionSceneModel.getCurrentTransition();
        String endConstraintSetName = mCurrentTransition.getTo();
        String startConstraintSetName = mCurrentTransition.getFrom();
        Utils.log("endConstraintSetName   = " + endConstraintSetName);
        Utils.log("startConstraintSetName = " + startConstraintSetName);
        mStartConstraintSet = motionSceneModel.getConstraintSet(startConstraintSetName);
        mEndConstraintSet = motionSceneModel.getConstraintSet(endConstraintSetName);
        mTransition.clear();
        mCurrentTransition.updateTransition(mTransition);
//        mState = cset.mState;
        setMeasurer();
        Utils.log("parse completed");
    }

    void setMeasurer() {
        BasicMeasure.Measurer measurer = new BasicMeasure.Measurer() {

            @Override
            public void measure(ConstraintWidget widget, BasicMeasure.Measure measure) {
                innerMeasure(widget, measure);
            }

            @Override
            public void didMeasures() {
            }
        };
        mInterLayout.setMeasurer(measurer);
        mStartLayout.setMeasurer(measurer);
        mEndLayout.setMeasurer(measurer);
    }

    public void add(String id) {
        Utils.log(" " + id);

        mIds.add(id);
        addConstraintWidget(id, mStartLayout, mStartConstraintMap);
        addConstraintWidget(id, mEndLayout, mEndConstraintMap);
        addConstraintWidget(id, mInterLayout, mInterConstraintMap);

        mNeedMeasure = true;


    }

    void addConstraintWidget(String id, ConstraintWidgetContainer layout, HashMap<String, ConstraintWidget> mInterConstraintMap) {
        ConstraintWidget cw = new ConstraintWidget();
        cw.stringId = id;
        layout.add(cw);
        mInterConstraintMap.put(id, cw);
    }

    public void remove(String id) {
        ConstraintWidget interpolated = mInterConstraintMap.get(id);
        ConstraintWidget start = mStartConstraintMap.get(id);
        ConstraintWidget end = mEndConstraintMap.get(id);
        mInterLayout.remove(interpolated);
        mStartLayout.remove(start);
        mEndLayout.remove(end);
        mIds.remove(id);
        mInterConstraintMap.remove(id);
        mStartConstraintMap.remove(id);
        mEndConstraintMap.remove(id);
    }

    public int getWidth() {
        return mInterLayout.getWidth();
    }

    public int getHeight() {
        return mInterLayout.getHeight();
    }

    public void setProgress(float p) {
        mProgress = p;
    }

    public interface LayoutWidget {
        void layout(String id, WidgetFrame widget);
    }

    private void setupTransition(int width, int height) {
        Utils.log("  " + width + " " + height);

        mNeedMeasure = false;


        Utils.log(" ================ start");
        mStartConstraintSet.mState.guidelines.apply(mStartLayout, mStartConstraintMap);
        printState();
        layoutChildren(width, height, mStartConstraintSet.mState, mStartLayout, mStartConstraintMap);
        mEndConstraintSet.mState.guidelines.apply(mEndLayout, mEndConstraintMap);
        layoutChildren(width, height, mEndConstraintSet.mState, mEndLayout, mEndConstraintMap);

        mTransition.updateFrom(mStartLayout, Transition.START);
        mTransition.updateFrom(mEndLayout, Transition.END);
        mTransition.updateFrom(mInterLayout, Transition.INTERPOLATED);
        mCurrentTransition.updateTransition(mTransition);
        printState();

        Utils.log("  done");

    }

    void printState() {
        StackTraceElement s = new Throwable().getStackTrace()[1];
        String methodName = s.getMethodName();
        methodName = (methodName + "                  ").substring(0, 17);
        String npad = "    ".substring(Integer.toString(s.getLineNumber()).length());
        String ss = ".(" + s.getFileName() + ":" + s.getLineNumber() + ")" + npad + methodName;
        for (String mId : mIds) {
            int ws = -1, we = -1;
            if (mTransition.getStart(mId) != null) {
                ws = mTransition.getStart(mId).width();

            }
            if (mTransition.getEnd(mId) != null) {
                we = mTransition.getEnd(mId).width();

            }
            System.out.println(ss + " " + mId + "   >  > " + ws + " ========= " + we);
        }
        System.out.println(ss + "  ");

    }

    private static void layoutChildren(int width, int height, ConstraintLayoutState state, ConstraintWidgetContainer layout,
                                       HashMap<String, ConstraintWidget> map) {
        for (String id : map.keySet()) {
            if (id.equals("parent")) {
                continue;
            }
            // ask the state to create a widget
            ConstraintWidget constraintWidget = map.get(id);
            if (constraintWidget instanceof Guideline) {
                continue;
            }

            state.constraints.apply(map, constraintWidget);
        }

        layout.setWidth(width);
        layout.setHeight(height);
        layout.layout();
        Utils.log("layout layout layout layout");

        layout.setOptimizationLevel(Optimizer.OPTIMIZATION_STANDARD);
        layout.measure(Optimizer.OPTIMIZATION_STANDARD, BasicMeasure.EXACTLY, width, BasicMeasure.EXACTLY, height,
                0, 0, 0, 0);
    }

    public void layout(int width, int height, LayoutWidget layoutCallback) {
        if (mNeedMeasure) {
            setupTransition(width, height);
        }
        mTransition.interpolate(0, 0, mProgress);


        for (String child : mIds) {
            WidgetFrame component = mTransition.getInterpolated(child);
            if (component == null) {
                Utils.log("could not find " + child);
                continue;
            }
            layoutCallback.layout(child, component);
        }

    }

    private void innerMeasure(ConstraintWidget constraintWidget, BasicMeasure.Measure measure) {
        Component component = (Component) constraintWidget.getCompanionWidget();
        int measuredWidth = constraintWidget.getWidth();
        int measuredHeight = constraintWidget.getHeight();

        if (measure.horizontalBehavior == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
            measuredWidth = component.getMinimumSize().width;
        } else if (measure.horizontalBehavior == ConstraintWidget.DimensionBehaviour.MATCH_PARENT) {
            measuredWidth = getWidth();
        }
        if (measure.verticalBehavior == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
            measuredHeight = component.getMinimumSize().height;
        } else if (measure.verticalBehavior == ConstraintWidget.DimensionBehaviour.MATCH_PARENT) {
            measuredHeight = getHeight();
        }
        measure.measuredWidth = measuredWidth;
        measure.measuredHeight = measuredHeight;
    }
    public WidgetFrame getInterpolated(String id) {
        return mTransition.getInterpolated(id);
    }
}
