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

package org.constraintlayout.swing;

import androidx.constraintlayout.core.parser.*;
import androidx.constraintlayout.core.state.Registry;
import androidx.constraintlayout.core.state.RegistryCallback;

class ConstraintSetParser {

    private String exportedName;

    public void parse(String content, ConstraintLayoutState state) {
        try {
            state.clear();
            CLObject json = CLParser.parse(content);
            for (CLKey key : json) {
                String name = key.getName();
                if (name.equals("Header")) {
                    CLElement value = key.getValue();
                    if (value instanceof CLObject) {
                        parseHeader((CLObject) value, content);
                    }
                } else {
//                    System.out.println("object <" + name + ">");
                    CLElement value = key.getValue();
                    if (value instanceof CLObject) {
                        CLObject object = (CLObject) value;
                        if (object.has("type")) {
                            String type = object.getString("type");
                            switch (type) {
                                case "hGuideline":
                                case "vGuideline": {
                                    parseGuideline(state.guidelines, type, name, object);
                                } break;
                            }
                        } else {
                            parseConstraints(state.constraints, name, (CLObject) value);
                        }
                    }
                }
            }
        } catch (CLParsingException e) {
            e.printStackTrace();
        }
    }

    private void parseGuideline(GuidelinesState state, String type, String widgetId, CLObject json) {
        for (CLKey key : json) {
            String name = key.getName();
            switch (name) {
                case "type": break;
                case "start": {
                    int value = key.getValue().getInt();
                    state.addGuideline(widgetId, GuidelinesState.GUIDELINE_START, value);
                } break;
                case "end": {
                    int value = key.getValue().getInt();
                    state.addGuideline(widgetId, GuidelinesState.GUIDELINE_END, value);
                } break;
                case "top": {
                    int value = key.getValue().getInt();
                    state.addGuideline(widgetId, GuidelinesState.GUIDELINE_TOP, value);
                } break;
                case "bottom": {
                    int value = key.getValue().getInt();
                    state.addGuideline(widgetId, GuidelinesState.GUIDELINE_BOTTOM, value);
                } break;
                case "percent": {
                    float value = key.getValue().getFloat();
                    int guidelineType;
                    if (type.equals("vGuideline")) {
                        guidelineType = GuidelinesState.GUIDELINE_HORIZONTAL_PERCENT;
                    } else {
                        guidelineType = GuidelinesState.GUIDELINE_VERTICAL_PERCENT;
                    }
                    state.addGuideline(widgetId, guidelineType, value);
                } break;
            }
        }
    }

    private void parseConstraints(ConstraintsState state, String widgetId, CLObject json) {
        for (CLKey key : json) {
            String name = key.getName();
//            System.out.println("parsing constraint " + name);

            switch (name) {
                case "start":
                case "end":
                case "top":
                case "bottom": {
                    CLArray constraint = (CLArray) key.getValue();
                    parseConstraint(state, widgetId, name, constraint);
                }
                break;
                case "center": {
                    String target = json.getStringOrNull(name);
                    if (target != null) {
                        parseCenter(state, widgetId, target);
                    }
                }
                break;
                case "centerHorizontally": {
                    parseCenterHorizontally(state, widgetId, key.getValue());
                }
                break;
                case "centerVertically": {
                    parseCenterVertically(state, widgetId, key.getValue());
                }
                break;
                case "width":
                case "height": {
                    CLElement element = key.getValue();
                    parseDimension(state, widgetId, name, key.getValue());
                }
                break;
            }
        }
    }

    private void parseCenter(ConstraintsState state, String widgetId, String targetId) {
        state.addCenterConstraint(widgetId, targetId);
    }

    private void parseCenterHorizontally(ConstraintsState state, String widgetId, CLElement constraint) {
        if (constraint instanceof CLString) {
            state.addCenterHorizontallyConstraint(widgetId, constraint.content(), 0.5f);
        }
    }

    private void parseCenterVertically(ConstraintsState state, String widgetId, CLElement constraint) {
        if (constraint instanceof CLString) {
            state.addCenterVerticallyConstraint(widgetId, constraint.content(), 0.5f);
        }
    }

    private void parseDimension(ConstraintsState state, String widgetId, String constraintName, CLElement constraint) {
//        state.addDimension(widgetId, constraintName, );
        if (constraint instanceof CLString) {
            if (constraintName.equals("width")) {
                state.addWidthDimension(widgetId, constraint.content());
            } else if (constraintName.equals("height")) {
                state.addHeightDimension(widgetId, constraint.content());
            }
        }
    }

    private void parseConstraint(ConstraintsState state, String widgetId, String constraintName, CLArray constraint) {
        if (constraint.size() < 2) {
            return;
        }
        try {
            String targetId = constraint.getString(0);
            String targetAnchor = constraint.getString(1);
            int margin = 0;
            int marginGone = 0;
            if (constraint.size() > 2) {
                margin = constraint.getInt(2);
            }
            if (constraint.size() > 3) {
                marginGone = constraint.getInt(3);
            }
            state.addConstraint(widgetId, constraintName, targetId, targetAnchor, margin, marginGone);
        } catch (CLParsingException e) {
            e.printStackTrace();
        }
    }

    private void parseHeader(CLObject header, String content) {
        exportedName = header.getStringOrNull("exportAs");
    }

    public String getExportedName() {
        return exportedName;
    }
}
