package handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javathreadshandlers.JavaThreadHandlerWindows;
import normalizers.Normalizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cache.Cache;
import results.ProcessResult;
import results.Result;
import validate.Validator;
import core.Constants;
import core.ProcessData;
import core.ThreadData;

public class WindowsHandler extends Handler
{
	public static final int INVALID_PRIORITY_VALUE = 0xfffffc19; // value = -999
	public static final int INVALID_POLICY_VALUE = -999;
	public static final int INVALID_ID = -1;
	public static final String INVALID_NAME = null;
	public static final int[] INVALID_ARRAY = null;

	private JavaThreadHandlerWindows javaHandler;
	private Normalizer normalizer;
	private static int numberOfCpus;
	private static Logger log;

	public WindowsHandler(Normalizer norm)
	{
		log = LogManager.getRootLogger();
		WindowsServiceClass.initServiceClass();
		javaHandler = new JavaThreadHandlerWindows(WindowsServiceClass.getKernel32Instance());
		numberOfCpus = WindowsServiceClass.getNumberOfCpus();
		this.normalizer = norm;
		Validator.setNumberOfCpus(numberOfCpus);
		this.cache = new ArrayList<>();
	}

	@Override
	public int getProcessorCount()
	{
		return numberOfCpus;
	}

	@Override
	public void clearCache()
	{
		if (this.cache != null)
			this.cache.clear();
	}

	@Override
	public Cache checkCache(int id, String name)
	{
		for (Iterator<Cache> iterator = this.cache.iterator(); iterator.hasNext();)
		{
			Cache cache = iterator.next();
			if (cache.getName().equals(name))
			{
				if (cache.getId() != id)
				{
					iterator.remove();
					break;
				} else if (cache.getId() == id)
				{
					return cache;
				}
			}
		}

		return null;
	}

	/*--------------------------- SET AFFINITY -----------------------------------------------*/

	@Override
	public boolean setProcessAffinity(int pid, int[] affinity)
	{
		if (Validator.checkAffinityParams(pid, affinity))
		{
			boolean result = false;
			String pname = WindowsServiceClass.getProcessNameByProcessId(pid);
			if (pname == null)
			{
				for (Iterator<Cache> iterator = this.cache.iterator(); iterator.hasNext();)
				{
					Cache cache = iterator.next();
					if (cache.getId() == pid)
						iterator.remove();
				}
				if (log.isErrorEnabled())
				{
					log.error("setProcessAffinity failed to set affinity to process " + pid + ". Process not found.");
				}
			}
			Cache cache = checkCache(pid, pname);
			if (cache != null)
			{
				if (cache.getAffinity() != null)
					Arrays.sort(cache.getAffinity());
				Arrays.sort(affinity);
				if (Arrays.equals(cache.getAffinity(), affinity))
				{
					return true;
				} 
				else
				{
					result = WindowsServiceClass.setProcessAffinity(pid, affinity);
					if (result)
					{
						cache.setAffinity(affinity);
					}
				}
			} 
			else
			{
				result = WindowsServiceClass.setProcessAffinity(pid, affinity);
				if (result) // affinity set, add to cache
				{
					this.cache.add(new Cache(pid, pname, affinity, INVALID_PRIORITY_VALUE, INVALID_POLICY_VALUE));
				}
			}
			if (!result && log.isErrorEnabled())
			{
				log.error("setProcessAffinity failed to set affinity to process with ID " + pid + ".");
			}
			return result;
		} 
		else if (log.isErrorEnabled())
		{
			log.error("setProcessAffinity to process with ID " + pid + " stopped due to invalid parameters. Please check parameters.");
		}
		return false;
	}

	// end of setProcessAffinity

	@Override
	public boolean setProcessAffinity(String pName, int[] affinity)
	{
		boolean result = false;
		if (Validator.checkAffinityParams(pName, affinity))
		{
			int processId = WindowsServiceClass.getProcessIdByProcessName(pName);
			if (INVALID_ID == processId)
			{
				for (Iterator<Cache> iterator = this.cache.iterator(); iterator.hasNext();)
				{
					Cache cache = iterator.next();
					if (cache.getName().equals(pName))
						iterator.remove();
				}
				if (log.isErrorEnabled())
				{
					log.error("setProcessAffinity failed to set affinity to process " + pName + ". Process not found.");
				}
				return false;
			}
			Cache cache = checkCache(processId, pName);
			if (cache != null)
			{
				if (cache.getAffinity() != null)
					Arrays.sort(cache.getAffinity());
				Arrays.sort(affinity);
				if (Arrays.equals(cache.getAffinity(), affinity))
				{
					return true;
				} 
				else
				{
					result = WindowsServiceClass.setProcessAffinity(processId, affinity);
					if (result)
					{
						cache.setAffinity(affinity);
					}
				}
			} 
			else
			{
				result = WindowsServiceClass.setProcessAffinity(processId, affinity);
				if (result)
				{
					this.cache.add(new Cache(processId, pName, affinity, INVALID_PRIORITY_VALUE, INVALID_POLICY_VALUE));
				}
			}
			if (!result && log.isErrorEnabled())
			{
				log.error("setProcessAffinity failed to set affinity to process " + pName + " with ID " + processId);
			}
			return result;
		} 
		else if (log.isErrorEnabled())
		{
			log.error("setProcessAffinity to process " + pName + " stopped due to invalid parameters. Please check parameters.");
		}
		return false;
	}
	// end of setProcessAffinity

