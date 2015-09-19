package unitTests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import core.ProcessData;
import core.Results;
import core.ThreadData;
import handlers.LinuxHandler;

public class LinuxHandlerCollectionTest {
private LinuxHandler handler;

@Before
public void init()
{
	handler = new LinuxHandler();
}
	@Test
	public void testsetProcessPriority_ProcessData() {
		System.out.println("testsetProcessPriority_ProcessData");
		List<Results> results = new ArrayList<Results>();
		List<ProcessData> processList = new ArrayList<>();
		ThreadData thread = new ThreadData(-1, false, 0, null);
		ProcessData process = new ProcessData(-1, 0,null);
		Results result = new Results(process.getId(), false);
		result.addThread(thread.getId(), false);
		results.add(result);
		process.addThread(thread);
		processList.add(process);
		process = new ProcessData("systemd", 0, null);
		thread = new ThreadData("systemd", false, 0, null);
		result = new Results(process.getName(), false);
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
		List<Results> results = new ArrayList<Results>();
		Collection<ThreadData> threadList = new ArrayList<>();
		ThreadData thread = new ThreadData(-1, false, 0, null);
		Results result = new Results(thread.getId(), false);
		results.add(result);
		threadList.add(thread);
		thread = new ThreadData("systemd", false, 0, null);
		result = new Results(thread.getName(), false);
		results.add(result);
		threadList.add(thread);
		assertEquals(true,
				compare(results, handler.setNativeThreadPriority(threadList)));
	}

	@Test
	public void testsetProcessAffinity_ProcessData() {
		System.out.println("testsetProcessPriority_ProcessData");
		List<Results> results = new ArrayList<Results>();
		Collection<ProcessData> processList = new ArrayList<>();
		ThreadData thread = new ThreadData(-1, false, 0, null);
		ProcessData process = new ProcessData(-1, 0, null);
		Results result = new Results(process.getId(), false);
		result.addThread(thread.getId(), false);
		results.add(result);
		process.addThread(thread);
		processList.add(process);
		process = new ProcessData("systemd", 0, null);
		thread = new ThreadData("systemd", false, 0, null);
		result = new Results(process.getName(), false);
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
		List<Results> results = new ArrayList<Results>();
		Collection<ThreadData> threadList = new ArrayList<>();
		ThreadData thread = new ThreadData(-1, false, 0, null);
		Results result = new Results(thread.getId(), false);
		results.add(result);
		threadList.add(thread);
		thread = new ThreadData("systemd", false, 0, null);
		result = new Results(thread.getName(), false);
		results.add(result);
		threadList.add(thread);
		assertEquals(true,
				compare(results, handler.setNativeThreadAffinity(threadList)));
	}

	private boolean compare(List<Results> one, List<Results> two) {
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

}
