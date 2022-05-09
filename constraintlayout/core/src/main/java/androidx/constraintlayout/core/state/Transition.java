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

import androidx.constraintlayout.core.motion.Motion;
import androidx.constraintlayout.core.motion.MotionWidget;
import androidx.constraintlayout.core.motion.key.MotionKeyAttributes;
import androidx.constraintlayout.core.motion.key.MotionKeyCycle;
import androidx.constraintlayout.core.motion.key.MotionKeyPosition;
import androidx.constraintlayout.core.motion.utils.Easing;
import androidx.constraintlayout.core.motion.utils.KeyCache;
import androidx.constraintlayout.core.motion.utils.SpringStopEngine;
import androidx.constraintlayout.core.motion.utils.StopEngine;
import androidx.constraintlayout.core.motion.utils.StopLogicEngine;
import androidx.constraintlayout.core.motion.utils.TypedBundle;
import androidx.constraintlayout.core.motion.utils.TypedValues;
import androidx.constraintlayout.core.motion.utils.Utils;
import androidx.constraintlayout.core.widgets.ConstraintWidget;
import androidx.constraintlayout.core.widgets.ConstraintWidgetContainer;

import java.util.ArrayList;
import java.util.HashMap;

public class Transition implements TypedValues {
    private static final boolean DEBUG = false;
    public static final int START = 0;
    public static final int END = 1;
    public static final int INTERPOLATED = 2;
    static final int EASE_IN_OUT = 0;
    static final int EASE_IN = 1;
    static final int EASE_OUT = 2;
    static final int LINEAR = 3;
    static final int BOUNCE = 4;
    static final int OVERSHOOT = 5;
    static final int ANTICIPATE = 6;
    private static final int SPLINE_STRING = -1;
    private static final int INTERPOLATOR_REFERENCE_ID = -2;
    HashMap<Integer, HashMap<String, KeyPosition>> mKeyPositions = new HashMap<>();
    private HashMap<String, WidgetState> mState = new HashMap<>();
    TypedBundle mBundle = new TypedBundle();
    // Interpolation
    private int mDefaultInterpolator = 0;
    private String mDefaultInterpolatorString = null;
    private Easing mEasing = null;
    private int mAutoTransition = 0;
    private int mDuration = 400;
    private float mStagger = 0.0f;
    OnSwipe mOnSwipe = null;
    CorePixelDp mToPixel; // Todo placed here as a temp till the refactor is done

    /**
     * @return
     * @TODO: add description
     */
    OnSwipe createOnSwipe() {
        return mOnSwipe = new OnSwipe();
    }

    /**
     * @return
     * @TODO: add description
     */
    public boolean hasOnSwipe() {
        return mOnSwipe != null;
    }

    static class OnSwipe {

        String mAnchorId;
        int mAnchorSide;
        public static final int ANCHOR_SIDE_TOP = 0;
        public static final int ANCHOR_SIDE_LEFT = 1;
        public static final int ANCHOR_SIDE_RIGHT = 2;
        public static final int ANCHOR_SIDE_BOTTOM = 3;
        public static final int ANCHOR_SIDE_MIDDLE = 4;
        public static final int ANCHOR_SIDE_START = 5;
        public static final int ANCHOR_SIDE_END = 6;
        public static final String[] SIDES = {"top", "left", "right",
                "bottom", "middle", "start", "end"};
        private static final float[][] TOUCH_SIDES = {
                {0.5f, 0.0f}, // top
                {0.0f, 0.5f}, // left
                {1.0f, 0.5f}, // right
                {0.5f, 1.0f}, // bottom
                {0.5f, 0.5f}, // middle
                {0.0f, 0.5f}, // start TODO (dynamically updated)
                {1.0f, 0.5f}, // end  TODO (dynamically updated)
        };

        String mRotationCenterId;
        String mLimitBoundsTo;
        boolean mDragVertical = true;
        int mDragDirection = 0;
        public static final int DRAG_UP = 0;
        public static final int DRAG_DOWN = 1;
        public static final int DRAG_LEFT = 2;
        public static final int DRAG_RIGHT = 3;
        public static final int DRAG_START = 4;
        public static final int DRAG_END = 5;
        public static final int DRAG_CLOCKWISE = 6;
        public static final int DRAG_ANTICLOCKWISE = 7;
        public static final String[] DIRECTIONS = {"up", "down", "left", "right", "start",
                "end", "clockwise", "anticlockwise"};

