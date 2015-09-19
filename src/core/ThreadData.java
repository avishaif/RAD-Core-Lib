package core;

import normalizers.LinuxNormalizer;
import normalizers.Normalize;
import normalizers.WindowsNormalizer;

public class ThreadData 
{

	private int policy;
	private String tName = "none";
	private int priority = -1;
	private int[] affinity = null;
	private int tid = 0;
	private Normalize normalizer;
	private boolean isJavaThread = false;
	
	/**
	 * 
	 * @param name thread name
	 * @param policy scheduling policy value
	 * @param priority priority value
	 * @param affinity int array containing the numbers of cores/cpu's to set 
	 */
	public ThreadData (String name, boolean javaThread, int priority, int[] affinity)
	{
		if (System.getProperty("os.name").startsWith("Windows")) 
		{
			normalizer = new WindowsNormalizer();		
		} 
		else if (System.getProperty("os.name").startsWith("Linux")) 
		{
			normalizer = new LinuxNormalizer();
		}
		int[] normalizedValues = normalizer.normalize(priority, false);
		this.policy = normalizedValues[0];
		this.tName = name;
		this.isJavaThread = javaThread;
		this.priority = normalizedValues[1];
		this.affinity = new int[affinity.length];
		for (int i = 0; i < affinity.length; i++)
		{
			this.affinity[i] = affinity[i];
		}
	}
	
	/**
	 * 
	 * @param tid
	 * @param policy
	 * @param priority
	 * @param affinity
	 */
	
	public ThreadData (int tid, boolean javaThread, int priority, int[] affinity)
	{
		if (System.getProperty("os.name").startsWith("Windows")) 
		{
			normalizer = new WindowsNormalizer();
		}
		else if (System.getProperty("os.name").startsWith("Linux")) 
		{
			normalizer = new LinuxNormalizer();
		}
		int[] normalizedValues = normalizer.normalize(priority, false);
		this.policy = normalizedValues[0];
		this.tid = tid;
		this.isJavaThread = javaThread;
		this.priority = normalizedValues[1];
		this.affinity = new int[affinity.length];
		for (int i = 0; i < affinity.length; i++)
		{
			this.affinity[i] = affinity[i];
		}
	}
	
	/**
	 * 
	 * @param policy
	 */
	
	public void setPolicy(int policy){
		this.policy = policy;
	}
	/**
	 * 
	 * @param name
	 */
	
	public void setName(String name){
		this.tName = name;
	}
	
	/**
	 * 
	 * @param priority
	 */
	
	public void setPriority(int priority){
		this.priority = priority;
	}
	
	/**
	 * 
	 * @param affinity
	 */
	
	public void setAffinity (int[] affinity){
		this.affinity = new int[affinity.length];
		for (int i = 0; i < affinity.length; i++)
		{
			this.affinity[i] = affinity[i];
		}
	}
	
	/**
	 * 
	 * @param tid
	 */
	
	public void setId(int tid){
		this.tid = tid;
	}
	
	
	/**
	 * 
	 * @param javaThread
	 */
	public void SetJavaThread(boolean javaThread)
	{
		this.isJavaThread = javaThread;
	}
	
	
	
	/**
	 * 
	 * @return
	 */
	
	public int getId(){
		return this.tid;
	}
	
	/**
	 * 
	 * @return
	 */
	
	public int getPriority(){
		return this.priority;
	}
	/**
	 * 
	 * @return
	 */
	public int[] getAffinity(){
		return this.affinity;
	}
	
	/**
	 * 
	 * @return
	 */
	
	public String getName(){
		return this.tName;
	}
	
	/**
	 * 
	 * @return
	 */
	
	public int getPolicy(){
		return this.policy;
	}
	
	public boolean isJavaThread()
	{
		return this.isJavaThread;
	}
	
	
}





