package facade;

import java.util.Collection;
import java.util.List;

import normalizers.Normalizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import results.ProcessResult;
import results.Result;
import handlers.Handler;
import handlers.LinuxHandler;
import handlers.WindowsHandler;
import core.ProcessData;
import core.ThreadData;


/**
 * Facade class is the API of the library.
 * Use this class to set affinity/priority to processes, threads or a collection of processes/threads.
 * The library is a cross-platform tool. It works under Linux and Windows operating systems.
 */
public class Facade 
{
	private static Handler handler;
	private static Logger log;
	private static boolean initialized = false;
	private static Normalizer normalizer;
	
	/**
	 * Initializes the library.
	 * Loads the logger, reads priority configurations from XML file and loads necessary 
	 * libraries according to the operating system to perform affinity and priority operations.
	 * RUN THE METHOD BEFORE USING EACH OF THE METHODS IN THIS CLASS.
	 * @return boolean
	 * 				True if all data initiated successfully. False otherwise.
	 */
	public static boolean init() 
	{
		log = LogManager.getRootLogger();
		if (log == null)
			log = LogManager.getLogger(Facade.class);
		String os = System.getProperty("os.name");
		if (os.startsWith("Windows"))
		{
			normalizer = new Normalizer("Windows");
			if(!normalizer.checkSchema())
			{
				if(log.isErrorEnabled())
				{
					log.error("An error accured while reading/verifying priority configuration file");
				}
				return false;
			}
			normalizer.mapValues();
			handler = new WindowsHandler(normalizer);
		} 
		else if (os.startsWith("Linux"))
		{
			normalizer = new Normalizer("Linux");
			if(!normalizer.checkSchema())
			{
				if(log.isErrorEnabled())
				{
					log.error("An error accured while reading/verifying priority configuration file");
				}
				return false;
			}
			normalizer.mapValues();
			handler = new LinuxHandler(normalizer);
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
	 * Clears the cache used while the library is active.
	 */
	public static void clearCache()
	{
		if(initialized)
			handler.clearCache();
		else
			System.out.println("Please run init first.");
	}
	
	
	
	/**
	 * Get number of processors installed in the system.
	 * init() method must be called before using this method. 
	 * @return
	 * 		Number of CPUs.
	 */
	public static int getProcessorCount()
	{
		if(initialized)
			return handler.getProcessorCount();
		else
		{
			System.out.println("Please run init first.");
			return -1;
		}
	}
	
	/**
	* Set process affinity by process ID (user must have appropriate permissions).
	* init() method must be called before using this method.
	* @param pid
	*            Process ID
	* @param affinity
	*            Array of integer values. Each value represents a CPU.
	* @return 
	* 		True if the operation completed successfully, false otherwise.
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
	* Set process affinity by process name (user must have appropriate permissions).
	* init() method must be called before using this method.
	* @param pName
	*            Process name
	* @param affinity
	*            Array of integer values. Each value represents a CPU.
	* @return 
	* 		True if the operation completed successfully, false otherwise.
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
	* Set affinity on a collection of processes. If a process has threads their affinity will be set as well.
	* init() method must be called before using this method.
	* @param processes
	* 			A collection of processes, each process must be of ProcessData type.
	* @return 
	* 		List of ProcessResult type. Each record in the list represents the result of set process affinity operation.
	* 		See ProcessResult class documentation for further details. 
	*/
	public static List<ProcessResult> setProcessAffinity(Collection<ProcessData> processes) 
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
	 * Set thread affinity by thread ID (user must have appropriate permissions).
	 * init() method must be called before using this method.
	 * @param tid
	 *           Thread ID. If the thread is native, the ID value must be a native ID, if the thread is a java thread then the ID value
	 *           must be JVM defined ID of the thread.
	 * @param affinity
	 *           Array of integer values. Each value represents a CPU.
	 * @param isJavaThread
	 * 			True if the thread is java thread, false if the thread is a native thread.
	 * @return 
	 * 		True if the operation completed successfully, false otherwise.
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
	 * Set thread affinity by thread name (user must have appropriate permissions).
	 * init() method must be called before using this method.
	 * In Windows operating systems, native threads don't have names, hence if the thread is a native thread
	 * the method will be unable to perform the operation and the returned value will be false.
	 * @param tName
	 *           Thread name. 
	 * @param affinity
	 *           Array of integer values. Each value represents a CPU.
	 * @param isJavaThread
	 * 			True if the thread is java thread, false if the thread is a native thread.
	 * @return 
	 * 		True if the operation completed successfully, false otherwise.
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
 	 * Set thread affinity by process and thread name (user must have appropriate permissions).
	 * init() method must be called before using this method.
	 * In Windows operating systems, native threads don't have names, hence if the thread is a native thread
	 * the method will be unable to perform the operation and the returned value will be false.
	 * @param pName
	 * 			Process name under which the thread is executing.
	 * @param tName
	 * 			Thread name.
	 * @param affinity
	 *           Array of integer values. Each value represents a CPU.
	 * @param isJavaThread
	 * 			True if the thread is java thread, false if the thread is a native thread.
	 * @return 
	 * 		True if the operation completed successfully, false otherwise.
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
 	 * Set thread affinity by process ID and thread name (user must have appropriate permissions).
	 * init() method must be called before using this method.
	 * In Windows operating systems, native threads don't have names, hence if the thread is a native thread
	 * the method will be unable to perform the operation and the returned value will be false.
	 * @param pid
	 * 			Process ID under which the thread is executing.
	 * @param tName
	 * 			Thread name.
	 * @param affinity
	 *           Array of integer values. Each value represents a CPU.
	 * @param isJavaThread
	 * 			True if the thread is java thread, false if the thread is a native thread.
	 * @return 
	 * 		True if the operation completed successfully, false otherwise.
	 */
	public static boolean setThreadAffinity(int pid, String tName, int[] affinity, boolean isJavaThread)
	{
		if(initialized)
			if(isJavaThread)
				return handler.setJavaThreadAffinity(pid, tName, affinity);
			else
				return handler.setNativeThreadAffinity(pid, tName, affinity);
		else
		{
			System.out.println("Please run init first.");
			return false;
		}
	}
	
	/**
	* Set affinity on a collection of threads.
	* init() method must be called before using this method.
	* @param threads
	* 			A collection of threads, each thread must be of ThreadData type.
	* @return 
	* 		List of Result type. Each record in the list represents the result of set thread affinity operation.
	* 		See Result class documentation for further details. 
	*/
	public static List<Result> setThreadAffinity(Collection<ThreadData> threads) 
	{
		if (initialized)
			return handler.setThreadAffinity(threads);
		else 
		{
			System.out.println("Please run init first.");
			return null;
		}
	}

	/**
	* Set process priority by process ID (user must have appropriate permissions).
	* init() method must be called before using this method.
	* @param pid
	*            Process ID.
	* @param priority
	*            Raw priority value. The actual priority value determined according to the mapping values
	*            from priority configuration file.
	*            In Linux operating system the policy value defined from the mapping as it configured in the priority configuration file.
	* @return 
	* 		True if the operation completed successfully, false otherwise.
	*/
	public static boolean setProcessPriority(int pid, int priority) 
	{
		if (initialized)
		{
			return handler.setProcessPriority(pid, priority);
		}
		else 
		{
			System.out.println("Please run init first.");
			return false;
		}
	}

	/**
	* Set process priority by process name (user must have appropriate permissions).
	* init() method must be called before using this method.
	* @param pName
	*            Process name.
	* @param priority
	*            Raw priority value. The actual priority value determined according to the mapping values
	*            from priority configuration file.
	*            In Linux operating system the policy value defined from the mapping as it configured in the priority configuration file.
	* @return 
	* 		True if the operation completed successfully, false otherwise.
	*/
	public static boolean setProcessPriority(String pName, int priority) 
	{
		if (initialized) 
		{
			return handler.setProcessPriority(pName, priority);
		} else 
		{
			System.out.println("Please run init first.");
			return false;
		}
	}

	/**
	* Set priority on a collection of processes. If a process has threads, their priority will be set as well.
	* init() method must be called before using this method.
	* @param processes
	* 			A collection of processes, each process must be of ProcessData type.
	* @return 
	* 		List of ProcessResult type. Each record in the list represents the result of set process priority operation.
	* 		See ProcessResult class documentation for further details. 
	*/
	public static List<ProcessResult> setProcessPriority(Collection<ProcessData> processes) 
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
	* Set thread priority by thread ID (user must have appropriate permissions).
	* init() method must be called before using this method.
	* @param tid
	*           Thread ID. If the thread is native, the ID value must be a native ID, if the thread is a java thread then the ID value
	*           must be JVM defined ID of the thread.
	* @param priority
	* 			Raw priority value. The actual priority value determined according to the mapping values
	*           from priority configuration file.
	*           In Linux operating system the policy value defined from the mapping as it configured in the priority configuration file.
	* @param isJavaThread
	* 			True if the thread is java thread, false if the thread is a native thread.
	* @return 
	* 		True if the operation completed successfully, false otherwise.
	*/
	public static boolean setThreadPriority(int tid, int priority, boolean isJavaThread) 
	{
		if (initialized) 
		{
			if (isJavaThread) 
			{
				return handler.setJavaThreadPriority(tid, priority);
			}
			else 
			{
				return handler.setNativeThreadPriority(tid, priority);
			}
		} 
		else 
		{
			System.out.println("Please run init first.");
			return false;
		}
	}

	/**
	* Set thread priority by thread name (user must have appropriate permissions).
	* init() method must be called before using this method.
	* In Windows operating systems, native threads don't have names, hence if the thread is a native thread
	* the method will be unable to perform the operation and the returned value will be false.
	* @param tName
	*           Thread name.
	* @param priority
	* 			Raw priority value. The actual priority value determined according to the mapping values
	*           from priority configuration file.
	*           In Linux operating system the policy value defined from the mapping as it configured in the priority configuration file.
	* @param isJavaThread
	* 			True if the thread is java thread, false if the thread is a native thread.
	* @return 
	* 		True if the operation completed successfully, false otherwise.
	*/	
	public static boolean setThreadPriority(String tName, int priority, boolean isJavaThread) 
	{
		if (initialized) 
		{
			if (isJavaThread) 
			{
				return handler.setJavaThreadPriority(tName, priority);
			} 
			else 
			{
				return handler.setNativeThreadPriority(tName, priority);
			}
		} 
		else 
		{
			System.out.println("Please run init first.");
			return false;
		}
	}

	/**
	* Set thread priority by process and thread name (user must have appropriate permissions).
	* init() method must be called before using this method.
	* In Windows operating systems, native threads don't have names, hence if the thread is a native thread
	* the method will be unable to perform the operation and the returned value will be false.
	* @param pName
	* 			Process name under which the thread is executing.
	* @param tName
	*           Thread name.
	* @param priority
	* 			Raw priority value. The actual priority value determined according to the mapping values
	*           from priority configuration file.
	*           In Linux operating system the policy value defined from the mapping as it configured in the priority configuration file.
	* @param isJavaThread
	* 			True if the thread is java thread, false if the thread is a native thread.
	* @return 
	* 		True if the operation completed successfully, false otherwise.
	*/	
	public static boolean setThreadPriority(String pName, String tName, int priority, boolean isJavaThread)
	{
		if(initialized)
		{
			if(isJavaThread)
				return handler.setJavaThreadPriority(pName, tName, priority);
			else
				return handler.setNativeThreadPriority(pName, tName, priority);
		}
		else
		{
			System.out.println("Please run init first.");
			return false;
		}
	}
	
	/**
	* Set thread priority by process ID and thread name (user must have appropriate permissions).
	* init() method must be called before using this method.
	* In Windows operating systems, native threads don't have names, hence if the thread is a native thread
	* the method will be unable to perform the operation and the returned value will be false.
	* @param pid
	* 			Process ID under which the thread is executing.
	* @param tName
	*           Thread name.
	* @param priority
	* 			Raw priority value. The actual priority value determined according to the mapping values
	*           from priority configuration file.
	*           In Linux operating system the policy value defined from the mapping as it configured in the priority configuration file.
	* @param isJavaThread
	* 			True if the thread is java thread, false if the thread is a native thread.
	* @return 
	* 		True if the operation completed successfully, false otherwise.
	*/
	public static boolean setThreadPriority(int pid, String tName, int priority, boolean isJavaThread)
	{
		if(initialized)
		{
			if(isJavaThread)
				return handler.setJavaThreadPriority(pid, tName, priority);
			else
				return handler.setNativeThreadPriority(pid, tName, priority);
		}
		else
		{
			System.out.println("Please run init first.");
			return false;
		}
	}

	/**
	* Set priority on a collection of threads.
	* init() method must be called before using this method.
	* @param threads
	* 			A collection of threads, each thread must be of ThreadData type.
	* @return 
	* 		List of Result type. Each record in the list represents the result of set thread priority operation.
	* 		See Result class documentation for further details. 
	*/
	public static List<Result> setThreadPriority(Collection<ThreadData> threads) 
	{
		if (initialized)
			return handler.setThreadPriority(threads);
		else 
		{
			System.out.println("Please run init first.");
			return null;
		}
	}
}


















