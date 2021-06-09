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

import androidx.constraintlayout.core.widgets.ConstraintWidget;

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

    public float rotationX = 0f;
    public float rotationY = 0f;
    public float rotationZ = 0f;

    public float translationX = 0f;
    public float translationY = 0f;

    public float scaleX = 1f;
    public float scaleY = 1f;

    public float alpha = 1f;

    public int width() { return right - left; }
    public int height() { return bottom - top; }

    public boolean isDefaultTransform() {
        return rotationX == 0f
                && rotationY == 0f
                && rotationZ == 0f
                && translationX == 0f
                && translationY == 0f
                && scaleX == 1f
                && scaleY == 1f
                && alpha == 1f;
    }

    public static WidgetFrame interpolate(WidgetFrame start, WidgetFrame end, float progress) {
        WidgetFrame frame = new WidgetFrame();
        frame.left = (int) (start.left + progress*(end.left - start.left));
        frame.top = (int) (start.top + progress*(end.top - start.top));
        frame.right = (int) (start.right + progress*(end.right - start.right));
        frame.bottom = (int) (start.bottom + progress*(end.bottom - start.bottom));
        frame.rotationX = (start.rotationX + progress*(end.rotationX - start.rotationX));
        frame.rotationY = (start.rotationY + progress*(end.rotationY - start.rotationY));
        frame.rotationZ = (start.rotationZ + progress*(end.rotationZ - start.rotationZ));
        frame.scaleX = (start.scaleX + progress*(end.scaleX - start.scaleX));
        frame.scaleY = (start.scaleY + progress*(end.scaleY - start.scaleY));
        frame.translationX = (start.translationX + progress*(end.translationX - start.translationX));
        frame.translationY = (start.translationY + progress*(end.translationY - start.translationY));
        frame.alpha = (start.alpha + progress*(end.alpha - start.alpha));
        return frame;
    }

    public float centerX() {
        return left + (right - left)/2f;
    }

    public float centerY() {
        return top + (bottom - top)/2f;
    }
}
