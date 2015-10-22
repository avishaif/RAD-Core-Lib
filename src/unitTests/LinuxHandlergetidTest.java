package unitTests;

/**
* Test of thread/process id retrieval methods on LinuxHandler 
* parameters
*/
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import handlers.LinuxHandler;
import handlers.LinuxServiceClass;
import normalizers.Normalizer;

/**
 *
 * @author dementia
 */
@RunWith(Parameterized.class)
public class LinuxHandlergetidTest {

	private String name;
	private int expected;

	public LinuxHandlergetidTest(int expected, String name) {
		this.expected = expected;
		this.name = name;
	}

	@Parameters
	public static Collection<Object[]> setProcessAffinityId() {
		return Arrays.asList(new Object[][] {
				// {-1, ""},
				// {-1, "does not exist"},
				{ 1, "systemd" } });
	}

	/**
	 * Test of getPid method, of class LinuxHandler.
	 *
	 * @throws java.lang.Exception
	 */
	@Test
	public void testGetPid_string() throws Exception {
		System.out.println("getPid");
		Normalizer normalizer = new Normalizer("Linux");
		LinuxHandler linuxHandler = new LinuxHandler(normalizer);
		LinuxServiceClass service = new LinuxServiceClass(linuxHandler.getProcessorCount());
		assertEquals(expected, service.getPid(name));
	}

	/**
	 * Test of getTid method, of class LinuxHandler.
	 *
	 * @throws java.lang.Exception
	 */
	@Test
	public void testGetTid_String() throws Exception {
		System.out.println("getTid");
		Normalizer normalizer = new Normalizer("Linux");
		LinuxHandler linuxHandler = new LinuxHandler(normalizer);
		LinuxServiceClass service = new LinuxServiceClass(linuxHandler.getProcessorCount());
		assertEquals(expected, service.getTid(name));
	}

	/**
	 * Test of getTid method, of class LinuxHandler.
	 *
	 * @throws java.lang.Exception
	 */
	@Test
	public void testGetTid_int_String() throws Exception {
		System.out.println("getTid");
		Normalizer normalizer = new Normalizer("Linux");
		LinuxHandler linuxHandler = new LinuxHandler(normalizer);
		LinuxServiceClass service = new LinuxServiceClass(linuxHandler.getProcessorCount());
		assertEquals(expected, service.getTid(1, name));
	}
}