	@Override
	public List<ProcessResult> setProcessAffinity(Collection<ProcessData> processes)
	{
		List<ProcessResult> results;
		if (processes == null || processes.isEmpty())
		{
			if (log.isErrorEnabled())
			{
				log.error("setProcessAffinity failed to set affinity on a collection. Empty Collection");
			}
			return null;
		} 
		else
		{
			results = new ArrayList<>();
		}

		for (ProcessData process : processes)
		{
			ProcessResult procResult;
			if (process.getName().equals("none"))
				procResult = new ProcessResult(process.getId(), setProcessAffinity(process.getId(), process.getAffinity()));
			else
				procResult = new ProcessResult(process.getName(), setProcessAffinity(process.getName(),process.getAffinity()));

			// CHECKS IF PROCESS HAS THREAD LIST, IF IT DOES, SET THREADS
			// AFFINITY AS WELL.
			if (process.getThreads() != null || !process.getThreads().isEmpty())
			{
				for (ThreadData thread : process.getThreads())
				{
					if (thread.getName().equals("none"))
					{
						if (thread.isJavaThread())
							procResult.addThread(
									thread.getId(),
									setJavaThreadAffinity(thread.getId(),
											thread.getAffinity()));
						else
							procResult.addThread(
									thread.getId(),
									setNativeThreadAffinity(thread.getId(),
											thread.getAffinity()));
					} else
					{
						if (thread.isJavaThread())
							procResult.addThread(
									thread.getName(),
									setJavaThreadAffinity(thread.getName(),
											thread.getAffinity()));
						else
							procResult.addThread(
									thread.getName(),
									setNativeThreadAffinity(thread.getName(),
											thread.getAffinity()));
					}
				}
			}
			results.add(procResult);
		}
		return results;
	}
	// end of setProcessAffinity

	@Override
	public boolean setNativeThreadAffinity(int tid, int[] affinity)
	{
		if (Validator.checkAffinityParams(tid, affinity))
		{
			boolean result = false;
			Cache cache = checkCache(tid, INVALID_NAME);
			if (cache != null)
			{
				if (cache.getAffinity() != null)
				{
					Arrays.sort(cache.getAffinity());
				}
				Arrays.sort(affinity);
				if (Arrays.equals(cache.getAffinity(), affinity))
				{
					return true;
				} 
				else
				{
					result = WindowsServiceClass.setNativeThreadAffinity(tid, affinity);
					if (result)
					{
						cache.setAffinity(affinity);
					}
				}
			} else if (null == cache)
			{
				result = WindowsServiceClass.setNativeThreadAffinity(tid, affinity);
				if (result)
				{
					this.cache.add(new Cache(tid, INVALID_NAME, affinity, INVALID_PRIORITY_VALUE, INVALID_POLICY_VALUE));
				}
			}
			if (!result && log.isErrorEnabled())
			{
				log.error("setNativeThreadAffinity failed to set affinity to thread with ID "
						+ tid + ".");
			}
			return result;
		} else if (log.isErrorEnabled())
		{
			log.error("setNativeThreadAffinity to thread with ID "
					+ tid
					+ " stopped due to invalid parameters. Please check parameters.");
		}
		return false;
	}
	// end of setNativeThreadAffinity

	@Override
	public boolean setNativeThreadAffinity(String tName, int[] affinity)
	{
		if (log.isErrorEnabled())
		{
			log.error("setNativeThreadAffinity failed to set affinity to thread "
					+ tName + ". Windows was unable to determine thread name.");
		}
		return false;
	}
	// end of setNativeThreadAffinity

	@Override
	public boolean setNativeThreadAffinity(String pName, String tName,
			int[] affinity)
	{
		if (log.isErrorEnabled())
		{
			log.error("setNativeThreadAffinity failed to set affinity to thread "
					+ tName
					+ " under process "
					+ pName
					+ ". Windows was unable to determine thread name.");
		}
		return false;
	}

