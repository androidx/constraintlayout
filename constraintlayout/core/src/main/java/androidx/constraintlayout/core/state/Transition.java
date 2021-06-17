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

package androidx.constraintlayout.core.state;

import androidx.constraintlayout.core.widgets.ConstraintWidget;
import androidx.constraintlayout.core.widgets.ConstraintWidgetContainer;

import java.util.ArrayList;
import java.util.HashMap;

public class Transition {
    HashMap<String, WidgetState> state = new HashMap<>();
    HashMap<Integer, HashMap<String, KeyPosition>> keyPositions = new HashMap<>();

    public final static int START = 0;
    public final static int END = 1;
    public final static int CURRENT = 2;

    public KeyPosition findPreviousPosition(String target, int frameNumber) {
        while (frameNumber >= 0) {
            HashMap<String, KeyPosition> map = keyPositions.get(frameNumber);
            if (map != null) {
                KeyPosition keyPosition = map.get(target);
                if (keyPosition != null) {
                    return keyPosition;
                }
            }
            frameNumber--;
        }
        return null;
    }

    public KeyPosition findNextPosition(String target, int frameNumber) {
        while (frameNumber <= 100) {
            HashMap<String, KeyPosition> map = keyPositions.get(frameNumber);
            if (map != null) {
                KeyPosition keyPosition = map.get(target);
                if (keyPosition != null) {
                    return keyPosition;
                }
            }
            frameNumber++;
        }
        return null;
    }

    public int getNumberKeyPositions(WidgetFrame frame) {
        int numKeyPositions = 0;
        int frameNumber = 0;
        while (frameNumber <= 100) {
            HashMap<String, KeyPosition> map = keyPositions.get(frameNumber);
            if (map != null) {
                KeyPosition keyPosition = map.get(frame.widget.stringId);
                if (keyPosition != null) {
                    numKeyPositions++;
                }
            }
            frameNumber++;
        }
        return numKeyPositions;
    }

    public void fillKeyPositions(WidgetFrame frame, float[] x, float[] y, float[] pos) {
        int numKeyPositions = 0;
        int frameNumber = 0;
        while (frameNumber <= 100) {
            HashMap<String, KeyPosition> map = keyPositions.get(frameNumber);
            if (map != null) {
                KeyPosition keyPosition = map.get(frame.widget.stringId);
                if (keyPosition != null) {
                    x[numKeyPositions] = keyPosition.x;
                    y[numKeyPositions] = keyPosition.y;
                    pos[numKeyPositions] = keyPosition.frame;
                    numKeyPositions++;
                }
            }
            frameNumber++;
        }
    }

    public boolean hasPositionKeyframes() {
        return keyPositions.size() > 0;
    }

    static class WidgetState {
        WidgetFrame start;
        WidgetFrame end;
        WidgetFrame interpolated;

        public WidgetState() {
            start = new WidgetFrame();
            end = new WidgetFrame();
            interpolated = new WidgetFrame();
        }

        public WidgetState(ConstraintWidget child) {
            start = new WidgetFrame(child);
            end = new WidgetFrame(child);
            interpolated = new WidgetFrame(child);
        }

        public void interpolate(int parentWidth, int parentHeight, float progress, Transition transition) {
            WidgetFrame.interpolate(parentWidth, parentHeight, interpolated, start, end, transition, progress);
        }
    }

    static class KeyPosition {
        int frame;
        String target;
        int type;
        float x;
        float y;

        public KeyPosition(String target, int frame, int type, float x, float y) {
            this.target = target;
            this.frame = frame;
            this.type = type;
            this.x = x;
            this.y = y;
        }
    }

    public boolean isEmpty() {
        return state.isEmpty();
    }

    public void clear() {
        state.clear();
    }

    public boolean contains(String key) {
        return state.containsKey(key);
    }

