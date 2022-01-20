/*
 * Copyright (C) 2018 The Android Open Source Project
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

import androidx.constraintlayout.widget.ConstraintSet;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import androidx.constraintlayout.widget.R;

import java.util.HashMap;

import static androidx.constraintlayout.widget.ConstraintSet.BASELINE;
import static androidx.constraintlayout.widget.ConstraintSet.BOTTOM;
import static androidx.constraintlayout.widget.ConstraintSet.END;
import static androidx.constraintlayout.widget.ConstraintSet.HORIZONTAL;
import static androidx.constraintlayout.widget.ConstraintSet.LEFT;
import static androidx.constraintlayout.widget.ConstraintSet.RIGHT;
import static androidx.constraintlayout.widget.ConstraintSet.START;
import static androidx.constraintlayout.widget.ConstraintSet.TOP;
import static androidx.constraintlayout.widget.ConstraintSet.VERTICAL;
import static androidx.constraintlayout.widget.ConstraintSet.WRAP_CONTENT;

/**
 * This is the interface used by androidStudio design surface
 * It is marked with the interface so that java proxy will use the same interface to build a proxy.
 */
interface ProxyInterface {
    void setToolPosition(float position);

    long getTransitionTimeMs();

    boolean setKeyFramePosition(Object view, int position, int type, float x, float y);

    int designAccess(int cmd, String type, Object viewObject,
                     float[] in, int inLength, float[] out, int outLength);

    void setAttributes(int dpi, String constraintSetId, Object opaqueView, Object opaqueAttributes);

    float getKeyFramePosition(Object view, int type, float x, float y);

    void setKeyFrame(Object view, int position, String name, Object value);

    Boolean getPositionKeyframe(Object keyFrame, Object view, float x, float y, String[] attribute, float[] value);

    Object getKeyframeAtLocation(Object viewObject, float x, float y);
}

/**
 * Utility class to manipulate MotionLayout from the layout editor
 *
 * @hide
 */
public class DesignTool implements ProxyInterface {

    private static final boolean DEBUG = false;
    private static final String TAG = "DesignTool";

    private final MotionLayout mMotionLayout;
    private MotionScene mSceneCache;

    private String mLastStartState = null;
    private String mLastEndState = null;
    private int mLastStartStateId = -1;
    private int mLastEndStateId = -1;

    public DesignTool(MotionLayout motionLayout) {
        mMotionLayout = motionLayout;
    }

    final static HashMap<Pair<Integer, Integer>, String> allAttributes = new HashMap<>();
    final static HashMap<String, String> allMargins = new HashMap<>();

    static {
        allAttributes.put(Pair.create(BOTTOM, BOTTOM), "layout_constraintBottom_toBottomOf");
        allAttributes.put(Pair.create(BOTTOM, TOP), "layout_constraintBottom_toTopOf");
        allAttributes.put(Pair.create(TOP, BOTTOM), "layout_constraintTop_toBottomOf");
        allAttributes.put(Pair.create(TOP, TOP), "layout_constraintTop_toTopOf");
        allAttributes.put(Pair.create(START, START), "layout_constraintStart_toStartOf");
        allAttributes.put(Pair.create(START, END), "layout_constraintStart_toEndOf");
        allAttributes.put(Pair.create(END, START), "layout_constraintEnd_toStartOf");
        allAttributes.put(Pair.create(END, END), "layout_constraintEnd_toEndOf");
        allAttributes.put(Pair.create(LEFT, LEFT), "layout_constraintLeft_toLeftOf");
        allAttributes.put(Pair.create(LEFT, RIGHT), "layout_constraintLeft_toRightOf");
        allAttributes.put(Pair.create(RIGHT, RIGHT), "layout_constraintRight_toRightOf");
        allAttributes.put(Pair.create(RIGHT, LEFT), "layout_constraintRight_toLeftOf");
        allAttributes.put(Pair.create(BASELINE, BASELINE), "layout_constraintBaseline_toBaselineOf");

        allMargins.put("layout_constraintBottom_toBottomOf", "layout_marginBottom");
        allMargins.put("layout_constraintBottom_toTopOf", "layout_marginBottom");
        allMargins.put("layout_constraintTop_toBottomOf", "layout_marginTop");
        allMargins.put("layout_constraintTop_toTopOf", "layout_marginTop");
        allMargins.put("layout_constraintStart_toStartOf", "layout_marginStart");
        allMargins.put("layout_constraintStart_toEndOf", "layout_marginStart");
        allMargins.put("layout_constraintEnd_toStartOf", "layout_marginEnd");
        allMargins.put("layout_constraintEnd_toEndOf", "layout_marginEnd");
        allMargins.put("layout_constraintLeft_toLeftOf", "layout_marginLeft");
        allMargins.put("layout_constraintLeft_toRightOf", "layout_marginLeft");
        allMargins.put("layout_constraintRight_toRightOf", "layout_marginRight");
        allMargins.put("layout_constraintRight_toLeftOf", "layout_marginRight");
    }

