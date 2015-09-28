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

public class Facade {
	private static Handler handler;
	private static Normalize normalizer;
	private static Logger log;
	private static boolean initialized = false;

	public static boolean init() {
		log = LogManager.getLogger(Facade.class);
		if (System.getProperty("os.name").startsWith("Windows")) {
			handler = new WindowsHandler();
			normalizer = new WindowsNormalizer();
		} else if (System.getProperty("os.name").startsWith("Linux")) {
			handler = new LinuxHandler();
			normalizer = new LinuxNormalizer();
		} else {
			if (log.isErrorEnabled()) {
				log.error("Unsupported operating system.");
			}
			initialized = false;
			return false;
		}
		initialized = true;
		return true;
	}

	/**
	 * Assigns process to specific cores/CPUs.
	 * 
	 * @param pid
	 *            process id
	 * @param affinity
	 *            int array, containing core/CPU number to assign process to.
	 * @return boolean true if function was successful false otherwise.
	 */
	public static boolean setProcessAffinity(int pid, int[] affinity) {
		if (initialized)
			return handler.setProcessAffinity(pid, affinity);
		else {
			System.out.println("Please run init first.");
			return false;
		}

	}

	/**
	 * Assigns process to specific cores/CPUs.
	 * 
	 * @param pName
	 *            String, process name
	 * @param affinity
	 *            int array, containing core/CPU number to assign process to.
	 * @return boolean, true if function was successful false otherwise.
	 */
	public static boolean setProcessAffinity(String pName, int[] affinity) {
		if (initialized)
			return handler.setProcessAffinity(pName, affinity);
		else {
			System.out.println("Please run init first.");
			return false;
		}
	}

	/**
	 * Assigns each process and its threads (if exist in the collection) in the
	 * collection to specific cores/CPUs. **Notice: process must have a unique
	 * name otherwise first matching process will be assigned.
	 * 
	 * @param processes
	 *            Collection of ProcessData type.
	 * @param affinity
	 *            int array, containing core/CPU number to assign process to.
	 * @return ArrayList of type Result, containing int, id of each process and
	 *         boolean, true/false depending on function result for each item in
	 *         the collection.
	 */
	public static List<Results> setProcessAffinity(
			Collection<ProcessData> processes) {
		if (initialized)
			return handler.setProcessAffinity(processes);
		else {
			System.out.println("Please run init first.");
			return null;
		}
	}

	/**
	 * Assigns thread to specific cores/CPUs.
	 * 
	 * @param tid
	 *            int, thread id.
	 * @param affinity
	 *            int array, containing core/CPU number to assign process to.
	 * @return boolean, true if function was successful false otherwise.
	 */
	public static boolean setThreadAffinity(int tid, int[] affinity,
			boolean isJavaThread) {
		if (initialized)
			if (isJavaThread)
				return handler.setJavaThreadAffinity(tid, affinity);
			else
				return handler.setNativeThreadAffinity(tid, affinity);
		else {
			System.out.println("Please run init first.");
			return false;
		}
	}

	/**
	 * Assigns thread to specific cores/CPUs.
	 * 
	 * @param tName
	 *            String thread name.
	 * @param affinity
	 *            int array, containing core/CPU number to assign process to.
	 * @return boolean true if function was successful false otherwise.
	 */
	public static boolean setThreadAffinity(String tName, int[] affinity,
			boolean isJavaThread) {
		if (initialized)
			if (isJavaThread)
				return handler.setJavaThreadAffinity(tName, affinity);
			else
				return handler.setNativeThreadAffinity(tName, affinity);
		else {
			System.out.println("Please run init first.");
			return false;
		}
	}

	/**
	 * Assigns thread to specific cores/CPUs. **Notice: thread must have a
	 * unique name otherwise first matching thread will be assigned.
	 * 
	 * @param pid
	 *            int, process id.
	 * @param tid
	 *            int, thread id.
	 * @param affinity
	 *            int array, containing core/CPU number to assign process to.
	 * @param isJavaThread
	 *            boolean, true if thread is a jvm thread false otherwise.
	 * @return boolean true if function was successful false otherwise.
	 */
	public static boolean setThreadAffinity(int pid, int tid, int[] affinity,
			boolean isJavaThread) {
		if (initialized)
			if (isJavaThread)
				return handler.setJavaThreadAffinity(tid, affinity);
			else
				return handler.setNativeThreadAffinity(tid, affinity);
		else {
			System.out.println("Please run init first.");
			return false;
		}
	}

