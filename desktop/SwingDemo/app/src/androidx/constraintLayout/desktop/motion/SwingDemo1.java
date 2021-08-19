package androidx.constraintLayout.desktop.motion;

import org.constraintlayout.swing.ConstraintLayout;

import javax.swing.*;

public class SwingDemo1 extends JPanel {
    static String constraintSet  = "{" +
            "      b1: {\n" +
            "        width: 100,\n" +
            "        height: 100,\n" +
            "        start: ['parent', 'start', 16],\n" +
            "        centerVertically: 'parent'\n" +
            "      },\n" +
            "      b2: {\n" +
            "        width: 100,\n" +
            "        height: 100,\n" +
            "        start: ['parent', 'start', 36],\n" +
            "        top: ['b1', 'top', 20],\n" +
            "        translationZ: 30\n" +
            "      }\n" +
            "    }";
    public SwingDemo1() {
        super(new ConstraintLayout(constraintSet));
        JButton button1 =  new JButton("Hello");
        JButton button2 =  new JButton("World");

        add(button1,"b1");
        add(button2, "b2");

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Demo1");
        SwingDemo1 panel = new SwingDemo1();

        frame.setContentPane(panel);
        frame.setBounds(100, 100, 400, 500);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
