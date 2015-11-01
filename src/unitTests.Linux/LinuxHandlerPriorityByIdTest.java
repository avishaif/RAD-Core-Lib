/**
  * Test of all Priority methods on LinuxHandler with Int  
  * identifiers as parameters
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
public class LinuxHandlerPriorityByIdTest {

    private int priority;
    private int id;
    private boolean expected;
    private LinuxHandler handler;
	private Normalizer normalizer;

    public LinuxHandlerPriorityByIdTest(boolean expected, int id, int priority) {
        this.priority = priority;
        this.expected = expected;
        this.id = id;
    }

    @Parameters
    public static Collection<Object[]> setProcessAffinityId() {
        return Arrays.asList(new Object[][]{
            {false, -1, 1},
            {false, -1, 0},
            {false, 1, 0},
            {false, 1, 101},
            {false, 1, 100},
            {false, 1, 1},
            });
    }
    
    @Before
    public void init(){
    	normalizer = new Normalizer("Linux");
    	handler = new LinuxHandler(normalizer);
    }

    /**
     * Test of setProcessPriority method, of class LinuxHandler.
     */
    @Test
    public void testSetProcessPriority__int_int_int() {
        System.out.println("setProcessPriority");
        assertEquals(expected, handler.setProcessPriority(id, priority));
    }

    /**
     * Test of setThreadPriority method, of class LinuxHandler.
     */
    @Test
    public void testSetThreadPriority_int_int_int() {
        System.out.println("setThreadPriority");
        assertEquals(expected, handler.setNativeThreadPriority(id, priority));
    }
}
