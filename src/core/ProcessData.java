package core;

import java.util.ArrayList;
import java.util.List;


/**
 * ProcessData class represents the data of a process such as ID, name, affinity, priority and threads list in case the process has threads. 
 * Use this class when you want to set affinity/priority to a collection of processes.
 */
public class ProcessData 
{
	private int pid;
	private String pName = "none";
	private int priority;
	private int[] affinity;
	private List<ThreadData> threads;

	/**
	 * Constructor by process name.
	 * @param name
	 * 			Process name.
	 * @param priority
	 * 			Process priority.
	 * @param affinity
	 * 			Process affinity. Each value in the array represent a CPU.
	 */
	public ProcessData(String name, int priority, int[] affinity) 
	{
		this.threads = new ArrayList<>();
		this.priority = priority;
		this.pName = name;
		this.affinity = affinity;
	}

	/**
	 * Constructor by process ID.
	 * @param pid
	 * 			Process ID.
	 * @param priority
	 * 			Process priority.
	 * @param affinity
	 * 			Process affinity. Each value in the array represent a CPU.
	 */
	public ProcessData(int pid, int priority, int[] affinity) 
	{
		this.threads = new ArrayList<>();
		this.pid = pid;
		this.priority = priority;
		this.affinity = affinity;
	}

	/**
	 * Set process name.
	 * @param name
	 * 			Process name.
	 */
	public void setName(String name) {
		this.pName = name;
	}
	
	/**
	 * Set process ID.
	 * @param pid
	 * 			Process ID.
	 */
	public void setId(int pid)
	{
		this.pid = pid;
	}

	/**
	 * Set process priority.
	 * @param priority
	 * 				Process priority.
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * Set process affinity. Each value in the array represent a CPU.
	 * @param affinity
	 * 				Array of integer values which represent process affinity.
	 */
	public void setAffinity(int[] affinity) {
		this.affinity = new int[affinity.length];
		for (int i = 0; i < affinity.length; i++) {
			this.affinity[i] = affinity[i];
		}
	}

	/**
	 * Add a thread to a list of threads.
	 * @param thread
	 * 			Thread of ThreadData type.
	 */
	public void addThread(ThreadData thread) {
		this.threads.add(thread);
	}

	/**
	 * Get process ID.
	 * @return
	 * 		Process ID.
	 */
	public int getId() {
		return this.pid;
	}

	/**
	 * Get process priority.
	 * @return
	 * 		Process priority.
	 */
	public int getPriority() {
		return this.priority;
	}

	/**
	 * Get process affinity.
	 * @return
	 * 		Array of integer values, each represents a CPU.
	 */
	public int[] getAffinity() {
		return this.affinity;
	}

	/**
	 * Get process Name.
	 * @return
	 * 		Process name.
	 */
	public String getName() {
		return this.pName;
	}


	/**
	 * Get a list of threads running in the process.
	 * @return
	 * 		List of threads, each thread is of ThreadData type.
	 */
	public List<ThreadData> getThreads() {
		return this.threads;
	}
}
