package handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javathreadshandlers.JavaThreadHandlerWindows;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Tlhelp32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.win32.W32APIOptions;

import core.ProcessData;
import core.Results;
import core.ThreadData;
import os_api.windows.Kernel32;



public class WindowsHandler extends Handler
{
	private JavaThreadHandlerWindows javaHandler;
	private static Kernel32 kernel32;
	private static int numberOfCpus;
	private static Logger log;
	
	public WindowsHandler()
	{
		super();
		log = LogManager.getRootLogger();
		kernel32 = (Kernel32)Native.loadLibrary("kernel32", Kernel32.class, W32APIOptions.UNICODE_OPTIONS);
		WindowsServiceClass.initServiceClass(kernel32);
		numberOfCpus = WindowsServiceClass.getNumberOfCpus();
		javaHandler = new JavaThreadHandlerWindows();
	}
	

	/**
	 * This method returns the number of CPUs installed in the system.
	 * @return integer value representing the number of CPUs.
	 */
	@Override
	public int getProcessorCount() 
	{
		return numberOfCpus;
	}
	
	
	protected static List<Integer> getAllJvmsId()
	{
		List<Integer> jvmIds = new ArrayList<Integer>();
		Tlhelp32.PROCESSENTRY32.ByReference processEntry = new Tlhelp32.PROCESSENTRY32.ByReference();
		WinNT.HANDLE snapshot = kernel32.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPPROCESS, new WinDef.DWORD(0));
		try 
		{
			// GET THE LIST OF RUNNING PROCESSES.
		    while (kernel32.Process32Next(snapshot, processEntry)) 
		    {
		    	if(Native.toString(processEntry.szExeFile).contains("java"))
		    	{
		    		jvmIds.add(processEntry.th32ProcessID.intValue());
		    	}
		    }
		}
		finally 
		{
			kernel32.CloseHandle(snapshot);
		}
		if (jvmIds.size() == 0 && log.isErrorEnabled())
		{
			log.error("No jvm was found");
			return null;
		}
		return jvmIds;
	}
	
	
    
	
