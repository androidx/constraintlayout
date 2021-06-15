package androidx.constraintlayout.core.motion;

import static org.junit.Assert.assertEquals;

import androidx.constraintlayout.core.motion.Motion;
import androidx.constraintlayout.core.motion.MotionWidget;
import androidx.constraintlayout.core.motion.utils.ArcCurveFit;
import androidx.constraintlayout.core.motion.utils.CurveFit;
import androidx.constraintlayout.core.motion.utils.KeyCache;

import org.junit.Test;

public class MotionControlTest {


    @Test
    public void testBasic() {
        assertEquals(2, 1 + 1);

    }

    @Test
    public void simpleLinear() {
        MotionWidget mw1 = new MotionWidget();
        MotionWidget mw2 = new MotionWidget();
        MotionWidget res = new MotionWidget();
        KeyCache cache = new KeyCache();
        mw1.setBounds(0, 0, 30, 40);
        mw2.setBounds(500, 600, 530, 640);


        Motion motion = new Motion(mw1);
        motion.setStart(mw1);
        motion.setEnd(mw2);
        motion.setup(1000, 1000, 1, 1000000);
        System.out.println("-------------------------------------------");
        for (float p = 0; p <= 1; p += 0.1) {
            motion.interpolate(res, p, 1000000 + (int)(p*100), cache);
            System.out.println(res);
        }

        motion.interpolate(res, 0.5f, 1000000 + 1000, cache);
        assertEquals((int)(0.5+( mw1.getLeft() + mw2.getLeft()) / 2), res.getLeft());
        assertEquals((int)(0.5+(mw1.getRight() + mw2.getRight()) / 2), res.getRight());
        assertEquals((int)(0.5+( mw1.getTop() + mw2.getTop()) / 2), res.getTop());
        assertEquals((int)(0.5+(mw1.getBottom() + mw2.getBottom()) / 2), res.getBottom());

    }

    @Test
    public void archMode() {
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
        for (float p = 0; p <= 1; p += 0.1) {
            motion.interpolate(res, p, 1000000 + (int)(p*100), cache);
            System.out.println(res);
        }

        motion.interpolate(res, 0.5f, 1000000 + 1000, cache);
        float left = (float) (1 - Math.sqrt(0.5));
        float top = (float) (Math.sqrt(0.5));
        assertEquals(left, res.getLeft(), 0.01);
        assertEquals(left + 3, res.getRight(), 0.01);
        assertEquals(top, res.getTop(), 0.01);
        assertEquals(top + 4, res.getBottom(), 0.01);

    }

    @Test
    public void archMode() {
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
        for (float p = 0; p <= 1; p += 0.1) {
            motion.interpolate(res, p, 1000000 + (int)(p*100), cache);
            System.out.println(res);
        }

        motion.interpolate(res, 0.5f, 1000000 + 1000, cache);
        float left = (float) (1 - Math.sqrt(0.5));
        float top = (float) (Math.sqrt(0.5));
        assertEquals(left, res.getLeft(), 0.01);
        assertEquals(left + 3, res.getRight(), 0.01);
        assertEquals(top, res.getTop(), 0.01);
        assertEquals(top + 4, res.getBottom(), 0.01);

    }
}
