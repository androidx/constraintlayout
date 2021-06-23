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

public class CLParser {

  static boolean DEBUG = false;

  enum TYPE {UNKNOWN, OBJECT, ARRAY, NUMBER, STRING, KEY, TOKEN}

  public static CLObject parse(String string) throws CLParsingException {
    CLObject root = null;

    char[] content = string.toCharArray();
    CLElement currentElement = null;

    final int length = content.length;

    // First, let's find the root element start

    int startIndex = -1;
    for (int i = 0; i < length; i++) {
      char c = content[i];
      if (c == '{') {
        startIndex = i;
        break;
      }
    }
    if (startIndex == -1) {
      throw new CLParsingException("invalid json content");
    }

    // We have a root object, let's start
    root = CLObject.allocate(content);
    root.setStart(startIndex);
    currentElement = root;

    for (int i = startIndex + 1; i < length; i++) {
      char c = content[i];
      if (false) {
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
        if (c == '\'' || c == '"') {
          currentElement.setEnd(i - 1);
        }
      } else {
        if (currentElement instanceof CLToken) {
          CLToken token = (CLToken) currentElement;
          if (!token.validate(c, i)) {
            throw new CLParsingException("parsing incorrect token " + token.content());
          }
        }
        if (currentElement instanceof CLKey) {
          char ck = content[(int) currentElement.start];
          if (ck == '\'' && c == '\'') {
            currentElement.setStart(currentElement.start + 1);
            currentElement.setEnd(i - 1);
          }
          else if (ck == '"' && c == '"') {
            currentElement.setStart(currentElement.start + 1);
            currentElement.setEnd(i - 1);
          }
        }
        if (!currentElement.isDone()) {
          if (c == '}' || c == ']' || c == ',' || c == ' ' || c == '\t' || c == '\r' || c == '\n' || c == ':') {
            currentElement.setEnd(i - 1);
            if (c == '}' || c == ']') {
              currentElement = currentElement.getContainer();
              currentElement.setEnd(i - 1);
            }
          }
        }
      }

      if (currentElement.isDone() && (!(currentElement instanceof CLKey) || ((CLKey) currentElement).mElements.size() > 0) ) {
        currentElement = currentElement.getContainer();
      }
    }

    // Close all open elements -- allow us to be more resistant to invalid json, useful during editing.
    while (currentElement != null && !currentElement.isDone()) {
      currentElement.setEnd(length - 1);
      currentElement = currentElement.getContainer();
    }

    if (DEBUG) {
      System.out.println("Root: " + root.toJSON());
    }

    return root;
  }

  private static CLElement getNextJsonElement(int i, char c, CLElement currentElement,
                                              char[] content) throws CLParsingException {
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
        currentElement = createElement(currentElement, i, TYPE.OBJECT, true, content);
      }
      break;
      case '[': {
        currentElement = createElement(currentElement, i, TYPE.ARRAY, true, content);
      }
      break;
      case ']':
      case '}': {
        currentElement.setEnd(i-1);
        currentElement = currentElement.getContainer();
        currentElement.setEnd(i);
      } break;
      case '"':
      case '\'': {
        if (currentElement instanceof CLObject) {
          currentElement = createElement(currentElement, i, TYPE.KEY, true, content);
        } else {
          currentElement = createElement(currentElement, i, TYPE.STRING, true, content);
        }
      }
      break;
      case '-':
      case '+':
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
        currentElement = createElement(currentElement, i, TYPE.NUMBER, true, content);
      }
      break;
      default: {
        if (currentElement instanceof CLContainer && !(currentElement instanceof CLObject)) {
          currentElement = createElement(currentElement, i, TYPE.TOKEN, true, content);
          CLToken token = (CLToken) currentElement;
          if (!token.validate(c, i)) {
            throw new CLParsingException("incorrect token <" + c + ">");
          }
        } else {
          currentElement = createElement(currentElement, i, TYPE.KEY, true, content);
        }
      }
    }
    return currentElement;
  }

  private static CLElement createElement(CLElement currentElement, int position,
                                         TYPE type, boolean applyStart, char[] content) {
    CLElement newElement = null;
    if (DEBUG) {
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
        position++;
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
    }
    if (newElement == null) {
      return null;
    }
    if (applyStart) {
      newElement.setStart(position);
    }
    if (currentElement instanceof CLContainer) {
      CLContainer container = (CLContainer) currentElement;
      newElement.setContainer(container);
    }
    return newElement;
  }

  private static boolean isSpace(char c) {
    return c == ' ' || c == '\n' || c == '\t';
  }
}
