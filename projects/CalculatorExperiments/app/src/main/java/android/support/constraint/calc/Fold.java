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

package android.support.constraint.calc;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.constraintlayout.motion.widget.Debug;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.window.WindowManager;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.window.DeviceState;
import androidx.window.DisplayFeature;

import java.util.List;
import java.util.concurrent.Executor;

import androidx.core.util.Consumer;

import static androidx.window.DisplayFeature.TYPE_FOLD;


public class Fold {
    private static final String TAG = "Fold";
    private WindowManager mWindowManager;
    private MotionLayout mMotionLayout;

    public boolean isPostureHalfOpen() {
        int deviceState = mWindowManager.getDeviceState().getPosture();

        Log.v(TAG, Debug.getLoc() + " fold =  " + deviceState);

        return DeviceState.POSTURE_HALF_OPENED == deviceState;
    }

    public Fold(MotionLayout motionLayout) {
        mWindowManager = new WindowManager(motionLayout.getContext(), null);
        mMotionLayout = motionLayout;

        Handler handler = new Handler(Looper.getMainLooper());
        Executor executor = new Executor() {
            @Override
            public void execute(Runnable runnable) {
                handler.post(runnable);
            }
        };

        Consumer<DeviceState> callback = new Consumer<DeviceState>() {
            @Override
            public void accept(DeviceState deviceState) {
                switch (deviceState.getPosture()) {
                    case DeviceState.POSTURE_UNKNOWN:
                        Log.v(TAG, Debug.getLoc() + " POSTURE_UNKNOWN");
                        break;
                    case DeviceState.POSTURE_CLOSED:
                        Log.v(TAG, Debug.getLoc() + " POSTURE_CLOSED");
                        break;
                    case DeviceState.POSTURE_HALF_OPENED:
                        Log.v(TAG, Debug.getLoc() + " POSTURE_HALF_OPENED");
                        int fold = getFoldPosition(motionLayout, mWindowManager.getWindowLayoutInfo().getDisplayFeatures());
                        Log.v(TAG, Debug.getLoc() + " POSTURE_HALF_OPENED " + fold);
                        if (motionLayout.getCurrentState() == R.id.mode2d) {
                            motionLayout.transitionToState(R.id.mode2d_fold);
                        } else if (motionLayout.getCurrentState() == R.id.mode3d) {
                            motionLayout.transitionToState(R.id.mode3d_fold);
                        }

                        ConstraintLayout.getSharedValues().fireNewValue(R.id.fold, fold);
                        break;
                    case DeviceState.POSTURE_OPENED:
                        Log.v(TAG, Debug.getLoc() + " POSTURE_OPENED");
                        ConstraintLayout.getSharedValues().fireNewValue(R.id.fold, 0);

                        if (motionLayout.getCurrentState() == R.id.mode2d_fold) {
                            motionLayout.transitionToState(R.id.mode2d);
                        } else if (motionLayout.getCurrentState() == R.id.mode3d_fold) {
                            motionLayout.transitionToState(R.id.mode3d);
                        }
                        break;
                    case DeviceState.POSTURE_FLIPPED:
                        Log.v(TAG, Debug.getLoc() + " POSTURE_FLIPPED");
                        break;

                }
            }
        };
        mWindowManager.registerDeviceStateChangeCallback(executor, callback);
    }

    static int getFoldPosition(View view, List<DisplayFeature> displayFeatureList) {
        for (DisplayFeature feature : displayFeatureList) {
            if (feature.getType() == TYPE_FOLD) {
                Log.v(TAG, Debug.getLoc() + " TYPE_FOLD " + feature.getType() + " == " + TYPE_FOLD);

                Rect splitRect = getFeatureBoundsInWindow(feature, view, true);
                if (splitRect != null) {
                    return view.getHeight() - splitRect.top;
                }
            }
        }
        return 0;
    }

    public static boolean isFoldable(Context context) {
        Log.v(TAG, Debug.getLoc() + Build.MANUFACTURER);
        int orientation = context.getResources().getConfiguration().orientation;

        switch (orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                Log.v(TAG, Debug.getLoc() + "Configuration.ORIENTATION_LANDSCAPE");
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                Log.v(TAG, Debug.getLoc() + "Configuration.ORIENTATION_PORTRAIT");
                break;
            default:
                Log.v(TAG, Debug.getLoc() + "Configuration.??? = " + orientation);
        }
        return "samsung".equals(Build.MANUFACTURER);
    }

    /**
     * Get the bounds of the display feature translated to the View's coordinate space and current
     * position in the window. This will also include view padding in the calculations.
     */
    static Rect getFeatureBoundsInWindow(
            DisplayFeature displayFeature,
            View view,
            Boolean includePadding
    ) {
        // The the location of the view in window to be in the same coordinate space as the feature.
        int[] viewLocationInWindow = new int[2];
        view.getLocationInWindow(viewLocationInWindow);

        // Intersect the feature rectangle in window with view rectangle to clip the bounds.
        Rect viewRect = new Rect(
                viewLocationInWindow[0], viewLocationInWindow[1],
                viewLocationInWindow[0] + view.getWidth(), viewLocationInWindow[1] + view.getHeight()
        );

        // Include padding if needed
        if (includePadding) {
            viewRect.left += view.getPaddingLeft();
            viewRect.top += view.getPaddingTop();
            viewRect.right -= view.getPaddingRight();
            viewRect.bottom -= view.getPaddingBottom();
        }

        Rect featureRectInView = new Rect(displayFeature.getBounds());
        boolean intersects = featureRectInView.intersect(viewRect);

        //Checks to see if the display feature overlaps with our view at all
        if ((featureRectInView.width() == 0 && featureRectInView.height() == 0) ||
                !intersects
        ) {
            return null;
        }

        // Offset the feature coordinates to view coordinate space start point
        featureRectInView.offset(-viewLocationInWindow[0], -viewLocationInWindow[1]);

        return featureRectInView;
    }

}
