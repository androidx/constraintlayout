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

package androidx.constraintlayout.helper.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.constraintlayout.motion.widget.Debug;
import androidx.constraintlayout.motion.widget.Key;
import androidx.constraintlayout.motion.widget.KeyAttributes;
import androidx.constraintlayout.motion.widget.KeyPosition;
import androidx.constraintlayout.motion.widget.MotionController;
import androidx.constraintlayout.motion.widget.MotionHelper;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.R;

import java.util.HashMap;

/**
 * MotionHelper that automatically inserts keyframes for views moving in a given
 * direction, out of:
 * <ul>
 *     <li>NORTH</li>
 *     <li>SOUTH</li>
 *     <li>EAST</li>
 *     <li>WEST</li>
 * </ul>
 *
 * By default, will pick the opposite of the dominant direction (e.g. elements /not/ moving
 * in the dominant direction will have the keyframes inserted).
 */
public class MotionEffect extends MotionHelper {
    public static final String TAG = "FadeMove";

    public static final int AUTO = -1;
    public static final int NORTH = 0;
    public static final int SOUTH = 1;
    public static final int EAST = 2;
    public static final int WEST = 3;

    private float motionEffectAlpha = 0.1f;
    private int motionEffectStart = 49;
    private int motionEffectEnd = 50;
    private int motionEffectTranslationX = 0;
    private int motionEffectTranslationY = 0;
    private boolean motionEffectStrictMove = true;
    private static final int UNSET = -1;
    private int viewTransitionId = UNSET;

    private int fadeMove = AUTO;

    public MotionEffect(Context context) {
        super(context);
    }

