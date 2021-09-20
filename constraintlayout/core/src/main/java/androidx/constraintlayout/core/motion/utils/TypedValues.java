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
package androidx.constraintlayout.core.motion.utils;

/**
 * Provides an interface to values used in KeyFrames and in
 * Starting and Ending Widgets
 */
public interface TypedValues {
    public static final String S_CUSTOM = "CUSTOM";
    public static final int BOOLEAN_MASK = 1;
    public static final int INT_MASK = 2;
    public static final int FLOAT_MASK = 4;
    public static final int STRING_MASK = 8;

    /**
     * Used to set integer values
     *
     * @param id
     * @param value
     * @return true if it accepted the value
     */
    boolean setValue(int id, int value);

    /**
     * Used to set float values
     *
     * @param id
     * @param value
     * @return true if it accepted the value
     */
    boolean setValue(int id, float value);

    /**
     * Used to set String values
     *
     * @param id
     * @param value
     * @return true if it accepted the value
     */
    boolean setValue(int id, String value);

    /**
     * Used to set boolean values
     *
     * @param id
     * @param value
     * @return true if it accepted the value
     */
    boolean setValue(int id, boolean value);

    int getId(String name);

    public static final int TYPE_FRAME_POSITION = 100;
    public static final int TYPE_TARGET = 101;

    interface Attributes {
        public static final String NAME = "KeyAttributes";

        public static final int TYPE_CURVE_FIT = 301;
        public static final int TYPE_VISIBILITY = 302;
        public static final int TYPE_ALPHA = 303;
        public static final int TYPE_TRANSLATION_X = 304;
        public static final int TYPE_TRANSLATION_Y = 305;
        public static final int TYPE_TRANSLATION_Z = 306;
        public static final int TYPE_ELEVATION = 307;
        public static final int TYPE_ROTATION_X = 308;
        public static final int TYPE_ROTATION_Y = 309;
        public static final int TYPE_ROTATION_Z = 310;
        public static final int TYPE_SCALE_X = 311;
        public static final int TYPE_SCALE_Y = 312;
        public static final int TYPE_PIVOT_X = 313;
        public static final int TYPE_PIVOT_Y = 314;
        public static final int TYPE_PROGRESS = 315;
        public static final int TYPE_PATH_ROTATE = 316;
        public static final int TYPE_EASING = 317;
        public static final int TYPE_PIVOT_TARGET = 318;

        public static final String S_CURVE_FIT = "curveFit";
        public static final String S_VISIBILITY = "visibility";
        public static final String S_ALPHA = "alpha";

        public static final String S_TRANSLATION_X = "translationX";
        public static final String S_TRANSLATION_Y = "translationY";
        public static final String S_TRANSLATION_Z = "translationZ";
        public static final String S_ELEVATION = "elevation";
        public static final String S_ROTATION_X = "rotationX";
        public static final String S_ROTATION_Y = "rotationY";
        public static final String S_ROTATION_Z = "rotationZ";
        public static final String S_SCALE_X = "scaleX";
        public static final String S_SCALE_Y = "scaleY";
        public static final String S_PIVOT_X = "pivotX";
        public static final String S_PIVOT_Y = "pivotY";
        public static final String S_PROGRESS = "progress";
        public static final String S_PATH_ROTATE = "pathRotate";
        public static final String S_EASING = "easing";
        public static final String S_CUSTOM = "CUSTOM";
        public static final String S_FRAME = "frame";
        public static final String S_TARGET = "target";
        public static final String S_PIVOT_TARGET = "pivotTarget";

        public static final String[] KEY_WORDS = {
                S_CURVE_FIT,
                S_VISIBILITY,
                S_ALPHA,
                S_TRANSLATION_X,
                S_TRANSLATION_Y,
                S_TRANSLATION_Z,
                S_ELEVATION,
                S_ROTATION_X,
                S_ROTATION_Y,
                S_ROTATION_Z,
                S_SCALE_X,
                S_SCALE_Y,
                S_PIVOT_X,
                S_PIVOT_Y,
                S_PROGRESS,
                S_PATH_ROTATE,
                S_EASING,
                S_CUSTOM,
                S_FRAME,
                S_TARGET,
                S_PIVOT_TARGET,
        };

