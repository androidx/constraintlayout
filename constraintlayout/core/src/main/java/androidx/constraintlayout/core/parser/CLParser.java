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

import static androidx.constraintlayout.core.parser.CLElementUtils.createElement;

public class CLParser {

    static boolean sDebug = false;

    private String mContent;
    private boolean mHasComment = false;
    private int mLineNumber;

    enum TYPE {UNKNOWN, OBJECT, ARRAY, NUMBER, STRING, KEY, TOKEN}

    // @TODO: add description
    public static CLObject parse(String string) throws CLParsingException {
        return new CLParser(string).parse();
    }

    public CLParser(String content) {
        mContent = content;
    }

    // @TODO: add description
    public CLObject parse() throws CLParsingException {
        @SuppressWarnings("unused") CLObject root = null;

        char[] content = mContent.toCharArray();
        @SuppressWarnings("unused") CLElement currentElement = null;

        final int length = content.length;

        // First, let's find the root element start
        mLineNumber = 1;

        int startIndex = -1;
        for (int i = 0; i < length; i++) {
            char c = content[i];
            if (c == '{') {
                startIndex = i;
                break;
            }
            if (c == '\n') {
                mLineNumber++;
            }
        }
        if (startIndex == -1) {
            throw new CLParsingException("invalid json content", null);
        }

        // We have a root object, let's start
        root = CLObject.allocate(content);
        root.setLine(mLineNumber);
        root.setStart(startIndex);
        currentElement = root;

        for (int i = startIndex + 1; i < length; i++) {
            char c = content[i];
            if (c == '\n') {
                mLineNumber++;
            }
            if (mHasComment) {
                if (c == '\n') {
                    mHasComment = false;
                } else {
                    continue;
                }
            }
            if (sDebug) {
                System.out.println("Looking at " + i + " : <" + c + ">");
            }
            if (currentElement == null) {
                break;
            }
            if (currentElement.isDone()) {
                currentElement = getNextJsonElement(i, c, currentElement, content);
            } else if (currentElement instanceof CLObject) {
                if (c == '}') {
                    currentElement.setEnd(i - 1);
                } else {
                    currentElement = getNextJsonElement(i, c, currentElement, content);
                }
            } else if (currentElement instanceof CLArray) {
                if (c == ']') {
                    currentElement.setEnd(i - 1);
                } else {
                    currentElement = getNextJsonElement(i, c, currentElement, content);
                }
            } else if (currentElement instanceof CLString) {
                char ck = content[(int) currentElement.mStart];
                if (ck == c) {
                    currentElement.setStart(currentElement.mStart + 1);
                    currentElement.setEnd(i - 1);
                }
            } else {
                if (currentElement instanceof CLToken) {
                    CLToken token = (CLToken) currentElement;
                    if (!token.validate(c, i)) {
                        throw new CLParsingException("parsing incorrect token " + token.content()
                                + " at line " + mLineNumber, token);
                    }
                }
                if (currentElement instanceof CLKey || currentElement instanceof CLString) {
                    char ck = content[(int) currentElement.mStart];
                    if ((ck == '\'' || ck == '"') && ck == c) {
                        currentElement.setStart(currentElement.mStart + 1);
                        currentElement.setEnd(i - 1);
                    }
                }
                if (!currentElement.isDone()) {
                    if (c == '}' || c == ']' || c == ',' || c == ' '
                            || c == '\t' || c == '\r' || c == '\n' || c == ':') {
                        currentElement.setEnd(i - 1);
                        if (c == '}' || c == ']') {
                            currentElement = currentElement.getContainer();
                            currentElement.setEnd(i - 1);
                            if (currentElement instanceof CLKey) {
                                currentElement = currentElement.getContainer();
                                currentElement.setEnd(i - 1);
                            }
                        }
                    }
                }
            }

            if (currentElement.isDone() && (!(currentElement instanceof CLKey)
                    || ((CLKey) currentElement).mElements.size() > 0)) {
                currentElement = currentElement.getContainer();
            }
        }

        // Close all open elements --
        // allow us to be more resistant to invalid json, useful during editing.
        while (currentElement != null && !currentElement.isDone()) {
            if (currentElement instanceof CLString) {
                currentElement.setStart((int) currentElement.mStart + 1);
            }
            currentElement.setEnd(length - 1);
            currentElement = currentElement.getContainer();
        }

        if (sDebug) {
            System.out.println("Root: " + root.toJSON());
        }

        return root;
    }

    private CLElement getNextJsonElement(int position,
            char c,
            CLElement currentElement,
            char[] content) throws CLParsingException {
        CLElement nextElement = null;
        switch (c) {
            case ' ':
            case ':':
            case ',':
            case '\t':
            case '\r':
            case '\n': {
                // skip space
            }
            break;
            case '{': {
                nextElement = createElement(currentElement, position, TYPE.OBJECT, content, mLineNumber);
            }
            break;
            case '[': {
                nextElement = createElement(currentElement, position, TYPE.ARRAY, content, mLineNumber);
            }
            break;
            case ']':
            case '}': {
                currentElement.setEnd(position - 1);
                currentElement = currentElement.getContainer();
                currentElement.setEnd(position);
            }
            break;
            case '"':
            case '\'': {
                if (currentElement instanceof CLObject) {
                    nextElement = createElement(currentElement, position, TYPE.KEY, content, mLineNumber);
                } else {
                    nextElement = createElement(currentElement, position, TYPE.STRING, content, mLineNumber);
                }
            }
            break;
            case '/': {
                if (position + 1 < content.length && content[position + 1] == '/') {
                    mHasComment = true;
                }
            }
            break;
            case '-':
            case '+':
            case '.':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9': {
                nextElement = createElement(currentElement, position, TYPE.NUMBER, content, mLineNumber);
            }
            break;
            default: {
                if (currentElement instanceof CLContainer
                        && !(currentElement instanceof CLObject)) {
                    nextElement = createElement(currentElement, position, TYPE.TOKEN, content,
                            mLineNumber);
                    CLToken token = (CLToken) nextElement;
                    if (!token.validate(c, position)) {
                        throw new CLParsingException("incorrect token <"
                                + c + "> at line " + mLineNumber, token);
                    }
                } else {
                    nextElement = createElement(currentElement, position, TYPE.KEY, content, mLineNumber);
                }
            }
        }
        if (nextElement != null) {
            return nextElement;
        }
        else {
            return currentElement;
        }
    }
}
