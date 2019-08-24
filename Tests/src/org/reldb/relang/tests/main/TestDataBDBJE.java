package org.reldb.relang.tests.main;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reldb.relang.data.bdbje.BDBJEEnvironment;
import org.reldb.relang.tuples.Tuple;

import com.sleepycat.collections.StoredMap;

import org.reldb.relang.data.CatalogEntry;
import org.reldb.relang.data.bdbje.BDBJEBase;

public class TestDataBDBJE {
	
	private final static boolean verbose = true;
	
	private final static String testDir = "./test";
	
	private static BDBJEBase base;
	
	@BeforeClass
	public static void setup() {
		BDBJEEnvironment.purge(testDir);
		base = new BDBJEBase(testDir, true);
	}
	
	private static void showContainer(String prompt, StoredMap<?, ?> container) {
		if (verbose)
			System.out.println(prompt);
		container.forEach((key, value) -> {
			var str = key + ": " + value.toString();
			if (verbose)
				System.out.println(str);
		});
		if (verbose)
			System.out.println();
	}
	
	@Test 
	public void testData01() throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, InstantiationException, InvocationTargetException {
		final String tupleTypeName = "testData";
		try (var data = base.create(tupleTypeName)) {
			data.extend("col1", String.class);
			data.extend("col2", Integer.class);

			@SuppressWarnings("unchecked")
			var container = (StoredMap<Long, Tuple>)data.getStoredMap();

			/*
			 * The complexity below is needed because the new tuple type has been dynamically created in-line here. 
			 * If it has already been created elsewhere, we can simply use it in a conventional Java fashion. The code would then be:
			 * 
			 * var tuple = new testData();
			 * tuple.col1 = "blah";
			 * tuple.col2 = 3;
			 * container.put(Long.valueOf(1), tuple);
			 * tuple.col1 = "zot";
			 * tuple.col2 = 5;
			 * container.put(Long.valueOf(2), tuple);
			 * 
			 */
			
			// get tuple class
			var tupleType = base.getClassLoader().forName(tupleTypeName);
			// get tuple instance
			var tuple = tupleType.getConstructor().newInstance();
			// initialise instance
			tupleType.getField("col1").set(tuple, "blah");
			tupleType.getField("col2").set(tuple, 3);		
			// insert instance into database
			container.put(Long.valueOf(1), (Tuple)tuple);			
			// initialise instance to something else
			tupleType.getField("col1").set(tuple, "zot");
			tupleType.getField("col2").set(tuple, 5);
			// insert instance
			container.put(Long.valueOf(2), (Tuple)tuple);
			// initialise instance to something else
			tupleType.getField("col1").set(tuple, "zaz");
			tupleType.getField("col2").set(tuple, 66);
			// update instance
			container.put(Long.valueOf(2), (Tuple)tuple);
			
			// Iterate and display container contents
			showContainer("\n=== Container Contents Before Schema Change ===", container);
			
			// change schema
			data.extend("col3", Double.class);
			data.remove("col2");
			
			// get tuple type class and instance
			tupleType = base.getClassLoader().forName(tupleTypeName);
			tuple = tupleType.getConstructor().newInstance();
			// insert instance into database
			tupleType.getField("col1").set(tuple, "blat");
			tupleType.getField("col3").set(tuple, 2.7);
			container.put(Long.valueOf(3), (Tuple)tuple);
			// update instance in database
			tupleType.getField("col1").set(tuple, "zap");
			tupleType.getField("col3").set(tuple, -33.4);
			container.put(Long.valueOf(3), (Tuple)tuple);
			
			// Iterate and display container contents
			showContainer("\n=== Container Contents After Schema Change ===", container);
		}
	}
	
	@Test
	public void testData02() {
		try (var gridData = base.create("testData2")) {
		}
	}
	
	@AfterClass
	public static void teardown() {
		try (var catalog = base.open(BDBJEBase.catalogName)) {
			@SuppressWarnings("unchecked")
			var container = (StoredMap<String, CatalogEntry>)catalog.getStoredMap();
			assertEquals(true, container.containsKey("testData"));
			assertEquals(true, container.containsKey("testData2"));
			assertEquals(true, container.containsKey(BDBJEBase.catalogName));
			showContainer("\n=== Catalog ===", container);
		}
		base.close();
	}

}
