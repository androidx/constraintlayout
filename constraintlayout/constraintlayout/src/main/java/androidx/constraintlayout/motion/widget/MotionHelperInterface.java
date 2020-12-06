/*
 * Copyright (C) 2020 The Android Open Source Project
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

package androidx.constraintlayout.motion.widget;

import android.graphics.Canvas;
import android.view.View;

import java.util.HashMap;

public interface MotionHelperInterface extends  Animatable, MotionLayout.TransitionListener {
    boolean isUsedOnShow();

    boolean isUseOnHide();

    boolean isDecorator();

    void onPreDraw(Canvas canvas);

    void onPostDraw(Canvas canvas);

    /**
     * Called after motionController is populated with start and end and keyframes.
     *
     * @param motionLayout
     * @param controllerMap
     */
    void onPreSetup(MotionLayout motionLayout, HashMap<View, MotionController> controllerMap);

    /**
     * This is called after motionLayout read motionScene and assembles all constraintSets
     * @param motionLayout
     */
    void onFinishedMotionScene(MotionLayout motionLayout);
}
