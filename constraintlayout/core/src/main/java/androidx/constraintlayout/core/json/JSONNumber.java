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

public class JSONNumber extends JSONElement {

  float value = Float.NaN;
  public JSONNumber(char[] content) {
    super(content);
  }

  public static JSONElement allocate(char[] content) {
    return new JSONNumber(content);
  }

  protected String toJSON() {
    float value = Float.parseFloat(content());
    int intValue = (int) value;
    if ((float) intValue == value) {
      return "" + intValue;
    }
    return "" + value;
  }

  @Override
  public int getInt() {
    if (Float.isNaN(value)) {
      value = Integer.parseInt(content());
    }
    return (int) value;
  }

  @Override
  public float getFloat() {
    if (Float.isNaN(value)) {
      value = Float.parseFloat(content());
    }
    return value;
  }
}