	/**
	 * Assigns thread to specific cores/CPUs.
	 * 
	 * @param pName
	 *            String, process name.
	 * @param tName
	 *            String, thread name.
	 * @param affinity
	 *            int array, containing core/CPU number to assign process to.
	 * @param isJavaThread
	 *            boolean, true if thread is a jvm thread false otherwise.
	 * @return boolean true if function was successful false otherwise.
	 */
	public static boolean setThreadAffinity(String pName, String tName,
			int[] affinity, boolean isJavaThread) {
		if (initialized)
			if (isJavaThread)
				return handler.setJavaThreadAffinity(pName, tName, affinity);
			else
				return handler.setNativeThreadAffinity(pName, tName, affinity);
		else {
			System.out.println("Please run init first.");
			return false;
		}
	}

	/**
	 * Assigns each thread in the collection to specific cores/CPUs. **Notice:
	 * thread must have a unique name otherwise first matching thread will be
	 * assigned.
	 * 
	 * @param threads
	 *            A collection of threads of ThreadData type.
	 * @return ArrayList of type Result, containing int, 'id' of each thread and
	 *         boolean, true/false depending on function result for each item in
	 *         the collection.
	 */
	public static List<Results> setThreadAffinity(Collection<ThreadData> threads) {
		if (initialized)
			return handler.setNativeThreadAffinity(threads);
		else {
			System.out.println("Please run init first.");
			return null;
		}
	}

	/**
	 * Assing process with scheduling policy (if applicable, OS depandant) and a
	 * priority value.
	 * 
	 * @param pid
	 *            int, process id.
	 * @param priority
	 *            int, priority/policy value
	 * @return boolean true if function was successful false otherwise.
	 */
	public static boolean setProcessPriority(int pid, int priority) {
		if (initialized) {
			int[] normalizedValues = normalizer.normalize(priority, true);
			if (normalizedValues != null)
				return handler.setProcessPriority(pid, normalizedValues[0],
						normalizedValues[1]);
			else if (log.isErrorEnabled())
				log.error("Priority value is not within permitted range.");
			return false;
		} else {
			System.out.println("Please run init first.");
			return false;
		}
	}

	/**
	 * Assing process with scheduling policy (if applicable, OS depandant) and a
	 * priority value.
	 * 
	 * @param pName
	 *            String, process name.
	 * @param priority
	 *            int, priority/policy value
	 * @return boolean true if function was successful false otherwise.
	 */
	public static boolean setProcessPriority(String pName, int priority) {
		if (initialized) {
			int[] normalizedValues = normalizer.normalize(priority, true);
			if (normalizedValues != null)
				return handler.setProcessPriority(pName, normalizedValues[0],
						normalizedValues[1]);
			else if (log.isErrorEnabled())
				log.error("Priority value is not within permitted range.");
			return false;
		} else {
			System.out.println("Please run init first.");
			return false;
		}
	}

	/**
	 * Assigns each process and its threads (if exist in the collection) with a
	 * scheduling policy (if applicable, OS depandant) and a priority value.
	 * 
	 * @param processes
	 *            Collection of ProcessData type.
	 * @return ArrayList of type Result, containing int, id of each process and
	 *         boolean, true/false depending on function result for each item in
	 *         the collection.
	 */
	public static List<Results> setProcessPriority(
			Collection<ProcessData> processes) {
		if (initialized)
			return handler.setProcessPriority(processes);
		else {
			System.out.println("Please run init first.");
			return null;
		}
	}

	/**
	 * Assing thread with scheduling policy (if applicable, OS depandant) and a
	 * priority value.
	 * 
	 * @param tid
	 *            int, thread id.
	 * @param priority
	 *            int, priority/policy value.
	 * @param isJavaThread
	 *            boolean, true if thread is a jvm thread.
	 * @return boolean true if function was successful false otherwise.
	 */
	public static boolean setThreadPriority(int tid, int priority,
			boolean isJavaThread) {
		if (initialized) {
			if (isJavaThread) {
				int[] normalizedValues = normalizer.normalize(priority, false);
				if (normalizedValues != null)
					return handler.setJavaThreadPriority(tid,
							normalizedValues[0], normalizedValues[1]);
				else if (log.isErrorEnabled())
					log.error("Priority value is not within permitted range.");
				return false;
			} else {
				int[] normalizedValues = normalizer.normalize(priority, false);
				if (normalizedValues != null)
					return handler.setNativeThreadPriority(tid,
							normalizedValues[0], normalizedValues[1]);
				else if (log.isErrorEnabled())
					log.error("Priority value is not within permitted range.");
				return false;
			}
		} else {
			System.out.println("Please run init first.");
			return false;
		}
	}

