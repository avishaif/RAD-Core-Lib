package handlers;

import java.util.Collection;
import java.util.List;

import cache.Cache;
import results.ProcessResult;
import results.Result;
import core.ProcessData;
import core.ThreadData;

/**
 * 
 * 
 *
 */
public abstract class Handler 
{
	public List<Cache> cache = null;
	
	/**
	 * Get number of processors installed in the system. 
	 * @return
	 * 		Number of CPUs.
	 */
	public abstract int getProcessorCount();
	
	/**
	 * Clears the cache used while the library is active.
	 */
	public abstract void clearCache();
	
	/**
	 * Checks if process or thread is in cache.
	 * @param id
	 * 			Process or thread ID.
	 * @param name
	 * 			Process or thread name.
	 * @return
	 * 			Cache object if data found. Returns null otherwise. 
	 */
	public abstract Cache checkCache(int id, String name);
	
	
//	
// AFFINITY
//
	
	/**
	* Set process affinity by process ID (user must have appropriate permissions).
	* @param pid
	*            Process ID
	* @param affinity
	*            Array of integer values. Each value represents a CPU.
	* @return 
	* 		True if the operation completed successfully, false otherwise.
	*/
	public abstract boolean setProcessAffinity(int pid, int[] affinity);
	 
	/**
	* Set process affinity by process name (user must have appropriate permissions).
	* *Notice: process must have a unique name otherwise first matching process will be assigned.
	* @param pName
	*            Process name
	* @param affinity
	*            Array of integer values. Each value represents a CPU.
	* @return 
	* 		True if the operation completed successfully, false otherwise.
	*/
	public abstract boolean setProcessAffinity(String pName, int[] affinity);
	
	/**
	* Set affinity on a collection of processes. If a process has threads their affinity will be set as well.
	* @param processes
	* 			A collection of processes, each process must be of ProcessData type.
	* @return 
	* 		List of ProcessResult type. Each record in the list represents the result of set process affinity operation.
	* 		See ProcessResult class documentation for further details. 
	*/
	public abstract List<ProcessResult> setProcessAffinity(Collection<ProcessData> processes);
	
	/**
	 * Set native thread affinity by thread ID (user must have appropriate permissions).
	 * @param tid
	 *           Thread ID.
	 * @param affinity
	 *           Array of integer values. Each value represents a CPU.
	 * @return 
	 * 		True if the operation completed successfully, false otherwise.
	 */
	public abstract boolean setNativeThreadAffinity(int tid, int[] affinity);
	
	 /**
	 * Set native thread affinity by thread name (user must have appropriate permissions).
	 * *Notice: thread must have a unique name, otherwise first matching thread will be assigned.
	 * In Windows operating systems, native threads don't have names, hence the method will be unable
	 * to perform the operation and the returned value will be false.
	 * @param tName
	 *           Thread name. 
	 * @param affinity
	 *           Array of integer values. Each value represents a CPU.
	 * @return 
	 * 		True if the operation completed successfully, false otherwise.
	 */
	public abstract boolean setNativeThreadAffinity(String tName, int[] affinity);
	
	 /**
 	 * Set native thread affinity by process and thread name (user must have appropriate permissions).
	 * *Notice: process and thread must have unique names, otherwise the thread will be
	 * searched in first matching process and the first matching thread will be assigned.
	 * In Windows operating systems, native threads don't have names, hence the method will 
	 * be unable to perform the operation and the returned value will be false.
	 * @param pName
	 * 			Process name under which the thread is executing.
	 * @param tName
	 * 			Thread name.
	 * @param affinity
	 *           Array of integer values. Each value represents a CPU.
	 * @return 
	 * 		True if the operation completed successfully, false otherwise.
	 */
	public abstract boolean setNativeThreadAffinity(String pName, String tName, int[] affinity);
	
	/**
 	 * Set native thread affinity by process ID and thread name (user must have appropriate permissions).
 	 * *Notice: thread must have a unique name, otherwise first matching thread will be assigned.
	 * In Windows operating systems, native threads don't have names, hence the method will be
	 * unable to perform the operation and the returned value will be false.
	 * @param pid
	 * 			Process ID under which the thread is executing.
	 * @param tName
	 * 			Thread name.
	 * @param affinity
	 *           Array of integer values. Each value represents a CPU.
	 * @return 
	 * 		True if the operation completed successfully, false otherwise.
	 */
	public abstract boolean setNativeThreadAffinity(int pid, String tName, int[] affinity);
	
