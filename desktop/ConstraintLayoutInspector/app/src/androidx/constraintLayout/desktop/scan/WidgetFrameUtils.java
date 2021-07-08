/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package androidx.constraintLayout.desktop.scan;

import androidx.constraintlayout.core.parser.*;
import androidx.constraintlayout.core.state.WidgetFrame;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

public class WidgetFrameUtils {
    public static final int FILL = 1;
    public static final int OUTLINE = 2;
    public static final int DASH_OUTLINE = 4;
    public static final Color START_COLOR = Color.BLUE.darker();
    public static final Color END_COLOR = Color.BLUE.brighter();
    public static final Color INTERPOLATED_COLOR = Color.BLUE;
    final static float dash1[] = {10.0f};
    final static BasicStroke dashed =
            new BasicStroke(3f,
                    BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER,
                    10.0f, dash1, 0.0f);

    public static void deserialize(CLKey object, WidgetFrame dest) throws CLParsingException {
        CLKey clkey = ((CLKey) object);

        CLElement value = clkey.getValue();
        if (value instanceof CLObject) {
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
    }

    public static void render(WidgetFrame frame, Graphics2D g2d, int mask) {
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

        at.rotate(Math.toRadians(rotationZ));

        at.scale(scaleX, scaleY);
        at.translate(-pivotX, -pivotY);

        Graphics2D g = (Graphics2D) g2d.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.transform(at);
        if ((mask & DASH_OUTLINE) == 0) {
            g.drawRect(frame.left, frame.top, frame.right - frame.left, frame.bottom - frame.top);
        }
         int rgb = g.getColor().getRGB();
        int alpha = ((int) (0.5f + ((rgb >> 24) & 0xFF) * 0.2));

        g.setColor(new Color((rgb & 0xFFFFFF) | (alpha << 24), true));

        if ((mask & FILL) != 0)
            g.fillRect(frame.left, frame.top, frame.right - frame.left, frame.bottom - frame.top);
        Stroke restore = null;
        if ((mask & DASH_OUTLINE) != 0) {
            restore = g.getStroke();
            g.setStroke(dashed);
        }
        g.drawRect(frame.left, frame.top, frame.right - frame.left, frame.bottom - frame.top);
        if ((mask & DASH_OUTLINE) != 0) {

            g.setStroke(restore);
        }
    }

    public static void renderPath(Path2D path, Graphics2D g2d) {
        if (path == null) {
            return;
        }
        g2d.setStroke(new BasicStroke(1));
        g2d.draw(path);
    }

    public static void getPath(CLKey pathKey, Path2D.Float path2d) {

        CLArray array = ((CLArray) pathKey.getValue());
        int size = array.size();

        for (int i = 0; i < size; i += 4) {
            try {
                float x1 = array.get(i).getFloat();
                float y1 = array.get(i + 1).getFloat();
                float x2 = array.get(i + 2).getFloat();
                float y2 = array.get(i + 3).getFloat();

                path2d.moveTo(x1, y1);
                path2d.lineTo(x2, y2);
            } catch (CLParsingException e) {
                e.printStackTrace();
            }
        }
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