        /**
         * Method to go from String names of values to id of the values
         * IDs are use for efficiency
         *
         * @param name the name of the value
         * @return the id of the vlalue or -1 if no value exist
         */
        public static int getId(String name) {
            switch (name) {
                case S_CURVE_FIT:
                    return TYPE_CURVE_FIT;
                case S_VISIBILITY:
                    return TYPE_VISIBILITY;
                case S_ALPHA:
                    return TYPE_ALPHA;
                case S_TRANSLATION_X:
                    return TYPE_TRANSLATION_X;
                case S_TRANSLATION_Y:
                    return TYPE_TRANSLATION_Y;
                case S_TRANSLATION_Z:
                    return TYPE_TRANSLATION_Z;
                case S_ELEVATION:
                    return TYPE_ELEVATION;
                case S_ROTATION_X:
                    return TYPE_ROTATION_X;
                case S_ROTATION_Y:
                    return TYPE_ROTATION_Y;
                case S_ROTATION_Z:
                    return TYPE_ROTATION_Z;
                case S_SCALE_X:
                    return TYPE_SCALE_X;
                case S_SCALE_Y:
                    return TYPE_SCALE_Y;
                case S_PIVOT_X:
                    return TYPE_PIVOT_X;
                case S_PIVOT_Y:
                    return TYPE_PIVOT_Y;
                case S_PROGRESS:
                    return TYPE_PROGRESS;
                case S_PATH_ROTATE:
                    return TYPE_PATH_ROTATE;
                case S_EASING:
                    return TYPE_EASING;
                case S_FRAME:
                    return TYPE_FRAME_POSITION;
                case S_TARGET:
                    return TYPE_TARGET;
                case S_PIVOT_TARGET:
                    return TYPE_PIVOT_TARGET;
            }
            return -1;
        }

        static int getType(int name) {
            switch (name) {
                case TYPE_CURVE_FIT:
                case TYPE_VISIBILITY:
                case TYPE_FRAME_POSITION:
                    return INT_MASK;
                case TYPE_ALPHA:
                case TYPE_TRANSLATION_X:
                case TYPE_TRANSLATION_Y:
                case TYPE_TRANSLATION_Z:
                case TYPE_ELEVATION:
                case TYPE_ROTATION_X:
                case TYPE_ROTATION_Y:
                case TYPE_ROTATION_Z:
                case TYPE_SCALE_X:
                case TYPE_SCALE_Y:
                case TYPE_PIVOT_X:
                case TYPE_PIVOT_Y:
                case TYPE_PROGRESS:
                case TYPE_PATH_ROTATE:
                    return FLOAT_MASK;
                case TYPE_EASING:
                case TYPE_TARGET:
                case TYPE_PIVOT_TARGET:
                    return STRING_MASK;
            }
            return -1;
        }
    }

    interface Cycle {
        public static final String NAME = "KeyCycle";
        public static final int TYPE_CURVE_FIT = 401;
        public static final int TYPE_VISIBILITY = 402;
        public static final int TYPE_ALPHA = 403;
        public static final int TYPE_TRANSLATION_X = Attributes.TYPE_TRANSLATION_X;
        public static final int TYPE_TRANSLATION_Y = Attributes.TYPE_TRANSLATION_Y;
        public static final int TYPE_TRANSLATION_Z = Attributes.TYPE_TRANSLATION_Z;
        public static final int TYPE_ELEVATION = Attributes.TYPE_ELEVATION;

        public static final int TYPE_ROTATION_X = Attributes.TYPE_ROTATION_X;
        public static final int TYPE_ROTATION_Y = Attributes.TYPE_ROTATION_Y;
        public static final int TYPE_ROTATION_Z = Attributes.TYPE_ROTATION_Z;
        public static final int TYPE_SCALE_X = Attributes.TYPE_SCALE_X;
        public static final int TYPE_SCALE_Y = Attributes.TYPE_SCALE_Y;
        public static final int TYPE_PIVOT_X = Attributes.TYPE_PIVOT_X;
        public static final int TYPE_PIVOT_Y = Attributes.TYPE_PIVOT_Y;
        public static final int TYPE_PROGRESS = Attributes.TYPE_PROGRESS;
        public static final int TYPE_PATH_ROTATE = 416;
        public static final int TYPE_EASING = 420;
        public static final int TYPE_WAVE_SHAPE = 421;
        public static final int TYPE_CUSTOM_WAVE_SHAPE = 422;
        public static final int TYPE_WAVE_PERIOD = 423;
        public static final int TYPE_WAVE_OFFSET = 424;
        public static final int TYPE_WAVE_PHASE = 425;

