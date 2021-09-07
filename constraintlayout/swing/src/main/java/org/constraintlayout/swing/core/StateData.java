package org.constraintlayout.swing.core;

import java.util.HashMap;

public class StateData {
    private static boolean DEBUG = true;
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

    static class Constraint {
        int mOffset;
        StateData mData;

        void setData(StateData data) {
            mData = data;
        }

        void setOffset(int offset) {
            mOffset = offset;
        }

        final static int WIDTH_TYPE = 0;
        final static int WIDTH_VALUE = 1;
        final static int WIDTH_MIN = 2;
        final static int WIDTH_MAX = 3;
        final static int HEIGHT_TYPE = 4;
        final static int HEIGHT_VALUE = 5;
        final static int HEIGHT_MIN = 6;
        final static int HEIGHT_MAX = 7;

        final static int START_ = 8;
        final static int END_ = 12;
        final static int TOP_ = 16;
        final static int BOTTOM_ = 20;
        final static int BASELINE_ = 24;

        final static int _TARGET_ID = 0;
        final static int _ANCHOR = 1;
        final static int _MARGIN_ = 2;
        final static int _MARGIN_GONE = 3;

        final static int CENTER_TARGET_ID = 28;
        final static int HORIZONTAL_CENTER_TARGET_ID = 29;
        final static int HORIZONTAL_CENTER_BIAS = 30;
        final static int VERTICAL_CENTER_TARGET_ID = 31;
        final static int VERTICAL_CENTER_TARGET_BIAS = 32;

        public void set(int item, int offset, int value) {
            mData.table[item + offset] = value;
        }
        public void set(int item, int value) {
            mData.table[item ] = value;
        }
        public void set(int item, int offset, float value) {
            set(item, offset, Float.floatToIntBits(value));
        }
        public void set(int item, float value) {
            set(item,  Float.floatToIntBits(value));
        }

        int getInt(int item, int offset) {
            return mData.table[item + offset];
        }
        int getInt(int item) {
            return mData.table[item];
        }

        float getFloat(int item, int offset) {
            return Float.intBitsToFloat(getInt(item, offset));
        }
    }

}
