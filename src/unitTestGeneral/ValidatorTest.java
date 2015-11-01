package unitTestGeneral;

import static org.junit.Assert.*;
import handlers.WindowsHandler;
import handlers.WindowsServiceClass;
import normalizers.Normalizer;

import org.junit.Test;

import validate.Validator;

public class ValidatorTest
{
	Normalizer norm = new Normalizer("Windows");
	WindowsHandler handler = new WindowsHandler(norm);
	

	@Test
	public void pidAffinityTest()
	{
		System.out.println("check parameters: pid and affinity test");
		System.out.println("---------------------------------------");
		WindowsServiceClass.initServiceClass();
		Validator.setNumberOfCpus(WindowsServiceClass.getNumberOfCpus());
		int pid = WindowsServiceClass.getProcessIdByProcessName("eclipse.exe");
		assertTrue(Validator.checkAffinityParams(pid, new int[] {0}));
		assertTrue(Validator.checkAffinityParams(pid, new int[] {0,1}));
		assertTrue(Validator.checkAffinityParams(pid, new int[] {2}));
		assertFalse(Validator.checkAffinityParams(-1, new int[] {0}));
		assertFalse(Validator.checkAffinityParams(pid, new int[] {0,1,2}));
		System.out.println("--END OF TEST--\n");
	}
	
	@Test
	public void pNameAffinityTest()
	{
		System.out.println("check parameters: pName and affinity test");
		System.out.println("-----------------------------------------");
		WindowsServiceClass.initServiceClass();
		Validator.setNumberOfCpus(WindowsServiceClass.getNumberOfCpus());
		String pName = "eclipse.exe";
		assertTrue(Validator.checkAffinityParams(pName, new int[] {0}));
		assertTrue(Validator.checkAffinityParams(pName, new int[] {0,1}));
		assertTrue(Validator.checkAffinityParams(pName, new int[] {2}));
		assertFalse(Validator.checkAffinityParams(pName, new int[] {0,1,2}));
		assertFalse(Validator.checkAffinityParams(null, new int[] {0}));
		System.out.println("--END OF TEST--\n");
	}
	
	@Test
	public void pNameTNameAffinityTest()
	{
		System.out.println("check parameters: pName, tName and affinity test");
		System.out.println("------------------------------------------------");
		WindowsServiceClass.initServiceClass();
		Validator.setNumberOfCpus(WindowsServiceClass.getNumberOfCpus());
		String pName = "eclipse.exe";
		String tName = "main";
		assertTrue(Validator.checkAffinityParams(pName, tName, new int[] {0}));
		assertTrue(Validator.checkAffinityParams(pName, tName, new int[] {0,1}));
		assertTrue(Validator.checkAffinityParams(pName, tName, new int[] {2}));
		assertFalse(Validator.checkAffinityParams(pName, tName, new int[] {0,1,2}));
		assertFalse(Validator.checkAffinityParams(null, null, new int[] {0}));
		System.out.println("--END OF TEST--\n");
	}
	
	@Test
	public void pidTNameAffinityTest()
	{
		System.out.println("check parameters: pid, tName and affinity test");
		System.out.println("----------------------------------------------");
		WindowsServiceClass.initServiceClass();
		Validator.setNumberOfCpus(WindowsServiceClass.getNumberOfCpus());
		int pid = WindowsServiceClass.getProcessIdByProcessName("eclipse.exe");
		String tName = "main";
		assertTrue(Validator.checkAffinityParams(pid, tName, new int[] {0,1}));
		assertFalse(Validator.checkAffinityParams(null, null, new int[] {0}));
		System.out.println("--END OF TEST--\n");
	}
	
	@Test
	public void pidPriorityTest()
	{
		System.out.println("check parameters: pid and priority test");
		System.out.println("---------------------------------------");
		WindowsServiceClass.initServiceClass();
		Validator.setNumberOfCpus(WindowsServiceClass.getNumberOfCpus());
		int pid = WindowsServiceClass.getProcessIdByProcessName("eclipse.exe");
		assertTrue(Validator.checkPriorityParams(pid, 50));
		assertFalse(Validator.checkPriorityParams(pid, -50));
		System.out.println("--END OF TEST--\n");
	}
	
	@Test
	public void pNamePriorityTest()
	{
		System.out.println("check parameters: pName and priority test");
		System.out.println("-----------------------------------------");
		WindowsServiceClass.initServiceClass();
		Validator.setNumberOfCpus(WindowsServiceClass.getNumberOfCpus());
		String pName = "eclipse.exe";
		assertTrue(Validator.checkPriorityParams(pName, 50));
		assertFalse(Validator.checkPriorityParams(pName, -50));
		System.out.println("--END OF TEST--\n");
	}
	
	@Test
	public void pNameTNamePriorityTest()
	{
		System.out.println("check parameters: pName, tName and priority test");
		System.out.println("-------------------------------------------------");
		WindowsServiceClass.initServiceClass();
		Validator.setNumberOfCpus(WindowsServiceClass.getNumberOfCpus());
		String pName = "eclipse.exe";
		String tName = "main";
		assertTrue(Validator.checkPriorityParams(pName, tName, 50));
		assertFalse(Validator.checkPriorityParams(pName, tName, -50));
		System.out.println("--END OF TEST--\n");
	}
	
	@Test
	public void pidTNamePriorityTest()
	{
		System.out.println("check parameters: pName, tName and priority test");
		System.out.println("-------------------------------------------------");
		WindowsServiceClass.initServiceClass();
		Validator.setNumberOfCpus(WindowsServiceClass.getNumberOfCpus());
		int pid = WindowsServiceClass.getProcessIdByProcessName("eclipse.exe");
		String tName = "main";
		assertTrue(Validator.checkPriorityParams(pid, tName, 50));
		assertFalse(Validator.checkPriorityParams(pid, tName, -50));
		System.out.println("--END OF TEST--\n");
	}	

}