        public static final String S_CURVE_FIT = "curveFit";
        public static final String S_VISIBILITY = "visibility";
        public static final String S_ALPHA = Attributes.S_ALPHA;
        public static final String S_TRANSLATION_X = Attributes.S_TRANSLATION_X;
        public static final String S_TRANSLATION_Y = Attributes.S_TRANSLATION_Y;
        public static final String S_TRANSLATION_Z = Attributes.S_TRANSLATION_Z;
        public static final String S_ELEVATION = Attributes.S_ELEVATION;
        public static final String S_ROTATION_X = Attributes.S_ROTATION_X;
        public static final String S_ROTATION_Y = Attributes.S_ROTATION_Y;
        public static final String S_ROTATION_Z = Attributes.S_ROTATION_Z;
        public static final String S_SCALE_X = Attributes.S_SCALE_X;
        public static final String S_SCALE_Y = Attributes.S_SCALE_Y;
        public static final String S_PIVOT_X = Attributes.S_PIVOT_X;
        public static final String S_PIVOT_Y = Attributes.S_PIVOT_Y;
        public static final String S_PROGRESS = Attributes.S_PROGRESS;

        public static final String S_PATH_ROTATE = "pathRotate";
        public static final String S_EASING = "easing";
        public static final String S_WAVE_SHAPE = "waveShape";
        public static final String S_CUSTOM_WAVE_SHAPE = "customWave";
        public static final String S_WAVE_PERIOD = "period";
        public static final String S_WAVE_OFFSET = "offset";
        public static final String S_WAVE_PHASE = "phase";
        public static final String[] KEY_WORDS = {
                S_CURVE_FIT,
                S_VISIBILITY,
                S_ALPHA,
                S_TRANSLATION_X,
                S_TRANSLATION_Y,
                S_TRANSLATION_Z,
                S_ELEVATION,
                S_ROTATION_X,
                S_ROTATION_Y,
                S_ROTATION_Z,
                S_SCALE_X,
                S_SCALE_Y,
                S_PIVOT_X,
                S_PIVOT_Y,
                S_PROGRESS,

                S_PATH_ROTATE,
                S_EASING,
                S_WAVE_SHAPE,
                S_CUSTOM_WAVE_SHAPE,
                S_WAVE_PERIOD,
                S_WAVE_OFFSET,
                S_WAVE_PHASE,
        };

        /**
         * Method to go from String names of values to id of the values
         * IDs are use for efficiency
         *
         * @param name the name of the value
         * @return the id of the vlalue or -1 if no value exist
         */
        public static int getId(String name) {
            switch (name) {
                case S_CURVE_FIT:
                    return TYPE_CURVE_FIT;
                case S_VISIBILITY:
                    return TYPE_VISIBILITY;
                case S_ALPHA:
                    return TYPE_ALPHA;
                case S_TRANSLATION_X:
                    return TYPE_TRANSLATION_X;
                case S_TRANSLATION_Y:
                    return TYPE_TRANSLATION_Y;
                case S_TRANSLATION_Z:
                    return TYPE_TRANSLATION_Z;
                case S_ROTATION_X:
                    return TYPE_ROTATION_X;
                case S_ROTATION_Y:
                    return TYPE_ROTATION_Y;
                case S_ROTATION_Z:
                    return TYPE_ROTATION_Z;
                case S_SCALE_X:
                    return TYPE_SCALE_X;
                case S_SCALE_Y:
                    return TYPE_SCALE_Y;
                case S_PIVOT_X:
                    return TYPE_PIVOT_X;
                case S_PIVOT_Y:
                    return TYPE_PIVOT_Y;
                case S_PROGRESS:
                    return TYPE_PROGRESS;
                case S_PATH_ROTATE:
                    return TYPE_PATH_ROTATE;
                case S_EASING:
                    return TYPE_EASING;
            }
            return -1;
        }

