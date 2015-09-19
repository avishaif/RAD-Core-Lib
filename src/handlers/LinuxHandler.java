package handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javathreadshandlers.JavaThreadHandlerLinux;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import core.ProcessData;
import core.Results;
import core.ThreadData;
import os_api.Linux.PALib;

public class LinuxHandler extends Handler {

	private static Logger log;
	private JavaThreadHandlerLinux javaHandler;

	public LinuxHandler() {
		super();
		log = LogManager.getRootLogger();
	}

	/**
	 *
	 * @param pid
	 *            Int process id
	 * @param affinity
	 *            Integer array, containing core or cpus number to use
	 * @return
	 */

	@Override
	public boolean setProcessAffinity(int pid, int[] affinity) {

		if (pid < 0) {
			if (log.isErrorEnabled()) {
				log.error("invalid process id: '" + pid + "'");
			}
			return false;
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
		} else if (affinity.length > getProcessorCount()) {
			if (log.isErrorEnabled()) {
				log.error("invalid affinity mask");
			}
			return false;
		}
		String temp = PALib.INSTANCE.setAffinity(pid, affinity);
		if (temp.equals("")) {
			return true;
		} else {
			if (log.isErrorEnabled()) {
				log.error(temp);
			}
			return false;
		}
	}

	/**
	 *
	 * @param pName
	 *            process name
	 * @param affinity
	 *            integer array, containing core or cpus number to use
	 * @return true if successful
	 */
	@Override
	public boolean setProcessAffinity(String pName, int[] affinity) {
		if (pName.equals("")) {
			if (log.isErrorEnabled()) {
				log.error("invalid process name: '" + pName + "'");
			}
			return false;
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
		} else if (affinity.length > getProcessorCount()) {
			if (log.isErrorEnabled()) {
				log.error("invalid affinity mask");
			}
			return false;
		}
		int pid;
		try {
			pid = getPid(pName);
		} catch (IOException e) {
			if (log.isErrorEnabled()) {
				log.error(e);
			}
			return false;
		}

		String temp = PALib.INSTANCE.setAffinity(pid, affinity);
		if (temp.equals("")) {
			return true;
		} else {
			if (log.isErrorEnabled()) {
				log.error(temp);
			}
			return false;
		}
	}

	/**
	 * sets affinity for a list of processes for each process/thread returns
	 * true if set successfuly
	 * 
	 * @param processes
	 *            a collection of ProcessData type
	 * @return Array of type boolean the size of processes
	 * 
	 */
	@Override
	public List<Results> setProcessAffinity(Collection<ProcessData> processes) {
		List<Results> results = null;
		if (processes != null && !processes.isEmpty()) {
			results = new ArrayList<>();
			for (ProcessData process : processes) {
				Results result;
				if (process.getName().equals("none")) {
					result = new Results(process.getId(), setProcessAffinity(
							process.getId(), process.getAffinity()));
				} else
					result = new Results(process.getName(), setProcessAffinity(
							process.getName(), process.getAffinity()));

				if (process.getThreads().size() > 0) {
					for (ThreadData thread : process.getThreads()) {
						if (thread.getName().equals("none")) {
							result.addThread(
									thread.getId(),
									setNativeThreadAffinity(thread.getId(),
											thread.getAffinity()));
						} else

							result.addThread(
									thread.getName(),
									setNativeThreadAffinity(thread.getName(),
											thread.getAffinity()));

					}

				}

				results.add(result);
			}
		}

		return results;
	}

	/**
	 *
	 * @param tid
	 *            thread id of type integer
	 * @param affinity
	 *            integer array who stores core or cpu number for use
	 * @return boolean true if successful
	 * 
	 */
	@Override
	public boolean setNativeThreadAffinity(int tid, int[] affinity) {
		if (tid <= 0) {
			if (log.isErrorEnabled()) {
				log.error("invalid thread id: '" + tid + "'");
			}
			return false;
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
		} else if (affinity.length > getProcessorCount()) {
			if (log.isErrorEnabled()) {
				log.error("invalid affinity mask");
			}
			return false;
		}
		String temp = PALib.INSTANCE.setAffinity(tid, affinity);
		if (temp.equals("")) {
			return true;
		} else {
			if (log.isErrorEnabled()) {
				log.error(temp);
			}
			return false;
		}
	}