    private static int GetPxFromDp(int dpi, String value) {
        if (value == null) {
            return 0;
        }
        int index = value.indexOf('d');
        if (index == -1) {
            return 0;
        }
        String filteredValue = value.substring(0, index);
        int dpValue = (int) (Integer.valueOf(filteredValue) * dpi / 160f);
        return dpValue;
    }

    private static void Connect(int dpi, ConstraintSet set, View view, HashMap<String, String> attributes, int from, int to) {
        String connection = allAttributes.get(Pair.create(from, to));
        String connectionValue = attributes.get(connection);

        if (connectionValue != null) {
            int marginValue = 0;
            String margin = allMargins.get(connection);
            if (margin != null) {
                marginValue = GetPxFromDp(dpi, attributes.get(margin));
            }
            int id = Integer.parseInt(connectionValue);
            set.connect(view.getId(), from, id, to, marginValue);
        }
    }

    private static void SetBias(ConstraintSet set, View view, HashMap<String, String> attributes, int type) {
        String bias = "layout_constraintHorizontal_bias";
        if (type == VERTICAL) {
            bias = "layout_constraintVertical_bias";
        }
        String biasValue = attributes.get(bias);
        if (biasValue != null) {
            if (type == HORIZONTAL) {
                set.setHorizontalBias(view.getId(), Float.parseFloat(biasValue));
            } else if (type == VERTICAL) {
                set.setVerticalBias(view.getId(), Float.parseFloat(biasValue));
            }
        }
    }

    private static void SetDimensions(int dpi, ConstraintSet set, View view, HashMap<String, String> attributes, int type) {
        String dimension = "layout_width";
        if (type == VERTICAL) {
            dimension = "layout_height";
        }
        String dimensionValue = attributes.get(dimension);
        if (dimensionValue != null) {
            int value = WRAP_CONTENT;
            if (!dimensionValue.equalsIgnoreCase("wrap_content")) {
                value = GetPxFromDp(dpi, dimensionValue);
            }
            if (type == HORIZONTAL) {
                set.constrainWidth(view.getId(), value);
            } else {
                set.constrainHeight(view.getId(), value);
            }
        }
    }

    private static void SetAbsolutePositions(int dpi, ConstraintSet set, View view, HashMap<String, String> attributes) {
        String absoluteX = attributes.get("layout_editor_absoluteX");
        if (absoluteX != null) {
            set.setEditorAbsoluteX(view.getId(), GetPxFromDp(dpi, absoluteX));
        }
        String absoluteY = attributes.get("layout_editor_absoluteY");
        if (absoluteY != null) {
            set.setEditorAbsoluteY(view.getId(), GetPxFromDp(dpi, absoluteY));
        }
    }

    /**
     * Get the center point of the animation path of a view
     *
     * @param view view to getMap the animation of
     * @param path array to be filled (x1,y1,x2,y2...)
     * @return -1 if not under and animation 0 if not animated or number of point along animation
     */
    public int getAnimationPath(Object view, float[] path, int len) {
        if (mMotionLayout.mScene == null) {
            return -1;
        }

        MotionController motionController = mMotionLayout.mFrameArrayList.get(view);
        if (motionController == null) {
            return 0;
        }

        motionController.buildPath(path, len);
        return len;
    }