        static int getType(int name) {
            switch (name) {
                case TYPE_CURVE_FIT:
                case TYPE_VISIBILITY:
                case TYPE_FRAME_POSITION:
                    return INT_MASK;
                case TYPE_ALPHA:
                case TYPE_TRANSLATION_X:
                case TYPE_TRANSLATION_Y:
                case TYPE_TRANSLATION_Z:
                case TYPE_ELEVATION:
                case TYPE_ROTATION_X:
                case TYPE_ROTATION_Y:
                case TYPE_ROTATION_Z:
                case TYPE_SCALE_X:
                case TYPE_SCALE_Y:
                case TYPE_PIVOT_X:
                case TYPE_PIVOT_Y:
                case TYPE_PROGRESS:
                case TYPE_PATH_ROTATE:
                case TYPE_WAVE_PERIOD:
                case TYPE_WAVE_OFFSET:
                case TYPE_WAVE_PHASE:
                    return FLOAT_MASK;
                case TYPE_EASING:
                case TYPE_TARGET:
                case TYPE_WAVE_SHAPE:
                    return STRING_MASK;
            }
            return -1;
        }
    }

    interface Trigger {
        public static final String NAME = "KeyTrigger";
        public static final String VIEW_TRANSITION_ON_CROSS = "viewTransitionOnCross";
        public static final String VIEW_TRANSITION_ON_POSITIVE_CROSS = "viewTransitionOnPositiveCross";
        public static final String VIEW_TRANSITION_ON_NEGATIVE_CROSS = "viewTransitionOnNegativeCross";
        public static final String POST_LAYOUT = "postLayout";
        public static final String TRIGGER_SLACK = "triggerSlack";
        public static final String TRIGGER_COLLISION_VIEW = "triggerCollisionView";
        public static final String TRIGGER_COLLISION_ID = "triggerCollisionId";
        public static final String TRIGGER_ID = "triggerID";
        public static final String POSITIVE_CROSS = "positiveCross";
        public static final String NEGATIVE_CROSS = "negativeCross";
        public static final String TRIGGER_RECEIVER = "triggerReceiver";
        public static final String CROSS = "CROSS";
        public static final String[] KEY_WORDS = {
                VIEW_TRANSITION_ON_CROSS,
                VIEW_TRANSITION_ON_POSITIVE_CROSS,
                VIEW_TRANSITION_ON_NEGATIVE_CROSS,
                POST_LAYOUT,
                TRIGGER_SLACK,
                TRIGGER_COLLISION_VIEW,
                TRIGGER_COLLISION_ID,
                TRIGGER_ID,
                POSITIVE_CROSS,
                NEGATIVE_CROSS,
                TRIGGER_RECEIVER,
                CROSS,
        };
        public static final int TYPE_VIEW_TRANSITION_ON_CROSS = 301;
        public static final int TYPE_VIEW_TRANSITION_ON_POSITIVE_CROSS = 302;
        public static final int TYPE_VIEW_TRANSITION_ON_NEGATIVE_CROSS = 303;
        public static final int TYPE_POST_LAYOUT = 304;
        public static final int TYPE_TRIGGER_SLACK = 305;
        public static final int TYPE_TRIGGER_COLLISION_VIEW = 306;
        public static final int TYPE_TRIGGER_COLLISION_ID = 307;
        public static final int TYPE_TRIGGER_ID = 308;
        public static final int TYPE_POSITIVE_CROSS = 309;
        public static final int TYPE_NEGATIVE_CROSS = 310;
        public static final int TYPE_TRIGGER_RECEIVER = 311;
        public static final int TYPE_CROSS = 312;

