/*
 * Copyright (C) 2019 The Android Open Source Project
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
package androidx.constraintLayout.desktop.ui.adapters;

public class MotionLayoutAttrs {

  public static String[] KeyAttributesAll =
    {
      "framePosition",
      "motionTarget",
      "transitionEasing",
      "curveFit",
      "motionProgress",
      "visibility",
      "alpha",
      "elevation",
      "rotation",
      "rotationX",
      "rotationY",
      "transitionPathRotate",
      "scaleX",
      "scaleY",
      "translationX",
      "translationY",
      "translationZ",
    };

  public static String[] KeyAttributesKey =
    {
      "motionProgress",
      "visibility",
      "alpha",
      "elevation",
      "rotation",
      "rotationX",
      "rotationY",
      "transitionPathRotate",
      "scaleX",
      "scaleY",
      "translationX",
      "translationY",
      "translationZ",
    };

  public static final String ATTR_MOTIONPROGRESS = "motionProgress";
  public static final String ATTR_VISIBILITY = "visibility";
  public static final String ATTR_ALPHA = "alpha";
  public static final String ATTR_ELEVATION = "elevation";
  public static final String ATTR_ROTATION = "rotation";
  public static final String ATTR_ROTATION_X = "rotationX";
  public static final String ATTR_ROTATION_Y = "rotationY";
  public static final String ATTR_TRANSITION_PATH_ROTATE = "transitionPathRotate";
  public static final String ATTR_SCALE_X = "scaleX";
  public static final String ATTR_SCALE_Y = "scaleY";
  public static final String ATTR_TRANSLATION_X = "translationX";
  public static final String ATTR_TRANSLATION_Y = "translationY";
  public static final String ATTR_TRANSLATION_Z = "translationZ";
  public static final String ATTR_TRANSFORM_PIVOT_X = "transformPivotX";
  public static final String ATTR_TRANSFORM_PIVOT_Y = "transformPivotY";
  public static final String ATTR_TRANSFORM_PIVOT_TARGET = "transformPivotTarget";
  public static final String ATTR_PROGRESS = "transformPivotTarget";
  public static final String ATTR_WAVE_SHAPE= "waveShape";
  public static final String ATTR_WAVE_PERIOD= "wavePeriod";
  public static final String ATTR_WAVE_OFFSET= "waveOffset";
  public static final String ATTR_WAVE_PHASE= "wavePhase";
  public static final String ATTR_CUSTOM_WAVE_SHAPE= "waveShape";

  public static final String PERCENT_X = "percentX";
  public static final String PERCENT_Y = "percentY";
  public static final String PERCENT_WIDTH = "percentWidth";
  public static final String PERCENT_HEIGHT = "percentHeight";
  public static final String SIZE_PERCENT = "sizePercent";
  public static final String TRANSITION_EASING = "transitionEasing";
  public static final String PATH_MOTION_ARC = "pathMotionArc";
  public static final String CURVE_FIT = "curveFit";
  public static final String KEY_POSITION_TYPE = "keyPositionType";
  public static final String FRAME_POSITION = "framePosition";
  public static final String MOTION_TARGET = "motionTarget";


  public static String[] KeyPositionAll = {
    "keyPositionType",
    "percentX",
    "percentY",
    "percentWidth",
    "percentHeight",
    "framePosition",
    "motionTarget",
    "transitionEasing",
    "pathMotionArc",
    "curveFit",
    "drawPath",
    "sizePercent"
  };
  public static String[] KeyPositionKey = {
  };

  public static String[] KeyCycleAll = {
    "motionTarget",
    "curveFit",
    "framePosition",
    "transitionEasing",
    "motionProgress",
    "waveShape",
    "wavePeriod",
    "waveOffset",
    "transitionPathRotate",
    "alpha",
    "elevation",
    "rotation",
    "rotationX",
    "rotationY",
    "scaleX",
    "scaleY",
    "translationX",
    "translationY",
    "translationZ",
  };

  public static String[] KeyCycleKey = {
    "motionProgress",
    "transitionPathRotate",
    "alpha",
    "elevation",
    "rotation",
    "rotationX",
    "rotationY",
    "scaleX",
    "scaleY",
    "translationX",
    "translationY",
    "translationZ",
  };

  public static String[] KeyTimeCycleAll = {
    "framePosition",
    "motionTarget",
    "transitionEasing",
    "curveFit",
    "waveShape",
    "wavePeriod",
    "motionProgress",
    "waveOffset",
    "waveDecay",
    "alpha",
    "elevation",
    "rotation",
    "rotationX",
    "rotationY",
    "transitionPathRotate",
    "scaleX",
    "scaleY",
    "translationX",
    "translationY",
    "translationZ"
  };

  public static String[] KeyTimeCycleKey = {
    "motionProgress",
    "alpha",
    "elevation",
    "rotation",
    "rotationX",
    "rotationY",
    "transitionPathRotate",
    "scaleX",
    "scaleY",
    "translationX",
    "translationY",
    "translationZ"
  };

  public static String[] KeyTriggerAll = {
    "framePosition",
    "motionTarget",
    "triggerReceiver",
    "onNegativeCross",
    "onPositiveCross",
    "onCross",
    "triggerSlack",
    "triggerId",
    "motion_postLayoutCollision",
    "motion_triggerOnCollision",
  };

  public static String[] KeyTriggerKey = {};
}
