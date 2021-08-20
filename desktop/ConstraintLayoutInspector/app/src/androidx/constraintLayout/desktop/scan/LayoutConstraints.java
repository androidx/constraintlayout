package androidx.constraintLayout.desktop.scan;

import androidx.constraintLayout.desktop.constraintRendering.DrawConnection;
import androidx.constraintLayout.desktop.constraintRendering.draw.ColorSet;
import androidx.constraintLayout.desktop.ui.utils.Debug;
import androidx.constraintLayout.desktop.utils.ScenePicker;
import androidx.constraintlayout.core.parser.CLArray;
import androidx.constraintlayout.core.parser.CLElement;
import androidx.constraintlayout.core.parser.CLParsingException;

import java.awt.*;
import java.util.HashMap;

public class LayoutConstraints {
    String mName;
    double[] mBoundsXY = new double[8];
    private static int LEFT = 0;
    private static int RIGHT = 1;
    private static int TOP = 2;
    private static int BOTTOM = 3;
    private static int BASELINE = 4;
    private static int CENTER = 5;
    private static int CENTER_X = 6;
    private static int CENTER_Y = 7;

    String mTopTo;
    int mTopMargin = 0;
    int mTopToSide = -1;

    String mLeftTo;
    int mLeftMargin = 0;
    int mLeftToSide = -1;

    String mBottomTo;
    int mBottomMargin = 0;
    int mBottomToSide = -1;

    String mRightTo;
    int mRightMargin = 0;
    int mRightToSide = -1;

    String mBaselineTo;
    int mBaselineMargin = 0;
    int mBaselineToSide = -1;


    String mCenterTo;
    int mCenterMargin = 0;
    int mCenterToSide = -1;

    public void setValue(String name, CLElement v) {
       // Debug.log( mName+" "+ name);
        String to = getStr(v, 0);
        int toSide = sideInt(getStr(v, 1));
        int margin = Integer.parseInt(getStr(v, 2));
        //Debug.log(mName+" "+name+" "+to+" "+getStr(v, 1)+" "+margin);
        switch (name) {
            case "AnchorLEFT":
                mLeftMargin = margin;
                mLeftToSide = toSide;
                mLeftTo = to;
                break;
            case "AnchorTOP":
                mTopMargin = margin;
                mTopToSide = toSide;
                mTopTo = to;
                break;
            case "AnchorRIGHT":
                mRightMargin = margin;
                mRightToSide = toSide;
                mRightTo = to;
                break;
            case "AnchorBOTTOM":
                mBottomMargin = margin;
                mBottomToSide = toSide;
                mBottomTo = to;
                break;
            case "AnchorBASELINE":
                mBaselineMargin = margin;
                mBaselineToSide = toSide;
                mBaselineTo = to;
                break;
            case "AnchorCENTER":
                mCenterMargin = margin;
                mCenterToSide = toSide;
                mCenterTo = to;
                break;
            case "AnchorCENTER_X":
            case "AnchorCENTER_Y":

        }
    }

    private static int sideInt(String side) {
        switch (side) {
            case "LEFT":
                return LEFT;
            case "RIGHT":
                return RIGHT;
            case "TOP":
                return TOP;
            case "BOTTOM":
                return BOTTOM;
            case "BASELINE":
                return BASELINE;
            case "CENTER":
                return CENTER;
            case "CENTER_X":
                return CENTER_X;
            case "CENTER_Y":
                return CENTER_Y;
        }
        return -1;
    }

    private static String getStr(CLElement v, int index) {
        try {
            return ((CLArray) v).get(index).content();
        } catch (CLParsingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setBounds(double[] dstPts) {
        System.arraycopy(dstPts, 0, mBoundsXY, 0, 8);
    }

    public double[]  getBounds() {
        return mBoundsXY;
    }
    public void render(Graphics2D g, HashMap<String, LayoutConstraints> layoutMap, ScenePicker picker, LayoutConstraints root) {
        ColorSet set = new ColorSet();

        render(g, layoutMap, picker, set, LEFT, mLeftTo, mLeftMargin, mLeftToSide, root);
        render(g, layoutMap, picker, set, RIGHT, mRightTo, mRightMargin, mRightToSide, root);
        render(g, layoutMap, picker, set, TOP, mTopTo, mTopMargin, mTopToSide, root);
        render(g, layoutMap, picker, set, BOTTOM, mBottomTo, mBottomMargin, mBottomToSide, root);
    }

    private void render(Graphics2D g, HashMap<String, LayoutConstraints> layoutMap, ScenePicker picker,
                        ColorSet set,
                        int dir,
                        String to,
                        int margin,
                        int toSide,
                        LayoutConstraints root) {

        if (to == null) {
            return;
        }

        boolean parent = (to.equals("#PARENT"));
        LayoutConstraints toLayout = parent ? root : layoutMap.get(to);

        DrawConnection.draw(g, set,
                picker,
                null,
                DrawConnection.TYPE_NORMAL,
                mBoundsXY,
                dir,
                toLayout.mBoundsXY,
                toSide,
                parent ? DrawConnection.DEST_PARENT :DrawConnection.DEST_NORMAL ,
                margin,
                margin,
                false
        );
    }


}