        /**
         * Method to go from String names of values to id of the values
         * IDs are use for efficiency
         *
         * @param name the name of the value
         * @return the id of the vlalue or -1 if no value exist
         */
        public static int getId(String name) {
            switch (name) {
                case VIEW_TRANSITION_ON_CROSS:
                    return TYPE_VIEW_TRANSITION_ON_CROSS;
                case VIEW_TRANSITION_ON_POSITIVE_CROSS:
                    return TYPE_VIEW_TRANSITION_ON_POSITIVE_CROSS;
                case VIEW_TRANSITION_ON_NEGATIVE_CROSS:
                    return TYPE_VIEW_TRANSITION_ON_NEGATIVE_CROSS;
                case POST_LAYOUT:
                    return TYPE_POST_LAYOUT;
                case TRIGGER_SLACK:
                    return TYPE_TRIGGER_SLACK;
                case TRIGGER_COLLISION_VIEW:
                    return TYPE_TRIGGER_COLLISION_VIEW;
                case TRIGGER_COLLISION_ID:
                    return TYPE_TRIGGER_COLLISION_ID;
                case TRIGGER_ID:
                    return TYPE_TRIGGER_ID;
                case POSITIVE_CROSS:
                    return TYPE_POSITIVE_CROSS;
                case NEGATIVE_CROSS:
                    return TYPE_NEGATIVE_CROSS;
                case TRIGGER_RECEIVER:
                    return TYPE_TRIGGER_RECEIVER;
                case CROSS:
                    return TYPE_CROSS;
            }
            return -1;
        }
    }

    interface Position {
        public static final String NAME = "KeyPosition";
        public static final String S_TRANSITION_EASING = "transitionEasing";
        public static final String S_DRAWPATH = "drawPath";
        public static final String S_PERCENT_WIDTH = "percentWidth";
        public static final String S_PERCENT_HEIGHT = "percentHeight";
        public static final String S_SIZE_PERCENT = "sizePercent";
        public static final String S_PERCENT_X = "percentX";
        public static final String S_PERCENT_Y = "percentY";

        public static final int TYPE_TRANSITION_EASING = 501;
        public static final int TYPE_DRAWPATH = 502;
        public static final int TYPE_PERCENT_WIDTH = 503;
        public static final int TYPE_PERCENT_HEIGHT = 504;
        public static final int TYPE_SIZE_PERCENT = 505;
        public static final int TYPE_PERCENT_X = 506;
        public static final int TYPE_PERCENT_Y = 507;
        public static final int TYPE_CURVE_FIT = 508;
        public static final int TYPE_PATH_MOTION_ARC = 509;
        public static final int TYPE_POSITION_TYPE = 510;
        public static final String[] KEY_WORDS = {
                S_TRANSITION_EASING,
                S_DRAWPATH,
                S_PERCENT_WIDTH,
                S_PERCENT_HEIGHT,
                S_SIZE_PERCENT,
                S_PERCENT_X,
                S_PERCENT_Y,
        };

        /**
         * Method to go from String names of values to id of the values
         * IDs are use for efficiency
         *
         * @param name the name of the value
         * @return the id of the vlalue or -1 if no value exist
         */
        public static int getId(String name) {
            switch (name) {
                case S_TRANSITION_EASING:
                    return TypedValues.Position.TYPE_TRANSITION_EASING;
                case S_DRAWPATH:
                    return TypedValues.Position.TYPE_DRAWPATH;
                case S_PERCENT_WIDTH:
                    return TypedValues.Position.TYPE_PERCENT_WIDTH;
                case S_PERCENT_HEIGHT:
                    return TypedValues.Position.TYPE_PERCENT_HEIGHT;
                case S_SIZE_PERCENT:
                    return TypedValues.Position.TYPE_SIZE_PERCENT;
                case S_PERCENT_X:
                    return TypedValues.Position.TYPE_PERCENT_X;
                case S_PERCENT_Y:
                    return TypedValues.Position.TYPE_PERCENT_Y;
            }
            return -1;
        }

        static int getType(int name) {
            switch (name) {
                case TYPE_CURVE_FIT:
                case TYPE_FRAME_POSITION:
                    return INT_MASK;
                case TYPE_PERCENT_WIDTH:
                case TYPE_PERCENT_HEIGHT:
                case TYPE_SIZE_PERCENT:
                case TYPE_PERCENT_X:
                case TYPE_PERCENT_Y:
                    return FLOAT_MASK;
                case TYPE_TRANSITION_EASING:
                case TYPE_TARGET:
                case TYPE_DRAWPATH:
                    return STRING_MASK;
            }
            return -1;
        }


    }

