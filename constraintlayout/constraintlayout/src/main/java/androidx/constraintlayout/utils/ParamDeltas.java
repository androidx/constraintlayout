package androidx.constraintlayout.utils;

import android.os.Build;
import android.view.View;

import androidx.constraintlayout.core.ConstraintDelta;
import androidx.constraintlayout.core.motion.utils.TypedBundle;
import androidx.constraintlayout.core.motion.utils.TypedValues;
import androidx.constraintlayout.widget.ConstraintLayout;

public class ParamDeltas implements TypedValues {
    ConstraintLayout.LayoutParams params;

    public static String getLoc() {
        StackTraceElement s = new Throwable().getStackTrace()[1];
        return ".(" + s.getFileName() + ":" + s.getLineNumber() + ") " + s.getMethodName() + "()";
    }

    public void setParams(View view, TypedBundle bundle) {
        if (bundle == null) {
            return;
        }
        System.out.println(getLoc() + " LAYOUT_WIDTH value = " + view.getId());

        params = (ConstraintLayout.LayoutParams) view.getLayoutParams();
        bundle.applyDelta(this);
        view.setLayoutParams(params);
    }

    @Override
    public boolean setValue(int id, int value) {
        switch (id) {
            case ConstraintDelta.LAYOUT_WIDTH:
                params.width = value;
                System.out.println(getLoc() + " LAYOUT_WIDTH value = " + value);
                break;
            case ConstraintDelta.LAYOUT_HEIGHT:
                params.height = value;
                break;
            case ConstraintDelta.TOP_MARGIN:
                params.topMargin = value;
                break;
            case ConstraintDelta.LEFT_MARGIN:
                params.leftMargin = value;
                break;
            case ConstraintDelta.BOTTOM_MARGIN:
                params.bottomMargin = value;
                break;
            case ConstraintDelta.RIGHT_MARGIN:
                params.rightMargin = value;
                break;
            case ConstraintDelta.START_MARGIN:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    params.setMarginStart(value);
                } else {
                    params.leftMargin = value;
                }
                break;
            case ConstraintDelta.END_MARGIN:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    params.setMarginEnd(value);
                } else {
                    params.rightMargin = value;
                }
                break;
            case ConstraintDelta.BASELINE_MARGIN:
                params.baselineMargin = value;
                break;
            case ConstraintDelta.GONE_BOTTOM_MARGIN:
                params.goneBottomMargin = value;
                break;
            case ConstraintDelta.GONE_LEFT_MARGIN:
                params.goneLeftMargin = value;
                break;
            case ConstraintDelta.GONE_RIGHT_MARGIN:
                params.goneRightMargin = value;
                break;
            case ConstraintDelta.GONE_TOP_MARGIN:
                params.goneTopMargin = value;
                break;
            case ConstraintDelta.GUIDE_BEGIN:
                params.guideBegin = value;
                break;
            case ConstraintDelta.GUIDE_END:
                params.guideEnd = value;
                break;
            case ConstraintDelta.CIRCLE_RADIUS:
                params.circleRadius = value;
                break;
        }
        return false;
    }

    @Override
    public boolean setValue(int id, float value) {
        switch (id) {
            case ConstraintDelta.GUIDE_PERCENT:
                params.guidePercent = value;
                break;
            case ConstraintDelta.HORIZONTAL_BIAS:
                params.horizontalBias = value;
                break;
            case ConstraintDelta.VERTICAL_BIAS:
                params.verticalBias = value;
                break;
            case ConstraintDelta.HORIZONTAL_WEIGHT:
                params.horizontalWeight = value;
                break;
            case ConstraintDelta.VERTICAL_WEIGHT:
                params.verticalWeight = value;
                break;
            case ConstraintDelta.CIRCLE_ANGLE:
                params.circleAngle = value;
                break;
            case ConstraintDelta.WIDTH_PERCENT:
                params.matchConstraintPercentWidth = value;
                break;
            case ConstraintDelta.HEIGHT_PERCENT:
                params.matchConstraintPercentHeight = value;
                break;
        }

        return false;
    }

    @Override
    public boolean setValue(int id, String value) {
        return false;
    }

    @Override
    public boolean setValue(int id, boolean value) {
        return false;
    }

    @Override
    public int getId(String name) {
        return 0;
    }
}