/*--------------------------- SET AFFINITY -----------------------------------------------*/	
	
	/**
	 * This method uses JNA to set process affinity by process ID.
	 * In case set process affinity was unable to succeed, an error message will be written to log.
	 * @param pid - process id.
	 * @param affinity - array of CPUs.
	 * @return true if affinity set successfully, false otherwise.
	 */
	@Override
	public boolean setProcessAffinity(int pid, int[] affinity)
	{	
		boolean result = false;
		int affinityMask = 0;
		
		if(pid < 0)
		{
			if(log.isErrorEnabled())
			{
				log.error("Failed to set affinity. Process is not running.");
			}
			return false;
		}
		if(affinity.length > numberOfCpus)
		{
			if(log.isErrorEnabled())
			{
				log.error("Failed to set affinity to process with ID " + pid + ". Too many affinity values.");
			}
			return false;
		}
		affinityMask = WindowsServiceClass.getAffinityMask(affinity);
		if((affinityMask > numberOfCpus+1) || (affinityMask <= 0))
		{
			if(log.isErrorEnabled())
			{
				log.error("Failed to set affinity to process with ID " + pid + ". Trying to set process affinity on invalid CPU.");
			}
			return false;
		}
		
		HANDLE processHandle = WindowsServiceClass.getProcessHandle(pid);
		if(processHandle == null)
		{
			if(log.isErrorEnabled())
			{
				log.error("Failed to set affinity to process with ID " + pid + ". The process is not running.");
			}
			return false;
		}
		result = kernel32.SetProcessAffinityMask(processHandle, affinityMask);
		if(!result && log.isErrorEnabled())
		{
			log.error("Failed to set affinity to process with ID " + pid + ". " + WindowsServiceClass.getLastErrorMessage());
		}
		
		return result;
	}
	// end of setProcessAffinity
	
	
	/**
	 * This method uses JNA to set process affinity by process name.
	 * In case set process affinity was unable to succeed, an error message will be written to log.
	 * @param pName - process name.
	 * @param affinity - array of CPUs.
	 * @return true if affinity set successfully, false otherwise.
	 */
	@Override
	public boolean setProcessAffinity(String pName, int[] affinity) 
	{
		boolean result = false;
		int processId = WindowsServiceClass.getProcessIdByProcessName(pName);
		result = setProcessAffinity(processId, affinity);
		if(!result && log.isErrorEnabled())
		{
			log.error("Failed to set affinity to process " + pName + ". ID " + processId);
		}
		return result;
	}
	// end of setProcessAffinity
	
	
	
	/**
	 * This method set affinity for a list of processes. Affinity will be set for each process. 
	 * If a process has threads, their affinity will be set as well.
	 * In case set process affinity or set thread affinity was unable to succeed, an error message will be written to log.
	 * @param processes - a collection of processes of ProcessData type.
	 * @return a list of results. Each result is a Results class type.
	 */
	@Override
	public List<Results> setProcessAffinity(Collection<ProcessData> processes)
	{
		List<Results> results;
		if(processes == null || processes.isEmpty())
		{
			if(log.isErrorEnabled())
			{
				log.error("Failed to set affinity. Empty Collection");
			}
			return null;
		}
		else
		{
			results = new ArrayList<>();
		}
		 
		for (ProcessData process : processes)
        {
			Results result;
    		if (process.getName().equals("none"))
    			result = new Results(process.getId(), setProcessAffinity(process.getId(), process.getAffinity()));
    		else
    			result  = new Results(process.getName(), setProcessAffinity(process.getName(), process.getAffinity()));
    		
    		// CHECKS IF PROCESS HAS THREAD LIST, IF IT DOES, SET THREADS AFFINITY AS WELL.
    		if(process.getThreads() != null || !process.getThreads().isEmpty())
            {
    			for (ThreadData thread : process.getThreads())
    	        {
    	        	if(thread.getName().equals("none"))
    	        	{
    	        		if(thread.isJavaThread())
    	        			result.addThread(thread.getId(), setJavaThreadAffinity(thread.getId(), thread.getAffinity()));
    	        		else
    	        			result.addThread(thread.getId(), setNativeThreadAffinity(thread.getId(), thread.getAffinity()));
    	        	}
    	        	else
    	        	{
    	        		if(thread.isJavaThread())
    	        			result.addThread(thread.getName(), setJavaThreadAffinity(thread.getName(), thread.getAffinity()));
    	        		else
    	        			result.addThread(thread.getName(), setNativeThreadAffinity(thread.getName(), thread.getAffinity()));
    	        	}
    	        }
            }
    		results.add(result);
        }
		return results;
	}
	// end of setProcessAffinity
	

	/**
	 * This method uses JNA to set native thread affinity by thread ID.
	 * In case set thread affinity was unable to succeed, an error message will be written to log.
	 * @param tid - Native thread ID.
	 * @param affinity - Array of CPUs.
	 * @return true if affinity set successfully, false otherwise.
	 */
	@Override
	public  boolean setNativeThreadAffinity(int tid, int[] affinity) 
	{
		boolean result = false;
		int affinityMask = 0;
		
		if(tid < 0)
		{
			if(log.isErrorEnabled())
			{
				log.error("Failed to set affinity. Thread not found.");
			}
			return false;
		}
		if(affinity.length > numberOfCpus)
		{
			if(log.isErrorEnabled())
			{
				log.error("Failed to set affinity to native thread with ID " + tid + ". Too many affinity values.");
			}
			return false;
		}
		
		affinityMask = WindowsServiceClass.getAffinityMask(affinity);
		if((affinityMask > numberOfCpus+1) || (affinityMask <= 0))
		{
			if(log.isErrorEnabled())
			{
				log.error("Failed to set affinity to thread with ID " + tid + ". Trying to set thread affinity on invalid CPU.");
			}
			return false;
		}
		
		HANDLE threadHandle = WindowsServiceClass.getThreadHandle(tid);
		if(threadHandle == null)
		{
			if(log.isErrorEnabled())
			{
				log.error("Failed to set affinity to thread with ID " + tid + ". Thread not found.");
			}
			return false;
		}
		result = kernel32.SetThreadAffinityMask(threadHandle, affinityMask);
		if(!result && log.isErrorEnabled())
		{	
			log.error("Failed to set affinity to thread with ID " + tid + ". " + WindowsServiceClass.getLastErrorMessage());
		}
		return result;
	}
	// end of setThreadAffinity
	
	
	
	/**
	 * 
	 * 
	 * In case set thread affinity was unable to succeed, an error message will be written to log.
	 * @param tid - Java thread ID.
	 * @param affinity - Array of CPUs.
	 * @return true if affinity set successfully, false otherwise.
	 */
	@Override
	public  boolean setJavaThreadAffinity(int tid, int[] affinity) 
	{
		List<Integer> jvmIds = getAllJvmsId();
		int id = javaHandler.getNativeThreadId(tid, jvmIds);
		if(id == -1) // thread not found.
		{
			if(log.isErrorEnabled())
			{
				log.error("Failed to set affinity to java thread " + tid);
			}
			return false;
		}
		return setNativeThreadAffinity(id, affinity);
	}
	// end of setThreadAffinity
	
	
	/** 
	 * This method will return false because thread names are unknown in Windows.
	 * An error message will be written to log.
	 * @param tName - Thread name.
	 * @param affinity - Array of CPUs.
	 * @return true if affinity set successfully, false otherwise.  
	 */
	@Override
	public  boolean setNativeThreadAffinity(String tName, int[] affinity) 
	{
		if(log.isErrorEnabled())
		{
			log.error("Failed to set affinity to thread " + tName + ". Windows was unable to determine thread name.");
		}
		return false;
	}
	// end of setNativeThreadAffinity 
	
	
	/**
	 * This method will search for the thread in all available JVMs. If the thread exists, it will get it's native ID and set it's affinity with native ID.
	 * @param tName - Thread name.
	 * @param affinity - Array of CPUs.
	 * @return true if affinity set successfully, false otherwise.  
	 */
	@Override
	public  boolean setJavaThreadAffinity(String tName, int[] affinity) 
	{
		List<Integer> jvmIds = getAllJvmsId();
		int tid = javaHandler.getNativeThreadId(tName, jvmIds);		
		if(tid == -1) // thread not found.
		{
			if(log.isErrorEnabled())
			{
				log.error("Failed to set affinity to java thread " + tName);
			}
			return false;
		}
//		System.out.println("thread " + tName + " with native ID " + tid + " found.");
		return setNativeThreadAffinity(tid, affinity);
	}
	// end of setJavaThreadAffinity
	
	
	
	/**
	 * This method set affinity for a list of threads. Affinity will be set for each thread. 
	 * In case set thread affinity was unable to succeed, an error message will be written to log.
	 * @param threads - a collection of threads of ThreadData type.
	 * @return a list of results. Each result is a Results class type. 
	 */
	@Override
	public List<Results> setNativeThreadAffinity(Collection<ThreadData> threads)
	{
		List<Results> results;
		
		if(threads == null || threads.isEmpty())
		{
			if(log.isErrorEnabled())
			{
				log.error("Failed to set affinity. Empty Collection");
			}
			return null;
		}
		else
		{
			results = new ArrayList<>();
		}
		
    	for (ThreadData thread : threads)
        {
    		Results result;
    		
        	if(thread.getName().equals("none"))
        	{
        		if(thread.isJavaThread())
        			result = new Results(thread.getId(), setJavaThreadAffinity(thread.getId(), thread.getAffinity()));
        		else
        			result = new Results(thread.getId(), setNativeThreadAffinity(thread.getId(), thread.getAffinity()));
        	}
        	else
        	{
        		if(thread.isJavaThread())
        			result = new Results(thread.getName(), setJavaThreadAffinity(thread.getName(), thread.getAffinity()));
        		else
        			result = new Results(thread.getName(), setNativeThreadAffinity(thread.getName(), thread.getAffinity()));
        	}
        	results.add(result);
        }
		return results;
	}
	// end of setThreadAffinity
	
	
