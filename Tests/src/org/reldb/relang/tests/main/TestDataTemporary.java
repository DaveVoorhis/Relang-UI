package org.reldb.relang.tests.main;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.reldb.relang.data.Data;
import org.reldb.relang.data.DataTemporary;

public class TestDataTemporary {
	
	@Test 
	public void testDataTemporary01() {
		Data data = new DataTemporary();
		data.setColumnName(0, "Column1");
		data.setColumnName(1,  "Column2");
		data.setColumnType(0, String.class, "");
		data.setColumnType(1,  Integer.class, Integer.valueOf(0));
		data.setValue(0, 1, "blah");
		data.setValue(1,  1, 22);
		data.setValue(1, 25, 33);
		data.setValue(0,  44, "fish");
		data.setColumnType(1, Object.class, Integer.valueOf(2));
		assertEquals("blah", data.getValue(0,  1));
		assertEquals(22, data.getValue(1, 1));
		assertEquals(33, data.getValue(1, 25));
		assertEquals("fish", data.getValue(0,  44));
		data.appendDefaultColumn();
		data.setValue(2, 2, "zot");
		assertEquals("zot", data.getValue(2,  2));
	}
	
	@Test
	public void testDataTemporary02() {
		var gridData = new DataTemporary();
		gridData.setColumnName(0, "Col1");
		gridData.setColumnName(1, "Col2");
		gridData.setColumnName(2, "Col3");
		gridData.appendRow();
		gridData.appendRow();
		gridData.appendRow();
		gridData.appendRow();
		assertEquals(3, gridData.getColumnCount());
		assertEquals(4, gridData.getRowCount());
	}

}
