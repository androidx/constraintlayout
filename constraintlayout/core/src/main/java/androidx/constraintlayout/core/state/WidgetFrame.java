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
    private final static boolean OLD_SYSTEM = false;
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

    public HashMap<String, Color> mCustomColors = null;
    public HashMap<String, Float> mCustomFloats = null;
    HashMap<String, CustomVariable> mCustom = new HashMap<>();

    public static class Color {
        public float r;
        public float g;
        public float b;
        public float a;

        public Color(float r, float g, float b, float a) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
        }

        public void copy(Color start) {
            this.r = start.r;
            this.g = start.g;
            this.b = start.b;
            this.a = start.a;
        }
    }


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

    private void updateAttributes(WidgetFrame frame) {
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

        if (frame.mCustomColors != null) {
            mCustomColors = new HashMap<>();
            mCustomColors.putAll(frame.mCustomColors);
        }
        if (frame.mCustomFloats != null) {
            mCustomFloats = new HashMap<>();
            mCustomFloats.putAll(frame.mCustomFloats);
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

    public static void interpolateColor(Color result, Color start, Color end, float progress) {
        if (progress < 0) {
            result.copy(start);
        } else if (progress > 1) {
            result.copy(end);
        } else {
            result.r = (1f - progress) * start.r + progress * (end.r);
            result.g = (1f - progress) * start.g + progress * (end.g);
            result.b = (1f - progress) * start.b + progress * (end.b);
            result.a = (1f - progress) * start.a + progress * (end.a);
        }
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

    public void addCustomColor(String name, float r, float g, float b, float a) {
        if (OLD_SYSTEM) {
            Color color = new Color(r, g, b, a);
            if (mCustomColors == null) {
                mCustomColors = new HashMap<>();
            }
            mCustomColors.put(name, color);
        }
        setCustomAttribute(name, TypedValues.Custom.TYPE_COLOR, CustomVariable.rgbaTocColor(r, g, b, a));
    }

    public Color getCustomColor(String name) {
        if (OLD_SYSTEM) {
            if (mCustomColors == null) {
                return null;
            }
            return mCustomColors.get(name);
        }
        if (mCustom.containsKey(name)) {
            int color = mCustom.get(name).getColorValue();
            float fr = ((color >> 16) & 0xFF) / 255f;
            float fg = ((color >> 8) & 0xFF) / 255f;
            float fb = ((color) & 0xFF) / 255f;
            float fa = ((color >> 24) & 0xFF) / 255f;
            return new Color(fr, fg, fb, fa);
        }
        return new Color(1, 0.5f, 0.5f, 1);
    }

    public void addCustomFloat(String name, float value) {
        if (OLD_SYSTEM) {
            if (mCustomFloats == null) {
                mCustomFloats = new HashMap<>();
            }
            mCustomFloats.put(name, value);
        }
        setCustomAttribute(name, TypedValues.Custom.TYPE_FLOAT, value);
    }

    public float getCustomFloat(String name) {
        if (OLD_SYSTEM) {
            if (mCustomFloats == null) {
                return 0f;
            }
            return mCustomFloats.get(name);
        }
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
}
