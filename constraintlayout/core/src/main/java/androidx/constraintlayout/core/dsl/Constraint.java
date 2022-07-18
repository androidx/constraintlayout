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

import java.util.HashMap;
import java.util.Map;

/**
 * Provides the API for creating a Constraint Object for use in the Core
 * ConstraintLayout & MotionLayout system
 */
public class Constraint {

    private final String mId;

    public Constraint(String id) {
        mId = id;
    }

    public class VAnchor extends Anchor {
        final VSide mSide;

        VAnchor(VSide side) {
            super(Side.valueOf(side.name()));
            mSide = side;
        }
    }

    public class HAnchor extends Anchor {
        final HSide mSide;

        HAnchor(HSide side) {
            super(Side.valueOf(side.name()));
            mSide = side;
        }
    }

    public class Anchor {
        final Side mSide;
        Anchor mConnection = null;
        int mMargin;
        int mGoneMargin = Integer.MIN_VALUE;

        Anchor(Side side) {
            mSide = side;
        }

        public String getId() {
            return mId;
        }

        Constraint getParent() {
            return Constraint.this;
        }

        public void build(StringBuilder builder) {
            if (mConnection != null) {
                builder.append(mSide.toString().toLowerCase())
                        .append(":").append(this).append(",\n");
            }
        }

