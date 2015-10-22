package unit_test.windows;

import static org.junit.Assert.*;
import handlers.WindowsServiceClass;

import org.junit.Test;

public class WindowsServiceClassTest
{
	
	@Test
	public void getAffinityMask()
	{
		System.out.println("Affinity mask test");
		System.out.println("------------------");
		WindowsServiceClass.initServiceClass();
		assertEquals(3, WindowsServiceClass.getAffinityMask(new int[] {0,1}));
		assertNotEquals(1, WindowsServiceClass.getAffinityMask(new int[] {0,1}));
		assertEquals(0, WindowsServiceClass.getAffinityMask(new int[] {0,1,2}));
		assertEquals(0, WindowsServiceClass.getAffinityMask(new int[] {}));
		System.out.println("Test finished");
		System.out.println();
	}
	
	@Test
	public void getNumberOfCpus()
	{
		System.out.println("Number of CPUs test");
		System.out.println("-------------------");
		WindowsServiceClass.initServiceClass();
		assertEquals(2, WindowsServiceClass.getNumberOfCpus());
		assertNotEquals(1, WindowsServiceClass.getNumberOfCpus());
		System.out.println("Test finished");
		System.out.println();
	}
	
	@Test
	public void getProcessIdByProcessName()
	{
		System.out.println("Process ID by process name test");
		System.out.println("-------------------------------");
		WindowsServiceClass.initServiceClass();
		assertEquals(-1, WindowsServiceClass.getProcessIdByProcessName("KUKU"));
		int eclipsePid = WindowsServiceClass.getProcessIdByProcessName("eclipse.exe");
		assertEquals(eclipsePid, WindowsServiceClass.getProcessIdByProcessName("eclipse.exe"));
		System.out.println("Test finished");
		System.out.println();
	}
	
	@Test
	public void getProcessNameByProcessId()
	{
		System.out.println("Process name by process ID test");
		System.out.println("-------------------------------");
		WindowsServiceClass.initServiceClass();
		String pname = "eclipse.exe";
		int pid = WindowsServiceClass.getProcessIdByProcessName("eclipse.exe");
		assertEquals(pname, WindowsServiceClass.getProcessNameByProcessId(pid));
		assertNotEquals("KUKU", WindowsServiceClass.getProcessNameByProcessId(pid));
		System.out.println("Test finished");
		System.out.println();
	}

}
