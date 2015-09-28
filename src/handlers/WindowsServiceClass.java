package handlers;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.Tlhelp32;
import com.sun.jna.platform.win32.WinBase.SYSTEM_INFO;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HANDLE;

import os_api.windows.Kernel32;
import os_api.windows.Constants;



public class WindowsServiceClass
{
	private static Kernel32 kernel32;
	private static Logger log;
	private static int numOfCpus;
	

	/**
	 * This method initializes the service class.
	 * It sets the logger, kernel32 library instance and number of CPUs.
	 * The method called from the constructor of the WindowsHandler class.
	 * @param libraryInstance - An instance of Kernel32 library.
	 */
	public static void initServiceClass(Kernel32 kernel32LibraryInstance)
	{
		log = LogManager.getRootLogger();
		kernel32 = kernel32LibraryInstance;
		numOfCpus = getNumberOfCpus();
	}
	
	
	/**
	 * 
	 * @param affinity
	 * @param id
	 * @param name
	 * @return
	 */
	public static boolean checkParams(int[] affinity, int id, String name)
	{
		if(id != -1) // check by ID
		{
			if(id < 0)
			{
				if(log.isErrorEnabled())
				{
					log.error("Check parameters found an invalid ID: '" + id +"'.");
				}
				return false;
			}
		}
		else if(id == -1) // check by name
		{
			if(name == null)
			{
				if (log.isErrorEnabled()) 
				{
					log.error("Check parameters found that name is null");
				}
				return false;
			}
			else if(name.equals(""))
			{
				if (log.isErrorEnabled()) 
				{
					log.error("Check parameters found that name is empty");
				}
				return false;
			}
		}
		
		if(affinity == null)
		{
			if (log.isErrorEnabled()) 
			{
				log.error("Check parameters found null array.");
			}
			return false;
		}
		else if(affinity.length == 0)
		{
			if(log.isErrorEnabled())
			{
				log.error("Check parameters found that no affinity values passed.");
			}
			return false;
		}
		else if(affinity.length > numOfCpus)
		{
			if(log.isErrorEnabled())
			{
				log.error("Check parameters found too many affinity values.");
			}
			return false;
		}
		
		return true;
	}
	
		
	/**
	 * This method determines affinity mask value from array of affinity values.
	 * @param affinity - Integer array of affinity values.
	 * @return Integer value representing affinity mask according to CPU vector.
	 */
	public static int getAffinityMask(int[] affinity)
	{
		int affinityMask = 0;
		int numberOfCpus = numOfCpus;
		int[] cpuVector = new int[numberOfCpus];
		int cpuIndex = 0;
		int i = 0;
		
		Arrays.sort(affinity);
		
		for(i = 0; i < cpuVector.length; i++)
		{
			cpuVector[i] = 0;
		}
		
		i = 0;
		while(i < affinity.length)
		{
			
			if(affinity[i] >= numberOfCpus)
			{
				return -1;
			}
			
			if(affinity[i] == cpuIndex)
			{
				cpuVector[cpuIndex] = 1;
				cpuIndex++;
				i++;
			}
			else
			{
				cpuIndex++;
			}
			
			if(cpuIndex > numberOfCpus)
			{
				break;
			}
		}
		
		cpuIndex = 1;
		for(i = 0; i < cpuVector.length; i++)
		{
			if(cpuVector[i] == 1)
			{
				affinityMask = affinityMask + cpuIndex;
			}
			cpuIndex = cpuIndex * 2;
		}
		return affinityMask;
	}
	//end of getAffinityMask
	

	/**
	 * This method uses JNA to retrieve the number of CPUs installed in the system.
	 * @return Integer value representing the number of CPUs. 
	 */
	public static int getNumberOfCpus()
	{
		int cpus = 0;
		int errorCode = -1;
		
		SYSTEM_INFO lpSystemInfo = new SYSTEM_INFO();
		kernel32.GetSystemInfo(lpSystemInfo);
		errorCode = kernel32.GetLastError();
		if(errorCode > 0)
		{
			if(log.isErrorEnabled())
			{
				log.error("An error accured while trying to determine the number of CPUs in the system. " + getLastErrorMessage());
			}
		}
		cpus = Integer.parseInt(lpSystemInfo.dwNumberOfProcessors.toString());
		return cpus;
	}
	// end of getNumberOfCpus
	
	

