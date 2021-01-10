/*
 * Copyright (C) 2015 The Android Open Source Project
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

package androidx.constraintlayout.core;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AmountTest {
    Amount a1 = new Amount(2, 3);
    Amount a2 = new Amount(3, 5);

    @Before
    public void setUp() {
        a1.set(2, 3);
        a2.set(3, 5);
    }

    @Test
    public void testAdd() {
        a1.add(a2);
        assertEquals(a1.getNumerator(), 19);
        assertEquals(a1.getDenominator(), 15);
    }

    @Test
    public void testSubstract() {
        a1.substract(a2);
        assertEquals(a1.getNumerator(), 1);
        assertEquals(a1.getDenominator(), 15);
    }

    @Test
    public void testMultiply() {
        a1.multiply(a2);
        assertEquals(a1.getNumerator(), 2);
        assertEquals(a1.getDenominator(), 5);
    }

    @Test
    public void testDivide() {
        a1.divide(a2);
        assertEquals(a1.getNumerator(), 10);
        assertEquals(a1.getDenominator(), 9);
    }

    @Test
    public void testSimplify() {
        a1.set(20, 30);
        assertEquals(a1.getNumerator(), 2);
        assertEquals(a1.getDenominator(), 3);
        a1.set(77, 88);
        assertEquals(a1.getNumerator(), 7);
        assertEquals(a1.getDenominator(), 8);
    }

    @Test
    public void testEquality() {
        a2.set(a1.getNumerator(), a1.getDenominator());
        assertTrue(a1.equals(a2));
    }
}