        float mDragScale = 1;
        float mDragThreshold = 10;
        int mAutoCompleteMode = 0;
        public static final int MODE_CONTINUOUS_VELOCITY = 0;
        public static final int MODE_SPRING = 1;
        public static final String[] MODE = {"velocity", "spring"};
        float mMaxVelocity = 4.f;
        float mMaxAcceleration = 1.2f;

        // On touch up what happens
        int mOnTouchUp = 0;
        public static final int ON_UP_AUTOCOMPLETE = 0;
        public static final int ON_UP_AUTOCOMPLETE_TO_START = 1;
        public static final int ON_UP_AUTOCOMPLETE_TO_END = 2;
        public static final int ON_UP_STOP = 3;
        public static final int ON_UP_DECELERATE = 4;
        public static final int ON_UP_DECELERATE_AND_COMPLETE = 5;
        public static final int ON_UP_NEVER_COMPLETE_TO_START = 6;
        public static final int ON_UP_NEVER_COMPLETE_TO_END = 7;
        public static final String[] TOUCH_UP = {"autocomplete", "toStart",
                "toEnd", "stop", "decelerate", "decelerateComplete",
                "neverCompleteStart", "neverCompleteEnd"};

        float mSpringMass = 1;
        float mSpringStiffness = 400;
        float mSpringDamping = 10;
        float mSpringStopThreshold = 0.01f;

        // In spring mode what happens at the boundary
        int mSpringBoundary = 0;
        public static final int BOUNDARY_OVERSHOOT = 0;
        public static final int BOUNDARY_BOUNCE_START = 1;
        public static final int BOUNDARY_BOUNCE_END = 2;
        public static final int BOUNDARY_BOUNCE_BOTH = 3;
        public static final String[] BOUNDARY = {"overshoot", "bounceStart",
                "bounceEnd", "bounceBoth"};

        private static final float[][] TOUCH_DIRECTION = {
                {0.0f, -1.0f}, // up
                {0.0f, 1.0f}, // down
                {-1.0f, 0.0f}, // left
                {1.0f, 0.0f}, // right
                {-1.0f, 0.0f}, // start (dynamically updated)
                {1.0f, 0.0f}, // end  (dynamically updated)
        };
        private long mStart;

        float[] getDirection() {
            return TOUCH_DIRECTION[mDragDirection];
        }

        float[] getSide() {
            return TOUCH_SIDES[mAnchorSide];
        }

        void setAnchorId(String anchorId) {
            this.mAnchorId = anchorId;
        }

        void setAnchorSide(int anchorSide) {
            this.mAnchorSide = anchorSide;
        }

        void setRotationCenterId(String rotationCenterId) {
            this.mRotationCenterId = rotationCenterId;
        }

        void setLimitBoundsTo(String limitBoundsTo) {
            this.mLimitBoundsTo = limitBoundsTo;
        }

        void setDragDirection(int dragDirection) {
            this.mDragDirection = dragDirection;
            mDragVertical = (mDragDirection < 2);
        }

        void setDragScale(float dragScale) {
            if (Float.isNaN(dragScale)) {
                return;
            }
            this.mDragScale = dragScale;
        }

        void setDragThreshold(float dragThreshold) {
            if (Float.isNaN(dragThreshold)) {
                return;
            }
            this.mDragThreshold = dragThreshold;
        }

        void setAutoCompleteMode(int mAutoCompleteMode) {
            this.mAutoCompleteMode = mAutoCompleteMode;
        }

        void setMaxVelocity(float maxVelocity) {
            if (Float.isNaN(maxVelocity)) {
                return;
            }
            this.mMaxVelocity = maxVelocity;
        }

        void setMaxAcceleration(float maxAcceleration) {
            if (Float.isNaN(maxAcceleration)) {
                return;
            }
            this.mMaxAcceleration = maxAcceleration;
        }

        void setOnTouchUp(int onTouchUp) {
            this.mOnTouchUp = onTouchUp;
        }

        void setSpringMass(float mSpringMass) {
            if (Float.isNaN(mSpringMass)) {
                return;
            }
            this.mSpringMass = mSpringMass;
        }

        void setSpringStiffness(float mSpringStiffness) {
            if (Float.isNaN(mSpringStiffness)) {
                return;
            }
            this.mSpringStiffness = mSpringStiffness;
        }

