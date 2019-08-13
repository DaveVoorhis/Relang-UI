package org.reldb.relang.tests.main;

import java.lang.reflect.Field;

import org.junit.Test;
import org.reldb.relang.external.DirClassLoader;
import org.reldb.relang.tuples.TupleGenerator;
import org.reldb.relang.utilities.Directory;

public class TestTupleGenerator {
	
	@Test 
	public void testTupleGenerator01() throws ClassNotFoundException {
		Directory.rmAll("./testcode");
		
		var generator = new TupleGenerator("./testcode", "TestTuple");
		generator.addAttribute("Col1", String.class);
		generator.addAttribute("Col2", Integer.class);
		generator.addAttribute("Col3", Boolean.class);
		generator.addAttribute("Col4", Double.class);
		generator.compile();
		
		var loader = new DirClassLoader("./testcode");
		var testclass = loader.forName("TestTuple");
		for (Field field: testclass.getFields())
			System.out.println("Has field: " + field.getType().toString() + " " + field.getName());
	}

}
