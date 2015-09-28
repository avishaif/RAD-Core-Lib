package javathreadshandlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class JavaThreadHandler {
	static ThreadGroup rootThreadGroup = null;
	static Logger log = LogManager.getRootLogger();

	public abstract List<Integer> getAllJvmsPids(); 

	/**
	 * Retrieve all threads on hosting jvm
	 * 
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
	 * 
	 * @return ThreadGroup
	 */

	public ThreadGroup getRootThreadGroup() {
		if (rootThreadGroup != null)
			return rootThreadGroup;
		ThreadGroup tg = Thread.currentThread().getThreadGroup();
		ThreadGroup ptg;
		while ((ptg = tg.getParent()) != null)
			tg = ptg;
		return tg;
	}

	/**
	 * Retrieve a native thread id of a java using a thread name.<br>
	 * Function will return the first thread nid with given name found.
	 * 
	 * @param tName
	 *            String: Thread name
	 * @return An Integer number of the native thread id. <br>
	 *         -1 is returned if thread was not found.
	 * @throws IOException
	 */
	public int getNativeThreadId(String tName) {
		List<Integer> jvm = getAllJvmsPids();
		int tid;
		for (Integer ids : jvm) {
			Process p = null;
			try {
				p = Runtime.getRuntime().exec("jstack " + ids);
			} catch (IOException e) {

			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line;
			try {
				while ((line = reader.readLine()) != null) {
					if (line.startsWith('"' + tName + '"')) {
						int index = line.indexOf("nid=0x");
						String nid = line.substring(index + 6);
						nid = nid.substring(0, nid.indexOf(" "));
						tid = (int) Long.parseLong(nid, 16);
						return tid;
					}
				}
			} catch (NumberFormatException | IOException e) {
				e.printStackTrace();
			}
		}

		return -1;
	}

	/**
	 * Retrieve a native thread id of a java using a thread id. <br>
	 * Function applies *only* for threads of the hosting JMV.
	 * 
	 * @param tid
	 *            Integer: Thread id
	 * @return An Integer number of the native thread id. <br>
	 *         -1 is returned if thread was not found.
	 */

	public int getNativeThreadId(int tid) {
		String tName = getThreadName(tid);
		if (tName.equals("not found")) {
			if (log.isErrorEnabled()) {
				log.error("Thread with ID " + tid + " not found.");
			}

			return -1;
		}

		return getNativeThreadId(tName);
	}

	/**
	 * 
	 * @param pid
	 *            Int Process id
	 * @param tid
	 *            Int Java Thread id
	 * @return Int Native Thread id
	 */
	public int getNativeThreadIdByProcess(int pid, int tid) {
		String tName = getThreadName(tid);
		if (tName.equals("not found")) {
			if (log.isErrorEnabled()) {
				log.error("Thread with ID " + tid + " not found.");
			}

			return -1;
		}

		return getNativeThreadId(tName, pid);
	}

	/**
	 * function applicable *only* for threads running on hosting jvm.
	 * 
	 * @param tName
	 *            String Thread name.
	 * @param pid
	 *            int Process id
	 * @return int Thread native id.
	 */
	public int getNativeThreadId(String tName, int pid) {

		int tid;
		Process p = null;
		try {
			p = Runtime.getRuntime().exec("jstack " + pid);
		} catch (IOException e) {

		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				if (line.startsWith('"' + tName + '"')) {
					int index = line.indexOf("nid=0x");
					String nid = line.substring(index + 6);
					nid = nid.substring(0, nid.indexOf(" "));
					tid = (int) Long.parseLong(nid, 16);
					return tid;
				}
			}
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}

		return -1;
	}

	/**
	 * @param tid
	 *            int thread id.
	 * @return String thread name or "not found" if fails.
	 */
	public String getThreadName(int tid) {
		Thread[] threads = getAllThreads();
		for (Thread thread : threads) {
			if (thread.getId() == tid)
				return thread.getName();
		}

		return "not found";
	}

}
