/**
 * Test of all Affinity methods on handler with Int identifiers as
 * parameters
 * 
*/
package unitTests;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import handlers.LinuxHandler;
import normalizers.Normalizer;




/**
 *
 * @author Avishai Fox
 */
@RunWith(Parameterized.class)
public class LinuxHandlerAffinityByIdTest {

    private int id;
    private int[] affinity;
    private boolean expected;
    private LinuxHandler handler;
    private Normalizer normalizer;
    public LinuxHandlerAffinityByIdTest(boolean expected, int id, int[] affinity) {
        this.affinity = affinity;
        this.expected = expected;
        this.id = id;
    }

    @Parameters
    public static Collection<Object[]> setProcessAffinityId() {
        return Arrays.asList(new Object[][]{
            {false, -1, null},
            {false, 1, null},
            {false, 1, new int[]{}},
            {false, 1, new int[]{1,1,1,1,1}},
            {false, 1, new int[]{1,2}},
            {false, 1, new int[]{0,1,2}},
        });
    }

    @Before
    public void init()
    {
    	normalizer = new Normalizer("Linux");
    	handler = new LinuxHandler(normalizer);
    }
    /**
     * Test of setProcessAffinity method, of class handler.
     *
     */
    @Test
    public void testSetProcessAffinity_int_intArr() {
        System.out.println("setProcessAffinity");
        assertEquals(expected, handler.setProcessAffinity(id, affinity));
    }

    /**
     * Test of setThreadAffinity method, of class handler.
     */
    @Test
    public void testSetThreadAffinity_int_intArr() {
        System.out.println("setThreadAffinity");
        assertEquals(expected, handler.setNativeThreadAffinity(id, affinity));
    }
}

