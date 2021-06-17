package androidx.constraintlayout.core.motion.utils;

import java.util.Arrays;

public class TypedBundle {

    private static final int INITIAL_BOOLEAN = 4;
    private static final int INITIAL_INT = 10;
    private static final int INITIAL_FLOAT = 10;
    private static final int INITIAL_STRING = 5;

    int[] mTypeInt = new int[INITIAL_INT];
    int[] mValueInt = new int[INITIAL_INT];
    int mCountInt = 0;
    int[] mTypeFloat = new int[INITIAL_FLOAT];
    float[] mValueFloat = new float[INITIAL_FLOAT];
    int mCountFloat = 0;
    int[] mTypeString = new int[INITIAL_STRING];
    String[] mValueString = new String[INITIAL_STRING];
    int mCountString = 0;
    int[] mTypeBoolean = new int[INITIAL_BOOLEAN];
    boolean[] mValueBoolean = new boolean[INITIAL_BOOLEAN];
    int mCountBoolean = 0;

    public void add(int type, int value) {
        if (mCountInt >= mTypeInt.length) {
            mTypeInt = Arrays.copyOf(mTypeInt, mTypeInt.length * 2);
            mValueInt = Arrays.copyOf(mValueInt, mValueInt.length * 2);
        }
        mTypeInt[mCountInt] = type;
        mValueInt[mCountInt++] = value;
    }

    public void add(int type, float value) {
        if (mCountFloat >= mTypeFloat.length) {
            mTypeFloat = Arrays.copyOf(mTypeFloat, mTypeFloat.length * 2);
            mValueFloat = Arrays.copyOf(mValueFloat, mValueFloat.length * 2);
        }
        mTypeFloat[mCountFloat] = type;
        mValueFloat[mCountFloat++] = value;
    }

    public void add(int type, String value) {
        if (mCountString >= mTypeString.length) {
            mTypeString = Arrays.copyOf(mTypeString, mTypeString.length * 2);
            mValueString = Arrays.copyOf(mValueString, mValueString.length * 2);
        }
        mTypeString[mCountString] = type;
        mValueString[mCountString++] = value;
    }

    public void add(int type, boolean value) {
        if (mCountBoolean >= mTypeBoolean.length) {
            mTypeBoolean = Arrays.copyOf(mTypeBoolean, mTypeBoolean.length * 2);
            mValueBoolean = Arrays.copyOf(mValueBoolean, mValueBoolean.length * 2);
        }
        mTypeBoolean[mCountBoolean] = type;
        mValueBoolean[mCountBoolean++] = value;
    }

    public interface TypedValues {
        void setValue(int type, int value);

        void setValue(int type, float value);

        void setValue(int type, String value);

        void setValue(int type, boolean value);
    }

    public void applyDelta(TypedValues values) {
        for (int i = 0; i < mCountInt; i++) {
            values.setValue(mTypeInt[i], mValueInt[i]);
        }
        for (int i = 0; i < mCountFloat; i++) {
            values.setValue(mTypeFloat[i], mValueFloat[i]);
        }
        for (int i = 0; i < mCountString; i++) {
            values.setValue(mTypeString[i], mValueString[i]);
        }
        for (int i = 0; i < mCountBoolean; i++) {
            values.setValue(mTypeBoolean[i], mValueBoolean[i]);
        }
    }

    public void clear() {
        mCountBoolean = 0;
        mCountString = 0;
        mCountFloat = 0;
        mCountInt = 0;
    }
}
