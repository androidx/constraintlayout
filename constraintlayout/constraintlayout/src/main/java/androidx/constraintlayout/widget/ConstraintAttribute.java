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
package androidx.constraintlayout.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import androidx.constraintlayout.motion.widget.Debug;

import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;
import android.view.View;

import org.xmlpull.v1.XmlPullParser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Defines non standard Attributes
 *
 * @hide
 */
public class ConstraintAttribute {
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
    public int noOfInterpValues() {
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
        }
        return Float.NaN;
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
                mColorValue = Color.HSVToColor(value);
                mColorValue = (mColorValue & 0xFFFFFF) | (clamp((int) (0xFF * value[3])) << 24);
                break;
            case STRING_TYPE:
                throw new RuntimeException("Color does not have a single color to interpolate");
            case BOOLEAN_TYPE:
                mBooleanValue = value[0] > 0.5;
                break;
            case DIMENSION_TYPE:
                mFloatValue = value[0];

        }
    }

    /**
     * test if the two attributes are different
     *
     * @param constraintAttribute
     * @return
     */
    public boolean diff(ConstraintAttribute constraintAttribute) {
        if (constraintAttribute == null || mType != constraintAttribute.mType) {
            return false;
        }
        switch (mType) {
            case INT_TYPE:
            case REFERENCE_TYPE:
                return mIntegerValue == constraintAttribute.mIntegerValue;
            case FLOAT_TYPE:
                return mFloatValue == constraintAttribute.mFloatValue;
            case COLOR_TYPE:
            case COLOR_DRAWABLE_TYPE:
                return mColorValue == constraintAttribute.mColorValue;
            case STRING_TYPE:
                return mIntegerValue == constraintAttribute.mIntegerValue;
            case BOOLEAN_TYPE:
                return mBooleanValue == constraintAttribute.mBooleanValue;
            case DIMENSION_TYPE:
                return mFloatValue == constraintAttribute.mFloatValue;
        }
        return false;
    }

    public ConstraintAttribute(String name, AttributeType attributeType) {
        mName = name;
        mType = attributeType;
    }

    public ConstraintAttribute(String name, AttributeType attributeType, Object value, boolean method) {
        mName = name;
        mType = attributeType;
        mMethod = method;
        setValue(value);
    }

    public ConstraintAttribute(ConstraintAttribute source, Object value) {
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
        }
    }

    public static HashMap<String, ConstraintAttribute> extractAttributes(
            HashMap<String, ConstraintAttribute> base, View view) {
        HashMap<String, ConstraintAttribute> ret = new HashMap<>();
        Class<? extends View> viewClass = view.getClass();
        for (String name : base.keySet()) {
            ConstraintAttribute constraintAttribute = base.get(name);

            try {
                if (name.equals("BackgroundColor")) { // hack for getMap set background color
                    ColorDrawable viewColor = (ColorDrawable) view.getBackground();
                    Object val = viewColor.getColor();
                    ret.put(name, new ConstraintAttribute(constraintAttribute, val));
                } else {
                    Method method = viewClass.getMethod("getMap" + name);
                    Object val = method.invoke(view);
                    ret.put(name, new ConstraintAttribute(constraintAttribute, val));
                }

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

    public static void setAttributes(View view, HashMap<String, ConstraintAttribute> map) {
        Class<? extends View> viewClass = view.getClass();
        for (String name : map.keySet()) {
            ConstraintAttribute constraintAttribute = map.get(name);
            String methodName = name;
            if (!constraintAttribute.mMethod) {
                methodName = "set" + methodName;
            }
            try {
                Method method;
                switch (constraintAttribute.mType) {
                    case INT_TYPE:
                        method = viewClass.getMethod(methodName, Integer.TYPE);
                        method.invoke(view, constraintAttribute.mIntegerValue);
                        break;
                    case FLOAT_TYPE:
                        method = viewClass.getMethod(methodName, Float.TYPE);
                        method.invoke(view, constraintAttribute.mFloatValue);
                        break;
                    case COLOR_DRAWABLE_TYPE:
                        method = viewClass.getMethod(methodName, Drawable.class);
                        ColorDrawable drawable = new ColorDrawable(); // TODO cache
                        drawable.setColor(constraintAttribute.mColorValue);
                        method.invoke(view, drawable);
                        break;
                    case COLOR_TYPE:
                        method = viewClass.getMethod(methodName, Integer.TYPE);
                        method.invoke(view, constraintAttribute.mColorValue);
                        break;
                    case STRING_TYPE:
                        method = viewClass.getMethod(methodName, CharSequence.class);
                        method.invoke(view, constraintAttribute.mStringValue);
                        break;
                    case BOOLEAN_TYPE:
                        method = viewClass.getMethod(methodName, Boolean.TYPE);
                        method.invoke(view, constraintAttribute.mBooleanValue);
                        break;
                    case DIMENSION_TYPE:
                        method = viewClass.getMethod(methodName, Float.TYPE);
                        method.invoke(view, constraintAttribute.mFloatValue);
                        break;
                }
            } catch (NoSuchMethodException e) {
                Log.e(TAG, e.getMessage());
                Log.e(TAG, " Custom Attribute \"" + name + "\" not found on " + viewClass.getName());
                Log.e(TAG, viewClass.getName() + " must have a method " + methodName);
            } catch (IllegalAccessException e) {
                Log.e(TAG, " Custom Attribute \"" + name + "\" not found on " + viewClass.getName());
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                Log.e(TAG, " Custom Attribute \"" + name + "\" not found on " + viewClass.getName());
                e.printStackTrace();
            }
        }
    }

    public void applyCustom(View view) {
        Class<? extends View> viewClass = view.getClass();
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
                    if (AttributeType.REFERENCE_TYPE == mType)
                        Log.v(TAG, " call ing " + methodName + "  " + Debug.getName(view.getContext(), this.mIntegerValue));
                    break;
                case FLOAT_TYPE:
                    method = viewClass.getMethod(methodName, Float.TYPE);
                    method.invoke(view, this.mFloatValue);
                    break;
                case COLOR_DRAWABLE_TYPE:
                    method = viewClass.getMethod(methodName, Drawable.class);
                    ColorDrawable drawable = new ColorDrawable(); // TODO cache
                    drawable.setColor(this.mColorValue);
                    method.invoke(view, drawable);
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
            }
        } catch (NoSuchMethodException e) {
            Log.e(TAG, e.getMessage());
            Log.e(TAG, " Custom Attribute \"" + name + "\" not found on " + viewClass.getName());
            Log.e(TAG, viewClass.getName() + " must have a method " + methodName);
        } catch (IllegalAccessException e) {
            Log.e(TAG, " Custom Attribute \"" + name + "\" not found on " + viewClass.getName());
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            Log.e(TAG, " Custom Attribute \"" + name + "\" not found on " + viewClass.getName());
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

    public void setInterpolatedValue(View view, float[] value) {
        Class<? extends View> viewClass = view.getClass();

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
                case COLOR_DRAWABLE_TYPE: {
                    method = viewClass.getMethod(methodName, Drawable.class);
                    int r = clamp((int) ((float) Math.pow(value[0], 1.0 / 2.2) * 255.0f));
                    int g = clamp((int) ((float) Math.pow(value[1], 1.0 / 2.2) * 255.0f));
                    int b = clamp((int) ((float) Math.pow(value[2], 1.0 / 2.2) * 255.0f));
                    int a = clamp((int) (value[3] * 255.0f));
                    int color = a << 24 | (r << 16) | (g << 8) | b;
                    ColorDrawable drawable = new ColorDrawable(); // TODO cache
                    drawable.setColor(color);
                    method.invoke(view, drawable);
                }
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
            }
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "no method " + methodName + " on View \"" + Debug.getName(view) + "\"");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.e(TAG, "cannot access method " + methodName + " on View \"" + Debug.getName(view) + "\"");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void parse(Context context, XmlPullParser parser, HashMap<String, ConstraintAttribute> custom) {
        AttributeSet attributeSet = Xml.asAttributeSet(parser);
        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.CustomAttribute);
        String name = null;
        boolean method = false;
        Object value = null;
        AttributeType type = null;
        final int N = a.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.CustomAttribute_attributeName) {
                name = a.getString(attr);
                if (name != null && name.length() > 0) {
                    name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
                }
            } else if (attr == R.styleable.CustomAttribute_methodName) {
                method = true;
                name = a.getString(attr);
            } else if (attr == R.styleable.CustomAttribute_customBoolean) {
                value = a.getBoolean(attr, false);
                type = AttributeType.BOOLEAN_TYPE;
            } else if (attr == R.styleable.CustomAttribute_customColorValue) {
                type = AttributeType.COLOR_TYPE;
                value = a.getColor(attr, 0);
            } else if (attr == R.styleable.CustomAttribute_customColorDrawableValue) {
                type = AttributeType.COLOR_DRAWABLE_TYPE;
                value = a.getColor(attr, 0);
            } else if (attr == R.styleable.CustomAttribute_customPixelDimension) {
                type = AttributeType.DIMENSION_TYPE;
                value = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        a.getDimension(attr, 0),
                        context.getResources().getDisplayMetrics());
            } else if (attr == R.styleable.CustomAttribute_customDimension) {
                type = AttributeType.DIMENSION_TYPE;
                value = a.getDimension(attr, 0);
            } else if (attr == R.styleable.CustomAttribute_customFloatValue) {
                type = AttributeType.FLOAT_TYPE;
                value = a.getFloat(attr, Float.NaN);
            } else if (attr == R.styleable.CustomAttribute_customIntegerValue) {
                type = AttributeType.INT_TYPE;
                value = a.getInteger(attr, -1);
            } else if (attr == R.styleable.CustomAttribute_customStringValue) {
                type = AttributeType.STRING_TYPE;
                value = a.getString(attr);
            } else if (attr == R.styleable.CustomAttribute_customReference) {
                type = AttributeType.REFERENCE_TYPE;
                int tmp = a.getResourceId(attr, -1);
                if (tmp == -1) {
                    tmp = a.getInt(attr, -1);
                }
                value = tmp;
            }
        }
        if (name != null && value != null) {
            custom.put(name, new ConstraintAttribute(name, type, value, method));
        }
        a.recycle();
    }

}
