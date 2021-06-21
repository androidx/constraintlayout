package androidx.constraintlayout.core.motion.utils;

/**
 * Provides an interface to values used in KeyFrames and in
 * Starting and Ending Widgets
 */
public interface TypedValues {
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

        public static final String S_CURVE_FIT = "curve_fit";
        public static final String S_VISIBILITY = "visibility";
        public static final String S_ALPHA = "ALPHA ";
        public static final String S_TRANSLATION_X = "translation_x";
        public static final String S_TRANSLATION_Y = "translation_y";
        public static final String S_TRANSLATION_Z = "translation_z";
        public static final String S_ELEVATION = "elevation ";

        public static final String S_ROTATION_X = "rotation_x";
        public static final String S_ROTATION_Y = "rotation_y";
        public static final String S_ROTATION_Z = "rotation_z";
        public static final String S_SCALE_X = "scale_x";
        public static final String S_SCALE_Y = "scale_y";
        public static final String S_PIVOT_X = "pivot_x";
        public static final String S_PIVOT_Y = "pivot_y";
        public static final String S_PROGRESS = "progress";
        public static final String S_PATH_ROTATE = "path_rotate";
        public static final String S_EASING = "easing";

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
            }
            return -1;
        }
    }

    interface Cycle {
        public static final int TYPE_CURVE_FIT = 401;
        public static final int TYPE_VISIBILITY = 402;
        public static final int TYPE_ALPHA = 403;
        public static final int TYPE_TRANSLATION_X = 404;
        public static final int TYPE_TRANSLATION_Y = 405;
        public static final int TYPE_TRANSLATION_Z = 406;
        public static final int TYPE_ELEVATION = 407;

        public static final int TYPE_ROTATION_X = 408;
        public static final int TYPE_ROTATION_Y = 409;
        public static final int TYPE_ROTATION_Z = 410;
        public static final int TYPE_SCALE_X = 411;
        public static final int TYPE_SCALE_Y = 412;
        public static final int TYPE_PIVOT_X = 413;
        public static final int TYPE_PIVOT_Y = 414;
        public static final int TYPE_PROGRESS = 415;
        public static final int TYPE_PATH_ROTATE = 416;
        public static final int TYPE_EASING = 420;
        public static final int TYPE_WAVE_SHAPE = 421;
        public static final int TYPE_CUSTOM_WAVE_SHAPE = 422;
        public static final int TYPE_WAVE_PERIOD = 423;
        public static final int TYPE_WAVE_OFFSET = 424;
        public static final int TYPE_WAVE_PHASE = 425;


        public static final String S_CURVE_FIT = "curve_fit";
        public static final String S_VISIBILITY = "visibility";
        public static final String S_ALPHA = "ALPHA ";
        public static final String S_TRANSLATION_X = "translation_x";
        public static final String S_TRANSLATION_Y = "translation_y";
        public static final String S_TRANSLATION_Z = "translation_z";
        public static final String S_ROTATION_X = "rotation_x";
        public static final String S_ROTATION_Y = "rotation_y";
        public static final String S_ROTATION_Z = "rotation_z";
        public static final String S_ELEVATION = "elevation";

        public static final String S_SCALE_X = "scale_x";
        public static final String S_SCALE_Y = "scale_y";
        public static final String S_PIVOT_X = "pivot_x";
        public static final String S_PIVOT_Y = "pivot_y";
        public static final String S_PROGRESS = "progress";
        public static final String S_PATH_ROTATE = "path_rotate";
        public static final String S_EASING = "easing";
        public static final String S_WAVE_SHAPE = "waveShape";
        public static final String S_CUSTOM_WAVE_SHAPE = "customWave";
        public static final String S_WAVE_PERIOD = "wavePeriod";
        public static final String S_WAVE_OFFSET = "waveOffset";
        public static final String S_WAVE_PHASE = "wavePhase";

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
    }

    interface Trigger {
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
    }

    interface Motion {
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

}
