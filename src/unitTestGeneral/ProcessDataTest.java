package unitTestGeneral;

import static org.junit.Assert.*;

import org.junit.Test;

import core.ProcessData;
import core.ThreadData;

public class ProcessDataTest
{

	@Test
	public void processDataClassTest()
	{
		ProcessData proc = new ProcessData(-1, -1, null);
		proc.addThread(new ThreadData("main", true, -50, new int[] {0,1}));
		proc.addThread(new ThreadData(null, true, 50, new int[] {0}));
		
		assertEquals(-1, proc.getId());
		assertNotEquals(1, proc.getId());
		assertEquals(-1, proc.getPriority());
		assertNotEquals(1, proc.getPriority());
		assertEquals(null, proc.getAffinity());
		assertEquals(2, proc.getThreads().size());
		assertNotEquals(1, proc.getThreads().size());
		
		proc.setName("eclipse.exe");
		proc.setId(123);
		proc.setPriority(50);
		proc.setAffinity(new int[] {0,1});
		assertEquals(2, proc.getAffinity().length);
		assertNotEquals(1, proc.getAffinity().length);
		assertNotEquals(null, proc.getAffinity());
		assertEquals("eclipse.exe", proc.getName());
		assertNotEquals(null, proc.getName());
		assertEquals(50, proc.getPriority());
		assertNotEquals(20, proc.getPriority());
	}

}
