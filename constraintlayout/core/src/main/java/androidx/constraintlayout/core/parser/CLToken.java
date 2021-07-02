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

public class CLToken extends CLElement {
  int index = 0;
  Type type = Type.UNKNOWN;

  public boolean getBoolean() throws CLParsingException {
    if (type == Type.TRUE) {
      return true;
    }
    if (type == Type.FALSE) {
      return false;
    }
    throw new CLParsingException("this token is not a boolean: <" + content() + ">", this);
  }

  public boolean isNull() throws CLParsingException {
    if (type == Type.NULL) {
      return true;
    }
    throw new CLParsingException("this token is not a null: <" + content() + ">", this);
  }

  enum Type { UNKNOWN, TRUE, FALSE, NULL }

  char[] tokenTrue = "true".toCharArray();
  char[] tokenFalse = "false".toCharArray();
  char[] tokenNull = "null".toCharArray();

  public CLToken(char[] content) {
    super(content);
  }

  public static CLElement allocate(char[] content) {
    return new CLToken(content);
  }

  protected String toJSON() {
    if (CLParser.DEBUG) {
      return "<" + content() + ">";
    } else {
      return content();
    }
  }

  protected String toFormattedJSON(int indent, int forceIndent) {
    StringBuilder json = new StringBuilder();
    for (int i = 0; i < indent; i++) {
      json.append(' ');
    }
    json.append(content());
    return json.toString();
  }

  public Type getType() {
    return type;
  }

  public boolean validate(char c, long position) {
    boolean isValid = false;
    switch (type) {
      case TRUE: {
        isValid = (tokenTrue[index] == c);
        if (isValid && index + 1 == tokenTrue.length) {
          setEnd(position);
        }
      } break;
      case FALSE: {
        isValid = (tokenFalse[index] == c);
        if (isValid && index + 1 == tokenFalse.length) {
          setEnd(position);
        }
      } break;
      case NULL: {
        isValid = (tokenNull[index] == c);
        if (isValid && index + 1 == tokenNull.length) {
          setEnd(position);
        }
      } break;
      case UNKNOWN: {
        if (tokenTrue[index] == c) {
          type = Type.TRUE;
          isValid = true;
        } else if (tokenFalse[index] == c) {
          type = Type.FALSE;
          isValid = true;
        } else if (tokenNull[index] == c) {
          type = Type.NULL;
          isValid = true;
        }
      }
    }

    index ++;
    return isValid;
  }

}
