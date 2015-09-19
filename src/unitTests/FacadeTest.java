package unitTests;

import static org.junit.Assert.*;

import java.lang.management.ManagementFactory;

import org.junit.Before;
import org.junit.Test;

import facade.Facade;

public class FacadeTest {

	public Thread t;

	@Before
	public void init() {
		Facade.init();

	}

	// @Test
	// public void testInit() {
	// assertFalse(!Facade.init());
	// }
	//
	// @Test
	// public void testSetProcessAffinityIntIntArray() {
	// fail("Not yet implemented"); // TODO
	// }
	//
	// @Test
	// public void testSetProcessAffinityStringIntArray() {
	// fail("Not yet implemented"); // TODO
	// }
	//
	// @Test
	// public void testSetProcessAffinityCollectionOfProcessData() {
	// fail("Not yet implemented"); // TODO
	// }

	@Test
	public void testSetThreadAffinityIntIntArrayBoolean() {
		t = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					System.out.println("running");
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		t.setName("myThread");
		t.start();
		assertFalse(!Facade.setThreadAffinity((int) t.getId(), new int[] { 0,
				1, 2 }, true));
//		assertFalse(!Facade.setThreadAffinity(12655, new int[] { 0, 1, 2 },
//				false));
	}

	@Test
	public void testSetThreadAffinityStringIntArrayBoolean() {
		 t = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					System.out.println("running");
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		t.setName("myThread");
		t.start();

		assertFalse(!Facade.setThreadAffinity("myThread",
				new int[] { 0, 1, 2 }, true));
//		assertFalse(!Facade.setThreadAffinity("Compositor",
//				new int[] { 0, 1, 2 }, false));
	}

	@Test
	public void testSetThreadAffinityIntIntIntArrayBoolean() {

		t = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					System.out.println("running");
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		t.setName("myThread");
		t.start();
		String jvm = ManagementFactory.getRuntimeMXBean().getName();
		int pid = Integer.parseInt(jvm.substring(0, jvm.indexOf('@')));
		assertFalse(!Facade.setThreadAffinity(pid, (int) t.getId(), new int[] {
				0, 1, 2 }, true));
//		assertFalse(!Facade.setThreadAffinity(pid, 12659, new int[] { 0, 1, 2,
//				3 }, false));
	}

	@Test
	public void testSetThreadAffinityStringStringIntArrayBoolean() {
		t = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					System.out.println("running");
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		t.setName("myThread");
		t.start();
		
		assertFalse(!Facade.setThreadAffinity("java", "myThread", new int[] {
				0, 1, 2 }, true));
//		assertFalse(!Facade.setThreadAffinity("chrome", "Chrome_ChildIOT",
//				new int[] { 0, 1, 2 }, false));
	}

	//
	// @Test
	// public void testSetThreadAffinityCollectionOfThreadData() {
	// fail("Not yet implemented"); // TODO
	// }
	//
	// @Test
	// public void testSetProcessPriorityIntInt() {
	// fail("Not yet implemented"); // TODO
	// }
	//
	// @Test
	// public void testSetProcessPriorityStringInt() {
	// fail("Not yet implemented"); // TODO
	// }
	//
	// @Test
	// public void testSetProcessPriorityCollectionOfProcessData() {
	// fail("Not yet implemented"); // TODO
	// }
	//
	@Test
	public void testSetThreadPriorityIntIntBoolean() {
		t = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					System.out.println("running");
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		t.setName("myThread");
		t.start();


		assertFalse(!Facade.setThreadPriority((int) t.getId(), 30, true));

	}

	@Test
	public void testSetThreadPriorityStringIntBoolean() {

		t = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					System.out.println("running");
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		t.setName("myThread");
		t.start();

		assertFalse(!Facade.setThreadPriority(t.getName(), 30, true));
	}

	@Test
	public void testSetThreadPriorityIntIntIntBoolean() {
		t = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					System.out.println("running");
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		t.setName("myThread");
		t.start();

		String jvm = ManagementFactory.getRuntimeMXBean().getName();
		int pid = Integer.parseInt(jvm.substring(0, jvm.indexOf('@')));
		assertFalse(!Facade.setThreadPriority(pid, (int) t.getId(), 30, true));

	}

	@Test
	public void testSetThreadPriorityStringStringIntBoolean() {
		t = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					System.out.println("running");
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		t.setName("myThread");
		t.start();

		assertFalse(!Facade.setThreadPriority("java", t.getName(), 30, true));
		assertFalse(!Facade.setThreadPriority("java", t.getName(), 30, false));
	}
	//
	// @Test
	// public void testSetThreadPriorityCollectionOfThreadData() {
	// fail("Not yet implemented"); // TODO
	// }

}