	@Override
	public boolean setNativeThreadAffinity(int pid, String tName, int[] affinity)
	{
		if (log.isErrorEnabled())
		{
			log.error("setNativeThreadAffinity failed to set affinity to thread "
					+ tName
					+ " under process with ID "
					+ pid
					+ ". Windows was unable to determine thread name.");
		}
		return false;
	}

	@Override
	public boolean setJavaThreadAffinity(int tid, int[] affinity)
	{
		if (Validator.checkAffinityParams(tid, affinity))
		{
			boolean result = false;
			String tName = javaHandler.getThreadName(tid);
			if (tName.equals("not found"))
			{
				if (log.isErrorEnabled())
				{
					log.error("setJavaThreadAffinity failed to set affinity to thread with java ID "
							+ tid + ". Thread not found.");
				}

				return false;
			}
			int nid = javaHandler.getNativeThreadId(tName);
			if (INVALID_ID >= nid) // thread not found.
			{
				for (Iterator<Cache> iterator = this.cache.iterator(); iterator.hasNext();)
				{
					Cache cache = iterator.next();
					if (cache.getName().equals(tName))
						iterator.remove();
				}
				if (INVALID_ID == nid && log.isErrorEnabled())
					log.error("setJavaThreadAffinity failed to set affinity to java thread with java ID "
							+ tid + ". Thread not found.");
				return false;
			}
			Cache cache = checkCache(nid, tName);
			if (cache != null)
			{
				if (cache.getAffinity() != null)
				{
					Arrays.sort(cache.getAffinity());
				}
				Arrays.sort(affinity);
				if (Arrays.equals(cache.getAffinity(), affinity))
				{
					return true;
				} else
				{
					result = WindowsServiceClass.setNativeThreadAffinity(nid,
							affinity);
					if (result)
					{
						cache.setAffinity(affinity);
					}
				}
			} else
			{
				result = WindowsServiceClass.setNativeThreadAffinity(nid,
						affinity);
				if (result)
				{
					this.cache.add(new Cache(nid, tName, affinity,
							INVALID_PRIORITY_VALUE, INVALID_POLICY_VALUE));
				}
			}
			if (!result && log.isErrorEnabled())
			{
				log.error("setJavaThreadAffinity failed to set affinity to java thread "
						+ tName
						+ " with ID "
						+ tid
						+ "(native ID "
						+ nid
						+ ").");
			}
			return result;
		} else if (log.isErrorEnabled())
		{
			log.error("setJavaThreadAffinity to thread with java ID "
					+ tid
					+ " stopped due to invalid parameters. Please check parameters.");
		}
		return false;
	}
	// end of setThreadAffinity

	@Override
	public boolean setJavaThreadAffinity(String tName, int[] affinity)
	{
		if (Validator.checkAffinityParams(tName, affinity))
		{
			boolean result = false;
			int nid = javaHandler.getNativeThreadId(tName);
			if (INVALID_ID >= nid) // thread not found.
			{
				for (Iterator<Cache> iterator = this.cache.iterator(); iterator
						.hasNext();)
				{
					Cache cache = iterator.next();
					if (cache.getName().equals(tName))
						iterator.remove();
				}
				if (INVALID_ID == nid && log.isErrorEnabled())
					log.error("setJavaThreadAffinity failed to set affinity to java thread "
							+ tName + ". Thread not found.");
				return false;
			}
			Cache cache = checkCache(nid, tName);
			if (cache != null)
			{
				if (cache.getAffinity() != null)
				{
					Arrays.sort(cache.getAffinity());
				}
				Arrays.sort(affinity);
				if (Arrays.equals(cache.getAffinity(), affinity))
				{
					return true;
				} else
				{
					result = WindowsServiceClass.setNativeThreadAffinity(nid,
							affinity);
					if (result)
					{
						cache.setAffinity(affinity);
					}
				}
			} else
			{
				result = WindowsServiceClass.setNativeThreadAffinity(nid,
						affinity);
				if (result)
				{
					this.cache.add(new Cache(nid, tName, affinity,
							INVALID_PRIORITY_VALUE, INVALID_POLICY_VALUE));
				}

			}
			if (!result && log.isErrorEnabled())
			{
				log.error("setJavaThreadAffinity failed to set affinity to java thread "
						+ tName + " (native ID " + nid + ").");
			}
			return result;
		} else if (log.isErrorEnabled())
		{
			log.error("setJavaThreadAffinity to java thread "
					+ tName
					+ " stopped due to invalid parameters. Please check parameters.");
		}
		return false;
	}

	// end of setJavaThreadAffinity

