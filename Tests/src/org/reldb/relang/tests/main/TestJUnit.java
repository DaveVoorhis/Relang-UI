package org.reldb.relang.tests.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.reldb.relang.tests.BaseOfTest;

public class TestJUnit extends BaseOfTest {

	// Verify JUnit operation
	
	@Test 
	public void testOneIsOne() {
		assertEquals(1, 1);
	}
	
	@Test 
	public void testOneIsNotTwo() {
		assertNotEquals(1, 2);
	}

}
