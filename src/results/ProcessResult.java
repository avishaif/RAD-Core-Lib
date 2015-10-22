package results;

import java.util.ArrayList;
import java.util.List;


public class ProcessResult extends Result
{
	List<Result> threadResults;

	/**
	 * Constructor initiates class by a process id.
	 * @param id Process or thread id
	 * @param result True if operation was successful, false otherwise.
	 */
	public ProcessResult(int id, boolean result) 
	{
		super(id, result);
	}

	/**
	 * Constructor initiates class by a process name.
	 * @param name Process or a thread name
	 * @param result True if operation was successful, false otherwise.
	 */
	public ProcessResult(String name, boolean result) 
	{
		super(name, result);
	}

	/**
	 * Initializes a process thread list if is null, adds a thread result
	 * @param id Threads id
	 * @param result True if operation was successful false otherwise
	 */
	public void addThread(int id, boolean result) {

		Result thread = new Result(id, result);
		if (threadResults == null)
			threadResults = new ArrayList<Result>();
		threadResults.add(thread);

	}

	/**
	 * Initializes a process thread list if is null, adds a thread result
	 * @param name Threads name
	 * @param result True if operation was successful false otherwise
	 */
	public void addThread(String name, boolean result) {

		Result thread = new Result(name, result);
		if (threadResults == null)
			threadResults = new ArrayList<Result>();
		threadResults.add(thread);
	}

	/**
	 * Checks if a process has threads.
	 * @return
	 * 		true if process result contain a list of thread results.
	 */
	public boolean hasThreads() {
		if (this.threadResults != null)
			return true;
		return false;
	}

	/**
	 * Get all thread results.
	 * @return
	 * 		List of Result type, each record represents thread result.
	 */
	public List<Result> getThreads() {
		return this.threadResults;
	}
}





















