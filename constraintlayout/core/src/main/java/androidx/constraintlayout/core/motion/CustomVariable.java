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
package androidx.constraintlayout.core.motion;

import androidx.constraintlayout.core.motion.utils.TypedValues;

/**
 * Defines non standard Attributes
 */
public class CustomVariable {
    private static final String TAG = "TransitionLayout";
    String mName;
    private int mType;
    private int mIntegerValue = Integer.MIN_VALUE;
    private float mFloatValue = Float.NaN;
    private String mStringValue = null;
    boolean mBooleanValue;

    public CustomVariable(String name, int type, String value) {
        mName = name;
        mType = type;
        mStringValue = value;
    }

    public CustomVariable(String name, int type, int value) {
        mName = name;
        mType = type;
        if (type == TypedValues.Custom.TYPE_FLOAT) { // catch int ment for float
            mFloatValue = value;
        } else {
            mIntegerValue = value;
        }
    }

    public CustomVariable(String name, int type, float value) {
        mName = name;
        mType = type;
        mFloatValue = value;
    }

    public CustomVariable(String name, int type, boolean value) {
        mName = name;
        mType = type;
        mBooleanValue = value;
    }

    private static String colorString(int v) {
        String str = "00000000" + Integer.toHexString(v);
        return "#" + str.substring(str.length() - 8);
    }

    @Override
    public String toString() {
        String str = mName + ':';
        switch (mType) {
            case TypedValues.Custom.TYPE_INT:
                return str + mIntegerValue;
            case TypedValues.Custom.TYPE_FLOAT:
                return str + mFloatValue;
            case TypedValues.Custom.TYPE_COLOR:
                return str + colorString(mIntegerValue);
            case TypedValues.Custom.TYPE_STRING:
                return str + mStringValue;
            case TypedValues.Custom.TYPE_BOOLEAN:
                return str + (Boolean) mBooleanValue;
            case TypedValues.Custom.TYPE_DIMENSION:
                return str + mFloatValue;
        }
        return str + "????";
    }

    public int getType() {
        return mType;
    }

    public boolean getBooleanValue() {
        return mBooleanValue;
    }

    public float getFloatValue() {
        return mFloatValue;
    }

    public int getColorValue() {
        return mIntegerValue;
    }

    public int getIntegerValue() {
        return mIntegerValue;
    }

    public String getStringValue() {
        return mStringValue;
    }

    /**
     * Continuous types are interpolated they are fired only at
     *
     * @return
     */
    public boolean isContinuous() {
        switch (mType) {
            case TypedValues.Custom.TYPE_REFERENCE:
            case TypedValues.Custom.TYPE_BOOLEAN:
            case TypedValues.Custom.TYPE_STRING:
                return false;
            default:
                return true;
        }
    }

    public void setFloatValue(float value) {
        mFloatValue = value;
    }

    public void setBooleanValue(boolean value) {
        mBooleanValue = value;
    }

    public void setIntValue(int value) {
        mIntegerValue = value;
    }

    public void setStringValue(String value) {
        mStringValue = value;
    }

    /**
     * The number of interpolation values that need to be interpolated
     * Typically 1 but 3 for colors.
     *
     * @return Typically 1 but 3 for colors.
     */
    public int numberOfInterpolatedValues() {
        switch (mType) {
            case TypedValues.Custom.TYPE_COLOR:
                return 4;
            default:
                return 1;
        }
    }

    /**
     * Transforms value to a float for the purpose of interpolation
     *
     * @return interpolation value
     */
    public float getValueToInterpolate() {
        switch (mType) {
            case TypedValues.Custom.TYPE_INT:
                return mIntegerValue;
            case TypedValues.Custom.TYPE_FLOAT:
                return mFloatValue;
            case TypedValues.Custom.TYPE_COLOR:

                throw new RuntimeException("Color does not have a single color to interpolate");
            case TypedValues.Custom.TYPE_STRING:
                throw new RuntimeException("Cannot interpolate String");
            case TypedValues.Custom.TYPE_BOOLEAN:
                return mBooleanValue ? 1 : 0;
            case TypedValues.Custom.TYPE_DIMENSION:
                return mFloatValue;
        }
        return Float.NaN;
    }

