package androidx.constraintlayout.core.utils;

import androidx.constraintlayout.core.LinearSystem;
import androidx.constraintlayout.core.widgets.ConstraintWidget;
import androidx.constraintlayout.core.widgets.ConstraintWidgetContainer;
import androidx.constraintlayout.core.widgets.VirtualLayout;

public class Split extends VirtualLayout {

    ConstraintWidget splitBar;

    ConstraintWidget first;
    ConstraintWidget second;
    ConstraintWidgetContainer mContainer;
    int orientation;

    public Split() {
        splitBar = new ConstraintWidget();
        first = new ConstraintWidget();
        second = new ConstraintWidget();
    }

    public void setFirst(ConstraintWidget a) {
        first = a;
    }

    public void setSecond(ConstraintWidget b) {
        second = b;
    }

    public void setSplitBar(ConstraintWidget splitBar) {
        this.splitBar = splitBar;
    }

    public ConstraintWidget getFirst() {
        return first;
    }

    public ConstraintWidget getSecond() {
        return second;
    }

    public ConstraintWidget getSplitBar() {
        return splitBar;
    }

    public ConstraintWidgetContainer getContainer() {
        return mContainer;
    }

    public void setContainer(ConstraintWidgetContainer container) {
        mContainer = container;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    @Override
    public void measure(int widthMode, int widthSize, int heightMode, int heightSize) {
        super.measure(widthMode, widthSize, heightMode, heightSize);
        for (int i = 0; i < mWidgetsCount; i++) {
            if (i == 0) {
                setFirst(mWidgets[i]);
            } else if (i == 1) {
                setSecond(mWidgets[i]);
            } else if (i == 2) {
                setSplitBar(mWidgets[i]);
            }
        }
        splitBar.stringId = String.valueOf(splitBar.hashCode());
        first.stringId = String.valueOf(first.hashCode());
        second.stringId = String.valueOf(second.hashCode());
        mContainer = (ConstraintWidgetContainer) getParent();
        mContainer.add(splitBar);
    }

    @Override
    public void addToSolver(LinearSystem system, boolean optimize) {
        this.mTop.connect(getParent().mTop, 0);
        this.mLeft.connect(getParent().mLeft, 0);
        this.mRight.connect(getParent().mRight, 0);
        this.mBottom.connect(getParent().mBottom, 0);

        splitBar.mTop.connect(mTop, 0);
        splitBar.mBottom.connect(mBottom, 0);
        splitBar.mLeft.connect(mLeft, 0);
        splitBar.mRight.connect(mRight, 0);

        if (orientation == 0) {
            first.mLeft.connect(mLeft, 0);
            first.mRight.connect(splitBar.mLeft, 0);
            first.mTop.connect(mTop, 0);
            first.mBottom.connect(mBottom, 0);

            second.mLeft.connect(splitBar.mRight, 0);
            second.mRight.connect(mRight, 0);
            second.mTop.connect(mTop, 0);
            second.mBottom.connect(mBottom, 0);
        } else {
            first.mLeft.connect(mLeft, 0);
            first.mRight.connect(mRight, 0);
            first.mTop.connect(mTop, 0);
            first.mBottom.connect(splitBar.mTop, 0);

            second.mLeft.connect(mLeft, 0);
            second.mRight.connect(mRight, 0);
            second.mTop.connect(splitBar.mBottom, 0);
            second.mBottom.connect(mBottom, 0);
        }
    }
}
