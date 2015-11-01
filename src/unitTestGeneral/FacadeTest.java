package unitTestGeneral;

import static org.junit.Assert.*;
import org.junit.Test;
import facade.Facade;

public class FacadeTest {


	@Test
	public void testInit() {
		System.out.println("Facede.init()");
		assertFalse(!Facade.init());
	}
}
