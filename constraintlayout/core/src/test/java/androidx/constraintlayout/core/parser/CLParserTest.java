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

    private void testBasicFormat(String content) {
        try {
            CLObject parsedContent = CLParser.parse(content);
            assertEquals(parsedContent.toJSON(), content);
        } catch (CLParsingException e) {
            System.err.println("Exception " + e.reason());
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    public void testParsing() {
        testBasicFormat("{ a: { start: ['parent', 'start', 20], top: ['parent', 'top', 30] } }");
        testBasicFormat("{ test: 'hello, the', key: 'world' }");
        testBasicFormat("{ test: [1, 2, 3] }");
        testBasicFormat("{ test: ['hello', 'world', { value: 42 }] }");
        testBasicFormat("{ test: [null] }");
        testBasicFormat("{ test: [null, false, true] }");
        testBasicFormat("{ test: ['hello', 'world', { value: 42 }], value: false, plop: 23, hello: { key: 42, text: 'bonjour' } }");
    }

    @Test
    public void testValue() {
        try {
            String test = "{ test: ['hello', 'world', { value: 42 }], value: false, plop: 23, hello: { key: 49, text: 'bonjour' } }";
            CLObject parsedContent = CLParser.parse(test);
            assertTrue(parsedContent.toJSON().equals(test));
            assertEquals("hello", parsedContent.getArray("test").getString(0));
            assertEquals("world", parsedContent.getArray("test").getString(1));
            assertEquals(42, parsedContent.getArray("test").getObject(2).get("value").getInt());
            assertEquals(false, parsedContent.getBoolean("value"));
            assertEquals(23, parsedContent.getInt("plop"));
            assertEquals(49, parsedContent.getObject("hello").getInt("key"));
            assertEquals("bonjour", parsedContent.getObject("hello").getString("text"));
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
            CLObject parsedContent = CLParser.parse(test);
            parsedContent.getObject("test").getString(0);
        } catch (CLParsingException e) {
            assertEquals("no object associated for key <test>", e.reason());
            e.printStackTrace();
        }
    }

    @Test
    public void testTrailingCommas() {
        try {
            String test = "{ test: ['hello', 'world'],,,,,,, }";
            CLObject parsedContent = CLParser.parse(test);
            assertEquals("hello", parsedContent.getArray("test").getString(0));
            assertEquals("world", parsedContent.getArray("test").getString(1));
            assertEquals("{ test: ['hello', 'world'] }", parsedContent.toJSON());
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
            CLObject parsedContent = CLParser.parse(test);
            assertEquals("hello", parsedContent.getArray("test").getString(0));
            assertEquals("world", parsedContent.getArray("test").getString(1));
            assertEquals("{ test: ['hello', 'world'] }", parsedContent.toJSON());
        } catch (CLParsingException e) {
            System.err.println("Exception " + e.reason());
            e.printStackTrace();
            assertTrue(false);
        }
    }
}


