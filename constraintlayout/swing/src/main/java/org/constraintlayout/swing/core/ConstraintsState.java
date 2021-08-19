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

import androidx.constraintlayout.core.widgets.ConstraintAnchor;
import androidx.constraintlayout.core.widgets.ConstraintWidget;

import androidx.constraintlayout.core.widgets.ConstraintWidget.DimensionBehaviour;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Container for Constraints
 * Constraints are stored in a fixture structure in the int table which is grown as things are added
 * Floats are "unpacked" using Float.intBitsToFloat
 */
public class ConstraintsState {
    int MAX = 2 * TOTAL;
    int[] table = new int[MAX];
    int lastId = 1; // container

    HashMap<String, Integer> mapIdsToIdx = new HashMap<>();
    HashMap<Integer, String> mapIdxToIds = new HashMap<>();

    final static int DIMENSION_VALUE = 1;
    final static int DIMENSION_WRAP = 2;
    final static int DIMENSION_PREFER_WRAP = 3;
    final static int DIMENSION_SPREAD = 4;
    final static int DIMENSION_PARENT = 5;
    final static int DIMENSION_PERCENT = 6;
    final static int DIMENSION_RATIO = 7;

    final static int OFFSET_DIMENSION_TYPE = 0;
    final static int OFFSET_DIMENSION_VALUE = OFFSET_DIMENSION_TYPE + 1;
    final static int OFFSET_DIMENSION_MIN = OFFSET_DIMENSION_VALUE + 1;
    final static int OFFSET_DIMENSION_MAX = OFFSET_DIMENSION_MIN + 1;

    final static int OFFSET_CONSTRAINT_TARGET = 0;
    final static int OFFSET_CONSTRAINT_ANCHOR = OFFSET_CONSTRAINT_TARGET + 1;
    final static int OFFSET_CONSTRAINT_MARGIN = OFFSET_CONSTRAINT_ANCHOR + 1;
    final static int OFFSET_CONSTRAINT_MARGIN_GONE = OFFSET_CONSTRAINT_MARGIN + 1;

    final static int OFFSET_CENTER_CONSTRAINT = 0;
    final static int OFFSET_CENTER_BIAS = OFFSET_CENTER_CONSTRAINT + 1;

    final static int WIDTH_IDX = 0;
    // TYPE(0), VALUE(1), MIN(2), MAX(3)
    final static int HEIGHT_IDX = 4;
    // TYPE(4), VALUE(5), MIN(6), MAX(7)
    final static int START_IDX = 8;
    // TARGETID(8), ANCHOR(9), MARGIN(10), MARGIN_GONE(11)
    final static int END_IDX = 12;
    // TARGETID(12), ANCHOR(13), MARGIN(14), MARGIN_GONE(15)
    final static int TOP_IDX = 16;
    // TARGETID(16), ANCHOR(17), MARGIN(18), MARGIN_GONE(19)
    final static int BOTTOM_IDX = 20;
    // TARGETID(20), ANCHOR(21), MARGIN(22), MARGIN_GONE(23)
    final static int BASELINE_IDX = 24;
    // TARGETID(24), ANCHOR(25), MARGIN(26), MARGIN_GONE(27)
    final static int CENTER_IDX = 28;
    // TARGETID(28)
    final static int HORIZONTAL_CENTER_IDX = 29;
    // TARGETID(29), BIAS(30)
    final static int VERTICAL_CENTER_IDX = 31;
    // TARGETID(31), BIAS(32)
    final static int TOTAL = 32 + 1;

    final static int ANCHOR_START = START_IDX;
    final static int ANCHOR_END = END_IDX;
    final static int ANCHOR_TOP = TOP_IDX;
    final static int ANCHOR_BOTTOM = BOTTOM_IDX;
    final static int ANCHOR_BASELINE = BASELINE_IDX;
    final static int ANCHOR_HORIZONTAL_CENTER = HORIZONTAL_CENTER_IDX;
    final static int ANCHOR_VERTICAL_CENTER = VERTICAL_CENTER_IDX;
    final static int ANCHOR_CENTER = CENTER_IDX;

