package org.reldb.relang.tests.main;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.reldb.relang.data.Heading;

public class TestHeading {
	
	@Test 
	public void testHeading01() {
		Heading heading = new Heading();
		assertEquals("{}", heading.toString());
		assertEquals(0, heading.getColumnCount());
		
		heading.appendDefaultColumn();
		assertEquals("{java.lang.String Col0}", heading.toString());
		assertEquals(1, heading.getColumnCount());
		
		heading.appendDefaultColumn();
		assertEquals("{java.lang.String Col0, java.lang.String Col1}", heading.toString());
		assertEquals(2, heading.getColumnCount());
		
		heading.widenToIncludeColumnNumber(4);
		assertEquals("{java.lang.String Col0, java.lang.String Col1, java.lang.String Col2, java.lang.String Col3, java.lang.String Col4}", heading.toString());
		assertEquals(5, heading.getColumnCount());
		
		heading.defineColumn(1, "TestColumn", String.class, "default");
		assertEquals("{java.lang.String Col0, java.lang.String TestColumn, java.lang.String Col2, java.lang.String Col3, java.lang.String Col4}", heading.toString());
		assertEquals(5, heading.getColumnCount());
		
		heading.defineColumn(6, "TestColumn2", String.class, "def");
		assertEquals("{java.lang.String Col0, java.lang.String TestColumn, java.lang.String Col2, java.lang.String Col3, java.lang.String Col4, java.lang.String Col5, java.lang.String TestColumn2}", heading.toString());
		assertEquals(7, heading.getColumnCount());
		
		heading.appendColumn("TestColumn3", String.class, "def2");
		assertEquals("{java.lang.String Col0, java.lang.String TestColumn, java.lang.String Col2, java.lang.String Col3, java.lang.String Col4, java.lang.String Col5, java.lang.String TestColumn2, java.lang.String TestColumn3}", heading.toString());
		assertEquals(8, heading.getColumnCount());
		
		heading.setColumnName(2, "Fish");
		assertEquals("{java.lang.String Col0, java.lang.String TestColumn, java.lang.String Fish, java.lang.String Col3, java.lang.String Col4, java.lang.String Col5, java.lang.String TestColumn2, java.lang.String TestColumn3}", heading.toString());
		assertEquals(8, heading.getColumnCount());
		
		heading.setColumnType(2, String.class, "def4");
		assertEquals("{java.lang.String Col0, java.lang.String TestColumn, java.lang.String Fish, java.lang.String Col3, java.lang.String Col4, java.lang.String Col5, java.lang.String TestColumn2, java.lang.String TestColumn3}", heading.toString());
		assertEquals(8, heading.getColumnCount());
		
		heading.deleteColumnAt(2);
		assertEquals("{java.lang.String Col0, java.lang.String TestColumn, java.lang.String Col3, java.lang.String Col4, java.lang.String Col5, java.lang.String TestColumn2, java.lang.String TestColumn3}", heading.toString());
		assertEquals(7, heading.getColumnCount());

		heading.deleteColumnAt(0);
		assertEquals("{java.lang.String TestColumn, java.lang.String Col3, java.lang.String Col4, java.lang.String Col5, java.lang.String TestColumn2, java.lang.String TestColumn3}", heading.toString());
		assertEquals(6, heading.getColumnCount());
	}

}
