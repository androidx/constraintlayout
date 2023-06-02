package com.support.constraintlayout.extlib.graph3d;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame =  new JFrame("3d Plot");
        Graph3dPanel  p = new Graph3dPanel();
        frame.setContentPane(p);
        frame.setBounds(100,100,500,500);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }
}