    interface Motion {
        public static final String NAME = "Motion";

        public static final String S_STAGGER = "Stagger";
        public static final String S_PATH_ROTATE = "PathRotate";
        public static final String S_QUANTIZE_MOTION_PHASE = "QuantizeMotionPhase";
        public static final String S_EASING = "TransitionEasing";
        public static final String S_QUANTIZE_INTERPOLATOR = "QuantizeInterpolator";
        public static final String S_ANIMATE_RELATIVE_TO = "AnimateRelativeTo";
        public static final String S_ANIMATE_CIRCLEANGLE_TO = "AnimateCircleAngleTo";
        public static final String S_PATHMOTION_ARC = "PathMotionArc";
        public static final String S_DRAW_PATH = "DrawPath";
        public static final String S_POLAR_RELATIVETO = "PolarRelativeTo";
        public static final String S_QUANTIZE_MOTIONSTEPS = "QuantizeMotionSteps";
        public static final String S_QUANTIZE_INTERPOLATOR_TYPE = "QuantizeInterpolatorType";
        public static final String S_QUANTIZE_INTERPOLATOR_ID = "QuantizeInterpolatorID";
        public static final String[] KEY_WORDS = {
                S_STAGGER,
                S_PATH_ROTATE,
                S_QUANTIZE_MOTION_PHASE,
                S_EASING,
                S_QUANTIZE_INTERPOLATOR,
                S_ANIMATE_RELATIVE_TO,
                S_ANIMATE_CIRCLEANGLE_TO,
                S_PATHMOTION_ARC,
                S_DRAW_PATH,
                S_POLAR_RELATIVETO,
                S_QUANTIZE_MOTIONSTEPS,
                S_QUANTIZE_INTERPOLATOR_TYPE,
                S_QUANTIZE_INTERPOLATOR_ID,
        };
        public static final int TYPE_STAGGER = 600;
        public static final int TYPE_PATH_ROTATE = 601;
        public static final int TYPE_QUANTIZE_MOTION_PHASE = 602;
        public static final int TYPE_EASING = 603;
        public static final int TYPE_QUANTIZE_INTERPOLATOR = 604;
        public static final int TYPE_ANIMATE_RELATIVE_TO = 605;
        public static final int TYPE_ANIMATE_CIRCLEANGLE_TO = 606;
        public static final int TYPE_PATHMOTION_ARC = 607;
        public static final int TYPE_DRAW_PATH = 608;
        public static final int TYPE_POLAR_RELATIVETO = 609;
        public static final int TYPE_QUANTIZE_MOTIONSTEPS = 610;
        public static final int TYPE_QUANTIZE_INTERPOLATOR_TYPE = 611;
        public static final int TYPE_QUANTIZE_INTERPOLATOR_ID = 612;

        /**
         * Method to go from String names of values to id of the values
         * IDs are use for efficiency
         *
         * @param name the name of the value
         * @return the id of the vlalue or -1 if no value exist
         */
        public static int getId(String name) {
            switch (name) {
                case S_STAGGER:
                    return TYPE_STAGGER;
                case S_PATH_ROTATE:
                    return TYPE_PATH_ROTATE;
                case S_QUANTIZE_MOTION_PHASE:
                    return TYPE_QUANTIZE_MOTION_PHASE;
                case S_EASING:
                    return TYPE_EASING;
                case S_QUANTIZE_INTERPOLATOR:
                    return TYPE_QUANTIZE_INTERPOLATOR;
                case S_ANIMATE_RELATIVE_TO:
                    return TYPE_ANIMATE_RELATIVE_TO;
                case S_ANIMATE_CIRCLEANGLE_TO:
                    return TYPE_ANIMATE_CIRCLEANGLE_TO;
                case S_PATHMOTION_ARC:
                    return TYPE_PATHMOTION_ARC;
                case S_DRAW_PATH:
                    return TYPE_DRAW_PATH;
                case S_POLAR_RELATIVETO:
                    return TYPE_POLAR_RELATIVETO;
                case S_QUANTIZE_MOTIONSTEPS:
                    return TYPE_QUANTIZE_MOTIONSTEPS;
                case S_QUANTIZE_INTERPOLATOR_TYPE:
                    return TYPE_QUANTIZE_INTERPOLATOR_TYPE;
                case S_QUANTIZE_INTERPOLATOR_ID:
                    return TYPE_QUANTIZE_INTERPOLATOR_ID;
            }
            return -1;
        }

    }

