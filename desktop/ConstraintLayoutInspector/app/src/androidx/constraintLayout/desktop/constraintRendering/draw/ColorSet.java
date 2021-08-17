/*
 * Copyright (C) 2019 The Android Open Source Project
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

package androidx.constraintLayout.desktop.constraintRendering.draw;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;

/**
 * Holds a set of colors for drawing a scene
 */
public class ColorSet {

    public static Stroke
            sNormalStroke = new BasicStroke(1);

    public static Stroke
            sBoldStroke = new BasicStroke(2);

    public static Stroke
            sOutlineStroke = new BasicStroke(2);

    public static Stroke
            sDashedStroke = new BasicStroke(1, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_BEVEL, 0, new float[] { 2 }, 0);

    public static Stroke
            sLongDashedtroke = new BasicStroke(1, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_BEVEL, 0, new float[] { 4 }, 0);

    protected boolean mDrawBackground = true;
    protected boolean mDrawWidgetInfos = true;
    protected boolean mUseTooltips = true;
    protected boolean mAlwaysShowMargins = false;

    protected Color mBackground =  new Color(61, 158, 100);
    protected Color mComponentBackground =  new Color(32, 206, 250);
    protected Color mComponentHighlightedBackground =  new Color(174, 32, 250);
    protected Color mComponentObligatoryBackground =  new Color(250, 32, 228);
    protected Color mFrames =  new Color(149, 95, 88);
    protected Color mConstraints =  new Color(176, 203, 112);
    protected Color mSoftConstraintColor =  new Color(143, 147, 82);
    protected Color mMargins =  new Color(98, 149, 54);
    protected Color mText =  new Color(90, 156, 61);
    protected Color mSnapGuides =  new Color(86, 179, 171);
    protected Color mCreatedConstraints = new Color(85, 137, 153);
    protected Stroke mSoftConstraintStroke = sNormalStroke;

    protected Color mFakeUI  =  new Color(85, 101, 160);

    protected Color mSubduedText =  new Color(74, 64, 167);
    protected Color mSubduedBackground =  new Color(117, 80, 154);
    protected Color mSubduedFrames =  new Color(142, 75, 102);
    protected Color mSubduedConstraints =  new Color(59, 43, 75);

    protected Color mHighlightedBackground =  new Color(95, 63, 60);
    protected Color mHighlightedFrames =  new Color(29, 72, 78);
    protected Color mHighlightedSnapGuides =  new Color(85, 114, 56);
    protected Color mHighlightedConstraints =  new Color(153, 69, 115);

    protected Color mSelectedBackground =  new Color(94, 172, 132);
    protected Color mSelectedFrames =  new Color(163, 93, 151);
    protected Color mSelectedConstraints =  new Color(89, 149, 128);
    protected Color mSelectedText =  new Color(35, 103, 109);

    protected Color mInspectorStrokeColor =  new Color(78, 2, 68);
    protected Color mInspectorBackgroundColor =  new Color(59, 125, 85);
    protected Color mInspectorFillColor =  new Color(205, 89, 126);
    protected Color mInspectorTrackBackgroundColor =  new Color(134, 185, 224);
    protected Color mInspectorConstraintColor =  new Color(167, 246, 187);
    protected Color mInspectorTrackColor =  new Color(175, 193, 110);
    protected Color mInspectorHighlightsStrokeColor =  new Color(135, 40, 122);

    protected Color mAnchorCircle =  new Color(108, 108, 108);
    protected Color mAnchorCreationCircle =  new Color(114, 106, 87);
    protected Color mAnchorDisconnectionCircle =  new Color(51, 21, 108);
    protected Color mAnchorConnectionCircle =  new Color(58, 69, 46);

    protected Color mWidgetActionBackground =  new Color(86, 163, 157);
    protected Color mWidgetActionSelectedBackground =  new Color(105, 144, 186);
    protected Color mWidgetActionSelectedBorder =  new Color(141, 24, 167);
    protected Color mButtonBackground =  new Color(44, 90, 90);

    protected Color mSelectionColor =  new Color(35, 57, 125);

    protected Color mShadow = new Color(0, 0, 0, 50);
    protected Stroke mShadowStroke = new BasicStroke(3);

    protected Color mTooltipBackground =  new Color(250, 187, 32);

    protected Color mTootipText =  new Color(250, 187, 32);

    protected int mStyle;

    private Paint mBackgroundPaint;
    protected Color myUnconstrainedColor =  new Color(250, 187, 32);

    protected Color mDragReceiverFrames = new Color(135, 195, 77);
    protected Color mDragReceiverBackground = new Color(154, 221, 140, 60);
    protected Stroke mDragReceiverStroke = sBoldStroke;
    protected Color mDragOtherReceiversFrame = new Color(135, 195, 77, 102);
    protected Color mDragReceiverSiblingBackground = new Color(154, 221, 140, 26);
    protected Stroke mDragReceiverSiblingStroke = sNormalStroke;

//    protected Color mLassoSelectionBorder
//      = new JBColor(new Color(0xc01886f7, true), new Color(0xc09ccdff, true));
//    protected Color mLassoSelectionFill
//      = new JBColor(new Color(0x1a1886f7, true), new Color(0x1a9ccdff, true));

