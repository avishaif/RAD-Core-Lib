package handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javathreadshandlers.JavaThreadHandlerLinux;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cache.Cache;
import core.ProcessData;
import core.Results;
import core.ThreadData;
import os_api.Linux.PALib;

public class LinuxHandler extends Handler {

	private static Logger log;
	private JavaThreadHandlerLinux javaHandler;
	private LinuxSerivceClass service;
	private int processorCount;

	public LinuxHandler() {
		this.cache = new ArrayList<>();
		log = LogManager.getRootLogger();
		this.processorCount = getProcessorCount();
		this.service = new LinuxSerivceClass(processorCount);
	}

	private boolean setProcessAffinity(int pid, int[] affinity, boolean checked) {
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

	@Override
	public boolean setProcessAffinity(int pid, int[] affinity) {
		if (service.checkParams(affinity, pid, null)) {
			Cache c = checkCache(pid, null, -1, -1, affinity);
			if (c != null) {
				if (c.getAffinity() != null)
					Arrays.sort(c.getAffinity());
				Arrays.sort(affinity);
				if (Arrays.equals(c.getAffinity(), affinity))
					return true;
				else {
					if (setProcessAffinity(pid, affinity, true)) {
						c.setAffinity(affinity);
						return true;
					}
					return false;
				}
			} else
				return setProcessAffinity(pid, affinity, true);
		}
		return false;
	}

	@Override
	public boolean setProcessAffinity(String pName, int[] affinity) {
		if (service.checkParams(affinity, -1, pName)) {
			Cache c = checkCache(-1, pName, -1, -1, affinity);
			if (c != null) {
				Arrays.sort(c.getAffinity());
				Arrays.sort(affinity);
				if (Arrays.equals(c.getAffinity(), affinity))
					return true;
				else {
					if (setProcessAffinity(c.getId(), affinity, true)) {
						c.setAffinity(affinity);
						return true;
					}
					return false;
				}
			} else {
				int pid;
				try {
					pid = service.getPid(pName);
				} catch (IOException e) {
					if (log.isErrorEnabled()) {
						log.error(e);
					}

					return false;
				}

				this.cache.add(new Cache(pid, pName, affinity, -1, -1));
				return setProcessAffinity(pid, affinity, true);

			}
		}
		return false;
	}

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

	private boolean setThreadAffinity(int tid, int[] affinity, boolean checked) {
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
	public boolean setNativeThreadAffinity(int tid, int[] affinity) {
		if (service.checkParams(affinity, tid, null)) {
			Cache c = checkCache(tid, null, -1, -1, affinity);
			if (c != null) {
				Arrays.sort(c.getAffinity());
				Arrays.sort(affinity);
				if (Arrays.equals(c.getAffinity(), affinity))
					return true;
				else {
					if (setThreadAffinity(c.getId(), affinity, true)) {
						c.setAffinity(affinity);
						return true;
					}
					return false;
				}
			} else
				return setThreadAffinity(tid, affinity, true);
		}
		return false;
	}

	@Override
	public boolean setNativeThreadAffinity(String tName, int[] affinity) {
		if (service.checkParams(affinity, -1, tName)) {
			Cache c = checkCache(-1, tName, -1, -1, affinity);
			if (c != null) {
				Arrays.sort(c.getAffinity());
				Arrays.sort(affinity);
				if (Arrays.equals(c.getAffinity(), affinity))
					return true;
				else {
					if (setThreadAffinity(c.getId(), affinity, true)) {
						c.setAffinity(affinity);
						return true;
					}
					return false;
				}
			} else {
				int tid;
				try {
					tid = service.getTid(tName);
				} catch (IOException e) {
					if (log.isErrorEnabled()) {
						log.error(e);
					}
					return false;
				}
				this.cache.add(new Cache(tid, tName, affinity, -1, -1));
				return setThreadAffinity(tid, affinity, true);
			}
		}
		return false;
	}

	@Override
	public boolean setNativeThreadAffinity(String pName, String tName,
			int[] affinity) {
		if (service.checkParams(affinity, -1, tName)) {
			Cache c = checkCache(-1, tName, -1, -1, affinity);
			if (c != null) {
				Arrays.sort(c.getAffinity());
				Arrays.sort(affinity);
				if (Arrays.equals(c.getAffinity(), affinity))
					return true;
				else {
					if (setThreadAffinity(c.getId(), affinity, true)) {
						c.setAffinity(affinity);
						return true;
					}
				}
			} else {
				int tid = 0;
				try {
					for (int pid : service.getAllPids(pName)) {
						tid = service.getTid(pid, tName);
						if (tid != -1)
							break;
					}
				} catch (IOException e) {
					if (log.isDebugEnabled())
						log.debug(e);
					e.printStackTrace();
					return false;
				}
				this.cache.add(new Cache(tid, tName, affinity, -1, -1));
				return setThreadAffinity(tid, affinity, true);
			}
		}
		return false;
	}

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
		String tName = javaHandler.getThreadName(tid);
		if (service.checkParams(affinity, tid, null)) {
			Cache c = checkCache(-1, tName, -1, -1, affinity);
			if (c != null) {
				Arrays.sort(c.getAffinity());
				Arrays.sort(affinity);
				if (Arrays.equals(c.getAffinity(), affinity))
					return true;
				else {
					if (setThreadAffinity(c.getId(), affinity, true)) {
						c.setAffinity(affinity);
						return true;
					}
					return false;
				}
			} else {
				int id = javaHandler.getNativeThreadId(tid);
				this.cache.add(new Cache(id, tName, affinity, -1, -1));
				return setThreadAffinity(id, affinity, true);
			}
		}
		return false;
	}

	@Override
	public boolean setJavaThreadAffinity(String tName, int[] affinity) {
		if (service.checkParams(affinity, -1, tName)) {
			Cache c = checkCache(-1, tName, -1, -1, affinity);
			if (c != null) {
				Arrays.sort(c.getAffinity());
				Arrays.sort(affinity);
				if (Arrays.equals(c.getAffinity(), affinity))
					return true;
				else {
					if (setThreadAffinity(c.getId(), affinity, true)) {
						c.setAffinity(affinity);
						return true;
					}
					return false;
				}
			} else {
				javaHandler = new JavaThreadHandlerLinux();
				int id = javaHandler.getNativeThreadId(tName);
				this.cache.add(new Cache(id, tName, affinity, -1, -1));
				return setThreadAffinity(id, affinity, true);
			}
		}
		return false;
	}

	@Override
	public boolean setJavaThreadAffinity(String pName, String tName,
			int[] affinity) {
		if (service.checkParams(affinity, -1, tName)) {
			Cache c = checkCache(-1, tName, -1, -1, affinity);
			if (c != null) {
				Arrays.sort(c.getAffinity());
				Arrays.sort(affinity);
				if (Arrays.equals(c.getAffinity(), affinity))
					return true;
				else {
					if (setThreadAffinity(c.getId(), affinity, true)) {
						c.setAffinity(affinity);
						return true;
					}
					return false;
				}
			} else {
				int id = 0;
				try {
					javaHandler = new JavaThreadHandlerLinux();
					id = javaHandler.getNativeThreadId(tName,
							service.getPid(pName));
				} catch (IOException e) {
					if (log.isDebugEnabled())
						log.debug(e);
					e.printStackTrace();
					return false;
				}
				this.cache.add(new Cache(id, tName, affinity, -1, -1));
				return setThreadAffinity(id, affinity, true);
			}
		}
		return false;
	}

	private boolean setProcessPriority(int pid, int policy, int priority,
			boolean checked) {
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

	@Override
	public boolean setProcessPriority(int pid, int policy, int priority) {
		if (service.checkParams(pid, null, policy, priority)) {
			Cache c = checkCache(pid, null, priority, policy, null);
			if (c != null)
				if (c.getPolicy() == policy && c.getPriority() == priority)
					return true;
				else {
					if (setProcessPriority(c.getId(), policy, priority, true)) {
						c.setPolicy(policy);
						c.setPriority(priority);
						return true;
					}
					return false;
				}
			else {
				this.cache.add(new Cache(pid, null, null, priority, policy));
				return setProcessPriority(pid, policy, priority, true);
			}
		}
		return false;
	}

	@Override
	public boolean setProcessPriority(String pName, int policy, int priority) {
		if (service.checkParams(-1, pName, policy, priority)) {
			Cache c = checkCache(-1, pName, priority, policy, null);
			if (c != null)
				if (c.getPriority() == priority && c.getPolicy() == policy)
					return true;
				else {
					if (setProcessPriority(c.getId(), policy, priority, true)) {
						c.setPolicy(policy);
						c.setPriority(priority);
						return true;
					}
					return false;
				}
			else {
				int pid = 0;
				try {
					pid = service.getPid(pName);
				} catch (IOException e) {
					if (log.isErrorEnabled()) {
						log.error(e);
					}
					return false;
				}
				this.cache.add(new Cache(pid, pName, null, priority, policy));
				return setProcessPriority(pid, policy, priority, true);
			}
		}
		return false;
	}

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

	private boolean setThreadPriority(int tid, int policy, int priority,
			boolean checked) {
		String temp = PALib.INSTANCE.setPriority(tid, policy, priority);
		if (temp.equals("")) {
			return true;
		} else {
			if (log.isErrorEnabled())
				log.error(temp);
			return false;
		}
	}

	@Override
	public boolean setNativeThreadPriority(int tid, int policy, int priority) {
		if (service.checkParams(tid, null, policy, priority)) {
			Cache c = checkCache(tid, null, priority, policy, null);
			if (c != null)
				if (c.getPolicy() == policy && c.getPriority() == priority)
					return true;
				else {
					if (setThreadPriority(c.getId(), policy, priority, true)) {
						c.setPolicy(policy);
						c.setPriority(priority);
						return true;
					}
					return false;
				}
			else {
				this.cache.add(new Cache(tid, null, null, priority, policy));
				return setThreadPriority(tid, policy, priority, true);
			}
		}
		return false;
	}

	@Override
	public boolean setNativeThreadPriority(String tName, int policy,
			int priority) {
		if (service.checkParams(-1, tName, policy, priority)) {
			Cache c = checkCache(-1, tName, priority, policy, null);
			if (c != null)
				if (c.getPriority() == priority && c.getPolicy() == policy)
					return true;
				else {
					if (setThreadPriority(c.getId(), policy, priority, true)) {
						c.setPolicy(policy);
						c.setPriority(priority);
						return true;
					}
					return false;
				}
			else {
				int tid = 0;
				try {
					tid = service.getTid(tName);
				} catch (IOException e) {
					if (log.isErrorEnabled()) {
						log.error(e);
					}
					return false;
				}
				this.cache.add(new Cache(tid, tName, null, priority, policy));
				return setThreadPriority(tid, policy, priority, true);
			}
		}
		return false;
	}

	@Override
	public boolean setNativeThreadPriority(String pName, String tName,
			int policy, int priority) {
		if (service.checkParams(-1, tName, policy, priority)) {
			Cache c = checkCache(-1, tName, priority, policy, null);
			if (c != null)
				if (c.getPolicy() == policy && c.getPriority() == priority)
					return true;
				else {
					if (setThreadPriority(c.getId(), policy, priority, true)) {
						c.setPolicy(policy);
						c.setPriority(priority);
						return true;
					}
					return false;
				}
			else {
				int pid = 0;
				int tid = 0;
				try {
					pid = service.getPid(pName);
					tid = service.getTid(pid, tName);
				} catch (IOException e) {
					if (log.isDebugEnabled())
						log.debug(e);
					e.printStackTrace();
					return false;
				}
				this.cache.add(new Cache(tid, tName, null, priority, policy));
				return setThreadPriority(tid, policy, priority, true);
			}
		}
		return false;
	}

	@Override
	public boolean setNativeThreadPriority(int pid, String tName, int policy,
			int priority) {
		if (service.checkParams(null, -1, tName)) {
			Cache c = checkCache(0, tName, priority, policy, null);
			if (c != null)
				if (c.getPriority() == priority && c.getPolicy() == policy)
					return true;
				else {
					if (setThreadPriority(c.getId(), policy, priority, true)) {
						c.setPolicy(policy);
						c.setPriority(priority);
						return true;
					}
					return false;
				}
			else {
				int tid = 0;
				try {
					tid = service.getTid(pid, tName);
				} catch (IOException e) {
					if (log.isDebugEnabled())
						log.debug(e);
					e.printStackTrace();
					return false;
				}
				this.cache.add(new Cache(tid, tName, null, priority, policy));
				return setThreadPriority(tid, policy, priority, true);
			}
		}
		return false;
	}

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
		String tName = javaHandler.getThreadName(tid);
		if (service.checkParams(tid, null, policy, priority)) {
			Cache c = checkCache(-1, tName, priority, policy, null);
			if (c != null)
				if (c.getPolicy() == policy && c.getPriority() == priority)
					return true;
				else {
					if (setThreadPriority(c.getId(), policy, priority, true)) {
						c.setPolicy(policy);
						c.setPriority(priority);
						return true;
					}
				}
			else {
				int nTid = javaHandler.getNativeThreadId(tid);
				this.cache.add(new Cache(nTid, tName, null, priority, policy));
				return setThreadPriority(nTid, policy, priority, true);
			}
		}
		return false;
	}

	@Override
	public boolean setJavaThreadPriority(String tName, int policy, int priority) {
		if (service.checkParams(-1, tName, policy, priority)) {
			Cache c = checkCache(-1, tName, priority, policy, null);
			if (c != null)
				if (c.getPolicy() == policy && c.getPriority() == priority)
					return true;
				else {
					if (setThreadPriority(c.getId(), policy, priority, true)) {
						c.setPolicy(policy);
						c.setPriority(priority);
						return true;
					}
					return false;
				}
			else {
				javaHandler = new JavaThreadHandlerLinux();
				int tid = javaHandler.getNativeThreadId(tName);
				if (-1 == tid) {
					if (log.isErrorEnabled()) {
						log.error("SetPriority failed, thread " + tName
								+ "not found");
					}
					return false;
				}
				this.cache.add(new Cache(tid, tName, null, priority, policy));
				return setThreadPriority(tid, policy, priority, true);
			}
		}
		return false;
	}

	@Override
	public boolean setJavaThreadPriority(String pName, String tName,
			int policy, int priority) {
		if (service.checkParams(-1, tName, policy, priority)) {
			Cache c = checkCache(-1, tName, priority, policy, null);
			if (c != null)
				if (c.getPolicy() == policy && c.getPriority() == priority)
					return true;
				else {
					if (setThreadPriority(c.getId(), policy, priority, true)) {
						c.setPolicy(policy);
						c.setPriority(priority);
						return true;
					}
					return false;
				}
			else {
				javaHandler = new JavaThreadHandlerLinux();
				int tid = javaHandler.getNativeThreadId(tName);
				this.cache.add(new Cache(tid, tName, null, priority, policy));
				return setThreadPriority(tid, policy, priority, true);
			}
		}
		return false;
	}

	@Override
	public boolean setJavaThreadPriority(int pid, String tName, int policy,
			int priority) {
		if (service.checkParams(-1, tName, policy, priority)) {
			Cache c = checkCache(-1, tName, priority, policy, null);
			if (c != null)
				if (c.getPolicy() == policy && c.getPriority() == priority)
					return true;
				else {
					if (setThreadPriority(c.getId(), policy, priority, true)) {
						c.setPolicy(policy);
						c.setPriority(priority);
						return true;
					}
					return false;
				}
			else {
				javaHandler = new JavaThreadHandlerLinux();
				int tid = javaHandler.getNativeThreadId(tName, pid);
				this.cache.add(new Cache(tid, tName, null, priority, policy));
				return setThreadPriority(tid, policy, priority, true);
			}
		}
		return false;
	}

	@Override
	public int getProcessorCount() {
		return PALib.INSTANCE.getProcessorCount();
	}

	private Cache checkCache(int id, String name, int priority, int policy,
			int[] affinity) {
		for (Cache c : this.cache) {
			if (c.getId() == id || c.getName().equals(name)) {
				return c;
			}
		}
		return null;
	}

	@Override
	public void clearCache() {
		this.cache.clear();
	}
}
