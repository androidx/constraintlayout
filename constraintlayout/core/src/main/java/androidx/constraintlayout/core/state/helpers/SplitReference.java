package androidx.constraintlayout.core.state.helpers;

import androidx.constraintlayout.core.state.HelperReference;
import androidx.constraintlayout.core.state.State;
import androidx.constraintlayout.core.utils.Split;
import androidx.constraintlayout.core.widgets.ConstraintWidget;
import androidx.constraintlayout.core.widgets.Flow;
import androidx.constraintlayout.core.widgets.HelperWidget;

public class SplitReference extends HelperReference {

    public SplitReference(State state, State.Helper type) {
        super(state, type);
    }

    protected Split mSplit;

    protected ConstraintWidget mLeft;

    protected ConstraintWidget mRight;

    protected int mOrientation;

    public ConstraintWidget getLeft() {
        return mLeft;
    }

    public void setLeft(ConstraintWidget mLeft) {
        this.mLeft = mLeft;
    }

    public ConstraintWidget getRight() {
        return mRight;
    }

    public void setRight(ConstraintWidget mRight) {
        this.mRight = mRight;
    }

    @Override
    public HelperWidget getHelperWidget() {
        if (mSplit == null) {
            mSplit = new Split();
        }
        return mSplit;
    }

    @Override
    public void setHelperWidget(HelperWidget widget) {
        if (widget instanceof Flow) {
            mSplit = (Split) widget;
        } else {
            mSplit = null;
        }
    }

    public int getOrientation() {
        return mOrientation;
    }

    public void setOrientation(int orientation) {
        mOrientation = orientation;

    }

    @Override
    public void apply() {
        if (mLeft != null) {
            mSplit.setFirst(mLeft);
        }

        if (mRight != null) {
            mSplit.setSecond(mRight);
        }

        mSplit.setOrientation(mOrientation);

        // General attributes of a widget
        applyBase();
    }
}
