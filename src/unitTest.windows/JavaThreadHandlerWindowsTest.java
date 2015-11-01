package unitTest.windows;

import static org.junit.Assert.*;

import javathreadshandlers.JavaThreadHandlerWindows;

import org.junit.Before;
import org.junit.Test;

import os_api.windows.Kernel32;

import com.sun.jna.Native;
import com.sun.jna.win32.W32APIOptions;

public class JavaThreadHandlerWindowsTest
{
	JavaThreadHandlerWindows parser;
	Kernel32 kernel32;
	
	@Before
	public void init()
	{
		kernel32 = (Kernel32)Native.loadLibrary("kernel32", Kernel32.class, W32APIOptions.UNICODE_OPTIONS);
		 parser = new JavaThreadHandlerWindows(kernel32);
	}
	
	@Test
	public void JavaThreadHandlerTest()
	{
		System.out.println("getNativeThreadId test");
		System.out.println("----------------------");
		assertEquals(-1, parser.getNativeThreadId("kuku"));
		assertEquals("main", parser.getThreadName(1));
		System.out.println("--END OF TEST--\n");
	}

}