        void setSpringDamping(float mSpringDamping) {
            if (Float.isNaN(mSpringDamping)) {
                return;
            }
            this.mSpringDamping = mSpringDamping;
        }

        void setSpringStopThreshold(float mSpringStopThreshold) {
            if (Float.isNaN(mSpringStopThreshold)) {
                return;
            }
            this.mSpringStopThreshold = mSpringStopThreshold;
        }

        void setSpringBoundary(int mSpringBoundary) {
            this.mSpringBoundary = mSpringBoundary;
        }

        private StopEngine mEngine;

        float getDestinationPosition(float currentPosition, float velocity, float duration) {
            switch (mOnTouchUp) {
                case ON_UP_AUTOCOMPLETE_TO_START:
                case ON_UP_NEVER_COMPLETE_TO_END:
                    return 0;
                case ON_UP_AUTOCOMPLETE_TO_END:
                case ON_UP_NEVER_COMPLETE_TO_START:
                    return 1;
                case ON_UP_STOP:
                    return Float.NaN;
                case ON_UP_AUTOCOMPLETE:
                case ON_UP_DECELERATE:
                case ON_UP_DECELERATE_AND_COMPLETE:
            }
            float peek = currentPosition + velocity * duration / 3;
            float travel = velocity * duration / 3;
            if (velocity < 0) {
                peek = currentPosition - velocity * velocity / (2 * mMaxAcceleration);
                travel = velocity * duration / 3;
            }
            if (DEBUG) {
                Utils.log(" currentPosition = " + currentPosition);
                Utils.log("        velocity = " + velocity);
                Utils.log("       c + t = p   = " + currentPosition + " + " + travel + " = " + peek);

                Utils.log("            peek = " + peek);
                Utils.log("mMaxAcceleration = " + mMaxAcceleration);
            }
            return peek > .5 ? 1 : 0;
        }

        void config(float position, float velocity, long start, float duration) {
            mStart = start;
            if (mAutoCompleteMode == MODE_CONTINUOUS_VELOCITY) {
                StopLogicEngine sl;
                if (mEngine instanceof StopLogicEngine) {
                    sl = (StopLogicEngine) mEngine;
                } else {
                    mEngine = sl = new StopLogicEngine();
                }
                float destination = getDestinationPosition(position, velocity, duration);
                sl.config(position, destination, velocity,
                        duration, mMaxAcceleration,
                        mMaxVelocity);
            } else {
                SpringStopEngine sl;
                if (mEngine instanceof SpringStopEngine) {
                    sl = (SpringStopEngine) mEngine;
                } else {
                    mEngine = sl = new SpringStopEngine();
                }
                sl.springConfig(position, 1, velocity,
                        mSpringMass,
                        mSpringStiffness,
                        mSpringDamping,
                        mSpringStopThreshold, mSpringBoundary);
            }
        }

        public float getTouchUpProgress(long currentTime) {
            float time = (currentTime - mStart) * 1E-9f;
            return mEngine.getInterpolation(time);
        }

        public void printInfo() {
            if (mAutoCompleteMode == MODE_CONTINUOUS_VELOCITY) {
                System.out.println("velocity = " + mEngine.getVelocity());
                System.out.println("mMaxAcceleration = " + mMaxAcceleration);
                System.out.println("mMaxVelocity = " + mMaxVelocity);
            } else {
                System.out.println("mSpringMass          = " + mSpringMass);
                System.out.println("mSpringStiffness     = " + mSpringStiffness);
                System.out.println("mSpringDamping       = " + mSpringDamping);
                System.out.println("mSpringStopThreshold = " + mSpringStopThreshold);
                System.out.println("mSpringBoundary      = " + mSpringBoundary);
            }
        }

        public boolean isStopped(float progress) {
            if (mOnTouchUp == ON_UP_STOP)
                return true;
          return   (0 < progress && progress < 1f);
        }
    }


