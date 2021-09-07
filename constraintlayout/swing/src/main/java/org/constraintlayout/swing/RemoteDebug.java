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

import androidx.constraintlayout.core.motion.utils.Utils;
import androidx.constraintlayout.core.state.Registry;
import androidx.constraintlayout.core.state.RegistryCallback;
import androidx.constraintlayout.core.widgets.ConstraintWidget;
import androidx.constraintlayout.core.widgets.ConstraintWidgetContainer;
import androidx.constraintlayout.core.widgets.Guideline;
import org.constraintlayout.swing.core.ConstraintLayoutState;
import org.constraintlayout.swing.core.ConstraintSetParser;

/**
 * Utility class for SwingConstraintLayout to support remote debugging
 */
public class RemoteDebug {
    private static final boolean DEBUG = false;
    public static void debug(ConstraintSetParser parser, String content, ConstraintLayoutState state, ConstraintWidgetContainer widgetContainer, ConstraintLayout constraintLayout) {
        Registry registry = Registry.getInstance();
        registry.register(parser.getExportedName(), new RegistryCallback() {
            @Override
            public void onNewMotionScene(String content) {
                try {
                    parser.parse(content, state);
                    if (constraintLayout.parentContainer != null) {
                        constraintLayout.parentContainer.revalidate();
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onProgress(float progress) {

            }

            @Override
            public void onDimensions(int width, int height) {

            }

            @Override
            public String currentMotionScene() {
                return content;
            }

            @Override
            public void setDrawDebug(int debugMode) {

            }

            @Override
            public String currentLayoutInformation() {
                String layout = getSerializedLayout(widgetContainer);
                if (DEBUG) {
                    Utils.log("layout:\n" + layout);
                }
                return layout;
            }

            @Override
            public void setLayoutInformationMode(int layoutInformationMode) {

            }

            @Override
            public long getLastModified() {
                return 0;
            }
        });
    }

  private static  String getSerializedLayout( ConstraintWidgetContainer widgetContainer) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
       widgetContainer.stringId = "root";
        serializeWidget(builder, widgetContainer);

        for (ConstraintWidget widget : widgetContainer.getChildren()) {
            serializeWidget(builder, widget);
        }

        builder.append("}");
        return builder.toString();
    }


    private static void serializeWidget(StringBuilder builder, ConstraintWidget widget) {
        builder.append(widget.stringId);
        builder.append(": {");
        if (widget instanceof Guideline) {
            Guideline guideline = (Guideline) widget;
            if (guideline.getOrientation() == Guideline.HORIZONTAL) {
                builder.append("type: 'hGuideline',");
            } else {
                builder.append("type: 'vGuideline',");
            }
        }
        builder.append(" interpolated: { ");
        builder.append(" left: ");
        builder.append(widget.getLeft());
        builder.append(", top: ");
        builder.append(widget.getTop());
        builder.append(", ");
        builder.append("right: ");
        builder.append(widget.getRight());
        builder.append(", bottom: ");
        builder.append(widget.getBottom());
        builder.append("}}, ");
    }

}
