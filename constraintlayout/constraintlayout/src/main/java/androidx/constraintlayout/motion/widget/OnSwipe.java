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

package androidx.constraintlayout.motion.widget;

/**
 * Container for holding swipe infomation
 */
public class OnSwipe {
    private int mDragDirection = 0;
    private int mTouchAnchorSide = 0;
    private int mTouchAnchorId = MotionScene.UNSET;
    private int mTouchRegionId = MotionScene.UNSET;
    private int mLimitBoundsTo = MotionScene.UNSET;
    private int mOnTouchUp = 0;
    private int mRotationCenterId = MotionScene.UNSET;
    private float mMaxVelocity = 4;
    private float mMaxAcceleration = 1.2f;
    private boolean mMoveWhenScrollAtTop = true;
    private float mDragScale = 1f;
    private int mFlags = 0;
    private float mDragThreshold = 10;

    public static final int DRAG_UP = 0;
    public static final int DRAG_DOWN = 1;
    public static final int DRAG_LEFT = 2;
    public static final int DRAG_RIGHT = 3;
    public static final int DRAG_START = 4;
    public static final int DRAG_END = 5;
    public static final int DRAG_CLOCKWISE = 6;
    public static final int DRAG_ANTICLOCKWISE = 7;

    public static final int FLAG_DISABLE_POST_SCROLL = 1;
    public static final int FLAG_DISABLE_SCROLL = 2;

    public static final int SIDE_TOP = 0;
    public static final int SIDE_LEFT = 1;
    public static final int SIDE_RIGHT = 2;
    public static final int SIDE_BOTTOM = 3;
    public static final int SIDE_MIDDLE = 4;
    public static final int SIDE_START = 5;
    public static final int SIDE_END = 6;

    public static final int ON_UP_AUTOCOMPLETE = 0;
    public static final int ON_UP_AUTOCOMPLETE_TO_START = 1;
    public static final int ON_UP_AUTOCOMPLETE_TO_END = 2;
    public static final int ON_UP_STOP = 3;
    public static final int ON_UP_DECELERATE = 4;
    public static final int ON_UP_DECELERATE_AND_COMPLETE = 5;

    /**
     * The id of the view who's movement is matched to your drag
     * If not specified it will map to a linear movement across the width of the motionLayout
     *
     * @param side
     * @return
     */
    public OnSwipe setTouchAnchorId(int side) {
        mTouchAnchorId = side;
        return this;
    }

    public int getTouchAnchorId() {
        return mTouchAnchorId;
    }

    /**
     * This side of the view that matches the drag movement.
     * Only meaning full if the object changes size during the movement.
     * (rotation is not considered)
     *
     * @param side
     * @return
     */
    public OnSwipe setTouchAnchorSide(int side) {
        mTouchAnchorSide = side;
        return this;
    }

    public int getTouchAnchorSide() {
        return mTouchAnchorSide;
    }

    /**
     * The direction of the drag.
     *
     * @param dragDirection
     * @return
     */
    public OnSwipe setDragDirection(int dragDirection) {
        mDragDirection = dragDirection;
        return this;
    }

    public int getDragDirection() {
        return mDragDirection;
    }

    /**
     * The maximum velocity (Change in progress per second) animation can achive
     *
     * @param maxVelocity
     * @return
     */
    public OnSwipe setMaxVelocity(int maxVelocity) {
        mMaxVelocity = maxVelocity;
        return this;
    }

    public float getMaxVelocity() {
        return mMaxVelocity;
    }

    /**
     * The maximum acceleration and deceleration of the animation
     * (Change in Change in progress per second)
     * Faster makes the object seem lighter and quicker
     *
     * @param maxAcceleration
     * @return
     */
    public OnSwipe setMaxAcceleration(int maxAcceleration) {
        mMaxAcceleration = maxAcceleration;
        return this;
    }

    public float getMaxAcceleration() {
        return mMaxAcceleration;
    }

    /**
     * When collaborating with a NestedScrollView do you progress form 0-1 only
     * when the scroll view is at the top.
     *
     * @param moveWhenScrollAtTop
     * @return
     */
    public OnSwipe setMoveWhenScrollAtTop(boolean moveWhenScrollAtTop) {
        mMoveWhenScrollAtTop = moveWhenScrollAtTop;
        return this;
    }

    public boolean getMoveWhenScrollAtTop() {
        return mMoveWhenScrollAtTop;
    }

    /**
     * Normally 1 this can be tweaked to make the acceleration faster
     *
     * @param dragScale
     * @return
     */
    public OnSwipe setDragScale(int dragScale) {
        mDragScale = dragScale;
        return this;
    }

    public float getDragScale() {
        return mDragScale;
    }

    /**
     * This sets the threshold before the animation is kicked off.
     * It is important when have multi state animations the have some play before the
     * System decides which animation to jump on.
     *
     * @param dragThreshold
     * @return
     */
    public OnSwipe setDragThreshold(int dragThreshold) {
        mDragThreshold = dragThreshold;
        return this;
    }

    public float getDragThreshold() {
        return mDragThreshold;
    }

    /**
     * @param side
     * @return
     */
    public OnSwipe setTouchRegionId(int side) {
        mTouchRegionId = side;
        return this;
    }

    public int getTouchRegionId() {
        return mTouchRegionId;
    }

    /**
     * Configures what happens when the user releases on mouse up.
     * One of: ON_UP_AUTOCOMPLETE, ON_UP_AUTOCOMPLETE_TO_START, ON_UP_AUTOCOMPLETE_TO_END,
     * ON_UP_STOP, ON_UP_DECELERATE, ON_UP_DECELERATE_AND_COMPLETE
     *
     * @param mode default = ON_UP_AUTOCOMPLETE
     * @return
     */
    public OnSwipe setOnTouchUp(int mode) {
        mOnTouchUp = mode;
        return this;
    }

    public int getOnTouchUp() {
        return mOnTouchUp;
    }

    /**
     * Various flag to control behaviours of nested scroll
     * FLAG_DISABLE_POST_SCROLL = 1;
     * FLAG_DISABLE_SCROLL = 2;
     *
     * @param flags
     * @return
     */
    public OnSwipe setNestedScrollFlags(int flags) {
        mFlags = flags;
        return this;
    }

    public int getNestedScrollFlags() {
        return mFlags;
    }

    /**
     * Only allow touch actions to be initiated within this region
     *
     * @param id
     * @return
     */
    public OnSwipe setLimitBoundsTo(int id) {
        mLimitBoundsTo = id;
        return this;
    }

    public int getLimitBoundsTo() {
        return mLimitBoundsTo;
    }

    /**
     * The view to center the rotation about
     *
     * @param rotationCenterId
     * @return
     */
    public OnSwipe setRotateCenter(int rotationCenterId) {
        mRotationCenterId = rotationCenterId;
        return this;
    }

    public int getRotationCenterId() {
        return mRotationCenterId;
    }
}