    public Stroke getOutlineStroke() { return sOutlineStroke; }

    public Paint getBackgroundPaint() {
        return mBackgroundPaint;
    }

    public void setBackgroundPaint(Paint backgroundPaint) {
        mBackgroundPaint = backgroundPaint;
    }

    public Color getAnchorCircle() { return mAnchorCircle; }

    public Color getAnchorCreationCircle() { return mAnchorCreationCircle; }

    public Color getAnchorDisconnectionCircle() { return mAnchorDisconnectionCircle; }

    public Color getAnchorConnectionCircle() { return mAnchorConnectionCircle; }

    public Color getFakeUI() { return mFakeUI; }

    public Color getSubduedText() { return mSubduedText; }

    public Color getSelectedFrames() { return mSelectedFrames; }

    public Color getDragReceiverFrames() { return mDragReceiverFrames; }

    public Color getDragReceiverBackground() { return mDragReceiverBackground; }

    public Stroke getDragReceiverStroke() {
        return mDragReceiverStroke;
    }

    public Color getDragOtherReceiversFrame() {
        return mDragOtherReceiversFrame;
    }

    public Color getDragReceiverSiblingBackground() { return mDragReceiverSiblingBackground; }

    public Stroke getDragReceiverSiblingStroke() {
        return mDragReceiverSiblingStroke;
    }

    public Color getBackground() { return mBackground; }

    public Color getComponentBackground() { return mComponentBackground; }

    public Color getButtonBackground() { return mButtonBackground; }

    public Color getComponentObligatoryBackground() { return mComponentObligatoryBackground; }

    public Color getComponentHighlightedBackground() { return mComponentHighlightedBackground; }

    public Color getFrames() { return mFrames; }

    public Color getConstraints() { return mConstraints; }

    public Color getSoftConstraintColor() { return mSoftConstraintColor; }

    public Color getMargins() { return mMargins; }

    public Color getText() { return mText; }

    public Color getHighlightedFrames() { return mHighlightedFrames; }

    public Color getSnapGuides() { return mSnapGuides; }

    public Color getHighlightedSnapGuides() { return mHighlightedSnapGuides; }

    public Color getSubduedBackground() {
        return mSubduedBackground;
    }

    public Color getSubduedConstraints() { return mSubduedConstraints; }

    public Color getSubduedFrames() {
        return mSubduedFrames;
    }

    public Color getHighlightedBackground() { return mHighlightedBackground; }

    public Color getSelectedBackground() { return mSelectedBackground; }

    public Color getSelectedConstraints() { return mSelectedConstraints; }

    public Color getInspectorBackgroundColor() { return mInspectorBackgroundColor; }

    public Color getInspectorStrokeColor() { return mInspectorStrokeColor; }

    public Color getInspectorFillColor() { return mInspectorFillColor; }

    public Color getInspectorTrackBackgroundColor() { return mInspectorTrackBackgroundColor; }

    public Color getInspectorTrackColor() { return mInspectorTrackColor; }

    public Color getInspectorHighlightsStrokeColor() { return mInspectorHighlightsStrokeColor; }

    public Color getInspectorConstraintColor() { return mInspectorConstraintColor; }

    public Color getHighlightedConstraints() { return mHighlightedConstraints; }

    public void setHighlightedConstraints(Color highlightedConstraints) {
        mHighlightedConstraints = highlightedConstraints;
    }

    public boolean drawWidgetInfos() {
        return mDrawWidgetInfos;
    }

    public void setDrawWidgetInfos(boolean drawWidgetInfos) {
        mDrawWidgetInfos = drawWidgetInfos;
    }

    public boolean drawBackground() {
        return mDrawBackground;
    }

    public Color getSelectedText() {
        return mSelectedText;
    }

    public Color getShadow() {
        return mShadow;
    }

    public Stroke getShadowStroke() {
        return mShadowStroke;
    }

    public int getStyle() {
        return mStyle;
    }

    public boolean useTooltips() { return mUseTooltips; }

    public void setUseTooltips(boolean value) { mUseTooltips = value; }

    public boolean alwaysShowMargins() { return mAlwaysShowMargins; }

    public void setAlwaysShowMargins(boolean value) { mAlwaysShowMargins = value; }

    public Color getTooltipBackground() {
        return mTooltipBackground;
    }

    public Color getTooltipText() {
        return mTootipText;
    }

    public Color getCreatedConstraints() {
        return mCreatedConstraints;
    }

    public Color getSelectionColor() {
        return mSelectionColor;
    }

    public Color getWidgetActionBackground() { return mWidgetActionBackground; }

    public Color getWidgetActionSelectedBackground() { return mWidgetActionSelectedBackground; }

    public Color getWidgetActionSelectedBorder() { return mWidgetActionSelectedBorder; }

    public Stroke getSoftConstraintStroke() {
        return mSoftConstraintStroke;
    }

    public Color getUnconstrainedColor() {
        return myUnconstrainedColor;
    }


}
