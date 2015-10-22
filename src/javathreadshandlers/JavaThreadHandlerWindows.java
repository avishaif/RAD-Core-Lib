package javathreadshandlers;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Tlhelp32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;

import os_api.windows.Kernel32;

public class JavaThreadHandlerWindows extends JavaThreadHandler
{
	static ThreadGroup rootThreadGroup = null;
	static Logger log = LogManager.getRootLogger();
	private Kernel32 kernel32;

	public JavaThreadHandlerWindows(Kernel32 kernel32Instance)
	{
		kernel32 = kernel32Instance;
	}
	

	/**
	 * Retrieve all running jvms process ids.
	 * 
	 * @return Integer List: containing all active jvms process ids.
	 */
	@Override
	public List<Integer> getAllJvmsPids()
	{
		List<Integer> jvmIds = new ArrayList<Integer>();
		Tlhelp32.PROCESSENTRY32.ByReference processEntry = new Tlhelp32.PROCESSENTRY32.ByReference();
		WinNT.HANDLE snapshot = kernel32.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPPROCESS, new WinDef.DWORD(0));
		try 
		{
			// GET THE LIST OF RUNNING PROCESSES.
		    while (kernel32.Process32Next(snapshot, processEntry)) 
		    {
		    	if(Native.toString(processEntry.szExeFile).contains("java"))
		    	{
		    		jvmIds.add(processEntry.th32ProcessID.intValue());
		    	}
		    }
		}
		finally 
		{
			kernel32.CloseHandle(snapshot);
		}
		if (jvmIds.size() == 0 && log.isErrorEnabled())
		{
			log.error("No jvm was found");
			return null;
		}
		return jvmIds;
	}
}

	






