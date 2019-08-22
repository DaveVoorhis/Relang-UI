package org.reldb.relang.tests.main;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reldb.relang.data.bdbje.BDBJEEnvironment;

import com.sleepycat.collections.StoredMap;

import org.reldb.relang.data.CatalogEntry;
import org.reldb.relang.data.bdbje.BDBJEBase;

public class TestDataBDBJE {
	
	private final static String testDir = "./test";
	
	private static BDBJEBase base;
	
	@BeforeClass
	public static void setup() {
		BDBJEEnvironment.purge(testDir);
		base = new BDBJEBase(testDir, true);
	}
	
	@Test 
	public void testData01() {
		try (var data = base.create("testData")) {
			data.extend("col1", String.class);
			data.extend("col2", Integer.class);
			var container = (StoredMap<Long, ?>)data.getStoredMap();
			
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
			var container = (StoredMap<String, CatalogEntry>)catalog.getStoredMap();
			assertEquals(true, container.containsKey("testData"));
			assertEquals(true, container.containsKey("testData2"));
			assertEquals(true, container.containsKey(BDBJEBase.catalogName));
			System.out.println("=== Catalog ===");
			container.forEach((name, catalogEntry) -> System.out.println(name + ": " + catalogEntry));
		}
		base.close();
	}

}
