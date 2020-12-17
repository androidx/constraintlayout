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
    private var guideline: Guideline? = null
    private var motionLayout: MotionLayout? = null

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
                    val displayFeatures = windowManager.windowLayoutInfo.displayFeatures
                    halfOpened(displayFeatures)
                }
                DeviceState.POSTURE_OPENED -> {
                    // The foldable device is completely open, the screen space that is presented to the user is flat.
//                    motionLayout?.transitionToStart()
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

        // the idea here is to have two states in motionlayout, open and fold.
        // when the foldable device is open, we transition to the state open, and we do the opposite on fold.
        // when going to fold, we also get the position of the fold, and set the guideline we use in this example at
        // the position of the fold.
        // TODO: move this to a helper object

        setContentView(R.layout.activity_main2)
        motionLayout = findViewById<MotionLayout>(R.id.root)
//        guideline = findViewById<Guideline>(R.id.fold)
    }

    fun halfOpened(displayFeatures: List<DisplayFeature>) {
        for (feature in displayFeatures) {
            if (feature.type != TYPE_FOLD) {
                continue
            }
            val splitRect = motionLayout?.let { getFeatureBoundsInWindow(feature, it) } ?: continue
//            var constraintSet = motionLayout?.getConstraintSet(R.id.start)
////            constraintSet?.setGuidelineEnd(guideline?.id!!, 0)
//            var constraintSetEnd = motionLayout?.getConstraintSet(R.id.end)
////            constraintSetEnd?.setGuidelineBegin(guideline?.id!!, splitRect.left)
//            motionLayout?.updateState(R.id.start, constraintSet)
//            motionLayout?.updateState(R.id.end, constraintSetEnd)
//            motionLayout?.transitionToEnd()
            var h : Int? = motionLayout?.height?.minus(splitRect.top)
            h?.let {
                ConstraintLayout.getSharedValues().fireNewValue(R.id.fold, h)
            }
        }
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