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


import java.awt.geom.Point2D;
import java.util.ArrayList;

class CycleModel {

    double[][] values = {
            {0, 0.2, 0.5, 0.7, 1},
            {0, 2, 0, 3, 1},
            {20, 360, 360, 360, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0}
    };
    int mModeValue;
    String mTargetString;
    final int POS = 0;
    final int PERIOD = 1;
    final int AMP = 2;
    final int OFFSET = 3;
    final int PHASE = 4;

    public int selected = 3;
    interface ModelListener {
        void update(CycleModel model);
    };
    ArrayList<ModelListener> listeners= new ArrayList<>();
    int mSelectedIndex  = 0;


    CycleEngine mCycleEngine = new CycleEngine(this);
    CycleModel( ) {

    }


    boolean inCallBack = false;
    String[] waveShapeName = {
            "sin", "square", "triangle", "sawtooth", "reverseSawtooth", "cos", "bounce"
    };
    int mAttrIndex = 3;

    public void addModelListener(ModelListener listener) {
        listeners.add( listener);
    }

    public void removeModelListener(ModelListener listener) {
        listeners.remove( listener);
    }

    public String[] getStrings() {
        String[] str = new String[values[POS].length];
        for (int i = 0; i < str.length; i++) {
            str[i] = "" + i;

        }
        return str;
    }

    public void delete() {
        double[][] nv = new double[values.length][values[POS].length - 1];
        for (int i = 0; i < values.length; i++) {
            int k = 0;
            for (int j = 0; j < values[i].length; j++) {
                if (selected != j) {
                    nv[i][k] = values[i][j];
                    k++;
                }
            }
        }
        values = nv;
        if (selected == nv[POS].length) {
            selected--;
        }
        update();
    }

    /**
     *
     */
    public void add() {
        double[][] nv = new double[values.length][values[POS].length + 1];
        for (int i = 0; i < values.length; i++) {
            int k = 0;
            for (int j = 0; j < values[i].length; j++) {
                if (selected == j) {
                    nv[i][k] = values[i][j];
                    if (j > 0) {
                        nv[i][k] = (values[i][j] + values[i][j - 1]) / 2;
                    }
                    k++;
                    nv[i][k] = values[i][j];
                    if (j < values[i].length - 1) {
                        nv[i][k] = (values[i][j] + values[i][j + 1]) / 2;
                    }
                    k++;
                    continue;
                }
                nv[i][k] = values[i][j];
                k++;
            }
        }
        values = nv;
        if (selected == nv[POS].length) {
            selected--;
        }
        update();
    }


    public void update() {

        for (ModelListener listener : listeners) {
            listener.update(this);
        }
    }

    public float getComputedValue(float v) {
        return mCycleEngine.getComputedValue(v);
    }

//    public void setDot(float x, float y) {
//        mCycleEngine.setDot(x, y);
//    }

    /**
     *
     */
    public void setPos(int value) {
        int v = value;
        if (inCallBack) {
            return;
        }

        int min = v;
        int max = v;
        if (selected > 0) {
            min = 1 + (int) (0.5 + (100 * values[POS][selected - 1]));
        }
        if (selected < values[POS].length - 1) {
            max = ((int) (0.5 + (100 * values[POS][selected + 1]))) - 1;
        }
        double pvalue = Math.max(min, Math.min(v, max)) / 100f;
        values[POS][selected] = pvalue;
        update();
    }

    public void setPeriod(float value) {
        if (inCallBack) {
            return;
        }
        values[PERIOD][selected] = value;
        update();
    }

    public void setAmp(int value) {
        if (inCallBack) {
            return;
        }
        int val = value;
        if (CycleEngine.MainAttribute.mapTo100[mAttrIndex]) {
            float min = CycleEngine.MainAttribute.typicalRange[mAttrIndex][0];
            float max = CycleEngine.MainAttribute.typicalRange[mAttrIndex][1];
            values[AMP][selected] = val * (max - min) / 100 + min;

        } else {
            values[AMP][selected] = val;
        }
        update();
    }

    public void setOffset(float offset) {
        if (inCallBack) {
            return;
        }
        if (CycleEngine.MainAttribute.mapTo100[mAttrIndex]) {
            float min = CycleEngine.MainAttribute.typicalRange[mAttrIndex][0];
            float max = CycleEngine.MainAttribute.typicalRange[mAttrIndex][1];
            values[OFFSET][selected] = offset * (max - min) / 100 + min;
        } else {
            values[OFFSET][selected] = offset;
        }
        update();
    }

    void setMode(int mode) {
        mModeValue = mode;
        update();
    }

    public void setSelected(int selectedIndex) {
        inCallBack = true;
        selected = selectedIndex;

        update();
        inCallBack = false;
    }


    public void changeSelection(int delta) {
        int length = values[POS].length;
        selected = (delta + selected + length) % length;


        update();
    }


    public int start_caret = 0;
    public int end_caret = 0;



    String getAttName() {
        return CycleEngine.MainAttribute.ShortNames[mAttrIndex];
    }

    public void setAttr(int selectedIndex) {
        int old = mAttrIndex;

        mAttrIndex = selectedIndex;
        float old_min = CycleEngine.MainAttribute.typicalRange[old][0];
        float old_max = CycleEngine.MainAttribute.typicalRange[old][1];
        float new_min = CycleEngine.MainAttribute.typicalRange[mAttrIndex][0];
        float new_max = CycleEngine.MainAttribute.typicalRange[mAttrIndex][1];
        for (int i = 0; i < values[AMP].length; i++) {
            double value = values[AMP][i];
            values[AMP][i] =
                    ((new_max - new_min) * ((value - old_min) / (old_max - old_min))) + new_min;
        }

        update();

    }

    public void selectClosest(Point2D p) {
        double min = Double.MAX_VALUE;
        int mini = -1;
        for (int i = 0; i < values[POS].length; i++) {
            double dist = p.distance(values[POS][i], values[OFFSET][i]);
            if (min > dist) {
                mini = i;
                min = dist;
            }
        }
        if (mini != -1) {
            selected = mini;

            update();
        }
    }

}
