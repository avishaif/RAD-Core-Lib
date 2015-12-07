package os_api.windows;

import com.sun.jna.Library;
import com.sun.jna.PointerType;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.Tlhelp32.PROCESSENTRY32.ByReference;
import com.sun.jna.platform.win32.WinBase.SYSTEM_INFO;
import com.sun.jna.platform.win32.WinNT.HANDLE;



public interface Kernel32 extends Library
{
	
	public void GetSystemInfo(SYSTEM_INFO lpSystemInfo);
	
	public int GetLastError();
	
	public void CloseHandle(HANDLE snapshot);
	
	
	//
	// PROCESS METHODS
	//

//	public int GetPriorityClass(HANDLE hProc);
	
	public int GetProcessAffinityMask(HANDLE hProc, final PointerType lpProcessAffinityMask, final PointerType lpSystemAffinityMask);
	
	public boolean SetProcessAffinityMask(HANDLE hProc, int affinityMask);
	
	public HANDLE OpenProcess(int access, boolean hInherit, int pId);
	
	public boolean SetPriorityClass(HANDLE hProc, int priorityClass);
	
	public HANDLE CreateToolhelp32Snapshot(WinDef.DWORD flags, WinDef.DWORD th32ProcessID);
	
	public boolean Process32Next(HANDLE snapshot, ByReference processEntry);
	
	
	
	
	//
	// THREAD METHODS
	//
	
//	public int GetThreadPriority(HANDLE hThread);
	
	public boolean SetThreadAffinityMask(HANDLE hThread, int affinityMask);
	
	public boolean SetThreadPriority(HANDLE hThread, int threadPriority);
	
	public HANDLE OpenThread(int access, boolean hInherit, int tId);

	public boolean Thread32First(HANDLE hSnapshot, THREADENTRY32 LPTHREADENTRY32);
	
	public boolean Thread32Next(HANDLE hSnapshot, THREADENTRY32 LPTHREADENTRY32);
	
	
}









