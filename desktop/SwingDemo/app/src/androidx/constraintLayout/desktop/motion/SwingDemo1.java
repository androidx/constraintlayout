package androidx.constraintLayout.desktop.motion;


import org.constraintlayout.swing.ConstraintLayout;
import org.constraintlayout.swing.LinkServer;

import javax.swing.*;

public class SwingDemo1 extends JPanel {
    static String constraintSet  = "{" +
            "     Header: { exportAs: 'example 3'}," +
            "      b1: {\n" +
            "        width: 'wrap',\n" +
            "        height: 'wrap',\n" +
            "        start: ['parent', 'start', 166],\n" +
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
        new LinkServer();

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
