package android.support.constraint.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MyConstraintLayout extends ConstraintLayout {
    int mPosX,mPosY;
    public MyConstraintLayout(@NonNull Context context) {
        super(context);
        plugin();
    }

    public MyConstraintLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        plugin();
    }

    public MyConstraintLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        plugin();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void plugin() {
        setOnTouchListener((view, e)->{
            mPosX = (int)e.getX();
            mPosY = (int)e.getY();
            return true;
        });
        addValueModifier((width, height, id1, view, params) -> {
            if (id1 == R.id.drag) {
                params.leftMargin =mPosX-params.width/2;
                params.topMargin =mPosY-params.height/2;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    params.setMarginStart(mPosX);
                }
                return true;
            }
            return false;
        });
    }


}
