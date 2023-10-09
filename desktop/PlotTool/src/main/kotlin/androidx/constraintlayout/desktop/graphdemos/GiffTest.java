package androidx.constraintlayout.desktop.graphdemos;

import org.w3c.dom.Node;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.*;
import java.util.Arrays;
import java.util.Iterator;

public class GiffTest {

    public static BufferedImage getImage(int w, int h, float angle, int count) {
        float[] table;
        BufferedImage ret = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = ret.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);

        table = new float[w * h];
        Arrays.fill(table, Float.NaN);


        int[] data = ((DataBufferInt) ret.getRaster().getDataBuffer()).getData();
        float rsqr = Math.min(w, h) / 2f;
        rsqr *= rsqr;

        float offset = ((angle % 360) * 256);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                float xp = x - w / 2f;
                float yp = y - h / 2f;
                if (xp * xp + yp * yp < rsqr) {
                    int p = x + y * w;
                    float ang = table[p];
                    if (Float.isNaN(ang)) {
                        ang = (float) (256 * Math.toDegrees(Math.atan2(yp, xp)));
                        table[p] = ang;
                    }
                    int vr = ((int) (ang + offset) / 360) & 0xFF;
                    int vg = ((int) (ang * 1.1f + offset) / 360) & 0xFF;
                    int vb = ((int) (ang * 1.2f + offset) / 360) & 0xFF;
                    data[x + y * w] = (vr << 16) | (vg << 8) | vb;
                }
            }
            String str = Integer.toString(count);
            Rectangle2D bounds = g.getFontMetrics().getStringBounds(str, g);
            double scale = Math.min(w / bounds.getWidth(), h / bounds.getHeight()) * 0.8;
            Font f = g.getFont();
            g.setFont(f.deriveFont(f.getSize() * (float) scale));
            FontMetrics metrics = g.getFontMetrics();
            bounds = metrics.getStringBounds(str, g);

            g.drawString(str, (float) (w - bounds.getWidth()) / 2f, metrics.getAscent() + (float) (h - bounds.getHeight()) / 2f);
        }

        g.setColor(Color.BLACK);
        g.fillRect(w / 2, 0, 2, h);
        g.fillRect(0, h / 2, w, 2);
        return ret;
    }

    public static void main(String[] args) throws IOException {

        BufferedImage []img = new BufferedImage[10];
        for (int i = 0; i < img.length; i++) {
            BufferedImage bufferedImage = img[i] = getImage(128,128,i/img.length,i);
        }
        writeGif(new File("D:\\tmp\\foo.gif"),img, 16,true,"test");
        writeGif(new File("D:\\tmp\\foo3.gif"),img, 16,true,"test");

        binaryComp(new File("D:\\tmp\\foo.gif"),new File("D:\\tmp\\foo2.gif"));

    }

    private static void binaryComp(File file1, File file2) throws IOException {
        if (file1.length() != file2.length()){
            System.out.println("Files are not the same length");
            return;
        }
        FileInputStream is1 = new FileInputStream(file1);
        FileInputStream is2 = new FileInputStream(file2);

        byte[]data1 = new byte[3200];
        byte[]data2 = new byte[3200];
        int total = 0;
        while(true){
            int ret1 = is1.read(data1);
            int ret2 = is2.read(data2);
            if (ret1 < 0 || ret2<0) {
                System.out.println("files match "+file1.length());
                return;
            }
            for (int i = 0; i < ret1; i++) {
               if (data1[i] != data2[i]) {
                   System.out.println("data at positon "+(i+total)+ " diffrent");
                   return;
               }

            }
            total+= ret1;
        }
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