	@Override
	public boolean setJavaThreadAffinity(String pName, String tName,
			int[] affinity)
	{
		if (Validator.checkAffinityParams(pName, tName, affinity))
		{
			boolean result = false;
			int pid = WindowsServiceClass.getProcessIdByProcessName(pName);
			if (INVALID_ID == pid)
			{
				for (Iterator<Cache> iterator = this.cache.iterator(); iterator.hasNext();)
				{
					Cache cache = iterator.next();
					if (cache.getName().equals(tName))
						iterator.remove();
				}
				if (log.isErrorEnabled())
				{
					log.error("setJavaThreadAffinity failed to set affinity to java thread "
							+ tName
							+ " under process "
							+ pName
							+ ". Process not found.");
				}
				return false;
			}
			int nid = javaHandler.getNativeThreadId(tName, pid);
			if (INVALID_ID >= nid) // thread not found.
			{
				for (Cache cache : this.cache)
				{
					if (cache.getName().equals(tName))
						this.cache.remove(cache);
				}
				if (Constants.INVALID_ID == nid && log.isErrorEnabled())
					log.error("setJavaThreadAffinity failed to set affinity to java thread "
							+ tName
							+ " under process "
							+ pName
							+ ". Thread not found.");
				return false;
			}
			Cache cache = checkCache(nid, tName);
			if (cache != null)
			{
				if (cache.getAffinity() != null)
				{
					Arrays.sort(cache.getAffinity());
				}
				Arrays.sort(affinity);
				if (Arrays.equals(cache.getAffinity(), affinity))
				{
					return true;
				} else
				{
					result = WindowsServiceClass.setNativeThreadAffinity(nid,
							affinity);
					if (result)
					{
						cache.setAffinity(affinity);
					}
				}
			} else
			{
				result = WindowsServiceClass.setNativeThreadAffinity(nid,
						affinity);
				if (result)
				{
					this.cache.add(new Cache(nid, tName, affinity,
							INVALID_PRIORITY_VALUE, INVALID_POLICY_VALUE));
				}

			}
			if (!result && log.isErrorEnabled())
			{
				log.error("setJavaThreadAffinity failed to set affinity to java thread "
						+ tName
						+ "(native ID "
						+ nid
						+ ") under process "
						+ pName + " (ID " + pid + ").");
			}
			return result;
		} else if (log.isErrorEnabled())
		{
			log.error("setJavaThreadAffinity to java thread "
					+ tName
					+ " under process "
					+ pName
					+ " stopped due to invalid parameters. Please check parameters.");
		}
		return false;
	}
	// end of setJavaThreadAffinity

	@Override
	public boolean setJavaThreadAffinity(int pid, String tName, int[] affinity)
	{
		if (Validator.checkAffinityParams(pid, tName, affinity))
		{
			boolean result = false;
			int nid = javaHandler.getNativeThreadId(tName, pid);
			if (INVALID_ID >= nid) // thread not found.
			{
				for (Iterator<Cache> iterator = this.cache.iterator(); iterator
						.hasNext();)
				{
					Cache cache = iterator.next();
					if (cache.getName().equals(tName))
						iterator.remove();
				}
				if (INVALID_ID == nid && log.isErrorEnabled())
					log.error("setJavaThreadAffinity failed to set affinity to java thread "
							+ tName
							+ " under process with ID "
							+ pid
							+ ". Thread not found.");
				return false;
			}
			Cache cache = checkCache(nid, tName);
			if (cache != null)
			{
				if (cache.getAffinity() != null)
				{
					Arrays.sort(cache.getAffinity());
				}
				Arrays.sort(affinity);
				if (Arrays.equals(cache.getAffinity(), affinity))
				{
					return true;
				} else
				{
					result = WindowsServiceClass.setNativeThreadAffinity(nid,
							affinity);
					if (result)
					{
						cache.setAffinity(affinity);
					}
				}
			} else
			{
				result = WindowsServiceClass.setNativeThreadAffinity(nid,
						affinity);
				if (result)
				{
					this.cache.add(new Cache(nid, tName, affinity,
							INVALID_PRIORITY_VALUE, INVALID_POLICY_VALUE));
				}

			}
			if (!result && log.isErrorEnabled())
			{
				log.error("setJavaThreadAffinity failed to set affinity to java thread "
						+ tName
						+ "(native ID "
						+ nid
						+ ") under process with ID " + pid + ").");
			}
			return result;
		} else if (log.isErrorEnabled())
		{
			log.error("setJavaThreadAffinity to thread "
					+ tName
					+ " under process with ID "
					+ pid
					+ " stopped due to invalid parameters. Please check parameters.");
		}
		return false;
	}
	// end of setJavaThreadAffinity

