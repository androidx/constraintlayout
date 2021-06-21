package androidx.constraintlayout.core.motion;

import static org.junit.Assert.assertEquals;

import androidx.constraintlayout.core.motion.Motion;
import androidx.constraintlayout.core.motion.MotionWidget;
import androidx.constraintlayout.core.motion.utils.ArcCurveFit;
import androidx.constraintlayout.core.motion.utils.KeyCache;

import org.junit.Test;

public class MotionCustomAttributesTest {
    private static final boolean DEBUG = true;

    @Test
    public void testBasic() {
        assertEquals(2, 1 + 1);

    }

    @Test
    public void basicAttributes() {
        MotionWidget mw1 = new MotionWidget();
        MotionWidget mw2 = new MotionWidget();
        MotionWidget res = new MotionWidget();
        KeyCache cache = new KeyCache();
        mw1.setBounds(0, 0, 30, 40);
        mw2.setBounds(400, 400, 430, 440);
        mw1.setRotationZ(0);
        mw2.setRotationZ(360);
        // mw1.motion.mPathMotionArc = MotionWidget.A
        Motion motion = new Motion(mw1);
        motion.setPathMotionArc(ArcCurveFit.ARC_START_VERTICAL);
        motion.setStart(mw1);
        motion.setEnd(mw2);

        motion.setup(1000, 1000, 1, 1000000);
        if (DEBUG) {
            for (int p = 0; p <= 10; p ++) {
                motion.interpolate(res, p*0.1f, 1000000 + (int) (p * 100), cache);
                System.out.println(p*0.1f+" "+res.getRotationZ());
            }
        }
        motion.interpolate(res, 0.5f, 1000000 + 1000, cache);
        assertEquals(180, res.getRotationZ(),0.001);
    }

    class Scene {
        MotionWidget mw1 = new MotionWidget();
        MotionWidget mw2 = new MotionWidget();
        MotionWidget res = new MotionWidget();
        KeyCache cache = new KeyCache();
        Motion motion;
        Scene(){
            motion = new Motion(mw1);
            mw1.setBounds(0, 0, 30, 40);
            mw2.setBounds(400, 400, 430, 440);
            motion.setPathMotionArc(ArcCurveFit.ARC_START_VERTICAL);
        }

        public void setup() {
            motion.setStart(mw1);
            motion.setEnd(mw2);
            motion.setup(1000, 1000, 1, 1000000);
        }

        void sample(Runnable r) {
            for (int p = 0; p <= 10; p ++) {
                motion.interpolate(res, p * 0.1f, 1000000 + (int) (p * 100), cache);
                r.run();
            }
        }
    }

    @Test
    public void basicAttributes2() {
        Scene s = new Scene();
        s.mw1.setRotationZ(0);
        s.mw2.setRotationZ(360);
        s.setup();

        if (DEBUG) {
            s.sample(()-> {System.out.println(s.res.getRotationZ());});
        }
        s.motion.interpolate(s.res, 0.5f, 1000000 + 1000, s.cache);
        assertEquals(180, s.res.getRotationZ(),0.001);
    }


    @Test
    public void basicAttributesRotateX() {
        Scene s = new Scene();
        s.mw1.setRotationX(-10);
        s.mw2.setRotationX(10);
        s.setup();

        if (DEBUG) {
            s.sample(()-> {System.out.println(s.res.getRotationZ());});
        }
        s.motion.interpolate(s.res, 0.5f, 1000000 + 1000, s.cache);
        assertEquals(0, s.res.getRotationX(),0.001);
    }

    @Test
    public void basicAttributesRotateY() {
        Scene s = new Scene();
        s.mw1.setRotationY(-10);
        s.mw2.setRotationY(10);
        s.setup();

        if (DEBUG) {
            s.sample(()-> {System.out.println(s.res.getRotationY());});
        }
        s.motion.interpolate(s.res, 0.5f, 1000000 + 1000, s.cache);
        assertEquals(0, s.res.getRotationY(),0.001);
    }

    @Test
    public void basicAttributesTranslateX() {
        Scene s = new Scene();
        s.mw1.setTranslationX(-10);
        s.mw2.setTranslationX(40);
        s.setup();

        if (DEBUG) {
            s.sample(()-> {System.out.println(s.res.getTranslationX());});
        }
        s.motion.interpolate(s.res, 0.5f, 1000000 + 1000, s.cache);
        assertEquals(15.0, s.res.getTranslationX(),0.001);
    }

    @Test
    public void basicAttributesTranslateY() {
        Scene s = new Scene();
        s.mw1.setTranslationY(-10);
        s.mw2.setTranslationY(40);
        s.setup();

        if (DEBUG) {
            s.sample(()-> {System.out.println(s.res.getTranslationY());});
        }
        s.motion.interpolate(s.res, 0.5f, 1000000 + 1000, s.cache);
        assertEquals(15.0, s.res.getTranslationY(),0.001);
    }

    @Test
    public void basicAttributesTranslateZ() {
        Scene s = new Scene();
        s.mw1.setTranslationZ(-10);
        s.mw2.setTranslationZ(40);
        s.setup();

        if (DEBUG) {
            s.sample(()-> {System.out.println(s.res.getTranslationZ());});
        }
        s.motion.interpolate(s.res, 0.5f, 1000000 + 1000, s.cache);
        assertEquals(15.0, s.res.getTranslationZ(),0.001);
    }

    @Test
    public void basicAttributesScaleX() {
        Scene s = new Scene();
        s.mw1.setScaleX(-10);
        s.mw2.setScaleX(40);
        s.setup();

        if (DEBUG) {
            s.sample(()-> {System.out.println(s.res.getScaleX());});
        }
        s.motion.interpolate(s.res, 0.5f, 1000000 + 1000, s.cache);
        assertEquals(15.0, s.res.getScaleX(),0.001);
    }

    @Test
    public void basicAttributesScaleY() {
        Scene s = new Scene();
        s.mw1.setScaleY(-10);
        s.mw2.setScaleY(40);
        s.setup();

        if (DEBUG) {
            s.sample(()-> {System.out.println(s.res.getScaleY());});
        }
        s.motion.interpolate(s.res, 0.5f, 1000000 + 1000, s.cache);
        assertEquals(15.0, s.res.getScaleY(),0.001);
    }

    @Test
    public void basicAttributesPivotX() {
        Scene s = new Scene();
        s.mw1.setPivotX(-10);
        s.mw2.setPivotX(40);
        s.setup();

        if (DEBUG) {
            s.sample(()-> {System.out.println(s.res.getPivotX());});
        }
        s.motion.interpolate(s.res, 0.5f, 1000000 + 1000, s.cache);
        assertEquals(15.0, s.res.getPivotX(),0.001);
    }

    @Test
    public void basicAttributesPivotY() {
        Scene s = new Scene();
        s.mw1.setPivotY(-10);
        s.mw2.setPivotY(40);
        s.setup();

        if (DEBUG) {
            s.sample(()-> {System.out.println(s.res.getPivotY());});
        }
        s.motion.interpolate(s.res, 0.5f, 1000000 + 1000, s.cache);
        assertEquals(15.0, s.res.getPivotY(),0.001);
    }

}
