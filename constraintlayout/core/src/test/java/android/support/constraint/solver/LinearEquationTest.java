package android.support.constraint.solver;

import android.support.constraint.solver.LinearEquation;
import android.support.constraint.solver.LinearSystem;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class LinearEquationTest {
    LinearSystem s;
    LinearEquation e;

    @BeforeMethod
    public void setUp() {
        s = new LinearSystem();
        e = new LinearEquation();
        e.setSystem(s);
        LinearEquation.resetNaming();
    }

    @Test
    public void testDisplay1() {
        e.var("A").equalsTo().var(100);
        assertEquals(e.toString(), "A = 100");
    }

    @Test
    public void testDisplay2() {
        e.var("A").equalsTo().var("B");
        assertEquals(e.toString(), "A = B");
    }

    @Test
    public void testDisplay3() {
        e.var("A").greaterThan().var("B");
        assertEquals(e.toString(), "A >= B");
    }

    @Test
    public void testDisplay4() {
        e.var("A").lowerThan().var("B");
        assertEquals(e.toString(), "A <= B");
    }

    @Test
    public void testDisplay5() {
        e.var("A").greaterThan().var("B").plus(100);
        assertEquals(e.toString(), "A >= B + 100");
    }

    @Test
    public void testDisplay6() {
        e.var("A").plus("B").minus("C").plus(50).greaterThan().var("B").plus("C").minus(100);
        assertEquals(e.toString(), "A + B - C + 50 >= B + C - 100");
    }

    @Test
    public void testDisplay7() {
        e.var("A").lowerThan().var("B");
        e.normalize();
        assertEquals(e.toString(), "A + s1 = B");
    }

    @Test
    public void testDisplay8() {
        e.var("A").greaterThan().var("B");
        e.normalize();
        assertEquals(e.toString(), "A - s1 = B");
    }

    @Test
    public void testDisplay9() {
        e.var("A").greaterThan().var("B").withError();
        e.normalize();
        assertEquals(e.toString(), "A - s1 = B + e1+ - e1-");
    }

    @Test
    public void testDisplaySimplify() {
        e.var("A").plus(5).minus(2).plus(2, "B").minus(3, "B").greaterThan().var("C").minus(3, "C").withError();
        assertEquals(e.toString(), "A + 5 - 2 + 2 B - 3 B >= C - 3 C + e1+ - e1-");
        e.normalize();
        assertEquals(e.toString(), "A + 5 - 2 + 2 B - 3 B - s1 = C - 3 C + e1+ - e1-");
        e.simplify();
        assertEquals(e.toString(), "3 + A - B - s1 = - 2 C + e1+ - e1-");
    }

    @Test
    public void testDisplayBalance1() {
        e.var("A").plus(5).minus(2).plus(2, "B").minus(3, "B").greaterThan().var("C").minus(3, "C").withError();
        e.normalize();
        try {
            e.balance();
        } catch(Exception e) {
            System.err.println("Exception raised: " + e);
        }
        assertEquals(e.toString(), "A = - 3 + B - 2 C + e1+ - e1- + s1");
    }

    @Test
    public void testDisplayBalance2() {
        e.plus(5).minus(2).minus(2, "A").minus(3, "B").equalsTo().var(5, "C");
        try {
            e.balance();
        } catch(Exception e) {
            System.err.println("Exception raised: " + e);
        }
        assertEquals(e.toString(), "A = 3/2 - 3/2 B - 5/2 C");
    }

    @Test
    public void testDisplayBalance3() {
        e.plus(5).equalsTo().var(3);
        try {
            e.balance();
        } catch(Exception e) {
            assertTrue(true);
        }
        assertFalse(false);
    }

    @Test
    public void testDisplayBalance4() {
        // s1 = - 200 - e1- + 236 + e1- + e2+ - e2-
        e.withSlack().equalsTo().var(-200).withError("e1-", -1).plus(236);
        e.withError("e1-", 1).withError("e2+", 1).withError("e2-", -1);
        try {
            e.balance();
        } catch(Exception e) {
            System.err.println("Exception raised: " + e);
        }
        assertEquals(e.toString(), "s1 = 36 + e2+ - e2-");
    }

    @Test
    public void testDisplayBalance5() {
        // 236 + e1- + e2+ - e2- = e1- - e2+ + e2-
        e.var(236).withError("e1-", 1).withError("e2+", 1).withError("e2-", -1);
        e.equalsTo().withError("e1-", 1).withError("e2+", -1).withError("e2-", 1);
        try {
            e.balance();
        } catch(Exception e) {
            System.err.println("Exception raised: " + e);
        }
        // 236 + e1- + e2+ - e2- = e1- - e2+ + e2-
        // 0 = e1- - e2+ + e2- -236 -e1- - e2+ + e2-
        // 0 =     - e2+ + e2- -236      - e2+ + e2-
        // 0 = -236 - 2 e2+ + 2 e2-
        // 2 e2+ = -236 + 2 e2-
        // e2+ = -118 + e2-
        assertEquals(e.toString(), "e2+ = - 118 + e2-");
    }
}