	@Override
	public List<Result> setThreadAffinity(Collection<ThreadData> threads)
	{
		List<Result> results;

		if (threads == null || threads.isEmpty())
		{
			if (log.isErrorEnabled())
			{
				log.error("setNativeThreadAffinity failed to set affinity on a collection. Empty Collection");
			}
			return null;
		} else
		{
			results = new ArrayList<>();
		}

		for (ThreadData thread : threads)
		{
			Result threadResult;

			if (thread.getName().equals("none"))
			{
				if (thread.isJavaThread())
					threadResult = new Result(thread.getId(),
							setJavaThreadAffinity(thread.getId(),
									thread.getAffinity()));
				else
					threadResult = new Result(thread.getId(),
							setNativeThreadAffinity(thread.getId(),
									thread.getAffinity()));
			} else
			{
				if (thread.isJavaThread())
					threadResult = new Result(thread.getName(),
							setJavaThreadAffinity(thread.getName(),
									thread.getAffinity()));
				else
					threadResult = new Result(thread.getName(),
							setNativeThreadAffinity(thread.getName(),
									thread.getAffinity()));
			}
			results.add(threadResult);
		}
		return results;
	}

	// end of setThreadAffinity

	/*--------------------------- SET PRIORITY -----------------------------------------------*/

	@Override
	public boolean setProcessPriority(int pid, int priority)
	{
		if (Validator.checkPriorityParams(pid, priority))
		{
			String pname = WindowsServiceClass.getProcessNameByProcessId(pid);
			if (pname == null)
			{
				for (Iterator<Cache> iterator = this.cache.iterator(); iterator.hasNext();)
				{
					Cache cache = iterator.next();
					if (cache.getId() == pid)
						iterator.remove();
				}
				if (log.isErrorEnabled())
				{
					log.error("setProcessPriority failed to set priority to process "
							+ pid + ". Process not found.");
				}
				return false;
			}
			int normalizedPriority = normalizer.normalize(priority, true);
			boolean result = false;
			Cache cache = checkCache(pid, pname);
			if (cache != null)
			{
				if (cache.getPriority() == normalizedPriority)
				{
					return true;
				} else
				{
					result = WindowsServiceClass.setProcessPriority(pid,
							normalizedPriority);
					if (result)
					{
						cache.setPriority(normalizedPriority);
					}
				}
			} else if (null == cache)
			{
				result = WindowsServiceClass.setProcessPriority(pid,
						normalizedPriority);
				if (result)
				{
					this.cache.add(new Cache(pid, pname, INVALID_ARRAY,
							normalizedPriority, INVALID_POLICY_VALUE));
				}
			}
			if (!result && log.isErrorEnabled())
			{
				log.error("setProcessPriority failed to set priority to process with ID "
						+ pid + ".");
			}
			return result;
		} else if (log.isErrorEnabled())
		{
			log.error("setProcessPriority to process with ID "
					+ pid
					+ " stopped due to invalid parameters. Please check parameters.");
		}
		return false;
	}
	// end of setProcessPriority

	@Override
	public boolean setProcessPriority(String pName, int priority)
	{
		if (Validator.checkPriorityParams(pName, priority))
		{
			boolean result = false;
			int normalizedPriority = normalizer.normalize(priority, true);
			int processId = WindowsServiceClass.getProcessIdByProcessName(pName);
			if (INVALID_ID == processId)
			{
				for (Iterator<Cache> iterator = this.cache.iterator(); iterator.hasNext();)
				{
					Cache cache = iterator.next();
					if (cache.getName().equals(pName))
						iterator.remove();
				}
				if (log.isErrorEnabled())
				{
					log.error("setProcessPriority failed to set priority to process "
							+ pName + ". Process not found.");
				}
				return false;
			}
			Cache cache = checkCache(processId, pName);
			if (cache != null)
			{
				if (cache.getPriority() == normalizedPriority)
				{
					return true;
				} else
				{
					result = WindowsServiceClass.setProcessPriority(processId,
							normalizedPriority);
					if (result)
					{
						cache.setPriority(normalizedPriority);
					}
				}
			} else if (null == cache)
			{
				result = WindowsServiceClass.setProcessPriority(processId,
						normalizedPriority);
				if (result)
				{
					this.cache.add(new Cache(processId, pName, INVALID_ARRAY,
							normalizedPriority, INVALID_POLICY_VALUE));
				}
			}
			if (!result && log.isErrorEnabled())
			{
				log.error("setProcessPriority failed to set priority to process "
						+ pName + " with ID " + processId);
			}
			return result;
		} else if (log.isErrorEnabled())
		{
			log.error("setProcessPriority to process "
					+ pName
					+ " stopped due to invalid parameters. Please check parameters.");
		}
		return false;
	}
	// end of setProcessPriority