    interface Custom {
        public static final String NAME = "Custom";
        public static final String S_INT = "integer";
        public static final String S_FLOAT = "float";
        public static final String S_COLOR = "color";
        public static final String S_STRING = "string";
        public static final String S_BOOLEAN = "boolean";
        public static final String S_DIMENSION = "dimension";
        public static final String S_REFERENCE = "refrence";
        public static final String[] KEY_WORDS = {
                S_FLOAT,
                S_COLOR,
                S_STRING,
                S_BOOLEAN,
                S_DIMENSION,
                S_REFERENCE,
        };
        public static final int TYPE_INT = 900;
        public static final int TYPE_FLOAT = 901;
        public static final int TYPE_COLOR = 902;
        public static final int TYPE_STRING = 903;
        public static final int TYPE_BOOLEAN = 904;
        public static final int TYPE_DIMENSION = 905;
        public static final int TYPE_REFERENCE = 906;

        /**
         * Method to go from String names of values to id of the values
         * IDs are use for efficiency
         *
         * @param name the name of the value
         * @return the id of the vlalue or -1 if no value exist
         */
        public static int getId(String name) {
            switch (name) {
                case S_INT:
                    return TYPE_INT;
                case S_FLOAT:
                    return TYPE_FLOAT;
                case S_COLOR:
                    return TYPE_COLOR;
                case S_STRING:
                    return TYPE_STRING;
                case S_BOOLEAN:
                    return TYPE_BOOLEAN;
                case S_DIMENSION:
                    return TYPE_DIMENSION;
                case S_REFERENCE:
                    return TYPE_REFERENCE;
            }
            return -1;
        }
    }

    interface MotionScene {
        public static final String NAME = "MotionScene";
        public static final String S_DEFAULT_DURATION = "defaultDuration";
        public static final String S_LAYOUT_DURING_TRANSITION = "layoutDuringTransition";
        public static final int TYPE_DEFAULT_DURATION = 600;
        public static final int TYPE_LAYOUT_DURING_TRANSITION = 601;

        public static final String[] KEY_WORDS = {
                S_DEFAULT_DURATION,
                S_LAYOUT_DURING_TRANSITION,
        };

        public static int getType(int name) {
            switch (name) {
                case TYPE_DEFAULT_DURATION:
                    return INT_MASK;
                case TYPE_LAYOUT_DURING_TRANSITION:
                    return BOOLEAN_MASK;
            }
            return -1;
        }

        /**
         * Method to go from String names of values to id of the values
         * IDs are use for efficiency
         *
         * @param name the name of the value
         * @return the id of the vlalue or -1 if no value exist
         */
        public static int getId(String name) {
            switch (name) {
                case S_DEFAULT_DURATION:
                    return TYPE_DEFAULT_DURATION;
                case S_LAYOUT_DURING_TRANSITION:
                    return TYPE_LAYOUT_DURING_TRANSITION;
            }
            return -1;
        }
    }

    interface Transition {
        public static final String NAME = "Transitions";
        public static final String S_DURATION = "duration";
        public static final String S_FROM = "from";
        public static final String S_TO = "to";
        public static final String S_PATH_MOTION_ARC = "pathMotionArc";
        public static final String S_AUTO_TRANSITION = "autoTransition";
        public static final String S_INTERPOLATOR = "motionInterpolator";
        public static final String S_STAGGERED = "staggered";
        public static final String S_TRANSITION_FLAGS = "transitionFlags";

