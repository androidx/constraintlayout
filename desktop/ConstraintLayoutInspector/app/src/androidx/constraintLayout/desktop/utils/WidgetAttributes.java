package androidx.constraintLayout.desktop.utils;

import androidx.constraintlayout.core.motion.CustomVariable;
import androidx.constraintlayout.core.state.WidgetFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class WidgetAttributes extends JPanel {
    String[] columnNames = {"Attribute", "Value"};
    DefaultTableModel model = new DefaultTableModel(columnNames,0);
    JTable table = new JTable(model);
    JScrollPane scrollPane = new JScrollPane(table);
    JFrame frame;

    public WidgetAttributes() {
        super(new BorderLayout());
        add(scrollPane);
    }

    public void setWidgetFrame(WidgetFrame frame){
        clear();
           add( "pivotX", frame.pivotX);
        add("pivotY", frame.pivotY);
        add( "rotationX", frame.rotationX);
        add( "rotationY", frame.rotationY);
        add( "rotationZ", frame.rotationZ);
        add( "translationX", frame.translationX);
        add( "translationY", frame.translationY);
        add( "translationZ", frame.translationZ);
        add( "scaleX", frame.scaleX);
        add( "scaleY", frame.scaleY);
        add( "alpha", frame.alpha);
        add( "visibility", frame.left);
        for (String name : frame.mCustom.keySet()) {
            CustomVariable value = frame.mCustom.get(name);
            model.addRow(new String[]{name, value.toString().split(":")[2].trim()});
        }
    }

    private void add(String name, float value) {
        if (!Float.isNaN(value)) {
            model.addRow(new String[]{name, Float.toString(value)});
        }
    }

    private void add(String name, int value) {
        model.addRow(new String[]{name, Integer.toString(value)});
    }

    private void clear() {
        int count = model.getRowCount();
        for (int i = 0; i < count; i++) {
            model.removeRow(0);
        }
    }
    private void setTestData() {
        Timer t = new Timer(100 , (a) -> {
            if (model.getRowCount() > 6) {


            }
            model.addRow(new String[]{"foo", "barr"});
        });
        t.start();

    }
    private static JFrame ourFrame;

    public static WidgetAttributes display(WidgetFrame frame) {

        if (ourFrame != null) {
            WidgetAttributes p = (WidgetAttributes)ourFrame.getContentPane();
            p.setWidgetFrame(frame);
            return p;
        }

        JFrame f = new JFrame(" Attributes");
        ourFrame = f;
        WidgetAttributes p = new WidgetAttributes();
        p.frame = f;
        f.setContentPane(p);
        Desk.rememberPosition(f, new Rectangle(200,200,400,600));
        f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        f.setVisible(true);
        p.setWidgetFrame(frame);
        return p;
    }
    public static void main(String[] args) {
        JFrame f = new JFrame(" Attributes");
        WidgetAttributes p = new WidgetAttributes();
        f.setContentPane(p);
        Desk.rememberPosition(f, new Rectangle(200,200,400,600));
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setVisible(true);
    p.setTestData();

    }
}