	/**
	* Set affinity on a collection of threads.
	* @param threads
	* 			A collection of threads, each thread must be of ThreadData type.
	* @return 
	* 		List of Result type. Each record in the list represents the result of set thread affinity operation.
	* 		See Result class documentation for further details. 
	*/
	public abstract List<Result> setThreadAffinity(Collection<ThreadData> threads);
	
	/**
	 * Set java thread affinity by thread ID (user must have appropriate permissions).
	 * @param tid
	 *           Thread ID given by the JVM.
	 * @param affinity
	 *           Array of integer values. Each value represents a CPU.
	 * @return 
	 * 		True if the operation completed successfully, false otherwise.
	 */
	public abstract boolean setJavaThreadAffinity(int tid, int[] affinity);
	
	/**
	 * Set java thread affinity by thread name (user must have appropriate permissions).
	 * *Notice: thread must have a unique name, otherwise first matching thread will be assigned.
	 * @param tName
	 *           Thread name. 
	 * @param affinity
	 *           Array of integer values. Each value represents a CPU.
	 * @return 
	 * 		True if the operation completed successfully, false otherwise.
	 */
	public abstract boolean setJavaThreadAffinity(String tName, int[] affinity);
	
	/**
 	 * Set java thread affinity by process and thread name (user must have appropriate permissions).
	 * *Notice: process and thread must have unique names, otherwise the thread will be
	 * searched in first matching process and the first matching thread will be assigned.
	 * @param pName
	 * 			Process name under which the thread is executing.
	 * @param tName
	 * 			Thread name.
	 * @param affinity
	 *           Array of integer values. Each value represents a CPU.
	 * @return 
	 * 		True if the operation completed successfully, false otherwise.
	 */
	public abstract boolean setJavaThreadAffinity(String pName, String tName, int[] affinity);
	
	/**
 	 * Set java thread affinity by process ID and thread name (user must have appropriate permissions).
 	 * *Notice: thread must have a unique name, otherwise first matching thread will be assigned.
	 * @param pid
	 * 			Process ID under which the thread is executing.
	 * @param tName
	 * 			Thread name.
	 * @param affinity
	 *           Array of integer values. Each value represents a CPU.
	 * @return 
	 * 		True if the operation completed successfully, false otherwise.
	 */
	public abstract boolean setJavaThreadAffinity(int pid, String tName, int[] affinity);
	
//
// PRIORITY	
//
	
	/**
	* Set process priority by process ID (user must have appropriate permissions).
	* @param pid
	*            Process ID.
	* @param priority
	*            Raw priority value. The actual priority value determined according to the mapping values
	*            from priority configuration file.
	*            In Linux operating system the policy value defined from the mapping as it configured in the priority configuration file.
	* @return 
	* 		True if the operation completed successfully, false otherwise.
	*/
	public abstract boolean setProcessPriority(int pid, int priority);	
	
	/**
	* Set process priority by process name (user must have appropriate permissions).
	* *Notice: process must have a unique name, otherwise first matching process will be assigned.
	* @param pName
	*            Process name.
	* @param priority
	*            Raw priority value. The actual priority value determined according to the mapping values
	*            from priority configuration file.
	*            In Linux operating system the policy value defined from the mapping as it configured in the priority configuration file.
	* @return 
	* 		True if the operation completed successfully, false otherwise.
	*/
	public abstract boolean setProcessPriority(String pName, int priority);	
	
	/**
	* Set priority on a collection of processes. If a process has threads, their priority will be set as well.
	* @param processes
	* 			A collection of processes, each process must be of ProcessData type.
	* @return 
	* 		List of ProcessResult type. Each record in the list represents the result of set process priority operation.
	* 		See ProcessResult class documentation for further details. 
	*/
	public abstract List<ProcessResult> setProcessPriority(Collection<ProcessData> processes);
	
	/**
	* Set native thread priority by thread ID (user must have appropriate permissions).
	* @param tid
	*           Thread ID.
	* @param priority
	* 			Raw priority value. The actual priority value determined according to the mapping values
	*           from priority configuration file.
	*           In Linux operating system the policy value defined from the mapping as it configured in the priority configuration file.
	* @return 
	* 		True if the operation completed successfully, false otherwise.
	*/
	public abstract boolean setNativeThreadPriority(int tid, int priority);
	
	/**
	* Set native thread priority by thread name (user must have appropriate permissions).
	* *Notice: thread must have a unique name, otherwise first matching thread will be assigned.
	* In Windows operating systems, native threads don't have names, hence the method will be
	* unable to perform the operation and the returned value will be false.
	* @param tName
	*           Thread name.
	* @param priority
	* 			Raw priority value. The actual priority value determined according to the mapping values
	*           from priority configuration file.
	*           In Linux operating system the policy value defined from the mapping as it configured in the priority configuration file.
	* @return 
	* 		True if the operation completed successfully, false otherwise.
	*/
	public abstract boolean setNativeThreadPriority(String tName, int priority);
	
