package facade;

import java.util.Collection;
import java.util.List;

import normalizers.LinuxNormalizer;
import normalizers.Normalize;
import normalizers.WindowsNormalizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import handlers.Handler;
import handlers.LinuxHandler;
import handlers.WindowsHandler;
import core.ProcessData;
import core.Results;
import core.ThreadData;

public class Facade 
{
	private static Handler handler;
	private static Normalize normalizer;
	private static Logger log;
	private static boolean initialized = false;

	public static boolean init() 
	{
		log = LogManager.getLogger(Facade.class);
		if (System.getProperty("os.name").startsWith("Windows"))
		{
			handler = new WindowsHandler();
			normalizer = new WindowsNormalizer();
		} 
		else if (System.getProperty("os.name").startsWith("Linux"))
		{
			handler = new LinuxHandler();
			normalizer = new LinuxNormalizer();
		} 
		else
		{
			if (log.isErrorEnabled()) 
			{
				log.error("Unsupported operating system.");
			}
			initialized = false;
			return false;
		}
		initialized = true;
		return true;
	}

	
	/**
	 * sets a process affinity (user must have appropriate permissions)
	 * 
	 * @param pid
	 *            process id
	 * @param affinity
	 *            integer array
	 * @return true if successful
	 */
	public static boolean setProcessAffinity(int pid, int[] affinity) 
	{
		if (initialized)
			return handler.setProcessAffinity(pid, affinity);
		else 
		{
			System.out.println("Please run init first.");
			return false;
		}

	}

	
	/**
	 * sets a process affinity (user must have appropriate permissions)
	 * 
	 * @param pName
	 *            process name
	 * @param affinity
	 *            integer array
	 * @return
	 */
	public static boolean setProcessAffinity(String pName, int[] affinity) 
	{
		if (initialized)
			return handler.setProcessAffinity(pName, affinity);
		else 
		{
			System.out.println("Please run init first.");
			return false;
		}
	}

	/**
	 * 
	 * @param processes
	 * @param affinity
	 * @return
	 */
	public static List<Results> setProcessAffinity(Collection<ProcessData> processes) 
	{
		if (initialized)
			return handler.setProcessAffinity(processes);
		else 
		{
			System.out.println("Please run init first.");
			return null;
		}
	}

	/**
	 * 
	 * @param tid
	 * @param affinity
	 * @return
	 */
	public static boolean setThreadAffinity(int tid, int[] affinity, boolean isJavaThread) 
	{
		if (initialized)
			if (isJavaThread)
				return handler.setJavaThreadAffinity(tid, affinity);
			else
				return handler.setNativeThreadAffinity(tid, affinity);
		else 
		{
			System.out.println("Please run init first.");
			return false;
		}
	}

	/**
	 * 
	 * @param tName
	 * @param affinity
	 * @return
	 */
	public static boolean setThreadAffinity(String tName, int[] affinity, boolean isJavaThread) 
	{
		if (initialized)
			if (isJavaThread)
				return handler.setJavaThreadAffinity(tName, affinity);
			else
				return handler.setNativeThreadAffinity(tName, affinity);
		else 
		{
			System.out.println("Please run init first.");
			return false;
		}
	}
	
	
	/**
	 * 
	 * @param pid
	 * @param tid
	 * @param affinity
	 * @param isJavaThread
	 * @return
	 */
	public static boolean setThreadAffinity(int pid, int tid, int[] affinity, boolean isJavaThread)
	{
		if(initialized)
			if(isJavaThread)
				return handler.setJavaThreadAffinity(pid, tid, affinity);
			else
				return handler.setNativeThreadAffinity(tid, affinity);
		else
		{
			System.out.println("Please run init first.");
			return false;
		}
	}
	
	
	/**
	 * 
	 * @param pName
	 * @param tName
	 * @param affinity
	 * @param isJavaThread
	 * @return
	 */
	public static boolean setThreadAffinity(String pName, String tName, int[] affinity, boolean isJavaThread)
	{
		if(initialized)
			if(isJavaThread)
				return handler.setJavaThreadAffinity(pName, tName, affinity);
			else
				return handler.setNativeThreadAffinity(pName, tName, affinity);
		else
		{
			System.out.println("Please run init first.");
			return false;
		}
	}
	

	/**
	 *	This method set native and java threads affinity from a collection of threads.
	 * @param threads
	 * 				A collection of threads of ThreadData type.
	 * @return
	 * 		A list of Results objects. Each Results object contains data about the thread and a result whether affinity set successfully or not.
	 */
	public static List<Results> setThreadAffinity(Collection<ThreadData> threads) 
	{
		if (initialized)
			return handler.setNativeThreadAffinity(threads);
		else 
		{
			System.out.println("Please run init first.");
			return null;
		}
	}

