package core;


/**
 * Constants class contains constant values used in the program.
 */
public final class Constants 
{
	public static final int INVALID_PRIORITY_VALUE = 0xfffffc19; // value = -999
	public static final int INVALID_POLICY_VALUE = -999;
	public static final int INVALID_ID = -1;
	public static final String INVALID_NAME = null;
	public static final boolean CACHE_CHECKED = true;
	public static final int[] INVALID_ARRAY = null;
	public final static String ERR_PRO_NOT_FOUND = "Process not found: ";
	public final static String ERR_THR_NOT_FOUND = "Thread not found: ";
	public final static int IDLE_PRIORITY_CLASS         = 0x0000040; // value = 64
	public final static int BELOW_NORMAL_PRIORITY_CLASS = 0x0004000; // value = 16384
	public final static int NORMAL_PRIORITY_CLASS       = 0x0000020; // value = 32
	public final static int ABOVE_NORMAL_PRIORITY_CLASS = 0x0008000; // value = 32768
	public final static int HIGH_PRIORITY_CLASS         = 0x0000080; // value = 128
	public final static int REALTIME_PRIORITY_CLASS     = 0x0000100;
	public final static int THREAD_PRIORITY_IDLE          = -15;
	public final static int THREAD_PRIORITY_LOWEST        = -2;
	public final static int THREAD_PRIORITY_BELOW_NORMAL  = -1;
	public final static int THREAD_PRIORITY_NORMAL        = 0;
	public final static int THREAD_PRIORITY_ABOVE_NORMAL  = 1;
	public final static int THREAD_PRIORITY_HIGHEST       = 2;
	public final static int THREAD_PRIORITY_TIME_CRITICAL = 15;
}
