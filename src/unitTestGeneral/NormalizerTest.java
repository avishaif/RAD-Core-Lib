/**
 * 
 */
package unitTestGeneral;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import core.Constants;
import normalizers.Normalizer;

/**
 * @author dementia
 *
 */
public class NormalizerTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@Test
	public void test() {

		Normalizer norm = new Normalizer("Windows");
		if (norm.checkSchema())
			norm.mapValues();
		assertEquals(Constants.IDLE_PRIORITY_CLASS, norm.normalize(1, true));
		assertEquals(Constants.REALTIME_PRIORITY_CLASS, norm.normalize(172, true));
		assertEquals(Constants.REALTIME_PRIORITY_CLASS, norm.normalize(206, true));
		assertEquals(Constants.THREAD_PRIORITY_IDLE, norm.normalize(1, false));
		assertEquals(Constants.THREAD_PRIORITY_TIME_CRITICAL, norm.normalize(238, false));
		
		Normalizer normLinux = new Normalizer("Linux");
		if (normLinux.checkSchema())
			normLinux.mapValues();
		assertEquals(19, normLinux.normalize(1)[0]);
		assertEquals(-20, normLinux.normalize(40)[0]);
		assertEquals(1, normLinux.normalize(41)[0]);
		assertEquals(99, normLinux.normalize(139)[0]);
		assertEquals(1, normLinux.normalize(140)[0]);
		assertEquals(99, normLinux.normalize(238)[0]);
	}
}