    public MotionEffect(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MotionEffect(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MotionEffect);
            final int n = a.getIndexCount();
            for (int i = 0; i < n; i++) {
                int attr = a.getIndex(i);
                if (attr == R.styleable.MotionEffect_motionEffect_start) {
                    motionEffectStart = a.getInt(attr, motionEffectStart);
                    motionEffectStart = Math.max(Math.min(motionEffectStart, 99), 0);
                } else if (attr == R.styleable.MotionEffect_motionEffect_end) {
                    motionEffectEnd = a.getInt(attr, motionEffectEnd);
                    motionEffectEnd = Math.max(Math.min(motionEffectEnd, 99), 0);
                } else if (attr == R.styleable.MotionEffect_motionEffect_translationX) {
                    motionEffectTranslationX =
                            a.getDimensionPixelOffset(attr, motionEffectTranslationX);
                } else if (attr == R.styleable.MotionEffect_motionEffect_translationY) {
                    motionEffectTranslationY =
                            a.getDimensionPixelOffset(attr, motionEffectTranslationY);
                } else if (attr == R.styleable.MotionEffect_motionEffect_alpha) {
                    motionEffectAlpha = a.getFloat(attr, motionEffectAlpha);
                } else if (attr == R.styleable.MotionEffect_motionEffect_move) {
                    fadeMove = a.getInt(attr, fadeMove);
                } else if (attr == R.styleable.MotionEffect_motionEffect_strict) {
                    motionEffectStrictMove = a.getBoolean(attr, motionEffectStrictMove);
                } else if (attr == R.styleable.MotionEffect_motionEffect_viewTransition) {
                    viewTransitionId = a.getResourceId(attr, viewTransitionId);
                }
            }
            if (motionEffectStart == motionEffectEnd) {
                if (motionEffectStart > 0) {
                    motionEffectStart--;
                } else {
                    motionEffectEnd++;
                }
            }
            a.recycle();
        }
    }

    @Override
    public boolean isDecorator() {
        return true;
    }

    @Override
    public void onPreSetup(MotionLayout motionLayout,
                           HashMap<View,
            MotionController> controllerMap) {
        View[] views = getViews((ConstraintLayout) this.getParent());

        if (views == null) {
            Log.v(TAG, Debug.getLoc() + " views = null");
            return;
        }

        // Prepare a set of keyframes to be inserted

        KeyAttributes alpha1 = new KeyAttributes();
        KeyAttributes alpha2 = new KeyAttributes();
        alpha1.setValue(Key.ALPHA, motionEffectAlpha);
        alpha2.setValue(Key.ALPHA, motionEffectAlpha);
        alpha1.setFramePosition(motionEffectStart);
        alpha2.setFramePosition(motionEffectEnd);
        KeyPosition stick1 = new KeyPosition();
        stick1.setFramePosition(motionEffectStart);
        stick1.setType(KeyPosition.TYPE_CARTESIAN);
        stick1.setValue(KeyPosition.PERCENT_X, 0);
        stick1.setValue(KeyPosition.PERCENT_Y, 0);
        KeyPosition stick2 = new KeyPosition();
        stick2.setFramePosition(motionEffectEnd);
        stick2.setType(KeyPosition.TYPE_CARTESIAN);
        stick2.setValue(KeyPosition.PERCENT_X, 1);
        stick2.setValue(KeyPosition.PERCENT_Y, 1);

        KeyAttributes translationX1 = null;
        KeyAttributes translationX2 = null;
        if (motionEffectTranslationX > 0) {
            translationX1 = new KeyAttributes();
            translationX2 = new KeyAttributes();
            translationX1.setValue(Key.TRANSLATION_X, motionEffectTranslationX);
            translationX1.setFramePosition(motionEffectEnd);
            translationX2.setValue(Key.TRANSLATION_X, 0);
            translationX2.setFramePosition(motionEffectEnd - 1);
        }

        KeyAttributes translationY1 = null;
        KeyAttributes translationY2 = null;
        if (motionEffectTranslationY > 0) {
            translationY1 = new KeyAttributes();
            translationY2 = new KeyAttributes();
            translationY1.setValue(Key.TRANSLATION_Y, motionEffectTranslationY);
            translationY1.setFramePosition(motionEffectEnd);
            translationY2.setValue(Key.TRANSLATION_Y, 0);
            translationY2.setFramePosition(motionEffectEnd - 1);
        }

        int moveDirection = fadeMove;
        if (fadeMove == AUTO) {
            int[] direction = new int[4];
            // let's find out the general movement direction for all the referenced views
            for (int i = 0; i < views.length; i++) {
                MotionController mc = controllerMap.get(views[i]);
                if (mc == null) {
                    continue;
                }
                float x = mc.getFinalX() - mc.getStartX();
                float y = mc.getFinalY() - mc.getStartY();
                // look at the direction for this view, and increment the opposite direction
                // (as that's the one we will use to apply the fade)
                if (y < 0) {
                    direction[SOUTH]++;
                }
                if (y > 0) {
                    direction[NORTH]++;
                }
                if (x > 0) {
                    direction[WEST]++;
                }
                if (x < 0) {
                    direction[EAST]++;
                }
            }
            int max = direction[0];
            moveDirection = 0;
            for (int i = 1; i < 4; i++) {
                if (max < direction[i]) {
                    max = direction[i];
                    moveDirection = i;
                }
            }
        }

        for (int i = 0; i < views.length; i++) {
            MotionController mc = controllerMap.get(views[i]);
            if (mc == null) {
                continue;
            }
            float x = mc.getFinalX() - mc.getStartX();
            float y = mc.getFinalY() - mc.getStartY();
            boolean apply = true;

            // Any view that is moving in the given direction will have the fade applied
            // if move strict is true, also include views that are moving in diagonal, even
            // if they aren't moving in the opposite direction.
            if (moveDirection == NORTH) {
                if (y > 0 && (!motionEffectStrictMove || x == 0)) {
                    apply = false;
                }
            } else if (moveDirection == SOUTH) {
                if (y < 0 && (!motionEffectStrictMove || x == 0)) {
                    apply = false;
                }
            } else if (moveDirection == EAST) {
                if (x < 0 && (!motionEffectStrictMove || y == 0)) {
                    apply = false;
                }
            } else if (moveDirection == WEST) {
                if (x > 0 && (!motionEffectStrictMove || y == 0)) {
                    apply = false;
                }
            }

            if (apply) {
                if (viewTransitionId == UNSET) {
                    mc.addKey(alpha1);
                    mc.addKey(alpha2);
                    mc.addKey(stick1);
                    mc.addKey(stick2);
                    if (motionEffectTranslationX > 0) {
                        mc.addKey(translationX1);
                        mc.addKey(translationX2);
                    }
                    if (motionEffectTranslationY > 0) {
                        mc.addKey(translationY1);
                        mc.addKey(translationY2);
                    }
                } else {
                    motionLayout.applyViewTransition(viewTransitionId, mc);
                }
            }
        }
    }
}
