package handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javathreadshandlers.JavaThreadHandlerLinux;
import normalizers.Normalizer;
import os_api.Linux.PALib;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cache.Cache;
import core.Constants;
import core.ProcessData;
import core.ThreadData;

import results.ProcessResult;
import results.Result;
import validate.Validator;

public class LinuxHandler extends Handler {

	private static Logger log;
	private JavaThreadHandlerLinux javaHandler;
	private LinuxServiceClass service;
	private int processorCount;
	private Normalizer normalizer;

	public LinuxHandler(Normalizer norm) {
		log = LogManager.getRootLogger();
		this.processorCount = getProcessorCount();
		this.service = new LinuxServiceClass(processorCount);
		this.normalizer = norm;
		Validator.setNumberOfCpus(processorCount);
		this.cache = new ArrayList<>();
	}

	@Override
	public void clearCache() {
		if (this.cache != null)
			this.cache.clear();
	}

	@Override
	public Cache checkCache(int id, String name) {
		for (Iterator<Cache> iterator = this.cache.iterator(); iterator.hasNext();) {
			Cache cache = iterator.next();
			if (cache.getName().equals(name)) {
				if (cache.getId() != id) {
					this.cache.remove(cache);
					return null;
				} else if (cache.getId() == id) {
					return cache;
				}
			}
		}

		return null;
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
		if (Validator.checkAffinityParams(pid, affinity)) {
			boolean result = false;

			String pName = null;
			try {
				pName = service.getPname(pid);
				if (pName == null) {
					for (Iterator<Cache> iterator = this.cache.iterator(); iterator.hasNext();) {
						Cache cache = iterator.next();
						if (cache.getId() == pid)
							iterator.remove();
					}
					if (log.isErrorEnabled()) {
						log.error("setProcessAffinity failed to set priority to process " + pid
								+ ". Process not found.");
					}
					return false;
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

			Cache cache = checkCache(pid, pName);
			if (cache != null) {
				if (cache.getAffinity() != null)
					Arrays.sort(cache.getAffinity());
				Arrays.sort(affinity);
				if (Arrays.equals(cache.getAffinity(), affinity)) {
					return true;
				} else {
					result = setProcessAffinity(pid, affinity, true);
					if (result) {
						cache.setAffinity(affinity);
					}
				}
			} else {
				result = setProcessAffinity(pid, affinity, true);
				if (result) // affinity set, add to cache
				{
					this.cache.add(new Cache(pid, pName, affinity, Constants.INVALID_PRIORITY_VALUE,
							Constants.INVALID_POLICY_VALUE));
				}
			}
			if (!result && log.isErrorEnabled()) {
				log.error("setProcessAffinity failed to set affinity to process with ID " + pid + ".");
			}
			return result;
		} else if (log.isErrorEnabled()) {
			log.error("setProcessAffinity to process with ID " + pid
					+ " stopped due to invalid parameters. Please check parameters.");
		}

		return false;
	}
	// end of setProcessAffinity

	@Override
	public boolean setProcessAffinity(String pName, int[] affinity) {
		int pid = 0;
		if (Validator.checkAffinityParams(pName, affinity)) {
			boolean result = false;
			try {
				pid = service.getPid(pName);
				if (pid == Constants.INVALID_ID) {
					for (Iterator<Cache> iterator = this.cache.iterator(); iterator.hasNext();) {
						Cache cache = iterator.next();
						if (cache.getName().equals(pName))
							iterator.remove();
					}
					if (log.isErrorEnabled()) {
						log.error("setProcessAffinity failed to set affinity to process " + pName
								+ ". Process not found.");
					}
					return false;
				}
			} catch (IOException e) {
				if (log.isErrorEnabled()) {
					log.error(e);
				}
				return false;
			}

			Cache cache = checkCache(pid, pName);
			if (cache != null) {
				if (cache.getAffinity() != null)
					Arrays.sort(cache.getAffinity());
				Arrays.sort(affinity);
				if (Arrays.equals(cache.getAffinity(), affinity)) {
					return true;
				} else {
					result = setProcessAffinity(pid, affinity, true);
					if (result) {
						cache.setAffinity(affinity);
					}
				}
			} else {
				result = setProcessAffinity(pid, affinity, true);
				if (result) {
					this.cache.add(new Cache(pid, pName, affinity, Constants.INVALID_PRIORITY_VALUE,
							Constants.INVALID_POLICY_VALUE));
				}
			}
			if (!result && log.isErrorEnabled()) {
				log.error("setProcessAffinity failed to set affinity to process " + pName + " with ID " + pid);
			}
			return result;
		} else if (log.isErrorEnabled()) {
			log.error("setProcessAffinity to process " + pName
					+ " stopped due to invalid parameters. Please check parameters.");
		}
		return false;
	}
	// end of setProcessAffinity

	@Override
	public List<ProcessResult> setProcessAffinity(Collection<ProcessData> processes) {
		List<ProcessResult> results = null;
		if (processes != null && !processes.isEmpty()) {
			results = new ArrayList<>();
			for (ProcessData process : processes) {
				ProcessResult result;
				if (process.getName().equals("none")) {
					result = new ProcessResult(process.getId(),
							setProcessAffinity(process.getId(), process.getAffinity()));
				} else
					result = new ProcessResult(process.getName(),
							setProcessAffinity(process.getName(), process.getAffinity()));
				if (process.getThreads().size() > 0) {
					for (ThreadData thread : process.getThreads()) {
						if (thread.getName().equals("none")) {
							result.addThread(thread.getId(),
									setNativeThreadAffinity(thread.getId(), thread.getAffinity()));
						} else
							result.addThread(thread.getName(),
									setNativeThreadAffinity(thread.getName(), thread.getAffinity()));
					}
				}
				results.add(result);
			}
		} else if (processes == null || processes.isEmpty()) {
			if (log.isErrorEnabled()) {
				log.error("setProcessAffinity failed to set affinity on a collection. Empty Collection");
			}
			return null;
		}
		return results;
	}
	// end of setProcessAffinity

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
		if (Validator.checkAffinityParams(tid, affinity)) {
			boolean result = false;

			String tName;
			try {
				tName = service.getTname(tid);
				if (tName == null)
				{
					for (Iterator<Cache> iterator = this.cache.iterator(); iterator.hasNext();) {
						Cache cache = iterator.next();
						if (cache.getName().equals(tName))
							iterator.remove();
					}
					if (log.isErrorEnabled()) {
						log.error("setNativeThreadAffinity failed to set affinity to Thread " + tid + " Thread was not found");
					}	
				}
			} catch (IOException e) {
				return false;
			}

			Cache cache = checkCache(tid, tName);
			if (cache != null) {
				if (cache.getAffinity() != null) {
					Arrays.sort(cache.getAffinity());
				}
				Arrays.sort(affinity);
				if (Arrays.equals(cache.getAffinity(), affinity)) {
					return true;
				} else {
					result = setThreadAffinity(tid, affinity, true);
					if (result) {
						cache.setAffinity(affinity);
					}
				}
			} else if (null == cache) {
				result = setThreadAffinity(tid, affinity, true);
				if (result) {
					this.cache.add(new Cache(tid, tName, affinity, Constants.INVALID_PRIORITY_VALUE,
							Constants.INVALID_POLICY_VALUE));
				}
			}
			if (!result && log.isErrorEnabled()) {
				log.error("setNativeThreadAffinity failed to set affinity to thread with ID " + tid + ".");
			}
			return result;
		} else if (log.isErrorEnabled()) {
			log.error("setNativeThreadAffinity to thread with ID " + tid
					+ " stopped due to invalid parameters. Please check parameters.");
		}
		return false;
	}
	// end of setNativeThreadAffinity

	@Override
	public boolean setNativeThreadAffinity(String tName, int[] affinity) {
		int tid = 0;
		if (Validator.checkAffinityParams(tName, affinity)) {
			try {
				if ((tid = service.getTid(tName)) == Constants.INVALID_ID) {
					if (log.isErrorEnabled()) {
						for (Iterator<Cache> iterator = this.cache.iterator(); iterator.hasNext();) {
							Cache cache = iterator.next();
							if (cache.getName().equals(tName))
								iterator.remove();
						}
						log.error("setThreadAffinity failed to set affinity to thread with ID " + tName
								+ ". Thread not found.");
					}
					return false;
				}
			} catch (IOException e) {
				if (log.isErrorEnabled()) {
					log.error(e);
				}
				return false;
			}

			boolean result = false;
			Cache cache = checkCache(tid, tName);
			if (cache != null) {
				if (cache.getAffinity() != null) {
					Arrays.sort(cache.getAffinity());
				}
				Arrays.sort(affinity);
				if (Arrays.equals(cache.getAffinity(), affinity)) {
					return true;
				} else {
					result = setThreadAffinity(tid, affinity, true);
					if (result) {
						cache.setAffinity(affinity);
					}
				}
			} else if (null == cache) {
				result = setThreadAffinity(tid, affinity, true);
				if (result) {
					this.cache.add(new Cache(tid, tName, affinity, Constants.INVALID_PRIORITY_VALUE,
							Constants.INVALID_POLICY_VALUE));
				}
			}
			if (!result && log.isErrorEnabled()) {
				log.error("setNativeThreadAffinity failed to set affinity to thread  " + tName + " (ID " + tid + ").");
			}
			return result;
		} else if (log.isErrorEnabled()) {
			log.error("setNativeThreadAffinity to thread " + tName
					+ " stopped due to invalid parameters. Please check parameters.");
		}
		return false;

	}
	// end of setNativeThreadAffinity

	@Override
	public boolean setNativeThreadAffinity(String pName, String tName, int[] affinity) {
		int tid = 0;
		if (Validator.checkAffinityParams(pName, tName, affinity)) {
			try {
				for (int pid : service.getAllPids(pName)) {
					if ((tid = service.getTid(pid, tName)) != Constants.INVALID_ID)
						break;
				}
				if (tid == Constants.INVALID_ID) {
					for (Iterator<Cache> iterator = this.cache.iterator(); iterator.hasNext();) {
						Cache cache = iterator.next();
						if (cache.getName().equals(tName))
							iterator.remove();
					}
					if (log.isErrorEnabled()) {
						log.error("setNativeThreadAffinity failed to set affinity to thread " + tName
								+ " under process " + pName + ". Thread not found.");
					}
					return false;
				}
			} catch (IOException e) {
				if (log.isDebugEnabled())
					log.debug(e);
				e.printStackTrace();
				return false;
			}

			boolean result = false;
			Cache cache = checkCache(tid, tName);
			if (cache != null) {
				if (cache.getAffinity() != null) {
					Arrays.sort(cache.getAffinity());
				}
				Arrays.sort(affinity);
				if (Arrays.equals(cache.getAffinity(), affinity)) {
					return true;
				} else {
					result = setThreadAffinity(tid, affinity, true);
					if (result) {
						cache.setAffinity(affinity);
					}
				}
			} else {
				result = setThreadAffinity(tid, affinity, true);
				if (result) {
					this.cache.add(new Cache(tid, tName, affinity, Constants.INVALID_PRIORITY_VALUE,
							Constants.INVALID_POLICY_VALUE));
				}
			}
			if (!result && log.isErrorEnabled()) {
				log.error("setNativeThreadAffinity failed to set affinity to thread " + tName + "(native ID " + tid
						+ ") under process " + pName + ".");
			}
			return result;
		} else if (log.isErrorEnabled()) {
			log.error("setNativeThreadAffinity to thread " + tName + " under process " + pName
					+ " stopped due to invalid parameters. Please check parameters.");
		}
		return false;
	}
	// end of setNativeThreadAffinity

	@Override
	public List<Result> setThreadAffinity(Collection<ThreadData> threads) {
		List<Result> results = null;
		if (threads != null && !threads.isEmpty()) {
			results = new ArrayList<>();
			for (ThreadData thread : threads) {
				Result result;
				if (thread.getName().equals("none")) {
					if (thread.isJavaThread())
						result = new Result(thread.getId(),
								setJavaThreadAffinity(thread.getId(), thread.getAffinity()));
					else
						result = new Result(thread.getId(),
								setNativeThreadAffinity(thread.getId(), thread.getAffinity()));
				} else if (thread.isJavaThread())
					result = new Result(thread.getName(),
							setJavaThreadAffinity(thread.getName(), thread.getAffinity()));
				else
					result = new Result(thread.getName(),
							setNativeThreadAffinity(thread.getName(), thread.getAffinity()));
				results.add(result);
			}
		} else if (threads == null || threads.isEmpty()) {
			if (log.isErrorEnabled()) {
				log.error("setNativeThreadAffinity failed to set affinity on a collection. Empty Collection");
			}
			return null;
		}
		return results;
	}
	// end of setNativeThreadAffinity

	@Override
	public boolean setJavaThreadAffinity(int tid, int[] affinity) {
		int id = 0;
		if (Validator.checkAffinityParams(tid, affinity)) {
			javaHandler = new JavaThreadHandlerLinux();
			String tName = null;
			if ((tName = javaHandler.getThreadName(tid)) == null) {
				if (log.isErrorEnabled()) {
					log.error("setJavaThreadAffinity failed to set affinity to thread with java ID " + tid
							+ ". Thread not found.");
				}

				return false;

			}
			boolean result = false;
			int nid = javaHandler.getNativeThreadId(tName);
			if (nid == Constants.INVALID_ID) {
				if (log.isErrorEnabled()) {
					log.error("setJavaThreadAffinity failed to set affinity to java thread with java ID " + tid
							+ ". Thread not found.");
				}
				for (Iterator<Cache> iterator = this.cache.iterator(); iterator.hasNext();) {
					Cache cache = iterator.next();
					if (cache.getName().equals(tName))
						iterator.remove();
				}
				return false;
			}
			Cache cache = checkCache(id, tName);
			if (cache != null) {
				if (cache.getAffinity() != null) {
					Arrays.sort(cache.getAffinity());
				}
				Arrays.sort(affinity);
				if (Arrays.equals(cache.getAffinity(), affinity)) {
					return true;
				} else {
					result = setThreadAffinity(id, affinity, true);
					if (result) {
						cache.setAffinity(affinity);
					}
				}
			} else {
				result = setThreadAffinity(id, affinity, true);
				if (result) {
					this.cache.add(new Cache(id, tName, affinity, Constants.INVALID_PRIORITY_VALUE,
							Constants.INVALID_POLICY_VALUE));
				}
			}
			if (!result && log.isErrorEnabled()) {
				log.error("setJavaThreadAffinity failed to set affinity to java thread " + tName + " with ID " + tid
						+ "(native ID " + id + ").");
			}

			return result;

		} else if (log.isErrorEnabled()) {
			log.error("setJavaThreadAffinity to thread with ID " + tid
					+ " stopped due to invalid parameters. Please check parameters.");
		}

		return false;
	}
	// end of setJavaThreadAffinity

	@Override
	public boolean setJavaThreadAffinity(String tName, int[] affinity) {
		int tid = 0;
		if (Validator.checkAffinityParams(tName, affinity)) {
			javaHandler = new JavaThreadHandlerLinux();
			tid = javaHandler.getNativeThreadId(tName);
			if (tid <= Constants.INVALID_ID) {
				if (log.isErrorEnabled()) {
					for (Iterator<Cache> iterator = this.cache.iterator(); iterator.hasNext();) {
						Cache cache = iterator.next();
						if (cache.getName().equals(tName))
							iterator.remove();
					}
					if (tid == Constants.INVALID_ID)
						if (log.isErrorEnabled())
							log.error("setJavaThreadAffinity failed to set affinity to java thread " + tName
									+ ". Thread not found.");
				}

				return false;
			}

			boolean result = false;
			Cache cache = checkCache(tid, tName);
			if (cache != null) {
				if (cache.getAffinity() != null) {
					Arrays.sort(cache.getAffinity());
				}
				Arrays.sort(affinity);
				if (Arrays.equals(cache.getAffinity(), affinity)) {
					return true;
				} else {
					result = setThreadAffinity(tid, affinity, true);
					if (result) {
						cache.setAffinity(affinity);
					}
				}
			} else {
				result = setThreadAffinity(tid, affinity, true);
				if (result) {
					this.cache.add(new Cache(tid, tName, affinity, Constants.INVALID_PRIORITY_VALUE,
							Constants.INVALID_POLICY_VALUE));
				}

			}
			if (!result && log.isErrorEnabled()) {
				log.error("setJavaThreadAffinity failed to set affinity to java thread " + tName + " (native ID " + tid
						+ ").");
			}
			return result;

		} else if (log.isErrorEnabled()) {
			log.error("setJavaThreadAffinity to thread " + tName
					+ " stopped due to invalid parameters. Please check parameters.");
		}

		return false;
	}
	// end of setJavaThreadAffinity

	@Override
	public boolean setJavaThreadAffinity(String pName, String tName, int[] affinity) {
		int tid = 0;
		if (Validator.checkAffinityParams(pName, tName, affinity)) {
			int pid = -1;
			try {
				javaHandler = new JavaThreadHandlerLinux();
				pid = service.getPid(pName);
				tid = javaHandler.getNativeThreadId(tName, pid);
				if (tid <= Constants.INVALID_ID) {
					for (Iterator<Cache> iterator = this.cache.iterator(); iterator.hasNext();) {
						Cache cache = iterator.next();
						if (cache.getName().equals(tName))
							iterator.remove();
					}
					if (tid == Constants.INVALID_ID)
						if (log.isErrorEnabled()) {
							log.error("setJavaThreadAffinity failed to set affinity to java thread " + tName
									+ " under process " + pName + ". Thread not found.");
						}

					return false;
				}
			} catch (IOException e) {
				if (log.isDebugEnabled())
					log.debug(e);
				e.printStackTrace();
				return false;
			}

			boolean result = false;
			Cache cache = checkCache(tid, tName);
			if (cache != null) {
				if (cache.getAffinity() != null) {
					Arrays.sort(cache.getAffinity());
				}
				Arrays.sort(affinity);
				if (Arrays.equals(cache.getAffinity(), affinity)) {
					return true;
				} else {
					result = setThreadAffinity(tid, affinity, true);
					if (result) {
						cache.setAffinity(affinity);
					}
				}
			} else {
				result = setThreadAffinity(tid, affinity, true);
				if (result) {
					this.cache.add(new Cache(tid, tName, affinity, Constants.INVALID_PRIORITY_VALUE,
							Constants.INVALID_POLICY_VALUE));
				}

			}
			if (!result && log.isErrorEnabled()) {
				log.error("setJavaThreadAffinity failed to set affinity to java thread " + tName + "(native ID " + tid
						+ ") under process " + pName + " (ID " + pid + ").");
			}

			return result;

		} else if (log.isErrorEnabled()) {
			log.error("setJavaThreadAffinity to thread " + tName + " under process " + pName
					+ " stopped due to invalid parameters. Please check parameters.");
		}

		return false;
	}
	// end of setJavaThreadAffinity

	private boolean setProcessPriority(int pid, int policy, int priority, boolean checked) {
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
	public boolean setProcessPriority(int pid, int priority) {
		if (Validator.checkPriorityParams(pid, priority)) {
			int[] normalizedValues = normalizer.normalize(priority);
			int normPolicy = normalizedValues[0];
			int normPriority = normalizedValues[1];
			String pName;
			try {
				pName = service.getPname(pid);
				
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			if (pName == null) {
				for (Iterator<Cache> iterator = this.cache.iterator(); iterator.hasNext();) {
					Cache cache = iterator.next();
					if (cache.getId() == pid)
						iterator.remove();
				}
				if (log.isErrorEnabled()) {
					log.error("setProcessAffinity failed to set affinity to process " + pid
							+ ". Process not found.");
				}
			}
			boolean result = false;
			Cache cache = checkCache(pid, pName);
			if (cache != null) {
				if (cache.getPriority() == normPriority && cache.getPolicy() == normPolicy) {
					return true;
				} else {
					result = setProcessPriority(pid, normPolicy, normPriority, true);
					if (result) {
						cache.setPolicy(normPolicy);
						cache.setPriority(normPriority);
					}
				}
			} else if (null == cache) {
				result = setProcessPriority(pid, normPolicy, normPriority, true);
				if (result) {
					this.cache.add(new Cache(pid, pName, Constants.INVALID_ARRAY, normPriority, normPolicy));
				}
			}
			if (!result && log.isErrorEnabled()) {
				log.error("setProcessPriority failed to set priority to process with ID " + pid + ".");
			}

			return result;

		} else if (log.isErrorEnabled()) {
			log.error("setProcessPriority to process with ID " + pid
					+ " stopped due to invalid parameters. Please check parameters.");
		}

		return false;
	}
	// end of setProcessPriority

	@Override
	public boolean setProcessPriority(String pName, int priority) {
		if (Validator.checkPriorityParams(pName, priority)) {
			int[] normalizedValues = normalizer.normalize(priority);
			int normPolicy = normalizedValues[0];
			int normPriority = normalizedValues[1];
			int pid = 0;
			boolean result = false;
			try {
				if ((pid = service.getPid(pName)) == Constants.INVALID_ID) {
					for (Iterator<Cache> iterator = this.cache.iterator(); iterator.hasNext();) {
						Cache cache = iterator.next();
						if (cache.getName().equals(pName))
							iterator.remove();
					}
					if (log.isErrorEnabled()) {
						log.error("setProcessPriority failed to set priority to process " + pName
								+ ". Process not found.");
					}

					return false;
				}
			} catch (IOException e) {
				if (log.isErrorEnabled()) {
					log.error(e);
				}

				return false;
			}

			Cache cache = checkCache(pid, pName);
			if (cache != null) {
				if (cache.getPriority() == normPriority && cache.getPolicy() == normPolicy) {
					return true;
				} else {
					result = setProcessPriority(pid, normPolicy, normPriority, true);
					if (result) {
						cache.setPolicy(normPolicy);
						cache.setPriority(normPriority);
					}
				}
			} else if (null == cache) {
				result = setProcessPriority(pid, normPolicy, normPriority, true);
				if (result) {
					this.cache.add(new Cache(pid, pName, Constants.INVALID_ARRAY, normPriority, normPolicy));
				}
			}
			if (!result && log.isErrorEnabled()) {
				log.error("setProcessPriority failed to set priority to process " + pName + " with ID " + pid);
			}

			return result;

		} else if (log.isErrorEnabled()) {
			log.error("setProcessPriority to process " + pName
					+ " stopped due to invalid parameters. Please check parameters.");
		}

		return false;
	}
	// end of setProcessPriority

	@Override
	public List<ProcessResult> setProcessPriority(Collection<ProcessData> processes) {
		List<ProcessResult> results = null;
		if (processes != null && !processes.isEmpty()) {
			results = new ArrayList<>();
			for (ProcessData process : processes) {
				ProcessResult result;
				if (process.getName().equals("none")) {
					result = new ProcessResult(process.getId(),
							setProcessPriority(process.getId(), process.getPriority()));
				} else
					result = new ProcessResult(process.getName(),
							setProcessPriority(process.getName(), process.getPriority()));
				if (process.getThreads().size() > 0) {
					for (ThreadData thread : process.getThreads()) {
						if (thread.getName().equals("none")) {
							result.addThread(thread.getId(),
									setNativeThreadPriority(thread.getId(), thread.getPriority()));
						} else
							result.addThread(thread.getName(),
									setNativeThreadPriority(thread.getName(), thread.getPriority()));
					}
				}
				results.add(result);
			}
		}
		if (processes == null || processes.isEmpty()) {
			if (log.isErrorEnabled()) {
				log.error("setProcessPriority failed to set priority on a collection. Empty Collection");
			}
			return null;
		}
		return results;
	}
	// end of setProcessPriority

	private boolean setThreadPriority(int tid, int policy, int priority, boolean checked) {
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
	public boolean setNativeThreadPriority(int tid, int priority) {
		if (Validator.checkPriorityParams(tid, priority)) {
			int[] normalizedValues = normalizer.normalize(priority);
			int normPolicy = normalizedValues[0];
			int normPriority = normalizedValues[1];
			String tName = null;
			try {
				tName = service.getTname(tid);
			} catch (IOException e) {
				return false;
			}
			if (tName == null) {
				for (Iterator<Cache> iterator = this.cache.iterator(); iterator.hasNext();) {
					Cache cache = iterator.next();
					if (cache.getId() == tid)
						iterator.remove();
				}
				if (log.isErrorEnabled()) {
					log.error("setNativeThreadPriority failed to set priority to Thread " + tid
							+ ". Thread not found.");
				}
				return false;
			}
			boolean result = false;
			Cache cache = checkCache(tid, tName);
			if (cache != null) {
				if (cache.getPriority() == normPriority && cache.getPolicy() == normPolicy) {
					return true;
				} else {
					result = setThreadPriority(tid, normPolicy, normPriority, true);
					if (result) {
						cache.setPolicy(normPolicy);
						cache.setPriority(normPriority);
					}
				}
			} else {
				result = setThreadPriority(tid, normPolicy, normPriority, true);
				if (result) {
					this.cache.add(new Cache(tid, tName, Constants.INVALID_ARRAY, normPriority, normPolicy));
				}
			}
			if (!result && log.isErrorEnabled()) {
				log.error(
						"setNativeThreadPriority failed to set priority to thread " + tName + " with ID " + tid + ".");
			}

			return result;

		} else if (log.isErrorEnabled()) {
			log.error("setNativeThreadPriority to thread " + tid
					+ " stopped due to invalid parameters. Please check parameters.");
		}

		return false;
	}
	// end of setNativeThreadPriority

	@Override
	public boolean setNativeThreadPriority(String tName, int priority) {
		if (Validator.checkPriorityParams(tName, priority)) {
			int[] normalizedValues = normalizer.normalize(priority);
			int normPolicy = normalizedValues[0];
			int normPriority = normalizedValues[1];
			int tid = 0;
			try {
				if ((tid = service.getTid(tName)) == Constants.INVALID_ID) {
					for (Iterator<Cache> iterator = this.cache.iterator(); iterator.hasNext();) {
						Cache cache = iterator.next();
						if (cache.getName().equals(tName))
							iterator.remove();
					}
					if (log.isErrorEnabled()) {
						log.error("setNativeThreadPriority failed to set priority to thread " + tName + " with ID "
								+ tid + ". Thread not found.");
					}

					return false;
				}
			} catch (IOException e) {
				if (log.isErrorEnabled()) {
					log.error(e);
				}

				return false;
			}

			boolean result = false;
			Cache cache = checkCache(tid, tName);
			if (cache != null) {
				if (cache.getPriority() == normPriority && cache.getPolicy() == normPolicy) {
					return true;
				} else {
					result = setThreadPriority(tid, normPolicy, normPriority, true);
					if (result) {
						cache.setPolicy(normPolicy);
						cache.setPriority(normPriority);
					}
				}
			} else {
				result = setThreadPriority(tid, normPolicy, normPriority, true);
				if (result) {
					this.cache.add(new Cache(tid, tName, Constants.INVALID_ARRAY, normPriority, normPolicy));
				}
			}
			if (!result && log.isErrorEnabled()) {
				log.error(
						"setNativeThreadPriority failed to set priority to thread " + tName + " with ID " + tid + ".");
			}

			return result;

		} else if (log.isErrorEnabled()) {
			log.error("setNativeThreadPriority to thread " + tName
					+ " stopped due to invalid parameters. Please check parameters.");
		}

		return false;
	}
	// end of setNativeThreadPriority

	@Override
	public boolean setNativeThreadPriority(String pName, String tName, int priority) {
		if (Validator.checkPriorityParams(pName, tName, priority)) {
			int[] normalizedValues = normalizer.normalize(priority);
			int normPolicy = normalizedValues[0];
			int normPriority = normalizedValues[1];
			int tid = 0;
			int pid = -1;
			try {
				pid = service.getPid(pName);
				if ((tid = service.getTid(pid, tName)) == Constants.INVALID_ID) {
					for (Iterator<Cache> iterator = this.cache.iterator(); iterator.hasNext();) {
						Cache cache = iterator.next();
						if (cache.getName().equals(tName))
							iterator.remove();
					}
					if (log.isErrorEnabled()) {
						log.error("setNativeThreadPriority failed to set priority to thread " + tName
								+ " under process " + pName + ". Thread not found.");
					}

					return false;
				}
			} catch (IOException e) {
				if (log.isDebugEnabled())
					log.debug(e);
				e.printStackTrace();
				return false;
			}

			boolean result = false;
			Cache cache = checkCache(tid, tName);
			if (cache != null) {
				if (cache.getPriority() == normPriority && cache.getPolicy() == normPolicy) {
					return true;
				} else {
					result = setThreadPriority(tid, normPolicy, normPriority, true);
					if (result) {
						cache.setPolicy(normPolicy);
						cache.setPriority(normPriority);
					}
				}
			} else {
				result = setThreadPriority(tid, normPolicy, normPriority, true);
				if (result) {
					this.cache.add(new Cache(tid, tName, Constants.INVALID_ARRAY, normPriority, normPolicy));
				}
			}
			if (!result && log.isErrorEnabled()) {
				log.error("setNativeThreadPriority failed to set priority to thread " + tName + " with ID " + tid
						+ " under process " + pName + " with ID " + pid + ".");
			}

			return result;

		} else if (log.isErrorEnabled()) {
			log.error("setNativeThreadPriority to thread " + tName + " under process " + pName
					+ " stopped due to invalid parameters. Please check parameters.");
		}

		return false;
	}
	// end of setNativeThreadPriority

	@Override
	public boolean setNativeThreadPriority(int pid, String tName, int priority) {
		if (Validator.checkPriorityParams(pid, tName, priority)) {
			int[] normalizedValues = normalizer.normalize(priority);
			int normPolicy = normalizedValues[0];
			int normPriority = normalizedValues[1];
			int tid = 0;
			try {
				if ((tid = service.getTid(pid, tName)) == Constants.INVALID_ID) {
					for (Iterator<Cache> iterator = this.cache.iterator(); iterator.hasNext();) {
						Cache cache = iterator.next();
						if (cache.getName().equals(tName))
							iterator.remove();
					}
					if (log.isErrorEnabled()) {
						log.error("setNativeThreadPriority failed to set priority to thread " + tName
								+ " under process with ID " + pid + ". Thread not found.");
					}

					return false;
				}
			} catch (IOException e) {
				if (log.isDebugEnabled())
					log.debug(e);
				e.printStackTrace();
				return false;
			}

			boolean result = false;
			Cache cache = checkCache(tid, tName);
			if (cache != null) {
				if (cache.getPriority() == normPriority && cache.getPolicy() == normPolicy) {
					return true;
				} else {
					result = setThreadPriority(tid, normPolicy, normPriority, true);
					if (result) {
						cache.setPolicy(normPolicy);
						cache.setPriority(normPriority);
					}
				}
			} else {
				result = setThreadPriority(tid, normPolicy, normPriority, true);
				if (result) {
					this.cache.add(new Cache(tid, tName, Constants.INVALID_ARRAY, normPriority, normPolicy));
				}
			}
			if (!result && log.isErrorEnabled()) {
				log.error("setNativeThreadPriority failed to set priority to thread " + tName + "(native ID " + tid
						+ ") under process with ID " + pid + ").");
			}

			return result;

		} else if (log.isErrorEnabled()) {
			log.error("setNativeThreadPriority to thread " + tName + " under process with ID " + pid
					+ " stopped due to invalid parameters. Please check parameters.");
		}

		return false;
	}
	// end of setNativeThreadPriority

	@Override
	public List<Result> setThreadPriority(Collection<ThreadData> threads) {
		List<Result> results = null;
		if (threads != null && !threads.isEmpty()) {
			results = new ArrayList<>();
			for (ThreadData thread : threads) {
				Result result = null;
				if (thread.getName().equals("none")) {
					if (thread.isJavaThread())
						result = new Result(thread.getId(),
								setJavaThreadPriority(thread.getId(), thread.getPriority()));
					else
						result = new Result(thread.getId(),
								setNativeThreadPriority(thread.getId(), thread.getPriority()));
				} else {
					if (thread.isJavaThread())
						result = new Result(thread.getName(),
								setJavaThreadPriority(thread.getName(), thread.getPriority()));
					else
						result = new Result(thread.getName(),
								setJavaThreadPriority(thread.getName(), thread.getPriority()));
				}
				results.add(result);
			}
		}
		if (threads == null || threads.isEmpty()) {
			if (log.isErrorEnabled()) {
				log.error("setNativeThreadPriority failed to set priority on a collection. Empty Collection");
			}
			return null;
		}
		return results;
	}
	// end of setNativeThreadPriority

	@Override
	public boolean setJavaThreadPriority(int tid, int priority) {
		int nTid = 0;
		if (Validator.checkPriorityParams(tid, priority)) {
			javaHandler = new JavaThreadHandlerLinux();
			int[] normalizedValues = normalizer.normalize(priority);
			int normPolicy = normalizedValues[0];
			int normPriority = normalizedValues[1];
			String tName = javaHandler.getThreadName(tid);
			if (tName == null) {
				if (log.isErrorEnabled()) {
					log.error("setJavaThreadPriority failed to set priority to java thread " + tid
							+ ". Thread not found.");
				}
				return false;
			}
			nTid = javaHandler.getNativeThreadId(tid);
			if (nTid <= Constants.INVALID_ID) {
				for (Iterator<Cache> iterator = this.cache.iterator(); iterator.hasNext();) {
					Cache cache = iterator.next();
					if (cache.getId() == nTid)
						iterator.remove();
				}
				if (nTid == Constants.INVALID_ID)
					if (log.isErrorEnabled()) {
						log.error("setJavaThreadPriority failed to set priority to java thread " + tid
								+ ". Thread not found.");
					}
				return false;
			}

			boolean result = false;
			Cache cache = checkCache(nTid, tName);
			if (cache != null) {
				if (cache.getPriority() == normPriority && cache.getPolicy() == normPolicy) {
					return true;
				} else {
					result = setThreadPriority(nTid, normPolicy, normPriority, true);
					if (result) {
						cache.setPolicy(normPolicy);
						cache.setPriority(normPriority);
					}
				}
			} else {
				result = setThreadPriority(nTid, normPolicy, normPriority, true);
				if (result) {
					this.cache.add(new Cache(nTid, tName, Constants.INVALID_ARRAY, normPriority, normPolicy));
				}
			}
			if (!result && log.isErrorEnabled()) {
				log.error("setJavaThreadPriority failed to set priority to java thread " + tName + " with ID " + tid
						+ "(native ID " + nTid + ").");
			}

			return result;

		} else if (log.isErrorEnabled())

		{
			log.error("setJavaThreadPriority to thread " + tid
					+ " stopped due to invalid parameters. Please check parameters.");
		}

		return false;

	}
	// end of setJavaThreadPriority

	@Override
	public boolean setJavaThreadPriority(String tName, int priority) {
		if (Validator.checkPriorityParams(tName, priority)) {
			int tid = 0;
			int[] normalizedValues = normalizer.normalize(priority);
			int normPolicy = normalizedValues[0];
			int normPriority = normalizedValues[1];
			javaHandler = new JavaThreadHandlerLinux();
			tid = javaHandler.getNativeThreadId(tName);
			if (tid <= Constants.INVALID_ID) {
				for (Iterator<Cache> iterator = this.cache.iterator(); iterator.hasNext();) {
					Cache cache = iterator.next();
					if (cache.getName().equals(tName))
						iterator.remove();
				}
				if (tid == Constants.INVALID_ID)
					if (log.isErrorEnabled()) {
						if (log.isErrorEnabled()) {
							log.error("setJavaThreadPriority failed to set priority to java thread " + tName
									+ ". Thread not found.");
						}
					}

				return false;
			}

			boolean result = false;
			Cache cache = checkCache(tid, tName);
			if (cache != null) {
				if (cache.getPriority() == normPriority && cache.getPolicy() == normPolicy) {
					return true;
				} else {
					result = setThreadPriority(tid, normPolicy, normPriority, true);
					if (result) {
						cache.setPolicy(normPolicy);
						cache.setPriority(normPriority);
					}
				}
			} else {
				result = setThreadPriority(tid, normPolicy, normPriority, true);
				if (result) {
					this.cache.add(new Cache(tid, tName, Constants.INVALID_ARRAY, normPriority, normPolicy));
				}
			}
			if (!result && log.isErrorEnabled()) {
				log.error("setJavaThreadPriority failed to set priority to java thread " + tName + " (native ID " + tid
						+ ").");
			}
			return result;
		} else if (log.isErrorEnabled()) {
			log.error("setJavaThreadPriority to thread " + tName
					+ " stopped due to invalid parameters. Please check parameters.");
		}

		return false;
	}
	// end of setJavaThreadPriority

	@Override
	public boolean setJavaThreadPriority(String pName, String tName, int priority) {
		int tid = 0;
		if (Validator.checkPriorityParams(pName, tName, priority)) {
			int[] normalizedValues = normalizer.normalize(priority);
			int normPolicy = normalizedValues[0];
			int normPriority = normalizedValues[1];
			int pid = -1;
			try {
				if ((pid = service.getPid(pName)) == Constants.INVALID_ID) {
					for (Iterator<Cache> iterator = this.cache.iterator(); iterator.hasNext();) {
						Cache cache = iterator.next();
						if (cache.getName().equals(pName))
							iterator.remove();
					}
					if (log.isErrorEnabled()) {
						log.error("setJavaThreadPriority failed to set priority to java thread " + tName
								+ " under process " + pName + ". Process not found.");
					}

					return false;
				}
			} catch (IOException e) {
				if (log.isDebugEnabled())
					log.debug(e);
				e.printStackTrace();
				return false;
			}

			javaHandler = new JavaThreadHandlerLinux();
			tid = javaHandler.getNativeThreadId(tName, pid);
			if (tid <= Constants.INVALID_ID) {
				for (Iterator<Cache> iterator = this.cache.iterator(); iterator.hasNext();) {
					Cache cache = iterator.next();
					if (cache.getName().equals(tName))
						iterator.remove();
				}
				if (tid == Constants.INVALID_ID)
					if (log.isErrorEnabled()) {
						log.error("setJavaThreadPriority failed to set priority to java thread " + tName
								+ " under process " + pName + ". Thread not found.");
					}

				return false;
			}

			boolean result = false;
			Cache cache = checkCache(tid, tName);
			if (cache != null) {
				if (cache.getPriority() == normPriority && cache.getPolicy() == normPolicy) {
					return true;
				} else {
					result = setThreadPriority(tid, normPolicy, normPriority, true);
					if (result) {
						cache.setPolicy(normPolicy);
						cache.setPriority(normPriority);
					}
				}
			} else {
				result = setThreadPriority(tid, normPolicy, normPriority, true);
				if (result) {
					this.cache.add(new Cache(tid, tName, Constants.INVALID_ARRAY, normPriority, normPolicy));
				}
			}
			if (!result && log.isErrorEnabled()) {
				log.error("setJavaThreadPriority failed to set priority to java thread " + tName + " (native ID " + tid
						+ ") under process " + pName + " (ID " + pid + ").");
			}

			return result;

		} else if (log.isErrorEnabled()) {
			log.error("setJavaThreadPriority to thread " + tName + " under process " + pName
					+ " stopped due to invalid parameters. Please check parameters.");
		}

		return false;
	}
	// end of setJavaThreadPriority

	@Override
	public boolean setJavaThreadPriority(int pid, String tName, int priority) {
		int tid = 0;
		if (Validator.checkPriorityParams(pid, tName, priority)) {
			int[] normalizedValues = normalizer.normalize(priority);
			int normPolicy = normalizedValues[0];
			int normPriority = normalizedValues[1];
			javaHandler = new JavaThreadHandlerLinux();
			tid = javaHandler.getNativeThreadId(tName, pid);
			if (tid <= Constants.INVALID_ID) {
				for (Iterator<Cache> iterator = this.cache.iterator(); iterator.hasNext();) {
					Cache cache = iterator.next();
					if (cache.getName().equals(tName))
						iterator.remove();
				}
				if (tid == Constants.INVALID_ID)
					if (log.isErrorEnabled()) {
						log.error("setJavaThreadPriority failed to set priority to java thread " + tName
								+ " under process with ID " + pid + ". Thread not found.");
					}

				return false;
			}

			boolean result = false;
			Cache cache = checkCache(tid, tName);
			if (cache != null) {
				if (cache.getPriority() == normPriority && cache.getPolicy() == normPolicy) {
					return true;
				} else {
					result = setThreadPriority(tid, normPolicy, normPriority, true);
					if (result) {
						cache.setPolicy(normPolicy);
						cache.setPriority(normPriority);
					}
				}
			} else {
				result = setThreadPriority(tid, normPolicy, normPriority, true);
				if (result) {
					this.cache.add(new Cache(tid, tName, Constants.INVALID_ARRAY, normPriority, normPolicy));
				}
			}
			if (!result && log.isErrorEnabled()) {
				log.error("setJavaThreadPriority failed to set priority to java thread " + tName
						+ " under process with ID " + pid + ".");
			}

			return result;

		} else if (log.isErrorEnabled()) {
			log.error("setJavaThreadPriority to thread " + tName + " under process with ID " + pid
					+ " stopped due to invalid parameters. Please check parameters.");
		}

		return false;
	}
	// end of setNativeThreadPriority

	@Override
	public int getProcessorCount() {
		return PALib.INSTANCE.getProcessorCount();
	}

	@Override
	public boolean setNativeThreadAffinity(int pid, String tName, int[] affinity) {
		if (Validator.checkAffinityParams(pid, tName, affinity)) {
			int tid = 0;
			try {
				if ((tid = service.getTid(pid, tName)) == Constants.INVALID_ID) {
					for (Iterator<Cache> iterator = this.cache.iterator(); iterator.hasNext();) {
						Cache cache = iterator.next();
						if (cache.getName().equals(tName))
							iterator.remove();
					}
					if (log.isErrorEnabled()) {
						log.error("setNativeThreadAffinity failed to set affinity to thread " + tName
								+ " under process with ID " + pid + ". Thread not found.");
					}
					return false;
				}
			} catch (IOException e) {
				if (log.isDebugEnabled()) {
					log.debug(e);
					e.printStackTrace();
					return false;
				}
			}

			boolean result = false;
			Cache cache = checkCache(tid, tName);
			if (cache != null) {
				if (cache.getAffinity() != null) {
					Arrays.sort(cache.getAffinity());
				}
				Arrays.sort(affinity);
				if (Arrays.equals(cache.getAffinity(), affinity)) {
					return true;
				} else {
					result = setThreadAffinity(tid, affinity, true);
					if (result) {
						cache.setAffinity(affinity);
					}
				}
			} else if (null == cache) {
				result = setThreadAffinity(tid, affinity, true);
				if (result) {
					this.cache.add(new Cache(tid, tName, affinity, Constants.INVALID_PRIORITY_VALUE,
							Constants.INVALID_POLICY_VALUE));
				}
			}
			if (!result && log.isErrorEnabled()) {
				log.error("setNativeThreadAffinity failed to set affinity to thread " + tName + " (ID " + tid
						+ ") under process with ID " + pid + ".");
			}
			return result;
		} else if (log.isErrorEnabled()) {
			log.error("setNativeThreadAffinity to thread " + tName + " under process with ID " + pid
					+ " stopped due to invalid parameters. Please check parameters.");
		}
		return false;
	}
	// end of setNativeThreadAffinity

	@Override
	public boolean setJavaThreadAffinity(int pid, String tName, int[] affinity) {
		if (Validator.checkAffinityParams(pid, tName, affinity)) {
			int tid = 0;
			javaHandler = new JavaThreadHandlerLinux();
			tid = javaHandler.getNativeThreadId(tName, pid);
			if (tid == Constants.INVALID_ID) {
				if (log.isErrorEnabled()) {
					log.error("setJavaThreadAffinity failed to set affinity to java thread " + tName
							+ " under process ID " + pid + ". Thread not found.");
				}
				for (Iterator<Cache> iterator = this.cache.iterator(); iterator.hasNext();) {
					Cache cache = iterator.next();
					if (cache.getName().equals(tName))
						iterator.remove();
				}

				return false;
			}

			boolean result = false;
			Cache cache = checkCache(tid, tName);
			if (cache != null) {
				if (cache.getAffinity() != null) {
					Arrays.sort(cache.getAffinity());
				}
				Arrays.sort(affinity);
				if (Arrays.equals(cache.getAffinity(), affinity)) {
					return true;
				} else {
					result = setThreadAffinity(tid, affinity, true);
					if (result) {
						cache.setAffinity(affinity);
					}
				}
			} else if (null == cache) {
				result = setThreadAffinity(tid, affinity, true);
				if (result) {
					this.cache.add(new Cache(tid, tName, affinity, Constants.INVALID_PRIORITY_VALUE,
							Constants.INVALID_POLICY_VALUE));
				}
			}
			if (!result && log.isErrorEnabled()) {
				log.error("setJavaThreadAffinity failed to set affinity to java thread " + tName + " (ID " + tid
						+ ") under process with ID " + pid + ".");
			}
			return result;
		} else if (log.isErrorEnabled()) {
			log.error("setJavaThreadAffinity to thread " + tName + " under process with ID " + pid
					+ " stopped due to invalid parameters. Please check parameters.");
		}
		return false;
	}
}
