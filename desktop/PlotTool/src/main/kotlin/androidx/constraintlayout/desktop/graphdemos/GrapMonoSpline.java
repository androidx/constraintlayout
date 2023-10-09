package androidx.constraintlayout.desktop.graphdemos;

import androidx.constraintlayout.desktop.graph.Utils;
import androidx.constraintlayout.desktop.graphdemos.splineEngines.MonoSpline;
import androidx.constraintlayout.desktop.graph.GraphEngine;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class GrapMonoSpline {
    static double[] time = {
            0, 0.25, .5, .75, 1
    };

    public static MonoSpline buildSpline1() {
        double[][] points = {
                {0, 0.5}, {0.25, 1}, {0.5, 1}, {1, 0}, {0, 0}
        };

        MonoSpline spline = new MonoSpline(time, Arrays.asList(points));
        return spline;
    }


    public static void main(String[] args) {
        var spline = buildSpline1();
        JFrame frame = Utils.smartFrame("Graph");
        GraphEngine p = GraphEngine.setupFrame(frame, "position");       // frame.setUndecorated(true);
        Color xcolor = new Color(0x3C4DBD);
        Color ycolor = new Color(0x552149);
        Color xycolor = new Color(0xCD9E39);
        Color deltacolor = new Color(0xF7F4F2);
        Color deltacolor2 = new Color(0xCA763E);
        Color deltacolor3 = new Color(0x4ACA3E);
        p.addFunction2("x", 0, 1, xcolor, t -> spline.getPos(t, 0));
        p.addFunction2("y", 0, 1, ycolor, t -> spline.getPos(t, 1));
        p.addFunction2d("curve", 0, 1, xycolor,
                t -> spline.getPos(t, 0),
                t -> spline.getPos(t, 1));
        {
            double[] x = new double[20];
            double[] y = new double[x.length];
            double[] x2 = new double[20];
            double[] y2 = new double[x.length];
            for (int i = 0; i < y.length; i++) {
                double t = i / (double) (y.length - 1);
                double nt = spline.calcPercent(t);
            }
            for (int i = 0; i < y.length; i++) {
                double t = i / (double) (y.length - 1);
                double nt = spline.calcPercent(t);

                x[i] = spline.getPos(nt, 0);
                y[i] = spline.getPos(nt, 1) ;

                if (i > 0) {
                    x2[i] = t;
                    y2[i] = 100*Math.hypot(x[i]-x[i-1],y[i]-y[i-1]);
                }
            }
            p.addPoints("curve", xycolor, x, y);
            Color jumps = new Color(0x9F57F5);

            p.addPoints("curve", jumps, x2, y2);
        }

        if (true) {
            double[] x = Arrays.copyOf(time, time.length);
            double[] y = new double[x.length];
            for (int i = 0; i < y.length; i++) {
                double nt = spline.calcPercent(x[i]);
                double xv = x[i];
                x[i] = spline.getPos(xv, 0);
                y[i] = spline.getPos(xv, 1);
            }
            p.addPoints("curve", Color.RED, x, y);
        }
        if (false) {
            double[] x = new double[50];
            double[] y = new double[x.length];
            for (int i = 0; i < y.length; i++) {

                x[i] = i / (y.length - 1.);
                y[i] = spline.calcPercent(i / (y.length - 1.));
            }
            p.addPoints("curve", deltacolor, x, y);
        }
        p.addFunction2("transfer", 0, 1, deltacolor, t -> spline.calcPercent(t));

        double e = 0.0001;

        p.addFunction2("slope", 0, 1, deltacolor, t -> Math.hypot(spline.getSlope(t, 0), spline.getSlope(t, 1)));

        p.addFunction2("speed", 0, 1, deltacolor2,
                t -> 0.05 + Math.hypot((spline.getPos(t, 0) - spline.getPos(t + e, 0))
                        , (spline.getPos(t, 1) - spline.getPos(t + e, 1))) / e);


        p.addFunction2("speed", 0, 1, deltacolor3,
                t -> {
                    double nt = spline.calcPercent(t - e);
                    double ndt = spline.calcPercent(t + e);

                    return Math.hypot((spline.getPos(nt, 0) - spline.getPos(ndt, 0))
                            , (spline.getPos(nt, 1) - spline.getPos(ndt, 1))) / (2 * e);
                });

        p.addFunction2("speed2", 0, 1, deltacolor3,
                t -> {
                    double ep = 0.000001;
                    double nt = spline.calcPercent(t - ep);
                    double ndt = spline.calcPercent(t + ep);

                    return Math.hypot((spline.getPos(nt, 0) - spline.getPos(ndt, 0))
                            , (spline.getPos(nt, 1) - spline.getPos(ndt, 1))) / (2 * ep);
                });

        frame.setVisible(true);

    }
}
