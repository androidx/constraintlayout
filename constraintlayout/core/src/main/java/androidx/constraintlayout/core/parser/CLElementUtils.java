/*
 * Copyright (C) 2022 The Android Open Source Project
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

public class CLElementUtils {
    public static CLElement createElement(CLElement currentElement,
            int position,
            CLParser.TYPE type,
            char[] content,
            int lineNumber) {
        CLElement newElement = null;
        if (CLParser.sDebug) {
            System.out.println("CREATE " + type + " at " + content[position]);
        }
        switch (type) {
            case OBJECT: {
                newElement = CLObject.allocate(content);
                position++;
            }
            break;
            case ARRAY: {
                newElement = CLArray.allocate(content);
                position++;
            }
            break;
            case STRING: {
                newElement = CLString.allocate(content);
            }
            break;
            case NUMBER: {
                newElement = CLNumber.allocate(content);
            }
            break;
            case KEY: {
                newElement = CLKey.allocate(content);
            }
            break;
            case TOKEN: {
                newElement = CLToken.allocate(content);
            }
            break;
            default:
                break;
        }
        if (newElement == null) {
            return null;
        }
        newElement.setLine(lineNumber);
        newElement.setStart(position);
        if (currentElement instanceof CLContainer) {
            CLContainer container = (CLContainer) currentElement;
            newElement.setContainer(container);
        }
        return newElement;
    }
}
