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

import java.util.Iterator;

public class CLObject extends CLContainer implements Iterable<CLKey> {

    public CLObject(char[] content) {
        super(content);
    }

    /**
     * @TODO: add description
     * @param content
     * @return
     */
    public static CLObject allocate(char[] content) {
        return new CLObject(content);
    }

    /**
     * @TODO: add description
     * @return
     */
    public String toJSON() {
        StringBuilder json = new StringBuilder(getDebugName() + "{ ");
        boolean first = true;
        for (CLElement element : mElements) {
            if (!first) {
                json.append(", ");
            } else {
                first = false;
            }
            json.append(element.toJSON());
        }
        json.append(" }");
        return json.toString();
    }

    /**
     * @TODO: add description
     * @return
     */
    public String toFormattedJSON() {
        return toFormattedJSON(0, 0);
    }

    /**
     * @TODO: add description
     * @param indent
     * @param forceIndent
     * @return
     */
    public String toFormattedJSON(int indent, int forceIndent) {
        StringBuilder json = new StringBuilder(getDebugName());
        json.append("{\n");
        boolean first = true;
        for (CLElement element : mElements) {
            if (!first) {
                json.append(",\n");
            } else {
                first = false;
            }
            json.append(element.toFormattedJSON(indent + sBaseIndent, forceIndent - 1));
        }
        json.append("\n");
        addIndent(json, indent);
        json.append("}");
        return json.toString();
    }

    @Override
    public Iterator iterator() {
        return new CLObjectIterator(this);
    }

    private class CLObjectIterator implements Iterator {
        CLObject mObject;
        int mIndex = 0;
        CLObjectIterator(CLObject clObject) {
            mObject = clObject;
        }

        @Override
        public boolean hasNext() {
            return mIndex < mObject.size();
        }

        @Override
        public Object next() {
            CLKey key = (CLKey) mObject.mElements.get(mIndex);
            mIndex++;
            return key;
        }
    }
}
