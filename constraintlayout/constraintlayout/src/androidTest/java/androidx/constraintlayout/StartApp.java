package androidx.constraintlayout;

import android.content.Context;

import androidx.test.filters.SmallTest;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

@SmallTest
public class StartApp {

    @Test
    public void useAppContext() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("androidx.constraintlayout.widget.test", appContext.getPackageName());
    }
}
