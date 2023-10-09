/*
 * Copyright (C) 2023 The Android Open Source Project
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
package androidx.constraintlayout.desktop.graph;

/*
 * Copyright (C) 2023 The Android Open Source Project
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
import org.w3c.dom.Node;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class ControlPanel extends JPanel {
    GraphEngine graphEngine;
    boolean animating = false;
    float period = 1;

    ControlPanel(GraphEngine ge) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        graphEngine = ge;
        JButton button = new JButton(">");
        button.addActionListener(e -> {
            animating = !animating;
            button.setText(animating ? "||" : ">");
            graphEngine.animate(animating);
        });
        add(button);
        JButton periodLabel = new JButton("1.0");

        periodLabel.setBackground(getBackground());
        periodLabel.setBorderPainted(false);
        periodLabel.setOpaque(true);
        JButton more = new JButton("+");
        more.addActionListener(e -> {
            animating = !animating;
            period *= 2;
            periodLabel.setText("" + period);
            graphEngine.setPeriod(1 / period);
        });
        add(more);

        add(periodLabel);

        JButton less = new JButton("-");
        less.addActionListener(e -> {
            animating = !animating;
            period /= 2;
            periodLabel.setText("" + period);
            graphEngine.setPeriod(1 / period);
        });
        add(less);
        JButton save = new JButton("Save...");
        save.addActionListener((e) -> {
            saveGif(save);
        });
        add(save);
    }
    File saveFile = null;
    private void saveGif(Component c) {
        int count = (int) (30 * period);
        JFileChooser fileChooser = new JFileChooser();
        int ret = fileChooser.showSaveDialog(c);
        saveFile = fileChooser.getSelectedFile();
        if (saveFile == null) {
            return;
        }
        if (!saveFile.getName().toLowerCase().endsWith(".gif")) {
            saveFile = new File(saveFile.getParentFile(), saveFile.getName() + ".gif");
        }
        JFrame f = (JFrame) SwingUtilities.getWindowAncestor(c);
        JDialog jDialog = new JDialog(f, "modeal ", Dialog.ModalityType.TOOLKIT_MODAL);
        JPanel p = new JPanel();
        jDialog.setContentPane(p);
        Point point = c.getLocationOnScreen();
        jDialog.setBounds(point.x - 100, point.y - 100, 200, 200);
        JProgressBar progressBar = new JProgressBar(0, count);
        p.add(progressBar);
        JButton cb = new JButton("cancel");
        p.add(cb);
        cb.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progressBar.setMaximum(0);
                jDialog.dispose();
            }
        });
        Thread timer = new Thread() {
            public void run() {
                int w = graphEngine.getWidth();
                int h = graphEngine.getHeight();

                BufferedImage[] img = new BufferedImage[count];
                try {
                    for (int i = 0; i < img.length; i++) {
                        int frame = i;
                        img[i] = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                        if (progressBar.getMaximum() == 0) {
                            return;
                        }
                        Thread.sleep(30);
                        SwingUtilities.invokeAndWait(new Runnable() {
                            @Override
                            public void run() {

                                graphEngine.setAnimationForSave(frame / (float) (img.length - 1));
                                graphEngine.paint(img[frame].getGraphics());
                                graphEngine.repaint();
                                progressBar.setValue(frame);
                            }
                        });

                    }
                    System.out.println(" writing " + saveFile.getName());
                    writeGif(saveFile, img, 33, true, "written by John");
                    System.out.println("done  writing " + saveFile.getName());


                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                jDialog.dispose();
            }

        };
        timer.start();

        jDialog.setVisible(true);

    }


    public static void writeGif(File file, BufferedImage[] imgs, int timeBetweenFramesMS, boolean loopContinuously, String comment) throws IIOException, IOException {
        ImageOutputStream outputStream = new FileImageOutputStream(file);
        ImageWriter gifWriter;
        Iterator<ImageWriter> iter = ImageIO.getImageWritersBySuffix("gif");
        if (!iter.hasNext()) {
            throw new IOException("NO GIF writer");
        }
        gifWriter = iter.next();
        ImageWriteParam imageWriteParam = gifWriter.getDefaultWriteParam();
        ImageTypeSpecifier imageTypeSpecifier =
                ImageTypeSpecifier.createFromBufferedImageType(imgs[0].getType());

        IIOMetadata imageMetaData = gifWriter.getDefaultImageMetadata(imageTypeSpecifier,
                imageWriteParam);

        String metaFormatName = imageMetaData.getNativeMetadataFormatName();

        IIOMetadataNode root = (IIOMetadataNode) imageMetaData.getAsTree(metaFormatName);

        String CONTROL_EXTENSION = "GraphicControlExtension";
        String COMMENT_EXTENSION = "CommentExtensions";
        String APP_EXTENSION = "ApplicationExtensions";

        IIOMetadataNode meta = null, commentsNode = null, appEntensionsNode = null;
        int nNodes = root.getLength();
        for (int i = 0; i < nNodes; i++) {
            Node item = root.item(i);
            String nodeName = root.item(i).getNodeName();
            if (nodeName.equalsIgnoreCase(CONTROL_EXTENSION)) {
                meta = (IIOMetadataNode) item;
            } else if (nodeName.equalsIgnoreCase(COMMENT_EXTENSION)) {
                commentsNode = (IIOMetadataNode) item;
            } else if (nodeName.equalsIgnoreCase(APP_EXTENSION)) {
                appEntensionsNode = (IIOMetadataNode) item;
            }
        }

        if (meta == null) {
            meta = new IIOMetadataNode(CONTROL_EXTENSION);
            root.appendChild(meta);
        }

        meta.setAttribute("disposalMethod", "none");
        meta.setAttribute("userInputFlag", "FALSE");
        meta.setAttribute("transparentColorFlag", "FALSE");
        meta.setAttribute("delayTime", Integer.toString(timeBetweenFramesMS / 10));
        meta.setAttribute("transparentColorIndex", "0");

        if (commentsNode == null) {
            commentsNode = new IIOMetadataNode(COMMENT_EXTENSION);
            root.appendChild(commentsNode);
        }
        IIOMetadataNode c1 = new IIOMetadataNode("CommentExtension");
        c1.setAttribute("value", comment);
        commentsNode.appendChild(c1);

        if (appEntensionsNode == null) {
            appEntensionsNode = new IIOMetadataNode(APP_EXTENSION);
            root.appendChild(appEntensionsNode);
        }

        IIOMetadataNode appNode = new IIOMetadataNode("ApplicationExtension");
        appNode.setAttribute("applicationID", "NETSCAPE");
        appNode.setAttribute("authenticationCode", "2.0");
        int loop = loopContinuously ? 0 : 1;
        appNode.setUserObject(new byte[]{0x1, (byte) (loop & 0xFF), (byte) ((loop >> 8) & 0xFF)});
        appEntensionsNode.appendChild(appNode);
        imageMetaData.setFromTree(metaFormatName, root);
        gifWriter.setOutput(outputStream);
        gifWriter.prepareWriteSequence(null);
        for (int i = 0; i < imgs.length; i++) {
            gifWriter.writeToSequence(new IIOImage(imgs[i], null, imageMetaData), imageWriteParam);
        }
        gifWriter.endWriteSequence();
        outputStream.close();
    }


}
