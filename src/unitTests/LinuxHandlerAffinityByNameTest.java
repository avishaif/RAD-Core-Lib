/*
 * unit test all Affinity methods on LinuxHandler with String 
 * identifiers as parameter
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
public class LinuxHandlerAffinityByNameTest {
    private int[] affinity;
    private String name;
    private boolean expected;
    private LinuxHandler handler;
    Normalizer normalizer;
    public LinuxHandlerAffinityByNameTest(boolean expected,String name , int[] affinity) {
        this.expected = expected;
        this.name = name;
        this.affinity = affinity;
    }
     @Parameters
    public static Collection<Object[]> setProcessAffinityId() {
        return Arrays.asList(new Object[][] {
            {false, "", null},
            {false, "not empty", null},
            {false, "not empty", new int[] {}},
            {false, "not empty", new int[] {1,1,1,1,1}},
            {false, "systemd", new int[] {0,1,2,3}},
            {true , "chrome", new int[] {0,1}}
        });
    }
    
    @Before
    public void init()
    {
    	normalizer = new Normalizer("Linux");
    	handler = new LinuxHandler(normalizer);
    }
     /**
     * Test of setProcessAffinity method, of class LinuxHandler.
     * 
     */
    @Test
    public void testSetProcessAffinity_String_intArr() {
        System.out.println("setProcessAffinity");
        assertEquals(expected, handler.setProcessAffinity(name, affinity));
    }
    /**
    * Test of setThreadAffinity method, of class LinuxHandler.
    */
    @Test
    public void testSetAffinity_String_intArr() {
        System.out.println("setAffinity");
        assertEquals(expected, handler.setNativeThreadAffinity(name, affinity));
    }
}
