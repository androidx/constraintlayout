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

package androidx.constraintlayout.core.state;

import androidx.constraintlayout.core.motion.CustomVariable;
import androidx.constraintlayout.core.motion.utils.TypedValues;
import androidx.constraintlayout.core.widgets.ConstraintWidget;

import java.util.HashMap;
import java.util.Set;

/**
 * Utility class to encapsulate layout of a widget
 */
public class WidgetFrame {
    private final static boolean OLD_SYSTEM = true;
    public ConstraintWidget widget = null;
    public int left = 0;
    public int top = 0;
    public int right = 0;
    public int bottom = 0;

    // transforms

    public float pivotX = Float.NaN;
    public float pivotY = Float.NaN;

    public float rotationX = Float.NaN;
    public float rotationY = Float.NaN;
    public float rotationZ = Float.NaN;

    public float translationX = Float.NaN;
    public float translationY = Float.NaN;
    public float translationZ = Float.NaN;

    public float scaleX = Float.NaN;
    public float scaleY = Float.NaN;

    public float alpha = Float.NaN;

    public int visibility = ConstraintWidget.VISIBLE;

    final HashMap<String, CustomVariable> mCustom = new HashMap<>();

    public int width() {
        return right - left;
    }

    public int height() {
        return bottom - top;
    }

    public WidgetFrame() {
    }

    public WidgetFrame(ConstraintWidget widget) {
        this.widget = widget;
    }

    public WidgetFrame(WidgetFrame frame) {
        widget = frame.widget;
        left = frame.left;
        top = frame.top;
        right = frame.right;
        bottom = frame.bottom;
        updateAttributes(frame);
    }

    public void updateAttributes(WidgetFrame frame) {
        pivotX = frame.pivotX;
        pivotY = frame.pivotY;
        rotationX = frame.rotationX;
        rotationY = frame.rotationY;
        rotationZ = frame.rotationZ;
        translationX = frame.translationX;
        translationY = frame.translationY;
        translationZ = frame.translationZ;
        scaleX = frame.scaleX;
        scaleY = frame.scaleY;
        alpha = frame.alpha;
        visibility = frame.visibility;

        mCustom.clear();
        if (frame != null) {
            for (CustomVariable c : frame.mCustom.values()) {
                mCustom.put(c.getName(), c.copy());
            }
        }
    }

    public boolean isDefaultTransform() {
        return Float.isNaN(rotationX)
                && Float.isNaN(rotationY)
                && Float.isNaN(rotationZ)
                && Float.isNaN(translationX)
                && Float.isNaN(translationY)
                && Float.isNaN(translationZ)
                && Float.isNaN(scaleX)
                && Float.isNaN(scaleY)
                && Float.isNaN(alpha);
    }

    public static void interpolate(int parentWidth, int parentHeight, WidgetFrame frame, WidgetFrame start, WidgetFrame end, Transition transition, float progress) {
        int frameNumber = (int) (progress * 100);
        int startX = start.left;
        int startY = start.top;
        int endX = end.left;
        int endY = end.top;
        int startWidth = start.right - start.left;
        int startHeight = start.bottom - start.top;
        int endWidth = end.right - end.left;
        int endHeight = end.bottom - end.top;

        float progressPosition = progress;

        float startAlpha = start.alpha;
        float endAlpha = end.alpha;

        if (start.visibility == ConstraintWidget.GONE) {
            // On visibility gone, keep the same size to do an alpha to zero
            startX -= endWidth / 2f;
            startY -= endHeight / 2f;
            startWidth = endWidth;
            startHeight = endHeight;
            if (Float.isNaN(startAlpha)) {
                // override only if not defined...
                startAlpha = 0f;
            }
        }

        if (end.visibility == ConstraintWidget.GONE) {
            // On visibility gone, keep the same size to do an alpha to zero
            endX -= startWidth / 2f;
            endY -= startHeight / 2f;
            endWidth = startWidth;
            endHeight = startHeight;
            if (Float.isNaN(endAlpha)) {
                // override only if not defined...
                endAlpha = 0f;
            }
        }

        if (Float.isNaN(startAlpha) && !Float.isNaN(endAlpha)) {
            startAlpha = 1f;
        }
        if (!Float.isNaN(startAlpha) && Float.isNaN(endAlpha)) {
            endAlpha = 1f;
        }

        if (frame.widget != null && transition.hasPositionKeyframes()) {
            Transition.KeyPosition firstPosition = transition.findPreviousPosition(frame.widget.stringId, frameNumber);
            Transition.KeyPosition lastPosition = transition.findNextPosition(frame.widget.stringId, frameNumber);

            if (firstPosition == lastPosition) {
                lastPosition = null;
            }
            int interpolateStartFrame = 0;
            int interpolateEndFrame = 100;

            if (firstPosition != null) {
                startX = (int) (firstPosition.x * parentWidth);
                startY = (int) (firstPosition.y * parentHeight);
                interpolateStartFrame = firstPosition.frame;
            }
            if (lastPosition != null) {
                endX = (int) (lastPosition.x * parentWidth);
                endY = (int) (lastPosition.y * parentHeight);
                interpolateEndFrame = lastPosition.frame;
            }

            progressPosition = (progress * 100f - interpolateStartFrame) / (float) (interpolateEndFrame - interpolateStartFrame);
        }

        frame.widget = start.widget;

        frame.left = (int) (startX + progressPosition * (endX - startX));
        frame.top = (int) (startY + progressPosition * (endY - startY));
        int width = (int) ((1 - progress) * startWidth + (progress * endWidth));
        int height = (int) ((1 - progress) * startHeight + (progress * endHeight));
        frame.right = frame.left + width;
        frame.bottom = frame.top + height;

        frame.pivotX = interpolate(start.pivotX, end.pivotX, 0.5f, progress);
        frame.pivotY = interpolate(start.pivotY, end.pivotY, 0.5f, progress);

        frame.rotationX = interpolate(start.rotationX, end.rotationX, 0f, progress);
        frame.rotationY = interpolate(start.rotationY, end.rotationY, 0f, progress);
        frame.rotationZ = interpolate(start.rotationZ, end.rotationZ, 0f, progress);

        frame.scaleX = interpolate(start.scaleX, end.scaleX, 1f, progress);
        frame.scaleY = interpolate(start.scaleY, end.scaleY, 1f, progress);

        frame.translationX = interpolate(start.translationX, end.translationX, 0f, progress);
        frame.translationY = interpolate(start.translationY, end.translationY, 0f, progress);
        frame.translationZ = interpolate(start.translationZ, end.translationZ, 0f, progress);

        frame.alpha = interpolate(startAlpha, endAlpha, 1f, progress);
    }

