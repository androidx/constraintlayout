package androidx.constraintlayout.validation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.WindowInsets;
import androidx.constraintlayout.widget.ConstraintLayout;

public final class ConstraintLayoutWithInsetsFix extends ConstraintLayout {

    private OnApplyWindowInsetsListener onApplyWindowInsetsListener;

    public ConstraintLayoutWithInsetsFix(Context context) {
        super(context);
    }

    public ConstraintLayoutWithInsetsFix(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ConstraintLayoutWithInsetsFix(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {
        if (onApplyWindowInsetsListener != null) {
            insets = onApplyWindowInsetsListener.onApplyWindowInsets(this, insets);
        }

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).dispatchApplyWindowInsets(new WindowInsets(insets));
        }
        return insets;
    }

    @Override
    public void setOnApplyWindowInsetsListener(OnApplyWindowInsetsListener listener) {
        this.onApplyWindowInsetsListener = listener;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        requestApplyInsets();
    }
}