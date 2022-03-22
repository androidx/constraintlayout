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
import androidx.constraintlayout.core.motion.utils.TypedBundle;
import androidx.constraintlayout.core.motion.utils.TypedValues;
import androidx.constraintlayout.core.widgets.ConstraintWidget;
import androidx.constraintlayout.core.widgets.ConstraintWidgetContainer;

import java.util.ArrayList;
import java.util.HashMap;

public class Transition implements TypedValues {
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

    /**
     * @TODO: add description
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
     * @TODO: add description
     * @param target
     * @param frameNumber
     * @return
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
     * @TODO: add description
     * @param target
     * @param frameNumber
     * @return
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
     * @TODO: add description
     * @param frame
     * @return
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
     * @TODO: add description
     * @param id
     * @return
     */
    public Motion getMotion(String id) {
        return getWidgetState(id, null, 0).mMotionControl;
    }

    /**
     * @TODO: add description
     * @param frame
     * @param x
     * @param y
     * @param pos
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
     * @TODO: add description
     * @return
     */
    public boolean hasPositionKeyframes() {
        return mKeyPositions.size() > 0;
    }

    /**
     * @TODO: add description
     * @param bundle
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
     * @TODO: add description
     * @param key
     * @return
     */
    public boolean contains(String key) {
        return mState.containsKey(key);
    }

    /**
     * @TODO: add description
     * @param target
     * @param bundle
     */
    public void addKeyPosition(String target, TypedBundle bundle) {
        getWidgetState(target, null, 0).setKeyPosition(bundle);
    }

    /**
     * @TODO: add description
     * @param target
     * @param bundle
     */
    public void addKeyAttribute(String target, TypedBundle bundle) {
        getWidgetState(target, null, 0).setKeyAttribute(bundle);
    }

    /**
     * @TODO: add description
     * @param target
     * @param bundle
     */
    public void addKeyCycle(String target, TypedBundle bundle) {
        getWidgetState(target, null, 0).setKeyCycle(bundle);
    }

    /**
     * @TODO: add description
     * @param target
     * @param frame
     * @param type
     * @param x
     * @param y
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
     * @TODO: add description
     * @param state
     * @param widgetId
     * @param property
     * @param value
     */
    public void addCustomFloat(int state, String widgetId, String property, float value) {
        WidgetState widgetState = getWidgetState(widgetId, null, state);
        WidgetFrame frame = widgetState.getFrame(state);
        frame.addCustomFloat(property, value);
    }

    /**
     * @TODO: add description
     * @param state
     * @param widgetId
     * @param property
     * @param color
     */
    public void addCustomColor(int state, String widgetId, String property, int color) {
        WidgetState widgetState = getWidgetState(widgetId, null, state);
        WidgetFrame frame = widgetState.getFrame(state);
        frame.addCustomColor(property, color);
    }

    /**
     * @TODO: add description
     * @param container
     * @param state
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
     * @TODO: add description
     * @param parentWidth
     * @param parentHeight
     * @param progress
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
     * @TODO: add description
     * @param id
     * @return
     */
    public WidgetFrame getStart(String id) {
        WidgetState widgetState = mState.get(id);
        if (widgetState == null) {
            return null;
        }
        return widgetState.mStart;
    }

    /**
     * @TODO: add description
     * @param id
     * @return
     */
    public WidgetFrame getEnd(String id) {
        WidgetState widgetState = mState.get(id);
        if (widgetState == null) {
            return null;
        }
        return widgetState.mEnd;
    }

    /**
     * @TODO: add description
     * @param id
     * @return
     */
    public WidgetFrame getInterpolated(String id) {
        WidgetState widgetState = mState.get(id);
        if (widgetState == null) {
            return null;
        }
        return widgetState.mInterpolated;
    }

    /**
     * @TODO: add description
     * @param id
     * @return
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
     * @TODO: add description
     * @param id
     * @param rectangles
     * @param pathMode
     * @param position
     * @return
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
        int mParentHeight = -1;
        int mParentWidth = -1;

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
