package org.reldb.relang.tests.main;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;

import org.junit.Test;
import org.reldb.relang.external.DirClassLoader;
import org.reldb.relang.tuples.TupleGenerator;
import org.reldb.relang.utilities.Directory;

public class TestTupleGenerator {
	
	@Test 
	public void testTupleGenerator01() throws ClassNotFoundException, FileNotFoundException, IOException {
		var codeDir = "./testcode";
		var tupleName = "TestTuple";
		
		Directory.rmAll(codeDir);
		
		var generator = new TupleGenerator(codeDir, tupleName);
		generator.addAttribute("Col1", String.class);
		generator.addAttribute("Col2", Integer.class);
		generator.addAttribute("Col3", Boolean.class);
		generator.addAttribute("Col4", Double.class);
		var compilation = generator.compile();
		System.out.println("=== Compilation " + ((compilation.compiled) ? "succeeded" : "failed") + " ===");
		System.out.println(compilation.compilerMessages);
		
		var loader = new DirClassLoader(codeDir);
		var testclass = loader.forName(tupleName);
		for (Field field: testclass.getFields())
			System.out.println("Has field: " + field.getType().toString() + " " + field.getName());
		
		assertEquals(4, testclass.getFields().length);
		
		System.out.println();
		
		generator = new TupleGenerator(codeDir, tupleName);
		generator.addAttribute("Col5", Float.class);
		var compilation2 = generator.compile();

		System.out.println("=== Compilation " + ((compilation2.compiled) ? "succeeded" : "failed") + " ===");
		System.out.println(compilation2.compilerMessages);
		
		testclass = loader.forName(tupleName);
		for (Field field: testclass.getFields())
			System.out.println("Has field: " + field.getType().toString() + " " + field.getName());
		
		assertEquals(5, testclass.getFields().length);
		
		System.out.println();
		
		generator.removeAttribute("Col4");
		generator.compile();
		
		System.out.println("=== Compilation " + ((compilation2.compiled) ? "succeeded" : "failed") + " ===");
		System.out.println(compilation2.compilerMessages);
		
		testclass = loader.forName(tupleName);
		for (Field field: testclass.getFields())
			System.out.println("Has field: " + field.getType().toString() + " " + field.getName());
		
		assertEquals(4, testclass.getFields().length);
	}

}
