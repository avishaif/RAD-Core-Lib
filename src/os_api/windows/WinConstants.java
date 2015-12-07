package os_api.windows;

/**
 * 
 * This class contains constant values which used by Windows operating system.
 *
 */

public class WinConstants
{
	
	//
	// PROCESS ACCESS CONSTANTS
	public final static int PROCESS_ALL_ACCESS 	      = 0x001F0FFF;
	public final static int PROCESS_TERMINATE         = 0x00000001;
	public final static int PROCESS_SET_INFORMATION   = 0x00000200;
	public final static int PROCESS_QUERY_INFORMATION = 0x00000400;
	public final static int PROCESS_VM_READ           = 0x00000010;
	
	public final static int TH32CS_SNAPTHREAD = 0x00000004; // value used to create ToolHelp32Snapshot. 
	
	//
	// PROCESS PRIORITY CONSTANTS
	//
	// ATTRIBUTES BELOW MOVED TO CONSTANTS CLASS
	/*
	public final static int IDLE_PRIORITY_CLASS         = 0x0000040; // value = 64
	public final static int BELOW_NORMAL_PRIORITY_CLASS = 0x0004000; // value = 16384
	public final static int NORMAL_PRIORITY_CLASS       = 0x0000020; // value = 32
	public final static int ABOVE_NORMAL_PRIORITY_CLASS = 0x0008000; // value = 32768
	public final static int HIGH_PRIORITY_CLASS         = 0x0000080; // value = 128
	public final static int REALTIME_PRIORITY_CLASS     = 0x0000100; // value = 256
	*/
	
	//
	// THREAD ACCESS CONSTANTS
	public final static int SYNCHRONIZE                 = 0x00100000;
	public final static int THREAD_ALL_ACCESS           = 0x1F03FF;
	public final static int THREAD_DIRECT_IMPERSONATION = 0x0200;
	public final static int THREAD_GET_CONTEXT          = 0x0008;
	public final static int THREAD_IMPERSONATE          = 0x0100;
	public final static int THREAD_QUERY_INFORMATION    = 0x0040;
	public final static int THREAD_SET_CONTEXT          = 0x0010;
	public final static int THREAD_SET_INFORMATION      = 0x0020;
	public final static int THREAD_SET_THREAD_TOKEN     = 0x0080;
	public final static int THREAD_SUSPEND_RESUME       = 0x0002;
	public final static int THREAD_TERMINATE            = 0x0001;
		
	
	//
	// THREAD PRIORITY CONSTANTS
	//
	// ATTRIBUTES BELOW MOVED TO CONSTANTS CLASS
	/*
	public final static int THREAD_PRIORITY_IDLE          = -15;
	public final static int THREAD_PRIORITY_LOWEST        = -2;
	public final static int THREAD_PRIORITY_BELOW_NORMAL  = -1;
	public final static int THREAD_PRIORITY_NORMAL        = 0;
	public final static int THREAD_PRIORITY_ABOVE_NORMAL  = 1;
	public final static int THREAD_PRIORITY_HIGHEST       = 2;
	public final static int THREAD_PRIORITY_TIME_CRITICAL = 15;
	*/
	
	
}



