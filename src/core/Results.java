package core;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Avishai Fox
 *
 */
public class Results 
{

	private String name;
	private int id;
	private boolean result;
	List<Results> threads;

	/**
	 * 
	 * @param id Process or thread id
	 * @param result True if operation was successful, false otherwise.
	 */
	public Results(int id, boolean result) {
		this.id = id;
		this.result = result;

	}

	/**
	 * 
	 * @param name Process or a thread name
	 * @param result True if operation was successful, false otherwise.
	 */
	public Results(String name, boolean result) {
		this.setName(name);
		this.result = result;
	}

	public boolean getResult() {
		return result;
	}

	public int getId() {
		return id;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Initialises a process thread list if is null, adds a thread result
	 * @param id Threads id
	 * @param result True if operation was successful false otherwise
	 */
	public void addThread(int id, boolean result) {

		Results thread = new Results(id, result);
		if (threads == null)
			threads = new ArrayList<Results>();
		threads.add(thread);

	}

	public void addThread(String name, boolean result) {

		Results thread = new Results(name, result);
		if (threads == null)
			threads = new ArrayList<Results>();
		threads.add(thread);

	}

	public boolean hasThreads() {
		if (this.threads != null)
			return true;
		return false;
	}

	public List<Results> getThreads() {
		return this.threads;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}