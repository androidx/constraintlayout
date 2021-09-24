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
/*
 * Copyright (C) 2017 The Android Open Source Project
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

import androidx.constraintlayout.core.motion.utils.Utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Defines non standard Attributes
 *
 * @suppress
 */
public class CustomAttribute {
    private static final String TAG = "TransitionLayout";
    private boolean mMethod = false;
    String mName;
    private AttributeType mType;
    private int mIntegerValue;
    private float mFloatValue;
    private String mStringValue;
    boolean mBooleanValue;
    private int mColorValue;

    public enum AttributeType {
        INT_TYPE,
        FLOAT_TYPE,
        COLOR_TYPE,
        COLOR_DRAWABLE_TYPE,
        STRING_TYPE,
        BOOLEAN_TYPE,
        DIMENSION_TYPE,
        REFERENCE_TYPE
    }

    public AttributeType getType() {
        return mType;
    }

    /**
     * Continuous types are interpolated they are fired only at
     *
     * @return
     */
    public boolean isContinuous() {
        switch (mType) {
            case REFERENCE_TYPE:
            case BOOLEAN_TYPE:
            case STRING_TYPE:
                return false;
            default:
                return true;
        }
    }

    public void setFloatValue(float value) {
        mFloatValue = value;
    }

    public void setColorValue(int value) {
        mColorValue = value;
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
            case COLOR_TYPE:
            case COLOR_DRAWABLE_TYPE:
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
            case INT_TYPE:
                return mIntegerValue;
            case FLOAT_TYPE:
                return mFloatValue;
            case COLOR_TYPE:
            case COLOR_DRAWABLE_TYPE:
                throw new RuntimeException("Color does not have a single color to interpolate");
            case STRING_TYPE:
                throw new RuntimeException("Cannot interpolate String");
            case BOOLEAN_TYPE:
                return mBooleanValue ? 1 : 0;
            case DIMENSION_TYPE:
                return mFloatValue;
            default:
                return Float.NaN;
        }
    }

    public void getValuesToInterpolate(float[] ret) {
        switch (mType) {
            case INT_TYPE:
                ret[0] = mIntegerValue;
                break;
            case FLOAT_TYPE:
                ret[0] = mFloatValue;
                break;
            case COLOR_DRAWABLE_TYPE:
            case COLOR_TYPE:
                int a = 0xFF & (mColorValue >> 24);
                int r = 0xFF & (mColorValue >> 16);
                int g = 0xFF & (mColorValue >> 8);
                int b = 0xFF & (mColorValue);
                float f_r = (float) Math.pow(r / 255.0f, 2.2);
                float f_g = (float) Math.pow(g / 255.0f, 2.2);
                float f_b = (float) Math.pow(b / 255.0f, 2.2);
                ret[0] = f_r;
                ret[1] = f_g;
                ret[2] = f_b;
                ret[3] = a / 255f;
                break;
            case STRING_TYPE:
                throw new RuntimeException("Color does not have a single color to interpolate");
            case BOOLEAN_TYPE:
                ret[0] = mBooleanValue ? 1 : 0;
                break;
            case DIMENSION_TYPE:
                ret[0] = mFloatValue;
                break;
            default: break;
        }
    }

    public void setValue(float[] value) {
        switch (mType) {
            case REFERENCE_TYPE:
            case INT_TYPE:
                mIntegerValue = (int) value[0];
                break;
            case FLOAT_TYPE:
                mFloatValue = value[0];
                break;
            case COLOR_DRAWABLE_TYPE:
            case COLOR_TYPE:
                mColorValue = hsvToRgb(value[0], value[1], value[2]);
                mColorValue = (mColorValue & 0xFFFFFF) | (clamp((int) (0xFF * value[3])) << 24);
                break;
            case STRING_TYPE:
                throw new RuntimeException("Color does not have a single color to interpolate");
            case BOOLEAN_TYPE:
                mBooleanValue = value[0] > 0.5;
                break;
            case DIMENSION_TYPE:
                mFloatValue = value[0];
                break;
            default: break;
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
            default:
                return 0;
        }
    }

    /**
     * test if the two attributes are different
     *
     * @param CustomAttribute
     * @return
     */
    public boolean diff(CustomAttribute CustomAttribute) {
        if (CustomAttribute == null || mType != CustomAttribute.mType) {
            return false;
        }
        switch (mType) {
            case INT_TYPE:
            case REFERENCE_TYPE:
                return mIntegerValue == CustomAttribute.mIntegerValue;
            case FLOAT_TYPE:
                return mFloatValue == CustomAttribute.mFloatValue;
            case COLOR_TYPE:
            case COLOR_DRAWABLE_TYPE:
                return mColorValue == CustomAttribute.mColorValue;
            case STRING_TYPE:
                return mIntegerValue == CustomAttribute.mIntegerValue;
            case BOOLEAN_TYPE:
                return mBooleanValue == CustomAttribute.mBooleanValue;
            case DIMENSION_TYPE:
                return mFloatValue == CustomAttribute.mFloatValue;
            default:
                return false;
        }
    }

    public CustomAttribute(String name, AttributeType attributeType) {
        mName = name;
        mType = attributeType;
    }

    public CustomAttribute(String name, AttributeType attributeType, Object value, boolean method) {
        mName = name;
        mType = attributeType;
        mMethod = method;
        setValue(value);
    }

    public CustomAttribute(CustomAttribute source, Object value) {
        mName = source.mName;
        mType = source.mType;
        setValue(value);

    }

    public void setValue(Object value) {
        switch (mType) {
            case REFERENCE_TYPE:
            case INT_TYPE:
                mIntegerValue = (Integer) value;
                break;
            case FLOAT_TYPE:
                mFloatValue = (Float) value;
                break;
            case COLOR_TYPE:
            case COLOR_DRAWABLE_TYPE:
                mColorValue = (Integer) value;
                break;
            case STRING_TYPE:
                mStringValue = (String) value;
                break;
            case BOOLEAN_TYPE:
                mBooleanValue = (Boolean) value;
                break;
            case DIMENSION_TYPE:
                mFloatValue = (Float) value;
                break;
            default: break;
        }
    }

    public static HashMap<String, CustomAttribute> extractAttributes(
            HashMap<String, CustomAttribute> base, Object view) {
        HashMap<String, CustomAttribute> ret = new HashMap<>();
        Class<? extends Object> viewClass = view.getClass();
        for (String name : base.keySet()) {
            CustomAttribute CustomAttribute = base.get(name);

            try {

                Method method = viewClass.getMethod("getMap" + name);
                Object val = method.invoke(view);
                ret.put(name, new CustomAttribute(CustomAttribute, val));

            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    public static void setAttributes(Object view, HashMap<String, CustomAttribute> map) {
        Class<? extends Object> viewClass = view.getClass();
        for (String name : map.keySet()) {
            CustomAttribute CustomAttribute = map.get(name);
            String methodName = name;
            if (!CustomAttribute.mMethod) {
                methodName = "set" + methodName;
            }
            try {
                Method method;
                switch (CustomAttribute.mType) {
                    case INT_TYPE:
                        method = viewClass.getMethod(methodName, Integer.TYPE);
                        method.invoke(view, CustomAttribute.mIntegerValue);
                        break;
                    case FLOAT_TYPE:
                        method = viewClass.getMethod(methodName, Float.TYPE);
                        method.invoke(view, CustomAttribute.mFloatValue);
                        break;
                    case COLOR_TYPE:
                        method = viewClass.getMethod(methodName, Integer.TYPE);
                        method.invoke(view, CustomAttribute.mColorValue);
                        break;
                    case STRING_TYPE:
                        method = viewClass.getMethod(methodName, CharSequence.class);
                        method.invoke(view, CustomAttribute.mStringValue);
                        break;
                    case BOOLEAN_TYPE:
                        method = viewClass.getMethod(methodName, Boolean.TYPE);
                        method.invoke(view, CustomAttribute.mBooleanValue);
                        break;
                    case DIMENSION_TYPE:
                        method = viewClass.getMethod(methodName, Float.TYPE);
                        method.invoke(view, CustomAttribute.mFloatValue);
                        break;
                    case REFERENCE_TYPE:
                        method = viewClass.getMethod(methodName, Integer.TYPE);
                        method.invoke(view, CustomAttribute.mIntegerValue);
                        break;
                    default: break;
                }
            } catch (NoSuchMethodException e) {
                Utils.loge(TAG, e.getMessage());
                Utils.loge(TAG, " Custom Attribute \"" + name + "\" not found on " + viewClass.getName());
                Utils.loge(TAG, viewClass.getName() + " must have a method " + methodName);
            } catch (IllegalAccessException e) {
                Utils.loge(TAG, " Custom Attribute \"" + name + "\" not found on " + viewClass.getName());
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                Utils.loge(TAG, " Custom Attribute \"" + name + "\" not found on " + viewClass.getName());
                e.printStackTrace();
            }
        }
    }

    public void applyCustom(Object view) {
        Class<? extends Object> viewClass = view.getClass();
        String name = this.mName;
        String methodName = name;
        if (!mMethod) {
            methodName = "set" + methodName;
        }
        try {
            Method method;
            switch (this.mType) {
                case INT_TYPE:
                case REFERENCE_TYPE:
                    method = viewClass.getMethod(methodName, Integer.TYPE);
                    method.invoke(view, this.mIntegerValue);
                    break;
                case FLOAT_TYPE:
                    method = viewClass.getMethod(methodName, Float.TYPE);
                    method.invoke(view, this.mFloatValue);
                    break;
                case COLOR_TYPE:
                    method = viewClass.getMethod(methodName, Integer.TYPE);
                    method.invoke(view, this.mColorValue);
                    break;
                case STRING_TYPE:
                    method = viewClass.getMethod(methodName, CharSequence.class);
                    method.invoke(view, this.mStringValue);
                    break;
                case BOOLEAN_TYPE:
                    method = viewClass.getMethod(methodName, Boolean.TYPE);
                    method.invoke(view, this.mBooleanValue);
                    break;
                case DIMENSION_TYPE:
                    method = viewClass.getMethod(methodName, Float.TYPE);
                    method.invoke(view, this.mFloatValue);
                    break;
                default: break;
            }
        } catch (NoSuchMethodException e) {
            Utils.loge(TAG, e.getMessage());
            Utils.loge(TAG, " Custom Attribute \"" + name + "\" not found on " + viewClass.getName());
            Utils.loge(TAG, viewClass.getName() + " must have a method " + methodName);
        } catch (IllegalAccessException e) {
            Utils.loge(TAG, " Custom Attribute \"" + name + "\" not found on " + viewClass.getName());
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            Utils.loge(TAG, " Custom Attribute \"" + name + "\" not found on " + viewClass.getName());
            e.printStackTrace();
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

    public void setInterpolatedValue(Object view, float[] value) {
        Class<? extends Object> viewClass = view.getClass();

        String methodName = "set" + mName;
        try {
            Method method;
            switch (mType) {
                case INT_TYPE:
                    method = viewClass.getMethod(methodName, Integer.TYPE);
                    method.invoke(view, (int) value[0]);
                    break;
                case FLOAT_TYPE:
                    method = viewClass.getMethod(methodName, Float.TYPE);
                    method.invoke(view, value[0]);
                    break;
                case COLOR_TYPE:
                    method = viewClass.getMethod(methodName, Integer.TYPE);
                    int r = clamp((int) ((float) Math.pow(value[0], 1.0 / 2.2) * 255.0f));
                    int g = clamp((int) ((float) Math.pow(value[1], 1.0 / 2.2) * 255.0f));
                    int b = clamp((int) ((float) Math.pow(value[2], 1.0 / 2.2) * 255.0f));
                    int a = clamp((int) (value[3] * 255.0f));
                    int color = a << 24 | (r << 16) | (g << 8) | b;
                    method.invoke(view, color);
                    break;
                case STRING_TYPE:
                    throw new RuntimeException("unable to interpolate strings " + mName);

                case BOOLEAN_TYPE:
                    method = viewClass.getMethod(methodName, Boolean.TYPE);
                    method.invoke(view, value[0] > 0.5f);
                    break;
                case DIMENSION_TYPE:
                    method = viewClass.getMethod(methodName, Float.TYPE);
                    method.invoke(view, value[0]);
                    break;
                default: break;
            }
        } catch (NoSuchMethodException e) {
            Utils.loge(TAG, "no method " + methodName + " on View \"" + view.getClass().getName() + "\"");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Utils.loge(TAG, "cannot access method " + methodName + " on View \"" + view.getClass().getName() + "\"");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