	/**
	 * 
	 * @param pid
	 * @param policy
	 * @param priority
	 * @return
	 */
	public static boolean setProcessPriority(int pid, int priority) 
	{
		if (initialized)
		{
			int[] normalizedValues = normalizer.normalize(priority, true);
			return handler.setProcessPriority(pid, normalizedValues[0], normalizedValues[1]);
		}
		else 
		{
			System.out.println("Please run init first.");
			return false;
		}
	}

	/**
	 * 
	 * @param pName
	 * @param policy
	 * @param priority
	 * @return
	 */
	public static boolean setProcessPriority(String pName, int priority) 
	{
		if (initialized) 
		{
			int[] normalizedValues = normalizer.normalize(priority, true);
			return handler.setProcessPriority(pName, normalizedValues[0], normalizedValues[1]);
			
//			System.out.println("process priority: " + priority + ". process priority class: " + normalizedValues[1]);
//			return true;
		} else 
		{
			System.out.println("Please run init first.");
			return false;
		}
	}

	/**
	 * 
	 * @param processes
	 * @return
	 */
	public static List<Results> setProcessPriority(Collection<ProcessData> processes) 
	{
		if (initialized)
			return handler.setProcessPriority(processes);
		else 
		{
			System.out.println("Please run init first.");
			return null;
		}
	}

	/**
	 * 
	 * @param tid
	 * @param policy
	 * @param priority
	 * @return
	 */
	public static boolean setThreadPriority(int tid, int priority, boolean isJavaThread) 
	{
		if (initialized) 
		{
			if (isJavaThread) 
			{
				int[] normalizedValues = normalizer.normalize(priority, false);
				return handler.setJavaThreadPriority(tid, normalizedValues[0], normalizedValues[1]);
			}
			else 
			{
				int[] normalizedValues = normalizer.normalize(priority, false);
				return handler.setNativeThreadPriority(tid, normalizedValues[0], normalizedValues[1]);
			}
		} 
		else 
		{
			System.out.println("Please run init first.");
			return false;
		}
	}

	/**
	 * 
	 * @param tName
	 * @param policy
	 * @param priority
	 * @return
	 */
	public static boolean setThreadPriority(String tName, int priority, boolean isJavaThread) 
	{
		if (initialized) 
		{
			if (isJavaThread) 
			{
				int[] normalizedValues = normalizer.normalize(priority, false);
				return handler.setJavaThreadPriority(tName, normalizedValues[0], normalizedValues[1]);
			} 
			else 
			{
				int[] normalizedValues = normalizer.normalize(priority, false);
				return handler.setNativeThreadPriority(tName, normalizedValues[0], normalizedValues[1]);
			}
		} 
		else 
		{
			System.out.println("Please run init first.");
			return false;
		}
	}
	
	
	/**
	 * 
	 * @param pid
	 * @param tid
	 * @param priority
	 * @param isJavaThread
	 * @return
	 */
	public static boolean setThreadPriority(int pid, int tid, int priority, boolean isJavaThread)
	{
		if(initialized)
		{
			int[] normalizedValues = normalizer.normalize(priority, false);
			if(isJavaThread)
				return handler.setJavaThreadPriority(pid, tid, normalizedValues[0], normalizedValues[1]);
			else
				return handler.setNativeThreadPriority(tid, normalizedValues[0], normalizedValues[1]);
		}
		else
		{
			System.out.println("Please run init first.");
			return false;
		}
	}
	
	
	/**
	 * 
	 * @param pName
	 * @param tName
	 * @param priority
	 * @param isJavaThread
	 * @return
	 */
	public static boolean setThreadPriority(String pName, String tName, int priority, boolean isJavaThread)
	{
		if(initialized)
		{
			int[] normalizedValues = normalizer.normalize(priority, false);
			if(isJavaThread)
				return handler.setJavaThreadPriority(pName, tName, normalizedValues[0], normalizedValues[1]);
			else
				return handler.setNativeThreadPriority(pName, tName, normalizedValues[0], normalizedValues[1]);
		}
		else
		{
			System.out.println("Please run init first.");
			return false;
		}
	}
	

	/**
	 *	This method set native and java threads priority from a collection of threads.
	 * @param threads
	 * 				A collection of threads of ThreadData type.
	 * @return
	 * 		A list of Results objects. Each Results object contains data about the thread and a result whether priority set successfully or not.
	 */
	public static List<Results> setThreadPriority(Collection<ThreadData> threads) 
	{
		if (initialized)
			return handler.setNativeThreadPriority(threads);
		else 
		{
			System.out.println("Please run init first.");
			return null;
		}
	}
}