/*--------------------------- SET PRIORITY -----------------------------------------------*/	
	
	/**
	 * This method uses JNA to set process priority class.
	 * In case set process priority was unable to succeed, an error message will be written to the log. 
	 * @param pid - process id.
	 * @param priority - process priority value.
	 * @return true if priority set successfully, false otherwise.
	 */
	@Override
	public boolean setProcessPriority(int pid, int policy, int priority)
	{
		boolean result = false;
		HANDLE processHandle = WindowsServiceClass.getProcessHandle(pid);
		if(processHandle == null)
		{
			if(log.isErrorEnabled())
			{
				log.error("Failed to set priority to process with ID " + pid + ". The process is not running.");
			}
			return false;
		}
		result = kernel32.SetPriorityClass(processHandle, priority);
		if(!result && log.isErrorEnabled())
		{
			log.error("Failed to set priority to process with ID " + pid + ". " + WindowsServiceClass.getLastErrorMessage());
		}
		return result;
	}
	// end of setProcessPriority
	

	/**
	 * This method set process priority class by process name.
	 * In case set process priority was unable to succeed, an error message will be written to the log.
	 * @param pName - process name.
	 * @param priority - process priority value.
	 * @return true if priority set successfully, false otherwise.
	 */
	@Override
	public  boolean setProcessPriority(String pName, int policy, int priority) 
	{
		boolean result = false;
		int processId = WindowsServiceClass.getProcessIdByProcessName(pName);
		result = setProcessPriority(processId, policy, priority);
		if(!result && log.isErrorEnabled())
		{
			log.error("Failed to set priority to process " + pName + ".");
		}
		
		return result;
	}
	// end of setProcessPriority
	
	
	/**
	 * This method set priority class for a list of processes. priority class will be set for each process. 
	 * If a process has threads, their priority level will be set as well.
	 * In case set process priority or set thread priority was unable to succeed, an error message will be written to the log.
	 * @param processes - a collection of processes of ProcessData type.
	 * @return a list of results. Each result is a Results class type.
	 */
	@Override
	public List<Results> setProcessPriority(Collection<ProcessData> processes)
	{
		List<Results> results;
				
		if(processes == null || processes.isEmpty())
		{
			if(log.isErrorEnabled())
			{
				log.error("Failed to set priority. Empty Collection");
			}
			return null;
		}
		else
		{
			results = new ArrayList<>();
		}
		
		for (ProcessData process : processes) 
        {	
			Results result;
			
    		if (process.getName().equals("none"))
                result = new Results(process.getId(), setProcessPriority(process.getId(), process.getPolicy(), process.getPriority()));
            else
                result = new Results(process.getName(), setProcessPriority(process.getName(), process.getPolicy(), process.getPriority()));
    		
    		// CHECKS IF PROCESS HAS THREAD LIST, IF IT DOES, SET THREADS PRIORITY AS WELL.
    		if(process.getThreads() != null || !process.getThreads().isEmpty())
            {
    			for(ThreadData thread : process.getThreads())
    	        {
    	        	if(thread.getName().equals("none"))
    	        	{
    	        		if(thread.isJavaThread())
    	        			result.addThread(thread.getId(), setJavaThreadPriority(thread.getId(), thread.getPolicy(), thread.getPriority()));
    	        		else
    	        			result.addThread(thread.getId(), setNativeThreadPriority(thread.getId(), thread.getPolicy(), thread.getPriority()));
    	        	}
    	        	else
    	        	{
    	        		if(thread.isJavaThread())
    	        			result.addThread(thread.getName(), setJavaThreadPriority(thread.getName(), thread.getPolicy(), thread.getPriority()));
    	        		else
    	        			result.addThread(thread.getName(), setNativeThreadPriority(thread.getName(), thread.getPolicy(), thread.getPriority()));
    	        	}
    	        }
    			
            }
    		results.add(result);
        }
		return results;
	}
	//end of setProcessPriority
	
	
	
	/**
	 * This method uses JNA to set native thread priority level by thread ID.
	 * In case set thread priority was unable to succeed, an error message will be written to the log.
	 * @param tid - Native thread ID.
	 * @param priority - Thread priority value.
	 * @return true if priority set successfully, false otherwise.
	 */
	@Override
	public  boolean setNativeThreadPriority(int tid, int policy, int priority) 
	{
		boolean result = false;
		HANDLE threadHandle = WindowsServiceClass.getThreadHandle(tid);
		if(threadHandle == null)
		{
			if(log.isErrorEnabled())
			{
				log.error("Failed to set priority to thread with ID " + tid + ". The thread is not found.");
			}
			return false;
		}
		result = kernel32.SetThreadPriority(threadHandle, priority);
		if(!result && log.isErrorEnabled())
		{
			log.error("Failed to set priority to thread with ID " + tid + ". " + WindowsServiceClass.getLastErrorMessage());
		}
		return result;
	}
	// end of setNativeThreadPriority
	
	
	
	/**
	 * This method will search for the thread in all available JVMs. If the thread exists, it will get it's native ID and set it's priority with native ID.
	 * @param tid - Java thread ID.
	 * @param priority - Thread priority value.
	 * @return true if priority set successfully, false otherwise.
	 */
	@Override
	public  boolean setJavaThreadPriority(int tid, int policy, int priority) 
	{
		List<Integer> jvmIds = getAllJvmsId();
		int id = javaHandler.getNativeThreadId(tid, jvmIds);
		if(id == -1 ) // thread not found.
		{
			if(log.isErrorEnabled())
			{
				log.error("Failed to set priority to java thread " + tid);
			}
			return false;
		}
		return setNativeThreadPriority(tid, policy, priority);
	}
	// end of setJavaThreadPriority
	
	
	/** 
	 * This method will return false because thread names are unknown in Windows.
	 * An error message will be written to log.
	 * @param tName - Thread name.
	 * @param priority - thread priority value.
	 * @return true if affinity set successfully, false otherwise.  
	 */
	@Override
	public  boolean setNativeThreadPriority(String tName, int policy, int priority) 
	{
		if(log.isErrorEnabled())
		{
			log.error("Failed to set priority to thread " + tName + ". Windows was unable to determine thread name.");
		}
		return false;
	}
	// end of setNativeThreadAffinity 
	
	
	/**
	 * This method will search for the thread in all available JVMs. If the thread exists, it will get it's native ID and set it's priority with native ID.
	 * @param tName - thread name.
	 * @param priority - thread priority value.
	 * @return true if priority set successfully, false otherwise.
	 */
	@Override
	public  boolean setJavaThreadPriority(String tName, int policy, int priority) 
	{	
		List<Integer> jvmIds = getAllJvmsId();
		int tid = javaHandler.getNativeThreadId(tName, jvmIds);		
		if(tid == -1) // thread not found.
		{
			if(log.isErrorEnabled())
			{
				log.error("Failed to set priority to java thread " + tName);
			}
			return false;		
		}
		return setNativeThreadPriority(tid, policy, priority);
	}
	// end of setJavaThreadPriority


	/**
	 * This method set priority level for a list of threads. Priority level will be set for each thread. 
	 * In case set thread priority was unable to succeed, an error message will be written to the log.
	 * @param threads - a collection of threads of ThreadData type.
	 * @return a list of results. Each result is a Results class type. 
	 */
	@Override
	public List<Results> setNativeThreadPriority(Collection<ThreadData> threads)
	{
		List<Results> results;
		
		if(threads == null || threads.isEmpty())
		{
			if(log.isErrorEnabled())
			{
				log.error("Failed to set priority. Empty Collection");
			}
			return null;
		}
		else
		{
			results = new ArrayList<>();
		}
		
		for(ThreadData thread : threads)
        {
			Results result;
        	if(thread.getName().equals("none"))
        	{
        		if(thread.isJavaThread())
        			result = new Results(thread.getId(), setJavaThreadPriority(thread.getId(), thread.getPolicy(), thread.getPriority()));
        		else
        			result = new Results(thread.getId(), setNativeThreadPriority(thread.getId(), thread.getPolicy(), thread.getPriority()));
        	}
        	else
        	{
        		if(thread.isJavaThread())
        			result = new Results(thread.getName(), setJavaThreadPriority(thread.getName(), thread.getPolicy(), thread.getPriority()));
        		else
        			result = new Results(thread.getName(), setNativeThreadPriority(thread.getName(), thread.getPolicy(), thread.getPriority()));
        	}
        	results.add(result);
        }
		
		return results;
	}
	// end of setThreadPriority


	@Override
	public boolean setNativeThreadAffinity(String pName, String tName,
			int[] affinity) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean setJavaThreadAffinity(int pid, int tid, int[] affinity) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean setJavaThreadAffinity(String pName, String tName,
			int[] affinity) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean setNativeThreadPriority(String pName, String tName,
			int policy, int priority) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean setNativeThreadPriority(int pid, String tName, int policy,
			int priority) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean setJavaThreadPriority(int pid, int tid, int policy,
			int priority) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean setJavaThreadPriority(String pName, String tName,
			int policy, int priority) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean setJavaThreadPriority(int pid, String tName, int policy,
			int priority) {
		// TODO Auto-generated method stub
		return false;
	}
		
}
// end of class










