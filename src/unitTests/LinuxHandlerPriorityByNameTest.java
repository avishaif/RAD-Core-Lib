/**
 * Test of all Priority methods on LinuxHandler with String identifiers as
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

/**
 *
 * @author Avishai Fox
 */
@RunWith(Parameterized.class)
public class LinuxHandlerPriorityByNameTest {

    private int priority;
    private int policy;
    private String name;
    private boolean expected;
    private LinuxHandler handler;
    public LinuxHandlerPriorityByNameTest(boolean expected, String name, int policy, int priority) {
        this.name = name;
        this.policy = policy;
        this.priority = priority;
        this.expected = expected;
    }

    @Parameters
    public static Collection<Object[]> setProcessAffinityId() {
        return Arrays.asList(new Object[][]{
            {false, "", 0, 0},
            {false, "not empty", -1, 0},
            {false, "not empty", 3, 0},
            {false, "not empty", 0, -21},
            {false, "not empty", 0, 20},
            {false, "not empty", 1, 0},
            {false, "not empty", 1, 100},
            {false, "systemd", 0, 0},
            {true, "chrome", 0, 0}
        });
    }
    
    @Before
    public void init(){
    	handler = new LinuxHandler();
    }

    /**
     * Test of setProcessPriority method, of class LinuxHandler.
     */
    @Test
    public void testSetProcessPriority_string_int_int() {
        System.out.println("setProcessPriority");
        assertEquals(expected, handler.setProcessPriority(name, policy, priority));
    }

    /**
     * Test of setThreadPriority method, of class LinuxHandler.
     */
    @Test
    public void testSetThreadPriority_string_int_int() {
        System.out.println("setThreadPriority");
        assertEquals(expected, handler.setNativeThreadPriority(name, policy, priority));
    }
}

