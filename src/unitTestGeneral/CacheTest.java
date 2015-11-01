package unitTestGeneral;

import static org.junit.Assert.*;

import java.io.IOException;

import normalizers.Normalizer;

import org.junit.BeforeClass;
import org.junit.Test;

import handlers.WindowsHandler;

public class CacheTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@Test
	public void CacheFunctiontest() throws InterruptedException {
		Normalizer norm = new Normalizer("Windows");
		norm.mapValues();
		WindowsHandler handler = new WindowsHandler(norm);
		Thread a = new Thread(new Runnable() {
			public void run() {
				int i = 0;
				boolean res = true;
				while (res) {
					i++;
					if (i > 100000000)
						res = false;
					System.out.println(i);
				}
			}
		});
		// Thread b = new Thread();
		Process p = null;
		try {
			p = Runtime.getRuntime().exec("notepad");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (p != null) {
				// p.destroy();
			}
		}

		a.setName("Thread A");
		a.start();

		handler.setJavaThreadPriority("Thread A", 20);
		handler.setProcessPriority("notepad", 20);
		//assertEquals(2, handler.cache.size());
		p.destroy();
		if (handler.setJavaThreadPriority("Thread A", 20))
			handler.setProcessPriority("notepad", 20);
		// System.out.println(handler.cache.get(0).getName());
		assertEquals(2, handler.cache.size());

	}
}
