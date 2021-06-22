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
package androidx.constraintlayout.core.json;

public class JSONParser {

  static boolean DEBUG = false;

  enum TYPE {UNKNOWN, OBJECT, ARRAY, NUMBER, STRING, KEY, TOKEN}

  public static JSONObject parse(String string) throws JSONParsingException {
    JSONObject root = null;

    char[] content = string.toCharArray();
    JSONElement currentElement = null;

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
      throw new JSONParsingException("invalid json content");
    }

    // We have a root object, let's start
    root = JSONObject.allocate(content);
    root.setStart(startIndex);
    currentElement = root;

    for (int i = startIndex + 1; i < length; i++) {
      char c = content[i];
      if (false) {
        System.out.println("Looking at " + i + " : <" + c + ">");
      }
      if (currentElement.isDone()) {
        currentElement = getNextJsonElement(i, c, currentElement, content);
      } else if (currentElement instanceof JSONObject) {
        if (c == '}') {
          currentElement.setEnd(i - 1);
        } else {
          currentElement = getNextJsonElement(i, c, currentElement, content);
        }
      } else if (currentElement instanceof JSONArray) {
        if (c == ']') {
          currentElement.setEnd(i - 1);
        } else {
          currentElement = getNextJsonElement(i, c, currentElement, content);
        }
      } else if (currentElement instanceof JSONString) {
        if (c == '\'' || c == '"') {
          currentElement.setEnd(i - 1);
        }
      } else {
        if (currentElement instanceof JSONToken) {
          JSONToken token = (JSONToken) currentElement;
          if (!token.validate(c, i)) {
            throw new JSONParsingException("parsing incorrect token " + token.content());
          }
        }
        if (c == '}' || c == ']' || c == ',' || c == ' ' || c == '\t' || c == '\r' || c == '\n' || c == ':') {
          currentElement.setEnd(i - 1);
          if (c == '}' || c == ']') {
            currentElement = currentElement.getContainer();
            currentElement.setEnd(i - 1);
          }
        }
      }

      if (currentElement.isDone() && (!(currentElement instanceof JSONKey) || ((JSONKey) currentElement).mElements.size() > 0) ) {
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

  private static JSONElement getNextJsonElement(int i, char c, JSONElement currentElement,
      char[] content) throws JSONParsingException {
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
        currentElement = createElement(currentElement, i, TYPE.STRING, true, content);
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
        if (currentElement instanceof JSONContainer && !(currentElement instanceof JSONObject)) {
          currentElement = createElement(currentElement, i, TYPE.TOKEN, true, content);
          JSONToken token = (JSONToken) currentElement;
          if (!token.validate(c, i)) {
            throw new JSONParsingException("incorrect token <" + c + ">");
          }
        } else {
          currentElement = createElement(currentElement, i, TYPE.KEY, true, content);
        }
      }
    }
    return currentElement;
  }

  private static JSONElement createElement(JSONElement currentElement, int position,
      TYPE type, boolean applyStart, char[] content) {
    JSONElement newElement = null;
    if (DEBUG) {
      System.out.println("CREATE " + type + " at " + content[position]);
    }
    switch (type) {
      case OBJECT: {
        newElement = JSONObject.allocate(content);
        position++;
      }
      break;
      case ARRAY: {
        newElement = JSONArray.allocate(content);
        position++;
      }
      break;
      case STRING: {
        newElement = JSONString.allocate(content);
        position++;
      }
      break;
      case NUMBER: {
        newElement = JSONNumber.allocate(content);
      }
      break;
      case KEY: {
        newElement = JSONKey.allocate(content);
      }
      break;
      case TOKEN: {
        newElement = JSONToken.allocate(content);
      }
      break;
    }
    if (newElement == null) {
      return null;
    }
    if (applyStart) {
      newElement.setStart(position);
    }
    if (currentElement instanceof JSONContainer) {
      JSONContainer container = (JSONContainer) currentElement;
      newElement.setContainer(container);
    }
    return newElement;
  }

  private static boolean isSpace(char c) {
    return c == ' ' || c == '\n' || c == '\t';
  }
}