    /**
     * Converts from xy drag to progress
     * This should be used till touch up
     *
     * @param currentProgress
     * @param baseW           parent width
     * @param baseH           parent height
     * @param dx              change in x
     * @param dy              change in y
     * @return the change in progress
     */
    public float dragToProgress(float currentProgress, int baseW, int baseH, float dx, float dy) {
        if (mOnSwipe == null || mOnSwipe.mAnchorId == null) {
            WidgetState w = mState.values().stream().findFirst().get();
            return -dy / w.mParentHeight;
        }
        WidgetState base = mState.get(mOnSwipe.mAnchorId);
        float[] dir = mOnSwipe.getDirection();
        float[] side = mOnSwipe.getSide();
        float[] motionDpDt = new float[2];

        base.interpolate(baseW, baseH, currentProgress, this);
        base.mMotionControl.getDpDt(currentProgress, side[0], side[1], motionDpDt);
        float drag = dx * Math.abs(dir[0]) / motionDpDt[0] + dy * Math.abs(dir[1]) / motionDpDt[1];
        // float change = (mOnSwipe.mDragVertical) ? dy / motionDpDt[1] : dx / motionDpDt[0];
        return drag;
    }

    /**
     * Set the start of the touch up
     *
     * @param currentProgress
     * @param currentTime
     * @param velocityX       pixels per millisecond
     * @param velocityY       pixels per millisecond
     * @return
     */
    public void setTouchUp(float currentProgress,
                           long currentTime,
                           float velocityX,
                           float velocityY) {
        if (mOnSwipe != null) {
            if (DEBUG) {
                Utils.log(" >>> velocity x,y = " + velocityX + " , " + velocityY);
            }
            WidgetState base = mState.get(mOnSwipe.mAnchorId);
            float[] motionDpDt = new float[2];
            float[] dir = mOnSwipe.getDirection();
            float[] side = mOnSwipe.getSide();
            base.mMotionControl.getDpDt(currentProgress, side[0], side[1], motionDpDt);
            float movementInDir = dir[0] * motionDpDt[0]
                    + dir[1] * motionDpDt[1];
            if (Math.abs(movementInDir) < 0.01) {
                if (DEBUG) {
                    Utils.log(" >>> cap minimum v!! ");
                }
                motionDpDt[0] = .01f;
                motionDpDt[1] = .01f;
            }

            float drag = (dir[0] != 0) ? velocityX / motionDpDt[0]
                    : velocityY / motionDpDt[1];

            if (DEBUG) {
                Utils.log(" >>> velocity        " + drag);
                Utils.log(" >>> mDuration       " + mDuration);
                Utils.log(" >>> currentProgress " + currentProgress);
            }
            mOnSwipe.config(currentProgress, drag, currentTime, mDuration);
            if (DEBUG) {
                mOnSwipe.printInfo();
            }
        }
    }

    /**
     * get the current touch up progress current time in nanoseconds
     * (ideally coming from an animation clock)
     *
     * @param currentTime in nanoseconds
     * @return
     */
    public float getTouchUpProgress(long currentTime) {
        if (mOnSwipe != null) {
            return mOnSwipe.getTouchUpProgress(currentTime);
        }

        return 0;
    }

    public boolean isTouchNotDone(float newProgress) {

        return   mOnSwipe.isStopped(newProgress);
    }


    /**
     * get the interpolater based on a constant or a string
     *
     * @param interpolator
     * @param interpolatorString
     * @return
     */
    public static Interpolator getInterpolator(int interpolator, String interpolatorString) {
        switch (interpolator) {
            case SPLINE_STRING:
                return v -> (float) Easing.getInterpolator(interpolatorString).get(v);
            case EASE_IN_OUT:
                return v -> (float) Easing.getInterpolator("standard").get(v);
            case EASE_IN:
                return v -> (float) Easing.getInterpolator("accelerate").get(v);
            case EASE_OUT:
                return v -> (float) Easing.getInterpolator("decelerate").get(v);
            case LINEAR:
                return v -> (float) Easing.getInterpolator("linear").get(v);
            case ANTICIPATE:
                return v -> (float) Easing.getInterpolator("anticipate").get(v);
            case OVERSHOOT:
                return v -> (float) Easing.getInterpolator("overshoot").get(v);
            case BOUNCE: // TODO make a better bounce
                return v -> (float) Easing.getInterpolator("spline(0.0, 0.2, 0.4, 0.6, "
                        + "0.8 ,1.0, 0.8, 1.0, 0.9, 1.0)").get(v);
        }
        return null;
    }