	/**
	 * Assing thread with scheduling policy (if applicable, OS depandant) and a
	 * priority value.
	 * 
	 * @param tName
	 *            String, thread name.
	 * @param priority
	 *            int, priority/policy value.
	 * @param isJavaThread
	 *            boolean, true if thread is a jvm thread.
	 * @return boolean true if function was successful false otherwise.
	 */
	public static boolean setThreadPriority(String tName, int priority,
			boolean isJavaThread) {
		if (initialized) {
			if (isJavaThread) {
				int[] normalizedValues = normalizer.normalize(priority, false);
				if (normalizedValues != null)
					return handler.setJavaThreadPriority(tName,
							normalizedValues[0], normalizedValues[1]);
				else if (log.isErrorEnabled())
					log.error("Priority value is not within permitted range.");
				return false;
			} else {
				int[] normalizedValues = normalizer.normalize(priority, false);
				if (normalizedValues != null)
					return handler.setNativeThreadPriority(tName,
							normalizedValues[0], normalizedValues[1]);
				if (log.isErrorEnabled())
					log.error("Priority value is not within permitted range.");
				return false;
			}
		} else {
			System.out.println("Please run init first.");
			return false;
		}
	}

	/**
	 * Assing thread with scheduling policy (if applicable, OS depandant) and a
	 * priority value.
	 * 
	 * @param pid
	 *            int, process id.
	 * @param tid
	 *            int, thread id.
	 * @param priority
	 *            int, priority/policy value.
	 * @param isJavaThread
	 *            boolean, true if thread is a jvm thread.
	 * @return boolean true if function was successful false otherwise.
	 */
	public static boolean setThreadPriority(int pid, int tid, int priority,
			boolean isJavaThread) {
		if (initialized) {
			int[] normalizedValues = normalizer.normalize(priority, false);
			if (isJavaThread)
				if (normalizedValues != null)
					return handler.setJavaThreadPriority(tid,
							normalizedValues[0], normalizedValues[1]);
				else {
					if (log.isErrorEnabled())
						log.error("Priority value is not within permitted range.");
					return false;
				}
			else if (normalizedValues != null)
				return handler.setNativeThreadPriority(tid,
						normalizedValues[0], normalizedValues[1]);
			else if (log.isErrorEnabled())
				log.error("Priority value is not within permitted range.");
			return false;
		} else {
			System.out.println("Please run init first.");
			return false;
		}
	}

	/**
	 * 
	 * @param pName
	 *            String, process name.
	 * @param tName
	 *            String, thread name.
	 * @param priority
	 *            int, priority/policy value.
	 * @param isJavaThread
	 *            boolean, true if thread is a jvm thread.
	 * @return boolean true if function was successful false otherwise.
	 */
	public static boolean setThreadPriority(String pName, String tName,
			int priority, boolean isJavaThread) {
		if (initialized) {
			int[] normalizedValues = normalizer.normalize(priority, false);
			if (isJavaThread)
				return handler.setJavaThreadPriority(pName, tName,
						normalizedValues[0], normalizedValues[1]);
			else
				return handler.setNativeThreadPriority(pName, tName,
						normalizedValues[0], normalizedValues[1]);
		} else {
			System.out.println("Please run init first.");
			return false;
		}
	}

	/**
	 * Assigns each thread in a Collection of type threadData a priority and a
	 * schedualing policy if applicable (OS dependant).
	 * 
	 * @param threads
	 *            collection of ThreadData type.
	 * @return A list of Results objects. Each Results object contains data
	 *         about the thread and a result whether priority set successfully
	 *         or not.
	 */
	public static List<Results> setThreadPriority(Collection<ThreadData> threads) {
		if (initialized)
			return handler.setNativeThreadPriority(threads);
		else {
			System.out.println("Please run init first.");
			return null;
		}
	}
	
	public static void clearCache()
	{
		if(initialized)
			handler.clearCache();
		else
			System.out.println("Please run init first.");
	}
}
