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
        testBasicFormat("{ a: { start: ['parent', 'start', 20], " +
                "top: ['parent', 'top', 30] } }");
        testBasicFormat("{ test: 'hello, the', key: 'world' }");
        testBasicFormat("{ test: [1, 2, 3] }");
        testBasicFormat("{ test: ['hello', 'world', { value: 42 }] }");
        testBasicFormat("{ test: [null] }");
        testBasicFormat("{ test: [null, false, true] }");
        testBasicFormat("{ test: ['hello', 'world', { value: 42 }], value: false, " +
                "plop: 23, hello: { key: 42, text: 'bonjour' } }");
    }

    @Test
    public void testValue() {
        try {
            String test = "{ test: ['hello', 'world', { value: 42 }], value: false, plop: 23, " +
                    "hello: { key: 49, text: 'bonjour' } }";
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
            String test = "{ test: ['hello', 'world', { value: 42 }], value: false, " +
                    "plop: 23, hello: { key: 49, text: 'bonjour' } }";
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

    @Test
    public void testDoubleQuotes() {
        try {
            String test = "{ test: [\"hello\", \"world\"] }";
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
    public void testDoubleQuotesKey() {
        try {
            String test = "{ \"test\": [\"hello\", \"world\"] }";
            CLObject parsedContent = CLParser.parse(test);
            assertEquals("{ test: ['hello', 'world'] }", parsedContent.toJSON());
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
    public void testMultilines() {
        String test = "{\n" +
                "  firstName: 'John',\n" +
                "  lastName: 'Smith',\n" +
                "  isAlive: true,\n" +
                "  age: 27,\n" +
                "  address: {\n" +
                "    streetAddress: '21 2nd Street',\n" +
                "    city: 'New York',\n" +
                "    state: 'NY',\n" +
                "    postalCode: '10021-3100'\n" +
                "  },\n" +
                "  phoneNumbers: [\n" +
                "    {\n" +
                "      type: 'home',\n" +
                "      number: '212 555-1234'\n" +
                "    },\n" +
                "    {\n" +
                "      type: 'office',\n" +
                "      number: '646 555-4567'\n" +
                "    }\n" +
                "  ],\n" +
                "  children: [],\n" +
                "  spouse: null\n" +
                "}          ";
        try {
            CLObject parsedContent = CLParser.parse(test);
            assertEquals("John", parsedContent.getString("firstName"));
            assertEquals("{ firstName: 'John', lastName: 'Smith', isAlive: true, " +
                    "age: 27, address: { streetAddress: '21 2nd Street', city: 'New York', " +
                    "state: 'NY', postalCode: '10021-3100' }, " +
                    "phoneNumbers: [{ type: 'home', number: '212 555-1234' }, " +
                    "{ type: 'office', number: '646 555-4567' }], " +
                    "children: [], spouse: null }",
                    parsedContent.toJSON());
            assertEquals(2, parsedContent.getArray("phoneNumbers").length());
            CLElement element = parsedContent.get("spouse");
            if (element instanceof CLToken) {
                CLToken token = (CLToken) element;
                assertEquals(CLToken.Type.NULL, token.type);
            }
        } catch (CLParsingException e) {
            System.err.println("Exception " + e.reason());
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    public void testDoubleQuotesMultilines() {
        String test = "{\n" +
                "  \"firstName\": \"John\",\n" +
                "  \"lastName\": \"Smith\",\n" +
                "  \"isAlive\": true,\n" +
                "  \"age\": 27,\n" +
                "  \"address\": {\n" +
                "    \"streetAddress\": \"21 2nd Street\",\n" +
                "    \"city\": \"New York\",\n" +
                "    \"state\": \"NY\",\n" +
                "    \"postalCode\": \"10021-3100\"\n" +
                "  },\n" +
                "  \"phoneNumbers\": [\n" +
                "    {\n" +
                "      \"type\": \"home\",\n" +
                "      \"number\": \"212 555-1234\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\": \"office\",\n" +
                "      \"number\": \"646 555-4567\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"children\": [],\n" +
                "  \"spouse\": null\n" +
                "}          ";
        try {
            CLObject parsedContent = CLParser.parse(test);
            assertEquals("John", parsedContent.getString("firstName"));
            assertEquals("{ firstName: 'John', lastName: 'Smith', isAlive: true, " +
                    "age: 27, address: { streetAddress: '21 2nd Street', city: 'New York', " +
                    "state: 'NY', postalCode: '10021-3100' }, " +
                    "phoneNumbers: [{ type: 'home', number: '212 555-1234' }, " +
                    "{ type: 'office', number: '646 555-4567' }], " +
                    "children: [], spouse: null }", parsedContent.toJSON());
            assertEquals(2, parsedContent.getArray("phoneNumbers").length());
            CLElement element = parsedContent.get("spouse");
            if (element instanceof CLToken) {
                CLToken token = (CLToken) element;
                assertEquals(CLToken.Type.NULL, token.type);
            }
        } catch (CLParsingException e) {
            System.err.println("Exception " + e.reason());
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    public void testJSON5() {
        String test = "{\n" +
                      "  // comments\n  unquoted: 'and you can quote me on that',\n" +
                      "  singleQuotes: 'I can use \"double quotes\" here',\n" +
                      // "  hexadecimal: 0xdecaf,\n" +
                      "  leadingDecimalPoint: .8675309, andTrailing: 8675309.,\n" +
                      "  positiveSign: +1,\n" +
                      "  trailingComma: 'in objects', andIn: ['arrays',],\n" +
                      "  \"backwardsCompatible\": \"with JSON\",\n" +
                      "}";
        try {
            CLObject parsedContent = CLParser.parse(test);
            assertEquals("{ unquoted: 'and you can quote me on that', " +
                    "singleQuotes: 'I can use \"double quotes\" here', " +
                    "leadingDecimalPoint: 0.8675309, andTrailing: 8675309, " +
                    "positiveSign: 1, trailingComma: 'in objects', " +
                    "andIn: ['arrays'], backwardsCompatible: 'with JSON' }",
                    parsedContent.toJSON());
        } catch (CLParsingException e) {
            System.err.println("Exception " + e.reason());
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    public void testConstraints() {
        String test = "{\n" +
                      "  g1 : { type: 'vGuideline', start: 44 },\n" +
                      "  g2 : { type: 'vGuideline', end: 44 },\n" +
                      "  image: {\n" +
                      "    width: 201, height: 179,\n" +
                      "    top: ['parent','top', 32],\n" +
                      "    start: 'g1'\n" +
                      "  },\n";
        try {
            CLObject parsedContent = CLParser.parse(test);
            assertEquals("{ g1: { type: 'vGuideline', start: 44 }, " +
                            "g2: { type: 'vGuideline', end: 44 }, " +
                            "image: { width: 201, height: 179, top: ['parent', 'top', 32], " +
                            "start: 'g1' } }",
                    parsedContent.toJSON());
        } catch (CLParsingException e) {
            System.err.println("Exception " + e.reason());
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    public void testConstraints2() {
        String test = "            {\n" +
                "              Variables: {\n" +
                "                bottom: 20\n" +
                "              },\n" +
                "              Helpers: [\n" +
                "                ['hChain', ['a','b','c'], {\n" +
                "                  start: ['leftGuideline1', 'start'],\n" +
                "                  style: 'packed'\n" +
                "                }],\n" +
                "                ['hChain', ['d','e','f']],\n" +
                "                ['vChain', ['d','e','f'], {\n" +
                "                  bottom: ['topGuideline1', 'top']\n" +
                "                }],\n" +
                "                ['vGuideline', {\n" +
                "                  id: 'leftGuideline1', start: 100\n" +
                "                }],\n" +
                "                ['hGuideline', {\n" +
                "                  id: 'topGuideline1', percent: 0.5\n" +
                "                }]\n" +
                "              ],\n" +
                "              a: {\n" +
                "                bottom: ['b', 'top', 'bottom']\n" +
                "              },\n" +
                "              b: {\n" +
                "                width: '30%',\n" +
                "                height: '1:1',\n" +
                "                centerVertically: 'parent'\n" +
                "              },\n" +
                "              c: {\n" +
                "                top: ['b', 'bottom']\n" +
                "              }\n" +
                "            }";
        try {
            CLObject parsedContent = CLParser.parse(test);
            assertEquals("{ " +
                            "Variables: { bottom: 20 }, " +
                            "Helpers: [" +
                            "['hChain', ['a', 'b', 'c'], { start: ['leftGuideline1', 'start'], style: 'packed' }], " +
                            "['hChain', ['d', 'e', 'f']], " +
                            "['vChain', ['d', 'e', 'f'], { bottom: ['topGuideline1', 'top'] }], " +
                            "['vGuideline', { id: 'leftGuideline1', start: 100 }], " +
                            "['hGuideline', { id: 'topGuideline1', percent: 0.5 }]" +
                            "], " +
                            "a: { bottom: ['b', 'top', 'bottom'] }, " +
                            "b: { width: '30%', height: '1:1', centerVertically: 'parent' }, " +
                            "c: { top: ['b', 'bottom'] } }",
                    parsedContent.toJSON());
        } catch (CLParsingException e) {
            System.err.println("Exception " + e.reason());
            e.printStackTrace();
            assertTrue(false);
        }
    }
}


