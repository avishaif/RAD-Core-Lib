package unitTestGeneral;

import static org.junit.Assert.*;

import org.junit.Test;

import results.ProcessResult;

public class ProcessResultsTest
{

	@Test
	public void processResultsClassTest()
	{
		ProcessResult pres = new ProcessResult("eclipse.exe", true);
		assertEquals("eclipse.exe", pres.getName());
		assertNotEquals("eclipse", pres.getName());
		assertEquals(0, pres.getId());
		assertTrue(pres.getResult());
		
		pres.setId(123);
		pres.addThread("main", true);
		pres.addThread("kuku", false);
		assertEquals(123, pres.getId());
		assertEquals(2, pres.getThreads().size());
	}

}
