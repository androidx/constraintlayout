package androidx.constraintlayout.desktop.graphdemos;

import androidx.constraintlayout.desktop.graph.GraphEngine;
import androidx.constraintlayout.desktop.graph.Utils;

import javax.swing.*;
import java.awt.*;

public class Simple {
    public static void main(String[] args) {
        JFrame frame = Utils.smartFrame("Simple Plot");
        GraphEngine p = GraphEngine.setupFrameWidthControls(frame, "wave");
        var min = - Math.PI;
        var max =   Math.PI;
        p.addFunction("sin", min, max, new Color(0x213FA8), x -> Math.sin(x));
        p.addVelocity("dSin/dt", min, max, new Color(0x17B750), x ->  Math.sin(2*x));
        p.addFunction2("cos", min, max, new Color(0xB61B73), Math::cos);
        p.addFunction2d("circle", min, max, new Color(0xAFA915), Math::cos, Math::sin);
        double[] x = new double[20];
        double[] y = new double[x.length];
        for (int i = 0; i < y.length; i++) {
            var t = min + i * (max-min) / y.length;
            x[i] = Math.sin(t);
            y[i] = Math.cos(t);
        }
        p.addPoints("points", Color.GRAY, x, y);
        frame.setVisible(true);
        System.out.println(" "+Integer.toHexString(Float.floatToRawIntBits(Float.NaN)));
        System.out.println(" "+Float.intBitsToFloat( (Float.floatToRawIntBits(-Float.NaN)|0x80000000)));
    }
}
