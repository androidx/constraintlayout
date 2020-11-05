package androidx.constraintlayout.validation;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.appcompat.widget.AppCompatTextView;

public class TestTextView extends AppCompatTextView {
    public TestTextView(Context context) {
        super(context);
        setup();
    }

    public TestTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public TestTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    private void setup() {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, 37);
    }
}