        public static final int TYPE_DURATION = 700;
        public static final int TYPE_FROM = 701;
        public static final int TYPE_TO = 702;
        public static final int TYPE_PATH_MOTION_ARC = Position.TYPE_PATH_MOTION_ARC;
        public static final int TYPE_AUTO_TRANSITION = 704;
        public static final int TYPE_INTERPOLATOR = 705;
        public static final int TYPE_STAGGERED = 706;
        public static final int TYPE_TRANSITION_FLAGS = 707;


        public static final String[] KEY_WORDS = {
                S_DURATION,
                S_FROM,
                S_TO,
                S_PATH_MOTION_ARC,
                S_AUTO_TRANSITION,
                S_INTERPOLATOR,
                S_STAGGERED,
                S_FROM,
                S_TRANSITION_FLAGS,
        };

        public static int getType(int name) {
            switch (name) {
                case TYPE_DURATION:
                case TYPE_PATH_MOTION_ARC:
                    return INT_MASK;
                case TYPE_FROM:
                case TYPE_TO:
                case TYPE_INTERPOLATOR:
                case TYPE_TRANSITION_FLAGS:
                    return STRING_MASK;

                case TYPE_STAGGERED:
                    return FLOAT_MASK;
            }
            return -1;
        }

        /**
         * Method to go from String names of values to id of the values
         * IDs are use for efficiency
         *
         * @param name the name of the value
         * @return the id of the vlalue or -1 if no value exist
         */
        public static int getId(String name) {
            switch (name) {
                case S_DURATION:
                    return TYPE_DURATION;
                case S_FROM:
                    return TYPE_FROM;
                case S_TO:
                    return TYPE_TO;
                case S_PATH_MOTION_ARC:
                    return TYPE_PATH_MOTION_ARC;
                case S_AUTO_TRANSITION:
                    return TYPE_AUTO_TRANSITION;
                case S_INTERPOLATOR:
                    return TYPE_INTERPOLATOR;
                case S_STAGGERED:
                    return TYPE_STAGGERED;
                case S_TRANSITION_FLAGS:
                    return TYPE_TRANSITION_FLAGS;
            }
            return -1;
        }
    }

    interface OnSwipe {
        public static final String DRAG_SCALE = "dragscale";
        public static final String DRAG_THRESHOLD = "dragthreshold";

        public static final String MAX_VELOCITY = "maxvelocity";
        public static final String MAX_ACCELERATION = "maxacceleration";
        public static final String SPRING_MASS = "springmass";
        public static final String SPRING_STIFFNESS = "springstiffness";
        public static final String SPRING_DAMPING = "springdamping";
        public static final String SPRINGS_TOP_THRESHOLD = "springstopthreshold";

        public static final String DRAG_DIRECTION = "dragdirection";
        public static final String TOUCH_ANCHOR_ID = "touchanchorid";
        public static final String TOUCH_ANCHOR_SIDE = "touchanchorside";
        public static final String ROTATION_CENTER_ID = "rotationcenterid";
        public static final String TOUCH_REGION_ID = "touchregionid";
        public static final String LIMIT_BOUNDS_TO = "limitboundsto";

        public static final String MOVE_WHEN_SCROLLAT_TOP = "movewhenscrollattop";
        public static final String ON_TOUCH_UP = "ontouchup";
        public static final String[] ON_TOUCH_UP_ENUM = {"autoComplete",
                "autoCompleteToStart",
                "autoCompleteToEnd",
                "stop",
                "decelerate",
                "decelerateAndComplete",
                "neverCompleteToStart",
                "neverCompleteToEnd"};


        public static final String SPRING_BOUNDARY = "springboundary";
        public static final String[] SPRING_BOUNDARY_ENUM = {"overshoot",
                "bounceStart",
                "bounceEnd",
                "bounceBoth"};

        public static final String AUTOCOMPLETE_MODE = "autocompletemode";
        public static final String[] AUTOCOMPLETE_MODE_ENUM = {
                "continuousVelocity",
                "spring"};

        public static final String NESTED_SCROLL_FLAGS = "nestedscrollflags";
        public static final String[] NESTED_SCROLL_FLAGS_ENUM = {"none",
                "disablePostScroll",
                "disableScroll",
                "supportScrollUp"};

    }

}