    public void getValuesToInterpolate(float[] ret) {
        switch (mType) {
            case TypedValues.Custom.TYPE_INT:
                ret[0] = mIntegerValue;
                break;
            case TypedValues.Custom.TYPE_FLOAT:
                ret[0] = mFloatValue;
                break;
            case TypedValues.Custom.TYPE_COLOR:
                int a = 0xFF & (mIntegerValue >> 24);
                int r = 0xFF & (mIntegerValue >> 16);
                int g = 0xFF & (mIntegerValue >> 8);
                int b = 0xFF & (mIntegerValue);
                float f_r = (float) Math.pow(r / 255.0f, 2.2);
                float f_g = (float) Math.pow(g / 255.0f, 2.2);
                float f_b = (float) Math.pow(b / 255.0f, 2.2);
                ret[0] = f_r;
                ret[1] = f_g;
                ret[2] = f_b;
                ret[3] = a / 255f;
                break;
            case TypedValues.Custom.TYPE_STRING:
                throw new RuntimeException("Color does not have a single color to interpolate");
            case TypedValues.Custom.TYPE_BOOLEAN:
                ret[0] = mBooleanValue ? 1 : 0;
                break;
            case TypedValues.Custom.TYPE_DIMENSION:
                ret[0] = mFloatValue;
                break;
        }
    }

    public void setValue(float[] value) {
        switch (mType) {
            case TypedValues.Custom.TYPE_REFERENCE:
            case TypedValues.Custom.TYPE_INT:
                mIntegerValue = (int) value[0];
                break;
            case TypedValues.Custom.TYPE_FLOAT:
            case TypedValues.Custom.TYPE_DIMENSION:
                mFloatValue = value[0];
                break;
            case TypedValues.Custom.TYPE_COLOR:
                mIntegerValue = hsvToRgb(value[0], value[1], value[2]);
                mIntegerValue = (mIntegerValue & 0xFFFFFF) | (clamp((int) (0xFF * value[3])) << 24);
                break;
            case TypedValues.Custom.TYPE_STRING:
                throw new RuntimeException("Color does not have a single color to interpolate");
            case TypedValues.Custom.TYPE_BOOLEAN:
                mBooleanValue = value[0] > 0.5;
                break;
        }
    }

    public static int hsvToRgb(float hue, float saturation, float value) {
        int h = (int) (hue * 6);
        float f = hue * 6 - h;
        int p = (int) (0.5f + 255 * value * (1 - saturation));
        int q = (int) (0.5f + 255 * value * (1 - f * saturation));
        int t = (int) (0.5f + 255 * value * (1 - (1 - f) * saturation));
        int v = (int) (0.5f + 255 * value);
        switch (h) {
            case 0:
                return 0XFF000000 | (v << 16) + (t << 8) + p;
            case 1:
                return 0XFF000000 | (q << 16) + (v << 8) + p;
            case 2:
                return 0XFF000000 | (p << 16) + (v << 8) + t;
            case 3:
                return 0XFF000000 | (p << 16) + (q << 8) + v;
            case 4:
                return 0XFF000000 | (t << 16) + (p << 8) + v;
            case 5:
                return 0XFF000000 | (v << 16) + (p << 8) + q;

        }
        return 0;
    }

    /**
     * test if the two attributes are different
     *
     * @param CustomAttribute
     * @return
     */
    public boolean diff(CustomVariable CustomAttribute) {
        if (CustomAttribute == null || mType != CustomAttribute.mType) {
            return false;
        }
        switch (mType) {
            case TypedValues.Custom.TYPE_INT:
            case TypedValues.Custom.TYPE_REFERENCE:
                return mIntegerValue == CustomAttribute.mIntegerValue;
            case TypedValues.Custom.TYPE_FLOAT:
                return mFloatValue == CustomAttribute.mFloatValue;
            case TypedValues.Custom.TYPE_COLOR:
                return mIntegerValue == CustomAttribute.mIntegerValue;
            case TypedValues.Custom.TYPE_STRING:
                return mIntegerValue == CustomAttribute.mIntegerValue;
            case TypedValues.Custom.TYPE_BOOLEAN:
                return mBooleanValue == CustomAttribute.mBooleanValue;
            case TypedValues.Custom.TYPE_DIMENSION:
                return mFloatValue == CustomAttribute.mFloatValue;
        }
        return false;
    }

