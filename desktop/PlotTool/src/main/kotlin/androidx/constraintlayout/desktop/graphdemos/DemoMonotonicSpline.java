package androidx.constraintlayout.desktop.graphdemos;

import androidx.constraintlayout.desktop.graph.GraphEngine;
import androidx.constraintlayout.desktop.graph.Utils;
import androidx.constraintlayout.desktop.graphdemos.utils.Easing;
import androidx.constraintlayout.desktop.graphdemos.utils.MonotonicCurveFit;

import javax.swing.*;
import java.awt.*;

public class DemoMonotonicSpline {
    public static void main(String[] args) {
        JFrame frame = Utils.smartFrame("Monotonic Spline");
        GraphEngine p = GraphEngine.setupFrameWidthControls(frame, "wave");
        var min = 0;
        var max = 1;
        double[]tPts = {0,0.25,0.5,0.75,1};
        double[][]xy = { {0,0},{1,0},{1,1},{0,1},{0,0}};
//        double[][]xy = { {0,1},{0.5,0.5},{0.5,0.6},{1,0}};
        MonotonicCurveFit curve = new MonotonicCurveFit(tPts,xy);
//        p.addFunction2("x", min, max, new Color(0xB61B73), (x)->{return curve.getPos(x,0);});
        p.addFunction2("y", min, max, new Color(0x1BB6B1), (x)->{return curve.getPos(x,1);});
        p.addFunction2("y", min, max, new Color(0x1BB6B1), (x)->{return curve.getPos(x,1);});
        p.addVelocity("y", min, max, new Color(0x1BB6B1), (x)->{return curve.getPos(x,1);});
        p.addFunction2d("xy", min, max, new Color(0xAFA915),
                (t)->{return curve.getPos(t,0);},
                    (t)->{return curve.getPos(t,1);});
        double[] x = new double[60];
        double[] y = new double[x.length];
        for (int i = 0; i < y.length; i++) {
            var t = min + i * (max-min) / (y.length-1.);
            x[i] = curve.getPos(t,0);
            y[i] = curve.getPos(t,1);
        }
        p.addPoints("points", Color.GRAY, x, y);
        frame.setVisible(true);
        System.out.println(" "+Integer.toHexString(Float.floatToRawIntBits(Float.NaN)));
        System.out.println(" "+Float.intBitsToFloat( (Float.floatToRawIntBits(-Float.NaN)|0x80000000)));
    }
}
