/*
 * Copyright (C) 2016 The Android Open Source Project
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
package androidx.constraintlayout.core;

import androidx.constraintlayout.core.widgets.*;
import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileFilter;
import java.util.*;

import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * This test the the ConstraintWidget system buy loading XML that contain tags with there positions.
 * the xml files can be designed in android studio.
 */
@RunWith(Parameterized.class)
public class XmlBasedTest {
    private static final int ALLOWED_POSITION_ERROR = 1;
    HashMap<String, ConstraintWidget> widgetMap;
    HashMap<ConstraintWidget, String> boundsMap;
    ConstraintWidgetContainer container;
    ArrayList<Connection> connectionList;
    String file;

    public XmlBasedTest(String file) {
        this.file = file;
    }

    static class Connection {
        ConstraintWidget fromWidget;
        ConstraintAnchor.Type fromType, toType;
        String toName;
        int margin;
        int gonMargin = -1;
    }

    private static HashMap<String, Integer> visibilityMap = new HashMap<>();
    private static Map<String, Integer> stringWidthMap = new HashMap<String, Integer>();
    private static Map<String, Integer> stringHeightMap = new HashMap<String, Integer>();
    private static Map<String, Integer> buttonWidthMap = new HashMap<String, Integer>();
    private static Map<String, Integer> buttonHeightMap = new HashMap<String, Integer>();

    static {
        visibilityMap.put("gone", ConstraintWidget.GONE);
        visibilityMap.put("visible", ConstraintWidget.VISIBLE);
        visibilityMap.put("invisible", ConstraintWidget.INVISIBLE);
        stringWidthMap.put("TextView", 171);
        stringWidthMap.put("Button", 107);
        stringWidthMap.put("Hello World!", 200);
        stringHeightMap.put("TextView", 57);
        stringHeightMap.put("Button", 51);
        stringHeightMap.put("Hello World!", 51);
        String s = "12345678 12345678 12345678 12345678 12345678 12345678 12345678 12345678 12345678 12345678 12345678 12345678 12345678 12345678";
        stringWidthMap.put(s, 984);
        stringHeightMap.put(s, 204);
        buttonWidthMap.put("Button", 264);
        buttonHeightMap.put("Button", 144);
    }

    private static String rtl(String v) {
        if (v.equals("START")) return "LEFT";
        if (v.equals("END")) return "RIGHT";
        return v;
    }

    @Test
    public void testAccessToResources() {
        String dirName  = System.getProperty("user.dir") + "/src/test/resources/";
        assertTrue(" could not find dir " + dirName, new File(dirName).exists());
        Object[][]  names =  genListOfName();
        assertTrue(" Could not get Path " + dirName, names.length > 1);
    }

    @Parameterized.Parameters
    public static Object[][] genListOfName() {
        String dirName = System.getProperty("user.dir") + "/src/test/resources/";

        File[] f = new File(dirName).listFiles(pathname -> pathname.getName().startsWith("check"));
        assertNotNull(f);
        Arrays.sort(f, (o1, o2) -> o1.getName().compareTo(o2.getName()));

        Object[][] ret = new Object[f.length][1];
        for (int i = 0; i < ret.length; i++) {
            ret[i][0] = f[i].getAbsolutePath();
        }
        return ret;
    }

    String dim(ConstraintWidget w) {
        if (w instanceof Guideline) {
            return w.getLeft() + "," + w.getTop() + "," + 0 + "," + 0;
        }
        if (w.getVisibility() == ConstraintWidget.GONE) {
            return 0 + "," + 0 + "," + 0 + "," + 0;
        }
        return w.getLeft() + "," + w.getTop() + "," + w.getWidth() + "," + w.getHeight();
    }

    @Test
    public void testSolverXML() {
        parseXML(file);
        container.setOptimizationLevel(Optimizer.OPTIMIZATION_NONE);
        int[] perm = new int[boundsMap.size()];
        for (int i = 0; i < perm.length; i++) {
            perm[i] = i;
        }
        int total = fact(perm.length);
        int skip = 1 + total / 1000;
        populateContainer(perm);
        makeConnections();
        layout();
        validate();
        int k = 0;
        while (nextPermutation(perm)) {
            k++;
            if (k % skip != 0) continue;

            populateContainer(perm);
            makeConnections();
            layout();
            validate();

        }
    }