    static final HashMap<Integer, String> mapAnchorIdx = new HashMap<>();
    static final HashMap<String, Integer> mapAnchorStr = new HashMap<>();

    static final HashMap<Integer, ConstraintAnchor.Type> mapAnchorType = new HashMap<>();

    static {
        mapAnchorIdx.put(ANCHOR_START, "start");
        mapAnchorIdx.put(ANCHOR_END, "end");
        mapAnchorIdx.put(ANCHOR_TOP, "top");
        mapAnchorIdx.put(ANCHOR_BOTTOM, "bottom");
        mapAnchorIdx.put(ANCHOR_BASELINE, "baseline");
        mapAnchorIdx.put(ANCHOR_HORIZONTAL_CENTER, "centerHorizontally");
        mapAnchorIdx.put(ANCHOR_VERTICAL_CENTER, "centerVertically");
        mapAnchorIdx.put(ANCHOR_CENTER, "center");
        mapAnchorStr.put("start", ANCHOR_START);
        mapAnchorStr.put("end", ANCHOR_END);
        mapAnchorStr.put("top", ANCHOR_TOP);
        mapAnchorStr.put("bottom", ANCHOR_BOTTOM);
        mapAnchorStr.put("baseline", ANCHOR_BASELINE);
        mapAnchorStr.put("centerHorizontally", ANCHOR_HORIZONTAL_CENTER);
        mapAnchorStr.put("centerVertically", ANCHOR_VERTICAL_CENTER);
        mapAnchorStr.put("center", ANCHOR_CENTER);

        mapAnchorType.put(ANCHOR_START, ConstraintAnchor.Type.LEFT);
        mapAnchorType.put(ANCHOR_END, ConstraintAnchor.Type.RIGHT);
        mapAnchorType.put(ANCHOR_TOP, ConstraintAnchor.Type.TOP);
        mapAnchorType.put(ANCHOR_BOTTOM, ConstraintAnchor.Type.BOTTOM);
        mapAnchorType.put(ANCHOR_BASELINE, ConstraintAnchor.Type.BASELINE);
    }

