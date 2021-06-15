package androidx.constraintlayout.core.motion;

import static org.junit.Assert.assertEquals;

import androidx.constraintlayout.core.RandomLayoutTest;
import androidx.constraintlayout.core.motion.utils.ArcCurveFit;
import androidx.constraintlayout.core.motion.utils.CurveFit;
import androidx.constraintlayout.core.motion.utils.KeyFrameArray;

import org.junit.Test;

import java.util.Random;

public class MotionKeyFrameArray {
    @Test
    public void arcTest1() {
        KeyFrameArray<Integer> array = new KeyFrameArray<>();
        Random random = new Random();
        for (int i = 0; i < 32; i++) {
            assertEquals(i, array.size());
            array.append(i,i);
        }
        array.dump();
        for (int i = 0; i < array.size(); i++) {
            int k = array.keyAt(i);
            Integer val = array.valueAt(i);
            assertEquals(k, val.intValue());
        }
        array.clear();
        for (int i = 0; i < 32; i++) {
             int k = random.nextInt(100);
            System.out.println(k);
            array.append(k,k);
            array.dump();
        }

    }
}
