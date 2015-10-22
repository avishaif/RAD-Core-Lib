package unitTests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import facade.Facade;

public class FacadeTest {

	@Before
	public void init(){
		assertFalse(!Facade.init());
	}
	@Test
	public void testInit() {
		System.out.println("Facede.init()");
		assertFalse(!Facade.init());
	}

	@Test
	public void testSetProcessAffinityIntIntArray() {
		System.out.println("SetProcessAffinity(Int,Int[])");          
		assertFalse(!Facade.setProcessAffinity(2673, new int[] {0,1,2,3}));
	}

	@Test
	public void testSetProcessAffinityStringIntArray() {
		System.out.println("SetProcessAffinity(String,Int[])");
		assertFalse(!Facade.setProcessAffinity("chrome", new int[] {0,1,2}));
		
	}
//
//	@Test
//	public void testSetProcessAffinityCollectionOfProcessData() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public void testSetThreadAffinityIntIntArrayBoolean() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public void testSetThreadAffinityStringIntArrayBoolean() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public void testSetThreadAffinityCollectionOfThreadData() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public void testSetProcessPriorityIntIntInt() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public void testSetProcessPriorityStringInt() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public void testSetProcessPriorityCollectionOfProcessData() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public void testSetThreadPriorityIntIntBoolean() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public void testSetThreadPriorityStringIntBoolean() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public void testSetThreadPriorityCollectionOfThreadData() {
//		fail("Not yet implemented"); // TODO
//	}

}