    public ConstraintsState() {
        mapIdsToIdx.put("parent", 1);
        mapIdxToIds.put(1, "parent");
    }

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
        table[(lastId * TOTAL) + WIDTH_IDX + OFFSET_DIMENSION_TYPE] = DIMENSION_WRAP;
        table[(lastId * TOTAL) + HEIGHT_IDX + OFFSET_DIMENSION_TYPE] = DIMENSION_WRAP;
        return lastId;
    }

    public void displayTable() {
        System.out.println(serialize());
    }

    public String serializeTableWidget(int index) {
        StringBuilder builder = new StringBuilder();
        builder.append(mapIdxToIds.get(index) + ": ");
        index *= TOTAL;
        builder.append("{ ");
        builder.append(serializeDimension(index + WIDTH_IDX, "width"));
        builder.append(serializeDimension(index + HEIGHT_IDX, "height"));
        builder.append(serializeConstraint(index + START_IDX, "start"));
        builder.append(serializeConstraint(index + END_IDX, "end"));
        builder.append(serializeConstraint(index + TOP_IDX, "top"));
        builder.append(serializeConstraint(index + BOTTOM_IDX, "bottom"));
        builder.append(serializeConstraint(index + BASELINE_IDX, "baseline"));
        builder.append(serializeCenterConstraint(index + CENTER_IDX, "center"));
        builder.append(serializeCenterAxisConstraint(index + HORIZONTAL_CENTER_IDX, "centerHorizontally"));
        builder.append(serializeCenterAxisConstraint(index + VERTICAL_CENTER_IDX, "centerVertically"));
        builder.append("},");
        return builder.toString();
    }

    private String serializeCenterConstraint(int index, String name) {
        int target = table[index + OFFSET_CENTER_CONSTRAINT];
        if (target == 0) {
            return "";
        }
        String targetId = mapIdxToIds.get(target);
        StringBuilder builder = new StringBuilder();
        builder.append(name + ": '" + targetId + "'");
        builder.append(',');
        return builder.toString();
    }

    private String serializeCenterAxisConstraint(int index, String name) {
        int target = table[index + OFFSET_CENTER_CONSTRAINT];
        if (target == 0) {
            return "";
        }
        String targetId = mapIdxToIds.get(target);
        StringBuilder builder = new StringBuilder();
        float bias = Float.intBitsToFloat(table[index + OFFSET_CENTER_BIAS]);
        if (bias == 0.5f) {
            builder.append(name + ": '" + targetId + "'");
        } else {
            builder.append(name + ": { target: " + targetId + ", bias: " + bias + "}");
        }
        builder.append(',');
        return builder.toString();
    }

    private String serializeConstraint(int index, String name) {
        int target = table[index + OFFSET_CONSTRAINT_TARGET];
        if (target == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(name + ": [");
        int anchor = table[index + OFFSET_CONSTRAINT_ANCHOR];
        int margin = table[index + OFFSET_CONSTRAINT_MARGIN];
        int marginGone = table[index + OFFSET_CONSTRAINT_MARGIN_GONE];
        String targetId = mapIdxToIds.get(target);
        builder.append("'" + targetId + "',");
        String anchorStr = mapAnchorIdx.get(anchor);
        builder.append("'" + anchorStr + "'");
        if (margin != 0) {
            builder.append(", " + margin);
        }
        if (marginGone != 0) {
            if (margin == 0) {
                builder.append(", 0");
            }
            builder.append(", " + marginGone);
        }
        builder.append("], ");
        return builder.toString();
    }

    public String serializeDimension(int index, String name) {
        int type = table[index + OFFSET_DIMENSION_TYPE];
        if (type == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(name + ":");
        int value  = table[index + OFFSET_DIMENSION_VALUE];
        int min = table[index + OFFSET_DIMENSION_MIN];
        int max = table[index + OFFSET_DIMENSION_MAX];
        boolean complexDimension = false;
        if (min != 0 || max != 0) {
            complexDimension = true;
        }
        if (complexDimension) {
            builder.append(" { type: ");
        }
        switch (type) {
            case DIMENSION_VALUE: {
                builder.append(" " + value);
            } break;
            case DIMENSION_WRAP: {
                builder.append(" 'wrap'");
            } break;
            case DIMENSION_PREFER_WRAP: {
                builder.append(" 'preferWrap'");
            } break;
            case DIMENSION_SPREAD: {
                builder.append(" 'spread'");
            } break;
            case DIMENSION_PARENT: {
                builder.append(" 'parent'");
            } break;
            case DIMENSION_PERCENT: {
                float floatValue = Float.intBitsToFloat(value);
                floatValue = ((int)(floatValue * 100))/100f;
                builder.append(" '" + floatValue + "%'");
            } break;
            case DIMENSION_RATIO: {
                float floatValue = Float.intBitsToFloat(value);
                floatValue = ((int)(floatValue * 100))/100f;
                builder.append(" " + floatValue);
            } break;
        }
        if (complexDimension) {
            if (min != 0) {
                builder.append(", min: " + min);
            }
            if (max != 0) {
                builder.append(", max: " + max);
            }
            builder.append("}");
        }
        builder.append(", ");
        return builder.toString();
    }

    public void addConstraint(String id, int from, String targetId, int to, int margin, int marginGone) {
        int index = getIndex(id) * TOTAL + from;
        int indexTarget = getIndex(targetId);
        table[index + OFFSET_CONSTRAINT_TARGET] = indexTarget;
        table[index + OFFSET_CONSTRAINT_ANCHOR] = to;
        table[index + OFFSET_CONSTRAINT_MARGIN] = margin;
        table[index + OFFSET_CONSTRAINT_MARGIN_GONE] = marginGone;
    }

    public void addConstraint(String id, String from, String targetId, String to, int margin, int marginGone) {
        int fromAnchor = mapAnchorStr.get(from);
        int toAnchor = mapAnchorStr.get(to);
        addConstraint(id, fromAnchor, targetId, toAnchor, margin, marginGone);
       // displayTable();
    }

    public void addWidthDimension(String id, String type) {
        addDimension(id, type, WIDTH_IDX);
    }

    public void addHeightDimension(String id, String type) {
        addDimension(id, type, HEIGHT_IDX);
    }

    public void addDimension(String id, String type, int dimension) {
        int index = getIndex(id) * TOTAL + dimension;
        switch (type) {
            case "wrap": {
                table[index + OFFSET_DIMENSION_TYPE] = DIMENSION_WRAP;
            } break;
            case "parent": {
                table[index + OFFSET_DIMENSION_TYPE] = DIMENSION_PARENT;
            } break;
            case "spread": {
                table[index + OFFSET_DIMENSION_TYPE] = DIMENSION_SPREAD;
            } break;
            // TODO: Handle other dimensions types
        }
    }

    public void addCenterHorizontallyConstraint(String id, String targetId, float bias) {
        int index = getIndex(id) * TOTAL;
        int indexTarget = getIndex(targetId);
        table[index + ANCHOR_HORIZONTAL_CENTER + OFFSET_CENTER_CONSTRAINT] = indexTarget;
        table[index + ANCHOR_HORIZONTAL_CENTER + OFFSET_CENTER_BIAS] = Float.floatToIntBits(bias);
    }

    public void addCenterVerticallyConstraint(String id, String targetId, float bias) {
        int index = getIndex(id) * TOTAL;
        int indexTarget = getIndex(targetId);
        table[index + ANCHOR_VERTICAL_CENTER + OFFSET_CENTER_CONSTRAINT] = indexTarget;
        table[index + ANCHOR_VERTICAL_CENTER + OFFSET_CENTER_BIAS] = Float.floatToIntBits(bias);
    }

    public void addCenterConstraint(String id, String targetId) {
        int index = getIndex(id) * TOTAL;
        int indexTarget = getIndex(targetId);
        table[index + ANCHOR_CENTER + OFFSET_CENTER_CONSTRAINT] = indexTarget;
    }

    public String serialize() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i <= lastId; i++) {
            builder.append(serializeTableWidget(i));
            builder.append("\n");
        }
        return builder.toString();
    }

    public void apply(HashMap<String, ConstraintWidget> idsToConstraintWidgets, ConstraintWidget constraintWidget) {
        String id = constraintWidget.stringId;
        System.out.println("Apply <" + id + ">");
        int index = getIndex(id) * TOTAL;
        constraintWidget.resetAllConstraints();
        applyDimension(constraintWidget, index, WIDTH_IDX);
        applyDimension(constraintWidget, index, HEIGHT_IDX);
        applyConstraint(idsToConstraintWidgets, constraintWidget,index, ANCHOR_START);
        applyConstraint(idsToConstraintWidgets, constraintWidget,index, ANCHOR_END);
        applyConstraint(idsToConstraintWidgets, constraintWidget,index, ANCHOR_TOP);
        applyConstraint(idsToConstraintWidgets, constraintWidget,index, ANCHOR_BOTTOM);
        applyConstraint(idsToConstraintWidgets, constraintWidget,index, ANCHOR_BASELINE);
        applyAxisCenterConstraint(idsToConstraintWidgets, constraintWidget,index, ANCHOR_HORIZONTAL_CENTER);
        applyAxisCenterConstraint(idsToConstraintWidgets, constraintWidget,index, ANCHOR_VERTICAL_CENTER);
        //applyConstraint(idsToConstraintWidgets, constraintWidget,index, ANCHOR_CENTER);
    }

    private void applyDimension(ConstraintWidget constraintWidget, int index, int dimension) {
        index += dimension;
        int type = table[index + OFFSET_DIMENSION_TYPE];
        int value = table[index + OFFSET_DIMENSION_VALUE];
        // todo : add min/max
        if (dimension == WIDTH_IDX) {
            switch (type) {
                case DIMENSION_WRAP: {
                    constraintWidget.setHorizontalDimensionBehaviour(DimensionBehaviour.WRAP_CONTENT);
                }
                break;
                case DIMENSION_PARENT: {
                    constraintWidget.setHorizontalDimensionBehaviour(DimensionBehaviour.MATCH_PARENT);
                }
                break;
                case DIMENSION_SPREAD: {
                    constraintWidget.setHorizontalDimensionBehaviour(DimensionBehaviour.MATCH_CONSTRAINT);
                }
                break;
            }
        } else if (dimension == HEIGHT_IDX) {
            switch (type) {
                case DIMENSION_WRAP: {
                    constraintWidget.setVerticalDimensionBehaviour(DimensionBehaviour.WRAP_CONTENT);
                }
                break;
                case DIMENSION_PARENT: {
                    constraintWidget.setVerticalDimensionBehaviour(DimensionBehaviour.MATCH_PARENT);
                }
                break;
                case DIMENSION_SPREAD: {
                    constraintWidget.setVerticalDimensionBehaviour(DimensionBehaviour.MATCH_CONSTRAINT);
                }
                break;
            }
        }
    }

    private void applyConstraint(HashMap<String, ConstraintWidget> idsToConstraintWidgets, ConstraintWidget constraintWidget, int index, int from) {
        index += from;
        int target = table[index + OFFSET_CONSTRAINT_TARGET];
        if (target == 0) {
            return;
        }
        String targetString = mapIdxToIds.get(target);
        ConstraintWidget targetWidget = idsToConstraintWidgets.get(targetString);
        if (targetWidget == null) {
            System.out.println("Couldn't find target for " + targetString);
            return;
        }
        int to = table[index + OFFSET_CONSTRAINT_ANCHOR];
        int margin = table[index + OFFSET_CONSTRAINT_MARGIN];
        int marginGone = table[index + OFFSET_CONSTRAINT_MARGIN_GONE];

        ConstraintAnchor.Type startType = mapAnchorType.get(from);
        ConstraintAnchor.Type endType = mapAnchorType.get(to);
        constraintWidget.immediateConnect(startType, targetWidget, endType, margin, marginGone);
    }

    private void applyAxisCenterConstraint(HashMap<String, ConstraintWidget> idsToConstraintWidgets, ConstraintWidget constraintWidget, int index, int from) {
        index += from;
        int target = table[index + OFFSET_CENTER_CONSTRAINT];
        if (target == 0) {
            return;
        }
        String targetString = mapIdxToIds.get(target);
        ConstraintWidget targetWidget = idsToConstraintWidgets.get(targetString);
        if (targetWidget == null) {
            System.out.println("Couldn't find target for " + targetString);
            return;
        }
        float bias = Float.intBitsToFloat(table[index + OFFSET_CENTER_BIAS]);
        if (from == ANCHOR_HORIZONTAL_CENTER) {
            constraintWidget.immediateConnect(ConstraintAnchor.Type.LEFT, targetWidget, ConstraintAnchor.Type.LEFT, 0, 0);
            constraintWidget.immediateConnect(ConstraintAnchor.Type.RIGHT, targetWidget, ConstraintAnchor.Type.RIGHT, 0, 0);
            constraintWidget.setHorizontalBiasPercent(bias);
        } else if (from == ANCHOR_VERTICAL_CENTER) {
            constraintWidget.immediateConnect(ConstraintAnchor.Type.TOP, targetWidget, ConstraintAnchor.Type.TOP, 0, 0);
            constraintWidget.immediateConnect(ConstraintAnchor.Type.BOTTOM, targetWidget, ConstraintAnchor.Type.BOTTOM, 0, 0);
            constraintWidget.setVerticalBiasPercent(bias);
        }
    }

    public void clear() {
        Arrays.fill(table, 0);
    }
}
