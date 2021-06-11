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

import androidx.constraintlayout.core.ArrayRow;
import androidx.constraintlayout.core.widgets.ConstraintWidget;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to encapsulate layout of a widget
 */
public class WidgetFrame {
    public ConstraintWidget widget = null;
    public int left = 0;
    public int top = 0;
    public int right = 0;
    public int bottom = 0;

    // transforms

    public float rotationX = Float.NaN;
    public float rotationY = Float.NaN;
    public float rotationZ = Float.NaN;

    public float translationX = Float.NaN;
    public float translationY = Float.NaN;

    public float scaleX = Float.NaN;
    public float scaleY = Float.NaN;

    public float alpha = Float.NaN;

    public HashMap<String, Color> mCustomColors = null;
    public HashMap<String, Float> mCustomFloats = null;

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
    }


    public int width() { return right - left; }
    public int height() { return bottom - top; }

    public WidgetFrame(ConstraintWidget widget) {
        this.widget = widget;
    }

    public WidgetFrame(WidgetFrame frame) {
        widget = frame.widget;
        left = frame.left;
        top = frame.top;
        right = frame.right;
        bottom = frame.bottom;
        rotationX = frame.rotationX;
        rotationY = frame.rotationY;
        rotationZ = frame.rotationZ;
        translationX = frame.translationX;
        translationY = frame.translationY;
        scaleX = frame.scaleX;
        scaleY = frame.scaleY;
        alpha = frame.alpha;
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
                && Float.isNaN(scaleX)
                && Float.isNaN(scaleY)
                && Float.isNaN(alpha);
    }

    public static void interpolate(WidgetFrame frame, WidgetFrame start, WidgetFrame end, float progress) {
        frame.widget = start.widget;
        frame.left = (int) (start.left + progress*(end.left - start.left));
        frame.top = (int) (start.top + progress*(end.top - start.top));
        frame.right = (int) (start.right + progress*(end.right - start.right));
        frame.bottom = (int) (start.bottom + progress*(end.bottom - start.bottom));

        frame.rotationX = interpolate(start.rotationX, end.rotationX, 0f, progress);
        frame.rotationY = interpolate(start.rotationY, end.rotationY, 0f, progress);
        frame.rotationZ = interpolate(start.rotationZ, end.rotationZ, 0f, progress);

        frame.scaleX = interpolate(start.scaleX, end.scaleX, 0f, progress);
        frame.scaleY = interpolate(start.scaleY, end.scaleY, 0f, progress);

        frame.translationX = interpolate(start.translationX, end.translationX, 0f, progress);
        frame.translationY = interpolate(start.translationY, end.translationY, 0f, progress);

        frame.alpha = interpolate(start.alpha, end.alpha, 0f, progress);
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
        return left + (right - left)/2f;
    }

    public float centerY() {
        return top + (bottom - top)/2f;
    }

    public WidgetFrame update() {
        if (widget != null) {
            left = widget.getLeft();
            top = widget.getTop();
            right = widget.getRight();
            bottom = widget.getBottom();
        }
        return this;
    }

    public void addCustomColor(String name, float r, float g, float b, float a) {
        Color color = new Color(r, g, b, a);
        if (mCustomColors == null) {
            mCustomColors = new HashMap<>();
        }
        mCustomColors.put(name, color);
    }

    public Color getCustomColor(String name) {
        if (mCustomColors == null) {
            return null;
        }
        return mCustomColors.get(name);
    }

    public void addCustomFloat(String name, float value) {
        if (mCustomFloats == null) {
            mCustomFloats = new HashMap<>();
        }
        mCustomFloats.put(name, value);
    }

    public float getCustomFloat(String name) {
        if (mCustomFloats == null) {
            return 0f;
        }
        return mCustomFloats.get(name);
    }
}
