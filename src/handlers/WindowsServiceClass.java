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
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.win32.W32APIOptions;

import os_api.windows.Kernel32;
import os_api.windows.THREADENTRY32;
import os_api.windows.WinConstants;


/**
 * Service class for Windows operating system.
 * The class contains several auxiliary methods which necessary for the library. 
 * The core operations of set affinity and priority happened through methods of this class.
 */
public class WindowsServiceClass
{
	private static Kernel32 kernel32;
	private static Logger log;
	private static int numOfCpus;
	

	/**
	 * This method initializes the instances required for the service class.
	 * It sets the logger, kernel32 library instance and number of CPUs.
	 * The method called from the constructor of the WindowsHandler class.
	 */
	public static void initServiceClass()
	{
		log = LogManager.getRootLogger();
		kernel32 = (Kernel32)Native.loadLibrary("kernel32", Kernel32.class, W32APIOptions.UNICODE_OPTIONS);
		numOfCpus = getNumberOfCpus();
	}
	
	/**
	 * Returns the instance of Kernel32 interface.
	 * @return
	 * 		Instance of Kernel32 interface.
	 */
	public static Kernel32 getKernel32Instance()
	{
		return kernel32;
	}
	
	/**
	 * Sets process affinity. The binding with Windows API performed through the instance of Kernel32 interface.
	 * @param pid
	 * 			Process ID.
	 * @param affinity
	 * 			Array of integer values. Each value represents a CPU.
	 * @return
	 * 		True if the operation completed successfully, false otherwise.
	 */
	public static boolean setProcessAffinity(int pid, int[] affinity)
	{
		int affinityMask = getAffinityMask(affinity);
		
		if((affinityMask > numOfCpus+1) || (affinityMask == 0))
		{
			if(log.isErrorEnabled())
			{
				log.error("setProcessAffinity failed to set affinity to process with ID " + pid + ". Trying to set process affinity on invalid CPU.");
			}
			return false;
		}		
		HANDLE processHandle = getProcessHandle(pid);
		if(processHandle == null)
		{
			if(log.isErrorEnabled())
			{
				log.error("setProcessAffinity failed to set affinity to process with ID " + pid + ". The process is not running.");
			}
			return false;
		}
		return kernel32.SetProcessAffinityMask(processHandle, affinityMask);
	}
	
	/**
	 * Sets native thread affinity. The binding with Windows API performed through the instance of Kernel32 interface.
	 * @param nid
	 * 			Native thread ID.
	 * @param affinity
	 * 			Array of integer values. Each value represents a CPU.
	 * @return
	 * 		True if the operation completed successfully, false otherwise.
	 */
	public static boolean setNativeThreadAffinity(int nid, int[] affinity)
	{
		int affinityMask = 0;
		affinityMask = getAffinityMask(affinity);
		if((affinityMask > numOfCpus+1) || (affinityMask == 0))
		{
			if(log.isErrorEnabled())
			{
				log.error("setNativeThreadAffinity failed to set affinity to thread with ID " + nid + ". Trying to set thread affinity on invalid CPU.");
			}
			return false;
		}
		
		HANDLE threadHandle = getThreadHandle(nid);
		if(threadHandle == null)
		{
			if(log.isErrorEnabled())
			{
				log.error("setNativeThreadAffinity failed to set affinity to thread with ID " + nid + ". Thread not found.");
			}
			return false;
		}
		return kernel32.SetThreadAffinityMask(threadHandle, affinityMask);
	}
	
	/**
	 * Sets process priority. The binding with Windows API performed through the instance of Kernel32 interface.
	 * @param pid
	 * 		Process ID.
	 * @param priority
	 * 		Process priority class as defined by Microsoft. See MSDN documentation for further details.
	 * @return
	 * 		True if the operation completed successfully, false otherwise.
	 */
	public static boolean setProcessPriority(int pid, int priority)
	{
		HANDLE processHandle = WindowsServiceClass.getProcessHandle(pid);
		if(processHandle == null)
		{
			if(log.isErrorEnabled())
			{
				log.error("setProcessPriority failed to set priority to process with ID " + pid + ". The process is not running.");
			}
			return false;
		}
		return kernel32.SetPriorityClass(processHandle, priority);
	}
	// end of setProcessPriority
	
