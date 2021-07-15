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

import androidx.constraintLayout.desktop.utils.ScenePicker;
import androidx.constraintlayout.core.parser.*;
import androidx.constraintlayout.core.state.WidgetFrame;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

public class WidgetFrameUtils {
    public static final int FILL = 1;
    public static final int FILL_OPAQUE = 2;
    public static final int OUTLINE = 4;
    public static final int DASH_OUTLINE = 8;
    public static final int TEXT = 16;
    public static final Theme theme = new Theme(false);
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
    private static double []srcPts = new double[8];
    private static double []dstPts = new double[8];
    public static void render(WidgetFrame frame, Graphics2D g2d, ScenePicker scenePicker, int mask) {
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

        if ((mask & FILL) != 0)
            g.fillRect(frame.left, frame.top, frame.right - frame.left, frame.bottom - frame.top);
        Stroke restore = null;
        if ((mask & DASH_OUTLINE) != 0) {
            restore = g.getStroke();
            g.setStroke(dashed);
        }
        if (scenePicker != null) {
            srcPts[0] = frame.left;// top left
            srcPts[1] = frame.top;
            srcPts[2] = frame.right;
            srcPts[3] = frame.top;
            srcPts[4] = frame.right;
            srcPts[5] = frame.bottom;
            srcPts[6] = frame.left;
            srcPts[7] = frame.bottom;


            g.getTransform().transform(srcPts, 0, dstPts, 0, 4);
            scenePicker.addQuadrilateral(frame, dstPts, 0);
        }

        g.drawRect(frame.left, frame.top, frame.right - frame.left, frame.bottom - frame.top);
        if ((mask & DASH_OUTLINE) != 0) {
            g.setStroke(restore);
        }
        if (((mask & TEXT) != 0) && frame.name != null) {
            g.setColor(theme.textColor());
            g.drawString(frame.name, frame.left + 8, frame.bottom - 8);
        }
    }

    public static void renderPath(Path2D path, Graphics2D g2d) {
        if (path == null) {
            return;
        }
        g2d.setStroke(new BasicStroke(2));
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