    @Test
    public void testDirectResolutionXML() {

        parseXML(file);
        container.setOptimizationLevel(Optimizer.OPTIMIZATION_STANDARD);
        int[] perm = new int[boundsMap.size()];
        for (int i = 0; i < perm.length; i++) {
            perm[i] = i;
        }
        int total = fact(perm.length);
        int skip = 1 + total / 1000;
        populateContainer(perm);
        makeConnections();
        layout();
        validate();
        int k = 0;
        while (nextPermutation(perm)) {
            k++;
            if (k % skip != 0) continue;

            populateContainer(perm);
            makeConnections();
            layout();
            validate();
        }
    }

    /**
     * Calculate the Factorial of n
     *
     * @param N input number
     * @return Factorial of n
     */
    public static int fact(int N) {
        int ret = 1;
        while (N > 0) {
            ret *= (N--);
        }
        return ret;
    }

    /**
     * Compare two string containing comer separated integers
     *
     * @param a
     * @param b
     * @return
     */
    private boolean isSame(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        String[] a_split = a.split(",");
        String[] b_split = b.split(",");
        if (a_split.length != b_split.length) {
            return false;
        }
        for (int i = 0; i < a_split.length; i++) {
            if (a_split[i].length() == 0) {
                return false;
            }
            int error = ALLOWED_POSITION_ERROR;
            if (b_split[i].startsWith("+")) {
                error += 10;
            }
            int a_value = Integer.parseInt(a_split[i]);
            int b_value = Integer.parseInt(b_split[i]);
            if (Math.abs(a_value - b_value) > error) {
                return false;
            }
        }
        return true;
    }

    /**
     * Simple dimension parser
     * Multiply dp units by 3 because we simulate a screen with 3 pixels per dp
     *
     * @param dim
     * @return
     */
    static int parseDim(String dim) {
        if (dim.endsWith("dp")) {
            return 3 * Integer.parseInt(dim.substring(0, dim.length() - 2));
        }
        if (dim.equals("wrap_content")) {
            return -1;
        }
        return -2;
    }