	@Override
	public List<ProcessResult> setProcessPriority(Collection<ProcessData> processes)
	{
		List<ProcessResult> procResults;

		if (processes == null || processes.isEmpty())
		{
			if (log.isErrorEnabled())
			{
				log.error("setProcessPriority failed to set priority on a collection. Empty Collection");
			}
			return null;
		} else
		{
			procResults = new ArrayList<>();
		}

		for (ProcessData process : processes)
		{
			ProcessResult result;

			if (process.getName().equals("none"))
				result = new ProcessResult(process.getId(), setProcessPriority(
						process.getId(), process.getPriority()));
			else
				result = new ProcessResult(process.getName(),
						setProcessPriority(process.getName(),
								process.getPriority()));

			// CHECKS IF PROCESS HAS THREAD LIST, IF IT DOES, SET THREADS
			// PRIORITY AS WELL.
			if (process.getThreads() != null || !process.getThreads().isEmpty())
			{
				for (ThreadData thread : process.getThreads())
				{
					if (thread.getName().equals("none"))
					{
						if (thread.isJavaThread())
							result.addThread(
									thread.getId(),
									setJavaThreadPriority(thread.getId(),
											thread.getPriority()));
						else
							result.addThread(
									thread.getId(),
									setNativeThreadPriority(thread.getId(),
											thread.getPriority()));
					} else
					{
						if (thread.isJavaThread())
							result.addThread(
									thread.getName(),
									setJavaThreadPriority(thread.getName(),
											thread.getPriority()));
						else
							result.addThread(
									thread.getName(),
									setNativeThreadPriority(thread.getName(),
											thread.getPriority()));
					}
				}
			}
			procResults.add(result);
		}
		return procResults;
	}

	// end of setProcessPriority

	@Override
	public boolean setNativeThreadPriority(int tid, int priority)
	{
		if (Validator.checkPriorityParams(tid, priority))
		{
			boolean result = false;
			int normalizedPriority = normalizer.normalize(priority, true);
			Cache cache = checkCache(tid, INVALID_NAME);
			if (cache != null)
			{
				if (cache.getPriority() == normalizedPriority)
				{
					return true;
				} else
				{
					result = WindowsServiceClass.setNativeThreadPriority(tid,
							normalizedPriority);
					if (result)
					{
						cache.setPriority(normalizedPriority);
					}
				}
			} else
			{
				result = WindowsServiceClass.setNativeThreadPriority(tid,
						normalizedPriority);
				if (result)
				{
					this.cache.add(new Cache(tid, INVALID_NAME, INVALID_ARRAY,
							normalizedPriority, INVALID_POLICY_VALUE));
				}
			}
			if (!result && log.isErrorEnabled())
			{
				log.error("setNativeThreadPriority failed to set priority to thread with ID "
						+ tid + ".");
			}
			return result;
		} else if (log.isErrorEnabled())
		{
			log.error("setNativeThreadPriority to thread with ID "
					+ tid
					+ " stopped due to invalid parameters. Please check parameters.");
		}
		return false;
	}
	// end of setNativeThreadPriority

	@Override
	public boolean setNativeThreadPriority(String tName, int priority)
	{
		if (log.isErrorEnabled())
		{
			log.error("setNativeThreadPriority failed to set priority to thread "
					+ tName + ". Windows was unable to determine thread name.");
		}
		return false;
	}

	// end of setNativeThreadPriority

	@Override
	public boolean setNativeThreadPriority(String pName, String tName,
			int priority)
	{
		if (log.isErrorEnabled())
		{
			log.error("setNativeThreadPriority failed to set priority to thread "
					+ tName + ". Windows was unable to determine thread name.");
		}
		return false;
	}

	@Override
	public boolean setNativeThreadPriority(int pid, String tName, int priority)
	{
		if (log.isErrorEnabled())
		{
			log.error("setNativeThreadPriority failed to set priority to thread "
					+ tName + ". Windows was unable to determine thread name.");
		}
		return false;
	}

