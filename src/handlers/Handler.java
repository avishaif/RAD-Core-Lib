package handlers;

import java.util.Collection;
import java.util.List;

import core.ProcessData;
import core.Results;
import core.ThreadData;

public abstract class Handler 
{
	public abstract int getProcessorCount();

//	
// AFFINITY	
	public abstract boolean setProcessAffinity(int pid, int[] affinity);
	public abstract boolean setProcessAffinity(String pName, int[] affinity);
	public abstract List<Results> setProcessAffinity(Collection<ProcessData> processes);

	public abstract boolean setNativeThreadAffinity(int tid, int[] affinity);
	public abstract boolean setNativeThreadAffinity(String tName, int[] affinity);
	public abstract boolean setNativeThreadAffinity(String pName, String tName, int[] affinity);
	public abstract List<Results> setNativeThreadAffinity(Collection<ThreadData> threads);
	
	public abstract boolean setJavaThreadAffinity(int tid, int[] affinity);
	public abstract boolean setJavaThreadAffinity(String tName, int[] affinity);
	public abstract boolean setJavaThreadAffinity(int pid, int tid, int[] affinity);
	public abstract boolean setJavaThreadAffinity(String pName, String tName, int[] affinity);
	
//
// PRIORITY	
	public abstract boolean setProcessPriority(int pid, int policy, int priority);	
	public abstract boolean setProcessPriority(String pName, int policy, int priority);	
	public abstract List<Results> setProcessPriority(Collection<ProcessData> processes);

	public abstract boolean setNativeThreadPriority(int tid, int policy, int priority);
	public abstract boolean setNativeThreadPriority(String tName, int policy, int priority);	
	public abstract boolean setNativeThreadPriority(String pName, String tName, int policy, int priority);
	public abstract boolean setNativeThreadPriority(int pid, String tName, int policy, int priority);
	public abstract List<Results> setNativeThreadPriority(Collection<ThreadData> threads);
	
	public abstract boolean setJavaThreadPriority(int tid, int policy, int priority);
	public abstract boolean setJavaThreadPriority(String tName, int policy, int priority);
	public abstract boolean setJavaThreadPriority(int pid, int tid, int policy, int priority);
	public abstract boolean setJavaThreadPriority(String pName, String tName, int policy, int priority);
	public abstract boolean setJavaThreadPriority(int pid, String tName, int policy, int priority);
}



