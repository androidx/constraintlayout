package androidx.constraintLayout.desktop.scan;

import androidx.constraintlayout.core.parser.*;
import androidx.constraintlayout.core.state.WidgetFrame;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class WidgetFrameUtils {


    public static void deserialize(CLKey object, WidgetFrame dest) throws CLParsingException {
        CLKey clkey = ((CLKey) object);
        CLElement value = clkey.getValue();
        if (value instanceof CLObject) {
            System.out.println(value.getClass().getSimpleName());
            CLObject obj = ((CLObject) value);
            int n = obj.size();
            for (int i = 0; i < n; i++) {
                CLElement tmp = obj.get(i);
                CLKey k = ((CLKey) tmp);
                String name = k.content();
                CLElement v = k.getValue();
                dest.setValue(name, v);
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        dest.serialize(stringBuilder);
        System.out.println(">>>>>" + stringBuilder.toString());
    }

    public static void render(WidgetFrame frame, Graphics2D g2d) {
        float cx = (frame.left + frame.right) / 2f;
        float cy = (frame.top + frame.bottom) / 2f;
        float dx = frame.right - frame.left;
        float dy = frame.bottom - frame.top;

        float rotationZ = Float.isNaN(frame.rotationZ) ? 0 : frame.rotationZ;
        float pivotX = Float.isNaN(frame.pivotX) ? cx : frame.pivotX * dx + frame.left;
        float pivotY = Float.isNaN(frame.pivotY) ? cy : frame.pivotY * dy + frame.top;

        float rotationX = Float.isNaN(frame.rotationX) ? 0 : frame.rotationX;
        float rotationY = Float.isNaN(frame.rotationY) ? 0 : frame.rotationY;


        float translationX = Float.isNaN(frame.translationX) ? 0 : frame.translationX;
        float translationY = Float.isNaN(frame.translationY) ? 0 : frame.translationY;
        float translationZ = Float.isNaN(frame.translationZ) ? 0 : frame.translationZ;

        float scaleX = Float.isNaN(frame.scaleX) ? 1 : frame.scaleX;
        float scaleY = Float.isNaN(frame.scaleY) ? 1 : frame.scaleY;

        AffineTransform at = new AffineTransform();
        at.translate(translationX, translationY);
        at.translate(pivotX, pivotY);
//     Todo:    at.scale(scaleX * Math.cos(rotationY), scaleY + Math.cos(rotationX));
        at.rotate(Math.toRadians(rotationZ));
        at.translate(-pivotX, -pivotY);

        Graphics2D g = (Graphics2D) g2d.create();
        g.setTransform(at);
        g.drawRect(frame.left, frame.top, frame.right - frame.left, frame.bottom - frame.top);

        int rgb = g.getColor().getRGB();
        int alpha = ((int) (0.5f + ((rgb >> 24) & 0xFF) * 0.2));

        g.setColor(new Color((rgb & 0xFFFFFF) | (alpha << 24), true));

        g.fillRect(frame.left, frame.top, frame.right - frame.left, frame.bottom - frame.top);
        g.drawRect(frame.left, frame.top, frame.right - frame.left, frame.bottom - frame.top);

    }

    public static void main(String[] str) throws CLParsingException {
        String clString = "{" +
                "              b: {\n" +
                "                alpha: 0.2,\n" +
                "                scaleX: 5,\n" +
                "                scaleY: 5,\n" +
                "                rotationZ: -30,\n" +
                "                custom: {\n" +
                "                  background: '#FFFF00',\n" +
                "                  textColor: '#000000',\n" +
                "                  textSize: 64\n" +
                "                }" +
                "              }" +
                "}";
        CLObject obj = CLParser.parse(clString);
        CLKey tmp = (CLKey) obj.get(0);
        WidgetFrame frame = new WidgetFrame();
        deserialize(tmp, frame);
        StringBuilder builder = new StringBuilder();
        frame.serialize(builder);
        System.out.println(builder.toString());

    }
}