    public void addKeyPosition(String target, int frame, int type, float x, float y) {
        KeyPosition keyPosition = new KeyPosition(target, frame, type, x, y);
        HashMap<String, KeyPosition> map = keyPositions.get(frame);
        if (map == null) {
            map = new HashMap<>();
            keyPositions.put(frame, map);
        }
        map.put(target, keyPosition);
    }

    public void addCustomFloat(int state, String widgetId, String property, float value) {
        WidgetState widgetState = this.state.get(widgetId);
        if (widgetState == null) {
            widgetState = new WidgetState();
            this.state.put(widgetId, widgetState);
        }
        if (state == START) {
            widgetState.start.addCustomFloat(property, value);
        } else if (state == END) {
            widgetState.end.addCustomFloat(property, value);
        } else {
            widgetState.interpolated.addCustomFloat(property, value);
        }
    }

    public void addCustomColor(int state, String widgetId, String property,
                               float r, float g, float b, float a) {
        WidgetState widgetState = this.state.get(widgetId);
        if (widgetState == null) {
            widgetState = new WidgetState();
            this.state.put(widgetId, widgetState);
        }
        if (state == START) {
            widgetState.start.addCustomColor(property, r, g, b, a);
        } else if (state == END) {
            widgetState.end.addCustomColor(property, r, g, b, a);
        } else {
            widgetState.interpolated.addCustomColor(property, r, g, b, a);
        }
    }

    public void updateFrom(WidgetFrame frame, int state, String id) {
        WidgetState widgetState = this.state.get(id);
        if (widgetState == null) {
            widgetState = new WidgetState(null);
        }
        if (state == START) {
            widgetState.start = frame;
        } else if (state == END) {
            widgetState.end = frame;
        } else {
            widgetState.interpolated = frame;
        }
        this.state.put(id, widgetState);
    }

    public void updateFrom(ConstraintWidgetContainer container, int state) {
        final ArrayList<ConstraintWidget> children = container.getChildren();
        final int count = children.size();
        for (int i = 0; i < count; i++) {
            ConstraintWidget child = children.get(i);
            WidgetState widgetState = this.state.get(child.stringId);
            if (widgetState == null) {
                widgetState = new WidgetState(child);
                this.state.put(child.stringId, widgetState);
            }
            if (state == START) {
                widgetState.start.update(child);
            } else if (state == END) {
                widgetState.end.update(child);
            } else {
                widgetState.interpolated.update(child);
            }
        }
    }

    public void interpolate(int parentWidth, int parentHeight, float progress) {
        for (String key : state.keySet()) {
            WidgetState widget = state.get(key);
            widget.interpolate(parentWidth, parentHeight, progress, this);
        }
    }

    public WidgetFrame getStart(String id) {
        WidgetState widgetState = state.get(id);
        if (widgetState == null) {
            return null;
        }
        return widgetState.start;
    }

    public WidgetFrame getEnd(String id) {
        WidgetState widgetState = state.get(id);
        if (widgetState == null) {
            return null;
        }
        return widgetState.end;
    }

    public WidgetFrame getInterpolated(String id) {
        WidgetState widgetState = state.get(id);
        if (widgetState == null) {
            return null;
        }
        return widgetState.interpolated;
    }

    public WidgetFrame getStart(ConstraintWidget child) {
        WidgetState widgetState = this.state.get(child.stringId);
        if (widgetState == null) {
            widgetState = new WidgetState(child);
            state.put(child.stringId, widgetState);
        }
        return widgetState.start;
    }

    public WidgetFrame getEnd(ConstraintWidget child) {
        WidgetState widgetState = this.state.get(child.stringId);
        if (widgetState == null) {
            widgetState = new WidgetState(child);
            state.put(child.stringId, widgetState);
        }
        return widgetState.end;
    }

    public WidgetFrame getInterpolated(ConstraintWidget child) {
        WidgetState widgetState = this.state.get(child.stringId);
        if (widgetState == null) {
            widgetState = new WidgetState(child);
            state.put(child.stringId, widgetState);
        }
        return widgetState.interpolated;
    }

}
