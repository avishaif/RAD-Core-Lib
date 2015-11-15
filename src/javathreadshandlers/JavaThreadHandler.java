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
	Logger log = LogManager.getRootLogger();

	protected abstract List<Integer> getAllJvmsPids();

	/**
	 * Retrieves the native id of a java thread
	 * 
	 * @param tName
	 *            String thread name.
	 * @return int thread id.
	 */
	public int getNativeThreadId(String tName) {
		List<Integer> jvm = getAllJvmsPids();
		BufferedReader reader = null;
		int tid;
		for (Integer ids : jvm) {
			Process p = null;
			try {
				p = Runtime.getRuntime().exec("jstack " + ids);
			} catch (IOException e) {

			}
			if (p != null) {
				reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			} else {
				if (log.isErrorEnabled())
					log.error("An Unknown error accured while trying to activate Jstack");
				return -2;
			}
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
				return -1;
			}
		}
		return -1;
	}

	/**
	 * Retrieves the native id of a java Thread by making the query according to
	 * its containing process.
	 * 
	 * @param tName
	 *            String Thread name.
	 * @param pid
	 *            int Process id.
	 * @return int Thread native id.
	 */

	public int getNativeThreadId(String tName, int pid) {

		int tid;
		Process p = null;
		BufferedReader reader = null;
		try {
			p = Runtime.getRuntime().exec("jstack " + pid);
		} catch (IOException e) {

		}
		if (p != null) {
			reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		} else {
			if (log.isErrorEnabled())
				log.error("An Unknown error accured while trying to activate Jstack");
			return -2;
		}
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
			return -1;
		}

		return -1;
	}

	/**
	 * Retrieves the native id of a java Thread by making the query according to
	 * its containing process.
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
	 * Retrieves a thread name as it is called in the JVM by its id given to it
	 * by the JVM.
	 * @param tid
	 *            int thread id.
	 * @return String thread name, null if not found.
	 */
	public String getThreadName(int tid) {
		Thread[] threads = getAllThreads();
		for (Thread thread : threads) {
			if (thread.getId() == tid)
				return thread.getName();
		}

		return null;
	}

	/**
	 * Retrieve all threads on hosting JVM.
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
	 * Retrieve the root thread group on hosting JVM.
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
}