    public CustomVariable(String name, int attributeType) {
        mName = name;
        mType = attributeType;
    }

    public CustomVariable(String name, int attributeType, Object value) {
        mName = name;
        mType = attributeType;
        setValue(value);
    }

    public CustomVariable(CustomVariable source, Object value) {
        mName = source.mName;
        mType = source.mType;
        setValue(value);

    }

    public void setValue(Object value) {
        switch (mType) {
            case TypedValues.Custom.TYPE_REFERENCE:
            case TypedValues.Custom.TYPE_INT:
                mIntegerValue = (Integer) value;
                break;
            case TypedValues.Custom.TYPE_FLOAT:
                mFloatValue = (Float) value;
                break;
            case TypedValues.Custom.TYPE_COLOR:
                mIntegerValue = (Integer) value;
                break;
            case TypedValues.Custom.TYPE_STRING:
                mStringValue = (String) value;
                break;
            case TypedValues.Custom.TYPE_BOOLEAN:
                mBooleanValue = (Boolean) value;
                break;
            case TypedValues.Custom.TYPE_DIMENSION:
                mFloatValue = (Float) value;
                break;
        }
    }

    private static int clamp(int c) {
        int N = 255;
        c &= ~(c >> 31);
        c -= N;
        c &= (c >> 31);
        c += N;
        return c;
    }

    public int getInterpolatedColor(float[] value) {
        int r = clamp((int) ((float) Math.pow(value[0], 1.0 / 2.2) * 255.0f));
        int g = clamp((int) ((float) Math.pow(value[1], 1.0 / 2.2) * 255.0f));
        int b = clamp((int) ((float) Math.pow(value[2], 1.0 / 2.2) * 255.0f));
        int a = clamp((int) (value[3] * 255.0f));
        int color = (a << 24) | (r << 16) | (g << 8) | b;
        return color;
    }

    public void setInterpolatedValue(MotionWidget view, float[] value) {

        switch (mType) {
            case TypedValues.Custom.TYPE_INT:
                view.setCustomAttribute(mName, mType, (int) value[0]);
                break;
            case TypedValues.Custom.TYPE_COLOR:
                int r = clamp((int) ((float) Math.pow(value[0], 1.0 / 2.2) * 255.0f));
                int g = clamp((int) ((float) Math.pow(value[1], 1.0 / 2.2) * 255.0f));
                int b = clamp((int) ((float) Math.pow(value[2], 1.0 / 2.2) * 255.0f));
                int a = clamp((int) (value[3] * 255.0f));
                int color = (a << 24) | (r << 16) | (g << 8) | b;
                view.setCustomAttribute(mName, mType, color);
                break;
            case TypedValues.Custom.TYPE_REFERENCE:
            case TypedValues.Custom.TYPE_STRING:
                throw new RuntimeException("unable to interpolate " + mName);
            case TypedValues.Custom.TYPE_BOOLEAN:
                view.setCustomAttribute(mName, mType, value[0] > 0.5f);
                break;
            case TypedValues.Custom.TYPE_DIMENSION:
            case TypedValues.Custom.TYPE_FLOAT:
                view.setCustomAttribute(mName, mType, value[0]);
                break;
        }
    }

    public static int rgbaTocColor(float r, float g, float b, float a) {
        int ir = clamp((int) (r * 255f));
        int ig = clamp((int) (g * 255f));
        int ib = clamp((int) (b * 255f));
        int ia = clamp((int) (a * 255f));
        int color = (ia << 24) | (ir << 16) | (ig << 8) | ib;
        return color;
    }

    public void applyToWidget(MotionWidget view) {
        switch (mType) {
            case TypedValues.Custom.TYPE_INT:
            case TypedValues.Custom.TYPE_COLOR:
            case TypedValues.Custom.TYPE_REFERENCE:
                view.setCustomAttribute(mName, mType, mIntegerValue);
                break;
            case TypedValues.Custom.TYPE_STRING:
                view.setCustomAttribute(mName, mType, mStringValue);
            case TypedValues.Custom.TYPE_BOOLEAN:
                view.setCustomAttribute(mName, mType, mBooleanValue);
                break;
            case TypedValues.Custom.TYPE_DIMENSION:
            case TypedValues.Custom.TYPE_FLOAT:
                view.setCustomAttribute(mName, mType, mFloatValue);
                break;
        }
    }
}
