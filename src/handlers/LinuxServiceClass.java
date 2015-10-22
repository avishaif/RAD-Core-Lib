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

import core.Constants;

public class LinuxServiceClass {

	private Logger log;

	public LinuxServiceClass(int processorCount) {
		super();
		this.log = LogManager.getRootLogger();
	}

	/**
	 * Finds process ID by the process name.
	 * @param pName
	 *            Process name.
	 * @return
	 * 		If the operation succeeded, process ID will be returned, if fails, -1 will be returned.
	 * @throws IOException
	 * 
	 */

	public int getPid(String pName) throws IOException {
		int pid;
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
				} finally {
					try {
						if (br != null)
							br.close();
					} catch (Exception e) {
						if (log.isErrorEnabled()) {
							log.error(e);
						}
						return -1;
					}
				}
			}
		}
		if (log.isErrorEnabled()) {
			log.error(Constants.ERR_PRO_NOT_FOUND + pName);
		}
		br.close();
		return -1;
	}

	/**
	 * Gets the name of the process with specified ID. 
	 * @param pid
	 * 			Process ID.
	 * @return
	 * 		If the operation succeeded process name returned.
	 * @throws IOException
	 */
	public String getPname(int pid) throws IOException {
		BufferedReader br = null;
		String line = null;
		String[] word;
		File file = new File("/proc/" + pid + "/status");
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			if (log.isErrorEnabled())
				log.error(e);
			return null;
		}
		try {
			line = br.readLine();
		} catch (IOException e) {
			if (log.isErrorEnabled())
				log.error(e);
			br.close();
			return null;
		} finally {
			if (br != null)
				br.close();
		}
		word = line.split(":\t");
		return word[1];
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
		String[] processes = file.list((File current, String name) -> new File(current, name).isDirectory());
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
								br.close();
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
				} finally {
					try {
						if (br != null)
							br.close();
					} catch (Exception e) {
						return null;
					}
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
			String[] threads = file.list((File current, String name) -> new File(current, name).isDirectory());
			for (int i = 0; i < threads.length; i++) {
				File thread = new File("/proc/" + pid + "/task/" + threads[i] + "/status");
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
				} finally {
					try {
						if (br != null)
							br.close();
					} catch (Exception e) {
						return -1;
					}
				}

			}
			if (br != null) {
				br.close();
			}
			if (log.isErrorEnabled()) {
				log.error(Constants.ERR_THR_NOT_FOUND + tName);
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
		String[] processes = file.list((File current, String name) -> new File(current, name).isDirectory());
		for (int i = 0; i < processes.length; i++) {
			if (processes[i].matches("[0-9]+")) {
				process = new File("/proc/" + processes[i] + "/task");
				String[] threads = process.list((File current, String name) -> new File(current, name).isDirectory());
				processName = processes[i];
				for (int j = 0; j < threads.length; j++) {
					try {
						File thread = new File("/proc/" + processName + "/task/" + threads[j] + "/status");
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
					} finally {
						try {
							if (br != null)
								br.close();
						} catch (Exception e) {
							return -1;
						}
					}
				}
			}
		}

		if (br != null) {
			br.close();
		}

		return -1;
	}

	public String getTname(int tid) throws IOException {
		BufferedReader br = null;
		String line;
		File file = new File("/proc");
		String processName;
		String[] processes = file.list((File current, String name) -> new File(current, name).isDirectory());
		for (int i = 0; i < processes.length; i++) {
			if (processes[i].matches("[0-9]+")) {
				processName = processes[i];
				File thread = new File("/proc/" + processName + "/task/" + tid + "/status");
				try {
					br = new BufferedReader(new FileReader(thread));
				} catch (FileNotFoundException e) {
					if (br != null)
						br.close();
					continue;
				}
				line = br.readLine();
				String[] words = line.split(":\t");
				br.close();
				return words[1];
			}
		}
		
		return null;
	}
}