    /**
     * @param target
     * @param frameNumber
     * @return
     * @TODO: add description
     */
    public KeyPosition findPreviousPosition(String target, int frameNumber) {
        while (frameNumber >= 0) {
            HashMap<String, KeyPosition> map = mKeyPositions.get(frameNumber);
            if (map != null) {
                KeyPosition keyPosition = map.get(target);
                if (keyPosition != null) {
                    return keyPosition;
                }
            }
            frameNumber--;
        }
        return null;
    }

    /**
     * @param target
     * @param frameNumber
     * @return
     * @TODO: add description
     */
    public KeyPosition findNextPosition(String target, int frameNumber) {
        while (frameNumber <= 100) {
            HashMap<String, KeyPosition> map = mKeyPositions.get(frameNumber);
            if (map != null) {
                KeyPosition keyPosition = map.get(target);
                if (keyPosition != null) {
                    return keyPosition;
                }
            }
            frameNumber++;
        }
        return null;
    }

    /**
     * @param frame
     * @return
     * @TODO: add description
     */
    public int getNumberKeyPositions(WidgetFrame frame) {
        int numKeyPositions = 0;
        int frameNumber = 0;
        while (frameNumber <= 100) {
            HashMap<String, KeyPosition> map = mKeyPositions.get(frameNumber);
            if (map != null) {
                KeyPosition keyPosition = map.get(frame.widget.stringId);
                if (keyPosition != null) {
                    numKeyPositions++;
                }
            }
            frameNumber++;
        }
        return numKeyPositions;
    }

    /**
     * @param id
     * @return
     * @TODO: add description
     */
    public Motion getMotion(String id) {
        return getWidgetState(id, null, 0).mMotionControl;
    }

    /**
     * @param frame
     * @param x
     * @param y
     * @param pos
     * @TODO: add description
     */
    public void fillKeyPositions(WidgetFrame frame, float[] x, float[] y, float[] pos) {
        int numKeyPositions = 0;
        int frameNumber = 0;
        while (frameNumber <= 100) {
            HashMap<String, KeyPosition> map = mKeyPositions.get(frameNumber);
            if (map != null) {
                KeyPosition keyPosition = map.get(frame.widget.stringId);
                if (keyPosition != null) {
                    x[numKeyPositions] = keyPosition.mX;
                    y[numKeyPositions] = keyPosition.mY;
                    pos[numKeyPositions] = keyPosition.mFrame;
                    numKeyPositions++;
                }
            }
            frameNumber++;
        }
    }

    /**
     * @return
     * @TODO: add description
     */
    public boolean hasPositionKeyframes() {
        return mKeyPositions.size() > 0;
    }

    /**
     * @param bundle
     * @TODO: add description
     */
    public void setTransitionProperties(TypedBundle bundle) {
        bundle.applyDelta(mBundle);
        bundle.applyDelta(this);
    }

    @Override
    public boolean setValue(int id, int value) {
        return false;
    }

    @Override
    public boolean setValue(int id, float value) {
        if (id == TypedValues.TransitionType.TYPE_STAGGERED) {
            mStagger = value;
        }
        return false;
    }

    @Override
    public boolean setValue(int id, String value) {
        if (id == TransitionType.TYPE_INTERPOLATOR) {
            mEasing = Easing.getInterpolator(mDefaultInterpolatorString = value);
        }
        return false;
    }

    @Override
    public boolean setValue(int id, boolean value) {
        return false;
    }

    @Override
    public int getId(String name) {
        return 0;
    }

    public boolean isEmpty() {
        return mState.isEmpty();
    }

    /**
     * @TODO: add description
     */
    public void clear() {
        mState.clear();
    }

    /**
     * @param key
     * @return
     * @TODO: add description
     */
    public boolean contains(String key) {
        return mState.containsKey(key);
    }

    /**
     * @param target
     * @param bundle
     * @TODO: add description
     */
    public void addKeyPosition(String target, TypedBundle bundle) {
        getWidgetState(target, null, 0).setKeyPosition(bundle);
    }

    /**
     * @param target
     * @param bundle
     * @TODO: add description
     */
    public void addKeyAttribute(String target, TypedBundle bundle) {
        getWidgetState(target, null, 0).setKeyAttribute(bundle);
    }

    /**
     * @param target
     * @param bundle
     * @TODO: add description
     */
    public void addKeyCycle(String target, TypedBundle bundle) {
        getWidgetState(target, null, 0).setKeyCycle(bundle);
    }

