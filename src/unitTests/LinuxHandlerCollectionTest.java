package unitTests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import results.ProcessResult;
import results.Result;
import core.ProcessData;
import core.ThreadData;
import handlers.LinuxHandler;
import normalizers.Normalizer;

public class LinuxHandlerCollectionTest {
	private LinuxHandler handler;
	Normalizer normalizer;
	@Before
	public void init()
    {
    	normalizer = new Normalizer("Linux");
    	handler = new LinuxHandler(normalizer);
    }
	@Test
	public void testsetProcessPriority_ProcessData() {
		System.out.println("testsetProcessPriority_ProcessData");
		List<ProcessResult> results = new ArrayList<>();
		List<ProcessData> processList = new ArrayList<>();
		ThreadData thread = new ThreadData(-1, false, 1, null);
		ProcessData process = new ProcessData(-1, 1, null);
		ProcessResult result = new ProcessResult(process.getId(), false);
		result.addThread(thread.getId(), false);
		results.add(result);
		process.addThread(thread);
		processList.add(process);
		process = new ProcessData("systemd", 1, null);
		thread = new ThreadData("systemd", false, 1, null);
		result = new ProcessResult(process.getName(), false);
		result.addThread(thread.getName(), false);
		results.add(result);
		process.addThread(thread);
		processList.add(process);
		assertEquals(true,
				compare(results, handler.setProcessPriority(processList)));
	}

	@Test
	public void testsetThreadPriority_ThreadData() {
		System.out.println("testsetProcessPriority_ProcessData");
		List<Result> results = new ArrayList<>();
		Collection<ThreadData> threadList = new ArrayList<>();
		ThreadData thread = new ThreadData(-1, false, 1, null);
		Result result = new Result(thread.getId(), false);
		results.add(result);
		threadList.add(thread);
		thread = new ThreadData("systemd", false, 1, null);
		result = new Result(thread.getName(), false);
		results.add(result);
		threadList.add(thread);
		assertEquals(
				true,
				compareResult(results,
						handler.setThreadPriority(threadList)));
	}

	@Test
	public void testsetProcessAffinity_ProcessData() {
		System.out.println("testsetProcessPriority_ProcessData");
		List<ProcessResult> results = new ArrayList<>();
		Collection<ProcessData> processList = new ArrayList<>();
		ThreadData thread = new ThreadData(-1, false, 1, null);
		ProcessData process = new ProcessData(-1, 1, null);
		ProcessResult result = new ProcessResult(process.getId(), false);
		result.addThread(thread.getId(), false);
		results.add(result);
		process.addThread(thread);
		processList.add(process);
		process = new ProcessData("systemd", 1, null);
		thread = new ThreadData("systemd", false, 1, null);
		result = new ProcessResult(process.getName(), false);
		result.addThread(thread.getName(), false);
		results.add(result);
		process.addThread(thread);
		processList.add(process);
		assertEquals(true,
				compare(results, handler.setProcessAffinity(processList)));
	}

	@Test
	public void testsetThreadAffinity_ThreadData() {
		System.out.println("testsetProcessPriority_ProcessData");
		List<Result> results = new ArrayList<>();
		Collection<ThreadData> threadList = new ArrayList<>();
		ThreadData thread = new ThreadData(-1, false, 1, null);
		Result result = new Result(thread.getId(), false);
		results.add(result);
		threadList.add(thread);
		thread = new ThreadData("systemd", false, 1, null);
		result = new Result(thread.getName(), false);
		results.add(result);
		threadList.add(thread);
		assertEquals(
				true,
				compareResult(results,
						handler.setThreadAffinity(threadList)));
	}

	private boolean compare(List<ProcessResult> one, List<ProcessResult> two) {
		if (one.size() != two.size())
			return false;
		else {
			for (int i = 0; i < one.size(); i++) {
				if (one.get(i).getId() != two.get(i).getId())
					return false;
				if (one.get(i).getResult() != two.get(i).getResult())
					return false;
				if (one.get(i).hasThreads() && two.get(i).hasThreads()) {
					if (one.get(i).getThreads().size() != two.get(i)
							.getThreads().size())
						return false;
					for (int j = 0; j < one.get(i).getThreads().size(); j++) {
						if (one.get(i).getThreads().get(j).getId() != two
								.get(i).getThreads().get(j).getId())
							return false;
						if (one.get(i).getThreads().get(j).getResult() != two
								.get(i).getThreads().get(j).getResult())
							return false;
					}
				}

			}
		}

		return true;
	}

	private boolean compareResult(List<Result> one, List<Result> two) {
		if (one.size() != two.size())
			return false;
		else {
			for (int i = 0; i < one.size(); i++) {
				if (one.get(i).getId() != two.get(i).getId())
					return false;
				if (one.get(i).getName() != null && two.get(i).getName() != null)
					if (!one.get(i).getName().equals(two.get(i).getName()))
						return false;
				if (one.get(i).getResult() != two.get(i).getResult())
					return false;
			}
		}
		return true;
	}
}
	
