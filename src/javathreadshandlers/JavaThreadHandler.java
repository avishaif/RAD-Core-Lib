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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return -1;
	}
}