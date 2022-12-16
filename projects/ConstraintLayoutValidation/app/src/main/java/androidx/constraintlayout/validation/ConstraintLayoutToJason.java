/*
 * Copyright (C) 2020 The Android Open Source Project
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
package androidx.constraintlayout.validation;

import static androidx.constraintlayout.widget.ConstraintSet.Layout.UNSET_GONE_MARGIN;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.motion.widget.Debug;
import androidx.constraintlayout.widget.ConstraintAttribute;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;

public class ConstraintLayoutToJason {
    private ConstraintLayout mConstraintLayout;
    private Context mContext;
    private static String TAG = "ML_DEBUG";

    private ConstraintLayoutToJason() {
    }

    private static String escape(String str) {
        return str.replaceAll("\'", "\\'");
    }

    public static String writeJSonToFile(ConstraintLayout constraintLayout, String name) {
        FileOutputStream outputStream;
        ConstraintLayoutToJason c = new ConstraintLayoutToJason();
        try {
            File down = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            File file = new File(down, name + ".json5");
            outputStream = new FileOutputStream(file);
            outputStream.write(c.constraintLayoutToJson(constraintLayout).getBytes());
            outputStream.close();
            return file.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setConstraintLayout(ConstraintLayout constraintLayout) {
        ConstraintLayoutToJason c = new ConstraintLayoutToJason();
        Log.v(TAG, c.constraintLayoutToJson(constraintLayout));
    }

    public String constraintLayoutToJson(ConstraintLayout constraintLayout) {
        mConstraintLayout = constraintLayout;
        mContext = constraintLayout.getContext();
        StringWriter writer = new StringWriter();
        writer.append("{\n");

        writeWidgets(writer, constraintLayout);
        writer.append("  ConstraintSet:{\n");
        ConstraintSet set = new ConstraintSet();
        set.clone(constraintLayout);
        String name = Debug.getName(constraintLayout);
        try {
            writer.append(name + ":");
            new WriteJsonEngine(writer, set, constraintLayout, 0).writeLayout();
            writer.append("\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        writer.append("  }\n");
        writer.append("}\n");
        return writer.toString();
    }

    private void writeWidgets(StringWriter writer, ConstraintLayout constraintLayout) {
        writer.append("Widgets:{\n");
        int count = constraintLayout.getChildCount();

        for (int i = -1; i < count; i++) {
            View v = (i == -1) ? constraintLayout : constraintLayout.getChildAt(i);
            int id = v.getId();
            String name = (i == -1) ? "parent" : Debug.getName(v);
            String cname = v.getClass().getSimpleName();
            String bounds = ", bounds: [" + v.getLeft() + ", " + v.getTop()
                    + ", " + v.getRight() + ", " + v.getBottom() + "]},\n";
            writer.append("  " + name + ": { ");
            if (i == -1) {
                writer.append("type: '" + v.getClass().getSimpleName() + "' , ");

                try {
                    ViewGroup.LayoutParams p = (ViewGroup.LayoutParams) v.getLayoutParams();

                    String w = p.width == -1 ? "'MATCH_PARENT'" :
                            (p.width == -2) ? "'WRAP_CONTENT'" : p.width + "";
                    writer.append("width: " + w + ", ");
                    String h = p.height == -1 ? "'MATCH_PARENT'" :
                            (p.height == -2) ? "'WRAP_CONTENT'" : p.height + "";
                    writer.append("height: " + h);
                } catch (Exception e) {

                }
            } else if (cname.contains("Text")) {
                if (v instanceof TextView) {
                    writer.append("type: 'Text', label: '" + escape(((TextView) v).getText().toString()) + "'");
                } else {
                    writer.append("type: 'Text' },\n");
                }
            } else if (cname.contains("Button")) {
                if (v instanceof Button) {
                    writer.append("type: 'Button', label: '" + ((Button) v).getText()+"'");
                } else
                    writer.append("type: 'Button'");
            } else if (cname.contains("Image")) {
                writer.append("type: 'Image'");
            } else if (cname.contains("View")) {
                writer.append("type: 'Box'");
            } else {
                writer.append("type: '" + v.getClass().getSimpleName() + "'");
            }
            writer.append(bounds);
        }
        writer.append("},\n");
    }

    private void logBigString(String str) {
        int len = str.length();
        for (int i = 0; i < len; i++) {
            int k = str.indexOf("\n", i);
            if (k == -1) {
                Log.v(TAG, str.substring(i));
                break;
            }
            Log.v(TAG, str.substring(i, k));
            i = k;
        }
    }


    // ================================== JSON ===============================================
    static class WriteJsonEngine {
        public static final int UNSET = ConstraintLayout.LayoutParams.UNSET;
        ConstraintSet set;
        Writer mWriter;
        ConstraintLayout mLayout;
        Context mContext;
        int mFlags;
        int mUnknownCount = 0;
        final String mLEFT = "left";
        final String mRIGHT = "right";
        final String mBASELINE = "baseline";
        final String mBOTTOM = "bottom";
        final String mTOP = "top";
        final String mSTART = "start";
        final String mEND = "end";
        private static final String INDENT = "    ";
        private static final String SMALL_INDENT = "  ";

        WriteJsonEngine(Writer writer,
                        ConstraintSet set,
                        ConstraintLayout layout,
                        int flags) throws IOException {
            this.mWriter = writer;
            this.mLayout = layout;
            this.mContext = layout.getContext();
            this.mFlags = flags;
            this.set = set;
            set.getConstraint(2);
        }

        int[] getIDs() {
            return set.getKnownIds();
        }

        ConstraintSet.Constraint getConstraint(int id) {
            return set.getConstraint(id);
        }

        void writeLayout() throws IOException {
            mWriter.write("{\n");
            for (Integer id : getIDs()) {
                ConstraintSet.Constraint c = getConstraint(id);
                String idName = getSimpleName(id);
                View v = mLayout.getViewById(id);
                mWriter.write(SMALL_INDENT + idName + ":{\n");
                ConstraintSet.Layout l = c.layout;
                if (l.mReferenceIds != null) {
                    String ref = "type: '" + v.getClass() + "' , contains: [";
                    for (int r = 0; r < l.mReferenceIds.length; r++) {
                        int rid = l.mReferenceIds[r];
                        ref += ((r == 0) ? "" : ", ") + getName(rid);
                    }
                    mWriter.write(ref + "],\n");
                }
                if (l.mReferenceIdString != null) {
                    String ref = SMALL_INDENT + "type: '"+v.getClass() + "', contains: [";
                    String[] rids = l.mReferenceIdString.split(",");
                    for (int r = 0; r < rids.length; r++) {
                        String rid = rids[r];
                        ref += ((r == 0) ? "" : ", ") + "`" + rid + "`";
                    }
                    mWriter.write(ref + "],\n");
                }
                writeDimension("height", l.mHeight, l.heightDefault, l.heightPercent,
                        l.heightMin, l.heightMax, l.constrainedHeight);
                writeDimension("width", l.mWidth, l.widthDefault, l.widthPercent,
                        l.widthMin, l.widthMax, l.constrainedWidth);

                writeConstraint(mLEFT, l.leftToLeft, mLEFT, l.leftMargin, l.goneLeftMargin);
                writeConstraint(mLEFT, l.leftToRight, mRIGHT, l.leftMargin, l.goneLeftMargin);
                writeConstraint(mRIGHT, l.rightToLeft, mLEFT, l.rightMargin, l.goneRightMargin);
                writeConstraint(mRIGHT, l.rightToRight, mRIGHT, l.rightMargin, l.goneRightMargin);
                writeConstraint(mBASELINE, l.baselineToBaseline, mBASELINE, UNSET,
                        l.goneBaselineMargin);
                writeConstraint(mBASELINE, l.baselineToTop, mTOP, UNSET, l.goneBaselineMargin);
                writeConstraint(mBASELINE, l.baselineToBottom,
                        mBOTTOM, UNSET, l.goneBaselineMargin);

                writeConstraint(mTOP, l.topToBottom, mBOTTOM, l.topMargin, l.goneTopMargin);
                writeConstraint(mTOP, l.topToTop, mTOP, l.topMargin, l.goneTopMargin);
                writeConstraint(mBOTTOM, l.bottomToBottom, mBOTTOM, l.bottomMargin,
                        l.goneBottomMargin);
                writeConstraint(mBOTTOM, l.bottomToTop, mTOP, l.bottomMargin, l.goneBottomMargin);
                writeConstraint(mSTART, l.startToStart, mSTART, l.startMargin, l.goneStartMargin);
                writeConstraint(mSTART, l.startToEnd, mEND, l.startMargin, l.goneStartMargin);
                writeConstraint(mEND, l.endToStart, mSTART, l.endMargin, l.goneEndMargin);
                writeConstraint(mEND, l.endToEnd, mEND, l.endMargin, l.goneEndMargin);

                writeVariable("horizontalBias", l.horizontalBias, 0.5f);
                writeVariable("verticalBias", l.verticalBias, 0.5f);

                writeCircle(l.circleConstraint, l.circleAngle, l.circleRadius);

                writeGuideline(l.orientation, l.guideBegin, l.guideEnd, l.guidePercent);
                writeVariable("dimensionRatio", l.dimensionRatio);
                writeVariable("barrierMargin", l.mBarrierMargin);
                writeVariable("type", l.mHelperType);
                writeVariable("ReferenceId", l.mReferenceIdString);
                writeVariable("mBarrierAllowsGoneWidgets",
                        l.mBarrierAllowsGoneWidgets, true);
                writeVariable("WrapBehavior", l.mWrapBehavior);

                writeVariable("verticalWeight", l.verticalWeight);
                writeVariable("horizontalWeight", l.horizontalWeight);
                String []style = {null, "packed","spread_inside"};

                writeVariable("horizontalChainStyle", style[l.horizontalChainStyle]);
                writeVariable("verticalChainStyle", style[l.verticalChainStyle]);
                if (l.mBarrierDirection != UNSET) {
                    String []barr = {"left", "right","top","bottom"};
                    writeVariable("barrierDirection", barr[l.mBarrierDirection]);
                }
                if (l.mReferenceIds != null) {
                    writeVariable("ReferenceIds", l.mReferenceIds);
                }
                writeTransform(c.transform);
                writeCustom(c.mCustomConstraints);


                mWriter.write("  },\n");
            }
            mWriter.write("},\n");
        }


        private void writeTransform(ConstraintSet.Transform transform) throws IOException {
            if (transform.applyElevation) {
                writeVariable("elevation", transform.elevation);
            }
//            writeVariable("transformPivotX", transform.transformPivotX, Float.NaN);
//            writeVariable("transformPivotY", transform.transformPivotY, Float.NaN);
            writeVariable("rotationX", transform.rotationX, 0);
            writeVariable("rotationY", transform.rotationY, 0);
            writeVariable("rotationZ", transform.rotation, 0);
            writeVariable("scaleX", transform.scaleX, 1);
            writeVariable("scaleY", transform.scaleY, 1);
            writeVariable("translationX", transform.translationX, 0);
            writeVariable("translationY", transform.translationY, 0);
            writeVariable("translationZ", transform.translationZ, 0);
        }

        void writeCustom(HashMap<String, ConstraintAttribute> cset) throws IOException {
            if (cset != null && cset.size() > 0) {
                mWriter.write(INDENT + "custom: {\n");
                for (String s : cset.keySet()) {

                    ConstraintAttribute attr = cset.get(s);
                    String custom = INDENT + SMALL_INDENT + attr.getName() + ": ";
                    switch (attr.getType()) {
                        case INT_TYPE:
                            custom += attr.getIntegerValue();
                            break;
                        case COLOR_TYPE:
                            custom += colorString(attr.getColorValue());
                            break;
                        case FLOAT_TYPE:
                            custom += attr.getFloatValue();
                            break;
                        case STRING_TYPE:
                            custom += "'" + attr.getStringValue() + "'";
                            break;
                        case DIMENSION_TYPE:
                            custom += attr.getFloatValue();
                            break;
                        case REFERENCE_TYPE:
                        case COLOR_DRAWABLE_TYPE:
                        case BOOLEAN_TYPE:
                            custom = null;
                    }
                    if (custom != null) {
                        mWriter.write(custom + ",\n");
                    }
                }
                mWriter.write(SMALL_INDENT + "   } \n");
            }
        }

        static String colorString(int v) {
            String str = "00000000" + Integer.toHexString(v);
            return "#" + str.substring(str.length() - 8);
        }

        private void writeGuideline(int orientation,
                                    int guideBegin,
                                    int guideEnd,
                                    float guidePercent) throws IOException {
            writeVariable("orientation", orientation);
            writeVariable("guideBegin", guideBegin);
            writeVariable("guideEnd", guideEnd);
            writeVariable("guidePercent", guidePercent);

        }


        private void writeDimension(String dimString,
                                    int dim,
                                    int dimDefault,
                                    float dimPercent,
                                    int dimMin,
                                    int dimMax,
                                    boolean unusedConstrainedDim) throws IOException {
            if (dim == 0) {
                if (dimMax != UNSET || dimMin != UNSET) {
                    String s = "-----";
                    switch (dimDefault) {
                        case 0: // spread
                            s = INDENT + dimString + ": {value:'spread'";
                            break;
                        case 1: //  wrap
                            s = INDENT + dimString + ": {value:'wrap'";
                            break;
                        case 2: // percent
                            s = INDENT + dimString + ": {value: '" + dimPercent + "%'";
                            break;
                    }
                    if (dimMax != UNSET) {
                        s += ", max: " + dimMax;
                    }
                    if (dimMax != UNSET) {
                        s += ", min: " + dimMin;
                    }
                    s += "},\n";
                    mWriter.write(s);
                    return;
                }

                switch (dimDefault) {
                    case 0: // spread is the default
                        break;
                    case 1: //  wrap
                        mWriter.write(INDENT + dimString + ": '???????????',\n");
                        return;
                    case 2: // percent
                        mWriter.write(INDENT + dimString + ": '" + dimPercent + "%',\n");
                        return;
                }

            } else if (dim == -2) {
                mWriter.write(INDENT + dimString + ": 'wrap',\n");
            } else if (dim == -1) {
                mWriter.write(INDENT + dimString + ": 'parent',\n");
            } else {
                mWriter.write(INDENT + dimString + ": " + dim + ",\n");
            }
        }

        HashMap<Integer, String> mIdMap = new HashMap<>();

        String getSimpleName(int id) {
            if (mIdMap.containsKey(id)) {
                return "" + mIdMap.get(id);
            }
            if (id == 0) {
                return "parent";
            }
            String name = lookup(id);
            mIdMap.put(id, name);
            return "" + name + "";
        }

        String getName(int id) {
            return "\'" + getSimpleName(id) + "\'";
        }

        String lookup(int id) {
            try {
                if (id != -1) {
                    return mContext.getResources().getResourceEntryName(id);
                } else {
                    return "unknown" + ++mUnknownCount;
                }
            } catch (Exception ex) {
                return "unknown" + ++mUnknownCount;
            }
        }

        void writeConstraint(String my,
                             int leftToLeft,
                             String other,
                             int margin,
                             int goneMargin) throws IOException {
            if (leftToLeft == UNSET) {
                return;
            }
            mWriter.write(INDENT + my);
            mWriter.write(":[");
            mWriter.write(getName(leftToLeft));
            mWriter.write(", ");
            mWriter.write("'" + other + "'");
            if (margin != 0 || goneMargin != UNSET_GONE_MARGIN) {
                mWriter.write(", " + margin);
                if (goneMargin != UNSET_GONE_MARGIN) {
                    mWriter.write(", " + goneMargin);
                }
            }
            mWriter.write("],\n");
        }

        void writeCircle(int circleConstraint,
                         float circleAngle,
                         int circleRadius) throws IOException {
            if (circleConstraint == UNSET) {
                return;
            }
            mWriter.write(INDENT + "circle");
            mWriter.write(":[");
            mWriter.write(getName(circleConstraint));
            mWriter.write(", " + circleAngle);
            mWriter.write(circleRadius + "],\n");
        }

        void writeVariable(String name, int value) throws IOException {
            if (value == 0 || value == -1) {
                return;
            }
            mWriter.write(INDENT + name);
            mWriter.write(": " + value);
            mWriter.write(",\n");
        }

        void writeVariable(String name, float value) throws IOException {
            if (value == UNSET) {
                return;
            }
            mWriter.write(INDENT + name);
            mWriter.write(": " + value);
            mWriter.write(",\n");
        }

        void writeVariable(String name, float value, float def) throws IOException {
            if ((Float.isNaN(def) && Float.isNaN(value)) || value == def) {
                return;
            }
            mWriter.write(INDENT + name);
            mWriter.write(": " + value);
            mWriter.write(",\n");
        }

        void writeVariable(String name, boolean value) throws IOException {
            if (!value) {
                return;
            }
            mWriter.write(INDENT + name);
            mWriter.write(": " + value);
            mWriter.write(",\n");
        }

        void writeVariable(String name, boolean value, boolean def) throws IOException {
            if (value == def) {
                return;
            }
            mWriter.write(INDENT + name);
            mWriter.write(": " + value);
            mWriter.write(",\n");
        }

        void writeVariable(String name, int[] value) throws IOException {
            if (value == null) {
                return;
            }
            mWriter.write(INDENT + name);
            mWriter.write(": ");
            for (int i = 0; i < value.length; i++) {
                mWriter.write(((i == 0) ? "[" : ", ") + getName(value[i]));
            }
            mWriter.write("],\n");
        }

        void writeVariable(String name, String value) throws IOException {
            if (value == null) {
                return;
            }
            mWriter.write(INDENT + name);
            mWriter.write(": '" + value);
            mWriter.write("',\n");
        }
    }

}
