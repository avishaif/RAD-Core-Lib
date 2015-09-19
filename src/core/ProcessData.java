package core;

import java.util.ArrayList;
import java.util.List;

import normalizers.LinuxNormalizer;
import normalizers.Normalize;
import normalizers.WindowsNormalizer;

public class ProcessData {

	private int policy;
	private int pid;
	private String pName = "none";
	private int priority;
	private int[] affinity;
	private Normalize normalizer;
	private List<ThreadData> threads;

	/**
	 * 
	 * @param name
	 * @param policy
	 * @param priority
	 * @param affinity
	 */
	public ProcessData(String name, int priority, int[] affinity) {

		if (System.getProperty("os.name").startsWith("Windows")) 
		{
			normalizer = new WindowsNormalizer();
		} 
		else if (System.getProperty("os.name").startsWith("Linux")) 
		{
			normalizer = new LinuxNormalizer();
		}

		this.threads = new ArrayList<>();
		int[] normalizedValues = normalizer.normalize(priority, true);
		this.policy = normalizedValues[0];
		this.pName = name;
		this.priority = normalizedValues[1];
		this.affinity = affinity;
	}

	/**
	 *
	 * @param pid
	 * @param policy
	 * @param priority
	 * @param affinity
	 */
	public ProcessData(int pid, int priority, int[] affinity) {
		if (System.getProperty("os.name").startsWith("Windows")) 
		{
			normalizer = new WindowsNormalizer();
		}
		else if (System.getProperty("os.name").startsWith("Linux")) 
		{
			normalizer = new LinuxNormalizer();
		}

		this.threads = new ArrayList<>();
		int[] normalizedValues = normalizer.normalize(priority, true);
		this.policy = normalizedValues[0];
		this.pid = pid;
		this.priority = normalizedValues[1];
		this.affinity = affinity;
	}

	/**
	 * 
	 * @param policy
	 */
	public void setPolicy(int policy) {
		this.policy = policy;
	}

	/**
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.pName = name;
	}
	
	/**
	 * 
	 * @param pid
	 */
	public void setId(int pid)
	{
		this.pid = pid;
	}

	/**
	 * 
	 * @param priority
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * 
	 * @param affinity
	 */
	public void setAffinity(int[] affinity) {
		this.affinity = new int[affinity.length];
		for (int i = 0; i < affinity.length; i++) {
			this.affinity[i] = affinity[i];
		}
	}

	/**
	 * 
	 * @param thread
	 */
	public void addThread(ThreadData thread) {
		this.threads.add(thread);
	}

	/**
	 * 
	 * @return
	 */
	public int getId() {
		return this.pid;
	}

	/**
	 * 
	 * @return
	 */
	public int getPriority() {
		return this.priority;
	}

	/**
	 * 
	 * @return
	 */
	public int[] getAffinity() {
		return this.affinity;
	}

	/**
	 * 
	 * @return
	 */
	public String getName() {
		return this.pName;
	}

	/**
	 * 
	 * @return
	 */
	public int getPolicy() {
		return this.policy;
	}

	/**
	 * 
	 * @return
	 */
	public List<ThreadData> getThreads() {
		return this.threads;
	}
}