	@Override
	public List<Result> setThreadPriority(Collection<ThreadData> threads)
	{
		List<Result> threadResults;

		if (threads == null || threads.isEmpty())
		{
			if (log.isErrorEnabled())
			{
				log.error("setNativeThreadPriority failed to set priority on a collection. Empty Collection");
			}
			return null;
		} else
		{
			threadResults = new ArrayList<>();
		}

		for (ThreadData thread : threads)
		{
			Result result;
			if (thread.getName().equals("none"))
			{
				if (thread.isJavaThread())
					result = new Result(thread.getId(), setJavaThreadPriority(
							thread.getId(), thread.getPriority()));
				else
					result = new Result(thread.getId(),
							setNativeThreadPriority(thread.getId(),
									thread.getPriority()));
			} else
			{
				if (thread.isJavaThread())
					result = new Result(thread.getName(),
							setJavaThreadPriority(thread.getName(),
									thread.getPriority()));
				else
					result = new Result(thread.getName(),
							setNativeThreadPriority(thread.getName(),
									thread.getPriority()));
			}
			threadResults.add(result);
		}

		return threadResults;
	}
	// end of setNativeThreadPriority

	@Override
	public boolean setJavaThreadPriority(int tid, int priority)
	{
		if (Validator.checkPriorityParams(tid, priority))
		{
			boolean result = false;
			String tName = javaHandler.getThreadName(tid);
			if (tName.equals("not found"))
			{
				if (log.isErrorEnabled())
				{
					log.error("setJavaThreadPriority failed to set priority to java thread with ID "
							+ tid + ". Thread not found.");
				}
				return false;
			}
			int nid = javaHandler.getNativeThreadId(tName);
			if (INVALID_ID >= nid) // thread not found.
			{
				for (Iterator<Cache> iterator = this.cache.iterator(); iterator.hasNext();)
				{
					Cache cache = iterator.next();
					if (cache.getName().equals(tName))
						iterator.remove();
				}
				if (INVALID_ID == nid)
					if (log.isErrorEnabled())
					{
						log.error("setJavaThreadPriority failed to set priority to java thread with ID "
								+ tid + ". Thread not found.");
					}
				return false;
			}
			Cache cache = checkCache(nid, tName);
			int normalizedPriority = normalizer.normalize(priority, false);
			if (cache != null)
			{
				if (cache.getPriority() == normalizedPriority)
				{
					return true;
				} else
				{
					result = WindowsServiceClass.setNativeThreadPriority(nid,
							normalizedPriority);
					if (result)
					{
						cache.setPriority(normalizedPriority);
					}
				}
			} else
			{
				result = WindowsServiceClass.setNativeThreadPriority(nid,
						normalizedPriority);
				if (result)
				{
					this.cache.add(new Cache(nid, tName, INVALID_ARRAY,
							normalizedPriority, INVALID_POLICY_VALUE));
				}
			}
			if (!result && log.isErrorEnabled())
			{
				log.error("setJavaThreadPriority failed to set priority to java thread "
						+ tName
						+ " with ID "
						+ tid
						+ "(native ID "
						+ nid
						+ ").");
			}
			return result;
		} else if (log.isErrorEnabled())
		{
			log.error("setJavaThreadPriority to thread with ID "
					+ tid
					+ " stopped due to invalid parameters. Please check parameters.");
		}
		return false;
	}
	// end of setJavaThreadPriority

	@Override
	public boolean setJavaThreadPriority(String tName, int priority)
	{
		if (Validator.checkPriorityParams(tName, priority))
		{
			boolean result = false;
			int nid = javaHandler.getNativeThreadId(tName);
			if (INVALID_ID >= nid) // thread not found, or jstack failure.
			{
				for (Iterator<Cache> iterator = this.cache.iterator(); iterator.hasNext();)
				{
					Cache cache = iterator.next();
					if (cache.getName().equals(tName))
						iterator.remove();
				}
				if (INVALID_ID == nid && log.isErrorEnabled())
				{
					log.error("setJavaThreadPriority failed to set priority to java thread "
							+ tName + ". Thread not found.");
				}
				return false;
			}
			Cache cache = checkCache(nid, tName);
			int normalizedPriority = normalizer.normalize(priority, false);
			if (cache != null)
			{
				if (cache.getPriority() == normalizedPriority)
				{
					return true;
				} else
				{
					result = WindowsServiceClass.setNativeThreadPriority(nid,
							normalizedPriority);
					if (result)
					{
						cache.setPriority(normalizedPriority);
					}
				}
			} else
			{
				result = WindowsServiceClass.setNativeThreadPriority(nid,
						normalizedPriority);
				if (result)
				{
					this.cache.add(new Cache(nid, tName, INVALID_ARRAY,
							normalizedPriority, INVALID_POLICY_VALUE));
				}
			}
			if (!result && log.isErrorEnabled())
			{
				log.error("setJavaThreadPriority failed to set priority to java thread "
						+ tName + " (native ID " + nid + ").");
			}
			return result;
		} else if (log.isErrorEnabled())
		{
			log.error("setJavaThreadPriority to thread "
					+ tName
					+ " stopped due to invalid parameters. Please check parameters.");
		}
		return false;
	}