	/**
	* Set native thread priority by process and thread name (user must have appropriate permissions).
	* *Notice: process and thread must have unique names, otherwise the thread will be
	* searched in first matching process and the first matching thread will be assigned.
	* In Windows operating systems, native threads don't have names, hence the method will be unable 
	* to perform the operation and the returned value will be false.
	* @param pName
	* 			Process name under which the thread is executing.
	* @param tName
	*           Thread name.
	* @param priority
	* 			Raw priority value. The actual priority value determined according to the mapping values
	*           from priority configuration file.
	*           In Linux operating system the policy value defined from the mapping as it configured in the priority configuration file.
	* @return 
	* 		True if the operation completed successfully, false otherwise.
	*/
	public abstract boolean setNativeThreadPriority(String pName, String tName, int priority);
	
	/**
	* Set native thread priority by process ID and thread name (user must have appropriate permissions).
	* *Notice: thread must have a unique name, otherwise first matching thread will be assigned.
	* In Windows operating systems, native threads don't have names, hence the method will be unable 
	* to perform the operation and the returned value will be false.
	* @param pid
	* 			Process ID under which the thread is executing.
	* @param tName
	*           Thread name.
	* @param priority
	* 			Raw priority value. The actual priority value determined according to the mapping values
	*           from priority configuration file.
	*           In Linux operating system the policy value defined from the mapping as it configured in the priority configuration file.
	* @return 
	* 		True if the operation completed successfully, false otherwise.
	*/
	public abstract boolean setNativeThreadPriority(int pid, String tName, int priority);
	
	/**
	* Set priority on a collection of threads.
	* @param threads
	* 			A collection of threads, each thread must be of ThreadData type.
	* @return 
	* 		List of Result type. Each record in the list represents the result of set thread priority operation.
	* 		See Result class documentation for further details. 
	*/
	public abstract List<Result> setThreadPriority(Collection<ThreadData> threads);
	
	
	/**
	* Set java thread priority by thread ID (user must have appropriate permissions).
	* @param tid
	*           Thread ID given by the JVM.
	* @param priority
	* 			Raw priority value. The actual priority value determined according to the mapping values
	*           from priority configuration file.
	*           In Linux operating system the policy value defined from the mapping as it configured in the priority configuration file.
	* @return 
	* 		True if the operation completed successfully, false otherwise.
	*/
	public abstract boolean setJavaThreadPriority(int tid, int priority);
	
	/**
	 * Set java thread priority by thread name (user must have appropriate permissions).
	 * *Notice: thread must have a unique name, otherwise first matching thread will be assigned.
	 * @param tName
	 *           Thread name. 
	 * @param priority
	 * 			 Raw priority value. The actual priority value determined according to the mapping values
	 *           from priority configuration file.
	 *           In Linux operating system the policy value defined from the mapping as it configured in the priority configuration file.
	 * @return 
	 * 		True if the operation completed successfully, false otherwise.
	 */
	public abstract boolean setJavaThreadPriority(String tName, int priority);
	
	/**
 	 * Set java thread priority by process and thread name (user must have appropriate permissions).
	 * *Notice: process and thread must have unique names, otherwise the thread will be
	 * searched in first matching process and the first matching thread will be assigned.
	 * @param pName
	 * 			Process name under which the thread is executing.
	 * @param tName
	 * 			Thread name.
	 * @param priority
	 * 			 Raw priority value. The actual priority value determined according to the mapping values
	 *           from priority configuration file.
	 *           In Linux operating system the policy value defined from the mapping as it configured in the priority configuration file.
	 * @return 
	 * 		True if the operation completed successfully, false otherwise.
	 */
	public abstract boolean setJavaThreadPriority(String pName, String tName, int priority);
	
	/**
 	 * Set java thread priority by process ID and thread name (user must have appropriate permissions).
 	 * *Notice: thread must have a unique name, otherwise first matching thread will be assigned.
	 * @param pid
	 * 			Process ID under which the thread is executing.
	 * @param tName
	 * 			Thread name.
	 * @param priority
	 * 			 Raw priority value. The actual priority value determined according to the mapping values
	 *           from priority configuration file.
	 *           In Linux operating system the policy value defined from the mapping as it configured in the priority configuration file.
	 * @return 
	 * 		True if the operation completed successfully, false otherwise.
	 */
	public abstract boolean setJavaThreadPriority(int pid, String tName, int priority);

	
}














 