package androidx.constraintLayout.desktop.motion;

import org.constraintlayout.swing.ConstraintLayout;
import org.constraintlayout.swing.MotionLayout;

import javax.swing.*;
import java.awt.geom.RoundRectangle2D;

public class SwingDemo3 extends JPanel {
    static String motionScene  = "{\n" +
            "                 Header: {\n" +
            "                  name: 'RotationZ28'\n" +
            "                },\n" +
            "                ConstraintSets: {\n" +
            "                  start: {\n" +
            "                    b1: {\n" +
            "                      width: 400,\n" +
            "                      height: 40,\n" +
            "                      start: ['parent', 'start', 116],\n" +
            "                      bottom: ['parent', 'bottom', 16]\n" +
            "                    }\n" +
            "                    b2: {\n" +
            "                      width: 40,\n" +
            "                      height: 40,\n" +
            "                      start: ['parent', 'start', 16],\n" +
            "                      bottom: ['parent', 'bottom', 16]\n" +
            "                    }\n" +
            "                  },\n" +
            "                  end: {\n" +
            "                    b1: {\n" +
            "                      width: 40,\n" +
            "                      height: 40,\n" +
            "                      //rotationZ: 390,\n" +
            "                      end: ['parent', 'end', 16],\n" +
            "                      top: ['parent', 'top', 16]\n" +
            "                    }\n" +
            "                    b2: {\n" +
            "                      width: 40,\n" +
            "                      height: 40,\n" +
            "                      //rotationZ: 390,\n" +
            "                      end: ['parent', 'end', 16],\n" +
            "                      top: ['parent', 'top', 16]\n" +
            "                    }\n" +
            "                  }\n" +
            "                },\n" +
            "                Transitions: {\n" +
            "                  default: {\n" +
            "                    from: 'start',\n" +
            "                    to: 'end',\n" +
            "                    pathMotionArc: 'startHorizontal',\n" +
            "                    KeyFrames: {\n" +
            "                      KeyAttributes: [\n" +
            "                        {\n" +
            "                          target: ['a'],\n" +
            "                          frames: [33, 66],\n" +
            "                          rotationZ: [90, -90],\n" +
            "                          \n" +
            "                        }\n" +
            "                      ]\n" +
            "                    }\n" +
            "                  }\n" +
            "                }\n" +
            "            }";
    MotionLayout ml;
    float p = 0;
    public SwingDemo3() {
        setLayout(ml = new MotionLayout(motionScene,()-> {
            JButton button1 = new JButton("Hello");
            JButton button2 = new JButton("World");

            add(button1, "b1");
            add(button2, "b2");
        })
        );
        Timer timer = new Timer(16, (e)->ml.setProgress(p=(p+0.005f)%1));
        timer.setRepeats(true);
        timer.start();
    }

    public static void main(String[] args) throws InterruptedException {
        JFrame frame = new JFrame("Demo2");
        SwingDemo3 panel = new SwingDemo3();

        frame.setContentPane(panel);


        frame.setBounds(1300, 100, 400, 500);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        Thread.sleep(1000);
      //  frame.setShape(new RoundRectangle2D.Double(1300, 100, 400, 400, 50, 50));
    }
}
