package core;


/**
 * ThreadData class represents the data of a thread such as ID, name, affinity, priority and if the thread is java thread or native. 
 * Use this class when you want to set affinity/priority to a collection of threads or if a process has thread/s.
 */
public class ThreadData 
{
	private String tName = "none";
	private int priority = -1;
	private int[] affinity = null;
	private int tid = 0;
	private boolean isJavaThread = false;
	
	/**
	 * Constructor by thread name.
	 * @param name 
	 * 			Thread name
	 * @param javaThread
	 * 			Represents whether the thread is a java or native thread.
	 * @param priority 
	 * 			Thread priority value
	 * @param affinity 
	 * 			Thread affinity. Each value in the array represent a CPU.
	 */
	public ThreadData (String name, boolean javaThread, int priority, int[] affinity)
	{
		this.tName = name;
		this.isJavaThread = javaThread;
		this.priority = priority;
		this.affinity = new int[affinity.length];
		for (int i = 0; i < affinity.length; i++)
		{
			this.affinity[i] = affinity[i];
		}
	}
	
	/**
	 * Constructor by thread ID.
	 * @param tid 
	 * 			Thread ID
	 * @param javaThread
	 * 			Represents whether the thread is a java or native thread.
	 * @param priority 
	 * 			Thread priority value
	 * @param affinity 
	 * 			Thread affinity. Each value in the array represent a CPU.
	 */
	public ThreadData (int tid, boolean javaThread, int priority, int[] affinity)
	{
		this.tid = tid;
		this.isJavaThread = javaThread;
		this.priority = priority;
		this.affinity = new int[affinity.length];
		for (int i = 0; i < affinity.length; i++)
		{
			this.affinity[i] = affinity[i];
		}
	}
	
	
	/**
	 * Set thread name.
	 * @param name
	 * 			Thread name.
	 */
	public void setName(String name){
		this.tName = name;
	}
	
	/**
	 * Set thread priority.
	 * @param priority
	 * 				Thread priority.
	 */
	public void setPriority(int priority){
		this.priority = priority;
	}
	
	/**
	 * Set thread affinity. Each value in the array represent a CPU.
	 * @param affinity
	 * 				Array of integer values which represent thread affinity.
	 */
	public void setAffinity (int[] affinity){
		this.affinity = new int[affinity.length];
		for (int i = 0; i < affinity.length; i++)
		{
			this.affinity[i] = affinity[i];
		}
	}
	
	/**
	 * Set thread ID.
	 * @param tid
	 * 			Thread ID.
	 */
	public void setId(int tid){
		this.tid = tid;
	}
	
	
	/**
	 * Set whether the thread is java or native thread.
	 * @param javaThread
	 * 			True if the thread is java thread, false if the thread is a native thread.
	 */
	public void SetJavaThread(boolean javaThread)
	{
		this.isJavaThread = javaThread;
	}
	
	
	
	/**
	 * Get thread ID.
	 * @return
	 * 		Thread ID.
	 */
	public int getId(){
		return this.tid;
	}
	
	/**
	 * Get thread priority.
	 * @return
	 * 		Thread priority.
	 */
	public int getPriority(){
		return this.priority;
	}
	
	/**
	 * Get thread affinity.
	 * @return
	 * 		Array of integer values, each represents a CPU.
	 */
	public int[] getAffinity(){
		return this.affinity;
	}
	
	/**
	 * Get thread Name.
	 * @return
	 * 		Thread name.
	 */
	public String getName(){
		return this.tName;
	}
	
	/**
	 * Get a boolean representation if the the thread is java or native thread.
	 * @return
	 * 		True if the thread is java thread, false if the thread is a native thread.
	 */
	public boolean isJavaThread()
	{
		return this.isJavaThread;
	}
	
	
}





