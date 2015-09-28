package handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LinuxSerivceClass {

	private Logger log;
	private int processorCount;

	public LinuxSerivceClass(int processorCount) {
		super();
		this.log = LogManager.getRootLogger();
		this.processorCount = processorCount;
	}

	public boolean checkParams(int[] affinity, int id, String name) {
		if (id != -1) {
			if (id < 0) {
				if (log.isErrorEnabled()) {
					log.error("invalid id: '" + id + "'");
				}
				return false;
			}
		} else if (id == -1) {
			if (name == null) {
				if (log.isErrorEnabled()) {
					log.error("Name is null");
				}
				return false;
			}
			if (name.equals("")) {
				if (log.isErrorEnabled()) {
					log.error("Name is empty");
				}

				return false;
			}
		} else if (affinity == null) {
			if (log.isErrorEnabled()) {
				log.error("affinity mask is null");
			}
			return false;
		} else if (affinity.length < 1) {
			if (log.isErrorEnabled()) {
				log.error("affinity mask is empty");
			}
			return false;
		} else if (affinity.length > processorCount) {
			if (log.isErrorEnabled()) {
				log.error("invalid affinity mask");
			}
			return false;
		}
		return true;
	}

	public boolean checkParams(int id, String name, int policy, int priority) {
		if (id != -1) {
			if (id < 0) {
				if (log.isErrorEnabled()) {
					log.error("invalid id: '" + id + "'");
				}
				return false;
			}
		} else if (id == -1) {
			if (name == null) {
				if (log.isErrorEnabled()) {
					log.error("Name is null");
				}
				return false;
			}
			if (name.equals("")) {
				if (log.isErrorEnabled()) {
					log.error("Name is empty");
				}

				return false;
			}
		}

		else if (policy < 0 || policy > 2) {
			if (log.isErrorEnabled()) {
				log.error("invalid policy value: '" + policy + "'");

			}
			return false;
		} else if ((policy == 0 && priority < -20 || priority > 19)
				|| (policy != 0 && priority < 1 || priority > 99)) {
			if (log.isErrorEnabled()) {
				log.error("invalid priority value: '" + priority + "'");

			}

			return false;
		}

		return true;
	}

	/**
	 * finds process id by the process name, returns -1 if fails;
	 * 
	 * @param pName
	 *            process name
	 * @return true if successful
	 * @throws IOException
	 * 
	 */

	public int getPid(String pName) throws IOException {
		int pid;
		BufferedReader br = null;
		String line;
		File file = new File("/proc");
		String[] processes = file.list((File current, String name) -> new File(
				current, name).isDirectory());
		for (int i = 0; i < processes.length; i++) {
			if (processes[i].matches("[0-9]+")) {
				File process = new File("/proc/" + processes[i] + "/status");
				try {
					br = new BufferedReader(new FileReader(process));
					line = br.readLine();
					String[] words = line.split(":\t");
					if (words[1].equals(pName)) {
						while ((line = br.readLine()) != null) {
							words = line.split(":\t");
							if (words[0].equals("Pid")) {
								pid = Integer.parseInt(words[1]);
								br.close();
								return pid;
							}
						}
					}
				} catch (FileNotFoundException e) {
					if (log.isErrorEnabled()) {
						log.error(e);
					}
					br.close();
					return -1;

				} catch (IOException e) {
					if (log.isErrorEnabled()) {
						log.error(e);
					}
					br.close();
					return -1;
				}
			}
		}
		if (log.isErrorEnabled()) {
			log.error("Process: " + pName + " Not Found");
		}
		br.close();
		return -1;
	}

	/**
	 * 
	 * @param pName
	 *            String process name
	 * @return ArrayList<Integer> containing all process ids of matching
	 *         processes
	 * @throws IOException
	 */
	public List<Integer> getAllPids(String pName) throws IOException {
		List<Integer> pid = new ArrayList<Integer>();
		BufferedReader br = null;
		String line;
		File file = new File("/proc");
		String[] processes = file.list((File current, String name) -> new File(
				current, name).isDirectory());
		for (int i = 0; i < processes.length; i++) {
			if (processes[i].matches("[0-9]+")) {
				File process = new File("/proc/" + processes[i] + "/status");
				try {
					br = new BufferedReader(new FileReader(process));
					line = br.readLine();
					String[] words = line.split(":\t");
					if (words[1].equals(pName)) {
						while ((line = br.readLine()) != null) {
							words = line.split(":\t");
							if (words[0].equals("Pid")) {
								pid.add(Integer.parseInt(words[1]));
							}
						}
					}
				} catch (FileNotFoundException e) {
					if (log.isErrorEnabled()) {
						log.error(e);
					}
					br.close();
					return null;

				} catch (IOException e) {
					if (log.isErrorEnabled()) {
						log.error(e);
					}
					br.close();
					return null;
				}
			}
		}
		br.close();
		return pid;
	}

	/**
	 * find thread id by the parent process id and thread name returns -1 if
	 * fails
	 * 
	 * @param pid
	 *            process id
	 * @param tName
	 *            thread name
	 * @return thread id of type int
	 * @throws IOException
	 */
	//
	public int getTid(int pid, String tName) throws IOException {
		if (pid > 0) {
			int tid;
			BufferedReader br = null;
			String line;
			File file = new File("/proc/" + pid + "/task");
			String[] threads = file
					.list((File current, String name) -> new File(current, name)
							.isDirectory());
			for (int i = 0; i < threads.length; i++) {
				File thread = new File("/proc/" + pid + "/task/" + threads[i]
						+ "/status");
				try {
					br = new BufferedReader(new FileReader(thread));
					line = br.readLine();
					String[] words = line.split(":\t");
					if (words[1].equals(tName)) {
						while ((line = br.readLine()) != null) {
							words = line.split(":\t");
							if (words[0].equals("Pid")) {
								tid = Integer.parseInt(words[1]);
								br.close();
								return tid;
							}
						}
					}
				} catch (FileNotFoundException e) {
					if (log.isErrorEnabled()) {
						log.error(e);
					}
					if (br != null) {
						br.close();
					}
					return -1;

				} catch (IOException e) {
					if (log.isErrorEnabled()) {
						log.error(e);
					}
					if (br != null) {
						br.close();
					}
					return -1;
				}

			}

			if (br != null) {
				br.close();
			}

		}

		return -1;
	}

	/**
	 * find thread id by the thread name returns -1 if fails
	 * 
	 * @param tName
	 *            thread name
	 * @return thread id if found otherwise -1
	 * @throws IOException
	 */

	public int getTid(String tName) throws IOException {
		int tid = 0;
		BufferedReader br = null;
		String line;
		File file = new File("/proc");
		File process;
		String processName;
		String[] processes = file.list((File current, String name) -> new File(
				current, name).isDirectory());
		for (int i = 0; i < processes.length; i++) {
			if (processes[i].matches("[0-9]+")) {
				process = new File("/proc/" + processes[i] + "/task");
				String[] threads = process
						.list((File current, String name) -> new File(current,
								name).isDirectory());
				processName = processes[i];
				for (int j = 0; j < threads.length; j++) {
					try {
						File thread = new File("/proc/" + processName
								+ "/task/" + threads[j] + "/status");
						br = new BufferedReader(new FileReader(thread));
						line = br.readLine();
						String[] words = line.split(":\t");
						if (words[1].equals(tName)) {
							while ((line = br.readLine()) != null) {
								words = line.split(":\t");
								if (words[0].equals("Pid")) {
									tid = Integer.parseInt(words[1]);
									br.close();
									return tid;
								}
							}
						}
					} catch (FileNotFoundException e) {
						if (log.isErrorEnabled()) {
							log.error(e);
						}
						if (br != null) {
							br.close();
						}
						return -1;

					} catch (IOException e) {
						if (log.isErrorEnabled()) {
							log.error(e);
						}
						if (br != null) {
							br.close();
						}
						return -1;
					}
				}
			}
		}

		if (br != null) {
			br.close();
		}

		return -1;
	}
}