    /**
     * Get the center point of the animation path of a view
     *
     * @param view view to getMap the animation of
     * @param path array to be filled (in groups of 8) (x1,y1,x2,y2...)
     */
    public void getAnimationRectangles(Object view, float[] path) {
        if (mMotionLayout.mScene == null) {
            return;
        }
        int duration = mMotionLayout.mScene.getDuration();
        int frames = duration / 16;

        MotionController motionController = mMotionLayout.mFrameArrayList.get(view);
        if (motionController == null) {
            return;
        }

        motionController.buildRectangles(path, frames);
    }

    /**
     * Get the location of the start end and key frames
     *
     * @param view the view to track
     * @param key  array to be filled
     * @return number of key frames + 2
     */
    public int getAnimationKeyFrames(Object view, float[] key) {
        if (mMotionLayout.mScene == null) {
            return -1;
        }
        int duration = mMotionLayout.mScene.getDuration();
        int frames = duration / 16;

        MotionController motionController = mMotionLayout.mFrameArrayList.get(view);
        if (motionController == null) {
            return 0;
        }

        motionController.buildKeyFrames(key, null);
        return frames;
    }

    /**
     * @param position
     * @hide
     */
    public void setToolPosition(float position) {
        if (mMotionLayout.mScene == null) {
            mMotionLayout.mScene = mSceneCache;
        }
        mMotionLayout.setProgress(position);
        mMotionLayout.evaluate(true);
        mMotionLayout.requestLayout();
        mMotionLayout.invalidate();
    }

    /**
     * This sets the constraint set based on a string. (without the "@+id/")
     *
     * @param id
     */
    public void setState(String id) {
        if (id == null) {
            id = "motion_base";
        }
        if (mLastStartState == id) {
            return;
        }

        if (DEBUG) {
            System.out.println("================================");
            dumpConstraintSet(id);
        }

        mLastStartState = id;
        mLastEndState = null;
        if (id == null && false) { // going to base layout
            if (mMotionLayout.mScene != null) {
                mSceneCache = mMotionLayout.mScene;
                mMotionLayout.mScene = null;
            }

            mMotionLayout.setProgress(0);
            mMotionLayout.requestLayout();
        }

        if (mMotionLayout.mScene == null) {
            mMotionLayout.mScene = mSceneCache;
        }

        int rscId = mMotionLayout.lookUpConstraintId(id);
        mLastStartStateId = rscId;

        if (rscId != 0) {
            if (rscId == mMotionLayout.getStartState()) {
                mMotionLayout.setProgress(0);
            } else if (rscId == mMotionLayout.getEndState()) {
                mMotionLayout.setProgress(1);
            } else {
                mMotionLayout.transitionToState(rscId);
                mMotionLayout.setProgress(1);
            }
        }
        mMotionLayout.requestLayout();
    }

    public String getStartState() {
       int startId =  mMotionLayout.getStartState();
       if (mLastStartStateId == startId) {
           return mLastStartState;
       }
        String last =  mMotionLayout.getConstraintSetNames(startId);

        if (last != null) {
            mLastStartState = last;
            mLastStartStateId = startId;
        }
        return mMotionLayout.getConstraintSetNames(startId);
    }

    public String getEndState() {
        int endId =  mMotionLayout.getEndState();

        if (mLastEndStateId == endId) {
            return mLastEndState;
        }
        String last =  mMotionLayout.getConstraintSetNames(endId);
        if (last != null) {
            mLastEndState = last;
            mLastEndStateId = endId;
        }
        return last;
    }

    /**
     * Return the current progress of the current transition
     *
     * @return current transition's progress
     */
    public float getProgress() {
        return mMotionLayout.getProgress();
    }

