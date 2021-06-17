package androidx.constraintlayout.core.motion;

import static org.junit.Assert.assertEquals;

import androidx.constraintlayout.core.motion.Motion;
import androidx.constraintlayout.core.motion.MotionWidget;
import androidx.constraintlayout.core.motion.key.MotionKeyPosition;
import androidx.constraintlayout.core.motion.utils.ArcCurveFit;
import androidx.constraintlayout.core.motion.utils.KeyCache;

import org.junit.Test;

public class MotionKeyPositionTest {


    private static final boolean DEBUG = false;

    @Test
    public void testBasic() {
        assertEquals(2, 1 + 1);

    }


    @Test
    public void keyPosition1() {
        MotionWidget mw1 = new MotionWidget();
        MotionWidget mw2 = new MotionWidget();
        MotionWidget res = new MotionWidget();
        KeyCache cache = new KeyCache();
        mw1.setBounds(0, 0, 30, 40);
        mw2.setBounds(400, 400, 430, 440);
        // mw1.motion.mPathMotionArc = MotionWidget.A
        Motion motion = new Motion(mw1);
        motion.setPathMotionArc(ArcCurveFit.ARC_START_VERTICAL);
        motion.setStart(mw1);
        motion.setEnd(mw2);

        motion.setup(1000, 1000, 1, 1000000);
        if (DEBUG) {
            for (float p = 0; p <= 1; p += 0.1) {
                motion.interpolate(res, p, 1000000 + (int) (p * 100), cache);
                System.out.println(res);
            }
        }
        motion.interpolate(res, 0.5f, 1000000 + 1000, cache);
        int left =(int)( 0.5+400* (1 - Math.sqrt(0.5)));
        int top = (int) (0.5+400*(Math.sqrt(0.5)));
        assertEquals(left, res.getLeft());
        assertEquals(147  , res.getRight());
        assertEquals(top, res.getTop(), 0.01);
        assertEquals(top +40 , res.getBottom());

    }

    @Test
    public void keyPosition2() {
        MotionWidget mw1 = new MotionWidget();
        MotionWidget mw2 = new MotionWidget();
        MotionWidget res = new MotionWidget();
        KeyCache cache = new KeyCache();
        mw1.setBounds(0, 0, 30, 40);
        mw2.setBounds(400, 400, 430, 440);
        // mw1.motion.mPathMotionArc = MotionWidget.A
        Motion motion = new Motion(mw1);
        motion.setPathMotionArc(ArcCurveFit.ARC_START_HORIZONTAL);
        motion.setStart(mw1);
        motion.setEnd(mw2);
        motion.setup(1000, 1000, 2, 1000000);
        motion.interpolate(res, 0.5f, 1000000 + (int)(0.5*100), cache);
        System.out.println("0.5 "+ res  );
        if (DEBUG) {
            for (float p = 0; p <= 1; p += 0.01) {
                motion.interpolate(res, p, 1000000 + (int) (p * 100), cache);
                System.out.println(res + " ,     " + p);
            }
        }
        motion.interpolate(res, 0.5f, 1000000 + 1000, cache);

        assertEquals(283, res.getLeft()  );
        assertEquals(313 , res.getRight());
        assertEquals(117, res.getTop());
        assertEquals(157 , res.getBottom());

    }

    @Test
    public void keyPosition3() {
        MotionWidget mw1 = new MotionWidget();
        MotionWidget mw2 = new MotionWidget();
        MotionWidget res = new MotionWidget();
        KeyCache cache = new KeyCache();
        MotionKeyPosition keyPosition = new MotionKeyPosition();
        mw1.setBounds(0, 0, 30, 40);
        mw2.setBounds(400, 400, 460, 480);
        keyPosition.setFramePosition(50);
        keyPosition.setValue(MotionKeyPosition.PERCENT_X, 1);
        keyPosition.setValue(MotionKeyPosition.PERCENT_Y, 0.5);
        keyPosition.setValue(MotionKeyPosition.PERCENT_HEIGHT, 0.2);
        keyPosition.setValue(MotionKeyPosition.PERCENT_WIDTH, 1);
        // mw1.motion.mPathMotionArc = MotionWidget.A
        Motion motion = new Motion(mw1);
      //  motion.setPathMotionArc(ArcCurveFit.ARC_START_HORIZONTAL);
        motion.setStart(mw1);
        motion.setEnd(mw2);
        motion.addKey(keyPosition);
        motion.setup(1000, 1000, 2, 1000000);
        motion.interpolate(res, 0.5f, 1000000 + (int)(0.5*100), cache);
        System.out.println("0.5 "+ res  );
        if (true) {

            for (float p = 0; p <= 1; p += 0.01) {
                motion.interpolate(res, p, 1000000 + (int) (p * 100), cache);
                System.out.println(res + " ,     " + p);
            }
        }
        motion.interpolate(res, 0.5f, 1000000 + 1000, cache);

        assertEquals(283, res.getLeft()  );
        assertEquals(313 , res.getRight());
        assertEquals(117, res.getTop());
        assertEquals(157 , res.getBottom());

    }
    @Test
    public void keyPosition4() {
        MotionWidget mw1 = new MotionWidget();
        MotionWidget mw2 = new MotionWidget();
        MotionWidget res = new MotionWidget();
        KeyCache cache = new KeyCache();
        MotionKeyPosition keyPosition = new MotionKeyPosition();
        mw1.setBounds(0, 0, 30, 40);
        mw2.setBounds(400, 400, 460, 480);
        keyPosition.setFramePosition(20);
        keyPosition.setValue(MotionKeyPosition.PERCENT_X, 1);
        keyPosition.setValue(MotionKeyPosition.PERCENT_Y, 0.5);
        keyPosition.setValue(MotionKeyPosition.PERCENT_HEIGHT, 0.2);
        keyPosition.setValue(MotionKeyPosition.PERCENT_WIDTH, 1);
        // mw1.motion.mPathMotionArc = MotionWidget.A
        Motion motion = new Motion(mw1);
        //  motion.setPathMotionArc(ArcCurveFit.ARC_START_HORIZONTAL);
        motion.setStart(mw1);
        motion.setEnd(mw2);
        motion.addKey(keyPosition);
        motion.setup(1000, 1000, 2, 1000000);
        motion.interpolate(res, 0.5f, 1000000 + (int)(0.5*100), cache);
        System.out.println("0.5 "+ res  );
        if (true) {

            for (float p = 0; p <= 1; p += 0.01) {
                motion.interpolate(res, p, 1000000 + (int) (p * 100), cache);
                System.out.println(res + " ,     " + p);
            }
        }
        motion.interpolate(res, 0.5f, 1000000 + 1000, cache);

        assertEquals(283, res.getLeft()  );
        assertEquals(313 , res.getRight());
        assertEquals(117, res.getTop());
        assertEquals(157 , res.getBottom());

    }

    
}
