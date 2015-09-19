package javathreadshandlers;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.List;



public abstract class JavaThreadHandler 
{
	static ThreadGroup rootThreadGroup = null;
	
	public List<Integer> getAllJvmsPids()
	{
		return null;
	}
	
	public int getNativeThreadId(String tName)
	{
		return 0;
	}
	
	public int getNativeThreadId(int tid)
	{
		return 0;
	}
	

	public String getThreadName(int tid) {
			Thread[] threads = getAllThreads();
			for (Thread thread : threads)
			{
				if (thread.getId() == tid)
					return thread.getName();
			}
			
			return "not found";
		}
	
	
	
	/**
	 * Retrieve all threads on hosting jvm
	 * @return Thread Array: threads
	 */
	
	public Thread[] getAllThreads() {
		final ThreadGroup root = getRootThreadGroup();
		final ThreadMXBean thbean = ManagementFactory.getThreadMXBean();
		int nAlloc = thbean.getThreadCount();
		int n = 0;
		Thread[] threads;
		do {
			nAlloc *= 2;
			threads = new Thread[nAlloc];
			n = root.enumerate(threads, true);
		} while (n == nAlloc);
		return java.util.Arrays.copyOf(threads, n);
	}

	/**
	 * Retrieve the root thread group on hosting jvm
	 * @return ThreadGroup
	 */

	public ThreadGroup getRootThreadGroup() {
		if ( rootThreadGroup != null )
	        return rootThreadGroup;
	    ThreadGroup tg = Thread.currentThread( ).getThreadGroup( );
	    ThreadGroup ptg;
	    while ( (ptg = tg.getParent( )) != null )
	        tg = ptg;
	    return tg;
	}
	
}