    private static float interpolate(float start, float end, float defaultValue, float progress) {
        boolean isStartUnset = Float.isNaN(start);
        boolean isEndUnset = Float.isNaN(end);
        if (isStartUnset && isEndUnset) {
            return Float.NaN;
        }
        if (isStartUnset) {
            start = defaultValue;
        }
        if (isEndUnset) {
            end = defaultValue;
        }
        return (start + progress * (end - start));
    }

    public float centerX() {
        return left + (right - left) / 2f;
    }

    public float centerY() {
        return top + (bottom - top) / 2f;
    }

    public WidgetFrame update() {
        if (widget != null) {
            left = widget.getLeft();
            top = widget.getTop();
            right = widget.getRight();
            bottom = widget.getBottom();
            WidgetFrame frame = widget.frame;
            updateAttributes(frame);
        }
        return this;
    }

    public WidgetFrame update(ConstraintWidget widget) {
        if (widget == null) {
            return this;
        }

        this.widget = widget;
        update();
        return this;
    }

    public void addCustomColor(String name, int color) {
        setCustomAttribute(name, TypedValues.Custom.TYPE_COLOR, color);
    }

    public int getCustomColor(String name) {
        if (mCustom.containsKey(name)) {
            int color = mCustom.get(name).getColorValue();
            return color;
        }
        return 0xFFFFAA88;
    }

    public void addCustomFloat(String name, float value) {
        setCustomAttribute(name, TypedValues.Custom.TYPE_FLOAT, value);
    }

    public float getCustomFloat(String name) {
        if (mCustom.containsKey(name)) {
            return mCustom.get(name).getFloatValue();
        }
        return Float.NaN;
    }

    public void setCustomAttribute(String name, int type, float value) {
        if (mCustom.containsKey(name)) {
            mCustom.get(name).setFloatValue(value);
        } else {
            mCustom.put(name, new CustomVariable(name, type, value));
        }
    }

    public void setCustomAttribute(String name, int type, int value) {
        if (mCustom.containsKey(name)) {
            mCustom.get(name).setIntValue(value);
        } else {
            mCustom.put(name, new CustomVariable(name, type, value));
        }
    }

    public void setCustomAttribute(String name, int type, boolean value) {
        if (mCustom.containsKey(name)) {
            mCustom.get(name).setBooleanValue(value);
        } else {
            mCustom.put(name, new CustomVariable(name, type, value));
        }
    }

    public void setCustomAttribute(String name, int type, String value) {
        if (mCustom.containsKey(name)) {
            mCustom.get(name).setStringValue(value);
        } else {
            mCustom.put(name, new CustomVariable(name, type, value));
        }
    }

    public CustomVariable getCustomAttribute(String name) {
        return mCustom.get(name);
    }

    public Set<String> getCustomAttributeNames() {
        return mCustom.keySet();
    }

    void printCustomAttributes() {
        StackTraceElement s = new Throwable().getStackTrace()[1];
        String ss = ".(" + s.getFileName() + ":" + s.getLineNumber() + ") " + s.getMethodName();
        ss += " " + (this.hashCode() % 1000);
        if (widget != null) {
            ss += "/" + (widget.hashCode() % 1000) + " ";
        } else {
            ss += "/NULL ";
        }
        if (mCustom != null)
            for (String key : mCustom.keySet()) {
                System.out.println(ss + mCustom.get(key).toString());
            }
    }

    void logv(String str) {
        StackTraceElement s = new Throwable().getStackTrace()[1];
        String ss = ".(" + s.getFileName() + ":" + s.getLineNumber() + ") " + s.getMethodName();
        ss += " " + (this.hashCode() % 1000);
        if (widget != null) {
            ss += "/" + (widget.hashCode() % 1000);
        } else {
            ss += "/NULL";
        }

        System.out.println(ss + " " + str);
    }

}
