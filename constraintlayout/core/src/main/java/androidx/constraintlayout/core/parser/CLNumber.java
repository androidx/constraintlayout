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
package androidx.constraintlayout.core.parser;

public class CLNumber extends CLElement {

    float mValue = Float.NaN;

    public CLNumber(char[] content) {
        super(content);
    }

    public CLNumber(float value) {
        super(null);
        this.mValue = value;
    }

    // @TODO: add description
    public static CLElement allocate(char[] content) {
        return new CLNumber(content);
    }

    @Override
    protected String toJSON() {
        float value = getFloat();
        int intValue = (int) value;
        if ((float) intValue == value) {
            return "" + intValue;
        }
        return "" + value;
    }

    @Override
    protected String toFormattedJSON(int indent, int forceIndent) {
        StringBuilder json = new StringBuilder();
        addIndent(json, indent);
        float value = getFloat();
        int intValue = (int) value;
        if ((float) intValue == value) {
            json.append(intValue);
        } else {
            json.append(value);
        }
        return json.toString();
    }

    // @TODO: add description
    public boolean isInt() {
        float value = getFloat();
        int intValue = (int) value;
        return ((float) intValue == value);
    }

    @Override
    public int getInt() {
        if (Float.isNaN(mValue) && hasContent()) {
            // If the value is undefined, attempt to define it from the content
            mValue = Integer.parseInt(content());
        }
        return (int) mValue;
    }

    @Override
    public float getFloat() {
        if (Float.isNaN(mValue) && hasContent()) {
            // If the value is undefined, attempt to define it from the content
            mValue = Float.parseFloat(content());
        }
        return mValue;
    }

    // @TODO: add description
    public void putValue(float value) {
        this.mValue = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof CLNumber) {
            float thisFloat = getFloat();
            float otherFloat = ((CLNumber) obj).getFloat();
            if (Float.isNaN(thisFloat) && Float.isNaN(otherFloat)) {
                // Consider equal if both elements have a NaN value
                return true;
            }
            return thisFloat == otherFloat;
        }

        return false;
    }

    @Override
    public int hashCode() {
        // Auto-generated with Intellij Action "equals() and hashcode()"
        int result = super.hashCode();
        result = 31 * result + (mValue != 0.0f ? Float.floatToIntBits(mValue) : 0);
        return result;
    }
}