	/**
	 * Sets native thread priority. The binding with Windows API performed through the instance of Kernel32 interface.
	 * @param nid
	 * 		Native thread ID.
	 * @param priority
	 * 		Process priority class as defined by Microsoft. See MSDN documentation for further details.
	 * @return
	 * 		True if the operation completed successfully, false otherwise.
	 */
	public static boolean setNativeThreadPriority(int nid, int priority) 
	{
		HANDLE threadHandle = WindowsServiceClass.getThreadHandle(nid);
		if(threadHandle == null)
		{
			if(log.isErrorEnabled())
			{
				log.error("setNativeThreadPriority failed to set priority to thread with ID " + nid + ". Thread not found.");
			}
			return false;
		}
		return kernel32.SetThreadPriority(threadHandle, priority);
	}
	// end of setNativeThreadPriority
	
	
	
	
	
		
	/**
	 * Determines affinity mask value from array of affinity values.
	 * Each value in the array is representing a CPU and the mask is the binary vector representation of the CPUs.
	 * @param affinity
	 * 			Array of integer values. Each value represents a CPU.
	 * @return
	 * 		Integer value representing the affinity mask according to CPU vector.
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
				return 0;
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
	 * Returns the number of CPUs installed in the system.
	 * @return
	 * 	 Integer value representing the number of CPUs. 
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
				log.error("An error accured while trying to determine the number of CPUs in the system.");
			}
		}
		cpus = Integer.parseInt(lpSystemInfo.dwNumberOfProcessors.toString());
		return cpus;
	}
	// end of getNumberOfCpus
	
	

	/**
	 * Gets a pointer to the process.
	 * @param pid
	 * 		process ID.
	 * @return
	 * 		If the pointer to the process obtained it will be returned, otherwise null will be returned.
	 */
	public static HANDLE getProcessHandle(int pid)
	{	
		int errorCode = -1;
		HANDLE handle = null;
		handle = kernel32.OpenProcess(WinConstants.PROCESS_ALL_ACCESS, false, pid);
		errorCode = kernel32.GetLastError();
		if(errorCode > 0)
		{
			if(log.isErrorEnabled())
			{
				log.error("An error accured while trying to get a pointer to process with ID " + pid + ".");
			}
		}
		
		return handle;
	}
	// end of getProcessHandle
	
	
	/**
	 * Gets a pointer to the process.
	 * @param tid
	 * 		Native thread ID.
	 * @return
	 * 		If the pointer to the thread obtained it will be returned, otherwise null will be returned.
	 */
	public static HANDLE getThreadHandle(int tid)
	{	
		int errorCode = -1;
		HANDLE handle = null;
		handle = kernel32.OpenThread(WinConstants.THREAD_ALL_ACCESS, false, tid);
		errorCode = kernel32.GetLastError();
		if(errorCode > 0)
		{
			if(log.isErrorEnabled())
			{
				log.error("An error accured while trying to get a pointer to thread with ID " + tid + ".");
			}
		}
		return handle;
	}
	// end of getThreadHandle
	
	
	/**
	 * This method iterates on all processes running in the system and returns process ID of the specified process name.
	 * @param pName
	 * 			Process name.
	 * @return 
	 * 		If the process is found an integer value of the ID will be returned, if the process is not found a negative value (-1) will be returned.
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
	
	/**
	 * This method iterates on all processes running in the system and returns process name of the specified process ID.
	 * @param pid
	 * 			Process ID.
	 * @return 
	 * 		If the process is found a string process name will be returned, if the process is not found null will be returned.
	 */
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
	 * This method iterates on all threads running in the system and returns process ID of the specified thread.
	 * @param tid
	 * 			Native thread ID.
	 * @return 
	 * 		If the thread is found an integer value of its process ID will be returned, if the thread is not found a negative value (-1) will be returned.
	 */
	public static int getProcessIdByThreadId(int tid)
	{
		THREADENTRY32.ByReference threadEntry = new THREADENTRY32.ByReference();
		WinNT.HANDLE snapThread = kernel32.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPTHREAD, new WinDef.DWORD(0));
		
		try 
		{
		    while (kernel32.Thread32Next(snapThread, threadEntry)) 
		    {
		    	if(threadEntry.th32ThreadID == tid)
		    	{
		    		return threadEntry.th32OwnerProcessID;
		    	}
		    }	
		} 
		finally 
		{
			kernel32.CloseHandle(snapThread);
		}
		return -1;
	}
	// end of getProcessIdByThreadId
	
	/**
	 * Gets the affinity mask of a process. 
	 * @param pid
	 * 			Process ID
	 * @return
	 * 		Integer value representing a binary vector where every bit representing CPU number.
	 */		
	public static int getProcessAffinityMask(int pid)
	{
		int mask = 0;
		final LongByReference cpuset1 = new LongByReference(0);
        final LongByReference cpuset2 = new LongByReference(0);
        try 
        {
        	HANDLE pHan = getProcessHandle(pid);
            mask = kernel32.GetProcessAffinityMask(pHan, cpuset1, cpuset2);
            if(mask > 0)
            {
            	mask = (int) cpuset1.getValue();
            }
            
        }
        catch(IllegalStateException e){}        
        
        return mask;
	}
	//end of getProcessAffinityMask
	
	
	/**
	 * @return
	 * 		 Obtains the human-readable error message text from the last error
	 *       that occurred by invoking {@code Kernel32.GetLastError()}.
	 */
	public static String getLastErrorMessage() 
    {
		return Kernel32Util.formatMessageFromLastErrorCode(kernel32.GetLastError());
	}
	

}
// end of class










