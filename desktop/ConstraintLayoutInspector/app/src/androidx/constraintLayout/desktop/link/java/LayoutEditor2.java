package androidx.constraintLayout.desktop.link.java;

import androidx.constraintLayout.desktop.link.DesignSurfaceModification;
import androidx.constraintLayout.desktop.link.MotionLink;
import androidx.constraintLayout.desktop.link.Widget;
import androidx.constraintLayout.desktop.scan.WidgetFrameUtils;
import androidx.constraintLayout.desktop.ui.adapters.vd.ListIcons;
import androidx.constraintLayout.desktop.ui.adapters.vg.VDIcon;
import androidx.constraintLayout.desktop.utils.ScenePicker;
import androidx.constraintlayout.core.parser.CLElement;
import androidx.constraintlayout.core.parser.CLKey;
import androidx.constraintlayout.core.parser.CLObject;
import androidx.constraintlayout.core.parser.CLParsingException;
import androidx.constraintlayout.core.widgets.ConstraintWidget;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class LayoutEditor2 extends LayoutView2 {

    private int pressX = 0;
    private int pressY = 0;
    private final MotionLink link;
    ScenePicker scenePicker = new ScenePicker();

    Object currentDragElement = null;

    int currentX = 0;
    int rangeX = 0;
    int offsetX = 0;

    int currentY = 0;
    int rangeY = 0;
    int offsetY = 0;

    boolean dragging = false;

    DesignSurfaceModification designSurfaceModificationCallback = null;

    ArrayList<GuidelineModel> guidelines = new ArrayList<GuidelineModel>();

    private int density = 3;
    ResizeHandle resize;

    public LayoutEditor2(LayoutInspector2 inspector) {
        super(inspector);
        link = inspector.motionLink;
        resize = new ResizeHandle();
        scenePicker.setSelectListener((over, dist) -> currentDragElement = over);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    scenePicker.find(e.getX(), e.getY());
                    if (currentDragElement instanceof ResizeHandle) {
                        link.sendLayoutDimensions(Integer.MIN_VALUE, Integer.MIN_VALUE);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                scenePicker.find(e.getX(), e.getY());
                if (currentDragElement != null) {
                    dragging = true;
                    pressX = e.getX();
                    pressY = e.getY();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                currentDragElement = null;
                dragging = false;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (currentDragElement instanceof LayoutEditor2.GuidelineModel) {
                    float value = 0f;
                    if (currentDragElement instanceof LayoutEditor2.HorizontalGuideline) {
                        currentY = e.getY();
                        value = (currentY - offsetY) / (float) rangeY;
                    } else if (currentDragElement instanceof LayoutEditor2.VerticalGuideline) {
                        currentX = e.getX();
                        value = (currentX - offsetX) / (float) rangeX;
                    }
                    value = Math.max(0f, Math.min(1f, value));
                    value = (int) (value * 100) / 100f;

                    String target = (currentDragElement instanceof LayoutEditor2.GuidelineModel) ? getName() : null;
                    if (designSurfaceModificationCallback != null) {
                        CLElement element = designSurfaceModificationCallback.getElement(target);
                        if (element instanceof CLObject) {
                            ((CLObject) element).putNumber("percent", value);
                            designSurfaceModificationCallback.updateElement(target, element);
                        }
                    }
                } else if (currentDragElement instanceof ResizeHandle) {
                    float dx = (pressX - offX);
                    float dy = (pressY - offY);
                    float x = (e.getX() - offX) * (lastRootWidth / dx);
                    float y = (e.getY() - offY) * (lastRootHeight / dy);
                    link.sendLayoutDimensions((int) x, (int) y);
                }
            }
        });

    }

    void computeScale(float rootWidth, float rootHeight) {
        if (!dragging) {
            super.computeScale(rootWidth, rootHeight);
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (widgets.size() == 0) {
            return;
        }
        Widget root = widgets.get(0);
        float rootWidth = root.width();
        float rootHeight = root.height();
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.translate(offX, offY);

        scenePicker.reset();
        offsetX = (int) offX;
        rangeX = (int) (rootWidth * scaleX);
        offsetY = (int) offY;
        rangeY = (int) (rootHeight * scaleY);

        for (GuidelineModel guideline : guidelines) {
            guideline.draw(g2, rangeX, rangeY);
            guideline.addToPicker(scenePicker, offsetX, offsetY, rangeX, rangeY);
        }

        resize.draw(g2, rangeX, rangeY);
        resize.addToPicker(scenePicker, offsetX, offsetY, rangeX, rangeY);
    }

    void setModel(CLObject model) {
        guidelines.clear();
        if (model.has("ConstraintSets")) {
            // For now don't operate on MotionScenes
            return;
        }
        try {
        int count = model.size();
        for (int i = 0; i < count; i++) {
            CLElement element = model.get(i);
            if (element instanceof CLKey) {
                CLElement value = ((CLKey) element).getValue();
                if (value instanceof CLObject && ((CLObject) value).has("type")) {
                    String type = ((CLObject) value).getString("type");
                    switch (type) {
                        case "hGuideline":
                            addGuideline(element.content(),  ((CLObject) value), ConstraintWidget.HORIZONTAL);
                            break;
                        case "vGuideline":
                            addGuideline(element.content(),  ((CLObject) value), ConstraintWidget.VERTICAL);
                            break;
                    }
                }
            }
        }
        }
        catch (Exception ex) {

        }
        for (GuidelineModel guideline : guidelines) {
            for (Widget widget : widgets) {
                if (guideline.name.equals(widget.getId())) {
                    widget.setGuideline(true);;
                }
            }
        }
        repaint();
    }

    class ResizeHandle {
        VDIcon icon =  new VDIcon(ListIcons.getStream("resize.xml"));
        int gap = 4;
        int size = 30;
        ResizeHandle() {
        }

        void draw(Graphics2D g, int w, int h) {
            g.setColor(Color.BLUE);
            icon.paint( g,w + gap, h + gap, size, size);
        }

        void addToPicker(ScenePicker scenePicker, int ox, int oy, int w, int h) {
            int ih = icon.getIconHeight();
            int iw =  icon.getIconWidth();
            int x1 = ox + w + gap;
            int x2 = x1 + iw;
            int y1 = oy + h + gap;
            int y2 = y1 + ih;
            scenePicker.addRect(this, 0, x1, y1, x2, y2);
        }
    }

    abstract class GuidelineModel {
        String name;
        float percent = 0;

        GuidelineModel(String id, float p) {
            name = id;
            percent = p;
        }

        GuidelineModel(String id) {
            name = id;
        }

        abstract void draw(Graphics2D g, int w, int h);

        abstract void addToPicker(ScenePicker scenePicker, int ox, int oy, int w, int h);
    }

    class HorizontalGuideline extends GuidelineModel {
        BufferedImage img;
        int gap = 2;

        HorizontalGuideline(String key, CLObject element) throws CLParsingException {
            super(key, element.getFloat("percent"));
            try {
                img = ImageIO.read(new File("images/guideline-horiz.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        void draw(Graphics2D g, int w, int h) {
            g.setColor(WidgetFrameUtils.theme.pathColor());
            int y = (int) (h * percent);
            g.drawLine(0, y, w, y);
            drawImage(g, 0, y);
        }

        void drawImage(Graphics2D g, int x, int y) {
            int h = img.getHeight();
            int w = img.getWidth();
            g.drawImage(img, -gap - w, y - h / 2, null);
        }

        @Override
        void addToPicker(ScenePicker scenePicker, int ox, int oy, int w, int h) {
            int ih = img.getHeight();
            int iw = img.getWidth();
            int y = (int) (h * percent);
            int x1 = ox - gap - iw;
            int x2 = x1 + iw;
            int y1 = oy + y - ih / 2;
            int y2 = y1 + ih;
            scenePicker.addRect(this, 0, x1, y1, x2, y2);
        }
    }

    class VerticalGuideline extends GuidelineModel {
        BufferedImage img;
        int gap = 2;

        VerticalGuideline(String key, CLObject element) throws CLParsingException {
            super(key, element.getFloat("percent"));
            try {
                img = ImageIO.read(new File("images/guideline-vert.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        void draw(Graphics2D g, int w, int h) {
            g.setColor(WidgetFrameUtils.theme.pathColor());
            int x = (int) (w * percent);
            g.drawLine(x, 0, x, h);
            drawImage(g, x, 0);
        }

        void drawImage(Graphics2D g, int x, int y) {
            int h = img.getHeight();
            int w = img.getWidth();
            g.drawImage(img, x - w / 2, -gap - h, null);
        }

        @Override
        void addToPicker(ScenePicker scenePicker, int ox, int oy, int w, int h) {
            int ih = img.getHeight();
            int iw = img.getWidth();
            int x = (int) (w * percent);
            int x1 = ox + x - iw / 2;
            int x2 = x1 + iw;
            int y1 = oy - gap - ih;
            int y2 = y1 + ih;
            scenePicker.addRect(this, 0, x1, y1, x2, y2);
        }
    }

    private void addGuideline(String name, CLObject element, int orientation) {
        try {
            switch (orientation) {
                case ConstraintWidget.HORIZONTAL: {
                    GuidelineModel guideline = new HorizontalGuideline(name, element);
                    guidelines.add(guideline);
                }
                break;
                case ConstraintWidget.VERTICAL: {
                    GuidelineModel guideline = new VerticalGuideline(name, element);
                    guidelines.add(guideline);
                }
                break;
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setDesignSurfaceModificationCallback(DesignSurfaceModification designSurfaceModificationCallback) {
        this.designSurfaceModificationCallback = designSurfaceModificationCallback;
    }

}
