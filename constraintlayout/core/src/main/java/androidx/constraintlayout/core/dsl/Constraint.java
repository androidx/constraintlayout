/*
 * Copyright (C) 2022 The Android Open Source Project
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

package androidx.constraintlayout.core.dsl;

/**
 * Provides the API for creating a Constraint Object for use in the Core
 * ConstraintLayout & MotionLayout system
 */
public class Constraint {

    private final String mId;

    public Constraint(String id) {
        mId = id;
    }

    public class Anchor {
        final Side mSide;
        Anchor mConnection = null;
        int mMargin;
        int mGoneMargin = Integer.MIN_VALUE;

        Anchor(Side side) {
            mSide = side;
        }

        Constraint getParent() {
            return Constraint.this;
        }
    }

    enum Behaviour {
        SPREAD,
        WRAP,
        PERCENT,
        RATIO,
        RESOLVED,
    }

    enum ChainMode {
        SPREAD,
        SPREAD_INSIDE,
        PACKED,
    }

    enum Side {
        LEFT,
        RIGHT,
        TOP,
        BOTTOM,
        START,
        END,
        BASELINE
    }

    static int UNSET = Integer.MIN_VALUE;
    String helperType = null;
    String helperJason = null;

    Anchor left = new Anchor(Side.LEFT);
    Anchor right = new Anchor(Side.RIGHT);
    Anchor top = new Anchor(Side.TOP);
    Anchor Bottom = new Anchor(Side.BOTTOM);
    Anchor start = new Anchor(Side.START);
    Anchor end = new Anchor(Side.END);
    Anchor baseline = new Anchor(Side.BASELINE);
    int mWidth = UNSET;
    int mHeight = UNSET;
    public float horizontalBias = Float.NaN;
    public float verticalBias = Float.NaN;
    public String dimensionRatio = null;
    public String circleConstraint = null;
    public int circleRadius = Integer.MIN_VALUE;
    public float circleAngle = Float.NaN;
    public int editorAbsoluteX = Integer.MIN_VALUE;
    public int editorAbsoluteY = Integer.MIN_VALUE;
    public float verticalWeight = Float.NaN;
    public float horizontalWeight = Float.NaN;
    public ChainMode horizontalChainStyle = null;
    public ChainMode verticalChainStyle = null;
    public Behaviour widthDefault = null;
    public Behaviour heightDefault = null;
    public int widthMax = UNSET;
    public int heightMax = UNSET;
    public int widthMin = UNSET;
    public int heightMin = UNSET;

    public float widthPercent = Float.NaN;
    public float heightPercent = Float.NaN;
    public String[] mReferenceIds = null;
    public boolean constrainedWidth = false;
    public boolean constrainedHeight = false;

    @Override
    public String toString() {
        return "Constraint:{}";
    }
}
