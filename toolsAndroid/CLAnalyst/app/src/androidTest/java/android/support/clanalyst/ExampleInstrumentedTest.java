package android.support.clanalyst;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Test
    public void testMainActivity() {
        // Launch the MainActivity
        final ActivityScenario<MainActivity> activityScenario =
                ActivityScenario.launch(MainActivity.class);
        activityScenario.onActivity(activity -> {
         ConstraintLayout cl = MainActivity.findConstraintLayout(activity);
         assertNotNull(cl);

         cl.postDelayed(()->{
             String expect = "{\n" +
                     "Widgets:{\n" +
                     "  parent: { type: 'ConstraintLayout' , width: 'MATCH_PARENT', height: 'MATCH_PARENT', bounds: [0, 0, 1080, 1934]},\n" +
                     "  textView: { type: 'Text', label: 'Hello World!', bounds: [435, 918, 645, 1017]},\n" +
                     "  button: { type: 'Button', label: 'Button, bounds: [58, 63, 321, 195]},\n" +
                     "  button2: { type: 'Button', label: 'Button, bounds: [376, 272, 639, 404]},\n" +
                     "  button3: { type: 'Button', label: 'Button, bounds: [54, 640, 310, 808]},\n" +
                     "  button4: { type: 'Button', label: 'Button, bounds: [349, 676, 612, 808]},\n" +
                     "  button5: { type: 'Button', label: 'Button, bounds: [702, 880, 965, 1009]},\n" +
                     "  guideline2: { type: 'Guideline', bounds: [833, 0, 833, 0]},\n" +
                     "  button6: { type: 'Button', label: 'Apsect, bounds: [435, 1017, 965, 1546]},\n" +
                     "},\n" +
                     "  ConstraintSet:{\n" +
                     "cset:{\n" +
                     "  button:{\n" +
                     "    height: 'wrap',\n" +
                     "    width: 'wrap',\n" +
                     "    top:['parent', 'top', 63],\n" +
                     "    start:['parent', 'start', 58],\n" +
                     "  },\n" +
                     "  button2:{\n" +
                     "    height: 'wrap',\n" +
                     "    width: 'wrap',\n" +
                     "    top:['button', 'bottom', 77],\n" +
                     "    start:['button', 'end', 55],\n" +
                     "  },\n" +
                     "  textView:{\n" +
                     "    height: 99,\n" +
                     "    width: 'wrap',\n" +
                     "    top:['parent', 'top'],\n" +
                     "    bottom:['parent', 'bottom'],\n" +
                     "    start:['parent', 'start'],\n" +
                     "    end:['parent', 'end'],\n" +
                     "  },\n" +
                     "  button3:{\n" +
                     "    height: 168,\n" +
                     "    width: 256,\n" +
                     "    bottom:['textView', 'top', 110],\n" +
                     "    end:['button2', 'start', 66],\n" +
                     "  },\n" +
                     "  guideline2:{\n" +
                     "    height: 'wrap',\n" +
                     "    width: 'wrap',\n" +
                     "    orientation: 1,\n" +
                     "    guideEnd: 247,\n" +
                     "  },\n" +
                     "  button4:{\n" +
                     "    height: 'wrap',\n" +
                     "    width: 'wrap',\n" +
                     "    bottom:['button3', 'bottom'],\n" +
                     "    end:['textView', 'end', 33],\n" +
                     "  },\n" +
                     "  button5:{\n" +
                     "    height: 129,\n" +
                     "    width: 'wrap',\n" +
                     "    baseline:['textView', 'baseline', -1],\n" +
                     "    start:['guideline2', 'start'],\n" +
                     "    end:['guideline2', 'start'],\n" +
                     "  },\n" +
                     "  button6:{\n" +
                     "    height: {value:'spread', max: 0, min: 0},\n" +
                     "    width: {value:'spread', max: 0, min: 0},\n" +
                     "    top:['textView', 'bottom'],\n" +
                     "    start:['textView', 'start'],\n" +
                     "    end:['button5', 'end'],\n" +
                     "    dimensionRatio: 'w,1:1',\n" +
                     "  },\n" +
                     "},\n" +
                     "\n" +
                     "  }\n" +
                     "}\n";
             String ans = DumpCL.asString(cl);
             // DumpCL.toFile(cl, "test1"); this outputs to /storage/emulated/0/Download/test1.json5

             System.out.println("----------------\n"+ans+"\n--------------\n");
             assertEquals(expect, ans);

         },200);
        });
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        assertNotNull("Good");
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("android.support.clanalyst", appContext.getPackageName());
        LayoutInflater inflater = (LayoutInflater)appContext.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_main,null);
        MainActivity activity = new MainActivity();


    }
}