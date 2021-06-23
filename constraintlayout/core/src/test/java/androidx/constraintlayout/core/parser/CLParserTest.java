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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CLParserTest {

    private void testBasicJson(String json) {
        try {
            CLObject jsonObject = CLParser.parse(json);
            assertEquals(jsonObject.toJSON(), json);
        } catch (CLParsingException e) {
            System.err.println("Exception " + e.reason());
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    public void testJson() {
        testBasicJson("{ a: { start: ['parent', 'start', 20], top: ['parent', 'top', 30] } }");
        testBasicJson("{ test: 'hello, the', key: 'world' }");
        testBasicJson("{ test: [1, 2, 3] }");
        testBasicJson("{ test: ['hello', 'world', { value: 42 }] }");
        testBasicJson("{ test: [null] }");
        testBasicJson("{ test: [null, false, true] }");
        testBasicJson("{ test: ['hello', 'world', { value: 42 }], value: false, plop: 23, hello: { key: 42, text: 'bonjour' } }");
    }

    @Test
    public void testJsonValue() {
        try {
            String test = "{ test: ['hello', 'world', { value: 42 }], value: false, plop: 23, hello: { key: 49, text: 'bonjour' } }";
            CLObject jsonObject = CLParser.parse(test);
            assertTrue(jsonObject.toJSON().equals(test));
            assertEquals("hello", jsonObject.getArray("test").getString(0));
            assertEquals("world", jsonObject.getArray("test").getString(1));
            assertEquals(42, jsonObject.getArray("test").getObject(2).get("value").getInt());
            assertEquals(false, jsonObject.getBoolean("value"));
            assertEquals(23, jsonObject.getInt("plop"));
            assertEquals(49, jsonObject.getObject("hello").getInt("key"));
            assertEquals("bonjour", jsonObject.getObject("hello").getString("text"));
        } catch (CLParsingException e) {
            System.err.println("Exception " + e.reason());
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    public void testException() {
        try {
            String test = "{ test: ['hello', 'world', { value: 42 }], value: false, plop: 23, hello: { key: 49, text: 'bonjour' } }";
            CLObject jsonObject = CLParser.parse(test);
            jsonObject.getObject("test").getString(0);
        } catch (CLParsingException e) {
            assertEquals("no object associated for key <test>", e.reason());
            e.printStackTrace();
        }
    }

    @Test
    public void testTrailingCommas() {
        try {
            String test = "{ test: ['hello', 'world'],,,,,,, }";
            CLObject jsonObject = CLParser.parse(test);
            assertEquals("hello", jsonObject.getArray("test").getString(0));
            assertEquals("world", jsonObject.getArray("test").getString(1));
            assertEquals("{ test: ['hello', 'world'] }", jsonObject.toJSON());
        } catch (CLParsingException e) {
            System.err.println("Exception " + e.reason());
            e.printStackTrace();
            assertTrue(false);
        }
    }


    @Test
    public void testIncompleteObject() {
        try {
            String test = "{ test: ['hello', 'world";
            CLObject jsonObject = CLParser.parse(test);
            assertEquals("hello", jsonObject.getArray("test").getString(0));
            assertEquals("world", jsonObject.getArray("test").getString(1));
            assertEquals("{ test: ['hello', 'world'] }", jsonObject.toJSON());
        } catch (CLParsingException e) {
            System.err.println("Exception " + e.reason());
            e.printStackTrace();
            assertTrue(false);
        }
    }
}


