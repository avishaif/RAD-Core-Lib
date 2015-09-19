package javathreadshandlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JavaThreadHandlerLinux extends JavaThreadHandler{

	Logger log = LogManager.getRootLogger();

	/**
	 * Retrieve all running jvms process ids
	 * @return Integer List: containing all active jvms process ids.
	 */
	
	@Override
	public List<Integer> getAllJvmsPids() {
		List<Integer> jvm = new ArrayList<>();
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
					if (words[1].equals("java")) {
						while ((line = br.readLine()) != null) {
							words = line.split(":\t");
							if (words[0].equals("Pid")) {
								jvm.add(Integer.parseInt(words[1]));
								break;
							}
						}
					} else
						continue;

				} catch (FileNotFoundException e) {
					continue;

				} catch (IOException e) {
					if (log.isErrorEnabled()) {
						log.error(e);
					}
				}
			}
		}
		if (jvm.size() == 0 && log.isErrorEnabled()) {
			log.error("No jvm was found");
			return null;
		} else {
			try {
				br.close();
			} catch (IOException e) {
				
			}
			return jvm;
		}
	}

	/**
	 * Retrieve a native thread id of a java using a thread name.<br>
	 * Function will return the first thread nid with given name found.
	 * @param tName String: Thread name
	 * @return An Integer number of the native thread id. <br> 
	 * -1 is returned if thread was not found.
	 * @throws IOException
	 */
	@Override
	public int getNativeThreadId(String tName) {
		List<Integer> jvm = getAllJvmsPids();
		int tid;
		for (Integer ids : jvm) {
			Process p = null;
			try {
				p = Runtime.getRuntime().exec("jstack " + ids);
			} catch (IOException e) {
				
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			try {
				while ((line = reader.readLine()) != null) {
					if (line.startsWith('"' + tName + '"')) {
						int index = line.indexOf("nid=0x");
						String nid = line.substring(index + 6);
						nid = nid.substring(0, nid.indexOf(" "));
						tid = (int) Long.parseLong(nid, 16);
						return tid;
					}
				}
			} catch (NumberFormatException | IOException e) {
				e.printStackTrace();
			}
		}

		return -1;
	}
	
	/**
	 * 
	 * @param tName String Thread name
	 * @param pid Int Process id
	 * @return Int Thread native id.
	 */
	public int getNativeThreadId(String tName, int pid)
	{
		
		int tid;
		Process p = null;
		try {
			p = Runtime.getRuntime().exec("jstack " + pid);
		} catch (IOException e) {
			
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				if (line.startsWith('"' + tName + '"')) {
					int index = line.indexOf("nid=0x");
					String nid = line.substring(index + 6);
					nid = nid.substring(0, nid.indexOf(" "));
					tid = (int) Long.parseLong(nid, 16);
					return tid;
				}
			}
		}
		catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
		
		return -1;
	}
		/**
	 * Retrieve a native thread id of a java using a thread id. <br>
	 * Function applies *only* for threads of the hosting JMV.
	 * @param tid Integer: Thread id
	 * @return An Integer number of the native thread id. <br> 
	 * -1 is returned if thread was not found.
	 */
	
	public int getNativeThreadId(int tid) {
		String tName = getThreadName(tid);
		if(tName.equals("not found"))
		{
			if(log.isErrorEnabled())
			{
				log.error("Thread with ID " + tid + " not found.");
			}
			
			return -1;
		}
		
		return getNativeThreadId(tName);
	}
	
	/**
	 * 
	 * @param pid Int Process id
	 * @param tid Int Java Thread id
	 * @return Int Native Thread id
	 */
	public int getNativeThreadIdByProcess(int pid, int tid)
	{
		String tName = getThreadName(tid);
		if(tName.equals("not found"))
		{
			if(log.isErrorEnabled())
			{
				log.error("Thread with ID " + tid + " not found.");
			}
			
			return -1;
		}
		
		return getNativeThreadId(tName, pid);
	}
}
