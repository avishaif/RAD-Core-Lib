package handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javathreadshandlers.JavaThreadHandlerWindows;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cache.Cache;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.win32.W32APIOptions;

import core.ProcessData;
import core.Results;
import core.ThreadData;
import os_api.windows.Kernel32;



public class WindowsHandler extends Handler
{
	private static final int INVALID_PRIORITY_VALUE = -999;
	private static final int INVALID_POLICY_VALUE = -999;
	private static final int INVALID_ID = -1;
	private static final String INVALID_NAME = null;
	private static final boolean CACHE_CHECKED = true;
	private static final int[] INVALID_ARRAY = null;
	
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
		javaHandler = new JavaThreadHandlerWindows(kernel32);
		this.cache = new ArrayList<>();
	}
	


	@Override
	public int getProcessorCount() 
	{
		return numberOfCpus;
	}
	
	@Override
	public void clearCache()
	{
		if(this.cache != null)
			this.cache.clear();
	}
	
/*	
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
*/	
	
	private Cache checkCache(int id, String name) 
	{
		for (Cache cache : this.cache) 
		{
			if (cache.getId() == id || cache.getName().equals(name)) 
			{
				return cache;
			}
		}
		return null;
	}

	
	
	
/*--------------------------- SET AFFINITY -----------------------------------------------*/	
	
	/**
	 * 
	 * @param pid
	 * @param affinity
	 * @param checked
	 * @return
	 */
	private boolean setProcessAffinity(int pid, int[] affinity, boolean checked)
	{
		boolean result = false;
		int affinityMask = WindowsServiceClass.getAffinityMask(affinity);
		
		if((affinityMask > numberOfCpus+1) || (affinityMask <= 0))
		{
			if(log.isErrorEnabled())
			{
				log.error("Set process affinity failed to set affinity to process with ID " + pid + ". Trying to set process affinity on invalid CPU.");
			}
			return false;
		}		
		HANDLE processHandle = WindowsServiceClass.getProcessHandle(pid);
		if(processHandle == null)
		{
			if(log.isErrorEnabled())
			{
				log.error("Set process affinity failed to set affinity to process with ID " + pid + ". The process is not running.");
			}
			return false;
		}
		result = kernel32.SetProcessAffinityMask(processHandle, affinityMask);
		if(!result && log.isErrorEnabled())
		{
			log.error("Set process affinity failed to set affinity to process with ID " + pid + ". " + WindowsServiceClass.getLastErrorMessage());
		}
		return result;
	}

	@Override
	public boolean setProcessAffinity(int pid, int[] affinity)
	{	
		if(WindowsServiceClass.checkParams(affinity, pid, INVALID_NAME))
		{
			boolean result = false;
			Cache cache = checkCache(pid, INVALID_NAME);
			if(cache != null)
			{
				if(cache.getAffinity() != null)
					Arrays.sort(cache.getAffinity());
				Arrays.sort(affinity);
				if(Arrays.equals(cache.getAffinity(), affinity))
				{
					return true;
				}
				else
				{
					result = setProcessAffinity(pid, affinity, CACHE_CHECKED);
					if(result)
					{
						cache.setAffinity(affinity);
					}
					return result;
				}
			}
			else if(cache == null)
			{
				result = setProcessAffinity(pid, affinity, CACHE_CHECKED);
				if(result) // affinity set, add to cache
				{
					String pname = WindowsServiceClass.getProcessNameByProcessId(pid);
					this.cache.add(new Cache(pid, pname, affinity, INVALID_PRIORITY_VALUE, INVALID_POLICY_VALUE));
				}
				return result;
			}
		}
		return false;		
	}
	// end of setProcessAffinity
	

	@Override
	public boolean setProcessAffinity(String pName, int[] affinity) 
	{
		int processId = -1;
		boolean result = false;
		
		if(WindowsServiceClass.checkParams(affinity, INVALID_ID, pName))
		{
			Cache cache = checkCache(INVALID_ID, pName);
			processId = WindowsServiceClass.getProcessIdByProcessName(pName);
			if(cache != null)
			{
				if(cache.getAffinity() != null)
					Arrays.sort(cache.getAffinity());
				Arrays.sort(affinity);
				if(Arrays.equals(cache.getAffinity(), affinity))
				{
					return true;
				}
				else // affinities are different
				{
					result = setProcessAffinity(processId, affinity, CACHE_CHECKED);
					if(result)
					{
						cache.setAffinity(affinity);
					}
				}
			}
			else if(null == cache)
			{
				result = setProcessAffinity(processId, affinity, CACHE_CHECKED);
				if(result) // affinity set, add to cache
				{
					this.cache.add(new Cache(processId, pName, affinity, INVALID_PRIORITY_VALUE, INVALID_POLICY_VALUE));
				}
			}
			if(!result && log.isErrorEnabled())
			{
				log.error("Set process affinity failed to set affinity to process " + pName + " with ID " + processId);
			}
			return result;
		}
		return false;
	}
	// end of setProcessAffinity

	
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


	
	private boolean setNativeThreadAffinity(int tid, int[] affinity, boolean checked)
	{
		boolean result = false;
		int affinityMask = 0;
			
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
	

	@Override
	public  boolean setNativeThreadAffinity(int tid, int[] affinity) 
	{
		if(WindowsServiceClass.checkParams(affinity, tid, INVALID_NAME))
		{
			boolean result = false;
			Cache tCache = checkCache(tid, INVALID_NAME);
			if(tCache != null)
			{
				if(tCache.getAffinity() != null)
				{
					Arrays.sort(tCache.getAffinity());
				}
				Arrays.sort(affinity);
				if(Arrays.equals(tCache.getAffinity(), affinity))
				{
					return true;
				}
				else
				{
					result = setNativeThreadAffinity(tid, affinity, CACHE_CHECKED);
					if(result)
					{
						tCache.setAffinity(affinity);
					}
					return result;
				}
			}
			else if(null == tCache)
			{
				result = setNativeThreadAffinity(tid, affinity, CACHE_CHECKED);
				if(result)
				{
					this.cache.add(new Cache(tid, INVALID_NAME, affinity, INVALID_PRIORITY_VALUE, INVALID_POLICY_VALUE));
				}
				return result;
			}
		}
		return false;
	}
	// end of setNativeThreadAffinity
	

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
	
	
	/*
	@Override
	public boolean setNativeThreadAffinity(int pid, int tid, int[] affinity)
	{
		return false;
	}
	*/


	@Override
	public boolean setNativeThreadAffinity(String pName, String tName, int[] affinity)
	{
		if(log.isErrorEnabled())
		{
			log.error("Failed to set affinity to thread " + tName + " under process " + pName + ". Windows was unable to determine thread name.");
		}
		return false;
	}
	

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
	

	@Override
	public  boolean setJavaThreadAffinity(int tid, int[] affinity)
	{
		if(WindowsServiceClass.checkParams(affinity, tid, INVALID_NAME))
		{
			boolean result = false;
			String tName = javaHandler.getThreadName(tid);
			Cache cache = checkCache(INVALID_ID, tName);
			if(cache != null)
			{
				if(cache.getAffinity() != null)
				{
					Arrays.sort(cache.getAffinity());
				}
				Arrays.sort(affinity);
				if(Arrays.equals(cache.getAffinity(), affinity))
				{
					return true;
				}
				else
				{
					result = setNativeThreadAffinity(cache.getId(), affinity, CACHE_CHECKED);
					if(result)
					{
						cache.setAffinity(affinity);
					}
					return result;
				}
			}
			else if(null == cache)
			{
				int nid = javaHandler.getNativeThreadId(tName);
				if(nid == -1) // thread not found.
				{
					if(log.isErrorEnabled())
					{
						log.error("Failed to set affinity to java thread " + tid);
					}
					return false;
				}
				result = setNativeThreadAffinity(nid, affinity, CACHE_CHECKED);
				if(result)
				{
					this.cache.add(new Cache(nid, tName, affinity, INVALID_PRIORITY_VALUE, INVALID_POLICY_VALUE));
				}
				return result;
			}		
		}
		return false;
	}
	// end of setThreadAffinity
	
	
	@Override
	public  boolean setJavaThreadAffinity(String tName, int[] affinity) 
	{
		if(WindowsServiceClass.checkParams(affinity, INVALID_ID, tName))
		{
			int nid = -1;
			boolean result = false;
			Cache cache = checkCache(INVALID_ID, tName);
			if(cache != null)
			{
				if(cache.getAffinity() != null)
				{
					Arrays.sort(cache.getAffinity());
				}
				Arrays.sort(affinity);
				if(Arrays.equals(cache.getAffinity(), affinity))
				{
					return true;
				}
				else
				{
					result = setNativeThreadAffinity(cache.getId(), affinity, CACHE_CHECKED);
					if(result)
					{
						cache.setAffinity(affinity);
					}
					nid = cache.getId();
				}
			}
			else if(null == cache)
			{
				nid = javaHandler.getNativeThreadId(tName);		
				if(nid == -1) // thread not found.
				{
					if(log.isErrorEnabled())
					{
						log.error("Failed to set affinity to java thread " + tName);
					}
					return false;
				}
				result = setNativeThreadAffinity(nid, affinity, CACHE_CHECKED);
				if(result)
				{
					// get java thread id by tName and add it to cache
					this.cache.add(new Cache(nid, tName, affinity, INVALID_PRIORITY_VALUE, INVALID_POLICY_VALUE));
				}
			}
			if(!result && log.isErrorEnabled())
			{
				log.error("Set java thread affinity failed to set affinity to thread " + tName + " with native ID " + nid);
			}
			return result;
		}
		return false;
	}
	// end of setJavaThreadAffinity
	
	
	/*
	@Override
	public boolean setJavaThreadAffinity(int pid, int tid, int[] affinity)
	{
		List<Integer> jvm = new ArrayList<Integer>();
		jvm.add(pid);
		int id = javaHandler.getNativeThreadId(tid);
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
	*/


	@Override	
	public boolean setJavaThreadAffinity(String pName, String tName, int[] affinity)
	{
		if(WindowsServiceClass.checkParams(affinity, INVALID_ID, tName) && pName != null && pName != "")
		{
			int nid = -1;
			boolean result = false;
			Cache cache = checkCache(INVALID_ID, tName);
			if(cache != null)
			{
				if(cache.getAffinity() != null)
				{
					Arrays.sort(cache.getAffinity());
				}
				Arrays.sort(affinity);
				if(Arrays.equals(cache.getAffinity(), affinity))
				{
					return true;
				}
				else
				{
					result = setNativeThreadAffinity(cache.getId(), affinity, CACHE_CHECKED);
					if(result)
					{
						cache.setAffinity(affinity);
					}
					nid = cache.getId();
				}
			}
			else if(null == cache)
			{
				int pid = WindowsServiceClass.getProcessIdByProcessName(pName);
				nid = javaHandler.getNativeThreadId(tName, pid);
				if(nid == -1) // thread not found.
				{
					if(log.isErrorEnabled())
					{
						log.error("Failed to set affinity to java thread " + tName);
					}
					return false;
				}
				result = setNativeThreadAffinity(nid, affinity, CACHE_CHECKED);
				if(result)
				{
					this.cache.add(new Cache(nid, tName, affinity, INVALID_PRIORITY_VALUE, INVALID_POLICY_VALUE));
				}
			}
			if(!result && log.isErrorEnabled())
			{
				log.error("Set java thread affinity failed to set affinity to thread " + tName + " with native ID " + nid);
			}
			return result;
		}
		return false;		
	}
	
	
	
/*--------------------------- SET PRIORITY -----------------------------------------------*/	
	
	private boolean setProcessPriority(int pid, int policy, int priority, boolean checked)
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
	


	@Override
	public boolean setProcessPriority(int pid, int policy, int priority)
	{
		if(pid > 0)
		{
			boolean result = false;
			Cache cache = checkCache(pid, INVALID_NAME);
			if(cache != null)
			{
				if(cache.getPriority() == priority)
				{
					return true;
				}
				else
				{
					result = setProcessPriority(pid, policy, priority, CACHE_CHECKED);
					if(result) 
					{
						cache.setPriority(priority);
					}
					return result;
				}
			}
			else if(null == cache)
			{
				result = setProcessPriority(pid, policy, priority, CACHE_CHECKED);
				if(result)
				{
					String pname = WindowsServiceClass.getProcessNameByProcessId(pid);
					this.cache.add(new Cache(pid, pname, INVALID_ARRAY, priority, policy));
				}
				return result;
			}
		}
		return false;
	}
	// end of setProcessPriority
	

	@Override
	public  boolean setProcessPriority(String pName, int policy, int priority) 
	{
		if(pName != null && pName != "")
		{
			boolean result = false;
			int processId = -1;
			Cache cache = checkCache(INVALID_ID, pName);
			if(cache != null)
			{
				if(cache.getPriority() == priority)
				{
					return true;
				}
				else
				{
					processId = cache.getId();
					result = setProcessPriority(processId, policy, priority, CACHE_CHECKED);
					if(result)
					{
						cache.setPriority(priority);
					}
				}
			}
			else if(null == cache)
			{
				processId = WindowsServiceClass.getProcessIdByProcessName(pName);
				result = setProcessPriority(processId, policy, priority, CACHE_CHECKED);
				if(result)
				{
					this.cache.add(new Cache(processId, pName, INVALID_ARRAY, priority, policy));
				}
			}
			if(!result && log.isErrorEnabled())
			{
				log.error("Set process priority failed to set priority to process " + pName + " with ID " + processId);
			}
			return result;
		}
		return false;
	}
	// end of setProcessPriority
	
	
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
	
	
	private boolean setNativeThreadPriority(int tid, int policy, int priority, boolean checked)
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
	

	@Override
	public  boolean setNativeThreadPriority(int nid, int policy, int priority) 
	{
		if(nid > 0)
		{
			boolean result = false;
			Cache tCache = checkCache(nid, INVALID_NAME);
			if(tCache != null)
			{
				if(tCache.getPriority() == priority)
				{
					return true;
				}
				else
				{
					result = setNativeThreadPriority(nid, policy, priority, CACHE_CHECKED);
					if(result)
					{
						tCache.setPriority(priority);
					}
					return result;
				}
			}
			else if(null == tCache)
			{
				result = setNativeThreadPriority(nid, policy, priority, CACHE_CHECKED);
				if(result)
				{
					this.cache.add(new Cache(nid, INVALID_NAME, INVALID_ARRAY, priority, policy));
				}
				return result;
			}
		}
		return false;
	}
	// end of setNativeThreadPriority
	

	@Override
	public  boolean setNativeThreadPriority(String tName, int policy, int priority) 
	{
		if(log.isErrorEnabled())
		{
			log.error("Failed to set priority to thread " + tName + ". Windows was unable to determine thread name.");
		}
		return false;
	}
	// end of setNativeThreadPriority
	
	
	/*
	@Override
	public boolean setNativeThreadPriority(int pid, int tid, int policy, int priority)
	{
		// TODO Auto-generated method stub
		return false;
	}
*/
	

	@Override
	public boolean setNativeThreadPriority(String pName, String tName, int policy, int priority)
	{
		if(log.isErrorEnabled())
		{
			log.error("Failed to set priority to thread " + tName + ". Windows was unable to determine thread name.");
		}
		return false;
	}
	
	
	@Override
	public boolean setNativeThreadPriority(int pid, String tName, int policy, int priority)
	{
		if(log.isErrorEnabled())
		{
			log.error("Failed to set affinity to thread " + tName + ". Windows was unable to determine thread name.");
		}
		return false;
	}
	
	
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
	// end of setNativeThreadPriority
	

	@Override
	public  boolean setJavaThreadPriority(int tid, int policy, int priority) 
	{
		if(tid > 0)
		{
			boolean result = false;
			String tName = javaHandler.getThreadName(tid);
			Cache cache = checkCache(INVALID_ID, tName);
			if(cache != null)
			{
				if(cache.getPriority() == priority)
				{
					return true;
				}
				else
				{
					result = setNativeThreadPriority(cache.getId(), policy, priority, CACHE_CHECKED);
					if(result)
					{
						cache.setPriority(priority);
					}
					return result;
				}
			}
			else if(null == cache)
			{
				int nid = javaHandler.getNativeThreadId(tName);
				if(nid == -1 ) // thread not found.
				{
					if(log.isErrorEnabled())
					{
						log.error("Failed to set priority to java thread " + tid);
					}
					return false;
				}
				result = setNativeThreadPriority(nid, policy, priority, CACHE_CHECKED);
				if(result)
				{
					this.cache.add(new Cache(nid, tName, INVALID_ARRAY, priority, policy));
				}
				return result;
			}
		}
		return false;
	}
	// end of setJavaThreadPriority
	

	@Override
	public  boolean setJavaThreadPriority(String tName, int policy, int priority) 
	{
		if(tName != null && tName != "")
		{
			int nid = -1;
			boolean result = false;
			Cache cache = checkCache(INVALID_ID, tName);
			if(cache != null)
			{
				if(cache.getPriority() == priority)
				{
					return true;
				}
				else
				{
					result = setNativeThreadPriority(cache.getId(), policy, priority, CACHE_CHECKED);
					if(result)
					{
						cache.setPriority(priority);
					}
					nid = cache.getId();
				}
			}
			else if(null == cache)
			{
				nid = javaHandler.getNativeThreadId(tName);		
				if(nid == -1) // thread not found.
				{
					if(log.isErrorEnabled())
					{
						log.error("Failed to set priority to java thread " + tName);
					}
					return false;		
				}
				result = setNativeThreadPriority(nid, policy, priority, CACHE_CHECKED);
				if(result)
				{
					this.cache.add(new Cache(nid, tName, INVALID_ARRAY, priority, policy));
				}
			}
			if(!result && log.isErrorEnabled())
			{
				log.error("Set java thread priority failed to set priority to thread " + tName + " with native ID " + nid);
			}
			return result;
		}
		return false;
	}
	// end of setJavaThreadPriority
	
	
	/*
	@Override
	public boolean setJavaThreadPriority(int pid, int tid, int policy, int priority)
	{
		List<Integer> jvm = new ArrayList<Integer>();
		jvm.add(pid);
		int id = javaHandler.getNativeThreadId(tid);
		if(id == -1) // thread not found.
		{
			if(log.isErrorEnabled())
			{
				log.error("Failed to set priority to java thread " + tid);
			}
			return false;		
		}
		return setNativeThreadPriority(id, policy, priority);
	}
*/

	
	@Override
	public boolean setJavaThreadPriority(String pName, String tName, int policy, int priority)
	{
		if(pName != null && pName != "" && tName != null && tName != "")
		{
			boolean result = false;
			int nid = -1;
			int pid = -1;
			Cache cache = checkCache(INVALID_ID, tName);
			if(cache != null)
			{
				if(cache.getPriority() == priority)
				{
					return true;
				}
				else
				{
					result = setNativeThreadPriority(cache.getId(), policy, priority, CACHE_CHECKED);
					if(result)
					{
						cache.setPriority(priority);
					}
					nid = cache.getId();
				}
			}
			else if(null == cache)
			{
				pid = WindowsServiceClass.getProcessIdByProcessName(pName);
				nid = javaHandler.getNativeThreadId(tName, pid);
				if(nid == -1) // thread not found.
				{
					if(log.isErrorEnabled())
					{
						log.error("Failed to set priority to java thread " + tName);
					}
					return false;		
				}
				result = setNativeThreadPriority(nid, policy, priority, CACHE_CHECKED);
				if(result)
				{
					this.cache.add(new Cache(nid, tName, INVALID_ARRAY, priority, policy));
				}
			}
			if(!result && log.isErrorEnabled())
			{
				log.error("Set java thread priority failed to set priority to thread " + tName + " with native ID " + nid);
			}
			return result;
		}
		return false;
	}

	
	@Override
	public boolean setJavaThreadPriority(int pid, String tName, int policy, int priority)
	{
		if(pid > 0 && tName != null && tName != "")
		{
			boolean result = false;
			int nid = -1;
			Cache cache = checkCache(INVALID_ID, tName);
			if(cache != null)
			{
				if(cache.getPriority() == priority)
				{
					return true;
				}
				else
				{
					result = setNativeThreadPriority(cache.getId(), policy, priority, CACHE_CHECKED);
					if(result)
					{
						cache.setPriority(priority);
					}
					nid = cache.getId();
				}
			}
			else if(null == cache)
			{
				nid = javaHandler.getNativeThreadId(tName, pid);
				if(nid == -1) // thread not found.
				{
					if(log.isErrorEnabled())
					{
						log.error("Failed to set priority to java thread " + tName);
					}
					return false;		
				}
				result = setNativeThreadPriority(nid, policy, priority, CACHE_CHECKED);
				if(result)
				{
					this.cache.add(new Cache(nid, tName, INVALID_ARRAY, priority, policy));
				}
			}
			if(!result && log.isErrorEnabled())
			{
				log.error("Set java thread priority failed to set pririty to thread " + tName + " with native ID " + nid);
			}
			return result;
		}
		return false;
	}
	
}
// end of class









