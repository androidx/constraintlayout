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

    public final static int START = 0;
    public final static int END = 1;
    public final static int CURRENT = 2;

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

        public void interpolate(float progress) {
            WidgetFrame.interpolate(interpolated, start, end, progress);
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

    public void interpolate(float progress) {
        for (String key : state.keySet()) {
            WidgetState widget = state.get(key);
            widget.interpolate(progress);
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
