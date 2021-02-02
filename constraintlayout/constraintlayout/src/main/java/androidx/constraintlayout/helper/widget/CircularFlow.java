package androidx.constraintlayout.helper.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.R;
import androidx.constraintlayout.widget.VirtualLayout;

public class CircularFlow extends VirtualLayout {
    private static final String TAG = "CircularFlow";
    ConstraintLayout mContainer;
    int mViewCenter;
    private static final int DEFAULT_RADIUS = 0;
    private static final float DEFAULT_ANGLE = 0F;

    public CircularFlow(Context context) {
        super(context);
    }

    public CircularFlow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircularFlow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void init(AttributeSet attrs) {
        super.init(attrs);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ConstraintLayout_Layout);
            final int N = a.getIndexCount();

            for (int i = 0; i < N; i++) {
                int attr = a.getIndex(i);
                if (attr == R.styleable.ConstraintLayout_Layout_circularflow_viewCenter) {
                    mViewCenter = a.getResourceId(attr, 0);
                }
            }
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mContainer = (ConstraintLayout) getParent();
        ConstraintSet c = new ConstraintSet();
        c.clone(mContainer);
        for (int i = 0; i <= mCount; i++) {
            int id = mIds[i];
            View view = mContainer.getViewById(id);

            if (view != null) {
                int radius = DEFAULT_RADIUS;
                float angle = DEFAULT_ANGLE;

                if (i < getRadius().length){
                    radius = getRadius()[i];
                } else {
                    Log.e("CircularFlow", "Added radius to view with id: " + mMap.get(view.getId()));
                }

                if (i < getAngles().length){
                    angle = getAngles()[i];
                } else {
                    Log.e("CircularFlow", "Added angle to view with id: " + mMap.get(view.getId()));
                }

                c.constrainCircle(view.getId(), mViewCenter, radius, angle);
            }
        }
        c.applyTo(mContainer);
        applyLayoutFeatures();
    }
}
