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

package org.constraintlayout.swing.core;

import androidx.constraintlayout.core.widgets.ConstraintWidget;
import androidx.constraintlayout.core.widgets.ConstraintWidgetContainer;
import androidx.constraintlayout.core.widgets.Guideline;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Container for GuidelinesStates
 * Constraints are stored in a fixture structure in the int table which is grown as things are added
 * Floats are "unpacked" using Float.intBitsToFloat
 * Todo generalize this to store manage Helper information
 */
public class GuidelinesState {
    int MAX = 2 * TOTAL;
    int[] table = new int[MAX];

    int lastId = -1;

    final static int OFFSET_GUIDELINE_TYPE = 0;
    final static int OFFSET_GUIDELINE_VALUE = 1;
    final static int TOTAL = 2;

    final static int GUIDELINE_START = 1;
    final static int GUIDELINE_END = 2;
    final static int GUIDELINE_TOP = 3;
    final static int GUIDELINE_BOTTOM = 4;
    final static int GUIDELINE_HORIZONTAL_PERCENT = 5;
    final static int GUIDELINE_VERTICAL_PERCENT = 6;

    HashMap<String, Integer> mapIdsToIdx = new HashMap<>();
    HashMap<Integer, String> mapIdxToIds = new HashMap<>();

    private int getIndex(String id) {
        Integer idx = mapIdsToIdx.get(id);
        if (idx != null) {
            return idx;
        }
        lastId++;
        if (lastId * MAX > table.length) {
            MAX *= 2;
            table = Arrays.copyOf(table, MAX);
        }
        mapIdsToIdx.put(id, lastId);
        mapIdxToIds.put(lastId, id);
        return lastId;
    }

    public void addGuideline(String id, int type, int value) {
        int index = getIndex(id) * TOTAL;
        table[index + OFFSET_GUIDELINE_TYPE] = type;
        table[index + OFFSET_GUIDELINE_VALUE] = value;
    }

    public void addGuideline(String id, int type, float percent) {
        int index = getIndex(id) * TOTAL;
        table[index + OFFSET_GUIDELINE_TYPE] = type;
        table[index + OFFSET_GUIDELINE_VALUE] = Float.floatToIntBits(percent);
    }

    public void clear() {
        Arrays.fill(table, 0);
    }

    public String serialize() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i <= lastId; i++) {
            builder.append(serializeTableGuideline(i));
            builder.append(',');
            builder.append("\n");
        }
        return builder.toString();
    }

    private String serializeTableGuideline(int index) {
        StringBuilder builder = new StringBuilder();
        builder.append(mapIdxToIds.get(index) + ": ");
        int type = table[index + OFFSET_GUIDELINE_TYPE];
        int value = table[index + OFFSET_GUIDELINE_VALUE];
        switch (type) {
            case GUIDELINE_START: {
                builder.append("{ type: 'vGuideline', start: ");
                builder.append(value);
                builder.append("}");
            } break;
            case GUIDELINE_END: {
                builder.append("{ type: 'vGuideline', end: ");
                builder.append(value);
                builder.append("}");
            } break;
            case GUIDELINE_TOP: {
                builder.append("{ type: 'hGuideline', top: ");
                builder.append(value);
                builder.append("}");
            } break;
            case GUIDELINE_BOTTOM: {
                builder.append("{ type: 'hGuideline', bottom: ");
                builder.append(value);
                builder.append("}");
            } break;
            case GUIDELINE_HORIZONTAL_PERCENT: {
                builder.append("{ type: 'vGuideline', percent: ");
                float floatValue = ((int)(Float.intBitsToFloat(value) * 100)) / 100f;
                builder.append(floatValue);
                builder.append("}");
            } break;
            case GUIDELINE_VERTICAL_PERCENT: {
                builder.append("{ type: 'hGuideline', percent: ");
                float floatValue = ((int)(Float.intBitsToFloat(value) * 100)) / 100f;
                builder.append(floatValue);
                builder.append("}");
            } break;
        }
        return builder.toString();
    }

    public void apply(ConstraintWidgetContainer layout, HashMap<String, ConstraintWidget> idsToConstraintWidgets) {
        for (String id : mapIdsToIdx.keySet()) {
            ConstraintWidget g = idsToConstraintWidgets.get(id);
            if (g == null || !(g instanceof Guideline)) {
                if (g != null) {
                    layout.remove(g);
                }
                Guideline guideline = new Guideline();
                guideline.stringId = id;
                idsToConstraintWidgets.put(id, guideline);
                g = guideline;
                layout.add(g);
            }
            applyGuideline(2*mapIdsToIdx.get(id), g);
        }
    }

    private void applyGuideline(int index, ConstraintWidget constraintWidget) {
        if (constraintWidget instanceof Guideline) {
            Guideline guideline = (Guideline) constraintWidget;
            int type = table[index + OFFSET_GUIDELINE_TYPE];
            int value = table[index + OFFSET_GUIDELINE_VALUE];
            switch (type) {
                case GUIDELINE_START: {
                    guideline.setOrientation(ConstraintWidget.VERTICAL);
                    guideline.setGuideBegin(value);
                } break;
                case GUIDELINE_END: {
                    guideline.setOrientation(ConstraintWidget.VERTICAL);
                    guideline.setGuideEnd(value);
                } break;
                case GUIDELINE_TOP: {
                    guideline.setOrientation(ConstraintWidget.HORIZONTAL);
                    guideline.setGuideBegin(value);
                } break;
                case GUIDELINE_BOTTOM: {
                    guideline.setOrientation(ConstraintWidget.HORIZONTAL);
                    guideline.setGuideEnd(value);
                } break;
                case GUIDELINE_HORIZONTAL_PERCENT: {
                    float floatValue = ((int)(Float.intBitsToFloat(value) * 100)) / 100f;
                    guideline.setOrientation(ConstraintWidget.VERTICAL);
                    guideline.setGuidePercent(floatValue);
                } break;
                case GUIDELINE_VERTICAL_PERCENT: {
                    float floatValue = ((int)(Float.intBitsToFloat(value) * 100)) / 100f;
                    guideline.setOrientation(ConstraintWidget.HORIZONTAL);
                    guideline.setGuidePercent(floatValue);
                } break;
            }
        }
    }

}