    /**
     * Return the current state (ConstraintSet id) as a string
     *
     * @return the last state set via the design tool bridge
     */
    public String getState() {
        if (mLastStartState != null && mLastEndState != null) {
            float progress = getProgress();
            float epsilon = 0.01f;
            if (progress <= epsilon) {
                return mLastStartState;
            } else if (progress >= 1 - epsilon) {
                return mLastEndState;
            }
        }
        return mLastStartState;
    }

    /**
     * Utility method, returns true if we are currently in a transition
     *
     * @return true if in a transition, false otherwise
     */
    public boolean isInTransition() {
        return mLastStartState != null && mLastEndState != null;
    }

    /**
     * This sets the constraint set based on a string. (without the "@+id/")
     *
     * @param start
     * @param end
     */
    public void setTransition(String start, String end) {
        if (mMotionLayout.mScene == null) {
            mMotionLayout.mScene = mSceneCache;
        }
        int startId = mMotionLayout.lookUpConstraintId(start);
        int endId = mMotionLayout.lookUpConstraintId(end);

        mMotionLayout.setTransition(startId, endId);
        mLastStartStateId = startId;
        mLastEndStateId  = endId;

        mLastStartState = start;
        mLastEndState = end;
    }
    /**
     * this allow disabling autoTransitions to prevent design surface from being in undefined states
     *
     * @param disable
     */
    public void disableAutoTransition(boolean disable){
        mMotionLayout.disableAutoTransition(disable);
    }

    /**
     * Gets the time of the currently set animation.
     *
     * @return time in Milliseconds
     */
    public long getTransitionTimeMs() {
        return mMotionLayout.getTransitionTimeMs();
    }

    /**
     * Get the keyFrames for the view controlled by this MotionController.
     * The call is designed to be efficient because it will be called 30x Number of views a second
     *
     * @param view the view to return keyframe positions
     * @param type is position(0-100) + 1000*mType(1=Attributes, 2=Position, 3=TimeCycle 4=Cycle 5=Trigger
     * @param pos the x&y position of the keyFrame along the path
     * @return Number of keyFrames found
     */
    public int getKeyFramePositions(Object view,int []type, float[] pos) {
        MotionController controller = mMotionLayout.mFrameArrayList.get((View) view);
        if (controller == null) {
            return  0;
        }
        return controller.getKeyFramePositions(type, pos);
    }
    /**
     * Get the keyFrames for the view controlled by this MotionController.
     * The call is designed to be efficient because it will be called 30x Number of views a second
     *
     * @param view the view to return keyframe positions
     * @param info
     * @return Number of keyFrames found
     */
    public int getKeyFrameInfo(Object view, int type, int[] info) {
        MotionController controller = mMotionLayout.mFrameArrayList.get((View) view);
        if (controller == null) {
            return 0;
        }
        return controller.getKeyFrameInfo(type,info);
    }
    /**
     * @param view
     * @param type
     * @param x
     * @param y
     * @return
     * @hide
     */
    public float getKeyFramePosition(Object view, int type, float x, float y) {
        if (!(view instanceof View)) {
            return 0f;
        }

        MotionController mc = mMotionLayout.mFrameArrayList.get((View) view);
        if (mc == null) {
            return 0f;
        }

        return mc.getKeyFrameParameter(type, x, y);
    }

    /**
     * @param view
     * @param position
     * @param name
     * @param value
     * @hide
     */
    public void setKeyFrame(Object view, int position, String name, Object value) {
        if (DEBUG) {
            Log.v(TAG, "setKeyFrame " + position + " <" + name + "> " + value);
        }
        if (mMotionLayout.mScene != null) {
            mMotionLayout.mScene.setKeyframe((View) view, position, name, value);
            mMotionLayout.mTransitionGoalPosition = position / 100f;
            mMotionLayout.mTransitionLastPosition = 0;
            mMotionLayout.rebuildScene();
            mMotionLayout.evaluate(true);
        }
    }