    /**
     * @param target
     * @param frame
     * @param type
     * @param x
     * @param y
     * @TODO: add description
     */
    public void addKeyPosition(String target, int frame, int type, float x, float y) {
        TypedBundle bundle = new TypedBundle();
        bundle.add(TypedValues.PositionType.TYPE_POSITION_TYPE, 2);
        bundle.add(TypedValues.TYPE_FRAME_POSITION, frame);
        bundle.add(TypedValues.PositionType.TYPE_PERCENT_X, x);
        bundle.add(TypedValues.PositionType.TYPE_PERCENT_Y, y);
        getWidgetState(target, null, 0).setKeyPosition(bundle);

        KeyPosition keyPosition = new KeyPosition(target, frame, type, x, y);
        HashMap<String, KeyPosition> map = mKeyPositions.get(frame);
        if (map == null) {
            map = new HashMap<>();
            mKeyPositions.put(frame, map);
        }
        map.put(target, keyPosition);
    }

    /**
     * @param state
     * @param widgetId
     * @param property
     * @param value
     * @TODO: add description
     */
    public void addCustomFloat(int state, String widgetId, String property, float value) {
        WidgetState widgetState = getWidgetState(widgetId, null, state);
        WidgetFrame frame = widgetState.getFrame(state);
        frame.addCustomFloat(property, value);
    }

    /**
     * @param state
     * @param widgetId
     * @param property
     * @param color
     * @TODO: add description
     */
    public void addCustomColor(int state, String widgetId, String property, int color) {
        WidgetState widgetState = getWidgetState(widgetId, null, state);
        WidgetFrame frame = widgetState.getFrame(state);
        frame.addCustomColor(property, color);
    }

    /**
     * @param container
     * @param state
     * @TODO: add description
     */
    public void updateFrom(ConstraintWidgetContainer container, int state) {
        final ArrayList<ConstraintWidget> children = container.getChildren();
        final int count = children.size();
        for (int i = 0; i < count; i++) {
            ConstraintWidget child = children.get(i);
            WidgetState widgetState = getWidgetState(child.stringId, null, state);
            widgetState.update(child, state);
        }
    }

    /**
     * @param parentWidth
     * @param parentHeight
     * @param progress
     * @TODO: add description
     */
    public void interpolate(int parentWidth, int parentHeight, float progress) {
        if (mEasing != null) {
            progress = (float) mEasing.get(progress);
        }
        for (String key : mState.keySet()) {
            WidgetState widget = mState.get(key);
            widget.interpolate(parentWidth, parentHeight, progress, this);
        }
    }

    /**
     * @param id
     * @return
     * @TODO: add description
     */
    public WidgetFrame getStart(String id) {
        WidgetState widgetState = mState.get(id);
        if (widgetState == null) {
            return null;
        }
        return widgetState.mStart;
    }

    /**
     * @param id
     * @return
     * @TODO: add description
     */
    public WidgetFrame getEnd(String id) {
        WidgetState widgetState = mState.get(id);
        if (widgetState == null) {
            return null;
        }
        return widgetState.mEnd;
    }

    /**
     * @param id
     * @return
     * @TODO: add description
     */
    public WidgetFrame getInterpolated(String id) {
        WidgetState widgetState = mState.get(id);
        if (widgetState == null) {
            return null;
        }
        return widgetState.mInterpolated;
    }

    /**
     * @param id
     * @return
     * @TODO: add description
     */
    public float[] getPath(String id) {
        WidgetState widgetState = mState.get(id);
        int duration = 1000;
        int frames = duration / 16;
        float[] mPoints = new float[frames * 2];
        widgetState.mMotionControl.buildPath(mPoints, frames);
        return mPoints;
    }

    /**
     * @param id
     * @param rectangles
     * @param pathMode
     * @param position
     * @return
     * @TODO: add description
     */
    public int getKeyFrames(String id, float[] rectangles, int[] pathMode, int[] position) {
        WidgetState widgetState = mState.get(id);
        return widgetState.mMotionControl.buildKeyFrames(rectangles, pathMode, position);
    }

    private WidgetState getWidgetState(String widgetId) {
        return this.mState.get(widgetId);
    }