	/**
	 *
	 * @param tName
	 *            thread name
	 * @param affinity
	 *            integer array
	 * @return true if set successfuly
	 */
	@Override
	public boolean setNativeThreadAffinity(String tName, int[] affinity) {
		if (tName.equals("")) {
			if (log.isErrorEnabled()) {
				log.error("invalid thread name: '" + tName + "'");
			}
			return false;
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
		} else if (affinity.length > getProcessorCount()) {
			if (log.isErrorEnabled()) {
				log.error("invalid affinity mask");
			}
			return false;
		}
		int tid;
		try {
			tid = getTid(tName);
		} catch (IOException e) {
			if (log.isErrorEnabled()) {
				log.error(e);
			}
			return false;
		}

		String temp = PALib.INSTANCE.setAffinity(tid, affinity);
		if (temp.equals("")) {
			return true;
		} else {
			if (log.isErrorEnabled()) {
				log.error(temp);
			}
			return false;
		}
	}

	@Override
	public boolean setNativeThreadAffinity(String pName, String tName,
			int[] affinity) {
		int tid = 0;
		try {
			for (int pid : getAllPids(pName)) {
				tid = getTid(pid, tName);
				if (tid != -1)
					break;
			}

		} catch (IOException e) {
			if (log.isDebugEnabled())
				log.debug(e);
			e.printStackTrace();
			return false;
		}

		return setNativeThreadAffinity(tid, affinity);
	}

	/**
	 * 
	 * @param threads
	 *            collection of type ThreadData
	 * @return Array of type boolean the size of threads collection for each
	 *         process/thread true is set if set successfuly
	 * 
	 */
	@Override
	public List<Results> setNativeThreadAffinity(Collection<ThreadData> threads) {
		List<Results> results = null;
		if (threads != null && !threads.isEmpty()) {
			results = new ArrayList<>();
			for (ThreadData thread : threads) {
				Results result;
				if (thread.getName().equals("none")) {
					if (thread.isJavaThread())
						result = new Results(thread.getId(),
								setJavaThreadAffinity(thread.getId(),
										thread.getAffinity()));
					else
						result = new Results(thread.getId(),
								setNativeThreadAffinity(thread.getId(),
										thread.getAffinity()));

				} else if (thread.isJavaThread())
					result = new Results(thread.getName(),
							setJavaThreadAffinity(thread.getName(),
									thread.getAffinity()));
				else
					result = new Results(thread.getName(),
							setNativeThreadAffinity(thread.getName(),
									thread.getAffinity()));

				results.add(result);
			}

		}

		return results;
	}

	@Override
	public boolean setJavaThreadAffinity(int tid, int[] affinity) {
		javaHandler = new JavaThreadHandlerLinux();
		return setNativeThreadAffinity(javaHandler.getNativeThreadId(tid),
				affinity);
	}

	@Override
	public boolean setJavaThreadAffinity(String tName, int[] affinity) {
		javaHandler = new JavaThreadHandlerLinux();
		return setNativeThreadAffinity(javaHandler.getNativeThreadId(tName),
				affinity);
	}

	@Override
	public boolean setJavaThreadAffinity(String pName, String tName,
			int[] affinity) {
		javaHandler = new JavaThreadHandlerLinux();
		return setNativeThreadAffinity(javaHandler.getNativeThreadId(tName),
				affinity);
	}

	@Override
	public boolean setJavaThreadAffinity(int pid, int tid, int[] affinity) {
		javaHandler = new JavaThreadHandlerLinux();
		return setNativeThreadAffinity(
				javaHandler.getNativeThreadIdByProcess(pid, tid), affinity);
	}

	/**
	 *
	 * @param pid
	 *            process id
	 * @param priority
	 *            priority value to be set
	 * @return
	 */

