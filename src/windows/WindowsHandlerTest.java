package unit_test.windows;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import handlers.WindowsHandler;
import normalizers.Normalizer;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import core.ProcessData;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WindowsHandlerTest
{	
	/*
	 * BEFORE RUNNING UNIT TEST ON THIS CLASS
	 * MAKE SURE TO INSERT NEW VALUES TO ATTRIBUTES BELOW
	 */
	final int NATIVE_PROCESS_ID = 1988; // Test.exe ID
	final int NATIVE_THREAD_ID = 4308; // 'RAD-Core Thread A' ID through jstack
	final int JAVA_PROCESS_ID = 5708; // java.exe ID
	final int JAVA_THREAD_ID = 1; // current thread ID
	final int INVALID_ID = -2;
	final String NATIVE_PROCESS_NAME = "Test.exe";
	final String JAVA_PROCESS_NAME = "java.exe";
	final String JAVA_THREAD_NAME = "RAD-Core Thread A";
	final String INVALID_NAME = "KUKU";
	final int PRIORITY = 70;
	final int INVALID_PRIORITY_BELOW = -50;
	final int INVALID_PRIORITY_ABOVE = 200;
	
	Normalizer norm = new Normalizer("Windows");
	
	int[] affinityArray;
	WindowsHandler handler = new WindowsHandler(norm);
	
    
/*--------------------------- AFFINITY TEST ---------------------------------------*/
    
    /*
     * SET FINALS BEFORE RUNNING THE TESTS
     */
	
	
	@Test
	public void setProcessAffinityById()
	{
		
		System.out.println("setProcessAffinity by process ID test");
		System.out.println("-------------------------------------");
		//
		// true
		//
		boolean result = false;
		assertTrue(result = handler.setProcessAffinity(NATIVE_PROCESS_ID, new int[] {0}));
		System.out.println(result);
		assertTrue(result = handler.setProcessAffinity(NATIVE_PROCESS_ID, new int[] {0}));
		System.out.println(result);
		result = false;
		assertTrue(result = handler.setProcessAffinity(NATIVE_PROCESS_ID, new int[] {0,1}));
		System.out.println(result);
		result = false;
		assertTrue(result = handler.setProcessAffinity(NATIVE_PROCESS_ID, new int[] {1,1}));
		System.out.println(result);
		System.out.println();
		//
		// false
		//
		assertFalse(handler.setProcessAffinity(NATIVE_PROCESS_ID, null));
		System.out.println();
		assertFalse(handler.setProcessAffinity(NATIVE_PROCESS_ID, new int[] {}));
		System.out.println();
		assertFalse(handler.setProcessAffinity(NATIVE_PROCESS_ID, new int[] {0,-1}));
		System.out.println();
		assertFalse(handler.setProcessAffinity(NATIVE_PROCESS_ID, new int[] {0,3}));
		System.out.println();
		assertFalse(handler.setProcessAffinity(NATIVE_PROCESS_ID, new int[] {0,1,2}));
		System.out.println();
		assertFalse(handler.setProcessAffinity(INVALID_ID, null));
		System.out.println();
		assertFalse(handler.setProcessAffinity(INVALID_ID, new int[] {}));
		System.out.println();
		assertFalse(handler.setProcessAffinity(INVALID_ID, new int[] {0}));
		System.out.println();
		assertFalse(handler.setProcessAffinity(INVALID_ID, new int[] {0,1}));
		System.out.println();
		assertFalse(handler.setProcessAffinity(INVALID_ID, new int[] {0,-1}));
		System.out.println();
		assertFalse(handler.setProcessAffinity(INVALID_ID, new int[] {0,3}));
		System.out.println();
		assertFalse(handler.setProcessAffinity(INVALID_ID, new int[] {0,1,2}));
		System.out.println();
		System.out.println("--END OF TEST--\n");
	}
	
    
    
	@Test
	public void setProcessAffinityByName()
	{
		
		System.out.println("setProcessAffinity by process name test");
		System.out.println("---------------------------------------");
		//
		// true
		//
		boolean result = false;
		assertTrue(result = handler.setProcessAffinity(JAVA_PROCESS_NAME, new int[] {0}));
		System.out.println(result);
		result = false;
		assertTrue(result = handler.setProcessAffinity(JAVA_PROCESS_NAME, new int[] {1}));
		System.out.println(result);
		result = false;
		assertTrue(result = handler.setProcessAffinity(JAVA_PROCESS_NAME, new int[] {0,1}));
		System.out.println(result);
		System.out.println();
		//
		// false
		//
		assertFalse(handler.setProcessAffinity(NATIVE_PROCESS_NAME, null));
		System.out.println();
		assertFalse(handler.setProcessAffinity(NATIVE_PROCESS_NAME, new int[] {}));
		System.out.println();
		assertFalse(handler.setProcessAffinity(NATIVE_PROCESS_NAME, new int[] {0,-1}));
		System.out.println();
		assertFalse(handler.setProcessAffinity(NATIVE_PROCESS_NAME, new int[] {0,3}));
		System.out.println();
		assertFalse(handler.setProcessAffinity(NATIVE_PROCESS_NAME, new int[] {0,1,2}));
		System.out.println();
		assertFalse(handler.setProcessAffinity(INVALID_NAME, null));
		System.out.println();
		assertFalse(handler.setProcessAffinity(INVALID_NAME, new int[] {}));
		System.out.println();
		assertFalse(handler.setProcessAffinity(INVALID_NAME, new int[] {0}));
		System.out.println();
		assertFalse(handler.setProcessAffinity(INVALID_NAME, new int[] {0,1}));
		System.out.println();
		assertFalse(handler.setProcessAffinity(INVALID_NAME, new int[] {0,-1}));
		System.out.println();
		assertFalse(handler.setProcessAffinity(INVALID_NAME, new int[] {0,3}));
		System.out.println();
		assertFalse(handler.setProcessAffinity(INVALID_NAME, new int[] {0,1,2}));
		System.out.println();
		System.out.println("--END OF TEST--\n");
	}
	
	
	/*
	@Test
	public void setProcessAffinityByCollection()
	{
	
			
		List<ProcessData> procList = new ArrayList<>();
		ProcessData pdId = new ProcessData(NATIVE_PROCESS_ID, 50, new int[] {0});
		ProcessData pdName = new ProcessData(NATIVE_PROCESS_NAME, 50, new int[] {0});
		procList.add(pdId);
		procList.add(pdName);
	}
	*/
	
	
	@Test
	public void setNativeThreadAffinityById()
	{
		
		System.out.println("setNativeThreadAffinity by thread ID test");
		System.out.println("------------------------------------------");
		//
		// true
		//
		boolean result = false;
		assertTrue(result = handler.setNativeThreadAffinity(NATIVE_THREAD_ID, new int[] {0}));
		System.out.println(result);
		result = false;
		assertTrue(result = handler.setNativeThreadAffinity(NATIVE_THREAD_ID, new int[] {0,1}));
		System.out.println(result);
		result = false;
		assertTrue(result = handler.setNativeThreadAffinity(NATIVE_THREAD_ID, new int[] {1,1}));
		System.out.println(result);
		System.out.println();
		//
		// false
		//
		assertFalse(handler.setNativeThreadAffinity(NATIVE_THREAD_ID, null));
		System.out.println();
		assertFalse(handler.setNativeThreadAffinity(NATIVE_THREAD_ID, new int[] {}));
		System.out.println();
		assertFalse(handler.setNativeThreadAffinity(NATIVE_THREAD_ID, new int[] {0,-1}));
		System.out.println();
		assertFalse(handler.setNativeThreadAffinity(NATIVE_THREAD_ID, new int[] {0,3}));
		System.out.println();
		assertFalse(handler.setNativeThreadAffinity(NATIVE_THREAD_ID, new int[] {0,1,2}));
		System.out.println();
		assertFalse(handler.setNativeThreadAffinity(INVALID_ID, null));
		System.out.println();
		assertFalse(handler.setNativeThreadAffinity(INVALID_ID, new int[] {}));
		System.out.println();
		assertFalse(handler.setNativeThreadAffinity(INVALID_ID, new int[] {0}));
		System.out.println();
		assertFalse(handler.setNativeThreadAffinity(INVALID_ID, new int[] {0,1}));
		System.out.println();
		assertFalse(handler.setNativeThreadAffinity(INVALID_ID, new int[] {0,-1}));
		System.out.println();
		assertFalse(handler.setNativeThreadAffinity(INVALID_ID, new int[] {0,3}));
		System.out.println();
		assertFalse(handler.setNativeThreadAffinity(INVALID_ID, new int[] {0,1,2}));
		System.out.println();
		System.out.println("--END OF TEST--\n");
	}
	
    
	
	@Test
	public void setNativeThreadAffinityByName()
	{
			
		System.out.println("setNativeThreadAffinity by thread name test");
		System.out.println("-------------------------------------------");
		System.out.println("Native threads don't have names in Windows operating system.");
		System.out.println();
		assertFalse(handler.setNativeThreadAffinity(NATIVE_PROCESS_NAME, new int[] {0}));
		assertFalse(handler.setNativeThreadAffinity(INVALID_NAME, new int[] {0}));
		assertFalse(handler.setNativeThreadAffinity(NATIVE_PROCESS_NAME, new int[] {0,1}));
		assertFalse(handler.setNativeThreadAffinity(INVALID_NAME, new int[] {0,1}));
		assertFalse(handler.setNativeThreadAffinity(NATIVE_PROCESS_NAME, new int[] {0,1,2}));
		assertFalse(handler.setNativeThreadAffinity(INVALID_NAME, new int[] {0,1,2}));
		System.out.println("--END OF TEST--\n");
	}    
	
	@Test
	public void setNativeThreadAffinityByProcessNameAndThreadName()
	{
		System.out.println("setNativeThreadAffinity by process name and thread name test");
		System.out.println("------------------------------------------------------------");
		System.out.println("Native threads don't have names in Windows operating system.");
		System.out.println();
		System.out.println("--END OF TEST--\n");
	}
	
	@Test
    public void setNativeThreadAffinityByProcessIdAndThreadName()
    {
    	System.out.println("setNativeThreadAffinity by process name and thread name test");
		System.out.println("------------------------------------------------------------");
		System.out.println("Native threads don't have names in Windows operating system.");
		System.out.println();
		System.out.println("--END OF TEST--\n");
    }
	
	
    /*
	@Test
	public void setNativeThreadAffinityByCollection()
	{
		if(!initiated)
			init();
			
	}
	*/

    
	@Test
	public void setJavaThreadAffinityById()
	{
		
		System.out.println("setJavaThreadAffinity by thread ID test");
		System.out.println("---------------------------------------");
		//
		// true
		//
		boolean result = false;
		assertTrue(result = handler.setJavaThreadAffinity(JAVA_THREAD_ID, new int[] {0}));
		System.out.println(result);
		result = false;
		assertTrue(result = handler.setJavaThreadAffinity(JAVA_THREAD_ID, new int[] {0,1}));
		System.out.println(result);
		result = false;
		assertTrue(result = handler.setJavaThreadAffinity(JAVA_THREAD_ID, new int[] {0}));
		System.out.println(result);
		System.out.println();
		//
		// false
		//
		assertFalse(handler.setJavaThreadAffinity(JAVA_THREAD_ID, null));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(JAVA_THREAD_ID, new int[] {}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(JAVA_THREAD_ID, new int[] {0,-1}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(JAVA_THREAD_ID, new int[] {0,3}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(JAVA_THREAD_ID, new int[] {0,1,2}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(INVALID_ID, null));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(INVALID_ID, new int[] {}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(INVALID_ID, new int[] {0}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(INVALID_ID, new int[] {0,1}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(INVALID_ID, new int[] {0,-1}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(INVALID_ID, new int[] {0,3}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(INVALID_ID, new int[] {0,1,2}));
		System.out.println();
		System.out.println("--END OF TEST--\n");
	}
	
	
	
	@Test
	public void setJavaThreadAffinityByName()
	{
		
		System.out.println("setJavaThreadAffinity by thread name test");
		System.out.println("-----------------------------------------");
		//
		// true
		//
		boolean result = false;
		assertTrue(result = handler.setJavaThreadAffinity(JAVA_THREAD_NAME, new int[] {0}));
		System.out.println(result);
		result = false;
		assertTrue(result = handler.setJavaThreadAffinity(JAVA_THREAD_NAME, new int[] {0,1}));
		System.out.println(result);
		result = false;
		assertTrue(result = handler.setJavaThreadAffinity(JAVA_THREAD_NAME, new int[] {1,1}));
		System.out.println(result);
		System.out.println();
		//
		// false
		//
		assertFalse(handler.setJavaThreadAffinity(JAVA_THREAD_NAME, null));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(JAVA_THREAD_NAME, new int[] {}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(JAVA_THREAD_NAME, new int[] {0,-1}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(JAVA_THREAD_NAME, new int[] {0,3}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(JAVA_THREAD_NAME, new int[] {0,1,2}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(INVALID_NAME, null));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(INVALID_NAME, new int[] {}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(INVALID_NAME, new int[] {0}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(INVALID_NAME, new int[] {0,1}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(INVALID_NAME, new int[] {0,-1}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(INVALID_NAME, new int[] {0,3}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(INVALID_NAME, new int[] {0,1,2}));
		System.out.println();
		System.out.println("--END OF TEST--\n");
	}
	
	
	
	@Test
	public void setJavaThreadAffinityByProcessNameAndThreadName()
	{
		
		System.out.println("setJavaThreadAffinity by process name and thread name test");
		System.out.println("----------------------------------------------------------");
		//
		// true
		//
		boolean result = false;
		assertTrue(result = handler.setJavaThreadAffinity(JAVA_PROCESS_NAME, JAVA_THREAD_NAME, new int[] {0}));
		System.out.println(result);
		result = false;
		assertTrue(result = handler.setJavaThreadAffinity(JAVA_PROCESS_NAME, JAVA_THREAD_NAME, new int[] {0,1}));
		System.out.println(result);
		result = false;
		assertTrue(result = handler.setJavaThreadAffinity(JAVA_PROCESS_NAME, JAVA_THREAD_NAME, new int[] {1,1}));
		System.out.println(result);
		System.out.println();
		//
		// false
		//
		assertFalse(handler.setJavaThreadAffinity(JAVA_PROCESS_NAME, JAVA_THREAD_NAME, null));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(JAVA_PROCESS_NAME, JAVA_THREAD_NAME, new int[] {}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(JAVA_PROCESS_NAME, JAVA_THREAD_NAME, new int[] {0,-1}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(JAVA_PROCESS_NAME, JAVA_THREAD_NAME, new int[] {0,3}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(JAVA_PROCESS_NAME, JAVA_THREAD_NAME, new int[] {0,1,2}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(INVALID_NAME, JAVA_THREAD_NAME, null));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(INVALID_NAME, JAVA_THREAD_NAME, new int[] {}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(INVALID_NAME, JAVA_THREAD_NAME, new int[] {0,-1}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(INVALID_NAME, JAVA_THREAD_NAME, new int[] {0,3}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(INVALID_NAME, JAVA_THREAD_NAME, new int[] {0,1,2}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(JAVA_PROCESS_NAME, INVALID_NAME, null));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(JAVA_PROCESS_NAME, INVALID_NAME, new int[] {}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(JAVA_PROCESS_NAME, INVALID_NAME, new int[] {0}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(JAVA_PROCESS_NAME, INVALID_NAME, new int[] {0,1}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(JAVA_PROCESS_NAME, INVALID_NAME, new int[] {0,-1}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(JAVA_PROCESS_NAME, INVALID_NAME, new int[] {0,3}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(JAVA_PROCESS_NAME, INVALID_NAME, new int[] {0,1,2}));
		System.out.println();
		System.out.println("--END OF TEST--\n");
	}
	
	
	
	@Test
	public void setJavaThreadAffinityByProcessIdAndThreadName()
	{
		
		System.out.println("setJavaThreadAffinity by process ID and thread name test");
		System.out.println("----------------------------------------------------------");
		//
		// true
		//
		boolean result = false;
		assertTrue(result = handler.setJavaThreadAffinity(JAVA_PROCESS_ID, JAVA_THREAD_NAME, new int[] {0}));
		System.out.println(result);
		result = false;
		assertTrue(result = handler.setJavaThreadAffinity(JAVA_PROCESS_ID, JAVA_THREAD_NAME, new int[] {0,1}));
		System.out.println(result);
		result = false;
		assertTrue(result = handler.setJavaThreadAffinity(JAVA_PROCESS_ID, JAVA_THREAD_NAME, new int[] {1,1}));
		System.out.println(result);
		System.out.println();
		//
		// false
		//
		assertFalse(handler.setJavaThreadAffinity(JAVA_PROCESS_ID, JAVA_THREAD_NAME, null));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(JAVA_PROCESS_ID, JAVA_THREAD_NAME, new int[] {}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(JAVA_PROCESS_ID, JAVA_THREAD_NAME, new int[] {0,-1}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(JAVA_PROCESS_ID, JAVA_THREAD_NAME, new int[] {0,3}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(JAVA_PROCESS_ID, JAVA_THREAD_NAME, new int[] {0,1,2}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(INVALID_ID, JAVA_THREAD_NAME, null));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(INVALID_ID, JAVA_THREAD_NAME, new int[] {}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(INVALID_ID, JAVA_THREAD_NAME, new int[] {0,-1}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(INVALID_ID, JAVA_THREAD_NAME, new int[] {0,3}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(INVALID_ID, JAVA_THREAD_NAME, new int[] {0,1,2}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(JAVA_PROCESS_ID, INVALID_NAME, null));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(JAVA_PROCESS_ID, INVALID_NAME, new int[] {}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(JAVA_PROCESS_ID, INVALID_NAME, new int[] {0}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(JAVA_PROCESS_ID, INVALID_NAME, new int[] {0,1}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(JAVA_PROCESS_ID, INVALID_NAME, new int[] {0,-1}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(JAVA_PROCESS_ID, INVALID_NAME, new int[] {0,3}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(JAVA_PROCESS_ID, INVALID_NAME, new int[] {0,1,2}));
		System.out.println();
		System.out.println("--END OF TEST--\n");
	}
	
	
/*----------------------------- PRIORITY TEST ---------------------------------------*/	
	
    
	@Test
	public void setProcessPriorityById()
	{
		
		System.out.println("setProcessPriority by process ID test");
		System.out.println("-------------------------------------");
		//
		// true
		//
		boolean result = false;
		assertTrue(result = handler.setProcessPriority(NATIVE_PROCESS_ID, PRIORITY));
		System.out.println(result);
		System.out.println();
		//
		// false
		//
		assertFalse(handler.setProcessPriority(NATIVE_PROCESS_ID, INVALID_PRIORITY_BELOW));
		System.out.println();
		assertFalse(handler.setProcessPriority(NATIVE_PROCESS_ID, INVALID_PRIORITY_ABOVE));
		System.out.println();
		assertFalse(handler.setProcessPriority(INVALID_ID, PRIORITY));
		System.out.println();
		assertFalse(handler.setProcessPriority(INVALID_ID, INVALID_PRIORITY_BELOW));
		System.out.println();
		assertFalse(handler.setProcessPriority(INVALID_ID, INVALID_PRIORITY_ABOVE));
		System.out.println();
		System.out.println("--END OF TEST--\n");
	}
	
    
    
	@Test
	public void setProcessPriorityByName()
	{
		
		System.out.println("setProcessPriority by process name test");
		System.out.println("---------------------------------------");
		//
		// true
		//
		boolean result = false;
		assertTrue(result = handler.setProcessPriority(JAVA_PROCESS_NAME, PRIORITY));
		System.out.println(result);
		System.out.println();
		//
		// false
		//
		assertFalse(handler.setProcessPriority(JAVA_PROCESS_NAME, INVALID_PRIORITY_BELOW));
		System.out.println();
		assertFalse(handler.setProcessPriority(JAVA_PROCESS_NAME, INVALID_PRIORITY_ABOVE));
		System.out.println();
		assertFalse(handler.setProcessPriority(INVALID_NAME, PRIORITY));
		System.out.println();
		assertFalse(handler.setProcessPriority(INVALID_NAME, INVALID_PRIORITY_BELOW));
		System.out.println();
		assertFalse(handler.setProcessPriority(INVALID_NAME, INVALID_PRIORITY_ABOVE));
		System.out.println();
		System.out.println("--END OF TEST--\n");
	}
	
	
	/*
	@Test
	public void setProcessAffinityByCollection()
	{
		if(!initiated)
				init();
			
		List<ProcessData> procList = new ArrayList<>();
		ProcessData pdId = new ProcessData(NATIVE_PROCESS_ID, 50, new int[] {0});
		ProcessData pdName = new ProcessData(NATIVE_PROCESS_NAME, 50, new int[] {0});
		procList.add(pdId);
		procList.add(pdName);
	}
	*/
	
	
	@Test
	public void setNativeThreadPriorityById()
	{
		
		System.out.println("setNativeThreadPriority by thread ID test");
		System.out.println("------------------------------------------");
		//
		// true
		//
		boolean result = false;
		assertTrue(result = handler.setNativeThreadPriority(NATIVE_THREAD_ID, PRIORITY));
		System.out.println(result);
		System.out.println();
		//
		// false
		//
		assertFalse(handler.setNativeThreadPriority(NATIVE_THREAD_ID, INVALID_PRIORITY_BELOW));
		System.out.println();
		assertFalse(handler.setNativeThreadPriority(NATIVE_THREAD_ID, INVALID_PRIORITY_ABOVE));
		System.out.println();
		assertFalse(handler.setNativeThreadPriority(INVALID_ID, PRIORITY));
		System.out.println();
		assertFalse(handler.setNativeThreadPriority(INVALID_ID, INVALID_PRIORITY_BELOW));
		System.out.println();
		assertFalse(handler.setNativeThreadPriority(INVALID_ID, INVALID_PRIORITY_ABOVE));
		System.out.println();
		System.out.println("--END OF TEST--\n");
	}
	
    
	
	@Test
	public void setNativeThreadPriorityByName()
	{
			
		System.out.println("setNativeThreadAffinity by thread name test");
		System.out.println("-------------------------------------------");
		System.out.println("Native threads don't have names in Windows operating system.");
		System.out.println();
		System.out.println("--END OF TEST--\n");
	}    
	
	@Test
	public void setNativeThreadPriorityByProcessNameAndThreadName()
	{
		System.out.println("setNativeThreadAffinity by process name and thread name test");
		System.out.println("------------------------------------------------------------");
		System.out.println("Native threads don't have names in Windows operating system.");
		System.out.println();
		System.out.println("--END OF TEST--\n");
	}
	
	@Test
    public void setNativeThreadPriorityByProcessIdAndThreadName()
    {
    	System.out.println("setNativeThreadAffinity by process name and thread name test");
		System.out.println("------------------------------------------------------------");
		System.out.println("Native threads don't have names in Windows operating system.");
		System.out.println();
		System.out.println("--END OF TEST--\n");
    }
	
	
    /*
	@Test
	public void setNativeThreadAffinityByCollection()
	{
		if(!initiated)
			init();
			
	}
	*/

    
	@Test
	public void setJavaThreadPriorityById()
	{
		
		System.out.println("setJavaThreadPriority by thread ID test");
		System.out.println("------------------------------------------");
		//
		// true
		//
		boolean result = false;
		assertTrue(result = handler.setJavaThreadPriority(JAVA_THREAD_ID, PRIORITY));
		System.out.println(result);
		System.out.println();
		//
		// false
		//
		assertFalse(handler.setJavaThreadPriority(JAVA_THREAD_ID, INVALID_PRIORITY_BELOW));
		System.out.println();
		assertFalse(handler.setJavaThreadPriority(JAVA_THREAD_ID, INVALID_PRIORITY_ABOVE));
		System.out.println();
		assertFalse(handler.setJavaThreadPriority(INVALID_ID, PRIORITY));
		System.out.println();
		assertFalse(handler.setJavaThreadPriority(INVALID_ID, INVALID_PRIORITY_BELOW));
		System.out.println();
		assertFalse(handler.setJavaThreadPriority(INVALID_ID, INVALID_PRIORITY_ABOVE));
		System.out.println();
		System.out.println("--END OF TEST--\n");
	}
	
	
	
	@Test
	public void setJavaThreadPriorityByName()
	{
		
		System.out.println("setJavaThreadPriority by thread ID test");
		System.out.println("------------------------------------------");
		//
		// true
		//
		boolean result = false;
		assertTrue(result = handler.setJavaThreadPriority(JAVA_THREAD_NAME, PRIORITY));
		System.out.println(result);
		System.out.println();
		//
		// false
		//
		assertFalse(handler.setJavaThreadPriority(JAVA_THREAD_NAME, INVALID_PRIORITY_BELOW));
		System.out.println();
		assertFalse(handler.setJavaThreadPriority(JAVA_THREAD_NAME, INVALID_PRIORITY_ABOVE));
		System.out.println();
		assertFalse(handler.setJavaThreadPriority(INVALID_ID, PRIORITY));
		System.out.println();
		assertFalse(handler.setJavaThreadPriority(INVALID_ID, INVALID_PRIORITY_BELOW));
		System.out.println();
		assertFalse(handler.setJavaThreadPriority(INVALID_ID, INVALID_PRIORITY_ABOVE));
		System.out.println();
		System.out.println("--END OF TEST--\n");
	}
	
	
	
	@Test
	public void setJavaThreadPriorityByProcessNameAndThreadName()
	{
		
		System.out.println("setJavaThreadPriority by process name and thread name test");
		System.out.println("----------------------------------------------------------");
		//
		// true
		//
		boolean result = false;
		assertTrue(result = handler.setJavaThreadPriority(JAVA_PROCESS_NAME, JAVA_THREAD_NAME, PRIORITY));
		System.out.println(result);
		System.out.println();
		//
		// false
		//
		assertFalse(handler.setJavaThreadPriority(JAVA_PROCESS_NAME, JAVA_THREAD_NAME, INVALID_PRIORITY_BELOW));
		System.out.println();
		assertFalse(handler.setJavaThreadPriority(JAVA_PROCESS_NAME, JAVA_THREAD_NAME, INVALID_PRIORITY_ABOVE));
		System.out.println();
		assertFalse(handler.setJavaThreadPriority(INVALID_NAME, JAVA_THREAD_NAME, PRIORITY));
		System.out.println();
		assertFalse(handler.setJavaThreadPriority(JAVA_PROCESS_NAME, INVALID_NAME, PRIORITY));
		System.out.println();
		assertFalse(handler.setJavaThreadPriority(INVALID_NAME, INVALID_NAME, PRIORITY));
		System.out.println();
		System.out.println("--END OF TEST--\n");
	}
	
	
	
	@Test
	public void setJavaThreadPriorityByProcessIdAndThreadName()
	{
		
		System.out.println("setJavaThreadPriority by process ID and thread name test");
		System.out.println("--------------------------------------------------------");
		//
		// true
		//
		boolean result = false;
		assertTrue(result = handler.setJavaThreadPriority(JAVA_PROCESS_ID, JAVA_THREAD_NAME, PRIORITY));
		System.out.println(result);
		System.out.println();
		//
		// false
		//
		assertFalse(handler.setJavaThreadPriority(JAVA_PROCESS_ID, JAVA_THREAD_NAME, INVALID_PRIORITY_BELOW));
		System.out.println();
		assertFalse(handler.setJavaThreadPriority(JAVA_PROCESS_ID, JAVA_THREAD_NAME, INVALID_PRIORITY_ABOVE));
		System.out.println();
		assertFalse(handler.setJavaThreadPriority(INVALID_ID, JAVA_THREAD_NAME, PRIORITY));
		System.out.println();
		assertFalse(handler.setJavaThreadPriority(JAVA_PROCESS_ID, INVALID_NAME, PRIORITY));
		System.out.println();
		assertFalse(handler.setJavaThreadPriority(INVALID_ID, INVALID_NAME, PRIORITY));
		System.out.println();
		System.out.println("--END OF TEST--\n");
	}
	
	
	
	
	
	
	
	
}