    /**
     * Move the widget directly
     *
     * @param view
     * @param position
     * @param type
     * @param x
     * @param y
     * @return
     * @hide
     */
    public boolean setKeyFramePosition(Object view, int position, int type, float x, float y) {
        if (!(view instanceof View)) {
            return false;
        }

        if (mMotionLayout.mScene != null) {
            MotionController motionController = mMotionLayout.mFrameArrayList.get(view);
            position = (int) (mMotionLayout.mTransitionPosition * 100);
            if (motionController != null && mMotionLayout.mScene.hasKeyFramePosition((View) view, position)) {
                float fx = motionController.getKeyFrameParameter(MotionController.HORIZONTAL_PATH_X, x, y);
                float fy = motionController.getKeyFrameParameter(MotionController.VERTICAL_PATH_Y, x, y);
                // TODO: supports path relative
                mMotionLayout.mScene.setKeyframe((View) view, position, "motion:percentX", fx);
                mMotionLayout.mScene.setKeyframe((View) view, position, "motion:percentY", fy);
                mMotionLayout.rebuildScene();
                mMotionLayout.evaluate(true);
                mMotionLayout.invalidate();
                return true;
            }
        }
        return false;
    }

    /**
     * @param view
     * @param debugMode
     * @hide
     */
    public void setViewDebug(Object view, int debugMode) {
        if (!(view instanceof View)) {
            return;
        }

        MotionController motionController = mMotionLayout.mFrameArrayList.get(view);
        if (motionController != null) {
            motionController.setDrawPath(debugMode);
            mMotionLayout.invalidate();
        }
    }

    /**
     * This is a general access to systems in the  MotionLayout System
     * This provides a series of commands used by the designer to access needed logic
     * It is written this way to minimize the interface between the library and designer.
     * It allows the logic to be kept only in the library not replicated in the gui builder.
     * It also allows us to understand understand the version  of MotionLayout in use
     * commands
     * 0 return the version number
     * 1 Get the center point of the animation path of a view
     * 2 Get the location of the start end and key frames
     *
     * @param cmd        this provide the command needed
     * @param type       support argument for command
     * @param viewObject if this command references a view this provides access
     * @param in         this allows for an array of float to be the input to the system
     * @param inLength   this provides the length of the input
     * @param out        this provide the output array
     * @param outLength  the length of the output array
     * @return command dependent -1 is typically an error (do not understand)
     */
    public int designAccess(int cmd, String type, Object viewObject,
                            float[] in, int inLength, float[] out, int outLength) {
        View view = (View) viewObject;
        MotionController motionController = null;
        if (cmd != 0) {
            if (mMotionLayout.mScene == null) {
                return -1;
            }

            if (view != null) { // Cant find the view
                motionController = mMotionLayout.mFrameArrayList.get(view);
                if (motionController == null) {
                    return -1;
                }
            } else { // currently only cmd  == 0 does not require a motion view
                return -1;
            }

        }
        switch (cmd) {
            case 0: // version
                return 1;
            case 1: { // get View path

                int duration = mMotionLayout.mScene.getDuration();
                int frames = duration / 16;

                motionController.buildPath(out, frames);
                return frames;
            }
            case 2: { // get key frames

                int duration = mMotionLayout.mScene.getDuration();
                int frames = duration / 16;

                motionController.buildKeyFrames(out, null);
                return frames;
            }
            case 3: { // get Attribute

                int duration = mMotionLayout.mScene.getDuration();
                int frames = duration / 16;

                return motionController.getAttributeValues(type, out, outLength);
            }

            default:
                return -1;

        }
    }

    public Object getKeyframe(int type, int target, int position) {
        if (mMotionLayout.mScene == null) {
            return null;
        }
        return mMotionLayout.mScene.getKeyFrame(mMotionLayout.getContext(), type, target, position);
    }

    public Object getKeyframeAtLocation(Object viewObject, float x, float y) {
        View view = (View) viewObject;
        MotionController motionController = null;
        if (mMotionLayout.mScene == null) {
            return -1;
        }
        if (view != null) { // Cant find the view
            motionController = mMotionLayout.mFrameArrayList.get(view);
            if (motionController == null) {
                return null;
            }
        } else {
            return null;
        }
        ViewGroup viewGroup = ((ViewGroup) view.getParent());
        int layoutWidth = viewGroup.getWidth();
        int layoutHeight = viewGroup.getHeight();
        return motionController.getPositionKeyframe(layoutWidth, layoutHeight, x, y);
    }

