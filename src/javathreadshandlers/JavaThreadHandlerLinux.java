package javathreadshandlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JavaThreadHandlerLinux extends JavaThreadHandler {

	Logger log = LogManager.getRootLogger();

	/**
	 * Retrieve all running jvms process ids
	 * 
	 * @return Integer List: containing all active jvms process ids.
	 */
	@Override
	public List<Integer> getAllJvmsPids() {
		List<Integer> jvm = new ArrayList<>();
		BufferedReader br = null;
		String line;
		File file = new File("/proc");
		String[] processes = file.list((File current, String name) -> new File(current, name).isDirectory());
		for (int i = 0; i < processes.length; i++) {
			if (processes[i].matches("[0-9]+")) {
				File process = new File("/proc/" + processes[i] + "/status");
				try {
					br = new BufferedReader(new FileReader(process));
					line = br.readLine();
					String[] words = line.split(":\t");
					if (words[1].equals("java")) {
						while ((line = br.readLine()) != null) {
							words = line.split(":\t");
							if (words[0].equals("Pid")) {
								jvm.add(Integer.parseInt(words[1]));
								br.close();
								break;
							}
						}
					} else {
						br.close();
						continue;
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					continue;
				} catch (IOException e) {
					e.printStackTrace();
					if (log.isErrorEnabled()) {
						log.error(e);
					}
				}
			}
		}
		if (jvm.size() == 0 && log.isErrorEnabled()) {
			log.error("No jvm was found");
			return null;
		} else {
			return jvm;
		}
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
		if (tName == null) {
			if (log.isErrorEnabled()) {
				log.error("Thread with ID " + tid + " not found.");
			}

			return -1;
		}

		return getNativeThreadId(tName);
	}
}