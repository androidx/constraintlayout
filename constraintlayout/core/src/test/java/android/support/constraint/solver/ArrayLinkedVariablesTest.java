package android.support.constraint.solver;


import org.testng.Assert;
import org.testng.annotations.Test;


/**
 * Test nested layout
 */
public class ArrayLinkedVariablesTest {

    @Test
    public void testNestedLayout() {
        Cache cache = new Cache();
        ArrayRow row = new ArrayRow(cache);
        ArrayLinkedVariables variables = new ArrayLinkedVariables(row, cache );
        SolverVariable []v = new SolverVariable[9];
        for (int i = 0; i < v.length; i++) {
            int p = i^3;
            v[i] = new SolverVariable("dog"+p+"("+i+")"+p, SolverVariable.Type.UNRESTRICTED);
            cache.mIndexedVariables[i] = v[i];
            v[i].id = i;
            variables.add(v[i],20f, true);
            if (i%2==1) {
                variables.remove(v[i/2], true);

            }
             variables.display();
            System.out.println();
        }
        for (int i = 0; i < v.length; i++) {
            if (i%2==1) {
                variables.display();
                variables.add(v[i / 2], 24f, true);
            }
        }
        Assert.assertTrue(true);
    }

}