	public boolean setProcessPriority(int pid, int policy, int priority) {
		if (pid < 0) {
			if (log.isErrorEnabled()) {
				log.error("invalid process id: '" + pid + "'");
			}
			return false;
		} else if (policy < 0 || policy > 2) {
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
		String temp = PALib.INSTANCE.setPriority(pid, policy, priority);
		if (temp.equals("")) {
			return true;
		} else {
			if (log.isErrorEnabled()) {
				log.error(temp);

			}
			return false;
		}
	}

	/**
	 *
	 * @param pName
	 *            process name
	 * @param priority
	 *            priority value to be set
	 * @return
	 */
	public boolean setProcessPriority(String pName, int policy, int priority) {
		if (pName.equals("")) {
			if (log.isErrorEnabled()) {
				log.error("invalid process name: '" + pName + "'");
			}
			return false;
		} else if (policy < 0 || policy > 2) {
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
		int pid = 0;
		try {
			pid = getPid(pName);
		} catch (IOException e) {
			if (log.isErrorEnabled()) {
				log.error(e);
			}
			return false;
		}
		String temp = PALib.INSTANCE.setPriority(pid, policy, priority);
		if (temp.equals("")) {
			return true;
		} else {
			if (log.isErrorEnabled()) {
				log.error(temp);
			}
			return false;
		}
	}

	/**
	 * 
	 * @param processes
	 *            a collection of type ProcessData
	 * @return return an Array of type boolean foreach process/thread true if
	 *         set successfuly
	 * 
	 */
	@Override
	public List<Results> setProcessPriority(Collection<ProcessData> processes) {

		List<Results> results = null;
		if (processes != null && !processes.isEmpty()) {
			results = new ArrayList<>();
			for (ProcessData process : processes) {
				Results result;
				if (process.getName().equals("none")) {
					result = new Results(process.getId(), setProcessPriority(
							process.getId(), process.getPolicy(),
							process.getPriority()));

				} else
					result = new Results(process.getName(), setProcessPriority(
							process.getName(), process.getPolicy(),
							process.getPriority()));

				if (process.getThreads().size() > 0) {
					for (ThreadData thread : process.getThreads()) {
						if (thread.getName().equals("none")) {
							result.addThread(
									thread.getId(),
									setNativeThreadPriority(thread.getId(),
											thread.getPolicy(),
											thread.getPriority()));

						} else
							result.addThread(
									thread.getName(),
									setNativeThreadPriority(thread.getName(),
											thread.getPolicy(),
											thread.getPriority()));
					}

				}

				results.add(result);
			}
		}

		return results;
	}

	/**
	 *
	 * @param tid
	 *            thread name
	 * @param priority
	 * @return true if set successfuly
	 */
	public boolean setNativeThreadPriority(int tid, int policy, int priority) {
		if (tid < 0) {
			if (log.isErrorEnabled()) {
				log.error("invalid thread id: '" + tid + "'");
			}
			return false;
		} else if (policy < 0 || policy > 2) {
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
		String temp = PALib.INSTANCE.setPriority(tid, policy, priority);
		if (temp.equals("")) {
			return true;
		} else {
			if (log.isErrorEnabled()) {
				log.error(temp);
				return false;
			}

			return false;
		}
	}

	/**
	 *
	 * @param tName
	 *            thread name
	 * @param priority
	 *            priority value to be set
	 * @return true if set successfuly
	 */
	public boolean setNativeThreadPriority(String tName, int policy,
			int priority) {
		if (tName.equals("")) {
			if (log.isErrorEnabled()) {
				log.error("invalid thread name: '" + tName + "'");
			}
			return false;
		} else if (policy < 0 || policy > 2) {
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
		int tid = 0;
		try {
			tid = getTid(tName);
		} catch (IOException e) {
			if (log.isErrorEnabled()) {
				log.error(e);
			}
			return false;
		}

		String temp = PALib.INSTANCE.setPriority(tid, policy, priority);
		if (temp.equals("")) {
			return true;
		} else {
			if (log.isErrorEnabled()) {
				log.error(temp);
			}
			return false;
		}
	}

	@Override
	public boolean setNativeThreadPriority(String pName, String tName,
			int policy, int priority) {
		int pid = 0;
		int tid = 0;
		try {
			pid = getPid(pName);
			tid = getTid(pid, tName);
		} catch (IOException e) {
			if (log.isDebugEnabled())
				log.debug(e);
			e.printStackTrace();
			return false;
		}

		return setNativeThreadPriority(tid, policy, priority);
	}

	@Override
	public boolean setNativeThreadPriority(int pid, String tName, int policy,
			int priority) {
		int tid = 0;
		try {
			tid = getTid(pid, tName);
		} catch (IOException e) {
			if (log.isDebugEnabled())
				log.debug(e);
			e.printStackTrace();
			return false;
		}

		return setNativeThreadPriority(tid, policy, priority);
	}

	/**
	 * set priority for a list of threads
	 * 
	 * @param threads
	 *            a collection of ThreadData type
	 * @return return an Array of type boolean foreach thread true if set
	 *         successfuly
	 */
	@Override
	public List<Results> setNativeThreadPriority(Collection<ThreadData> threads) {
		List<Results> results = null;
		if (threads != null && !threads.isEmpty()) {
			results = new ArrayList<>();
			for (ThreadData thread : threads) {
				Results result = null;
				if (thread.getName().equals("none")) {
					if (thread.isJavaThread())
						result = new Results(thread.getId(),
								setJavaThreadPriority(thread.getId(),
										thread.getPolicy(),
										thread.getPriority()));
					else
						result = new Results(thread.getId(),
								setNativeThreadPriority(thread.getId(),
										thread.getPolicy(),
										thread.getPriority()));

				} else {
					if (thread.isJavaThread())
						result = new Results(thread.getName(),
								setJavaThreadPriority(thread.getName(),
										thread.getPolicy(),
										thread.getPriority()));
					else
						result = new Results(thread.getName(),
								setJavaThreadPriority(thread.getName(),
										thread.getPolicy(),
										thread.getPriority()));
				}

				results.add(result);
			}
		}
		return results;
	}

	@Override
	public boolean setJavaThreadPriority(int tid, int policy, int priority) {
		javaHandler = new JavaThreadHandlerLinux();
		int nTid = javaHandler.getNativeThreadId(tid);
		return setNativeThreadPriority(nTid, policy, priority);
	}

	@Override
	public boolean setJavaThreadPriority(String tName, int policy, int priority) {
		javaHandler = new JavaThreadHandlerLinux();
		int tid = javaHandler.getNativeThreadId(tName);
		if (-1 == tid) {
			if (log.isErrorEnabled()) {
				log.error("SetPriority failed, thread " + tName + "not found");
			}
			return false;
		}

		return setNativeThreadPriority(tid, policy, priority);

	}

	@Override
	public boolean setJavaThreadPriority(int pid, int tid, int policy,
			int priority) {
		javaHandler = new JavaThreadHandlerLinux();
		int nTid = javaHandler.getNativeThreadIdByProcess(pid, tid);
		return setNativeThreadPriority(nTid, policy, priority);
	}

	@Override
	public boolean setJavaThreadPriority(String pName, String tName,
			int policy, int priority) {
		javaHandler = new JavaThreadHandlerLinux();
		int tid = javaHandler.getNativeThreadId(tName);
		return setNativeThreadPriority(tid, policy, priority);
	}

	@Override
	public boolean setJavaThreadPriority(int pid, String tName, int policy,
			int priority) {
		javaHandler = new JavaThreadHandlerLinux();
		int tid = javaHandler.getNativeThreadId(tName, pid);
		return setNativeThreadPriority(tid, policy, priority);
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

	/**
	 * gets the available core/cpu count
	 * 
	 * @return number of available cores/cpu's
	 */
	@Override
	public int getProcessorCount() {
		return PALib.INSTANCE.getProcessorCount();
	}
}
