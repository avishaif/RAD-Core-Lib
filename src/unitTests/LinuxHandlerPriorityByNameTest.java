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
import normalizers.Normalizer;

/**
 *
 * @author Avishai Fox
 */
@RunWith(Parameterized.class)
public class LinuxHandlerPriorityByNameTest {

    private int priority;
    private String name;
    private boolean expected;
    private LinuxHandler handler;
	private Normalizer normalizer;
    public LinuxHandlerPriorityByNameTest(boolean expected, String name, int priority) {
        this.name = name;
        this.priority = priority;
        this.expected = expected;
    }

    @Parameters
    public static Collection<Object[]> setProcessPriorityName() {
        return Arrays.asList(new Object[][]{
            {false, "", 1},
            {false, "not empty", 1},
            {false, "not empty", 1},
            {false, "not empty", 0},
            {false, "not empty", 101},
            {false, "systemd", 1},
            {false, "chrome", 0},
            {true, "chrome", 1}
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
//    @Test
//    public void testSetProcessPriority_string_int_int() {
//        System.out.println("setProcessPriority");
//        assertEquals(expected, handler.setProcessPriority(name, priority));
//    }

    /**
     * Test of setThreadPriority method, of class LinuxHandler.
     */
    @Test
    public void testSetThreadPriority_string_int_int() {
        System.out.println("setThreadPriority");
        assertEquals(expected, handler.setNativeThreadPriority(name, priority));
    }
}

