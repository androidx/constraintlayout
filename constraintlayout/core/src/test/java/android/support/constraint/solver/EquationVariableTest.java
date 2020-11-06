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

package android.support.constraint.solver;

import android.support.constraint.solver.EquationVariable;
import android.support.constraint.solver.LinearSystem;
import android.support.constraint.solver.SolverVariable;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class EquationVariableTest {
    LinearSystem s;
    EquationVariable e1;
    EquationVariable e2;

    @BeforeMethod
    public void setUp() {
        s = new LinearSystem();
        e1 = new EquationVariable(s, 200);
        e2 = new EquationVariable(s, 200);
    }

    @Test
    public void testEquality() {
        assertTrue(e1.getAmount().equals(e2.getAmount()));
    }

    @Test
    public void testAddition() {
        e1.add(e2);
        assertEquals(e1.getAmount().getNumerator(), 400);
    }

    @Test
    public void testSubstraction() {
        e1.substract(e2);
        assertEquals(e1.getAmount().getNumerator(), 0);
    }

    @Test
    public void testMultiply() {
        e1.multiply(e2);
        assertEquals(e1.getAmount().getNumerator(), 40000);
    }

    @Test
    public void testDivide() {
        e1.divide(e2);
        assertEquals(e1.getAmount().getNumerator(), 1);
    }

    @Test
    public void testCompatible() {
        assertTrue(e1.isCompatible(e2));
        e2 = new EquationVariable(s, 200, "TEST", SolverVariable.Type.UNRESTRICTED);
        assertFalse(e1.isCompatible(e2));
    }
}