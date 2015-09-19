package javathreadshandlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JavaThreadHandlerWindows extends JavaThreadHandler
{
	static Logger log = LogManager.getRootLogger();


	

	/**
	 * Retrieve a native thread id of a java using a thread name.<br>
	 * Function will return the first thread nid with given name found.
	 * @param tName String: Thread name
	 * @param jvmIds List<Integer>: A list of java process IDs.
	 * @return An Integer number of the native thread id. <br> 
	 * -1 is returned if thread was not found.
	 * @throws IOException
	 */
	public int getNativeThreadId(String tName, List<Integer> jvmIds) {
		List<Integer> jvm = jvmIds;
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return -1;
	}
	
	/**
	 * Retrieve a native thread id of a java using a thread id. <br>
	 * Function applies *only* for threads of the hosting JMV.
	 * @param tid Integer: Thread id
	 * @param jvmIds List<Integer>: A list of java process IDs.
	 * @return An Integer number of the native thread id. <br> 
	 * -1 is returned if thread was not found.
	 */
	public int getNativeThreadId(int tid, List<Integer> jvmIds) 
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
		
		return getNativeThreadId(tName, jvmIds);
	}
}
