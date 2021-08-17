/*
 * Copyright (C) 2017 The Android Open Source Project
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
package androidx.constraintLayout.desktop.constraintRendering.layout3d;


import androidx.constraintLayout.desktop.link.LayoutView;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.prefs.Preferences;


/**
 * This is a simple test driver for the 3d engine
 */
public class CheckLayout3d extends JPanel {
  private static final boolean DEBUG_WITH_FILE = true;
  Display3D myDisplay3D = new Display3D();

  public CheckLayout3d() {
    super(new BorderLayout());
    add(myDisplay3D);
    myDisplay3D.addViewChangeListener(e -> savePref());
  }

  public static BufferedImage getTestImage() {
    BufferedImage img = null;

    int width = 1024;
    int height = 1920;
    img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    int[] data = ((DataBufferInt)img.getRaster().getDataBuffer()).getData();
    for (int i = 0; i < data.length; i++) {
      data[i] = ((((i % width) * 255) / width) << 16) | ((((i / width) * 255) / height) << 8) | (i >> 10);
    }

    return img;
  }

  public static Layout.Widget getTestViews(BufferedImage img) {
    float w = img.getWidth();
    float h = img.getHeight();
    float block = w / 10;
    float bottom = 398;
    float button_w = 124;

    float button_h = 108;
    float gap = (w - button_w * 5) / 6;
    Layout.Widget v = new Layout.Widget(0, 0, w, h, 0)
      .addChild(
        new Layout.Widget(0, 0, w, 300, 0)
          .addChild(
            new Layout.Widget(0, 94, w, 182, 0)
          ),

        new Layout.Widget(gap * 1 + button_w * 0, h - bottom, button_w, button_h, 0),
        new Layout.Widget(gap * 2 + button_w * 1, h - bottom, button_w, button_h, 0),
        new Layout.Widget(gap * 3 + button_w * 2, h - bottom, button_w, button_h, 0),
        new Layout.Widget(gap * 4 + button_w * 3, h - bottom, button_w, button_h, 0),
        new Layout.Widget(gap * 5 + button_w * 4, h - bottom, button_w, button_h, 0)
      );
    return v;
  }

  private void savePref() {
    Preferences prefs = Preferences.userNodeForPackage(CheckLayout3d.class);
    final String ORIENTATION = "name_of_preference";
    JFrame topFrame = (JFrame)SwingUtilities.getWindowAncestor(this);
    Rectangle rect = topFrame.getBounds();
    prefs.put(ORIENTATION, myDisplay3D.getOrientationString(rect));
  }

  public void loadPref() {
    Preferences prefs = Preferences.userNodeForPackage(CheckLayout3d.class);
    final String ORIENTATION = "name_of_preference";
    String pref = prefs.get(ORIENTATION, null);
    if (pref != null) {
      myDisplay3D.setup();
      Rectangle rect = myDisplay3D.parseOrientationString(pref);
      Frame topFrame = (JFrame)SwingUtilities.getWindowAncestor(this);
      topFrame.setBounds(rect);
    }
  }


  public static void create(Layout.Widget views, BufferedImage img) {
    JFrame f = new JFrame("CheckTriangles");
    CheckLayout3d p = new CheckLayout3d();
    f.setContentPane(p);
    f.setBounds(100, 100, 512, 512);

    p.myDisplay3D.setTriData(new Layout(img, views));

    f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    f.setVisible(true);
  }


  public static Layout.Widget get3dWidgets(ArrayList<LayoutView.Widget> widgets) {

    Layout.Widget rw = null;
    for (LayoutView.Widget widget : widgets) {
      float top = widget.getInterpolated().top;
      float bottom = widget.getInterpolated().bottom;
      float left = widget.getInterpolated().left;
      float right = widget.getInterpolated().right;
      float w = right -  left;
      float h =  bottom -  top;
      if ("root".equals(widget.getId())) {
       rw = new Layout.Widget(0, 0, w, h,0);
      }
    }
    if (rw == null) {
      return null;
    }
    float z = 0;
    for (LayoutView.Widget widget : widgets) {
      float top = widget.getInterpolated().top;
      float bottom = widget.getInterpolated().bottom;
      float left = widget.getInterpolated().left;
      float right = widget.getInterpolated().right;
      float w = right -  left;
      float h =  bottom -  top;
      if ("root".equals(widget.getId())) {
      continue;
      }
      rw.addChild(new Layout.Widget(left, top, w, h, z));
      z+=1;
    }

    return rw;
  }

  BufferedImage img;
  public void update(ArrayList<LayoutView.Widget> widgets) {
    myDisplay3D.updateTriData(new Layout(img, get3dWidgets(widgets)));
    myDisplay3D.isImageInvalid = true;
    myDisplay3D.repaint();
  }
  public static CheckLayout3d create3d(ArrayList<LayoutView.Widget> widgets) {
    JFrame f = new JFrame("CheckTriangles");
    CheckLayout3d p = new CheckLayout3d();
    f.setContentPane(p);
    f.setBounds(100, 100, 512, 512);
    BufferedImage img = getTestImage();
    p.img = img;
    Layout.Widget views = (widgets ==null) ? getTestViews(img):get3dWidgets(widgets) ;
    p.myDisplay3D.setTriData(new Layout(img, views));

    f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    f.setVisible(true);
    return p;
  }



  public static void main(String[] args) {
    create3d(null);
  }
}
