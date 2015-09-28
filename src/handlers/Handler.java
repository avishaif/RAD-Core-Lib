package handlers;

import java.util.Collection;
import java.util.List;

import cache.Cache;
import core.ProcessData;
import core.Results;
import core.ThreadData;

public abstract class Handler 
{
	public List<Cache> cache = null;
	
	/**
	 * gets the available core/cpu count
	 * 
	 * @return number of available cores/cpu's
	 */
	
	public abstract int getProcessorCount();
	
	public abstract void clearCache();
	

//	
// AFFINITY	
	/**
	 * Assigns process to specific cores/CPUs. 
	 * @param pid
	 *            int process id
	 * @param affinity
	 *            int array, containing core/CPU number to assign process to.
	 * @return boolean true if function was successful false otherwise.
	 */

	public abstract boolean setProcessAffinity(int pid, int[] affinity);
	/**
	 * Assigns process to specific cores/CPUs.
	 * *Notice: process must have a unique name otherwise first matching process will be assigned.
	 * @param pName
	 *            String process name
	 * @param affinity
	 *            int array, containing core/CPU number to assign process to.
	 * @return boolean true if function was successful false otherwise.
	 */
	public abstract boolean setProcessAffinity(String pName, int[] affinity);
	/**
	 * Assigns each process on the list to  specific cores/CPUs.
	 * @param processes
	 *            Collection of ProcessData type.
	 * @return ArrayList of type Result, containing int id of each process and boolean true/false depending on function result.
	 * 
	 */
	public abstract List<Results> setProcessAffinity(Collection<ProcessData> processes);
	/**
	 * Assigns thread to specific cores/CPUs.
	 * @param tid
	 *            int Thread id.
	 * @param affinity
	 *            int array containing cores/CPUs numbers to assign thread.
	 * @return boolean true if function was successful false otherwise.
	 * 
	 */
	public abstract boolean setNativeThreadAffinity(int tid, int[] affinity);
	/**
	 *Assigns thread to specific cores/CPUs.
	 **Notice: thread must have a unique name otherwise first matching thread will be assigned.
	 * @param tName
	 *            String thread name.
	 * @param affinity
	 *            int array containing cores/CPUs numbers to assign thread.
	 * @return boolean true if function was successful false otherwise.
	 */
	public abstract boolean setNativeThreadAffinity(String tName, int[] affinity);
	/**
	 * Assigns thread to specific cores/CPUs. search for thread is done by its process name.
	 * *Notice: process, thread must have a unique name otherwise first matching thread will be assigned.
	 * @param pName String process name
	 * @param tName String thread name
	 * @param affinity int array containing cores/CPUs numbers to assign thread.
	 * @return boolean true if function was successful false otherwise.
	 */
	public abstract boolean setNativeThreadAffinity(String pName, String tName, int[] affinity);
	/**
	 * 
	 * @param threads
	 *            Collection of type ThreadData
	 * @return ArrayList of type Result containing int thread id and boolean true/false depending on function result.
	 *         process/thread true is set if set successfuly
	 */
	public abstract List<Results> setNativeThreadAffinity(Collection<ThreadData> threads);
	/**
	 * Assigns a Java thread to specific cores/CPUs.
	 * @param tid int thread id.
	 * @param affinity int array containing cores/CPUs numbers to assign thread.
	 * @return boolean true if function was successful false otherwise.
	 */
	public abstract boolean setJavaThreadAffinity(int tid, int[] affinity);
	/**
	 * Assigns a Java thread to specific cores/CPUs. searches for thread by its name<br>
	 * *Notice: thread must have a unique name otherwise first matching thread will be assigned.
	 * @param tName String thread name
	 * @param affinity int array containing cores/CPUs numbers to assign thread.
	 * @return boolean true if function was successful false otherwise.
	 */
	public abstract boolean setJavaThreadAffinity(String tName, int[] affinity);
	
	/**
	 * Assigns a Java thread to specific cores/CPUs. search is done by JVM name
	 * @param pName String process name (JVM).
	 * @param tName String thread name.
	 * @param affinity int array containing cores/CPUs numbers to assign thread.
	 * @return boolean true if function was successful false otherwise.
	 */
	public abstract boolean setJavaThreadAffinity(String pName, String tName, int[] affinity);
	
//
// PRIORITY	
	/**
	 * Assigns a process a priority and a schedualing policy if applicable (OS dependant).
	 * @param pid
	 *             int process id.
	 * @param priority
	 *            int priority value.
	 * @return
	 */
	public abstract boolean setProcessPriority(int pid, int policy, int priority);	
	/**
	 *Assigns a process a priority and a schedualing policy if applicable (OS dependant).
	 * @param pName
	 *            String process name.
	 * @param priority
	 *            int priority value.
	 * @return
	 */
	public abstract boolean setProcessPriority(String pName, int policy, int priority);	
	/**
	 * Assigns each process in a Collection of type processData a priority and a schedualing policy if applicable (OS dependant).
	 * @param processes
	 *            Collection of type ProcessData.
	 * @return ArrayList of type Result containing int: process id and boolean: true/false depending on function result.
	 *         process/thread true is set if set successfuly
	 * 
	 */
	public abstract List<Results> setProcessPriority(Collection<ProcessData> processes);
	/**
	 *
	 * @param tid
	 *            thread name
	 * @param priority
	 * @return true if set successfuly
	 */
	public abstract boolean setNativeThreadPriority(int tid, int policy, int priority);
	public abstract boolean setNativeThreadPriority(String tName, int policy, int priority);	
	public abstract boolean setNativeThreadPriority(String pName, String tName, int policy, int priority);
	public abstract boolean setNativeThreadPriority(int pid, String tName, int policy, int priority);
	
	/**
	 * set priority for a list of threads
	 * 
	 * @param threads
	 *            a collection of ThreadData type
	 * @return return an Array of type boolean foreach thread true if set
	 *         successfuly
	 */
	public abstract List<Results> setNativeThreadPriority(Collection<ThreadData> threads);
	
	public abstract boolean setJavaThreadPriority(int tid, int policy, int priority);
	
	/**
	 *
	 * @param tName
	 *            thread name
	 * @param priority
	 *            priority value to be set
	 * @return true if set successfuly
	 */
	public abstract boolean setJavaThreadPriority(String tName, int policy, int priority);
	
	public abstract boolean setJavaThreadPriority(String pName, String tName, int policy, int priority);
	public abstract boolean setJavaThreadPriority(int pid, String tName, int policy, int priority);
}



 