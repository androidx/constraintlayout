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

import static androidx.constraintlayout.core.dsl.Utils.convertStringArrayToString;

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

    Anchor left = new Anchor(Side.LEFT);
    Anchor right = new Anchor(Side.RIGHT);
    Anchor top = new Anchor(Side.TOP);
    Anchor bottom = new Anchor(Side.BOTTOM);
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

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        mHeight = height;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder(mId + ":{\n");

        if (helperType != null) {
            ret.append("helperType:'").append(helperType).append("',\n");
        }
        if (helperJason != null) {
            ret.append("helperJason:'").append(helperJason).append("',\n");
        }

        if (left.mConnection != null) {
            ret.append("left:").append(left).append(",\n");
        }
        if (right.mConnection != null) {
            ret.append("right:").append(right).append(",\n");
        }
        if (top.mConnection != null) {
            ret.append("top:").append(top).append(",\n");
        }
        if (bottom.mConnection != null) {
            ret.append("bottom:").append(bottom).append(",\n");
        }
        if (start.mConnection != null) {
            ret.append("start:").append(start).append(",\n");
        }
        if (end.mConnection != null) {
            ret.append("end:").append(end).append(",\n");
        }
        if (baseline.mConnection != null) {
            ret.append("baseline:").append(baseline).append(",\n");
        }


        if (mWidth != UNSET) {
            ret.append("width:").append(mWidth).append(",\n");
        }
        if (mHeight != UNSET) {
            ret.append("height:").append(mHeight).append(",\n");
        }
        if (!Float.isNaN(horizontalBias)) {
            ret.append("horizontalBias:").append(horizontalBias).append(",\n");
        }
        if (!Float.isNaN(verticalBias)) {
            ret.append("verticalBias:").append(verticalBias).append(",\n");
        }
        if (dimensionRatio != null) {
            ret.append("dimensionRatio:'").append(dimensionRatio).append("',\n");
        }
        if (circleConstraint != null) {
            if (!Float.isNaN(circleAngle) || circleRadius != Integer.MIN_VALUE) {
                ret.append("circular:['").append(circleConstraint).append("'");
                if (!Float.isNaN(circleAngle)) {
                    ret.append(",").append(circleAngle);
                }
                if (circleRadius != Integer.MIN_VALUE) {
                    if (Float.isNaN(circleAngle)) {
                        ret.append(",0,").append(circleRadius);
                    } else {
                        ret.append(",").append(circleRadius);
                    }
                }
                ret.append("],\n");
            }
        }
        if (editorAbsoluteX != Integer.MIN_VALUE) {
            ret.append("editorAbsoluteX:").append(editorAbsoluteX).append(",\n");
        }
        if (editorAbsoluteY != Integer.MIN_VALUE) {
            ret.append("editorAbsoluteY:").append(editorAbsoluteY).append(",\n");
        }
        if (!Float.isNaN(verticalWeight)) {
            ret.append("verticalWeight:").append(verticalWeight).append(",\n");
        }
        if (!Float.isNaN(horizontalWeight)) {
            ret.append("horizontalWeight:").append(horizontalWeight).append(",\n");
        }
        if (horizontalChainStyle != null) {
            ret.append("horizontalChainStyle:'").append(chainModeMap.get(horizontalChainStyle))
                    .append("',\n");
        }
        if (verticalChainStyle != null) {
            ret.append("verticalChainStyle:'").append(chainModeMap.get(verticalChainStyle))
                    .append("',\n");
        }
        if (widthDefault != null) {
            if (widthMax == UNSET && widthMin == UNSET) {
                ret.append("width:'").append(widthDefault.toString().toLowerCase())
                        .append("',\n");
            } else {
                ret.append("width:{value:'").append(widthDefault.toString().toLowerCase())
                        .append("'");
                if (widthMax != UNSET) {
                    ret.append(",max:").append(widthMax);
                }
                if (widthMin != UNSET) {
                    ret.append(",min:").append(widthMin);
                }
                ret.append("},\n");
            }
        }
        if (heightDefault != null) {
            if (heightMax == UNSET && heightMin == UNSET) {
                ret.append("height:'").append(heightDefault.toString().toLowerCase())
                        .append("',\n");
            } else {
                ret.append("height:{value:'").append(heightDefault.toString().toLowerCase())
                        .append("'");
                if (heightMax != UNSET) {
                    ret.append(",max:").append(heightMax);
                }
                if (heightMin != UNSET) {
                    ret.append(",min:").append(heightMin);
                }
                ret.append("},\n");
            }
        }
        if (!Double.isNaN(widthPercent)) {
            ret.append("width:'").append((int) widthPercent).append("%',\n");
        }
        if (!Double.isNaN(heightPercent)) {
            ret.append("height:'").append((int) heightPercent).append("%',\n");
        }
        if (mReferenceIds != null) {
            ret.append("mReferenceIds:")
                    .append(convertStringArrayToString(mReferenceIds))
                    .append(",\n");
        }
        if (constrainedWidth) {
            ret.append("constrainedWidth:").append(constrainedWidth).append(",\n");
        }
        if (constrainedHeight) {
            ret.append("constrainedHeight:").append(constrainedHeight).append(",\n");
        }

        ret.append("},\n");
        return ret.toString();
    }
}
