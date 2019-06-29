package org.reldb.relang.tests.main;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.reldb.relang.core.GridData;
import org.reldb.relang.core.GridDataTemporary;
import org.reldb.relang.core.Heading;
import org.reldb.relang.tests.BaseOfTest;

public class TestGridDataTemporary extends BaseOfTest {
	
	@Test 
	public void testGridDataTemporary() {
		GridData data = new GridDataTemporary();
		data.setColumnName(0, "Column1");
		data.setColumnName(1,  "Column2");
		data.setColumnType(0, String.class, "");
		data.setColumnType(1,  Integer.class, Integer.valueOf(0));
		
		// TODO fix broken!
		data.setValue(0, 1, "blah");
		data.setValue(1,  1, 22);
	}

}
