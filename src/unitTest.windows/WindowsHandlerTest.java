package unitTest.windows;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javathreadshandlers.JavaThreadHandlerWindows;
import handlers.WindowsHandler;
import handlers.WindowsServiceClass;
import normalizers.Normalizer;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import results.ProcessResult;
import results.Result;
import core.ProcessData;
import core.ThreadData;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WindowsHandlerTest
{	
	int PROCESS_ID;
	int NATIVE_THREAD_ID;
	int JAVA_THREAD_ID;
	int INVALID_ID = -1;
	String PROCESS_NAME = "eclipse.exe";
	String JAVA_THREAD_NAME = "main";
	String INVALID_NAME = "KUKU";
	int PRIORITY = 30;
	int INVALID_PRIORITY = -50;
	
	Process proc;
	Normalizer norm = new Normalizer("Windows");
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
		
		PROCESS_ID = WindowsServiceClass.getProcessIdByProcessName(PROCESS_NAME);
		//
		// true
		//
		boolean result = false;
		assertTrue(result = handler.setProcessAffinity(PROCESS_ID, new int[] {0}));
		System.out.println(result);
		result = false;
		assertTrue(result = handler.setProcessAffinity(PROCESS_ID, new int[] {0,1}));
		System.out.println(result);
		result = false;
		assertTrue(result = handler.setProcessAffinity(PROCESS_ID, new int[] {1,1}));
		System.out.println(result);
		System.out.println();
		//
		// false
		//
		assertFalse(handler.setProcessAffinity(PROCESS_ID, null));
		System.out.println();
		assertFalse(handler.setProcessAffinity(PROCESS_ID, new int[] {}));
		System.out.println();
		assertFalse(handler.setProcessAffinity(PROCESS_ID, new int[] {0,-1}));
		System.out.println();
		assertFalse(handler.setProcessAffinity(INVALID_ID, null));
		System.out.println();
		assertFalse(handler.setProcessAffinity(INVALID_ID, new int[] {}));
		System.out.println();
		assertFalse(handler.setProcessAffinity(INVALID_ID, new int[] {0}));
		System.out.println();
		assertFalse(handler.setProcessAffinity(INVALID_ID, new int[] {0,-1}));
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
		assertTrue(result = handler.setProcessAffinity(PROCESS_NAME, new int[] {0}));
		System.out.println(result);
		result = false;
		assertTrue(result = handler.setProcessAffinity(PROCESS_NAME, new int[] {0,1}));
		System.out.println(result);
		System.out.println();
		//
		// false
		//
		assertFalse(handler.setProcessAffinity(PROCESS_NAME, null));
		System.out.println();
		assertFalse(handler.setProcessAffinity(PROCESS_NAME, new int[] {}));
		System.out.println();
		assertFalse(handler.setProcessAffinity(PROCESS_NAME, new int[] {0,-1}));
		System.out.println();
		assertFalse(handler.setProcessAffinity(INVALID_NAME, null));
		System.out.println();
		assertFalse(handler.setProcessAffinity(INVALID_NAME, new int[] {}));
		System.out.println();
		assertFalse(handler.setProcessAffinity(INVALID_NAME, new int[] {0}));
		System.out.println();
		assertFalse(handler.setProcessAffinity(INVALID_NAME, new int[] {0,-1}));
		System.out.println();
		System.out.println("--END OF TEST--\n");
	}
	
	
	
	@Test
	public void setProcessAffinityByCollection() throws IOException
	{
		Process proc = Runtime.getRuntime().exec("calc.exe");
		int procId = WindowsServiceClass.getProcessIdByProcessName("calc.exe");
		PROCESS_ID = WindowsServiceClass.getProcessIdByProcessName(PROCESS_NAME);
		
		ProcessData proc1 = new ProcessData(PROCESS_ID, PRIORITY, new int[] {0,1});
		ThreadData thread1 = new ThreadData(JAVA_THREAD_NAME, true, PRIORITY, new int[] {0,1});
		proc1.addThread(thread1);
		ProcessData proc2 = new ProcessData(procId, PRIORITY, new int[] {0});
		ThreadData thread2 = new ThreadData(INVALID_NAME, true, PRIORITY, new int[] {0});	
		proc2.addThread(thread2);
		
		List<ProcessData> procList = new ArrayList<>();
		procList.add(proc1);
		procList.add(proc2);
		List<ProcessResult> results = new ArrayList<>();
		results = handler.setProcessAffinity(procList);	
		for(ProcessResult result : results)
		{
			assertTrue(result.getResult());
			List<Result> threadRes = result.getThreads();
			for(Result tr : threadRes)
			{
				if(tr.getName().equals(JAVA_THREAD_NAME))
				{
					assertTrue(tr.getResult());
				}
				else if(tr.getName().equals(INVALID_NAME))
				{
					assertFalse(tr.getResult());
				}
			}
		}
		proc.destroy();
	}
	
	
	
	@Test
	public void setNativeThreadAffinityById()
	{
		
		System.out.println("setNativeThreadAffinity by thread ID test");
		System.out.println("------------------------------------------");
		
		WindowsServiceClass.initServiceClass();
		JavaThreadHandlerWindows javaHandler = new JavaThreadHandlerWindows(WindowsServiceClass.getKernel32Instance());
		NATIVE_THREAD_ID = javaHandler.getNativeThreadId("main");
		
		//
		// true
		//
		boolean result = false;
		assertTrue(result = handler.setNativeThreadAffinity(NATIVE_THREAD_ID, new int[] {0,1}));
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
		assertFalse(handler.setNativeThreadAffinity(INVALID_ID, null));
		System.out.println();
		assertFalse(handler.setNativeThreadAffinity(INVALID_ID, new int[] {}));
		System.out.println();
		assertFalse(handler.setNativeThreadAffinity(INVALID_ID, new int[] {0}));
		System.out.println();
		assertFalse(handler.setNativeThreadAffinity(INVALID_ID, new int[] {0,-1}));
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
		assertFalse(handler.setNativeThreadAffinity(JAVA_THREAD_NAME, new int[] {0}));
		assertFalse(handler.setNativeThreadAffinity(INVALID_NAME, new int[] {0}));
		assertFalse(handler.setNativeThreadAffinity(JAVA_THREAD_NAME, new int[] {0,1}));
		assertFalse(handler.setNativeThreadAffinity(INVALID_NAME, new int[] {0,1}));
		System.out.println("--END OF TEST--\n");
	}    
	
	
	@Test
	public void setNativeThreadAffinityByProcessNameAndThreadName()
	{
		System.out.println("setNativeThreadAffinity by process name and thread name test");
		System.out.println("------------------------------------------------------------");
		System.out.println("Native threads don't have names in Windows operating system.");
		System.out.println();
		assertFalse(handler.setNativeThreadAffinity(PROCESS_NAME, JAVA_THREAD_NAME, new int[] {0}));
		assertFalse(handler.setNativeThreadAffinity(INVALID_NAME, JAVA_THREAD_NAME, new int[] {0}));
		assertFalse(handler.setNativeThreadAffinity(PROCESS_NAME, INVALID_NAME, new int[] {0,1}));
		System.out.println("--END OF TEST--\n");
	}
	
	@Test
    public void setNativeThreadAffinityByProcessIdAndThreadName()
    {
    	System.out.println("setNativeThreadAffinity by process name and thread name test");
		System.out.println("------------------------------------------------------------");
		PROCESS_ID = WindowsServiceClass.getProcessIdByProcessName(PROCESS_NAME);
		System.out.println("Native threads don't have names in Windows operating system.");
		System.out.println();
		assertFalse(handler.setNativeThreadAffinity(PROCESS_ID, JAVA_THREAD_NAME, new int[] {0}));
		assertFalse(handler.setNativeThreadAffinity(INVALID_ID, JAVA_THREAD_NAME, new int[] {0}));
		assertFalse(handler.setNativeThreadAffinity(PROCESS_ID, INVALID_NAME, new int[] {0,1}));
		System.out.println("--END OF TEST--\n");
    }
	
	
    
	@Test
	public void setNativeThreadAffinityByCollection()
	{
		ThreadData thread1 = new ThreadData(JAVA_THREAD_NAME, true, PRIORITY, new int[] {0,1});
		ThreadData thread2 = new ThreadData(INVALID_NAME, true, PRIORITY, new int[] {0});	
		List<ThreadData> thrdList = new ArrayList<>();
		thrdList.add(thread1);
		thrdList.add(thread2);
		List<Result> results = new ArrayList<>();
		results = handler.setThreadAffinity(thrdList);
		for(Result result : results)
		{
			if(result.getName().equals(JAVA_THREAD_NAME))
			{
				assertTrue(result.getResult());
			}
			else if(result.getName().equals(INVALID_NAME))
			{
				assertFalse(result.getResult());
			}
		}
	}
	

    
	@Test
	public void setJavaThreadAffinityById()
	{
		
		System.out.println("setJavaThreadAffinity by thread ID test");
		System.out.println("---------------------------------------");
		
		JAVA_THREAD_ID = (int) Thread.currentThread().getId();
		
		//
		// true
		//
		boolean result = false;
		assertTrue(result = handler.setJavaThreadAffinity(JAVA_THREAD_ID, new int[] {0,1}));
		System.out.println(result);
		result = false;
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
		assertFalse(handler.setJavaThreadAffinity(INVALID_ID, null));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(INVALID_ID, new int[] {}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(INVALID_ID, new int[] {0}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(INVALID_ID, new int[] {0,-1}));
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
		assertTrue(result = handler.setJavaThreadAffinity(JAVA_THREAD_NAME, new int[] {0,1}));
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
		assertTrue(result = handler.setJavaThreadAffinity(PROCESS_NAME, JAVA_THREAD_NAME, new int[] {0}));
		System.out.println(result);
		result = false;
		assertTrue(result = handler.setJavaThreadAffinity(PROCESS_NAME, JAVA_THREAD_NAME, new int[] {0,1}));
		System.out.println(result);
		System.out.println();
		//
		// false
		//
		assertFalse(handler.setJavaThreadAffinity(PROCESS_NAME, JAVA_THREAD_NAME, null));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(PROCESS_NAME, JAVA_THREAD_NAME, new int[] {}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(PROCESS_NAME, JAVA_THREAD_NAME, new int[] {0,-1}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(INVALID_NAME, JAVA_THREAD_NAME, null));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(INVALID_NAME, JAVA_THREAD_NAME, new int[] {}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(INVALID_NAME, JAVA_THREAD_NAME, new int[] {0,-1}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(PROCESS_NAME, INVALID_NAME, null));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(PROCESS_NAME, INVALID_NAME, new int[] {}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(PROCESS_NAME, INVALID_NAME, new int[] {0}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(PROCESS_NAME, INVALID_NAME, new int[] {0,-1}));
		System.out.println();
		System.out.println("--END OF TEST--\n");
	}
	
	
	
	@Test
	public void setJavaThreadAffinityByProcessIdAndThreadName()
	{
		
		System.out.println("setJavaThreadAffinity by process ID and thread name test");
		System.out.println("----------------------------------------------------------");
		
		PROCESS_ID = WindowsServiceClass.getProcessIdByProcessName(PROCESS_NAME);
		//
		// true
		//
		boolean result = false;
		assertTrue(result = handler.setJavaThreadAffinity(PROCESS_ID, JAVA_THREAD_NAME, new int[] {0}));
		System.out.println(result);
		result = false;
		assertTrue(result = handler.setJavaThreadAffinity(PROCESS_ID, JAVA_THREAD_NAME, new int[] {0,1}));
		System.out.println(result);
		System.out.println();
		//
		// false
		//
		assertFalse(handler.setJavaThreadAffinity(PROCESS_ID, JAVA_THREAD_NAME, null));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(PROCESS_ID, JAVA_THREAD_NAME, new int[] {}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(PROCESS_ID, JAVA_THREAD_NAME, new int[] {0,-1}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(INVALID_ID, JAVA_THREAD_NAME, null));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(INVALID_ID, JAVA_THREAD_NAME, new int[] {}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(INVALID_ID, JAVA_THREAD_NAME, new int[] {0,-1}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(PROCESS_ID, INVALID_NAME, null));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(PROCESS_ID, INVALID_NAME, new int[] {}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(PROCESS_ID, INVALID_NAME, new int[] {0}));
		System.out.println();
		assertFalse(handler.setJavaThreadAffinity(PROCESS_ID, INVALID_NAME, new int[] {0,-1}));
		System.out.println();
		System.out.println("--END OF TEST--\n");
	}
	
	
/*----------------------------- PRIORITY TEST ---------------------------------------*/	
	
    
	@Test
	public void setProcessPriorityById()
	{
		System.out.println("setProcessPriority by process ID test");
		System.out.println("-------------------------------------");
		
		PROCESS_ID = WindowsServiceClass.getProcessIdByProcessName(PROCESS_NAME);
		norm.mapValues();
		//
		// true
		//
		boolean result = false;
		assertTrue(result = handler.setProcessPriority(PROCESS_ID, PRIORITY));
		System.out.println(result);
		System.out.println();
		//
		// false
		//
		assertFalse(handler.setProcessPriority(PROCESS_ID, INVALID_PRIORITY));
		System.out.println();
		assertFalse(handler.setProcessPriority(INVALID_ID, PRIORITY));
		System.out.println();
		assertFalse(handler.setProcessPriority(INVALID_ID, INVALID_PRIORITY));
		System.out.println();
		System.out.println("--END OF TEST--\n");
	}
	
    
    
	@Test
	public void setProcessPriorityByName()
	{
		
		System.out.println("setProcessPriority by process name test");
		System.out.println("---------------------------------------");
		
		norm.mapValues();
		//
		// true
		//
		boolean result = false;
		assertTrue(result = handler.setProcessPriority(PROCESS_NAME, PRIORITY));
		System.out.println(result);
		System.out.println();
		//
		// false
		//
		assertFalse(handler.setProcessPriority(PROCESS_NAME, INVALID_PRIORITY));
		System.out.println();
		assertFalse(handler.setProcessPriority(INVALID_NAME, PRIORITY));
		System.out.println();
		assertFalse(handler.setProcessPriority(INVALID_NAME, INVALID_PRIORITY));
		System.out.println();
		System.out.println("--END OF TEST--\n");
	}
	
	
	
	@Test
	public void setProcessPriorityByCollection() throws IOException
	{
		Process proc = Runtime.getRuntime().exec("calc.exe");
		int procId = WindowsServiceClass.getProcessIdByProcessName("calc.exe");
		PROCESS_ID = WindowsServiceClass.getProcessIdByProcessName(PROCESS_NAME);
		norm.mapValues();
		
		ProcessData proc1 = new ProcessData(PROCESS_ID, PRIORITY, null);
		ThreadData thread1 = new ThreadData(JAVA_THREAD_NAME, true, PRIORITY, null);
		proc1.addThread(thread1);
		ProcessData proc2 = new ProcessData(procId, PRIORITY, null);
		ThreadData thread2 = new ThreadData(INVALID_NAME, false, PRIORITY, null);	
		proc2.addThread(thread2);
		
		List<ProcessData> procList = new ArrayList<>();
		procList.add(proc1);
		procList.add(proc2);
		List<ProcessResult> results = new ArrayList<>();
		results = handler.setProcessPriority(procList);	
		for(ProcessResult result : results)
		{
			assertTrue(result.getResult());
			List<Result> threadRes = result.getThreads();
			for(Result tr : threadRes)
			{
				if(tr.getName().equals(JAVA_THREAD_NAME))
				{
					assertTrue(tr.getResult());
				}
				else if(tr.getName().equals(INVALID_NAME))
				{
					assertFalse(tr.getResult());
				}
			}
		}
		proc.destroy();
	}
	
	
	
	@Test
	public void setNativeThreadPriorityById()
	{
		
		System.out.println("setNativeThreadPriority by thread ID test");
		System.out.println("------------------------------------------");
		
		WindowsServiceClass.initServiceClass();
		JavaThreadHandlerWindows javaHandler = new JavaThreadHandlerWindows(WindowsServiceClass.getKernel32Instance());
		NATIVE_THREAD_ID = javaHandler.getNativeThreadId("main");
		norm.mapValues();
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
		assertFalse(handler.setNativeThreadPriority(NATIVE_THREAD_ID, INVALID_PRIORITY));
		System.out.println();
		assertFalse(handler.setNativeThreadPriority(INVALID_ID, PRIORITY));
		System.out.println();
		assertFalse(handler.setNativeThreadPriority(INVALID_ID, INVALID_PRIORITY));
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
		assertFalse(handler.setNativeThreadPriority(JAVA_THREAD_NAME, PRIORITY));
		assertFalse(handler.setNativeThreadPriority(JAVA_THREAD_NAME, INVALID_PRIORITY));
		assertFalse(handler.setNativeThreadPriority(INVALID_NAME, INVALID_PRIORITY));
		System.out.println("--END OF TEST--\n");
	}    
	
	
	
	@Test
	public void setNativeThreadPriorityByProcessNameAndThreadName()
	{
		System.out.println("setNativeThreadAffinity by process name and thread name test");
		System.out.println("------------------------------------------------------------");
		System.out.println("Native threads don't have names in Windows operating system.");
		System.out.println();
		assertFalse(handler.setNativeThreadPriority(PROCESS_NAME, JAVA_THREAD_NAME, PRIORITY));
		assertFalse(handler.setNativeThreadPriority(PROCESS_NAME, JAVA_THREAD_NAME, INVALID_PRIORITY));
		assertFalse(handler.setNativeThreadPriority(PROCESS_NAME, INVALID_NAME, INVALID_PRIORITY));
		assertFalse(handler.setNativeThreadPriority(INVALID_NAME, INVALID_NAME, INVALID_PRIORITY));
		System.out.println("--END OF TEST--\n");
	}
	
	
	
	@Test
    public void setNativeThreadPriorityByProcessIdAndThreadName()
    {
		PROCESS_ID = WindowsServiceClass.getProcessIdByProcessName(PROCESS_NAME);
    	System.out.println("setNativeThreadAffinity by process name and thread name test");
		System.out.println("------------------------------------------------------------");
		System.out.println("Native threads don't have names in Windows operating system.");
		System.out.println();
		assertFalse(handler.setNativeThreadPriority(PROCESS_ID, JAVA_THREAD_NAME, PRIORITY));
		assertFalse(handler.setNativeThreadPriority(PROCESS_ID, JAVA_THREAD_NAME, INVALID_PRIORITY));
		assertFalse(handler.setNativeThreadPriority(PROCESS_ID, INVALID_NAME, INVALID_PRIORITY));
		assertFalse(handler.setNativeThreadPriority(INVALID_ID, INVALID_NAME, INVALID_PRIORITY));
		System.out.println("--END OF TEST--\n");
    }
	
	
    
	@Test
	public void setNativeThreadPriorityByCollection()
	{
		norm.mapValues();
		ThreadData thread1 = new ThreadData(JAVA_THREAD_NAME, true, PRIORITY, null);
		ThreadData thread2 = new ThreadData(INVALID_NAME, true, PRIORITY, null);	
		List<ThreadData> thrdList = new ArrayList<>();
		thrdList.add(thread1);
		thrdList.add(thread2);
		List<Result> results = new ArrayList<>();
		results = handler.setThreadPriority(thrdList);
		for(Result result : results)
		{
			if(result.getName().equals(JAVA_THREAD_NAME))
			{
				assertTrue(result.getResult());
			}
			else if(result.getName().equals(INVALID_NAME))
			{
				assertFalse(result.getResult());
			}
		}
	}
	

    
	@Test
	public void setJavaThreadPriorityById()
	{
		
		System.out.println("setJavaThreadPriority by thread ID test");
		System.out.println("---------------------------------------");
		
		JAVA_THREAD_ID = (int) Thread.currentThread().getId();
		norm.mapValues();
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
		assertFalse(handler.setJavaThreadPriority(JAVA_THREAD_ID, INVALID_PRIORITY));
		System.out.println();
		assertFalse(handler.setJavaThreadPriority(JAVA_THREAD_ID, INVALID_PRIORITY));
		System.out.println();
		assertFalse(handler.setJavaThreadPriority(INVALID_ID, PRIORITY));
		System.out.println();
		assertFalse(handler.setJavaThreadPriority(INVALID_ID, INVALID_PRIORITY));
		System.out.println();
		assertFalse(handler.setJavaThreadPriority(INVALID_ID, INVALID_PRIORITY));
		System.out.println();
		System.out.println("--END OF TEST--\n");
	}
	
	
	
	@Test
	public void setJavaThreadPriorityByName()
	{
		
		System.out.println("setJavaThreadPriority by thread name test");
		System.out.println("-----------------------------------------");
		
		norm.mapValues();
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
		assertFalse(handler.setJavaThreadPriority(JAVA_THREAD_NAME, INVALID_PRIORITY));
		System.out.println();
		assertFalse(handler.setJavaThreadPriority(INVALID_ID, PRIORITY));
		System.out.println();
		assertFalse(handler.setJavaThreadPriority(INVALID_ID, INVALID_PRIORITY));
		System.out.println();
		System.out.println("--END OF TEST--\n");
	}
	
	
	
	@Test
	public void setJavaThreadPriorityByProcessNameAndThreadName()
	{
		
		System.out.println("setJavaThreadPriority by process name and thread name test");
		System.out.println("----------------------------------------------------------");
		
		norm.mapValues();
		//
		// true
		//
		boolean result = false;
		assertTrue(result = handler.setJavaThreadPriority(PROCESS_NAME, JAVA_THREAD_NAME, PRIORITY));
		System.out.println(result);
		System.out.println();
		//
		// false
		//
		assertFalse(handler.setJavaThreadPriority(PROCESS_NAME, JAVA_THREAD_NAME, INVALID_PRIORITY));
		System.out.println();
		assertFalse(handler.setJavaThreadPriority(INVALID_NAME, JAVA_THREAD_NAME, PRIORITY));
		System.out.println();
		assertFalse(handler.setJavaThreadPriority(PROCESS_NAME, INVALID_NAME, PRIORITY));
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
		
		PROCESS_ID = WindowsServiceClass.getProcessIdByProcessName(PROCESS_NAME);
		norm.mapValues();
		//
		// true
		//
		boolean result = false;
		assertTrue(result = handler.setJavaThreadPriority(PROCESS_ID, JAVA_THREAD_NAME, PRIORITY));
		System.out.println(result);
		System.out.println();
		//
		// false
		//
		assertFalse(handler.setJavaThreadPriority(PROCESS_ID, JAVA_THREAD_NAME, INVALID_PRIORITY));
		System.out.println();
		assertFalse(handler.setJavaThreadPriority(INVALID_ID, JAVA_THREAD_NAME, PRIORITY));
		System.out.println();
		assertFalse(handler.setJavaThreadPriority(PROCESS_ID, INVALID_NAME, PRIORITY));
		System.out.println();
		assertFalse(handler.setJavaThreadPriority(INVALID_ID, INVALID_NAME, PRIORITY));
		System.out.println();
		System.out.println("--END OF TEST--\n");
	}
	
	
	
	
	
	
	
	
}







