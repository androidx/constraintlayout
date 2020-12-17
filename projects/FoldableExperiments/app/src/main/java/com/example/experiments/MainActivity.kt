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

package com.example.experiments

import android.app.Activity
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.TransitionManager
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.core.util.Consumer
import androidx.window.DeviceState
import androidx.window.DisplayFeature
import androidx.window.DisplayFeature.TYPE_FOLD
import androidx.window.WindowManager
import java.util.concurrent.Executor

class MainActivity : Activity() {
    private lateinit var motionLayout: MotionLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val windowManager = WindowManager(this, null)

        val handler = Handler(Looper.getMainLooper())
        val mainThreadExecutor = Executor { r -> handler.post(r) }
        val callback = Consumer<DeviceState> { deviceState ->
            when (deviceState.posture) {
                DeviceState.POSTURE_UNKNOWN -> {
                    // Unknown state of the device. May mean that either this device doesn't support different postures or doesn't provide any information about its state at all.
                }
                DeviceState.POSTURE_CLOSED -> {
                    // The foldable device is closed, its primary screen area is not accessible.
                }
                DeviceState.POSTURE_HALF_OPENED -> {
                    // The foldable device's hinge is in an intermediate position between opened and closed state.
                    var fold = foldPosition(motionLayout, windowManager.windowLayoutInfo.displayFeatures)
                    ConstraintLayout.getSharedValues().fireNewValue(R.id.fold, fold)
                }
                DeviceState.POSTURE_OPENED -> {
                    // The foldable device is completely open, the screen space that is presented to the user is flat.
                    ConstraintLayout.getSharedValues().fireNewValue(R.id.fold, 0);
                }
                DeviceState.POSTURE_FLIPPED -> {
                    // The foldable device is flipped with the flexible screen parts or physical screens facing opposite directions.
                }
                else -> {
                    // etc
                }
            }
        }
        windowManager.registerDeviceStateChangeCallback(
            mainThreadExecutor,
            callback
        )

        setContentView(R.layout.activity_main2)
        motionLayout = findViewById<MotionLayout>(R.id.root)
    }

    /**
     * Returns the position of the fold relative to the view
     */
    fun foldPosition(view: View, displayFeatures: List<DisplayFeature>) : Int {
        for (feature in displayFeatures) {
            if (feature.type != TYPE_FOLD) {
                continue
            }
            val splitRect = getFeatureBoundsInWindow(feature, view)
            splitRect?.let {
                return view.height.minus(splitRect.top)
            }
        }
        return 0
    }

    /**
     * Get the bounds of the display feature translated to the View's coordinate space and current
     * position in the window. This will also include view padding in the calculations.
     */
    fun getFeatureBoundsInWindow(
        displayFeature: DisplayFeature,
        view: View,
        includePadding: Boolean = true
    ): Rect? {
        // The the location of the view in window to be in the same coordinate space as the feature.
        val viewLocationInWindow = IntArray(2)
        view.getLocationInWindow(viewLocationInWindow)

        // Intersect the feature rectangle in window with view rectangle to clip the bounds.
        val viewRect = Rect(
            viewLocationInWindow[0], viewLocationInWindow[1],
            viewLocationInWindow[0] + view.width, viewLocationInWindow[1] + view.height
        )

        // Include padding if needed
        if (includePadding) {
            viewRect.left += view.paddingLeft
            viewRect.top += view.paddingTop
            viewRect.right -= view.paddingRight
            viewRect.bottom -= view.paddingBottom
        }

        val featureRectInView = Rect(displayFeature.bounds)
        val intersects = featureRectInView.intersect(viewRect)

        //Checks to see if the display feature overlaps with our view at all
        if ((featureRectInView.width() == 0 && featureRectInView.height() == 0) ||
            !intersects
        ) {
            return null
        }

        // Offset the feature coordinates to view coordinate space start point
        featureRectInView.offset(-viewLocationInWindow[0], -viewLocationInWindow[1])

        return featureRectInView
    }

}