        @Override
        public String toString() {
            StringBuilder ret = new StringBuilder("[");

            if (mConnection != null) {
                ret.append("'").append(mConnection.getId()).append("',")
                        .append("'").append(mConnection.mSide.toString().toLowerCase()).append("'");
            }

            if (mMargin != 0) {
                ret.append(",").append(mMargin);
            }

            if (mGoneMargin != Integer.MIN_VALUE) {
                if ( mMargin == 0) {
                    ret.append(",0,").append(mGoneMargin);
                } else {
                    ret.append(",").append(mGoneMargin);
                }
            }

            ret.append("]");
            return ret.toString();
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

    enum VSide {
        TOP,
        BOTTOM,
        BASELINE
    }

    enum HSide {
        LEFT,
        RIGHT,
        START,
        END
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
    static Map<ChainMode, String> chainModeMap = new HashMap<>();
    static {
        chainModeMap.put(ChainMode.SPREAD, "spread");
        chainModeMap.put(ChainMode.SPREAD_INSIDE, "spread_inside");
        chainModeMap.put(ChainMode.PACKED, "packed");
    }

    String helperType = null;
    String helperJason = null;

    private HAnchor mLeft = new HAnchor(HSide.LEFT);
    private HAnchor mRight = new HAnchor(HSide.RIGHT);
    private VAnchor mTop = new VAnchor(VSide.TOP);
    private VAnchor mBottom = new VAnchor(VSide.BOTTOM);
    private HAnchor mStart = new HAnchor(HSide.START);
    private HAnchor mEnd = new HAnchor(HSide.END);
    private VAnchor mBaseline = new VAnchor(VSide.BASELINE);
    private int mWidth = UNSET;
    private int mHeight = UNSET;
    private float mHorizontalBias = Float.NaN;
    private float mVerticalBias = Float.NaN;
    private String mDimensionRatio = null;
    private String mCircleConstraint = null;
    private int mCircleRadius = Integer.MIN_VALUE;
    private float mCircleAngle = Float.NaN;
    private int mEditorAbsoluteX = Integer.MIN_VALUE;
    private int mEditorAbsoluteY = Integer.MIN_VALUE;
    private float mVerticalWeight = Float.NaN;
    private float mHorizontalWeight = Float.NaN;
    private ChainMode mHorizontalChainStyle = null;
    private ChainMode mVerticalChainStyle = null;
    private Behaviour mWidthDefault = null;
    private Behaviour mHeightDefault = null;
    private int mWidthMax = UNSET;
    private int mHeightMax = UNSET;
    private int mWidthMin = UNSET;
    private int mHeightMin = UNSET;
    private float mWidthPercent = Float.NaN;
    private float mHeightPercent = Float.NaN;
    private String[] mReferenceIds = null;
    private boolean mConstrainedWidth = false;
    private boolean mConstrainedHeight = false;

    /**
     * get left anchor
     *
     * @return left anchor
     */
    public HAnchor getLeft() {
        return mLeft;
    }

    /**
     * set left anchor
     *
     * @param left left anchor
     */
    public void setLeft(HAnchor left) {
        this.mLeft = left;
    }

    /**
     * get right anchor
     *
     * @return right anchor
     */
    public HAnchor getRight() {
        return mRight;
    }

    /**
     * set right anchor
     *
     * @param right right anchor
     */
    public void setRight(HAnchor right) {
        this.mRight = right;
    }

    /**
     * get top anchor
     *
     * @return top anchor
     */
    public VAnchor getTop() {
        return mTop;
    }

    /**
     * set top anchor
     *
     * @param top top anchor
     */
    public void setTop(VAnchor top) {
        this.mTop = top;
    }

    /**
     * get bottom anchor
     *
     * @return bottom anchor
     */
    public VAnchor getBottom() {
        return mBottom;
    }

    /**
     * set bottom anchor
     *
     * @param bottom bottom anchor
     */
    public void setBottom(VAnchor bottom) {
        this.mBottom = bottom;
    }

    /**
     * get start anchor
     *
     * @return start anchor
     */
    public HAnchor getStart() {
        return mStart;
    }

    /**
     * set start anchor
     *
     * @param start start anchor
     */
    public void setStart(HAnchor start) {
        this.mStart = start;
    }

    /**
     * get end anchor
     *
     * @return end anchor
     */
    public HAnchor getEnd() {
        return mEnd;
    }

    /**
     * set end anchor
     *
     * @param end end anchor
     */
    public void setEnd(HAnchor end) {
        this.mEnd = end;
    }

    /**
     * get baseline anchor
     *
     * @return baseline anchor
     */
    public VAnchor getBaseline() {
        return mBaseline;
    }

    /**
     * set baseline anchor
     *
     * @param baseline baseline anchor
     */
    public void setBaseline(VAnchor baseline) {
        this.mBaseline = baseline;
    }

    /**
     * get horizontalBias
     *
     * @return horizontalBias
     */
    public float getHorizontalBias() {
        return mHorizontalBias;
    }

    /**
     * set horizontalBias
     *
     * @param horizontalBias
     */
    public void setHorizontalBias(float horizontalBias) {
        this.mHorizontalBias = horizontalBias;
    }

    /**
     * get verticalBias
     *
     * @return verticalBias
     */
    public float getVerticalBias() {
        return mVerticalBias;
    }

    /**
     * set verticalBias
     *
     * @param verticalBias
     */
    public void setVerticalBias(float verticalBias) {
        this.mVerticalBias = verticalBias;
    }

    /**
     * get dimensionRatio
     *
     * @return dimensionRatio
     */
    public String getDimensionRatio() {
        return mDimensionRatio;
    }

    /**
     * set dimensionRatio
     *
     * @param dimensionRatio
     */
    public void setDimensionRatio(String dimensionRatio) {
        this.mDimensionRatio = dimensionRatio;
    }

    /**
     * get circleConstraint
     *
     * @return circleConstraint
     */
    public String getCircleConstraint() {
        return mCircleConstraint;
    }

    /**
     * set circleConstraint
     *
     * @param circleConstraint
     */
    public void setCircleConstraint(String circleConstraint) {
        this.mCircleConstraint = circleConstraint;
    }

    /**
     * get circleRadius
     *
     * @return circleRadius
     */
    public int getCircleRadius() {
        return mCircleRadius;
    }

    /**
     * set circleRadius
     *
     * @param circleRadius
     */
    public void setCircleRadius(int circleRadius) {
        this.mCircleRadius = circleRadius;
    }

    /**
     * get circleAngle
     *
     * @return circleAngle
     */
    public float getCircleAngle() {
        return mCircleAngle;
    }

    /**
     * set circleAngle
     *
     * @param circleAngle
     */
    public void setCircleAngle(float circleAngle) {
        this.mCircleAngle = circleAngle;
    }

    /**
     * get editorAbsoluteX
     * @return editorAbsoluteX
     */
    public int getEditorAbsoluteX() {
        return mEditorAbsoluteX;
    }

    /**
     * set editorAbsoluteX
     * @param editorAbsoluteX
     */
    public void setEditorAbsoluteX(int editorAbsoluteX) {
        mEditorAbsoluteX = editorAbsoluteX;
    }

    /**
     * get editorAbsoluteY
     * @return editorAbsoluteY
     */
    public int getEditorAbsoluteY() {
        return mEditorAbsoluteY;
    }

    /**
     * set editorAbsoluteY
     * @param editorAbsoluteY
     */
    public void setEditorAbsoluteY(int editorAbsoluteY) {
        mEditorAbsoluteY = editorAbsoluteY;
    }

    /**
     * get verticalWeight
     *
     * @return verticalWeight
     */
    public float getVerticalWeight() {
        return mVerticalWeight;
    }

    /**
     * set verticalWeight
     *
     * @param verticalWeight
     */
    public void setVerticalWeight(float verticalWeight) {
        this.mVerticalWeight = verticalWeight;
    }

    /**
     * get horizontalWeight
     *
     * @return horizontalWeight
     */
    public float getHorizontalWeight() {
        return mHorizontalWeight;
    }

    /**
     * set horizontalWeight
     *
     * @param horizontalWeight
     */
    public void setHorizontalWeight(float horizontalWeight) {
        this.mHorizontalWeight = horizontalWeight;
    }

    /**
     * get horizontalChainStyle
     *
     * @return horizontalChainStyle
     */
    public ChainMode getHorizontalChainStyle() {
        return mHorizontalChainStyle;
    }

    /**
     * set horizontalChainStyle
     *
     * @param horizontalChainStyle
     */
    public void setHorizontalChainStyle(
            ChainMode horizontalChainStyle) {
        this.mHorizontalChainStyle = horizontalChainStyle;
    }

    /**
     * get verticalChainStyle
     *
     * @return verticalChainStyle
     */
    public ChainMode getVerticalChainStyle() {
        return mVerticalChainStyle;
    }

    /**
     * set verticalChainStyle
     *
     * @param verticalChainStyle
     */
    public void setVerticalChainStyle(
            ChainMode verticalChainStyle) {
        this.mVerticalChainStyle = verticalChainStyle;
    }

    /**
     * get widthDefault
     *
     * @return widthDefault
     */
    public Behaviour getWidthDefault() {
        return mWidthDefault;
    }

    /**
     * set widthDefault
     *
     * @param widthDefault
     */
    public void setWidthDefault(Behaviour widthDefault) {
        this.mWidthDefault = widthDefault;
    }

    /**
     * get heightDefault
     *
     * @return heightDefault
     */
    public Behaviour getHeightDefault() {
        return mHeightDefault;
    }

    /**
     * set heightDefault
     *
     * @param heightDefault
     */
    public void setHeightDefault(Behaviour heightDefault) {
        this.mHeightDefault = heightDefault;
    }

    /**
     * get widthMax
     *
     * @return widthMax
     */
    public int getWidthMax() {
        return mWidthMax;
    }

    /**
     * set widthMax
     *
     * @param widthMax
     */
    public void setWidthMax(int widthMax) {
        this.mWidthMax = widthMax;
    }

    /**
     * get heightMax
     *
     * @return heightMax
     */
    public int getHeightMax() {
        return mHeightMax;
    }

    /**
     * set heightMax
     *
     * @param heightMax
     */
    public void setHeightMax(int heightMax) {
        this.mHeightMax = heightMax;
    }

    /**
     * get widthMin
     *
     * @return widthMin
     */
    public int getWidthMin() {
        return mWidthMin;
    }

    /**
     * set widthMin
     *
     * @param widthMin
     */
    public void setWidthMin(int widthMin) {
        this.mWidthMin = widthMin;
    }

    /**
     * get heightMin
     *
     * @return heightMin
     */
    public int getHeightMin() {
        return mHeightMin;
    }

    /**
     * set heightMin
     *
     * @param heightMin
     */
    public void setHeightMin(int heightMin) {
        this.mHeightMin = heightMin;
    }

    /**
     * get widthPercent
     *
     * @return
     */
    public float getWidthPercent() {
        return mWidthPercent;
    }

    /**
     * set widthPercent
     *
     * @param widthPercent
     */
    public void setWidthPercent(float widthPercent) {
        this.mWidthPercent = widthPercent;
    }

    /**
     * get heightPercent
     *
     * @return heightPercent
     */
    public float getHeightPercent() {
        return mHeightPercent;
    }

    /**
     * set heightPercent
     *
     * @param heightPercent
     */
    public void setHeightPercent(float heightPercent) {
        this.mHeightPercent = heightPercent;
    }

    /**
     * get referenceIds
     *
     * @return referenceIds
     */
    public String[] getReferenceIds() {
        return mReferenceIds;
    }

    /**
     * set referenceIds
     *
     * @param referenceIds
     */
    public void setReferenceIds(String[] referenceIds) {
        mReferenceIds = referenceIds;
    }

    /**
     * is constrainedWidth
     *
     * @return true if width constrained
     */
    public boolean isConstrainedWidth() {
        return mConstrainedWidth;
    }

    /**
     * set constrainedWidth
     *
     * @param constrainedWidth
     */
    public void setConstrainedWidth(boolean constrainedWidth) {
        this.mConstrainedWidth = constrainedWidth;
    }

    /**
     * is constrainedHeight
     *
     * @return true if height constrained
     */
    public boolean isConstrainedHeight() {
        return mConstrainedHeight;
    }

    /**
     * set constrainedHeight
     *
     * @param constrainedHeight
     */
    public void setConstrainedHeight(boolean constrainedHeight) {
        this.mConstrainedHeight = constrainedHeight;
    }

    /**
     * get width
     * @return width
     */
    public int getWidth() {
        return mWidth;
    }

    /**
     * set width
     *
     * @param width
     */
    public void setWidth(int width) {
        mWidth = width;
    }

    /**
     * get height
     * @return height
     */
    public int getHeight() {
        return mHeight;
    }

    /**
     * set height
     *
     * @param height
     */
    public void setHeight(int height) {
        mHeight = height;
    }

    /**
     * Connect anchor to Top
     *
     * @param anchor anchor to be connected
     */
    public void connectTop(VAnchor anchor) {
        connectTop(anchor, 0);
    }

    /**
     * Connect anchor to Left
     *
     * @param anchor anchor to be connected
     */
    public void connectLeft(HAnchor anchor) {
        connectLeft(anchor, 0);
    }

    /**
     * Connect anchor to Right
     *
     * @param anchor anchor to be connected
     */
    public void connectRight(HAnchor anchor) {
        connectRight(anchor, 0);
    }

    /**
     * Connect anchor to Start
     *
     * @param anchor anchor to be connected
     */
    public void connectStart(HAnchor anchor) {
        connectStart(anchor, 0);
    }

    /**
     * Connect anchor to End
     *
     * @param anchor anchor to be connected
     */
    public void connectEnd(HAnchor anchor) {
        connectEnd(anchor, 0);
    }

    /**
     * Connect anchor to Bottom
     *
     * @param anchor anchor to be connected
     */
    public void connectBottom(VAnchor anchor) {
        connectBottom(anchor, 0);
    }

    /**
     * Connect anchor to Baseline
     *
     * @param anchor anchor to be connected
     */
    public void connectBaseline(VAnchor anchor) {
        connectBaseline(anchor, 0);
    }

    /**
     * Connect anchor to Top
     *
     * @param anchor anchor to be connected
     * @param margin value of the margin
     */
    public void connectTop(VAnchor anchor, int margin) {
        connectTop(anchor, margin, Integer.MIN_VALUE);
    }

    /**
     * Connect anchor to Left
     *
     * @param anchor anchor to be connected
     * @param margin value of the margin
     */
    public void connectLeft(HAnchor anchor, int margin) {
        connectLeft(anchor, margin, Integer.MIN_VALUE);
    }

    /**
     * Connect anchor to Right
     *
     * @param anchor anchor to be connected
     * @param margin value of the margin
     */
    public void connectRight(HAnchor anchor, int margin) {
        connectRight(anchor, margin, Integer.MIN_VALUE);
    }

    /**
     * Connect anchor to Start
     *
     * @param anchor anchor to be connected
     * @param margin value of the margin
     */
    public void connectStart(HAnchor anchor, int margin) {
        connectStart(anchor, margin, Integer.MIN_VALUE);
    }

    /**
     * Connect anchor to End
     *
     * @param anchor anchor to be connected
     * @param margin value of the margin
     */
    public void connectEnd(HAnchor anchor, int margin) {
        connectEnd(anchor, margin, Integer.MIN_VALUE);
    }

    /**
     * Connect anchor to Bottom
     *
     * @param anchor anchor to be connected
     * @param margin value of the margin
     */
    public void connectBottom(VAnchor anchor, int margin) {
        connectBottom(anchor, margin, Integer.MIN_VALUE);
    }

    /**
     * Connect anchor to Baseline
     *
     * @param anchor anchor to be connected
     * @param margin value of the margin
     */
    public void connectBaseline(VAnchor anchor, int margin) {
        connectBaseline(anchor, margin, Integer.MIN_VALUE);
    }

    /**
     * Connect anchor to Top
     *
     * @param anchor anchor to be connected
     * @param margin value of the margin
     * @param goneMargin value of the goneMargin
     */
    public void connectTop(VAnchor anchor, int margin, int goneMargin) {
        mTop.mConnection = anchor;
        mTop.mMargin = margin;
        mTop.mGoneMargin = goneMargin;
    }

    /**
     * Connect anchor to Left
     *
     * @param anchor anchor to be connected
     * @param margin value of the margin
     * @param goneMargin value of the goneMargin
     */
    public void connectLeft(HAnchor anchor, int margin, int goneMargin) {
        mLeft.mConnection = anchor;
        mLeft.mMargin = margin;
        mLeft.mGoneMargin = goneMargin;
    }

    /**
     * Connect anchor to Right
     *
     * @param anchor anchor to be connected
     * @param margin value of the margin
     * @param goneMargin value of the goneMargin
     */
    public void connectRight(HAnchor anchor, int margin, int goneMargin) {
        mRight.mConnection = anchor;
        mRight.mMargin = margin;
        mRight.mGoneMargin = goneMargin;
    }

    /**
     * Connect anchor to Start
     *
     * @param anchor anchor to be connected
     * @param margin value of the margin
     * @param goneMargin value of the goneMargin
     */
    public void connectStart(HAnchor anchor, int margin, int goneMargin) {
        mStart.mConnection = anchor;
        mStart.mMargin = margin;
        mStart.mGoneMargin = goneMargin;
    }

    /**
     * Connect anchor to End
     *
     * @param anchor anchor to be connected
     * @param margin value of the margin
     * @param goneMargin value of the goneMargin
     */
    public void connectEnd(HAnchor anchor, int margin, int goneMargin) {
        mEnd.mConnection = anchor;
        mEnd.mMargin = margin;
        mEnd.mGoneMargin = goneMargin;
    }

    /**
     * Connect anchor to Bottom
     *
     * @param anchor anchor to be connected
     * @param margin value of the margin
     * @param goneMargin value of the goneMargin
     */
    public void connectBottom(VAnchor anchor, int margin, int goneMargin) {
        mBottom.mConnection = anchor;
        mBottom.mMargin = margin;
        mBottom.mGoneMargin = goneMargin;
    }

    /**
     * Connect anchor to Baseline
     *
     * @param anchor anchor to be connected
     * @param margin value of the margin
     * @param goneMargin value of the goneMargin
     */
    public void connectBaseline(VAnchor anchor, int margin, int goneMargin) {
        mBaseline.mConnection = anchor;
        mEnd.mMargin = margin;
        mEnd.mGoneMargin = goneMargin;
    }

    /**
     * convert a String array into a String representation
     *
     * @param str String array to be converted
     * @return a String representation of the input array.
     */
    public String convertStringArrayToString(String[] str) {
        StringBuilder ret = new StringBuilder("[");
        for (int i = 0; i < str.length; i++) {

            ret.append((i == 0) ? "'" : ",'");

            ret.append(str[i]);
            ret.append("'");

        }
        ret.append("]");
        return ret.toString();
    }

    protected void append(StringBuilder builder, String name, float value) {
        if (Float.isNaN(value)) {
            return;
        }
        builder.append(name);
        builder.append(":").append(value).append(",\n");

    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder(mId + ":{\n");
        mLeft.build(ret);
        mRight.build(ret);
        mTop.build(ret);
        mBottom.build(ret);
        mStart.build(ret);
        mEnd.build(ret);
        mBaseline.build(ret);

        if (mWidth != UNSET) {
            ret.append("width:").append(mWidth).append(",\n");
        }
        if (mHeight != UNSET) {
            ret.append("height:").append(mHeight).append(",\n");
        }
        append(ret, "horizontalBias", mHorizontalBias);
        append(ret, "verticalBias", mVerticalBias);
        if (mDimensionRatio != null) {
            ret.append("dimensionRatio:'").append(mDimensionRatio).append("',\n");
        }
        if (mCircleConstraint != null) {
            if (!Float.isNaN(mCircleAngle) || mCircleRadius != Integer.MIN_VALUE) {
                ret.append("circular:['").append(mCircleConstraint).append("'");
                if (!Float.isNaN(mCircleAngle)) {
                    ret.append(",").append(mCircleAngle);
                }
                if (mCircleRadius != Integer.MIN_VALUE) {
                    if (Float.isNaN(mCircleAngle)) {
                        ret.append(",0,").append(mCircleRadius);
                    } else {
                        ret.append(",").append(mCircleRadius);
                    }
                }
                ret.append("],\n");
            }
        }
        append(ret, "verticalWeight", mVerticalWeight);
        append(ret, "horizontalWeight", mHorizontalWeight);
        if (mHorizontalChainStyle != null) {
            ret.append("horizontalChainStyle:'").append(chainModeMap.get(mHorizontalChainStyle))
                    .append("',\n");
        }
        if (mVerticalChainStyle != null) {
            ret.append("verticalChainStyle:'").append(chainModeMap.get(mVerticalChainStyle))
                    .append("',\n");
        }
        if (mWidthDefault != null) {
            if (mWidthMax == UNSET && mWidthMin == UNSET) {
                ret.append("width:'").append(mWidthDefault.toString().toLowerCase())
                        .append("',\n");
            } else {
                ret.append("width:{value:'").append(mWidthDefault.toString().toLowerCase())
                        .append("'");
                if (mWidthMax != UNSET) {
                    ret.append(",max:").append(mWidthMax);
                }
                if (mWidthMin != UNSET) {
                    ret.append(",min:").append(mWidthMin);
                }
                ret.append("},\n");
            }
        }
        if (mHeightDefault != null) {
            if (mHeightMax == UNSET && mHeightMin == UNSET) {
                ret.append("height:'").append(mHeightDefault.toString().toLowerCase())
                        .append("',\n");
            } else {
                ret.append("height:{value:'").append(mHeightDefault.toString().toLowerCase())
                        .append("'");
                if (mHeightMax != UNSET) {
                    ret.append(",max:").append(mHeightMax);
                }
                if (mHeightMin != UNSET) {
                    ret.append(",min:").append(mHeightMin);
                }
                ret.append("},\n");
            }
        }
        if (!Double.isNaN(mWidthPercent)) {
            ret.append("width:'").append((int) mWidthPercent).append("%',\n");
        }
        if (!Double.isNaN(mHeightPercent)) {
            ret.append("height:'").append((int) mHeightPercent).append("%',\n");
        }
        if (mReferenceIds != null) {
            ret.append("mReferenceIds:")
                    .append(convertStringArrayToString(mReferenceIds))
                    .append(",\n");
        }
        if (mConstrainedWidth) {
            ret.append("constrainedWidth:").append(mConstrainedWidth).append(",\n");
        }
        if (mConstrainedHeight) {
            ret.append("constrainedHeight:").append(mConstrainedHeight).append(",\n");
        }

        ret.append("},\n");
        return ret.toString();
    }
}
