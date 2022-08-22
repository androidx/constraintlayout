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

package androidx.constraintlayout.widget;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.constraintlayout.core.Metrics;

import java.util.ArrayList;

public class ConstraintLayoutPerformance {
    private final Metrics mMetrics = new Metrics();
    ConstraintLayout mConstraintLayout;

    /**
     * Measure performance information about ConstraintLayout
     *
     * @param constraintLayout
     */
    public ConstraintLayoutPerformance(ConstraintLayout constraintLayout) {
        attach(constraintLayout);
    }
    public void attach(ConstraintLayout constraintLayout){
        constraintLayout.fillMetrics(mMetrics);
        mConstraintLayout = constraintLayout;
    }

    public void detach() {
        if (mConstraintLayout != null) {
            mConstraintLayout.fillMetrics(null);
        }
    }

    public void reset() {
        mMetrics.reset();
    }

    @SuppressLint("LogConditional")
    public void logSummary(String tag) {
        Log.v(tag, "CL Perf: -------------- ");

        Log.v(tag, "CL Perf: measuresWidgetsDuration = " + getMeasuresWidgetsDuration() * 1E-6f + "ms");
        Log.v(tag, "CL Perf: measuresLayoutDuration  = " + getMeasuresLayoutDuration() * 1E-6f + "ms");
        Log.v(tag, "CL Perf: measuredWidgets         = " + getMeasuredWidgets());
        Log.v(tag, "CL Perf: layouts                 = " + getLayouts());
        Log.v(tag, "CL Perf: LastTableSize           = " + getLastTableSize());
        Log.v(tag, "CL Perf: Constraints             = " + getConstraints());
        Log.v(tag, "CL Perf: NonresolvedWidgets      = " + getNonresolvedWidgets());
        Log.v(tag, "CL Perf: MaxVariables            = " + getMaxVariables());
        Log.v(tag, "CL Perf: LinearSolved            = " + getLinearSolved());
        Log.v(tag, "CL Perf: Errors                  = " + getErrors());
        Log.v(tag, "CL Perf: MinimizeGoal            = " + getMinimizeGoal());
        Log.v(tag, "CL Perf: MaxRows                 = " + getMaxRows());
        Log.v(tag, "CL Perf: Optimize                = " + getOptimize());
        Log.v(tag, "CL Perf: AdditionalMeasures      = " + getAdditionalMeasures());
        Log.v(tag, "CL Perf: Minimize                = " + getMinimize());


        Log.v(tag, "CL Perf: getResolvedWidgets      = " + getResolvedWidgets());
        Log.v(tag, "CL Perf: getGraphSolved          = " + getGraphSolved());
        Log.v(tag, "CL Perf: Variables               = " + getVariables());
        Log.v(tag, "CL Perf: MeasuredMatchWidgets    = " + getMeasuredMatchWidgets());
        Log.v(tag, "CL Perf: Measures                = " + getMeasures());
        Log.v(tag, "CL Perf: Measures                = " + getMeasures());
        Log.v(tag, "CL Perf: Simpleconstraints       = " + getSimpleconstraints());


        Log.v(tag, "CL Perf: MeasuresWrap              = " + getMeasuresWrap());
        Log.v(tag, "CL Perf: MeasuresWrapInfeasible    = " + getMeasuresWrapInfeasible());
        Log.v(tag, "CL Perf: DetermineGroups           = " + getDetermineGroups());
        Log.v(tag, "CL Perf: InfeasibleDetermineGroups = " + getInfeasibleDetermineGroups());
        Log.v(tag, "CL Perf: Grouping                  = " + getGrouping());

    }

    public long getMeasuresWidgetsDuration() {
        return mMetrics.measuresWidgetsDuration;
    }

    public long getMeasuresLayoutDuration() {
        return mMetrics.measuresLayoutDuration;
    }

    public long getMeasuredWidgets() {
        return mMetrics.measuredWidgets;
    }

    public long getMeasuredMatchWidgets() {
        return mMetrics.measuredMatchWidgets;
    }

    public long getMeasures() {
        return mMetrics.measures;
    }

    public long getAdditionalMeasures() {
        return mMetrics.additionalMeasures;
    }

    public long getResolutions() {
        return mMetrics.resolutions;
    }

    public long getTableSizeIncrease() {
        return mMetrics.tableSizeIncrease;
    }

    public long getMinimize() {
        return mMetrics.minimize;
    }

    public long getConstraints() {
        return mMetrics.constraints;
    }

    public long getSimpleconstraints() {
        return mMetrics.simpleconstraints;
    }

    public long getOptimize() {
        return mMetrics.optimize;
    }

    public long getIterations() {
        return mMetrics.iterations;
    }

    public long getPivots() {
        return mMetrics.pivots;
    }

    public long getBfs() {
        return mMetrics.bfs;
    }

    public long getVariables() {
        return mMetrics.variables;
    }

    public long getErrors() {
        return mMetrics.errors;
    }

    public long getSlackvariables() {
        return mMetrics.slackvariables;
    }

    public long getExtravariables() {
        return mMetrics.extravariables;
    }

    public long getMaxTableSize() {
        return mMetrics.maxTableSize;
    }

    public long getFullySolved() {
        return mMetrics.fullySolved;
    }

    public long getGraphOptimizer() {
        return mMetrics.graphOptimizer;
    }

    public long getGraphSolved() {
        return mMetrics.graphSolved;
    }

    public long getLinearSolved() {
        return mMetrics.linearSolved;
    }

    public long getResolvedWidgets() {
        return mMetrics.resolvedWidgets;
    }

    public long getMinimizeGoal() {
        return mMetrics.minimizeGoal;
    }

    public long getMaxVariables() {
        return mMetrics.maxVariables;
    }

    public long getMaxRows() {
        return mMetrics.maxRows;
    }



    public long getNonresolvedWidgets() {
        return mMetrics.nonresolvedWidgets;
    }

    public ArrayList<String> ProblematicLayouts() {
        return mMetrics.problematicLayouts;
    }

    public long getLastTableSize() {
        return mMetrics.lastTableSize;
    }

    public long getWidgets() {
        return mMetrics.widgets;
    }

    public long getMeasuresWrap() {
        return mMetrics.measuresWrap;
    }

    public long getMeasuresWrapInfeasible() {
        return mMetrics.measuresWrapInfeasible;
    }

    public long getInfeasibleDetermineGroups() {
        return mMetrics.infeasibleDetermineGroups;
    }

    public long getDetermineGroups() {
        return mMetrics.determineGroups;
    }

    public long getLayouts() {
        return mMetrics.layouts;
    }

    public long getGrouping() {
        return mMetrics.grouping;
    }

   
}