    /**
     * parse the XML file
     *
     * @param fileName
     */
    private void parseXML(String fileName) {
        container = new ConstraintWidgetContainer(0, 0, 1080, 1920);
        container.setDebugName("parent");
        widgetMap = new HashMap<String, ConstraintWidget>();
        boundsMap = new HashMap<ConstraintWidget, String>();

        connectionList = new ArrayList<Connection>();

        DefaultHandler handler = new DefaultHandler() {
            String parentId;

            public void startDocument() throws SAXException {
            }

            public void endDocument() throws SAXException {
            }

            public void startElement(String namespaceURI,
                                     String localName,
                                     String qName,
                                     Attributes attributes)
                    throws SAXException {

                if (qName != null) {

                    Map<String, String> androidAttrs = new HashMap<String, String>();
                    Map<String, String> appAttrs = new HashMap<String, String>();
                    Map<String, String> widgetConstraints = new HashMap<String, String>();
                    Map<String, String> widgetGoneMargins = new HashMap<String, String>();
                    Map<String, String> widgetMargins = new HashMap<String, String>();

                    for (int i = 0; i < attributes.getLength(); i++) {
                        String attrName = attributes.getLocalName(i);
                        String attrValue = attributes.getValue(i);
                        if (!attrName.contains(":")) {
                            continue;
                        }
                        if (attrValue.trim().isEmpty()) {
                            continue;
                        }
                        String[] parts = attrName.split(":");
                        String scheme = parts[0];
                        String attr = parts[1];
                        if (scheme.equals("android")) {
                            androidAttrs.put(attr, attrValue);

                            if (attr.startsWith("layout_margin")) {
                                widgetMargins.put(attr, attrValue);
                            }
                        } else if (scheme.equals("app")) {
                            appAttrs.put(attr, attrValue);

                            if (attr.equals("layout_constraintDimensionRatio")) {
                            } else if (attr.equals("layout_constraintGuide_begin")) {
                            } else if (attr.equals("layout_constraintGuide_percent")) {
                            } else if (attr.equals("layout_constraintGuide_end")) {
                            } else if (attr.equals("layout_constraintHorizontal_bias")) {
                            } else if (attr.equals("layout_constraintVertical_bias")) {
                            } else if (attr.startsWith("layout_constraint")) {
                                widgetConstraints.put(attr, attrValue);
                            }

                            if (attr.startsWith("layout_goneMargin")) {
                                widgetGoneMargins.put(attr, attrValue);
                            }
                        }
                    }

                    String id = androidAttrs.get("id");
                    String tag = androidAttrs.get("tag");
                    int layoutWidth = parseDim(androidAttrs.get("layout_width"));
                    int layoutHeight = parseDim(androidAttrs.get("layout_height"));
                    String text = androidAttrs.get("text");
                    String visibility = androidAttrs.get("visibility");
                    String orientation = androidAttrs.get("orientation");

                    if (qName.endsWith("ConstraintLayout")) {
                        if (id != null) {
                            container.setDebugName(id);
                        }
                        widgetMap.put(container.getDebugName(), container);
                        widgetMap.put("parent", container);
                    } else if (qName.endsWith("Guideline")) {
                        Guideline guideline = new Guideline();
                        if (id != null) {
                            guideline.setDebugName(id);
                        }
                        widgetMap.put(guideline.getDebugName(), guideline);
                        boundsMap.put(guideline, tag);
                        boolean horizontal = "horizontal".equals(orientation);
                        System.out.println("Guideline " + id + " " + (horizontal ? "HORIZONTAL" : "VERTICAL"));
                        guideline.setOrientation(horizontal ? Guideline.HORIZONTAL : Guideline.VERTICAL);

                        String constraintGuideBegin = appAttrs.get("layout_constraintGuide_begin");
                        String constraintGuidePercent = appAttrs.get("layout_constraintGuide_percent");
                        String constraintGuideEnd = appAttrs.get("layout_constraintGuide_end");

                        if (constraintGuideBegin != null) {
                            guideline.setGuideBegin(parseDim(constraintGuideBegin));
                            System.out.println("Guideline " + id + " setGuideBegin " + parseDim(constraintGuideBegin));

                        } else if (constraintGuidePercent != null) {
                            guideline.setGuidePercent(Float.parseFloat(constraintGuidePercent));
                            System.out.println("Guideline " + id + " setGuidePercent " + Float.parseFloat(constraintGuidePercent));

                        } else if (constraintGuideEnd != null) {
                            guideline.setGuideEnd(parseDim(constraintGuideEnd));
                            System.out.println("Guideline " + id + " setGuideBegin " + parseDim(constraintGuideEnd));
                        }
                        System.out.println(">>>>>>>>>>>>  " + guideline);

                    } else {
                        ConstraintWidget widget = new ConstraintWidget(200, 51);
                        widget.setBaselineDistance(28);

                        Connection[] connect = new Connection[5];

                        String widgetLayoutConstraintDimensionRatio = appAttrs.get("layout_constraintDimensionRatio");
                        String widgetLayoutConstraintHorizontalBias = appAttrs.get("layout_constraintHorizontal_bias");
                        String widgetLayoutConstraintVerticalBias = appAttrs.get("layout_constraintVertical_bias");

                        if (id != null) {
                            widget.setDebugName(id);
                        } else {
                            widget.setDebugName("widget" + (widgetMap.size() + 1));
                        }

                        if (tag != null) {
                            boundsMap.put(widget, tag);
                        }

                        ConstraintWidget.DimensionBehaviour hBehaviour = ConstraintWidget.DimensionBehaviour.FIXED;
                        if (layoutWidth == 0) {
                            hBehaviour = ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT;
                            widget.setDimension(layoutWidth, widget.getHeight());
                        } else if (layoutWidth == -1) {
                            hBehaviour = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
                        } else {
                            widget.setDimension(layoutWidth, widget.getHeight());
                        }
                        widget.setHorizontalDimensionBehaviour(hBehaviour);

                        ConstraintWidget.DimensionBehaviour vBehaviour = ConstraintWidget.DimensionBehaviour.FIXED;
                        if (layoutHeight == 0) {
                            vBehaviour = ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT;
                            widget.setDimension(widget.getWidth(), layoutHeight);
                        } else if (layoutHeight == -1) {
                            vBehaviour = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
                        } else {
                            widget.setDimension(widget.getWidth(), layoutHeight);
                        }
                        widget.setVerticalDimensionBehaviour(vBehaviour);

                        if (text != null) {
                            System.out.print("text = \"" + text + "\"");
                            Map<String, Integer> wmap = (qName.equals("Button")) ? buttonWidthMap : stringWidthMap;
                            Map<String, Integer> hmap = (qName.equals("Button")) ? buttonHeightMap : stringHeightMap;
                            if (wmap.containsKey(text) && widget.getHorizontalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                                widget.setWidth(wmap.get(text));
                            }
                            if (hmap.containsKey(text) && widget.getVerticalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                                widget.setHeight(hmap.get(text));
                            }
                        }

                        if (visibility != null) {
                            widget.setVisibility(visibilityMap.get(visibility));
                        }

                        if (widgetLayoutConstraintDimensionRatio != null) {
                            widget.setDimensionRatio(widgetLayoutConstraintDimensionRatio);
                        }

                        if (widgetLayoutConstraintHorizontalBias != null) {
                            System.out.println("widgetLayoutConstraintHorizontalBias " + widgetLayoutConstraintHorizontalBias);
                            widget.setHorizontalBiasPercent(Float.parseFloat(widgetLayoutConstraintHorizontalBias));
                        }

                        if (widgetLayoutConstraintVerticalBias != null) {
                            System.out.println("widgetLayoutConstraintVerticalBias " + widgetLayoutConstraintVerticalBias);
                            widget.setVerticalBiasPercent(Float.parseFloat(widgetLayoutConstraintVerticalBias));
                        }

                        Set<String> constraintKeySet = widgetConstraints.keySet();
                        String[] constraintKeys = constraintKeySet.toArray(new String[constraintKeySet.size()]);
                        for (int i = 0; i < constraintKeys.length; i++) {
                            String attrName = constraintKeys[i];
                            String attrValue = widgetConstraints.get(attrName);
                            String[] sp = attrName.substring("layout_constraint".length()).split("_to");
                            String fromString = rtl(sp[0].toUpperCase());
                            ConstraintAnchor.Type from = ConstraintAnchor.Type.valueOf(fromString);
                            String toString = rtl(sp[1].substring(0, sp[1].length() - 2).toUpperCase());
                            ConstraintAnchor.Type to = ConstraintAnchor.Type.valueOf(toString);
                            int side = from.ordinal() - 1;
                            if (connect[side] == null) {
                                connect[side] = new Connection();
                            }
                            connect[side].fromWidget = widget;
                            connect[side].fromType = from;
                            connect[side].toType = to;
                            connect[side].toName = attrValue;
                        }

                        Set<String> goneMarginSet = widgetGoneMargins.keySet();
                        String[] goneMargins = goneMarginSet.toArray(new String[goneMarginSet.size()]);
                        for (int i = 0; i < goneMargins.length; i++) {
                            String attrName = goneMargins[i];
                            String attrValue = widgetGoneMargins.get(attrName);
                            String marginSide = rtl(attrName.substring("layout_goneMargin".length()).toUpperCase());
                            ConstraintAnchor.Type marginType = ConstraintAnchor.Type.valueOf(marginSide);
                            int side = marginType.ordinal() - 1;
                            if (connect[side] == null) {
                                connect[side] = new Connection();
                            }
                            connect[side].gonMargin = 3 * Integer.parseInt(attrValue.substring(0, attrValue.length() - 2));
                        }

                        Set<String> marginSet = widgetMargins.keySet();
                        String[] margins = marginSet.toArray(new String[marginSet.size()]);
                        for (int i = 0; i < margins.length; i++) {
                            String attrName = margins[i];
                            String attrValue = widgetMargins.get(attrName);
                            // System.out.println("margin [" + attrName + "] by [" + attrValue +"]");
                            String marginSide = rtl(attrName.substring("layout_margin".length()).toUpperCase());
                            ConstraintAnchor.Type marginType = ConstraintAnchor.Type.valueOf(marginSide);
                            int side = marginType.ordinal() - 1;
                            if (connect[side] == null) {
                                connect[side] = new Connection();
                            }
                            connect[side].margin = 3 * Integer.parseInt(attrValue.substring(0, attrValue.length() - 2));
                        }

                        widgetMap.put(widget.getDebugName(), widget);

                        for (int i = 0; i < connect.length; i++) {
                            if (connect[i] != null) {
                                connectionList.add(connect[i]);
                            }
                        }

                    }
                }


            }
        };

        File file = new File(fileName);
        SAXParserFactory spf = SAXParserFactory.newInstance();

        try {
            SAXParser saxParser = spf.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(handler);
            xmlReader.parse(file.toURI().toString());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateContainer(int[] order) {
        System.out.println(Arrays.toString(order));
        ConstraintWidget[] widgetSet = boundsMap.keySet().toArray(new ConstraintWidget[0]);
        for (int i = 0; i < widgetSet.length; i++) {
            ConstraintWidget widget = widgetSet[order[i]];
            if (widget.getDebugName().equals("parent")) {
                continue;
            }
            ConstraintWidget.DimensionBehaviour hBehaviour = widget.getHorizontalDimensionBehaviour();
            ConstraintWidget.DimensionBehaviour vBehaviour = widget.getVerticalDimensionBehaviour();

            if (widget instanceof Guideline) {
                Guideline copy = new Guideline();
                copy.copy(widget, new HashMap<>());
                container.remove(widget);
                widget.copy(copy, new HashMap<>());
            } else {
                ConstraintWidget copy = new ConstraintWidget();
                copy.copy(widget, new HashMap<>());
                container.remove(widget);
                widget.copy(copy, new HashMap<>());
            }
            widget.setHorizontalDimensionBehaviour(hBehaviour);
            widget.setVerticalDimensionBehaviour(vBehaviour);
            container.add(widget);
        }
    }

    private void makeConnections() {
        for (Connection connection : connectionList) {
            ConstraintWidget toConnect;
            if (connection.toName.equalsIgnoreCase("parent") || connection.toName.equals(container.getDebugName())) {
                toConnect = container;
            } else {
                toConnect = widgetMap.get(connection.toName);
            }
            if (toConnect == null) {
                System.err.println("   " + connection.toName);
            } else {
                connection.fromWidget.connect(connection.fromType, toConnect, connection.toType, connection.margin);
                connection.fromWidget.setGoneMargin(connection.fromType, connection.gonMargin);
            }
        }
    }

    private void layout() {
        container.layout();
    }

    private void validate() {
        ConstraintWidgetContainer root = (ConstraintWidgetContainer) widgetMap.remove("parent");

        String[] keys = widgetMap.keySet().toArray(new String[0]);
        boolean ok = true;
        StringBuilder layout = new StringBuilder("\n");
        for (String key : keys) {
            if (key.contains("activity_main")) {
                continue;
            }
            ConstraintWidget widget = widgetMap.get(key);
            String bounds = boundsMap.get(widget);
            String dim = dim(widget);
            boolean same = isSame(dim, bounds);
            String compare = rightPad(key, 17) + rightPad(dim, 15) + "   " + bounds;
            ok &= same;
            layout.append(compare).append("\n");
        }
        assertTrue(layout.toString(), ok);
    }

    private static String rightPad(String s, int n) {
        s = s + new String(new byte[n]).replace('\0', ' ');
        return s.substring(0, n);
    }

    private static String R(String s) {
        s = "             " + s;
        return s.substring(s.length() - 13);
    }

    /**
     * Ordered array (1,2,3...) will be cycled till the order is reversed (9,8,7...)
     *
     * @param array to be carried
     * @return false when the order is reversed
     */
    private static boolean nextPermutation(int[] array) {
        int i = array.length - 1;
        while (i > 0 && array[i - 1] >= array[i]) {
            i--;
        }
        if (i <= 0)
            return false;
        int j = array.length - 1;
        while (array[j] <= array[i - 1]) {
            j--;
        }

        int temp = array[i - 1];
        array[i - 1] = array[j];
        array[j] = temp;

        j = array.length - 1;
        while (i < j) {
            temp = array[i];
            array[i] = array[j];
            array[j] = temp;
            i++;
            j--;
        }

        return true;
    }

    @Test
    public void SimpleTest() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1080, 1920);

        final ConstraintWidget A = new ConstraintWidget(0, 0, 200, 51);
        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        A.setDebugName("A");
        A.connect(ConstraintAnchor.Type.LEFT, root, ConstraintAnchor.Type.LEFT, 0);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 0);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, 0);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM, 0);
        root.add(A);
        root.layout();
        System.out.println("f) A: " + A + " " + A.getWidth() + "," + A.getHeight());
    }

    @Test
    public void GuideLineTest() {
        ConstraintWidgetContainer root = new ConstraintWidgetContainer(0, 0, 1080, 1920);
        final ConstraintWidget A = new ConstraintWidget(0, 0, 200, 51);
        final Guideline guideline = new Guideline();
        root.add(guideline);

        guideline.setGuidePercent(0.50f);
        guideline.setOrientation(Guideline.VERTICAL);
        guideline.setDebugName("guideline");

        A.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        A.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
        A.setDebugName("A");
        A.connect(ConstraintAnchor.Type.LEFT, guideline, ConstraintAnchor.Type.LEFT, 0);
        A.connect(ConstraintAnchor.Type.TOP, root, ConstraintAnchor.Type.TOP, 0);
        A.connect(ConstraintAnchor.Type.RIGHT, root, ConstraintAnchor.Type.RIGHT, 0);
        A.connect(ConstraintAnchor.Type.BOTTOM, root, ConstraintAnchor.Type.BOTTOM, 0);
        root.add(A);

        root.layout();
        System.out.println("f) A: " + A + " " + A.getWidth() + "," + A.getHeight());
        System.out.println("f) A: " + guideline + " " + guideline.getWidth() + "," + guideline.getHeight());

    }


}
