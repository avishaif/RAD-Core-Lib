package unitTestGeneral;

import static org.junit.Assert.*;

import org.junit.Test;

import core.ThreadData;

public class ThreadDataTest
{

	@Test
	public void threadDataClasstest()
	{
		ThreadData t = new ThreadData("main", true, -50, null);
		assertEquals("main", t.getName());
		assertEquals(true, t.isJavaThread());
		assertEquals(-50, t.getPriority());
		assertArrayEquals(null, t.getAffinity());
		
		t.setAffinity(new int[] {0,1});
		t.setId(123);
		t.setName("KUKU");
		t.SetJavaThread(false);
		t.setPriority(50);
		assertEquals(2, t.getAffinity().length);
		assertNotEquals(0, t.getAffinity().length);
		assertEquals(123, t.getId());
		assertNotEquals(111, t.getId());
		assertFalse(t.isJavaThread());
		assertEquals("KUKU", t.getName());
		assertNotEquals("main", t.getName());
	}

}
