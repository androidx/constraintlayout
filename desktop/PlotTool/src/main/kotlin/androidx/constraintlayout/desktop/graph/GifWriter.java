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

import org.w3c.dom.Node;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class GifWriter {
    File mFile;
    int mTimeBetweenFramesMS;
    boolean mLoopContinuously;
    String comment;
    ImageWriter mGifWriter;
    IIOMetadata mImageMetaData;
    ImageWriteParam mImageWriteParam;
    ImageOutputStream mOutputStream;

    public GifWriter(File file, int timeBetweenFramesMS, boolean loopContinuously, String comment) {
        this.mFile = file;
        this.mTimeBetweenFramesMS = timeBetweenFramesMS;
        this.mLoopContinuously = loopContinuously;
        this.comment = comment;
    }

    public void setup(File file, int timeBetweenFramesMS, boolean loopContinuously, String comment) {
        this.mFile = file;
        this.mTimeBetweenFramesMS = timeBetweenFramesMS;
        this.mLoopContinuously = loopContinuously;
        this.comment = comment;
        mGifWriter = null;
    }
    public void addImage(BufferedImage img) throws IOException {
        if (mGifWriter == null) {
            setup(img.getType());
        }
        mGifWriter.writeToSequence(new IIOImage(img, null, mImageMetaData), mImageWriteParam);
    }

    private void setup(int type) throws IOException {
        mOutputStream = new FileImageOutputStream(mFile);

        Iterator<ImageWriter> iter = ImageIO.getImageWritersBySuffix("gif");
        if (!iter.hasNext()) {
            throw new IOException("NO GIF writer");
        }
        mGifWriter = iter.next();
        mImageWriteParam = mGifWriter.getDefaultWriteParam();
        ImageTypeSpecifier imageTypeSpecifier =
                ImageTypeSpecifier.createFromBufferedImageType(type);

        mImageMetaData = mGifWriter.getDefaultImageMetadata(imageTypeSpecifier,
                mImageWriteParam);

        String metaFormatName = mImageMetaData.getNativeMetadataFormatName();

        IIOMetadataNode root = (IIOMetadataNode) mImageMetaData.getAsTree(metaFormatName);

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
        meta.setAttribute("delayTime", Integer.toString(mTimeBetweenFramesMS / 10));
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
        int loop = mLoopContinuously ? 0 : 1;
        appNode.setUserObject(new byte[]{0x1, (byte) (loop & 0xFF), (byte) ((loop >> 8) & 0xFF)});
        appEntensionsNode.appendChild(appNode);
        mImageMetaData.setFromTree(metaFormatName, root);
        mGifWriter.setOutput(mOutputStream);
        mGifWriter.prepareWriteSequence(null);
    }

    public void close() throws IOException {
        mGifWriter.endWriteSequence();
        mOutputStream.close();
        mGifWriter = null;
    }


    public static void writeGif(File file, BufferedImage[] imgs, int timeBetweenFramesMS, boolean loopContinuously, String comment) throws IIOException, IOException {
        GifWriter writer = new GifWriter(file, timeBetweenFramesMS, loopContinuously, comment);
        for (int i = 0; i < imgs.length; i++) {
            writer.addImage(imgs[i]);
        }
        writer.close();
    }

}