    public Boolean getPositionKeyframe(Object keyFrame, Object view, float x, float y, String[] attribute, float[] value) {
        if (keyFrame instanceof KeyPositionBase) {
            KeyPositionBase key = (KeyPositionBase) keyFrame;
            MotionController motionController = mMotionLayout.mFrameArrayList.get((View) view);
            motionController.positionKeyframe((View) view, key, x, y, attribute, value);
            mMotionLayout.rebuildScene();
            mMotionLayout.mInTransition = true;
            return true;
        }
        return false;
    }

    public Object getKeyframe(Object view, int type, int position) {
        if (mMotionLayout.mScene == null) {
            return null;
        }
        int target = ((View) view).getId();
        return mMotionLayout.mScene.getKeyFrame(mMotionLayout.getContext(), type, target, position);
    }

    public void setKeyframe(Object keyFrame, String tag, Object value) {
        if (keyFrame instanceof Key) {
            Key key = (Key) keyFrame;
            key.setValue(tag, value);
            mMotionLayout.rebuildScene();
            mMotionLayout.mInTransition = true;
        }
    }

    /**
     * Live setting of attributes on a view
     *
     * @param dpi dpi used by the application
     * @param constraintSetId ConstraintSet id
     * @param opaqueView the Android View we operate on, passed as an Object
     * @param opaqueAttributes the list of attributes (hash<string,string>) we pass to the view
     */
    public void setAttributes(int dpi, String constraintSetId, Object opaqueView, Object opaqueAttributes) {
        View view = (View) opaqueView;
        HashMap<String, String> attributes = (HashMap<String, String>) opaqueAttributes;

        int rscId = mMotionLayout.lookUpConstraintId(constraintSetId);
        ConstraintSet set = mMotionLayout.mScene.getConstraintSet(rscId);

        if (DEBUG) {
            Log.v(TAG, "constraintSetId  = " + constraintSetId + "  " + rscId);
        }

        if (set == null) {
            return;
        }

        set.clear(view.getId());

        SetDimensions(dpi, set, view, attributes, HORIZONTAL);
        SetDimensions(dpi, set, view, attributes, VERTICAL);

        Connect(dpi, set, view, attributes, START, START);
        Connect(dpi, set, view, attributes, START, END);
        Connect(dpi, set, view, attributes, END, END);
        Connect(dpi, set, view, attributes, END, START);
        Connect(dpi, set, view, attributes, LEFT, LEFT);
        Connect(dpi, set, view, attributes, LEFT, RIGHT);
        Connect(dpi, set, view, attributes, RIGHT, RIGHT);
        Connect(dpi, set, view, attributes, RIGHT, LEFT);
        Connect(dpi, set, view, attributes, TOP, TOP);
        Connect(dpi, set, view, attributes, TOP, BOTTOM);
        Connect(dpi, set, view, attributes, BOTTOM, TOP);
        Connect(dpi, set, view, attributes, BOTTOM, BOTTOM);
        Connect(dpi, set, view, attributes, BASELINE, BASELINE);

        SetBias(set, view, attributes, HORIZONTAL);
        SetBias(set, view, attributes, VERTICAL);

        SetAbsolutePositions(dpi, set, view, attributes);

        mMotionLayout.updateState(rscId, set);
        mMotionLayout.requestLayout();
    }

    public void dumpConstraintSet(String set) {
        if (mMotionLayout.mScene == null) {
            mMotionLayout.mScene = mSceneCache;
        }
        int setId = mMotionLayout.lookUpConstraintId(set);
        System.out.println(" dumping  "+set+" ("+setId+")");
        try {
            mMotionLayout.mScene.getConstraintSet(setId).dump(mMotionLayout.mScene);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