    private WidgetState getWidgetState(String widgetId,
                                       ConstraintWidget child,
                                       int transitionState) {
        WidgetState widgetState = this.mState.get(widgetId);
        if (widgetState == null) {
            widgetState = new WidgetState();
            mBundle.applyDelta(widgetState.mMotionControl);

            mState.put(widgetId, widgetState);
            if (child != null) {
                widgetState.update(child, transitionState);
            }
        }
        return widgetState;
    }

    /**
     * Used in debug draw
     *
     * @param child
     * @return
     */
    public WidgetFrame getStart(ConstraintWidget child) {
        return getWidgetState(child.stringId, null, Transition.START).mStart;
    }

    /**
     * Used in debug draw
     *
     * @param child
     * @return
     */
    public WidgetFrame getEnd(ConstraintWidget child) {
        return getWidgetState(child.stringId, null, Transition.END).mEnd;
    }

    /**
     * Used after the interpolation
     *
     * @param child
     * @return
     */
    public WidgetFrame getInterpolated(ConstraintWidget child) {
        return getWidgetState(child.stringId, null, Transition.INTERPOLATED).mInterpolated;
    }

    public Interpolator getInterpolator() {
        return getInterpolator(mDefaultInterpolator, mDefaultInterpolatorString);
    }

    public int getAutoTransition() {
        return mAutoTransition;
    }

    static class WidgetState {
        WidgetFrame mStart;
        WidgetFrame mEnd;
        WidgetFrame mInterpolated;
        Motion mMotionControl;
        MotionWidget mMotionWidgetStart;
        MotionWidget mMotionWidgetEnd;
        MotionWidget mMotionWidgetInterpolated;
        KeyCache mKeyCache = new KeyCache();
        private int mParentHeight = -1;
        private int mParentWidth = -1;

        WidgetState() {
            mStart = new WidgetFrame();
            mEnd = new WidgetFrame();
            mInterpolated = new WidgetFrame();
            mMotionWidgetStart = new MotionWidget(mStart);
            mMotionWidgetEnd = new MotionWidget(mEnd);
            mMotionWidgetInterpolated = new MotionWidget(mInterpolated);
            mMotionControl = new Motion(mMotionWidgetStart);
            mMotionControl.setStart(mMotionWidgetStart);
            mMotionControl.setEnd(mMotionWidgetEnd);
        }

        public void setKeyPosition(TypedBundle prop) {
            MotionKeyPosition keyPosition = new MotionKeyPosition();
            prop.applyDelta(keyPosition);
            mMotionControl.addKey(keyPosition);
        }

        public void setKeyAttribute(TypedBundle prop) {
            MotionKeyAttributes keyAttributes = new MotionKeyAttributes();
            prop.applyDelta(keyAttributes);
            mMotionControl.addKey(keyAttributes);
        }

        public void setKeyCycle(TypedBundle prop) {
            MotionKeyCycle keyAttributes = new MotionKeyCycle();
            prop.applyDelta(keyAttributes);
            mMotionControl.addKey(keyAttributes);
        }

        public void update(ConstraintWidget child, int state) {
            if (state == START) {
                mStart.update(child);
                mMotionControl.setStart(mMotionWidgetStart);
            } else if (state == END) {
                mEnd.update(child);
                mMotionControl.setEnd(mMotionWidgetEnd);
            }
            mParentWidth = -1;
        }

        public WidgetFrame getFrame(int type) {
            if (type == START) {
                return mStart;
            } else if (type == END) {
                return mEnd;
            }
            return mInterpolated;
        }

        public void interpolate(int parentWidth,
                                int parentHeight,
                                float progress,
                                Transition transition) {
            // TODO  only update if parentHeight != mParentHeight || parentWidth != mParentWidth) {
            mParentHeight = parentHeight;
            mParentWidth = parentWidth;
            mMotionControl.setup(parentWidth, parentHeight, 1, System.nanoTime());

            WidgetFrame.interpolate(parentWidth, parentHeight,
                    mInterpolated, mStart, mEnd, transition, progress);
            mInterpolated.interpolatedPos = progress;
            mMotionControl.interpolate(mMotionWidgetInterpolated,
                    progress, System.nanoTime(), mKeyCache);
        }
    }

    static class KeyPosition {
        int mFrame;
        String mTarget;
        int mType;
        float mX;
        float mY;

        KeyPosition(String target, int frame, int type, float x, float y) {
            this.mTarget = target;
            this.mFrame = frame;
            this.mType = type;
            this.mX = x;
            this.mY = y;
        }
    }
}
