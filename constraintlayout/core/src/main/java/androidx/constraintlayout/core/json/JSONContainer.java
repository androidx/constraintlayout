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

import java.util.ArrayList;

public class JSONContainer extends JSONElement {
  ArrayList<JSONElement> mElements = new ArrayList<>();

  public JSONContainer(char[] content) {
    super(content);
  }

  public static JSONElement allocate(char[] content) {
    return new JSONContainer(content);
  }

  public void add(JSONElement element) {
    mElements.add(element);
    if (JSONParser.DEBUG) {
      System.out.println("added element " + element + " to " + this);
    }
  }

  @Override
  public String toString() {
    StringBuilder list = new StringBuilder();
    for (JSONElement element : mElements) {
      if (list.length() > 0) {
        list.append("; ");
      }
      list.append(element);
    }
    return super.toString() + " value : <" + list + " >";
  }

  /////////////////////////////////////////////////////////////////////////
  // By name
  /////////////////////////////////////////////////////////////////////////

  public JSONElement get(String name) throws JSONParsingException {
    for (JSONElement element : mElements) {
      JSONKey key = (JSONKey) element;
      if (key.content().equals(name)) {
        return key.getValue();
      }
    }
    throw new JSONParsingException("no element for key <" + name + ">");
  }

  public int getInt(String name) throws JSONParsingException {
    JSONElement element = get(name);
    if (element != null) {
      return element.getInt();
    }
    throw new JSONParsingException("no int for key <" + name + ">");
  }

  public float getFloat(String name) throws JSONParsingException {
    JSONElement element = get(name);
    if (element != null) {
      return element.getFloat();
    }
    throw new JSONParsingException("no float for key <" + name + ">");
  }

  public JSONArray getArray(String name) throws JSONParsingException {
    JSONElement element = get(name);
    if (element instanceof JSONArray) {
      return (JSONArray) element;
    }
    throw new JSONParsingException("no array for key <" + name + ">");
  }

  public JSONObject getObject(String name) throws JSONParsingException {
    JSONElement element = get(name);
    if (element instanceof JSONObject) {
      return (JSONObject) element;
    }
    throw new JSONParsingException("no object associated for key <" + name + ">");
  }

  public String getString(String name) throws JSONParsingException {
    JSONElement element = get(name);
    if (element instanceof JSONString) {
      return  element.content();
    }
    throw new JSONParsingException("no string associated for key <" + name + ">");
  }

  public boolean getBoolean(String name) throws JSONParsingException {
    JSONElement element = get(name);
    if (element instanceof JSONToken) {
      return ((JSONToken) element).getBoolean();
    }
    throw new JSONParsingException("no boolean associated for key <" + name + ">");
  }

  /////////////////////////////////////////////////////////////////////////
  // By index
  /////////////////////////////////////////////////////////////////////////

  public JSONElement get(int index) throws JSONParsingException {
    if (index >= 0 && index < mElements.size()) {
      return mElements.get(index);
    }
    throw new JSONParsingException("no element at index " + index);
  }

  public int getInt(int index) throws JSONParsingException {
    JSONElement element = get(index);
    if (element != null) {
      return element.getInt();
    }
    throw new JSONParsingException("no int at index " + index);
  }

  public float getFloat(int index) throws JSONParsingException {
    JSONElement element = get(index);
    if (element != null) {
      return element.getFloat();
    }
    throw new JSONParsingException("no float at index " + index);
  }

  public JSONArray getArray(int index) throws JSONParsingException {
    JSONElement element = get(index);
    if (element instanceof JSONArray) {
      return (JSONArray) element;
    }
    throw new JSONParsingException("no array at index " + index);
  }

  public JSONObject getObject(int index) throws JSONParsingException {
    JSONElement element = get(index);
    if (element instanceof JSONObject) {
      return (JSONObject) element;
    }
    throw new JSONParsingException("no object at index " + index);
  }

  public String getString(int index) throws JSONParsingException {
    JSONElement element = get(index);
    if (element instanceof JSONString) {
      return  element.content();
    }
    throw new JSONParsingException("no string at index " + index);
  }

  public boolean getBoolean(int index) throws JSONParsingException {
    JSONElement element = get(index);
    if (element instanceof JSONToken) {
      return ((JSONToken) element).getBoolean();
    }
    throw new JSONParsingException("no boolean at index " + index);
  }
}
