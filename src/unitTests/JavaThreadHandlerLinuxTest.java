package unitTests;

import static org.junit.Assert.*;

import java.io.IOException;

import javathreadshandlers.JavaThreadHandlerLinux;

import org.junit.Before;
import org.junit.Test;

public class JavaThreadHandlerLinuxTest {
private JavaThreadHandlerLinux parser;

	@Before
	public void init()
	{
		 parser = new JavaThreadHandlerLinux();
	}
	
	@Test
	public void JavaThreadHandlerLinuxCompleteTest() throws IOException {
		System.out.println("getNativeThreadId(int)");
		assertEquals(-1, parser.getNativeThreadId(12345678));
		System.out.println("getNativeThreadId(String)");
		assertEquals(-1, parser.getNativeThreadId("something123"));
		System.out.println("getThreadName");
		assertEquals("main", parser.getThreadName(1));
		System.out.println("getThreadName");
		assertEquals(null, parser.getThreadName(123456));
	}

}