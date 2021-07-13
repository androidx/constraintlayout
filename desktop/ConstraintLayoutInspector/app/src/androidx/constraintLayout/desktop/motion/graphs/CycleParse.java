/*
 * Copyright (C) 2021 The Android Open Source Project
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
package androidx.constraintLayout.desktop.motion.graphs;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

public class CycleParse {

    class ParseResults {

        double[][] values = new double[4][0];
        final int POS = 0;
        final int PERIOD = 1;
        final int AMP = 2;
        final int OFFSET = 3;

        int current = -1;
        double tmpPos;
        double tmpPeriod;
        double tmpValue;
        double tmpOffset;
        String tmpTarget;
        int tmpShape;
        CycleEngine.Prop tmpValueType;

        void add() {
            for (int i = 0; i < values.length; i++) {
                values[i] = Arrays.copyOf(values[i], values[i].length + 1);
            }
            current = values[0].length - 1;
            values[POS][current] = tmpPos;
            values[PERIOD][current] = tmpPeriod;
            values[AMP][current] = tmpValue;
            values[OFFSET][current] = tmpOffset;
            target = tmpTarget;
            shape = tmpShape;
            valueType = tmpValueType;

        }

        void setFramePosition(double v) {
            tmpPos = v;
        }

        void setWavePeriod(double v) {
            tmpPeriod = v;
        }

        void setWaveValue(double v) {
            tmpValue = v;
        }

        void setWaveOffset(double v) {
            tmpOffset = v;
        }

        void setTarget(String v) {
            tmpTarget = v;
        }

        void setShape(int v) {
            tmpShape = v;
        }

        void setValueType(CycleEngine.Prop v) {
            tmpValueType = v;
        }

        String target;
        int shape;
        CycleEngine.Prop valueType;
    }

    public void parseXML(String str, CycleModel model) {
        try {
            InputStream inputStream = new ByteArrayInputStream(str.getBytes(Charset.forName("UTF-8")));
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            ParseResults results = new ParseResults();
            saxParser.parse(inputStream, new DefaultHandler() {
                public void startElement(String uri, String localName,
                                         String qName, Attributes attributes)
                        throws SAXException {
                    if ("KeyCycle".equals(qName)) {

                        for (int i = 0; i < attributes.getLength(); i++) {
                            switch (attributes.getQName(i)) {
                                case "motion:framePosition":
                                    results.setFramePosition(Integer.parseInt(attributes.getValue(i)) / 100f);
                                    break;
                                case "motion:target":
                                    results.setTarget(attributes.getValue(i).substring(5));
                                    break;
                                case "motion:wavePeriod":
                                    results.setWavePeriod(Float.parseFloat(attributes.getValue(i)));
                                    break;
                                case "motion:waveOffset":
                                    results.setWaveOffset(Float.parseFloat(trimDp(attributes.getValue(i))));
                                    break;
                                case "motion:waveShape":
                                    String shape = attributes.getValue(i);
                                    for (int j = 0; j < model.waveShapeName.length; j++) {
                                        if (model.waveShapeName[j].equals(shape)) {
                                            results.setShape(j);
                                        }
                                    }
                                    break;
                                case "motion:transitionPathRotate":
                                    results.setValueType(CycleEngine.Prop.PATH_ROTATE);
                                    results.setWaveValue(Float.parseFloat(trimDp(attributes.getValue(i))));
                                    break;
                                case "android:alpha":
                                    results.setValueType(CycleEngine.Prop.ALPHA);
                                    results.setWaveValue(Float.parseFloat(trimDp(attributes.getValue(i))));
                                    break;
                                case "android:elevation":
                                    results.setValueType(CycleEngine.Prop.ELEVATION);
                                    results.setWaveValue(Float.parseFloat(trimDp(attributes.getValue(i))));
                                    break;
                                case "android:rotation":
                                    results.setValueType(CycleEngine.Prop.ROTATION);
                                    results.setWaveValue(Float.parseFloat(trimDp(attributes.getValue(i))));
                                    break;
                                case "android:rotationX":
                                    results.setValueType(CycleEngine.Prop.ROTATION_X);
                                    results.setWaveValue(Float.parseFloat(trimDp(attributes.getValue(i))));
                                    break;
                                case "android:rotationY":
                                    results.setValueType(CycleEngine.Prop.ROTATION_Y);
                                    results.setWaveValue(Float.parseFloat(trimDp(attributes.getValue(i))));
                                    break;
                                case "android:scaleX":
                                    results.setValueType(CycleEngine.Prop.SCALE_X);
                                    results.setWaveValue(Float.parseFloat(trimDp(attributes.getValue(i))));
                                    break;
                                case "android:scaleY":
                                    results.setValueType(CycleEngine.Prop.SCALE_Y);
                                    results.setWaveValue(Float.parseFloat(trimDp(attributes.getValue(i))));
                                    break;
                                case "android:translationX":
                                    results.setValueType(CycleEngine.Prop.TRANSLATION_X);
                                    results.setWaveValue(Float.parseFloat(trimDp(attributes.getValue(i))));
                                    break;
                                case "android:translationY":
                                    results.setValueType(CycleEngine.Prop.TRANSLATION_Y);
                                    results.setWaveValue(Float.parseFloat(trimDp(attributes.getValue(i))));
                                    break;
                                case "android:translationZ":
                                    results.setValueType(CycleEngine.Prop.TRANSLATION_Z);
                                    results.setWaveValue(Float.parseFloat(trimDp(attributes.getValue(i))));
                                    break;
                                case "motion:progress":
                                    results.setValueType(CycleEngine.Prop.PROGRESS);
                                    results.setWaveValue(Float.parseFloat(trimDp(attributes.getValue(i))));
                                    break;
                            }
                        }
                        if (results.tmpValueType.ordinal() == model.mAttrIndex) {
                            results.add();
                        }
                    }

                }

                public void endElement(String uri, String localName, String qName)
                        throws SAXException {

                }
            });
            model.values = results.values;
            model.selected = model.values[model.POS].length / 2;
            model.mSelectedIndex = results.shape;
            model.mTargetString = results.target;
            model.mAttrIndex = results.valueType.ordinal();
            model.update();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getKeyFrames(CycleModel model) {
        String str = "\n";
        model.start_caret = 1;
        model.end_caret = 1;
        String target = model.mTargetString;
        for (int i = 0; i < model.values[model.POS].length; i++) {
            double pos = model.values[model.POS][i];
            double per = model.values[model.PERIOD][i];
            double amp = model.values[model.AMP][i];
            double off = model.values[model.OFFSET][i];
            String xmlstr = "<KeyCycle \n";
            xmlstr += "        motion:framePosition=\"" + (int) (0.5 + pos * 100) + "\"\n";
            xmlstr += "        motion:target=\"@+id/" + target + "\"\n";
            xmlstr += "        motion:wavePeriod=\"" + (int) (per) + "\"\n";
            xmlstr += "        motion:waveOffset=\"" + CycleEngine.MainAttribute.process(off, model.mAttrIndex) + "\"\n";
            xmlstr += "        motion:waveShape=\"" + model.waveShapeName[model.selected] + "\"\n";
            xmlstr += "        " + CycleEngine.MainAttribute.Names[model.mAttrIndex] + "=\"" + CycleEngine.MainAttribute
                    .process(amp, model.mAttrIndex) + "\"/>\n\n";
            if (model.selected == i) {
                model.start_caret = str.length();
            }
            str += xmlstr;
            if (model.selected == i) {
                model.end_caret = str.length();
            }
        }
        return str;
    }

    private static String trimDp(String v) {
        if (v.lastIndexOf("dp") != -1) {
            return v.substring(0, v.lastIndexOf("dp"));
        }
        return v;
    }


}