	/**
	 * This method uses JNA to retrieve a handle for a process.
	 * @param pId - process id.
	 * @return Process handle.
	 */
	public static HANDLE getProcessHandle(int pid)
	{	
		int errorCode = -1;
		HANDLE handle = null;

		handle = kernel32.OpenProcess(Constants.PROCESS_ALL_ACCESS, false, pid);
		
		errorCode = kernel32.GetLastError();
		if(errorCode > 0)
		{
			if(log.isErrorEnabled())
			{
				log.error("An error accured while trying to get a pointer to process with ID " + pid + ". " + getLastErrorMessage());
			}
		}
		
		return handle;
	}
	// end of getProcessHandle
	
	
	/**
	 * This method uses JNA to retrieve a handle for a thread.
	 * @param tId - thread id.
	 * @return pointer (HANDLE) to the thread.
	 */
	public static HANDLE getThreadHandle(int tid)
	{	
		int errorCode = -1;
		HANDLE handle = null;
		
		handle = kernel32.OpenThread(Constants.THREAD_ALL_ACCESS, false, tid);
		errorCode = kernel32.GetLastError();
		if(errorCode > 0)
		{
			if(log.isErrorEnabled())
			{
				log.error("An error accured while trying to get a pointer to thread with ID " + tid + ". " + getLastErrorMessage());
			}
		}
		return handle;
	}
	// end of getThreadHandle
	
	
	/**
	 * This method iterates on all processes running in the system and returns process ID of the specified process name.
	 * @param pName - process name.
	 * @return process id.
	 */
	public static int getProcessIdByProcessName(String pName)
	{
		
		Tlhelp32.PROCESSENTRY32.ByReference processEntry = new Tlhelp32.PROCESSENTRY32.ByReference();
		WinNT.HANDLE snapshot = kernel32.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPPROCESS, new WinDef.DWORD(0));
		
		try 
		{
			// GET THE LIST OF RUNNING PROCESSES.
			while (kernel32.Process32Next(snapshot, processEntry))
		    {
		    	if(Native.toString(processEntry.szExeFile).contains(pName))
		    	{
		    		return processEntry.th32ProcessID.intValue();
		    	}
		    }
		}
		finally 
		{
			kernel32.CloseHandle(snapshot);
		}
		return -1;   		
	}
	// end of getProcessIdByProcessName
	
	
	public static String getProcessNameByProcessId(int pid)
	{
		Tlhelp32.PROCESSENTRY32.ByReference processEntry = new Tlhelp32.PROCESSENTRY32.ByReference();
		WinNT.HANDLE snapshot = kernel32.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPPROCESS, new WinDef.DWORD(0));
		
		try 
		{
			// GET THE LIST OF RUNNING PROCESSES.
			while (kernel32.Process32Next(snapshot, processEntry))
		    {
		    	if(processEntry.th32ProcessID.intValue() == pid)
		    	{
		    		return Native.toString(processEntry.szExeFile);
		    	}
		    }
		}
		finally 
		{
			kernel32.CloseHandle(snapshot);
		}
		return null;   		
	}
	// end of getProcessNameByProcessId

	
	/**
	 * This method uses JNA. It returns an array of thread IDs.
	 * @param processId - ID of a process.
	 * @param numberOfThreads - number of threads of the process.
	 * @return array of threads IDs.
	 */
	/*
	public static int[] getThreadIds(int processId)
	{
		int[] threadIds = null;
		int i = 0;
		THREADENTRY32.ByReference threadEntry = new THREADENTRY32.ByReference();
		WinNT.HANDLE snapThread = kernel32.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPTHREAD, new WinDef.DWORD(0));
		//Tlhelp32.PROCESSENTRY32.ByReference processEntry = new Tlhelp32.PROCESSENTRY32.ByReference();
		//WinNT.HANDLE snapproc = kernel32.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPPROCESS, new WinDef.DWORD(0));
		
		try 
		{
		    while (kernel32.Thread32Next(snapThread, threadEntry)) 
		    {
		    	System.out.println("ID: " + threadEntry.th32ThreadID);
		    	if(threadEntry.th32OwnerProcessID == processId)
		    	{
		    		
		    		if(threadIds == null && numberOfThreads != 0)
		    		{
		    			threadIds = new int[numberOfThreads];
		    		}
		    		
//		    		threadIds[i] = threadEntry.th32ThreadID;
		    		System.out.println("thread ID: " + threadEntry.th32ThreadID);
		    		
		    	}
		    }	
		} 
		finally 
		{
			kernel32.CloseHandle(snapThread);
		}
		return threadIds;
	}
	*/
	// end of getThreadIds
	
	
	
	/**
	 * @return Obtains the human-readable error message text from the last error
	 *         that occurred by invocating {@code Kernel32.GetLastError()}.
	 */
	public static String getLastErrorMessage() 
    {
		return Kernel32Util.formatMessageFromLastErrorCode(kernel32.GetLastError());
	}
}
// end of class