	// end of setJavaThreadPriority

	@Override
	public boolean setJavaThreadPriority(String pName, String tName,
			int priority)
	{
		if (Validator.checkPriorityParams(pName, tName, priority))
		{
			boolean result = false;
			int pid = WindowsServiceClass.getProcessIdByProcessName(pName);
			if (INVALID_ID == pid)
			{
				for (Iterator<Cache> iterator = this.cache.iterator(); iterator
						.hasNext();)
				{
					Cache cache = iterator.next();
					if (cache.getName().equals(pName))
						iterator.remove();
				}
				if (log.isErrorEnabled())
				{
					log.error("setJavaThreadPriority failed to set priority to java thread "
							+ tName
							+ " under process "
							+ pName
							+ ". Process not found.");
				}
				return false;
			}
			int nid = javaHandler.getNativeThreadId(tName, pid);
			if (INVALID_ID >= nid)
			{
				for (Iterator<Cache> iterator = this.cache.iterator(); iterator
						.hasNext();)
				{
					Cache cache = iterator.next();
					if (cache.getName().equals(tName))
						iterator.remove();
				}
				if (INVALID_ID == nid && log.isErrorEnabled())
				{
					log.error("setJavaThreadPriority failed to set priority to java thread "
							+ tName
							+ " under process "
							+ pName
							+ ". Thread not found.");
				}
				return false;
			}
			int normalizedPriority = normalizer.normalize(priority, false);
			Cache cache = checkCache(nid, tName);
			if (cache != null)
			{
				if (cache.getPriority() == normalizedPriority)
				{
					return true;
				} else
				{
					result = WindowsServiceClass.setNativeThreadPriority(nid,
							normalizedPriority);
					if (result)
					{
						cache.setPriority(normalizedPriority);
					}
				}
			} else
			{
				result = WindowsServiceClass.setNativeThreadPriority(nid,
						normalizedPriority);
				if (result)
				{
					this.cache.add(new Cache(nid, tName, INVALID_ARRAY,
							normalizedPriority, INVALID_POLICY_VALUE));
				}
			}
			if (!result && log.isErrorEnabled())
			{
				log.error("setJavaThreadPriority failed to set priority to java thread "
						+ tName
						+ " (native ID "
						+ nid
						+ ") under process "
						+ pName + " (ID " + pid + ").");
			}
			return result;
		} else if (log.isErrorEnabled())
		{
			log.error("setJavaThreadPriority to thread "
					+ tName
					+ " under process "
					+ pName
					+ " stopped due to invalid parameters. Please check parameters.");
		}
		return false;
	}

	@Override
	public boolean setJavaThreadPriority(int pid, String tName, int priority)
	{
		if (Validator.checkPriorityParams(pid, tName, priority))
		{
			boolean result = false;
			int nid = javaHandler.getNativeThreadId(tName, pid);
			if (INVALID_ID >= nid) // thread not found.
			{
				for (Iterator<Cache> iterator = this.cache.iterator(); iterator
						.hasNext();)
				{
					Cache cache = iterator.next();
					if (cache.getName().equals(tName))
						iterator.remove();
				}
				if (INVALID_ID == nid && log.isErrorEnabled())
					log.error("setJavaThreadPriority failed to set priority to java thread "
							+ tName
							+ " under process with ID "
							+ pid
							+ ". Thread not found.");
				return false;
			}
			int normalizedPriority = normalizer.normalize(priority, false);
			Cache cache = checkCache(nid, tName);
			if (cache != null)
			{
				if (cache.getPriority() == normalizedPriority)
				{
					return true;
				} else
				{
					result = WindowsServiceClass.setNativeThreadPriority(nid,
							normalizedPriority);
					if (result)
					{
						cache.setPriority(normalizedPriority);
					}
				}
			} else
			{
				result = WindowsServiceClass.setNativeThreadPriority(nid,
						normalizedPriority);
				if (result)
				{
					this.cache.add(new Cache(nid, tName, INVALID_ARRAY,
							normalizedPriority, INVALID_POLICY_VALUE));
				}
			}
			if (!result && log.isErrorEnabled())
			{
				log.error("setJavaThreadPriority failed to set priority to java thread "
						+ tName
						+ " (native ID "
						+ nid
						+ ") under process with ID " + pid + ".");
			}
			return result;
		} else if (log.isErrorEnabled())
		{
			log.error("setJavaThreadPriority to thread "
					+ tName
					+ " under process with ID "
					+ pid
					+ " stopped due to invalid parameters. Please check parameters.");
		}
		return false;
	}
	// end of setJavaThreadPriority
}
// end of class
